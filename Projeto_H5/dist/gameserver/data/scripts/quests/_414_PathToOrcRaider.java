package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _414_PathToOrcRaider extends Quest implements ScriptFile
{
	// npc
	public final int KARUKIA = 30570;
	public final int KASMAN = 30501;
	public final int TAZEER = 31978;
	// mobs
	public final int GOBLIN_TOMB_RAIDER_LEADER = 20320;
	public final int KURUKA_RATMAN_LEADER = 27045;
	public final int UMBAR_ORC = 27054;
	public final int TIMORA_ORC = 27320;
	// items
	public final int GREEN_BLOOD = 1578;
	public final int GOBLIN_DWELLING_MAP = 1579;
	public final int KURUKA_RATMAN_TOOTH = 1580;
	public final int BETRAYER_UMBAR_REPORT = 1589;
	public final int HEAD_OF_BETRAYER = 1591;
	public final int TIMORA_ORCS_HEAD = 8544;
	public final int MARK_OF_RAIDER = 1592;

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

	public _414_PathToOrcRaider()
	{
		super(false);

		addStartNpc(KARUKIA);

		addTalkId(KASMAN);
		addTalkId(TAZEER);

		addKillId(GOBLIN_TOMB_RAIDER_LEADER);
		addKillId(KURUKA_RATMAN_LEADER);
		addKillId(UMBAR_ORC);
		addKillId(TIMORA_ORC);

		addQuestItem(KURUKA_RATMAN_TOOTH);
		addQuestItem(GOBLIN_DWELLING_MAP);
		addQuestItem(GREEN_BLOOD);
		addQuestItem(HEAD_OF_BETRAYER);
		addQuestItem(BETRAYER_UMBAR_REPORT);
		addQuestItem(TIMORA_ORCS_HEAD);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("prefect_karukia_q0414_05.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.giveItems(GOBLIN_DWELLING_MAP, 1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("to_Gludin"))
		{
			htmltext = "prefect_karukia_q0414_07a.htm";
			st.takeItems(KURUKA_RATMAN_TOOTH, -1);
			st.takeItems(GOBLIN_DWELLING_MAP, -1);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(BETRAYER_UMBAR_REPORT, 1);
			st.addRadar(-74490, 83275, -3374);
			st.setCond(3);
		}
		else if (event.equalsIgnoreCase("to_Schuttgart"))
		{
			htmltext = "prefect_karukia_q0414_07b.htm";
			st.takeItems(KURUKA_RATMAN_TOOTH, -1);
			st.takeItems(GOBLIN_DWELLING_MAP, -1);
			st.addRadar(90000, -143286, -1520);
			st.playSound(SOUND_MIDDLE);
			st.setCond(5);
		}
		else if (event.equalsIgnoreCase("prefect_tazar_q0414_02.htm"))
		{
			st.addRadar(57502, -117576, -3700);
			st.setCond(6);
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
		int playerClassID = st.getPlayer().getClassId().getId();
		int playerLvl = st.getPlayer().getLevel();
		if (npcId == KARUKIA)
		{
			if (cond < 1)
			{
				if (playerLvl >= 18 && playerClassID == 0x2c && st.getQuestItemsCount(MARK_OF_RAIDER) == 0 && st.getQuestItemsCount(GOBLIN_DWELLING_MAP) == 0)
				{
					htmltext = "prefect_karukia_q0414_01.htm";
				}
				else if (playerClassID != 0x2c)
				{
					if (playerClassID == 0x2d)
					{
						htmltext = "prefect_karukia_q0414_02a.htm";
					}
					else
					{
						htmltext = "prefect_karukia_q0414_03.htm";
					}
				}
				else if (playerLvl < 18 && playerClassID == 0x2c)
				{
					htmltext = "prefect_karukia_q0414_02.htm";
				}
				else if (playerLvl >= 18 && playerClassID == 0x2c && st.getQuestItemsCount(MARK_OF_RAIDER) > 0)
				{
					htmltext = "prefect_karukia_q0414_04.htm";
				}
				else
				{
					htmltext = "prefect_karukia_q0414_02.htm";
				}
			}
			else if (cond == 1 && st.getQuestItemsCount(GOBLIN_DWELLING_MAP) > 0 && st.getQuestItemsCount(KURUKA_RATMAN_TOOTH) < 10)
			{
				htmltext = "prefect_karukia_q0414_06.htm";
			}
			else if (cond == 2 && st.getQuestItemsCount(GOBLIN_DWELLING_MAP) > 0 && st.getQuestItemsCount(KURUKA_RATMAN_TOOTH) > 9)
			{
				htmltext = "prefect_karukia_q0414_07.htm";
			}
			else if (cond == 3 && st.getQuestItemsCount(BETRAYER_UMBAR_REPORT) > 0 && st.getQuestItemsCount(HEAD_OF_BETRAYER) < 2)
			{
				htmltext = "prefect_karukia_q0414_08.htm";
			}
			else if (cond == 4 && st.getQuestItemsCount(BETRAYER_UMBAR_REPORT) > 0 && st.getQuestItemsCount(HEAD_OF_BETRAYER) == 2)
			{
				htmltext = "prefect_karukia_q0414_09.htm";
			}
		}
		else if (npcId == KASMAN && cond > 0)
		{
			if (cond == 3 && st.getQuestItemsCount(BETRAYER_UMBAR_REPORT) > 0 && st.getQuestItemsCount(HEAD_OF_BETRAYER) < 1)
			{
				htmltext = "prefect_kasman_q0414_01.htm";
			}
			else if (cond == 3 && st.getQuestItemsCount(HEAD_OF_BETRAYER) > 0 && st.getQuestItemsCount(HEAD_OF_BETRAYER) < 2)
			{
				htmltext = "prefect_kasman_q0414_02.htm";
			}
			else if (cond == 4 && st.getQuestItemsCount(HEAD_OF_BETRAYER) > 1)
			{
				htmltext = "prefect_kasman_q0414_03.htm";
				st.exitCurrentQuest(true);
				if (st.getPlayer().getClassId().getLevel() == 1)
				{
					st.giveItems(MARK_OF_RAIDER, 1);
					if (!st.getPlayer().getVarB("prof1"))
					{
						st.getPlayer().setVar("prof1", "1", -1);
						st.addExpAndSp(228064, 16455);
						// FIXME [G1ta0] дать адены, только если первый чар на акке
						st.giveItems(ADENA_ID, 81900);
					}
				}
				st.playSound(SOUND_FINISH);
			}
		}
		else if (npcId == TAZEER)
		{
			if (cond == 5)
			{
				htmltext = "prefect_tazar_q0414_01b.htm";
			}
			else if (cond == 6 && st.getQuestItemsCount(TIMORA_ORCS_HEAD) < 1)
			{
				htmltext = "prefect_tazar_q0414_03.htm";
			}
			else if (cond == 7 && st.getQuestItemsCount(TIMORA_ORCS_HEAD) > 0)
			{
				htmltext = "prefect_tazar_q0414_05.htm";
				st.exitCurrentQuest(true);
				if (st.getPlayer().getClassId().getLevel() == 1)
				{
					st.giveItems(MARK_OF_RAIDER, 1);
					if (!st.getPlayer().getVarB("prof1"))
					{
						st.getPlayer().setVar("prof1", "1", -1);
						st.addExpAndSp(228064, 16455);
						// FIXME [G1ta0] дать адены, только если первый чар на акке
						st.giveItems(ADENA_ID, 81900);
					}
				}
				st.playSound(SOUND_FINISH);
			}
		}
		return htmltext;
		/*
		 * 1 Defeat Ratman Leader Quest that must be fulfilled to change occupation to Orc Raider. Prefect Karukia says that Orc Raiders must prove that their courage and loyalty are
		 * without fault. To prove your courage you must destroy the Goblins and their Kuruka Ratmen helpers that are ruining this land. Kill Goblin Tomb Raider Leaders and Kuruka
		 * Ratman Leaders.\n
		 * 2 Return to Prefect Karukia You have killed all the Kuruka Ratman Leaders. Now, return to Prefect Karukia of Orc Fortress.\n
		 * 3 Kill the Betrayers! Prefect Karukia orders you to kill two traitors who betrayed their tribe and went into hiding with Umbar tribe. Take their heads and go to Prefect
		 * Kasman of Gludin Village. Read the betrayer's report and by using the information in it, slay the Orc betrayers!\n
		 * 4 Visit Prefect Kasman You have slain the betrayerous Orc who hid out with the Umbar tribe. Take the head to Prefect Kasman in Gludin Village.\n
		 * 5 Toward the Town of Schuttgart Go to Prefect Tazeer of Schuttgart. He will advise you on what to do.\n
		 * 6 Defeat the Timora Orc! Prefect Tazeer tells you to kill the Orc, Timora, the betrayer, who is hiding among the Ragna Orcs. Return with the traitor's head.\n
		 * 7 Return to Tazeer You have claimed the Orc Timora's head. Return to Prefect Tazeer in the Town of Schuttgart.\n
		 */
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == GOBLIN_TOMB_RAIDER_LEADER && cond == 1)
		{
			if (st.getQuestItemsCount(GOBLIN_DWELLING_MAP) == 1 && st.getQuestItemsCount(KURUKA_RATMAN_TOOTH) < 10 && st.getQuestItemsCount(GREEN_BLOOD) < 40)
			{
				if (st.getQuestItemsCount(GREEN_BLOOD) > 20 && Rnd.chance((st.getQuestItemsCount(GREEN_BLOOD) - 20) * 5))
				{
					st.takeItems(GREEN_BLOOD, -1);
					st.addSpawn(27045);
				}
				else
				{
					st.giveItems(GREEN_BLOOD, 1);
					st.playSound(SOUND_ITEMGET);
				}
			}
		}
		else if (npcId == KURUKA_RATMAN_LEADER && cond == 1)
		{
			if (st.getQuestItemsCount(GOBLIN_DWELLING_MAP) > 0 && st.getQuestItemsCount(KURUKA_RATMAN_TOOTH) < 10)
			{
				st.giveItems(KURUKA_RATMAN_TOOTH, 1);
				if (st.getQuestItemsCount(KURUKA_RATMAN_TOOTH) > 9)
				{
					st.setCond(2);
					st.playSound(SOUND_MIDDLE);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
		}
		else if (npcId == UMBAR_ORC && cond == 3)
		{
			if (st.getQuestItemsCount(BETRAYER_UMBAR_REPORT) > 0 && st.getQuestItemsCount(HEAD_OF_BETRAYER) < 2)
			{
				st.giveItems(HEAD_OF_BETRAYER, 1);
				if (st.getQuestItemsCount(HEAD_OF_BETRAYER) > 1)
				{
					st.setCond(4);
					st.addRadar(-80450, 153410, -3175);
					st.playSound(SOUND_MIDDLE);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
		}
		else if (npcId == TIMORA_ORC && cond == 6)
		{
			if (st.getQuestItemsCount(TIMORA_ORCS_HEAD) < 1 && Rnd.chance(50))
			{
				st.giveItems(TIMORA_ORCS_HEAD, 1);
				st.addRadar(90000, -143286, -1520);
				st.setCond(7);
				st.playSound(SOUND_MIDDLE);
			}
		}
		return null;
	}
}