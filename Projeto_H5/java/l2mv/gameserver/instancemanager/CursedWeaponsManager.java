package l2mv.gameserver.instancemanager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ScheduledFuture;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import gnu.trove.map.hash.TIntObjectHashMap;
import l2mv.commons.dbutils.DbUtils;
import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.CursedWeapon;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.Location;

public class CursedWeaponsManager
{
	private static final Logger LOG = LoggerFactory.getLogger(CursedWeaponsManager.class);

	private static final CursedWeaponsManager _instance = new CursedWeaponsManager();

	public static final CursedWeaponsManager getInstance()
	{
		return _instance;
	}

	private CursedWeapon[] _cursedWeapons;
	private TIntObjectHashMap<CursedWeapon> _cursedWeaponsMap;
	private ScheduledFuture<?> _removeTask;

	private static final int CURSEDWEAPONS_MAINTENANCE_INTERVAL = 60 * 60 * 1000; // 60 min in millisec

	public CursedWeaponsManager()
	{
		_cursedWeaponsMap = new TIntObjectHashMap<CursedWeapon>();
		_cursedWeapons = new CursedWeapon[0];

		if (!Config.ALLOW_CURSED_WEAPONS)
		{
			return;
		}

		load();
		restore();
		checkConditions();

		cancelTask();
		_removeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RemoveTask(), CURSEDWEAPONS_MAINTENANCE_INTERVAL, CURSEDWEAPONS_MAINTENANCE_INTERVAL);

		LOG.info("CursedWeaponsManager: Loaded " + _cursedWeapons.length + " cursed weapon(s).");
	}

	@Deprecated
	public final void reload()
	{

	}

	private void load()
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);

			File file = new File(Config.DATAPACK_ROOT, "data/cursed_weapons.xml");
			if (!file.exists())
			{
				return;
			}

			Document doc = factory.newDocumentBuilder().parse(file);

			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("list".equalsIgnoreCase(n.getNodeName()))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if ("item".equalsIgnoreCase(d.getNodeName()))
						{
							NamedNodeMap attrs = d.getAttributes();
							int id = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
							int skillId = Integer.parseInt(attrs.getNamedItem("skillId").getNodeValue());
							String name = "Unknown cursed weapon";
							if (attrs.getNamedItem("name") != null)
							{
								name = attrs.getNamedItem("name").getNodeValue();
							}
							else if (ItemHolder.getInstance().getTemplate(id) != null)
							{
								name = ItemHolder.getInstance().getTemplate(id).getName();
							}

							if (id == 0)
							{
								continue;
							}

							CursedWeapon cw = new CursedWeapon(id, skillId, name);
							for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
							{
								if ("dropRate".equalsIgnoreCase(cd.getNodeName()))
								{
									cw.setDropRate(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
								}
								else if ("duration".equalsIgnoreCase(cd.getNodeName()))
								{
									attrs = cd.getAttributes();
									cw.setDurationMin(Integer.parseInt(attrs.getNamedItem("min").getNodeValue()));
									cw.setDurationMax(Integer.parseInt(attrs.getNamedItem("max").getNodeValue()));
								}
								else if ("durationLost".equalsIgnoreCase(cd.getNodeName()))
								{
									cw.setDurationLost(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
								}
								else if ("disapearChance".equalsIgnoreCase(cd.getNodeName()))
								{
									cw.setDisapearChance(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
								}
								else if ("stageKills".equalsIgnoreCase(cd.getNodeName()))
								{
									cw.setStageKills(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
								}
								else if ("transformationId".equalsIgnoreCase(cd.getNodeName()))
								{
									cw.setTransformationId(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
								}
								else if ("transformationTemplateId".equalsIgnoreCase(cd.getNodeName()))
								{
									cw.setTransformationTemplateId(Integer.parseInt(cd.getAttributes().getNamedItem("val").getNodeValue()));
								}
								else if ("transformationName".equalsIgnoreCase(cd.getNodeName()))
								{
									cw.setTransformationName(cd.getAttributes().getNamedItem("val").getNodeValue());
								}
							}

							// Store cursed weapon
							_cursedWeaponsMap.put(id, cw);
						}
					}
				}
			}

			_cursedWeapons = _cursedWeaponsMap.values(new CursedWeapon[_cursedWeaponsMap.size()]);
		}
		catch (DOMException | NumberFormatException | ParserConfigurationException | SAXException e)
		{
			LOG.error("CursedWeaponsManager: Error parsing cursed_weapons file. ", e);
		}
		catch (IOException e)
		{
			LOG.error("CursedWeaponsManager: IOException parsing cursed_weapons file. ", e);
		}
	}

	private void restore()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("SELECT * FROM cursed_weapons");
			rset = statement.executeQuery();

			while (rset.next())
			{
				int itemId = rset.getInt("item_id");
				CursedWeapon cw = _cursedWeaponsMap.get(itemId);
				if (cw != null)
				{
					cw.setPlayerId(rset.getInt("player_id"));
					cw.setPlayerKarma(rset.getInt("player_karma"));
					cw.setPlayerPkKills(rset.getInt("player_pkkills"));
					cw.setNbKills(rset.getInt("nb_kills"));
					cw.setLoc(new Location(rset.getInt("x"), rset.getInt("y"), rset.getInt("z")));
					cw.setEndTime(rset.getLong("end_time") * 1000L);

					if (!cw.reActivate())
					{
						endOfLife(cw);
					}
				}
				else
				{
					removeFromDb(itemId);
					LOG.warn("CursedWeaponsManager: Unknown cursed weapon " + itemId + ", deleted");
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warn("CursedWeaponsManager: Could not restore cursed_weapons data: ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	private void checkConditions()
	{
		Connection con = null;
		PreparedStatement statement1 = null, statement2 = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement1 = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=?");
			statement2 = con.prepareStatement("SELECT owner_id FROM items WHERE item_id=?");

			for (CursedWeapon cw : _cursedWeapons)
			{
				int itemId = cw.getItemId();
				// Do an item check to be sure that the cursed weapon and/or skill isn't hold by someone
				int skillId = cw.getSkillId();
				boolean foundedInItems = false;

				// Delete all cursed weapons skills (we don`t care about same skill on multiply weapons, when player back, skill will appears again)
				statement1.setInt(1, skillId);
				statement1.executeUpdate();

				statement2.setInt(1, itemId);
				rset = statement2.executeQuery();

				while (rset.next())
				{
					// A player has the cursed weapon in his inventory ...
					int playerId = rset.getInt("owner_id");

					if (!foundedInItems)
					{
						if (playerId != cw.getPlayerId() || cw.getPlayerId() == 0)
						{
							emptyPlayerCursedWeapon(playerId, itemId, cw);
							LOG.info("CursedWeaponsManager[254]: Player " + playerId + " owns the cursed weapon " + itemId + " but he shouldn't.");
						}
						else
						{
							foundedInItems = true;
						}
					}
					else
					{
						emptyPlayerCursedWeapon(playerId, itemId, cw);
						LOG.info("CursedWeaponsManager[262]: Player " + playerId + " owns the cursed weapon " + itemId + " but he shouldn't.");
					}
				}

				if (!foundedInItems && cw.getPlayerId() != 0)
				{
					removeFromDb(cw.getItemId());

					LOG.info("CursedWeaponsManager: Unownered weapon, removing from table...");
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warn("CursedWeaponsManager: Could not check cursed_weapons data: ", e);
		}
		finally
		{
			DbUtils.closeQuietly(statement1);
			DbUtils.closeQuietly(con, statement2, rset);
		}
	}

	private void emptyPlayerCursedWeapon(int playerId, int itemId, CursedWeapon cw)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			// Delete the item
			statement = con.prepareStatement("DELETE FROM items WHERE owner_id=? AND item_id=?");
			statement.setInt(1, playerId);
			statement.setInt(2, itemId);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("UPDATE characters SET karma=?, pkkills=? WHERE obj_id=?");
			statement.setInt(1, cw.getPlayerKarma());
			statement.setInt(2, cw.getPlayerPkKills());
			statement.setInt(3, playerId);
			if (statement.executeUpdate() != 1)
			{
				LOG.warn("Error while updating karma & pkkills for userId " + cw.getPlayerId());
			}
			// clean up the cursedweapons table.
			removeFromDb(itemId);
		}
		catch (SQLException e)
		{
			LOG.error("Error while deleting Player Cursed Weapon! ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void removeFromDb(int itemId)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("DELETE FROM cursed_weapons WHERE item_id = ?");
			statement.setInt(1, itemId);
			statement.executeUpdate();

			if (getCursedWeapon(itemId) != null)
			{
				getCursedWeapon(itemId).initWeapon();
			}
		}
		catch (SQLException e)
		{
			LOG.error("CursedWeaponsManager: Failed to remove data: ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	private void cancelTask()
	{
		if (_removeTask != null)
		{
			_removeTask.cancel(false);
			_removeTask = null;
		}
	}

	private class RemoveTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for (CursedWeapon cw : _cursedWeapons)
			{
				if (cw.isActive() && cw.getTimeLeft() <= 0)
				{
					endOfLife(cw);
				}
			}
		}
	}

	public void endOfLife(CursedWeapon cw)
	{
		if (cw.isActivated())
		{
			Player player = cw.getOnlineOwner();
			if (player != null)
			{
				// Remove from player
				LOG.info("CursedWeaponsManager: " + cw.getName() + " being removed online from " + player + ".");

				player.abortAttack(true, true);

				player.setKarma(cw.getPlayerKarma());
				player.setPkKills(cw.getPlayerPkKills());
				player.setCursedWeaponEquippedId(0);
				player.setTransformation(0);
				player.setTransformationName(null);
				player.removeSkill(SkillTable.getInstance().getInfo(cw.getSkillId(), player.getSkillLevel(cw.getSkillId())), false);
				player.getInventory().destroyItemByItemId(cw.getItemId(), 1L, "CursedWeapon");
				player.broadcastCharInfo();
			}
			else
			{
				// Remove from Db
				LOG.info("CursedWeaponsManager: " + cw.getName() + " being removed offline.");

				Connection con = null;
				PreparedStatement statement = null;
				try
				{
					con = DatabaseFactory.getInstance().getConnection();

					// Delete the item
					statement = con.prepareStatement("DELETE FROM items WHERE owner_id=? AND item_id=?");
					statement.setInt(1, cw.getPlayerId());
					statement.setInt(2, cw.getItemId());
					statement.executeUpdate();
					DbUtils.close(statement);

					// Delete the skill
					statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND skill_id=?");
					statement.setInt(1, cw.getPlayerId());
					statement.setInt(2, cw.getSkillId());
					statement.executeUpdate();
					DbUtils.close(statement);

					// Restore the karma
					statement = con.prepareStatement("UPDATE characters SET karma=?, pkkills=? WHERE obj_Id=?");
					statement.setInt(1, cw.getPlayerKarma());
					statement.setInt(2, cw.getPlayerPkKills());
					statement.setInt(3, cw.getPlayerId());
					statement.executeUpdate();
				}
				catch (SQLException e)
				{
					LOG.warn("CursedWeaponsManager: Could not delete : ", e);
				}
				finally
				{
					DbUtils.closeQuietly(con, statement);
				}
			}
		}
		else // either this cursed weapon is in the inventory of someone who has another cursed weapon equipped,
				// OR this cursed weapon is on the ground.
		if (cw.getPlayer() != null && cw.getPlayer().getInventory().getItemByItemId(cw.getItemId()) != null)
		{
			Player player = cw.getPlayer();
			if (!cw.getPlayer().getInventory().destroyItemByItemId(cw.getItemId(), 1, "CursedWeapon"))
			{
				LOG.info("CursedWeaponsManager[453]: Error! Cursed weapon not found!!!");
			}

			player.sendChanges();
			player.broadcastUserInfo(true);
		}
		// is dropped on the ground
		else if (cw.getItem() != null)
		{
			cw.getItem().deleteMe();
			cw.getItem().delete();
			LOG.info("CursedWeaponsManager: " + cw.getName() + " item has been removed from World.");
		}

		cw.initWeapon();
		removeFromDb(cw.getItemId());

		announce(new SystemMessage(SystemMessage.S1_HAS_DISAPPEARED_CW).addString(cw.getName()));
	}

	public void saveData(CursedWeapon cw)
	{
		Connection con = null;
		PreparedStatement statement = null;
		synchronized (cw)// FIXME [G1ta0] зачем синхронизация если она только на сохранении
		{
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				// Delete previous datas
				statement = con.prepareStatement("DELETE FROM cursed_weapons WHERE item_id = ?");
				statement.setInt(1, cw.getItemId());
				statement.executeUpdate();
				if (cw.isActive())
				{
					DbUtils.close(statement);
					statement = con.prepareStatement("REPLACE INTO cursed_weapons (item_id, player_id, player_karma, player_pkkills, nb_kills, x, y, z, end_time) VALUES (?,?,?,?,?,?,?,?,?)");
					statement.setInt(1, cw.getItemId());
					statement.setInt(2, cw.getPlayerId());
					statement.setInt(3, cw.getPlayerKarma());
					statement.setInt(4, cw.getPlayerPkKills());
					statement.setInt(5, cw.getNbKills());
					statement.setInt(6, cw.getLoc().x);
					statement.setInt(7, cw.getLoc().y);
					statement.setInt(8, cw.getLoc().z);
					statement.setLong(9, cw.getEndTime() / 1000);
					statement.executeUpdate();
				}
			}
			catch (SQLException e)
			{
				LOG.error("CursedWeapon: Failed to save data: ", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}
	}

	public void saveData()
	{
		for (CursedWeapon cw : _cursedWeapons)
		{
			saveData(cw);
		}
	}

	/**
	 * вызывается, когда проклятое оружие оказывается в инвентаре игрока
	 * @param player
	 * @param item
	 */
	public void checkPlayer(Player player, ItemInstance item)
	{
		if (player == null || item == null || player.isInOlympiadMode())
		{
			return;
		}

		CursedWeapon cw = _cursedWeaponsMap.get(item.getItemId());
		if (cw == null)
		{
			return;
		}

		if (player.getObjectId() == cw.getPlayerId() || cw.getPlayerId() == 0 || cw.isDropped())
		{
			activate(player, item);
			showUsageTime(player, cw);
		}
		else
		{
			// wtf? how you get it?
			LOG.warn("CursedWeaponsManager: " + player + " tried to obtain " + item + " in wrong way");
			player.getInventory().destroyItem(item, item.getCount(), "CursedWeapon");
		}
	}

	public void activate(Player player, ItemInstance item)
	{
		if (player == null || player.isInOlympiadMode())
		{
			return;
		}
		CursedWeapon cw = _cursedWeaponsMap.get(item.getItemId());
		if (cw == null)
		{
			return;
		}

		if (player.isCursedWeaponEquipped()) // cannot own 2 cursed swords
		{
			if (player.getCursedWeaponEquippedId() != item.getItemId())
			{
				CursedWeapon cw2 = _cursedWeaponsMap.get(player.getCursedWeaponEquippedId());
				cw2.setNbKills(cw2.getStageKills() - 1);
				cw2.increaseKills();
			}

			// erase the newly obtained cursed weapon
			endOfLife(cw);
			player.getInventory().destroyItem(item, 1, "CursedWeapon");
		}
		else if (cw.getTimeLeft() > 0)
		{
			cw.activate(player, item);
			announce(new SystemMessage(SystemMessage.THE_OWNER_OF_S2_HAS_APPEARED_IN_THE_S1_REGION).addZoneName(player.getLoc()).addString(cw.getName()));
			player.sendChanges();
			player.broadcastUserInfo(true);
			saveData(cw);
		}
		else
		{
			endOfLife(cw);
			player.getInventory().destroyItem(item, 1, "CursedWeapon");
		}
	}

	public void doLogout(Player player)
	{
		for (CursedWeapon cw : _cursedWeapons)
		{
			if (player.getInventory().getItemByItemId(cw.getItemId()) != null)
			{
				cw.setPlayer(null);
				cw.setItem(null);
			}
		}
	}

	/**
	 * drop from L2NpcInstance killed by L2Player
	 * @param attackable
	 * @param killer
	 */
	public void dropAttackable(NpcInstance attackable, Player killer)
	{
		if (killer.isInOlympiadMode() || killer.isCursedWeaponEquipped() || _cursedWeapons.length == 0 || killer.getReflection() != ReflectionManager.DEFAULT)
		{
			return;
		}

		synchronized (_cursedWeapons)
		{
			CursedWeapon[] cursedWeapons = new CursedWeapon[0];
			for (CursedWeapon cw : _cursedWeapons)
			{
				if (cw.isActive())
				{
					continue;
				}
				cursedWeapons = ArrayUtils.add(cursedWeapons, cw);
			}

			if (cursedWeapons.length > 0)
			{
				CursedWeapon cw = cursedWeapons[Rnd.get(cursedWeapons.length)];
				if (Rnd.get(100000000) <= cw.getDropRate())
				{
					cw.create(attackable, killer);
				}
			}
		}
	}

	/**
	 * Выпадение оружия из владельца, или исчезновение с определенной вероятностью.
	 * Вызывается при смерти игрока.
	 * @param player
	 */
	public void dropPlayer(Player player)
	{
		CursedWeapon cw = _cursedWeaponsMap.get(player.getCursedWeaponEquippedId());
		if (cw == null)
		{
			return;
		}

		if (cw.dropIt(null, null, player))
		{
			saveData(cw);
			announce(new SystemMessage(SystemMessage.S2_WAS_DROPPED_IN_THE_S1_REGION).addZoneName(player.getLoc()).addItemName(cw.getItemId()));
		}
		else
		{
			endOfLife(cw);
		}
	}

	public void increaseKills(int itemId)
	{
		CursedWeapon cw = _cursedWeaponsMap.get(itemId);
		if (cw != null)
		{
			cw.increaseKills();
			saveData(cw);
		}
	}

	public int getLevel(int itemId)
	{
		CursedWeapon cw = _cursedWeaponsMap.get(itemId);
		return cw != null ? cw.getLevel() : 0;
	}

	public void announce(SystemMessage sm)
	{
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			player.sendPacket(sm);
		}
	}

	public void showUsageTime(Player player, int itemId)
	{
		CursedWeapon cw = _cursedWeaponsMap.get(itemId);
		if (cw != null)
		{
			showUsageTime(player, cw);
		}
	}

	public void showUsageTime(Player player, CursedWeapon cw)
	{
		SystemMessage sm = new SystemMessage(SystemMessage.S2_MINUTE_OF_USAGE_TIME_ARE_LEFT_FOR_S1);
		sm.addString(cw.getName());
		sm.addNumber(new Long(cw.getTimeLeft() / 60000).intValue());
		player.sendPacket(sm);
	}

	public boolean isCursed(int itemId)
	{
		return _cursedWeaponsMap.containsKey(itemId);
	}

	public CursedWeapon[] getCursedWeapons()
	{
		return _cursedWeapons;
	}

	public int[] getCursedWeaponsIds()
	{
		return _cursedWeaponsMap.keys();
	}

	public CursedWeapon getCursedWeapon(int itemId)
	{
		return _cursedWeaponsMap.get(itemId);
	}
}