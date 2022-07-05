package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 */

public class _278_HomeSecurity extends Quest implements ScriptFile
{
	private static final int Tunatun = 31537;
	private static final int[] FarmMonsters =
	{
		18905,
		18906
	};
	private static final int SelMahumMane = 15531;

	public _278_HomeSecurity()
	{
		super(false);
		addStartNpc(Tunatun);
		addKillId(FarmMonsters);
		addQuestItem(SelMahumMane);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("tunatun_q278_03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == Tunatun)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 82)
				{
					htmltext = "tunatun_q278_01.htm";
				}
				else
				{
					htmltext = "tunatun_q278_00.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				htmltext = "tunatun_q278_04.htm";
				break;
			case 2:
				if (st.getQuestItemsCount(SelMahumMane) >= 300)
				{
					htmltext = "tunatun_q278_05.htm";
					st.takeAllItems(SelMahumMane);
					switch (Rnd.get(1, 13))
					{
					case 1:
						st.giveItems(960, 1);
						break;
					case 2:
						st.giveItems(960, 2);
						break;
					case 3:
						st.giveItems(960, 3);
						break;
					case 4:
						st.giveItems(960, 4);
						break;
					case 5:
						st.giveItems(960, 5);
						break;
					case 6:
						st.giveItems(960, 6);
						break;
					case 7:
						st.giveItems(960, 7);
						break;
					case 8:
						st.giveItems(960, 8);
						break;
					case 9:
						st.giveItems(960, 9);
						break;
					case 10:
						st.giveItems(960, 10);
						break;
					case 11:
						st.giveItems(9553, 1);
						break;
					case 12:
						st.giveItems(9553, 2);
						break;
					case 13:
						st.giveItems(959, 1);
						break;
					}
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(true);
				}
				else
				{
					htmltext = "tunatun_q278_04.htm";
				}
				break;
			default:
				break;
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (cond == 1)
		{
			if (ArrayUtils.contains(FarmMonsters, npcId) && st.getQuestItemsCount(SelMahumMane) < 300)
			{
				st.giveItems(SelMahumMane, 1, true);
				if (st.getQuestItemsCount(SelMahumMane) >= 300)
				{
					st.setCond(2);
				}
			}
		}
		return null;
	}

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
}