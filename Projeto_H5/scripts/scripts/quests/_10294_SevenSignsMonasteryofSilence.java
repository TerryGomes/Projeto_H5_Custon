package quests;

import java.util.StringTokenizer;

import l2mv.commons.lang.ArrayUtils;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.ExStartScenePlayer;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;

/**
 * @author pchayka
 */
public class _10294_SevenSignsMonasteryofSilence extends Quest implements ScriptFile
{
	private static final int Elcardia = 32784;
	private static final int ErisEvilThoughts = 32792;
	private static final int ElcardiaInzone1 = 32787;
	private static final int RelicGuard = 32803;
	private static final int[] RelicWatcher = ArrayUtils.createAscendingArray(32804, 32807);
	private static final int YellowRelicWatcher = RelicWatcher[0];
	private static final int GreenRelicWatcher = RelicWatcher[1];
	private static final int BlueRelicWatcher = RelicWatcher[2];
	private static final int RedRelicWatcher = RelicWatcher[3];

	private static final int JudevanEtinasEvilThoughts = 32888;
	private static final int SolinaLayrother = 27407;

	private static final int JudevanEtinasEvilThoughts2 = 32797;
	private static final int SolinasEvilThoughts = 32793;

	// reading desks
	private static final int[] ReadingDesk = ArrayUtils.createAscendingArray(32821, 32836);

	private static final int[] YellowRoomDesks =
	{
		ReadingDesk[0],
		ReadingDesk[1],
		ReadingDesk[2],
		ReadingDesk[3]
	};
	private static final int YellowTrueReadingDesk = YellowRoomDesks[2];

	private static final int[] GreenRoomDesks =
	{
		ReadingDesk[4],
		ReadingDesk[5],
		ReadingDesk[6],
		ReadingDesk[7]
	};
	private static final int GreenTrueReadingDesk = GreenRoomDesks[3];

	private static final int[] BlueRoomDesks =
	{
		ReadingDesk[8],
		ReadingDesk[9],
		ReadingDesk[10],
		ReadingDesk[11]
	};
	private static final int BlueTrueReadingDesk = BlueRoomDesks[1];

	private static final int[] RedRoomDesks =
	{
		ReadingDesk[12],
		ReadingDesk[13],
		ReadingDesk[14],
		ReadingDesk[15]
	};
	private static final int RedTrueReadingDesk = RedRoomDesks[0];

	public _10294_SevenSignsMonasteryofSilence()
	{
		super(false);
		addStartNpc(Elcardia);
		addTalkId(ErisEvilThoughts, ElcardiaInzone1, RelicGuard, JudevanEtinasEvilThoughts2, SolinasEvilThoughts);
		addTalkId(ReadingDesk);
		addTalkId(RelicWatcher);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if (event.equalsIgnoreCase("elcardia_q10294_4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("eris_q10294_3.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("teleport_deeper"))
		{
			player.teleToLocation(new Location(85937, -249618, -8320));
			teleportElcardia(player);
			return null;
		}
		else if (event.startsWith("watcher_teleport"))
		{
			StringTokenizer tokenizer = new StringTokenizer(event);
			tokenizer.nextToken();
			Location loc = null;
			switch (Integer.parseInt(tokenizer.nextToken()))
			{
			case 1: // yellow
				if (st.getInt("yellow") == 0)
				{
					loc = new Location(82434, -249546, -8320);
				}
				break;
			case 2: // green
				if (st.getInt("green") == 0)
				{
					loc = new Location(88536, -249784, -8320);
				}
				break;
			case 3: // blue
				if (st.getInt("blue") == 0)
				{
					loc = new Location(85672, -246872, -8320);
				}
				break;
			case 4: // red
				if (st.getInt("red") == 0)
				{
					loc = new Location(85896, -252664, -8320);
				}
				break;
			default:
				break;
			}
			if (loc != null)
			{
				player.teleToLocation(loc);
				teleportElcardia(player);
				return null;
			}
			else
			{
				htmltext = "movingdevice_q10294_0.htm";
			}
		}
		else if (event.equalsIgnoreCase("teleport_deeper_out"))
		{
			player.teleToLocation(new Location(120600, -86952, -3392));
			teleportElcardia(player);
			return null;
		}
		else if (event.equalsIgnoreCase("readingdesk_q10294_yellowtrue2.htm"))
		{
			if (st.getInt("yellow") == 0)
			{
				npc.setNpcState(1);
				st.set("yellow", 1);
				proccessComplete(st);
			}
			else
			{
				htmltext = "readingdesk_q10294_0.htm";
			}
		}
		else if (event.equalsIgnoreCase("readingdesk_q10294_greentrue2.htm"))
		{
			if (st.getInt("green") == 0)
			{
				npc.setNpcState(1);
				st.set("green", 1);
				st.getPlayer().getReflection().addSpawnWithoutRespawn(JudevanEtinasEvilThoughts, new Location(87704, -249496, -8320, 49152), 0);
				for (int i = 0; i < 3; i++)
				{
					st.getPlayer().getReflection().addSpawnWithoutRespawn(SolinaLayrother, Location.findPointToStay(st.getPlayer(), 300), 0);
				}
				proccessComplete(st);
			}
			else
			{
				htmltext = "readingdesk_q10294_0.htm";
			}
		}
		else if (event.equalsIgnoreCase("readingdesk_q10294_bluetrue2.htm"))
		{
			if (st.getInt("blue") == 0)
			{
				npc.setNpcState(1);
				st.set("blue", 1);
				st.getPlayer().getReflection().addSpawnWithoutRespawn(SolinasEvilThoughts, new Location(86680, -246728, -8320, 0), 0);
				proccessComplete(st);
			}
			else
			{
				htmltext = "readingdesk_q10294_0.htm";
			}
		}
		else if (event.equalsIgnoreCase("readingdesk_q10294_redtrue2.htm"))
		{
			if (st.getInt("red") == 0)
			{
				npc.setNpcState(1);
				st.set("red", 1);
				st.getPlayer().getReflection().addSpawnWithoutRespawn(JudevanEtinasEvilThoughts2, new Location(84840, -252392, -8320, 49152), 0);
				proccessComplete(st);
			}
			else
			{
				htmltext = "readingdesk_q10294_0.htm";
			}
		}
		else if (event.equalsIgnoreCase("teleport_to_guardian"))
		{
			if (npc.getNpcId() == YellowRelicWatcher && st.getInt("yellow") != 0 || npc.getNpcId() == GreenRelicWatcher && st.getInt("green") != 0 || npc.getNpcId() == BlueRelicWatcher && st.getInt("blue") != 0 || npc.getNpcId() == RedRelicWatcher && st.getInt("red") != 0)
			{
				htmltext = "relicwatcher_q10294_4.htm";
			}
			else
			{
				player.teleToLocation(new Location(85937, -249618, -8320));
				teleportElcardia(player);
				return null;
			}
		}
		else if (event.equalsIgnoreCase("take_test"))
		{
			if (npc.getNpcId() == YellowRelicWatcher)
			{
				if (st.getInt("yellow") != 0)
				{
					htmltext = "relicwatcher_q10294_yellowtest.htm";
				}

			}
			else if (npc.getNpcId() == GreenRelicWatcher)
			{
				if (st.getInt("green") != 0)
				{
					htmltext = "relicwatcher_q10294_greentest.htm";
				}

			}
			else if (npc.getNpcId() == BlueRelicWatcher)
			{
				if (st.getInt("blue") != 0)
				{
					htmltext = "relicwatcher_q10294_bluetest.htm";
				}

			}
			else if (npc.getNpcId() == RedRelicWatcher)
			{
				if (st.getInt("red") != 0)
				{
					htmltext = "relicwatcher_q10294_redtest.htm";
				}

			}
		}
		else if (event.equalsIgnoreCase("false_answer"))
		{
			htmltext = "relicwatcher_q10294_falseanswer.htm";
		}
		else if (event.equalsIgnoreCase("true_answer"))
		{
			player.teleToLocation(new Location(85937, -249618, -8320));
			teleportElcardia(player);
			return null;
		}
		else if (event.equalsIgnoreCase("eris_q10294_9.htm"))
		{
			st.addExpAndSp(25000000, 2500000);
			st.setState(COMPLETED);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Player player = st.getPlayer();
		if (player.getBaseClassId() != player.getActiveClassId())
		{
			return "no_subclass_allowed.htm";
		}
		switch (npcId)
		{
		case Elcardia:
			if (cond == 0)
			{
				QuestState qs = player.getQuestState(_10293_SevenSignsForbiddenBook.class);
				if (player.getLevel() >= 81 && qs != null && qs.isCompleted())
				{
					htmltext = "elcardia_q10294_1.htm";
				}
				else
				{
					htmltext = "elcardia_q10294_0.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1)
			{
				htmltext = "elcardia_q10294_5.htm";
			}
			break;
		case ErisEvilThoughts:
			switch (cond)
			{
			case 1:
				htmltext = "eris_q10294_1.htm";
				break;
			case 2:
				htmltext = "eris_q10294_4.htm";
				break;
			case 3:
				htmltext = "eris_q10294_8.htm";
				break;
			default:
				break;
			}
			break;
		case ElcardiaInzone1:
			if (cond == 1 || cond == 2)
			{
				htmltext = "elcardia2_q10294_1.htm";
			}
			break;
		case RelicGuard:
			if (cond == 2 && checkComplete(st))
			{
				st.setCond(3);
				htmltext = "relicguard_q10294_4.htm";
			}
			else if (cond == 1 || cond == 2)
			{
				htmltext = "relicguard_q10294_1.htm";
			}
			else if (cond == 3)
			{
				htmltext = "relicguard_q10294_5.htm";
			}
			break;
		default:
			if (org.apache.commons.lang3.ArrayUtils.contains(RelicWatcher, npcId))
			{
				if (cond == 2)
				{
					htmltext = "relicwatcher_q10294_1.htm";
				}
			}
			else if (org.apache.commons.lang3.ArrayUtils.contains(ReadingDesk, npcId))
			{
				if (cond == 2)
				{
					if (org.apache.commons.lang3.ArrayUtils.contains(YellowRoomDesks, npcId))
					{
						if (npcId == YellowTrueReadingDesk)
						{
							htmltext = "readingdesk_q10294_yellowtrue.htm";
						}
						else
						{
							htmltext = "readingdesk_q10294_false.htm";
						}
					}
					else if (org.apache.commons.lang3.ArrayUtils.contains(GreenRoomDesks, npcId))
					{
						if (npcId == GreenTrueReadingDesk)
						{
							htmltext = "readingdesk_q10294_greentrue.htm";
						}
						else
						{
							htmltext = "readingdesk_q10294_false.htm";
						}
					}
					else if (org.apache.commons.lang3.ArrayUtils.contains(BlueRoomDesks, npcId))
					{
						if (npcId == BlueTrueReadingDesk)
						{
							htmltext = "readingdesk_q10294_bluetrue.htm";
						}
						else
						{
							htmltext = "readingdesk_q10294_false.htm";
						}
					}
					else if (org.apache.commons.lang3.ArrayUtils.contains(RedRoomDesks, npcId))
					{
						if (npcId == RedTrueReadingDesk)
						{
							htmltext = "readingdesk_q10294_redtrue.htm";
						}
						else
						{
							htmltext = "readingdesk_q10294_false.htm";
						}
					}
				}
			}
			else if (npcId == JudevanEtinasEvilThoughts2)
			{
				htmltext = "jude_q10294_1.htm";
			}
			else if (npcId == SolinasEvilThoughts)
			{
				htmltext = "solina_q10294_1.htm";
			}
			break;
		}
		return htmltext;
	}

	private void teleportElcardia(Player player)
	{
		for (NpcInstance n : player.getReflection().getNpcs())
		{
			if (n.getNpcId() == ElcardiaInzone1)
			{
				n.teleToLocation(Location.findPointToStay(player, 100));
			}
		}
	}

	private boolean checkComplete(QuestState st)
	{
		return st.getInt("yellow") != 0 && st.getInt("green") != 0 && st.getInt("blue") != 0 && st.getInt("red") != 0;
	}

	private void proccessComplete(QuestState st)
	{
		if (checkComplete(st))
		{
			st.getPlayer().showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_HOLY_BURIAL_GROUND_CLOSING);
		}

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