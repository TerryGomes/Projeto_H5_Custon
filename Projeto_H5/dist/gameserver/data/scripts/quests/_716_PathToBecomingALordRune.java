package quests;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 *
 * TODO: удалять квест у доверенного лица
 */

public class _716_PathToBecomingALordRune extends Quest implements ScriptFile
{
	private static final int Frederick = 35509;
	private static final int Agripel = 31348;
	private static final int Innocentin = 31328;

	private static final int RuneCastle = 8;
	private static List<Integer> Pagans = new ArrayList<Integer>();

	static
	{
		for (int i = 22138; i <= 22176; i++)
		{
			Pagans.add(i);
		}
		for (int i = 22188; i <= 22195; i++)
		{
			Pagans.add(i);
		}
	}

	public _716_PathToBecomingALordRune()
	{
		super(false);
		addStartNpc(Frederick);
		addTalkId(Agripel, Innocentin);
		addKillId(Pagans);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Castle castle = ResidenceHolder.getInstance().getResidence(RuneCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		Player castleOwner = castle.getOwner().getLeader().getPlayer();
		String htmltext = event;
		if (event.equals("frederick_q716_03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("agripel_q716_03.htm"))
		{
			st.setCond(3);
		}
		else if (event.equals("frederick_q716_08.htm"))
		{
			castleOwner.getQuestState(this.getClass()).set("confidant", String.valueOf(st.getPlayer().getObjectId()), true);
			castleOwner.getQuestState(this.getClass()).setCond(5);
			st.setState(STARTED);
		}
		else if (event.equals("innocentin_q716_03.htm"))
		{
			if (castleOwner != null && castleOwner != st.getPlayer() && castleOwner.getQuestState(this.getClass()) != null && castleOwner.getQuestState(this.getClass()).getCond() == 5)
			{
				castleOwner.getQuestState(this.getClass()).setCond(6);
			}
		}
		else if (event.equals("agripel_q716_08.htm"))
		{
			st.setCond(8);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Castle castle = ResidenceHolder.getInstance().getResidence(RuneCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		Player castleOwner = castle.getOwner().getLeader().getPlayer();

		switch (npcId)
		{
		case Frederick:
			switch (cond)
			{
			case 0:
				if (castleOwner == st.getPlayer())
				{
					if (castle.getDominion().getLordObjectId() != st.getPlayer().getObjectId())
					{
						htmltext = "frederick_q716_01.htm";
					}
					else
					{
						htmltext = "frederick_q716_00.htm";
						st.exitCurrentQuest(true);
					}
				}
				// Лидер клана в игре, говорящий не лидер, у лидера взят квест и пройден до стадии назначения поверенного
				else if (castleOwner != null && castleOwner != st.getPlayer() && castleOwner.getQuestState(getClass()) != null && castleOwner.getQuestState(getClass()).getCond() == 4)
				{
					if (castleOwner.isInRangeZ(npc, 200))
					{
						htmltext = "frederick_q716_07.htm";
					}
					else
					{
						htmltext = "frederick_q716_07a.htm";
					}
				}
				else if (st.getState() == STARTED)
				{
					htmltext = "frederick_q716_00b.htm";
				}
				else
				{
					htmltext = "frederick_q716_00a.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
			{
				QuestState hidingBehindTheTruth = st.getPlayer().getQuestState(_025_HidingBehindTheTruth.class);
				QuestState hiddenTruth = st.getPlayer().getQuestState(_021_HiddenTruth.class);
				if (hidingBehindTheTruth != null && hidingBehindTheTruth.isCompleted() && hiddenTruth != null && hiddenTruth.isCompleted())
				{
					st.setCond(2);
					htmltext = "frederick_q716_04.htm";
				}
				else
				{
					htmltext = "frederick_q716_03.htm";
				}
				break;
			}
			case 2:
				htmltext = "frederick_q716_04a.htm";
				break;
			case 3:
				st.setCond(4);
				htmltext = "frederick_q716_05.htm";
				break;
			case 4:
				htmltext = "frederick_q716_06.htm";
				break;
			case 5:
				htmltext = "frederick_q716_09.htm";
				break;
			case 6:
				st.setCond(7);
				htmltext = "frederick_q716_10.htm";
				break;
			case 7:
				htmltext = "frederick_q716_11.htm";
				break;
			case 8:
				Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_RUNE, st.getPlayer().getName());
				castle.getDominion().changeOwner(castleOwner.getClan());
				htmltext = "frederick_q716_12.htm";
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
				break;
			default:
				break;
			}
			break;
		case Agripel:
			switch (cond)
			{
			case 2:
				htmltext = "agripel_q716_01.htm";
				break;
			case 7:
				if (st.get("paganCount") != null && Integer.parseInt(st.get("paganCount")) >= 100)
				{
					htmltext = "agripel_q716_07.htm";
				}
				else
				{
					htmltext = "agripel_q716_04.htm";
				}
				break;
			case 8:
				htmltext = "agripel_q716_09.htm";
				break;
			default:
				break;
			}
			break;
		case Innocentin:
			if (st.getState() == STARTED && st.getCond() == 0)
			{
				if (castleOwner != null && castleOwner != st.getPlayer() && castleOwner.getQuestState(this.getClass()) != null && castleOwner.getQuestState(this.getClass()).getCond() == 5)
				{
					if (Integer.parseInt(castleOwner.getQuestState(this.getClass()).get("confidant")) == st.getPlayer().getObjectId())
					{
						htmltext = "innocentin_q716_01.htm";
					}
					else
					{
						htmltext = "innocentin_q716_00.htm";
					}
				}
				else
				{
					htmltext = "innocentin_q716_00a.htm";
				}
			}
			break;
		default:
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		Castle castle = ResidenceHolder.getInstance().getResidence(RuneCastle);
		Player castleOwner = castle.getOwner().getLeader().getPlayer();
		if (st.getState() == STARTED && st.getCond() == 0)
		{
			if (castleOwner != null && castleOwner != st.getPlayer() && castleOwner.getQuestState(this.getClass()) != null && castleOwner.getQuestState(this.getClass()).getCond() == 7)
			{
				if (castleOwner.getQuestState(this.getClass()).get("paganCount") != null)
				{
					castleOwner.getQuestState(this.getClass()).set("paganCount", String.valueOf(Integer.parseInt(castleOwner.getQuestState(this.getClass()).get("paganCount")) + 1), true);
				}
				else
				{
					castleOwner.getQuestState(this.getClass()).set("paganCount", "1", true);
				}
			}
		}
		return null;
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}