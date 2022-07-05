package l2f.gameserver.model.entity.residence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CTreeIntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dao.JdbcEntityState;
import l2f.commons.dbutils.DbUtils;
import l2f.commons.math.SafeMath;
import l2f.gameserver.Config;
import l2f.gameserver.dao.CastleDAO;
import l2f.gameserver.dao.CastleHiredGuardDAO;
import l2f.gameserver.dao.ClanDataDAO;
import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.instancemanager.CastleManorManager;
import l2f.gameserver.model.Manor;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.SevenSigns;
import l2f.gameserver.model.items.ClanWarehouse;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.templates.StatsSet;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.templates.item.support.MerchantGuard;
import l2f.gameserver.templates.manor.CropProcure;
import l2f.gameserver.templates.manor.SeedProduction;
import l2f.gameserver.utils.GameStats;
import l2f.gameserver.utils.Log;
import l2f.gameserver.utils.Util;

@SuppressWarnings("rawtypes")
public class Castle extends Residence
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger _log = LoggerFactory.getLogger(Castle.class);

	private static final String CASTLE_MANOR_DELETE_PRODUCTION = "DELETE FROM castle_manor_production WHERE castle_id=?;";
	private static final String CASTLE_MANOR_DELETE_PRODUCTION_PERIOD = "DELETE FROM castle_manor_production WHERE castle_id=? AND period=?;";
	private static final String CASTLE_MANOR_DELETE_PROCURE = "DELETE FROM castle_manor_procure WHERE castle_id=?;";
	private static final String CASTLE_MANOR_DELETE_PROCURE_PERIOD = "DELETE FROM castle_manor_procure WHERE castle_id=? AND period=?;";
	private static final String CASTLE_UPDATE_CROP = "UPDATE castle_manor_procure SET can_buy=? WHERE crop_id=? AND castle_id=? AND period=?";
	private static final String CASTLE_UPDATE_SEED = "UPDATE castle_manor_production SET can_produce=? WHERE seed_id=? AND castle_id=? AND period=?";

	private final IntObjectMap<MerchantGuard> _merchantGuards = new HashIntObjectMap<MerchantGuard>();
	private final IntObjectMap<List> _relatedFortresses = new CTreeIntObjectMap<List>();
	private Dominion _dominion;

	private List<CropProcure> _procure;
	private List<SeedProduction> _production;
	private List<CropProcure> _procureNext;
	private List<SeedProduction> _productionNext;
	private boolean _isNextPeriodApproved;

	private int _TaxPercent;
	private double _TaxRate;
	private long _treasury;
	private long _collectedShops;
	private long _collectedSeed;

	private final NpcString _npcStringName;

	private final Set<ItemInstance> _spawnMerchantTickets = new CopyOnWriteArraySet<ItemInstance>();

	public Castle(StatsSet set)
	{
		super(set);
		_npcStringName = NpcString.valueOf(1001000 + _id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init()
	{
		super.init();

		for (IntObjectMap.Entry<List> entry : _relatedFortresses.entrySet())
		{
			_relatedFortresses.remove(entry.getKey());

			List<Integer> list = entry.getValue();
			List<Fortress> list2 = new ArrayList<Fortress>(list.size());
			for (int i : list)
			{
				final Fortress fortress = ResidenceHolder.getInstance().getResidence(Fortress.class, i);
				if (fortress == null)
				{
					continue;
				}

				list2.add(fortress);

				fortress.addRelatedCastle(this);
			}
			_relatedFortresses.put(entry.getKey(), list2);
		}
	}

	@Override
	public ResidenceType getType()
	{
		return ResidenceType.Castle;
	}

	// This method sets the castle owner; null here means give it back to NPC
	@Override
	public void changeOwner(Clan newOwner)
	{
		// Если клан уже владел каким-либо замком/крепостью, отбираем его.
		if (newOwner != null)
		{
			if (newOwner.getHasFortress() != 0)
			{
				final Fortress oldFortress = ResidenceHolder.getInstance().getResidence(Fortress.class, newOwner.getHasFortress());
				if (oldFortress != null)
				{
					oldFortress.changeOwner(null);
				}
			}
			if (newOwner.getCastle() != 0)
			{
				final Castle oldCastle = ResidenceHolder.getInstance().getResidence(Castle.class, newOwner.getCastle());
				if (oldCastle != null)
				{
					oldCastle.changeOwner(null);
				}
			}
		}

		Clan oldOwner = null;
		// Если этим замком уже кто-то владел, отбираем у него замок
		if (getOwnerId() > 0 && (newOwner == null || newOwner.getClanId() != getOwnerId()))
		{
			// Удаляем замковые скилы у старого владельца
			removeSkills();
			getDominion().changeOwner(null);
			getDominion().removeSkills();

			// Убираем налог
			setTaxPercent(null, 0);
			cancelCycleTask();

			oldOwner = getOwner();
			if (oldOwner != null)
			{
				// Переносим сокровищницу в вархауз старого владельца
				final long amount = getTreasury();
				if (amount > 0)
				{
					ClanWarehouse warehouse = oldOwner.getWarehouse();
					if (warehouse != null)
					{
						warehouse.addItem(ItemTemplate.ITEM_ID_ADENA, amount, "Castle Change Owner");
						addToTreasuryNoTax(-amount, false, false);
					}
				}

				// Проверяем членов старого клана владельца, снимаем короны замков и корону лорда с лидера
				for (Player clanMember : oldOwner.getOnlineMembers(0))
				{
					if (clanMember != null && clanMember.getInventory() != null)
					{
						clanMember.getInventory().validateItems();
					}
				}

				// Отнимаем замок у старого владельца
				oldOwner.setHasCastle(0);
			}
		}

		// Выдаем замок новому владельцу
		if (newOwner != null)
		{
			newOwner.setHasCastle(getId());
		}

		// Сохраняем в базу
		updateOwnerInDB(newOwner);

		// Выдаем замковые скилы новому владельцу
		rewardSkills();
		getDominion().rewardSkills();
		setJdbcState(JdbcEntityState.UPDATED);
		update();
	}

	// This method loads castle
	@Override
	protected void loadData()
	{
		_TaxPercent = 0;
		_TaxRate = 0;
		_treasury = 0;
		_procure = new ArrayList<CropProcure>();
		_production = new ArrayList<SeedProduction>();
		_procureNext = new ArrayList<CropProcure>();
		_productionNext = new ArrayList<SeedProduction>();
		_isNextPeriodApproved = false;

		_owner = ClanDataDAO.getInstance().getOwner(this);
		CastleDAO.getInstance().select(this);
		CastleHiredGuardDAO.getInstance().load(this);
	}

	public void setTaxPercent(int percent)
	{
		_TaxPercent = Util.constrain(percent, 0, 100);
		_TaxRate = _TaxPercent / 100.0;
	}

	public void setTreasury(long t)
	{
		_treasury = t;
	}

	private void updateOwnerInDB(Clan clan)
	{
		_owner = clan; // Update owner id property

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_data SET hasCastle=0 WHERE hasCastle=? LIMIT 1");
			statement.setInt(1, getId());
			statement.execute();
			DbUtils.close(statement);

			if (clan != null)
			{
				statement = con.prepareStatement("UPDATE clan_data SET hasCastle=? WHERE clan_id=? LIMIT 1");
				statement.setInt(1, getId());
				statement.setInt(2, getOwnerId());
				statement.execute();

				clan.broadcastClanStatus(true, false, false);
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while updating Castle Owner in database", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public int getTaxPercent()
	{
		return getTaxPercent(true);
	}

	public int getTaxPercent(boolean checkSevenSign)
	{
		// Если печатью SEAL_STRIFE владеют DUSK то налог можно выставлять не более 5%
		if (checkSevenSign && _TaxPercent > 5 && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DUSK)
		{
			_TaxPercent = 5;
		}
		return _TaxPercent;
	}

	public long getCollectedShops()
	{
		return _collectedShops;
	}

	public long getCollectedSeed()
	{
		return _collectedSeed;
	}

	public void setCollectedShops(long value)
	{
		_collectedShops = value;
	}

	public void setCollectedSeed(long value)
	{
		_collectedSeed = value;
	}

	// This method add to the treasury
	/** Add amount to castle instance's treasury (warehouse). */
	public void addToTreasury(long amount, boolean shop, boolean seed)
	{
		if ((getOwnerId() <= 0) || (amount == 0))
		{
			return;
		}

		if (amount > 1 && _id != 5 && _id != 8) // If current castle instance is not Aden or Rune
		{
			final Castle royal = ResidenceHolder.getInstance().getResidence(Castle.class, _id >= 7 ? 8 : 5);
			if (royal != null)
			{
				final long royalTax = (long) (amount * royal.getTaxRate()); // Find out what royal castle gets from the current castle instance's income
				if (royal.getOwnerId() > 0)
				{
					royal.addToTreasury(royalTax, shop, seed); // Only bother to really add the tax to the treasury if not npc owned
					if (_id == 5)
					{
						Log.add("Aden|" + royalTax + "|Castle:adenTax", "treasury");
					}
					else if (_id == 8)
					{
						Log.add("Rune|" + royalTax + "|Castle:runeTax", "treasury");
					}
				}

				amount -= royalTax; // Subtract royal castle income from current castle instance's income
			}
		}

		addToTreasuryNoTax(amount, shop, seed);
	}

	/** Add amount to castle instance's treasury (warehouse), no tax paying. */
	public void addToTreasuryNoTax(long amount, boolean shop, boolean seed)
	{
		if ((getOwnerId() <= 0) || (amount == 0))
		{
			return;
		}

		if (Config.RATE_DROP_ADENA < 20)
		{
			GameStats.addAdena(amount);
		}

		// Add to the current treasury total. Use "-" to substract from treasury
		_treasury = SafeMath.addAndLimit(_treasury, amount);

		if (shop)
		{
			_collectedShops += amount;
		}

		if (seed)
		{
			_collectedSeed += amount;
		}

		setJdbcState(JdbcEntityState.UPDATED);
		update();
	}

	public int getCropRewardType(int crop)
	{
		int rw = 0;
		for (CropProcure cp : _procure)
		{
			if (cp.getId() == crop)
			{
				rw = cp.getReward();
			}
		}
		return rw;
	}

	// This method updates the castle tax rate
	public void setTaxPercent(Player activeChar, int taxPercent)
	{
		setTaxPercent(taxPercent);

		setJdbcState(JdbcEntityState.UPDATED);
		update();

		if (activeChar != null)
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.model.entity.Castle.OutOfControl.CastleTaxChangetTo", activeChar).addString(getName()).addNumber(taxPercent));
		}
	}

	public double getTaxRate()
	{
		// Если печатью SEAL_STRIFE владеют DUSK то налог можно выставлять не более 5%
		if (_TaxRate > 0.05 && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DUSK)
		{
			_TaxRate = 0.05;
		}
		return _TaxRate;
	}

	public long getTreasury()
	{
		return _treasury;
	}

	public List<SeedProduction> getSeedProduction(int period)
	{
		return period == CastleManorManager.PERIOD_CURRENT ? _production : _productionNext;
	}

	public List<CropProcure> getCropProcure(int period)
	{
		return period == CastleManorManager.PERIOD_CURRENT ? _procure : _procureNext;
	}

	public void setSeedProduction(List<SeedProduction> seed, int period)
	{
		if (period == CastleManorManager.PERIOD_CURRENT)
		{
			_production = seed;
		}
		else
		{
			_productionNext = seed;
		}
	}

	public void setCropProcure(List<CropProcure> crop, int period)
	{
		if (period == CastleManorManager.PERIOD_CURRENT)
		{
			_procure = crop;
		}
		else
		{
			_procureNext = crop;
		}
	}

	public synchronized SeedProduction getSeed(int seedId, int period)
	{
		for (SeedProduction seed : getSeedProduction(period))
		{
			if (seed.getId() == seedId)
			{
				return seed;
			}
		}
		return null;
	}

	public synchronized CropProcure getCrop(int cropId, int period)
	{
		for (CropProcure crop : getCropProcure(period))
		{
			if (crop.getId() == cropId)
			{
				return crop;
			}
		}
		return null;
	}

	public long getManorCost(int period)
	{
		List<CropProcure> procure;
		List<SeedProduction> production;

		if (period == CastleManorManager.PERIOD_CURRENT)
		{
			procure = _procure;
			production = _production;
		}
		else
		{
			procure = _procureNext;
			production = _productionNext;
		}

		long total = 0;
		if (production != null)
		{
			for (SeedProduction seed : production)
			{
				total += Manor.getInstance().getSeedBuyPrice(seed.getId()) * seed.getStartProduce();
			}
		}
		if (procure != null)
		{
			for (CropProcure crop : procure)
			{
				total += crop.getPrice() * crop.getStartAmount();
			}
		}
		return total;
	}

	// Save manor production data
	public void saveSeedData()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(CASTLE_MANOR_DELETE_PRODUCTION);
			statement.setInt(1, getId());
			statement.execute();
			DbUtils.close(statement);

			if (_production != null)
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_production VALUES ";
				final String values[] = new String[_production.size()];
				for (SeedProduction s : _production)
				{
					values[count] = "(" + getId() + "," + s.getId() + "," + s.getCanProduce() + "," + s.getStartProduce() + "," + s.getPrice() + "," + CastleManorManager.PERIOD_CURRENT + ")";
					count++;
				}
				if (values.length > 0)
				{
					query += values[0];
					for (int i = 1; i < values.length; i++)
					{
						query += "," + values[i];
					}
					statement = con.prepareStatement(query);
					statement.execute();
					DbUtils.close(statement);
				}
			}

			if (_productionNext != null)
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_production VALUES ";
				final String values[] = new String[_productionNext.size()];
				for (SeedProduction s : _productionNext)
				{
					values[count] = "(" + getId() + "," + s.getId() + "," + s.getCanProduce() + "," + s.getStartProduce() + "," + s.getPrice() + "," + CastleManorManager.PERIOD_NEXT + ")";
					count++;
				}
				if (values.length > 0)
				{
					query += values[0];
					for (int i = 1; i < values.length; i++)
					{
						query += "," + values[i];
					}
					statement = con.prepareStatement(query);
					statement.execute();
					DbUtils.close(statement);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error adding seed production data for castle " + getName() + '!', e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	// Save manor production data for specified period
	public void saveSeedData(int period)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(CASTLE_MANOR_DELETE_PRODUCTION_PERIOD);
			statement.setInt(1, getId());
			statement.setInt(2, period);
			statement.execute();
			DbUtils.close(statement);

			List<SeedProduction> prod = null;
			prod = getSeedProduction(period);

			if (prod != null)
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_production VALUES ";
				final String values[] = new String[prod.size()];
				for (SeedProduction s : prod)
				{
					values[count] = "(" + getId() + "," + s.getId() + "," + s.getCanProduce() + "," + s.getStartProduce() + "," + s.getPrice() + "," + period + ")";
					count++;
				}
				if (values.length > 0)
				{
					query += values[0];
					for (int i = 1; i < values.length; i++)
					{
						query += "," + values[i];
					}
					statement = con.prepareStatement(query);
					statement.execute();
					DbUtils.close(statement);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error adding seed production data for castle " + getName() + '!', e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	// Save crop procure data
	public void saveCropData()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(CASTLE_MANOR_DELETE_PROCURE);
			statement.setInt(1, getId());
			statement.execute();
			DbUtils.close(statement);
			if (_procure != null)
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_procure VALUES ";
				final String values[] = new String[_procure.size()];
				for (CropProcure cp : _procure)
				{
					values[count] = "(" + getId() + "," + cp.getId() + "," + cp.getAmount() + "," + cp.getStartAmount() + "," + cp.getPrice() + "," + cp.getReward() + "," + CastleManorManager.PERIOD_CURRENT + ")";
					count++;
				}
				if (values.length > 0)
				{
					query += values[0];
					for (int i = 1; i < values.length; i++)
					{
						query += "," + values[i];
					}
					statement = con.prepareStatement(query);
					statement.execute();
					DbUtils.close(statement);
				}
			}
			if (_procureNext != null)
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_procure VALUES ";
				final String values[] = new String[_procureNext.size()];
				for (CropProcure cp : _procureNext)
				{
					values[count] = "(" + getId() + "," + cp.getId() + "," + cp.getAmount() + "," + cp.getStartAmount() + "," + cp.getPrice() + "," + cp.getReward() + "," + CastleManorManager.PERIOD_NEXT + ")";
					count++;
				}
				if (values.length > 0)
				{
					query += values[0];
					for (int i = 1; i < values.length; i++)
					{
						query += "," + values[i];
					}
					statement = con.prepareStatement(query);
					statement.execute();
					DbUtils.close(statement);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error adding crop data for castle " + getName() + '!', e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	// Save crop procure data for specified period
	public void saveCropData(int period)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(CASTLE_MANOR_DELETE_PROCURE_PERIOD);
			statement.setInt(1, getId());
			statement.setInt(2, period);
			statement.execute();
			DbUtils.close(statement);

			List<CropProcure> proc = null;
			proc = getCropProcure(period);

			if (proc != null)
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_procure VALUES ";
				final String values[] = new String[proc.size()];
				for (CropProcure cp : proc)
				{
					values[count] = "(" + getId() + "," + cp.getId() + "," + cp.getAmount() + "," + cp.getStartAmount() + "," + cp.getPrice() + "," + cp.getReward() + "," + period + ")";
					count++;
				}
				if (values.length > 0)
				{
					query += values[0];
					for (int i = 1; i < values.length; i++)
					{
						query += "," + values[i];
					}
					statement = con.prepareStatement(query);
					statement.execute();
					DbUtils.close(statement);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error adding crop data for castle " + getName() + '!', e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void updateCrop(int cropId, long amount, int period)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(CASTLE_UPDATE_CROP);
			statement.setLong(1, amount);
			statement.setInt(2, cropId);
			statement.setInt(3, getId());
			statement.setInt(4, period);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error adding crop data for castle " + getName() + '!', e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void updateSeed(int seedId, long amount, int period)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(CASTLE_UPDATE_SEED);
			statement.setLong(1, amount);
			statement.setInt(2, seedId);
			statement.setInt(3, getId());
			statement.setInt(4, period);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error adding seed production data for castle " + getName() + '!', e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public boolean isNextPeriodApproved()
	{
		return _isNextPeriodApproved;
	}

	public void setNextPeriodApproved(boolean val)
	{
		_isNextPeriodApproved = val;
	}

	public Dominion getDominion()
	{
		return _dominion;
	}

	public void setDominion(Dominion dominion)
	{
		_dominion = dominion;
	}

	@SuppressWarnings("unchecked")
	public void addRelatedFortress(int type, int fortress)
	{
		List<Integer> fortresses = _relatedFortresses.get(type);
		if (fortresses == null)
		{
			_relatedFortresses.put(type, fortresses = new ArrayList<Integer>());
		}

		fortresses.add(fortress);
	}

	public int getDomainFortressContract()
	{
		@SuppressWarnings("unchecked")
		final List<Fortress> list = _relatedFortresses.get(Fortress.DOMAIN);
		if (list == null)
		{
			return 0;
		}
		for (Fortress f : list)
		{
			if (f.getContractState() == Fortress.CONTRACT_WITH_CASTLE && f.getCastleId() == getId())
			{
				return f.getId();
			}
		}
		return 0;
	}

	@Override
	public void update()
	{
		CastleDAO.getInstance().update(this);
	}

	public NpcString getNpcStringName()
	{
		return _npcStringName;
	}

	public IntObjectMap<List> getRelatedFortresses()
	{
		return _relatedFortresses;
	}

	public void addMerchantGuard(MerchantGuard merchantGuard)
	{
		_merchantGuards.put(merchantGuard.getItemId(), merchantGuard);
	}

	public MerchantGuard getMerchantGuard(int itemId)
	{
		return _merchantGuards.get(itemId);
	}

	public IntObjectMap<MerchantGuard> getMerchantGuards()
	{
		return _merchantGuards;
	}

	public Set<ItemInstance> getSpawnMerchantTickets()
	{
		return _spawnMerchantTickets;
	}

	@Override
	public void startCycleTask()
	{
	}
}