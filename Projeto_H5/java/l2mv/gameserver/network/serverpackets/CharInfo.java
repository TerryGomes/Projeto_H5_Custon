package l2mv.gameserver.network.serverpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.instancemanager.CursedWeaponsManager;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.entity.events.impl.AbstractFightClub;
import l2mv.gameserver.model.instances.DecoyInstance;
import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.model.matching.MatchingRoom;
import l2mv.gameserver.model.pledge.Alliance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.skills.effects.EffectCubic;
import l2mv.gameserver.templates.npc.IFakePlayer;
import l2mv.gameserver.utils.Location;

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
			this._invis = true;
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
			this._loc = player.getInBoatPosition();
			if (player.isClanAirShipDriver())
			{
				this._clanBoatObjectId = player.getBoat().getObjectId();
			}
		}

		if (this._loc == null)
		{
			this._loc = cha.getLoc();
		}

		this._objId = cha.getObjectId();

		// Cursed weapon and transformation to hide the name of the TV and all the other markings
		if (player.getTransformationName() != null || (player.getReflection() == ReflectionManager.GIRAN_HARBOR || player.getReflection() == ReflectionManager.PARNASSUS) && player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			this._name = player.getTransformationName() != null ? player.getTransformationName() : player.getVisibleName();
			this._title = "";
			this.clan_id = 0;
			this.clan_crest_id = 0;
			this.ally_id = 0;
			this.ally_crest_id = 0;
			this.large_clan_crest_id = 0;
			if (player.isCursedWeaponEquipped())
			{
				this.cw_level = CursedWeaponsManager.getInstance().getLevel(player.getCursedWeaponEquippedId());
			}
		}
		else
		{
			this._name = player.getVisibleName();
			if (player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE && !player.isInBuffStore())
			{
				this._title = "";
			}
			else if (!player.isConnected() && !player.isFakePlayer() && !player.isInBuffStore() && !player.isPhantom())
			{
				this._title = "No Carrier";
				this._title_color = 255;
			}
			else
			{
				this._title = player.getVisibleTitle();
				this._title_color = player.getVisibleTitleColor();
			}

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
		}

//		if (_invis)
//			_title = "[I]";

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

		this._inv = new int[Inventory.PAPERDOLL_MAX][2];
		for (int PAPERDOLL_ID : PAPERDOLL_ORDER)
		{
			this._inv[PAPERDOLL_ID][0] = player.isInOlympiadMode() ? player.getInventory().getPaperdollItemId(PAPERDOLL_ID) : player.getInventory().getPaperdollVisualItemId(PAPERDOLL_ID);
			this._inv[PAPERDOLL_ID][1] = player.getInventory().getPaperdollAugmentationId(PAPERDOLL_ID);
		}

		this._mAtkSpd = player.getMAtkSpd();
		this._pAtkSpd = player.getPAtkSpd();
		this.speed_move = player.getMovementSpeedMultiplier();
		this._runSpd = (int) (player.getRunSpeed() / this.speed_move);
		this._walkSpd = (int) (player.getWalkSpeed() / this.speed_move);

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

		this._swimSpd = player.getSwimSpeed();
		this._race = player.getBaseTemplate().race.ordinal();
		this._sex = player.getSex();
		this.base_class = player.getBaseClassId();
		this.pvp_flag = player.getPvpFlag();
		this.karma = player.getKarma();

		this.speed_atack = player.getAttackSpeedMultiplier();
		this.col_radius = player.getColRadius();
		this.col_height = player.getColHeight();
		this.hair_style = player.getHairStyle();
		this.hair_color = player.getHairColor();
		this.face = player.getFace();
		if (this.clan_id > 0 && player.getClan() != null)
		{
			this.clan_rep_score = player.getClan().getReputationScore();
		}
		else
		{
			this.clan_rep_score = 0;
		}
		this._sit = player.isSitting() ? 0 : 1; // standing = 1 sitting = 0
		this._run = player.isRunning() ? 1 : 0; // running = 1 walking = 0
		this._combat = player.isInCombat() ? 1 : 0;
		this._dead = player.isAlikeDead() ? 1 : 0;
		this.private_store = player.isInObserverMode() ? Player.STORE_OBSERVING_GAMES : (player.isInBuffStore() ? 0 : player.getPrivateStoreType());
		this.cubics = player.getCubics().toArray(new EffectCubic[player.getCubics().size()]);
		this._abnormalEffect = player.getAbnormalEffect();
		this._abnormalEffect2 = player.getAbnormalEffect2();
		this.rec_have = player.isGM() ? 0 : player.getRecomHave();
		this.class_id = player.getClassId().getId();
		this._team = player.getTeam();

		this._noble = player.isNoble() ? 1 : 0; // 0x01: symbol on char menu ctrl+I
		this._hero = player.isHero() || player.isHeroAura() || player.isGM() && Config.GM_HERO_AURA ? 1 : 0; // 0x01: Hero Aura
		this._fishing = player.isFishing() ? 1 : 0;
		this._fishLoc = player.getFishLoc();
		this._nameColor = player.getVisibleNameColor(); // New C5
		this.plg_class = player.getPledgeClass();
		this.pledge_type = player.getPledgeType();
		this._transform = player.getTransformation();
		this._agathion = player.getAgathionId();
		this._isPartyRoomLeader = player.getMatchingRoom() != null && player.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING && player.getMatchingRoom().getLeader() == player;
		this._isFlying = player.isInFlyingTransform();

		// Minimalizing Lags in towns
		if (player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE && !player.isInBuffStore())
		{
			this._title = "";
			this.clan_crest_id = 0;
			this.ally_crest_id = 0;
			// Weapon
			this._inv[Inventory.PAPERDOLL_LHAND][0] = 0;
			this._inv[Inventory.PAPERDOLL_LHAND][1] = 0;
			this._inv[Inventory.PAPERDOLL_RHAND][0] = 0;
			this._inv[Inventory.PAPERDOLL_RHAND][1] = 0;
			this._inv[Inventory.PAPERDOLL_LRHAND][0] = 0;
			this._inv[Inventory.PAPERDOLL_LRHAND][1] = 0;
			// Cloak
			this._inv[Inventory.PAPERDOLL_BACK][0] = 0;
			this._inv[Inventory.PAPERDOLL_BACK][1] = 0;
			// Accessory
			this._inv[Inventory.PAPERDOLL_HAIR][0] = 0;
			this._inv[Inventory.PAPERDOLL_HAIR][1] = 0;
			this._inv[Inventory.PAPERDOLL_DHAIR][0] = 0;
			this._inv[Inventory.PAPERDOLL_DHAIR][1] = 0;

		}

		if (player.isInFightClub())
		{
			AbstractFightClub fightClubEvent = player.getFightClubEvent();
			this._name = fightClubEvent.getVisibleName(player, this._name, false);
			this._title = fightClubEvent.getVisibleTitle(player, this._title, false);
			this._title_color = fightClubEvent.getVisibleTitleColor(player, this._title_color, false);
			this._nameColor = fightClubEvent.getVisibleNameColor(player, this._nameColor, false);
		}
	}

	public CharInfo(IFakePlayer fakePlayer)
	{
		this._objId = fakePlayer.getObjectId();
		this._name = fakePlayer.getName();
		this._title = fakePlayer.getTitle();
		this._loc = fakePlayer.getLocation();
		this.pvp_flag = fakePlayer.getPvPFlag();
		this.karma = fakePlayer.getKarma();
		this.rec_have = fakePlayer.getRecommendations();
		this._nameColor = fakePlayer.getNameColor();
		this._title_color = fakePlayer.getTitleColor();
		this._race = fakePlayer.getRace();
		this._sex = fakePlayer.getSex();
		this.class_id = fakePlayer.getClassId();
		this.base_class = fakePlayer.getClassId();
		this.col_radius = fakePlayer.getCollisionRadius();
		this.col_height = fakePlayer.getCollisionHeight();
		this.hair_style = fakePlayer.getHairStyle();
		this.hair_color = fakePlayer.getHairColor();
		this.face = fakePlayer.getFace();
		this._abnormalEffect = fakePlayer.getAbnormalEffect();
		this._abnormalEffect2 = fakePlayer.getAbnormalEffect2();
		this._noble = fakePlayer.getNoble();
		this._hero = fakePlayer.getHero();
		this._invis = fakePlayer.isInvis();
		this._transform = fakePlayer.getTransform();
		this.cw_level = fakePlayer.getCursedWeaponLevel();
		this._agathion = fakePlayer.getAgathion();
		this.cubics = fakePlayer.getCubics();
		this._team = fakePlayer.getTeam();
		this._fishing = fakePlayer.getFishing();
		this._fishLoc = fakePlayer.getFishLocation();
		this.mount_type = fakePlayer.getMountType();
		this.mount_id = fakePlayer.getMountId();
		this._sit = fakePlayer.getSit();
		this._run = fakePlayer.getRun();
		this._combat = fakePlayer.getCombat();
		this._dead = fakePlayer.getDead();
		this.private_store = fakePlayer.getPrivateStore();
		this._mAtkSpd = fakePlayer.getMAtkSpd();
		this._pAtkSpd = fakePlayer.getPAtkSpd();
		this._runSpd = fakePlayer.getRunSpeed();
		this._walkSpd = fakePlayer.getWalkSpeed();
		this.speed_move = fakePlayer.getSpeedMove();
		this.speed_atack = fakePlayer.getSpeedAttack();
		this._swimSpd = fakePlayer.getSwimSpd();
		this._flyRunSpd = fakePlayer.getFlySpd();
		this._flyWalkSpd = fakePlayer.getFlyWalkSpd();
		this._isFlying = fakePlayer.isFlying();
		this._inv = fakePlayer.getInventory();
		this._enchant = fakePlayer.getEnchant();
		this._isPartyRoomLeader = fakePlayer.isPartyRoomLeader();
		this.clan_id = fakePlayer.getClanId();
		this.clan_crest_id = fakePlayer.getClanCrestId();
		this.large_clan_crest_id = fakePlayer.getLargeClanCrestId();
		this.ally_id = fakePlayer.getAllyId();
		this.ally_crest_id = fakePlayer.getAllyCrestId();
		this.plg_class = fakePlayer.getPledgeClass();
		this.pledge_type = fakePlayer.getPledgeType();
		this.clan_rep_score = fakePlayer.getClanReputationScore();
		this._clanBoatObjectId = fakePlayer.getClanBoatObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if ((activeChar == null) || (this._objId == 0))
		{
			return;
		}

		if (activeChar.getObjectId() == this._objId)
		{
			_log.error("You cant send CharInfo about his character to active user!!!");
			return;
		}

		if (this._invis && !activeChar.isGM())
		{
			return;
		}

		this.writeC(0x31);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z + Config.CLIENT_Z_SHIFT);
		this.writeD(this._clanBoatObjectId);
		this.writeD(this._objId);
		this.writeS(this._name);
		this.writeD(this._race);
		this.writeD(this._sex);
		this.writeD(this.base_class);

		for (int PAPERDOLL_ID : PAPERDOLL_ORDER)
		{
			this.writeD(this._inv[PAPERDOLL_ID][0]);
		}

		for (int PAPERDOLL_ID : PAPERDOLL_ORDER)
		{
			this.writeD(this._inv[PAPERDOLL_ID][1]);
		}

		this.writeD(0x01); // TODO talisman count(VISTALL)
		this.writeD(0x00); // TODO cloak status(VISTALL)

		this.writeD(this.pvp_flag);
		this.writeD(this.karma);

		this.writeD(this._mAtkSpd);
		this.writeD(this._pAtkSpd);

		this.writeD(0x00);

		this.writeD(this._runSpd);
		this.writeD(this._walkSpd);
		this.writeD(this._swimSpd);
		this.writeD(this._swimSpd);
		this.writeD(this._flRunSpd);
		this.writeD(this._flWalkSpd);
		this.writeD(this._flyRunSpd);
		this.writeD(this._flyWalkSpd);

		this.writeF(this.speed_move); // _cha.getProperMultiplier()
		this.writeF(this.speed_atack); // _cha.getAttackSpeedMultiplier()
		this.writeF(this.col_radius);
		this.writeF(this.col_height);
		this.writeD(this.hair_style);
		this.writeD(this.hair_color);
		this.writeD(this.face);
		this.writeS(this._title);
		this.writeD(this.clan_id);
		this.writeD(this.clan_crest_id);
		this.writeD(this.ally_id);
		this.writeD(this.ally_crest_id);

		this.writeC(this._sit);
		this.writeC(this._run);
		this.writeC(this._combat);
		this.writeC(this._dead);
		this.writeC(0x00); // is invisible
		this.writeC(this.mount_type); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
		this.writeC(this.private_store);
		this.writeH(this.cubics.length);
		for (EffectCubic cubic : this.cubics)
		{
			this.writeH(cubic == null ? 0 : cubic.getId());
		}
		this.writeC(this._isPartyRoomLeader ? 0x01 : 0x00); // find party members
		this.writeD(this._abnormalEffect);
		this.writeC(this._isFlying ? 0x02 : 0x00);
		this.writeH(this.rec_have);
		this.writeD(this.mount_id);
		this.writeD(this.class_id);
		this.writeD(0x00);
		this.writeC(this._enchant);

		this.writeC(this._team.ordinal()); // team circle around feet 1 = Blue, 2 = red

		this.writeD(this.large_clan_crest_id);
		this.writeC(this._noble);
		this.writeC(this._hero);

		this.writeC(this._fishing);
		this.writeD(this._fishLoc.x);
		this.writeD(this._fishLoc.y);
		this.writeD(this._fishLoc.z);

		this.writeD(this._nameColor);
		this.writeD(this._loc.h);
		this.writeD(this.plg_class);
		this.writeD(this.pledge_type);
		this.writeD(this._title_color);
		this.writeD(this.cw_level);
		this.writeD(this.clan_rep_score);
		this.writeD(this._transform);
		this.writeD(this._agathion);

		this.writeD(0x01); // T2

		this.writeD(this._abnormalEffect2);
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