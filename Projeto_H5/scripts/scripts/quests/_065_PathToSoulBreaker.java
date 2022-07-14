package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;

public class _065_PathToSoulBreaker extends Quest implements ScriptFile
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

	private static final int Vitus = 32213;
	private static final int Kekropus = 32138;
	private static final int Casca = 32139;
	private static final int Holst = 32199;
	private static final int Harlan = 30074;
	private static final int Jacob = 30073;
	private static final int Lucas = 30071;
	private static final int Xaber = 30075;
	private static final int Liam = 30076; // (listto)
	private static final int Vesa = 30123;
	private static final int Zerom = 30124;
	private static final int Felton = 30879;
	private static final int Meldina = 32214;
	private static final int Katenar = 32332;
	private static final int Box = 32243;
	private static final int Guardian_Angel = 27332;
	private static final int Wyrm = 20176;

	private static final int DD = 7562;
	private static final int Sealed_Doc = 9803;
	private static final int Wyrm_Heart = 9804;
	private static final int Kekropus_Rec = 9805;
	private static final int SB_Certificate = 9806;

	public _065_PathToSoulBreaker()
	{
		super(false);

		addStartNpc(Vitus);

		addTalkId(Vitus);
		addTalkId(Kekropus);
		addTalkId(Casca);
		addTalkId(Holst);
		addTalkId(Harlan);
		addTalkId(Lucas);
		addTalkId(Jacob);
		addTalkId(Xaber);
		addTalkId(Liam);
		addTalkId(Vesa);
		addTalkId(Zerom);
		addTalkId(Felton);
		addTalkId(Meldina);
		addTalkId(Katenar);
		addTalkId(Box);
		addKillId(Guardian_Angel);
		addKillId(Wyrm);
	}

	public NpcInstance Katenar_Spawn;
	public NpcInstance Guardian_Angel_Spawn;

	private void Despawn_Katenar()
	{
		if (Katenar_Spawn != null)
		{
			Katenar_Spawn.deleteMe();
		}
		Katenar_Spawn = null;
	}

	private void Spawn_Katenar(QuestState st)
	{
		Katenar_Spawn = Functions.spawn(Location.findPointToStay(st.getPlayer(), 50, 100), Katenar);
	}

	private void Despawn_Guardian_Angel()
	{
		if (Guardian_Angel_Spawn != null)
		{
			Guardian_Angel_Spawn.deleteMe();
		}
		Guardian_Angel_Spawn = null;
	}

	private void Spawn_Guardian_Angel(QuestState st)
	{
		Guardian_Angel_Spawn = Functions.spawn(Location.findPointToStay(st.getPlayer(), 50, 100), Guardian_Angel);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("32213-02.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			if (!st.getPlayer().getVarB("dd1"))
			{
				st.giveItems(DD, 47);
				st.getPlayer().setVar("dd1", "1", -1);
			}
			st.playSound(SOUND_ACCEPT);
		}
		if (event.equalsIgnoreCase("32138-03.htm"))
		{
			st.setCond(2);
			st.setState(STARTED);
		}
		if (event.equalsIgnoreCase("32139-01.htm"))
		{
			st.setCond(3);
			st.setState(STARTED);
		}
		if (event.equalsIgnoreCase("32139-03.htm"))
		{
			st.setCond(4);
			st.setState(STARTED);
		}
		if (event.equalsIgnoreCase("32199-01.htm"))
		{
			st.setCond(5);
			st.setState(STARTED);
		}
		if (event.equalsIgnoreCase("30071-01.htm"))
		{
			st.setCond(8);
			st.setState(STARTED);
		}
		if (event.equalsIgnoreCase("32214-01.htm"))
		{
			st.setCond(11);
			st.setState(STARTED);
		}
		if (event.equalsIgnoreCase("30879-02.htm"))
		{
			st.setCond(12);
			st.setState(STARTED);
		}
		if (event.equalsIgnoreCase("32332-01.htm"))
		{
			st.cancelQuestTimer("Katenar_Fail");
			st.giveItems(Sealed_Doc, 1);
			st.setCond(13);
			st.unset("id");
			st.setState(STARTED);
			Despawn_Katenar();
		}
		if (event.equalsIgnoreCase("32139-06.htm"))
		{
			st.takeItems(Sealed_Doc, 1);
			st.setCond(14);
			st.setState(STARTED);
		}
		if (event.equalsIgnoreCase("32138-05.htm"))
		{
			st.setCond(15);
			st.setState(STARTED);
		}
		if (event.equalsIgnoreCase("32138-09.htm"))
		{
			st.takeItems(Wyrm_Heart, 10);
			st.giveItems(Kekropus_Rec, 1);
			st.setCond(17);
			st.setState(STARTED);
		}
		if (event.equalsIgnoreCase("Guardian_Angel_Fail"))
		{
			Despawn_Guardian_Angel();
			htmltext = null;
		}
		if (event.equalsIgnoreCase("Katenar_Fail"))
		{
			Despawn_Katenar();
			htmltext = null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch (npcId)
		{
		case Vitus:
			if (st.getQuestItemsCount(SB_Certificate) > 0)
			{
				htmltext = "completed";
				st.exitCurrentQuest(true);
			}
			else if (cond == 0)
			{
				if (st.getPlayer().getClassId().getId() == 0x7e || st.getPlayer().getClassId().getId() == 0x7d)
				{
					if (st.getPlayer().getLevel() >= 39)
					{
						htmltext = "32213.htm";
					}
					else
					{
						htmltext = "32213-00a.htm";
						st.exitCurrentQuest(true);
					}
				}
				else
				{
					htmltext = "32213-000.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 17)
			{
				htmltext = "32213-03.htm";
				st.takeItems(Kekropus_Rec, 1);
				if (!st.getPlayer().getVarB("prof2.1"))
				{
					st.addExpAndSp(196875, 13510);
					st.giveItems(ADENA_ID, 35597);
					st.getPlayer().setVar("prof2.1", "1", -1);
				}
				st.giveItems(SB_Certificate, 1);
				st.exitCurrentQuest(true);
			}
			break;
		case Kekropus:
			if (cond == 1)
			{
				htmltext = "32138.htm";
			}
			if (cond == 14)
			{
				htmltext = "32138-04.htm";
			}
			if (cond == 16)
			{
				htmltext = "32138-06.htm";
			}
			break;
		case Casca:
			if (cond == 2)
			{
				htmltext = "32139.htm";
			}
			if (cond == 3)
			{
				htmltext = "32139-02.htm";
			}
			if (cond == 13)
			{
				htmltext = "32139-04.htm";
			}
			break;
		case Holst:
			if (cond == 4)
			{
				htmltext = "32199.htm";
			}
			if (cond == 5)
			{
				st.setCond(6);
				htmltext = "32199-02.htm";
			}
			break;
		case Harlan:
			if (cond == 6)
			{
				htmltext = "30074.htm";
			}
			break;
		case Jacob:
			if (cond == 6)
			{
				htmltext = "30073.htm";
				st.setCond(7);
				st.setState(STARTED);
			}
			break;
		case Lucas:
			if (cond == 7)
			{
				htmltext = "30071.htm";
			}
			break;
		case Xaber:
			if (cond == 8)
			{
				htmltext = "30075.htm";
			}
			break;
		case Liam:
			if (cond == 8)
			{
				htmltext = "30076.htm";
				st.setCond(9);
				st.setState(STARTED);
			}
			break;
		case Zerom:
			if (cond == 9)
			{
				htmltext = "30124.htm";
			}
			break;
		case Vesa:
			if (cond == 9)
			{
				htmltext = "30123.htm";
				st.setCond(10);
				st.setState(STARTED);
			}
			break;
		case Meldina:
			if (cond == 10)
			{
				htmltext = "32214.htm";
			}
			break;
		case Box:
			if (cond == 12)
			{
				htmltext = "32243-01.htm";
				for (Player cha : World.getAroundPlayers(st.getPlayer()))
				{
					if (cha.getRace() == Race.kamael)
					{
						htmltext = "32243-02.htm";
						break;
					}
				}
				if (!htmltext.equals("32243-02.htm"))
				{
					Despawn_Guardian_Angel();

					st.set("id", "0");
					Spawn_Guardian_Angel(st);
					st.startQuestTimer("Guardian_Angel_Fail", 120000);
					// Натравим ангела
					if (Guardian_Angel_Spawn != null)
					{
						Guardian_Angel_Spawn.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1);
					}
				}
			}
			else
			{
				htmltext = "32243.htm";
			}
			break;
		case Felton:
			if (cond == 11)
			{
				htmltext = "30879.htm";
			}
			if (cond == 12)
			{
				htmltext = "30879.htm";
			}
			break;
		default:
			if (npcId == Katenar && st.getInt("id") == 1)
			{
				if (cond == 12)
				{
					htmltext = "32332.htm";
				}
			}
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == Guardian_Angel)
		{
			st.cancelQuestTimer("Guardian_Angel_Fail");

			Despawn_Guardian_Angel();

			if (cond == 12)
			{
				for (Player cha : World.getAroundPlayers(st.getPlayer()))
				{
					if (cha.getRace() == Race.kamael)
					{
						return null;
					}
				}
				Despawn_Katenar();

				st.set("id", "1");
				Spawn_Katenar(st);
				st.startQuestTimer("Katenar_Fail", 120000);
				if (Katenar_Spawn != null)
				{
					Functions.npcSay(Katenar_Spawn, "I am late!");
				}
			}
		}
		if (cond == 15 && npcId == Wyrm && Rnd.chance(40))
		{
			st.giveItems(Wyrm_Heart, 1);
			if (st.getQuestItemsCount(Wyrm_Heart) < 10)
			{
				st.playSound(SOUND_ITEMGET);
			}
			else
			{
				st.playSound(SOUND_MIDDLE);
				st.setState(STARTED);
				st.setCond(16);
			}
		}
		return null;
	}
}