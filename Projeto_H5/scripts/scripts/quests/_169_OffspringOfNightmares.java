package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2mv.gameserver.scripts.ScriptFile;

public class _169_OffspringOfNightmares extends Quest implements ScriptFile
{
	// NPC
	private static final int Vlasty = 30145;
	// QuestItem
	private static final int CrackedSkull = 1030;
	private static final int PerfectSkull = 1031;
	// Item
	private static final int BoneGaiters = 31;
	// MOB
	private static final int DarkHorror = 20105;
	private static final int LesserDarkHorror = 20025;

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

	public _169_OffspringOfNightmares()
	{
		super(false);

		addStartNpc(Vlasty);

		addTalkId(Vlasty);

		addKillId(DarkHorror);
		addKillId(LesserDarkHorror);

		addQuestItem(new int[]
		{
			CrackedSkull,
			PerfectSkull
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30145-04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30145-08.htm"))
		{
			st.takeItems(CrackedSkull, -1);
			st.takeItems(PerfectSkull, -1);
			st.giveItems(BoneGaiters, 1);
			st.giveItems(ADENA_ID, 17050, true);
			st.getPlayer().addExpAndSp(17475, 818);

			if (st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("p1q4"))
			{
				st.getPlayer().setVar("p1q4", "1", -1);
				st.getPlayer().sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
			}

			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npcId == Vlasty)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getRace() != Race.darkelf)
				{
					htmltext = "30145-00.htm";
					st.exitCurrentQuest(true);
				}
				else if (st.getPlayer().getLevel() >= 15)
				{
					htmltext = "30145-03.htm";
				}
				else
				{
					htmltext = "30145-02.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				if (st.getQuestItemsCount(CrackedSkull) == 0)
				{
					htmltext = "30145-05.htm";
				}
				else
				{
					htmltext = "30145-06.htm";
				}
				break;
			case 2:
				htmltext = "30145-07.htm";
				break;
			default:
				break;
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if (cond == 1)
		{
			if (Rnd.chance(20) && st.getQuestItemsCount(PerfectSkull) == 0)
			{
				st.giveItems(PerfectSkull, 1);
				st.playSound(SOUND_MIDDLE);
				st.setCond(2);
				st.setState(STARTED);
			}
			if (Rnd.chance(70))
			{
				st.giveItems(CrackedSkull, 1);
				st.playSound(SOUND_ITEMGET);
			}
		}
		return null;
	}
}