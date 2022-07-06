package l2mv.gameserver.scripts;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.lang.reference.HardReference;
import l2mv.commons.lang.reference.HardReferences;
import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.instancemanager.ServerVariables;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.Summon;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.network.serverpackets.ExNoticePostArrived;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.NpcSay;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.MapUtils;
import l2mv.gameserver.utils.NpcUtils;
import l2mv.gameserver.utils.Strings;

public class Functions
{
	private static final Logger _log = LoggerFactory.getLogger(Functions.class);

	public HardReference<Player> self = HardReferences.emptyRef();
	public HardReference<NpcInstance> npc = HardReferences.emptyRef();

	public static ScheduledFuture<?> executeTask(Player caller, String className, String methodName, Object[] args, Map<String, Object> variables, long delay)
	{
		return ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				callScripts(caller, className, methodName, args, variables);
			}
		}, delay);
	}

	public static ScheduledFuture<?> executeTask(String className, String methodName, Object[] args, Map<String, Object> variables, long delay)
	{
		return executeTask(null, className, methodName, args, variables, delay);
	}

	public static ScheduledFuture<?> executeTask(Player player, String className, String methodName, Object[] args, long delay)
	{
		return executeTask(player, className, methodName, args, null, delay);
	}

	public static ScheduledFuture<?> executeTask(String className, String methodName, Object[] args, long delay)
	{
		return executeTask(className, methodName, args, null, delay);
	}

	public static Object callScripts(String className, String methodName, Object[] args)
	{
		return callScripts(className, methodName, args, null);
	}

	public static Object callScripts(String className, String methodName, Object[] args, Map<String, Object> variables)
	{
		return callScripts(null, className, methodName, args, variables);
	}

	public static Object callScripts(Player player, String className, String methodName, Object[] args, Map<String, Object> variables)
	{
		return Scripts.getInstance().callScripts(player, className, methodName, args, variables);
	}

	public void show(String text, Player self)
	{
		show(text, self, getNpc());
	}

	public static void show(String text, Player self, NpcInstance npc, Object... arg)
	{
		if (text == null || self == null)
		{
			return;
		}

		NpcHtmlMessage msg = new NpcHtmlMessage(self, npc);

		if (text.endsWith(".html") || text.endsWith(".htm"))
		{
			msg.setFile(text);
		}
		else
		{
			msg.setHtml(Strings.bbParse(text));
		}

		if (arg != null && arg.length % 2 == 0)
		{
			for (int i = 0; i < arg.length; i = +2)
			{
				msg.replace(String.valueOf(arg[i]), String.valueOf(arg[i + 1]));
			}
		}

		self.sendPacket(msg);
	}

	public static void show(CustomMessage message, Player self)
	{
		show(message.toString(), self, null);
	}

	public static void sendMessage(String text, Player self)
	{
		self.sendMessage(text);
	}

	public static void sendMessage(CustomMessage message, Player self)
	{
		self.sendMessage(message);
	}

	public static void npcSayInRange(NpcInstance npc, String text, int range)
	{
		npcSayInRange(npc, range, NpcString.NONE, text);
	}

	public static void npcSayInRange(NpcInstance npc, int range, NpcString fStringId, String... params)
	{
		if (npc == null)
		{
			return;
		}
		NpcSay cs = new NpcSay(npc, ChatType.ALL, fStringId, params);
		for (Player player : World.getAroundPlayers(npc, range, Math.max(range / 2, 200)))
		{
			if (npc.getReflection() == player.getReflection())
			{
				player.sendPacket(cs);
			}
		}
	}

	public static void npcSay(NpcInstance npc, String text)
	{
		npcSayInRange(npc, text, 1500);
	}

	public static void npcSay(NpcInstance npc, NpcString npcString, String... params)
	{
		npcSayInRange(npc, 1500, npcString, params);
	}

	public static void npcSayInRangeCustomMessage(NpcInstance npc, int range, String address, Object... replacements)
	{
		if (npc == null)
		{
			return;
		}
		for (Player player : World.getAroundPlayers(npc, range, Math.max(range / 2, 200)))
		{
			if (npc.getReflection() == player.getReflection())
			{
				player.sendPacket(new NpcSay(npc, ChatType.ALL, new CustomMessage(address, player, replacements).toString()));
			}
		}
	}

	public static void npcSayCustomMessage(NpcInstance npc, String address, Object... replacements)
	{
		npcSayInRangeCustomMessage(npc, 1500, address, replacements);
	}

	// private message
	public static void npcSayToPlayer(NpcInstance npc, Player player, String text)
	{
		npcSayToPlayer(npc, player, NpcString.NONE, text);
	}

	// private message
	public static void npcSayToPlayer(NpcInstance npc, Player player, NpcString npcString, String... params)
	{
		if (npc == null)
		{
			return;
		}
		player.sendPacket(new NpcSay(npc, ChatType.TELL, npcString, params));
	}

	public static void npcShout(NpcInstance npc, String text)
	{
		npcShout(npc, NpcString.NONE, text);
	}

	public static void npcShout(NpcInstance npc, NpcString npcString, String... params)
	{
		if (npc == null)
		{
			return;
		}
		NpcSay cs = new NpcSay(npc, ChatType.SHOUT, npcString, params);

		int rx = MapUtils.regionX(npc);
		int ry = MapUtils.regionY(npc);
		int offset = Config.SHOUT_OFFSET;

		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.getReflection() != npc.getReflection())
			{
				continue;
			}

			int tx = MapUtils.regionX(player);
			int ty = MapUtils.regionY(player);

			if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset)
			{
				player.sendPacket(cs);
			}
		}
	}

	public static void npcShoutCustomMessage(NpcInstance npc, String address, Object... replacements)
	{
		if (npc == null)
		{
			return;
		}

		int rx = MapUtils.regionX(npc);
		int ry = MapUtils.regionY(npc);
		int offset = Config.SHOUT_OFFSET;

		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.getReflection() != npc.getReflection())
			{
				continue;
			}

			int tx = MapUtils.regionX(player);
			int ty = MapUtils.regionY(player);

			if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset || npc.isInRange(player, Config.CHAT_RANGE))
			{
				player.sendPacket(new NpcSay(npc, ChatType.SHOUT, new CustomMessage(address, player, replacements).toString()));
			}
		}
	}

	public static void npcSay(NpcInstance npc, NpcString address, ChatType type, int range, String... replacements)
	{
		if (npc == null)
		{
			return;
		}
		for (Player player : World.getAroundPlayers(npc, range, Math.max(range / 2, 200)))
		{
			if (player.getReflection() == npc.getReflection())
			{
				player.sendPacket(new NpcSay(npc, type, address, replacements));
			}
		}
	}

	/**
	 * @param playable
	 * @param itemId
	 * @param count
	 * @param log
	 * @see ItemFunctions
	 */
	public static void addItem(Playable playable, int itemId, long count, String log)
	{
		ItemFunctions.addItem(playable, itemId, count, true, log);
	}

	/**
	 * @param playable
	 * @param itemId
	 * @param count
	 * @param mess
	 * @param log
	 * @see ItemFunctions
	 */
	public static void addItem(Playable playable, int itemId, long count, boolean mess, String log)
	{
		ItemFunctions.addItem(playable, itemId, count, mess, log);
	}

	/**
	 * @param playable
	 * @param itemId
	 * @return
	 * @see ItemFunctions
	 */
	public static long getItemCount(Playable playable, int itemId)
	{
		return ItemFunctions.getItemCount(playable, itemId);
	}

	/**
	 * @param playable
	 * @param itemId
	 * @param count
	 * @param log
	 * @return
	 * @see ItemFunctions
	 */
	public static long removeItem(Playable playable, int itemId, long count, String log)
	{
		return ItemFunctions.removeItem(playable, itemId, count, true, log);
	}

	public static boolean ride(Player player, int pet)
	{
		if (player.isMounted())
		{
			player.setMount(0, 0, 0);
		}

		if (player.getPet() != null)
		{
			player.sendPacket(SystemMsg.YOU_ALREADY_HAVE_A_PET);
			return false;
		}

		player.setMount(pet, 0, 0);
		return true;
	}

	public static void unRide(Player player)
	{
		if (player.isMounted())
		{
			player.setMount(0, 0, 0);
		}
	}

	public static void unSummonPet(Player player, boolean onlyPets)
	{
		Summon pet = player.getPet();
		if (pet == null)
		{
			return;
		}
		if (pet.isPet() || !onlyPets)
		{
			pet.unSummon();
		}
	}

	// @Deprecated
	// TODO [VISTALL] use NpcUtils
	public static NpcInstance spawn(Location loc, int npcId)
	{
		return spawn(loc, npcId, ReflectionManager.DEFAULT);
	}

	public static NpcInstance spawn(Location loc, int npcId, Reflection reflection)
	{
		return NpcUtils.spawnSingle(npcId, loc, reflection, 0);
	}

	public Player getSelf()
	{
		return self.get();
	}

	public NpcInstance getNpc()
	{
		return npc.get();
	}

	// @Deprecated
	// TODO [VISTALL] use NpcUtils
	public static void SpawnNPCs(int npcId, int[][] locations, List<SimpleSpawner> list)
	{
		NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
		if (template == null)
		{
			_log.warn("WARNING! Functions.SpawnNPCs template is null for npc: " + npcId);
			Thread.dumpStack();
			return;
		}
		for (int[] location : locations)
		{
			SimpleSpawner sp = new SimpleSpawner(template);
			sp.setLoc(new Location(location[0], location[1], location[2]));
			sp.setAmount(1);
			sp.setRespawnDelay(0);
			sp.init();
			if (list != null)
			{
				list.add(sp);
			}
		}
	}

	public static void SpawnNPCs(int npcId, int[][] locations, List<SimpleSpawner> list, int respawn)
	{
		NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
		if (template == null)
		{
			_log.warn("WARNING! Functions.SpawnNPCs template is null for npc: " + npcId);
			Thread.dumpStack();
			return;
		}
		for (int[] location : locations)
		{
			SimpleSpawner sp = new SimpleSpawner(template);
			sp.setLoc(new Location(location[0], location[1], location[2]));
			sp.setAmount(1);
			sp.setRespawnDelay(respawn);
			sp.setRespawnDelay(0);
			sp.init();
			if (list != null)
			{
				list.add(sp);
			}
		}
	}

	public static void deSpawnNPCs(List<SimpleSpawner> list)
	{
		for (SimpleSpawner sp : list)
		{
			sp.deleteAll();
		}

		list.clear();
	}

	public static boolean IsActive(String name)
	{
		return ServerVariables.getString(name, "off").equalsIgnoreCase("on");
	}

	public static boolean SetActive(String name, boolean active)
	{
		if (active == IsActive(name))
		{
			return false;
		}
		if (active)
		{
			ServerVariables.set(name, "on");
		}
		else
		{
			ServerVariables.unset(name);
		}
		return true;
	}

	public static boolean SimpleCheckDrop(Creature mob, Creature killer)
	{
		return mob != null && mob.isMonster() && !mob.isRaid() && killer != null && killer.getPlayer() != null && killer.getLevel() - mob.getLevel() < 9;
	}

	public static boolean isPvPEventStarted()
	{
		if (((Boolean) callScripts("events.TvT.TvT", "isRunned", new Object[0])).booleanValue() || ((Boolean) callScripts("events.lastHero.LastHero", "isRunned", new Object[] {})))
		{
			return true;
		}
		return false;
	}

	public static boolean isEventStarted(String event)
	{
		if ((Boolean) callScripts(event, "isRunned", new Object[] {}))
		{
			return true;
		}
		return false;
	}

	public static void sendDebugMessage(Player player, String message)
	{
		if (!player.isGM())
		{
			return;
		}
		player.sendMessage(message);
	}

	public static void sendSystemMail(Player receiver, String title, String body, Map<Integer, Long> items)
	{
		if (receiver == null || !receiver.isOnline() || (title == null) || (items.keySet().size() > 8))
		{
			return;
		}

		Mail mail = new Mail();
		mail.setSenderId(1);
		mail.setSenderName("Admin");
		mail.setReceiverId(receiver.getObjectId());
		mail.setReceiverName(receiver.getName());
		mail.setTopic(title);
		mail.setBody(body);
		for (Map.Entry<Integer, Long> itm : items.entrySet())
		{
			ItemInstance item = ItemFunctions.createItem(itm.getKey());
			item.setLocation(ItemInstance.ItemLocation.MAIL);
			item.setCount(itm.getValue());
			item.save();
			mail.addAttachment(item);
		}
		mail.setType(Mail.SenderType.NEWS_INFORMER);
		mail.setUnread(true);
		mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
		mail.save();

		receiver.sendPacket(ExNoticePostArrived.STATIC_TRUE);
		receiver.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
	}

	public static boolean sendSystemMail(String receiverName, String title, String body, Map<Integer, Long> items)
	{
		final Player receiver = GameObjectsStorage.getPlayer(receiverName);
		final int receiverObjectId = receiver != null ? receiver.getObjectId() : CharacterDAO.getInstance().getObjectIdByName(receiverName);
		return receiverObjectId > 0 && sendSystemMail(receiverName, receiverObjectId, title, body, items);
	}

	public static boolean sendSystemMail(int receiverObjectId, String title, String body, Map<Integer, Long> items)
	{
		final String receiverName = CharacterDAO.getNameByObjectId(receiverObjectId);
		return !receiverName.equals("") && sendSystemMail(receiverName, receiverObjectId, title, body, items);
	}

	public static boolean sendSystemMail(String receiverName, int receiverObjectId, String title, String body, Map<Integer, Long> items)
	{
		if (title == null || receiverObjectId <= 0 || (items.keySet().size() > 8))
		{
			return false;
		}

		final Mail mail = new Mail();
		mail.setSenderId(1);
		mail.setSenderName("Admin");
		mail.setReceiverId(receiverObjectId);
		mail.setReceiverName(receiverName);
		mail.setTopic(title);
		mail.setBody(body);
		for (Map.Entry<Integer, Long> itm : items.entrySet())
		{
			final ItemInstance item = ItemFunctions.createItem(itm.getKey());
			item.setLocation(ItemInstance.ItemLocation.MAIL);
			item.setCount(itm.getValue());
			item.save();
			mail.addAttachment(item);
		}
		mail.setType(Mail.SenderType.NEWS_INFORMER);
		mail.setUnread(true);
		mail.setExpireTime(2592000 + (int) (System.currentTimeMillis() / 1000L));
		mail.save();
		final Player receiver = GameObjectsStorage.getPlayer(receiverName);
		if (receiver != null)
		{
			receiver.sendPacket(ExNoticePostArrived.STATIC_TRUE);
			receiver.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
		}
		return true;
	}

	public static String GetStringCount(long count)
	{
		String scount = Long.toString(count);
		if (count < 1000)
		{
			return scount;
		}
		if ((count > 999) && (count < 1000000))
		{
			return scount.substring(0, scount.length() - 3) + " k";
		}
		if ((count > 999999) && (count < 1000000000))
		{
			return scount.substring(0, scount.length() - 6) + " kk";
		}
		if (count > 999999999)
		{
			return scount.substring(0, scount.length() - 9) + " kkk";
		}
		if (count == 0)
		{
			return "00.00";
		}
		return "ERROR";
	}
}