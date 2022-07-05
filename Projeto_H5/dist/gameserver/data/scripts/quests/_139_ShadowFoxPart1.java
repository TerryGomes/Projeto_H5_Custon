package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _139_ShadowFoxPart1 extends Quest implements ScriptFile
{
	// NPC
	private final static int MIA = 30896;

	// Items
	private final static int FRAGMENT = 10345;
	private final static int CHEST = 10346;

	// Monsters
	private final static int TasabaLizardman1 = 20784;
	private final static int TasabaLizardman2 = 21639;
	private final static int TasabaLizardmanShaman1 = 20785;
	private final static int TasabaLizardmanShaman2 = 21640;

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

	public _139_ShadowFoxPart1()
	{
		super(false);

		// Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
		addFirstTalkId(MIA);
		addTalkId(MIA);
		addQuestItem(FRAGMENT, CHEST);
		addKillId(TasabaLizardman1, TasabaLizardman2, TasabaLizardmanShaman1, TasabaLizardmanShaman2);
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState qs = player.getQuestState(_138_TempleChampionPart2.class);
		if (qs != null && qs.isCompleted() && player.getQuestState(getClass()) == null)
		{
			newQuestState(player, STARTED);
		}
		return "";
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("30896-03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30896-11.htm"))
		{
			st.setCond(2);
			st.setState(STARTED);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30896-14.htm"))
		{
			st.takeItems(FRAGMENT, -1);
			st.takeItems(CHEST, -1);
			st.set("talk", "1");
		}
		else if (event.equalsIgnoreCase("30896-16.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.giveItems(ADENA_ID, 14050);
			st.addExpAndSp(30000, 2000);
			st.exitCurrentQuest(false);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		if (npcId == MIA)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 37)
				{
					htmltext = "30896-01.htm";
				}
				else
				{
					htmltext = "30896-00.htm";
				}
				break;
			case 1:
				htmltext = "30896-03.htm";
				break;
			case 2:
				if (st.getQuestItemsCount(FRAGMENT) >= 10 && st.getQuestItemsCount(CHEST) >= 1)
				{
					htmltext = "30896-13.htm";
				}
				else if (st.getInt("talk") == 1)
				{
					htmltext = "30896-14.htm";
				}
				else
				{
					htmltext = "30896-12.htm";
				}
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
		if (cond == 2)
		{
			st.giveItems(FRAGMENT, 1);
			st.playSound(SOUND_ITEMGET);
			if (Rnd.chance(10))
			{
				st.giveItems(CHEST, 1);
			}
		}
		return null;
	}
}