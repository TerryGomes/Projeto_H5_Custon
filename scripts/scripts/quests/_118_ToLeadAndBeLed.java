package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _118_ToLeadAndBeLed extends Quest implements ScriptFile
{
	private static int PINTER = 30298;
	private static int MAILLE_LIZARDMAN = 20919;
	private static int BLOOD_OF_MAILLE_LIZARDMAN = 8062;
	private static int KING_OF_THE_ARANEID = 20927;
	private static int KING_OF_THE_ARANEID_LEG = 8063;
	private static int D_CRY = 1458;
	private static int D_CRY_COUNT_HEAVY = 721;
	private static int D_CRY_COUNT_LIGHT_MAGIC = 604;

	private static int CLAN_OATH_HELM = 7850;

	private static int CLAN_OATH_ARMOR = 7851;
	private static int CLAN_OATH_GAUNTLETS = 7852;
	private static int CLAN_OATH_SABATON = 7853;

	private static int CLAN_OATH_BRIGANDINE = 7854;
	private static int CLAN_OATH_LEATHER_GLOVES = 7855;
	private static int CLAN_OATH_BOOTS = 7856;

	private static int CLAN_OATH_AKETON = 7857;
	private static int CLAN_OATH_PADDED_GLOVES = 7858;
	private static int CLAN_OATH_SANDALS = 7859;

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

	public _118_ToLeadAndBeLed()
	{
		super(false);

		addStartNpc(PINTER);

		addKillId(MAILLE_LIZARDMAN);
		addKillId(KING_OF_THE_ARANEID);

		addQuestItem(BLOOD_OF_MAILLE_LIZARDMAN, KING_OF_THE_ARANEID_LEG);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equals("30298-02.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("30298-05a.htm"))
		{
			st.set("choose", "1");
			st.setCond(3);
		}
		else if (event.equals("30298-05b.htm"))
		{
			st.set("choose", "2");
			st.setCond(4);
		}
		else if (event.equals("30298-05c.htm"))
		{
			st.set("choose", "3");
			st.setCond(5);
		}
		else if (event.equals("30298-08.htm"))
		{
			int choose = st.getInt("choose");
			int need_dcry = choose == 1 ? D_CRY_COUNT_HEAVY : D_CRY_COUNT_LIGHT_MAGIC;
			if (st.getQuestItemsCount(D_CRY) < need_dcry)
			{
				return "30298-07.htm";
			}
			st.setCond(7);
			st.takeItems(D_CRY, need_dcry);
			st.playSound(SOUND_MIDDLE);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if (npc.getNpcId() != PINTER)
		{
			return "noquest";
		}
		int _state = st.getState();
		if (_state == CREATED)
		{
			if (st.getPlayer().getLevel() < 19)
			{
				st.exitCurrentQuest(true);
				return "30298-00.htm";
			}
			if (st.getPlayer().getClanId() == 0)
			{
				st.exitCurrentQuest(true);
				return "30298-00a.htm";
			}
			if (st.getPlayer().getSponsor() == 0)
			{
				st.exitCurrentQuest(true);
				return "30298-00b.htm";
			}
			st.setCond(0);
			return "30298-01.htm";
		}

		int cond = st.getCond();

		if (cond == 1 && _state == STARTED)
		{
			return "30298-02a.htm";
		}

		if (cond == 2 && _state == STARTED)
		{
			if (st.getQuestItemsCount(BLOOD_OF_MAILLE_LIZARDMAN) < 10)
			{
				st.setCond(1);
				return "30298-02a.htm";
			}
			st.takeItems(BLOOD_OF_MAILLE_LIZARDMAN, -1);
			return "30298-04.htm";
		}

		if (cond == 3 && _state == STARTED)
		{
			return "30298-05a.htm";
		}

		if (cond == 4 && _state == STARTED)
		{
			return "30298-05b.htm";
		}

		if (cond == 5 && _state == STARTED)
		{
			return "30298-05c.htm";
		}

		if (cond == 7 && _state == STARTED)
		{
			return "30298-08a.htm";
		}

		if (cond == 8 && _state == STARTED)
		{
			if (st.getQuestItemsCount(KING_OF_THE_ARANEID_LEG) < 8)
			{
				st.setCond(7);
				return "30298-08a.htm";
			}
			st.takeItems(KING_OF_THE_ARANEID_LEG, -1);
			st.giveItems(CLAN_OATH_HELM, 1);
			int choose = st.getInt("choose");
			if (choose == 1)
			{
				st.giveItems(CLAN_OATH_ARMOR, 1);
				st.giveItems(CLAN_OATH_GAUNTLETS, 1);
				st.giveItems(CLAN_OATH_SABATON, 1);
			}
			else if (choose == 2)
			{
				st.giveItems(CLAN_OATH_BRIGANDINE, 1);
				st.giveItems(CLAN_OATH_LEATHER_GLOVES, 1);
				st.giveItems(CLAN_OATH_BOOTS, 1);
			}
			else
			{
				st.giveItems(CLAN_OATH_AKETON, 1);
				st.giveItems(CLAN_OATH_PADDED_GLOVES, 1);
				st.giveItems(CLAN_OATH_SANDALS, 1);
			}
			st.unset("cond");
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
			return "30298-09.htm";
		}

		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == MAILLE_LIZARDMAN && st.getQuestItemsCount(BLOOD_OF_MAILLE_LIZARDMAN) < 10 && cond == 1 && Rnd.chance(50))
		{
			st.giveItems(BLOOD_OF_MAILLE_LIZARDMAN, 1);
			if (st.getQuestItemsCount(BLOOD_OF_MAILLE_LIZARDMAN) == 10)
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(2);
			}
			else
			{
				st.playSound(SOUND_ITEMGET);
			}
		}
		else if (npcId == KING_OF_THE_ARANEID && st.getQuestItemsCount(KING_OF_THE_ARANEID_LEG) < 8 && cond == 7 && Rnd.chance(50))
		{
			st.giveItems(KING_OF_THE_ARANEID_LEG, 1);
			if (st.getQuestItemsCount(KING_OF_THE_ARANEID_LEG) == 8)
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(8);
			}
			else
			{
				st.playSound(SOUND_ITEMGET);
			}
		}
		return null;
	}
}