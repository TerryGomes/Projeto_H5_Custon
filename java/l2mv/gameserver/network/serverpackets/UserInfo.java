package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.instancemanager.CursedWeaponsManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.base.Experience;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.entity.events.GlobalEvent;
import l2mv.gameserver.model.entity.events.impl.AbstractFightClub;
import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.model.matching.MatchingRoom;
import l2mv.gameserver.model.pledge.Alliance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.skills.effects.EffectCubic;
import l2mv.gameserver.utils.Location;

public class UserInfo extends L2GameServerPacket
{
	private boolean can_writeImpl = false;
	private final boolean partyRoom;
	private final int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, _flRunSpd, _flWalkSpd;
	private int _flyRunSpd;
	private int _flyWalkSpd;
	private int _relation;
	private final double move_speed, attack_speed, col_radius, col_height;
	private final int[][] _inv;
	private final Location _loc, _fishLoc;
	private final int obj_id, vehicle_obj_id, _race, sex, base_class, level, curCp, maxCp;
	private int _enchant;
	private final int _weaponFlag;
	private final long _exp;
	private final int curHp, maxHp, curMp, maxMp, curLoad, maxLoad, rec_left, rec_have;
	private final int _str, _con, _dex, _int, _wit, _men, _sp, ClanPrivs, InventoryLimit;
	private final int _patk, _patkspd, _pdef, evasion, accuracy, crit, _matk, _matkspd;
	private final int _mdef, pvp_flag, karma, hair_style, hair_color, face, gm_commands, fame, vitality;
	private int clan_id, clan_crest_id, ally_id, ally_crest_id, large_clan_crest_id;
	private final int private_store, can_crystalize, pk_kills, pvp_kills, class_id, agathion;
	private final int _abnormalEffect, _abnormalEffect2, noble, hero;
	private int mount_id;
	private int cw_level;
	private int name_color;
	private final int running;
	private final int pledge_class;
	private final int pledge_type;
	private int title_color;
	private final int transformation;
	private final int defenceFire, defenceWater, defenceWind, defenceEarth, defenceHoly, defenceUnholy;
	private int mount_type;
	private String _name, title;
	private final EffectCubic[] cubics;
	private final Element attackElement;
	private final int attackElementValue;
	private final boolean isFlying, _allowMap;
	private final int talismans;
	private final boolean openCloak;
	private final double _expPercent;
	private final TeamType _team;

	public UserInfo(Player player)
	{
		if (player.getTransformationName() != null)
		{
			this._name = player.getTransformationName();
			this.title = "";
			this.clan_crest_id = 0;
			this.ally_crest_id = 0;
			this.large_clan_crest_id = 0;
			this.cw_level = CursedWeaponsManager.getInstance().getLevel(player.getCursedWeaponEquippedId());
		}
		else
		{
			this._name = player.getVisibleName();

			Clan clan = player.getClan();
			Alliance alliance = clan == null ? null : clan.getAlliance();
			//
			this.clan_id = clan == null ? 0 : clan.getClanId();
			this.clan_crest_id = clan == null ? 0 : clan.getCrestId();
			this.large_clan_crest_id = clan == null ? 0 : clan.getCrestLargeId();
			//
			this.ally_id = alliance == null ? 0 : alliance.getAllyId();
			this.ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();

			this.cw_level = 0;
			this.title = player.getVisibleTitle();
		}

//		if (player.getPlayerAccess().GodMode && player.isInvisible())
//			title += "[I]";
		if (player.isPolymorphed())
		{
			if (NpcHolder.getInstance().getTemplate(player.getPolyId()) != null)
			{
				this.title += " - " + NpcHolder.getInstance().getTemplate(player.getPolyId()).name;
			}
			else
			{
				this.title += " - Polymorphed";
			}
		}

		if (player.isMounted())
		{
			this._enchant = 0;
			this.mount_id = player.getMountNpcId() + 1000000;
			this.mount_type = player.getMountType();
		}
		else
		{
			this._enchant = player.getEnchantEffect();
			this.mount_id = 0;
			this.mount_type = 0;
		}

		this._weaponFlag = player.getActiveWeaponInstance() == null ? 0x14 : 0x28;

		this.move_speed = player.getMovementSpeedMultiplier();
		this._runSpd = (int) (player.getRunSpeed() / this.move_speed);
		this._walkSpd = (int) (player.getWalkSpeed() / this.move_speed);

		this._flRunSpd = 0; // TODO
		this._flWalkSpd = 0; // TODO

		if (player.isFlying())
		{
			this._flyRunSpd = this._runSpd;
			this._flyWalkSpd = this._walkSpd;
		}
		else
		{
			this._flyRunSpd = 0;
			this._flyWalkSpd = 0;
		}

		this._swimRunSpd = player.getSwimSpeed();
		this._swimWalkSpd = player.getSwimSpeed();

		this._inv = new int[Inventory.PAPERDOLL_MAX][3];
		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			this._inv[PAPERDOLL_ID][0] = player.getInventory().getPaperdollObjectId(PAPERDOLL_ID);
			this._inv[PAPERDOLL_ID][1] = player.isInOlympiadMode() ? player.getInventory().getPaperdollItemId(PAPERDOLL_ID) : player.getInventory().getPaperdollVisualItemId(PAPERDOLL_ID);
			this._inv[PAPERDOLL_ID][2] = player.getInventory().getPaperdollAugmentationId(PAPERDOLL_ID);
		}

		this._relation = player.isClanLeader() ? 0x40 : 0;
		for (GlobalEvent e : player.getEvents())
		{
			this._relation = e.getUserRelation(player, this._relation);
		}

		this._loc = player.getLoc();
		this.obj_id = player.getObjectId();
		this.vehicle_obj_id = player.isInBoat() ? player.getBoat().getObjectId() : 0x00;
		this._race = player.getRace().ordinal();
		this.sex = player.getSex();
		this.base_class = player.getBaseClassId();
		this.level = player.getLevel();
		this._exp = player.getExp();
		this._expPercent = Experience.getExpPercent(player.getLevel(), player.getExp());
		this._str = player.getSTR();
		this._dex = player.getDEX();
		this._con = player.getCON();
		this._int = player.getINT();
		this._wit = player.getWIT();
		this._men = player.getMEN();
		this.curHp = (int) player.getCurrentHp();
		this.maxHp = player.getMaxHp();
		this.curMp = (int) player.getCurrentMp();
		this.maxMp = player.getMaxMp();
		this.curLoad = player.getCurrentLoad();
		this.maxLoad = player.getMaxLoad();
		this._sp = player.getIntSp();
		this._patk = player.getPAtk(null);
		this._patkspd = player.getPAtkSpd();
		this._pdef = player.getPDef(null);
		this.evasion = player.getEvasionRate(null);
		this.accuracy = player.getAccuracy();
		this.crit = player.getCriticalHit(null, null);
		this._matk = player.getMAtk(null, null);
		this._matkspd = player.getMAtkSpd();
		this._mdef = player.getMDef(null, null);
		this.pvp_flag = player.getPvpFlag(); // 0=white, 1=purple, 2=purpleblink
		this.karma = player.getKarma();
		this.attack_speed = player.getAttackSpeedMultiplier();
		this.col_radius = player.getColRadius();
		this.col_height = player.getColHeight();
		this.hair_style = player.getHairStyle();
		this.hair_color = player.getHairColor();
		this.face = player.getFace();
		this.gm_commands = player.isGM() || player.getPlayerAccess().CanUseGMCommand ? 1 : 0;
		// builder level активирует в клиенте админские команды
		this.clan_id = player.getClanId();
		this.ally_id = player.getAllyId();
		this.private_store = (player.isInBuffStore() ? 0 : player.getPrivateStoreType());
		this.can_crystalize = player.getSkillLevel(Skill.SKILL_CRYSTALLIZE) > 0 ? 1 : 0;
		this.pk_kills = player.getPkKills();
		this.pvp_kills = player.getPvpKills();
		this.cubics = player.getCubics().toArray(new EffectCubic[player.getCubics().size()]);
		this._abnormalEffect = player.getAbnormalEffect();
		this._abnormalEffect2 = player.getAbnormalEffect2();
		this.ClanPrivs = player.getClanPrivileges();
		this.rec_left = player.getRecomLeft(); // c2 recommendations remaining
		this.rec_have = player.getRecomHave(); // c2 recommendations received
		this.InventoryLimit = player.getInventoryLimit();
		this.class_id = player.getClassId().getId();
		this.maxCp = player.getMaxCp();
		this.curCp = (int) player.getCurrentCp();
		this._team = player.getTeam();
		this.noble = player.isNoble() || player.isGM() && Config.GM_HERO_AURA ? 1 : 0; // 0x01: symbol on char menu ctrl+I
		this.hero = player.isHero() || player.isHeroAura() || player.isGM() && Config.GM_HERO_AURA ? 1 : 0; // 0x01: Hero Aura and symbol
		// fishing = _cha.isFishing() ? 1 : 0; // Fishing Mode
		this._fishLoc = player.getFishLoc();
		this.name_color = player.getVisibleNameColor();
		this.running = player.isRunning() ? 0x01 : 0x00; // changes the Speed display on Status Window
		this.pledge_class = player.getPledgeClass();
		this.pledge_type = player.getPledgeType();
		this.title_color = player.getVisibleTitleColor();
		this.transformation = player.getTransformation();
		this.attackElement = player.getAttackElement();
		this.attackElementValue = player.getAttack(this.attackElement);
		this.defenceFire = player.getDefence(Element.FIRE);
		this.defenceWater = player.getDefence(Element.WATER);
		this.defenceWind = player.getDefence(Element.WIND);
		this.defenceEarth = player.getDefence(Element.EARTH);
		this.defenceHoly = player.getDefence(Element.HOLY);
		this.defenceUnholy = player.getDefence(Element.UNHOLY);
		this.agathion = player.getAgathionId();
		this.fame = player.getFame();
		this.vitality = (int) player.getVitality();
		this.partyRoom = player.getMatchingRoom() != null && player.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING && player.getMatchingRoom().getLeader() == player;
		this.isFlying = player.isInFlyingTransform();
		this.talismans = player.getTalismanCount();
		this.openCloak = player.getOpenCloak();
		this._allowMap = player.isActionBlocked(Zone.BLOCKED_ACTION_MINIMAP);

		this.can_writeImpl = true;

		if (player.isInFightClub())
		{
			AbstractFightClub fightClubEvent = player.getFightClubEvent();
			this._name = fightClubEvent.getVisibleName(player, this._name, true);
			this.title = fightClubEvent.getVisibleTitle(player, this.title, true);
			this.title_color = fightClubEvent.getVisibleTitleColor(player, this.title_color, true);
			this.name_color = fightClubEvent.getVisibleNameColor(player, this.name_color, true);
		}
	}

	@Override
	protected final void writeImpl()
	{
		if (!this.can_writeImpl)
		{
			return;
		}

		this.writeC(0x32);

		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z + Config.CLIENT_Z_SHIFT);
		this.writeD(this.vehicle_obj_id);
		this.writeD(this.obj_id);
		this.writeS(this._name);
		this.writeD(this._race);
		this.writeD(this.sex);
		this.writeD(this.base_class);
		this.writeD(this.level);
		this.writeQ(this._exp);
		this.writeF(this._expPercent);
		this.writeD(this._str);
		this.writeD(this._dex);
		this.writeD(this._con);
		this.writeD(this._int);
		this.writeD(this._wit);
		this.writeD(this._men);
		this.writeD(this.maxHp);
		this.writeD(this.curHp);
		this.writeD(this.maxMp);
		this.writeD(this.curMp);
		this.writeD(this._sp);
		this.writeD(this.curLoad);
		this.writeD(this.maxLoad);
		this.writeD(this._weaponFlag);

		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			this.writeD(this._inv[PAPERDOLL_ID][0]);
		}

		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			this.writeD(this._inv[PAPERDOLL_ID][1]);
		}

		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			this.writeD(this._inv[PAPERDOLL_ID][2]);
		}

		this.writeD(this.talismans);
		this.writeD(this.openCloak ? 0x01 : 0x00);

		this.writeD(this._patk);
		this.writeD(this._patkspd);
		this.writeD(this._pdef);
		this.writeD(this.evasion);
		this.writeD(this.accuracy);
		this.writeD(this.crit);
		this.writeD(this._matk);
		this.writeD(this._matkspd);
		this.writeD(this._patkspd);
		this.writeD(this._mdef);
		this.writeD(this.pvp_flag);
		this.writeD(this.karma);
		this.writeD(this._runSpd);
		this.writeD(this._walkSpd);
		this.writeD(this._swimRunSpd); // swimspeed
		this.writeD(this._swimWalkSpd); // swimspeed
		this.writeD(this._flRunSpd);
		this.writeD(this._flWalkSpd);
		this.writeD(this._flyRunSpd);
		this.writeD(this._flyWalkSpd);
		this.writeF(this.move_speed);
		this.writeF(this.attack_speed);
		this.writeF(this.col_radius);
		this.writeF(this.col_height);
		this.writeD(this.hair_style);
		this.writeD(this.hair_color);
		this.writeD(this.face);
		this.writeD(this.gm_commands);
		this.writeS(this.title);
		this.writeD(this.clan_id);
		this.writeD(this.clan_crest_id);
		this.writeD(this.ally_id);
		this.writeD(this.ally_crest_id);
		// 0x40 leader rights
		// siege flags: attacker - 0x180 sword over name, defender - 0x80 shield, 0xC0 crown (|leader), 0x1C0 flag (|leader)
		this.writeD(this._relation);
		this.writeC(this.mount_type); // mount type
		this.writeC(this.private_store);
		this.writeC(this.can_crystalize);
		this.writeD(this.pk_kills);
		this.writeD(this.pvp_kills);
		this.writeH(this.cubics.length);
		for (EffectCubic cubic : this.cubics)
		{
			this.writeH(cubic == null ? 0 : cubic.getId());
		}
		this.writeC(this.partyRoom ? 0x01 : 0x00); // 1-find party members
		this.writeD(this._abnormalEffect);
		this.writeC(this.isFlying ? 0x02 : 0x00);
		this.writeD(this.ClanPrivs);
		this.writeH(this.rec_left);
		this.writeH(this.rec_have);
		this.writeD(this.mount_id);
		this.writeH(this.InventoryLimit);
		this.writeD(this.class_id);
		this.writeD(0x00); // special effects? circles around player...
		this.writeD(this.maxCp);
		this.writeD(this.curCp);
		this.writeC(this._enchant);
		this.writeC(this._team.ordinal());
		this.writeD(this.large_clan_crest_id);
		this.writeC(this.noble);
		this.writeC(this.hero);
		this.writeC(0x00);
		this.writeD(this._fishLoc.x);
		this.writeD(this._fishLoc.y);
		this.writeD(this._fishLoc.z);
		this.writeD(this.name_color);
		this.writeC(this.running);
		this.writeD(this.pledge_class);
		this.writeD(this.pledge_type);
		this.writeD(this.title_color);
		this.writeD(this.cw_level);
		this.writeD(this.transformation); // Transformation id

		// AttackElement (0 - Fire, 1 - Water, 2 - Wind, 3 - Earth, 4 - Holy, 5 - Dark, -2 - None)
		this.writeH(this.attackElement.getId());
		this.writeH(this.attackElementValue); // AttackElementValue
		this.writeH(this.defenceFire); // DefAttrFire
		this.writeH(this.defenceWater); // DefAttrWater
		this.writeH(this.defenceWind); // DefAttrWind
		this.writeH(this.defenceEarth); // DefAttrEarth
		this.writeH(this.defenceHoly); // DefAttrHoly
		this.writeH(this.defenceUnholy); // DefAttrUnholy

		this.writeD(this.agathion);

		// T2 Starts
		this.writeD(this.fame); // Fame
		this.writeD(this._allowMap ? 1 : 0); // Minimap on Hellbound

		this.writeD(this.vitality); // Vitality Points
		this.writeD(this._abnormalEffect2);
	}
}