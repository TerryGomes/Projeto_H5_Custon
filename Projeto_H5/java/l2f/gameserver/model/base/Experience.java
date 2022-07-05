package l2f.gameserver.model.base;

import l2f.gameserver.Config;

public class Experience
{

	public final static long LEVEL[] =
	{
		-1L, // level 0 (unreachable)
		/* Lvl:1 */ 0L,
		/* Lvl:2 */ 68L,
		/* Lvl:3 */ 363L,
		/* Lvl:4 */ 1168L,
		/* Lvl:5 */ 2884L,
		/* Lvl:6 */ 6038L,
		/* Lvl:7 */ 11287L,
		/* Lvl:8 */ 19423L,
		/* Lvl:9 */ 31378L,
		/* Lvl:10 */ 48229L,
		/* Lvl:11 */ 71202L,
		/* Lvl:12 */ 101677L,
		/* Lvl:13 */ 141193L,
		/* Lvl:14 */ 191454L,
		/* Lvl:15 */ 254330L,
		/* Lvl:16 */ 331867L,
		/* Lvl:17 */ 426288L,
		/* Lvl:18 */ 540000L,
		/* Lvl:19 */ 675596L,
		/* Lvl:20 */ 835862L,
		/* Lvl:21 */ 1023784L,
		/* Lvl:22 */ 1242546L,
		/* Lvl:23 */ 1495543L,
		/* Lvl:24 */ 1786379L,
		/* Lvl:25 */ 2118876L,
		/* Lvl:26 */ 2497077L,
		/* Lvl:27 */ 2925250L,
		/* Lvl:28 */ 3407897L,
		/* Lvl:29 */ 3949754L,
		/* Lvl:30 */ 4555796L,
		/* Lvl:31 */ 5231246L,
		/* Lvl:32 */ 5981576L,
		/* Lvl:33 */ 6812513L,
		/* Lvl:34 */ 7730044L,
		/* Lvl:35 */ 8740422L,
		/* Lvl:36 */ 9850166L,
		/* Lvl:37 */ 11066072L,
		/* Lvl:38 */ 12395215L,
		/* Lvl:39 */ 13844951L,
		/* Lvl:40 */ 15422929L,
		/* Lvl:41 */ 17137087L,
		/* Lvl:42 */ 18995665L,
		/* Lvl:43 */ 21007203L,
		/* Lvl:44 */ 23180550L,
		/* Lvl:45 */ 25524868L,
		/* Lvl:46 */ 28049635L,
		/* Lvl:47 */ 30764654L,
		/* Lvl:48 */ 33680052L,
		/* Lvl:49 */ 36806289L,
		/* Lvl:50 */ 40154162L,
		/* Lvl:51 */ 45525133L,
		/* Lvl:52 */ 51262490L,
		/* Lvl:53 */ 57383988L,
		/* Lvl:54 */ 63907911L,
		/* Lvl:55 */ 70853089L,
		/* Lvl:56 */ 80700831L,
		/* Lvl:57 */ 91162654L,
		/* Lvl:58 */ 102265881L,
		/* Lvl:59 */ 114038596L,
		/* Lvl:60 */ 126509653L,
		/* Lvl:61 */ 146308200L,
		/* Lvl:62 */ 167244337L,
		/* Lvl:63 */ 189364894L,
		/* Lvl:64 */ 212717908L,
		/* Lvl:65 */ 237352644L,
		/* Lvl:66 */ 271975263L,
		/* Lvl:67 */ 308443198L,
		/* Lvl:68 */ 346827154L,
		/* Lvl:69 */ 387199547L,
		/* Lvl:70 */ 429634523L,
		/* Lvl:71 */ 474207979L,
		/* Lvl:72 */ 532694979L,
		/* Lvl:73 */ 606322775L,
		/* Lvl:74 */ 696381369L,
		/* Lvl:75 */ 804225364L,
		/* Lvl:76 */ 931275828L,
		/* Lvl:77 */ 1151275834L,
		/* Lvl:78 */ 1511275834L,
		/* Lvl:79 */ 2044287599L,
		/* Lvl:80 */ 3075966164L,
		/* Lvl:81 */ 4295351949L,
		/* Lvl:82 */ 5766985062L,
		/* Lvl:83 */ 7793077345L,
		/* Lvl:84 */ 10235368963L,
		/* Lvl:85 */ 13180481103L,
		/* Lvl:86 */ 25314105600L,
		/* Lvl:87 */ 32211728640L,
		/* Lvl:88 */ 40488876288L,
		/* Lvl:89 */ 50421453466L,
		/* Lvl:90 */ 63424099953L,
		/* Lvl:91 */ 79027275737L,
		/* Lvl:92 */ 97751086678L,
		/* Lvl:93 */ 121155850355L,
		/* Lvl:94 */ 149241566767L,
		/* Lvl:95 */ 182944426462L,
		/* Lvl:96 */ 225005595360L,
		/* Lvl:97 */ 275478998038L,
		/* Lvl:98 */ 336047081252L,
		/* Lvl:99 */ 408728781109L,
		/* Lvl:100 */ 418728781109L,
		/* Lvl:101 */ 428728781109L,
		/* Lvl:102 */ 438728781109L,
		/* Lvl:103 */ 448728781109L,
		/* Lvl:104 */ 458728781109L,
		/* Lvl:105 */ 468728781109L,
		/* Lvl:106 */ 478728781109L,
		/* Lvl:107 */ 488728781109L,
		/* Lvl:108 */ 498728781109L,
		/* Lvl:109 */ 508728781109L,
		/* Lvl:110 */ 518728781109L,
		/* Lvl:111 */ 558728781109L,
		/* Lvl:112 */ 608728781109L,
		/* Lvl:113 */ 658728781109L,
		/* Lvl:114 */ 708728781109L,
		/* Lvl:115 */ 758728781109L,
		/* Lvl:116 */ 808728781109L,
		/* Lvl:117 */ 958728781109L,
		/* Lvl:118 */ 1008728781109L,
		/* Lvl:119 */ 1058728781109L,
		/* Lvl:120 */ 1108728781109L,
		/* Lvl:131 */ 1158728781109L,
		/* Lvl:132 */ 1208728781109L,
		/* Lvl:133 */ 1258728781109L,
		/* Lvl:134 */ 1308728781109L,
		/* Lvl:135 */ 1408728781109L,
		/* Lvl:136 */ 1458728781109L,
		/* Lvl:137 */ 1508728781109L,
		/* Lvl:138 */ 1558728781109L,
		/* Lvl:139 */ 1608728781109L,
		/* Lvl:140 */ 1658728781109L,
		/* Lvl:141 */ 1708728781109L,
		/* Lvl:142 */ 1758728781109L,
		/* Lvl:143 */ 1808728781109L,
		/* Lvl:144 */ 1858728781109L,
		/* Lvl:145 */ 1908728781109L,
		/* Lvl:150 */ 1958728781109L,

		/* Lvl:151 */ 1958728781109L
	};

	/**
	 * Return PenaltyModifier (can use in all cases)
	 *
	 * @param count	- how many times <percents> will be substructed
	 * @param percents - percents to substruct
	 *
	 * @author Styx
	 */

	/*
	 * This is for fine view only ;)
	 * public final static double penaltyModifier(int count, int percents)
	 * {
	 * int allPercents = 100;
	 * int allSubstructedPercents = count * percents;
	 * int penaltyInPercents = allPercents - allSubstructedPercents;
	 * double penalty = penaltyInPercents / 100.0;
	 * return penalty;
	 * }
	 */
	public static double penaltyModifier(long count, double percents)
	{
		return Math.max(1. - count * percents / 100, 0);
	}

	/**
	 * Максимальный достижимый уровень
	 */
	public static int getMaxLevel()
	{
		return Config.ALT_MAX_LEVEL;
	}

	/**
	 * Максимальный уровень для саба
	 */
	public static int getMaxSubLevel()
	{
		return Config.ALT_MAX_SUB_LEVEL;
	}

	public static int getLevel(long thisExp)
	{
		int level = 0;
		for (int i = 0; i < LEVEL.length; i++)
		{
			long exp = LEVEL[i];
			if (thisExp >= exp)
			{
				level = i;
			}
		}
		return level;
	}

	public static long getExpForLevel(int lvl)
	{
		if (lvl >= Experience.LEVEL.length)
		{
			return 0;
		}
		return Experience.LEVEL[lvl];
	}

	public static double getExpPercent(int level, long exp)
	{
		return (exp - getExpForLevel(level)) / ((getExpForLevel(level + 1) - getExpForLevel(level)) / 100.0D) * 0.01D;
	}
}