package l2mv.gameserver.model;

import static l2mv.gameserver.network.serverpackets.ExSetCompassZoneCode.ZONE_ALTERED_FLAG;
import static l2mv.gameserver.network.serverpackets.ExSetCompassZoneCode.ZONE_PEACE_FLAG;
import static l2mv.gameserver.network.serverpackets.ExSetCompassZoneCode.ZONE_PVP_FLAG;
import static l2mv.gameserver.network.serverpackets.ExSetCompassZoneCode.ZONE_SIEGE_FLAG;
import static l2mv.gameserver.network.serverpackets.ExSetCompassZoneCode.ZONE_SSQ_FLAG;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastList;
import javolution.util.FastMap;
import l2mv.commons.annotations.Nullable;
import l2mv.commons.dao.JdbcEntityState;
import l2mv.commons.dbutils.DbUtils;
import l2mv.commons.lang.reference.HardReference;
import l2mv.commons.lang.reference.HardReferences;
import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Announcements;
import l2mv.gameserver.Config;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.GameTimeController;
import l2mv.gameserver.PartyMatchingBBSManager;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.ai.PhantomPlayerAI;
import l2mv.gameserver.ai.PlayableAI.nextAction;
import l2mv.gameserver.ai.PlayerAI;
import l2mv.gameserver.cache.Msg;
import l2mv.gameserver.dao.AccountReportDAO;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.dao.CharacterGroupReuseDAO;
import l2mv.gameserver.dao.CharacterPostFriendDAO;
import l2mv.gameserver.dao.EffectsDAO;
import l2mv.gameserver.data.htm.bypasshandler.BypassType;
import l2mv.gameserver.data.xml.holder.CharTemplateHolder;
import l2mv.gameserver.data.xml.holder.EventHolder;
import l2mv.gameserver.data.xml.holder.HennaHolder;
import l2mv.gameserver.data.xml.holder.InstantZoneHolder;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.data.xml.holder.MultiSellHolder.MultiSellListContainer;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.data.xml.holder.RecipeHolder;
import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.data.xml.holder.SkillAcquireHolder;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.database.mysql;
import l2mv.gameserver.fandc.academy.AcademyList;
import l2mv.gameserver.fandc.datatables.OfflineBuffersTable;
import l2mv.gameserver.fandc.facebook.FacebookProfile;
import l2mv.gameserver.fandc.facebook.FacebookProfilesHolder;
import l2mv.gameserver.fandc.managers.GmEventManager;
import l2mv.gameserver.fandc.managers.OfflineBufferManager;
//import fandc.pc.PcStats;
//import fandc.templates.Ranking;
import l2mv.gameserver.fandc.security.AntiFeedManager;
import l2mv.gameserver.fandc.tournament.model.AbstractTournament;
import l2mv.gameserver.fandc.tournament.model.enums.TournamentPhase;
import l2mv.gameserver.handler.bbs.CommunityBoardManager;
import l2mv.gameserver.handler.bbs.ICommunityBoardHandler;
import l2mv.gameserver.handler.bypass.BypassHandler;
import l2mv.gameserver.handler.items.IItemHandler;
import l2mv.gameserver.hwid.HwidGamer;
import l2mv.gameserver.idfactory.IdFactory;
import l2mv.gameserver.instancemanager.AutoHuntingManager;
import l2mv.gameserver.instancemanager.BypassManager;
import l2mv.gameserver.instancemanager.BypassManager.DecodedBypass;
import l2mv.gameserver.instancemanager.BypassManager.EncodingType;
import l2mv.gameserver.instancemanager.CursedWeaponsManager;
import l2mv.gameserver.instancemanager.DimensionalRiftManager;
import l2mv.gameserver.instancemanager.MatchingRoomManager;
import l2mv.gameserver.instancemanager.QuestManager;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.instancemanager.achievements_engine.AchievementsManager;
import l2mv.gameserver.instancemanager.games.HandysBlockCheckerManager;
import l2mv.gameserver.instancemanager.games.HandysBlockCheckerManager.ArenaParticipantsHolder;
import l2mv.gameserver.listener.actor.OnAttackListener;
import l2mv.gameserver.listener.actor.OnMagicUseListener;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2mv.gameserver.listener.actor.player.impl.ScriptAnswerListener;
import l2mv.gameserver.listener.actor.player.impl.SummonAnswerListener;
import l2mv.gameserver.masteriopack.rankpvpsystem.RPSCookie;
import l2mv.gameserver.model.GameObjectTasks.EndSitDownTask;
import l2mv.gameserver.model.GameObjectTasks.EndStandUpTask;
import l2mv.gameserver.model.GameObjectTasks.HourlyTask;
import l2mv.gameserver.model.GameObjectTasks.KickTask;
import l2mv.gameserver.model.GameObjectTasks.PvPFlagTask;
import l2mv.gameserver.model.GameObjectTasks.RecomBonusTask;
import l2mv.gameserver.model.GameObjectTasks.UnJailTask;
import l2mv.gameserver.model.GameObjectTasks.WaterTask;
import l2mv.gameserver.model.Request.L2RequestType;
import l2mv.gameserver.model.Skill.AddedSkill;
import l2mv.gameserver.model.Zone.ZoneType;
import l2mv.gameserver.model.actor.instances.player.Bonus;
import l2mv.gameserver.model.actor.instances.player.BookMarkList;
import l2mv.gameserver.model.actor.instances.player.FriendList;
import l2mv.gameserver.model.actor.instances.player.Macro;
import l2mv.gameserver.model.actor.instances.player.MacroList;
import l2mv.gameserver.model.actor.instances.player.NevitSystem;
import l2mv.gameserver.model.actor.instances.player.RecomBonus;
import l2mv.gameserver.model.actor.instances.player.ShortCut;
import l2mv.gameserver.model.actor.instances.player.ShortCutList;
import l2mv.gameserver.model.actor.listener.PlayerListenerList;
import l2mv.gameserver.model.actor.permission.PlayerPermissionList;
import l2mv.gameserver.model.actor.recorder.PlayerStatsChangeRecorder;
import l2mv.gameserver.model.base.AcquireType;
import l2mv.gameserver.model.base.ClassId;
import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.base.Experience;
import l2mv.gameserver.model.base.InvisibleType;
import l2mv.gameserver.model.base.PlayerAccess;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.base.RestartType;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.entity.DimensionalRift;
import l2mv.gameserver.model.entity.Hero;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.entity.CCPHelpers.itemLogs.ItemActionType;
import l2mv.gameserver.model.entity.CCPHelpers.itemLogs.ItemLogHandler;
import l2mv.gameserver.model.entity.SevenSignsFestival.DarknessFestival;
import l2mv.gameserver.model.entity.achievements.Achievement;
import l2mv.gameserver.model.entity.achievements.Achievements;
import l2mv.gameserver.model.entity.achievements.PlayerCounters;
import l2mv.gameserver.model.entity.auction.Auction;
import l2mv.gameserver.model.entity.auction.AuctionManager;
import l2mv.gameserver.model.entity.boat.Boat;
import l2mv.gameserver.model.entity.boat.ClanAirShip;
import l2mv.gameserver.model.entity.events.GameEvent;
import l2mv.gameserver.model.entity.events.GlobalEvent;
import l2mv.gameserver.model.entity.events.fightclubmanager.FightClubGameRoom;
import l2mv.gameserver.model.entity.events.impl.AbstractFightClub;
import l2mv.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2mv.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import l2mv.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2mv.gameserver.model.entity.events.impl.DuelEvent;
import l2mv.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
import l2mv.gameserver.model.entity.forum.ForumMember;
import l2mv.gameserver.model.entity.forum.ForumMembersHolder;
import l2mv.gameserver.model.entity.olympiad.CompType;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.model.entity.olympiad.OlympiadGame;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.entity.residence.ClanHall;
import l2mv.gameserver.model.entity.residence.Fortress;
import l2mv.gameserver.model.entity.residence.Residence;
import l2mv.gameserver.model.instances.DecoyInstance;
import l2mv.gameserver.model.instances.FestivalMonsterInstance;
import l2mv.gameserver.model.instances.GuardInstance;
import l2mv.gameserver.model.instances.MonsterInstance;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.instances.PetBabyInstance;
import l2mv.gameserver.model.instances.PetInstance;
import l2mv.gameserver.model.instances.ReflectionBossInstance;
import l2mv.gameserver.model.instances.SchemeBufferInstance;
import l2mv.gameserver.model.instances.StaticObjectInstance;
import l2mv.gameserver.model.instances.TamedBeastInstance;
import l2mv.gameserver.model.instances.TrapInstance;
import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.model.items.ItemContainer;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.LockType;
import l2mv.gameserver.model.items.ManufactureItem;
import l2mv.gameserver.model.items.PcFreight;
import l2mv.gameserver.model.items.PcInventory;
import l2mv.gameserver.model.items.PcRefund;
import l2mv.gameserver.model.items.PcWarehouse;
import l2mv.gameserver.model.items.TradeItem;
import l2mv.gameserver.model.items.Warehouse;
import l2mv.gameserver.model.items.Warehouse.WarehouseType;
import l2mv.gameserver.model.items.attachment.FlagItemAttachment;
import l2mv.gameserver.model.items.attachment.PickableAttachment;
import l2mv.gameserver.model.matching.MatchingRoom;
import l2mv.gameserver.model.petition.PetitionMainGroup;
import l2mv.gameserver.model.pledge.Alliance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.ClanWar;
import l2mv.gameserver.model.pledge.Privilege;
import l2mv.gameserver.model.pledge.RankPrivs;
import l2mv.gameserver.model.pledge.SubUnit;
import l2mv.gameserver.model.pledge.UnitMember;
import l2mv.gameserver.model.premium.PremiumEnd;
import l2mv.gameserver.model.premium.PremiumStart;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestEventType;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.loginservercon.gspackets.ChangeAccessLevel;
import l2mv.gameserver.network.serverpackets.AbnormalStatusUpdate;
import l2mv.gameserver.network.serverpackets.ActionFail;
import l2mv.gameserver.network.serverpackets.AutoAttackStart;
import l2mv.gameserver.network.serverpackets.CameraMode;
import l2mv.gameserver.network.serverpackets.ChairSit;
import l2mv.gameserver.network.serverpackets.ChangeWaitType;
import l2mv.gameserver.network.serverpackets.CharInfo;
import l2mv.gameserver.network.serverpackets.ConfirmDlg;
import l2mv.gameserver.network.serverpackets.CreatureSay;
import l2mv.gameserver.network.serverpackets.EtcStatusUpdate;
import l2mv.gameserver.network.serverpackets.ExAutoSoulShot;
import l2mv.gameserver.network.serverpackets.ExBR_AgathionEnergyInfo;
import l2mv.gameserver.network.serverpackets.ExBR_ExtraUserInfo;
import l2mv.gameserver.network.serverpackets.ExBasicActionList;
import l2mv.gameserver.network.serverpackets.ExDominionWarStart;
import l2mv.gameserver.network.serverpackets.ExDuelUpdateUserInfo;
import l2mv.gameserver.network.serverpackets.ExOlympiadMatchEnd;
import l2mv.gameserver.network.serverpackets.ExOlympiadMode;
import l2mv.gameserver.network.serverpackets.ExOlympiadSpelledInfo;
import l2mv.gameserver.network.serverpackets.ExPCCafePointInfo;
import l2mv.gameserver.network.serverpackets.ExQuestItemList;
import l2mv.gameserver.network.serverpackets.ExSetCompassZoneCode;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2mv.gameserver.network.serverpackets.ExStartScenePlayer;
import l2mv.gameserver.network.serverpackets.ExStorageMaxCount;
import l2mv.gameserver.network.serverpackets.ExUseSharedGroupItem;
import l2mv.gameserver.network.serverpackets.ExVitalityPointInfo;
import l2mv.gameserver.network.serverpackets.ExVoteSystemInfo;
import l2mv.gameserver.network.serverpackets.GetItem;
import l2mv.gameserver.network.serverpackets.HennaInfo;
import l2mv.gameserver.network.serverpackets.InventoryUpdate;
import l2mv.gameserver.network.serverpackets.ItemList;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
import l2mv.gameserver.network.serverpackets.LeaveWorld;
import l2mv.gameserver.network.serverpackets.MagicSkillLaunched;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.MyTargetSelected;
import l2mv.gameserver.network.serverpackets.NpcInfoPoly;
import l2mv.gameserver.network.serverpackets.ObserverEnd;
import l2mv.gameserver.network.serverpackets.ObserverStart;
import l2mv.gameserver.network.serverpackets.PartySmallWindowUpdate;
import l2mv.gameserver.network.serverpackets.PartySpelled;
import l2mv.gameserver.network.serverpackets.PlaySound;
import l2mv.gameserver.network.serverpackets.PledgeShowMemberListDelete;
import l2mv.gameserver.network.serverpackets.PledgeShowMemberListDeleteAll;
import l2mv.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import l2mv.gameserver.network.serverpackets.PrivateStoreListBuy;
import l2mv.gameserver.network.serverpackets.PrivateStoreListSell;
import l2mv.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import l2mv.gameserver.network.serverpackets.PrivateStoreMsgSell;
import l2mv.gameserver.network.serverpackets.QuestList;
import l2mv.gameserver.network.serverpackets.RadarControl;
import l2mv.gameserver.network.serverpackets.RecipeShopMsg;
import l2mv.gameserver.network.serverpackets.RecipeShopSellList;
import l2mv.gameserver.network.serverpackets.RelationChanged;
import l2mv.gameserver.network.serverpackets.Revive;
import l2mv.gameserver.network.serverpackets.Ride;
import l2mv.gameserver.network.serverpackets.SendTradeDone;
import l2mv.gameserver.network.serverpackets.ServerClose;
import l2mv.gameserver.network.serverpackets.SetupGauge;
import l2mv.gameserver.network.serverpackets.ShortBuffStatusUpdate;
import l2mv.gameserver.network.serverpackets.ShortCutInit;
import l2mv.gameserver.network.serverpackets.ShortCutRegister;
import l2mv.gameserver.network.serverpackets.SkillCoolTime;
import l2mv.gameserver.network.serverpackets.SkillList;
import l2mv.gameserver.network.serverpackets.Snoop;
import l2mv.gameserver.network.serverpackets.SocialAction;
import l2mv.gameserver.network.serverpackets.SpawnEmitter;
import l2mv.gameserver.network.serverpackets.SpecialCamera;
import l2mv.gameserver.network.serverpackets.StatusUpdate;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.TargetSelected;
import l2mv.gameserver.network.serverpackets.TargetUnselected;
import l2mv.gameserver.network.serverpackets.TeleportToLocation;
import l2mv.gameserver.network.serverpackets.UserInfo;
import l2mv.gameserver.network.serverpackets.ValidateLocation;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.network.serverpackets.components.SceneMovie;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Events;
import l2mv.gameserver.skills.AbnormalEffect;
import l2mv.gameserver.skills.EffectType;
import l2mv.gameserver.skills.TimeStamp;
import l2mv.gameserver.skills.effects.EffectCubic;
import l2mv.gameserver.skills.effects.EffectTemplate;
import l2mv.gameserver.skills.skillclasses.Charge;
import l2mv.gameserver.skills.skillclasses.Transformation;
import l2mv.gameserver.stats.Formulas;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.stats.funcs.FuncTemplate;
import l2mv.gameserver.tables.ClanTable;
import l2mv.gameserver.tables.PetDataTable;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.tables.SkillTreeTable;
import l2mv.gameserver.taskmanager.AutoSaveManager;
import l2mv.gameserver.taskmanager.CancelTaskManager;
import l2mv.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2mv.gameserver.templates.FishTemplate;
import l2mv.gameserver.templates.Henna;
import l2mv.gameserver.templates.InstantZone;
import l2mv.gameserver.templates.PlayerTemplate;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.templates.item.ArmorTemplate;
import l2mv.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2mv.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.item.WeaponTemplate;
import l2mv.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.AntiFlood;
import l2mv.gameserver.utils.AutoHuntingPunish;
import l2mv.gameserver.utils.EffectsComparator;
import l2mv.gameserver.utils.FixEnchantOlympiad;
import l2mv.gameserver.utils.GArray;
import l2mv.gameserver.utils.GameStats;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Language;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.Log;
import l2mv.gameserver.utils.PositionUtils;
import l2mv.gameserver.utils.SiegeUtils;
import l2mv.gameserver.utils.SqlBatch;
import l2mv.gameserver.utils.Strings;
import l2mv.gameserver.utils.TeleportUtils;
import l2mv.gameserver.utils.Util;

@SuppressWarnings("serial")
public final class Player extends Playable implements PlayerGroup
{
	public static final int DEFAULT_TITLE_COLOR = 0xFFFF77;
	public static final int MAX_POST_FRIEND_SIZE = 100;
	public static final int MAX_FRIEND_SIZE = 128;

	private static final Logger _log = LoggerFactory.getLogger(Player.class);

	public static final String NO_TRADERS_VAR = "notraders";
	public static final String NO_ANIMATION_OF_CAST_VAR = "notShowBuffAnim";
	public static final String NO_EMOTIONS_VAR = "notShowEmotions";
	public static final String NO_OLYMPIAD_ANNOUNCEMENTS_VAR = "notShowOlyAnnounces";
	public static final String MY_BIRTHDAY_RECEIVE_YEAR = "MyBirthdayReceiveYear";
	public static final String NOT_CONNECTED = "<not connected>";

	public Map<Integer, SubClass> _classlist = new HashMap<Integer, SubClass>(4);

	public final static int OBSERVER_NONE = 0;
	public final static int OBSERVER_STARTING = 1;
	public final static int OBSERVER_STARTED = 3;
	public final static int OBSERVER_LEAVING = 2;

	public static final int STORE_PRIVATE_NONE = 0;
	public static final int STORE_PRIVATE_SELL = 1;
	public static final int STORE_PRIVATE_BUY = 3;
	public static final int STORE_PRIVATE_MANUFACTURE = 5;
	public static final int STORE_OBSERVING_GAMES = 7;
	public static final int STORE_PRIVATE_SELL_PACKAGE = 8;
	public static final int STORE_PRIVATE_BUFF = 20;

	public static final int RANK_VAGABOND = 0;
	public static final int RANK_VASSAL = 1;
	public static final int RANK_HEIR = 2;
	public static final int RANK_KNIGHT = 3;
	public static final int RANK_WISEMAN = 4;
	public static final int RANK_BARON = 5;
	public static final int RANK_VISCOUNT = 6;
	public static final int RANK_COUNT = 7;
	public static final int RANK_MARQUIS = 8;
	public static final int RANK_DUKE = 9;
	public static final int RANK_GRAND_DUKE = 10;
	public static final int RANK_DISTINGUISHED_KING = 11;
	public static final int RANK_EMPEROR = 12; // unused

	public static final int LANG_ENG = 0;
	public static final int LANG_RUS = 1;
	public static final int LANG_UNK = -1;

	public static final int[] EXPERTISE_LEVELS =
	{
		0,
		20,
		40,
		52,
		61,
		76,
		80,
		84,
		Integer.MAX_VALUE
	};

	private GameClient _connection;
	private String _login;

	private HwidGamer _gamer;
	private String _hwidLock;

	// Rank PvP System by Masterio
	private RPSCookie _RPSCookie = new RPSCookie();

	public RPSCookie getRPSCookie()
	{
		return _RPSCookie;
	}

	private int _karma, _pkKills, _pvpKills;
	private int _face, _hairStyle, _hairColor;
	private int _recomHave, _recomLeftToday, _fame;
	private int _recomLeft = 20;
	private int _recomBonusTime = 3600;
	private boolean _isHourglassEffected, _isRecomTimerActive;
	private boolean _isUndying = false;
	private int _deleteTimer;

	private NpcInstance lastAugmentNpc = null;

	private int _ping = -1;

	private long _createTime, _onlineTime, _onlineBeginTime, _leaveClanTime, _deleteClanTime, _NoChannel, _NoChannelBegin;

	private long _uptime;
	/**
	 * Time on login in game
	 */
	private long _lastAccess;

	/**
	 * The Color of players name / title (white is 0xFFFFFF)
	 */
	private int _nameColor, _titlecolor;

	private int _vitalityLevel = -1;
	private double _vitality = Config.VITALITY_LEVELS[4];
	private boolean _overloaded;

	public boolean sittingTaskLaunched;

	/**
	 * Time counter when L2Player is sitting
	 */
	private int _waitTimeWhenSit;

	private boolean _autoLoot = Config.AUTO_LOOT, AutoLootHerbs = Config.AUTO_LOOT_HERBS, AutoLootOnlyAdena = Config.AUTO_LOOT_ONLY_ADENA;

	private final PcInventory _inventory = new PcInventory(this);
	private final Warehouse _warehouse = new PcWarehouse(this);
	private Warehouse _withdrawWarehouse = null; // Used for GMs withdrawing from CWH or other player warehouses
	private final ItemContainer _refund = new PcRefund(this);
	private final PcFreight _freight = new PcFreight(this);

	public final BookMarkList bookmarks = new BookMarkList(this, 0);

	public final AntiFlood antiFlood = new AntiFlood();

	/**
	 * The table containing all l2fecipeList of the L2Player
	 */
	private final Map<Integer, Recipe> _recipebook = new TreeMap<Integer, Recipe>();
	private final Map<Integer, Recipe> _commonrecipebook = new TreeMap<Integer, Recipe>();

	private final Map<String, Object> quickVars = new ConcurrentHashMap<>();
	private final List<Integer> loadedImages = new ArrayList<>();

	/**
	 * Premium Items
	 */
	private final Map<Integer, PremiumItem> _premiumItems = new TreeMap<Integer, PremiumItem>();

	/**
	 * The table containing all Quests began by the L2Player
	 */
	private final Map<String, QuestState> _quests = new HashMap<String, QuestState>();

	/**
	 * The list containing all shortCuts of this L2Player
	 */
	private final ShortCutList _shortCuts = new ShortCutList(this);

	/**
	 * The list containing all macroses of this L2Player
	 */
	private final MacroList _macroses = new MacroList(this);

	/**
	 * The Private Store type of the L2Player (STORE_PRIVATE_NONE=0, STORE_PRIVATE_SELL=1, sellmanage=2, STORE_PRIVATE_BUY=3, buymanage=4, STORE_PRIVATE_MANUFACTURE=5)
	 */
	private int _privatestore;
	private String _manufactureName;
	private List<ManufactureItem> _createList = Collections.emptyList();
	private String _sellStoreName;
	private List<TradeItem> _sellList = Collections.emptyList();
	private List<TradeItem> _packageSellList = Collections.emptyList();
	private String _buyStoreName;
	private List<TradeItem> _buyList = Collections.emptyList();
	private List<TradeItem> _tradeList = Collections.emptyList();

	/**
	 * hennas
	 */
	private final Henna[] _henna = new Henna[3];
	private int _hennaSTR, _hennaINT, _hennaDEX, _hennaMEN, _hennaWIT, _hennaCON;

	private Party _party;
	private Location _lastPartyPosition;

	private Clan _clan;
	private int _pledgeClass = 0, _pledgeType = Clan.SUBUNIT_NONE, _powerGrade = 0, _lvlJoinedAcademy = 0, _apprentice = 0;

	/**
	 * GM Stuff
	 */
	private int _accessLevel;
	private PlayerAccess _playerAccess = new PlayerAccess();

	private boolean _messageRefusal = false, _tradeRefusal = false, _blockAll = false;
	private boolean _isPendingOlyEnd = false;

	private Skill _macroSkill = null;

	private boolean _isFakePlayer = false;
	/**
	 * The L2Summon of the L2Player
	 */
	private Summon _summon = null;
	private boolean _riding;

	private DecoyInstance _decoy = null;

	private Map<Integer, EffectCubic> _cubics = null;
	private int _agathionId = 0;

	private Request _request;

	private ItemInstance _arrowItem;

	/**
	 * The fists L2Weapon of the L2Player (used when no weapon is equipped)
	 */
	private WeaponTemplate _fistsWeaponItem;

	private Map<Integer, String> _chars = new HashMap<Integer, String>(8);

	/**
	 * The current higher Expertise of the L2Player (None=0, D=1, C=2, B=3, A=4, S=5, S80=6, S84=7)
	 */
	public int expertiseIndex = 0;

	private ItemInstance _enchantScroll = null;

	private WarehouseType _usingWHType;

	private boolean _isOnline = false;

	private final AtomicBoolean _isLogout = new AtomicBoolean();

	/**
	 * The L2NpcInstance corresponding to the last Folk which one the player talked.
	 */
	private HardReference<NpcInstance> _lastNpc = HardReferences.emptyRef();
	private MultiSellListContainer _multisell = null;

	private final Set<Integer> _activeSoulShots = new CopyOnWriteArraySet<Integer>();

	private WorldRegion _observerRegion;
	private final AtomicInteger _observerMode = new AtomicInteger(0);

	public int _telemode = 0;

	private int _handysBlockCheckerEventArena = -1;

	public boolean entering = true;

	public Location _stablePoint = null;

	/**
	 * new loto ticket *
	 */
	public int _loto[] = new int[5];
	/**
	 * new race ticket *
	 */
	public int _race[] = new int[2];

	private final Map<Integer, String> _blockList = new ConcurrentSkipListMap<Integer, String>(); // characters blocked with '/block <charname>' cmd
	private final FriendList _friendList = new FriendList(this);

	private boolean _hero = false;
	private boolean _heroAura = false;

	/**
	 * True if the L2Player is in a boat
	 */
	private Boat _boat;
	private Location _inBoatPosition;

	protected int _baseClass = -1;
	protected SubClass _activeClass = null;

	private final Bonus _bonus = new Bonus();

	private Future<?> _bonusExpiration;

	private boolean _isSitting;
	private StaticObjectInstance _sittingObject;

	private boolean _noble = false;

	private boolean _inOlympiadMode;
	private OlympiadGame _olympiadGame;
	private OlympiadGame _olympiadObserveGame;

	private int _olympiadSide = -1;

	/**
	 * ally with ketra or varka related wars
	 */
	private int _varka = 0;
	private int _ketra = 0;
	private int _ram = 0;

	private byte[] _keyBindings = ArrayUtils.EMPTY_BYTE_ARRAY;

	private int _cursedWeaponEquippedId = 0;

	private final Fishing _fishing = new Fishing(this);
	private boolean _isFishing;

	private Future<?> _taskWater;
	private Future<?> _autoSaveTask;
	private Future<?> _kickTask;

	private Future<?> _vitalityTask;
	private Future<?> _pcCafePointsTask;
	private Future<?> _unjailTask;

	private final Lock _storeLock = new ReentrantLock();

	private int _zoneMask;

	private boolean _offline = false;
	private boolean _awaying = false;

	private int _transformationId;
	private int _transformationTemplate;
	private String _transformationName;

	private int _pcBangPoints;

	Map<Integer, Skill> _transformationSkills = new HashMap<Integer, Skill>();

	private int _expandInventory = 0;
	private int _expandWarehouse = 0;
	private int _battlefieldChatId;
	private int _lectureMark;
	private InvisibleType _invisibleType = InvisibleType.NONE;

	private final Map<BypassType, List<String>> bypasses;
	private IntObjectMap<String> _postFriends = Containers.emptyIntObjectMap();

	private final List<String> _blockedActions = new ArrayList<String>();

	private boolean _notShowBuffAnim = false;
	private boolean _notShowTraders = false;
	private boolean _debug = false;

	private final List<SchemeBufferInstance.PlayerScheme> buffSchemes;

	private long _dropDisabled;
	private long _lastItemAuctionInfoRequest;

	private final IntObjectMap<TimeStamp> _sharedGroupReuses = new CHashIntObjectMap<TimeStamp>();
	private Pair<Integer, OnAnswerListener> _askDialog = null;

	// High Five: Navit's Bonus System
	private final NevitSystem _nevitSystem = new NevitSystem(this);

	private MatchingRoom _matchingRoom;
	private boolean _matchingRoomWindowOpened = false;
	private PetitionMainGroup _petitionGroup;
	private final Map<Integer, Long> _instancesReuses = new ConcurrentHashMap<Integer, Long>();

	public List<TeleportPoints> _teleportPoints = new ArrayList<TeleportPoints>();

	public GameEvent _event = null;
	private FightClubGameRoom _fightClubGameRoom = null;

	// Prims
	private long _resurrectionMaxTime = 0;
	private long _resurrectionBuffBlockedTime = 0;

	@Override
	public boolean isFakePlayer()
	{
		return _isFakePlayer;
	}

	public void setFakePlayer()
	{
		_isFakePlayer = true;
	}

	public Player(int objectId, PlayerTemplate template, String accountName)
	{
		super(objectId, template);

		_login = accountName;
		_nameColor = 0xFFFFFF;
		_titlecolor = 0xFFFF77;
		_baseClass = getClassId().getId();
		buffSchemes = new CopyOnWriteArrayList<>();
		bypasses = new EnumMap<BypassType, List<String>>(BypassType.class);
		for (BypassType bypassType : BypassType.values())
		{
			bypasses.put(bypassType, new ArrayList<String>());
		}
	}

	/**
	 * Constructor<?> of L2Player (use L2Character constructor).<BR>
	 * <BR>
	 * <p/>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Call the L2Character constructor to create an empty _skills slot and copy basic Calculator set to this L2Player</li>
	 * <li>Create a l2fadar object</li>
	 * <li>Retrieve from the database all items of this L2Player and add them to _inventory</li>
	 * <p/>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SET the account name of the L2Player</B></FONT><BR>
	 * <BR>
	 * @param objectId Identifier of the object to initialized
	 * @param template The L2PlayerTemplate to apply to the L2Player
	 */
	private Player(int objectId, PlayerTemplate template)
	{
		this(objectId, template, null);

		_ai = new PlayerAI(this);

		if (!Config.EVERYBODY_HAS_ADMIN_RIGHTS)
		{
			setPlayerAccess(Config.gmlist.get(objectId));
		}
		else
		{
			setPlayerAccess(Config.gmlist.get(0));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public HardReference<Player> getRef()
	{
		return (HardReference<Player>) super.getRef();
	}

	public String getAccountName()
	{
		if (_connection == null)
		{
			return _login;
		}
		return _connection.getLogin();
	}

	public String getIP()
	{
		if (_connection == null)
		{
			return NOT_CONNECTED;
		}
		return _connection.getIpAddr();
	}

	public String getHWID()
	{
		if (_connection == null)
		{
			return NOT_CONNECTED;
		}
		return _connection.getHWID();
	}

	public boolean hasHWID()
	{
		if (getPlayer() == null || _connection == null || getHWID() == null || getHWID().equalsIgnoreCase(NOT_CONNECTED) || getHWID().equalsIgnoreCase("NO-SMART-GUARD-ENABLED"))
		{
			return false;
		}
		return true;
	}

	public HwidGamer getHwidGamer()
	{
		return _gamer;
	}

	public void setHwidGamer(HwidGamer gamer)
	{
		_gamer = gamer;
	}

	public Map<Integer, String> getAccountChars()
	{
		return _chars;
	}

	@Override
	public final PlayerTemplate getTemplate()
	{
		return (PlayerTemplate) _template;
	}

	@Override
	public PlayerTemplate getBaseTemplate()
	{
		return (PlayerTemplate) _baseTemplate;
	}

	public void changeSex()
	{
		_template = CharTemplateHolder.getInstance().getTemplate(getClassId(), getSex() == 0);
	}

	@Override
	public PlayerAI getAI()
	{
		return (PlayerAI) _ai;
	}

	@Override
	public boolean checkDoCastConditions(Skill skill, Creature target, boolean sendMessage)
	{
		if (skill == null || (_event != null && !_event.canUseSkill(this, target, skill)))
		{
			if (sendMessage)
			{
				sendActionFailed();
			}
			return false;
		}

		return super.checkDoCastConditions(skill, target, sendMessage);
	}

	@Override
	public void doAttack(Creature target)
	{
		if ((_event != null) && !_event.canAttack(this, target))
		{
			sendActionFailed();
			return;
		}
		super.doAttack(target);
	}

	@Override
	public void sendReuseMessage(Skill skill)
	{
		if (isCastingNow())
		{
			return;
		}
		TimeStamp sts = getSkillReuse(skill);
		if ((sts == null) || !sts.hasNotPassed())
		{
			return;
		}
		long timeleft = sts.getReuseCurrent();
		if ((!Config.ALT_SHOW_REUSE_MSG && (timeleft < 10000)) || (timeleft < 500))
		{
			return;
		}
		long hours = timeleft / 3600000;
		long minutes = (timeleft - (hours * 3600000)) / 60000;
		long seconds = (long) Math.ceil((timeleft - (hours * 3600000) - (minutes * 60000)) / 1000.);
		if (hours > 0)
		{
			sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(hours).addNumber(minutes).addNumber(seconds));
		}
		else if (minutes > 0)
		{
			sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(minutes).addNumber(seconds));
		}
		else
		{
			sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(seconds));
		}
	}

	@Override
	public final int getLevel()
	{
		return _activeClass == null ? 1 : _activeClass.getLevel();
	}

	/**
	 *
	 * @return 0-No, 1-D, 2-C, 3-B, 4-A, 5-S, 6-S80, 7-S84
	 */
	public int getGrade()
	{
		switch (getSkillLevel(239))
		{
		case -1:
			return 0; // No-Grade
		case 1:
			return 1; // D-Grade
		case 2:
			return 2; // C-Grade
		case 3:
			return 3; // B-Grade
		case 4:
			return 4; // A-Grade
		case 5:
			return 5; // S-Grade
		case 6:
			return 6; // S80-Grade
		case 7:
			return 7; // S84-Grade
		default:
			return 0; // No-Grade
		}
	}

	public int getSex()
	{
		return getTemplate().isMale ? 0 : 1;
	}

	public int getFace()
	{
		return _face;
	}

	public void setFace(int face)
	{
		_face = face;
	}

	public int getHairColor()
	{
		return _hairColor;
	}

	public void setHairColor(int hairColor)
	{
		_hairColor = hairColor;
	}

	public int getHairStyle()
	{
		return _hairStyle;
	}

	public void setHairStyle(int hairStyle)
	{
		_hairStyle = hairStyle;
	}

	public void offline()
	{
		if (getHwidGamer() != null)
		{
			getHwidGamer().removePlayer(this);
		}
		if (_connection != null)
		{
			_connection.setActiveChar(null);
			_connection.close(ServerClose.STATIC);
			setNetConnection(null);
		}

		setNameColor(Config.SERVICES_OFFLINE_TRADE_NAME_COLOR);
		setOnlineTime(getOnlineTime());
		setUptime(0L);
		setOfflineMode(true);

		setVar("offline", String.valueOf(System.currentTimeMillis() / 1000L), -1);

		// Synerge - Academy
		if (isInSearchOfAcademy())
		{
			setSearchforAcademy(false);
			AcademyList.deleteFromAcdemyList(this);
		}

		Party party = getParty();
		if (party != null)
		{
			if (isFestivalParticipant())
			{
				party.sendMessage(getName() + " has been removed from the upcoming festival.");
			}
			leaveParty();
		}

		if (getPet() != null)
		{
			getPet().unSummon();
		}

		CursedWeaponsManager.getInstance().doLogout(this);

		if (isInOlympiadMode() || getOlympiadGame() != null)
		{
			Olympiad.logoutPlayer(this);
		}
		if (Olympiad.isRegistered(this))
		{
			Olympiad.unRegisterNoble(this);
		}

		if (isInObserverMode())
		{
			if (getOlympiadObserveGame() == null)
			{
				leaveObserverMode();
			}
			else
			{
				leaveOlympiadObserverMode(true);
			}
			_observerMode.set(OBSERVER_NONE);
		}

		broadcastCharInfo();
		stopWaterTask();
		stopBonusTask();
		stopHourlyTask();
		stopVitalityTask();
		stopPcBangPointsTask();
		stopAutoSaveTask();
		stopRecomBonusTask(true);
		stopQuestTimers();
		getNevitSystem().stopTasksOnLogout();

		try
		{
			getInventory().store();
		}
		catch (Throwable t)
		{
			_log.error("Error while storing Player Inventory", t);
		}

		try
		{
			store(false);
		}
		catch (Throwable t)
		{
			_log.error("Error while storing Player", t);
		}
	}

	public void kick()
	{
		if (_connection != null)
		{
			Log.logLeftGame(this, "Kick");
			_connection.close(LeaveWorld.STATIC);
			setNetConnection(null);
		}
		stopAbnormalEffect(AbnormalEffect.FIREROOT_STUN);
		prepareToLogout();
		deleteMe();
	}

	public void restart()
	{
		if (_connection != null)
		{
			Log.logLeftGame(this, "Restart");
			_connection.setActiveChar(null);
			setNetConnection(null);
		}

		prepareToLogout();
		deleteMe();
	}

	/**
	 * The connection is closed, the client does not close, the character is saved and removed from the game Writing an inscription NO CARRIER
	 */
	public void logout()
	{
		if (_connection != null)
		{
			// Log.logLeftGame(this, "Logout");
			_connection.close(ServerClose.STATIC);
			setNetConnection(null);
		}

		prepareToLogout();
		deleteMe();
	}

	private void prepareToLogout()
	{
		if (_isLogout.getAndSet(true))
		{
			return;
		}

		if (getTournament() != null)
		{
			getTournament().onDisconnect(this);
		}

		if (getHwidGamer() != null)
		{
			getHwidGamer().removePlayer(this);
		}

		setNetConnection(null);
		setIsOnline(false);

		getListeners().onExit();

		if (isFlying() && !checkLandingState())
		{
			_stablePoint = TeleportUtils.getRestartLocation(this, RestartType.TO_VILLAGE);
		}

		if (isCastingNow())
		{
			abortCast(true, true);
		}

		// Synerge - Academy
		if (isInSearchOfAcademy())
		{
			setSearchforAcademy(false);
			AcademyList.deleteFromAcdemyList(this);
		}

		if (!isInOfflineMode() && getPrivateStoreType() == STORE_PRIVATE_SELL && isSitting())
		{
			for (TradeItem item : _sellList)
			{
				AuctionManager.getInstance().removeStore(this, item.getAuctionId());
			}
		}

		Party party = getParty();

		if ((party != null))
		{
			if (isFestivalParticipant())
			{
				party.sendMessage(getName() + " has been removed from the upcoming festival.");
			}
			leaveParty();
		}

		if (isInFightClub())
		{
			getFightClubEvent().loggedOut(this);
		}

		CursedWeaponsManager.getInstance().doLogout(this);

		if (_olympiadObserveGame != null)
		{
			_olympiadObserveGame.removeSpectator(this);
		}

		if (isInOlympiadMode() || getOlympiadGame() != null)
		{
			Olympiad.logoutPlayer(this);
		}
		if (Olympiad.isRegistered(this))
		{
			Olympiad.unRegisterNoble(this);
		}

		stopFishing();

		if (isInObserverMode())
		{
			if (getOlympiadObserveGame() == null)
			{
				leaveObserverMode();
			}
			else
			{
				leaveOlympiadObserverMode(true);
			}
			_observerMode.set(OBSERVER_NONE);
		}

		if (_stablePoint != null)
		{
			teleToLocation(_stablePoint);
		}

		Summon pet = getPet();
		if (pet != null)
		{
			pet.saveEffects();
			pet.unSummon();
		}

		_friendList.notifyFriends(false);

		if (isProcessingRequest())
		{
			getRequest().cancel();
		}

		stopAllTimers();

		if (isInBoat())
		{
			getBoat().removePlayer(this);
		}

		SubUnit unit = getSubUnit();
		UnitMember member = unit == null ? null : unit.getUnitMember(getObjectId());
		if (member != null)
		{
			int sponsor = member.getSponsor();
			int apprentice = getApprentice();
			PledgeShowMemberListUpdate memberUpdate = new PledgeShowMemberListUpdate(this);
			for (Player clanMember : _clan.getOnlineMembers(getObjectId()))
			{
				clanMember.sendPacket(memberUpdate);
				if (clanMember.getObjectId() == sponsor)
				{
					clanMember.sendPacket(new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_APPRENTICE_HAS_LOGGED_OUT).addString(_name));
				}
				else if (clanMember.getObjectId() == apprentice)
				{
					clanMember.sendPacket(new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_SPONSOR_HAS_LOGGED_OUT).addString(_name));
				}
			}
			member.setPlayerInstance(this, true);
		}

		FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
		if (attachment != null)
		{
			attachment.onLogout(this);
		}

		if (CursedWeaponsManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()) != null)
		{
			CursedWeaponsManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()).setPlayer(null);
		}

		MatchingRoom room = getMatchingRoom();
		if (room != null)
		{
			if (room.getLeader() == this)
			{
				room.disband();
			}
			else
			{
				room.removeMember(this, false);
			}
		}
		setMatchingRoom(null);

		MatchingRoomManager.getInstance().removeFromWaitingList(this);

		destroyAllTraps();

		if (_decoy != null)
		{
			_decoy.unSummon();
			_decoy = null;
		}

		stopPvPFlag();

		if (_event != null)
		{
			_event.onLogout(this);
		}

		Reflection ref = getReflection();

		if (ref != ReflectionManager.DEFAULT)
		{
			if (ref.getReturnLoc() != null)
			{
				_stablePoint = ref.getReturnLoc();
			}

			ref.removeObject(this);
		}

		// Bot punishment
		if (Config.ENABLE_AUTO_HUNTING_REPORT)
		{
			// Save punish
			if (isBeingPunished())
			{
				try
				{
					AutoHuntingManager.getInstance().savePlayerPunish(this);
				}
				catch (final Exception e)
				{
					_log.warn("deleteMe()", e);
				}
			}
			// Save report points left
			if (_account != null)
			{
				try
				{
					_account.updatePoints(_login);
				}
				catch (final Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		try
		{
			getInventory().store();
			getRefund().clear();
		}
		catch (Throwable t)
		{
			_log.error("Error while storing Inventory and Refund", t);
		}

		try
		{
			store(false);
		}
		catch (Throwable t)
		{
			_log.error("Error while storing Player", t);
		}
	}

	/**
	 * @return a table containing all l2fecipeList of the L2Player.<BR>
	 * <BR>
	 */
	public Collection<Recipe> getDwarvenRecipeBook()
	{
		return _recipebook.values();
	}

	public Collection<Recipe> getCommonRecipeBook()
	{
		return _commonrecipebook.values();
	}

	public int recipesCount()
	{
		return _commonrecipebook.size() + _recipebook.size();
	}

	public boolean hasRecipe(Recipe id)
	{
		return _recipebook.containsValue(id) || _commonrecipebook.containsValue(id);
	}

	public boolean findRecipe(int id)
	{
		return _recipebook.containsKey(id) || _commonrecipebook.containsKey(id);
	}

	/**
	 * Add a new l2fecipList to the table _recipebook containing all l2fecipeList of the L2Player
	 * @param recipe
	 * @param saveDB
	 */
	public void registerRecipe(Recipe recipe, boolean saveDB)
	{
		if (recipe == null)
		{
			return;
		}
		if (recipe.isDwarvenRecipe())
		{
			_recipebook.put(recipe.getId(), recipe);
		}
		else
		{
			_commonrecipebook.put(recipe.getId(), recipe);
		}
		if (saveDB)
		{
			mysql.set("REPLACE INTO character_recipebook (char_id, id) VALUES(?,?)", getObjectId(), recipe.getId());
		}
	}

	/**
	 * Remove a l2fecipList from the table _recipebook containing all l2fecipeList of the L2Player
	 * @param RecipeID
	 */
	public void unregisterRecipe(int RecipeID)
	{
		if (_recipebook.containsKey(RecipeID))
		{
			mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", getObjectId(), RecipeID);
			_recipebook.remove(RecipeID);
		}
		else if (_commonrecipebook.containsKey(RecipeID))
		{
			mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", getObjectId(), RecipeID);
			_commonrecipebook.remove(RecipeID);
		}
		else
		{
			_log.warn("Attempted to remove unknown RecipeList" + RecipeID);
		}
	}

	// ------------------- Quest Engine ----------------------

	public QuestState getQuestState(String quest)
	{
		questRead.lock();
		try
		{
			return _quests.get(quest);
		}
		finally
		{
			questRead.unlock();
		}
	}

	public QuestState getQuestState(Class<?> quest)
	{
		return getQuestState(quest.getSimpleName());
	}

	public boolean isQuestCompleted(String quest)
	{
		QuestState q = getQuestState(quest);
		return (q != null) && q.isCompleted();
	}

	public boolean isQuestCompleted(Class<?> quest)
	{
		QuestState q = getQuestState(quest);
		return (q != null) && q.isCompleted();
	}

	public void setQuestState(QuestState qs)
	{
		questWrite.lock();
		try
		{
			_quests.put(qs.getQuest().getName(), qs);
		}
		finally
		{
			questWrite.unlock();
		}
	}

	public void removeQuestState(String quest)
	{
		questWrite.lock();
		try
		{
			_quests.remove(quest);
		}
		finally
		{
			questWrite.unlock();
		}
	}

	public Quest[] getAllActiveQuests()
	{
		List<Quest> quests = new ArrayList<Quest>(_quests.size());
		questRead.lock();
		try
		{
			for (QuestState qs : _quests.values())
			{
				if (qs.isStarted())
				{
					quests.add(qs.getQuest());
				}
			}
		}
		finally
		{
			questRead.unlock();
		}
		return quests.toArray(new Quest[quests.size()]);
	}

	public QuestState[] getAllQuestsStates()
	{
		questRead.lock();
		try
		{
			return _quests.values().toArray(new QuestState[_quests.size()]);
		}
		finally
		{
			questRead.unlock();
		}
	}

	public List<QuestState> getQuestsForEvent(NpcInstance npc, QuestEventType event)
	{
		List<QuestState> states = new ArrayList<QuestState>();
		Quest[] quests = npc.getTemplate().getEventQuests(event);
		QuestState qs;
		if (quests != null)
		{
			for (Quest quest : quests)
			{
				qs = getQuestState(quest.getName());
				if ((qs != null) && !qs.isCompleted())
				{
					states.add(getQuestState(quest.getName()));
				}
			}
		}
		return states;
	}

	public void processQuestEvent(String quest, String event, NpcInstance npc, boolean... sendPacket)
	{
		if (event == null)
		{
			event = "";
		}
		QuestState qs = getQuestState(quest);
		if (qs == null)
		{
			Quest q = QuestManager.getQuest(quest);
			if (q == null)
			{
				_log.warn("Quest " + quest + " not found!");
				return;
			}
			qs = q.newQuestState(this, Quest.CREATED);
		}
		if ((qs == null) || qs.isCompleted())
		{
			return;
		}
		qs.getQuest().notifyEvent(event, qs, npc);
		if (sendPacket.length == 0 || sendPacket[0])
		{
			sendPacket(new QuestList(this));
		}
	}

	public boolean isQuestContinuationPossible(boolean msg)
	{
		if ((getWeightPenalty() >= 3) || ((getInventoryLimit() * 0.9) < getInventory().getSize()) || ((Config.QUEST_INVENTORY_MAXIMUM * 0.9) < getInventory().getQuestSize()))
		{
			if (msg)
			{
				sendPacket(Msg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_VOLUME_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
			}
			return false;
		}
		return true;
	}

	public void stopQuestTimers()
	{
		for (QuestState qs : getAllQuestsStates())
		{
			if (qs.isStarted())
			{
				qs.pauseQuestTimers();
			}
			else
			{
				qs.stopQuestTimers();
			}
		}
	}

	public void resumeQuestTimers()
	{
		for (QuestState qs : getAllQuestsStates())
		{
			qs.resumeQuestTimers();
		}
	}

	// ----------------- End of Quest Engine -------------------

	public Collection<ShortCut> getAllShortCuts()
	{
		return _shortCuts.getAllShortCuts();
	}

	public ShortCut getShortCut(int slot, int page)
	{
		return _shortCuts.getShortCut(slot, page);
	}

	public void registerShortCut(ShortCut shortcut)
	{
		_shortCuts.registerShortCut(shortcut);
	}

	public void deleteShortCut(int slot, int page)
	{
		_shortCuts.deleteShortCut(slot, page);
	}

	public void registerMacro(Macro macro)
	{
		_macroses.registerMacro(macro);
	}

	public void deleteMacro(int id)
	{
		_macroses.deleteMacro(id);
	}

	public MacroList getMacroses()
	{
		return _macroses;
	}

	public boolean isCastleLord(int castleId)
	{
		return (_clan != null) && isClanLeader() && (_clan.getCastle() == castleId);
	}

	public boolean isFortressLord(int fortressId)
	{
		return (_clan != null) && isClanLeader() && (_clan.getHasFortress() == fortressId);
	}

	public int getPkKills()
	{
		return _pkKills;
	}

	public void setPkKills(int pkKills)
	{
		_pkKills = pkKills;
	}

	public long getCreateTime()
	{
		return _createTime;
	}

	public void setCreateTime(long createTime)
	{
		_createTime = createTime;
	}

	public int getDeleteTimer()
	{
		return _deleteTimer;
	}

	public void setDeleteTimer(int deleteTimer)
	{
		_deleteTimer = deleteTimer;
	}

	public int getCurrentLoad()
	{
		return getInventory().getTotalWeight();
	}

	public long getLastAccess()
	{
		return _lastAccess;
	}

	public void setLastAccess(long value)
	{
		_lastAccess = value;
	}

	public int getRecomHave()
	{
		return _recomHave;
	}

	public void setRecomHave(int value)
	{
		if (value > 255)
		{
			_recomHave = 255;
		}
		else if (value < 0)
		{
			_recomHave = 0;
		}
		else
		{
			_recomHave = value;
		}
	}

	public int getRecomBonusTime()
	{
		if (_recomBonusTask != null)
		{
			return (int) Math.max(0, _recomBonusTask.getDelay(TimeUnit.SECONDS));
		}
		return _recomBonusTime;
	}

	public void setRecomBonusTime(int val)
	{
		_recomBonusTime = val;
	}

	public int getRecomLeft()
	{
		return _recomLeft;
	}

	public void setRecomLeft(int value)
	{
		_recomLeft = Math.max(0, Math.min(999, value));
	}

	public boolean isHourglassEffected()
	{
		return _isHourglassEffected;
	}

	public void setHourlassEffected(boolean val)
	{
		_isHourglassEffected = val;
	}

	public void startHourglassEffect()
	{
		setHourlassEffected(true);
		stopRecomBonusTask(true);
		sendVoteSystemInfo();
	}

	public void stopHourglassEffect()
	{
		setHourlassEffected(false);
		startRecomBonusTask();
		sendVoteSystemInfo();
	}

	public int addRecomLeft()
	{
		int recoms = 0;
		if (getRecomLeftToday() < 20)
		{
			recoms = 10;
		}
		else
		{
			recoms = 1;
		}
		setRecomLeft(getRecomLeft() + recoms);
		setRecomLeftToday(getRecomLeftToday() + recoms);
		sendUserInfo(true);
		return recoms;
	}

	public int getRecomLeftToday()
	{
		return _recomLeftToday;
	}

	public void setRecomLeftToday(int value)
	{
		_recomLeftToday = value;
		setVar("recLeftToday", String.valueOf(_recomLeftToday), -1);
	}

	public void giveRecom(Player target)
	{
		int targetRecom = target.getRecomHave();
		if (targetRecom < 255)
		{
			target.addRecomHave(1);
		}
		if (getRecomLeft() > 0)
		{
			setRecomLeft(getRecomLeft() - 1);
		}

		sendUserInfo(true);
	}

	public void addRecomHave(int val)
	{
		setRecomHave(getRecomHave() + val);
		broadcastUserInfo(true);
		sendVoteSystemInfo();
	}

	public int getRecomBonus()
	{
		if ((getRecomBonusTime() > 0) || isHourglassEffected())
		{
			return RecomBonus.getRecoBonus(this);
		}
		return 0;
	}

	public double getRecomBonusMul()
	{
		if ((getRecomBonusTime() > 0) || isHourglassEffected())
		{
			return RecomBonus.getRecoMultiplier(this);
		}
		return 1;
	}

	public void sendVoteSystemInfo()
	{
		sendPacket(new ExVoteSystemInfo(this));
	}

	public boolean isRecomTimerActive()
	{
		return _isRecomTimerActive;
	}

	public void setRecomTimerActive(boolean val)
	{
		if (_isRecomTimerActive == val)
		{
			return;
		}

		_isRecomTimerActive = val;

		if (val)
		{
			startRecomBonusTask();
		}
		else
		{
			stopRecomBonusTask(true);
		}

		sendVoteSystemInfo();
	}

	private ScheduledFuture<?> _recomBonusTask;

	public void startRecomBonusTask()
	{
		if ((_recomBonusTask == null) && (getRecomBonusTime() > 0) && isRecomTimerActive() && !isHourglassEffected())
		{
			_recomBonusTask = ThreadPoolManager.getInstance().schedule(new RecomBonusTask(this), getRecomBonusTime() * 1000);
		}
	}

	public void stopRecomBonusTask(boolean saveTime)
	{
		if (_recomBonusTask != null)
		{
			if (saveTime)
			{
				setRecomBonusTime((int) Math.max(0, _recomBonusTask.getDelay(TimeUnit.SECONDS)));
			}
			_recomBonusTask.cancel(false);
			_recomBonusTask = null;
		}
	}

	@Override
	public int getKarma()
	{
		return _karma;
	}

	public void setKarma(int karma)
	{
		if (isInTournament())
		{
			return;
		}

		if (karma < 0)
		{
			karma = 0;
		}

		if (_karma == karma)
		{
			return;
		}

		_karma = karma;

		sendChanges();

		if (getPet() != null)
		{
			getPet().broadcastCharInfo();
		}
	}

	@Override
	public int getMaxLoad()
	{
		// Weight Limit = (CON Modifier*69000)*Skills
		// Source http://l2f.bravehost.com/weightlimit.html (May 2007)
		// Fitted exponential curve to the data
		final int con = getCON();
		double mod = Config.MAXLOAD_MODIFIER;
		final GameClient client = getNetConnection();
		if (client != null && client.getBonusExpire() > System.currentTimeMillis() / 1000L)
		{
			final Bonus bonus = getBonus();
			mod += bonus.getWeight();
		}

		if (con < 1)
		{
			return (int) (31000.0 * mod);
		}

		if (con > 59)
		{
			return (int) (176000.0 * mod);
		}

		return (int) calcStat(Stats.MAX_LOAD, Math.pow(1.029993928, con) * 30495.627366 * mod, this, null);
	}

	private Future<?> _updateEffectIconsTask;

	private class UpdateEffectIcons extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			updateEffectIconsImpl();
			_updateEffectIconsTask = null;
		}
	}

	@Override
	public void updateEffectIcons()
	{
		if (entering || isLogoutStarted())
		{
			return;
		}

		if (Config.USER_INFO_INTERVAL == 0)
		{
			if (_updateEffectIconsTask != null)
			{
				_updateEffectIconsTask.cancel(false);
				_updateEffectIconsTask = null;
			}
			updateEffectIconsImpl();
			return;
		}

		if (_updateEffectIconsTask != null)
		{
			return;
		}

		_updateEffectIconsTask = ThreadPoolManager.getInstance().schedule(new UpdateEffectIcons(), Config.USER_INFO_INTERVAL);
	}

	public void updateEffectIconsImpl()
	{
		Effect[] effects = getEffectList().getAllFirstEffects();
		Arrays.sort(effects, EffectsComparator.getInstance());

		PartySpelled ps = new PartySpelled(this, false);
		AbnormalStatusUpdate mi = new AbnormalStatusUpdate();

		for (Effect effect : effects)
		{
			if (effect.isInUse())
			{
				if (effect.getStackType().equals(EffectTemplate.HP_RECOVER_CAST))
				{
					sendPacket(new ShortBuffStatusUpdate(effect));
				}
				else
				{
					effect.addIcon(mi);
				}
				if (_party != null)
				{
					effect.addPartySpelledIcon(ps);
				}
			}
		}

		sendPacket(mi);
		if (_party != null)
		{
			_party.sendPacket(ps);
		}

		if (isInOlympiadMode() && isOlympiadCompStart())
		{
			OlympiadGame olymp_game = _olympiadGame;
			if (olymp_game != null)
			{
				ExOlympiadSpelledInfo olympiadSpelledInfo = new ExOlympiadSpelledInfo();

				for (Effect effect : effects)
				{
					if ((effect != null) && effect.isInUse())
					{
						effect.addOlympiadSpelledIcon(this, olympiadSpelledInfo);
					}
				}

				if ((olymp_game.getType() == CompType.CLASSED) || (olymp_game.getType() == CompType.NON_CLASSED))
				{
					for (Player member : olymp_game.getTeamMembers(this))
					{
						member.sendPacket(olympiadSpelledInfo);
					}
				}

				for (Player member : olymp_game.getSpectators())
				{
					member.sendPacket(olympiadSpelledInfo);
				}
			}
		}
	}

	public int getWeightPenalty()
	{
		return getSkillLevel(4270, 0);
	}

	public void refreshOverloaded()
	{
		if (isLogoutStarted() || (getMaxLoad() <= 0))
		{
			return;
		}

		setOverloaded(getCurrentLoad() > getMaxLoad());
		double weightproc = (100. * (getCurrentLoad() - calcStat(Stats.MAX_NO_PENALTY_LOAD, 0, this, null))) / getMaxLoad();
		int newWeightPenalty = 0;

		if (weightproc < 50)
		{
			newWeightPenalty = 0;
		}
		else if (weightproc < 66.6)
		{
			newWeightPenalty = 1;
		}
		else if (weightproc < 80)
		{
			newWeightPenalty = 2;
		}
		else if (weightproc < 100)
		{
			newWeightPenalty = 3;
		}
		else
		{
			newWeightPenalty = 4;
		}

		int current = getWeightPenalty();
		if (current == newWeightPenalty)
		{
			return;
		}

		if (newWeightPenalty > 0)
		{
			super.addSkill(SkillTable.getInstance().getInfo(4270, newWeightPenalty));
		}
		else
		{
			super.removeSkill(getKnownSkill(4270));
		}

		sendPacket(new SkillList(this));
		sendEtcStatusUpdate();
		updateStats();
	}

	public int getArmorsExpertisePenalty()
	{
		return getSkillLevel(6213, 0);
	}

	public int getWeaponsExpertisePenalty()
	{
		return getSkillLevel(6209, 0);
	}

	public int getExpertisePenalty(ItemInstance item)
	{
		if (item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
		{
			return getWeaponsExpertisePenalty();
		}
		else if ((item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR) || (item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY))
		{
			return getArmorsExpertisePenalty();
		}
		return 0;
	}

	public void refreshExpertisePenalty()
	{
		if (isLogoutStarted())
		{
			return;
		}

		boolean skillUpdate = false;

		int level = (int) calcStat(Stats.GRADE_EXPERTISE_LEVEL, getLevel(), null, null);
		int i = 0;
		for (i = 0; (i < EXPERTISE_LEVELS.length) && (level >= EXPERTISE_LEVELS[(i + 1)]); i++)
		{
		}
		if (expertiseIndex != i)
		{
			expertiseIndex = i;
			if ((expertiseIndex > 0) && Config.EXPERTISE_PENALTY) // TODO who to do? No need here! redo with a check for an item epic! added Config.EPIC_EXPERTISE_PENALTY
			{
				addSkill(SkillTable.getInstance().getInfo(239, expertiseIndex), false);
				skillUpdate = true;
			}
		}

		int newWeaponPenalty = 0;
		int newArmorPenalty = 0;
		ItemInstance[] items = getInventory().getPaperdollItems();
		for (ItemInstance item : items)
		{
			if (item != null)
			{
				int crystaltype = item.getTemplate().getCrystalType().ordinal();
				if (item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
				{
					if (crystaltype > newWeaponPenalty)
					{
						newWeaponPenalty = crystaltype;
					}
				}
				else if ((item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR) || (item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY))
				{
					if (crystaltype > newArmorPenalty)
					{
						newArmorPenalty = crystaltype;
					}
				}
			}
		}

		newWeaponPenalty = newWeaponPenalty - expertiseIndex;
		if (newWeaponPenalty <= 0)
		{
			newWeaponPenalty = 0;
		}
		else if (newWeaponPenalty >= 4)
		{
			newWeaponPenalty = 4;
		}

		newArmorPenalty = newArmorPenalty - expertiseIndex;
		if (newArmorPenalty <= 0)
		{
			newArmorPenalty = 0;
		}
		else if (newArmorPenalty >= 4)
		{
			newArmorPenalty = 4;
		}

		int weaponExpertise = getWeaponsExpertisePenalty();
		int armorExpertise = getArmorsExpertisePenalty();

		if (weaponExpertise != newWeaponPenalty)
		{
			weaponExpertise = newWeaponPenalty;
			if ((newWeaponPenalty > 0) && Config.EXPERTISE_PENALTY)
			{
				addSkill(SkillTable.getInstance().getInfo(6209, weaponExpertise));
			}
			else
			{
				removeSkill(getKnownSkill(6209));
			}
			skillUpdate = true;
		}
		if (armorExpertise != newArmorPenalty)
		{
			armorExpertise = newArmorPenalty;
			if ((newArmorPenalty > 0) && Config.EXPERTISE_PENALTY)
			{
				addSkill(SkillTable.getInstance().getInfo(6213, armorExpertise));
			}
			else
			{
				removeSkill(getKnownSkill(6213));
			}
			skillUpdate = true;
		}

		if (skillUpdate)
		{
			_inventory.validateItemsSkills();

			sendPacket(new SkillList(this));
			sendEtcStatusUpdate();
			updateStats();
		}
	}

	public int getPvpKills()
	{
		return _pvpKills;
	}

	public void setPvpKills(int pvpKills)
	{
		_pvpKills = pvpKills;
	}

	public ClassId getClassId()
	{
		return getTemplate().classId;
	}

	public boolean isPendingOlyEnd()
	{
		return _isPendingOlyEnd;
	}

	public void setPendingOlyEnd(boolean val)
	{
		_isPendingOlyEnd = val;
	}

	public void addClanPointsOnProfession(int id)
	{
		if ((getLvlJoinedAcademy() != 0) && (_clan != null) && (_clan.getLevel() >= 5) && (ClassId.VALUES[id].getLevel() == 2))
		{
			_clan.incReputation(100, true, "Academy");
		}
		else if ((getLvlJoinedAcademy() != 0) && (_clan != null) && (_clan.getLevel() >= 5) && (ClassId.VALUES[id].getLevel() == 3))
		{
			int earnedPoints = 0;
			if (getLvlJoinedAcademy() <= 16)
			{
				earnedPoints = Config.MAX_ACADEM_POINT;
			}
			else if (getLvlJoinedAcademy() >= 39)
			{
				earnedPoints = Config.MIN_ACADEM_POINT;
			}
			else
			{
				earnedPoints = Config.MAX_ACADEM_POINT - ((getLvlJoinedAcademy() - 16) * 20);
			}

			_clan.removeClanMember(getObjectId());

			SystemMessage sm = new SystemMessage(SystemMessage.CLAN_ACADEMY_MEMBER_S1_HAS_SUCCESSFULLY_COMPLETED_THE_2ND_CLASS_TRANSFER_AND_OBTAINED_S2_CLAN_REPUTATION_POINTS);
			sm.addString(getName());
			sm.addNumber(_clan.incReputation(earnedPoints, true, "Academy"));
			_clan.broadcastToOnlineMembers(sm);
			_clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListDelete(getName()), this);
			// claww add academy reward
			AcademyList.removeAcademyFromDB(_clan, getObjectId(), true, false);
			setClan(null);
			setTitle("");
			sendPacket(Msg.CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_AS_A_GRADUATE_OF_THE_ACADEMY_YOU_CAN_IMMEDIATELY_JOIN_A_CLAN_AS_A_REGULAR_MEMBER_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES);
			setLeaveClanTime(0);

			broadcastCharInfo();

			sendPacket(PledgeShowMemberListDeleteAll.STATIC);

			ItemFunctions.addItem(this, 8181, 1, true, "Academy");
		}
	}

	/**
	 * Set the template of the L2Player.
	 * @param id The Identifier of the L2PlayerTemplate to set to the L2Player
	 * @param noban
	 * @param fromQuest
	 */
	public synchronized void setClassId(int id, boolean noban, boolean fromQuest)
	{
		boolean cannotChangeClass = !getPlayerAccess().CanChangeClass && !Config.EVERYBODY_HAS_ADMIN_RIGHTS;
		if (!noban && !ClassId.VALUES[id].equalsOrChildOf(ClassId.VALUES[getActiveClassId()]) && cannotChangeClass)
		{
			_log.error("Error while setting new class as :" + id + " Player:" + getName() + " current Class:" + getActiveClassId() + "|cannot change class? " + cannotChangeClass);
			Thread.dumpStack();
			return;
		}

		if (!getSubClasses().containsKey(id))
		{
			final SubClass cclass = getActiveClass();
			getSubClasses().remove(getActiveClassId());
			changeClassInDb(cclass.getClassId(), id);
			if (cclass.isBase())
			{
				setBaseClass(id);
				addClanPointsOnProfession(id);
				ItemInstance coupons = null;
				if (ClassId.VALUES[id].getLevel() == 2)
				{
					if (fromQuest && Config.ALT_ALLOW_SHADOW_WEAPONS)
					{
						coupons = ItemFunctions.createItem(8869);
					}
					unsetVar("newbieweapon");
					unsetVar("p1q2");
					unsetVar("p1q3");
					unsetVar("p1q4");
					unsetVar("prof1");
					unsetVar("ng1");
					unsetVar("ng2");
					unsetVar("ng3");
					unsetVar("ng4");
				}
				else if (ClassId.VALUES[id].getLevel() == 3)
				{
					if (fromQuest && Config.ALT_ALLOW_SHADOW_WEAPONS)
					{
						coupons = ItemFunctions.createItem(8870);
					}
					unsetVar("newbiearmor");
					unsetVar("dd1");
					unsetVar("dd2");
					unsetVar("dd3");
					unsetVar("prof2.1");
					unsetVar("prof2.2");
					unsetVar("prof2.3");
				}

				if (coupons != null)
				{
					coupons.setCount(15);
					sendPacket(SystemMessage2.obtainItems(coupons));
					getInventory().addItem(coupons, "Class Change");
				}
			}

			// Holy Pomander
			switch (ClassId.VALUES[id])
			{
			case cardinal:
				ItemFunctions.addItem(this, 15307, 1, true, "Class Change");
				break;
			case evaSaint:
				ItemFunctions.addItem(this, 15308, 1, true, "Class Change");
				break;
			case shillienSaint:
				ItemFunctions.addItem(this, 15309, 4, true, "Class Change");
				break;
			}

			cclass.setClassId(id);
			getSubClasses().put(id, cclass);
			rewardSkills(true);
			storeCharSubClasses();

			if (fromQuest)
			{
				broadcastPacket(new MagicSkillUse(this, this, 5103, 1, 1000, 0));
				sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
			}
			broadcastCharInfo();
		}

		PlayerTemplate t = CharTemplateHolder.getInstance().getTemplate(id, getSex() == 1);
		if (t == null)
		{
			_log.error("Missing template for classId: " + id);
			// do not throw error - only print error
			return;
		}

		// Set the template of the L2Player
		_template = t;

		// Update class icon in party and clan
		if (isInParty())
		{
			_party.sendPacket(new PartySmallWindowUpdate(this));
		}
		if (_clan != null)
		{
			_clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(this));
		}
		if (_matchingRoom != null)
		{
			_matchingRoom.broadcastPlayerUpdate(this);
		}
	}

	public long getExp()
	{
		return _activeClass == null ? 0 : _activeClass.getExp();
	}

	public long getMaxExp()
	{
		return _activeClass == null ? Experience.LEVEL[Experience.getMaxLevel() + 1] : _activeClass.getMaxExp();
	}

	public void setEnchantScroll(ItemInstance scroll)
	{
		_enchantScroll = scroll;
	}

	public ItemInstance getEnchantScroll()
	{
		return _enchantScroll;
	}

	public void setFistsWeaponItem(WeaponTemplate weaponItem)
	{
		_fistsWeaponItem = weaponItem;
	}

	public WeaponTemplate getFistsWeaponItem()
	{
		return _fistsWeaponItem;
	}

	public WeaponTemplate findFistsWeaponItem(int classId)
	{
		// human fighter fists
		if ((classId >= 0x00) && (classId <= 0x09))
		{
			return (WeaponTemplate) ItemHolder.getInstance().getTemplate(246);
		}

		// human mage fists
		if ((classId >= 0x0a) && (classId <= 0x11))
		{
			return (WeaponTemplate) ItemHolder.getInstance().getTemplate(251);
		}

		// elven fighter fists
		if ((classId >= 0x12) && (classId <= 0x18))
		{
			return (WeaponTemplate) ItemHolder.getInstance().getTemplate(244);
		}

		// elven mage fists
		if ((classId >= 0x19) && (classId <= 0x1e))
		{
			return (WeaponTemplate) ItemHolder.getInstance().getTemplate(249);
		}

		// dark elven fighter fists
		if ((classId >= 0x1f) && (classId <= 0x25))
		{
			return (WeaponTemplate) ItemHolder.getInstance().getTemplate(245);
		}

		// dark elven mage fists
		if ((classId >= 0x26) && (classId <= 0x2b))
		{
			return (WeaponTemplate) ItemHolder.getInstance().getTemplate(250);
		}

		// orc fighter fists
		if ((classId >= 0x2c) && (classId <= 0x30))
		{
			return (WeaponTemplate) ItemHolder.getInstance().getTemplate(248);
		}

		// orc mage fists
		if ((classId >= 0x31) && (classId <= 0x34))
		{
			return (WeaponTemplate) ItemHolder.getInstance().getTemplate(252);
		}

		// dwarven fists
		if ((classId >= 0x35) && (classId <= 0x39))
		{
			return (WeaponTemplate) ItemHolder.getInstance().getTemplate(247);
		}

		return null;
	}

	public void addExpAndCheckBonus(MonsterInstance mob, double noRateExp, double noRateSp, double partyVitalityMod)
	{
		if (_activeClass == null || getVarB("NoExp"))
		{
			return;
		}

		double neededExp = calcStat(Stats.SOULS_CONSUME_EXP, 0.0D, mob, null);
		if ((neededExp > 0.0D) && (noRateExp > neededExp))
		{
			mob.broadcastPacket(new L2GameServerPacket[]
			{
				new SpawnEmitter(mob, this)
			});
			ThreadPoolManager.getInstance().schedule(new GameObjectTasks.SoulConsumeTask(this), 1000L);
		}

		double vitalityBonus = 0.0D;
		int npcLevel = mob.getLevel();
		if (Config.ALT_VITALITY_ENABLED)
		{
			boolean blessActive = getNevitSystem().isBlessingActive();
			vitalityBonus = mob.isRaid() ? 0.0D : getVitalityLevel(blessActive) / 2.0D;
			vitalityBonus *= Config.ALT_VITALITY_RATE;

			if (noRateExp > 0.0D)
			{
				if (!mob.isRaid())
				{
					if ((blessActive) && ((!getVarB("NoExp")) || (getExp() != (l2mv.gameserver.model.base.Experience.LEVEL[(getLevel() + 1)] - 1L))))
					{
						double points = ((noRateExp / (npcLevel * npcLevel)) * 100.0D) / 9.0D;
						points *= Config.ALT_VITALITY_CONSUME_RATE;
						vitalityBonus = 4.0D * Config.ALT_VITALITY_RATE;
						setVitality(getVitality() + (points * partyVitalityMod));
					}
					else if ((!blessActive) && ((!getVarB("NoExp")) || (getExp() != (l2mv.gameserver.model.base.Experience.LEVEL[(getLevel() + 1)] - 1L))))
					{
						double points = ((noRateExp / (npcLevel * npcLevel)) * 100.0D) / 9.0D;
						points *= Config.ALT_VITALITY_CONSUME_RATE;

						if (getEffectList().getEffectByType(EffectType.Vitality) != null)
						{
							points *= -1.0D;
						}
						if (getEffectList().getEffectByType(EffectType.VitalityMaintenance) == null)
						{
							setVitality(getVitality() - (points * partyVitalityMod));
						}
					}
				}
				else
				{
					setVitality(getVitality() + Config.ALT_VITALITY_RAID_BONUS);
				}
			}
		}

		// In the first call, activate the timer bonuses.
		if (!isInPeaceZone())
		{
			setRecomTimerActive(true);
			getNevitSystem().startAdventTask();
			if ((getLevel() - npcLevel) <= 9)
			{
				int nevitPoints = (int) Math.round(((noRateExp / (npcLevel * npcLevel)) * 100) / 20); // TODO: Formula from the bulldozer.
				getNevitSystem().addPoints(nevitPoints);
			}
		}

		// Synerge - After lvl 80 characters will get -5% of xp sp
//		if (getLevel() >= 80)
//		{
//			noRateExp *= 0.95;
//			noRateSp *= 0.95;
//		}

		long normalExp = (long) (noRateExp * (((Config.RATE_XP * getRateExp()) + vitalityBonus) * getRecomBonusMul()));
		long normalSp = (long) (noRateSp * ((Config.RATE_SP * getRateSp()) + vitalityBonus));

		long expWithoutBonus = (long) (noRateExp * Config.RATE_XP * getRateExp());
		long spWithoutBonus = (long) (noRateSp * Config.RATE_SP * getRateSp());

		addExpAndSp(normalExp, normalSp, normalExp - expWithoutBonus, normalSp - spWithoutBonus, false, true);
	}

	private boolean _isVitalityStop = false;

	public void VitalityStop(boolean stop)
	{
		_isVitalityStop = stop;
	}

	@SuppressWarnings("unused")
	private boolean isVitalityStop()
	{
		return _isVitalityStop;
	}

	public void setExp(long exp)
	{
		int oldLvl = _activeClass.getLevel();

		_activeClass.setExp(exp);

		int level = _activeClass.getLevel();
		if (level != oldLvl)
		{
			int levels = level - oldLvl;
			if (levels > 0)
			{
				getNevitSystem().addPoints(1950);
			}
			levelSet(levels);
		}

		updateStats();
	}

	@Override
	public void addExpAndSp(long exp, long sp)
	{
		addExpAndSp(exp, sp, 0, 0, false, false);
	}

	public void addExpAndSp(long addToExp, long addToSp, long bonusAddExp, long bonusAddSp, boolean applyRate, boolean applyToPet)
	{
		if (_activeClass == null || (getVarB("NoExp") && applyRate && addToExp > 0))
		{
			return;
		}

		if (applyRate)
		{
			addToExp *= Config.RATE_XP * getRateExp();
			addToSp *= Config.RATE_SP * getRateSp();
		}

		Summon pet = getPet();
		if (addToExp > 0)
		{
			if (applyToPet)
			{
				if ((pet != null) && !pet.isDead() && !PetDataTable.isVitaminPet(pet.getNpcId()))
				{
					if (pet.getNpcId() == PetDataTable.SIN_EATER_ID)
					{
						pet.addExpAndSp(addToExp, 0);
						addToExp = 0;
					}
					else if (pet.isPet() && (pet.getExpPenalty() > 0f))
					{
						if ((pet.getLevel() > (getLevel() - 20)) && (pet.getLevel() < (getLevel() + 5)))
						{
							pet.addExpAndSp((long) (addToExp * pet.getExpPenalty()), 0);
							addToExp *= 1. - pet.getExpPenalty();
						}
						else
						{
							pet.addExpAndSp((long) ((addToExp * pet.getExpPenalty()) / 5.), 0);
							addToExp *= 1. - (pet.getExpPenalty() / 5.);
						}
					}
					else if (pet.isSummon())
					{
						addToExp *= 1. - pet.getExpPenalty();
					}
				}
			}

			// Prims fix - stabileste dak a avut karma inainte
			boolean hadKarma = _karma > 0;

			// Remove Karma when the player kills L2MonsterInstance
			if (!isCursedWeaponEquipped() && (addToSp > 0) && (_karma > 0))
			{
				long toDecrease = Config.KARMA_MIN_KARMA / 10 + getPkKills() * Config.KARMA_SP_DIVIDER;
				setKarma(_karma - (int) Rnd.get(toDecrease / 2, toDecrease * 2));
			}

			// Prims PK fix
			if (_karma <= 0)
			{
				_karma = 0;
				if (hadKarma)
				{
					startPvPFlag(this);
				}
			}

			getCounters().expAcquired += addToExp;

			long max_xp = getVarB("NoExp") ? Experience.LEVEL[getLevel() + 1] - 1 : getMaxExp();
			addToExp = Math.min(addToExp, max_xp - getExp());
		}

		int oldLvl = _activeClass.getLevel();
		long oldExp = _activeClass.getExp();

		_activeClass.addExp(addToExp);
		_activeClass.addSp(addToSp);

		if ((addToExp > 0) && (addToSp > 0) && ((bonusAddExp > 0) || (bonusAddSp > 0)))
		{
			sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_ACQUIRED_S1_EXP_BONUS_S2_AND_S3_SP_BONUS_S4).addLong(addToExp).addLong(bonusAddExp).addInteger(addToSp).addInteger((int) bonusAddSp));
		}
		else if ((addToSp > 0) && (addToExp == 0))
		{
			sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_SP).addNumber(addToSp));
		}
		else if ((addToSp > 0) && (addToExp > 0))
		{
			sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_EXPERIENCE_AND_S2_SP).addNumber(addToExp).addNumber(addToSp));
		}
		else if ((addToSp == 0) && (addToExp > 0))
		{
			sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_EXPERIENCE).addNumber(addToExp));
		}

		// Synerge - Custom tutorial event for the first exp got and then in lvl 6
		if (addToExp > 0 && (oldExp < 100 || (_activeClass.getLevel() >= 6 && _activeClass.getLevel() <= 10)))
		{
			Quest q = QuestManager.getQuest(255);
			if (q != null)
			{
				processQuestEvent(q.getName(), "CE41", null);
			}
		}

		int level = _activeClass.getLevel();
		if (level != oldLvl)
		{
			int levels = level - oldLvl;
			if (levels > 0)
			{
				getNevitSystem().addPoints(1950);
			}
			levelSet(levels);
		}
		// Custom Level Up Soul Crystals
		if (Config.AUTO_SOUL_CRYSTAL_QUEST)
		{
			Quest q = QuestManager.getQuest(350);
			if (level >= 45 && q != null && getQuestState(q.getName()) == null)
			{
				processQuestEvent(q.getName(), "30115-04.htm", null, false);
			}
		}

		if ((pet != null) && pet.isPet() && PetDataTable.isVitaminPet(pet.getNpcId()))
		{
			PetInstance _pet = (PetInstance) pet;
			_pet.setLevel(getLevel());
			_pet.setExp(_pet.getExpForNextLevel());
			_pet.broadcastStatusUpdate();
		}

		if (getNevitSystem().isBlessingActive())
		{
			addVitality(Config.ALT_VITALITY_NEVIT_POINT);
		}

		updateStats();
	}

	/**
	 * Give Expertise skill of this level.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Get the Level of the L2Player</li> <li>Add the Expertise skill corresponding to its Expertise level</li> <li>Update the overloaded status of the L2Player</li><BR>
	 * <BR>
	 * <p/>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T give other free skills (SP needed = 0)</B></FONT><BR>
	 * <BR>
	 * @param send
	 */
	private void rewardSkills(boolean send)
	{
		boolean update = false;
		if (Config.AUTO_LEARN_SKILLS)
		{
			int unLearnable = 0;
			Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL);
			while (skills.size() > unLearnable)
			{
				unLearnable = 0;
				for (SkillLearn s : skills)
				{
					final Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
					if (sk == null)
					{
						_log.error("Attempting to add unexisting skill: " + s.getId() + " level: " + s.getLevel());
						break;
					}

					if (!sk.getCanLearn(getClassId()) || (!Config.AUTO_LEARN_FORGOTTEN_SKILLS && s.isClicked()))
					{
						unLearnable++;
						continue;
					}

					addSkill(sk, true);

					// Update shortcuts for skills on Level up.
					if ((getAllShortCuts().size() > 0) && (sk.getLevel() > 1))
					{
						for (ShortCut sc : getAllShortCuts())
						{
							if ((sc.getId() == sk.getId()) && (sc.getType() == ShortCut.TYPE_SKILL))
							{
								ShortCut newsc = new ShortCut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), sk.getLevel(), 1);
								sendPacket(new ShortCutRegister(this, newsc));
								registerShortCut(newsc);
							}
						}
					}
				}
				skills = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL);
			}
			update = true;
		}
		else
		{
			// Skills gives subscription-free does not need to be studied
			for (SkillLearn skill : SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL))
			{
				if ((skill.getCost() == 0) && (skill.getItemId() == 0))
				{
					final Skill sk = SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel());
					if (sk == null)
					{
						_log.error("Attempting to add unexisting skill: " + skill.getId() + " level: " + skill.getLevel());
						break;
					}

					addSkill(sk, true);

					// Update shortcuts for skills on Level up.
					if ((getAllShortCuts().size() > 0) && (sk.getLevel() > 1))
					{
						for (ShortCut sc : getAllShortCuts())
						{
							if ((sc.getId() == sk.getId()) && (sc.getType() == ShortCut.TYPE_SKILL))
							{
								ShortCut newsc = new ShortCut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), sk.getLevel(), 1);
								sendPacket(new ShortCutRegister(this, newsc));
								registerShortCut(newsc);
							}
						}
					}
					update = true;
				}
			}
		}

		if (send && update)
		{
			sendPacket(new SkillList(this));
		}

		updateStats();
	}

	public Race getRace()
	{
		return getBaseTemplate().race;
	}

	public int getIntSp()
	{
		return (int) getSp();
	}

	public long getSp()
	{
		return _activeClass == null ? 0 : _activeClass.getSp();
	}

	public void setSp(long sp)
	{
		if (_activeClass != null)
		{
			_activeClass.setSp(sp);
		}
	}

	public int getClanId()
	{
		return _clan == null ? 0 : _clan.getClanId();
	}

	public long getLeaveClanTime()
	{
		return _leaveClanTime;
	}

	public long getDeleteClanTime()
	{
		return _deleteClanTime;
	}

	public void setLeaveClanTime(long time)
	{
		_leaveClanTime = time;
	}

	public void setDeleteClanTime(long time)
	{
		_deleteClanTime = time;
	}

	public void setOnlineTime(long time)
	{
		_onlineTime = time;
		_onlineBeginTime = System.currentTimeMillis();
	}

	public long getOnlineTime()
	{
		return _onlineTime + getUptime();
	}

	/**
	 * @return Time since logging in in seconds
	 */
	public long getOnlineBeginTime()
	{
		return _onlineBeginTime / 1000L;
	}

	public void setNoChannel(long time)
	{
		_NoChannel = time;
		if ((_NoChannel > 2145909600000L) || (_NoChannel < 0))
		{
			_NoChannel = -1;
		}

		if (_NoChannel > 0)
		{
			_NoChannelBegin = System.currentTimeMillis();
		}
		else
		{
			_NoChannelBegin = 0;
		}
	}

	public long getNoChannel()
	{
		return _NoChannel;
	}

	public long getNoChannelRemained()
	{
		if (_NoChannel == 0)
		{
			return 0;
		}
		else if (_NoChannel < 0)
		{
			return -1;
		}
		else
		{
			long remained = (_NoChannel - System.currentTimeMillis()) + _NoChannelBegin;
			if (remained < 0)
			{
				return 0;
			}

			return remained;
		}
	}

	public void setLeaveClanCurTime()
	{
		if (Config.CLAN_LEAVE_PENALTY == 0)
		{
			return;
		}

		_leaveClanTime = System.currentTimeMillis();
	}

	public void setDeleteClanCurTime()
	{
		_deleteClanTime = System.currentTimeMillis();
	}

	public boolean canJoinClan()
	{
		if (_leaveClanTime == 0)
		{
			return true;
		}
		if (System.currentTimeMillis() - _leaveClanTime >= Config.CLAN_LEAVE_PENALTY * 60 * 60 * 1000L)
		{
			_leaveClanTime = 0;
			return true;
		}
		return false;
	}

	public boolean canCreateClan()
	{
		if (_deleteClanTime == 0)
		{
			return true;
		}
		if (System.currentTimeMillis() - _deleteClanTime >= 10 * 24 * 60 * 60 * 1000L)
		{
			_deleteClanTime = 0;
			return true;
		}
		return false;
	}

	public IStaticPacket canJoinParty(Player inviter)
	{
		Request request = getRequest();
		if ((request != null) && request.isInProgress() && (request.getOtherPlayer(this) != inviter))
		{
			return SystemMsg.WAITING_FOR_ANOTHER_REPLY.packet(inviter);
		}
		if (isBlockAll() || getMessageRefusal())
		{
			return SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE.packet(inviter);
		}
		if (isInParty())
		{
			return new SystemMessage2(SystemMsg.C1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED).addName(this);
		}
		if (inviter.getReflection() != getReflection())
		{
			if ((inviter.getReflection() != ReflectionManager.DEFAULT) && (getReflection() != ReflectionManager.DEFAULT))
			{
				return SystemMsg.INVALID_TARGET.packet(inviter);
			}
		}
		if (isCursedWeaponEquipped() || inviter.isCursedWeaponEquipped())
		{
			return SystemMsg.INVALID_TARGET.packet(inviter);
		}
		if (inviter.isInOlympiadMode() || isInOlympiadMode())
		{
			return SystemMsg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS.packet(inviter);
		}
		if (!inviter.getPlayerAccess().CanJoinParty || !getPlayerAccess().CanJoinParty || (getTeam() != TeamType.NONE))
		{
			return SystemMsg.INVALID_TARGET.packet(inviter);
		}
		if (isInFightClub() && !getFightClubEvent().canJoinParty(inviter, this))
		{
			return SystemMsg.INVALID_TARGET.packet(inviter);
		}
		return null;
	}

	@Override
	public PcInventory getInventory()
	{
		return _inventory;
	}

	@Override
	public long getWearedMask()
	{
		return _inventory.getWearedMask();
	}

	public PcFreight getFreight()
	{
		return _freight;
	}

	public void removeItemFromShortCut(int objectId)
	{
		_shortCuts.deleteShortCutByObjectId(objectId);
	}

	public void removeSkillFromShortCut(int skillId)
	{
		_shortCuts.deleteShortCutBySkillId(skillId);
	}

	public boolean isSitting()
	{
		return _isSitting;
	}

	public void setSitting(boolean val)
	{
		_isSitting = val;
	}

	public boolean getSittingTask()
	{
		return sittingTaskLaunched;
	}

	@Override
	public void sitDown(StaticObjectInstance throne, boolean... force)
	{
		if (isSitting() || sittingTaskLaunched || isAlikeDead())
		{
			return;
		}
		if (force.length == 0 || !force[0])
		{
			if (sittingTaskLaunched || isAlikeDead())
			{
				return;
			}

			if (isStunned() || isSleeping() || isParalyzed() || isAttackingNow() || isCastingNow() || isMoving)
			{
				getAI().setNextAction(nextAction.REST, null, null, false, false);
				return;
			}
		}

		resetWaitSitTime();
		getAI().setIntention(CtrlIntention.AI_INTENTION_REST, null, null);

		if (throne == null)
		{
			broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_SITTING));
		}
		else
		{
			broadcastPacket(new ChairSit(this, throne));
		}

		_sittingObject = throne;
		setSitting(true);
		sittingTaskLaunched = true;
		ThreadPoolManager.getInstance().schedule(new EndSitDownTask(this), 2500);
	}

	@Override
	public void standUp()
	{
		if (!isSitting() || sittingTaskLaunched || isInStoreMode() || isAlikeDead())
		{
			return;
		}
		if (isInFightClub() && !getFightClubEvent().canStandUp(this))
		{
			return;
		}

		getEffectList().stopAllSkillEffects(EffectType.Relax);

		if (getEffectList().getEffectsBySkillId(296) != null)
		{
			getEffectList().stopEffect(296);
		}
		if (getEffectList().getEffectsBySkillId(226) != null)
		{
			getEffectList().stopEffect(226);
		}

		getAI().clearNextAction();
		broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_STANDING));

		_sittingObject = null;
		sittingTaskLaunched = true;
		ThreadPoolManager.getInstance().schedule(new EndStandUpTask(this), 2500);
	}

	public void updateWaitSitTime()
	{
		if (_waitTimeWhenSit < 200)
		{
			_waitTimeWhenSit += 2;
		}
	}

	public int getWaitSitTime()
	{
		return _waitTimeWhenSit;
	}

	public void resetWaitSitTime()
	{
		_waitTimeWhenSit = 0;
	}

	public Warehouse getWarehouse()
	{
		return _warehouse;
	}

	public ItemContainer getRefund()
	{
		return _refund;
	}

	public long getAdena()
	{
		return getInventory().getAdena();
	}

	public boolean reduceAdena(long adena, String log)
	{
		return reduceAdena(adena, false, log);
	}

	public boolean reduceAdena(long adena, boolean notify, String log)
	{
		if (adena < 0)
		{
			return false;
		}
		if (adena == 0)
		{
			return true;
		}
		boolean result = getInventory().reduceAdena(adena, log);
		if (notify && result)
		{
			sendPacket(SystemMessage2.removeItems(ItemTemplate.ITEM_ID_ADENA, adena));
		}
		return result;
	}

	public ItemInstance addAdena(long adena, String log)
	{
		return addAdena(adena, false, log);
	}

	public ItemInstance addAdena(long adena, boolean notify, String log)
	{
		if (adena < 1)
		{
			return null;
		}
		ItemInstance item = getInventory().addAdena(adena, log);
		if ((item != null) && notify)
		{
			sendPacket(SystemMessage2.obtainItems(ItemTemplate.ITEM_ID_ADENA, adena, 0));
		}
		return item;
	}

	public GameClient getNetConnection()
	{
		return _connection;
	}

	public GameClient getClient()
	{
		return _connection;
	}

	public int getRevision()
	{
		return _connection == null ? 0 : _connection.getRevision();
	}

	public void setNetConnection(GameClient connection)
	{
		_connection = connection;
	}

	public boolean isConnected()
	{
		return (_connection != null) && _connection.isConnected();
	}

	@Override
	public void onAction(Player player, boolean shift)
	{
		if (isFrozen() || Events.onAction(player, this, shift))
		{
			player.sendPacket(ActionFail.STATIC);
			return;
		}
		// Check if the other player already target this L2Player
		if (player.getTarget() != this)
		{
			player.setTarget(this);
			if (player.getTarget() == this)
			{
				player.sendPacket(new MyTargetSelected(getObjectId(), 0)); // The color to display in the select window is White
			}
			else
			{
				player.sendPacket(ActionFail.STATIC);
			}
		}
		else if (getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			if ((getDistance(player) > INTERACTION_DISTANCE) && (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT))
			{
				if (!shift)
				{
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
				}
				else
				{
					player.sendPacket(ActionFail.STATIC);
				}
			}
			else
			{
				player.doInteract(this);
			}
		}
		else if (isAutoAttackable(player))
		{
			player.getAI().Attack(this, false, shift);
		}
		else if ((player != this) && (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_FOLLOW))
		{
			if (!shift)
			{
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this, Config.FOLLOW_RANGE);
			}
			else
			{
				player.sendPacket(ActionFail.STATIC);
			}
		}
		else
		{
			player.sendPacket(ActionFail.STATIC);
		}
	}

	@Override
	public void broadcastStatusUpdate()
	{
		if (!needStatusUpdate())
		{
			return;
		}

		StatusUpdate su = makeStatusUpdate(StatusUpdate.MAX_HP, StatusUpdate.MAX_MP, StatusUpdate.MAX_CP, StatusUpdate.CUR_HP, StatusUpdate.CUR_MP, StatusUpdate.CUR_CP);
		sendPacket(su);

		// Check if a party is in progress
		if (isInParty())
		{
			// Send the Server->Client packet PartySmallWindowUpdate with current HP, MP and Level to all other L2Player of the Party
			getParty().sendPacket(this, new PartySmallWindowUpdate(this));
		}

		DuelEvent duelEvent = getEvent(DuelEvent.class);
		if (duelEvent != null)
		{
			duelEvent.sendPacket(new ExDuelUpdateUserInfo(this), getTeam().revert().name());
		}

		if (isInOlympiadMode() && isOlympiadCompStart())
		{
			if (_olympiadGame != null)
			{
				_olympiadGame.broadcastInfo(this, null, false);
			}
		}

		getListeners().onStatucUpdateBroadcasted();
	}

	private ScheduledFuture<?> _broadcastCharInfoTask;

	public class BroadcastCharInfoTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			broadcastCharInfoImpl();
			_broadcastCharInfoTask = null;
		}
	}

	@Override
	public void broadcastCharInfo()
	{
		broadcastUserInfo(false);
	}

	public void broadcastUserInfo(boolean force)
	{
		sendUserInfo(force);

		if (Config.BROADCAST_CHAR_INFO_INTERVAL == 0)
		{
			force = true;
		}

		if (force)
		{
			if (_broadcastCharInfoTask != null)
			{
				_broadcastCharInfoTask.cancel(false);
				_broadcastCharInfoTask = null;
			}
			broadcastCharInfoImpl();
			return;
		}

		if (_broadcastCharInfoTask != null)
		{
			return;
		}

		_broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new BroadcastCharInfoTask(), Config.BROADCAST_CHAR_INFO_INTERVAL);
	}

	@Override
	public L2GameServerPacket getPartyStatusUpdatePacket()
	{
		return new PartySmallWindowUpdate(this);
	}

	private int _polyNpcId;

	public void setPolyId(int polyid)
	{
		_polyNpcId = polyid;

		// teleToLocation(getLoc());
		broadcastUserInfo(true);
	}

	public boolean isPolymorphed()
	{
		return _polyNpcId != 0;
	}

	public int getPolyId()
	{
		return _polyNpcId;
	}

	private void broadcastCharInfoImpl()
	{
		L2GameServerPacket exCi = new ExBR_ExtraUserInfo(this);
		L2GameServerPacket dominion = getEvent(DominionSiegeEvent.class) != null ? new ExDominionWarStart(this) : null;
		for (Player player : World.getAroundPlayers(this))
		{
			player.sendPacket(isPolymorphed() ? new NpcInfoPoly(this) : new CharInfo(this, player), exCi);
			player.sendPacket(RelationChanged.update(player, this, player));
			if (dominion != null)
			{
				player.sendPacket(dominion);
			}

			// Synerge - Also send the relation change update of the summon/pet if he has one
			if (getPet() != null)
			{
				player.sendPacket(RelationChanged.update(player, getPet(), player));
			}
		}
	}

	public void broadcastRelationChanged()
	{
		for (Player player : World.getAroundPlayers(this))
		{
			player.sendPacket(RelationChanged.update(player, this, player));

			// Synerge - Also send the relation change update of the summon/pet if he has one
			if (getPet() != null)
			{
				player.sendPacket(RelationChanged.update(player, getPet(), player));
			}
		}
	}

	public void sendEtcStatusUpdate()
	{
		if (!isVisible())
		{
			return;
		}

		sendPacket(new EtcStatusUpdate(this));
	}

	private Future<?> _userInfoTask;

	private class UserInfoTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			sendUserInfoImpl();
			_userInfoTask = null;
		}
	}

	private void sendUserInfoImpl()
	{
		sendPacket(new UserInfo(this), new ExBR_ExtraUserInfo(this));
		DominionSiegeEvent siegeEvent = getEvent(DominionSiegeEvent.class);
		if (siegeEvent != null)
		{
			sendPacket(new ExDominionWarStart(this));
		}
	}

	public void sendUserInfo()
	{
		sendUserInfo(false);
	}

	public void sendUserInfo(boolean force)
	{
		if (!isVisible() || entering || isLogoutStarted())
		{
			return;
		}

		if ((Config.USER_INFO_INTERVAL == 0) || force)
		{
			if (_userInfoTask != null)
			{
				_userInfoTask.cancel(false);
				_userInfoTask = null;
			}
			sendUserInfoImpl();
			return;
		}

		if (_userInfoTask != null)
		{
			return;
		}

		_userInfoTask = ThreadPoolManager.getInstance().schedule(new UserInfoTask(), Config.USER_INFO_INTERVAL);
	}

	@Override
	public StatusUpdate makeStatusUpdate(int... fields)
	{
		StatusUpdate su = new StatusUpdate(getObjectId());
		for (int field : fields)
		{
			switch (field)
			{
			case StatusUpdate.CUR_HP:
				su.addAttribute(field, (int) getCurrentHp());
				break;
			case StatusUpdate.MAX_HP:
				su.addAttribute(field, getMaxHp());
				break;
			case StatusUpdate.CUR_MP:
				su.addAttribute(field, (int) getCurrentMp());
				break;
			case StatusUpdate.MAX_MP:
				su.addAttribute(field, getMaxMp());
				break;
			case StatusUpdate.CUR_LOAD:
				su.addAttribute(field, getCurrentLoad());
				break;
			case StatusUpdate.MAX_LOAD:
				su.addAttribute(field, getMaxLoad());
				break;
			case StatusUpdate.PVP_FLAG:
				su.addAttribute(field, _pvpFlag);
				break;
			case StatusUpdate.KARMA:
				su.addAttribute(field, getKarma());
				break;
			case StatusUpdate.CUR_CP:
				su.addAttribute(field, (int) getCurrentCp());
				break;
			case StatusUpdate.MAX_CP:
				su.addAttribute(field, getMaxCp());
				break;
			}
		}
		return su;
	}

	public void sendStatusUpdate(boolean broadCast, boolean withPet, int... fields)
	{
		if ((fields.length == 0) || (entering && !broadCast))
		{
			return;
		}

		StatusUpdate su = makeStatusUpdate(fields);
		if (!su.hasAttributes())
		{
			return;
		}

		List<L2GameServerPacket> packets = new ArrayList<L2GameServerPacket>(withPet ? 2 : 1);
		if (withPet && (getPet() != null))
		{
			packets.add(getPet().makeStatusUpdate(fields));
		}

		packets.add(su);

		if (!broadCast)
		{
			sendPacket(packets);
		}
		else if (entering)
		{
			broadcastPacketToOthers(packets);
		}
		else
		{
			broadcastPacket(packets);
		}
	}

	/**
	 * @return the Alliance Identifier of the L2Player.<BR>
	 * <BR>
	 */
	public int getAllyId()
	{
		return _clan == null ? 0 : _clan.getAllyId();
	}

	@Override
	public void sendPacket(IStaticPacket p)
	{
		if (p == null || !isConnected() || isPhantom() || isPacketIgnored(p.packet(this)))
		{
			return;
		}

		_connection.sendPacket(p.packet(this));
	}

	@Override
	public void sendPacket(IStaticPacket... packets)
	{
		if (packets == null || !isConnected() || isPhantom())
		{
			return;
		}

		for (IStaticPacket p : packets)
		{
			if (isPacketIgnored(p))
			{
				continue;
			}

			_connection.sendPacket(p.packet(this));
		}
	}

	private boolean isPacketIgnored(IStaticPacket p)
	{
		if ((p == null) || (_notShowBuffAnim && (/* (p.getClass() == MagicSkillUse.class) || */(p.getClass() == MagicSkillLaunched.class) || (p.getClass() == SocialAction.class))))
		{
			return true;
		}

		// if (_notShowTraders && (p.getClass() == PrivateStoreMsgBuy.class || p.getClass() == PrivateStoreMsgSell.class || p.getClass() == RecipeShopMsg.class))
		// return true;

		return false;
	}

	@Override
	public void sendPacket(List<? extends IStaticPacket> packets)
	{
		if (packets == null || !isConnected() || isPhantom())
		{
			return;
		}

		for (IStaticPacket p : packets)
		{
			_connection.sendPacket(p.packet(this));
		}
	}

	public void doInteract(GameObject target)
	{
		if ((target == null) || isActionsDisabled())
		{
			sendActionFailed();
			return;
		}
		if (target.isPlayer())
		{
			if (target.getDistance(this) <= INTERACTION_DISTANCE)
			{
				Player temp = (Player) target;

				if ((temp.getPrivateStoreType() == STORE_PRIVATE_SELL) || (temp.getPrivateStoreType() == STORE_PRIVATE_SELL_PACKAGE))
				{
					sendPacket(new PrivateStoreListSell(this, temp));
				}
				else if (temp.getPrivateStoreType() == STORE_PRIVATE_BUY)
				{
					sendPacket(new PrivateStoreListBuy(this, temp));
				}
				else if (temp.getPrivateStoreType() == STORE_PRIVATE_MANUFACTURE)
				{
					sendPacket(new RecipeShopSellList(this, temp));
				}
				// Prims - Support for buff stores
				else if (temp.getPrivateStoreType() == STORE_PRIVATE_BUFF)
				{
					OfflineBufferManager.getInstance().processBypass(this, "BuffStore bufflist " + temp.getObjectId());
				}
				sendActionFailed();
			}
			else if (getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
			{
				getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
			}
		}
		else
		{
			target.onAction(this, false);
		}
	}

	public void doAutoLootOrDrop(ItemInstance item, NpcInstance fromNpc)
	{
		boolean forceAutoloot = fromNpc.isFlying() || getReflection().isAutolootForced();

		if ((fromNpc.isRaid() || (fromNpc instanceof ReflectionBossInstance)) && !Config.AUTO_LOOT_FROM_RAIDS && !item.isHerb() && !forceAutoloot)
		{
			item.dropToTheGround(this, fromNpc);
			return;
		}

		if (!item.isAdena())
		{
			if ((item.isHerb() && !AutoLootHerbs && AutoLootOnlyAdena) || (!item.isHerb() && AutoLootOnlyAdena))
			{
				item.dropToTheGround(this, fromNpc);
				return;
			}
		}
		// Champion mob drop
		if (!item.isAdena() && fromNpc.isChampion() && Config.CHAMPION_DROP_ONLY_ADENA)
		{
			item.deleteMe();
			return;
		}
		// Herbs
		if (item.isHerb())
		{
			if (fromNpc.isChampion() && !Config.ALT_CHAMPION_DROP_HERBS)
			{
				item.deleteMe();
				return;
			}

			if (!AutoLootHerbs && !forceAutoloot)
			{
				item.dropToTheGround(this, fromNpc);
				return;
			}
			Skill[] skills = item.getTemplate().getAttachedSkills();
			if (skills.length > 0)
			{
				for (Skill skill : skills)
				{
					altUseSkill(skill, this);
					if ((getPet() != null) && getPet().isSummon() && !getPet().isDead())
					{
						getPet().altUseSkill(skill, getPet());
					}
				}
			}
			item.deleteMe();
			return;
		}

		if (!_autoLoot && !forceAutoloot)
		{
			item.dropToTheGround(this, fromNpc);
			return;
		}

		if (Config.AUTO_LOOT_PA)
		{
			if (!(_bonusExpiration != null))
			{
				item.dropToTheGround(this, fromNpc);
				sendMessage("Need buy Premium Account");
				return;
			}
		}

		// Check if the L2Player is in a Party
		if (!isInParty() || item.isCursed())
		{
			if (!pickupItem(item, Log.Pickup))
			{
				item.dropToTheGround(this, fromNpc);
				return;
			}
		}
		else
		{
			getParty().distributeItem(this, item, fromNpc);
		}

		broadcastPickUpMsg(item);
	}

	@Override
	public void doPickupItem(GameObject object)
	{
		// Check if the L2Object to pick up is a L2ItemInstance
		if (!object.isItem())
		{
			_log.warn("trying to pickup wrong target." + getTarget());
			return;
		}

		sendActionFailed();
		stopMove();

		ItemInstance item = (ItemInstance) object;

		synchronized (item)
		{
			if (!item.isVisible())
			{
				return;
			}

			// Check if me not owner of item and, if in party, not in owner party and nonowner pickup delay still active
			if (!ItemFunctions.checkIfCanPickup(this, item))
			{
				SystemMessage sm;
				if (item.getItemId() == 57)
				{
					sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
					sm.addNumber(item.getCount());
				}
				else
				{
					sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1);
					sm.addItemName(item.getItemId());
				}
				sendPacket(sm);
				return;
			}

			// Herbs
			if (item.isHerb())
			{
				Skill[] skills = item.getTemplate().getAttachedSkills();
				if (skills.length > 0)
				{
					for (Skill skill : skills)
					{
						altUseSkill(skill, this);
						if ((getPet() != null) && getPet().isSummon() && !getPet().isDead())
						{
							getPet().altUseSkill(skill, getPet());
						}
					}
				}

				broadcastPacket(new GetItem(item, getObjectId()));
				item.deleteMe();
				return;
			}

			FlagItemAttachment attachment = item.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment) item.getAttachment() : null;

			if (!isInParty() || (attachment != null) || item.isCursed())
			{
				if (pickupItem(item, Log.Pickup))
				{
					broadcastPacket(new GetItem(item, getObjectId()));
					broadcastPickUpMsg(item);
					item.pickupMe();
				}
			}
			else
			{
				getParty().distributeItem(this, item, null);
			}
		}
	}

	public boolean pickupItem(ItemInstance item, String log)
	{
		PickableAttachment attachment = item.getAttachment() instanceof PickableAttachment ? (PickableAttachment) item.getAttachment() : null;

		if (!ItemFunctions.canAddItem(this, item))
		{
			return false;
		}

		if ((item.getItemId() == ItemTemplate.ITEM_ID_ADENA) || (item.getItemId() == 6353))
		{
			Quest q = QuestManager.getQuest(255);
			if (q != null)
			{
				processQuestEvent(q.getName(), "CE" + item.getItemId(), null);
			}
		}

		sendPacket(SystemMessage2.obtainItems(item));
		getInventory().addItem(item, log);

		if (attachment != null)
		{
			attachment.pickUp(this);
		}

		sendChanges();
		return true;
	}

	public void setObjectTarget(GameObject target)
	{
		setTarget(target);
		if (target == null)
		{
			return;
		}

		if (target == getTarget())
		{
			if (target.isNpc())
			{
				NpcInstance npc = (NpcInstance) target;
				sendPacket(new MyTargetSelected(npc.getObjectId(), getLevel() - npc.getLevel()));
				sendPacket(npc.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
				sendPacket(new ValidateLocation(npc), ActionFail.STATIC);
			}
			else
			{
				sendPacket(new MyTargetSelected(target.getObjectId(), 0));
			}
		}
	}

	@Override
	public void setTarget(GameObject newTarget)
	{
		// Check if the new target is visible
		if ((newTarget != null) && !newTarget.isVisible())
		{
			newTarget = null;
		}

		// Can't target and attack festival monsters if not participant
		if ((newTarget instanceof FestivalMonsterInstance) && !isFestivalParticipant())
		{
			newTarget = null;
		}

		Party party = getParty();

		// Can't target and attack rift invaders if not in the same room
		if ((party != null) && party.isInDimensionalRift())
		{
			int riftType = party.getDimensionalRift().getType();
			int riftRoom = party.getDimensionalRift().getCurrentRoom();
			if ((newTarget != null) && !DimensionalRiftManager.getInstance().getRoom(riftType, riftRoom).checkIfInZone(newTarget.getX(), newTarget.getY(), newTarget.getZ()))
			{
				newTarget = null;
			}
		}

		GameObject oldTarget = getTarget();

		if (oldTarget != null)
		{
			if (oldTarget.equals(newTarget))
			{
				return;
			}

			// Remove the L2Player from the _statusListener of the old target if it was a L2Character
			if (oldTarget.isCreature())
			{
				((Creature) oldTarget).removeStatusListener(this);
			}

//			if(newTarget == null)
//			{
//				broadcastPacket(new TargetUnselected(this));
//			}
			broadcastPacket(new TargetUnselected(this));
		}

		if (newTarget != null)
		{
			// Add the L2Player to the _statusListener of the new target if it's a L2Character
			if (newTarget.isCreature())
			{
				((Creature) newTarget).addStatusListener(this);
			}

			// broadcastPacketToOthers(new TargetSelected(getObjectId(), newTarget.getObjectId(), getLoc()));
			broadcastPacket(new TargetSelected(getObjectId(), newTarget.getObjectId(), getLoc()));
		}

		super.setTarget(newTarget);
	}

	/**
	 * @return the active weapon instance (always equipped in the right hand).<BR>
	 * <BR>
	 */
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
	}

	/**
	 * @return the active weapon item (always equipped in the right hand).<BR>
	 * <BR>
	 */
	@Override
	public WeaponTemplate getActiveWeaponItem()
	{
		final ItemInstance weapon = getActiveWeaponInstance();

		if (weapon == null)
		{
			return getFistsWeaponItem();
		}

		return (WeaponTemplate) weapon.getTemplate();
	}

	/**
	 * @return the secondary weapon instance (always equipped in the left hand).<BR>
	 * <BR>
	 */
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
	}

	/**
	 * @return the secondary weapon item (always equipped in the left hand) or the fists weapon.<BR>
	 * <BR>
	 */
	@Override
	public WeaponTemplate getSecondaryWeaponItem()
	{
		final ItemInstance weapon = getSecondaryWeaponInstance();

		if (weapon == null)
		{
			return getFistsWeaponItem();
		}

		final ItemTemplate item = weapon.getTemplate();

		if (item instanceof WeaponTemplate)
		{
			return (WeaponTemplate) item;
		}

		return null;
	}

	public boolean isWearingArmor(ArmorType armorType)
	{
		final ItemInstance chest = getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);

		if (chest == null)
		{
			return armorType == ArmorType.NONE;
		}

		if (chest.getItemType() != armorType)
		{
			return false;
		}

		if (chest.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
		{
			return true;
		}

		final ItemInstance legs = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);

		return legs == null ? armorType == ArmorType.NONE : legs.getItemType() == armorType;
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage)
	{
		if ((attacker == null) || isDead() || (attacker.isDead() && !isDot))
		{
			return;
		}

		// Synerge - If the oly must finish then do not make more damage to the player?
		if (isPendingOlyEnd())
		{
			return;
		}

		// 5182 = Blessing of protection
		if (attacker.isPlayer() && (Math.abs(attacker.getLevel() - getLevel()) > 10))
		{
			if ((attacker.getKarma() > 0) && (getEffectList().getEffectsBySkillId(5182) != null) && !isInZone(ZoneType.SIEGE))
			{
				return;
			}

			if ((getKarma() > 0) && (attacker.getEffectList().getEffectsBySkillId(5182) != null) && !attacker.isInZone(ZoneType.SIEGE))
			{
				return;
			}
		}

		// Reduce the current HP of the L2Player
		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
	}

	private Creature lastAttacker = null;
	private long lastAttackDate = 0L;

	public Creature getLastAttacker()
	{
		return lastAttacker;
	}

	public long getLastAttackDate()
	{
		return lastAttackDate;
	}

	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp)
	{
		if (standUp)
		{
			standUp();
			if (isFakeDeath())
			{
				breakFakeDeath();
			}
		}

		// Synerge - If the oly must finish then do not make more damage to the player?
		if (isPendingOlyEnd())
		{
			return;
		}

		lastAttacker = attacker;
		lastAttackDate = System.currentTimeMillis();

		if (attacker.isPlayable())
		{
			if (!directHp && (getCurrentCp() > 0))
			{
				double cp = getCurrentCp();
				if (isInOlympiadMode())
				{
					addDamageOnOlympiad(attacker, skill, damage, cp);
				}

				if (cp >= damage)
				{
					cp -= damage;
					damage = 0;
				}
				else
				{
					damage -= cp;
					cp = 0;
				}

				setCurrentCp(cp);
			}
		}

		double hp = getCurrentHp();

		DuelEvent duelEvent = getEvent(DuelEvent.class);
		if (duelEvent != null)
		{
			if (hp < damage) // set to < instead of <= for testing reasons... possible bugs
			{
				damage = 0;
				setCurrentHp(1, true);
				duelEvent.onDie(this);
				return;
			}
		}

		if ((getPvPTeam() != 0) && (_event != null))
		{
			if (hp < damage) // set to < instead of <= for testing reasons... possible bugs
			{
				damage = 0;
				setCurrentHp(1, true);
				_event.doDie(attacker, this);
				return;
			}
		}

		if (isInOlympiadMode())
		{
			addDamageOnOlympiad(attacker, skill, damage, hp);
			if (hp - damage <= 1.5)
			{
				damage = 0;
				if (_olympiadGame.getType() != CompType.TEAM)
				{
					attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
					attacker.sendActionFailed();

					setCurrentHp(1, true);
					_olympiadGame.setWinner(getOlympiadSide() == 1 ? 2 : 1);
					_olympiadGame.endGame(20000, false, false);

					if (attacker.isPlayer())
					{
						attacker.getPlayer().setPendingOlyEnd(true);
					}
					for (Effect e : attacker.getEffectList().getAllEffects())
					{
						if (e.getEffectType() != EffectType.Cubic && !e.getSkill().isToggle())
						{
							e.exit();
						}
					}

					setPendingOlyEnd(true);
					for (Effect e : attacker.getEffectList().getAllEffects())
					{
						if (e.getEffectType() != EffectType.Cubic && !e.getSkill().isToggle())
						{
							e.exit();
						}
					}
					/*
					 * if (isDead())
					 * broadcastPacket(new Revive(this));
					 */
					return;
				}
				else if (_olympiadGame.doDie(this)) // Đ’Ń�Đµ Ń�ĐĽĐµŃ€Đ»Đ¸
				{
					_olympiadGame.setWinner(getOlympiadSide() == 1 ? 2 : 1);
					_olympiadGame.endGame(20000, false, false);
				}
			}
		}

		super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
	}

	public void addDamageOnOlympiad(Creature attacker, Skill skill, double damage, double hpcp)
	{
		if ((this != attacker) && ((skill == null) || skill.isOffensive()))
		{
			_olympiadGame.addDamage(this, Math.min(hpcp, damage));
		}
	}

	private void altDeathPenalty(Creature killer)
	{
		// Reduce the Experience of the L2Player in function of the calculated Death Penalty
		if (!Config.ALT_GAME_DELEVEL || isInZoneBattle() || isInZonePvP() || getNevitSystem().isBlessingActive())
		{
			return;
		}
		deathPenalty(killer);
	}

	public final boolean atWarWith(Player player)
	{
		return (_clan != null) && (player.getClan() != null) && (getPledgeType() != -1) && (player.getPledgeType() != -1) && _clan.isAtWarWith(player.getClan().getClanId());
	}

	public boolean atMutualWarWith(Player player)
	{
		return (_clan != null) && (player.getClan() != null) && (getPledgeType() != -1) && (player.getPledgeType() != -1) && _clan.isAtWarWith(player.getClan().getClanId()) && player.getClan().isAtWarWith(_clan.getClanId());
	}

	public final void doPurePk(Player killer)
	{
		if (killer.getKarma() > 0)
		{
			killer.getCounters().pkInARowKills++;
		}
		else
		{
			killer.getCounters().pkInARowKills = 1;
		}

		// Check if the attacker has a PK counter greater than 0
		final int pkCountMulti = Math.max(killer.getPkKills() / 2, 1);

		// Calculate the level difference Multiplier between attacker and killed L2Player
		// final int lvlDiffMulti = Math.max(killer.getLevel() / _level, 1);

		// Calculate the new Karma of the attacker : newKarma = baseKarma*pkCountMulti*lvlDiffMulti
		// Add karma to attacker and increase its PK counter
		killer.increaseKarma(Config.KARMA_MIN_KARMA * pkCountMulti); // * lvlDiffMulti);
		killer.setPkKills(killer.getPkKills() + 1);

	}

	public final void doKillInPeace(Player killer) // Check if the L2Player killed haven't Karma
	{
		if ((_karma <= 0) && ((_event == null) || _event.checkPvP(killer, this)))
		{
			if (Config.SERVICES_PK_PVP_KILL_ENABLE)
			{
				if (Config.SERVICES_PK_PVP_TIE_IF_SAME_IP)
				{
					if (getIP() != killer.getIP())
					{
						if (Config.SERVICES_ANNOUNCE_PK_ENABLED)
						{
							Announcements.getInstance().announceToAll("Player " + killer.getName() + " has pk" + killer.getTarget().getName());
						}
						ItemFunctions.addItem(killer, Config.SERVICES_PK_KILL_REWARD_ITEM, Config.SERVICES_PK_KILL_REWARD_COUNT, true, "Pk");
					}
				}
				else
				{
					if (Config.SERVICES_ANNOUNCE_PK_ENABLED)
					{
						Announcements.getInstance().announceToAll("Player " + killer.getName() + " has pk" + killer.getTarget().getName());
					}
					ItemFunctions.addItem(killer, Config.SERVICES_PK_KILL_REWARD_ITEM, Config.SERVICES_PK_KILL_REWARD_COUNT, true, "Pk");
				}
			}
			doPurePk(killer);
		}
		else // Synerge - Antifeed system
		if (AntiFeedManager.getInstance().check(killer, this))
		{
			killer.setPvpKills(killer.getPvpKills() + 1);

			getRPSCookie().runPvpTask(killer, this);
		}
	}

	public void checkAddItemToDrop(List<ItemInstance> array, List<ItemInstance> items, int maxCount)
	{
		for (int i = 0; (i < maxCount) && !items.isEmpty(); i++)
		{
			array.add(items.remove(Rnd.get(items.size())));
		}
	}

	public FlagItemAttachment getActiveWeaponFlagAttachment()
	{
		ItemInstance item = getActiveWeaponInstance();
		if ((item == null) || !(item.getAttachment() instanceof FlagItemAttachment))
		{
			return null;
		}
		return (FlagItemAttachment) item.getAttachment();
	}

	protected void doPKPVPManage(Creature killer)
	{
		FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
		if (attachment != null)
		{
			attachment.onDeath(this, killer);
		}

		if ((killer == null) || (killer == _summon) || (killer == this))
		{
			return;
		}

		if ((isInZoneBattle() || killer.isInZoneBattle()) && !Config.ZONE_PVP_COUNT)
		{
			// Synerge - Add the arena kill to the stats
//			if (killer.isPlayer())
//			{
//				addPlayerStats(Ranking.STAT_TOP_ARENA_DEATHS);
//
//				killer.getPlayer().addPlayerStats(Ranking.STAT_TOP_ARENA_KILLS);
//			}

			// Processing Karma/PKCount/PvPCount for killer
			if (killer.isPlayer()) // addon if killer is clone instance should do also this method.
			{
				final Player pk = killer.getPlayer();
				boolean war = atMutualWarWith(pk);

				// TODO [VISTALL] fix it
				if (war /*
						 * || _clan.getSiege() != null && _clan.getSiege() == pk.getClan().getSiege() && (_clan.isDefender() && pk.getClan().isAttacker() || _clan.isAttacker() &&
						 * pk.getClan().isDefender())
						 */)
				{
					ClanWar clanWar = _clan.getClanWar(pk.getClan());
					if (clanWar != null)
					{
						clanWar.onKill(pk, this);
					}
				}

				if (isOnSiegeField())
				{
					return;
				}

				Castle castle = getCastle();
				if (getPvpFlag() > 0 || war || castle != null)
				{
					pk.setPvpKills(pk.getPvpKills() + 1);
				}
				else
				{
					doKillInPeace(pk);
				}

				pk.sendChanges();
			}

			return;
		}

		if (killer instanceof Summon && ((killer = killer.getPlayer()) == null))
		{
			return;
		}

		if (isInFightClub() || killer.isPlayable() && killer.getPlayer().isInFightClub())
		{
			return;
		}

		// Processing Karma/PKCount/PvPCount for killer
		if (killer.isPlayer())
		{
			Player pk = (Player) killer;
			int repValue = (getLevel() - pk.getLevel()) >= 20 ? 2 : 1;
			boolean war = atMutualWarWith(pk);

			if ((war) && (pk.getClan().getReputationScore() > 0) && (_clan.getLevel() >= 5) && (_clan.getReputationScore() > 0) && (pk.getClan().getLevel() >= 5))
			{
				// Synerge - Antifeed system
				if (AntiFeedManager.getInstance().check(killer, this))
				{
					_clan.broadcastToOtherOnlineMembers(new SystemMessage(1782).addString(getName()).addNumber(-_clan.incReputation(-repValue, true, "ClanWar")), this);
					pk.getClan().broadcastToOtherOnlineMembers(new SystemMessage(1783).addNumber(pk.getClan().incReputation(repValue, true, "ClanWar")), pk);
				}
			}

			CastleSiegeEvent siegeEvent = getEvent(CastleSiegeEvent.class);
			CastleSiegeEvent siegeEventPk = pk.getEvent(CastleSiegeEvent.class);
			if (siegeEvent != null && (siegeEvent == siegeEventPk) && (pk.getClan() != null))
			{
				// Synerge - Antifeed system
				if (AntiFeedManager.getInstance().check(killer, this))
				{
					pk.getClan().incSiegeKills();
					if (((siegeEventPk.getSiegeClan("defenders", pk.getClan()) != siegeEvent.getSiegeClan("attackers", getClan())) || (siegeEventPk.getSiegeClan("attackers", pk.getClan()) != siegeEvent.getSiegeClan("defenders", getClan()))) && (pk.getClan().getReputationScore() > 0) && (_clan.getLevel() >= 5) && (_clan.getReputationScore() > 0) && (pk.getClan().getLevel() >= 5))
					{
						_clan.broadcastToOtherOnlineMembers(new SystemMessage(1782).addString(getName()).addNumber(-_clan.incReputation(-repValue, true, "ClanWar")), this);
						pk.getClan().broadcastToOtherOnlineMembers(new SystemMessage(1783).addNumber(pk.getClan().incReputation(repValue, true, "ClanWar")), pk);
					}
				}
			}
			DominionSiegeEvent dominionEvent = getEvent(DominionSiegeEvent.class);
			DominionSiegeEvent dominionEventPk = pk.getEvent(DominionSiegeEvent.class);
			if ((dominionEvent != null) && (dominionEventPk != null) && (pk.getClan() != null))
			{
				// Synerge - Antifeed system
				if (AntiFeedManager.getInstance().check(killer, this))
				{
					pk.getClan().incSiegeKills();
				}
			}
			FortressSiegeEvent fsiegeEvent = getEvent(FortressSiegeEvent.class);
			FortressSiegeEvent fsiegeEventPk = pk.getEvent(FortressSiegeEvent.class);
			if ((fsiegeEvent != null) && (fsiegeEvent == fsiegeEventPk) && (pk.getClan() != null) && (_clan != null) && ((fsiegeEventPk.getSiegeClan("defenders", pk.getClan()) != fsiegeEvent.getSiegeClan("attackers", getClan())) || (fsiegeEventPk.getSiegeClan("attackers", pk.getClan()) != fsiegeEvent.getSiegeClan("defenders", getClan()))) && (pk.getClan().getReputationScore() > 0) && (_clan.getLevel() >= 5) && (_clan.getReputationScore() > 0) && (pk.getClan().getLevel() >= 5))
			{
				// Synerge - Antifeed system
				if (AntiFeedManager.getInstance().check(killer, this))
				{
					_clan.broadcastToOtherOnlineMembers(new SystemMessage(1782).addString(getName()).addNumber(-_clan.incReputation(-repValue, true, "ClanWar")), this);
					pk.getClan().broadcastToOtherOnlineMembers(new SystemMessage(1783).addNumber(pk.getClan().incReputation(repValue, true, "ClanWar")), pk);
				}
			}
			ClanHallSiegeEvent chsiegeEvent = getEvent(ClanHallSiegeEvent.class);
			ClanHallSiegeEvent chsiegeEventPk = pk.getEvent(ClanHallSiegeEvent.class);
			if ((chsiegeEvent != null) && (pk.getClan() != null) && (chsiegeEvent == chsiegeEventPk) && ((chsiegeEventPk.getSiegeClan("defenders", pk.getClan()) != chsiegeEvent.getSiegeClan("attackers", getClan())) || (chsiegeEventPk.getSiegeClan("attackers", pk.getClan()) != chsiegeEvent.getSiegeClan("defenders", getClan()))) && (pk.getClan().getReputationScore() > 0) && (_clan.getLevel() >= 5) && (_clan.getReputationScore() > 0) && (pk.getClan().getLevel() >= 5))
			{
				// Synerge - Antifeed system
				if (AntiFeedManager.getInstance().check(killer, this))
				{
					_clan.broadcastToOtherOnlineMembers(new SystemMessage(1782).addString(getName()).addNumber(-_clan.incReputation(-repValue, true, "ClanWar")), this);
					pk.getClan().broadcastToOtherOnlineMembers(new SystemMessage(1783).addNumber(pk.getClan().incReputation(repValue, true, "ClanWar")), pk);
				}
			}
			if (isOnSiegeField())
			{
				// Synerge - Antifeed system
				if (AntiFeedManager.getInstance().check(killer, this))
				{
					incSiegeKills();
				}
				if (!Config.SIEGE_PVP_COUNT)
				{
					return;
				}
			}
			if ((_pvpFlag > 0) || (war) || (Config.SIEGE_PVP_COUNT) || (Config.ZONE_PVP_COUNT) || isInZonePvP())
			{
				if (Config.SERVICES_PK_PVP_KILL_ENABLE)
				{
					if (Config.SERVICES_PK_PVP_TIE_IF_SAME_IP)
					{
						if (getIP() != pk.getIP())
						{
							if (Config.SERVICES_ANNOUNCE_PVP_ENABLED)
							{
								Announcements.getInstance().announceToAll("Player " + pk.getName() + " has killed" + pk.getTarget().getName());
							}
							ItemFunctions.addItem(pk, Config.SERVICES_PVP_KILL_REWARD_ITEM, Config.SERVICES_PVP_KILL_REWARD_COUNT, true, "PvP");
						}
					}
					else
					{
						if (Config.SERVICES_ANNOUNCE_PVP_ENABLED)
						{
							Announcements.getInstance().announceToAll("Player " + pk.getName() + " has killed" + pk.getTarget().getName());
						}
						ItemFunctions.addItem(pk, Config.SERVICES_PVP_KILL_REWARD_ITEM, Config.SERVICES_PVP_KILL_REWARD_COUNT, true, "PvP");
					}
				}

				// Synerge - Antifeed system
				if (AntiFeedManager.getInstance().check(killer, this))
				{
					pk.setPvpKills(pk.getPvpKills() + 1);
					// Synerge - Killing Spree System
					addKillingSpreeKill();

					getRPSCookie().runPvpTask(pk, this);
				}
			}
			else
			{
				doKillInPeace(pk);
			}

			// Achievement system, increase pvp kills! Not sure if here is the place...
			if (getCounters().pvpKills < getPvpKills() && getHWID() != null && !getHWID().equalsIgnoreCase(pk.getHWID()))
			{
				getCounters().pvpKills = getPvpKills();
			}

			pk.sendChanges();
		}

		int karma = _karma;
		decreaseKarma(Config.KARMA_LOST_BASE);

		// under normal conditions, things are lost with the death of the guard tower or player
		// In addition, the loss of viola at things smetri can lose things in the monster smteri
		boolean isPvP = killer.isPlayable() || (killer instanceof GuardInstance);

		if ((killer.isMonster() && !Config.DROP_ITEMS_ON_DIE // if you kill the monster and viola off
		) || (isPvP // if you kill a player or the Guard and
					&& ((_pkKills < Config.MIN_PK_TO_ITEMS_DROP // number of PCs too little
					) || ((karma == 0) && Config.KARMA_NEEDED_TO_DROP)) // karma is not
		) || isFestivalParticipant() // the festival things are not lost
					|| (!killer.isMonster() && !isPvP))
		{
			return;
		}

		// No drop from GM's
		if (!Config.KARMA_DROP_GM && isGM())
		{
			return;
		}

		final int max_drop_count = isPvP ? Config.KARMA_DROP_ITEM_LIMIT : 1;

		double dropRate; // base percentage chance
		if (isPvP)
		{
			dropRate = (_pkKills * Config.KARMA_DROPCHANCE_MOD) + Config.KARMA_DROPCHANCE_BASE;
		}
		else
		{
			dropRate = Config.NORMAL_DROPCHANCE_BASE;
		}

		int dropEquipCount = 0, dropWeaponCount = 0, dropItemCount = 0;

		for (int i = 0; (i < Math.ceil(dropRate / 100)) && (i < max_drop_count); i++)
		{
			if (Rnd.chance(dropRate))
			{
				int rand = Rnd.get(Config.DROPCHANCE_EQUIPPED_WEAPON + Config.DROPCHANCE_EQUIPMENT + Config.DROPCHANCE_ITEM) + 1;
				if (rand > (Config.DROPCHANCE_EQUIPPED_WEAPON + Config.DROPCHANCE_EQUIPMENT))
				{
					dropItemCount++;
				}
				else if (rand > Config.DROPCHANCE_EQUIPPED_WEAPON)
				{
					dropEquipCount++;
				}
				else
				{
					dropWeaponCount++;
				}
			}
		}

		List<ItemInstance> drop = new ArrayList<ItemInstance>(), // total array with the results of the choice
					dropItem = new ArrayList<ItemInstance>(), dropEquip = new ArrayList<ItemInstance>(), dropWeapon = new ArrayList<ItemInstance>();

		getInventory().writeLock();
		try
		{
			for (ItemInstance item : getInventory().getItems())
			{
				if (!item.canBeDropped(this, true) || Config.KARMA_LIST_NONDROPPABLE_ITEMS.contains(item.getItemId()))
				{
					continue;
				}

				if (item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
				{
					dropWeapon.add(item);
				}
				else if ((item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR) || (item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY))
				{
					dropEquip.add(item);
				}
				else if (item.getTemplate().getType2() == ItemTemplate.TYPE2_OTHER)
				{
					dropItem.add(item);
				}
			}

			checkAddItemToDrop(drop, dropWeapon, dropWeaponCount);
			checkAddItemToDrop(drop, dropEquip, dropEquipCount);
			checkAddItemToDrop(drop, dropItem, dropItemCount);

			// Dropping items, if present
			if (drop.isEmpty())
			{
				return;
			}

			for (ItemInstance item : drop)
			{
				if (item.isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED)
				{
					item.setAugmentationId(0);
				}

				item = getInventory().removeItem(item, "Karma Drop");

				if (item.getEnchantLevel() > 0)
				{
					sendPacket(new SystemMessage(SystemMessage.DROPPED__S1_S2).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
				}
				else
				{
					sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_DROPPED_S1).addItemName(item.getItemId()));
				}

				if (killer.isPlayable() && ((Config.AUTO_LOOT && Config.AUTO_LOOT_PK) || isInFlyingTransform()))
				{
					killer.getPlayer().getInventory().addItem(item, Log.Pickup);

					killer.getPlayer().sendPacket(SystemMessage2.obtainItems(item));
				}
				else
				{
					item.dropToTheGround(this, Location.findAroundPosition(this, Config.KARMA_RANDOM_DROP_LOCATION_LIMIT));
				}
			}

			ItemLogHandler.getInstance().addLog(this, drop, ItemActionType.DROPPED_BY_KARMA);
		}
		finally
		{
			getInventory().writeUnlock();
		}
	}

	@Override
	protected void onDeath(Creature killer)
	{
		Player player = getPlayer();

		// Check for active charm of luck for death penalty
		if (player != null && !player.isPhantom())
		{
			getDeathPenalty().checkCharmOfLuck();
		}

		if (isInStoreMode())
		{
			setPrivateStoreType(Player.STORE_PRIVATE_NONE);
		}
		if (isProcessingRequest())
		{
			Request request = getRequest();
			if (isInTrade())
			{
				Player parthner = request.getOtherPlayer(this);
				sendPacket(SendTradeDone.FAIL);
				parthner.sendPacket(SendTradeDone.FAIL);
			}
			request.cancel();
		}

		if (_cubics != null)
		{
			getEffectList().stopAllSkillEffects(EffectType.Cubic);
		}

		setAgathion(0);

		boolean checkPvp = true;
		if (Config.ALLOW_CURSED_WEAPONS)
		{
			if (isCursedWeaponEquipped())
			{
				CursedWeaponsManager.getInstance().dropPlayer(this);
				checkPvp = false;
			}
			else if (killer != null && killer.isPlayer() && killer.isCursedWeaponEquipped())
			{
				CursedWeaponsManager.getInstance().increaseKills(((Player) killer).getCursedWeaponEquippedId());
				checkPvp = false;
			}
		}

		if (checkPvp)
		{
			doPKPVPManage(killer);

			altDeathPenalty(killer);
		}

		// And in the end of process notify death penalty that owner died :)
		if (!player.isPhantom())
		{
			getDeathPenalty().notifyDead(killer);
		}

		if (_event != null)
		{
			_event.doDie(killer, this);
		}

		setIncreasedForce(0);

		if (isInParty() && getParty().isInReflection() && (getParty().getReflection() instanceof DimensionalRift))
		{
			((DimensionalRift) getParty().getReflection()).memberDead(this);
		}

		stopWaterTask();

		if (!isSalvation() && isOnSiegeField() && isCharmOfCourage())
		{
			ask(new ConfirmDlg(SystemMsg.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU, 60000), new ReviveAnswerListener(this, 100, false));
			setCharmOfCourage(false);
		}

		if (getLevel() < 6)
		{
			Quest q = QuestManager.getQuest(255);
			if (q != null)
			{
				processQuestEvent(q.getName(), "CE30", null);
			}
		}

		if (isInOlympiadMode() || isOlympiadCompStart() || isOlympiadGameStart() || isPendingOlyEnd())
		{
			_log.warn("Player: " + getName() + " DIED in olympiad from: " + (killer != null ? killer.getName() : ""));
			Thread.dumpStack();
		}

		// Synerge - Call the gm event manager due to this death
		GmEventManager.getInstance().onPlayerKill(this, killer);

		// Synerge - Antifeed system
		AntiFeedManager.getInstance().setLastDeathTime(getObjectId());

		// Synerge - Killing Spree System
		resetKillingSpreeKills();

		super.onDeath(killer);
	}

	public void restoreExp()
	{
		restoreExp(100.);
	}

	public void restoreExp(double percent)
	{
		if (percent == 0)
		{
			return;
		}

		int lostexp = 0;

		String lostexps = getVar("lostexp");
		if (lostexps != null)
		{
			lostexp = Integer.parseInt(lostexps);
			unsetVar("lostexp");
		}

		if (lostexp != 0)
		{
			addExpAndSp((long) ((lostexp * percent) / 100), 0);
		}
	}

	public void deathPenalty(Creature killer)
	{
		if (killer == null || isInFightClub() || isPhantom())
		{
			return;
		}

		final boolean atwar = (killer.getPlayer() != null) && atWarWith(killer.getPlayer());

		double deathPenaltyBonus = getDeathPenalty().getLevel() * Config.ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY;
		if (deathPenaltyBonus < 2)
		{
			deathPenaltyBonus = 1;
		}
		else
		{
			deathPenaltyBonus = deathPenaltyBonus / 2;
		}

		// The death steal you some Exp: 10-40 lvl 8% loose
		double percentLost = 8.0;

		int level = getLevel();
		if (level >= 79)
		{
			percentLost = 1.0;
		}
		else if (level >= 78)
		{
			percentLost = 1.5;
		}
		else if (level >= 76)
		{
			percentLost = 2.0;
		}
		else if (level >= 40)
		{
			percentLost = 4.0;
		}

		if (Config.ALT_DEATH_PENALTY)
		{
			percentLost = (percentLost * Config.RATE_XP) + (_pkKills * Config.ALT_PK_DEATH_RATE);
		}

		if (isFestivalParticipant() || atwar)
		{
			percentLost = percentLost / 4.0;
		}

		// Calculate the Experience loss
		int lostexp = (int) Math.round(((Experience.LEVEL[level + 1] - Experience.LEVEL[level]) * percentLost) / 100);
		lostexp *= deathPenaltyBonus;

		lostexp = (int) calcStat(Stats.EXP_LOST, lostexp, killer, null);

		if (isOnSiegeField())
		{
			SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
			if (siegeEvent != null)
			{
				lostexp = 0;
			}

			if (siegeEvent != null)
			{
				List<Effect> effect = getEffectList().getEffectsBySkillId(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
				if (effect != null)
				{
					int syndromeLvl = effect.get(0).getSkill().getLevel();
					if (syndromeLvl < 5)
					{
						getEffectList().stopEffect(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
						Skill skill = SkillTable.getInstance().getInfo(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, syndromeLvl + 1);
						skill.getEffects(this, this, false, false);
					}
					else if (syndromeLvl == 5)
					{
						getEffectList().stopEffect(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
						Skill skill = SkillTable.getInstance().getInfo(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, 5);
						skill.getEffects(this, this, false, false);
					}
				}
				else
				{
					Skill skill = SkillTable.getInstance().getInfo(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, 1);
					if (skill != null)
					{
						skill.getEffects(this, this, false, false);
					}
				}
			}
		}

		if (getNevitSystem().isBlessingActive() || ((_event != null) && !_event.canLostExpOnDie()))
		{
			return;
		}

		long before = getExp();
		addExpAndSp(-lostexp, 0);
		long lost = before - getExp();

		if (lost > 0)
		{
			setVar("lostexp", String.valueOf(lost), -1);
		}
	}

	public void setRequest(Request transaction)
	{
		_request = transaction;
	}

	public Request getRequest()
	{
		return _request;
	}

	public boolean isBusy()
	{
		return isProcessingRequest() || isOutOfControl() || isInOlympiadMode() || (getTeam() != TeamType.NONE) || isInStoreMode() || isInDuel() || getMessageRefusal() || isBlockAll() || isInvisible();
	}

	public boolean isProcessingRequest()
	{
		if ((_request == null) || !_request.isInProgress())
		{
			return false;
		}
		return true;
	}

	public boolean isInTrade()
	{
		return isProcessingRequest() && getRequest().isTypeOf(L2RequestType.TRADE);
	}

	public boolean isInPost()
	{
		return isProcessingRequest() && getRequest().isTypeOf(L2RequestType.POST);
	}

	public List<L2GameServerPacket> addVisibleObject(GameObject object, Creature dropper)
	{
		if (isLogoutStarted() || (object == null) || (object.getObjectId() == getObjectId()) || !object.isVisible())
		{
			return Collections.emptyList();
		}

		return object.addPacketList(this, dropper);
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		if (getPrivateStoreType() != STORE_PRIVATE_NONE && !isInBuffStore() && forPlayer.getVarB("notraders"))
		{
			return Collections.emptyList();
		}

		// If this is fake an Observer - do not show.
		if (isInObserverMode() && (getCurrentRegion() != getObserverRegion()) && (getObserverRegion() == forPlayer.getCurrentRegion()))
		{
			return Collections.emptyList();
		}

		List<L2GameServerPacket> list = new ArrayList<>();
		if (forPlayer.getObjectId() != getObjectId())
		{
			list.add(isPolymorphed() ? new NpcInfoPoly(this) : new CharInfo(this, forPlayer));
		}

		list.add(new ExBR_ExtraUserInfo(this));

		if (isSitting() && (_sittingObject != null))
		{
			list.add(new ChairSit(this, _sittingObject));
		}

		if (getPrivateStoreType() != STORE_PRIVATE_NONE)
		{
			if (getPrivateStoreType() == STORE_PRIVATE_BUY)
			{
				list.add(new PrivateStoreMsgBuy(this));
			}
			else if ((getPrivateStoreType() == STORE_PRIVATE_SELL) || (getPrivateStoreType() == STORE_PRIVATE_SELL_PACKAGE))
			{
				list.add(new PrivateStoreMsgSell(this));
			}
			else if (getPrivateStoreType() == STORE_PRIVATE_MANUFACTURE)
			{
				list.add(new RecipeShopMsg(this));
			}
			if (forPlayer.isInZonePeace())
			{
				return list;
			}
		}

		if (isCastingNow())
		{
			Creature castingTarget = getCastingTarget();
			Skill castingSkill = getCastingSkill();
			long animationEndTime = getAnimationEndTime();
			if ((castingSkill != null) && (castingTarget != null) && castingTarget.isCreature() && (getAnimationEndTime() > 0))
			{
				list.add(new MagicSkillUse(this, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0));
			}
		}

		if (isInCombat())
		{
			list.add(new AutoAttackStart(getObjectId()));
		}

		list.add(RelationChanged.update(forPlayer, this, forPlayer));
		DominionSiegeEvent dominionSiegeEvent = getEvent(DominionSiegeEvent.class);
		if (dominionSiegeEvent != null)
		{
			list.add(new ExDominionWarStart(this));
		}

		if (isInBoat())
		{
			list.add(getBoat().getOnPacket(this, getInBoatPosition()));
		}
		else if (isMoving || isFollow)
		{
			list.add(movePacket());
		}
		return list;
	}

	public List<L2GameServerPacket> removeVisibleObject(GameObject object, List<L2GameServerPacket> list)
	{
		if (isLogoutStarted() || (object == null) || (object.getObjectId() == getObjectId()))
		{
			return null;
		}

		List<L2GameServerPacket> result = list == null ? object.deletePacketList() : list;

		getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, object);
		return result;
	}

	public void levelSet(int levels)
	{
		if (levels > 0)
		{
			sendPacket(Msg.YOU_HAVE_INCREASED_YOUR_LEVEL);
			broadcastPacket(new SocialAction(getObjectId(), SocialAction.LEVEL_UP));

			setCurrentHpMp(getMaxHp(), getMaxMp());
			setCurrentCp(getMaxCp());

			getListeners().onLevelIncreased();

			Quest q = QuestManager.getQuest(255);
			if (q != null)
			{
				processQuestEvent(q.getName(), "CE40", null);
				processQuestEvent(q.getName(), "OpenClassMaster", null);
			}
		}
		else if (levels < 0)
		{
			if (Config.ALT_REMOVE_SKILLS_ON_DELEVEL)
			{
				checkSkills();
			}
		}

		// Recalculate the party level
		if (isInParty())
		{
			getParty().recalculatePartyData();
		}

		if (_clan != null)
		{
			_clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(this));
		}

		if (_matchingRoom != null)
		{
			_matchingRoom.broadcastPlayerUpdate(this);
		}

		// Give Expertise skill of this level
		rewardSkills(true);
	}

	public void checkSkills()
	{
		for (Skill sk : getAllSkillsArray())
		{
			SkillTreeTable.checkSkill(this, sk);
		}
	}

	public void startTimers()
	{
		startAutoSaveTask();
		startPcBangPointsTask();
		startHourlyTask();
		PremiumStart.getInstance().start(this);
		// startBonusTask();
		getInventory().startTimers();
		resumeQuestTimers();
	}

	public void stopAllTimers()
	{
		setAgathion(0);
		stopWaterTask();
		stopBonusTask();
		stopHourlyTask();
		stopKickTask();
		stopVitalityTask();
		stopPcBangPointsTask();
		stopAutoSaveTask();
		stopRecomBonusTask(true);
		getInventory().stopAllTimers();
		stopQuestTimers();
		getNevitSystem().stopTasksOnLogout();
	}

	@Override
	public Summon getPet()
	{
		return _summon;
	}

	public void setPet(Summon summon)
	{
		boolean isPet = false;
		if ((_summon != null) && _summon.isPet())
		{
			isPet = true;
		}
		unsetVar("pet");
		_summon = summon;
		autoShot();
		if (summon == null)
		{
			if (isPet)
			{
				if (isLogoutStarted())
				{
					if (getPetControlItem() != null)
					{
						setVar("pet", String.valueOf(getPetControlItem().getObjectId()), -1);
					}
				}
				setPetControlItem(null);
			}
			getEffectList().stopEffect(4140);
		}
	}

	public void scheduleDelete()
	{
		long time = 0;

		// Synerge - Use noCarrier only on epic zones, in any other place as it causes problems
		if (Config.SERVICES_ENABLE_NO_CARRIER && isInZone(ZoneType.epic))
		{
			time = NumberUtils.toInt(getVar("noCarrier"), Config.SERVICES_NO_CARRIER_DEFAULT_TIME);
		}
		scheduleDelete(time * 1000);
	}

	/**
	 * Removes the character of the world in the specified time, if at the time of the expiry of the time it will not be connected. <br>
	 * <br>
	 * a minute to make him invulnerable. <br>
	 * make a binding time to the context for areas with a time limit to leave the game on all the time in the zone. <br>
	 * <br>
	 * @param time
	 */
	public void scheduleDelete(long time)
	{
		if (isInOfflineMode())
		{
			return;
		}
		broadcastCharInfo();

		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				if (!isConnected())
				{
					prepareToLogout();
					deleteMe();
				}
			}
		}, time);
	}

	@Override
	protected void onDelete()
	{
		super.onDelete();

		// Remove the fake at the observation point
		WorldRegion observerRegion = getObserverRegion();
		if (observerRegion != null)
		{
			observerRegion.removeObject(this);
		}

		// Send friendlists to friends that this player has logged off
		_friendList.notifyFriends(false);

		bookmarks.clear();

		_inventory.clear();
		_warehouse.clear();
		_summon = null;
		_arrowItem = null;
		_fistsWeaponItem = null;
		_chars = null;
		_enchantScroll = null;
		_lastNpc = HardReferences.emptyRef();
		_observerRegion = null;
	}

	public void setTradeList(List<TradeItem> list)
	{
		_tradeList = list;
	}

	public List<TradeItem> getTradeList()
	{
		return _tradeList;
	}

	public String getSellStoreName()
	{
		return _sellStoreName;
	}

	public void setSellStoreName(String name)
	{
		_sellStoreName = Strings.stripToSingleLine(name);
	}

	public void setSellList(boolean packageSell, List<TradeItem> list)
	{
		if (packageSell)
		{
			_packageSellList = list;
		}
		else
		{
			_sellList = list;
		}
	}

	public List<TradeItem> getSellList()
	{
		return getSellList(_privatestore == STORE_PRIVATE_SELL_PACKAGE);
	}

	public List<TradeItem> getSellList(boolean packageSell)
	{
		return packageSell ? _packageSellList : _sellList;
	}

	public String getBuyStoreName()
	{
		return _buyStoreName;
	}

	public void setBuyStoreName(String name)
	{
		_buyStoreName = Strings.stripToSingleLine(name);
	}

	public void setBuyList(List<TradeItem> list)
	{
		_buyList = list;
	}

	public List<TradeItem> getBuyList()
	{
		return _buyList;
	}

	public void setManufactureName(String name)
	{
		_manufactureName = Strings.stripToSingleLine(name);
	}

	public String getManufactureName()
	{
		return _manufactureName;
	}

	public List<ManufactureItem> getCreateList()
	{
		return _createList;
	}

	public void setCreateList(List<ManufactureItem> list)
	{
		_createList = list;
	}

	public void setPrivateStoreType(int type)
	{
		_privatestore = type;
		if (type != STORE_PRIVATE_NONE)
		{
			setVar("storemode", String.valueOf(type), -1);
		}
		else
		{
			for (TradeItem item : _sellList)
			{
				AuctionManager.getInstance().removeStore(this, item.getAuctionId());
			}
			unsetVar("storemode");
		}
	}

	public boolean isInStoreMode()
	{
		return _privatestore != STORE_PRIVATE_NONE;
	}

	public boolean isInBuffStore()
	{
		return (getPrivateStoreType() == STORE_PRIVATE_BUFF);
	}

	public int getPrivateStoreType()
	{
		return _privatestore;
	}

	/**
	 * Set the _clan object, _clanId, _clanLeader Flag and title of the L2Player.<BR>
	 * <BR>
	 * @param clan the clat to set
	 */
	public void setClan(Clan clan)
	{
		if ((_clan != clan) && (_clan != null))
		{
			unsetVar("canWhWithdraw");
		}

		Clan oldClan = _clan;
		if ((oldClan != null) && (clan == null))
		{
			// Remove clan skills
			for (Skill skill : oldClan.getSkills())
			{
				removeSkill(skill, false);
			}

			// Also remove subunit skills
			for (SubUnit subUnit : oldClan.getAllSubUnits())
			{
				for (Skill sk : subUnit.getSkills())
				{
					removeSkill(sk, false);
				}
			}
		}

		_clan = clan;

		if (clan == null)
		{
			_pledgeType = Clan.SUBUNIT_NONE;
			_pledgeClass = 0;
			_powerGrade = 0;
			_apprentice = 0;
			_inventory.validateItems();

			if (getEvent(CastleSiegeEvent.class) != null)
			{
				removeEvent(getEvent(CastleSiegeEvent.class));
			}
			return;
		}

		if (!clan.isAnyMember(getObjectId()))
		{
			clan.restartMembers();
		}

		if (!clan.isAnyMember(getObjectId()))
		{
			setClan(null);
			if (!_noble)
			{
				setTitle("");
			}
		}
	}

	@Override
	public Clan getClan()
	{
		return _clan;
	}

	public SubUnit getSubUnit()
	{
		return _clan == null ? null : _clan.getSubUnit(_pledgeType);
	}

	public ClanHall getClanHall()
	{
		int id = _clan != null ? _clan.getHasHideout() : 0;
		return ResidenceHolder.getInstance().getResidence(ClanHall.class, id);
	}

	public Castle getCastle()
	{
		int id = _clan != null ? _clan.getCastle() : 0;
		return ResidenceHolder.getInstance().getResidence(Castle.class, id);
	}

	public Fortress getFortress()
	{
		int id = _clan != null ? _clan.getHasFortress() : 0;
		return ResidenceHolder.getInstance().getResidence(Fortress.class, id);
	}

	public Alliance getAlliance()
	{
		return _clan == null ? null : _clan.getAlliance();
	}

	public boolean isClanLeader()
	{
		return (_clan != null) && (getObjectId() == _clan.getLeaderId());
	}

	public boolean isAllyLeader()
	{
		return (getAlliance() != null) && (getAlliance().getLeader().getLeaderId() == getObjectId());
	}

	@Override
	public void reduceArrowCount()
	{
		sendPacket(SystemMsg.YOU_CAREFULLY_NOCK_AN_ARROW);
		if ((_arrowItem != null) && !Config.ALLOW_ARROW_INFINITELY)
		{
			if (!getInventory().destroyItemByObjectId(getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1L, null, null))
			{
				getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
				_arrowItem = null;
			}
		}
	}

	/**
	 * Equip arrows needed in left hand and send a Server->Client packet ItemList to the L2Player then return True.
	 * @return
	 */
	protected boolean checkAndEquipArrows()
	{
		// Check if nothing is equipped in left hand
		if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND) == null)
		{
			ItemInstance activeWeapon = getActiveWeaponInstance();
			if (activeWeapon != null)
			{
				if (activeWeapon.getItemType() == WeaponType.BOW)
				{
					_arrowItem = getInventory().findArrowForBow(activeWeapon.getTemplate());
				}
				else if (activeWeapon.getItemType() == WeaponType.CROSSBOW)
				{
					getInventory().findArrowForCrossbow(activeWeapon.getTemplate());
				}
			}

			// Equip arrows needed in left hand
			if (_arrowItem != null)
			{
				getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, _arrowItem);
			}
		}
		else
		{
			// Get the L2ItemInstance of arrows equipped in left hand
			_arrowItem = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		}

		return _arrowItem != null;
	}

	public void setUptime(long time)
	{
		_uptime = time;
	}

	public long getUptime()
	{
		return _uptime == 0L ? 0L : System.currentTimeMillis() - _uptime;
	}

	public boolean isInParty()
	{
		return _party != null;
	}

	public void setParty(Party party)
	{
		_party = party;
	}

	public void joinParty(Party party)
	{
		if (party != null)
		{
			party.addPartyMember(this);
			for (Player member : party.getMembers())
			{
				if (PartyMatchingBBSManager.getInstance().partyMatchingPlayersList.contains(member))
				{
					PartyMatchingBBSManager.getInstance().partyMatchingPlayersList.remove(member);
					PartyMatchingBBSManager.getInstance().partyMatchingDescriptionList.remove(member.getObjectId());
					member.sendMessage("Now that you have found a party, you've been removed from the Party Matching list.");
				}
			}

		}
	}

	public void leaveParty()
	{
		if (isInParty())
		{
			_party.removePartyMember(this, false, false);
		}
	}

	public Party getParty()
	{
		return _party;
	}

	public void setLastPartyPosition(Location loc)
	{
		_lastPartyPosition = loc;
	}

	public Location getLastPartyPosition()
	{
		return _lastPartyPosition;
	}

	public boolean isGM()
	{
		return _playerAccess == null ? false : _playerAccess.IsGM;
	}

	public void setAccessLevel(int level)
	{
		_accessLevel = level;
	}

	@Override
	public int getAccessLevel()
	{
		return _accessLevel;
	}

	public void setPlayerAccess(PlayerAccess pa)
	{
		if (pa != null)
		{
			_playerAccess = pa;
		}
		else
		{
			_playerAccess = new PlayerAccess();
		}

		setAccessLevel(isGM() || _playerAccess.Menu ? 100 : 0);
	}

	public PlayerAccess getPlayerAccess()
	{
		return _playerAccess;
	}

	@Override
	public double getLevelMod()
	{
		return (89. + getLevel()) / 100.0;
	}

	/**
	 * Update Stats of the Player client side by sending Server->Client packet UserInfo/StatusUpdate to this L2Player and CharInfo/StatusUpdate to all players around (broadcast).<BR>
	 * <BR>
	 */
	@Override
	public void updateStats()
	{
		if (entering || isLogoutStarted())
		{
			return;
		}

		refreshOverloaded();
		if (Config.EXPERTISE_PENALTY)
		{
			refreshExpertisePenalty();
		}
		super.updateStats();
	}

	@Override
	public void sendChanges()
	{
		if (!isPhantom() && entering || isLogoutStarted())
		{
			return;
		}
		super.sendChanges();
	}

	/**
	 * Send a Server->Client StatusUpdate packet with Karma to the L2Player and all L2Player to inform (broadcast).
	 * @param flagChanged
	 */
	public void updateKarma(boolean flagChanged)
	{
		sendStatusUpdate(true, true, StatusUpdate.KARMA);
		if (flagChanged)
		{
			broadcastRelationChanged();
		}
	}

	public boolean isOnline()
	{
		return _isOnline;
	}

	public void setIsOnline(boolean isOnline)
	{
		_isOnline = isOnline;
	}

	public void setOnlineStatus(boolean isOnline)
	{
		_isOnline = isOnline;
		updateOnlineStatus();
	}

	public void updateOnlineStatus()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE characters SET online=?, lastAccess=? WHERE obj_id=?"))
		{
			statement.setInt(1, (isOnline() && !isInOfflineMode()) || (isInOfflineMode() && Config.SHOW_OFFLINE_MODE_IN_ONLINE) ? 1 : 0);
			statement.setLong(2, System.currentTimeMillis() / 1000L);
			statement.setInt(3, getObjectId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error while updating Online Status", e);
		}
	}

	/**
	 * Decrease Karma of the L2Player and Send it StatusUpdate packet with Karma and PvP Flag (broadcast).
	 * @param add_karma
	 */
	public void increaseKarma(long add_karma)
	{
		if (isInTournament())
		{
			return;
		}

		boolean flagChanged = _karma == 0;
		long newKarma = _karma + add_karma;

		if (newKarma > Integer.MAX_VALUE)
		{
			newKarma = Integer.MAX_VALUE;
		}

		if ((_karma == 0) && (newKarma > 0))
		{
			if (_pvpFlag > 0)
			{
				_pvpFlag = 0;
				if (_PvPRegTask != null)
				{
					_PvPRegTask.cancel(true);
					_PvPRegTask = null;
				}
				sendStatusUpdate(true, true, StatusUpdate.PVP_FLAG);
			}

			_karma = (int) newKarma;
		}
		else
		{
			_karma = (int) newKarma;
		}

		if (getCounters().highestKarma < newKarma)
		{
			getCounters().highestKarma = (int) newKarma;
		}

		updateKarma(flagChanged);
	}

	/**
	 * Decrease Karma of the L2Player and Send it StatusUpdate packet with Karma and PvP Flag (broadcast).
	 * @param i
	 */
	public void decreaseKarma(int i)
	{
		boolean flagChanged = _karma > 0;
		_karma -= i;
		if (_karma <= 0)
		{
			_karma = 0;
			updateKarma(flagChanged);
		}
		else
		{
			updateKarma(false);
		}
	}

	/**
	 * Create a new L2Player and add it in the characters table of the database.<BR>
	 * <BR>
	 * <p/>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Create a new L2Player with an account name</li>
	 * <li>Set the name, the Hair Style, the Hair Color and the Face type of the L2Player</li>
	 * <li>Add the player in the characters table of the database</li><BR>
	 * <BR>
	 * @param classId
	 * @param sex
	 * @param accountName The name of the L2Player
	 * @param name The name of the L2Player
	 * @param hairStyle The hair style Identifier of the L2Player
	 * @param hairColor The hair color Identifier of the L2Player
	 * @param face The face type Identifier of the L2Player
	 * @return The L2Player added to the database or null
	 */
	public static Player create(int classId, int sex, String accountName, String name, int hairStyle, int hairColor, int face)
	{
		PlayerTemplate template = CharTemplateHolder.getInstance().getTemplate(classId, sex != 0);

		// Create a new L2Player with an account name
		Player player = new Player(IdFactory.getInstance().getNextId(), template, accountName);

		player.setName(name);
		player.setTitle("");
		player.setHairStyle(hairStyle);
		player.setHairColor(hairColor);
		player.setFace(face);
		player.setCreateTime(System.currentTimeMillis());

		// Add the player in the characters table of the database
		if (!CharacterDAO.getInstance().insert(player))
		{
			return null;
		}

		return player;
	}

	/**
	 * Retrieve a L2Player from the characters table of the database and add it in _allObjects of the L2World
	 * @param objectId
	 * @return The L2Player loaded from the database
	 */
	public static Player restore(int objectId)
	{
		Player player = null;

		try (Connection con = DatabaseFactory.getInstance().getConnection(); Statement statement = con.createStatement(); Statement statement2 = con.createStatement(); ResultSet rset = statement.executeQuery("SELECT * FROM `characters` WHERE `obj_Id`=" + objectId + " LIMIT 1"); ResultSet rset2 = statement2.executeQuery("SELECT `class_id` FROM `character_subclasses` WHERE `char_obj_id`=" + objectId + " AND `isBase`=1 LIMIT 1"))
		{
			if (rset.next() && rset2.next())
			{
				final int classId = rset2.getInt("class_id");
				final boolean female = rset.getInt("sex") == 1;
				final PlayerTemplate template = CharTemplateHolder.getInstance().getTemplate(classId, female);

				player = new Player(objectId, template);

				player.setIsPhantom(false, false);

				player.loadVariables(con);
				player.loadInstanceReuses(con);
				player.loadPremiumItemList(con);
				player.bookmarks.setCapacity(rset.getInt("bookmarks"));
				player.bookmarks.restore(con);
				player._friendList.restore(con);
				player._postFriends = CharacterPostFriendDAO.select(player, con);
				CharacterGroupReuseDAO.select(player, con);

				player._baseClass = classId;
				player._login = rset.getString("account_name");
				player.setName(rset.getString("char_name"));

				player._face = rset.getInt("face");
				player._hairStyle = rset.getInt("hairStyle");
				player._hairColor = rset.getInt("hairColor");
				player.setHeading(0);

				player.setKarma(rset.getInt("karma"));
				player._pvpKills = rset.getInt("pvpkills");
				player._pkKills = rset.getInt("pkkills");
				player._raidKills = rset.getInt("raidkills");
				player._eventKills = rset.getInt("eventKills");
				player._siegeKills = rset.getInt("siege_kills");
				player._olyWins = rset.getInt("oly_wins");

				player.setLeaveClanTime(rset.getLong("leaveclan") * 1000L);
				if (player.getLeaveClanTime() > 0 && player.canJoinClan())
				{
					player.setLeaveClanTime(0);
				}
				player.setDeleteClanTime(rset.getLong("deleteclan") * 1000L);
				if (player.getDeleteClanTime() > 0 && player.canCreateClan())
				{
					player.setDeleteClanTime(0);
				}
				player.setNoChannel(rset.getLong("nochannel") * 1000L);
				if ((player._NoChannel > 0L) && (player.getNoChannelRemained() < 0L))
				{
					player.setNoChannel(0L);
				}
				player._hwidLock = rset.getString("hwid_lock");
				if (player._hwidLock != null && player._hwidLock.isEmpty())
				{
					player._hwidLock = null;
				}

				if (!player.isInOfflineMode() || !player.isInBuffStore())
				{
					player.setOnlineTime(rset.getLong("onlinetime") * 1000L);
				}

				player._forumLogin = rset.getString("forum_login");
				if (player._forumLogin != null && !player._forumLogin.isEmpty())
				{
					final ForumMember member = ForumMembersHolder.getInstance().getMemberByName(player._forumLogin, null);
					if (member != null && member.getWarningLevel() < 100)
					{
						player._forumMember = member;
					}
				}

				player._facebookProfile = FacebookProfilesHolder.getInstance().getProfileById(rset.getString("facebook_id"));

				final int clanId = rset.getInt("clanid");
				if (clanId > 0)
				{
					player.setClan(ClanTable.getInstance().getClan(clanId));
					player._pledgeType = rset.getInt("pledge_type");
					player._powerGrade = rset.getInt("pledge_rank");
					player._lvlJoinedAcademy = rset.getInt("lvl_joined_academy");
					player._apprentice = rset.getInt("apprentice");
				}

				player._createTime = rset.getLong("createtime") * 1000L;
				player._deleteTimer = rset.getInt("deletetime");

				SchemeBufferInstance.loadSchemes(player, con);

				player.setTitle(rset.getString("title"));

				if (player.getVar("titlecolor") != null)
				{
					player.setTitleColor(Integer.decode("0x" + player.getVar("titlecolor")).intValue());
				}

				if (player.getVar("namecolor") == null)
				{
					if (player.isGM())
					{
						player.setNameColor(Config.GM_NAME_COLOUR);
					}
					else if ((player._clan != null) && (player._clan.getLeaderId() == player.getObjectId()))
					{
						player.setNameColor(Config.CLANLEADER_NAME_COLOUR);
					}
					else
					{
						player.setNameColor(Config.NORMAL_NAME_COLOUR);
					}
				}
				else
				{
					player.setNameColor(Integer.decode("0x" + player.getVar("namecolor")).intValue());
				}

				if (Config.AUTO_LOOT_INDIVIDUAL)
				{
					player._autoLoot = player.getVarB("AutoLoot", Config.AUTO_LOOT);
					player.AutoLootHerbs = player.getVarB("AutoLootHerbs", Config.AUTO_LOOT_HERBS);
					player.AutoLootOnlyAdena = player.getVarB("AutoLootOnlyAdena", Config.AUTO_LOOT_ONLY_ADENA);
				}

				player._fistsWeaponItem = player.findFistsWeaponItem(classId);
				player._uptime = System.currentTimeMillis();
				player._lastAccess = rset.getLong("lastAccess");

				player.setRecomHave(rset.getInt("rec_have"));
				player.setRecomLeft(rset.getInt("rec_left"));
				player._recomBonusTime = rset.getInt("rec_bonus_time");

				if (player.getVar("recLeftToday") != null)
				{
					player._recomLeftToday = Integer.parseInt(player.getVar("recLeftToday"));
				}
				else
				{
					player._recomLeftToday = 0;
				}

				player._nevitSystem.setPoints(rset.getInt("hunt_points"), rset.getInt("hunt_time"));

				player.setKeyBindings(rset.getBytes("key_bindings"));
				player._pcBangPoints = rset.getInt("pcBangPoints");

				player._fame = rset.getInt("fame");

				player.restoreRecipeBook(con);

				if (Config.ENABLE_OLYMPIAD)
				{
					player._hero = Hero.getInstance().isHero(player.getObjectId());
					player._noble = Olympiad.isNoble(player.getObjectId());
				}

				player.setHwidLock(rset.getString("hwid_lock"));

				player.updatePledgeClass();

				int reflection = 0;

				if (player.isInJail())
				{
					// randomly spawn in prison
					player.setXYZ(Rnd.get(-114936, -114136), Rnd.get(-249768, -248952), -2984);

					long period = player.getVarTimeToExpire("jailed");
					player.updateNoChannel(period);
					player.sitDown(null);
					player.block();

					if (period != -1)
					{
						player._unjailTask = ThreadPoolManager.getInstance().schedule(new UnJailTask(player, true), period);
					}
				}
				else if (player.getVar("jailedFrom") != null)
				{
					String[] re = player.getVar("jailedFrom").split(";");

					player.setXYZ(Integer.parseInt(re[0]), Integer.parseInt(re[1]), Integer.parseInt(re[2]));
					player.setReflection(re.length > 3 ? Integer.parseInt(re[3]) : 0);

					player.unsetVar("jailedFrom");
				}
				else
				{
					player.setXYZ(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));
					String ref = player.getVar("reflection");
					if (ref != null && Integer.parseInt(ref) != ReflectionManager.JAIL.getId())
					{
						reflection = Integer.parseInt(ref);
						if (reflection > 0) // not the portal back of the GC Parnassus, Gila
						{
							String back = player.getVar("backCoords");
							if (back != null)
							{
								player.setLoc(Location.parseLoc(back));
								player.unsetVar("backCoords");
							}
							reflection = 0;
						}
					}
				}

				player.setReflection(reflection);

				EventHolder.getInstance().findEvent(player);

				Quest.restoreQuestStates(player, con);

				player._inventory.restore();

				player.isntAfk();

				restoreCharSubClasses(player, con);

				// 4 points per minute based casino
				player.setVitality(rset.getInt("vitality") + (int) (((System.currentTimeMillis() / 1000L) - rset.getLong("lastAccess")) / 15.));

				try
				{
					String var = player.getVar("ExpandInventory");
					if (var != null)
					{
						player._expandInventory = Integer.parseInt(var);
					}
				}
				catch (NumberFormatException e)
				{
					_log.error("Error while restoring Expand Inventory ", e);
				}

				try
				{
					String var = player.getVar("ExpandWarehouse");
					if (var != null)
					{
						player._expandWarehouse = Integer.parseInt(var);
					}
				}
				catch (NumberFormatException e)
				{
					_log.error("Error while restoring Expand Warehouse", e);
				}

				try
				{
					String var = player.getVar(NO_ANIMATION_OF_CAST_VAR);
					if (var != null)
					{
						player._notShowBuffAnim = Boolean.parseBoolean(var);
					}
				}
				catch (RuntimeException e)
				{
					_log.error("Error while restoring No Animation Player Config ", e);
				}

				try
				{
					String var = player.getVar(NO_TRADERS_VAR);
					if (var != null)
					{
						player._notShowTraders = Boolean.parseBoolean(var);
					}
				}
				catch (RuntimeException e)
				{
					_log.error("Error while restoring Not Show Traders Player Config", e);
				}

				try
				{
					String var = player.getVar("pet");
					if (var != null)
					{
						player.setPetControlItem(Integer.parseInt(var));
					}
				}
				catch (NumberFormatException e)
				{
					_log.error("Error while restoring Pet Control Item ", e);
				}

				try
				{
					String var = player.getVar("isPvPevents");
					if (var != null)
					{
						player.unsetVar("isPvPevents");
					}
				}
				catch (RuntimeException e)
				{
					_log.error("Error while restoring some strange thing ", e);
				}

				try (PreparedStatement statement3 = con.prepareStatement("SELECT obj_Id, char_name FROM characters WHERE account_name=? AND obj_Id!=?"))
				{
					statement3.setString(1, player._login);
					statement3.setInt(2, objectId);
					try (ResultSet rset3 = statement3.executeQuery())
					{
						while (rset3.next())
						{
							final Integer charId = rset3.getInt("obj_Id");
							final String charName = rset3.getString("char_name");
							player._chars.put(charId, charName);
						}
					}
				}

				// if (!player.isGM())
				{
					ArrayList<Zone> zones = new ArrayList<>();

					World.getZones(zones, player.getLoc(), player.getReflection());

					if (!zones.isEmpty())
					{
						for (Zone zone : zones)
						{
							if (zone.getType() == ZoneType.no_restart)
							{
								if (((System.currentTimeMillis() / 1000L) - player.getLastAccess()) > zone.getRestartTime())
								{
									player.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.EnterWorld.TeleportedReasonNoRestart", player));
									player.setLoc(TeleportUtils.getRestartLocation(player, RestartType.TO_VILLAGE));
								}
							}
							else if (zone.getType() == ZoneType.SIEGE)
							{
								SiegeEvent<?, ?> siegeEvent = player.getEvent(SiegeEvent.class);
								if (siegeEvent != null)
								{
									player.setLoc(siegeEvent.getEnterLoc(player));
								}
								else
								{
									Residence r = ResidenceHolder.getInstance().getResidence(zone.getParams().getInteger("residence"));
									player.setLoc(r.getNotOwnerRestartPoint(player));
								}
							}
						}
					}

					if (DimensionalRiftManager.getInstance().checkIfInRiftZone(player.getLoc(), false))
					{
						player.setLoc(DimensionalRiftManager.getInstance().getRoom(0, 0).getTeleportCoords());
					}
				}

				player.restoreBlockList(con);
				player._macroses.restore(con);

				player.refreshExpertisePenalty();
				player.refreshOverloaded();

				player._warehouse.restore();
				player._freight.restore();

				player.restoreTradeList();
				if (player.getVar("storemode") != null)
				{
					player.setPrivateStoreType(Integer.parseInt(player.getVar("storemode")));
					player._isSitting = true;

					if (Config.AUCTION_PRIVATE_STORE_AUTO_ADDED)
					{
						if (player._privatestore == STORE_PRIVATE_SELL)
						{
							if (player.getVar("offline") != null)
							{
								AuctionManager.getInstance().removePlayerStores(player);
							}
							for (TradeItem item : player._sellList)
							{
								ItemInstance itemToSell = player._inventory.getItemByItemId(item.getItemId());

								// Synerge - Dont add potions to the auction house
								if (itemToSell.getItemType() == EtcItemType.POTION)
								{
									continue;
								}

								Auction a = AuctionManager.getInstance().addNewStore(player, itemToSell, item.getOwnersPrice(), item.getCount());
								item.setAuctionId(a.getAuctionId());
							}
						}
					}
				}

				try
				{
					String var = player.getVar("FightClubRate");
					if (var != null)
					{
						RestoreFightClub(player);
					}
				}
				catch (RuntimeException e)
				{
					_log.error("Error while restoring FightClubRate", e);
				}

				try
				{
					String var = player.getVar("EnItemOlyRec");
					if (Config.OLY_ENCH_LIMIT_ENABLE && (var != null))
					{
						FixEnchantOlympiad.restoreEnchantItemsOly(player);
					}
				}
				catch (RuntimeException e)
				{
					_log.error("Error while restoring EnItemOlyRec", e);
				}
				player.updateKetraVarka();
				player.updateRam();
				player.checkRecom();
				if (player.isCursedWeaponEquipped())
				{
					player.restoreCursedWeapon();
				}

				if (Config.ENABLE_PLAYER_COUNTERS)
				{
					player.getCounters().load();

					if (Config.ENABLE_ACHIEVEMENTS)
					{
						player.loadAchivements();
					}
				}
			}
		}
		catch (IllegalArgumentException | SQLException e)
		{
			_log.error("Could not restore char data! ", e);
		}

		// Synerge - Now we must get all the stats from the alternative table, using the ranking values
//		try (Connection conEl = DatabaseFactory.getInstance().getConnection();
//			PreparedStatement statementEl = conEl.prepareStatement("SELECT variable,value FROM character_stats WHERE charId=?"))
//		{
//			statementEl.setInt(1, objectId);
//			try (ResultSet rsetEl = statementEl.executeQuery())
//			{
//				while (rsetEl.next())
//				{
//					// Obtengo dinamicamente cada ranking perteneciente a esta tabla con su valor correspondiente
//					for (Ranking top : Ranking.values())
//					{
//						if (player != null && top.getDbName().equalsIgnoreCase(rsetEl.getString("variable")) && top.getDbLocation().equalsIgnoreCase("character_stats"))
//						{
//							player.getStats().setPlayerStats(top, rsetEl.getLong("value"));
//						}
//					}
//				}
//			}
//		}
//		catch (Exception e)
//		{
//			_log.error("Failed loading character stats", e);
//		}
		return player;
	}

	private void loadPremiumItemList(Connection con)
	{
		try (PreparedStatement statement = con.prepareStatement("SELECT itemNum, itemId, itemCount, itemSender FROM character_premium_items WHERE charId=?"))
		{
			statement.setInt(1, getObjectId());

			try (ResultSet rs = statement.executeQuery())
			{
				while (rs.next())
				{
					int itemNum = rs.getInt("itemNum");
					int itemId = rs.getInt("itemId");
					long itemCount = rs.getLong("itemCount");
					String itemSender = rs.getString("itemSender");
					PremiumItem item = new PremiumItem(itemId, itemCount, itemSender);
					_premiumItems.put(itemNum, item);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while loading Premium Item List for Id " + getObjectId(), e);
		}
	}

	public void updatePremiumItem(int itemNum, long newcount)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE character_premium_items SET itemCount=? WHERE charId=? AND itemNum=?"))
		{
			statement.setLong(1, newcount);
			statement.setInt(2, getObjectId());
			statement.setInt(3, itemNum);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error while updating Premium Items", e);
		}
	}

	public void deletePremiumItem(int itemNum)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM character_premium_items WHERE charId=? AND itemNum=?"))
		{
			statement.setInt(1, getObjectId());
			statement.setInt(2, itemNum);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error while deleting Premium Item", e);
		}
	}

	public Map<Integer, PremiumItem> getPremiumItemList()
	{
		return _premiumItems;
	}

	/**
	 * Update L2Player stats in the characters table of the database.
	 * @param fast
	 */
	public void store(boolean fast)
	{
		if (!_storeLock.tryLock())
		{
			return;
		}

		try
		{
			try (Connection con = DatabaseFactory.getInstance().getConnection();
						PreparedStatement statement = con.prepareStatement(//
									"UPDATE characters SET face=?,hairStyle=?,hairColor=?,x=?,y=?,z=?" + //
												",karma=?,pvpkills=?,pkkills=?,rec_have=?,rec_left=?,rec_bonus_time=?,hunt_points=?,hunt_time=?,clanid=?,deletetime=?," + //
												"title=?,accesslevel=?,online=?,leaveclan=?,deleteclan=?,nochannel=?," + //
												"onlinetime=?,pledge_type=?,pledge_rank=?,lvl_joined_academy=?,apprentice=?,key_bindings=?,pcBangPoints=?,char_name=?,vitality=?," + "fame=?,bookmarks=?,hwid_lock=?,raidkills=?,eventKills=?,siege_kills=?,oly_wins=?,facebook_id=?,forum_login=? WHERE obj_Id=? LIMIT 1");)
			{
				statement.setInt(1, getFace());
				statement.setInt(2, getHairStyle());
				statement.setInt(3, getHairColor());
				if (_stablePoint == null)
				{
					statement.setInt(4, getX());
					statement.setInt(5, getY());
					statement.setInt(6, getZ());
				}
				else
				{
					statement.setInt(4, _stablePoint.x);
					statement.setInt(5, _stablePoint.y);
					statement.setInt(6, _stablePoint.z);
				}
				statement.setInt(7, getKarma());
				statement.setInt(8, getPvpKills());
				statement.setInt(9, getPkKills());
				statement.setInt(10, getRecomHave());
				statement.setInt(11, getRecomLeft());
				statement.setInt(12, getRecomBonusTime());
				statement.setInt(13, getNevitSystem().getPoints());
				statement.setInt(14, getNevitSystem().getTime());
				statement.setInt(15, getClanId());
				statement.setInt(16, getDeleteTimer());
				statement.setString(17, _title);
				statement.setInt(18, _accessLevel);
				statement.setInt(19, (isOnline() && !isInOfflineMode()) || (isInOfflineMode() && Config.SHOW_OFFLINE_MODE_IN_ONLINE) ? 1 : 0);
				statement.setLong(20, getLeaveClanTime() / 1000L);
				statement.setLong(21, getDeleteClanTime() / 1000L);
				statement.setLong(22, _NoChannel > 0 ? getNoChannelRemained() / 1000 : _NoChannel);
				statement.setInt(23, (int) (getOnlineTime() / 1000L));
				statement.setInt(24, getPledgeType());
				statement.setInt(25, getPowerGrade());
				statement.setInt(26, getLvlJoinedAcademy());
				statement.setInt(27, getApprentice());
				statement.setBytes(28, getKeyBindings());
				statement.setInt(29, getPcBangPoints());
				statement.setString(30, getName());
				statement.setInt(31, (int) getVitality());
				statement.setInt(32, getFame());
				statement.setInt(33, bookmarks.getCapacity());
				statement.setString(34, getHwidLock());
				statement.setInt(35, getRaidKills());
				statement.setInt(36, getEventKills());
				statement.setInt(37, getSiegeKills());
				statement.setInt(38, getOlyWins());
				statement.setString(39, (_facebookProfile == null ? "" : _facebookProfile.getId()));
				statement.setString(40, _forumLogin);
				statement.setInt(41, getObjectId());

				statement.executeUpdate();

				if (!isPhantom())
				{
					if (Config.RATE_DROP_ADENA < 20)
					{
						GameStats.increaseUpdatePlayerBase();
					}

					if (!fast)
					{
						EffectsDAO.getInstance().insert(this);
						CharacterGroupReuseDAO.getInstance().insert(this);
						storeDisableSkills();
						storeBlockList();
					}

					storeCharSubClasses();
					bookmarks.store();

					if (Config.ENABLE_PLAYER_COUNTERS)
					{
						getCounters().save();

						if (Config.ENABLE_ACHIEVEMENTS)
						{
							saveAchivements();
						}
					}
				}
			}
			catch (SQLException e)
			{
				_log.error("Could not store char data: " + this + '!', e);
			}
		}
		finally
		{
			_storeLock.unlock();
		}
	}

	private int _raidKills;

	public void updateRaidKills()
	{
		_raidKills++;
	}

	public int getRaidKills()
	{
		return _raidKills;
	}

	/**
	 * Add a skill to the L2Player _skills and its Func objects to the calculator set of the L2Player and save update in the character_skills table of the database.
	 * @param newSkill
	 * @param store
	 * @return The L2Skill replaced or null if just added a new L2Skill
	 */
	public Skill addSkill(Skill newSkill, boolean store)
	{
		if (newSkill == null)
		{
			return null;
		}

		// Synerge - Fix If the skill existed before, then we must transfer its reuse to the new level. Its a known exploit of enchant a skill to reset its reuse
		if (getKnownSkill(newSkill.getId()) != null)
		{
			disableSkillByNewLvl(SkillTable.getInstance().getInfo(newSkill.getId(), getKnownSkill(newSkill.getId()).getLevel()).hashCode(), SkillTable.getInstance().getInfo(newSkill.getId(), newSkill.getLevel()).hashCode());
		}
		// Add a skill to the L2Player _skills and its Func objects to the calculator set of the L2Player
		Skill oldSkill = super.addSkill(newSkill);

		if (newSkill.equals(oldSkill))
		{
			return oldSkill;
		}

		// Add or update a L2Player skill in the character_skills table of the database
		if (store)
		{
			storeSkill(newSkill, oldSkill);
		}

		// Synerge - Force a user info update if the skill has stats changes
		if (newSkill.isPassive())
		{
			broadcastUserInfo(false);
		}

		return oldSkill;
	}

	public Skill removeSkill(Skill skill, boolean fromDB)
	{
		if (skill == null)
		{
			return null;
		}
		return removeSkill(skill.getId(), fromDB);
	}

	/**
	 * Remove a skill from the L2Character and its Func objects from calculator set of the L2Character and save update in the character_skills table of the database.
	 * @param id
	 * @param fromDB
	 * @return The L2Skill removed
	 */
	public Skill removeSkill(int id, boolean fromDB)
	{
		// Remove a skill from the L2Character and its Func objects from calculator set of the L2Character
		Skill oldSkill = super.removeSkillById(id);

		if (!fromDB)
		{
			return oldSkill;
		}

		if (oldSkill != null)
		{
			try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=? AND class_index=?"))
			{
				// Remove or update a L2Player skill from the character_skills table of the database
				statement.setInt(1, oldSkill.getId());
				statement.setInt(2, getObjectId());
				statement.setInt(3, getActiveClassId());
				statement.execute();
			}
			catch (Exception e)
			{
				_log.error("Could not delete skill!", e);
			}

			// Synerge - Force a user info update if the skill has stats changes
			if (oldSkill.isPassive())
			{
				broadcastUserInfo(false);
			}
		}

		return oldSkill;
	}

	public void removeSiegeSkills()
	{
		removeSkill(SkillTable.getInstance().getInfo(246, 1), false);
		removeSkill(SkillTable.getInstance().getInfo(247, 1), false);
		removeSkill(SkillTable.getInstance().getInfo(326, 1), false);
		if (getClan() != null && getClan().getCastle() > 0)
		{
			removeSkill(SkillTable.getInstance().getInfo(844, 1), false);
			removeSkill(SkillTable.getInstance().getInfo(845, 1), false);
		}
	}

	/**
	 * Add or update a L2Player skill in the character_skills table of the database.
	 * @param newSkill
	 * @param oldSkill
	 */
	private void storeSkill(Skill newSkill, Skill oldSkill)
	{
		if (newSkill == null)
		{
			_log.warn("could not store new skill. its NULL");
			return;
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("REPLACE INTO character_skills (char_obj_id,skill_id,skill_level,class_index) values(?,?,?,?)"))
		{
			statement.setInt(1, getObjectId());
			statement.setInt(2, newSkill.getId());
			statement.setInt(3, newSkill.getLevel());
			statement.setInt(4, getActiveClassId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error could not store skills!", e);
		}
	}

	/**
	 * Retrieve from the database all skills of this L2Player and add them to _skills.
	 */
	private void restoreSkills()
	{
		PreparedStatement statement = null;
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			// Retrieve all skills of this L2Player from the database
			// Send the SQL query : SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? to the database
			if (Config.ALT_ENABLE_MULTI_PROFA)
			{
				statement = con.prepareStatement("SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=?");
				statement.setInt(1, getObjectId());
			}
			else
			{
				statement = con.prepareStatement("SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? AND class_index=?");
				statement.setInt(1, getObjectId());
				statement.setInt(2, getActiveClassId());
			}

			try (ResultSet rset = statement.executeQuery())
			{
				// Go though the recordset of this SQL query
				while (rset.next())
				{
					final int id = rset.getInt("skill_id");
					final int level = rset.getInt("skill_level");

					// Create a L2Skill object for each record
					final Skill skill = SkillTable.getInstance().getInfo(id, level);

					if (skill == null)
					{
						continue;
					}

					if (!isGM() && !SkillAcquireHolder.getInstance().isSkillPossible(this, skill))
					{
						// int ReturnSP = SkillTreeTable.getInstance().getSkillCost(this, skill);
						// if (ReturnSP == Integer.MAX_VALUE || ReturnSP < 0)
						// ReturnSP = 0;
						removeSkill(skill, true);
						removeSkillFromShortCut(skill.getId());
						// if (ReturnSP > 0)
						// setSp(getSp() + ReturnSP);
						continue;
					}
					super.addSkill(skill);
				}
			}

			// Restore noble skills
			if (isNoble())
			{
				updateNobleSkills();
			}

			// Restore Hero skills at main class only
			if (_hero && (getBaseClassId() == getActiveClassId()))
			{
				Hero.addSkills(this);
			}

			// Restore clan skills
			if (_clan != null)
			{
				_clan.addSkillsQuietly(this);

				// Restore clan leader siege skills
				if ((_clan.getLeaderId() == getObjectId()) && (_clan.getLevel() >= 5))
				{
					SiegeUtils.addSiegeSkills(this);
				}
			}

			// Give dwarven craft skill
			if (((getActiveClassId() >= 53) && (getActiveClassId() <= 57)) || (getActiveClassId() == 117) || (getActiveClassId() == 118))
			{
				super.addSkill(SkillTable.getInstance().getInfo(1321, 1));
			}

			super.addSkill(SkillTable.getInstance().getInfo(1322, 1));

			if (Config.UNSTUCK_SKILL && (getSkillLevel(1050) < 0))
			{
				super.addSkill(SkillTable.getInstance().getInfo(2099, 1));
			}
		}
		catch (SQLException e)
		{
			_log.warn("Could not restore skills for player objId: " + getObjectId(), e);
		}
		finally
		{
			DbUtils.closeQuietly(statement);
		}
	}

	public void storeDisableSkills()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); Statement statement = con.createStatement())
		{
			statement.executeUpdate("DELETE FROM character_skills_save WHERE char_obj_id = " + getObjectId() + " AND class_index=" + getActiveClassId() + " AND `end_time` < " + System.currentTimeMillis());

			if (_skillReuses.isEmpty())
			{
				return;
			}

			SqlBatch b = new SqlBatch("REPLACE INTO `character_skills_save` (`char_obj_id`,`skill_id`,`skill_level`,`class_index`,`end_time`,`reuse_delay_org`) VALUES");
			synchronized (_skillReuses)
			{
				StringBuilder sb;
				for (TimeStamp timeStamp : _skillReuses.values())
				{
					if (timeStamp.hasNotPassed())
					{
						sb = new StringBuilder("(");
						sb.append(getObjectId()).append(",");
						sb.append(timeStamp.getId()).append(",");
						sb.append(timeStamp.getLevel()).append(",");
						sb.append(getActiveClassId()).append(",");
						sb.append(timeStamp.getEndTime()).append(",");
						sb.append(timeStamp.getReuseBasic()).append(")");
						b.write(sb.toString());
					}
				}
			}
			if (!b.isEmpty())
			{
				statement.executeUpdate(b.close());
			}
		}
		catch (Exception e)
		{
			_log.warn("Could not store disable skills data: " + e);
		}
	}

	public void restoreDisableSkills()
	{
		_skillReuses.clear();

		try (Connection con = DatabaseFactory.getInstance().getConnection(); Statement statement = con.createStatement();)
		{

			try (ResultSet rset = statement.executeQuery("SELECT skill_id,skill_level,end_time,reuse_delay_org FROM character_skills_save WHERE char_obj_id=" + getObjectId() + " AND class_index=" + getActiveClassId()))
			{
				while (rset.next())
				{
					int skillId = rset.getInt("skill_id");
					int skillLevel = rset.getInt("skill_level");
					long endTime = rset.getLong("end_time");
					long rDelayOrg = rset.getLong("reuse_delay_org");
					long curTime = System.currentTimeMillis();

					Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);

					if ((skill != null) && ((endTime - curTime) > 500))
					{
						_skillReuses.put(skill.hashCode(), new TimeStamp(skill, endTime, rDelayOrg));
					}
				}
			}

			statement.executeUpdate("DELETE FROM character_skills_save WHERE char_obj_id = " + getObjectId() + " AND class_index=" + getActiveClassId() + " AND `end_time` < " + System.currentTimeMillis());
		}
		catch (Exception e)
		{
			_log.error("Could not restore active skills data!", e);
		}
	}

	/**
	 * Retrieve from the database all Henna of this L2Player, add them to _henna and calculate stats of the L2Player.<BR>
	 * <BR>
	 */
	private void restoreHenna()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("select slot, symbol_id from character_hennas where char_obj_id=? AND class_index=?"))
		{
			statement.setInt(1, getObjectId());
			statement.setInt(2, getActiveClassId());

			try (ResultSet rset = statement.executeQuery())
			{
				for (int i = 0; i < 3; i++)
				{
					_henna[i] = null;
				}

				while (rset.next())
				{
					final int slot = rset.getInt("slot");
					if ((slot < 1) || (slot > 3))
					{
						continue;
					}

					final int symbol_id = rset.getInt("symbol_id");

					if (symbol_id != 0)
					{
						final Henna tpl = HennaHolder.getInstance().getHenna(symbol_id);
						if (tpl != null)
						{
							_henna[slot - 1] = tpl;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.warn("could not restore henna: " + e);
		}

		// Calculate Henna modifiers of this L2Player
		recalcHennaStats();

	}

	public int getHennaEmptySlots()
	{
		int totalSlots = 1 + getClassId().level();
		for (int i = 0; i < 3; i++)
		{
			if (_henna[i] != null)
			{
				totalSlots--;
			}
		}

		if (totalSlots <= 0)
		{
			return 0;
		}

		return totalSlots;

	}

	/**
	 * Remove a Henna of the L2Player, save update in the character_hennas table of the database and send Server->Client HennaInfo/UserInfo packet to this L2Player.<BR>
	 * <BR>
	 * @param slot
	 * @return
	 */
	public boolean removeHenna(int slot)
	{
		if ((slot < 1) || (slot > 3))
		{
			return false;
		}

		slot--;

		if (_henna[slot] == null)
		{
			return false;
		}

		final Henna henna = _henna[slot];
		final int dyeID = henna.getDyeId();

		_henna[slot] = null;

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM character_hennas where char_obj_id=? and slot=? and class_index=?"))
		{
			statement.setInt(1, getObjectId());
			statement.setInt(2, slot + 1);
			statement.setInt(3, getActiveClassId());
			statement.execute();
		}
		catch (Exception e)
		{
			_log.warn("could not remove char henna: " + e, e);
		}

		// Calculate Henna modifiers of this L2Player
		recalcHennaStats();

		// Send Server->Client HennaInfo packet to this L2Player
		sendPacket(new HennaInfo(this));
		// Send Server->Client UserInfo packet to this L2Player
		sendUserInfo(true);

		// Add the recovered dyes to the player's inventory and notify them.
		ItemFunctions.addItem(this, dyeID, henna.getDrawCount() / 2, true, "removeHenna");

		return true;
	}

	/**
	 * Add a Henna to the L2Player, save update in the character_hennas table of the database and send Server->Client HennaInfo/UserInfo packet to this L2Player.<BR>
	 * @param henna
	 * @return
	 */
	public boolean addHenna(Henna henna)
	{
		if (getHennaEmptySlots() == 0)
		{
			sendPacket(SystemMsg.NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL);
			return false;
		}

		// int slot = 0;
		for (int i = 0; i < 3; i++)
		{
			if (_henna[i] == null)
			{
				_henna[i] = henna;

				// Calculate Henna modifiers of this L2Player
				recalcHennaStats();

				try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("INSERT INTO `character_hennas` (char_obj_id, symbol_id, slot, class_index) VALUES (?,?,?,?)"))
				{
					statement.setInt(1, getObjectId());
					statement.setInt(2, henna.getSymbolId());
					statement.setInt(3, i + 1);
					statement.setInt(4, getActiveClassId());
					statement.execute();
				}
				catch (Exception e)
				{
					_log.warn("could not save char henna: " + e);
				}

				sendPacket(new HennaInfo(this));
				sendUserInfo(true);

				return true;
			}
		}

		return false;
	}

	/**
	 * Calculate Henna modifiers of this L2Player.
	 */
	private void recalcHennaStats()
	{
		_hennaINT = 0;
		_hennaSTR = 0;
		_hennaCON = 0;
		_hennaMEN = 0;
		_hennaWIT = 0;
		_hennaDEX = 0;

		for (int i = 0; i < 3; i++)
		{
			Henna henna = _henna[i];
			if ((henna == null) || !henna.isForThisClass(this))
			{
				continue;
			}

			_hennaINT += henna.getStatINT();
			_hennaSTR += henna.getStatSTR();
			_hennaMEN += henna.getStatMEN();
			_hennaCON += henna.getStatCON();
			_hennaWIT += henna.getStatWIT();
			_hennaDEX += henna.getStatDEX();
		}

		if (_hennaINT > Config.HENNA_STATS)
		{
			_hennaINT = Config.HENNA_STATS;
		}
		if (_hennaSTR > Config.HENNA_STATS)
		{
			_hennaSTR = Config.HENNA_STATS;
		}
		if (_hennaMEN > Config.HENNA_STATS)
		{
			_hennaMEN = Config.HENNA_STATS;
		}
		if (_hennaCON > Config.HENNA_STATS)
		{
			_hennaCON = Config.HENNA_STATS;
		}
		if (_hennaWIT > Config.HENNA_STATS)
		{
			_hennaWIT = Config.HENNA_STATS;
		}
		if (_hennaDEX > Config.HENNA_STATS)
		{
			_hennaDEX = Config.HENNA_STATS;
		}
	}

	/**
	 * @param slot
	 * @return the Henna of this L2Player corresponding to the selected slot.<BR>
	 * <BR>
	 */
	public Henna getHenna(int slot)
	{
		if ((slot < 1) || (slot > 3))
		{
			return null;
		}
		return _henna[slot - 1];
	}

	public int getHennaStatINT()
	{
		return _hennaINT;
	}

	public int getHennaStatSTR()
	{
		return _hennaSTR;
	}

	public int getHennaStatCON()
	{
		return _hennaCON;
	}

	public int getHennaStatMEN()
	{
		return _hennaMEN;
	}

	public int getHennaStatWIT()
	{
		return _hennaWIT;
	}

	public int getHennaStatDEX()
	{
		return _hennaDEX;
	}

	@Override
	public boolean consumeItem(int itemConsumeId, long itemCount)
	{
		if (getInventory().destroyItemByItemId(itemConsumeId, itemCount, "Consume"))
		{
			sendPacket(SystemMessage2.removeItems(itemConsumeId, itemCount));
			return true;
		}
		return false;
	}

	@Override
	public boolean consumeItemMp(int itemId, int mp)
	{
		for (ItemInstance item : getInventory().getPaperdollItems())
		{
			if ((item != null) && (item.getItemId() == itemId))
			{
				final int newMp = item.getLifeTime() - mp;
				if (newMp >= 0)
				{
					item.setLifeTime(newMp);
					sendPacket(new InventoryUpdate().addModifiedItem(item));
					return true;
				}
				break;
			}
		}
		return false;
	}

	/**
	 * @return True if the L2Player is a Mage.<BR>
	 * <BR>
	 */
	@Override
	public boolean isMageClass()
	{
		return _template.baseMAtk > 3;
	}

	public boolean isMounted()
	{
		return _mountNpcId > 0;
	}

	public final boolean isRiding()
	{
		return _riding;
	}

	public final void setRiding(boolean mode)
	{
		_riding = mode;
	}

	public boolean checkLandingState()
	{
		if (isInZone(ZoneType.no_landing))
		{
			return false;
		}

		SiegeEvent<?, ?> siege = getEvent(SiegeEvent.class);
		if (siege != null)
		{
			Residence unit = siege.getResidence();
			if ((unit != null) && (getClan() != null) && isClanLeader() && ((getClan().getCastle() == unit.getId()) || (getClan().getHasFortress() == unit.getId())))
			{
				return true;
			}
			return false;
		}

		return true;
	}

	public void dismount()
	{
		setMount(0, 0, 0);
	}

	public void setMount(int npcId, int obj_id, int level)
	{
		if (isCursedWeaponEquipped())
		{
			return;
		}

		switch (npcId)
		{
		case 0: // Dismount
			setFlying(false);
			setRiding(false);
			if (getTransformation() > 0)
			{
				setTransformation(0);
			}
			removeSkillById(Skill.SKILL_STRIDER_ASSAULT);
			removeSkillById(Skill.SKILL_WYVERN_BREATH);
			getEffectList().stopEffect(Skill.SKILL_HINDER_STRIDER);
			break;
		case PetDataTable.STRIDER_WIND_ID:
		case PetDataTable.STRIDER_STAR_ID:
		case PetDataTable.STRIDER_TWILIGHT_ID:
		case PetDataTable.RED_STRIDER_WIND_ID:
		case PetDataTable.RED_STRIDER_STAR_ID:
		case PetDataTable.RED_STRIDER_TWILIGHT_ID:
		case PetDataTable.GUARDIANS_STRIDER_ID:
			setRiding(true);
			if (isNoble())
			{
				addSkill(SkillTable.getInstance().getInfo(Skill.SKILL_STRIDER_ASSAULT, 1), false);
			}
			break;
		case PetDataTable.WYVERN_ID:
			setFlying(true);
			setLoc(getLoc().changeZ(32));
			addSkill(SkillTable.getInstance().getInfo(Skill.SKILL_WYVERN_BREATH, 1), false);
			break;
		case PetDataTable.WGREAT_WOLF_ID:
		case PetDataTable.FENRIR_WOLF_ID:
		case PetDataTable.WFENRIR_WOLF_ID:
			setRiding(true);
			break;
		}

		if (npcId > 0)
		{
			unEquipWeapon();
		}

		_mountNpcId = npcId;
		_mountObjId = obj_id;
		_mountLevel = level;

		broadcastUserInfo(true);
		broadcastPacket(new Ride(this));
		broadcastUserInfo(true);

		sendPacket(new SkillList(this));
	}

	public void unEquipWeapon()
	{
		ItemInstance wpn = getSecondaryWeaponInstance();
		if (wpn != null)
		{
			sendDisarmMessage(wpn);
			getInventory().unEquipItem(wpn);
		}

		wpn = getActiveWeaponInstance();
		if (wpn != null)
		{
			sendDisarmMessage(wpn);
			getInventory().unEquipItem(wpn);
		}

		abortAttack(true, true);
		abortCast(true, true);
	}

	@Override
	public int getSpeed(int baseSpeed)
	{
		if (isMounted())
		{
			PetData petData = PetDataTable.getInstance().getInfo(_mountNpcId, _mountLevel);
			int speed = 187;
			if (petData != null)
			{
				speed = petData.getSpeed();
			}
			double mod = 1.;
			int level = getLevel();
			if ((_mountLevel > level) && ((level - _mountLevel) > 10))
			{
				mod = 0.5;
			}
			baseSpeed = (int) (mod * speed);
		}
		return super.getSpeed(baseSpeed);
	}

	private int _mountNpcId;
	private int _mountObjId;
	private int _mountLevel;

	public int getMountNpcId()
	{
		return _mountNpcId;
	}

	public int getMountObjId()
	{
		return _mountObjId;
	}

	public int getMountLevel()
	{
		return _mountLevel;
	}

	public void sendDisarmMessage(ItemInstance wpn)
	{
		if (wpn.getEnchantLevel() > 0)
		{
			SystemMessage sm = new SystemMessage(SystemMessage.EQUIPMENT_OF__S1_S2_HAS_BEEN_REMOVED);
			sm.addNumber(wpn.getEnchantLevel());
			sm.addItemName(wpn.getItemId());
			sendPacket(sm);
		}
		else
		{
			SystemMessage sm = new SystemMessage(SystemMessage.S1__HAS_BEEN_DISARMED);
			sm.addItemName(wpn.getItemId());
			sendPacket(sm);
		}
	}

	public void setUsingWarehouseType(WarehouseType type)
	{
		_usingWHType = type;
	}

	public WarehouseType getUsingWarehouseType()
	{
		return _usingWHType;
	}

	public Warehouse getWithdrawWarehouse()
	{
		return _withdrawWarehouse;
	}

	public void setWithdrawWarehouse(Warehouse withdrawWarehouse)
	{
		_withdrawWarehouse = withdrawWarehouse;
	}

	public Collection<EffectCubic> getCubics()
	{
		return _cubics == null ? Collections.<EffectCubic>emptyList() : _cubics.values();
	}

	public void addCubic(EffectCubic cubic)
	{
		if (_cubics == null)
		{
			_cubics = new ConcurrentHashMap<>(3);
		}
		_cubics.put(cubic.getId(), cubic);
	}

	public void removeCubic(int id)
	{
		if (_cubics != null)
		{
			_cubics.remove(id);
		}
	}

	public EffectCubic getCubic(int id)
	{
		return _cubics == null ? null : _cubics.get(id);
	}

	@Override
	public String toString()
	{
		return getName() + "[" + getObjectId() + "]";
	}

	/**
	 * @return the modifier corresponding to the Enchant Effect of the Active Weapon (Min : 127).<BR>
	 * <BR>
	 */
	public int getEnchantEffect()
	{
		final ItemInstance wpn = getActiveWeaponInstance();

		if (wpn == null)
		{
			return 0;
		}

		return Math.min(127, wpn.getEnchantLevel());
	}

	/**
	 * Set the _lastFolkNpc of the L2Player corresponding to the last Folk witch one the player talked.<BR>
	 * <BR>
	 * @param npc
	 */
	public void setLastNpc(NpcInstance npc)
	{
		if (npc == null)
		{
			_lastNpc = HardReferences.emptyRef();
		}
		else
		{
			_lastNpc = npc.getRef();
		}
	}

	/**
	 * @return the _lastFolkNpc of the L2Player corresponding to the last Folk witch one the player talked.<BR>
	 * <BR>
	 */
	public NpcInstance getLastNpc()
	{
		return _lastNpc.get();
	}

	public void setMultisell(MultiSellListContainer multisell)
	{
		_multisell = multisell;
	}

	public MultiSellListContainer getMultisell()
	{
		return _multisell;
	}

	/**
	 * @return True if L2Player is a participant in the Festival of Darkness.<BR>
	 * <BR>
	 */
	public boolean isFestivalParticipant()
	{
		return getReflection() instanceof DarknessFestival;
	}

	@Override
	public boolean unChargeShots(boolean spirit)
	{
		ItemInstance weapon = getActiveWeaponInstance();
		if (weapon == null)
		{
			return false;
		}

		if (spirit)
		{
			weapon.setChargedSpiritshot(ItemInstance.CHARGED_NONE);
		}
		else
		{
			weapon.setChargedSoulshot(ItemInstance.CHARGED_NONE);
		}

		autoShot();
		return true;
	}

	public boolean unChargeFishShot()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		if (weapon == null)
		{
			return false;
		}
		weapon.setChargedFishshot(false);
		autoShot();
		return true;
	}

	public void autoShot()
	{
		for (Integer shotId : _activeSoulShots)
		{
			ItemInstance item = getInventory().getItemByItemId(shotId);
			if (item == null)
			{
				removeAutoSoulShot(shotId);
				continue;
			}
			IItemHandler handler = item.getTemplate().getHandler();
			if (handler == null)
			{
				continue;
			}
			handler.useItem(this, item, false);
		}
	}

	public boolean getChargedFishShot()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		return (weapon != null) && weapon.getChargedFishshot();
	}

	@Override
	public boolean getChargedSoulShot()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		return (weapon != null) && (weapon.getChargedSoulshot() == ItemInstance.CHARGED_SOULSHOT);
	}

	@Override
	public int getChargedSpiritShot()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		if (weapon == null)
		{
			return 0;
		}
		return weapon.getChargedSpiritshot();
	}

	public void addAutoSoulShot(Integer itemId)
	{
		_activeSoulShots.add(itemId);
	}

	public void removeAutoSoulShot(Integer itemId)
	{
		_activeSoulShots.remove(itemId);
	}

	public Set<Integer> getAutoSoulShot()
	{
		return _activeSoulShots;
	}

	public void setInvisibleType(InvisibleType vis)
	{
		_invisibleType = vis;
	}

	@Override
	public InvisibleType getInvisibleType()
	{
		return _invisibleType;
	}

	public int getClanPrivileges()
	{
		if (_clan == null)
		{
			return 0;
		}
		if (isClanLeader())
		{
			return Clan.CP_ALL;
		}
		if ((_powerGrade < 1) || (_powerGrade > 9))
		{
			return 0;
		}
		RankPrivs privs = _clan.getRankPrivs(_powerGrade);
		if (privs != null)
		{
			return privs.getPrivs();
		}
		return 0;
	}

	public void teleToClosestTown()
	{
		teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_VILLAGE), ReflectionManager.DEFAULT);
	}

	public void teleToCastle()
	{
		teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_CASTLE), ReflectionManager.DEFAULT);
	}

	public void teleToFortress()
	{
		teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_FORTRESS), ReflectionManager.DEFAULT);
	}

	public void teleToClanhall()
	{
		teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_CLANHALL), ReflectionManager.DEFAULT);
	}

	public void sendMessageS(String text, int timeonscreenins)
	{
		sendPacket(new ExShowScreenMessage(text, timeonscreenins * 1000, ScreenMessageAlign.TOP_CENTER, text.length() > 30 ? false : true));
	}

	@Override
	public void sendMessage(CustomMessage message)
	{
		sendMessage(message.toString());
	}

	@Override
	public void sendCustomMessage(String address, Object... args)
	{
		sendMessage(new CustomMessage(address, this, args));
	}

	@Override
	public void sendChatMessage(int objectId, int messageType, String charName, String text)
	{
		sendPacket(new CreatureSay(objectId, messageType, charName, text));
	}

	@Override
	public void teleToLocation(int x, int y, int z, int refId)
	{
		if (isDeleted())
		{
			return;
		}

		super.teleToLocation(x, y, z, refId);
	}

	@Override
	public boolean onTeleported()
	{
		if (!super.onTeleported())
		{
			return false;
		}

		if (isFakeDeath())
		{
			breakFakeDeath();
		}

		if (isInBoat())
		{
			setLoc(getBoat().getLoc());
		}

		// 15 seconds after teleport the character does not cast agr
		setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);

		// Synerge - We also give player spawn protection, so he cannot recieve damage until he does something. 20 seconds protection
		setSpawnProtection(System.currentTimeMillis() + SPAWN_PROTECTION_TIME);

		spawnMe();

		setLastClientPosition(getLoc());
		setLastServerPosition(getLoc());

		if (isPendingRevive())
		{
			doRevive();
		}

		sendActionFailed();

		getAI().notifyEvent(CtrlEvent.EVT_TELEPORTED);

		if (isLockedTarget() && (getTarget() != null))
		{
			sendPacket(new MyTargetSelected(getTarget().getObjectId(), 0));
		}

		sendUserInfo(true);
		if (getPet() != null)
		{
			if (!getPet().isInRange(getLoc(), Config.FOLLOW_RANGE))
			{
				getPet().teleportToOwner();
			}
		}

		getListeners().onTeleported();

		return true;
	}

	private boolean _partyMatchingVisible = true;

	public void setPartyMatchingVisible()
	{
		_partyMatchingVisible = (!(_partyMatchingVisible));
	}

	public boolean isPartyMatchingVisible()
	{
		return _partyMatchingVisible;
	}

	public boolean enterObserverMode(Location loc)
	{
		return enterObserverMode(loc, getReflection());
	}

	public boolean enterObserverMode(Location loc, Reflection reflection)
	{
		final WorldRegion observerRegion = World.getRegion(loc);
		if ((observerRegion == null) || !_observerMode.compareAndSet(OBSERVER_NONE, OBSERVER_STARTING))
		{
			return false;
		}

		setReflection(reflection);
		World.removeObjectsFromPlayer(this);

		setTarget(null);
		stopMove();
		sitDown(null);
		setFlying(true);

		_observerRegion = observerRegion;

		broadcastCharInfo();
		sendPacket(new ObserverStart(loc));
		return true;
	}

	public void appearObserverMode()
	{
		if (!_observerMode.compareAndSet(OBSERVER_STARTING, OBSERVER_STARTED))
		{
			return;
		}

		WorldRegion currentRegion = getCurrentRegion();

		// Add a fake to the point of observation
		if (!_observerRegion.equals(currentRegion))
		{
			_observerRegion.addObject(this);
		}

		World.showObjectsToPlayer(this);

		if (_olympiadObserveGame != null)
		{
			_olympiadObserveGame.addSpectator(this);
			_olympiadObserveGame.broadcastInfo(null, this, true);
		}
	}

	public void leaveObserverMode()
	{
		if (!_observerMode.compareAndSet(OBSERVER_STARTED, OBSERVER_LEAVING))
		{
			return;
		}

		getListeners().onObservationEnd();
		final WorldRegion currentRegion = getCurrentRegion();
		if (!_observerRegion.equals(currentRegion))
		{
			_observerRegion.removeObject(this);
		}

		// Clear all visible objects
		setReflection(ReflectionManager.DEFAULT);
		World.removeObjectsFromPlayer(this);

		_observerRegion = null;

		setTarget(null);
		stopMove();

		// Exit the mode observing
		sendPacket(new ObserverEnd(getLoc()));
	}

	public void returnFromObserverMode()
	{
		if (!_observerMode.compareAndSet(OBSERVER_LEAVING, OBSERVER_NONE))
		{
			return;
		}

		// It is necessary when teleport from a higher point to a lower, or harmed by the "fall"
		_lastClientPosition = null;
		_lastServerPosition = null;

		unblock();
		standUp();
		setFlying(false);

		broadcastCharInfo();

		World.showObjectsToPlayer(this);
	}

	public void enterOlympiadObserverMode(Location loc, OlympiadGame game, Reflection reflect)
	{
		final WorldRegion observerRegion = World.getRegion(loc);
		final WorldRegion oldObserver = _observerRegion;
		if (observerRegion == null)
		{
			return;
		}

		final OlympiadGame oldGame = _olympiadObserveGame;
		if (!_observerMode.compareAndSet(oldGame != null ? OBSERVER_STARTED : OBSERVER_NONE, OBSERVER_STARTING))
		{
			return;
		}

		setTarget(null);
		stopMove();

		World.removeObjectsFromPlayer(this);
		_observerRegion = observerRegion;

		if (oldGame != null)
		{
			if (isInObserverMode() && oldObserver != null)
			{
				oldObserver.removeObject(this);
			}
			oldGame.removeSpectator(this);
			sendPacket(ExOlympiadMatchEnd.STATIC);
		}
		else
		{
			block();

			broadcastCharInfo();

			sendPacket(new ExOlympiadMode(3));
		}

		_olympiadObserveGame = game;

		setReflection(reflect);
		sendPacket(new TeleportToLocation(this, loc));
	}

	public void leaveOlympiadObserverMode(boolean removeFromGame)
	{
		if ((_olympiadObserveGame == null) || !_observerMode.compareAndSet(OBSERVER_STARTED, OBSERVER_LEAVING))
		{
			return;
		}

		getListeners().onObservationEnd();

		if (removeFromGame)
		{
			_olympiadObserveGame.removeSpectator(this);
		}
		_olympiadObserveGame = null;

		final WorldRegion currentRegion = getCurrentRegion();
		if (_observerRegion != null && currentRegion != null && !_observerRegion.equals(currentRegion))
		{
			_observerRegion.removeObject(this);
		}

		World.removeObjectsFromPlayer(this);

		_observerRegion = null;

		setTarget(null);
		stopMove();

		sendPacket(new ExOlympiadMode(0));
		sendPacket(ExOlympiadMatchEnd.STATIC);

		setReflection(ReflectionManager.DEFAULT);

		sendPacket(new TeleportToLocation(this, getLoc()));
	}

	public void setOlympiadSide(int i)
	{
		_olympiadSide = i;
	}

	public int getOlympiadSide()
	{
		return _olympiadSide;
	}

	@Override
	public boolean isInObserverMode()
	{
		return _observerMode.get() > OBSERVER_NONE;// So it can be OBSERVER_STARTING(1), OBSERVER_LEAVING(2) or OBSERVER_STARTED(3)
	}

	public int getObserverMode()
	{
		return _observerMode.get();
	}

	public WorldRegion getObserverRegion()
	{
		return _observerRegion;
	}

	public void setObserverRegion(WorldRegion region)
	{
		_observerRegion = region;
	}

	public int getTeleMode()
	{
		return _telemode;
	}

	public void setTeleMode(int mode)
	{
		_telemode = mode;
	}

	public void setLoto(int i, int val)
	{
		_loto[i] = val;
	}

	public int getLoto(int i)
	{
		return _loto[i];
	}

	public void setRace(int i, int val)
	{
		_race[i] = val;
	}

	public int getRace(int i)
	{
		return _race[i];
	}

	public boolean getMessageRefusal()
	{
		return _messageRefusal;
	}

	public void setMessageRefusal(boolean mode)
	{
		_messageRefusal = mode;
	}

	public void setTradeRefusal(boolean mode)
	{
		_tradeRefusal = mode;
	}

	public boolean getTradeRefusal()
	{
		return _tradeRefusal;
	}

	public void addToBlockList(String charName)
	{
		if ((charName == null) || charName.equalsIgnoreCase(getName()) || isInBlockList(charName))
		{
			sendPacket(Msg.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
			return;
		}

		Player block_target = World.getPlayer(charName);

		if (block_target != null)
		{
			if (block_target.isGM())
			{
				sendPacket(Msg.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
				return;
			}
			_blockList.put(block_target.getObjectId(), block_target.getName());
			sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST).addString(block_target.getName()));
			block_target.sendPacket(new SystemMessage(SystemMessage.S1__HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST).addString(getName()));
			return;
		}

		int charId = CharacterDAO.getInstance().getObjectIdByName(charName);

		if (charId == 0)
		{
			sendPacket(Msg.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
			return;
		}

		if (Config.gmlist.containsKey(charId) && Config.gmlist.get(charId).IsGM)
		{
			sendPacket(Msg.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
			return;
		}
		_blockList.put(charId, charName);
		sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST).addString(charName));
	}

	public void removeFromBlockList(String charName)
	{
		int charId = 0;
		for (int blockId : _blockList.keySet())
		{
			if (charName.equalsIgnoreCase(_blockList.get(blockId)))
			{
				charId = blockId;
				break;
			}
		}
		if (charId == 0)
		{
			sendPacket(Msg.YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER_FROM_IGNORE_LIST);
			return;
		}
		sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_REMOVED_FROM_YOUR_IGNORE_LIST).addString(_blockList.remove(charId)));
		Player block_target = GameObjectsStorage.getPlayer(charId);
		if (block_target != null)
		{
			block_target.sendMessage(getName() + " has removed you from his/her Ignore List.");
		}
	}

	public boolean isInBlockList(Player player)
	{
		return isInBlockList(player.getObjectId());
	}

	public boolean isInBlockList(int charId)
	{
		return (_blockList != null) && _blockList.containsKey(charId);
	}

	public boolean isInBlockList(String charName)
	{
		for (int blockId : _blockList.keySet())
		{
			if (charName.equalsIgnoreCase(_blockList.get(blockId)))
			{
				return true;
			}
		}
		return false;
	}

	private void restoreBlockList(Connection con)
	{
		_blockList.clear();

		try (PreparedStatement statement = con.prepareStatement("SELECT target_Id, char_name FROM character_blocklist LEFT JOIN characters ON ( character_blocklist.target_Id = characters.obj_Id ) WHERE character_blocklist.obj_Id = ?"))
		{
			statement.setInt(1, getObjectId());

			try (ResultSet rs = statement.executeQuery())
			{
				while (rs.next())
				{
					int targetId = rs.getInt("target_Id");
					String name = rs.getString("char_name");
					if (name == null)
					{
						continue;
					}
					_blockList.put(targetId, name);
				}
			}
		}
		catch (SQLException e)
		{
			_log.warn("Can't restore player blocklist " + e, e);
		}
	}

	private void storeBlockList()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); Statement statement = con.createStatement())
		{
			statement.executeUpdate("DELETE FROM character_blocklist WHERE obj_Id=" + getObjectId());

			if (_blockList.isEmpty())
			{
				return;
			}

			SqlBatch b = new SqlBatch("INSERT IGNORE INTO `character_blocklist` (`obj_Id`,`target_Id`) VALUES");

			synchronized (_blockList)
			{
				StringBuilder sb;
				for (Entry<Integer, String> e : _blockList.entrySet())
				{
					sb = new StringBuilder("(");
					sb.append(getObjectId()).append(",");
					sb.append(e.getKey()).append(")");
					b.write(sb.toString());
				}
			}
			if (!b.isEmpty())
			{
				statement.executeUpdate(b.close());
			}
		}
		catch (Exception e)
		{
			_log.warn("Can't store player blocklist " + e);
		}
	}

	public boolean isBlockAll()
	{
		return _blockAll;
	}

	public void setBlockAll(boolean state)
	{
		_blockAll = state;
	}

	public Collection<String> getBlockList()
	{
		return _blockList.values();
	}

	public Map<Integer, String> getBlockListMap()
	{
		return _blockList;
	}

	public void setHeroAura(boolean heroAura)
	{
		_heroAura = heroAura;
	}

	public boolean isHeroAura()
	{
		return (_hero) || (_heroAura) || (isFakeHero());
	}

	public boolean isFakeHero()
	{
		if (getVarB("hasFakeHero"))
		{
			return true;
		}
		return false;
	}

	public boolean FakeHeroEquip()
	{
		if ((isFakeHero() && (Config.SERVICES_HERO_SELL_ITEMS)) || (getVarB("hasFakeHeroItems")))
		{
			return true;
		}
		return false;
	}

	public boolean FakeHeroChat()
	{
		if ((isFakeHero() && (Config.SERVICES_HERO_SELL_CHAT)) || (getVarB("hasFakeHeroChat")))
		{
			return true;
		}
		return false;
	}

	public boolean FakeHeroSkill()
	{
		if ((isFakeHero() && (Config.SERVICES_HERO_SELL_SKILL)) || (getVarB("hasFakeHeroSkills")))
		{
			return true;
		}
		return false;
	}

	public void setHero(boolean hero)
	{
		_hero = hero;
	}

	@Override
	public boolean isHero()
	{
		return _hero;
	}

	public void setHero(Player player)
	{
		StatsSet hero = new StatsSet();
		hero.set(Olympiad.CLASS_ID, player.getBaseClassId());
		hero.set(Olympiad.CHAR_ID, player.getObjectId());
		hero.set(Olympiad.CHAR_NAME, player.getName());
		hero.set(Hero.ACTIVE, 1);

		List<StatsSet> heroesToBe = new ArrayList<StatsSet>();
		heroesToBe.add(hero);

		Hero.getInstance().computeNewHeroes(heroesToBe);
		player.setHero(true);
		Hero.addSkills(player);
		player.updatePledgeClass();
		if (player.isHero())
		{
			player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
		}
		player.broadcastUserInfo(true);
	}

	public int getPing()
	{
		return _ping;
	}

	public void setPing(int ping)
	{
		_ping = ping;
	}

	public void setIsInOlympiadMode(boolean b)
	{
		_inOlympiadMode = b;
	}

	@Override
	public boolean isInOlympiadMode()
	{
		return _inOlympiadMode;
	}

	public boolean isOlympiadGameStart()
	{
		return _olympiadGame != null && _olympiadGame.getState() == 1;
	}

	public boolean isOlympiadCompStart()
	{
		return _olympiadGame != null && _olympiadGame.getState() == 2;
	}

	public void updateNobleSkills()
	{
		if (isNoble())
		{
			if (isClanLeader() && (getClan().getCastle() > 0))
			{
				super.addSkill(SkillTable.getInstance().getInfo(Skill.SKILL_WYVERN_AEGIS, 1));
			}
			super.addSkill(SkillTable.getInstance().getInfo(Skill.SKILL_NOBLESSE_BLESSING, 1));
			super.addSkill(SkillTable.getInstance().getInfo(Skill.SKILL_SUMMON_CP_POTION, 1));
			super.addSkill(SkillTable.getInstance().getInfo(Skill.SKILL_FORTUNE_OF_NOBLESSE, 1));
			super.addSkill(SkillTable.getInstance().getInfo(Skill.SKILL_HARMONY_OF_NOBLESSE, 1));
			super.addSkill(SkillTable.getInstance().getInfo(Skill.SKILL_SYMPHONY_OF_NOBLESSE, 1));
		}
		else
		{
			super.removeSkillById(Skill.SKILL_WYVERN_AEGIS);
			super.removeSkillById(Skill.SKILL_NOBLESSE_BLESSING);
			super.removeSkillById(Skill.SKILL_SUMMON_CP_POTION);
			super.removeSkillById(Skill.SKILL_FORTUNE_OF_NOBLESSE);
			super.removeSkillById(Skill.SKILL_HARMONY_OF_NOBLESSE);
			super.removeSkillById(Skill.SKILL_SYMPHONY_OF_NOBLESSE);
		}
	}

	public void setNoble(boolean noble)
	{
		if (noble)
		{
			broadcastPacket(new MagicSkillUse(this, this, 6673, 1, 1000, 0));
		}
		_noble = noble;
	}

	public void setNoble(boolean noble, boolean animation)
	{
		if (animation) // without broadcast of animation.
		{
			broadcastPacket(new MagicSkillUse(this, this, 6673, 1, 1000, 0));
		}

		_noble = noble;
	}

	public boolean isNoble()
	{
		return _noble;
	}

	public int getSubLevel()
	{
		return isSubClassActive() ? getLevel() : 0;
	}

	/* varka silenos and ketra orc quests related functions */
	public void updateKetraVarka()
	{
		if (ItemFunctions.getItemCount(this, 7215) > 0)
		{
			_ketra = 5;
		}
		else if (ItemFunctions.getItemCount(this, 7214) > 0)
		{
			_ketra = 4;
		}
		else if (ItemFunctions.getItemCount(this, 7213) > 0)
		{
			_ketra = 3;
		}
		else if (ItemFunctions.getItemCount(this, 7212) > 0)
		{
			_ketra = 2;
		}
		else if (ItemFunctions.getItemCount(this, 7211) > 0)
		{
			_ketra = 1;
		}
		else if (ItemFunctions.getItemCount(this, 7225) > 0)
		{
			_varka = 5;
		}
		else if (ItemFunctions.getItemCount(this, 7224) > 0)
		{
			_varka = 4;
		}
		else if (ItemFunctions.getItemCount(this, 7223) > 0)
		{
			_varka = 3;
		}
		else if (ItemFunctions.getItemCount(this, 7222) > 0)
		{
			_varka = 2;
		}
		else if (ItemFunctions.getItemCount(this, 7221) > 0)
		{
			_varka = 1;
		}
		else
		{
			_varka = 0;
			_ketra = 0;
		}
	}

	public int getVarka()
	{
		return _varka;
	}

	public int getKetra()
	{
		return _ketra;
	}

	public void updateRam()
	{
		if (ItemFunctions.getItemCount(this, 7247) > 0)
		{
			_ram = 2;
		}
		else if (ItemFunctions.getItemCount(this, 7246) > 0)
		{
			_ram = 1;
		}
		else
		{
			_ram = 0;
		}
	}

	public int getRam()
	{
		return _ram;
	}

	public void setPledgeType(int typeId)
	{
		_pledgeType = typeId;
	}

	public int getPledgeType()
	{
		return _pledgeType;
	}

	public void setLvlJoinedAcademy(int lvl)
	{
		_lvlJoinedAcademy = lvl;
	}

	public int getLvlJoinedAcademy()
	{
		return _lvlJoinedAcademy;
	}

	public int getPledgeClass()
	{
		return _pledgeClass;
	}

	public void updatePledgeClass()
	{
		int clanLevel = _clan == null ? -1 : _clan.getLevel();
		boolean inAcademy = (_clan != null) && Clan.isAcademy(_pledgeType);
		boolean isGuard = (_clan != null) && Clan.isRoyalGuard(_pledgeType);
		boolean isKnight = (_clan != null) && Clan.isOrderOfKnights(_pledgeType);

		boolean isGuardCaptain = false, isKnightCommander = false, isLeader = false;

		SubUnit unit = getSubUnit();
		if (unit != null)
		{
			UnitMember unitMember = unit.getUnitMember(getObjectId());
			if (unitMember == null)
			{
				_log.warn("Player: unitMember null, clan: " + _clan.getClanId() + "; pledgeType: " + unit.getType());
				return;
			}
			isGuardCaptain = Clan.isRoyalGuard(unitMember.getLeaderOf());
			isKnightCommander = Clan.isOrderOfKnights(unitMember.getLeaderOf());
			isLeader = unitMember.getLeaderOf() == Clan.SUBUNIT_MAIN_CLAN;
		}

		switch (clanLevel)
		{
		case -1:
			_pledgeClass = RANK_VAGABOND;
			break;
		case 0:
		case 1:
		case 2:
		case 3:
			if (isLeader)
			{
				_pledgeClass = RANK_HEIR;
			}
			else
			{
				_pledgeClass = RANK_VASSAL;
			}
			break;
		case 4:
			if (isLeader)
			{
				_pledgeClass = RANK_KNIGHT;
			}
			else
			{
				_pledgeClass = RANK_HEIR;
			}
			break;
		case 5:
			if (isLeader)
			{
				_pledgeClass = RANK_WISEMAN;
			}
			else if (inAcademy)
			{
				_pledgeClass = RANK_VASSAL;
			}
			else
			{
				_pledgeClass = RANK_HEIR;
			}
			break;
		case 6:
			if (isLeader)
			{
				_pledgeClass = RANK_BARON;
			}
			else if (inAcademy)
			{
				_pledgeClass = RANK_VASSAL;
			}
			else if (isGuardCaptain)
			{
				_pledgeClass = RANK_WISEMAN;
			}
			else if (isGuard)
			{
				_pledgeClass = RANK_HEIR;
			}
			else
			{
				_pledgeClass = RANK_KNIGHT;
			}
			break;
		case 7:
			if (isLeader)
			{
				_pledgeClass = RANK_COUNT;
			}
			else if (inAcademy)
			{
				_pledgeClass = RANK_VASSAL;
			}
			else if (isGuardCaptain)
			{
				_pledgeClass = RANK_VISCOUNT;
			}
			else if (isGuard)
			{
				_pledgeClass = RANK_KNIGHT;
			}
			else if (isKnightCommander)
			{
				_pledgeClass = RANK_BARON;
			}
			else if (isKnight)
			{
				_pledgeClass = RANK_HEIR;
			}
			else
			{
				_pledgeClass = RANK_WISEMAN;
			}
			break;
		case 8:
			if (isLeader)
			{
				_pledgeClass = RANK_MARQUIS;
			}
			else if (inAcademy)
			{
				_pledgeClass = RANK_VASSAL;
			}
			else if (isGuardCaptain)
			{
				_pledgeClass = RANK_COUNT;
			}
			else if (isGuard)
			{
				_pledgeClass = RANK_WISEMAN;
			}
			else if (isKnightCommander)
			{
				_pledgeClass = RANK_VISCOUNT;
			}
			else if (isKnight)
			{
				_pledgeClass = RANK_KNIGHT;
			}
			else
			{
				_pledgeClass = RANK_BARON;
			}
			break;
		case 9:
			if (isLeader)
			{
				_pledgeClass = RANK_DUKE;
			}
			else if (inAcademy)
			{
				_pledgeClass = RANK_VASSAL;
			}
			else if (isGuardCaptain)
			{
				_pledgeClass = RANK_MARQUIS;
			}
			else if (isGuard)
			{
				_pledgeClass = RANK_BARON;
			}
			else if (isKnightCommander)
			{
				_pledgeClass = RANK_COUNT;
			}
			else if (isKnight)
			{
				_pledgeClass = RANK_WISEMAN;
			}
			else
			{
				_pledgeClass = RANK_VISCOUNT;
			}
			break;
		case 10:
			if (isLeader)
			{
				_pledgeClass = RANK_GRAND_DUKE;
			}
			else if (inAcademy)
			{
				_pledgeClass = RANK_VASSAL;
			}
			else if (isGuard)
			{
				_pledgeClass = RANK_VISCOUNT;
			}
			else if (isKnight)
			{
				_pledgeClass = RANK_BARON;
			}
			else if (isGuardCaptain)
			{
				_pledgeClass = RANK_DUKE;
			}
			else if (isKnightCommander)
			{
				_pledgeClass = RANK_MARQUIS;
			}
			else
			{
				_pledgeClass = RANK_COUNT;
			}
			break;
		case 11:
			if (isLeader)
			{
				_pledgeClass = RANK_DISTINGUISHED_KING;
			}
			else if (inAcademy)
			{
				_pledgeClass = RANK_VASSAL;
			}
			else if (isGuard)
			{
				_pledgeClass = RANK_COUNT;
			}
			else if (isKnight)
			{
				_pledgeClass = RANK_VISCOUNT;
			}
			else if (isGuardCaptain)
			{
				_pledgeClass = RANK_GRAND_DUKE;
			}
			else if (isKnightCommander)
			{
				_pledgeClass = RANK_DUKE;
			}
			else
			{
				_pledgeClass = RANK_MARQUIS;
			}
			break;
		}

		if (_hero && (_pledgeClass < RANK_MARQUIS))
		{
			_pledgeClass = RANK_MARQUIS;
		}
		else if (_noble && (_pledgeClass < RANK_BARON))
		{
			_pledgeClass = RANK_BARON;
		}
	}

	public void setPowerGrade(int grade)
	{
		_powerGrade = grade;
	}

	public int getPowerGrade()
	{
		return _powerGrade;
	}

	public void setApprentice(int apprentice)
	{
		_apprentice = apprentice;
	}

	public int getApprentice()
	{
		return _apprentice;
	}

	public int getSponsor()
	{
		return _clan == null ? 0 : _clan.getAnyMember(getObjectId()).getSponsor();
	}

	public int getNameColor()
	{
		if (isInObserverMode())
		{
			return Color.black.getRGB();
		}

		return _nameColor;
	}

	public void setNameColor(int nameColor)
	{
		if ((nameColor != Config.NORMAL_NAME_COLOUR) && (nameColor != Config.CLANLEADER_NAME_COLOUR) && (nameColor != Config.GM_NAME_COLOUR) && (nameColor != Config.SERVICES_OFFLINE_TRADE_NAME_COLOR))
		{
			setVar("namecolor", Integer.toHexString(nameColor), -1);
		}
		else if (nameColor == Config.NORMAL_NAME_COLOUR)
		{
			unsetVar("namecolor");
		}
		_nameColor = nameColor;
	}

	public void setNameColor(int red, int green, int blue)
	{
		_nameColor = (red & 0xFF) + ((green & 0xFF) << 8) + ((blue & 0xFF) << 16);
		if ((_nameColor != Config.NORMAL_NAME_COLOUR) && (_nameColor != Config.CLANLEADER_NAME_COLOUR) && (_nameColor != Config.GM_NAME_COLOUR) && (_nameColor != Config.SERVICES_OFFLINE_TRADE_NAME_COLOR))
		{
			setVar("namecolor", Integer.toHexString(_nameColor), -1);
		}
		else
		{
			unsetVar("namecolor");
		}
	}

	public void setNameColor(String RGB)
	{
		if (RGB.length() == 6)
		{
			RGB = RGB.substring(4, 6) + RGB.substring(2, 4) + RGB.substring(0, 2);
		}
		setNameColor(Integer.decode("0x" + RGB));
	}

	private final Map<String, PlayerVar> user_variables = new ConcurrentHashMap<String, PlayerVar>();

	public void increaseWroteMessages(ChatType chatType)
	{
		if (!Config.RECORD_WROTE_CHAT_MSGS_COUNT)
		{
			return;
		}

		final String varName = "wroteMessages_" + chatType.toString();

		final int wroteMessages = getVarInt(varName, 0);
		setVarOffline(getObjectId(), varName, String.valueOf(wroteMessages + 1), -1);
	}

	public static void setVarOffline(int playerObjId, String name, String value, long expireDate)
	{
		Player player = World.getPlayer(playerObjId);
		if (player != null)
		{
			player.setVar(name, value, expireDate);
		}
		else
		{
			mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,?)", playerObjId, name, value, expireDate);
		}
	}

	public static void setVarOffline(int playerObjId, String name, int value, long expireDate)
	{
		setVarOffline(playerObjId, name, String.valueOf(value), expireDate);
	}

	public static void setVarOffline(int playerObjId, String name, int value)
	{
		setVarOffline(playerObjId, name, String.valueOf(value), -1);
	}

	public static void setVarOffline(int playerObjId, String name, long value, long expireDate)
	{
		setVarOffline(playerObjId, name, String.valueOf(value), expireDate);
	}

	public static void setVarOffline(int playerObjId, String name, long value)
	{
		setVarOffline(playerObjId, name, String.valueOf(value), -1);
	}

	public static void unsetVarOffline(int playerObjId, String name)
	{
		Player player = World.getPlayer(playerObjId);
		if (player != null)
		{
			player.unsetVar(name);
		}
		else
		{
			mysql.set("DELETE FROM `character_variables` WHERE `obj_id`=? AND `type`='user-var' AND `name`=? LIMIT 1", playerObjId, name);
		}
	}

	public void setVar(String name, String value, long expireDate)
	{
		if (user_variables.containsKey(name))
		{
			getVarObject(name).stopExpireTask();
		}

		user_variables.put(name, new PlayerVar(this, name, value, expireDate));
		mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,?)", getObjectId(), name, value, expireDate);
	}

	public void setVar(String name, String value)
	{
		setVar(name, value, -1);
	}

	public void setVar(String name, int value, long expireDate)
	{
		setVar(name, String.valueOf(value), expireDate);
	}

	public void setVar(String name, int value)
	{
		setVar(name, String.valueOf(value), -1);
	}

	public void setVar(String name, long value, long expireDate)
	{
		setVar(name, String.valueOf(value), expireDate);
	}

	public void setVar(String name, long value)
	{
		setVar(name, String.valueOf(value), -1);
	}

	public void unsetVar(String name)
	{
		if ((name == null) || isPhantom())
		{
			return;
		}

		PlayerVar pv = user_variables.remove(name);

		if (pv != null)
		{
			pv.stopExpireTask();
			mysql.set("DELETE FROM `character_variables` WHERE `obj_id`=? AND `type`='user-var' AND `name`=? LIMIT 1", getObjectId(), name);
		}
	}

	public String getVar(String name)
	{
		PlayerVar pv = getVarObject(name);

		if (pv == null)
		{
			return null;
		}

		return pv.getValue();
	}

	public long getVarTimeToExpire(String name)
	{
		try
		{
			return getVarObject(name).getTimeToExpire();
		}
		catch (NullPointerException npe)
		{

		}

		return 0;
	}

	public PlayerVar getVarObject(String name)
	{
		return user_variables.get(name);
	}

	public boolean getVarB(String name, boolean defaultVal)
	{
		PlayerVar pv = getVarObject(name);

		if (pv == null)
		{
			return defaultVal;
		}

		return pv.getValueBoolean();
	}

	public boolean getVarB(String name)
	{
		return getVarB(name, false);
	}

	public long getVarLong(String name)
	{
		return getVarLong(name, 0L);
	}

	public long getVarLong(String name, long defaultVal)
	{
		long result = defaultVal;
		String var = getVar(name);
		if (var != null)
		{
			result = Long.parseLong(var);
		}
		return result;
	}

	public int getVarInt(String name)
	{
		return getVarInt(name, 0);
	}

	public int getVarInt(String name, int defaultVal)
	{
		int result = defaultVal;
		String var = getVar(name);
		if (var != null)
		{
			result = Integer.parseInt(var);
		}
		return result;
	}

	public Map<String, PlayerVar> getVars()
	{
		return user_variables;
	}

	private void loadVariables(Connection con)
	{
		try (PreparedStatement offline = con.prepareStatement("SELECT * FROM character_variables WHERE obj_id = ?"))
		{
			offline.setInt(1, getObjectId());

			try (ResultSet rs = offline.executeQuery())
			{
				while (rs.next())
				{
					String name = rs.getString("name");
					String value = Strings.stripSlashes(rs.getString("value"));
					long expire_time = rs.getLong("expire_time");
					long curtime = System.currentTimeMillis();

					if ((expire_time <= curtime) && (expire_time > 0))
					{
						continue;
					}

					user_variables.put(name, new PlayerVar(this, name, value, expire_time));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while loading Character_variables for Id " + getObjectId(), e);
		}
	}

	public static String getVarFromPlayer(int objId, String var)
	{
		String value = null;

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement offline = con.prepareStatement("SELECT value FROM character_variables WHERE obj_id = ? AND name = ?"))
		{
			offline.setInt(1, objId);
			offline.setString(2, var);

			try (ResultSet rs = offline.executeQuery())
			{
				if (rs.next())
				{
					value = Strings.stripSlashes(rs.getString("value"));
				}
			}
		}
		catch (Exception e)
		{
			_log.error("Error while getting Variable from Player", e);
		}
		return value;
	}

	/**
	 * Adding Variable to Map<Name, Value>. It's not saved to database.
	 * Value can be taken back by {@link #getQuickVarO(String, Object...)} method.
	 * @param name key
	 * @param value value
	 */
	public void addQuickVar(String name, Object value)
	{
		if (quickVars.containsKey(name))
		{
			quickVars.remove(name);
		}
		quickVars.put(name, value);
	}

	/**
	 * Getting back String Value located in quickVars Map<Name, Value>.
	 * If value doesn't exist, defaultValue is returned.
	 * If value isn't String type, throws Error
	 * @param name key
	 * @param defaultValue Value returned when <code>name</code> key doesn't exist
	 * @return value
	 */
	public String getQuickVarS(String name, String... defaultValue)
	{
		if (!quickVars.containsKey(name))
		{
			if (defaultValue.length > 0)
			{
				return defaultValue[0];
			}
			return null;
		}
		return (String) quickVars.get(name);
	}

	/**
	 * Getting back String Value located in quickVars Map<Name, Value>.
	 * If value doesn't exist, defaultValue is returned.
	 * If value isn't Boolean type, throws Error
	 * @param name key
	 * @param defaultValue Value returned when <code>name</code> key doesn't exist
	 * @return value
	 */
	public boolean getQuickVarB(String name, boolean... defaultValue)
	{
		if (!quickVars.containsKey(name))
		{
			if (defaultValue.length > 0)
			{
				return defaultValue[0];
			}
			return false;
		}
		return ((Boolean) quickVars.get(name)).booleanValue();
	}

	/**
	 * Getting back Integer Value located in quickVars Map<Name, Value>.
	 * If value doesn't exist, defaultValue is returned.
	 * If value isn't Integer type, throws Error
	 * @param name key
	 * @param defaultValue Value returned when <code>name</code> key doesn't exist
	 * @return value
	 */
	public int getQuickVarI(String name, int... defaultValue)
	{
		if (!quickVars.containsKey(name))
		{
			if (defaultValue.length > 0)
			{
				return defaultValue[0];
			}
			return -1;
		}
		return ((Integer) quickVars.get(name)).intValue();
	}

	/**
	 * Getting back Long Value located in quickVars Map<Name, Value>.
	 * If value doesn't exist, defaultValue is returned.
	 * If value isn't Long type, throws Error
	 * @param name key
	 * @param defaultValue Value returned when <code>name</code> key doesn't exist
	 * @return value
	 */
	public long getQuickVarL(String name, long... defaultValue)
	{
		if (!quickVars.containsKey(name))
		{
			if (defaultValue.length > 0)
			{
				return defaultValue[0];
			}
			return -1L;
		}
		return ((Long) quickVars.get(name)).longValue();
	}

	/**
	 * Getting back Object Value located in quickVars Map<Name, Value>.
	 * If value doesn't exist, defaultValue is returned.
	 * @param name key
	 * @param defaultValue Value returned when <code>name</code> key doesn't exist
	 * @return value
	 */
	public Object getQuickVarO(String name, Object... defaultValue)
	{
		if (!quickVars.containsKey(name))
		{
			if (defaultValue.length > 0)
			{
				return defaultValue[0];
			}
			return null;
		}
		return quickVars.get(name);
	}

	/**
	 * Checking if quickVars Map<Name, Value> contains a name as a Key
	 * @param name key
	 * @return contains name
	 */
	public boolean containsQuickVar(String name)
	{
		return quickVars.containsKey(name);
	}

	/**
	 * Removing Key from quickVars Map
	 * @param name - key
	 */
	public void deleteQuickVar(String name)
	{
		quickVars.remove(name);
	}

	public String getQuickVarsToPrint()
	{
		final StringBuilder builder = new StringBuilder("Quick Vars[");
		for (Map.Entry<String, Object> entry : quickVars.entrySet())
		{
			builder.append(entry.getKey()).append(" - ").append(entry.getValue());
		}
		builder.append(']');
		return builder.toString();
	}

	/**
	 * Adding new Image Id to List of Images loaded by Game Client of this plater
	 * @param id of the image
	 */
	public void addLoadedImage(int id)
	{
		loadedImages.add(id);
	}

	/**
	 * Did Game Client already receive Custom Image from the server?
	 * @param id of the image
	 * @return client received image
	 */
	public boolean wasImageLoaded(int id)
	{
		return loadedImages.contains(id);
	}

	/**
	 * @return Number of Custom Images sent from Server to the Player
	 */
	public int getLoadedImagesSize()
	{
		return loadedImages.size();
	}

	public String getLang()
	{
		return "en";
	}

	public int getLangId()
	{
		String lang = getLang();
		if (lang.equalsIgnoreCase("en") || lang.equalsIgnoreCase("e") || lang.equalsIgnoreCase("eng"))
		{
			return LANG_ENG;
		}
		if (lang.equalsIgnoreCase("ru") || lang.equalsIgnoreCase("r") || lang.equalsIgnoreCase("rus"))
		{
			return LANG_RUS;
		}
		return LANG_UNK;
	}

	public Language getLanguage()
	{
		Player player = getPlayer();

		if ((player == null) || (player != null && player.isPhantom()))
		{
			return Language.ENGLISH;
		}

		String lang = getLang();
		if ((lang == null) || lang.equalsIgnoreCase("en") || lang.equalsIgnoreCase("e") || lang.equalsIgnoreCase("eng"))
		{
			return Language.ENGLISH;
		}
		if (lang.equalsIgnoreCase("ru") || lang.equalsIgnoreCase("r") || lang.equalsIgnoreCase("rus"))
		{
			return Language.RUSSIAN;
		}
		return Language.ENGLISH;
	}

	public boolean isLangRus()
	{
		return getLangId() == LANG_RUS;
	}

	public int isAtWarWith(Integer id)
	{
		return (_clan == null) || !_clan.isAtWarWith(id) ? 0 : 1;
	}

	public int isAtWar()
	{
		return (_clan == null) || (_clan.isAtWarOrUnderAttack() <= 0) ? 0 : 1;
	}

	public void stopWaterTask()
	{
		if (_taskWater != null)
		{
			_taskWater.cancel(false);
			_taskWater = null;
			sendPacket(new SetupGauge(this, SetupGauge.CYAN, 0));
			sendChanges();
		}
	}

	public void startWaterTask()
	{
		if (isDead())
		{
			stopWaterTask();
		}
		else if (Config.ALLOW_WATER && (_taskWater == null))
		{
			int timeinwater = (int) (calcStat(Stats.BREATH, 86, null, null) * 1000L);
			sendPacket(new SetupGauge(this, SetupGauge.CYAN, timeinwater));
			if ((getTransformation() > 0) && (getTransformationTemplate() > 0) && !isCursedWeaponEquipped())
			{
				setTransformation(0);
			}
			_taskWater = ThreadPoolManager.getInstance().scheduleAtFixedRate(new WaterTask(this), timeinwater, 1000L);
			sendChanges();
		}
	}

	public void doRevive(double percent)
	{
		restoreExp(percent);
		doRevive();
	}

	@Override
	public void doRevive()
	{
		super.doRevive();
		setAgathionRes(false);
		unsetVar("lostexp");
		updateEffectIcons();
		autoShot();

		// Prims - Block the community buffer 10 seconds so the player cannot buff when resurrected
		_resurrectionBuffBlockedTime = System.currentTimeMillis() + 10 * 1000;
	}

	public void reviveRequest(Player reviver, double percent, boolean pet)
	{
		ReviveAnswerListener reviveAsk = (_askDialog != null) && (_askDialog.getValue() instanceof ReviveAnswerListener) ? (ReviveAnswerListener) _askDialog.getValue() : null;
		if (reviveAsk != null)
		{
			if ((reviveAsk.isForPet() == pet) && (reviveAsk.getPower() >= percent))
			{
				reviver.sendPacket(Msg.BETTER_RESURRECTION_HAS_BEEN_ALREADY_PROPOSED);
				return;
			}
			if (pet && !reviveAsk.isForPet())
			{
				reviver.sendPacket(Msg.SINCE_THE_MASTER_WAS_IN_THE_PROCESS_OF_BEING_RESURRECTED_THE_ATTEMPT_TO_RESURRECT_THE_PET_HAS_BEEN_CANCELLED);
				return;
			}
			if (pet && isDead())
			{
				reviver.sendPacket(Msg.WHILE_A_PET_IS_ATTEMPTING_TO_RESURRECT_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER);
				return;
			}
		}

		if ((pet && getPet() != null && getPet().isDead()) || (!pet && isDead()))
		{
			ConfirmDlg pkt = new ConfirmDlg(SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, (pet ? 0 : 5 * 60 * 1000));
			pkt.addName(reviver).addString(Math.round(percent) + " percent");

			if (!isDualbox(reviver))
			{
				reviver.getCounters().playersRessurected++;
			}

			ask(pkt, new ReviveAnswerListener(this, percent, pet));
		}
	}

	/**
	 * @return Prims - Max time for the player to accept the resurrection request
	 */
	public long getResurrectionMaxTime()
	{
		return _resurrectionMaxTime;
	}

	/**
	 * @return Prims - Block time that the player cannot use the community buffer
	 */
	public long getResurrectionBuffBlockedTime()
	{
		return _resurrectionBuffBlockedTime;
	}

	public void summonCharacterRequest(Creature summoner, Location loc, int summonConsumeCrystal)
	{
		ConfirmDlg cd = new ConfirmDlg(SystemMsg.C1_WISHES_TO_SUMMON_YOU_FROM_S2, 60000);
		cd.addName(summoner).addZoneName(loc);

		ask(cd, new SummonAnswerListener(this, loc, summonConsumeCrystal));
	}

	public void scriptRequest(String text, String scriptName, Object[] args)
	{
		ask(new ConfirmDlg(SystemMsg.S1, 30000).addString(text), new ScriptAnswerListener(this, scriptName, args));
	}

	public void updateNoChannel(long time)
	{
		setNoChannel(time);

		final String stmt = "UPDATE characters SET nochannel = ? WHERE obj_Id=?";

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(stmt))
		{
			statement.setLong(1, _NoChannel > 0 ? _NoChannel / 1000 : _NoChannel);
			statement.setInt(2, getObjectId());
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			_log.warn("Could not activate nochannel:" + e);
		}

		sendPacket(new EtcStatusUpdate(this));
	}

	public boolean isJailed()
	{
		return getVar("jailed") != null;
	}

	private void checkRecom()
	{
		Calendar temp = Calendar.getInstance();
		temp.set(Calendar.HOUR_OF_DAY, 6);
		temp.set(Calendar.MINUTE, 30);
		temp.set(Calendar.SECOND, 0);
		temp.set(Calendar.MILLISECOND, 0);
		long count = Math.round(((System.currentTimeMillis() / 1000) - _lastAccess) / 86400);
		if ((count == 0) && (_lastAccess < (temp.getTimeInMillis() / 1000)) && (System.currentTimeMillis() > temp.getTimeInMillis()))
		{
			count++;
		}

		for (int i = 1; i < count; i++)
		{
			setRecomHave(getRecomHave() - 20);
		}

		if (count > 0)
		{
			restartRecom();
		}
	}

	public void restartRecom()
	{
		setRecomBonusTime(3600);
		setRecomLeftToday(0);
		setRecomLeft(20);
		setRecomHave(getRecomHave() - 20);
		stopRecomBonusTask(false);
		startRecomBonusTask();
		sendUserInfo(true);
		sendVoteSystemInfo();
	}

	@Override
	public boolean isInBoat()
	{
		return _boat != null;
	}

	public Boat getBoat()
	{
		return _boat;
	}

	public void setBoat(Boat boat)
	{
		_boat = boat;
	}

	public Location getInBoatPosition()
	{
		return _inBoatPosition;
	}

	public void setInBoatPosition(Location loc)
	{
		_inBoatPosition = loc;
	}

	public Map<Integer, SubClass> getSubClasses()
	{
		return _classlist;
	}

	public void setBaseClass(int baseClass)
	{
		_baseClass = baseClass;
	}

	public int getBaseClassId()
	{
		return _baseClass;
	}

	public void setActiveClass(SubClass activeClass)
	{
		_activeClass = activeClass;
	}

	public SubClass getActiveClass()
	{
		return _activeClass;
	}

	public int getActiveClassId()
	{
		if (getActiveClass() == null)
		{
			return -1;
		}

		return getActiveClass().getClassId();
	}

	/**
	 * Changing index of class in DB, used for changing class when finished professional quests
	 * @param oldclass
	 * @param newclass
	 */
	public synchronized void changeClassInDb(int oldclass, int newclass)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement statement = con.prepareStatement("UPDATE character_subclasses SET class_id=? WHERE char_obj_id=? AND class_id=?"))
			{
				statement.setInt(1, newclass);
				statement.setInt(2, getObjectId());
				statement.setInt(3, oldclass);
				statement.executeUpdate();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=?"))
			{
				statement.setInt(1, getObjectId());
				statement.setInt(2, newclass);
				statement.executeUpdate();
			}

			try (PreparedStatement statement = con.prepareStatement("UPDATE character_hennas SET class_index=? WHERE char_obj_id=? AND class_index=?"))
			{
				statement.setInt(1, newclass);
				statement.setInt(2, getObjectId());
				statement.setInt(3, oldclass);
				statement.executeUpdate();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND class_index=?"))
			{
				statement.setInt(1, getObjectId());
				statement.setInt(2, newclass);
				statement.executeUpdate();
			}

			try (PreparedStatement statement = con.prepareStatement("UPDATE character_shortcuts SET class_index=? WHERE object_id=? AND class_index=?"))
			{
				statement.setInt(1, newclass);
				statement.setInt(2, getObjectId());
				statement.setInt(3, oldclass);
				statement.executeUpdate();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=?"))
			{
				statement.setInt(1, getObjectId());
				statement.setInt(2, newclass);
				statement.executeUpdate();
			}

			try (PreparedStatement statement = con.prepareStatement("UPDATE character_skills SET class_index=? WHERE char_obj_id=? AND class_index=?"))
			{
				statement.setInt(1, newclass);
				statement.setInt(2, getObjectId());
				statement.setInt(3, oldclass);
				statement.executeUpdate();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id=? AND id=?"))
			{
				statement.setInt(1, getObjectId());
				statement.setInt(2, newclass);
				statement.executeUpdate();
			}

			try (PreparedStatement statement = con.prepareStatement("UPDATE character_effects_save SET id=? WHERE object_id=? AND id=?"))
			{
				statement.setInt(1, newclass);
				statement.setInt(2, getObjectId());
				statement.setInt(3, oldclass);
				statement.executeUpdate();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=?"))
			{
				statement.setInt(1, getObjectId());
				statement.setInt(2, newclass);
				statement.executeUpdate();
			}

			try (PreparedStatement statement = con.prepareStatement("UPDATE character_skills_save SET class_index=? WHERE char_obj_id=? AND class_index=?"))
			{
				statement.setInt(1, newclass);
				statement.setInt(2, getObjectId());
				statement.setInt(3, oldclass);
				statement.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while changing Class in Database", e);
		}
	}

	public void storeCharSubClasses()
	{
		SubClass main = getActiveClass();
		if (main != null)
		{
			main.setCp(getCurrentCp());
			// main.setExp(getExp());
			// main.setLevel(getLevel());
			// main.setSp(getSp());
			main.setHp(getCurrentHp());
			main.setMp(getCurrentMp());
			main.setActive(true);
			getSubClasses().put(getActiveClassId(), main);
		}
		else
		{
			_log.warn("Could not store char sub data, main class " + getActiveClassId() + " not found for " + this);
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); Statement statement = con.createStatement())
		{

			StringBuilder sb;
			for (SubClass subClass : getSubClasses().values())
			{
				sb = new StringBuilder("UPDATE character_subclasses SET ");
				sb.append("exp=").append(subClass.getExp()).append(",");
				sb.append("sp=").append(subClass.getSp()).append(",");
				sb.append("curHp=").append(subClass.getHp()).append(",");
				sb.append("curMp=").append(subClass.getMp()).append(",");
				sb.append("curCp=").append(subClass.getCp()).append(",");
				sb.append("level=").append(subClass.getLevel()).append(",");
				sb.append("active=").append(subClass.isActive() ? 1 : 0).append(",");
				sb.append("isBase=").append(subClass.isBase() ? 1 : 0).append(",");
				sb.append("death_penalty=").append(subClass.getDeathPenalty(this).getLevelOnSaveDB()).append(",");
				sb.append("certification='").append(subClass.getCertification()).append("'");
				sb.append(" WHERE char_obj_id=").append(getObjectId()).append(" AND class_id=").append(subClass.getClassId()).append(" LIMIT 1");
				statement.executeUpdate(sb.toString());
			}

			sb = new StringBuilder("UPDATE character_subclasses SET ");
			sb.append("maxHp=").append(getMaxHp()).append(",");
			sb.append("maxMp=").append(getMaxMp()).append(",");
			sb.append("maxCp=").append(getMaxCp());
			sb.append(" WHERE char_obj_id=").append(getObjectId()).append(" AND active=1 LIMIT 1");
			statement.executeUpdate(sb.toString());
		}
		catch (SQLException e)
		{
			_log.warn("Error while storing Char Subclasses", e);
		}
	}

	/**
	 * Restore list of character professions and set up active proof Used when character is loading
	 * @param player
	 * @param con
	 */
	public static void restoreCharSubClasses(Player player, Connection con)
	{
		try (PreparedStatement statement = con.prepareStatement("SELECT class_id,exp,sp,curHp,curCp,curMp,active,isBase,death_penalty,certification FROM character_subclasses WHERE char_obj_id=?"))
		{
			statement.setInt(1, player.getObjectId());
			SubClass activeSubclass = null;

			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					final SubClass subClass = new SubClass();
					subClass.setBase(rset.getInt("isBase") != 0);
					subClass.setClassId(rset.getInt("class_id"));
					subClass.setExp(rset.getLong("exp"));
					subClass.setSp(rset.getInt("sp"));
					subClass.setHp(rset.getDouble("curHp"));
					subClass.setMp(rset.getDouble("curMp"));
					subClass.setCp(rset.getDouble("curCp"));
					subClass.setDeathPenalty(new DeathPenalty(player, rset.getInt("death_penalty")));
					subClass.setCertification(rset.getInt("certification"));

					boolean active = rset.getInt("active") != 0;
					if (active)
					{
						activeSubclass = subClass;
					}
					player._classlist.put(subClass.getClassId(), subClass);
				}
			}
			if (player._classlist.isEmpty())
			{
				_log.error("Error! There are no subclasses for player: " + player);
				return;
			}

			int baseClassId = player._baseClass;
			if (baseClassId == -1)
			{
				_log.error("Error! There is no base class for player: " + player);
				return;
			}

			if (activeSubclass != null)
			{
				player.setActiveSubClass(activeSubclass.getClassId(), false);
			}

			if (player._activeClass == null)
			{
				final SubClass subClass = player._classlist.get(baseClassId);
				subClass.setActive(true);
				player.setActiveSubClass(subClass.getClassId(), false);
			}
		}
		catch (SQLException e)
		{
			_log.warn("Could not restore char sub-classes: ", e);
		}
	}

	public boolean addSubClass(int classId, boolean storeOld, int certification)
	{
		if (_classlist.size() >= Config.ALT_GAME_SUB_ADD)
		{
			return false;
		}
		/*
		 * if (_classlist.size() >= (4 + Config.ALT_GAME_SUB_ADD))
		 * {
		 * return false;
		 * }
		 */

		final ClassId newId = ClassId.VALUES[classId];

		final SubClass newClass = new SubClass();
		newClass.setBase(false);
		if (newId.getRace() == null)
		{
			return false;
		}

		newClass.setClassId(classId);
		newClass.setCertification(certification);

		_classlist.put(classId, newClass);

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("INSERT INTO character_subclasses (char_obj_id, class_id, exp, sp, curHp, curMp, curCp, maxHp, maxMp, maxCp, level, active, isBase, death_penalty, certification) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"))
		{
			// Store the basic info about this new sub-class.
			statement.setInt(1, getObjectId());
			statement.setInt(2, newClass.getClassId());
			statement.setLong(3, Experience.LEVEL[40]);
			statement.setInt(4, 0);
			statement.setDouble(5, getCurrentHp());
			statement.setDouble(6, getCurrentMp());
			statement.setDouble(7, getCurrentCp());
			statement.setDouble(8, getCurrentHp());
			statement.setDouble(9, getCurrentMp());
			statement.setDouble(10, getCurrentCp());
			statement.setInt(11, 40);
			statement.setInt(12, 0);
			statement.setInt(13, 0);
			statement.setInt(14, 0);
			statement.setInt(15, certification);
			statement.execute();
		}
		catch (Exception e)
		{
			_log.warn("Could not add character sub-class: " + e, e);
			return false;
		}

		setActiveSubClass(classId, storeOld);

		boolean countUnlearnable = true;
		int unLearnable = 0;

		Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL);
		while (skills.size() > unLearnable)
		{
			for (SkillLearn s : skills)
			{
				final Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
				if ((sk == null) || !sk.getCanLearn(newId))
				{
					if (countUnlearnable)
					{
						unLearnable++;
					}
					continue;
				}
				addSkill(sk, true);
			}
			countUnlearnable = false;
			skills = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL);
		}

		sendPacket(new SkillList(this));
		setCurrentHpMp(getMaxHp(), getMaxMp(), true);
		setCurrentCp(getMaxCp());
		return true;
	}

	public boolean modifySubClass(int oldClassId, int newClassId)
	{
		final SubClass originalClass = _classlist.get(oldClassId);
		if ((originalClass == null) || originalClass.isBase())
		{
			return false;
		}

		final int certification = originalClass.getCertification();

		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{

			// Remove all basic info stored about this sub-class.
			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_subclasses WHERE char_obj_id=? AND class_id=? AND isBase = 0"))
			{
				statement.setInt(1, getObjectId());
				statement.setInt(2, oldClassId);
				statement.execute();
			}

			// Remove all skill info stored for this sub-class.
			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=? "))
			{
				statement.setInt(1, getObjectId());
				statement.setInt(2, oldClassId);
				statement.execute();
			}

			// Remove all saved skills info stored for this sub-class.
			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=? "))
			{
				statement.setInt(1, getObjectId());
				statement.setInt(2, oldClassId);
				statement.execute();
			}

			// Remove all saved effects stored for this sub-class.
			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id=? AND id=? "))
			{
				statement.setInt(1, getObjectId());
				statement.setInt(2, oldClassId);
				statement.execute();
			}

			// Remove all henna info stored for this sub-class.
			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=? "))
			{
				statement.setInt(1, getObjectId());
				statement.setInt(2, oldClassId);
				statement.execute();
			}

			// Remove all shortcuts info stored for this sub-class.
			try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND class_index=? "))
			{
				statement.setInt(1, getObjectId());
				statement.setInt(2, oldClassId);
				statement.execute();
			}
		}
		catch (Exception e)
		{
			_log.warn("Error while deleting char sub-class", e);
		}

		_classlist.remove(oldClassId);

		return (newClassId <= 0) || addSubClass(newClassId, false, certification);
	}

	public void setActiveSubClass(int subId, boolean store)
	{
		final SubClass sub = _classlist.get(subId);
		if (sub == null)
		{
			return;
		}

		if (isInDuel())
		{
			sendMessage("Unable to perform during a duel!");
			return;
		}

		// Fix for Cancel exploit
		CancelTaskManager.getInstance().cancelPlayerTasks(this);

		if (_activeClass != null)
		{
			EffectsDAO.getInstance().insert(this);
			storeDisableSkills();

			if (QuestManager.getQuest(422) != null)
			{
				String qn = QuestManager.getQuest(422).getName();
				if (qn != null)
				{
					QuestState qs = getQuestState(qn);
					if (qs != null)
					{
						qs.exitCurrentQuest(true);
					}
				}
			}
		}

		if (store)
		{
			final SubClass oldsub = _activeClass;
			oldsub.setCp(getCurrentCp());
			// oldsub.setExp(getExp());
			// oldsub.setLevel(getLevel());
			// oldsub.setSp(getSp());
			oldsub.setHp(getCurrentHp());
			oldsub.setMp(getCurrentMp());
			oldsub.setActive(false);
			_classlist.put(getActiveClassId(), oldsub);
		}

		sub.setActive(true);
		_activeClass = sub;
		_classlist.put(getActiveClassId(), sub);

		setClassId(subId, false, false);

		removeAllSkills();

		getEffectList().stopAllEffects();

		if ((_summon != null) && (_summon.isSummon() || (Config.ALT_IMPROVED_PETS_LIMITED_USE && (((_summon.getNpcId() == PetDataTable.IMPROVED_BABY_KOOKABURRA_ID) && !isMageClass()) || ((_summon.getNpcId() == PetDataTable.IMPROVED_BABY_BUFFALO_ID) && isMageClass())))))
		{
			_summon.unSummon();
		}

		setAgathion(0);

		restoreSkills();
		rewardSkills(false);
		checkSkills();
		sendPacket(new ExStorageMaxCount(this));

		refreshExpertisePenalty();

		sendPacket(new SkillList(this));

		_inventory.refreshEquip();
		_inventory.validateItems();

		for (int i = 0; i < 3; i++)
		{
			_henna[i] = null;
		}

		restoreHenna();
		sendPacket(new HennaInfo(this));

		EffectsDAO.getInstance().restoreEffects(this, true, sub.getHp(), sub.getCp(), sub.getMp());
		restoreDisableSkills();

		setCurrentHpMp(sub.getHp(), sub.getMp());
		setCurrentCp(sub.getCp());

		_shortCuts.restore();
		sendPacket(new ShortCutInit(this));
		for (int shotId : _activeSoulShots)
		{
			sendPacket(new ExAutoSoulShot(shotId, true));
		}
		sendPacket(new SkillCoolTime(this));

		broadcastPacket(new SocialAction(getObjectId(), SocialAction.LEVEL_UP));

		getDeathPenalty().restore(this);

		setIncreasedForce(0);

		startHourlyTask();

		broadcastCharInfo();
		updateEffectIcons();
		updateStats();
	}

	public void startKickTask(long delayMillis)
	{
		stopKickTask();
		_kickTask = ThreadPoolManager.getInstance().schedule(new KickTask(this), delayMillis);
	}

	public void stopKickTask()
	{
		if (_kickTask != null)
		{
			_kickTask.cancel(false);
			_kickTask = null;
		}
	}

	public Future<?> getExpiration()
	{
		return _bonusExpiration;
	}

	public void setExpiration(Future<?> expiration)
	{
		_bonusExpiration = expiration;
	}

	public void stopBonusTask(boolean silence)
	{
		PremiumEnd.getInstance().stopBonusTask(this, silence);
	}

	public void startBonusTask()
	{
		if (Config.SERVICES_RATE_TYPE != Bonus.NO_BONUS)
		{
			int bonusExpire = _connection.getBonusExpire();
			double bonus = _connection.getBonus();
			if (bonusExpire > (System.currentTimeMillis() / 1000L))
			{
				_bonus.setRateXp(Config.SERVICES_BONUS_XP * bonus);
				_bonus.setRateSp(Config.SERVICES_BONUS_SP * bonus);
				_bonus.setDropAdena(Config.SERVICES_BONUS_ADENA * bonus);
				_bonus.setDropItems(Config.SERVICES_BONUS_ITEMS * bonus);
				_bonus.setDropSpoil(Config.SERVICES_BONUS_SPOIL * bonus);
				_bonus.setBonusExpire(bonusExpire);

				if (_bonusExpiration == null)
				{
					_bonusExpiration = LazyPrecisionTaskManager.getInstance().startBonusExpirationTask(this);
				}
			}
		}
	}

	public void stopBonusTask()
	{
		if (_bonusExpiration != null)
		{
			_bonusExpiration.cancel(false);
			_bonusExpiration = null;
		}
	}

	@Override
	public int getInventoryLimit()
	{
		return (int) calcStat(Stats.INVENTORY_LIMIT, 0.0, null, null);
	}

	public int getWarehouseLimit()
	{
		return (int) calcStat(Stats.STORAGE_LIMIT, 0.0, null, null);
	}

	public int getTradeLimit()
	{
		return (int) calcStat(Stats.TRADE_LIMIT, 0.0, null, null);
	}

	public int getDwarvenRecipeLimit()
	{
		return (int) calcStat(Stats.DWARVEN_RECIPE_LIMIT, 50.0, null, null) + Config.ALT_ADD_RECIPES;
	}

	public int getCommonRecipeLimit()
	{
		return (int) calcStat(Stats.COMMON_RECIPE_LIMIT, 50.0, null, null) + Config.ALT_ADD_RECIPES;
	}

	public Element getAttackElement()
	{
		return Formulas.getAttackElement(this, null);
	}

	public int getAttack(Element element)
	{
		if (element == Element.NONE)
		{
			return 0;
		}
		return (int) calcStat(element.getAttack(), 0., null, null);
	}

	public int getDefence(Element element)
	{
		if (element == Element.NONE)
		{
			return 0;
		}
		return (int) calcStat(element.getDefence(), 0., null, null);
	}

	public boolean getAndSetLastItemAuctionRequest()
	{
		if ((_lastItemAuctionInfoRequest + 2000L) < System.currentTimeMillis())
		{
			_lastItemAuctionInfoRequest = System.currentTimeMillis();
			return true;
		}
		else
		{
			_lastItemAuctionInfoRequest = System.currentTimeMillis();
			return false;
		}
	}

	@Override
	public int getNpcId()
	{
		return -2;
	}

	public GameObject getVisibleObject(int id)
	{
		if (getObjectId() == id)
		{
			return this;
		}

		GameObject target = null;

		if (getTargetId() == id)
		{
			target = getTarget();
		}

		if ((target == null) && (_party != null))
		{
			for (Player p : _party.getMembers())
			{
				if ((p != null) && (p.getObjectId() == id))
				{
					target = p;
					break;
				}
			}
		}

		if (target == null)
		{
			target = World.getAroundObjectById(this, id);
		}

		return (target == null) || target.isInvisible() ? null : target;
	}

	@Override
	public int getPAtk(Creature target)
	{
		double init = getActiveWeaponInstance() == null ? (isMageClass() ? 3 : 4) : 0;
		return (int) calcStat(Stats.POWER_ATTACK, init, target, null);
	}

	@Override
	public int getPDef(Creature target)
	{
		double init = 4.; // empty cloak and underwear slots

		final ItemInstance chest = getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		if (chest == null)
		{
			init += isMageClass() ? ArmorTemplate.EMPTY_BODY_MYSTIC : ArmorTemplate.EMPTY_BODY_FIGHTER;
		}
		if ((getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS) == null) && ((chest == null) || (chest.getBodyPart() != ItemTemplate.SLOT_FULL_ARMOR)))
		{
			init += isMageClass() ? ArmorTemplate.EMPTY_LEGS_MYSTIC : ArmorTemplate.EMPTY_LEGS_FIGHTER;
		}

		if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD) == null)
		{
			init += ArmorTemplate.EMPTY_HELMET;
		}
		if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES) == null)
		{
			init += ArmorTemplate.EMPTY_GLOVES;
		}
		if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET) == null)
		{
			init += ArmorTemplate.EMPTY_BOOTS;
		}

		return (int) calcStat(Stats.POWER_DEFENCE, init, target, null);
	}

	@Override
	public int getMDef(Creature target, Skill skill)
	{
		double init = 0.;

		if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR) == null)
		{
			init += ArmorTemplate.EMPTY_EARRING;
		}
		if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR) == null)
		{
			init += ArmorTemplate.EMPTY_EARRING;
		}
		if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK) == null)
		{
			init += ArmorTemplate.EMPTY_NECKLACE;
		}
		if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER) == null)
		{
			init += ArmorTemplate.EMPTY_RING;
		}
		if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER) == null)
		{
			init += ArmorTemplate.EMPTY_RING;
		}

		return (int) calcStat(Stats.MAGIC_DEFENCE, init, target, skill);
	}

	public boolean isSubClassActive()
	{
		return getBaseClassId() != getActiveClassId();
	}

	@Override
	public String getTitle()
	{
		return super.getTitle();
	}

	public int getTitleColor()
	{
		return _titlecolor;
	}

	public void setTitleColor(int titlecolor)
	{
		if (titlecolor != DEFAULT_TITLE_COLOR)
		{
			setVar("titlecolor", Integer.toHexString(titlecolor), -1);
		}
		else
		{
			unsetVar("titlecolor");
		}
		_titlecolor = titlecolor;
	}

	@Override
	public boolean isCursedWeaponEquipped()
	{
		return _cursedWeaponEquippedId != 0;
	}

	public void setCursedWeaponEquippedId(int value)
	{
		_cursedWeaponEquippedId = value;
	}

	public int getCursedWeaponEquippedId()
	{
		return _cursedWeaponEquippedId;
	}

	@Override
	public boolean isImmobilized()
	{
		return super.isImmobilized() || isOverloaded() || isSitting() || isFishing();
	}

	@Override
	public boolean isBlocked()
	{
		return super.isBlocked() || isInMovie() || isInObserverMode() || isTeleporting() || isLogoutStarted();
	}

	@Override
	public boolean isInvul()
	{
		return super.isInvul() || isInMovie() || isSpawnProtected();
	}

	@Override
	public void unblock()
	{
		super.unblock();

		// Synerge - Send an action failed packet after unblocking the character. This is because many packets are not read when the char is blocked and the client could be blocked too
		sendPacket(ActionFail.STATIC);
	}

	/**
	 * Synerge. This is needed because we only want to check the block, not any other different status that causes block
	 * @return Returns if the player is blocked, doesnt take into consideration any other blocking status like oberserver, movie, etc, only real block
	 */
	public final boolean isFullBlocked()
	{
		return super.isBlocked();
	}

	/**
	 * if True, the L2Player can't take more item
	 * @param overloaded
	 */
	public void setOverloaded(boolean overloaded)
	{
		_overloaded = overloaded;
	}

	public boolean isOverloaded()
	{
		return _overloaded;
	}

	public boolean isFishing()
	{
		return _isFishing;
	}

	public Fishing getFishing()
	{
		return _fishing;
	}

	public void setFishing(boolean value)
	{
		_isFishing = value;
	}

	public void startFishing(FishTemplate fish, int lureId)
	{
		_fishing.setFish(fish);
		_fishing.setLureId(lureId);
		_fishing.startFishing();
	}

	public void stopFishing()
	{
		_fishing.stopFishing();
	}

	public Location getFishLoc()
	{
		return _fishing.getFishLoc();
	}

	public Bonus getBonus()
	{
		return _bonus;
	}

	public boolean hasBonus()
	{
		return _bonus.getBonusExpire() > (System.currentTimeMillis() / 1000L);
	}

	@Override
	public double getRateAdena()
	{
		return _party == null ? _bonus.getDropAdena() : _party._rateAdena;
	}

	@Override
	public double getRateItems()
	{
		return _party == null ? _bonus.getDropItems() : _party._rateDrop;
	}

	@Override
	public double getRateExp()
	{
		return calcStat(Stats.EXP, (_party == null ? _bonus.getRateXp() : _party._rateExp), null, null);
	}

	@Override
	public double getRateSp()
	{
		return calcStat(Stats.SP, (_party == null ? _bonus.getRateSp() : _party._rateSp), null, null);
	}

	@Override
	public double getRateSpoil()
	{
		return _party == null ? _bonus.getDropSpoil() : _party._rateSpoil;
	}

	@Override
	public double getRateSiege()
	{
		return _party == null ? _bonus.getDropSiege() : _party._rateSiege;
	}

	private boolean _maried = false;
	private int _partnerId = 0;
	private int _coupleId = 0;
	private boolean _maryrequest = false;
	private boolean _maryaccepted = false;

	public boolean isMaried()
	{
		return _maried;
	}

	public void setMaried(boolean state)
	{
		_maried = state;
	}

	public void setMaryRequest(boolean state)
	{
		_maryrequest = state;
	}

	public boolean isMaryRequest()
	{
		return _maryrequest;
	}

	public void setMaryAccepted(boolean state)
	{
		_maryaccepted = state;
	}

	public boolean isMaryAccepted()
	{
		return _maryaccepted;
	}

	public int getPartnerId()
	{
		return _partnerId;
	}

	public void setPartnerId(int partnerid)
	{
		_partnerId = partnerid;
	}

	public int getCoupleId()
	{
		return _coupleId;
	}

	public void setCoupleId(int coupleId)
	{
		_coupleId = coupleId;
	}

	public void setUndying(boolean val)
	{
		if (!isGM())
		{
			return;
		}
		_isUndying = val;
	}

	public boolean isUndying()
	{
		return _isUndying;
	}

	private final GArray<Player> _snoopListener = new GArray<Player>();
	private final GArray<Player> _snoopedPlayer = new GArray<Player>();

	public void broadcastSnoop(int type, String name, String _text)
	{
		if (_snoopListener.size() > 0)
		{
			Snoop sn = new Snoop(getObjectId(), getName(), type, name, _text);
			for (Player pci : _snoopListener)
			{
				if (pci != null)
				{
					pci.sendPacket(sn);
				}
			}
		}
	}

	public void addSnooper(Player pci)
	{
		if (!_snoopListener.contains(pci))
		{
			_snoopListener.add(pci);
		}
	}

	public void removeSnooper(Player pci)
	{
		_snoopListener.remove(pci);
	}

	public void addSnooped(Player pci)
	{
		if (!_snoopedPlayer.contains(pci))
		{
			_snoopedPlayer.add(pci);
		}
	}

	public void removeSnooped(Player pci)
	{
		_snoopedPlayer.remove(pci);
	}

	public void resetReuse()
	{
		_skillReuses.clear();
		_sharedGroupReuses.clear();
	}

	public DeathPenalty getDeathPenalty()
	{
		return _activeClass == null ? null : _activeClass.getDeathPenalty(this);
	}

	private boolean _charmOfCourage = false;

	public boolean isCharmOfCourage()
	{
		return _charmOfCourage;
	}

	public void setCharmOfCourage(boolean val)
	{
		_charmOfCourage = val;

		if (!val)
		{
			getEffectList().stopEffect(Skill.SKILL_CHARM_OF_COURAGE);
		}

		sendEtcStatusUpdate();
	}

	private int _increasedForce = 0;
	private int _consumedSouls = 0;

	@Override
	public int getIncreasedForce()
	{
		return _increasedForce;
	}

	@Override
	public int getConsumedSouls()
	{
		return _consumedSouls;
	}

	@Override
	public void setConsumedSouls(int i, NpcInstance monster)
	{
		if (i == _consumedSouls)
		{
			return;
		}

		int max = (int) calcStat(Stats.SOULS_LIMIT, 0, monster, null);

		if (i > max)
		{
			i = max;
		}

		if (i <= 0)
		{
			_consumedSouls = 0;
			sendEtcStatusUpdate();
			return;
		}

		if (_consumedSouls != i)
		{
			int diff = i - _consumedSouls;
			if (diff > 0)
			{
				SystemMessage sm = new SystemMessage(SystemMessage.YOUR_SOUL_HAS_INCREASED_BY_S1_SO_IT_IS_NOW_AT_S2);
				sm.addNumber(diff);
				sm.addNumber(i);
				sendPacket(sm);
			}
		}
		else if (max == i)
		{
			sendPacket(Msg.SOUL_CANNOT_BE_ABSORBED_ANY_MORE);
			return;
		}

		_consumedSouls = i;
		sendPacket(new EtcStatusUpdate(this));
	}

	@Override
	public void setIncreasedForce(int i)
	{
		i = Math.min(i, Charge.MAX_CHARGE);
		i = Math.max(i, 0);

		if ((i != 0) && (i > _increasedForce))
		{
			sendPacket(new SystemMessage(SystemMessage.YOUR_FORCE_HAS_INCREASED_TO_S1_LEVEL).addNumber(i));
		}

		_increasedForce = i;
		sendEtcStatusUpdate();
	}

	private long _lastFalling;

	public boolean isFalling()
	{
		return (System.currentTimeMillis() - _lastFalling) < 5000;
	}

	public void falling(int height)
	{
		if (!Config.DAMAGE_FROM_FALLING || isDead() || isFlying() || isInWater() || isInBoat())
		{
			return;
		}
		_lastFalling = System.currentTimeMillis();
		int damage = (int) calcStat(Stats.FALL, (getMaxHp() / 2000) * height, null, null);
		if (damage > 0)
		{
			int curHp = (int) getCurrentHp();
			if ((curHp - damage) < 1)
			{
				setCurrentHp(1, false);
			}
			else
			{
				setCurrentHp(curHp - damage, false);
			}
			sendPacket(new SystemMessage(SystemMessage.YOU_RECEIVED_S1_DAMAGE_FROM_TAKING_A_HIGH_FALL).addNumber(damage));
		}
	}

	@Override
	public void checkHpMessages(double curHp, double newHp)
	{
		int[] _hp =
		{
			30,
			30
		};
		int[] skills =
		{
			290,
			291
		};

		int[] _effects_skills_id =
		{
			139,
			176,
			292,
			292,
			420
		};
		int[] _effects_hp =
		{
			30,
			30,
			30,
			60,
			30
		};

		double percent = getMaxHp() / 100;
		double _curHpPercent = curHp / percent;
		double _newHpPercent = newHp / percent;
		boolean needsUpdate = false;

		// check for passive skills
		for (int i = 0; i < skills.length; i++)
		{
			int level = getSkillLevel(skills[i]);
			if (level > 0)
			{
				if ((_curHpPercent > _hp[i]) && (_newHpPercent <= _hp[i]))
				{
					sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(skills[i], level));
					needsUpdate = true;
				}
				else if ((_curHpPercent <= _hp[i]) && (_newHpPercent > _hp[i]))
				{
					sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR).addSkillName(skills[i], level));
					needsUpdate = true;
				}
			}
		}

		// check for active effects
		for (Integer i = 0; i < _effects_skills_id.length; i++)
		{
			if (getEffectList().getEffectsBySkillId(_effects_skills_id[i]) != null)
			{
				if ((_curHpPercent > _effects_hp[i]) && (_newHpPercent <= _effects_hp[i]))
				{
					sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(_effects_skills_id[i], 1));
					needsUpdate = true;
				}
				else if ((_curHpPercent <= _effects_hp[i]) && (_newHpPercent > _effects_hp[i]))
				{
					sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR).addSkillName(_effects_skills_id[i], 1));
					needsUpdate = true;
				}
			}
		}

		if (needsUpdate)
		{
			sendChanges();
		}
	}

	public void checkDayNightMessages()
	{
		int level = getSkillLevel(294);
		if (level > 0)
		{
			if (GameTimeController.getInstance().isNowNight())
			{
				sendPacket(new SystemMessage(SystemMessage.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(294, level));
			}
			else
			{
				sendPacket(new SystemMessage(SystemMessage.IT_IS_DAWN_AND_THE_EFFECT_OF_S1_WILL_NOW_DISAPPEAR).addSkillName(294, level));
			}
		}
		sendChanges();
	}

	public int getZoneMask()
	{
		return _zoneMask;
	}

	@Override
	protected void onUpdateZones(List<Zone> leaving, List<Zone> entering)
	{
		super.onUpdateZones(leaving, entering);

		if (((leaving == null) || leaving.isEmpty()) && ((entering == null) || entering.isEmpty()))
		{
			return;
		}

		boolean lastInCombatZone = (_zoneMask & ZONE_PVP_FLAG) == ZONE_PVP_FLAG;
		boolean lastInDangerArea = (_zoneMask & ZONE_ALTERED_FLAG) == ZONE_ALTERED_FLAG;
		boolean lastOnSiegeField = (_zoneMask & ZONE_SIEGE_FLAG) == ZONE_SIEGE_FLAG;
		boolean lastInPeaceZone = (_zoneMask & ZONE_PEACE_FLAG) == ZONE_PEACE_FLAG;
		@SuppressWarnings("unused")
		boolean lastInSSQZone = (_zoneMask & ZONE_SSQ_FLAG) == ZONE_SSQ_FLAG;

		boolean isInCombatZone = isInCombatZone();
		boolean isInDangerArea = isInDangerArea();
		boolean isOnSiegeField = isOnSiegeField();
		boolean isInPeaceZone = isInPeaceZone();
		boolean isInSSQZone = isInSSQZone();

		// update the compass, only if the character in the world
		int lastZoneMask = _zoneMask;
		_zoneMask = 0;

		if (isInCombatZone)
		{
			_zoneMask |= ZONE_PVP_FLAG;
		}
		if (isInDangerArea)
		{
			_zoneMask |= ZONE_ALTERED_FLAG;
		}
		if (isOnSiegeField)
		{
			_zoneMask |= ZONE_SIEGE_FLAG;
		}
		if (isInPeaceZone)
		{
			_zoneMask |= ZONE_PEACE_FLAG;
		}
		if (isInSSQZone)
		{
			_zoneMask |= ZONE_SSQ_FLAG;
		}

		if (lastZoneMask != _zoneMask)
		{
			sendPacket(new ExSetCompassZoneCode(this));
		}

		if (lastInCombatZone != isInCombatZone)
		{
			broadcastRelationChanged();
		}

		if (lastInDangerArea != isInDangerArea)
		{
			sendPacket(new EtcStatusUpdate(this));
		}

		if (lastOnSiegeField != isOnSiegeField)
		{
			broadcastRelationChanged();
			if (isOnSiegeField)
			{
				sendPacket(Msg.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
			}
			else
			{
				final FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
				if (attachment != null)
				{
					attachment.onOutTerritory(this);
				}
				sendPacket(Msg.YOU_HAVE_LEFT_A_COMBAT_ZONE);
				if (!isTeleporting() && (getPvpFlag() == 0))
				{
					startPvPFlag(null);
				}
			}
		}

		if (lastInPeaceZone != isInPeaceZone)
		{
			if (isInPeaceZone)
			{
				setRecomTimerActive(false);
				if (getNevitSystem().isActive())
				{
					getNevitSystem().stopAdventTask(true);
				}
				startVitalityTask();
			}
			else
			{
				stopVitalityTask();
			}
		}

		if (isInWater())
		{
			startWaterTask();
		}
		else
		{
			stopWaterTask();
		}
	}

	public void startAutoSaveTask()
	{
		if (!Config.AUTOSAVE)
		{
			return;
		}
		if (_autoSaveTask == null)
		{
			_autoSaveTask = AutoSaveManager.getInstance().addAutoSaveTask(this);
		}
	}

	public void stopAutoSaveTask()
	{
		if (_autoSaveTask != null)
		{
			_autoSaveTask.cancel(false);
		}
		_autoSaveTask = null;
	}

	public void startVitalityTask()
	{
		if (!Config.ALT_VITALITY_ENABLED)
		{
			return;
		}
		if (_vitalityTask == null)
		{
			_vitalityTask = LazyPrecisionTaskManager.getInstance().addVitalityRegenTask(this);
		}
	}

	public void stopVitalityTask()
	{
		if (_vitalityTask != null)
		{
			_vitalityTask.cancel(false);
		}
		_vitalityTask = null;
	}

	public void startPcBangPointsTask()
	{
		if (!Config.ALT_PCBANG_POINTS_ENABLED || (Config.ALT_PCBANG_POINTS_DELAY <= 0))
		{
			return;
		}
		if (_pcCafePointsTask == null)
		{
			_pcCafePointsTask = LazyPrecisionTaskManager.getInstance().addPCCafePointsTask(this);
		}
	}

	public void stopPcBangPointsTask()
	{
		if (_pcCafePointsTask != null)
		{
			_pcCafePointsTask.cancel(false);
		}
		_pcCafePointsTask = null;
	}

	public final boolean isInJail()
	{
		return getVarB("jailed");
	}

	@Override
	public void sendMessage(String message)
	{
		sendPacket(new SystemMessage(message));
	}

	private Location _lastClientPosition;
	private Location _lastServerPosition;

	public void setLastClientPosition(Location position)
	{
		_lastClientPosition = position;
	}

	public Location getLastClientPosition()
	{
		return _lastClientPosition;
	}

	public void setLastServerPosition(Location position)
	{
		_lastServerPosition = position;
	}

	public Location getLastServerPosition()
	{
		return _lastServerPosition;
	}

	public void setHwidLock(String hwid)
	{
		_hwidLock = hwid;
	}

	public String getHwidLock()
	{
		return _hwidLock;
	}

	private int _useSeed = 0;

	public void setUseSeed(int id)
	{
		_useSeed = id;
	}

	public int getUseSeed()
	{
		return _useSeed;
	}

	public int getFriendRelation()
	{
		int result = 0;

		result |= RelationChanged.RELATION_CLAN_MEMBER;
		result |= RelationChanged.RELATION_CLAN_MATE;

		return result;
	}

	public int getWarRelation()
	{
		int result = 0;

		result |= RelationChanged.RELATION_CLAN_MEMBER;
		result |= RelationChanged.RELATION_1SIDED_WAR;
		result |= RelationChanged.RELATION_MUTUAL_WAR;

		return result;
	}

	public int getRelation(Player target)
	{
		if (getTeam() != TeamType.NONE && target.getTeam() != TeamType.NONE)
		{
			return getTeam() == target.getTeam() ? getFriendRelation() : getWarRelation();
		}

		int result = 0;

		if (getClan() != null)
		{
			result |= RelationChanged.RELATION_CLAN_MEMBER;
			if (getClan() == target.getClan())
			{
				result |= RelationChanged.RELATION_CLAN_MATE;
			}
			if (getClan().getAllyId() != 0)
			{
				result |= RelationChanged.RELATION_ALLY_MEMBER;
				if (getClan().getAllyId() == target.getAllyId())
				{
					result |= RelationChanged.RELATION_ALLY_MATE;
				}
			}
		}

		if (isClanLeader())
		{
			result |= RelationChanged.RELATION_LEADER;
		}

		Party party = getParty();
		if (party != null)
		{
			result |= RelationChanged.RELATION_HAS_PARTY;
			if (party.isInCommandChannel())
			{
				result |= RelationChanged.RELATION_CC_MEMBER;
			}

			if (party == target.getParty())
			{
				result |= RelationChanged.RELATION_PARTY_MATE;
				switch (party.getMembers().indexOf(this))
				{
				case 0:
					result |= RelationChanged.RELATION_PARTYLEADER; // 0x10
					break;
				case 1:
					result |= RelationChanged.RELATION_PARTY4; // 0x8
					break;
				case 2:
					result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1; // 0x7
					break;
				case 3:
					result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2; // 0x6
					break;
				case 4:
					result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY1; // 0x5
					break;
				case 5:
					result |= RelationChanged.RELATION_PARTY3; // 0x4
					break;
				case 6:
					result |= RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1; // 0x3
					break;
				case 7:
					result |= RelationChanged.RELATION_PARTY2; // 0x2
					break;
				case 8:
					result |= RelationChanged.RELATION_PARTY1; // 0x1
					break;
				}
			}
			else if (getPlayerGroup() == target.getPlayerGroup()) // Command Channel check
			{
				result |= RelationChanged.RELATION_CC_MATE;
			}
		}

		Clan clan1 = getClan();
		Clan clan2 = target.getClan();
		if ((clan1 != null) && (clan2 != null))
		{
			if ((target.getPledgeType() != Clan.SUBUNIT_ACADEMY) && (getPledgeType() != Clan.SUBUNIT_ACADEMY))
			{
				if (clan2.isAtWarWith(clan1.getClanId()))
				{
					result |= RelationChanged.RELATION_1SIDED_WAR;
					if (clan1.isAtWarWith(clan2.getClanId()))
					{
						result |= RelationChanged.RELATION_MUTUAL_WAR;
					}
				}
			}
			if (getBlockCheckerArena() != -1)
			{
				result |= RelationChanged.RELATION_INSIEGE;
				ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(getBlockCheckerArena());
				if (holder.getPlayerTeam(this) == 0)
				{
					result |= RelationChanged.RELATION_ENEMY;
				}
				else
				{
					result |= RelationChanged.RELATION_ALLY;
				}
				result |= RelationChanged.RELATION_ATTACKER;
			}
		}

		for (GlobalEvent e : getEvents())
		{
			result = e.getRelation(this, target, result);
		}

		return result;
	}

	/**
	 * 0=White, 1=Purple, 2=PurpleBlink
	 */
	protected int _pvpFlag;

	private ScheduledFuture<?> _PvPRegTask;
	private long _lastPvpAttack;

	public long getlastPvpAttack()
	{
		return _lastPvpAttack;
	}

	@Override
	public void startPvPFlag(Creature target)
	{
		if ((_karma > 0) || isInTournament())
		{
			return;
		}

		long startTime = System.currentTimeMillis();
		if (target != null && (target.getPvpFlag() != 0 || target.isMonster()))
		{
			startTime -= Config.PVP_TIME / 2;
		}

		if (_pvpFlag != 0 && _lastPvpAttack > startTime)
		{
			return;
		}

		_lastPvpAttack = startTime;

		updatePvPFlag(1);

		if (_PvPRegTask == null)
		{
			_PvPRegTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PvPFlagTask(this), 1000, 1000);
		}
	}

	public void stopPvPFlag()
	{
		if (_PvPRegTask != null)
		{
			_PvPRegTask.cancel(false);
			_PvPRegTask = null;
		}
		updatePvPFlag(0);
	}

	public void updatePvPFlag(int value)
	{
		if ((_handysBlockCheckerEventArena != -1) || (_pvpFlag == value))
		{
			return;
		}

		setPvpFlag(value);

		sendStatusUpdate(true, true, StatusUpdate.PVP_FLAG);

		broadcastRelationChanged();
	}

	public void setPvpFlag(int pvpFlag)
	{
		_pvpFlag = pvpFlag;
	}

	/**
	 * @return The remaning time of the pvp flag in miliseconds.
	 */
	public int getPvpFlagRemaningTime()
	{
		if (_PvPRegTask == null || _PvPRegTask.isDone())
		{
			return 0;
		}

		return (int) _PvPRegTask.getDelay(TimeUnit.MILLISECONDS);
	}

	@Override
	public int getPvpFlag()
	{
		return _pvpFlag;
	}

	public boolean isInDuel()
	{
		return getEvent(DuelEvent.class) != null;
	}

	public boolean isRegisteredInFightClub()
	{
		return getEvent(AbstractFightClub.class) != null;
	}

	public boolean isInFightClub()
	{
		try
		{
			if (getEvent(AbstractFightClub.class) == null)
			{
				return false;
			}

			return getEvent(AbstractFightClub.class).getFightClubPlayer(this) != null;
		}
		catch (NullPointerException e)
		{
			return false;
		}
	}

	public FightClubGameRoom getFightClubGameRoom()
	{
		return _fightClubGameRoom;
	}

	public void setFightClubGameRoom(FightClubGameRoom room)
	{
		_fightClubGameRoom = room;
	}

	public AbstractFightClub getFightClubEvent()
	{
		return getEvent(AbstractFightClub.class);
	}

	private final Map<Integer, TamedBeastInstance> _tamedBeasts = new ConcurrentHashMap<Integer, TamedBeastInstance>();

	public Map<Integer, TamedBeastInstance> getTrainedBeasts()
	{
		return _tamedBeasts;
	}

	public void addTrainedBeast(TamedBeastInstance tamedBeast)
	{
		_tamedBeasts.put(tamedBeast.getObjectId(), tamedBeast);
	}

	public void removeTrainedBeast(int npcId)
	{
		_tamedBeasts.remove(npcId);
	}

	private long _lastAttackPacket = 0;

	public long getLastAttackPacket()
	{
		return _lastAttackPacket;
	}

	public void setLastAttackPacket()
	{
		_lastAttackPacket = System.currentTimeMillis();
	}

	private long _lastMovePacket = 0;

	public long getLastMovePacket()
	{
		return _lastMovePacket;
	}

	public void setLastMovePacket()
	{
		_lastMovePacket = System.currentTimeMillis();
	}

	public byte[] getKeyBindings()
	{
		return _keyBindings;
	}

	public void setKeyBindings(byte[] keyBindings)
	{
		if (keyBindings == null)
		{
			_keyBindings = ArrayUtils.EMPTY_BYTE_ARRAY;
		}
		else
		{
			_keyBindings = keyBindings;
		}
	}

	public void setTransformation(int transformationId)
	{
		if ((transformationId == _transformationId) || ((_transformationId != 0) && (transformationId != 0)))
		{
			return;
		}

		if (transformationId == 0)
		{
			for (Effect effect : getEffectList().getAllEffects())
			{
				if ((effect != null) && (effect.getEffectType() == EffectType.Transformation))
				{
					if (effect.calc() == 0)
					{
						continue;
					}
					effect.exit();
					preparateToTransform(effect.getSkill());
					break;
				}
			}

			if (!_transformationSkills.isEmpty())
			{
				for (Skill s : _transformationSkills.values())
				{
					if (!s.isCommon() && !SkillAcquireHolder.getInstance().isSkillPossible(this, s) && !s.isHeroic())
					{
						super.removeSkill(s);
					}
				}
				_transformationSkills.clear();
			}
		}
		else
		{
			int _id = 0;
			int _level = 1;
			switch (getBaseClassId())
			{
			case 97:// Cardinal
				_id = 24001;
				break;
			case 98:// Hierophant
				_id = 24002;
				break;
			case 100:// SwordMuse
				_id = 24003;
				break;
			case 105:// EvaSaint
				_id = 24004;
				break;
			case 107:// SpectralDancer
				_id = 24005;
				break;
			case 112:// ShillienSaint
				_id = 24006;
				break;
			case 115:// Dominator
				_id = 24007;
				break;
			case 116:// Doomcryer
				_id = 24008;
				break;
			}

			Skill _skill = SkillTable.getInstance().getInfo(_id, _level);
			if (_skill != null)
			{
				super.removeSkill(_skill);
				removeSkillFromShortCut(_skill.getId());
			}

			if (!isCursedWeaponEquipped())
			{
				for (Effect effect : getEffectList().getAllEffects())
				{
					if ((effect != null) && (effect.getEffectType() == EffectType.Transformation))
					{
						if ((effect.getSkill() instanceof Transformation) && ((Transformation) effect.getSkill()).isDisguise)
						{
							for (Skill s : getAllSkills())
							{
								if ((s != null) && (s.isActive() || s.isToggle()))
								{
									_transformationSkills.put(s.getId(), s);
								}
							}
						}
						else
						{
							for (AddedSkill s : effect.getSkill().getAddedSkills())
							{
								if (s.level == 0)
								{
									int s2 = getSkillLevel(s.id);
									if (s2 > 0)
									{
										_transformationSkills.put(s.id, SkillTable.getInstance().getInfo(s.id, s2));
									}
								}
								else if (s.level == -2) // XXX: wild heartburn for skills depending on the player's level
								{
									int learnLevel = Math.max(effect.getSkill().getMagicLevel(), 40);
									int maxLevel = SkillTable.getInstance().getBaseLevel(s.id);
									int curSkillLevel = 1;
									if (maxLevel > 3)
									{
										curSkillLevel += getLevel() - learnLevel;
									}
									else
									{
										curSkillLevel += (getLevel() - learnLevel) / ((76 - learnLevel) / maxLevel);
									}
									curSkillLevel = Math.min(Math.max(curSkillLevel, 1), maxLevel);
									_transformationSkills.put(s.id, SkillTable.getInstance().getInfo(s.id, curSkillLevel));
								}
								else
								{
									_transformationSkills.put(s.id, s.getSkill());
								}
							}
						}
						preparateToTransform(effect.getSkill());
						break;
					}
				}
			}
			else
			{
				preparateToTransform(null);
			}

			if (!isInOlympiadMode() && !isCursedWeaponEquipped() && _hero && (getBaseClassId() == getActiveClassId()))
			{
				_transformationSkills.put(395, SkillTable.getInstance().getInfo(395, 1));
				_transformationSkills.put(396, SkillTable.getInstance().getInfo(396, 1));
				_transformationSkills.put(1374, SkillTable.getInstance().getInfo(1374, 1));
				_transformationSkills.put(1375, SkillTable.getInstance().getInfo(1375, 1));
				_transformationSkills.put(1376, SkillTable.getInstance().getInfo(1376, 1));
			}

			for (Skill s : _transformationSkills.values())
			{
				addSkill(s, false);
			}
		}

		_transformationId = transformationId;

		sendPacket(new ExBasicActionList(this));
		sendPacket(new SkillList(this));
		sendPacket(new ShortCutInit(this));
		for (int shotId : getAutoSoulShot())
		{
			sendPacket(new ExAutoSoulShot(shotId, true));
		}
		broadcastUserInfo(true);
	}

	private void preparateToTransform(Skill transSkill)
	{
		if ((transSkill == null) || !transSkill.isBaseTransformation())
		{
			for (Effect effect : getEffectList().getAllEffects())
			{
				if ((effect != null) && effect.getSkill().isToggle())
				{
					effect.exit();
				}
			}
		}
	}

	public boolean isInFlyingTransform()
	{
		return (_transformationId == 8) || (_transformationId == 9) || (_transformationId == 260);
	}

	public boolean isInMountTransform()
	{
		return (_transformationId == 106) || (_transformationId == 109) || (_transformationId == 110) || (_transformationId == 20001);
	}

	public int getTransformation()
	{
		return _transformationId;
	}

	public String getTransformationName()
	{
		return _transformationName;
	}

	public void setTransformationName(String name)
	{
		_transformationName = name;
	}

	public void setTransformationTemplate(int template)
	{
		_transformationTemplate = template;
	}

	public int getTransformationTemplate()
	{
		return _transformationTemplate;
	}

	@Override
	public final Collection<Skill> getAllSkills()
	{
		if (_transformationId == 0)
		{
			return super.getAllSkills();
		}

		Map<Integer, Skill> tempSkills = new HashMap<Integer, Skill>();
		for (Skill s : super.getAllSkills())
		{
			if ((s != null) && !s.isActive() && !s.isToggle())
			{
				tempSkills.put(s.getId(), s);
			}
		}
		tempSkills.putAll(_transformationSkills);
		return tempSkills.values();
	}

	public void setAgathion(int id)
	{
		if (_agathionId == id)
		{
			return;
		}

		_agathionId = id;
		broadcastCharInfo();
	}

	public int getAgathionId()
	{
		return _agathionId;
	}

	public int getPcBangPoints()
	{
		return _pcBangPoints;
	}

	public void setPcBangPoints(int val)
	{
		_pcBangPoints = val;
	}

	public void addPcBangPoints(int count, boolean doublePoints)
	{
		if (doublePoints)
		{
			count *= 2;
		}

		_pcBangPoints += count;

		sendPacket(new SystemMessage(doublePoints ? SystemMessage.DOUBLE_POINTS_YOU_AQUIRED_S1_PC_BANG_POINT : SystemMessage.YOU_ACQUIRED_S1_PC_BANG_POINT).addNumber(count));
		sendPacket(new ExPCCafePointInfo(this, count, 1, 2, 12));
	}

	public boolean reducePcBangPoints(int count)
	{
		if (_pcBangPoints < count)
		{
			return false;
		}

		_pcBangPoints -= count;
		sendPacket(new SystemMessage(SystemMessage.YOU_ARE_USING_S1_POINT).addNumber(count));
		sendPacket(new ExPCCafePointInfo(this, 0, 1, 2, 12));
		return true;
	}

	private Location _groundSkillLoc;

	public void setGroundSkillLoc(Location location)
	{
		_groundSkillLoc = location;
	}

	public Location getGroundSkillLoc()
	{
		return _groundSkillLoc;
	}

	public boolean isLogoutStarted()
	{
		return _isLogout.get();
	}

	public void setOfflineMode(boolean val)
	{
		if (!val)
		{
			unsetVar("offline");
		}
		_offline = val;
	}

	public boolean isInOfflineMode()
	{
		return _offline;
	}

	public void saveTradeList()
	{
		StringBuilder tradeListBuilder = new StringBuilder();

		if ((_sellList == null) || _sellList.isEmpty())
		{
			unsetVar("selllist");
		}
		else
		{
			for (TradeItem i : _sellList)
			{
				tradeListBuilder.append(i.getObjectId()).append(";").append(i.getCount()).append(";").append(i.getOwnersPrice()).append(":");
			}
			setVar("selllist", tradeListBuilder.toString(), -1);
			tradeListBuilder.delete(0, tradeListBuilder.length());
			if ((_tradeList != null) && (getSellStoreName() != null))
			{
				setVar("sellstorename", getSellStoreName(), -1);
			}
		}

		if ((_packageSellList == null) || _packageSellList.isEmpty())
		{
			unsetVar("packageselllist");
		}
		else
		{
			for (TradeItem i : _packageSellList)
			{
				tradeListBuilder.append(i.getObjectId()).append(";").append(i.getCount()).append(";").append(i.getOwnersPrice()).append(":");
			}
			setVar("packageselllist", tradeListBuilder.toString(), -1);
			tradeListBuilder.delete(0, tradeListBuilder.length());
			if ((_tradeList != null) && (getSellStoreName() != null))
			{
				setVar("sellstorename", getSellStoreName(), -1);
			}
		}

		if ((_buyList == null) || _buyList.isEmpty())
		{
			unsetVar("buylist");
		}
		else
		{
			for (TradeItem i : _buyList)
			{
				tradeListBuilder.append(i.getItemId()).append(";").append(i.getCount()).append(";").append(i.getOwnersPrice()).append(":");
			}
			setVar("buylist", tradeListBuilder.toString(), -1);
			tradeListBuilder.delete(0, tradeListBuilder.length());
			if ((_tradeList != null) && (getBuyStoreName() != null))
			{
				setVar("buystorename", getBuyStoreName(), -1);
			}
		}

		if ((_createList == null) || _createList.isEmpty())
		{
			unsetVar("createlist");
		}
		else
		{
			for (ManufactureItem i : _createList)
			{
				tradeListBuilder.append(i.getRecipeId()).append(";").append(i.getCost()).append(":");
			}
			setVar("createlist", tradeListBuilder.toString(), -1);
			if (getManufactureName() != null)
			{
				setVar("manufacturename", getManufactureName(), -1);
			}
		}
	}

	public void restoreTradeList()
	{
		String var;
		var = getVar("selllist");
		if (var != null)
		{
			_sellList = new CopyOnWriteArrayList<TradeItem>();
			String[] items = var.split(":");
			for (String item : items)
			{
				if (item.equals(""))
				{
					continue;
				}
				String[] values = item.split(";");
				if (values.length < 3)
				{
					continue;
				}

				int oId = Integer.parseInt(values[0]);
				long count = Long.parseLong(values[1]);
				long price = Long.parseLong(values[2]);

				ItemInstance itemToSell = getInventory().getItemByObjectId(oId);

				if ((count < 1) || (itemToSell == null))
				{
					continue;
				}

				if (count > itemToSell.getCount())
				{
					count = itemToSell.getCount();
				}

				TradeItem i = new TradeItem(itemToSell);
				i.setCount(count);
				i.setOwnersPrice(price);

				_sellList.add(i);
			}
			var = getVar("sellstorename");
			if (var != null)
			{
				if (Config.containsAbuseWord(var))
				{
					var = "Sell";
				}

				setSellStoreName(var);
			}
		}
		var = getVar("packageselllist");
		if (var != null)
		{
			_packageSellList = new CopyOnWriteArrayList<TradeItem>();
			String[] items = var.split(":");
			for (String item : items)
			{
				if (item.equals(""))
				{
					continue;
				}
				String[] values = item.split(";");
				if (values.length < 3)
				{
					continue;
				}

				int oId = Integer.parseInt(values[0]);
				long count = Long.parseLong(values[1]);
				long price = Long.parseLong(values[2]);

				ItemInstance itemToSell = getInventory().getItemByObjectId(oId);

				if ((count < 1) || (itemToSell == null))
				{
					continue;
				}

				if (count > itemToSell.getCount())
				{
					count = itemToSell.getCount();
				}

				TradeItem i = new TradeItem(itemToSell);
				i.setCount(count);
				i.setOwnersPrice(price);

				_packageSellList.add(i);
			}
			var = getVar("sellstorename");
			if (var != null)
			{
				if (Config.containsAbuseWord(var))
				{
					var = "Sell Package";
				}

				setSellStoreName(var);
			}
		}
		var = getVar("buylist");
		if (var != null)
		{
			_buyList = new CopyOnWriteArrayList<TradeItem>();
			String[] items = var.split(":");
			for (String item : items)
			{
				if (item.equals(""))
				{
					continue;
				}
				String[] values = item.split(";");
				if (values.length < 3)
				{
					continue;
				}
				TradeItem i = new TradeItem();
				i.setItemId(Integer.parseInt(values[0]));
				i.setCount(Long.parseLong(values[1]));
				i.setOwnersPrice(Long.parseLong(values[2]));
				_buyList.add(i);
			}
			var = getVar("buystorename");
			if (var != null)
			{
				if (Config.containsAbuseWord(var))
				{
					var = "Buy";
				}

				setBuyStoreName(var);
			}
		}
		var = getVar("createlist");
		if (var != null)
		{
			_createList = new CopyOnWriteArrayList<ManufactureItem>();
			String[] items = var.split(":");
			for (String item : items)
			{
				if (item.equals(""))
				{
					continue;
				}
				String[] values = item.split(";");
				if (values.length < 2)
				{
					continue;
				}
				int recId = Integer.parseInt(values[0]);
				long price = Long.parseLong(values[1]);
				if (findRecipe(recId))
				{
					_createList.add(new ManufactureItem(recId, price));
				}
			}
			var = getVar("manufacturename");
			if (var != null)
			{
				if (Config.containsAbuseWord(var))
				{
					var = "Manufacture";
				}

				setManufactureName(var);
			}
		}
	}

	public void restoreRecipeBook(Connection con)
	{
		try (PreparedStatement statement = con.prepareStatement("SELECT id FROM character_recipebook WHERE char_id=?"))
		{
			statement.setInt(1, getObjectId());

			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					int id = rset.getInt("id");
					Recipe recipe = RecipeHolder.getInstance().getRecipeByRecipeId(id);
					registerRecipe(recipe, false);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while restoring Recipe Book for Id " + getObjectId(), e);
		}
	}

	public DecoyInstance getDecoy()
	{
		return _decoy;
	}

	public void setDecoy(DecoyInstance decoy)
	{
		_decoy = decoy;
	}

	public int getMountType()
	{
		switch (getMountNpcId())
		{
		case PetDataTable.STRIDER_WIND_ID:
		case PetDataTable.STRIDER_STAR_ID:
		case PetDataTable.STRIDER_TWILIGHT_ID:
		case PetDataTable.RED_STRIDER_WIND_ID:
		case PetDataTable.RED_STRIDER_STAR_ID:
		case PetDataTable.RED_STRIDER_TWILIGHT_ID:
		case PetDataTable.GUARDIANS_STRIDER_ID:
			return 1;
		case PetDataTable.WYVERN_ID:
			return 2;
		case PetDataTable.WGREAT_WOLF_ID:
		case PetDataTable.FENRIR_WOLF_ID:
		case PetDataTable.WFENRIR_WOLF_ID:
			return 3;
		case 32: // Jet Bike
		case 13130: // Light Purple Maned Horse
		case 13146: // Tawny-Maned Lion
		case 13147: // Steam Sledge
			return 4;
		}
		return 0;
	}

	@Override
	public double getColRadius()
	{
		if (getTransformation() != 0)
		{
			if (getTransformationTemplate() == 32)
			{
				setTransformation(0);
			}
			else
			{
				final int template = getTransformationTemplate();
				if (template != 0)
				{
					final NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(template);
					if (npcTemplate != null)
					{
						return npcTemplate.collisionRadius;
					}
				}
			}
		}
		else if (isMounted())
		{
			final int mountTemplate = getMountNpcId();
			if (mountTemplate != 0)
			{
				final NpcTemplate mountNpcTemplate = NpcHolder.getInstance().getTemplate(mountTemplate);
				if (mountNpcTemplate != null)
				{
					return mountNpcTemplate.collisionRadius;
				}
			}
		}
		return getBaseTemplate().collisionRadius;
	}

	@Override
	public double getColHeight()
	{
		if (getTransformation() != 0)
		{
			if (getTransformationTemplate() == 32)
			{
				setTransformation(0);
			}
			else
			{
				final int template = getTransformationTemplate();
				if (template != 0)
				{
					final NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(template);
					if (npcTemplate != null)
					{
						return npcTemplate.collisionHeight;
					}
				}
			}
		}
		else if (isMounted())
		{
			final int mountTemplate = getMountNpcId();
			if (mountTemplate != 0)
			{
				final NpcTemplate mountNpcTemplate = NpcHolder.getInstance().getTemplate(mountTemplate);
				if (mountNpcTemplate != null)
				{
					return mountNpcTemplate.collisionHeight;
				}
			}
		}
		return getBaseTemplate().collisionHeight;
	}

	@Override
	public void setReflection(Reflection reflection)
	{
		if (getReflection() == reflection)
		{
			return;
		}

		super.setReflection(reflection);

		if ((_summon != null) && !_summon.isDead())
		{
			_summon.setReflection(reflection);
		}

		if (reflection != ReflectionManager.DEFAULT)
		{
			String var = getVar("reflection");
			if ((var == null) || !var.equals(String.valueOf(reflection.getId())))
			{
				setVar("reflection", String.valueOf(reflection.getId()), -1);
			}
		}
		else
		{
			unsetVar("reflection");
		}

		if (getActiveClass() != null)
		{
			getInventory().validateItems();
			// _129_PailakaDevilsLegacy
			if ((getPet() != null) && ((getPet().getNpcId() == 14916) || (getPet().getNpcId() == 14917)))
			{
				getPet().unSummon();
			}
		}
	}

	public boolean isTerritoryFlagEquipped()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		return (weapon != null) && weapon.getTemplate().isTerritoryFlag();
	}

	private int _buyListId;

	public void setBuyListId(int listId)
	{
		_buyListId = listId;
	}

	public int getBuyListId()
	{
		return _buyListId;
	}

	public int getFame()
	{
		return _fame;
	}

	public void setFame(int fame, String log)
	{
		fame = Math.min(Config.LIM_FAME, fame);
		if ((log != null) && !log.isEmpty())
		{
			Log.add(_name + "|" + (fame - _fame) + "|" + fame + "|" + log, "fame");
		}
		if (fame > _fame)
		{
			int added = fame - _fame;
			getCounters().fameAcquired += added;
			sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_REPUTATION_SCORE).addNumber(added));
		}

		// Synerge - Add the fame acquired to the stats
//		if (fame > _fame)
//			addPlayerStats(Ranking.STAT_TOP_FAME_ACQUIRED, fame - _fame);

		_fame = fame;
		sendChanges();
	}

	public void setFame(int fame)
	{
		fame = Math.min(Config.LIM_FAME, fame);
		if (fame > _fame)
		{
			sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_REPUTATION_SCORE).addNumber(fame - _fame));
		}

		// Synerge - Add the fame acquired to the stats
//		if (fame > _fame)
//			addPlayerStats(Ranking.STAT_TOP_FAME_ACQUIRED, fame - _fame);

		_fame = fame;
		sendChanges();
	}

	public int getVitalityLevel(boolean blessActive)
	{
		return Config.ALT_VITALITY_ENABLED ? (blessActive ? 4 : _vitalityLevel) : 0;
	}

	public double getVitality()
	{
		return Config.ALT_VITALITY_ENABLED ? _vitality : 0;
	}

	public void addVitality(double val)
	{
		setVitality(getVitality() + val);
	}

	public void setVitality(double newVitality)
	{
		if (!Config.ALT_VITALITY_ENABLED)
		{
			return;
		}

		newVitality = Math.max(Math.min(newVitality, Config.VITALITY_LEVELS[4]), 0);

		if ((newVitality >= _vitality) || (getLevel() >= 10))
		{
			if (newVitality != _vitality)
			{
				if (newVitality == 0)
				{
					sendPacket(Msg.VITALITY_IS_FULLY_EXHAUSTED);
				}
				else if (newVitality == Config.VITALITY_LEVELS[4])
				{
					sendPacket(Msg.YOUR_VITALITY_IS_AT_MAXIMUM);
				}
			}

			_vitality = newVitality;
		}

		int newLevel = 0;
		if (_vitality >= Config.VITALITY_LEVELS[3])
		{
			newLevel = 4;
		}
		else if (_vitality >= Config.VITALITY_LEVELS[2])
		{
			newLevel = 3;
		}
		else if (_vitality >= Config.VITALITY_LEVELS[1])
		{
			newLevel = 2;
		}
		else if (_vitality >= Config.VITALITY_LEVELS[0])
		{
			newLevel = 1;
		}

		if (_vitalityLevel > newLevel)
		{
			getNevitSystem().addPoints(1500);
		}

		if (_vitalityLevel != newLevel)
		{
			if (_vitalityLevel != -1)
			{
				sendPacket(newLevel < _vitalityLevel ? Msg.VITALITY_HAS_DECREASED : Msg.VITALITY_HAS_INCREASED);
			}
			_vitalityLevel = newLevel;
		}

		sendPacket(new ExVitalityPointInfo((int) _vitality));
	}

	private final int _incorrectValidateCount = 0;

	public int getIncorrectValidateCount()
	{
		return _incorrectValidateCount;
	}

	public int setIncorrectValidateCount(int count)
	{
		return _incorrectValidateCount;
	}

	public int getExpandInventory()
	{
		return _expandInventory;
	}

	public void setExpandInventory(int inventory)
	{
		_expandInventory = inventory;
	}

	public int getExpandWarehouse()
	{
		return _expandWarehouse;
	}

	public void setExpandWarehouse(int warehouse)
	{
		_expandWarehouse = warehouse;
	}

	public boolean isNotShowBuffAnim()
	{
		return _notShowBuffAnim;
	}

	public void setNotShowBuffAnim(boolean value)
	{
		_notShowBuffAnim = value;
	}

	public List<SchemeBufferInstance.PlayerScheme> getBuffSchemes()
	{
		return buffSchemes;
	}

	public SchemeBufferInstance.PlayerScheme getBuffSchemeById(int id)
	{
		for (SchemeBufferInstance.PlayerScheme scheme : buffSchemes)
		{
			if (scheme.schemeId == id)
			{
				return scheme;
			}
		}
		return null;
	}

	public SchemeBufferInstance.PlayerScheme getBuffSchemeByName(String name)
	{
		for (SchemeBufferInstance.PlayerScheme scheme : buffSchemes)
		{
			if (scheme.schemeName.equals(name))
			{
				return scheme;
			}
		}
		return null;
	}

	public void enterMovieMode()
	{
		if (isInMovie())
		{
			return;
		}

		setTarget(null);
		stopMove();
		setIsInMovie(true);
		sendPacket(new CameraMode(1));
		sendPacket(new Revive(this));
	}

	public void leaveMovieMode()
	{
		setIsInMovie(false);
		sendPacket(new CameraMode(0));
		broadcastCharInfo();
	}

	public void specialCamera(GameObject target, int dist, int yaw, int pitch, int time, int duration)
	{
		sendPacket(new SpecialCamera(target.getObjectId(), dist, yaw, pitch, time, duration));
	}

	public void specialCamera(GameObject target, int dist, int yaw, int pitch, int time, int duration, int turn, int rise, int widescreen, int unk)
	{
		sendPacket(new SpecialCamera(target.getObjectId(), dist, yaw, pitch, time, duration, turn, rise, widescreen, unk));
	}

	private int _movieId = 0;
	private boolean _isInMovie;

	public void setMovieId(int id)
	{
		_movieId = id;
	}

	public int getMovieId()
	{
		return _movieId;
	}

	public boolean isInMovie()
	{
		return _isInMovie;
	}

	public void setIsInMovie(boolean state)
	{
		_isInMovie = state;
	}

	public void showQuestMovie(SceneMovie movie)
	{
		if (isInMovie())
		{
			return;
		}

		sendActionFailed();
		setTarget(null);
		stopMove();
		setMovieId(movie.getId());
		setIsInMovie(true);
		sendPacket(movie.packet(this));
	}

	public void showQuestMovie(int movieId)
	{
		if (isInMovie())
		{
			return;
		}

		sendActionFailed();
		setTarget(null);
		stopMove();
		setMovieId(movieId);
		setIsInMovie(true);
		sendPacket(new ExStartScenePlayer(movieId));
	}

	public void setAutoLoot(boolean enable)
	{
		if (Config.AUTO_LOOT_INDIVIDUAL)
		{
			_autoLoot = enable;
			setVar("AutoLoot", String.valueOf(enable), -1);
		}
	}

	public void setAutoLootHerbs(boolean enable)
	{
		if (Config.AUTO_LOOT_INDIVIDUAL)
		{
			AutoLootHerbs = enable;
			setVar("AutoLootHerbs", String.valueOf(enable), -1);
		}
	}

	public void setAutoLootOnlyAdena(boolean enable)
	{
		if (Config.AUTO_LOOT_INDIVIDUAL)
		{
			AutoLootOnlyAdena = enable;
			setVar("AutoLootOnlyAdena", String.valueOf(enable), -1);
		}
	}

	public boolean isAutoLootEnabled()
	{
		return _autoLoot;
	}

	public boolean isAutoLootHerbsEnabled()
	{
		return AutoLootHerbs;
	}

	public boolean isAutoLootOnlyAdenaEnabled()
	{
		return AutoLootOnlyAdena;
	}

	public final void reName(String name, boolean saveToDB)
	{
		setName(name);
		if (saveToDB)
		{
			saveNameToDB();
		}
		broadcastCharInfo();
	}

	public final void reName(String name)
	{
		reName(name, false);
	}

	public final void saveNameToDB()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement st = con.prepareStatement("UPDATE characters SET char_name = ? WHERE obj_Id = ?"))
		{
			st.setString(1, getName());
			st.setInt(2, getObjectId());
			st.executeUpdate();
		}
		catch (SQLException e)
		{
			_log.error("Error while saving Char Name", e);
		}
	}

	@Override
	public Player getPlayer()
	{
		return this;
	}

	private List<String> getStoredBypasses(BypassType type)
	{
		return bypasses.get(type);
	}

	public void cleanBypasses(BypassType type)
	{
		final List<String> bypassStorage = getStoredBypasses(type);
		synchronized (bypassStorage)
		{
			bypassStorage.clear();
		}
	}

	public String encodeBypasses(String htmlCode, BypassType type)
	{
		final List<String> bypassStorage = getStoredBypasses(type);
		synchronized (bypassStorage)
		{
			return BypassManager.encode(this, htmlCode, bypassStorage, type);
		}
	}

	public DecodedBypass decodeBypass(String bypass)
	{
		final EncodingType bpType = BypassManager.getBypassType(bypass);
		final BypassType bypassType = BypassType.getBypassByEncoding(bpType);
		if (bypassType != null)
		{
			final List<String> bypassStorage = getStoredBypasses(bypassType);
			return BypassManager.decode(bypass, bypassStorage, bypassType, this);
		}

		if (bpType == EncodingType.SIMPLE)
		{
			return new DecodedBypass(bypass, BypassType.NPC).trim();
		}
		if (bpType == EncodingType.SIMPLE_BBS && !bypass.startsWith("_bbsscripts"))
		{
			return new DecodedBypass(bypass, BypassType.COMMUNITY).trim();
		}
		if (bpType == EncodingType.SIMPLE_DIRECT)
		{
			final DecodedBypass decodedBypass = BypassHandler.getInstance().tryDecodeSimpleDirect(bypass);
			if (decodedBypass != null)
			{
				return decodedBypass;
			}
		}

		if (isKickableBypass(bypass))
		{
			Log.logIllegalActivity(toString() + " used Kickable Bypass " + bypass + ". Kicked out of the game!");
			if (getAccessLevel() <= 0)
			{
				kick();
			}
			return null;
		}

		final ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(bypass);
		if (handler != null)
		{
			if (!ArrayUtils.contains(ConfigHolder.getStringArray("AllowedDirectSimpleBypasses"), bypass))
			{
				Log.logIllegalActivity(toString() + " Used Simple Direct Bypass: " + bypass);
			}
			return new DecodedBypass(bypass, BypassType.COMMUNITY, handler).trim();
		}

		Log.logIllegalActivity("Direct access to bypass: " + bypass + " / Player: " + getName());
		return null;
	}

	private static boolean isKickableBypass(String bypass)
	{
		for (String kickableBypass : ConfigHolder.getStringArray("DirectSimpleKickableBypasses"))
		{
			if (bypass.startsWith(kickableBypass))
			{
				return true;
			}
		}
		return false;
	}

	public int getTalismanCount()
	{
		return (int) calcStat(Stats.TALISMANS_LIMIT, 0, null, null);
	}

	public boolean getOpenCloak()
	{
		if (Config.ALT_OPEN_CLOAK_SLOT || isGM())
		{
			return true;
		}
		return (int) calcStat(Stats.CLOAK_SLOT, 0, null, null) > 0;
	}

	public final void disableDrop(int time)
	{
		_dropDisabled = System.currentTimeMillis() + time;
	}

	public final boolean isDropDisabled()
	{
		return _dropDisabled > System.currentTimeMillis();
	}

	private ItemInstance _petControlItem = null;

	public void setPetControlItem(int itemObjId)
	{
		setPetControlItem(getInventory().getItemByObjectId(itemObjId));
	}

	public void setPetControlItem(ItemInstance item)
	{
		_petControlItem = item;
	}

	public ItemInstance getPetControlItem()
	{
		return _petControlItem;
	}

	private long _lastNotAfkTime = 0;

	public void isntAfk()
	{
		_lastNotAfkTime = System.currentTimeMillis();
	}

	public long getLastNotAfkTime()
	{
		return _lastNotAfkTime;
	}

	private final AtomicBoolean isActive = new AtomicBoolean();

	public long lastActive = 0;

	public boolean isActive()
	{
		return isActive.get();
	}

	public void setActive()
	{
		setNonAggroTime(0);

		isntAfk();

		lastActive = System.currentTimeMillis();

		if (isActive.getAndSet(true))
		{
			return;
		}

		onActive();
	}

	private void onActive()
	{
		setNonAggroTime(0L);

		if (!isRegisteredInFightClub())
		{
			sendPacket(Msg.YOU_ARE_PROTECTED_AGGRESSIVE_MONSTERS);
		}

		if (getPetControlItem() != null)
		{
			ThreadPoolManager.getInstance().execute(new RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					if (getPetControlItem() != null)
					{
						summonPet();
					}
				}

			});
		}
	}

	public void summonPet()
	{
		if (getPet() != null)
		{
			return;
		}

		ItemInstance controlItem = getPetControlItem();
		if (controlItem == null)
		{
			return;
		}

		int npcId = PetDataTable.getSummonId(controlItem);
		if (npcId == 0)
		{
			return;
		}

		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if (petTemplate == null)
		{
			return;
		}

		PetInstance pet = PetInstance.restore(controlItem, petTemplate, this);
		if (pet == null)
		{
			return;
		}

		setPet(pet);
		pet.setTitle(getName());

		if (!pet.isRespawned())
		{
			pet.setCurrentHp(pet.getMaxHp(), false);
			pet.setCurrentMp(pet.getMaxMp());
			pet.setCurrentFed(pet.getMaxFed());
			pet.updateControlItem();
			pet.store();
		}

		pet.getInventory().restore();

		pet.setReflection(getReflection());
		pet.spawnMe(Location.findPointToStay(this, 50, 70));
		pet.setRunning();
		pet.setFollowMode(true);
		pet.getInventory().validateItems();

		if (pet instanceof PetBabyInstance)
		{
			((PetBabyInstance) pet).startBuffTask();
		}

		getListeners().onSummonedPet(pet);
	}

	private Map<Integer, Long> _traps;

	public Collection<TrapInstance> getTraps()
	{
		if (_traps == null)
		{
			return null;
		}
		Collection<TrapInstance> result = new ArrayList<TrapInstance>(getTrapsCount());
		TrapInstance trap;
		for (Integer trapId : _traps.keySet())
		{
			if ((trap = (TrapInstance) GameObjectsStorage.get(_traps.get(trapId))) != null)
			{
				result.add(trap);
			}
			else
			{
				_traps.remove(trapId);
			}
		}
		return result;
	}

	public int getTrapsCount()
	{
		return _traps == null ? 0 : _traps.size();
	}

	public void addTrap(TrapInstance trap)
	{
		if (_traps == null)
		{
			_traps = new HashMap<Integer, Long>();
		}
		_traps.put(trap.getObjectId(), trap.getStoredId());
	}

	public void removeTrap(TrapInstance trap)
	{
		Map<Integer, Long> traps = _traps;
		if ((traps == null) || traps.isEmpty())
		{
			return;
		}
		traps.remove(trap.getObjectId());
	}

	public void destroyFirstTrap()
	{
		Map<Integer, Long> traps = _traps;
		if ((traps == null) || traps.isEmpty())
		{
			return;
		}
		TrapInstance trap;
		for (Integer trapId : traps.keySet())
		{
			if ((trap = (TrapInstance) GameObjectsStorage.get(traps.get(trapId))) != null)
			{
				trap.deleteMe();
				return;
			}
			return;
		}
	}

	public void destroyAllTraps()
	{
		Map<Integer, Long> traps = _traps;
		if ((traps == null) || traps.isEmpty())
		{
			return;
		}
		List<TrapInstance> toRemove = new ArrayList<TrapInstance>();
		for (Integer trapId : traps.keySet())
		{
			toRemove.add((TrapInstance) GameObjectsStorage.get(traps.get(trapId)));
		}
		for (TrapInstance t : toRemove)
		{
			if (t != null)
			{
				t.deleteMe();
			}
		}
	}

	public void setBlockCheckerArena(byte arena)
	{
		_handysBlockCheckerEventArena = arena;
	}

	public int getBlockCheckerArena()
	{
		return _handysBlockCheckerEventArena;
	}

	@Override
	public PlayerListenerList getListeners()
	{
		if (listeners == null)
		{
			synchronized (this)
			{
				if (listeners == null)
				{
					listeners = new PlayerListenerList(this);
				}
			}
		}
		return (PlayerListenerList) listeners;
	}

	@Override
	public PlayerPermissionList getPermissions()
	{
		if (_permissions == null)
		{
			synchronized (this)
			{
				if (_permissions == null)
				{
					_permissions = new PlayerPermissionList(this);
				}
			}
		}
		return (PlayerPermissionList) _permissions;
	}

	@Override
	public PlayerStatsChangeRecorder getStatsRecorder()
	{
		if (_statsRecorder == null)
		{
			synchronized (this)
			{
				if (_statsRecorder == null)
				{
					_statsRecorder = new PlayerStatsChangeRecorder(this);
				}
			}
		}
		return (PlayerStatsChangeRecorder) _statsRecorder;
	}

	private Future<?> _hourlyTask;
	private int _hoursInGame = 0;

	public int getHoursInGame()
	{
		_hoursInGame++;
		return _hoursInGame;
	}

	public int getHoursInGames()
	{
		return _hoursInGame;
	}

	public void startHourlyTask()
	{
		_hourlyTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HourlyTask(this), 3600000L, 3600000L);
	}

	public void stopHourlyTask()
	{
		if (_hourlyTask != null)
		{
			_hourlyTask.cancel(false);
			_hourlyTask = null;
		}
	}

	@Override
	public void setTeam(TeamType t)
	{
		super.setTeam(t);

		if (getPet() != null)
		{
			getPet().sendChanges();
		}
	}

	private int _team = 0;
	@SuppressWarnings("unused")
	private boolean _checksForTeam = false;

	public void setTeamEvents(int team, boolean checksForTeam)
	{
		_checksForTeam = checksForTeam;
		if (_team != team)
		{
			_team = team;

			broadcastUserInfo(true);
			if (getPet() != null)
			{
				getPet().broadcastCharInfo();
			}
		}
	}

	public int getTeamEvents()
	{
		return _team;
	}

	public long getPremiumPoints()
	{
		if (Config.GAME_POINT_ITEM_ID != -1)
		{
			return ItemFunctions.getItemCount(this, Config.GAME_POINT_ITEM_ID);
		}
		return 0;
	}

	public void reducePremiumPoints(int val)
	{
		if (Config.GAME_POINT_ITEM_ID != -1)
		{
			ItemFunctions.removeItem(this, Config.GAME_POINT_ITEM_ID, val, true, "PremiumPoints");
		}
	}

	public void addPremiumPoints(int val)
	{
		if (Config.GAME_POINT_ITEM_ID != -1)
		{
			ItemFunctions.addItem(this, Config.GAME_POINT_ITEM_ID, val, true, "PremiumPoints");
		}
	}

	private boolean _agathionResAvailable = false;

	public boolean isAgathionResAvailable()
	{
		return _agathionResAvailable;
	}

	public void setAgathionRes(boolean val)
	{
		_agathionResAvailable = val;
	}

	public boolean isClanAirShipDriver()
	{
		return isInBoat() && getBoat().isClanAirShip() && (((ClanAirShip) getBoat()).getDriver() == this);
	}

	private Map<String, String> _userSession;

	public String getSessionVar(String key)
	{
		if (_userSession == null)
		{
			return null;
		}
		return _userSession.get(key);
	}

	public void setSessionVar(String key, String val)
	{
		if (_userSession == null)
		{
			_userSession = new ConcurrentHashMap<String, String>();
		}

		if ((val == null) || val.isEmpty())
		{
			_userSession.remove(key);
		}
		else
		{
			_userSession.put(key, val);
		}
	}

	public FriendList getFriendList()
	{
		return _friendList;
	}

	public boolean isNotShowTraders()
	{
		return _notShowTraders;
	}

	public void setNotShowTraders(boolean notShowTraders)
	{
		_notShowTraders = notShowTraders;
	}

	public boolean isDebug()
	{
		return _debug;
	}

	public void setDebug(boolean b)
	{
		_debug = b;
	}

	public void sendItemList(boolean show)
	{
		ItemInstance[] items = getInventory().getItems();
		LockType lockType = getInventory().getLockType();
		int[] lockItems = getInventory().getLockItems();

		int allSize = items.length;
		int questItemsSize = 0;
		int agathionItemsSize = 0;
		for (ItemInstance item : items)
		{
			if (item.getTemplate().isQuest())
			{
				questItemsSize++;
			}
			if (item.getTemplate().getAgathionEnergy() > 0)
			{
				agathionItemsSize++;
			}
		}

		sendPacket(new ItemList(allSize - questItemsSize, items, show, lockType, lockItems));
		if (questItemsSize > 0)
		{
			sendPacket(new ExQuestItemList(questItemsSize, items, lockType, lockItems));
		}
		if (agathionItemsSize > 0)
		{
			sendPacket(new ExBR_AgathionEnergyInfo(agathionItemsSize, items));
		}
	}

	public int getBeltInventoryIncrease()
	{
		ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_BELT);
		if ((item != null) && (item.getTemplate().getAttachedSkills() != null))
		{
			for (Skill skill : item.getTemplate().getAttachedSkills())
			{
				for (FuncTemplate func : skill.getAttachedFuncs())
				{
					if (func._stat == Stats.INVENTORY_LIMIT)
					{
						return (int) func._value;
					}
				}
			}
		}
		return 0;
	}

	@Override
	public boolean isPlayer()
	{
		return true;
	}

	public boolean checkCoupleAction(Player target)
	{
		if (target.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IN_PRIVATE_STORE).addName(target));
			return false;
		}
		if (target.isFishing())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_FISHING).addName(target));
			return false;
		}
		if (target.isInCombat())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_COMBAT).addName(target));
			return false;
		}
		if (target.isCursedWeaponEquipped())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_CURSED_WEAPON_EQUIPED).addName(target));
			return false;
		}
		if (target.isInOlympiadMode())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_OLYMPIAD).addName(target));
			return false;
		}
		if (target.isOnSiegeField())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_SIEGE).addName(target));
			return false;
		}
		if (target.isInBoat() || (target.getMountNpcId() != 0))
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_VEHICLE_MOUNT_OTHER).addName(target));
			return false;
		}
		if (target.isTeleporting())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_TELEPORTING).addName(target));
			return false;
		}
		if (target.getTransformation() != 0)
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_TRANSFORM).addName(target));
			return false;
		}
		if (target.isDead())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_DEAD).addName(target));
			return false;
		}
		if (isInFightClub() && !getFightClubEvent().isFriend(this, target))
		{
			sendMessage("You cannot request couple action while player is your enemy!");
			return false;
		}
		return true;
	}

	@Override
	public void startAttackStanceTask()
	{
		startAttackStanceTask0();
		Summon summon = getPet();
		if (summon != null)
		{
			summon.startAttackStanceTask0();
		}
	}

	@Override
	public void displayGiveDamageMessage(Creature target, int damage, boolean crit, boolean miss, boolean shld, boolean magic)
	{
		super.displayGiveDamageMessage(target, damage, crit, miss, shld, magic);
		if (crit)
		{
			if (magic)
			{
				getCounters().mcritsDone++;
				sendPacket(new SystemMessage(SystemMessage.MAGIC_CRITICAL_HIT).addName(this));
			}
			else
			{
				getCounters().critsDone++;
				sendPacket(new SystemMessage(SystemMessage.C1_HAD_A_CRITICAL_HIT).addName(this));
			}
		}

		if (miss)
		{
			sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(this));
		}
		else if (!target.isDamageBlocked())
		{
			sendPacket(new SystemMessage(SystemMessage.C1_HAS_GIVEN_C2_DAMAGE_OF_S3).addName(this).addName(target).addNumber(damage));
		}

		if (target.isPlayer())
		{
			if (shld && (damage > 1))
			{
				target.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
			}
			else if (shld && (damage == 1))
			{
				target.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
			}
		}
	}

	@Override
	public void displayReceiveDamageMessage(Creature attacker, int damage)
	{
		if (attacker != this)
		{
			sendPacket(new SystemMessage(SystemMessage.C1_HAS_RECEIVED_DAMAGE_OF_S3_FROM_C2).addString(getVisibleName()).addString(attacker.getVisibleName()).addNumber((long) damage));
		}
	}

	public IntObjectMap<String> getPostFriends()
	{
		return _postFriends;
	}

	public boolean isSharedGroupDisabled(int groupId)
	{
		TimeStamp sts = _sharedGroupReuses.get(groupId);
		if (sts == null)
		{
			return false;
		}
		if (sts.hasNotPassed())
		{
			return true;
		}
		_sharedGroupReuses.remove(groupId);
		return false;
	}

	public TimeStamp getSharedGroupReuse(int groupId)
	{
		return _sharedGroupReuses.get(groupId);
	}

	public void addSharedGroupReuse(int group, TimeStamp stamp)
	{
		_sharedGroupReuses.put(group, stamp);
	}

	public Collection<IntObjectMap.Entry<TimeStamp>> getSharedGroupReuses()
	{
		return _sharedGroupReuses.entrySet();
	}

	public void sendReuseMessage(ItemInstance item)
	{
		TimeStamp sts = getSharedGroupReuse(item.getTemplate().getReuseGroup());
		if ((sts == null) || !sts.hasNotPassed())
		{
			return;
		}

		long timeleft = sts.getReuseCurrent();
		long hours = timeleft / 3600000;
		long minutes = (timeleft - (hours * 3600000)) / 60000;
		long seconds = (long) Math.ceil((timeleft - (hours * 3600000) - (minutes * 60000)) / 1000.);

		if (hours > 0)
		{
			sendPacket(new SystemMessage2(item.getTemplate().getReuseType().getMessages()[2]).addItemName(item.getTemplate().getItemId()).addInteger(hours).addInteger(minutes).addInteger(seconds));
		}
		else if (minutes > 0)
		{
			sendPacket(new SystemMessage2(item.getTemplate().getReuseType().getMessages()[1]).addItemName(item.getTemplate().getItemId()).addInteger(minutes).addInteger(seconds));
		}
		else
		{
			sendPacket(new SystemMessage2(item.getTemplate().getReuseType().getMessages()[0]).addItemName(item.getTemplate().getItemId()).addInteger(seconds));
		}
	}

	public NevitSystem getNevitSystem()
	{
		return _nevitSystem;
	}

	public void ask(ConfirmDlg dlg, OnAnswerListener listener)
	{
		if (_askDialog != null)
		{
			return;
		}

		int rnd = Rnd.nextInt();
		_askDialog = new ImmutablePair<Integer, OnAnswerListener>(rnd, listener);
		dlg.setRequestId(rnd);
		sendPacket(dlg);

		// Prims - Set the resurrection max time to accept it to 5 minutes. After that it will be rejected. Only for players
		if (listener instanceof ReviveAnswerListener && !((ReviveAnswerListener) listener).isForPet())
		{
			_resurrectionMaxTime = System.currentTimeMillis() + 5 * 60 * 1000;
		}
	}

	public Pair<Integer, OnAnswerListener> getAskListener(boolean clear)
	{
		if (!clear)
		{
			return _askDialog;
		}
		else
		{
			Pair<Integer, OnAnswerListener> ask = _askDialog;
			_askDialog = null;
			return ask;
		}
	}

	public boolean hasDialogAskActive()
	{
		return _askDialog != null;
	}

	@Override
	public boolean isDead()
	{
		// Synerge - If is in oly, check if it already finished or has hp less than 1. Else check if is already dead dont go to superclass
		if (isInOlympiadMode())
		{
			if (_olympiadGame.getType() == CompType.TEAM || isPendingOlyEnd())
			{
				return getCurrentHp() <= 1.;
			}
			return isDead.get();
		}
		return (isInDuel() ? getCurrentHp() <= 1. : super.isDead());
	}

	@Override
	public int getAgathionEnergy()
	{
		ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
		return item == null ? 0 : item.getAgathionEnergy();
	}

	@Override
	public void setAgathionEnergy(int val)
	{
		ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
		if (item == null)
		{
			return;
		}
		item.setAgathionEnergy(val);
		item.setJdbcState(JdbcEntityState.UPDATED);

		sendPacket(new ExBR_AgathionEnergyInfo(1, item));
	}

	public boolean hasPrivilege(Privilege privilege)
	{
		return (_clan != null) && ((getClanPrivileges() & privilege.mask()) == privilege.mask());
	}

	public MatchingRoom getMatchingRoom()
	{
		return _matchingRoom;
	}

	public void setMatchingRoom(MatchingRoom matchingRoom)
	{
		_matchingRoom = matchingRoom;
		if (matchingRoom == null)
		{
			_matchingRoomWindowOpened = false;
		}
	}

	public boolean isMatchingRoomWindowOpened()
	{
		return _matchingRoomWindowOpened;
	}

	public void setMatchingRoomWindowOpened(boolean b)
	{
		_matchingRoomWindowOpened = b;
	}

	public void dispelBuffs()
	{
		for (Effect e : getEffectList().getAllEffects())
		{
			if (!e.getSkill().isOffensive() && !e.getSkill().isNewbie() && e.isCancelable() && !e.getSkill().isPreservedOnDeath())
			{
				sendPacket(new SystemMessage(SystemMessage.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill().getId(), e.getSkill().getLevel()));
				e.exit();
			}
		}
		if (getPet() != null)
		{
			for (Effect e : getPet().getEffectList().getAllEffects())
			{
				if (!e.getSkill().isOffensive() && !e.getSkill().isNewbie() && e.isCancelable() && !e.getSkill().isPreservedOnDeath())
				{
					e.exit();
				}
			}
		}
	}

	public void setInstanceReuse(int id, long time)
	{
		final SystemMessage msg = new SystemMessage(SystemMessage.INSTANT_ZONE_FROM_HERE__S1_S_ENTRY_HAS_BEEN_RESTRICTED_YOU_CAN_CHECK_THE_NEXT_ENTRY_POSSIBLE).addString(getName());
		sendPacket(msg);
		_instancesReuses.put(id, time);
		mysql.set("REPLACE INTO character_instances (obj_id, id, reuse) VALUES (?,?,?)", getObjectId(), id, time);
	}

	public void removeInstanceReuse(int id)
	{
		if (_instancesReuses.remove(id) != null)
		{
			mysql.set("DELETE FROM `character_instances` WHERE `obj_id`=? AND `id`=? LIMIT 1", getObjectId(), id);
		}
	}

	public void removeAllInstanceReuses()
	{
		_instancesReuses.clear();
		mysql.set("DELETE FROM `character_instances` WHERE `obj_id`=?", getObjectId());
	}

	public void removeInstanceReusesByGroupId(int groupId)
	{
		for (int i : InstantZoneHolder.getInstance().getSharedReuseInstanceIdsByGroup(groupId))
		{
			if (getInstanceReuse(i) != null)
			{
				removeInstanceReuse(i);
			}
		}
	}

	public Long getInstanceReuse(int id)
	{
		return _instancesReuses.get(id);
	}

	public Map<Integer, Long> getInstanceReuses()
	{
		return _instancesReuses;
	}

	private void loadInstanceReuses(Connection con)
	{
		try (PreparedStatement offline = con.prepareStatement("SELECT * FROM character_instances WHERE obj_id = ?"))
		{
			offline.setInt(1, getObjectId());

			try (ResultSet rs = offline.executeQuery())
			{
				while (rs.next())
				{
					int id = rs.getInt("id");
					long reuse = rs.getLong("reuse");
					_instancesReuses.put(id, reuse);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while loading Instance Reuses for Id " + getObjectId(), e);
		}
	}

	public Reflection getActiveReflection()
	{
		for (Reflection r : ReflectionManager.getInstance().getAll())
		{
			if ((r != null) && ArrayUtils.contains(r.getVisitors(), getObjectId()))
			{
				return r;
			}
		}
		return null;
	}

	public boolean canEnterInstance(int instancedZoneId)
	{
		InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);

		if (isDead())
		{
			return false;
		}

		if (!Config.ALLOW_ENTER_INSTANCE)
		{
			sendMessage("Instances cannot be entered at the time!");
			return false;
		}

		if (ReflectionManager.getInstance().size() > Config.MAX_REFLECTIONS_COUNT)
		{
			sendPacket(SystemMsg.THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED);
			return false;
		}

		if (iz == null)
		{
			sendPacket(SystemMsg.SYSTEM_ERROR);
			return false;
		}

		if (ReflectionManager.getInstance().getCountByIzId(instancedZoneId) >= iz.getMaxChannels())
		{
			sendPacket(SystemMsg.THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED);
			return false;
		}

		if (getActiveWeaponFlagAttachment() != null)
		{
			sendMessage("You cannot enter an instance while holding a flag!");
			return false;
		}

		return iz.getEntryType().canEnter(this, iz);
	}

	public boolean canReenterInstance(int instancedZoneId)
	{
		InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);

		if (!Config.ALLOW_ENTER_INSTANCE)
		{
			sendMessage("Instances cannot be entered at the time!");
			return false;
		}

		if ((getActiveReflection() != null) && (getActiveReflection().getInstancedZoneId() != instancedZoneId))
		{
			sendPacket(SystemMsg.YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON);
			return false;
		}
		if (iz.isDispelBuffs())
		{
			dispelBuffs();
		}
		return iz.getEntryType().canReEnter(this, iz);
	}

	public int getBattlefieldChatId()
	{
		return _battlefieldChatId;
	}

	public void setBattlefieldChatId(int battlefieldChatId)
	{
		_battlefieldChatId = battlefieldChatId;
	}

	@Override
	public Iterator<Player> iterator()
	{
		return Collections.singleton(this).iterator();
	}

	public PlayerGroup getPlayerGroup()
	{
		if (getParty() != null)
		{
			if (getParty().getCommandChannel() != null)
			{
				return getParty().getCommandChannel();
			}
			else
			{
				return getParty();
			}
		}
		else
		{
			return this;
		}
	}

	@Override
	public int size()
	{
		return 1;
	}

	@Override
	public Player getLeader()
	{
		return this;
	}

	@Override
	public List<Player> getMembers(Player... excluded)
	{
		if (Util.arrayContains(excluded, this))
		{
			return Collections.emptyList();
		}

		return Collections.singletonList(this);
	}

	@Override
	public boolean containsMember(Player player)
	{
		return this == player;
	}

	public boolean isActionBlocked(String action)
	{
		return _blockedActions.contains(action);
	}

	public void blockActions(String... actions)
	{
		Collections.addAll(_blockedActions, actions);
	}

	public void unblockActions(String... actions)
	{
		for (String action : actions)
		{
			_blockedActions.remove(action);
		}
	}

	public OlympiadGame getOlympiadGame()
	{
		return _olympiadGame;
	}

	public void setOlympiadGame(OlympiadGame olympiadGame)
	{
		_olympiadGame = olympiadGame;
	}

	public boolean isInOlympiadObserverMode()
	{
		return _olympiadObserveGame != null;
	}

	public OlympiadGame getOlympiadObserveGame()
	{
		return _olympiadObserveGame;
	}

	public void setOlympiadObserveGame(OlympiadGame olympiadObserveGame)
	{
		_olympiadObserveGame = olympiadObserveGame;
	}

	public void addRadar(int x, int y, int z)
	{
		sendPacket(new RadarControl(0, 1, x, y, z));
	}

	public void addRadarWithMap(int x, int y, int z)
	{
		sendPacket(new RadarControl(0, 2, x, y, z));
	}

	public PetitionMainGroup getPetitionGroup()
	{
		return _petitionGroup;
	}

	public void setPetitionGroup(PetitionMainGroup petitionGroup)
	{
		_petitionGroup = petitionGroup;
	}

	public int getLectureMark()
	{
		return _lectureMark;
	}

	public void setLectureMark(int lectureMark)
	{
		_lectureMark = lectureMark;
	}

	class TeleportPoints
	{
		private final String _name;
		private final Location _xyz;
		private final long _prace;
		private final int _itemId;
		private final int _id;

		public TeleportPoints(String name, Location xyz, int id, int itemId, long price)
		{
			_id = id;
			_name = name;
			_xyz = xyz;
			_itemId = itemId;
			_prace = price;
		}

		public int getId()
		{
			return _id;
		}

		public String getName()
		{
			return _name;
		}

		public long getPrice()
		{
			return _prace;
		}

		public int getItemId()
		{
			return _itemId;
		}

		public Location getXYZ()
		{
			return _xyz;
		}
	}

	public TeleportPoints getTeleportPoint(String name)
	{
		for (TeleportPoints point : _teleportPoints)
		{
			if (point.getName().equalsIgnoreCase(name))
			{
				return point;
			}
		}
		return null;
	}

	public void addTeleportPoint(String name, int id, int itemId, long price)
	{
		_teleportPoints.add(new TeleportPoints(name, new Location(getX(), getY(), getZ()), id, itemId, price));
	}

	public void delTeleportPoint(String name)
	{
		for (TeleportPoints point : _teleportPoints)
		{
			if (point.getName().equals(name))
			{
				_teleportPoints.remove(point);
			}
		}
	}

	public void setLastHeroTrue(boolean value)
	{
		setHero(value);
	}

	private boolean is_bbs_use = false;

	public void setIsBBSUse(boolean value)
	{
		is_bbs_use = value;
	}

	public boolean isBBSUse()
	{
		return is_bbs_use;
	}

	public void setAccountAccesslevel(int level, String comments, int banTime)
	{
		AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(getAccountName(), level, banTime));
	}

	private static void RestoreFightClub(Player player)
	{
		String[] values = player.getVar("FightClubRate").split(";");
		int id = Integer.parseInt(values[0]);
		int count = Integer.parseInt(values[1]);
		ItemFunctions.addItem(player, id, count, true, "RestoreFightClub");
		player.unsetVar("FightClubRate");
		player.unsetVar("isPvPevents");
	}

	private static boolean _isNoAttackEvents = false;

	public static void setNoAttackEvents(boolean set)
	{
		_isNoAttackEvents = set;
	}

	public boolean isNoAttackEvents()
	{
		return _isNoAttackEvents;
	}

	private void restoreCursedWeapon()
	{
		for (ItemInstance item : getInventory().getItems())
		{
			if (item.isCursed())
			{
				int skillLvl = CursedWeaponsManager.getInstance().getLevel(item.getItemId());
				if (item.getItemId() == 8190)
				{
					addSkill(SkillTable.getInstance().getInfo(3603, skillLvl), false);
				}
				else if (item.getItemId() == 8689)
				{
					addSkill(SkillTable.getInstance().getInfo(3629, skillLvl), false);
				}
			}
		}
		updateStats();
	}

	private int _pvp_team = 0;

	public void setPvPTeam(int team)
	{
		if (_pvp_team != team)
		{
			_pvp_team = team;
			broadcastUserInfo(true);
			if (getPet() != null)
			{
				getPet().broadcastCharInfo();
			}
		}
	}

	public void allowPvPTeam()
	{
		switch (_pvp_team)
		{
		case 0:
			setTeam(TeamType.NONE);
			break;
		case 2:
			setTeam(TeamType.RED);
			break;
		case 1:
			setTeam(TeamType.BLUE);
			break;
		default:
			break;
		}
	}

	public int getPvPTeam()
	{
		return _pvp_team;
	}

	public void startUnjailTask(Player player, long time, boolean msg)
	{
		if (time < 1)
		{
			return;
		}

		if (_unjailTask != null)
		{
			_unjailTask.cancel(false);
		}

		_unjailTask = ThreadPoolManager.getInstance().schedule(new UnJailTask(player, msg), time);
	}

	public void stopUnjailTask()
	{
		if (_unjailTask != null)
		{
			_unjailTask.cancel(false);
		}
		_unjailTask = null;
	}

	public long _firstExp;

	/**
	 * Sets exp holded by the character on log in
	 * @param value
	 */
	public void setFirstExp(long value)
	{
		_firstExp = value;
	}

	/**
	 * Will return true if the player has gained exp since logged in
	 * @return
	 */
	public boolean hasEarnedExp()
	{
		if ((getExp() - _firstExp) != 0)
		{
			return true;
		}
		return false;
	}

	private boolean _isVoting = false;

	public boolean isInSameParty(Player target)
	{
		return ((getParty() != null) && (target.getParty() != null) && (getParty() == target.getParty()));
	}

	public boolean isInSameChannel(Player target)
	{
		Party activeCharP = getParty();
		Party targetP = target.getParty();
		if ((activeCharP == null) || (targetP == null))
		{
			return false;
		}
		CommandChannel chan = activeCharP.getCommandChannel();

		return chan != null && chan == targetP.getCommandChannel();
	}

	public boolean isInSameClan(Player target)
	{
		return getClanId() != 0 && getClanId() == target.getClanId();
	}

	public final boolean isInSameAlly(Player target)
	{
		return getAllyId() != 0 && getAllyId() == target.getAllyId();
	}

	public boolean isVoting()
	{
		return _isVoting;
	}

	public void setIsVoting(boolean value)
	{
		_isVoting = value;
	}

	public void setMacroSkill(Skill skill)
	{
		_macroSkill = skill;
	}

	public Skill getMacroSkill()
	{
		return _macroSkill;
	}

	// Prims - Support for visible non permanent colors
	private int _visibleNameColor = 0;

	public int getVisibleNameColor()
	{
		if (_visibleNameColor != 0)
		{
			return _visibleNameColor;
		}

		return getNameColor();
	}

	public void setVisibleNameColor(int nameColor)
	{
		_visibleNameColor = nameColor;
	}

	private int _visibleTitleColor = 0;

	public int getVisibleTitleColor()
	{
		if (_visibleTitleColor != 0)
		{
			return _visibleTitleColor;
		}

		return getTitleColor();
	}

	public void setVisibleTitleColor(int nameColor)
	{
		_visibleTitleColor = nameColor;
	}

	public void setLastAugmentNpc(NpcInstance npc)
	{
		lastAugmentNpc = npc;
	}

	public boolean checkLastAugmentNpc()
	{
		if (lastAugmentNpc == null)
		{
			return false;
		}

		if (!PositionUtils.checkIfInRange(300, this, lastAugmentNpc, true))
		{
			lastAugmentNpc = null;
			return false;
		}

		return true;
	}

	// Support for visible non permanent name and title
	private String _visibleName = null;

	@Override
	public String getVisibleName()
	{
		if (_visibleName != null)
		{
			return _visibleName;
		}

		return getName();
	}

	public void setVisibleName(String name)
	{
		_visibleName = name;
	}

	private String _visibleTitle = null;

	public String getVisibleTitle()
	{
		if (_visibleTitle != null)
		{
			return _visibleTitle;
		}

		return getTitle();
	}

	public void setVisibleTitle(String title)
	{
		_visibleTitle = title;
	}

	// Offline buff store function
	public void offlineBuffStore()
	{
		if (getHwidGamer() != null)
		{
			getHwidGamer().removePlayer(this);
		}

		if (_connection != null)
		{
			_connection.setActiveChar(null);
			_connection.close(ServerClose.STATIC);
			setNetConnection(null);
		}
		setOnlineTime(getOnlineTime());
		setUptime(0);
		setOfflineMode(true);

		Party party = getParty();
		if (party != null)
		{
			if (isFestivalParticipant())
			{
				party.sendMessage(getName() + " has been removed from the upcoming festival.");
			}
			leaveParty();
		}

		if (getPet() != null)
		{
			getPet().unSummon();
		}

		CursedWeaponsManager.getInstance().doLogout(this);

		if (isInOlympiadMode() || (getOlympiadGame() != null))
		{
			Olympiad.logoutPlayer(this);
		}

		if (isInObserverMode())
		{
			if (getOlympiadObserveGame() == null)
			{
				leaveObserverMode();
			}
			else
			{
				leaveOlympiadObserverMode(true);
			}
			_observerMode.set(OBSERVER_NONE);
		}

		setVisibleNameColor(Config.BUFF_STORE_OFFLINE_NAME_COLOR);
		broadcastCharInfo();

		// Guardamos el offline buffer en la db al salir
		OfflineBuffersTable.getInstance().onLogout(this);

		// Stop all tasks
		stopWaterTask();
		stopBonusTask();
		stopHourlyTask();
		stopVitalityTask();
		stopPcBangPointsTask();
		stopAutoSaveTask();
		stopRecomBonusTask(true);
		stopQuestTimers();
		getNevitSystem().stopTasksOnLogout();

		try
		{
			getInventory().store();
		}
		catch (Throwable t)
		{
			_log.error("Error while storing Player Inventory", t);
		}

		try
		{
			store(false);
		}
		catch (Throwable t)
		{
			_log.error("Error while storing Player", t);
		}
	}

	/** ----------------Start Achievement System ------------------- */
	private final List<Integer> _completedAchievements = new FastList<>();

	public List<Integer> getCompletedAchievements()
	{
		return _completedAchievements;
	}

	public void saveAchievementData(int achievementID, int objid)
	{
		// Connection con = null;

		try (Connection con = DatabaseFactory.getInstance().getConnection();)
		{
			// con = L2DatabaseFactory.getInstance().getConnection();
			Statement statement = con.createStatement();
			if ((achievementID == 4) || (achievementID == 6) || (achievementID == 11) || (achievementID == 13))
			{
				statement.executeUpdate("UPDATE achievements SET a" + achievementID + "=1" + objid + " WHERE owner_id=" + getObjectId());
			}
			else
			{
				statement.executeUpdate("UPDATE achievements SET a" + achievementID + "=1 WHERE owner_id=" + getObjectId());
			}

			statement.close();

			if (!_completedAchievements.contains(achievementID))
			{
				_completedAchievements.add(achievementID);
			}
		}
		catch (SQLException e)
		{
			_log.warn("[ACHIEVEMENTS SAVE GETDATA]" + e);
		}
	}

	public boolean readyAchievementsList()
	{
		if (_completedAchievements.isEmpty())
		{
			return false;
		}
		return true;
	}

	public void getAchievemntData()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement;
			PreparedStatement insertStatement;
			ResultSet rs;

			statement = con.prepareStatement("SELECT * FROM achievements WHERE owner_id=" + getObjectId());

			rs = statement.executeQuery();

			String values = "owner_id";
			String in = Integer.toString(getObjectId());
			String questionMarks = in;
			int ilosc = AchievementsManager.getInstance().getAchievementList().size();

			if (rs.next())
			{
				_completedAchievements.clear();
				for (int i = 1; i <= ilosc; i++)
				{
					int a = rs.getInt("a" + i);

					if (!_completedAchievements.contains(i))
					{
						if ((a == 1) || String.valueOf(a).startsWith("1"))
						{
							_completedAchievements.add(i);
						}
					}
				}
			}
			else
			{
				// Player hasnt entry in database, means we have to create it.
				for (int i = 1; i <= ilosc; i++)
				{
					values += ", a" + i;
					questionMarks += ", 0";
				}

				String s = "INSERT INTO achievements(" + values + ") VALUES (" + questionMarks + ")";
				insertStatement = con.prepareStatement(s);

				insertStatement.execute();
				insertStatement.close();
			}
		}
		catch (SQLException e)
		{
			_log.warn("[Achievements event loaded data: ]" + e);
		}
	}

	/** ----------------- End Achievement System ------------------- */

	@Override
	public boolean isInZoneBattle()
	{
		// Prims - If the player is in a Gm Event and is a pvp event, then its in a zone battle also
		if (GmEventManager.getInstance().isParticipating(this) && GmEventManager.getInstance().isPvPEvent())
		{
			return true;
		}

		return super.isInZoneBattle();
	}

	@Override
	public boolean isInZonePeace()
	{
		// Prims - If the player is in a Gm Event and is a peace event, then its in a peace zone
		if (GmEventManager.getInstance().isParticipating(this) && GmEventManager.getInstance().isPeaceEvent())
		{
			return true;
		}

		return super.isInZonePeace();
	}

	/** ----------------- Hit Man System ------------------- */
	public int _ordered;

	public int getOrdered()
	{
		return _ordered;
	}

	public void setOrdered(int ordered)
	{
		_ordered = ordered;
		broadcastUserInfo(true);
	}

	/** ----------------- End Hit Man System -------------------

	 * @return
	 * */
	public boolean isInAwayingMode()
	{
		return _awaying;
	}

	public void setAwayingMode(boolean awaying)
	{
		_awaying = awaying;
	}

	/**
	 * Synerge - This is used to transfer the skill reuse to a new skill. This happens when a player level up or enchants an skill, its reused is lost due to its hashCode
	 *
	 * @param oldSkillReuseHashCode
	 * @param newSkillReuseHashCode
	 */
	public void disableSkillByNewLvl(Integer oldSkillReuseHashCode, Integer newSkillReuseHashCode)
	{
		if ((oldSkillReuseHashCode == newSkillReuseHashCode) || (_skillReuses == null))
		{
			return;
		}

		final TimeStamp timeStamp = _skillReuses.get(oldSkillReuseHashCode);
		if (timeStamp == null)
		{
			return;
		}

		_skillReuses.remove(oldSkillReuseHashCode);

		if (timeStamp.getEndTime() <= 0 || timeStamp.getEndTime() < System.currentTimeMillis())
		{
			return;
		}

		_skillReuses.put(newSkillReuseHashCode, timeStamp);
	}

	// Synerge - Support for being able to enchant a weapon/armor using all attribute stones available
	private boolean _isEnchantAllAttribute = false;

	public void setIsEnchantAllAttribute(boolean isEnchantAllAttribute)
	{
		_isEnchantAllAttribute = isEnchantAllAttribute;
	}

	public boolean isEnchantAllAttribute()
	{
		return _isEnchantAllAttribute;
	}

	public boolean isDualbox(Player... players)
	{
		return isDualbox(0, false, Arrays.asList(players));
	}

	public boolean isDualbox(Iterable<Player> players)
	{
		return isDualbox(0, false, players);
	}

	public boolean isDualbox(int maxBoxesAllowed, boolean hwidOnly, Player... players)
	{
		if (maxBoxesAllowed < 0)
		{
			return false;
		}

		return isDualbox(maxBoxesAllowed, hwidOnly, Arrays.asList(players));
	}

	public boolean isDualbox(int maxBoxesAllowed, boolean hwidOnly, Iterable<Player> players)
	{
		if (maxBoxesAllowed < 0)
		{
			return false;
		}

		for (Player player : players)
		{
			if (player == null || player.getNetConnection() == null || player == this)
			{
				continue;
			}

			if ((!hwidOnly && getIP().equalsIgnoreCase(player.getIP())) || getHWID().equalsIgnoreCase(player.getHWID()))
			{
				maxBoxesAllowed--;
			}

			if (maxBoxesAllowed < 0)
			{
				return true;
			}
		}

		return false;
	}

	private final Map<Integer, Integer> _achievementLevels = new FastMap<>();

	public boolean achievement_nf_open;

	public Map<Integer, Integer> getAchievements(int category)
	{
		Map<Integer, Integer> result = new FastMap<>();
		for (Entry<Integer, Integer> entry : _achievementLevels.entrySet())
		{
			int achievementId = entry.getKey();
			int achievementLevel = entry.getValue();
			Achievement ach = Achievements.getInstance().getAchievement(achievementId, Math.max(1, achievementLevel));
			if ((ach != null) && (ach.getCategoryId() == category))
			{
				result.put(achievementId, achievementLevel);
			}
		}
		return result;
	}

	public Map<Integer, Integer> getAchievements()
	{
		return _achievementLevels;
	}

	private void loadAchivements()
	{
		String achievements = getVar("achievements");
		if ((achievements != null) && !achievements.isEmpty())
		{
			String[] levels = achievements.split(";");
			for (String ach : levels)
			{
				String[] lvl = ach.split(",");

				// Check if achievement exists.
				if (Achievements.getInstance().getMaxLevel(Integer.parseInt(lvl[0])) > 0)
				{
					_achievementLevels.put(Integer.parseInt(lvl[0]), Integer.parseInt(lvl[1]));
				}
			}
		}

		for (int achievementId : Achievements.getInstance().getAchievementIds())
		{
			if (!_achievementLevels.containsKey(achievementId))
			{
				_achievementLevels.put(achievementId, 0);
			}
		}
	}

	private void saveAchivements()
	{
		String str = "";
		for (Entry<Integer, Integer> a : _achievementLevels.entrySet())
		{
			str += a.getKey() + "," + a.getValue() + ";";
		}

		setVar("achievements", str);
	}

	private PlayerCounters _playerCountersExtension = null;

	public PlayerCounters getCounters()
	{
		if (!Config.ENABLE_PLAYER_COUNTERS)
		{
			return PlayerCounters.DUMMY_COUNTER;
		}

		if (_playerCountersExtension == null)
		{
			synchronized (this)
			{
				if (_playerCountersExtension == null)
				{
					_playerCountersExtension = new PlayerCounters(this);
				}
			}
		}
		return _playerCountersExtension;
	}

	public void broadcastSkillOrSocialAnimation(int id, int level, int hitTime, int lockActivityTime)
	{
		if (isAlikeDead())
		{
			return;
		}

		boolean performSocialAction = (level < 1);

		if (!performSocialAction)
		{
			broadcastPacket(new MagicSkillUse(this, this, id, level, hitTime, 0));
		}
		else
		{
			broadcastPacket(new SocialAction(getObjectId(), id));
		}
	}

	// Event Kills
	private int _eventKills;

	public void setEventKills(int eventKills)
	{
		_eventKills = eventKills;
	}

	public int getEventKills()
	{
		return _eventKills;
	}

	// Siege Kills
	private int _siegeKills;

	public void setSiegeKills(int siegeKills)
	{
		_siegeKills = siegeKills;
	}

	public void incSiegeKills()
	{
		_siegeKills++;
	}

	public int getSiegeKills()
	{
		return _siegeKills;
	}

	// Olympiad wins
	private int _olyWins;

	public void setOlyWins(int olyWins)
	{
		_olyWins = olyWins;
	}

	public void incOlyWins()
	{
		_olyWins++;
	}

	public int getOlyWins()
	{
		return _olyWins;
	}

	// Captcha
	private int _capchaCount = 0;

	public int getCapchaCount()
	{
		return _capchaCount;
	}

	public void updateCapchaCount(int count)
	{
		_capchaCount = count;
	}

	// Synerge - Used for catpcha system, sets when was the last time that this player did damage to a monster
	private long _lastMonsterDamageTime = 0;

	public long getLastMonsterDamageTime()
	{
		return _lastMonsterDamageTime;
	}

	public void setLastMonsterDamageTime()
	{
		_lastMonsterDamageTime = System.currentTimeMillis();
	}

	// Synerge - Facebook support
	private FacebookProfile _facebookProfile = null;

	public void setFacebookProfile(FacebookProfile facebookProfile)
	{
		_facebookProfile = facebookProfile;
	}

	public boolean hasFacebookProfile()
	{
		return _facebookProfile != null;
	}

	@Nullable
	public FacebookProfile getFacebookProfile()
	{
		return _facebookProfile;
	}

	// Synerge - Forum Support
	private String _forumLogin;
	private ForumMember _forumMember;

	public void setForumLogin(String forumLogin)
	{
		_forumLogin = forumLogin;
	}

	public String getForumLogin()
	{
		return _forumLogin;
	}

	public void setForumMember(ForumMember forumMember)
	{
		_forumMember = forumMember;
	}

	public ForumMember getForumMember()
	{
		return _forumMember;
	}

	// Synerge - Support for Automatic Potions (Toogle)
	private final Set<Integer> _autoPotions = new CopyOnWriteArraySet<Integer>();
	private Future<?> _autoPotionTask = null;

	public void addAutoPotion(int itemId)
	{
		_autoPotions.add(itemId);

		// A thread will handle the auto potions, each second will see if they must be used considering the player status
		if (_autoPotionTask == null)
		{
			_autoPotionTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new AutoPotionTask(), 1000, 1000);
		}
	}

	public void removeAutoPotion(int itemId)
	{
		_autoPotions.remove(itemId);
		if (_autoPotions.isEmpty())
		{
			if (_autoPotionTask != null)
			{
				_autoPotionTask.cancel(false);
				_autoPotionTask = null;
			}
		}
	}

	public Set<Integer> getAutoPotions()
	{
		return _autoPotions;
	}

	private class AutoPotionTask implements Runnable
	{
		@Override
		public void run()
		{
			if (_autoPotions.isEmpty())
			{
				if (_autoPotionTask != null)
				{
					_autoPotionTask.cancel(false);
					_autoPotionTask = null;
				}
				return;
			}

			// Special conditions, cannot use any item
			if (isOutOfControl() || isInStoreMode() || isFishing() || isInAwayingMode() || isInOlympiadMode() || isInvisible() || isInvul())
			{
				return;
			}

			for (int itemId : _autoPotions)
			{
				final ItemInstance item = getInventory().getItemByItemId(itemId);

				// If there are no more items available, que must deactivate the toogle for this potion
				if (item == null || item.getCount() < 1)
				{
					final ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
					sendPacket(new ExAutoSoulShot(itemId, false));
					sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addString(template.getName()));
					removeAutoPotion(itemId);
					continue;
				}

				// Depending on the type of potions we will check different values
				switch (itemId)
				{
				// Greater Healing Potion
				case 1539:
				{
					if (getCurrentHp() >= 0.90 * getMaxHp())
					{
						continue;
					}

					break;
				}
				// Mana Potion
				case 728:
				{
					if (getCurrentMp() >= 0.90 * getMaxMp())
					{
						continue;
					}

					break;
				}
				// Greater CP Potion
				case 5592:
				{
					if (getCurrentCp() >= 0.90 * getMaxCp())
					{
						continue;
					}

					break;
				}
				}

				// Check if we can use this item
				if (getInventory().isLockedItem(item) || isSharedGroupDisabled(item.getTemplate().getReuseGroup()) || !item.getTemplate().testCondition(Player.this, item) || !getPermissions().canUseItem(item, true, false))
				{
					continue;
				}

				// Check if we dont have already an effect of this item
				if (getEffectList().getEffectsBySkill(item.getTemplate().getFirstSkill()) != null)
				{
					continue;
				}

				// Use the item and add the reuses
				boolean success = item.getTemplate().getHandler().useItem(Player.this, item, true);
				if (success)
				{
					long nextTimeUse = item.getTemplate().getReuseType().next(item);
					if (nextTimeUse > System.currentTimeMillis())
					{
						TimeStamp timeStamp = new TimeStamp(item.getItemId(), nextTimeUse, item.getTemplate().getReuseDelay());
						addSharedGroupReuse(item.getTemplate().getReuseGroup(), timeStamp);

						if (item.getTemplate().getReuseDelay() > 0)
						{
							sendPacket(new ExUseSharedGroupItem(item.getTemplate().getDisplayReuseGroup(), timeStamp));
						}
					}
				}
			}
		}
	}

	// Synerge - Spawn protection for players. This is different to non-aggro, but invulnerability when spawning
	public static final int SPAWN_PROTECTION_TIME = 20000;

	private long _spawnProtection = 0;
	private SpawnProtectionListener _spawnProtectionListener;

	public void setSpawnProtection(long time)
	{
		if (time < 1 && _spawnProtection > 0)
		{
			sendMessage("Your spawn protection has been lifted");
			removeListener(_spawnProtectionListener);
			_spawnProtectionListener = null;
		}
		else if (time > 0 && _spawnProtection < 1)
		{
			sendMessage("You are now spawn protected");
			addListener(_spawnProtectionListener = new SpawnProtectionListener());
		}

		_spawnProtection = time;
	}

	public boolean isSpawnProtected()
	{
		if (_spawnProtection < 1)
		{
			return false;
		}

		if (_spawnProtection > System.currentTimeMillis())
		{
			return true;
		}

		setSpawnProtection(0);
		return false;
	}

	private class SpawnProtectionListener implements OnAttackListener, OnMagicUseListener
	{
		// Use a listener to cancel the spawn protection when doing an action
		@Override
		public void onMagicUse(Creature actor, Skill skill, Creature target, boolean alt)
		{
			setSpawnProtection(0);
		}

		@Override
		public void onAttack(Creature actor, Creature target)
		{
			setSpawnProtection(0);
		}
	}

	// Synerge - Phantom System
	private boolean _IsPhantom = false;

	public void setIsPhantom(boolean isPhantom, boolean hasAi)
	{
		_IsPhantom = isPhantom;

		if (hasAi)
		{
			setAI(new PhantomPlayerAI(this));
		}
		else
		{
			setAI(new PlayerAI(this));
		}
	}

	public boolean isPhantom()
	{
		return _IsPhantom;
	}

	public int getGearScore()
	{
		return Util.getGearPoints(this);
	}

	public Location getStablePoint()
	{
		return _stablePoint;
	}

	public void setStablePoint(Location point)
	{
		_stablePoint = point;
	}

	// Synerge - Bot Report
	private AutoHuntingPunish _AutoHuntingPunish = null;

	/**
	 * Initializes his _botPunish object with the specified punish and for the specified time
	 *
	 * @param punishType
	 * @param minsOfPunish
	 */
	public synchronized void setPunishDueBotting(AutoHuntingPunish.Punish punishType, int minsOfPunish)
	{
		if (_AutoHuntingPunish == null)
		{
			_AutoHuntingPunish = new AutoHuntingPunish(punishType, minsOfPunish);
		}
	}

	/**
	 * Returns the current object-representative player punish
	 *
	 * @return
	 */
	public AutoHuntingPunish getPlayerPunish()
	{
		return _AutoHuntingPunish;
	}

	/**
	 * Returns the type of punish being applied
	 *
	 * @return
	 */
	public AutoHuntingPunish.Punish getBotPunishType()
	{
		return _AutoHuntingPunish.getBotPunishType();
	}

	/**
	 * Will return true if the player has any bot punishment active
	 *
	 * @return
	 */
	public boolean isBeingPunished()
	{
		return _AutoHuntingPunish != null;
	}

	private AccountReportDAO _account = null;

	public AccountReportDAO getReportedAccount()
	{
		return _account;
	}

	public void setReportedAccount(String accountName)
	{
		_account = new AccountReportDAO(accountName);
	}

	/**
	 * Will end the punishment once a player attempt to perform any forbid action and his punishment has expired
	 */
	public void endPunishment()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM bot_reported_punish WHERE charId = ?"))
		{
			statement.setInt(1, getObjectId());
			statement.execute();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}

		_AutoHuntingPunish = null;
		sendMessage(isLangRus() ? "Đ’Đ°Ń�Đ° Đ˝Đ°ĐşĐ°Đ·Đ°Đ˝Đ¸ŃŹ Đ¸Ń�Ń‚ĐµĐş. ĐťĐµ Đ±ĐľŃ‚ Ń�Đ˝ĐľĐ˛Đ°!" : "Your punishment has expired. Do not bot again!");
	}

	// Synerge - Killing Spree System
	private int _killingSpreeKills = 0;

	public void addKillingSpreeKill()
	{
		if (!Config.KILLING_SPREE_ENABLED)
		{
			return;
		}

		_killingSpreeKills++;

		// Send the current killing spree
		sendPacket(new ExShowScreenMessage("+" + _killingSpreeKills + " PvPs", 6000, ScreenMessageAlign.BOTTOM_RIGHT, false));

		// Change color on breakpoint
		if (Config.KILLING_SPREE_COLORS.containsKey(_killingSpreeKills))
		{
			final String color = Config.KILLING_SPREE_COLORS.get(_killingSpreeKills);
			setVisibleNameColor(Integer.decode("0x" + color));
			broadcastUserInfo(true);
		}

		// Announce on breakpoint
		if (Config.KILLING_SPREE_ANNOUNCEMENTS.containsKey(_killingSpreeKills))
		{
			final String text = Config.KILLING_SPREE_ANNOUNCEMENTS.get(_killingSpreeKills);
			Announcements.getInstance().announceToAll(text.replace("%name%", getName()));

			// Send the current killing spree to the player with the pvps, replacing the other screenmessage
			sendPacket(new ExShowScreenMessage("+" + _killingSpreeKills + " PvPs\n" + text.replace("%name%", "").replace("is on an", "").replace("is on a", "").replace("is on", "").replace(" is ", "").trim(), 6000, ScreenMessageAlign.MIDDLE_RIGHT, false));
		}
	}

	public void resetKillingSpreeKills()
	{
		if (!Config.KILLING_SPREE_ENABLED)
		{
			return;
		}

		_killingSpreeKills = 0;

		// Reset name color
		if (!Config.KILLING_SPREE_COLORS.isEmpty())
		{
			setVisibleNameColor(0);
			broadcastUserInfo(true);
		}

		// End announcements
		if (!Config.KILLING_SPREE_ANNOUNCEMENTS.isEmpty() && _killingSpreeKills >= (int) Config.KILLING_SPREE_ANNOUNCEMENTS.keySet().toArray()[0])
		{
			Announcements.getInstance().announceToAll(getName() + "'s Killing Spree has ended!");
		}
	}

	// Synerge - Community Augmentation Service
	private int _communityAugmentStat = 0;

	public void setCommunityAugmentStat(int id)
	{
		_communityAugmentStat = id;
	}

	public int getCommnityAugmentStat()
	{
		return _communityAugmentStat;
	}

	private ItemInstance _communityAugmentItem = null;

	public void setCommunityAugmentItem(ItemInstance item)
	{
		_communityAugmentItem = item;
	}

	public ItemInstance getCommnityAugmentItem()
	{
		return _communityAugmentItem;
	}

	// Synerge - Community Academy
	private boolean _isInAcademyList = false;
	private int _pledgeItemId = 0;
	private long _pledgePrice = 0;

	public void setSearchforAcademy(boolean search)
	{
		_isInAcademyList = search;
	}

	public boolean isInSearchOfAcademy()
	{
		return _isInAcademyList;
	}

	public int getPledgeItemId()
	{
		return _pledgeItemId;
	}

	public void setPledgeItemId(int itemId)
	{
		_pledgeItemId = itemId;
	}

	public void setPledgePrice(long price)
	{
		_pledgePrice = price;
	}

	public long getPledgePrice()
	{
		return _pledgePrice;
	}

	// Synerge - Dont show emotions config
	private boolean _notShowEmotions = false;

	public boolean isNotShowEmotions()
	{
		return _notShowEmotions;
	}

	public void setNotShowEmotions(boolean value)
	{
		_notShowEmotions = value;
	}

	// Synerge - Dont show olympiad announcements config
	private boolean _isNotShowOlympiadAnnouncements = false;

	public boolean isNotShowOlympiadAnnouncements()
	{
		return _isNotShowOlympiadAnnouncements;
	}

	public void setNotShowOlympiadAnnouncements(boolean value)
	{
		_isNotShowOlympiadAnnouncements = value;
	}

	public String getTwitch()
	{
		return getVar("twitch");
	}

	public void setTwitch(String twitch)
	{
		setVar("twitch", twitch);
	}

	private AbstractTournament _tournament;

	public void setTournament(AbstractTournament tour)
	{
		_tournament = tour;
	}

	public AbstractTournament getTournament()
	{
		return _tournament;
	}

	public boolean isRegisteredInTournament()
	{
		return _tournament != null;
	}

	public boolean isInTournament()
	{
		return isRegisteredInTournament() && _tournament.getPhase() == TournamentPhase.ACTIVE;
	}

	private Location _lastLocation;

	public void setLastLocation(Location loc)
	{
		_lastLocation = loc;
	}

	public Location getLastLocation()
	{
		return _lastLocation;
	}
}