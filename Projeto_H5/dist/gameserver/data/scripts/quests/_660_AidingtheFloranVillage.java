package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _660_AidingtheFloranVillage extends Quest implements ScriptFile
{
	// NPC
	public final int MARIA = 30608;
	public final int ALEX = 30291;
	// MOBS
	public final int CARSED_SEER = 21106;
	public final int PLAIN_WATCMAN = 21102;
	public final int ROUGH_HEWN_ROCK_GOLEM = 21103;
	public final int DELU_LIZARDMAN_SHAMAN = 20781;
	public final int DELU_LIZARDMAN_SAPPLIER = 21104;
	public final int DELU_LIZARDMAN_COMMANDER = 21107;
	public final int DELU_LIZARDMAN_SPESIAL_AGENT = 21105;
	// ITEMS
	public final int WATCHING_EYES = 8074;
	public final int ROUGHLY_HEWN_ROCK_GOLEM_SHARD = 8075;
	public final int DELU_LIZARDMAN_SCALE = 8076;
	// REWARDS
	public final int SCROLL_ENCANT_ARMOR = 956;
	public final int SCROLL_ENCHANT_WEAPON = 955;

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

	public _660_AidingtheFloranVillage()
	{
		super(false);

		addStartNpc(MARIA);

		addTalkId(MARIA);
		addTalkId(ALEX);

		addKillId(CARSED_SEER);
		addKillId(PLAIN_WATCMAN);
		addKillId(ROUGH_HEWN_ROCK_GOLEM);
		addKillId(DELU_LIZARDMAN_SHAMAN);
		addKillId(DELU_LIZARDMAN_SAPPLIER);
		addKillId(DELU_LIZARDMAN_COMMANDER);
		addKillId(DELU_LIZARDMAN_SPESIAL_AGENT);

		addQuestItem(WATCHING_EYES);
		addQuestItem(DELU_LIZARDMAN_SCALE);
		addQuestItem(ROUGHLY_HEWN_ROCK_GOLEM_SHARD);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		long EYES = st.getQuestItemsCount(WATCHING_EYES);
		long SCALE = st.getQuestItemsCount(DELU_LIZARDMAN_SCALE);
		long SHARD = st.getQuestItemsCount(ROUGHLY_HEWN_ROCK_GOLEM_SHARD);
		if (event.equalsIgnoreCase("30608-04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30291-05.htm"))
		{
			if (EYES + SCALE + SHARD >= 45)
			{
				st.giveItems(ADENA_ID, EYES * 100 + SCALE * 100 + SHARD * 100 + 9000);
				st.takeItems(WATCHING_EYES, -1);
				st.takeItems(DELU_LIZARDMAN_SCALE, -1);
				st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD, -1);
			}
			else
			{
				st.giveItems(ADENA_ID, EYES * 100 + SCALE * 100 + SHARD * 100);
				st.takeItems(WATCHING_EYES, -1);
				st.takeItems(DELU_LIZARDMAN_SCALE, -1);
				st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD, -1);
			}
			st.playSound(SOUND_ITEMGET);
		}
		else if (event.equalsIgnoreCase("30291-11.htm"))
		{
			if (EYES + SCALE + SHARD >= 99)
			{
				long n = 100 - EYES;
				long t = 100 - SCALE - EYES;
				if (EYES >= 100)
				{
					st.takeItems(WATCHING_EYES, 100);
				}
				else
				{
					st.takeItems(WATCHING_EYES, -1);
					if (SCALE >= n)
					{
						st.takeItems(DELU_LIZARDMAN_SCALE, n);
					}
					else
					{
						st.takeItems(DELU_LIZARDMAN_SCALE, -1);
						st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD, t);
					}
				}
				if (Rnd.chance(80))
				{
					st.giveItems(ADENA_ID, 13000);
					st.giveItems(SCROLL_ENCANT_ARMOR, 1);
				}
				else
				{
					st.giveItems(ADENA_ID, 1000);
				}
				st.playSound(SOUND_ITEMGET);
			}
			else
			{
				htmltext = "30291-14.htm";
			}
		}
		else if (event.equalsIgnoreCase("30291-12.htm"))
		{
			if (EYES + SCALE + SHARD >= 199)
			{
				long n = 200 - EYES;
				long t = 200 - SCALE - EYES;
				int luck = Rnd.get(15);
				if (EYES >= 200)
				{
					st.takeItems(WATCHING_EYES, 200);
				}
				else
				{
					st.takeItems(WATCHING_EYES, -1);
				}
				if (SCALE >= n)
				{
					st.takeItems(DELU_LIZARDMAN_SCALE, n);
				}
				else
				{
					st.takeItems(DELU_LIZARDMAN_SCALE, -1);
				}
				st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD, t);
				if (luck < 9)
				{
					st.giveItems(ADENA_ID, 20000);
					st.giveItems(SCROLL_ENCANT_ARMOR, 1);
				}
				else if (luck > 8 && luck < 12)
				{
					st.giveItems(SCROLL_ENCHANT_WEAPON, 1);
				}
				else
				{
					st.giveItems(ADENA_ID, 2000);
				}
				st.playSound(SOUND_ITEMGET);
			}
			else
			{
				htmltext = "30291-14.htm";
			}
		}
		else if (event.equalsIgnoreCase("30291-13.htm"))
		{
			if (EYES + SCALE + SHARD >= 499)
			{
				long n = 500 - EYES;
				long t = 500 - SCALE - EYES;
				if (EYES >= 500)
				{
					st.takeItems(WATCHING_EYES, 500);
				}
				else
				{
					st.takeItems(WATCHING_EYES, -1);
				}
				if (SCALE >= n)
				{
					st.takeItems(DELU_LIZARDMAN_SCALE, n);
				}
				else
				{
					st.takeItems(DELU_LIZARDMAN_SCALE, -1);
					st.takeItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD, t);
				}
				if (Rnd.chance(80))
				{
					st.giveItems(ADENA_ID, 45000);
					st.giveItems(SCROLL_ENCHANT_WEAPON, 1);
				}
				else
				{
					st.giveItems(ADENA_ID, 5000);
				}
				st.playSound(SOUND_ITEMGET);
			}
			else
			{
				htmltext = "30291-14.htm";
			}
		}
		else if (event.equalsIgnoreCase("30291-06.htm"))
		{
			st.exitCurrentQuest(true);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == MARIA && cond < 1)
		{
			if (st.getPlayer().getLevel() < 30)
			{
				htmltext = "30608-01.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "30608-02.htm";
			}
		}
		else if (npcId == MARIA && cond == 1)
		{
			htmltext = "30608-06.htm";
		}
		else if (npcId == ALEX && cond == 1)
		{
			htmltext = "30291-01.htm";
			st.playSound(SOUND_MIDDLE);
			st.setCond(2);
		}
		else if (npcId == ALEX && cond == 2)
		{
			if (st.getQuestItemsCount(WATCHING_EYES) + st.getQuestItemsCount(DELU_LIZARDMAN_SCALE) + st.getQuestItemsCount(ROUGHLY_HEWN_ROCK_GOLEM_SHARD) == 0)
			{
				htmltext = "30291-02.htm";
			}
			else
			{
				htmltext = "30291-03.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int chance = Rnd.get(100) + 1;
		if (st.getCond() == 2)
		{
			if (npcId == 21106 | npcId == 21102 && chance < 79)
			{
				st.giveItems(WATCHING_EYES, 1);
				st.playSound(SOUND_ITEMGET);
			}
			else if (npcId == ROUGH_HEWN_ROCK_GOLEM && chance < 75)
			{
				st.giveItems(ROUGHLY_HEWN_ROCK_GOLEM_SHARD, 1);
				st.playSound(SOUND_ITEMGET);
			}
			else if (npcId == 20781 | npcId == 21104 | npcId == 21107 | npcId == 21105 && chance < 67)
			{
				st.giveItems(DELU_LIZARDMAN_SCALE, 1);
				st.playSound(SOUND_ITEMGET);
			}
		}
		return null;
	}
}