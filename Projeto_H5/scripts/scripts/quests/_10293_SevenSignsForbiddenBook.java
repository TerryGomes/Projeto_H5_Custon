package quests;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public class _10293_SevenSignsForbiddenBook extends Quest implements ScriptFile
{
	private static final int Elcardia = 32784;
	private static final int Sophia = 32596;

	private static final int SophiaInzone1 = 32861;
	private static final int ElcardiaInzone1 = 32785;
	private static final int SophiaInzone2 = 32863;

	private static final int SolinasBiography = 17213;

	private static final int[] books =
	{
		32809,
		32810,
		32811,
		32812,
		32813
	};

	public _10293_SevenSignsForbiddenBook()
	{
		super(false);
		addStartNpc(Elcardia);
		addTalkId(Sophia, SophiaInzone1, ElcardiaInzone1, SophiaInzone2);
		addTalkId(books);
		addQuestItem(SolinasBiography);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if (event.equalsIgnoreCase("elcardia_q10293_3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("enter_library"))
		{
			enterInstance(player, 156);
			return null;
		}
		else if (event.equalsIgnoreCase("sophia2_q10293_4.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("sophia2_q10293_8.htm"))
		{
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("elcardia2_q10293_4.htm"))
		{
			st.setCond(5);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("sophia2_q10293_10.htm"))
		{
			st.setCond(6);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("teleport_in"))
		{
			Location loc = new Location(37348, -50383, -1168);
			st.getPlayer().teleToLocation(loc);
			teleportElcardia(player);
			return null;
		}
		else if (event.equalsIgnoreCase("teleport_out"))
		{
			Location loc = new Location(37205, -49753, -1128);
			st.getPlayer().teleToLocation(loc);
			teleportElcardia(player);
			return null;
		}
		else if (event.equalsIgnoreCase("book_q10293_3a.htm"))
		{
			st.giveItems(SolinasBiography, 1);
			st.setCond(7);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("elcardia_q10293_7.htm"))
		{
			st.addExpAndSp(15000000, 1500000);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
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
				QuestState qs = player.getQuestState(_10292_SevenSignsGirlOfDoubt.class);
				if (player.getLevel() >= 81 && qs != null && qs.isCompleted())
				{
					htmltext = "elcardia_q10293_1.htm";
				}
				else
				{
					htmltext = "elcardia_q10293_0.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond >= 1 && cond < 8)
			{
				htmltext = "elcardia_q10293_4.htm";
			}
			else if (cond == 8)
			{
				htmltext = "elcardia_q10293_5.htm";
			}
			break;
		case Sophia:
			if (cond >= 1 && cond <= 7)
			{
				htmltext = "sophia_q10293_1.htm";
			}
			break;
		case SophiaInzone1:
			switch (cond)
			{
			case 1:
				htmltext = "sophia2_q10293_1.htm";
				break;
			case 2:
			case 4:
			case 7:
			case 8:
				htmltext = "sophia2_q10293_5.htm";
				break;
			case 3:
				htmltext = "sophia2_q10293_6.htm";
				break;
			case 5:
				htmltext = "sophia2_q10293_9.htm";
				break;
			case 6:
				htmltext = "sophia2_q10293_11.htm";
				break;
			default:
				break;
			}
			break;
		case ElcardiaInzone1:
			switch (cond)
			{
			case 1:
			case 3:
			case 5:
			case 6:
				htmltext = "elcardia2_q10293_1.htm";
				break;
			case 2:
				st.setCond(3);
				htmltext = "elcardia2_q10293_2.htm";
				break;
			case 4:
				htmltext = "elcardia2_q10293_3.htm";
				break;
			case 7:
				st.setCond(8);
				htmltext = "elcardia2_q10293_5.htm";
				break;
			case 8:
				htmltext = "elcardia2_q10293_5.htm";
				break;
			default:
				break;
			}

			break;
		case SophiaInzone2:
			if (cond == 6 || cond == 7)
			{
				htmltext = "sophia3_q10293_1.htm";
			}
			else if (cond == 8)
			{
				htmltext = "sophia3_q10293_4.htm";
			}
			break;
		// Books
		case 32809:
			htmltext = "book_q10293_3.htm";
			break;
		case 32811:
			htmltext = "book_q10293_1.htm";
			break;
		case 32812:
			htmltext = "book_q10293_2.htm";
			break;
		case 32810:
			htmltext = "book_q10293_4.htm";
			break;
		case 32813:
			htmltext = "book_q10293_5.htm";
			break;

		}
		return htmltext;
	}

	private void enterInstance(Player player, int instancedZoneId)
	{
		Reflection r = player.getActiveReflection();
		if (r != null)
		{
			if (player.canReenterInstance(instancedZoneId))
			{
				player.teleToLocation(r.getTeleportLoc(), r);
			}
		}
		else if (player.canEnterInstance(instancedZoneId))
		{
			ReflectionUtils.enterReflection(player, instancedZoneId);
		}
	}

	private void teleportElcardia(Player player)
	{
		for (NpcInstance n : player.getReflection().getNpcs())
		{
			if (n.getNpcId() == ElcardiaInzone1)
			{
				n.teleToLocation(Location.findPointToStay(player, 60));
			}
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