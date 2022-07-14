package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;

public class _709_PathToBecomingALordDion extends Quest implements ScriptFile
{
	private static final int Crosby = 35142;
	private static final int Rouke = 31418;
	private static final int Sophia = 30735;

	private static final int MandragoraRoot = 13849;
	private static final int Epaulette = 13850;

	private static final int[] OlMahums =
	{
		20208,
		20209,
		20210,
		20211
	};
	private static final int[] Manragoras =
	{
		20154,
		20155,
		20156
	};

	private static final int DionCastle = 2;

	public _709_PathToBecomingALordDion()
	{
		super(false);
		addStartNpc(Crosby);
		addTalkId(Sophia, Rouke);
		addQuestItem(Epaulette, MandragoraRoot);
		addKillId(OlMahums);
		addKillId(Manragoras);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Castle castle = ResidenceHolder.getInstance().getResidence(DionCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		Player castleOwner = castle.getOwner().getLeader().getPlayer();
		if (event.equals("crosby_q709_03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("crosby_q709_06.htm"))
		{
			if (isLordAvailable(2, st))
			{
				castleOwner.getQuestState(getClass()).set("confidant", String.valueOf(st.getPlayer().getObjectId()), true);
				castleOwner.getQuestState(getClass()).setCond(3);
				st.setState(STARTED);
			}
			else
			{
				htmltext = "crosby_q709_05a.htm";
			}
		}
		else if (event.equals("rouke_q709_03.htm"))
		{
			if (isLordAvailable(3, st))
			{
				castleOwner.getQuestState(getClass()).setCond(4);
			}
			else
			{
				htmltext = "crosby_q709_05a.htm";
			}
		}
		else if (event.equals("sophia_q709_02.htm"))
		{
			st.setCond(6);
		}
		else if (event.equals("sophia_q709_05.htm"))
		{
			st.setCond(8);
		}
		else if (event.equals("rouke_q709_05.htm"))
		{
			if (isLordAvailable(8, st))
			{
				st.takeAllItems(MandragoraRoot);
				castleOwner.getQuestState(getClass()).setCond(9);
			}
		}
		else if (event.equals("crosby_q709_10.htm"))
		{
			Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_DION, st.getPlayer().getName());
			castle.getDominion().changeOwner(castleOwner.getClan());
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();
		Castle castle = ResidenceHolder.getInstance().getResidence(DionCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		Player castleOwner = castle.getOwner().getLeader().getPlayer();
		switch (npcId)
		{
		case Crosby:
			switch (cond)
			{
			case 0:
				if (castleOwner == st.getPlayer())
				{
					if (castle.getDominion().getLordObjectId() != st.getPlayer().getObjectId())
					{
						htmltext = "crosby_q709_01.htm";
					}
					else
					{
						htmltext = "crosby_q709_00.htm";
						st.exitCurrentQuest(true);
					}
				}
				else if (isLordAvailable(2, st))
				{
					if (castleOwner.isInRangeZ(npc, 200))
					{
						htmltext = "crosby_q709_05.htm";
					}
					else
					{
						htmltext = "crosby_q709_05a.htm";
					}
				}
				else
				{
					htmltext = "crosby_q709_00a.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				st.setCond(2);
				htmltext = "crosby_q709_04.htm";
				break;
			case 2:
			case 3:
				htmltext = "crosby_q709_04a.htm";
				break;
			case 4:
				st.setCond(5);
				htmltext = "crosby_q709_07.htm";
				break;
			case 5:
				htmltext = "crosby_q709_07.htm";
				break;
			default:
				if (cond > 5 && cond < 9)
				{
					htmltext = "crosby_q709_08.htm";
				}
				else if (cond == 9)
				{
					htmltext = "crosby_q709_09.htm";
				}
				break;
			}
			break;
		case Rouke:
			if (st.getState() == STARTED && cond == 0 && isLordAvailable(3, st))
			{
				if (Integer.parseInt(castleOwner.getQuestState(getClass()).get("confidant")) == st.getPlayer().getObjectId())
				{
					htmltext = "rouke_q709_01.htm";
				}
			}
			else if (st.getState() == STARTED && cond == 0 && isLordAvailable(8, st))
			{
				if (st.getQuestItemsCount(MandragoraRoot) >= 100)
				{
					htmltext = "rouke_q709_04.htm";
				}
				else
				{
					htmltext = "rouke_q709_04a.htm";
				}
			}
			else if (st.getState() == STARTED && cond == 0 && isLordAvailable(9, st))
			{
				htmltext = "rouke_q709_06.htm";
			}
			break;
		case Sophia:
			switch (cond)
			{
			case 5:
				htmltext = "sophia_q709_01.htm";
				break;
			case 6:
				htmltext = "sophia_q709_03.htm";
				break;
			case 7:
				htmltext = "sophia_q709_04.htm";
				break;
			case 8:
				htmltext = "sophia_q709_06.htm";
				break;
			default:
				break;
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
		if (st.getCond() == 6 && ArrayUtils.contains(OlMahums, npc.getNpcId()))
		{
			if (Rnd.chance(10))
			{
				st.giveItems(Epaulette, 1);
				st.setCond(7);
			}
		}
		if (st.getState() == STARTED && st.getCond() == 0 && isLordAvailable(8, st) && ArrayUtils.contains(Manragoras, npc.getNpcId()))
		{
			if (st.getQuestItemsCount(MandragoraRoot) < 100)
			{
				st.giveItems(MandragoraRoot, 1);
			}
		}
		return null;
	}

	private boolean isLordAvailable(int cond, QuestState st)
	{
		Castle castle = ResidenceHolder.getInstance().getResidence(DionCastle);
		Clan owner = castle.getOwner();
		Player castleOwner = castle.getOwner().getLeader().getPlayer();
		if (owner != null)
		{
			if (castleOwner != null && castleOwner != st.getPlayer() && owner == st.getPlayer().getClan() && castleOwner.getQuestState(getClass()) != null && castleOwner.getQuestState(getClass()).getCond() == cond)
			{
				return true;
			}
		}
		return false;
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