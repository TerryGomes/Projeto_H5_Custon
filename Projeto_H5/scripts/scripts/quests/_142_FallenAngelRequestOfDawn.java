package quests;

import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _142_FallenAngelRequestOfDawn extends Quest implements ScriptFile
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

	// NPCs
	private final static int NATOOLS = 30894;
	private final static int RAYMOND = 30289;
	private final static int CASIAN = 30612;
	private final static int ROCK = 32368;
	private final static int NpcAngel = 32369;

	// Items
	private final static int CRYPT = 10351;
	private final static int FRAGMENT = 10352;
	private final static int BLOOD = 10353;

	// Monsters
	private final static int Ant = 20079;
	private final static int AntCaptain = 20080;
	private final static int AntOverseer = 20081;
	private final static int AntRecruit = 20082;
	private final static int AntPatrol = 20084;
	private final static int AntGuard = 20086;
	private final static int AntSoldier = 20087;
	private final static int AntWarriorCaptain = 20088;
	private final static int NobleAnt = 20089;
	private final static int NobleAntLeader = 20090;
	private final static int FallenAngel = 27338;

	public _142_FallenAngelRequestOfDawn()
	{
		super(false);

		// Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
		addTalkId(NATOOLS, RAYMOND, CASIAN, ROCK);
		addQuestItem(CRYPT, FRAGMENT, BLOOD);
		addKillId(Ant, AntCaptain, AntOverseer, AntRecruit, AntPatrol, AntGuard, AntSoldier, AntWarriorCaptain, NobleAnt, NobleAntLeader, FallenAngel);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("start"))
		{
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			st.setState(STARTED);
			htmltext = "stained_rock_q0142_03.htm";
		}
		else if (event.equalsIgnoreCase("warehouse_chief_natools_q0142_10.htm"))
		{
			st.setCond(2);
			st.setState(STARTED);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(CRYPT, 1);
		}
		else if (event.equalsIgnoreCase("bishop_raimund_q0142_05.htm"))
		{
			st.setCond(3);
			st.setState(STARTED);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("sage_kasian_q0142_10.htm"))
		{
			st.setCond(4);
			st.setState(STARTED);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("stained_rock_q0142_05.htm"))
		{
			if (GameObjectsStorage.getByNpcId(NpcAngel) != null)
			{
				htmltext = "stained_rock_q0142_03.htm";
			}
			else if (GameObjectsStorage.getByNpcId(FallenAngel) != null)
			{
				htmltext = "stained_rock_q0142_04.htm";
			}
			else
			{
				st.addSpawn(FallenAngel, 180000);
				st.playSound(SOUND_MIDDLE);
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		switch (npcId)
		{
		case NATOOLS:
			if (cond == 1 || st.isStarted() && cond == 0)
			{
				htmltext = "warehouse_chief_natools_q0142_07.htm";
			}
			else if (cond == 2)
			{
				htmltext = "warehouse_chief_natools_q0142_11.htm";
			}
			break;
		case RAYMOND:
			switch (cond)
			{
			case 2:
				if (st.getInt("talk") == 1)
				{
					htmltext = "bishop_raimund_q0142_02a.htm";
				}
				else
				{
					htmltext = "bishop_raimund_q0142_02.htm";
					st.takeItems(CRYPT, -1);
					st.set("talk", "1");
				}
				break;
			case 3:
				htmltext = "bishop_raimund_q0142_06.htm";
				break;
			case 6:
				htmltext = "bishop_raimund_q0142_07.htm";
				st.playSound(SOUND_FINISH);
				st.giveItems(ADENA_ID, 92676);
				st.exitCurrentQuest(false);
				break;
			default:
				break;
			}
			break;
		case CASIAN:
			if (cond == 3)
			{
				htmltext = "sage_kasian_q0142_02.htm";
			}
			else if (cond == 4)
			{
				htmltext = "sage_kasian_q0142_10.htm";
			}
			break;
		case ROCK:
			if (cond <= 4)
			{
				htmltext = "stained_rock_q0142_01.htm";
			}
			else if (cond == 5)
			{
				htmltext = "stained_rock_q0142_02.htm";
				if (st.getInt("talk") != 1)
				{
					st.takeItems(BLOOD, -1);
					st.set("talk", "1");
				}
			}
			else
			{
				htmltext = "stained_rock_q0142_06.htm";
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
		int cond = st.getCond();
		if (npc.getNpcId() == FallenAngel)
		{
			if (cond == 5)
			{
				st.setCond(6);
				st.setState(STARTED);
				st.playSound(SOUND_MIDDLE);
				st.giveItems(BLOOD, 1);
			}
		}
		else if (cond == 4 && st.getQuestItemsCount(FRAGMENT) < 30)
		{
			st.rollAndGive(FRAGMENT, 1, 1, 30, 20);
			if (st.getQuestItemsCount(FRAGMENT) >= 30)
			{
				st.setCond(5);
				st.setState(STARTED);
			}
		}
		return null;
	}
}