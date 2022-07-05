package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _638_SeekersOfTheHolyGrail extends Quest implements ScriptFile
{
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

	private static final int DROP_CHANCE = 5; // For x1 mobs
	private static final int INNOCENTIN = 31328;
	private static final int TOTEM = 8068;
	private static final int EAS = 960;
	private static final int EWS = 959;

	public _638_SeekersOfTheHolyGrail()
	{
		super(true);
		addStartNpc(INNOCENTIN);
		addQuestItem(TOTEM);
		for (int i = 22137; i <= 22176; i++)
		{
			addKillId(i);
		}
		addKillId(22194);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("highpriest_innocentin_q0638_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("highpriest_innocentin_q0638_09.htm"))
		{
			st.playSound(SOUND_GIVEUP);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();

		if (id == CREATED)
		{
			if (st.getPlayer().getLevel() >= 73)
			{
				htmltext = "highpriest_innocentin_q0638_01.htm";
			}
			else
			{
				htmltext = "highpriest_innocentin_q0638_02.htm";
			}
		}
		else
		{
			htmltext = tryRevard(st);
		}

		return htmltext;
	}

	private String tryRevard(QuestState st)
	{
		boolean ok = false;
		while (st.getQuestItemsCount(TOTEM) >= 2000)
		{
			st.takeItems(TOTEM, 2000);
			int rnd = Rnd.get(100);
			if (rnd < 40)
			{
				st.giveItems(ADENA_ID, 10728000, false);
			}
			else if (rnd < 85)
			{
				st.giveItems(EAS, 3, false);
			}
			else
			{
				st.giveItems(EWS, 3, false);
			}
			ok = true;
		}
		if (ok)
		{
			st.playSound(SOUND_MIDDLE);
			return "highpriest_innocentin_q0638_10.htm";
		}
		return "highpriest_innocentin_q0638_05.htm";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		st.rollAndGive(TOTEM, 1, DROP_CHANCE * npc.getTemplate().rateHp);

		if ((npc.getNpcId() == 22146 || npc.getNpcId() == 22151) && Rnd.chance(10))
		{
			npc.dropItem(st.getPlayer(), 8275, 1);
		}

		if ((npc.getNpcId() == 22140 || npc.getNpcId() == 22149) && Rnd.chance(10))
		{
			npc.dropItem(st.getPlayer(), 8273, 1);
		}

		if ((npc.getNpcId() == 22142 || npc.getNpcId() == 22143) && Rnd.chance(10))
		{
			npc.dropItem(st.getPlayer(), 8274, 1);
		}

		return null;
	}
}