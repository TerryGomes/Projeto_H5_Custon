package quests;

import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2f.gameserver.scripts.ScriptFile;

public class _166_DarkMass extends Quest implements ScriptFile
{
	int UNDRES_LETTER_ID = 1088;
	int CEREMONIAL_DAGGER_ID = 1089;
	int DREVIANT_WINE_ID = 1090;
	int GARMIELS_SCRIPTURE_ID = 1091;

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

	public _166_DarkMass()
	{
		super(false);
		addStartNpc(30130);
		addTalkId(30135, 30139, 30143);
		addQuestItem(CEREMONIAL_DAGGER_ID, DREVIANT_WINE_ID, GARMIELS_SCRIPTURE_ID, UNDRES_LETTER_ID);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("1"))
		{
			htmltext = "30130-04.htm";
			st.giveItems(UNDRES_LETTER_ID, 1);
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{

		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		int cond = st.getCond();

		switch (npcId)
		{
		case 30130:
			if (id == CREATED)
			{
				if (st.getPlayer().getRace() != Race.darkelf && st.getPlayer().getRace() != Race.human)
				{
					htmltext = "30130-00.htm";
				}
				else if (st.getPlayer().getLevel() >= 2)
				{
					htmltext = "30130-03.htm";
					return htmltext;
				}
				else
				{
					htmltext = "30130-02.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1)
			{
				htmltext = "30130-05.htm";
			}
			else if (cond == 2)
			{
				htmltext = "30130-06.htm";
				st.takeItems(UNDRES_LETTER_ID, -1);
				st.takeItems(CEREMONIAL_DAGGER_ID, -1);
				st.takeItems(DREVIANT_WINE_ID, -1);
				st.takeItems(GARMIELS_SCRIPTURE_ID, -1);
				st.giveItems(ADENA_ID, 2966);
				st.getPlayer().addExpAndSp(5672, 446);
				if (st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("ng1"))
				{
					st.getPlayer().sendPacket(new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
				}
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
			break;
		case 30135:
			if (cond == 1 && st.getQuestItemsCount(CEREMONIAL_DAGGER_ID) == 0)
			{
				giveItem(st, CEREMONIAL_DAGGER_ID);
				htmltext = "30135-01.htm";
			}
			else
			{
				htmltext = "30135-02.htm";
			}
			break;
		case 30139:
			if (cond == 1 && st.getQuestItemsCount(DREVIANT_WINE_ID) == 0)
			{
				giveItem(st, DREVIANT_WINE_ID);
				htmltext = "30139-01.htm";
			}
			else
			{
				htmltext = "30139-02.htm";
			}
			break;
		case 30143:
			if (cond == 1 && st.getQuestItemsCount(GARMIELS_SCRIPTURE_ID) == 0)
			{
				giveItem(st, GARMIELS_SCRIPTURE_ID);
				htmltext = "30143-01.htm";
			}
			else
			{
				htmltext = "30143-02.htm";
			}
			break;
		default:
			break;
		}
		return htmltext;
	}

	private void giveItem(QuestState st, int item)
	{
		st.giveItems(item, 1);
		if (st.getQuestItemsCount(CEREMONIAL_DAGGER_ID) >= 1 && st.getQuestItemsCount(DREVIANT_WINE_ID) >= 1 && st.getQuestItemsCount(GARMIELS_SCRIPTURE_ID) >= 1)
		{
			st.setCond(2);
		}
	}
}