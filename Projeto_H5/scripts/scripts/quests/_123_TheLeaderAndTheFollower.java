package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _123_TheLeaderAndTheFollower extends Quest implements ScriptFile
{
	int NEWYEAR = 31961;
	int BRUIN_LIZARDMEN = 27321;
	int BRUIN_BLOOD = 8549;
	int PICOT_ARANEID = 27322;
	int PICOT_LEG = 8550;
	int D_CRY = 1458;
	int D_CRY_COUNT_HEAVY = 721;
	int D_CRY_COUNT_LIGHT_MAGIC = 604;

	int CLAN_OATH_HELM = 7850;

	int CLAN_OATH_ARMOR = 7851;
	int CLAN_OATH_GAUNTLETS = 7852;
	int CLAN_OATH_SABATON = 7853;

	int CLAN_OATH_BRIGANDINE = 7854;
	int CLAN_OATH_LEATHER_GLOVES = 7855;
	int CLAN_OATH_BOOTS = 7856;

	int CLAN_OATH_AKETON = 7857;
	int CLAN_OATH_PADDED_GLOVES = 7858;
	int CLAN_OATH_SANDALS = 7859;

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

	public _123_TheLeaderAndTheFollower()
	{
		super(false);

		addStartNpc(NEWYEAR);

		addKillId(BRUIN_LIZARDMEN);
		addKillId(PICOT_ARANEID);

		addQuestItem(BRUIN_BLOOD, PICOT_LEG);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("31961-03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("31961-05.htm"))
		{
			st.set("choose", "1");
			st.setCond(3);
		}
		else if (event.equals("31961-06.htm"))
		{
			st.set("choose", "2");
			st.setCond(4);
		}
		else if (event.equals("31961-07.htm"))
		{
			st.set("choose", "3");
			st.setCond(5);
		}
		else if (event.equals("31961-08.htm"))
		{
			int choose = st.getInt("choose");
			int D_CRY_COUNT = D_CRY_COUNT_LIGHT_MAGIC;
			if (choose == 1)
			{
				D_CRY_COUNT = D_CRY_COUNT_HEAVY;
			}
			if (st.getQuestItemsCount(D_CRY) >= D_CRY_COUNT)
			{
				st.setCond(7);
				st.takeItems(D_CRY, D_CRY_COUNT);
			}
			else
			{
				htmltext = "<html><body>771 D Cry!</body></html>";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		switch (cond)
		{
		case 0:
			if (st.getPlayer().getLevel() < 19)
			{
				htmltext = "<html><body>Your level is too low</body></html>";
				return htmltext;
			}
			else if (st.getPlayer().getClanId() == 0)
			{
				htmltext = "<html><body>You are not in clan</body></html>";
				return htmltext;
			}
			else if (st.getPlayer().getSponsor() == 0)
			{
				htmltext = "<html><body>You have no sponsor</body></html>";
				return htmltext;
			}
			else
			{
				htmltext = "31961-00.htm";
			}
			break;
		case 1:
			htmltext = "<html><body>Bring me 10 Bruin Lizardmen blood.</body></html>";
			break;
		case 2:
			st.takeItems(BRUIN_BLOOD, 10);
			htmltext = "31961-04.htm";
			break;
		case 3:
			htmltext = "31961-05.htm";
			break;
		case 4:
			htmltext = "31961-06.htm";
			break;
		case 5:
			htmltext = "31961-07.htm";
			break;
		case 7:
			htmltext = "<html><body>Bring me 8 Picot Legs.</body></html>";
			break;
		case 8:
		{
			st.takeItems(PICOT_LEG, 8);
			int choose = st.getInt("choose");
			st.giveItems(CLAN_OATH_HELM, 1);
			switch (choose)
			{
			case 1:
				st.giveItems(CLAN_OATH_ARMOR, 1);
				st.giveItems(CLAN_OATH_GAUNTLETS, 1);
				st.giveItems(CLAN_OATH_SABATON, 1);
				break;
			case 2:
				st.giveItems(CLAN_OATH_BRIGANDINE, 1);
				st.giveItems(CLAN_OATH_LEATHER_GLOVES, 1);
				st.giveItems(CLAN_OATH_BOOTS, 1);
				break;
			case 3:
				st.giveItems(CLAN_OATH_AKETON, 1);
				st.giveItems(CLAN_OATH_PADDED_GLOVES, 1);
				st.giveItems(CLAN_OATH_SANDALS, 1);
				break;
			default:
				break;
			}
			st.setCond(0);
			st.playSound(SOUND_FINISH);
			htmltext = "<html><body>OK!</body></html>";
			st.exitCurrentQuest(false);
			break;
		}
		default:
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == BRUIN_LIZARDMEN && st.getQuestItemsCount(BRUIN_BLOOD) < 10 && cond == 1 && Rnd.chance(50))
		{
			st.giveItems(BRUIN_BLOOD, 1);
			if (st.getQuestItemsCount(BRUIN_BLOOD) == 10)
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(2);
			}
		}
		else if (npcId == PICOT_ARANEID && st.getQuestItemsCount(PICOT_LEG) < 8 && cond == 7 && Rnd.chance(50))
		{
			st.giveItems(PICOT_LEG, 1);
			if (st.getQuestItemsCount(PICOT_LEG) == 8)
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(8);
			}
		}
		return null;
	}
}