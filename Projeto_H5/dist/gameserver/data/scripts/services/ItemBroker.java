package services;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.data.xml.holder.RecipeHolder;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Recipe;
import l2f.gameserver.model.World;
import l2f.gameserver.model.base.Element;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.items.ManufactureItem;
import l2f.gameserver.model.items.TradeItem;
import l2f.gameserver.network.serverpackets.CharInfo;
import l2f.gameserver.network.serverpackets.ExShowTrace;
import l2f.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import l2f.gameserver.network.serverpackets.PrivateStoreMsgSell;
import l2f.gameserver.network.serverpackets.RadarControl;
import l2f.gameserver.network.serverpackets.RecipeShopMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.templates.item.ItemTemplate.ItemClass;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.Util;

public class ItemBroker extends Functions
{
	private static final int MAX_ITEMS_PER_PAGE = 10;
	private static final int MAX_PAGES_PER_LIST = 9;

	private static Map<Integer, NpcInfo> _npcInfos = new ConcurrentHashMap<Integer, NpcInfo>();

	public int[] RARE_ITEMS =
	{
		16255,
		16256,
		16257,
		16258,
		16259,
		16260,
		16261,
		16262,
		16263,
		16264,
		16265,
		16266,
		16267,
		16268,
		16269,
		16270,
		16271,
		16272,
		16273,
		16274,
		16275,
		16276,
		16277,
		16278,
		16279,
		16280,
		16281,
		16282,
		16283,
		16284,
		16285,
		16286,
		16287,
		16288,
		16357,
		16358,
		16359,
		16360,
		16361,
		16362,
		10119,
		10120,
		10121,
		11349,
		11350,
		11351,
		11352,
		11353,
		11354,
		11355,
		11356,
		11357,
		11358,
		11359,
		11360,
		11361,
		11363,
		11364,
		11365,
		11366,
		11367,
		11368,
		11369,
		11370,
		11371,
		11372,
		11373,
		11375,
		11376,
		11377,
		11378,
		11379,
		11380,
		11381,
		11382,
		11383,
		11384,
		11386,
		11387,
		11388,
		11389,
		11390,
		11391,
		11392,
		11393,
		11394,
		11395,
		11396,
		11397,
		11398,
		11399,
		11400,
		11401,
		11402,
		11403,
		11404,
		11405,
		11406,
		11407,
		11408,
		11409,
		11410,
		11411,
		11412,
		11413,
		11414,
		11415,
		11417,
		11418,
		11419,
		11420,
		11421,
		11422,
		11423,
		11424,
		11426,
		11427,
		11428,
		11429,
		11430,
		11431,
		11432,
		11433,
		11434,
		11435,
		11436,
		11437,
		11438,
		11439,
		11440,
		11441,
		11442,
		11443,
		11444,
		11445,
		11446,
		11447,
		11448,
		11449,
		11450,
		11451,
		11452,
		11453,
		11454,
		11455,
		11456,
		11457,
		11458,
		11459,
		11460,
		11461,
		11462,
		11463,
		11464,
		11465,
		11466,
		11467,
		11468,
		11470,
		11471,
		11472,
		11473,
		11474,
		11475,
		11476,
		11477,
		11478,
		11479,
		11481,
		11482,
		11483,
		11484,
		11485,
		11486,
		11487,
		11488,
		11489,
		11490,
		11491,
		11492,
		11493,
		11494,
		11495,
		11496,
		11497,
		11498,
		11499,
		11500,
		11501,
		11503,
		11504,
		11505,
		11506,
		11507,
		11509,
		11510,
		11511,
		11512,
		11513,
		11514,
		11515,
		11516,
		11517,
		11518,
		11519,
		11520,
		11521,
		11522,
		11523,
		11524,
		11525,
		11526,
		11527,
		11528,
		11529,
		11530,
		11531,
		11533,
		11534,
		11535,
		11536,
		11537,
		11538,
		11539,
		11540,
		11541,
		11542,
		11543,
		11544,
		11545,
		11546,
		11547,
		11548,
		11549,
		11550,
		11551,
		11552,
		11553,
		11554,
		11555,
		11556,
		11557,
		11558,
		11559,
		11560,
		11561,
		11562,
		11563,
		11564,
		11565,
		11566,
		11567,
		11568,
		11570,
		11571,
		11572,
		11573,
		11574,
		11575,
		11576,
		11577,
		11578,
		11579,
		11580,
		11581,
		11582,
		11583,
		11584,
		11585,
		11586,
		11587,
		11588,
		11589,
		11590,
		11591,
		11592,
		11593,
		11594,
		11595,
		11596,
		11597,
		11598,
		11599,
		11600,
		11601,
		11602,
		11603,
		11604,
		12978,
		12979,
		12980,
		12981,
		12982,
		12983,
		12984,
		12985,
		12986,
		12987,
		12988,
		12989,
		12990,
		12991,
		12992,
		12993,
		12994,
		12995,
		12996,
		12997,
		12998,
		12999,
		13000,
		13001,
		13078,
		16289,
		16290,
		16291,
		16292,
		16293,
		16294,
		16295,
		16296,
		16297,
		16298,
		16299,
		16300,
		16301,
		16302,
		16303,
		16305,
		16306,
		16307,
		16308,
		16309,
		16310,
		16311,
		16312,
		16313,
		16314,
		16315,
		16316,
		16317,
		16318,
		16319,
		16320,
		16322,
		16323,
		16324,
		16325,
		16326,
		16327,
		16328,
		16329,
		16330,
		16331,
		16332,
		16333,
		16334,
		16335,
		16336,
		16337,
		16339,
		16340,
		16341,
		16342,
		16343,
		16344,
		16345,
		16346,
		16347,
		16348,
		16349,
		16350,
		16351,
		16352,
		16353,
		16354,
		16356,
		16369,
		16370,
		16371,
		16372,
		16373,
		16374,
		16375,
		16376,
		16377,
		16378,
		16379,
		16380,
		16837,
		16838,
		16839,
		16840,
		16841,
		16842,
		16843,
		16844,
		16845,
		16846,
		16847,
		16848,
		16849,
		16850,
		16851,
		10870,
		10871,
		10872,
		10873,
		10874,
		10875,
		10876,
		10877,
		10878,
		10879,
		10880,
		10881,
		10882,
		10883,
		10884,
		10885,
		10886,
		10887,
		10888,
		10889,
		10890,
		10891,
		10892,
		10893,
		10894,
		10895,
		10896,
		10897,
		10898,
		10899,
		10900,
		10901,
		10902,
		10903,
		10904,
		10905,
		10906,
		10907,
		10908,
		10909,
		10910,
		10911,
		10912,
		10913,
		10914,
		10915,
		10916,
		10917,
		10918,
		10919,
		10920,
		10921,
		10922,
		10923,
		10924,
		10925,
		10926,
		10927,
		10928,
		10929,
		10930,
		10931,
		10932,
		10933,
		10934,
		10935,
		10936,
		10937,
		10938,
		10939,
		10940,
		10941,
		10942,
		10943,
		10944,
		10945,
		10946,
		10947,
		10948,
		10949,
		10950,
		10951,
		10952,
		10953,
		10954,
		10955,
		10956,
		10957,
		10958,
		10959,
		10960,
		10961,
		10962,
		10963,
		10964,
		10965,
		10966,
		10967,
		10968,
		10969,
		10970,
		10971,
		10972,
		10973,
		10974,
		10975,
		10976,
		10977,
		10978,
		10979,
		10980,
		10981,
		10982,
		10983,
		10984,
		10985,
		10986,
		10987,
		10988,
		10989,
		10990,
		10991,
		10992,
		10993,
		10994,
		10995,
		10996,
		10997,
		10998,
		10999,
		11000,
		11001,
		11002,
		11003,
		11004,
		11005,
		11006,
		11007,
		11008,
		11009,
		11010,
		11011,
		11012,
		11013,
		11014,
		11015,
		11016,
		11017,
		11018,
		11019,
		11020,
		11021,
		11022,
		11023,
		11024,
		11025,
		11026,
		11027,
		11028,
		11029,
		11030,
		11031,
		11032,
		11033,
		11034,
		11035,
		11036,
		11037,
		11038,
		11039,
		11040,
		11041,
		11042,
		11043,
		11044,
		11045,
		11046,
		11047,
		11048,
		11049,
		11050,
		11051,
		11052,
		11053,
		11054,
		11055,
		11056,
		11057,
		11058,
		11059,
		11060,
		11061,
		11062,
		11063,
		11064,
		11065,
		11066,
		11067,
		11068,
		11069,
		11070,
		11071,
		11072,
		11073,
		11074,
		11075,
		11076,
		11077,
		11078,
		11079,
		11080,
		11081,
		11082,
		11083,
		11084,
		11085,
		11086,
		11087,
		11088,
		11089,
		11090,
		11091,
		11092,
		11093,
		11094,
		11095,
		11096,
		11097,
		11098,
		11099,
		11100,
		11101,
		11102,
		11103,
		11104,
		11105,
		11106,
		11107,
		11108,
		11109,
		11110,
		11111,
		11112,
		11113,
		11114,
		11115,
		11116,
		11117,
		11118,
		11119,
		11120,
		11121,
		11122,
		11123,
		11124,
		11125,
		11126,
		11127,
		11128,
		11129,
		11130,
		11131,
		11132,
		11133,
		11134,
		11135,
		11136,
		11137,
		11138,
		11139,
		11140,
		11141,
		11142,
		11143,
		11144,
		11145,
		11146,
		11147,
		11148,
		11149,
		11150,
		11151,
		11152,
		11153,
		11154,
		11155,
		11156,
		11157,
		11158,
		11159,
		11160,
		11161,
		11162,
		11163,
		11164,
		11165,
		11166,
		11167,
		11168,
		11169,
		11170,
		11171,
		11172,
		11173,
		11174,
		11175,
		11176,
		11177,
		11178,
		11179,
		11180,
		11181,
		11182,
		11183,
		11184,
		11185,
		11186,
		11187,
		11188,
		11189,
		11190,
		11191,
		11192,
		11193,
		11194,
		11195,
		11196,
		11197,
		11198,
		11199,
		11200,
		11201,
		11202,
		11203,
		11204,
		11205,
		11206,
		11207,
		11208,
		11209,
		11210,
		11211,
		11212,
		11213,
		11214,
		11215,
		11216,
		11217,
		11218,
		11219,
		11220,
		11221,
		11222,
		11223,
		11224,
		11225,
		11226,
		11227,
		11228,
		11229,
		11230,
		11231,
		11232,
		11233,
		11234,
		11235,
		11236,
		11237,
		11238,
		11239,
		11240,
		11241,
		11242,
		11243,
		11244,
		11245,
		11246,
		11247,
		11248,
		11249,
		11250,
		11251,
		11252,
		11253,
		11254,
		11255,
		11256,
		11257,
		11258,
		11259,
		11260,
		11261,
		11262,
		11263,
		11264,
		11265,
		11266,
		11267,
		11268,
		11269,
		11270,
		11271,
		11272,
		11273,
		11274,
		11275,
		11276,
		11277,
		11278,
		11279,
		11280,
		11281,
		11282,
		11283,
		11284,
		11285,
		11286,
		11287,
		11288,
		11289,
		11290,
		11291,
		11292,
		11293,
		11294,
		11295,
		11296,
		11297,
		11298,
		11299,
		11300,
		11301,
		11302,
		11303,
		11304,
		11305,
		11306,
		11307,
		11308,
		11309,
		11310,
		11311,
		11312,
		11313,
		11314,
		11315,
		11316,
		11317,
		11318,
		11319,
		11320,
		11321,
		11322,
		11323,
		11324,
		11325,
		11326,
		11327,
		11328,
		11329,
		11330,
		11331,
		11332,
		11333,
		11334,
		11335,
		11336,
		11337,
		11338,
		11339,
		11340,
		11341,
		11342,
		11343,
		11344,
		11345,
		11346,
		11347,
		11348,
		11362,
		11374,
		11385,
		11416,
		11425,
		11469,
		11480,
		11502,
		11508,
		11532,
		11569,
		12852,
		12853,
		12854,
		12855,
		12856,
		12857,
		12858,
		12859,
		12860,
		12861,
		12862,
		12863,
		12864,
		12865,
		12866,
		12867,
		12868,
		12869,
		12870,
		12871,
		12872,
		12873,
		12874,
		12875,
		12876,
		12877,
		12878,
		12879,
		12880,
		12881,
		12882,
		12883,
		12884,
		12885,
		12886,
		12887,
		12888,
		12889,
		12890,
		12891,
		12892,
		12893,
		12894,
		12895,
		12896,
		12897,
		12898,
		12899,
		12900,
		12901,
		12902,
		12903,
		12904,
		12905,
		12906,
		12907,
		12908,
		12909,
		12910,
		12911,
		12912,
		12913,
		12914,
		12915,
		12916,
		12917,
		12918,
		12919,
		12920,
		12921,
		12922,
		12923,
		12924,
		12925,
		12926,
		12927,
		12928,
		12929,
		12930,
		12931,
		12932,
		12933,
		12934,
		12935,
		12936,
		12937,
		12938,
		12939,
		12940,
		12941,
		12942,
		12943,
		12944,
		12945,
		12946,
		12947,
		12948,
		12949,
		12950,
		12951,
		12952,
		12953,
		12954,
		12955,
		12956,
		12957,
		12958,
		12959,
		12960,
		12961,
		12962,
		12963,
		12964,
		12965,
		12966,
		12967,
		12968,
		12969,
		12970,
		12971,
		12972,
		12973,
		12974,
		12975,
		12976,
		12977,
		14412,
		14413,
		14414,
		14415,
		14416,
		14417,
		14418,
		14419,
		14420,
		14421,
		14422,
		14423,
		14424,
		14425,
		14426,
		14427,
		14428,
		14429,
		14430,
		14431,
		14432,
		14433,
		14434,
		14435,
		14436,
		14437,
		14438,
		14439,
		14440,
		14441,
		14442,
		14443,
		14444,
		14445,
		14446,
		14447,
		14448,
		14449,
		14450,
		14451,
		14452,
		14453,
		14454,
		14455,
		14456,
		14457,
		14458,
		14459,
		14460,
		14526,
		14527,
		14528,
		14529,
		14560,
		14561,
		14562,
		14563,
		14564,
		14565,
		14566,
		14567,
		14568,
		14569,
		14570,
		14571,
		14572,
		14573,
		14574,
		14575,
		14576,
		14577,
		14578,
		14579,
		14580,
		14581,
		16042,
		16043,
		16044,
		16045,
		16046,
		16047,
		16048,
		16049,
		16050,
		16051,
		16052,
		16053,
		16054,
		16055,
		16056,
		16057,
		16058,
		16059,
		16060,
		16061,
		16062,
		16063,
		16064,
		16065,
		16066,
		16067,
		16068,
		16069,
		16070,
		16071,
		16072,
		16073,
		16074,
		16075,
		16076,
		16077,
		16078,
		16079,
		16080,
		16081,
		16082,
		16083,
		16084,
		16085,
		16086,
		16087,
		16088,
		16089,
		16090,
		16091,
		16092,
		16093,
		16094,
		16095,
		16096,
		16097,
		16134,
		16135,
		16136,
		16137,
		16138,
		16139,
		16140,
		16141,
		16142,
		16143,
		16144,
		16145,
		16146,
		16147,
		16148,
		16149,
		16150,
		16151,
		16179,
		16180,
		16181,
		16182,
		16183,
		16184,
		16185,
		16186,
		16187,
		16188,
		16189,
		16190,
		16191,
		16192,
		16193,
		16194,
		16195,
		16196,
		16197,
		16198,
		16199,
		16200,
		16201,
		16202,
		16203,
		16204,
		16205,
		16206,
		16207,
		16208,
		16209,
		16210,
		16211,
		16212,
		16213,
		16214,
		16215,
		16216,
		16217,
		16218,
		16219,
		16220,
		16304,
		16321,
		16338,
		16355
	};

	public class NpcInfo
	{
		public long lastUpdate;
		public TreeMap<String, TreeMap<Long, Item>> bestSellItems;
		public TreeMap<String, TreeMap<Long, Item>> bestBuyItems;
		public TreeMap<String, TreeMap<Long, Item>> bestCraftItems;
	}

	public class Item
	{
		public final int itemId;
		public final int itemObjId;
		public final int type;
		public final long price;
		public final long count;
		public final int enchant;
		public final boolean rare;
		public final int merchantObjectId;
		public final String name;

		public final String merchantName;
		public final Location player;
		public final TradeItem item;
		public final boolean isPackage;

		public Item(int itemId, int type, long price, long count, int enchant, String itemName, int mobjectId, String merchantName, Location player, int itemObjId, TradeItem item, boolean isPkg)
		{
			this.itemId = itemId;
			this.type = type;
			this.price = price;
			this.count = count;
			this.enchant = enchant;
			rare = org.apache.commons.lang3.ArrayUtils.contains(RARE_ITEMS, itemId);

			StringBuilder out = new StringBuilder(70);
			if (enchant > 0)
			{
				if (rare)
				{
					out.append("<font color=\"FF0000\">+");
				}
				else
				{
					out.append("<font color=\"7CFC00\">+");
				}
				out.append(enchant);
				out.append(" ");
			}
			else if (rare)
			{
				out.append("<font color=\"0000FF\">Rare ");
			}
			else
			{
				out.append("<font color=\"LEVEL\">");
			}

			out.append(itemName);
			out.append("</font>]");

			if (item != null)
			{
				if (item.getAttackElement() != Element.NONE.getId())
				{
					out.append(" &nbsp;<font color=\"7CFC00\">+");
					out.append(item.getAttackElementValue());
					switch (item.getAttackElement())
					{
					case ItemTemplate.ATTRIBUTE_FIRE:
						out.append(" Fire");
						break;
					case ItemTemplate.ATTRIBUTE_WATER:
						out.append(" Water");
						break;
					case ItemTemplate.ATTRIBUTE_WIND:
						out.append(" Wind");
						break;
					case ItemTemplate.ATTRIBUTE_EARTH:
						out.append(" Earth");
						break;
					case ItemTemplate.ATTRIBUTE_HOLY:
						out.append(" Holy");
						break;
					case ItemTemplate.ATTRIBUTE_DARK:
						out.append(" Unholy");
						break;
					}
					out.append("</font>");
				}
				else
				{
					final int fire = item.getDefenceFire();
					final int water = item.getDefenceWater();
					final int wind = item.getDefenceWind();
					final int earth = item.getDefenceEarth();
					final int holy = item.getDefenceHoly();
					final int unholy = item.getDefenceUnholy();
					if (fire + water + wind + earth + holy + unholy > 0)
					{
						out.append("&nbsp;<font color=\"7CFC00\">");
						if (fire > 0)
						{
							out.append("+");
							out.append(fire);
							out.append(" Fire ");
						}
						if (water > 0)
						{
							out.append("+");
							out.append(water);
							out.append(" Water ");
						}
						if (wind > 0)
						{
							out.append("+");
							out.append(wind);
							out.append(" Wind ");
						}
						if (earth > 0)
						{
							out.append("+");
							out.append(earth);
							out.append(" Earth ");
						}
						if (holy > 0)
						{
							out.append("+");
							out.append(holy);
							out.append(" Holy ");
						}
						if (unholy > 0)
						{
							out.append("+");
							out.append(unholy);
							out.append(" Unholy ");
						}
						out.append("</font>");
					}
				}
			}
			name = out.toString();

			merchantObjectId = mobjectId;
			this.merchantName = merchantName;
			this.player = player;
			this.itemObjId = itemObjId;
			this.item = item;
			isPackage = isPkg;
		}
	}

	private TreeMap<String, TreeMap<Long, Item>> getItems(int type)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return null;
		}
		updateInfo(player, npc);
		NpcInfo info = _npcInfos.get(getNpc().getObjectId());
		if (info == null)
		{
			return null;
		}
		switch (type)
		{
		case Player.STORE_PRIVATE_SELL:
			return info.bestSellItems;
		case Player.STORE_PRIVATE_BUY:
			return info.bestBuyItems;
		case Player.STORE_PRIVATE_MANUFACTURE:
			return info.bestCraftItems;
		}
		return null;
	}

	public String DialogAppend_32320(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_32321(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String DialogAppend_32322(Integer val)
	{
		return getHtmlAppends(val);
	}

	public String getHtmlAppends(Integer val)
	{
		if (!Config.ITEM_BROKER_ITEM_SEARCH)
		{
			return "";
		}

		StringBuilder append = new StringBuilder();
		int type = 0;
		String typeNameEn = "";

		switch (val)
		{
		case 0:
			append.append("<br><font color=\"LEVEL\">Search for dealers:</font><br1>");
			append.append("[npc_%objectId%_Chat 11|<font color=\"FF9900\">The list of goods for sale</font>]<br1>");
			append.append("[npc_%objectId%_Chat 13|<font color=\"FF9900\">The list of goods to buy</font>]<br1>");
			append.append("[npc_%objectId%_Chat 15|<font color=\"FF9900\">The list of goods to craft</font>]<br1>");
			break;
		case 10 + Player.STORE_PRIVATE_SELL:
			type = Player.STORE_PRIVATE_SELL;
			typeNameEn = "sell";
			break;
		case 10 + Player.STORE_PRIVATE_BUY:
			type = Player.STORE_PRIVATE_BUY;
			typeNameEn = "buy";
			break;
		case 10 + Player.STORE_PRIVATE_MANUFACTURE:
			type = Player.STORE_PRIVATE_MANUFACTURE;
			typeNameEn = "craft";
			break;
		case 20 + Player.STORE_PRIVATE_SELL:
		case 20 + Player.STORE_PRIVATE_BUY:
		case 20 + Player.STORE_PRIVATE_MANUFACTURE:
			type = val - 20;
			append.append("!The list of equipment:<br>");

			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 1 1 0 0|<font color=\"FF9900\">Weapons</font>]<br1>");
			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 2 1 0 0|<font color=\"FF9900\">Armors</font>]<br1>");
			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 3 1 0 0|<font color=\"FF9900\">Jewels</font>]<br1>");
			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 4 1 0 0|<font color=\"FF9900\">Accessories</font>]<br1>");

			append.append("<br>[npc_%objectId%_Chat ").append(10 + type).append("|<font color=\"FF9900\">Back</font>]");
			return append.toString();
		case 30 + Player.STORE_PRIVATE_SELL:
		case 30 + Player.STORE_PRIVATE_BUY:
		case 30 + Player.STORE_PRIVATE_MANUFACTURE:
			type = val - 30;
			append.append("!The list of equipment, enchanted to +4 and more:<br>");

			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 1 1 4 0|<font color=\"FF9900\">Weapons+</font>]<br1>");
			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 2 1 4 0|<font color=\"FF9900\">Armors+</font>]<br1>");
			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 3 1 4 0|<font color=\"FF9900\">Jewels+</font>]<br1>");
			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 4 1 4 0|<font color=\"FF9900\">Accessories+</font>]<br1>");

			append.append("<br>[npc_%objectId%_Chat ").append(10 + type).append("|<font color=\"FF9900\">Back</font>]");

			return append.toString();
		}

		if (type > 0)
		{
			append.append("!The list of goods to ").append(typeNameEn).append(":<br>");

			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 0 1 0 0|<font color=\"FF9900\">List all</font>]<br1>");
			append.append("[npc_%objectId%_Chat ").append(type + 20).append("|<font color=\"FF9900\">Equipment</font>]<br1>");
			if (type == Player.STORE_PRIVATE_SELL)
			{
				append.append("[npc_%objectId%_Chat ").append(type + 30).append("|<font color=\"FF9900\">Equipment +4 and more</font>]<br1>");
			}
			if (type != Player.STORE_PRIVATE_MANUFACTURE)
			{
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 0 1 0 1|<font color=\"FF9900\">Rare equipment</font>]<br1>");
			}
			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 5 1 0 0|<font color=\"FF9900\">Consumable</font>]<br1>");
			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 6 1 0 0|<font color=\"FF9900\">Matherials</font>]<br1>");
			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 7 1 0 0|<font color=\"FF9900\">Key matherials</font>]<br1>");
			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 8 1 0 0|<font color=\"FF9900\">Recipies</font>]<br1>");
			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 9 1 0 0|<font color=\"FF9900\">Books and amulets</font>]<br1>");
			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 10 1 0 0|<font color=\"FF9900\">Enchant items</font>]<br1>");
			append.append("[scripts_services.ItemBroker:list ").append(type).append(" 11 1 0 0|<font color=\"FF9900\">Other</font>]<br1>");
			if (type != Player.STORE_PRIVATE_MANUFACTURE)
			{
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 90 1 0 0|<font color=\"FF9900\">Commons</font>]<br1>");
			}

			append.append("<edit var=\"tofind\" width=100><br1>");
			append.append("[scripts_services.ItemBroker:find ").append(type).append(" 1 $tofind|<font color=\"FF9900\">Find</font>]<br1>");

			append.append("<br>[npc_%objectId%_Chat 0|<font color=\"FF9900\">Back</font>]");
		}

		return append.toString();
	}

	public void list(String[] var)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}

		if (var.length != 5)
		{
			show("Incorrect data length", player, npc);
			return;
		}

		int type;
		int itemType;
		int currentPage;
		int minEnchant;
		int rare;

		try
		{
			type = Integer.valueOf(var[0]);
			itemType = Integer.valueOf(var[1]);
			currentPage = Integer.valueOf(var[2]);
			minEnchant = Integer.valueOf(var[3]);
			rare = Integer.valueOf(var[4]);
		}
		catch (Exception e)
		{
			show("incorrect data", player, npc);
			return;
		}

		ItemClass itemClass = itemType >= ItemClass.values().length ? null : ItemClass.values()[itemType];

		TreeMap<String, TreeMap<Long, Item>> allItems = getItems(type);
		if (allItems == null)
		{
			show("Error - this type of objects found", player, npc);
			return;
		}

		List<Item> items = new ArrayList<Item>(allItems.size() * 10);
		for (TreeMap<Long, Item> tempItems : allItems.values())
		{
			TreeMap<Long, Item> tempItems2 = new TreeMap<Long, Item>();
			for (Entry<Long, Item> entry : tempItems.entrySet())
			{
				Item tempItem = entry.getValue();
				if ((tempItem == null) || (tempItem.enchant < minEnchant))
				{
					continue;
				}
				ItemTemplate temp = tempItem.item != null ? tempItem.item.getItem() : ItemHolder.getInstance().getTemplate(tempItem.itemId);
				if (temp == null || (rare > 0 && !tempItem.rare) || (itemClass == null ? !temp.isCommonItem() : temp.isCommonItem()))
				{
					continue;
				}
				if (itemClass != null && itemClass != ItemClass.ALL && temp.getItemClass() != itemClass)
				{
					continue;
				}
				tempItems2.put(entry.getKey(), tempItem);
			}
			if (tempItems2.isEmpty())
			{
				continue;
			}

			Item item = type == Player.STORE_PRIVATE_BUY ? tempItems2.lastEntry().getValue() : tempItems2.firstEntry().getValue();
			if (item != null)
			{
				items.add(item);
			}
		}

		StringBuilder out = new StringBuilder(200);
		out.append("[npc_%objectId%_Chat 1");
		out.append(type);
		out.append("|Back]&nbsp;&nbsp;");

		int totalPages = items.size();
		totalPages = totalPages / MAX_ITEMS_PER_PAGE + (totalPages % MAX_ITEMS_PER_PAGE > 0 ? 1 : 0);
		totalPages = Math.max(1, totalPages);
		currentPage = Math.min(totalPages, Math.max(1, currentPage));

		if (totalPages > 1)
		{
			int page = Math.max(1, Math.min(totalPages - MAX_PAGES_PER_LIST + 1, currentPage - MAX_PAGES_PER_LIST / 2));

			if (page > 1)
			{
				listPageNum(out, type, itemType, 1, minEnchant, rare, "1");
			}
			if (currentPage > 11)
			{
				listPageNum(out, type, itemType, currentPage - 10, minEnchant, rare, String.valueOf(currentPage - 10));
			}
			if (currentPage > 1)
			{
				listPageNum(out, type, itemType, currentPage - 1, minEnchant, rare, "<");
			}

			for (int count = 0; count < MAX_PAGES_PER_LIST && page <= totalPages; count++, page++)
			{
				if (page == currentPage)
				{
					out.append(page).append("&nbsp;");
				}
				else
				{
					listPageNum(out, type, itemType, page, minEnchant, rare, String.valueOf(page));
				}
			}

			if (currentPage < totalPages)
			{
				listPageNum(out, type, itemType, currentPage + 1, minEnchant, rare, ">");
			}
			if (currentPage < totalPages - 10)
			{
				listPageNum(out, type, itemType, currentPage + 10, minEnchant, rare, String.valueOf(currentPage + 10));
			}
			if (page <= totalPages)
			{
				listPageNum(out, type, itemType, totalPages, minEnchant, rare, String.valueOf(totalPages));
			}
		}

		out.append("<table width=100%>");

		if (items.size() > 0)
		{
			int count = 0;
			ListIterator<Item> iter = items.listIterator((currentPage - 1) * MAX_ITEMS_PER_PAGE);
			while (iter.hasNext() && count < MAX_ITEMS_PER_PAGE)
			{
				Item item = iter.next();
				ItemTemplate temp = item.item != null ? item.item.getItem() : ItemHolder.getInstance().getTemplate(item.itemId);
				if (temp == null)
				{
					continue;
				}

				out.append("<tr><td>");
				out.append(temp.getIcon32());
				out.append("</td><td><table width=100%><tr><td>[scripts_services.ItemBroker:listForItem ");
				out.append(type);
				out.append(" ");
				out.append(item.itemId);
				out.append(" ");
				out.append(minEnchant);
				out.append(" ");
				out.append(rare);
				out.append(" ");
				out.append(itemType);
				out.append(" 1 ");
				out.append(currentPage);
				out.append("|");
				out.append(item.name);
				out.append("</td></tr><tr><td>price: ");
				out.append(Util.formatAdena(item.price));
				if (item.isPackage)
				{
					out.append(" (Package)");
				}
				if (temp.isStackable())
				{
					out.append(", count: ").append(Util.formatAdena(item.count));
				}
				out.append("</td></tr></table></td></tr>");
				count++;
			}
		}
		else
		{
			out.append("<tr><td colspan=2>Nothing found.</td></tr>");
		}

		out.append("</table><br>&nbsp;");

		show(out.toString(), player, npc);
	}

	private void listPageNum(StringBuilder out, int type, int itemType, int page, int minEnchant, int rare, String letter)
	{
		out.append("[scripts_services.ItemBroker:list ");
		out.append(type);
		out.append(" ");
		out.append(itemType);
		out.append(" ");
		out.append(page);
		out.append(" ");
		out.append(minEnchant);
		out.append(" ");
		out.append(rare);
		out.append("|");
		out.append(letter);
		out.append("]&nbsp;");
	}

	public void listForItem(String[] var)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}

		if (var.length < 7 || var.length > 12)
		{
			show("Incorrect data length", player, npc);
			return;
		}

		int type;
		int itemId;
		int minEnchant;
		int rare;
		int itemType;
		int currentPage;
		int returnPage;
		String[] search = null;

		try
		{
			type = Integer.valueOf(var[0]);
			itemId = Integer.valueOf(var[1]);
			minEnchant = Integer.valueOf(var[2]);
			rare = Integer.valueOf(var[3]);
			itemType = Integer.valueOf(var[4]);
			currentPage = Integer.valueOf(var[5]);
			returnPage = Integer.valueOf(var[6]);
			if (var.length > 7)
			{
				search = new String[var.length - 7];
				System.arraycopy(var, 7, search, 0, search.length);
			}
		}
		catch (Exception e)
		{
			show("Incorrect data", player, npc);
			return;
		}

		ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
		if (template == null)
		{
			show("Error - itemId not specified.", player, npc);
			return;
		}

		TreeMap<String, TreeMap<Long, Item>> tmpItems = getItems(type);
		if (tmpItems == null)
		{
			show("Error - this type of subject matter is not.", player, npc);
			return;
		}

		TreeMap<Long, Item> allItems = tmpItems.get(template.getName());
		if (allItems == null)
		{
			show("Error - items with the same name found.", player, npc);
			return;
		}

		StringBuilder out = new StringBuilder(200);
		if (search == null)
		{
			listPageNum(out, type, itemType, returnPage, minEnchant, rare, "Back");
		}
		else
		{
			findPageNum(out, type, returnPage, search, "Back");
		}
		out.append("&nbsp;&nbsp;");

		NavigableMap<Long, Item> sortedItems = type == Player.STORE_PRIVATE_BUY ? allItems.descendingMap() : allItems;
		if (sortedItems == null)
		{
			show("Error - No results.", player, npc);
			return;
		}

		List<Item> items = new ArrayList<Item>(sortedItems.size());
		for (Item item : sortedItems.values())
		{
			if (item == null || item.enchant < minEnchant || (rare > 0 && !item.rare))
			{
				continue;
			}

			items.add(item);
		}

		int totalPages = items.size();
		totalPages = totalPages / MAX_ITEMS_PER_PAGE + (totalPages % MAX_ITEMS_PER_PAGE > 0 ? 1 : 0);
		totalPages = Math.max(1, totalPages);
		currentPage = Math.min(totalPages, Math.max(1, currentPage));

		if (totalPages > 1)
		{
			int page = Math.max(1, Math.min(totalPages - MAX_PAGES_PER_LIST + 1, currentPage - MAX_PAGES_PER_LIST / 2));

			if (page > 1)
			{
				listForItemPageNum(out, type, itemId, minEnchant, rare, itemType, 1, returnPage, search, "1");
			}
			if (currentPage > 11)
			{
				listForItemPageNum(out, type, itemId, minEnchant, rare, itemType, currentPage - 10, returnPage, search, String.valueOf(currentPage - 10));
			}
			if (currentPage > 1)
			{
				listForItemPageNum(out, type, itemId, minEnchant, rare, itemType, currentPage - 1, returnPage, search, "<");
			}

			for (int count = 0; count < MAX_PAGES_PER_LIST && page <= totalPages; count++, page++)
			{
				if (page == currentPage)
				{
					out.append(page).append("&nbsp;");
				}
				else
				{
					listForItemPageNum(out, type, itemId, minEnchant, rare, itemType, page, returnPage, search, String.valueOf(page));
				}
			}

			if (currentPage < totalPages)
			{
				listForItemPageNum(out, type, itemId, minEnchant, rare, itemType, currentPage + 1, returnPage, search, ">");
			}
			if (currentPage < totalPages - 10)
			{
				listForItemPageNum(out, type, itemId, minEnchant, rare, itemType, currentPage + 10, returnPage, search, String.valueOf(currentPage + 10));
			}
			if (page <= totalPages)
			{
				listForItemPageNum(out, type, itemId, minEnchant, rare, itemType, totalPages, returnPage, search, String.valueOf(totalPages));
			}
		}

		out.append("<table width=100%>");

		if (items.size() > 0)
		{
			int count = 0;
			ListIterator<Item> iter = items.listIterator((currentPage - 1) * MAX_ITEMS_PER_PAGE);
			while (iter.hasNext() && count < MAX_ITEMS_PER_PAGE)
			{
				Item item = iter.next();
				ItemTemplate temp = item.item != null ? item.item.getItem() : ItemHolder.getInstance().getTemplate(item.itemId);
				if (temp == null)
				{
					continue;
				}

				out.append("<tr><td>");
				out.append(temp.getIcon32());
				out.append("</td><td><table width=100%><tr><td>[scripts_services.ItemBroker:path ");
				out.append(type);
				out.append(" ");
				out.append(item.itemId);
				out.append(" ");
				out.append(item.itemObjId);
				out.append("|");
				out.append(item.name);
				out.append("</td></tr><tr><td>price: ");
				out.append(Util.formatAdena(item.price));
				if (item.isPackage)
				{
					out.append(" (Package)");
				}
				if (temp.isStackable())
				{
					out.append(", count: ").append(Util.formatAdena(item.count));
				}
				out.append(", owner: ").append(item.merchantName);
				out.append("</td></tr></table></td></tr>");
				count++;
			}
		}
		else
		{
			out.append("<tr><td colspan=2>Nothing found.</td></tr>");
		}

		out.append("</table><br>&nbsp;");

		show(out.toString(), player, npc);
	}

	private void listForItemPageNum(StringBuilder out, int type, int itemId, int minEnchant, int rare, int itemType, int page, int returnPage, String[] search, String letter)
	{
		out.append("[scripts_services.ItemBroker:listForItem ");
		out.append(type);
		out.append(" ");
		out.append(itemId);
		out.append(" ");
		out.append(minEnchant);
		out.append(" ");
		out.append(rare);
		out.append(" ");
		out.append(itemType);
		out.append(" ");
		out.append(page);
		out.append(" ");
		out.append(returnPage);
		if (search != null)
		{
			for (int i = 0; i < search.length; i++)
			{
				out.append(" ");
				out.append(search[i]);
			}
		}
		out.append("|");
		out.append(letter);
		out.append("]&nbsp;");
	}

	public void path(String[] var)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}

		if (var.length != 3)
		{
			show("Incorrect data length", player, npc);
			return;
		}

		int type;
		int itemId;
		int itemObjId;

		try
		{
			type = Integer.valueOf(var[0]);
			itemId = Integer.valueOf(var[1]);
			itemObjId = Integer.valueOf(var[2]);
		}
		catch (Exception e)
		{
			show("Incorrect data", player, npc);
			return;
		}

		ItemTemplate temp = ItemHolder.getInstance().getTemplate(itemId);
		if (temp == null)
		{
			show("Error - itemId not specified.", player, npc);
			return;
		}

		TreeMap<String, TreeMap<Long, Item>> allItems = getItems(type);
		if (allItems == null)
		{
			show("Error - this type of objects found.", player, npc);
			return;
		}

		TreeMap<Long, Item> items = allItems.get(temp.getName());
		if (items == null)
		{
			show("Error - items with the same name found.", player, npc);
			return;
		}

		Item item = null;
		for (Item i : items.values())
		{
			if (i.itemObjId == itemObjId)
			{
				item = i;
				break;
			}
		}

		if (item == null)
		{
			show("Error - object not found.", player, npc);
			return;
		}

		boolean found = false;
		Player trader = GameObjectsStorage.getPlayer(item.merchantObjectId);
		if (trader == null)
		{
			show("Merchant not found, maybe he got out of the.", player, npc);
			return;
		}

		switch (type)
		{
		case Player.STORE_PRIVATE_SELL:
			if (trader.getSellList() != null)
			{
				if (trader.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE)
				{
					if (item.isPackage)
					{
						long packagePrice = 0;
						for (TradeItem tradeItem : trader.getSellList())
						{
							packagePrice += tradeItem.getOwnersPrice() * tradeItem.getCount();
							if (tradeItem.getItemId() == item.itemId)
							{
								found = true;
							}
						}

						if (packagePrice != item.price)
						{
							found = false;
						}
					}
				}
				else if (!item.isPackage)
				{
					for (TradeItem tradeItem : trader.getSellList())
					{
						if (tradeItem.getItemId() == item.itemId && tradeItem.getOwnersPrice() == item.price)
						{
							found = true;
							break;
						}
					}
				}
			}
			break;
		case Player.STORE_PRIVATE_BUY:
			if (trader.getBuyList() != null)
			{
				for (TradeItem tradeItem : trader.getBuyList())
				{
					if (tradeItem.getItemId() == item.itemId && tradeItem.getOwnersPrice() == item.price)
					{
						found = true;
						break;
					}
				}
			}
			break;
		case Player.STORE_PRIVATE_MANUFACTURE:
			found = true; // not done
			break;
		}

		if (!found)
		{
			show("Caution, price or item was changed, please be careful !", player, npc);
		}

		ExShowTrace trace = new ExShowTrace();
		trace.addLine(item.player, item.player, 30, 60000);
		player.sendPacket(trace);
		RadarControl rc = new RadarControl(0, 1, item.player);
		player.sendPacket(rc);

		if (player.getVarB(Player.NO_TRADERS_VAR))
		{
			player.sendPacket(new CharInfo(trader, player));
			if (trader.getPrivateStoreType() == Player.STORE_PRIVATE_BUY)
			{
				player.sendPacket(new PrivateStoreMsgBuy(trader));
			}
			else if (trader.getPrivateStoreType() == Player.STORE_PRIVATE_SELL || trader.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE)
			{
				player.sendPacket(new PrivateStoreMsgSell(trader));
			}
			else if (trader.getPrivateStoreType() == Player.STORE_PRIVATE_MANUFACTURE)
			{
				player.sendPacket(new RecipeShopMsg(trader));
			}

		}

		player.setTarget(trader);
	}

	public void updateInfo(Player player, NpcInstance npc)
	{
		NpcInfo info = _npcInfos.get(npc.getObjectId());
		if (info == null || info.lastUpdate < System.currentTimeMillis() - 300000)
		{
			info = new NpcInfo();
			info.lastUpdate = System.currentTimeMillis();
			info.bestBuyItems = new TreeMap<String, TreeMap<Long, Item>>();
			info.bestSellItems = new TreeMap<String, TreeMap<Long, Item>>();
			info.bestCraftItems = new TreeMap<String, TreeMap<Long, Item>>();

			int itemObjId = 0;

			for (Player pl : World.getAroundPlayers(npc))
			{
				TreeMap<String, TreeMap<Long, Item>> items = null;
				List<TradeItem> tradeList = null;

				int type = pl.getPrivateStoreType();
				switch (type)
				{
				case Player.STORE_PRIVATE_SELL:
					items = info.bestSellItems;
					tradeList = pl.getSellList();

					for (TradeItem item : tradeList)
					{
						ItemTemplate temp = item.getItem();
						if (temp == null)
						{
							continue;
						}
						TreeMap<Long, Item> oldItems = items.get(temp.getName());
						if (oldItems == null)
						{
							oldItems = new TreeMap<Long, Item>();
							items.put(temp.getName(), oldItems);
						}
						Item newItem = new Item(item.getItemId(), type, item.getOwnersPrice(), item.getCount(), item.getEnchantLevel(), temp.getName(), pl.getObjectId(), pl.getName(), pl.getLoc(),
									item.getObjectId(), item, false);
						long key = newItem.price * 100;
						while (key < newItem.price * 100 + 100 && oldItems.containsKey(key))
						{
							key++;
						}
						oldItems.put(key, newItem);
					}

					break;
				case Player.STORE_PRIVATE_SELL_PACKAGE:
					items = info.bestSellItems;
					tradeList = pl.getSellList();

					long packagePrice = 0;
					for (TradeItem item : tradeList)
					{
						packagePrice += item.getOwnersPrice() * item.getCount();
					}

					for (TradeItem item : tradeList)
					{
						ItemTemplate temp = item.getItem();
						if (temp == null)
						{
							continue;
						}
						TreeMap<Long, Item> oldItems = items.get(temp.getName());
						if (oldItems == null)
						{
							oldItems = new TreeMap<Long, Item>();
							items.put(temp.getName(), oldItems);
						}
						Item newItem = new Item(item.getItemId(), type, packagePrice, item.getCount(), item.getEnchantLevel(), temp.getName(), pl.getObjectId(), pl.getName(), pl.getLoc(), item.getObjectId(),
									item, true);
						long key = newItem.price * 100;
						while (key < newItem.price * 100 + 100 && oldItems.containsKey(key))
						{
							key++;
						}
						oldItems.put(key, newItem);
					}

					break;
				case Player.STORE_PRIVATE_BUY:
					items = info.bestBuyItems;
					tradeList = pl.getBuyList();

					for (TradeItem item : tradeList)
					{
						ItemTemplate temp = item.getItem();
						if (temp == null)
						{
							continue;
						}
						TreeMap<Long, Item> oldItems = items.get(temp.getName());
						if (oldItems == null)
						{
							oldItems = new TreeMap<Long, Item>();
							items.put(temp.getName(), oldItems);
						}
						Item newItem = new Item(item.getItemId(), type, item.getOwnersPrice(), item.getCount(), item.getEnchantLevel(), temp.getName(), pl.getObjectId(), pl.getName(), pl.getLoc(), itemObjId++,
									item, false);
						long key = newItem.price * 100;
						while (key < newItem.price * 100 + 100 && oldItems.containsKey(key))
						{
							key++;
						}
						oldItems.put(key, newItem);
					}

					break;
				case Player.STORE_PRIVATE_MANUFACTURE:
					items = info.bestCraftItems;
					List<ManufactureItem> createList = pl.getCreateList();
					if (createList == null)
					{
						continue;
					}

					for (ManufactureItem mitem : createList)
					{
						int recipeId = mitem.getRecipeId();
						Recipe recipe = RecipeHolder.getInstance().getRecipeByRecipeId(recipeId);
						if (recipe == null)
						{
							continue;
						}

						ItemTemplate temp = ItemHolder.getInstance().getTemplate(recipe.getItemId());
						if (temp == null)
						{
							continue;
						}
						TreeMap<Long, Item> oldItems = items.get(temp.getName());
						if (oldItems == null)
						{
							oldItems = new TreeMap<Long, Item>();
							items.put(temp.getName(), oldItems);
						}
						Item newItem = new Item(recipe.getItemId(), type, mitem.getCost(), recipe.getCount(), 0, temp.getName(), pl.getObjectId(), pl.getName(), pl.getLoc(), itemObjId++, null, false);
						long key = newItem.price * 100;
						while (key < newItem.price * 100 + 100 && oldItems.containsKey(key))
						{
							key++;
						}
						oldItems.put(key, newItem);
					}

					break;
				default:
					continue;
				}
			}
			_npcInfos.put(npc.getObjectId(), info);
		}
	}

	@SuppressWarnings("null")
	public void find(String[] var)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}

		if (var.length < 3 || var.length > 7)
		{
			show("Please enter from 1 up to 16 symbols.<br>[npc_%objectId%_Chat 0|<font color=\"FF9900\">Back</font>]", player, npc);
			return;
		}

		int type;
		int currentPage;
		int minEnchant = 0;
		String[] search = null;

		try
		{
			type = Integer.valueOf(var[0]);
			currentPage = Integer.valueOf(var[1]);
			search = new String[var.length - 2];
			String line;
			for (int i = 0; i < search.length; i++)
			{
				line = var[i + 2].trim().toLowerCase();
				search[i] = line;
				if (line.length() > 1 && line.startsWith("+"))
				{
					minEnchant = Integer.valueOf(line.substring(1));
				}
			}
		}
		catch (Exception e)
		{
			show("incorrect data", player, npc);
			return;
		}

		TreeMap<String, TreeMap<Long, Item>> allItems = getItems(type);
		if (allItems == null)
		{
			show("Error - with this type of objects found.", player, npc);
			return;
		}

		List<Item> items = new ArrayList<Item>();
		String line;
		TreeMap<Long, Item> itemMap;
		Item item;
		mainLoop:
		for (Entry<String, TreeMap<Long, Item>> entry : allItems.entrySet())
		{
			for (int i = 0; i < search.length; i++)
			{
				line = search[i];
				if (line.startsWith("+"))
				{
					continue;
				}
				if (entry.getKey().toLowerCase().indexOf(line) == -1)
				{
					continue mainLoop;
				}
			}

			itemMap = entry.getValue();
			item = null;
			for (Item itm : itemMap.values())
			{
				if (itm != null && itm.enchant >= minEnchant)
				{
					item = itm;
					break;
				}
			}

			if (item != null)
			{
				items.add(item);
			}
		}

		StringBuilder out = new StringBuilder(200);
		out.append("[npc_%objectId%_Chat 1");
		out.append(type);
		out.append("|Back]&nbsp;&nbsp;");

		int totalPages = items.size();
		totalPages = totalPages / MAX_ITEMS_PER_PAGE + (totalPages % MAX_ITEMS_PER_PAGE > 0 ? 1 : 0);
		totalPages = Math.max(1, totalPages);
		currentPage = Math.min(totalPages, Math.max(1, currentPage));

		if (totalPages > 1)
		{
			int page = Math.max(1, Math.min(totalPages - MAX_PAGES_PER_LIST + 1, currentPage - MAX_PAGES_PER_LIST / 2));

			if (page > 1)
			{
				findPageNum(out, type, 1, search, "1");
			}
			if (currentPage > 11)
			{
				findPageNum(out, type, currentPage - 10, search, String.valueOf(currentPage - 10));
			}
			if (currentPage > 1)
			{
				findPageNum(out, type, currentPage - 1, search, "<");
			}

			for (int count = 0; count < MAX_PAGES_PER_LIST && page <= totalPages; count++, page++)
			{
				if (page == currentPage)
				{
					out.append(page).append("&nbsp;");
				}
				else
				{
					findPageNum(out, type, page, search, String.valueOf(page));
				}
			}

			if (currentPage < totalPages)
			{
				findPageNum(out, type, currentPage + 1, search, ">");
			}
			if (currentPage < totalPages - 10)
			{
				findPageNum(out, type, currentPage + 10, search, String.valueOf(currentPage + 10));
			}
			if (page <= totalPages)
			{
				findPageNum(out, type, totalPages, search, String.valueOf(totalPages));
			}
		}

		out.append("<table width=100%>");

		if (items.size() > 0)
		{
			int count = 0;
			ListIterator<Item> iter = items.listIterator((currentPage - 1) * MAX_ITEMS_PER_PAGE);
			while (iter.hasNext() && count < MAX_ITEMS_PER_PAGE)
			{
				item = iter.next();
				ItemTemplate temp = item.item != null ? item.item.getItem() : ItemHolder.getInstance().getTemplate(item.itemId);
				if (temp == null)
				{
					continue;
				}

				out.append("<tr><td>");
				out.append(temp.getIcon32());
				out.append("</td><td><table width=100%><tr><td>[scripts_services.ItemBroker:listForItem ");
				out.append(type);
				out.append(" ");
				out.append(item.itemId);
				out.append(" ");
				out.append(minEnchant);
				out.append(" 0 0 1 ");
				out.append(currentPage);
				if (search != null)
				{
					for (int i = 0; i < search.length; i++)
					{
						out.append(" ");
						out.append(search[i]);
					}
				}
				out.append("|");
				out.append("<font color=\"LEVEL\">");
				out.append(temp.getName());
				out.append("</font>]");
				out.append("</td></tr>");
				out.append("</table></td></tr>");
				count++;
			}
		}
		else
		{
			out.append("<tr><td colspan=2>Nothing found.</td></tr>");
		}
		out.append("</table><br>&nbsp;");

		show(out.toString(), player, npc);
	}

	private void findPageNum(StringBuilder out, int type, int page, String[] search, String letter)
	{
		out.append("[scripts_services.ItemBroker:find ");
		out.append(type);
		out.append(" ");
		out.append(page);
		if (search != null)
		{
			for (int i = 0; i < search.length; i++)
			{
				out.append(" ");
				out.append(search[i]);
			}
		}
		out.append("|");
		out.append(letter);
		out.append("]&nbsp;");
	}
}