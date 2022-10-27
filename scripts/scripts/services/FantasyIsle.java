package services;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.GameTimeController;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.PlaySound;
import l2mv.gameserver.network.serverpackets.SocialAction;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;

public class FantasyIsle extends Functions implements ScriptFile
{
	private static ScheduledFuture<?> _startTask;
	private static boolean _isStarted;

	private static int MC = 32433;
	private static int singer1 = 32431;
	private static int singer2 = 32432;
	private static int circus1 = 32442;
	private static int circus2 = 32443;
	private static int circus3 = 32444;
	private static int circus4 = 32445;
	private static int circus5 = 32446;
	private static int individual1 = 32439;
	private static int individual2 = 32440;
	private static int individual3 = 32441;
	private static int showstuff1 = 32424;
	private static int showstuff2 = 32425;
	private static int showstuff3 = 32426;
	private static int showstuff4 = 32427;
	private static int showstuff5 = 32428;

	private static Map<String, Walk> WALKS = new HashMap<String, Walk>();
	static
	{
		WALKS.put("npc1_1", new Walk(-56546, -56384, -2008, "npc1_2", 1200));
		WALKS.put("npc1_2", new Walk(-56597, -56384, -2008, "npc1_3", 1200));
		WALKS.put("npc1_3", new Walk(-56596, -56428, -2008, "npc1_4", 1200));
		WALKS.put("npc1_4", new Walk(-56593, -56474, -2008, "npc1_5", 1000));
		WALKS.put("npc1_5", new Walk(-56542, -56474, -2008, "npc1_6", 1000));
		WALKS.put("npc1_6", new Walk(-56493, -56473, -2008, "npc1_7", 2000));
		WALKS.put("npc1_7", new Walk(-56495, -56425, -2008, "npc1_1", 4000));
		WALKS.put("npc2_1", new Walk(-56550, -56291, -2008, "npc2_2", 1200));
		WALKS.put("npc2_2", new Walk(-56601, -56293, -2008, "npc2_3", 1200));
		WALKS.put("npc2_3", new Walk(-56603, -56247, -2008, "npc2_4", 1200));
		WALKS.put("npc2_4", new Walk(-56605, -56203, -2008, "npc2_5", 1000));
		WALKS.put("npc2_5", new Walk(-56553, -56202, -2008, "npc2_6", 1100));
		WALKS.put("npc2_6", new Walk(-56504, -56200, -2008, "npc2_7", 2000));
		WALKS.put("npc2_7", new Walk(-56503, -56243, -2008, "npc2_1", 4000));
		WALKS.put("npc3_1", new Walk(-56500, -56290, -2008, "npc3_2", 1200));
		WALKS.put("npc3_2", new Walk(-56551, -56313, -2008, "npc3_3", 1200));
		WALKS.put("npc3_3", new Walk(-56601, -56293, -2008, "npc3_4", 1200));
		WALKS.put("npc3_4", new Walk(-56651, -56294, -2008, "npc3_5", 1200));
		WALKS.put("npc3_5", new Walk(-56653, -56250, -2008, "npc3_6", 1200));
		WALKS.put("npc3_6", new Walk(-56654, -56204, -2008, "npc3_7", 1200));
		WALKS.put("npc3_7", new Walk(-56605, -56203, -2008, "npc3_8", 1200));
		WALKS.put("npc3_8", new Walk(-56554, -56202, -2008, "npc3_9", 1200));
		WALKS.put("npc3_9", new Walk(-56503, -56200, -2008, "npc3_10", 1200));
		WALKS.put("npc3_10", new Walk(-56502, -56244, -2008, "npc3_1", 900));
		WALKS.put("npc4_1", new Walk(-56495, -56381, -2008, "npc4_2", 1200));
		WALKS.put("npc4_2", new Walk(-56548, -56383, -2008, "npc4_3", 1200));
		WALKS.put("npc4_3", new Walk(-56597, -56383, -2008, "npc4_4", 1200));
		WALKS.put("npc4_4", new Walk(-56643, -56385, -2008, "npc4_5", 1200));
		WALKS.put("npc4_5", new Walk(-56639, -56436, -2008, "npc4_6", 1200));
		WALKS.put("npc4_6", new Walk(-56639, -56473, -2008, "npc4_7", 1200));
		WALKS.put("npc4_7", new Walk(-56589, -56473, -2008, "npc4_8", 1200));
		WALKS.put("npc4_8", new Walk(-56541, -56473, -2008, "npc4_9", 1200));
		WALKS.put("npc4_9", new Walk(-56496, -56473, -2008, "npc4_10", 1200));
		WALKS.put("npc4_10", new Walk(-56496, -56429, -2008, "npc4_1", 900));
		WALKS.put("npc5_1", new Walk(-56549, -56335, -2008, "npc5_2", 1000));
		WALKS.put("npc5_2", new Walk(-56599, -56337, -2008, "npc5_3", 2000));
		WALKS.put("npc5_3", new Walk(-56649, -56341, -2008, "npc5_4", 26000));
		WALKS.put("npc5_4", new Walk(-56600, -56341, -2008, "npc5_5", 1000));
		WALKS.put("npc5_5", new Walk(-56553, -56341, -2008, "npc5_6", 1000));
		WALKS.put("npc5_6", new Walk(-56508, -56331, -2008, "npc5_2", 8000));
		WALKS.put("npc6_1", new Walk(-56595, -56428, -2008, "npc6_2", 1000));
		WALKS.put("npc6_2", new Walk(-56596, -56383, -2008, "npc6_3", 1000));
		WALKS.put("npc6_3", new Walk(-56648, -56384, -2008, "npc6_4", 1000));
		WALKS.put("npc6_4", new Walk(-56645, -56429, -2008, "npc6_5", 1000));
		WALKS.put("npc6_5", new Walk(-56644, -56475, -2008, "npc6_6", 1000));
		WALKS.put("npc6_6", new Walk(-56595, -56473, -2008, "npc6_7", 1000));
		WALKS.put("npc6_7", new Walk(-56542, -56473, -2008, "npc6_8", 1000));
		WALKS.put("npc6_8", new Walk(-56492, -56472, -2008, "npc6_9", 1200));
		WALKS.put("npc6_9", new Walk(-56495, -56426, -2008, "npc6_10", 2000));
		WALKS.put("npc6_10", new Walk(-56540, -56426, -2008, "npc6_1", 3000));
		WALKS.put("npc7_1", new Walk(-56603, -56249, -2008, "npc7_2", 1000));
		WALKS.put("npc7_2", new Walk(-56601, -56294, -2008, "npc7_3", 1000));
		WALKS.put("npc7_3", new Walk(-56651, -56295, -2008, "npc7_4", 1000));
		WALKS.put("npc7_4", new Walk(-56653, -56248, -2008, "npc7_5", 1000));
		WALKS.put("npc7_5", new Walk(-56605, -56203, -2008, "npc7_6", 1000));
		WALKS.put("npc7_6", new Walk(-56554, -56202, -2008, "npc7_7", 1000));
		WALKS.put("npc7_7", new Walk(-56504, -56201, -2008, "npc7_8", 1000));
		WALKS.put("npc7_8", new Walk(-56502, -56247, -2008, "npc7_9", 1200));
		WALKS.put("npc7_9", new Walk(-56549, -56248, -2008, "npc7_10", 2000));
		WALKS.put("npc7_10", new Walk(-56549, -56248, -2008, "npc7_1", 3000));
		WALKS.put("npc8_1", new Walk(-56493, -56426, -2008, "npc8_2", 1000));
		WALKS.put("npc8_2", new Walk(-56497, -56381, -2008, "npc8_3", 1200));
		WALKS.put("npc8_3", new Walk(-56544, -56381, -2008, "npc8_4", 1200));
		WALKS.put("npc8_4", new Walk(-56596, -56383, -2008, "npc8_5", 1200));
		WALKS.put("npc8_5", new Walk(-56594, -56428, -2008, "npc8_6", 900));
		WALKS.put("npc8_6", new Walk(-56645, -56429, -2008, "npc8_7", 1200));
		WALKS.put("npc8_7", new Walk(-56647, -56384, -2008, "npc8_8", 1200));
		WALKS.put("npc8_8", new Walk(-56649, -56362, -2008, "npc8_9", 9200));
		WALKS.put("npc8_9", new Walk(-56654, -56429, -2008, "npc8_10", 1200));
		WALKS.put("npc8_10", new Walk(-56644, -56474, -2008, "npc8_11", 900));
		WALKS.put("npc8_11", new Walk(-56593, -56473, -2008, "npc8_12", 1100));
		WALKS.put("npc8_12", new Walk(-56543, -56472, -2008, "npc8_13", 1200));
		WALKS.put("npc8_13", new Walk(-56491, -56471, -2008, "npc8_1", 1200));
		WALKS.put("npc9_1", new Walk(-56505, -56246, -2008, "npc9_2", 1000));
		WALKS.put("npc9_2", new Walk(-56504, -56291, -2008, "npc9_3", 1200));
		WALKS.put("npc9_3", new Walk(-56550, -56291, -2008, "npc9_4", 1200));
		WALKS.put("npc9_4", new Walk(-56600, -56292, -2008, "npc9_5", 1200));
		WALKS.put("npc9_5", new Walk(-56603, -56248, -2008, "npc9_6", 900));
		WALKS.put("npc9_6", new Walk(-56653, -56249, -2008, "npc9_7", 1200));
		WALKS.put("npc9_7", new Walk(-56651, -56294, -2008, "npc9_8", 1200));
		WALKS.put("npc9_8", new Walk(-56650, -56316, -2008, "npc9_9", 9200));
		WALKS.put("npc9_9", new Walk(-56660, -56250, -2008, "npc9_10", 1200));
		WALKS.put("npc9_10", new Walk(-56656, -56205, -2008, "npc9_11", 900));
		WALKS.put("npc9_11", new Walk(-56606, -56204, -2008, "npc9_12", 1100));
		WALKS.put("npc9_12", new Walk(-56554, -56203, -2008, "npc9_13", 1200));
		WALKS.put("npc9_13", new Walk(-56506, -56203, -2008, "npc9_1", 1200));
		WALKS.put("24", new Walk(-56730, -56340, -2008, "25", 1800));
		WALKS.put("27", new Walk(-56702, -56340, -2008, "29", 1800));
	}

	private static String[] TEXT = new String[]
	{
		"How come people are not here... We are about to start the show.. Hmm",
		"Ugh, I have butterflies in my stomach.. The show starts soon...",
		"Thank you all for coming here tonight.",
		"It is an honor to have the special show today.",
		"Our Fantasy Isle is fully committed to your happiness.",
		"Now I'd like to introduce the most beautiful singer in Aden. Please welcome Leyla Mira!",
		"Here she comes!",
		"Thank you very much, Leyla. Next is",
		"It was very difficult to invite this first group that just came back from their world tour. Let's welcome the Fantasy Isle Circus!",
		"Come on ~ everyone",
		"Did you like it? That was so amazing.",
		"Now we also invited individuals with special talents.",
		"Let's welcome the first person here!",
		";;;;;;Oh",
		"Okay, now here comes the next person. Come on up please.",
		"Oh, it looks like something great is going to happen, right?",
		"Oh, my ;;;;",
		"That's g- .. great. Now, here comes the last person.",
		"Now this is the end of today's show.",
		"How was it? I am not sure if you really enjoyed it.",
		"Please remember that Fantasy Isle is always planning a lot of great shows for you.",
		"Well, I wish I could continue all night long, but this is it for today. Thank you."
	};

	private static Map<String, Talk> TALKS = new HashMap<String, Talk>();
	static
	{
		TALKS.put("1", new Talk(TEXT[1], "2", 1000));
		TALKS.put("2", new Talk(TEXT[2], "3", 6000));
		TALKS.put("3", new Talk(TEXT[3], "4", 4000));
		TALKS.put("4", new Talk(TEXT[4], "5", 5000));
		TALKS.put("5", new Talk(TEXT[5], "6", 3000));
		TALKS.put("8", new Talk(TEXT[8], "9", 5000));
		TALKS.put("9", new Talk(TEXT[9], "10", 5000));
		TALKS.put("12", new Talk(TEXT[11], "13", 5000));
		TALKS.put("13", new Talk(TEXT[12], "14", 5000));
		TALKS.put("15", new Talk(TEXT[13], "16", 5000));
		TALKS.put("16", new Talk(TEXT[14], "17", 5000));
		TALKS.put("18", new Talk(TEXT[16], "19", 5000));
		TALKS.put("19", new Talk(TEXT[17], "20", 5000));
		TALKS.put("21", new Talk(TEXT[18], "22", 5000));
		TALKS.put("22", new Talk(TEXT[19], "23", 400));
		TALKS.put("25", new Talk(TEXT[20], "26", 5000));
		TALKS.put("26", new Talk(TEXT[21], "27", 5400));
	}

	@Override
	public void onLoad()
	{
		_startTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new StartTask(), 60000, 60000);
	}

	@Override
	public void onReload()
	{
		if (_startTask != null)
		{
			_startTask.cancel(false);
			_startTask = null;
		}
		_isStarted = false;
	}

	@Override
	public void onShutdown()
	{
	}

	public static boolean isStarted()
	{
		return _isStarted;
	}

	public class StartTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if (!isStarted())
			{
				int gameTime = GameTimeController.getInstance().getGameTime();
				int h = gameTime / 60 % 24;
				int m = gameTime % 60;
				if (h == 20 && m >= 27 && m <= 33)
				{
					_isStarted = true;
					start("Start");
				}
			}
		}
	}

	private static NpcInstance addSpawn(int npcId, int x, int y, int z, int heading)
	{
		return Functions.spawn(new Location(x, y, z, heading), npcId);
	}

	private static void startQuestTimer(String event, int time, NpcInstance temp_npc)
	{
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("npc", temp_npc.getRef());
		executeTask("services.FantasyIsle", "start", new Object[]
		{
			event
		}, variables, time);
	}

	public void manualStart()
	{
		if (!isStarted())
		{
			_isStarted = true;
			start("Start");
		}
	}

	public void start(String event)
	{
		NpcInstance temp_npc = getNpc();
		if (event.equals("Start"))
		{
			NpcInstance mc = addSpawn(MC, -56698, -56430, -2008, 32768);
			Functions.npcSay(mc, TEXT[0]);
			startQuestTimer("1", 30000, mc);
		}
		else if (TALKS.containsKey(event) && temp_npc != null)
		{
			String text = TALKS.get(event).text;
			String nextEvent = TALKS.get(event).event;
			int time = TALKS.get(event).time;
			Functions.npcSay(temp_npc, text);
			startQuestTimer(nextEvent, time, temp_npc);
		}
		else if (WALKS.containsKey(event) && temp_npc != null)
		{
			int x = WALKS.get(event).x;
			int y = WALKS.get(event).y;
			int z = WALKS.get(event).z;
			String nextEvent = WALKS.get(event).event;
			int time = WALKS.get(event).time;
			temp_npc.moveToLocation(new Location(x, y, z), 0, true);
			startQuestTimer(nextEvent, time, temp_npc);
		}
		else if (event.equals("6") && temp_npc != null)
		{
			Functions.npcSay(temp_npc, TEXT[6]);
			temp_npc.moveToLocation(new Location(-56511, -56647, -2008, 36863), 0, true);
			temp_npc.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "NS22_F", 0, 0, 0, 0, 0));
			NpcInstance elf = addSpawn(singer1, -56344, -56328, -2008, 32768);
			elf.moveToLocation(new Location(-56657, -56338, -2008, 33102), 0, true);
			NpcInstance elf1 = addSpawn(singer2, -56552, -56245, -2008, 36863);
			NpcInstance elf2 = addSpawn(singer2, -56546, -56426, -2008, 28672);
			NpcInstance elf3 = addSpawn(singer2, -56570, -56473, -2008, 28672);
			NpcInstance elf4 = addSpawn(singer2, -56594, -56516, -2008, 28672);
			NpcInstance elf5 = addSpawn(singer2, -56580, -56203, -2008, 36863);
			NpcInstance elf6 = addSpawn(singer2, -56606, -56157, -2008, 36863);

			startQuestTimer("social1", 6000, elf);
			startQuestTimer("social1", 6000, elf1);
			startQuestTimer("social1", 6000, elf2);
			startQuestTimer("social1", 6000, elf3);
			startQuestTimer("social1", 6000, elf4);
			startQuestTimer("social1", 6000, elf5);
			startQuestTimer("social1", 6000, elf6);

			startQuestTimer("7", 215000, temp_npc);
			startQuestTimer("7", 215000, elf);
			startQuestTimer("7", 215000, elf1);
			startQuestTimer("7", 215000, elf2);
			startQuestTimer("7", 215000, elf3);
			startQuestTimer("7", 215000, elf4);
			startQuestTimer("7", 215000, elf5);
			startQuestTimer("7", 215000, elf6);
		}
		else if (event.equals("7") && temp_npc != null)
		{
			if (temp_npc.getNpcId() == MC)
			{
				Functions.npcSay(temp_npc, TEXT[7]);
				temp_npc.moveToLocation(new Location(-56698, -56430, -2008, 32768), 0, true);
				startQuestTimer("8", 12000, temp_npc);
			}
			else
			{
				// cancelQuestTimer("social1", npc, null);
				temp_npc.moveToLocation(new Location(-56594, -56064, -2008), 0, true);
				startQuestTimer("clean_npc", 9000, temp_npc);
			}
		}
		else if (event.equals("10") && temp_npc != null)
		{
			temp_npc.moveToLocation(new Location(-56483, -56665, -2034), 0, true);
			NpcInstance npc1 = addSpawn(circus1, -56495, -56375, -2008, 32768);
			NpcInstance npc2 = addSpawn(circus1, -56491, -56289, -2008, 32768);
			NpcInstance npc3 = addSpawn(circus2, -56502, -56246, -2008, 32768);
			NpcInstance npc4 = addSpawn(circus2, -56496, -56429, -2008, 32768);
			NpcInstance npc5 = addSpawn(circus3, -56505, -56334, -2008, 32768);
			NpcInstance npc6 = addSpawn(circus4, -56545, -56427, -2008, 32768);
			NpcInstance npc7 = addSpawn(circus4, -56552, -56248, -2008, 32768);
			NpcInstance npc8 = addSpawn(circus5, -56493, -56473, -2008, 32768);
			NpcInstance npc9 = addSpawn(circus5, -56504, -56201, -2008, 32768);
			temp_npc.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "TP05_F", 0, 0, 0, 0, 0));
			startQuestTimer("npc1_1", 3000, npc1);
			startQuestTimer("npc2_1", 3000, npc2);
			startQuestTimer("npc3_1", 3000, npc3);
			startQuestTimer("npc4_1", 3000, npc4);
			startQuestTimer("npc5_1", 3500, npc5);
			startQuestTimer("npc6_1", 4000, npc6);
			startQuestTimer("npc7_1", 4000, npc7);
			startQuestTimer("npc8_1", 3000, npc8);
			startQuestTimer("npc9_1", 3000, npc9);
			startQuestTimer("11", 100000, temp_npc);
			startQuestTimer("11", 100000, npc1);
			startQuestTimer("11", 100000, npc2);
			startQuestTimer("11", 100000, npc3);
			startQuestTimer("11", 100000, npc4);
			startQuestTimer("11", 100000, npc5);
			startQuestTimer("11", 100000, npc6);
			startQuestTimer("11", 100000, npc7);
			startQuestTimer("11", 100000, npc8);
			startQuestTimer("11", 100000, npc9);
		}
		else if (event.equals("11") && temp_npc != null)
		{
			if (temp_npc.getNpcId() == MC)
			{
				Functions.npcSay(temp_npc, TEXT[10]);
				temp_npc.moveToLocation(new Location(-56698, -56430, -2008), 0, true);
				startQuestTimer("12", 5000, temp_npc);
			}
			else
			{
				temp_npc.moveToLocation(new Location(-56343, -56330, -2008), 0, true);
				startQuestTimer("clean_npc", 1000, temp_npc);
			}
		}
		else if (event.equals("14") && temp_npc != null)
		{
			NpcInstance npc1 = addSpawn(individual1, -56700, -56385, -2008, 32768);
			startQuestTimer("social1", 2000, npc1);
			startQuestTimer("clean_npc", 49000, npc1);
			startQuestTimer("15", 7000, temp_npc);
		}
		else if (event.equals("17") && temp_npc != null)
		{
			Functions.npcSay(temp_npc, TEXT[15]);
			NpcInstance npc1 = addSpawn(individual2, -56700, -56340, -2008, 32768);
			startQuestTimer("social1", 2000, npc1);
			startQuestTimer("clean_npc", 32000, npc1);
			startQuestTimer("18", 9000, temp_npc);
		}
		else if (event.equals("20") && temp_npc != null)
		{
			NpcInstance npc1 = addSpawn(individual3, -56703, -56296, -2008, 32768);
			startQuestTimer("social1", 2000, npc1);
			startQuestTimer("clean_npc", 13000, npc1);
			startQuestTimer("21", 8000, temp_npc);
		}
		else if (event.equals("23") && temp_npc != null)
		{
			temp_npc.moveToLocation(new Location(-56702, -56340, -2008), 0, true);
			startQuestTimer("24", 2800, temp_npc);
			NpcInstance npc1 = addSpawn(showstuff1, -56672, -56406, -2000, 32768);
			NpcInstance npc2 = addSpawn(showstuff2, -56648, -56368, -2000, 32768);
			NpcInstance npc3 = addSpawn(showstuff3, -56608, -56338, -2000, 32768);
			NpcInstance npc4 = addSpawn(showstuff4, -56652, -56307, -2000, 32768);
			NpcInstance npc5 = addSpawn(showstuff5, -56672, -56272, -2000, 32768);

			startQuestTimer("social1", 5500, npc1);
			startQuestTimer("social1_1", 12500, npc1);
			startQuestTimer("28", 19700, npc1);

			startQuestTimer("social1", 5500, npc2);
			startQuestTimer("social1_1", 12500, npc2);
			startQuestTimer("28", 19700, npc2);

			startQuestTimer("social1", 5500, npc3);
			startQuestTimer("social1_1", 12500, npc3);
			startQuestTimer("28", 19700, npc3);

			startQuestTimer("social1", 5500, npc4);
			startQuestTimer("social1_1", 12500, npc4);
			startQuestTimer("28", 19700, npc4);

			startQuestTimer("social1", 5500, npc5);
			startQuestTimer("social1_1", 12500, npc5);
			startQuestTimer("28", 19700, npc5);
		}
		else if (event.equals("28") && temp_npc != null)
		{
			Functions.npcSay(temp_npc, "We love you.");
			startQuestTimer("social1", 1, temp_npc);
			startQuestTimer("clean_npc", 1200, temp_npc);
		}
		else if (event.equals("29") && temp_npc != null)
		{
			temp_npc.moveToLocation(new Location(-56730, -56340, -2008), 0, true);
			startQuestTimer("clean_npc", 4100, temp_npc);
			_isStarted = false;
		}
		else if ((event.equals("social1") || event.equals("social1_1")) && temp_npc != null)
		{
			temp_npc.broadcastPacket(new SocialAction(temp_npc.getObjectId(), 1));
		}
		else if (event.equals("clean_npc") && temp_npc != null)
		{
			temp_npc.deleteMe();
		}
	}

	public static class Walk
	{
		public int x, y, z, time;
		public String event;

		public Walk(int x, int y, int z, String event, int time)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.event = event;
			this.time = time;
		}
	}

	public static class Talk
	{
		public int time;
		public String text, event;

		public Talk(String text, String event, int time)
		{
			this.text = text;
			this.event = event;
			this.time = time;
		}
	}
}