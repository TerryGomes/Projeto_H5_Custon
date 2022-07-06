package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * * Автор: Gnacik
 * * Доделал: Bonux
 */
public class _463_IMustBeaGenius extends Quest implements ScriptFile
{
	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	private static final int GUTENHAGEN = 32069;
	private static final int CORPSE_LOG = 15510;
	private static final int COLLECTION = 15511;
	private static final int[] MOBS =
	{
		22801,
		22802,
		22804,
		22805,
		22807,
		22808,
		22809,
		22810,
		22811,
		22812
	};

	public _463_IMustBeaGenius()
	{
		super(false);

		addStartNpc(GUTENHAGEN);
		addTalkId(GUTENHAGEN);
		addQuestItem(CORPSE_LOG, COLLECTION);
		addKillId(MOBS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();
		if (npc.getNpcId() == GUTENHAGEN)
		{
			if (event.equalsIgnoreCase("collecter_gutenhagen_q0463_05.htm"))
			{
				st.playSound(SOUND_ACCEPT);
				st.setState(STARTED);
				st.setCond(1);
				// Generate random daily number for player
				int _number = Rnd.get(500, 600);
				st.set("number", String.valueOf(_number));
				// Set drop for mobs
				for (int _mob : MOBS)
				{
					int _rand = Rnd.get(-2, 4);
					if (_rand == 0)
					{
						_rand = 5;
					}
					st.set(String.valueOf(_mob), String.valueOf(_rand));
				}
				// One with higher chance
				st.set(String.valueOf(MOBS[Rnd.get(0, MOBS.length - 1)]), String.valueOf(Rnd.get(1, 100)));
				htmltext = HtmCache.getInstance().getNotNull("quests/_463_IMustBeaGenius/" + event, st.getPlayer());
				htmltext = htmltext.replace("%num%", String.valueOf(_number));
			}
			else if (event.equalsIgnoreCase("collecter_gutenhagen_q0463_07.htm"))
			{
				htmltext = HtmCache.getInstance().getNotNull("quests/_463_IMustBeaGenius/" + event, st.getPlayer());
				htmltext = htmltext.replace("%num%", st.get("number"));
			}
			else if (event.equalsIgnoreCase("reward"))
			{
				int diff = st.getInt("number") - 500;
				if (diff == 0)
				{
					st.addExpAndSp(198725, 15892);
					htmltext = "collecter_gutenhagen_q0463_09.htm";
				}
				else if (diff >= 1 && diff < 5)
				{
					st.addExpAndSp(278216, 22249);
					htmltext = "collecter_gutenhagen_q0463_10.htm";
				}
				else if (diff >= 5 && diff < 10)
				{
					st.addExpAndSp(317961, 25427);
					htmltext = "collecter_gutenhagen_q0463_11.htm";
				}
				else if (diff >= 10 && diff < 25)
				{
					st.addExpAndSp(357706, 28606);
					htmltext = "collecter_gutenhagen_q0463_11.htm";
				}
				else if (diff >= 25 && diff < 40)
				{
					st.addExpAndSp(397451, 31784);
					htmltext = "collecter_gutenhagen_q0463_12.htm";
				}
				else if (diff >= 40 && diff < 60)
				{
					st.addExpAndSp(596176, 47677);
					htmltext = "collecter_gutenhagen_q0463_13.htm";
				}
				else if (diff >= 60 && diff < 72)
				{
					st.addExpAndSp(715411, 57212);
					htmltext = "collecter_gutenhagen_q0463_14.htm";
				}
				else if (diff >= 72 && diff < 81)
				{
					st.addExpAndSp(794901, 63569);
					htmltext = "collecter_gutenhagen_q0463_14.htm";
				}
				else if (diff >= 81 && diff < 89)
				{
					st.addExpAndSp(914137, 73104);
					htmltext = "collecter_gutenhagen_q0463_15.htm";
				}
				else
				{
					st.addExpAndSp(1192352, 95353);
					htmltext = "collecter_gutenhagen_q0463_15.htm";
				}
				st.unset("cond");
				st.unset("number");
				for (int _mob : MOBS)
				{
					st.unset(String.valueOf(_mob));
				}
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(this);
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		Player player = st.getPlayer();
		if (npc.getNpcId() == GUTENHAGEN)
		{
			switch (st.getState())
			{
			case CREATED:
				if (player.getLevel() >= 70)
				{
					if (st.isNowAvailable())
					{
						htmltext = "collecter_gutenhagen_q0463_01.htm";
					}
					else
					{
						htmltext = "collecter_gutenhagen_q0463_03.htm";
					}
				}
				else
				{
					htmltext = "collecter_gutenhagen_q0463_02.htm";
				}
				break;
			case STARTED:
				if (st.getCond() == 1)
				{
					htmltext = "collecter_gutenhagen_q0463_06.htm";
				}
				else if (st.getCond() == 2)
				{
					if (st.getQuestItemsCount(COLLECTION) > 0)
					{
						st.takeItems(COLLECTION, -1);
						htmltext = "collecter_gutenhagen_q0463_08.htm";
					}
					else
					{
						htmltext = "collecter_gutenhagen_q0463_08a.htm";
					}
				}
				break;
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getState() == STARTED && st.getCond() == 1)
		{
			int _day_number = st.getInt("number");
			int _number = st.getInt(String.valueOf(npc.getNpcId()));
			if (_number > 0)
			{
				st.giveItems(CORPSE_LOG, _number);
				st.playSound(SOUND_ITEMGET);
				Functions.npcSay(npc, NpcString.ATT__ATTACK__S1__RO__ROGUE__S2, st.getPlayer().getName(), String.valueOf(_number));
			}
			else if (_number < 0 && ((st.getQuestItemsCount(CORPSE_LOG) + _number) > 0))
			{
				st.takeItems(CORPSE_LOG, Math.abs(_number));
				st.playSound(SOUND_ITEMGET);
				Functions.npcSay(npc, NpcString.ATT__ATTACK__S1__RO__ROGUE__S2, st.getPlayer().getName(), String.valueOf(_number));
			}

			if (st.getQuestItemsCount(CORPSE_LOG) >= _day_number)
			{
				st.takeItems(CORPSE_LOG, -1);
				st.giveItems(COLLECTION, 1);
				st.setCond(2);
			}

		}
		return null;
	}
}