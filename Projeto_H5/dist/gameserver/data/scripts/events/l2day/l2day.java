package events.l2day;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import l2f.gameserver.model.reward.RewardData;

@SuppressWarnings("unused")
public class l2day extends LettersCollection
{
	// Награды
	private static int BSOE = 3958;
	private static int BSOR = 3959;
	private static int GUIDANCE = 3926;
	private static int WHISPER = 3927;
	private static int FOCUS = 3928;
	private static int ACUMEN = 3929;
	private static int HASTE = 3930;
	private static int AGILITY = 3931;
	private static int EMPOWER = 3932;
	private static int MIGHT = 3933;
	private static int WINDWALK = 3934;
	private static int SHIELD = 3935;

	private static int ENCH_WPN_D = 955;
	private static int ENCH_WPN_C = 951;
	private static int ENCH_WPN_B = 947;
	private static int ENCH_WPN_A = 729;

	private static int RABBIT_EARS = 8947;
	private static int FEATHERED_HAT = 8950;
	private static int FAIRY_ANTENNAE = 8949;
	private static int ARTISANS_GOOGLES = 8951;
	private static int LITTLE_ANGEL_WING = 8948;

	private static int RING_OF_ANT_QUIEEN = 6660;
	private static int EARRING_OF_ORFEN = 6661;
	private static int RING_OF_CORE = 6662;
	private static int FRINTEZZA_NECKLACE = 8191;

	static
	{
		_name = "l2day";
		_msgStarted = "scripts.events.l2day.AnnounceEventStarted";
		_msgEnded = "scripts.events.l2day.AnnounceEventStoped";

		EVENT_MANAGERS = new int[][]
		{
			{
				19541,
				145419,
				-3103,
				30419
			},
			{
				147485,
				-59049,
				-2980,
				9138
			},
			{
				109947,
				218176,
				-3543,
				63079
			},
			{
				-81363,
				151611,
				-3121,
				42910
			},
			{
				144741,
				28846,
				-2453,
				2059
			},
			{
				44192,
				-48481,
				-796,
				23331
			},
			{
				-13889,
				122999,
				-3109,
				40099
			},
			{
				116278,
				75498,
				-2713,
				12022
			},
			{
				82029,
				55936,
				-1519,
				58708
			},
			{
				147142,
				28555,
				-2261,
				59402
			},
			{
				82153,
				148390,
				-3466,
				57344
			},
		};

		_words.put("LineageII", new Integer[][]
		{
			{
				L,
				1
			},
			{
				I,
				1
			},
			{
				N,
				1
			},
			{
				E,
				2
			},
			{
				A,
				1
			},
			{
				G,
				1
			},
			{
				II,
				1
			}
		});
		_rewards.put("LineageII", new RewardData[]
		{
			// L2Day Scrolls
			new RewardData(GUIDANCE, 3, 3, 85000),
			new RewardData(WHISPER, 3, 3, 85000),
			new RewardData(FOCUS, 3, 3, 85000),
			new RewardData(ACUMEN, 3, 3, 85000),
			new RewardData(HASTE, 3, 3, 85000),
			new RewardData(AGILITY, 3, 3, 85000),
			new RewardData(EMPOWER, 3, 3, 85000),
			new RewardData(MIGHT, 3, 3, 85000),
			new RewardData(WINDWALK, 3, 3, 85000),
			new RewardData(SHIELD, 3, 3, 85000),
			// Other
			new RewardData(BSOE, 1, 1, 50000),
			new RewardData(BSOR, 1, 1, 50000),
			new RewardData(ENCH_WPN_C, 3, 3, 14000),
			new RewardData(ENCH_WPN_B, 2, 2, 7000),
			new RewardData(ENCH_WPN_A, 1, 1, 7000),
			new RewardData(RABBIT_EARS, 1, 1, 5000),
			new RewardData(FEATHERED_HAT, 1, 1, 5000),
			new RewardData(FAIRY_ANTENNAE, 1, 1, 5000),
			new RewardData(RING_OF_ANT_QUIEEN, 1, 1, 100),
			new RewardData(RING_OF_CORE, 1, 1, 100),
		});

		_words.put("Throne", new Integer[][]
		{
			{
				T,
				1
			},
			{
				H,
				1
			},
			{
				R,
				1
			},
			{
				O,
				1
			},
			{
				N,
				1
			},
			{
				E,
				1
			}
		});
		_rewards.put("Throne", new RewardData[]
		{
			// L2Day Scrolls
			new RewardData(GUIDANCE, 3, 3, 85000),
			new RewardData(WHISPER, 3, 3, 85000),
			new RewardData(FOCUS, 3, 3, 85000),
			new RewardData(ACUMEN, 3, 3, 85000),
			new RewardData(HASTE, 3, 3, 85000),
			new RewardData(AGILITY, 3, 3, 85000),
			new RewardData(EMPOWER, 3, 3, 85000),
			new RewardData(MIGHT, 3, 3, 85000),
			new RewardData(WINDWALK, 3, 3, 85000),
			new RewardData(SHIELD, 3, 3, 85000),
			// Other
			new RewardData(BSOE, 1, 1, 50000),
			new RewardData(BSOR, 1, 1, 50000),
			new RewardData(ENCH_WPN_D, 4, 4, 16000),
			new RewardData(ENCH_WPN_C, 3, 3, 11000),
			new RewardData(ENCH_WPN_B, 2, 2, 6000),
			new RewardData(ARTISANS_GOOGLES, 1, 1, 6000),
			new RewardData(LITTLE_ANGEL_WING, 1, 1, 5000),
			new RewardData(RING_OF_ANT_QUIEEN, 1, 1, 100),
			new RewardData(RING_OF_CORE, 1, 1, 100),
		});

		_words.put("NCSoft", new Integer[][]
		{
			{
				N,
				1
			},
			{
				C,
				1
			},
			{
				S,
				1
			},
			{
				O,
				1
			},
			{
				F,
				1
			},
			{
				T,
				1
			}
		});
		_rewards.put("NCSoft", new RewardData[]
		{
			// L2Day Scrolls
			new RewardData(GUIDANCE, 3, 3, 85000),
			new RewardData(WHISPER, 3, 3, 85000),
			new RewardData(FOCUS, 3, 3, 85000),
			new RewardData(ACUMEN, 3, 3, 85000),
			new RewardData(HASTE, 3, 3, 85000),
			new RewardData(AGILITY, 3, 3, 85000),
			new RewardData(EMPOWER, 3, 3, 85000),
			new RewardData(MIGHT, 3, 3, 85000),
			new RewardData(WINDWALK, 3, 3, 85000),
			new RewardData(SHIELD, 3, 3, 85000),
			// Other
			new RewardData(BSOE, 1, 1, 50000),
			new RewardData(BSOR, 1, 1, 50000),
			new RewardData(ENCH_WPN_D, 4, 4, 16000),
			new RewardData(ENCH_WPN_C, 3, 3, 11000),
			new RewardData(ENCH_WPN_B, 2, 2, 6000),
			new RewardData(ARTISANS_GOOGLES, 1, 1, 6000),
			new RewardData(LITTLE_ANGEL_WING, 1, 1, 5000),
			new RewardData(RING_OF_ANT_QUIEEN, 1, 1, 100),
			new RewardData(RING_OF_CORE, 1, 1, 100),
		});

		// дальше трогать не рекомендуется
		final int DROP_MULT = 3; // Множитель шанса дропа
		// Балансируем дроплист на базе используемых слов
		Map<Integer, Integer> temp = new HashMap<Integer, Integer>();
		for (Integer[][] ii : _words.values())
		{
			for (Integer[] i : ii)
			{
				Integer curr = temp.get(i[0]);
				if (curr == null)
				{
					temp.put(i[0], i[1]);
				}
				else
				{
					temp.put(i[0], curr + i[1]);
				}
			}
		}
		letters = new int[temp.size()][2];
		int i = 0;
		for (Entry<Integer, Integer> e : temp.entrySet())
		{
			letters[i++] = new int[]
			{
				e.getKey(),
				e.getValue() * DROP_MULT
			};
		}
	}
}