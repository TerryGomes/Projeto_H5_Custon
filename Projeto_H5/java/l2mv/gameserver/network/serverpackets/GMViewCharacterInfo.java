package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.base.Experience;
import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.model.pledge.Alliance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.utils.Location;

public class GMViewCharacterInfo extends L2GameServerPacket
{
	private final Location _loc;
	private final int[][] _inv;
	private final int obj_id, _race, _sex, class_id, pvp_flag, karma, level, mount_type;
	private final int _str, _con, _dex, _int, _wit, _men, _sp;
	private final int curHp, maxHp, curMp, maxMp, curCp, maxCp, curLoad, maxLoad, rec_left, rec_have;
	private final int _patk, _patkspd, _pdef, evasion, accuracy, crit, _matk, _matkspd;
	private final int _mdef, hair_style, hair_color, face, gm_commands;
	private final int clan_id, clan_crest_id, ally_id, title_color;
	private final int noble, hero, private_store, name_color, pk_kills, pvp_kills;
	private final int _runSpd, _walkSpd, _swimSpd, DwarvenCraftLevel, running, pledge_class;
	private final String _name, title;
	private final long _exp;
	private final double move_speed, attack_speed, col_radius, col_height;
	private final Element attackElement;
	private final int attackElementValue;
	private final int defenceFire, defenceWater, defenceWind, defenceEarth, defenceHoly, defenceUnholy;
	private final int fame, vitality;
	private final int talismans;
	private final boolean openCloak;
	private final double _expPercent;

	public GMViewCharacterInfo(Player cha)
	{
		this._loc = cha.getLoc();
		this.obj_id = cha.getObjectId();
		this._name = cha.getName();
		this._race = cha.getRace().ordinal();
		this._sex = cha.getSex();
		this.class_id = cha.getClassId().getId();
		this.level = cha.getLevel();
		this._exp = cha.getExp();
		this._str = cha.getSTR();
		this._dex = cha.getDEX();
		this._con = cha.getCON();
		this._int = cha.getINT();
		this._wit = cha.getWIT();
		this._men = cha.getMEN();
		this.curHp = (int) cha.getCurrentHp();
		this.maxHp = cha.getMaxHp();
		this.curMp = (int) cha.getCurrentMp();
		this.maxMp = cha.getMaxMp();
		this._sp = cha.getIntSp();
		this.curLoad = cha.getCurrentLoad();
		this.maxLoad = cha.getMaxLoad();
		this._patk = cha.getPAtk(null);
		this._patkspd = cha.getPAtkSpd();
		this._pdef = cha.getPDef(null);
		this.evasion = cha.getEvasionRate(null);
		this.accuracy = cha.getAccuracy();
		this.crit = cha.getCriticalHit(null, null);
		this._matk = cha.getMAtk(null, null);
		this._matkspd = cha.getMAtkSpd();
		this._mdef = cha.getMDef(null, null);
		this.pvp_flag = cha.getPvpFlag();
		this.karma = cha.getKarma();
		this._runSpd = cha.getRunSpeed();
		this._walkSpd = cha.getWalkSpeed();
		this._swimSpd = cha.getSwimSpeed();
		this.move_speed = cha.getMovementSpeedMultiplier();
		this.attack_speed = cha.getAttackSpeedMultiplier();
		this.mount_type = cha.getMountType();
		this.col_radius = cha.getColRadius();
		this.col_height = cha.getColHeight();
		this.hair_style = cha.getHairStyle();
		this.hair_color = cha.getHairColor();
		this.face = cha.getFace();
		this.gm_commands = cha.isGM() ? 1 : 0;
		this.title = cha.getTitle();
		this._expPercent = Experience.getExpPercent(cha.getLevel(), cha.getExp());
		//
		Clan clan = cha.getClan();
		Alliance alliance = clan == null ? null : clan.getAlliance();
		//
		this.clan_id = clan == null ? 0 : clan.getClanId();
		this.clan_crest_id = clan == null ? 0 : clan.getCrestId();
		//
		this.ally_id = alliance == null ? 0 : alliance.getAllyId();
		// ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();

		this.private_store = cha.isInObserverMode() ? Player.STORE_OBSERVING_GAMES : cha.getPrivateStoreType();
		this.DwarvenCraftLevel = Math.max(cha.getSkillLevel(1320), 0);
		this.pk_kills = cha.getPkKills();
		this.pvp_kills = cha.getPvpKills();
		this.rec_left = cha.getRecomLeft(); // c2 recommendations remaining
		this.rec_have = cha.getRecomHave(); // c2 recommendations received
		this.curCp = (int) cha.getCurrentCp();
		this.maxCp = cha.getMaxCp();
		this.running = cha.isRunning() ? 0x01 : 0x00;
		this.pledge_class = cha.getPledgeClass();
		this.noble = cha.isNoble() ? 1 : 0; // 0x01: symbol on char menu ctrl+I
		this.hero = ((cha.isHero()) || (cha.isFakeHero()) ? 1 : 0); // 0x01: Hero Aura and symbol
		this.name_color = cha.getVisibleNameColor();
		this.title_color = cha.getVisibleTitleColor();
		this.attackElement = cha.getAttackElement();
		this.attackElementValue = cha.getAttack(this.attackElement);
		this.defenceFire = cha.getDefence(Element.FIRE);
		this.defenceWater = cha.getDefence(Element.WATER);
		this.defenceWind = cha.getDefence(Element.WIND);
		this.defenceEarth = cha.getDefence(Element.EARTH);
		this.defenceHoly = cha.getDefence(Element.HOLY);
		this.defenceUnholy = cha.getDefence(Element.UNHOLY);
		this.fame = cha.getFame();
		this.vitality = (int) cha.getVitality();
		this.talismans = cha.getTalismanCount();
		this.openCloak = cha.getOpenCloak();
		this._inv = new int[Inventory.PAPERDOLL_MAX][3];
		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			this._inv[PAPERDOLL_ID][0] = cha.getInventory().getPaperdollObjectId(PAPERDOLL_ID);
			this._inv[PAPERDOLL_ID][1] = cha.getInventory().getPaperdollItemId(PAPERDOLL_ID);
			this._inv[PAPERDOLL_ID][2] = cha.getInventory().getPaperdollAugmentationId(PAPERDOLL_ID);
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x95);

		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeD(this._loc.h);
		this.writeD(this.obj_id);
		this.writeS(this._name);
		this.writeD(this._race);
		this.writeD(this._sex);
		this.writeD(this.class_id);
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
		this.writeD(this.pk_kills);

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
		this.writeD(this._swimSpd); // swimspeed
		this.writeD(this._swimSpd); // swimspeed
		this.writeD(this._runSpd);
		this.writeD(this._walkSpd);
		this.writeD(this._runSpd);
		this.writeD(this._walkSpd);
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
		this.writeC(this.mount_type);
		this.writeC(this.private_store);
		this.writeC(this.DwarvenCraftLevel); // _cha.getDwarvenCraftLevel() > 0 ? 1 : 0
		this.writeD(this.pk_kills);
		this.writeD(this.pvp_kills);
		this.writeH(this.rec_left);
		this.writeH(this.rec_have); // Blue value for name (0 = white, 255 = pure blue)
		this.writeD(this.class_id);
		this.writeD(0x00); // special effects? circles around player...
		this.writeD(this.maxCp);
		this.writeD(this.curCp);
		this.writeC(this.running); // changes the Speed display on Status Window
		this.writeC(321);
		this.writeD(this.pledge_class); // changes the text above CP on Status Window
		this.writeC(this.noble);
		this.writeC(this.hero);
		this.writeD(this.name_color);
		this.writeD(this.title_color);

		this.writeH(this.attackElement.getId());
		this.writeH(this.attackElementValue);
		this.writeH(this.defenceFire);
		this.writeH(this.defenceWater);
		this.writeH(this.defenceWind);
		this.writeH(this.defenceEarth);
		this.writeH(this.defenceHoly);
		this.writeH(this.defenceUnholy);

		this.writeD(this.fame);
		this.writeD(this.vitality);
	}
}