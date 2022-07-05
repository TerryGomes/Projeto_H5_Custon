package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _381_LetsBecomeARoyalMember extends Quest implements ScriptFile
{
	// Quest items
	private static int KAILS_COIN = 5899;
	private static int COIN_ALBUM = 5900;
	private static int MEMBERSHIP_1 = 3813;
	private static int CLOVER_COIN = 7569;
	private static int ROYAL_MEMBERSHIP = 5898;
	// NPCs
	private static int SORINT = 30232;
	private static int SANDRA = 30090;
	// MOBs
	private static int ANCIENT_GARGOYLE = 21018;
	private static int VEGUS = 27316;
	// CHANCES (custom values, feel free to change them)
	private static int GARGOYLE_CHANCE = 5;
	private static int VEGUS_CHANCE = 100;

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

	public _381_LetsBecomeARoyalMember()
	{
		super(false);

		addStartNpc(SORINT);
		addTalkId(SANDRA);

		addKillId(ANCIENT_GARGOYLE);
		addKillId(VEGUS);

		addQuestItem(KAILS_COIN);
		addQuestItem(COIN_ALBUM);
		addQuestItem(CLOVER_COIN);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("warehouse_keeper_sorint_q0381_02.htm"))
		{
			if (st.getPlayer().getLevel() >= 55 && st.getQuestItemsCount(MEMBERSHIP_1) > 0)
			{
				st.setCond(1);
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
				htmltext = "warehouse_keeper_sorint_q0381_03.htm";
			}
			else
			{
				htmltext = "warehouse_keeper_sorint_q0381_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("sandra_q0381_02.htm"))
		{
			if (st.getCond() == 1)
			{
				st.set("id", "1");
				st.playSound(SOUND_ACCEPT);
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";

		int cond = st.getCond();
		int npcId = npc.getNpcId();
		long album = st.getQuestItemsCount(COIN_ALBUM);

		if (npcId == SORINT)
		{
			if (cond == 0)
			{
				htmltext = "warehouse_keeper_sorint_q0381_01.htm";
			}
			else if (cond == 1)
			{
				long coin = st.getQuestItemsCount(KAILS_COIN);
				if (coin > 0 && album > 0)
				{
					st.takeItems(KAILS_COIN, -1);
					st.takeItems(COIN_ALBUM, -1);
					st.giveItems(ROYAL_MEMBERSHIP, 1);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(true);
					htmltext = "warehouse_keeper_sorint_q0381_06.htm";
				}
				else if (album == 0)
				{
					htmltext = "warehouse_keeper_sorint_q0381_05.htm";
				}
				else if (coin == 0)
				{
					htmltext = "warehouse_keeper_sorint_q0381_04.htm";
				}
			}
		}
		else
		{
			long clover = st.getQuestItemsCount(CLOVER_COIN);
			if (album > 0)
			{
				htmltext = "sandra_q0381_05.htm";
			}
			else if (clover > 0)
			{
				st.takeItems(CLOVER_COIN, -1);
				st.giveItems(COIN_ALBUM, 1);
				st.playSound(SOUND_ITEMGET);
				htmltext = "sandra_q0381_04.htm";
			}
			else if (st.getInt("id") == 0)
			{
				htmltext = "sandra_q0381_01.htm";
			}
			else
			{
				htmltext = "sandra_q0381_03.htm";
			}
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getState() != STARTED)
		{
			return null;
		}
		int npcId = npc.getNpcId();

		long album = st.getQuestItemsCount(COIN_ALBUM);
		long coin = st.getQuestItemsCount(KAILS_COIN);
		long clover = st.getQuestItemsCount(CLOVER_COIN);

		if (npcId == ANCIENT_GARGOYLE && coin == 0)
		{
			if (Rnd.chance(GARGOYLE_CHANCE))
			{
				st.giveItems(KAILS_COIN, 1);
				if (album > 0 || clover > 0)
				{
					st.playSound(SOUND_MIDDLE);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
		}
		else if (npcId == VEGUS && clover + album == 0 && st.getInt("id") != 0)
		{
			if (Rnd.chance(VEGUS_CHANCE))
			{
				st.giveItems(CLOVER_COIN, 1);
				if (coin > 0)
				{
					st.playSound(SOUND_MIDDLE);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
		}
		return null;
	}
}