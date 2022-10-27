package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _343_UndertheShadowoftheIvoryTower extends Quest implements ScriptFile
{
	// NPC
	public final int CEMA = 30834;
	public final int ICARUS = 30835;
	public final int MARSHA = 30934;
	public final int TRUMPIN = 30935;
	// Mob
	public final int[] MOBS =
	{
		20563,
		20564,
		20565,
		20566
	};

	// Items
	public final int ORB = 4364;
	public final int ECTOPLASM = 4365;
	// Var
	public final int[] AllowClass =
	{
		0xb,
		0xc,
		0xd,
		0xe,
		0x1a,
		0x1b,
		0x1c,
		0x27,
		0x28,
		0x29
	};
	public final int CHANCE = 50;

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

	public _343_UndertheShadowoftheIvoryTower()
	{
		super(false);

		addStartNpc(CEMA);
		addTalkId(CEMA);
		addTalkId(ICARUS);
		addTalkId(MARSHA);
		addTalkId(TRUMPIN);

		for (int i : MOBS)
		{
			addKillId(i);
		}

		addQuestItem(ORB);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		int random1 = Rnd.get(3);
		int random2 = Rnd.get(2);
		long orbs = st.getQuestItemsCount(ORB);
		if (event.equalsIgnoreCase("30834-03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30834-08.htm"))
		{
			if (orbs > 0)
			{
				st.giveItems(ADENA_ID, orbs * 120);
				st.takeItems(ORB, -1);
			}
			else
			{
				htmltext = "30834-08.htm";
			}
		}
		else if (event.equalsIgnoreCase("30834-09.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else if (event.equalsIgnoreCase("30934-02.htm") || event.equalsIgnoreCase("30934-03.htm"))
		{
			if (orbs < 10)
			{
				htmltext = "noorbs.htm";
			}
			else if (event.equalsIgnoreCase("30934-03.htm"))
			{
				if (orbs >= 10)
				{
					st.takeItems(ORB, 10);
					st.set("playing", "1");
				}
				else
				{
					htmltext = "noorbs.htm";
				}
			}
		}
		else if (event.equalsIgnoreCase("30934-04.htm"))
		{
			if (st.getInt("playing") > 0)
			{
				if (random1 == 0)
				{
					htmltext = "30934-05.htm";
					st.giveItems(ORB, 10);
				}
				else if (random1 == 1)
				{
					htmltext = "30934-06.htm";
				}
				else
				{
					htmltext = "30934-04.htm";
					st.giveItems(ORB, 20);
				}
				st.unset("playing");
			}
			else
			{
				htmltext = "Player is cheating";
				st.takeItems(ORB, -1);
				st.exitCurrentQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("30934-05.htm"))
		{
			if (st.getInt("playing") > 0)
			{
				if (random1 == 0)
				{
					htmltext = "30934-04.htm";
					st.giveItems(ORB, 20);
				}
				else if (random1 == 1)
				{
					htmltext = "30934-05.htm";
					st.giveItems(ORB, 10);
				}
				else
				{
					htmltext = "30934-06.htm";
				}
				st.unset("playing");
			}
			else
			{
				htmltext = "Player is cheating";
				st.takeItems(ORB, -1);
				st.exitCurrentQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("30934-06.htm"))
		{
			if (st.getInt("playing") > 0)
			{
				if (random1 == 0)
				{
					htmltext = "30934-04.htm";
					st.giveItems(ORB, 20);
				}
				else if (random1 == 1)
				{
					htmltext = "30934-06.htm";
				}
				else
				{
					htmltext = "30934-05.htm";
					st.giveItems(ORB, 10);
				}
				st.unset("playing");
			}
			else
			{
				htmltext = "Player is cheating";
				st.takeItems(ORB, -1);
				st.exitCurrentQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("30935-02.htm") || event.equalsIgnoreCase("30935-03.htm"))
		{
			st.unset("toss");
			if (orbs < 10)
			{
				htmltext = "noorbs.htm";
			}
		}
		else if (event.equalsIgnoreCase("30935-05.htm"))
		{
			if (orbs >= 10)
			{
				if (random2 == 0)
				{
					int toss = st.getInt("toss");
					if (toss == 4)
					{
						st.unset("toss");
						st.giveItems(ORB, 150);
						htmltext = "30935-07.htm";
					}
					else
					{
						st.set("toss", String.valueOf(toss + 1));
						htmltext = "30935-04.htm";
					}
				}
				else
				{
					st.unset("toss");
					st.takeItems(ORB, 10);
				}
			}
			else
			{
				htmltext = "noorbs.htm";
			}
		}
		else if (event.equalsIgnoreCase("30935-06.htm"))
		{
			if (orbs >= 10)
			{
				int toss = st.getInt("toss");
				st.unset("toss");
				switch (toss)
				{
				case 1:
					st.giveItems(ORB, 10);
					break;
				case 2:
					st.giveItems(ORB, 30);
					break;
				case 3:
					st.giveItems(ORB, 70);
					break;
				case 4:
					st.giveItems(ORB, 150);
					break;
				default:
					break;
				}
			}
			else
			{
				htmltext = "noorbs.htm";
			}
		}
		else if (event.equalsIgnoreCase("30835-02.htm"))
		{
			if (st.getQuestItemsCount(ECTOPLASM) > 0)
			{
				st.takeItems(ECTOPLASM, 1);
				int random = Rnd.get(1000);
				if (random <= 119)
				{
					st.giveItems(955, 1);
				}
				else if (random <= 169)
				{
					st.giveItems(951, 1);
				}
				else if (random <= 329)
				{
					st.giveItems(2511, Rnd.get(200) + 401);
				}
				else if (random <= 559)
				{
					st.giveItems(2510, Rnd.get(200) + 401);
				}
				else if (random <= 561)
				{
					st.giveItems(316, 1);
				}
				else if (random <= 578)
				{
					st.giveItems(630, 1);
				}
				else if (random <= 579)
				{
					st.giveItems(188, 1);
				}
				else if (random <= 581)
				{
					st.giveItems(885, 1);
				}
				else if (random <= 582)
				{
					st.giveItems(103, 1);
				}
				else if (random <= 584)
				{
					st.giveItems(917, 1);
				}
				else
				{
					st.giveItems(736, 1);
				}
			}
			else
			{
				htmltext = "30835-03.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		switch (npcId)
		{
		case CEMA:
			if (id != STARTED)
			{
				for (int i : AllowClass)
				{
					if (st.getPlayer().getClassId().getId() == i && st.getPlayer().getLevel() >= 40)
					{
						htmltext = "30834-01.htm";
					}
				}
				if (!htmltext.equals("30834-01.htm"))
				{
					htmltext = "30834-07.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (st.getQuestItemsCount(ORB) > 0)
			{
				htmltext = "30834-06.htm";
			}
			else
			{
				htmltext = "30834-05.htm";
			}
			break;
		case ICARUS:
			htmltext = "30835-01.htm";
			break;
		case MARSHA:
			htmltext = "30934-01.htm";
			break;
		case TRUMPIN:
			htmltext = "30935-01.htm";
			break;
		default:
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (Rnd.chance(CHANCE))
		{
			st.giveItems(ORB, 1);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}