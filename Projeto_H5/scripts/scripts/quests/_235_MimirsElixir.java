package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _235_MimirsElixir extends Quest implements ScriptFile
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

	private final static int chance = 45;

	// prerequisites:
	private final static int STAR_OF_DESTINY = 5011;
	private final static int MINLEVEL = 75;

	// Quest items
	private final static int PURE_SILVER = 6320;
	private final static int TRUE_GOLD = 6321;
	private final static int SAGES_STONE = 6322;
	private final static int BLOOD_FIRE = 6318;
	// private final static int MIMIRS_ELIXIR = 6319;

	private final static int SCROLL_ENCHANT_WEAPON_A = 729;

	// NPCs
	private final static int LADD = 30721;
	private final static int JOAN = 30718;
	private final static int Chimera_Piece = 20965;
	private final static int Bloody_Guardian = 21090;

	public _235_MimirsElixir()
	{
		super(false);

		addStartNpc(LADD);

		addTalkId(JOAN);
		addTalkId(LADD);

		addKillId(Chimera_Piece);
		addKillId(Bloody_Guardian);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("1"))
		{
			st.takeItems(STAR_OF_DESTINY, -1);
			st.setState(STARTED);
			st.setCond(1);
			htmltext = "30721-02a.htm";
		}
		else if (event.equalsIgnoreCase("30718_1"))
		{
			st.setCond(3);
			htmltext = "30718-01a.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int id = st.getState();
		if (id == COMPLETED)
		{
			return "completed";
		}

		if (st.getPlayer().getRace() == Race.kamael)
		{
			st.exitCurrentQuest(true);
			return "<html><body>I'm sorry, but I am not allowed to offer this quest to Kamael. Talk to Hierarch Kekropus.</body></html>";
		}

		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == LADD)
		{
			if (id == CREATED)
			{
				if (st.getPlayer().getLevel() < MINLEVEL)
				{
					st.exitCurrentQuest(true);
					return "30721-01.htm";
				}
				if (st.getQuestItemsCount(STAR_OF_DESTINY) > 0)
				{
					st.takeItems(STAR_OF_DESTINY, -1);
					st.setCond(0);
					return "30721-02.htm";
				}
				if (st.getPlayer().getQuestState(_234_FatesWhisper.class) != null && st.getPlayer().getQuestState(_234_FatesWhisper.class).isCompleted())
				{
					st.setCond(0);
					return "30721-02.htm";
				}
				st.exitCurrentQuest(true);
				return "30721-01a.htm";
			}
			// was asked to get pure silver but has not done so yet. Repeat: get pure silver
			else if (cond == 1 && st.getQuestItemsCount(PURE_SILVER) < 1)
			{
				htmltext = "30721-03.htm"; // Bring me Pure silver from Reagents quest
			}
			else if (cond == 1 && st.getQuestItemsCount(PURE_SILVER) > 0)
			{
				st.setCond(2);
				htmltext = "30721-04.htm"; // Bring me True Gold from Joan
			}
			else if (1 < cond && cond < 5)
			{ // интересное условие...
				htmltext = "30721-05.htm"; // Where is my GOLD?! Bring to me first.
			}
			else if (cond == 5)
			{
				st.setCond(6);
				htmltext = "30721-06.htm"; // find Blood Fire from "bloody guardians"
			}
			// still looking for blood fire?
			else if (cond == 6)
			{
				htmltext = "30721-07.htm"; // find Blood Fire from "bloody guardians"
			}
			else if (cond == 7 && st.getQuestItemsCount(PURE_SILVER) > 0 && st.getQuestItemsCount(TRUE_GOLD) > 0)
			{
				htmltext = "30721-08.htm"; // what are you standing there for? Go to the cauldron and mix them...
			}
			else if (cond == 7)
			{
				htmltext = "30721-09.htm"; // Well...you already know what to do...go get the 3 items...
				st.setCond(3); // start over...yay...
			}
			// cond for this quest is set to 8 from Supplier or Reagents, when you create Mimir's Elixir.
			// Finally, all is done...time to learn how to use the Elixir...
			else if (cond == 8)
			{
				htmltext = "30721-10.htm"; // here's what you do...
				// st.takeItems(MIMIRS_ELIXIR,-1) #disabled for compatibility with the way java expects things at Grand Master.
				st.giveItems(SCROLL_ENCHANT_WEAPON_A, 1, true);
				st.unset("cond");
				st.exitCurrentQuest(false);
			}

		}
		else if (npcId == JOAN)
		{
			// first time talking to Joan: You ask for True Gold, she sends you for Sage's stone
			switch (cond)
			{
			case 2:
				htmltext = "30718-01.htm"; // You want True Gold? Please get the sage's stone. Kill Chimera!
				break;
			case 3:
				htmltext = "30718-02.htm"; // you haven't gotten the sage's stone yet?
				break;
			case 4:
				st.takeItems(SAGES_STONE, -1);
				st.giveItems(TRUE_GOLD, 1);
				st.setCond(5);
				htmltext = "30718-03.htm"; // here you go...take the gold. Now go back to ladd.
				break;
			default:
				if (cond >= 5)
				{
					htmltext = "30718-04.htm"; // Go back to ladd already!
				}
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
		if (npcId == Chimera_Piece && cond == 3 && st.getQuestItemsCount(SAGES_STONE) == 0 && Rnd.chance(chance))
		{
			st.giveItems(SAGES_STONE, 1);
			st.playSound(SOUND_ITEMGET);
			st.setCond(cond + 1);
		}
		if (npcId == Bloody_Guardian && cond == 6 && st.getQuestItemsCount(BLOOD_FIRE) == 0 && Rnd.chance(chance))
		{
			st.giveItems(BLOOD_FIRE, 1);
			st.playSound(SOUND_ITEMGET);
			st.setCond(cond + 1);
		}
		return null;
	}
}