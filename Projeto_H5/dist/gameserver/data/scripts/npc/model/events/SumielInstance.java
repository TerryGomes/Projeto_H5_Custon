package npc.model.events;

import java.util.concurrent.ScheduledFuture;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;

/**
 * @author claww
 */
@SuppressWarnings("serial")
public class SumielInstance extends NpcInstance
{
	private Player c_ai0 = null;
	private Player c_ai1 = null;
	private int i_ai0 = 0;
	private int i_ai1 = 0;
	private int i_ai2 = 0;
	private int i_ai3 = 0;
	private int i_ai4 = 0;
	private int i_ai5 = 0;
	private int i_ai6 = 0;
	private int i_ai7 = 0;
	private int i_ai8 = 0;
	private int i_ai9 = 0;
	private int i_quest0 = 0;
	private int i_quest1 = 0;
	private int i_quest2 = 0;
	private int i_quest9 = 0;
	private int interval_time = 3;
	private ScheduledFuture<?> HURRY_UP_1;
	private ScheduledFuture<?> HURRY_UP2_1;
	private ScheduledFuture<?> HURRY_UP_2;
	private ScheduledFuture<?> HURRY_UP2_2;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> GAME_TIME;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> PC_TURN;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> GAME_TIME_EXPIRED;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> TIMER_0;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> TIMER_1;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> TIMER_2;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> TIMER_3;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> TIMER_4;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> TIMER_5;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> TIMER_6;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> TIMER_7;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> TIMER_8;
	@SuppressWarnings("unused")
	private ScheduledFuture<?> TIMER_9;
	@SuppressWarnings("unused")
	private long _storage;

	public SumielInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		_storage = getStoredId();
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if (val == 0)
		{
			String htmlpath = null;
			if (c_ai0 == null && i_quest2 == 0)
			{
				htmlpath = "event/monastyre/minigame_instructor001.htm";
				c_ai1 = player;
			}
			else if (c_ai0 == null && i_quest2 == 1)
			{
				htmlpath = "event/monastyre/minigame_instructor008.htm";
			}
			else if (c_ai0 == player && i_quest0 == 1 && i_quest1 == 0)
			{
				htmlpath = "event/monastyre/minigame_instructor002.htm";
			}
			else if (c_ai0 == player && i_quest0 == 2 && i_quest1 == 0)
			{
				htmlpath = "event/monastyre/minigame_instructor003.htm";
			}
			else if (c_ai0 != player)
			{
				htmlpath = "event/monastyre/minigame_instructor004.htm";
			}
			else if (c_ai0 == player && i_quest1 == 1)
			{
				htmlpath = "event/monastyre/minigame_instructor007.htm";
			}

			player.sendPacket(new NpcHtmlMessage(player, this, htmlpath, val));
		}
		else
		{
			super.showChatWindow(player, val);
		}
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.equals("teleport"))
		{
			showChatWindow(player, "event/monastyre/minigame_instructor006.htm");
		}
		else if (command.equals("teleport1"))
		{
			switch (getAISpawnParam())
			{
			case 1:
				player.teleToLocation(110705, -81328, -1600);
				break;
			case 2:
				player.teleToLocation(114866, -71627, -560);
				break;
			}
		}
		else if (command.equals("teleport2"))
		{
			player.teleToLocation(110712, -81352, -2688);
		}
		else if (command.equals("start"))
		{
			if (player.getInventory().getCountOf(15540) == 0)
			{
				showChatWindow(player, "event/monastyre/minigame_instructor005.htm");
			}
			else if (c_ai1 != player)
			{
				showChatWindow(player, "event/monastyre/minigame_instructor004.htm");
			}
			else if (c_ai1 == player)
			{
				switch (getAISpawnParam())
				{
				case 1:
					if (HURRY_UP_1 != null)
					{
						HURRY_UP_1.cancel(false);
						HURRY_UP_1 = null;
					}
					if (HURRY_UP2_1 != null)
					{
						HURRY_UP2_1.cancel(false);
						HURRY_UP2_1 = null;
					}
					break;
				case 2:
					if (HURRY_UP_2 != null)
					{
						HURRY_UP_2.cancel(false);
						HURRY_UP_2 = null;
					}
					if (HURRY_UP2_2 != null)
					{
						HURRY_UP2_2.cancel(false);
						HURRY_UP2_2 = null;
					}
					break;
				}

				player.getInventory().getItemByItemId(15540);
				player.getInventory().destroyItemByItemId(15540, 1, "SumielInstance");
				player.getInventory().addItem(15485, 1, "SumielInstance");
				Functions.npcShout(this, NpcString.FURNFACE1);
				i_ai1 = Rnd.get(9) + 1;
				i_ai2 = Rnd.get(9) + 1;
				i_ai3 = Rnd.get(9) + 1;
				i_ai4 = Rnd.get(9) + 1;
				i_ai5 = Rnd.get(9) + 1;
				i_ai6 = Rnd.get(9) + 1;
				i_ai7 = Rnd.get(9) + 1;
				i_ai8 = Rnd.get(9) + 1;
				i_ai9 = Rnd.get(9) + 1;
				c_ai0 = player;
				if (i_quest9 == 1)
				{
				}

				switch (getAISpawnParam())
				{
				case 1:
					HURRY_UP_1 = ThreadPoolManager.getInstance().schedule(new HURRY_UP(), 2 * 60 * 1000);
					break;
				case 2:
					HURRY_UP_2 = ThreadPoolManager.getInstance().schedule(new HURRY_UP(), 2 * 60 * 1000);
					break;
				}
				GAME_TIME = ThreadPoolManager.getInstance().schedule(new GAME_TIME(), 3 * 60 * 1000 + 10 * 1000);
				TIMER_0 = ThreadPoolManager.getInstance().schedule(new TIMER_0(), 1 * 1000);
			}
			else if (command.equals("restart"))
			{
				i_quest1 = 1;
				i_ai1 = Rnd.get(9) + 1;
				i_ai2 = Rnd.get(9) + 1;
				i_ai3 = Rnd.get(9) + 1;
				i_ai4 = Rnd.get(9) + 1;
				i_ai5 = Rnd.get(9) + 1;
				i_ai6 = Rnd.get(9) + 1;
				i_ai7 = Rnd.get(9) + 1;
				i_ai8 = Rnd.get(9) + 1;
				i_ai9 = Rnd.get(9) + 1;
				c_ai0 = player;
				if (i_quest9 == 1)
				{
				}
				TIMER_0 = ThreadPoolManager.getInstance().schedule(new TIMER_0(), 1 * 1000);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	private class TIMER_0 extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (NpcInstance npc : GameObjectsStorage.getAllNpcs())
			{
				if (npc != null && npc.getNpcId() == 18913 && getDistance(npc) <= 1200)
				{
					((FurnfaceInstance) npc).setActive2114002();
				}
			}
			TIMER_1 = ThreadPoolManager.getInstance().schedule(new TIMER_1(), interval_time * 2000);
		}
	}

	private class TIMER_1 extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (NpcInstance npc : GameObjectsStorage.getAllNpcs())
			{
				if (npc != null && npc.getNpcId() == 18913 && getDistance(npc) <= 1200)
				{
					((FurnfaceInstance) npc).setActive2114001(i_ai1);
				}
			}
			TIMER_2 = ThreadPoolManager.getInstance().schedule(new TIMER_2(), interval_time * 1000);
		}
	}

	private class TIMER_2 extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (NpcInstance npc : GameObjectsStorage.getAllNpcs())
			{
				if (npc != null && npc.getNpcId() == 18913 && getDistance(npc) <= 1200)
				{
					((FurnfaceInstance) npc).setActive2114001(i_ai2);
				}
			}
			TIMER_3 = ThreadPoolManager.getInstance().schedule(new TIMER_3(), interval_time * 1000);
		}
	}

	private class TIMER_3 extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (NpcInstance npc : GameObjectsStorage.getAllNpcs())
			{
				if (npc != null && npc.getNpcId() == 18913 && getDistance(npc) <= 1200)
				{
					((FurnfaceInstance) npc).setActive2114001(i_ai3);
				}
			}
			TIMER_4 = ThreadPoolManager.getInstance().schedule(new TIMER_4(), interval_time * 1000);
		}
	}

	private class TIMER_4 extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (NpcInstance npc : GameObjectsStorage.getAllNpcs())
			{
				if (npc != null && npc.getNpcId() == 18913 && getDistance(npc) <= 1200)
				{
					((FurnfaceInstance) npc).setActive2114001(i_ai4);
				}
			}
			TIMER_5 = ThreadPoolManager.getInstance().schedule(new TIMER_5(), interval_time * 1000);
		}
	}

	private class TIMER_5 extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (NpcInstance npc : GameObjectsStorage.getAllNpcs())
			{
				if (npc != null && npc.getNpcId() == 18913 && getDistance(npc) <= 1200)
				{
					((FurnfaceInstance) npc).setActive2114001(i_ai5);
				}
			}
			TIMER_6 = ThreadPoolManager.getInstance().schedule(new TIMER_6(), interval_time * 1000);
		}
	}

	private class TIMER_6 extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (NpcInstance npc : GameObjectsStorage.getAllNpcs())
			{
				if (npc != null && npc.getNpcId() == 18913 && getDistance(npc) <= 1200)
				{
					((FurnfaceInstance) npc).setActive2114001(i_ai6);
				}
			}
			TIMER_7 = ThreadPoolManager.getInstance().schedule(new TIMER_7(), interval_time * 1000);
		}
	}

	private class TIMER_7 extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (NpcInstance npc : GameObjectsStorage.getAllNpcs())
			{
				if (npc != null && npc.getNpcId() == 18913 && getDistance(npc) <= 1200)
				{
					((FurnfaceInstance) npc).setActive2114001(i_ai7);
				}
			}
			TIMER_8 = ThreadPoolManager.getInstance().schedule(new TIMER_8(), interval_time * 1000);
		}
	}

	private class TIMER_8 extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (NpcInstance npc : GameObjectsStorage.getAllNpcs())
			{
				if (npc != null && npc.getNpcId() == 18913 && getDistance(npc) <= 1200)
				{
					((FurnfaceInstance) npc).setActive2114001(i_ai8);
				}
			}
			TIMER_9 = ThreadPoolManager.getInstance().schedule(new TIMER_9(), interval_time * 1000);
		}
	}

	private class TIMER_9 extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (NpcInstance npc : GameObjectsStorage.getAllNpcs())
			{
				if (npc != null && npc.getNpcId() == 18913 && getDistance(npc) <= 1200)
				{
					((FurnfaceInstance) npc).setActive2114001(i_ai9);
				}
			}
			PC_TURN = ThreadPoolManager.getInstance().schedule(new PC_TURN(), interval_time * 1000);
		}
	}

	private class HURRY_UP extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			NpcInstance npc = GameObjectsStorage.getAsNpc(_storedId);
			Functions.npcShout(npc, NpcString.FURNFACE2);
			switch (getAISpawnParam())
			{
			case 1:
				HURRY_UP2_1 = ThreadPoolManager.getInstance().schedule(new HURRY_UP2(), 60 * 1000);
				break;
			case 2:
				HURRY_UP2_2 = ThreadPoolManager.getInstance().schedule(new HURRY_UP2(), 60 * 1000);
				break;
			}
		}
	}

	private class HURRY_UP2 extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			NpcInstance npc = GameObjectsStorage.getAsNpc(_storedId);
			Functions.npcShout(npc, NpcString.FURNFACE3);
			GAME_TIME_EXPIRED = ThreadPoolManager.getInstance().schedule(new GAME_TIME_EXPIRED(), 10 * 1000);
		}
	}

	private class PC_TURN extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			NpcInstance npc1 = GameObjectsStorage.getAsNpc(_storedId);
			Functions.npcShout(npc1, NpcString.FURNFACE4);
			for (NpcInstance npc : GameObjectsStorage.getAllNpcs())
			{
				if (npc != null && npc.getNpcId() == 18913 && getDistance(npc) <= 1200)
				{
					((FurnfaceInstance) npc).setSCE_GAME_PLAYER_START();
				}
			}
			i_ai0 = 1;
		}
	}

	private class GAME_TIME_EXPIRED extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			NpcInstance npc1 = GameObjectsStorage.getAsNpc(_storedId);
			Functions.npcShout(npc1, NpcString.FURNFACE5);
			for (NpcInstance npc : GameObjectsStorage.getAllNpcs())
			{
				if (npc != null && npc.getNpcId() == 18913 && getDistance(npc) <= 1200)
				{
					((FurnfaceInstance) npc).setSCE_GAME_END();
				}
			}
			c_ai0 = null;
			i_quest0 = 0;
			i_quest1 = 0;
		}
	}

	private class GAME_TIME extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			i_quest2 = 0;
		}
	}

	public void setSCE_POT_ON(int i)
	{
		if (i == i_ai1 && i_ai0 == 1)
		{
			if (i_quest9 == 1)
			{
			}
			i_ai0 = 2;
		}
		else if (i == i_ai2 && i_ai0 == 2)
		{
			if (i_quest9 == 1)
			{
			}
			i_ai0 = 3;
		}
		else if (i == i_ai3 && i_ai0 == 3)
		{
			if (i_quest9 == 1)
			{
			}
			i_ai0 = 4;
		}
		else if (i == i_ai4 && i_ai0 == 4)
		{
			if (i_quest9 == 1)
			{
			}
			i_ai0 = 5;
		}
		else if (i == i_ai5 && i_ai0 == 5)
		{
			if (i_quest9 == 1)
			{
			}
			i_ai0 = 6;
		}
		else if (i == i_ai6 && i_ai0 == 6)
		{
			if (i_quest9 == 1)
			{
			}
			i_ai0 = 7;
		}
		else if (i == i_ai7 && i_ai0 == 7)
		{
			if (i_quest9 == 1)
			{
			}
			i_ai0 = 8;
		}
		else if (i == i_ai8 && i_ai0 == 8)
		{
			if (i_quest9 == 1)
			{
			}
			i_ai0 = 9;
		}
		else if (i == i_ai9 && i_ai0 == 9)
		{
			if (i_quest9 == 1)
			{
			}
			for (NpcInstance npc : GameObjectsStorage.getAllNpcs())
			{
				if (npc != null && npc.getNpcId() == 18913 && getDistance(npc) <= 1200)
				{
					((FurnfaceInstance) npc).setSCE_GAME_END();
				}
			}

			SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(18934));
			switch (getAISpawnParam())
			{
			case 1:
				sp.setLoc(new Location(110772, -82063, -1584));
				break;
			case 2:
				sp.setLoc(new Location(114915, -70998, -544));
				break;
			}
			sp.doSpawn(true);
			Functions.npcShout(this, NpcString.FURNFACE6);
			switch (getAISpawnParam())
			{
			case 1:
				if (HURRY_UP_1 != null)
				{
					HURRY_UP_1.cancel(false);
					HURRY_UP_1 = null;
				}
				if (HURRY_UP2_1 != null)
				{
					HURRY_UP2_1.cancel(false);
					HURRY_UP2_1 = null;
				}
				break;
			case 2:
				if (HURRY_UP_2 != null)
				{
					HURRY_UP_2.cancel(false);
					HURRY_UP_2 = null;
				}
				if (HURRY_UP2_2 != null)
				{
					HURRY_UP2_2.cancel(false);
					HURRY_UP2_2 = null;
				}
				break;
			}
			c_ai0 = null;
			i_quest0 = 0;
			i_quest1 = 0;
		}
		else
		{
			for (NpcInstance npc : GameObjectsStorage.getAllNpcs())
			{
				if (npc != null && npc.getNpcId() == 18913 && getDistance(npc) <= 1200)
				{
					((FurnfaceInstance) npc).setSCE_GAME_FAILURE();
				}
			}
			if (i_quest9 == 1)
			{
			}
			else if (i_quest0 < 2)
			{
				i_quest0 = i_quest0 + 1;
				Functions.npcShout(this, NpcString.FURNFACE7);
				i_quest1 = 0;
			}
			else
			{
				switch (getAISpawnParam())
				{
				case 1:
					if (HURRY_UP_1 != null)
					{
						HURRY_UP_1.cancel(false);
						HURRY_UP_1 = null;
					}
					if (HURRY_UP2_1 != null)
					{
						HURRY_UP2_1.cancel(false);
						HURRY_UP2_1 = null;
					}
					break;
				case 2:
					if (HURRY_UP_2 != null)
					{
						HURRY_UP_2.cancel(false);
						HURRY_UP_2 = null;
					}
					if (HURRY_UP2_2 != null)
					{
						HURRY_UP2_2.cancel(false);
						HURRY_UP2_2 = null;
					}
					break;
				}
				Functions.npcShout(this, NpcString.FURNFACE8);
				c_ai0 = null;
				i_quest0 = 0;
				i_quest1 = 0;
			}
		}
	}
}