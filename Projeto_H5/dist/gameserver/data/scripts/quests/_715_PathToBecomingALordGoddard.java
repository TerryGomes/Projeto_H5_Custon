package quests;

import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 */

public class _715_PathToBecomingALordGoddard extends Quest implements ScriptFile
{
	private static final int Alfred = 35363;

	private static final int WaterSpiritAshutar = 25316;
	private static final int FireSpiritNastron = 25306;

	private static final int GoddardCastle = 7;

	public _715_PathToBecomingALordGoddard()
	{
		super(false);
		addStartNpc(Alfred);
		addKillId(WaterSpiritAshutar, FireSpiritNastron);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Castle castle = ResidenceHolder.getInstance().getResidence(GoddardCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		Player castleOwner = castle.getOwner().getLeader().getPlayer();

		if (event.equals("alfred_q715_03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("alfred_q715_04a.htm"))
		{
			st.setCond(3);
		}
		else if (event.equals("alfred_q715_04b.htm"))
		{
			st.setCond(2);
		}
		else if (event.equals("alfred_q715_08.htm"))
		{
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
		int cond = st.getCond();
		Castle castle = ResidenceHolder.getInstance().getResidence(GoddardCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		Player castleOwner = castle.getOwner().getLeader().getPlayer();

		switch (cond)
		{
		case 0:
			if (castleOwner == st.getPlayer())
			{
				if (castle.getDominion().getLordObjectId() != st.getPlayer().getObjectId())
				{
					htmltext = "alfred_q715_01.htm";
				}
				else
				{
					htmltext = "alfred_q715_00.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "alfred_q715_00a.htm";
				st.exitCurrentQuest(true);
			}
			break;
		case 1:
			htmltext = "alfred_q715_03.htm";
			break;
		case 2:
			htmltext = "alfred_q715_05b.htm";
			break;
		case 3:
			htmltext = "alfred_q715_05a.htm";
			break;
		case 4:
			st.setCond(6);
			htmltext = "alfred_q715_06b.htm";
			break;
		case 5:
			st.setCond(7);
			htmltext = "alfred_q715_06a.htm";
			break;
		case 6:
			htmltext = "alfred_q715_06b.htm";
			break;
		case 7:
			htmltext = "alfred_q715_06a.htm";
			break;
		case 8:
		case 9:
			htmltext = "alfred_q715_07.htm";
			break;
		default:
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 2 && npc.getNpcId() == FireSpiritNastron)
		{
			st.setCond(4);
		}
		else if (st.getCond() == 3 && npc.getNpcId() == WaterSpiritAshutar)
		{
			st.setCond(5);
		}

		if (st.getCond() == 6 && npc.getNpcId() == WaterSpiritAshutar)
		{
			st.setCond(9);
		}
		else if (st.getCond() == 7 && npc.getNpcId() == FireSpiritNastron)
		{
			st.setCond(8);
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