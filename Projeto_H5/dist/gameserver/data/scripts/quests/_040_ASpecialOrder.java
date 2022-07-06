package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _040_ASpecialOrder extends Quest implements ScriptFile
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

	// NPC
	static final int Helvetia = 30081;
	static final int OFulle = 31572;
	static final int Gesto = 30511;

	// Items
	static final int FatOrangeFish = 6452;
	static final int NimbleOrangeFish = 6450;
	static final int OrangeUglyFish = 6451;
	static final int GoldenCobol = 5079;
	static final int ThornCobol = 5082;
	static final int GreatCobol = 5084;

	// Quest items
	static final int FishChest = 12764;
	static final int SeedJar = 12765;
	static final int WondrousCubic = 10632;

	public _040_ASpecialOrder()
	{
		super(false);
		addStartNpc(Helvetia);

		addQuestItem(FishChest);
		addQuestItem(SeedJar);

		addTalkId(OFulle);
		addTalkId(Gesto);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("take"))
		{
			int rand = Rnd.get(1, 2);
			if (rand == 1)
			{
				st.setCond(2);
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
				htmltext = "Helvetia-gave-ofulle.htm";
			}
			else
			{
				st.setCond(5);
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
				htmltext = "Helvetia-gave-gesto.htm";
			}
		}
		else if (event.equals("6"))
		{
			st.setCond(6);
			htmltext = "Gesto-3.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch (npcId)
		{
		case Helvetia:
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 40)
				{
					htmltext = "Helvetia-1.htm";
				}
				else
				{
					htmltext = "Helvetia-level.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 2:
			case 3:
			case 5:
			case 6:
				htmltext = "Helvetia-whereismyfish.htm";
				break;
			case 4:
				st.takeAllItems(FishChest);
				st.giveItems(WondrousCubic, 1, false);
				st.exitCurrentQuest(false);
				htmltext = "Helvetia-finish.htm";
				break;
			case 7:
				st.takeAllItems(SeedJar);
				st.giveItems(WondrousCubic, 1, false);
				st.exitCurrentQuest(false);
				htmltext = "Helvetia-finish.htm";
				break;
			default:
				break;
			}
			break;
		case OFulle:
			switch (cond)
			{
			case 2:
				htmltext = "OFulle-1.htm";
				st.setCond(3);
				break;
			case 3:
				if (st.getQuestItemsCount(FatOrangeFish) >= 10 && st.getQuestItemsCount(NimbleOrangeFish) >= 10 && st.getQuestItemsCount(OrangeUglyFish) >= 10)
				{
					st.takeItems(FatOrangeFish, 10);
					st.takeItems(NimbleOrangeFish, 10);
					st.takeItems(OrangeUglyFish, 10);
					st.giveItems(FishChest, 1, false);
					st.setCond(4);
					htmltext = "OFulle-2.htm";
				}
				else
				{
					htmltext = "OFulle-1.htm";
				}
				break;
			case 5:
			case 6:
				htmltext = "OFulle-3.htm";
				break;
			default:
				break;
			}
			break;
		case Gesto:
			switch (cond)
			{
			case 5:
				htmltext = "Gesto-1.htm";
				break;
			case 6:
				if (st.getQuestItemsCount(GoldenCobol) >= 40 && st.getQuestItemsCount(ThornCobol) >= 40 && st.getQuestItemsCount(GreatCobol) >= 40)
				{
					st.takeItems(GoldenCobol, 40);
					st.takeItems(ThornCobol, 40);
					st.takeItems(GreatCobol, 40);
					st.giveItems(SeedJar, 1, false);
					st.setCond(7);
					htmltext = "Gesto-4.htm";
				}
				else
				{
					htmltext = "Gesto-5.htm";
				}
				break;
			case 7:
				htmltext = "Gesto-6.htm";
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return htmltext;
	}
}