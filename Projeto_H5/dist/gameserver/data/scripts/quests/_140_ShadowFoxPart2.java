package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.instancemanager.QuestManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _140_ShadowFoxPart2 extends Quest implements ScriptFile
{
	// NPCs
	private final static int KLUCK = 30895;
	private final static int XENOVIA = 30912;

	// Items
	private final static int CRYSTAL = 10347;
	private final static int OXYDE = 10348;
	private final static int CRYPT = 10349;

	// Monsters
	private final static int Crokian = 20789;
	private final static int Dailaon = 20790;
	private final static int CrokianWarrior = 20791;
	private final static int Farhite = 20792;

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

	public _140_ShadowFoxPart2()
	{
		super(false);

		// Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
		addFirstTalkId(KLUCK);
		addTalkId(KLUCK, XENOVIA);
		addQuestItem(CRYSTAL, OXYDE, CRYPT);
		addKillId(Crokian, Dailaon, CrokianWarrior, Farhite);
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState qs = player.getQuestState(_139_ShadowFoxPart1.class);
		if (qs != null && qs.isCompleted() && player.getQuestState(getClass()) == null)
		{
			newQuestState(player, STARTED);
		}
		return "";
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30895-02.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30895-05.htm"))
		{
			st.setCond(2);
			st.setState(STARTED);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30895-09.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.giveItems(ADENA_ID, 18775);
			st.addExpAndSp(30000, 2000);
			Quest q = QuestManager.getQuest(_141_ShadowFoxPart3.class);
			if (q != null)
			{
				q.newQuestState(st.getPlayer(), STARTED);
			}
			st.exitCurrentQuest(false);
		}
		else if (event.equalsIgnoreCase("30912-07.htm"))
		{
			st.setCond(3);
			st.setState(STARTED);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30912-09.htm"))
		{
			st.takeItems(CRYSTAL, 5);
			if (Rnd.chance(60))
			{
				st.giveItems(OXYDE, 1);
				if (st.getQuestItemsCount(OXYDE) >= 3)
				{
					htmltext = "30912-09b.htm";
					st.setCond(4);
					st.setState(STARTED);
					st.playSound(SOUND_MIDDLE);
					st.takeItems(CRYSTAL, -1);
					st.takeItems(OXYDE, -1);
					st.giveItems(CRYPT, 1);
				}
			}
			else
			{
				htmltext = "30912-09a.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = "noquest";
		if (npcId == KLUCK)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 37)
				{
					htmltext = "30895-01.htm";
				}
				else
				{
					htmltext = "30895-00.htm";
				}
				break;
			case 1:
				htmltext = "30895-02.htm";
				break;
			case 2:
			case 3:
				htmltext = "30895-06.htm";
				break;
			case 4:
				if (st.getInt("talk") == 1)
				{
					htmltext = "30895-08.htm";
				}
				else
				{
					htmltext = "30895-07.htm";
					st.takeItems(CRYPT, -1);
					st.set("talk", "1");
				}
				break;
			default:
				break;
			}
		}
		else if (npcId == XENOVIA)
		{
			switch (cond)
			{
			case 2:
				htmltext = "30912-01.htm";
				break;
			case 3:
				if (st.getQuestItemsCount(CRYSTAL) >= 5)
				{
					htmltext = "30912-08.htm";
				}
				else
				{
					htmltext = "30912-07.htm";
				}
				break;
			case 4:
				htmltext = "30912-10.htm";
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
		if (st.getCond() == 3)
		{
			st.rollAndGive(CRYSTAL, 1, 80 * npc.getTemplate().rateHp);
		}
		return null;
	}
}