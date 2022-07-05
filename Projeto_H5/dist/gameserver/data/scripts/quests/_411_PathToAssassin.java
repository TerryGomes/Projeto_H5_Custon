package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _411_PathToAssassin extends Quest implements ScriptFile
{
	// npc
	public final int TRISKEL = 30416;
	public final int LEIKAN = 30382;
	public final int ARKENIA = 30419;
	// mobs
	public final int MOONSTONE_BEAST = 20369;
	public final int CALPICO = 27036;
	// items
	public final int SHILENS_CALL_ID = 1245;
	public final int ARKENIAS_LETTER_ID = 1246;
	public final int LEIKANS_NOTE_ID = 1247;
	public final int ONYX_BEASTS_MOLAR_ID = 1248;
	public final int LEIKANS_KNIFE_ID = 1249;
	public final int SHILENS_TEARS_ID = 1250;
	public final int ARKENIA_RECOMMEND_ID = 1251;
	public final int IRON_HEART_ID = 1252;

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

	public _411_PathToAssassin()
	{
		super(false);

		addStartNpc(TRISKEL);

		addTalkId(LEIKAN);
		addTalkId(ARKENIA);

		addKillId(MOONSTONE_BEAST);
		addKillId(CALPICO);

		addQuestItem(new int[]
		{
			SHILENS_CALL_ID,
			LEIKANS_NOTE_ID,
			LEIKANS_KNIFE_ID,
			ARKENIA_RECOMMEND_ID,
			ARKENIAS_LETTER_ID,
			ONYX_BEASTS_MOLAR_ID,
			SHILENS_TEARS_ID
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("1"))
		{
			if (st.getPlayer().getLevel() >= 18 && st.getPlayer().getClassId().getId() == 0x1f && st.getQuestItemsCount(IRON_HEART_ID) < 1)
			{
				st.setCond(1);
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
				st.giveItems(SHILENS_CALL_ID, 1);
				htmltext = "triskel_q0411_05.htm";
			}
			else if (st.getPlayer().getClassId().getId() != 0x1f)
			{
				if (st.getPlayer().getClassId().getId() == 0x23)
				{
					htmltext = "triskel_q0411_02a.htm";
				}
				else
				{
					htmltext = "triskel_q0411_02.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (st.getPlayer().getLevel() < 18 && st.getPlayer().getClassId().getId() == 0x1f)
			{
				htmltext = "triskel_q0411_03.htm";
				st.exitCurrentQuest(true);
			}
			else if (st.getPlayer().getLevel() >= 18 && st.getPlayer().getClassId().getId() == 0x1f && st.getQuestItemsCount(IRON_HEART_ID) > 0)
			{
				htmltext = "triskel_q0411_04.htm";
			}
		}
		else if (event.equalsIgnoreCase("30419_1"))
		{
			htmltext = "arkenia_q0411_05.htm";
			st.takeItems(SHILENS_CALL_ID, -1);
			st.giveItems(ARKENIAS_LETTER_ID, 1);
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30382_1"))
		{
			htmltext = "guard_leikan_q0411_03.htm";
			st.takeItems(ARKENIAS_LETTER_ID, -1);
			st.giveItems(LEIKANS_NOTE_ID, 1);
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
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
		case TRISKEL:
			if (cond < 1)
			{
				if (st.getQuestItemsCount(IRON_HEART_ID) < 1)
				{
					htmltext = "triskel_q0411_01.htm";
				}
				else
				{
					htmltext = "triskel_q0411_04.htm";
				}
			}
			else
			{
				switch (cond)
				{
				case 7:
					htmltext = "triskel_q0411_06.htm";
					st.takeItems(ARKENIA_RECOMMEND_ID, -1);
					if (st.getPlayer().getClassId().getLevel() == 1)
					{
						st.giveItems(IRON_HEART_ID, 1);
						if (!st.getPlayer().getVarB("prof1"))
						{
							st.getPlayer().setVar("prof1", "1", -1);
							st.addExpAndSp(228064, 16455);
							// FIXME [G1ta0] дать адены, только если первый чар на акке
							st.giveItems(ADENA_ID, 81900);
						}
					}
					st.exitCurrentQuest(true);
					st.playSound(SOUND_FINISH);
					break;
				case 2:
					htmltext = "triskel_q0411_07.htm";
					break;
				case 1:
					htmltext = "triskel_q0411_11.htm";
					break;
				default:
					if (cond > 2 && cond < 7)
					{
						if (cond > 2 && cond < 5)
						{
							htmltext = "triskel_q0411_08.htm";
						}
						else if (cond > 4 && cond < 7)
						{
							if (st.getQuestItemsCount(SHILENS_TEARS_ID) < 1)
							{
								htmltext = "triskel_q0411_09.htm";
							}
							else
							{
								htmltext = "triskel_q0411_10.htm";
							}
						}
					}
					break;
				}
			}
			break;
		case ARKENIA:
			if (cond == 1 && st.getQuestItemsCount(SHILENS_CALL_ID) > 0)
			{
				htmltext = "arkenia_q0411_01.htm";
			}
			else if (cond == 2 && st.getQuestItemsCount(ARKENIAS_LETTER_ID) > 0)
			{
				htmltext = "arkenia_q0411_07.htm";
			}
			else if (cond > 2 && cond < 5 && st.getQuestItemsCount(LEIKANS_NOTE_ID) > 0)
			{
				htmltext = "arkenia_q0411_10.htm";
			}
			else if (cond == 5 && st.getQuestItemsCount(LEIKANS_KNIFE_ID) > 0)
			{
				htmltext = "arkenia_q0411_11.htm";
			}
			else if (cond == 6 && st.getQuestItemsCount(SHILENS_TEARS_ID) > 0)
			{
				htmltext = "arkenia_q0411_08.htm";
				st.takeItems(SHILENS_TEARS_ID, -1);
				st.takeItems(LEIKANS_KNIFE_ID, -1);
				st.giveItems(ARKENIA_RECOMMEND_ID, 1);
				st.setCond(7);
				st.playSound(SOUND_MIDDLE);
			}
			else if (cond == 7)
			{
				htmltext = "arkenia_q0411_09.htm";
			}
			break;
		case LEIKAN:
			if (cond == 2 && st.getQuestItemsCount(ARKENIAS_LETTER_ID) > 0)
			{
				htmltext = "guard_leikan_q0411_01.htm";
			}
			else if (cond > 2 && cond < 4 && st.getQuestItemsCount(ONYX_BEASTS_MOLAR_ID) < 1)
			{
				htmltext = "guard_leikan_q0411_05.htm";
				if (cond == 4)
				{
					st.setCond(3);
				}
			}
			else if (cond > 2 && cond < 4 && st.getQuestItemsCount(ONYX_BEASTS_MOLAR_ID) < 10)
			{
				htmltext = "guard_leikan_q0411_06.htm";
				if (cond == 4)
				{
					st.setCond(3);
				}
			}
			else if (cond == 4 && st.getQuestItemsCount(ONYX_BEASTS_MOLAR_ID) > 9)
			{
				htmltext = "guard_leikan_q0411_07.htm";
				st.takeItems(ONYX_BEASTS_MOLAR_ID, -1);
				st.takeItems(LEIKANS_NOTE_ID, -1);
				st.giveItems(LEIKANS_KNIFE_ID, 1);
				st.setCond(5);
				st.playSound(SOUND_MIDDLE);
			}
			else if (cond > 4 && cond < 7 && st.getQuestItemsCount(SHILENS_TEARS_ID) < 1)
			{
				htmltext = "guard_leikan_q0411_09.htm";
				if (cond == 6)
				{
					st.setCond(5);
				}
			}
			else if (cond == 6 && st.getQuestItemsCount(SHILENS_TEARS_ID) > 0)
			{
				htmltext = "guard_leikan_q0411_08.htm";
			}
			break;
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
		if (npcId == CALPICO)
		{
			if (cond == 5 && st.getQuestItemsCount(LEIKANS_KNIFE_ID) > 0 && st.getQuestItemsCount(SHILENS_TEARS_ID) < 1)
			{
				st.giveItems(SHILENS_TEARS_ID, 1);
				st.playSound(SOUND_MIDDLE);
				st.setCond(6);
			}
		}
		else if (npcId == MOONSTONE_BEAST)
		{
			if (cond == 3 && st.getQuestItemsCount(LEIKANS_NOTE_ID) > 0 && st.getQuestItemsCount(ONYX_BEASTS_MOLAR_ID) < 10)
			{
				st.giveItems(ONYX_BEASTS_MOLAR_ID, 1);
				if (st.getQuestItemsCount(ONYX_BEASTS_MOLAR_ID) > 9)
				{
					st.playSound(SOUND_MIDDLE);
					st.setCond(4);
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