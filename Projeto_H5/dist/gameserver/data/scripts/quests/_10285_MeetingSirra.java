package quests;

import gnu.trove.map.hash.TIntIntHashMap;
import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Spawner;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.ExStartScenePlayer;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public class _10285_MeetingSirra extends Quest implements ScriptFile
{
	private static final int Rafforty = 32020;
	private static final int Jinia = 32760;
	private static final int Jinia2 = 32781;
	private static final int Kegor = 32761;
	private static final int Sirra = 32762;
	private static TIntIntHashMap _instances = new TIntIntHashMap();

	public _10285_MeetingSirra()
	{
		super(false);
		addStartNpc(Rafforty);
		addTalkId(Jinia, Jinia2, Kegor, Sirra);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("rafforty_q10285_03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("enterinstance"))
		{
			if (st.getCond() == 1)
			{
				st.setCond(2);
			}
			enterInstance(st.getPlayer(), 141);
			return null;
		}
		else if (event.equalsIgnoreCase("jinia_q10285_02.htm"))
		{
			st.setCond(3);
		}
		else if (event.equalsIgnoreCase("kegor_q10285_02.htm"))
		{
			st.setCond(4);
		}
		else if (event.equalsIgnoreCase("sirraspawn"))
		{
			st.setCond(5);
			st.getPlayer().getReflection().addSpawnWithoutRespawn(Sirra, new Location(-23848, -8744, -5413, 49152), 0);
			for (NpcInstance sirra : st.getPlayer().getAroundNpc(1000, 100))
			{
				if (sirra.getNpcId() == Sirra)
				{
					Functions.npcSay(sirra, "You listen to it that you know about everything. But I can no longer listen to your philosophising!");
				}
			}
			return null;
		}
		else if (event.equalsIgnoreCase("sirra_q10285_07.htm"))
		{
			st.setCond(6);
			for (NpcInstance sirra : st.getPlayer().getAroundNpc(1000, 100))
			{
				if (sirra.getNpcId() == 32762)
				{
					sirra.deleteMe();
				}
			}
		}
		else if (event.equalsIgnoreCase("jinia_q10285_10.htm"))
		{
			if (!st.getPlayer().getReflection().isDefault())
			{
				st.getPlayer().getReflection().startCollapseTimer(60 * 1000L);
				st.getPlayer().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(1));
			}
			st.setCond(7);
		}
		else if (event.equalsIgnoreCase("exitinstance"))
		{
			st.getPlayer().getReflection().collapse();
			return null;
		}
		else if (event.equalsIgnoreCase("enterfreya"))
		{
			st.setCond(9);
			enterInstance(st.getPlayer(), 137);
			return null;
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
		case Rafforty:
			if (cond == 0)
			{
				QuestState qs = st.getPlayer().getQuestState(_10284_AcquisionOfDivineSword.class);
				if (st.getPlayer().getLevel() >= 82 && qs != null && qs.isCompleted())
				{
					htmltext = "rafforty_q10285_01.htm";
				}
				else
				{
					htmltext = "rafforty_q10285_00.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond >= 1 && cond < 7)
			{
				htmltext = "rafforty_q10285_03.htm";
			}
			else if (cond == 10)
			{
				htmltext = "rafforty_q10285_04.htm";
				st.giveItems(ADENA_ID, 283425);
				st.addExpAndSp(939075, 83855);
				st.setState(COMPLETED);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
			break;
		case Jinia:
			switch (cond)
			{
			case 2:
				htmltext = "jinia_q10285_01.htm";
				break;
			case 4:
				htmltext = "jinia_q10285_03.htm";
				break;
			case 6:
				htmltext = "jinia_q10285_05.htm";
				break;
			case 7:
				htmltext = "jinia_q10285_10.htm";
				break;
			default:
				break;
			}
			break;
		case Kegor:
			if (cond == 3)
			{
				htmltext = "kegor_q10285_01.htm";
			}
			break;
		case Sirra:
			if (cond == 5)
			{
				htmltext = "sirra_q10285_01.htm";
			}
			break;
		case Jinia2:
			if (cond == 7 || cond == 8)
			{
				st.setCond(8);
				htmltext = "jinia2_q10285_01.htm";
			}
			else if (cond == 9)
			{
				htmltext = "jinia2_q10285_02.htm";
			}
			break;
		default:
			break;
		}
		return htmltext;
	}

	private void enterInstance(Player player, int izId)
	{
		Reflection r = player.getActiveReflection();
		if (r != null)
		{
			if (player.canReenterInstance(izId))
			{
				player.teleToLocation(r.getTeleportLoc(), r);
			}
		}
		else if (player.canEnterInstance(izId))
		{
			Reflection newInstance = ReflectionUtils.enterReflection(player, izId);
			if (izId == 137)
			{
				ThreadPoolManager.getInstance().schedule(new FreyaSpawn(newInstance, player), 2 * 60 * 1000L);
			}
		}
	}

	private class FreyaSpawn extends RunnableImpl
	{
		private Player _player;
		private Reflection _r;

		public FreyaSpawn(Reflection r, Player player)
		{
			_r = r;
			_player = player;
		}

		@Override
		public void runImpl() throws Exception
		{
			if (_r != null)
			{
				NpcInstance freya = _r.addSpawnWithoutRespawn(18847, new Location(114720, -117085, -11088, 15956), 0);
				ThreadPoolManager.getInstance().schedule(new FreyaMovie(_player, _r, freya), 2 * 60 * 1000L);
			}
		}
	}

	private class FreyaMovie extends RunnableImpl
	{
		Player _player;
		Reflection _r;
		NpcInstance _npc;

		public FreyaMovie(Player player, Reflection r, NpcInstance npc)
		{
			_player = player;
			_r = r;
			_npc = npc;
		}

		@Override
		public void runImpl() throws Exception
		{
			for (Spawner sp : _r.getSpawns())
			{
				sp.deleteAll();
			}
			if (_npc != null && !_npc.isDead())
			{
				_npc.deleteMe();
			}
			_player.showQuestMovie(ExStartScenePlayer.SCENE_BOSS_FREYA_FORCED_DEFEAT);
			ThreadPoolManager.getInstance().schedule(new ResetInstance(_player, _r), 23000L);
		}
	}

	private class ResetInstance extends RunnableImpl
	{
		Player _player;
		Reflection _r;

		public ResetInstance(Player player, Reflection r)
		{
			_player = player;
			_r = r;
		}

		@Override
		public void runImpl() throws Exception
		{
			_player.getQuestState(_10285_MeetingSirra.class).setCond(10);
			_r.collapse();
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