package quests;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.ExStartScenePlayer;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public class _10296_SevenSignsPoweroftheSeal extends Quest implements ScriptFile
{
	private static final int Eris = 32792;
	private static final int ElcardiaInzone1 = 32787;
	private static final int EtisEtina = 18949;
	private static final int ElcardiaHome = 32784;
	private static final int Hardin = 30832;
	private static final int Wood = 32593;
	private static final int Franz = 32597;

	private static final Location hiddenLoc = new Location(120744, -87432, -3392);

	public _10296_SevenSignsPoweroftheSeal()
	{
		super(false);
		addStartNpc(Eris);
		addTalkId(ElcardiaInzone1, ElcardiaHome, Hardin, Wood, Franz);
		addKillId(EtisEtina);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if (event.equalsIgnoreCase("eris_q10296_3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("start_scene"))
		{
			st.setCond(2);
			teleportElcardia(player, hiddenLoc);
			ThreadPoolManager.getInstance().schedule(new Teleport(player), 60500L);
			player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_BOSS_OPENING);
			return null;
		}
		else if (event.equalsIgnoreCase("teleport_back"))
		{
			player.teleToLocation(new Location(76736, -241021, -10832));
			teleportElcardia(player);
			return null;
		}
		else if (event.equalsIgnoreCase("elcardiahome_q10296_3.htm"))
		{
			st.setCond(4);
		}
		else if (event.equalsIgnoreCase("hardin_q10296_3.htm"))
		{
			st.setCond(5);
		}
		else if (event.equalsIgnoreCase("enter_instance"))
		{
			enterInstance(player, 146);
			return null;
		}
		else if (event.equalsIgnoreCase("franz_q10296_3.htm"))
		{
			if (player.getLevel() >= 81)
			{
				st.addExpAndSp(225000000, 22500000);
				st.giveItems(17265, 1);
				st.setState(COMPLETED);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
			else
			{
				htmltext = "franz_q10296_0.htm";
			}
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
		case Eris:
			switch (cond)
			{
			case 0:
			{
				QuestState qs = player.getQuestState(_10295_SevenSignsSolinasTomb.class);
				if (player.getLevel() >= 81 && qs != null && qs.isCompleted())
				{
					htmltext = "eris_q10296_1.htm";
				}
				else
				{
					htmltext = "eris_q10296_0.htm";
					st.exitCurrentQuest(true);
				}
				break;
			}
			case 1:
				htmltext = "eris_q10296_4.htm";
				break;
			case 2:
				htmltext = "eris_q10296_5.htm";
				break;
			default:
				if (cond >= 3)
				{
					htmltext = "eris_q10296_6.htm";
				}
				break;
			}
			break;
		case ElcardiaInzone1:
			if (cond == 1)
			{
				htmltext = "elcardia_q10296_1.htm";
			}
			else if (cond == 2)
			{
				if (st.getInt("EtisKilled") == 0)
				{
					htmltext = "elcardia_q10296_1.htm";
				}
				else
				{
					st.setCond(3);
					htmltext = "elcardia_q10296_2.htm";
				}
			}
			else if (cond >= 3)
			{
				htmltext = "elcardia_q10296_4.htm";
			}
			break;
		case ElcardiaHome:
			if (cond == 3)
			{
				htmltext = "elcardiahome_q10296_1.htm";
			}
			else if (cond >= 4)
			{
				htmltext = "elcardiahome_q10296_3.htm";
			}
			break;
		case Hardin:
			if (cond == 4)
			{
				htmltext = "hardin_q10296_1.htm";
			}
			else if (cond == 5)
			{
				htmltext = "hardin_q10296_4.htm";
			}
			break;
		case Wood:
			if (cond == 5)
			{
				htmltext = "wood_q10296_1.htm";
			}
			break;
		case Franz:
			if (cond == 5)
			{
				htmltext = "franz_q10296_1.htm";
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
		int npcId = npc.getNpcId();
		if (npcId == EtisEtina)
		{
			st.set("EtisKilled", 1);
			for (NpcInstance n : st.getPlayer().getReflection().getNpcs())
			{
				if (n.getNpcId() == ElcardiaInzone1)
				{
					n.teleToLocation(new Location(120664, -86968, -3392));
				}
			}
			ThreadPoolManager.getInstance().schedule(new ElcardiaTeleport(st.getPlayer()), 60500L);
			st.getPlayer().showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_BOSS_CLOSING);

		}
		return null;
	}

	private void teleportElcardia(Player player)
	{
		for (NpcInstance n : player.getReflection().getNpcs())
		{
			if (n.getNpcId() == ElcardiaInzone1)
			{
				n.teleToLocation(Location.findPointToStay(player, 60));
				if (n.isBlocked())
				{
					n.unblock();
				}
			}
		}
	}

	private void teleportElcardia(Player player, Location loc)
	{
		for (NpcInstance n : player.getReflection().getNpcs())
		{
			if (n.getNpcId() == ElcardiaInzone1)
			{
				n.teleToLocation(loc);
				n.block();
			}
		}
	}

	private class Teleport extends RunnableImpl
	{
		Player _player;

		public Teleport(Player player)
		{
			_player = player;
		}

		@Override
		public void runImpl() throws Exception
		{
			_player.teleToLocation(new Location(76736, -241021, -10832));
			teleportElcardia(_player);
		}
	}

	private class ElcardiaTeleport extends RunnableImpl
	{
		Player _player;

		public ElcardiaTeleport(Player player)
		{
			_player = player;
		}

		@Override
		public void runImpl() throws Exception
		{
			teleportElcardia(_player);
		}
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