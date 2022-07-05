package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.Element;
import l2f.gameserver.model.base.Experience;
import l2f.gameserver.model.items.Inventory;
import l2f.gameserver.model.pledge.Alliance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.utils.Location;

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
		_loc = cha.getLoc();
		obj_id = cha.getObjectId();
		_name = cha.getName();
		_race = cha.getRace().ordinal();
		_sex = cha.getSex();
		class_id = cha.getClassId().getId();
		level = cha.getLevel();
		_exp = cha.getExp();
		_str = cha.getSTR();
		_dex = cha.getDEX();
		_con = cha.getCON();
		_int = cha.getINT();
		_wit = cha.getWIT();
		_men = cha.getMEN();
		curHp = (int) cha.getCurrentHp();
		maxHp = cha.getMaxHp();
		curMp = (int) cha.getCurrentMp();
		maxMp = cha.getMaxMp();
		_sp = cha.getIntSp();
		curLoad = cha.getCurrentLoad();
		maxLoad = cha.getMaxLoad();
		_patk = cha.getPAtk(null);
		_patkspd = cha.getPAtkSpd();
		_pdef = cha.getPDef(null);
		evasion = cha.getEvasionRate(null);
		accuracy = cha.getAccuracy();
		crit = cha.getCriticalHit(null, null);
		_matk = cha.getMAtk(null, null);
		_matkspd = cha.getMAtkSpd();
		_mdef = cha.getMDef(null, null);
		pvp_flag = cha.getPvpFlag();
		karma = cha.getKarma();
		_runSpd = cha.getRunSpeed();
		_walkSpd = cha.getWalkSpeed();
		_swimSpd = cha.getSwimSpeed();
		move_speed = cha.getMovementSpeedMultiplier();
		attack_speed = cha.getAttackSpeedMultiplier();
		mount_type = cha.getMountType();
		col_radius = cha.getColRadius();
		col_height = cha.getColHeight();
		hair_style = cha.getHairStyle();
		hair_color = cha.getHairColor();
		face = cha.getFace();
		gm_commands = cha.isGM() ? 1 : 0;
		title = cha.getTitle();
		_expPercent = Experience.getExpPercent(cha.getLevel(), cha.getExp());
		//
		Clan clan = cha.getClan();
		Alliance alliance = clan == null ? null : clan.getAlliance();
		//
		clan_id = clan == null ? 0 : clan.getClanId();
		clan_crest_id = clan == null ? 0 : clan.getCrestId();
		//
		ally_id = alliance == null ? 0 : alliance.getAllyId();
		// ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();

		private_store = cha.isInObserverMode() ? Player.STORE_OBSERVING_GAMES : cha.getPrivateStoreType();
		DwarvenCraftLevel = Math.max(cha.getSkillLevel(1320), 0);
		pk_kills = cha.getPkKills();
		pvp_kills = cha.getPvpKills();
		rec_left = cha.getRecomLeft(); // c2 recommendations remaining
		rec_have = cha.getRecomHave(); // c2 recommendations received
		curCp = (int) cha.getCurrentCp();
		maxCp = cha.getMaxCp();
		running = cha.isRunning() ? 0x01 : 0x00;
		pledge_class = cha.getPledgeClass();
		noble = cha.isNoble() ? 1 : 0; // 0x01: symbol on char menu ctrl+I
		hero = ((cha.isHero()) || (cha.isFakeHero()) ? 1 : 0); // 0x01: Hero Aura and symbol
		name_color = cha.getVisibleNameColor();
		title_color = cha.getVisibleTitleColor();
		attackElement = cha.getAttackElement();
		attackElementValue = cha.getAttack(attackElement);
		defenceFire = cha.getDefence(Element.FIRE);
		defenceWater = cha.getDefence(Element.WATER);
		defenceWind = cha.getDefence(Element.WIND);
		defenceEarth = cha.getDefence(Element.EARTH);
		defenceHoly = cha.getDefence(Element.HOLY);
		defenceUnholy = cha.getDefence(Element.UNHOLY);
		fame = cha.getFame();
		vitality = (int) cha.getVitality();
		talismans = cha.getTalismanCount();
		openCloak = cha.getOpenCloak();
		_inv = new int[Inventory.PAPERDOLL_MAX][3];
		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			_inv[PAPERDOLL_ID][0] = cha.getInventory().getPaperdollObjectId(PAPERDOLL_ID);
			_inv[PAPERDOLL_ID][1] = cha.getInventory().getPaperdollItemId(PAPERDOLL_ID);
			_inv[PAPERDOLL_ID][2] = cha.getInventory().getPaperdollAugmentationId(PAPERDOLL_ID);
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x95);

		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_loc.h);
		writeD(obj_id);
		writeS(_name);
		writeD(_race);
		writeD(_sex);
		writeD(class_id);
		writeD(level);
		writeQ(_exp);
		writeF(_expPercent);
		writeD(_str);
		writeD(_dex);
		writeD(_con);
		writeD(_int);
		writeD(_wit);
		writeD(_men);
		writeD(maxHp);
		writeD(curHp);
		writeD(maxMp);
		writeD(curMp);
		writeD(_sp);
		writeD(curLoad);
		writeD(maxLoad);
		writeD(pk_kills);

		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			writeD(_inv[PAPERDOLL_ID][0]);
		}

		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			writeD(_inv[PAPERDOLL_ID][1]);
		}

		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			writeD(_inv[PAPERDOLL_ID][2]);
		}

		writeD(talismans);
		writeD(openCloak ? 0x01 : 0x00);

		writeD(_patk);
		writeD(_patkspd);
		writeD(_pdef);
		writeD(evasion);
		writeD(accuracy);
		writeD(crit);
		writeD(_matk);
		writeD(_matkspd);
		writeD(_patkspd);
		writeD(_mdef);
		writeD(pvp_flag);
		writeD(karma);
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_swimSpd); // swimspeed
		writeD(_swimSpd); // swimspeed
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_runSpd);
		writeD(_walkSpd);
		writeF(move_speed);
		writeF(attack_speed);
		writeF(col_radius);
		writeF(col_height);
		writeD(hair_style);
		writeD(hair_color);
		writeD(face);
		writeD(gm_commands);
		writeS(title);
		writeD(clan_id);
		writeD(clan_crest_id);
		writeD(ally_id);
		writeC(mount_type);
		writeC(private_store);
		writeC(DwarvenCraftLevel); // _cha.getDwarvenCraftLevel() > 0 ? 1 : 0
		writeD(pk_kills);
		writeD(pvp_kills);
		writeH(rec_left);
		writeH(rec_have); // Blue value for name (0 = white, 255 = pure blue)
		writeD(class_id);
		writeD(0x00); // special effects? circles around player...
		writeD(maxCp);
		writeD(curCp);
		writeC(running); // changes the Speed display on Status Window
		writeC(321);
		writeD(pledge_class); // changes the text above CP on Status Window
		writeC(noble);
		writeC(hero);
		writeD(name_color);
		writeD(title_color);

		writeH(attackElement.getId());
		writeH(attackElementValue);
		writeH(defenceFire);
		writeH(defenceWater);
		writeH(defenceWind);
		writeH(defenceEarth);
		writeH(defenceHoly);
		writeH(defenceUnholy);

		writeD(fame);
		writeD(vitality);
	}
}