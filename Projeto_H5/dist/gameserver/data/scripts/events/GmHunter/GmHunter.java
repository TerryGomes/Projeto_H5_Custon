package events.GmHunter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastMap;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Announcements;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.instancemanager.MapRegionManager;
import l2mv.gameserver.listener.actor.OnAttackListener;
import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.RadarControl;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.stats.funcs.FuncSet;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.mapregion.RestartArea;
import l2mv.gameserver.templates.mapregion.RestartPoint;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Location;

public class GmHunter extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener, OnAttackListener
{
	private static final Logger _log = LoggerFactory.getLogger(GmHunter.class);
	private static boolean _active = false;
	private static final List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();

	private static final int EVENT_MANAGER_ID = 10003; // Event manager..

	private static FastMap<String, Location> _gminfo = new FastMap<String, Location>();

	public void startEvent()
	{
		Player player = getSelf();

		if (SetActive("GmHunter", true))
		{
			spawnEventManagers();

			player.setKarma(1000);
			player.setPkKills(0);
			// player.addStatFunc(new FuncSet(Stats.RUN_SPEED, 0x90, player, Config.GM_HUNTER_EVENT_SET_SPEED));
			player.addStatFunc(new FuncSet(Stats.POWER_DEFENCE, 0x90, player, Config.GM_HUNTER_EVENT_SET_PDEFENCE));
			player.addStatFunc(new FuncSet(Stats.MAGIC_DEFENCE, 0x90, player, Config.GM_HUNTER_EVENT_SET_MDEFENCE));
			player.addStatFunc(new FuncSet(Stats.MAX_HP, 0x90, player, Config.GM_HUNTER_EVENT_SET_HP));
			player.addStatFunc(new FuncSet(Stats.MAX_CP, 0x90, player, Config.GM_HUNTER_EVENT_SET_CP));
			player.sendUserInfo();

			player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
			player.setCurrentCp(player.getMaxCp());
			player.broadcastUserInfo(true);
			player.sendChanges();

			System.out.println("Event 'GM Hunter' started.");
			Announcements.getInstance().announceToAll("GmHunter Event has been started. Visit the NPC for more information.");
			Announcements.getInstance().announceToAll("GmHunter Event: Find and destroy " + player.getName());
			_gminfo.put(player.getName(), player.getLoc());
		}
		else
		{
			player.sendMessage(new CustomMessage("scripts.events.gmhunter.started", player));
		}

		_active = true;
		show("admin/events/events.htm", player);
	}

	public void stopEvent()
	{
		Player player = getSelf();

		if (SetActive("GmHunter", false))
		{
			unSpawnEventManagers();
			System.out.println("Event 'GM Hunter' stopped.");
			Announcements.getInstance().announceToAll("GmHunter Event has been stopped.");
			player.removeStatsOwner(player);
			player.getPlayer().setKarma(0);
			_gminfo.clear();
		}
		else
		{
			player.sendMessage(new CustomMessage("scripts.events.gmhunter.stopped", player));
		}

		_active = false;
		show("admin/events/events.htm", player);
	}

	@Override
	public void onAttack(Creature actor, Creature target)
	{
		if (_active && actor.isPlayer() && target.isPlayer() && target.getPlayer().isGM())
		{
			ItemInstance item = ItemFunctions.createItem(57);
			item.setCount(Rnd.get(10000, 100000));
			item.dropMe(target, target.getLoc().rnd(0, 100, false));
		}
	}

	@Override
	public void onDeath(Creature cha, Creature killer)
	{
		if (_active && cha != null && killer != null && killer.isPlayer() && cha.isPlayer() && cha.getPlayer().isGM())
		{
			for (String rewards : Config.GM_HUNTER_EVENT_REWARDS)
			{
				String[] reward2 = rewards.split(",");

				int id = Integer.parseInt(reward2[0]);
				long mincount = Long.parseLong(reward2[1]);
				long maxcount = Long.parseLong(reward2[2]);
				int chance = Integer.parseInt(reward2[3]);

				long rnddrop = Rnd.get(mincount, maxcount);

				if (Rnd.get(100) < chance)
				{
					ItemTemplate item = ItemHolder.getInstance().getTemplate(id);

					SystemMessage2 sm;
					if (item.getItemId() == 57)
					{
						sm = new SystemMessage2(SystemMsg.C1_HAS_DIED_AND_DROPPED_S2_ADENA);
						sm.addName(cha);
						sm.addLong(rnddrop);
					}
					else
					{
						sm = new SystemMessage2(SystemMsg.C1_DIED_AND_DROPPED_S3_S2);
						sm.addName(cha);
						sm.addItemName(item.getItemId());
						sm.addLong(rnddrop);
					}

					cha.broadcastPacket(sm);

					Functions.addItem(killer.getPlayer(), id, rnddrop, "added droped item");
					break;
				}
			}

			cha.removeStatsOwner(cha);
			cha.getPlayer().setKarma(0);
			_active = false;
			SetActive("GmHunter", false);
			System.out.println("Event 'GM Hunter' finished.");
			_gminfo.clear();
			unSpawnEventManagers();
			Announcements.getInstance().announceToAll("GmHunter Event: " + killer.getName() + " has killed our EventGM: " + cha.getName() + " and with that event has finished!");
		}
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		if (_active)
		{
			player.sendChatMessage(player.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "GmHunter", "Hunt the GM head event is active! Visit the NPC for more information.");
		}
	}

	private static boolean isActive()
	{
		return IsActive("GmHunter");
	}

	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] =
		{
			{
				146936,
				26654,
				-2208,
				16384
			}, // Aden
			{
				82168,
				148842,
				-3464,
				7806
			}, // Giran
			{
				82204,
				53259,
				-1488,
				16384
			}, // Oren
			{
				18924,
				145782,
				-3088,
				44034
			}, // Dion
			{
				111794,
				218967,
				-3536,
				20780
			}, // Heine
			{
				-14539,
				124066,
				-3112,
				50874
			}, // Gludio
			{
				147271,
				-55573,
				-2736,
				60304
			}, // Goddard
			{
				87801,
				-143150,
				-1296,
				28800
			}, // Shuttgard
			{
				-80684,
				149458,
				-3040,
				16384
			}, // Gludin
		};

		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
	}

	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		if (isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: Gm Hunter [state: activated]");
		}
		else
		{
			_log.info("Loaded Event: Gm Hunter [state: deactivated]");
		}

	}

	@Override
	public void onReload()
	{
		unSpawnEventManagers();
	}

	@Override
	public void onShutdown()
	{
		onReload();
	}

	public String DialogAppend_10003(Integer val)
	{
		if (val != 0)
		{
			return "";
		}
		return OutDia();
	}

	public String OutDia()
	{
		Player activeChar = getSelf();
		String append = HtmCache.getInstance().getNotNull("events/GmHunter/index.htm", activeChar);

		append = append.replaceFirst("%Name%", activeChar.getName());

		String gmname = "";
		Location gmloc = null;

		for (Entry<String, Location> data : _gminfo.entrySet())
		{
			gmname = data.getKey();
			gmloc = data.getValue();
		}

		if (gmname == null || gmloc == null)
		{
			return "";
		}

		append = append.replaceAll("%gmName%", gmname);

		RestartArea ra = MapRegionManager.getInstance().getRegionData(RestartArea.class, gmloc);
		String nearestTown = "";
		if (ra != null)
		{
			RestartPoint rp = ra.getRestartPoint().get(Race.human);
			nearestTown = rp.getNameLoc();
		}

		append = append.replaceAll("%gmposition%", nearestTown);

		return append;
	}

	public void setTarget()
	{
		Player activeChar = getSelf();

		String gmname = "";
		for (Entry<String, Location> data : _gminfo.entrySet())
		{
			gmname = data.getKey();
		}

		if (gmname.isEmpty() || gmname == null)
		{
			return;
		}

		Player gmchar = World.getPlayer(gmname);

		if (gmchar == null)
		{
			return;
		}

		if (activeChar != null)
		{
			activeChar.sendPacket(new RadarControl(2, 2, gmchar.getLoc()), new RadarControl(0, 1, gmchar.getLoc()));
		}
	}

	public void viewRewards()
	{
		Player activeChar = getSelf();

		String html = HtmCache.getInstance().getNotNull("events/GmHunter/rewards.htm", activeChar);

		NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
		npcHtmlMessage.setHtml(html);
		npcHtmlMessage.replace("%rewards%", getRewards());
		activeChar.sendPacket(npcHtmlMessage);

	}

	private static String _rewardsHtml = "<html><body>Error 404. Page Not Found </body></html>";

	private static String getRewards()
	{
		for (String rewards : Config.GM_HUNTER_EVENT_REWARDS)
		{
			String[] reward = rewards.split(",");

			int id = Integer.parseInt(reward[0]);
			long mincount = Long.parseLong(reward[1]);
			long maxcount = Long.parseLong(reward[2]);
			int chance = Integer.parseInt(reward[3]);

			String dropcount = "Min. " + mincount + " Max. " + maxcount + "";

			String icon = ItemHolder.getInstance().getTemplate(id).getIcon();
			if (icon == null || icon.equals(StringUtils.EMPTY))
			{
				icon = "icon.etc_question_mark_i00";
			}

			_rewardsHtml += "<tr><td width=40 height=35><img src=" + icon + " width=32 height=32></td></tr>";
			_rewardsHtml += "<tr><td width=150 height=15><font color=LEVEL>" + ItemHolder.getInstance().getTemplate(id).getName() + "</font></td></tr>";
			_rewardsHtml += "<tr><td width=150 height=15><font color=7FFFD4>Count:</font> " + dropcount + ", <font color=7FFF00>Chance:</font> " + chance + "%</td></tr>";
		}

		return _rewardsHtml;
	}
}