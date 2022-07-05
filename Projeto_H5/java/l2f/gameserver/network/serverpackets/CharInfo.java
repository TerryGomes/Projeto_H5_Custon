package l2f.gameserver.network.serverpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.instancemanager.CursedWeaponsManager;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.TeamType;
import l2f.gameserver.model.entity.events.impl.AbstractFightClub;
import l2f.gameserver.model.instances.DecoyInstance;
import l2f.gameserver.model.items.Inventory;
import l2f.gameserver.model.matching.MatchingRoom;
import l2f.gameserver.model.pledge.Alliance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.skills.effects.EffectCubic;
import l2f.gameserver.templates.npc.IFakePlayer;
import l2f.gameserver.utils.Location;

public class CharInfo extends L2GameServerPacket
{
	private static final Logger _log = LoggerFactory.getLogger(CharInfo.class);

	private int[][] _inv;
	private int _mAtkSpd, _pAtkSpd;
	private int _runSpd, _walkSpd, _swimSpd, _flRunSpd, _flWalkSpd, _flyRunSpd, _flyWalkSpd;
	private Location _loc, _fishLoc;
	private String _name, _title;
	private int _objId, _race, _sex, base_class, pvp_flag, karma, rec_have;
	private double speed_move, speed_atack, col_radius, col_height;
	private int hair_style, hair_color, face, _abnormalEffect, _abnormalEffect2;
	private int clan_id, clan_crest_id, large_clan_crest_id, ally_id, ally_crest_id, class_id;
	private int _sit, _run, _combat, _dead, private_store, _enchant;
	private int _noble, _hero, _fishing, mount_type;
	private int plg_class, pledge_type, clan_rep_score, cw_level, mount_id;
	private int _nameColor, _title_color, _transform, _agathion, _clanBoatObjectId;
	private EffectCubic[] cubics;
	private boolean _invis, _isPartyRoomLeader, _isFlying;
	private TeamType _team;

	public CharInfo(Player cha, Player forPlayer)
	{
		this((Creature) cha, forPlayer);
	}

	public CharInfo(DecoyInstance cha, Player forPlayer)
	{
		this((Creature) cha, forPlayer);
	}

	public CharInfo(Creature cha, Player forPlayer)
	{
		if (cha == null)
		{
			_log.error("CharInfo: cha is null!");
			Thread.dumpStack();
			return;
		}

		if (cha.isInvisible())
		{
			_invis = true;
		}

		if (cha.isDeleted())
		{
			return;
		}

		Player player = cha.getPlayer();
		if (player == null)
		{
			return;
		}

		if (player.isInBoat())
		{
			_loc = player.getInBoatPosition();
			if (player.isClanAirShipDriver())
			{
				_clanBoatObjectId = player.getBoat().getObjectId();
			}
		}

		if (_loc == null)
		{
			_loc = cha.getLoc();
		}

		_objId = cha.getObjectId();

		// Cursed weapon and transformation to hide the name of the TV and all the other markings
		if (player.getTransformationName() != null || (player.getReflection() == ReflectionManager.GIRAN_HARBOR || player.getReflection() == ReflectionManager.PARNASSUS) && player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			_name = player.getTransformationName() != null ? player.getTransformationName() : player.getVisibleName();
			_title = "";
			clan_id = 0;
			clan_crest_id = 0;
			ally_id = 0;
			ally_crest_id = 0;
			large_clan_crest_id = 0;
			if (player.isCursedWeaponEquipped())
			{
				cw_level = CursedWeaponsManager.getInstance().getLevel(player.getCursedWeaponEquippedId());
			}
		}
		else
		{
			_name = player.getVisibleName();
			if (player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE && !player.isInBuffStore())
			{
				_title = "";
			}
			else if (!player.isConnected() && !player.isFakePlayer() && !player.isInBuffStore() && !player.isPhantom())
			{
				_title = "No Carrier";
				_title_color = 255;
			}
			else
			{
				_title = player.getVisibleTitle();
				_title_color = player.getVisibleTitleColor();
			}

			Clan clan = player.getClan();
			Alliance alliance = clan == null ? null : clan.getAlliance();
			//
			clan_id = clan == null ? 0 : clan.getClanId();
			clan_crest_id = clan == null ? 0 : clan.getCrestId();
			large_clan_crest_id = clan == null ? 0 : clan.getCrestLargeId();
			//
			ally_id = alliance == null ? 0 : alliance.getAllyId();
			ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();

			cw_level = 0;
		}

//		if (_invis)
//			_title = "[I]";

		if (player.isMounted())
		{
			_enchant = 0;
			mount_id = player.getMountNpcId() + 1000000;
			mount_type = player.getMountType();
		}
		else
		{
			_enchant = player.getEnchantEffect();
			mount_id = 0;
			mount_type = 0;
		}

		_inv = new int[Inventory.PAPERDOLL_MAX][2];
		for (int PAPERDOLL_ID : PAPERDOLL_ORDER)
		{
			_inv[PAPERDOLL_ID][0] = player.isInOlympiadMode() ? player.getInventory().getPaperdollItemId(PAPERDOLL_ID) : player.getInventory().getPaperdollVisualItemId(PAPERDOLL_ID);
			_inv[PAPERDOLL_ID][1] = player.getInventory().getPaperdollAugmentationId(PAPERDOLL_ID);
		}

		_mAtkSpd = player.getMAtkSpd();
		_pAtkSpd = player.getPAtkSpd();
		speed_move = player.getMovementSpeedMultiplier();
		_runSpd = (int) (player.getRunSpeed() / speed_move);
		_walkSpd = (int) (player.getWalkSpeed() / speed_move);

		_flRunSpd = 0; // TODO
		_flWalkSpd = 0; // TODO

		if (player.isFlying())
		{
			_flyRunSpd = _runSpd;
			_flyWalkSpd = _walkSpd;
		}
		else
		{
			_flyRunSpd = 0;
			_flyWalkSpd = 0;
		}

		_swimSpd = player.getSwimSpeed();
		_race = player.getBaseTemplate().race.ordinal();
		_sex = player.getSex();
		base_class = player.getBaseClassId();
		pvp_flag = player.getPvpFlag();
		karma = player.getKarma();

		speed_atack = player.getAttackSpeedMultiplier();
		col_radius = player.getColRadius();
		col_height = player.getColHeight();
		hair_style = player.getHairStyle();
		hair_color = player.getHairColor();
		face = player.getFace();
		if (clan_id > 0 && player.getClan() != null)
		{
			clan_rep_score = player.getClan().getReputationScore();
		}
		else
		{
			clan_rep_score = 0;
		}
		_sit = player.isSitting() ? 0 : 1; // standing = 1 sitting = 0
		_run = player.isRunning() ? 1 : 0; // running = 1 walking = 0
		_combat = player.isInCombat() ? 1 : 0;
		_dead = player.isAlikeDead() ? 1 : 0;
		private_store = player.isInObserverMode() ? Player.STORE_OBSERVING_GAMES : (player.isInBuffStore() ? 0 : player.getPrivateStoreType());
		cubics = player.getCubics().toArray(new EffectCubic[player.getCubics().size()]);
		_abnormalEffect = player.getAbnormalEffect();
		_abnormalEffect2 = player.getAbnormalEffect2();
		rec_have = player.isGM() ? 0 : player.getRecomHave();
		class_id = player.getClassId().getId();
		_team = player.getTeam();

		_noble = player.isNoble() ? 1 : 0; // 0x01: symbol on char menu ctrl+I
		_hero = player.isHero() || player.isHeroAura() || player.isGM() && Config.GM_HERO_AURA ? 1 : 0; // 0x01: Hero Aura
		_fishing = player.isFishing() ? 1 : 0;
		_fishLoc = player.getFishLoc();
		_nameColor = player.getVisibleNameColor(); // New C5
		plg_class = player.getPledgeClass();
		pledge_type = player.getPledgeType();
		_transform = player.getTransformation();
		_agathion = player.getAgathionId();
		_isPartyRoomLeader = player.getMatchingRoom() != null && player.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING && player.getMatchingRoom().getLeader() == player;
		_isFlying = player.isInFlyingTransform();

		// Minimalizing Lags in towns
		if (player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE && !player.isInBuffStore())
		{
			_title = "";
			clan_crest_id = 0;
			ally_crest_id = 0;
			// Weapon
			_inv[Inventory.PAPERDOLL_LHAND][0] = 0;
			_inv[Inventory.PAPERDOLL_LHAND][1] = 0;
			_inv[Inventory.PAPERDOLL_RHAND][0] = 0;
			_inv[Inventory.PAPERDOLL_RHAND][1] = 0;
			_inv[Inventory.PAPERDOLL_LRHAND][0] = 0;
			_inv[Inventory.PAPERDOLL_LRHAND][1] = 0;
			// Cloak
			_inv[Inventory.PAPERDOLL_BACK][0] = 0;
			_inv[Inventory.PAPERDOLL_BACK][1] = 0;
			// Accessory
			_inv[Inventory.PAPERDOLL_HAIR][0] = 0;
			_inv[Inventory.PAPERDOLL_HAIR][1] = 0;
			_inv[Inventory.PAPERDOLL_DHAIR][0] = 0;
			_inv[Inventory.PAPERDOLL_DHAIR][1] = 0;

		}

		if (player.isInFightClub())
		{
			AbstractFightClub fightClubEvent = player.getFightClubEvent();
			_name = fightClubEvent.getVisibleName(player, _name, false);
			_title = fightClubEvent.getVisibleTitle(player, _title, false);
			_title_color = fightClubEvent.getVisibleTitleColor(player, _title_color, false);
			_nameColor = fightClubEvent.getVisibleNameColor(player, _nameColor, false);
		}
	}

	public CharInfo(IFakePlayer fakePlayer)
	{
		_objId = fakePlayer.getObjectId();
		_name = fakePlayer.getName();
		_title = fakePlayer.getTitle();
		_loc = fakePlayer.getLocation();
		pvp_flag = fakePlayer.getPvPFlag();
		karma = fakePlayer.getKarma();
		rec_have = fakePlayer.getRecommendations();
		_nameColor = fakePlayer.getNameColor();
		_title_color = fakePlayer.getTitleColor();
		_race = fakePlayer.getRace();
		_sex = fakePlayer.getSex();
		class_id = fakePlayer.getClassId();
		base_class = fakePlayer.getClassId();
		col_radius = fakePlayer.getCollisionRadius();
		col_height = fakePlayer.getCollisionHeight();
		hair_style = fakePlayer.getHairStyle();
		hair_color = fakePlayer.getHairColor();
		face = fakePlayer.getFace();
		_abnormalEffect = fakePlayer.getAbnormalEffect();
		_abnormalEffect2 = fakePlayer.getAbnormalEffect2();
		_noble = fakePlayer.getNoble();
		_hero = fakePlayer.getHero();
		_invis = fakePlayer.isInvis();
		_transform = fakePlayer.getTransform();
		cw_level = fakePlayer.getCursedWeaponLevel();
		_agathion = fakePlayer.getAgathion();
		cubics = fakePlayer.getCubics();
		_team = fakePlayer.getTeam();
		_fishing = fakePlayer.getFishing();
		_fishLoc = fakePlayer.getFishLocation();
		mount_type = fakePlayer.getMountType();
		mount_id = fakePlayer.getMountId();
		_sit = fakePlayer.getSit();
		_run = fakePlayer.getRun();
		_combat = fakePlayer.getCombat();
		_dead = fakePlayer.getDead();
		private_store = fakePlayer.getPrivateStore();
		_mAtkSpd = fakePlayer.getMAtkSpd();
		_pAtkSpd = fakePlayer.getPAtkSpd();
		_runSpd = fakePlayer.getRunSpeed();
		_walkSpd = fakePlayer.getWalkSpeed();
		speed_move = fakePlayer.getSpeedMove();
		speed_atack = fakePlayer.getSpeedAttack();
		_swimSpd = fakePlayer.getSwimSpd();
		_flyRunSpd = fakePlayer.getFlySpd();
		_flyWalkSpd = fakePlayer.getFlyWalkSpd();
		_isFlying = fakePlayer.isFlying();
		_inv = fakePlayer.getInventory();
		_enchant = fakePlayer.getEnchant();
		_isPartyRoomLeader = fakePlayer.isPartyRoomLeader();
		clan_id = fakePlayer.getClanId();
		clan_crest_id = fakePlayer.getClanCrestId();
		large_clan_crest_id = fakePlayer.getLargeClanCrestId();
		ally_id = fakePlayer.getAllyId();
		ally_crest_id = fakePlayer.getAllyCrestId();
		plg_class = fakePlayer.getPledgeClass();
		pledge_type = fakePlayer.getPledgeType();
		clan_rep_score = fakePlayer.getClanReputationScore();
		_clanBoatObjectId = fakePlayer.getClanBoatObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if ((activeChar == null) || (_objId == 0))
		{
			return;
		}

		if (activeChar.getObjectId() == _objId)
		{
			_log.error("You cant send CharInfo about his character to active user!!!");
			return;
		}

		if (_invis && !activeChar.isGM())
		{
			return;
		}

		writeC(0x31);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z + Config.CLIENT_Z_SHIFT);
		writeD(_clanBoatObjectId);
		writeD(_objId);
		writeS(_name);
		writeD(_race);
		writeD(_sex);
		writeD(base_class);

		for (int PAPERDOLL_ID : PAPERDOLL_ORDER)
		{
			writeD(_inv[PAPERDOLL_ID][0]);
		}

		for (int PAPERDOLL_ID : PAPERDOLL_ORDER)
		{
			writeD(_inv[PAPERDOLL_ID][1]);
		}

		writeD(0x01); // TODO talisman count(VISTALL)
		writeD(0x00); // TODO cloak status(VISTALL)

		writeD(pvp_flag);
		writeD(karma);

		writeD(_mAtkSpd);
		writeD(_pAtkSpd);

		writeD(0x00);

		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_swimSpd);
		writeD(_swimSpd);
		writeD(_flRunSpd);
		writeD(_flWalkSpd);
		writeD(_flyRunSpd);
		writeD(_flyWalkSpd);

		writeF(speed_move); // _cha.getProperMultiplier()
		writeF(speed_atack); // _cha.getAttackSpeedMultiplier()
		writeF(col_radius);
		writeF(col_height);
		writeD(hair_style);
		writeD(hair_color);
		writeD(face);
		writeS(_title);
		writeD(clan_id);
		writeD(clan_crest_id);
		writeD(ally_id);
		writeD(ally_crest_id);

		writeC(_sit);
		writeC(_run);
		writeC(_combat);
		writeC(_dead);
		writeC(0x00); // is invisible
		writeC(mount_type); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
		writeC(private_store);
		writeH(cubics.length);
		for (EffectCubic cubic : cubics)
		{
			writeH(cubic == null ? 0 : cubic.getId());
		}
		writeC(_isPartyRoomLeader ? 0x01 : 0x00); // find party members
		writeD(_abnormalEffect);
		writeC(_isFlying ? 0x02 : 0x00);
		writeH(rec_have);
		writeD(mount_id);
		writeD(class_id);
		writeD(0x00);
		writeC(_enchant);

		writeC(_team.ordinal()); // team circle around feet 1 = Blue, 2 = red

		writeD(large_clan_crest_id);
		writeC(_noble);
		writeC(_hero);

		writeC(_fishing);
		writeD(_fishLoc.x);
		writeD(_fishLoc.y);
		writeD(_fishLoc.z);

		writeD(_nameColor);
		writeD(_loc.h);
		writeD(plg_class);
		writeD(pledge_type);
		writeD(_title_color);
		writeD(cw_level);
		writeD(clan_rep_score);
		writeD(_transform);
		writeD(_agathion);

		writeD(0x01); // T2

		writeD(_abnormalEffect2);
	}

	public static final int[] PAPERDOLL_ORDER =
	{
		Inventory.PAPERDOLL_UNDER,
		Inventory.PAPERDOLL_HEAD,
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_LHAND,
		Inventory.PAPERDOLL_GLOVES,
		Inventory.PAPERDOLL_CHEST,
		Inventory.PAPERDOLL_LEGS,
		Inventory.PAPERDOLL_FEET,
		Inventory.PAPERDOLL_BACK,
		Inventory.PAPERDOLL_LRHAND,
		Inventory.PAPERDOLL_HAIR,
		Inventory.PAPERDOLL_DHAIR,
		Inventory.PAPERDOLL_RBRACELET,
		Inventory.PAPERDOLL_LBRACELET,
		Inventory.PAPERDOLL_DECO1,
		Inventory.PAPERDOLL_DECO2,
		Inventory.PAPERDOLL_DECO3,
		Inventory.PAPERDOLL_DECO4,
		Inventory.PAPERDOLL_DECO5,
		Inventory.PAPERDOLL_DECO6,
		Inventory.PAPERDOLL_BELT
	};
}