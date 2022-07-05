package l2f.gameserver.model.instances;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dao.JdbcEntityState;
import l2f.commons.dbutils.DbUtils;
import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.idfactory.IdFactory;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.Party;
import l2f.gameserver.model.PetData;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Summon;
import l2f.gameserver.model.base.BaseStats;
import l2f.gameserver.model.base.Experience;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.items.PetInventory;
import l2f.gameserver.model.items.attachment.FlagItemAttachment;
import l2f.gameserver.network.serverpackets.InventoryUpdate;
import l2f.gameserver.network.serverpackets.SocialAction;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.stats.Stats;
import l2f.gameserver.tables.PetDataTable;
import l2f.gameserver.templates.item.WeaponTemplate;
import l2f.gameserver.templates.npc.NpcTemplate;

public class PetInstance extends Summon
{
	private static final Logger _log = LoggerFactory.getLogger(PetInstance.class);

	private static final int DELUXE_FOOD_FOR_STRIDER = 5169;

	class FeedTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			Player owner = getPlayer();

			while (getCurrentFed() <= 0.55 * getMaxFed() && tryFeed())
			{
			}

			if (PetDataTable.isVitaminPet(getNpcId()) && getCurrentFed() <= 0)
			{
				deleteMe();
			}
			else if (getCurrentFed() <= 0.10 * getMaxFed())
			{
				// If the food is over, withdraw pet
				owner.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2PetInstance.UnSummonHungryPet", owner));
				unSummon();
				return;
			}

			setCurrentFed(getCurrentFed() - 5);

			sendStatusUpdate();
			startFeed(isInCombat());
		}
	}

	private final int _controlItemObjId;
	private int _curFed;
	protected PetData _data;
	private Future<?> _feedTask;
	protected PetInventory _inventory;
	private int _level;
	private boolean _respawned;
	private int lostExp;

	public static final PetInstance restore(ItemInstance control, NpcTemplate template, Player owner)
	{
		PetInstance pet = null;

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT objId, name, level, curHp, curMp, exp, sp, fed FROM pets WHERE item_obj_id=?");
			statement.setInt(1, control.getObjectId());
			rset = statement.executeQuery();

			if (!rset.next())
			{
				if (PetDataTable.isBabyPet(template.getNpcId()) || PetDataTable.isImprovedBabyPet(template.getNpcId()))
				{
					pet = new PetBabyInstance(IdFactory.getInstance().getNextId(), template, owner, control);
				}
				else
				{
					pet = new PetInstance(IdFactory.getInstance().getNextId(), template, owner, control);
				}
				return pet;
			}

			if (PetDataTable.isBabyPet(template.getNpcId()) || PetDataTable.isImprovedBabyPet(template.getNpcId()))
			{
				pet = new PetBabyInstance(rset.getInt("objId"), template, owner, control, rset.getInt("level"), rset.getLong("exp"));
			}
			else
			{
				pet = new PetInstance(rset.getInt("objId"), template, owner, control, rset.getInt("level"), rset.getLong("exp"));
			}

			pet.setRespawned(true);

			String name = rset.getString("name");
			pet.setName(name == null || name.isEmpty() ? template.name : name);
			pet.setCurrentHpMp(rset.getDouble("curHp"), rset.getInt("curMp"), true);
			pet.setCurrentCp(pet.getMaxCp());
			pet.setSp(rset.getInt("sp"));
			pet.setCurrentFed(rset.getInt("fed"));
		}
		catch (SQLException e)
		{
			_log.error("Could not restore Pet data from item: " + control + '!', e);
			return null;
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return pet;
	}

	/**
	 * Create a new pet
	 */
	public PetInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control)
	{
		this(objectId, template, owner, control, 0, 0);
	}

	/**
	 * Loading an existing pet
	 */
	public PetInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control, int currentLevel, long exp)
	{
		super(objectId, template, owner);

		_controlItemObjId = control.getObjectId();
		_exp = exp;
		_level = control.getEnchantLevel();

		if (_level <= 0)
		{
			if (template.npcId == PetDataTable.SIN_EATER_ID)
			{
				_level = owner.getLevel();
			}
			else
			{
				_level = template.level;
			}
			_exp = getExpForThisLevel();
		}

		int minLevel = PetDataTable.getMinLevel(template.npcId);
		if (_level < minLevel)
		{
			_level = minLevel;
		}

		if (_exp < getExpForThisLevel())
		{
			_exp = getExpForThisLevel();
		}

		while (_exp >= getExpForNextLevel() && _level < Experience.getMaxLevel())
		{
			_level++;
		}

		while (_exp < getExpForThisLevel() && _level > minLevel)
		{
			_level--;
		}

		if (PetDataTable.isVitaminPet(template.npcId))
		{
			_level = owner.getLevel();
			_exp = getExpForNextLevel();
		}

		_data = PetDataTable.getInstance().getInfo(template.npcId, _level);
		_inventory = new PetInventory(this);
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		startFeed(false);
	}

	@Override
	protected void onDespawn()
	{
		super.onSpawn();

		stopFeed();
	}

	public boolean tryFeedItem(ItemInstance item)
	{
		if (item == null)
		{
			return false;
		}

		boolean deluxFood = PetDataTable.isStrider(getNpcId()) && item.getItemId() == DELUXE_FOOD_FOR_STRIDER;
		if (getFoodId() != item.getItemId() && !deluxFood)
		{
			return false;
		}

		int newFed = Math.min(getMaxFed(), getCurrentFed() + Math.max(getMaxFed() * getAddFed() * (deluxFood ? 2 : 1) / 100, 1));
		if (getCurrentFed() != newFed)
		{
			if (getInventory().destroyItem(item, 1L, null))
			{
				getPlayer().sendPacket(new SystemMessage(SystemMessage.PET_TOOK_S1_BECAUSE_HE_WAS_HUNGRY).addItemName(item.getItemId()));
				setCurrentFed(newFed);
				sendStatusUpdate();
			}
		}
		return true;
	}

	public boolean tryFeed()
	{
		ItemInstance food = getInventory().getItemByItemId(getFoodId());
		if (food == null && PetDataTable.isStrider(getNpcId()))
		{
			food = getInventory().getItemByItemId(DELUXE_FOOD_FOR_STRIDER);
		}
		return tryFeedItem(food);
	}

	@Override
	public void addExpAndSp(long addToExp, long addToSp)
	{
		Player owner = getPlayer();

		if (PetDataTable.isVitaminPet(getNpcId()))
		{
			return;
		}

		_exp += addToExp;
		_sp += addToSp;

		if (_exp > getMaxExp())
		{
			_exp = getMaxExp();
		}

		if (addToExp > 0 || addToSp > 0)
		{
			owner.sendPacket(new SystemMessage(SystemMessage.THE_PET_ACQUIRED_EXPERIENCE_POINTS_OF_S1).addNumber(addToExp));
		}

		int old_level = _level;

		while (_exp >= getExpForNextLevel() && _level < Experience.getMaxLevel())
		{
			_level++;
		}

		while (_exp < getExpForThisLevel() && _level > getMinLevel())
		{
			_level--;
		}

		if (old_level < _level)
		{
			owner.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2PetInstance.PetLevelUp", owner).addNumber(_level));
			broadcastPacket(new SocialAction(getObjectId(), SocialAction.LEVEL_UP));
			setCurrentHpMp(getMaxHp(), getMaxMp());
		}

		if (old_level != _level)
		{
			updateControlItem();
			updateData();
		}

		if (addToExp > 0 || addToSp > 0)
		{
			sendStatusUpdate();
		}
	}

	@Override
	public boolean consumeItem(int itemConsumeId, long itemCount)
	{
		return getInventory().destroyItemByItemId(itemConsumeId, itemCount, "Consume");
	}

	private void deathPenalty()
	{
		if (isInZoneBattle())
		{
			return;
		}
		int lvl = getLevel();
		double percentLost = -0.07 * lvl + 6.5;
		// Calculate the Experience loss
		lostExp = (int) Math.round((getExpForNextLevel() - getExpForThisLevel()) * percentLost / 100);
		addExpAndSp(-lostExp, 0);
	}

	/**
	 * Remove the Pet from DB and its associated item from the player inventory
	 */
	private void destroyControlItem()
	{
		Player owner = getPlayer();
		if ((getControlItemObjId() == 0) || !owner.getInventory().destroyItemByObjectId(getControlItemObjId(), 1L, "Destroy"))
		{
			return;
		}

		// pet control item no longer exists, delete the pet from the database
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?");
			statement.setInt(1, getControlItemObjId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("could not delete pet:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);

		Player owner = getPlayer();

		owner.sendPacket(Msg.THE_PET_HAS_BEEN_KILLED_IF_YOU_DO_NOT_RESURRECT_IT_WITHIN_24_HOURS_THE_PETS_BODY_WILL_DISAPPEAR_ALONG_WITH_ALL_THE_PETS_ITEMS);
		startDecay(86400000L);

		if (PetDataTable.isVitaminPet(getNpcId()))
		{
			return;
		}

		saveEffects();

		stopFeed();
		deathPenalty();
	}

	@Override
	public void doPickupItem(GameObject object)
	{
		Player owner = getPlayer();

		stopMove();

		if (!object.isItem())
		{
			return;
		}

		ItemInstance item = (ItemInstance) object;

		if (item.isCursed())
		{
			owner.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1).addItemName(item.getItemId()));
			return;
		}

		synchronized (item)
		{
			if (!item.isVisible())
			{
				return;
			}

			if (item.isHerb())
			{
				Skill[] skills = item.getTemplate().getAttachedSkills();
				if (skills.length > 0)
				{
					for (Skill skill : skills)
					{
						altUseSkill(skill, this);
					}
				}
				item.deleteMe();
				return;
			}

			if (!getInventory().validateWeight(item))
			{
				sendPacket(Msg.EXCEEDED_PET_INVENTORYS_WEIGHT_LIMIT);
				return;
			}

			if (!getInventory().validateCapacity(item))
			{
				sendPacket(Msg.DUE_TO_THE_VOLUME_LIMIT_OF_THE_PETS_INVENTORY_NO_MORE_ITEMS_CAN_BE_PLACED_THERE);
				return;
			}

			if (!item.getTemplate().getHandler().pickupItem(this, item))
			{
				return;
			}

			FlagItemAttachment attachment = item.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment) item.getAttachment() : null;
			if (attachment != null)
			{
				return;
			}

			item.pickupMe();
		}

		if (owner.getParty() == null || owner.getParty().getLootDistribution() == Party.ITEM_LOOTER)
		{
			getInventory().addItem(item, "PickUp");
			sendChanges();
		}
		else if (item.isCursed())
		{
			owner.getInventory().addItem(item, "PickUp");
			owner.sendChanges();
		}
		else
		{
			owner.getParty().distributeItem(owner, item, null);
		}

		broadcastPickUpMsg(item);
	}

	public void doRevive(double percent)
	{
		restoreExp(percent);
		doRevive();
	}

	@Override
	public void doRevive()
	{
		stopDecay();
		super.doRevive();
		startFeed(false);
		setRunning();
	}

	@Override
	public int getAccuracy()
	{
		return (int) calcStat(Stats.ACCURACY_COMBAT, _data.getAccuracy(), null, null);
	}

	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return null;
	}

	@Override
	public WeaponTemplate getActiveWeaponItem()
	{
		return null;
	}

	public ItemInstance getControlItem()
	{
		Player owner = getPlayer();
		if (owner == null)
		{
			return null;
		}
		int item_obj_id = getControlItemObjId();
		if (item_obj_id == 0)
		{
			return null;
		}
		return owner.getInventory().getItemByObjectId(item_obj_id);
	}

	@Override
	public int getControlItemObjId()
	{
		return _controlItemObjId;
	}

	@Override
	public int getCriticalHit(Creature target, Skill skill)
	{
		return (int) calcStat(Stats.CRITICAL_BASE, _data.getCritical(), target, skill);
	}

	@Override
	public int getCurrentFed()
	{
		return _curFed;
	}

	@Override
	public int getEvasionRate(Creature target)
	{
		return (int) calcStat(Stats.EVASION_RATE, _data.getEvasion(), target, null);
	}

	@Override
	public long getExpForNextLevel()
	{
		return PetDataTable.getInstance().getInfo(getNpcId(), _level + 1).getExp();
	}

	@Override
	public long getExpForThisLevel()
	{
		return PetDataTable.getInstance().getInfo(getNpcId(), _level).getExp();
	}

	public int getFoodId()
	{
		return _data.getFoodId();
	}

	public int getAddFed()
	{
		return _data.getAddFed();
	}

	@Override
	public PetInventory getInventory()
	{
		return _inventory;
	}

	@Override
	public long getWearedMask()
	{
		return _inventory.getWearedMask();
	}

	@Override
	public final int getLevel()
	{
		return _level;
	}

	public void setLevel(int level)
	{
		_level = level;
	}

	@Override
	public double getLevelMod()
	{
		return (89. + getLevel()) / 100.0;
	}

	public int getMinLevel()
	{
		return _data.getMinLevel();
	}

	public long getMaxExp()
	{
		return PetDataTable.getInstance().getInfo(getNpcId(), Experience.getMaxLevel() + 1).getExp();
	}

	@Override
	public int getMaxFed()
	{
		return _data.getFeedMax();
	}

	@Override
	public int getMaxLoad()
	{
		return (int) calcStat(Stats.MAX_LOAD, _data.getMaxLoad(), null, null);
	}

	@Override
	public int getInventoryLimit()
	{
		return Config.ALT_PET_INVENTORY_LIMIT;
	}

	@Override
	public int getMaxHp()
	{
		return (int) calcStat(Stats.MAX_HP, _data.getHP(), null, null);
	}

	@Override
	public int getMaxMp()
	{
		return (int) calcStat(Stats.MAX_MP, _data.getMP(), null, null);
	}

	@Override
	public int getPAtk(Creature target)
	{
		// В базе указаны параметры, уже домноженные на этот модификатор, для удобства. Поэтому вычисляем и убираем его.
		double mod = BaseStats.STR.calcBonus(this) * getLevelMod();
		return (int) calcStat(Stats.POWER_ATTACK, _data.getPAtk() / mod, target, null);
	}

	@Override
	public int getPDef(Creature target)
	{
		// В базе указаны параметры, уже домноженные на этот модификатор, для удобства. Поэтому вычисляем и убираем его.
		double mod = getLevelMod();
		return (int) calcStat(Stats.POWER_DEFENCE, _data.getPDef() / mod, target, null);
	}

	@Override
	public int getMAtk(Creature target, Skill skill)
	{
		// В базе указаны параметры, уже домноженные на этот модификатор, для удобства. Поэтому вычисляем и убираем его.
		double ib = BaseStats.INT.calcBonus(this);
		double lvlb = getLevelMod();
		double mod = lvlb * lvlb * ib * ib;
		return (int) calcStat(Stats.MAGIC_ATTACK, _data.getMAtk() / mod, target, skill);
	}

	@Override
	public int getMDef(Creature target, Skill skill)
	{
		// В базе указаны параметры, уже домноженные на этот модификатор, для удобства. Поэтому вычисляем и убираем его.
		double mod = BaseStats.MEN.calcBonus(this) * getLevelMod();
		return (int) calcStat(Stats.MAGIC_DEFENCE, _data.getMDef() / mod, target, skill);
	}

	@Override
	public int getPAtkSpd()
	{
		return (int) calcStat(Stats.POWER_ATTACK_SPEED, calcStat(Stats.ATK_BASE, _data.getAtkSpeed(), null, null), null, null);
	}

	@Override
	public int getMAtkSpd()
	{
		return (int) calcStat(Stats.MAGIC_ATTACK_SPEED, _data.getCastSpeed(), null, null);
	}

	@Override
	public int getRunSpeed()
	{
		return getSpeed(_data.getSpeed());
	}

	@Override
	public int getSoulshotConsumeCount()
	{
		return PetDataTable.getSoulshots(getNpcId());
	}

	@Override
	public int getSpiritshotConsumeCount()
	{
		return PetDataTable.getSpiritshots(getNpcId());
	}

	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}

	@Override
	public WeaponTemplate getSecondaryWeaponItem()
	{
		return null;
	}

	public int getSkillLevel(int skillId)
	{
		if (_skills == null || _skills.get(skillId) == null)
		{
			return -1;
		}
		int lvl = getLevel();
		return lvl > 70 ? 7 + (lvl - 70) / 5 : lvl / 10;
	}

	@Override
	public int getSummonType()
	{
		return 2;
	}

	@Override
	public NpcTemplate getTemplate()
	{
		return (NpcTemplate) _template;
	}

	@Override
	public boolean isMountable()
	{
		return _data.isMountable();
	}

	public boolean isRespawned()
	{
		return _respawned;
	}

	public void restoreExp(double percent)
	{
		if (lostExp != 0)
		{
			addExpAndSp((long) (lostExp * percent / 100.), 0);
			lostExp = 0;
		}
	}

	public void setCurrentFed(int num)
	{
		_curFed = Math.min(getMaxFed(), Math.max(0, num));
	}

	public void setRespawned(boolean respawned)
	{
		_respawned = respawned;
	}

	@Override
	public void setSp(int sp)
	{
		_sp = sp;
	}

	public void startFeed(boolean battleFeed)
	{
		boolean first = _feedTask == null;
		stopFeed();
		if (!isDead())
		{
			int feedTime;
			if (PetDataTable.isVitaminPet(getNpcId()))
			{
				feedTime = 10000;
			}
			else
			{
				feedTime = Math.max(first ? 15000 : 1000, 60000 / (battleFeed ? _data.getFeedBattle() : _data.getFeedNormal()));
			}
			_feedTask = ThreadPoolManager.getInstance().schedule(new FeedTask(), feedTime);
		}
	}

	private void stopFeed()
	{
		if (_feedTask != null)
		{
			_feedTask.cancel(false);
			_feedTask = null;
		}
	}

	public void store()
	{
		if (getControlItemObjId() == 0 || _exp == 0)
		{
			return;
		}

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			String req;
			if (!isRespawned())
			{
				req = "INSERT INTO pets (name,level,curHp,curMp,exp,sp,fed,objId,item_obj_id) VALUES (?,?,?,?,?,?,?,?,?)";
			}
			else
			{
				req = "UPDATE pets SET name=?,level=?,curHp=?,curMp=?,exp=?,sp=?,fed=?,objId=? WHERE item_obj_id = ?";
			}
			statement = con.prepareStatement(req);
			if (getName().length() > 13)
			{
				setName("");
			}
			statement.setString(1, getName().equalsIgnoreCase(getTemplate().name) ? "" : getName());
			statement.setInt(2, _level);
			statement.setDouble(3, getCurrentHp());
			statement.setDouble(4, getCurrentMp());
			statement.setLong(5, _exp);
			statement.setLong(6, _sp);
			statement.setInt(7, _curFed);
			statement.setInt(8, getObjectId());
			statement.setInt(9, _controlItemObjId);
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			_log.error("Could not store pet data!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}

		_respawned = true;
	}

	@Override
	protected void onDecay()
	{
		getInventory().store();
		destroyControlItem(); // this should also delete the pet from the db

		super.onDecay();
	}

	@Override
	public void unSummon()
	{
		stopFeed();
		getInventory().store();
		store();

		super.unSummon();
	}

	public void updateControlItem()
	{
		ItemInstance controlItem = getControlItem();
		if (controlItem == null)
		{
			return;
		}
		controlItem.setEnchantLevel(_level);
		controlItem.setCustomType2(isDefaultName() ? 0 : 1);
		controlItem.setJdbcState(JdbcEntityState.UPDATED);
		controlItem.update();
		Player owner = getPlayer();
		owner.sendPacket(new InventoryUpdate().addModifiedItem(controlItem));
	}

	private void updateData()
	{
		_data = PetDataTable.getInstance().getInfo(getTemplate().npcId, _level);
	}

	@Override
	public double getExpPenalty()
	{
		return PetDataTable.getExpPenalty(getTemplate().npcId);
	}

	@Override
	public void displayGiveDamageMessage(Creature target, int damage, boolean crit, boolean miss, boolean shld, boolean magic)
	{
		Player owner = getPlayer();
		if (crit)
		{
			owner.sendPacket(SystemMsg.SUMMONED_MONSTERS_CRITICAL_HIT);
		}
		if (miss)
		{
			owner.sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(this));
		}
		else
		{
			owner.sendPacket(new SystemMessage(SystemMessage.THE_PET_GAVE_DAMAGE_OF_S1).addNumber(damage));
		}
	}

	@Override
	public void displayReceiveDamageMessage(Creature attacker, int damage)
	{
		Player owner = getPlayer();

		if (!isDead())
		{
			SystemMessage sm = new SystemMessage(SystemMessage.THE_PET_RECEIVED_DAMAGE_OF_S2_CAUSED_BY_S1);
			if (attacker.isNpc())
			{
				sm.addNpcName(((NpcInstance) attacker).getTemplate().npcId);
			}
			else
			{
				sm.addString(attacker.getName());
			}
			sm.addNumber((long) damage);
			owner.sendPacket(sm);
		}
	}

	@Override
	public int getFormId()
	{
		switch (getNpcId())
		{
		case PetDataTable.GREAT_WOLF_ID:
		case PetDataTable.WGREAT_WOLF_ID:
		case PetDataTable.FENRIR_WOLF_ID:
		case PetDataTable.WFENRIR_WOLF_ID:
			if (getLevel() >= 70)
			{
				return 3;
			}
			else if (getLevel() >= 65)
			{
				return 2;
			}
			else if (getLevel() >= 60)
			{
				return 1;
			}
			break;
		}
		return 0;
	}

	@Override
	public boolean isPet()
	{
		return true;
	}

	public boolean isDefaultName()
	{
		return StringUtils.isEmpty(_name) || getName().equalsIgnoreCase(getTemplate().name);
	}

	@Override
	public int getEffectIdentifier()
	{
		return 0;// TODO [VISTALL] objectId if buffs pets saved
	}

	public void changeTemplate(int npcId)
	{
		NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
		if (template == null)
		{
			throw new NullPointerException("Not find npc: " + npcId);
		}
		changeTemplate(template);
	}

	public void changeTemplate(NpcTemplate template)
	{
		_template = template;
	}
}