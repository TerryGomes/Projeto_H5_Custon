package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _358_IllegitimateChildOfAGoddess extends Quest implements ScriptFile
{
	// Variables
	private static final int DROP_RATE = 70; // in %
	private static final int REQUIRED = 108; // how many items will be paid for a reward (affects onkill sounds too)

	// Quest items
	private static final int SN_SCALE = 5868;

	// Rewards
	// private static final int REWARDS=range(6329,6340,2)+range(5364,5367,2)
	private static final int SPhoenixNecl70 = 6329; // 16%
	private static final int SPhoenixEarr70 = 6331; // 17%
	private static final int SPhoenixRing70 = 6333; // 17%

	private static final int SMajestNecl70 = 6335; // 8%
	private static final int SMajestEarr70 = 6337; // 9%
	private static final int SMajestRing70 = 6339; // 9%

	private static final int SDarkCryShield60 = 5364; // 8%
	private static final int SNightMareShield60 = 5366; // 16%
	// Messages
	private static final String defaulttext = "noquest";

	// NPCs
	private static final int OLTLIN = 30862;

	// Mobs
	private static final int MOB1 = 20672;
	private static final int MOB2 = 20673;

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

	public _358_IllegitimateChildOfAGoddess()
	{
		super(true);
		addStartNpc(OLTLIN);

		addKillId(MOB1);
		addKillId(MOB2);

		addQuestItem(SN_SCALE);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30862-5.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30862-6.htm"))
		{
			st.exitCurrentQuest(true);
		}
		else if (event.equalsIgnoreCase("30862-7.htm"))
		{
			if (st.getQuestItemsCount(SN_SCALE) >= REQUIRED)
			{
				st.takeItems(SN_SCALE, REQUIRED);
				// Opredelenie nagradi
				for (int i = 0; i < (int) st.getRateQuestsReward(); i++)
				{
					int item;
					int chance = Rnd.get(100);
					if (chance <= 16)
					{
						item = SPhoenixNecl70;
					}
					else if (chance <= 33)
					{
						item = SPhoenixEarr70;
					}
					else if (chance <= 50)
					{
						item = SPhoenixRing70;
					}
					else if (chance <= 58)
					{
						item = SMajestNecl70;
					}
					else if (chance <= 67)
					{
						item = SMajestEarr70;
					}
					else if (chance <= 76)
					{
						item = SMajestRing70;
					}
					else if (chance <= 84)
					{
						item = SDarkCryShield60;
					}
					else
					{
						item = SNightMareShield60;
					}
					st.giveItems(item, 1);
				}
				st.exitCurrentQuest(true);
				st.playSound(SOUND_FINISH);
			}
			else
			{
				htmltext = "30862-4.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = defaulttext;
		int id = st.getState();
		if (id == CREATED)
		{
			if (st.getPlayer().getLevel() < 63)
			{
				st.exitCurrentQuest(true);
				htmltext = "30862-1.htm";
			}
			else
			{
				htmltext = "30862-2.htm";
			}
		}
		else if (id == STARTED)
		{
			if (st.getQuestItemsCount(SN_SCALE) >= REQUIRED)
			{
				htmltext = "30862-3.htm";
			}
			else
			{
				htmltext = "30862-4.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		long count = st.getQuestItemsCount(SN_SCALE);
		if (count < REQUIRED && Rnd.chance(DROP_RATE))
		{
			st.giveItems(SN_SCALE, 1);
			if (count + 1 == REQUIRED)
			{
				st.setState(STARTED);
				st.playSound(SOUND_MIDDLE);
				st.setCond(2);
			}
			else
			{
				st.playSound(SOUND_ITEMGET);
			}
		}
		return null;
	}
}