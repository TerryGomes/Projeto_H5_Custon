package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2mv.gameserver.scripts.ScriptFile;

public class _103_SpiritOfCraftsman extends Quest implements ScriptFile
{
	public final int KAROYDS_LETTER_ID = 968;
	public final int CECKTINONS_VOUCHER1_ID = 969;
	public final int CECKTINONS_VOUCHER2_ID = 970;
	public final int BONE_FRAGMENT1_ID = 1107;
	public final int SOUL_CATCHER_ID = 971;
	public final int PRESERVE_OIL_ID = 972;
	public final int ZOMBIE_HEAD_ID = 973;
	public final int STEELBENDERS_HEAD_ID = 974;
	public final int BLOODSABER_ID = 975;

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

	public _103_SpiritOfCraftsman()
	{
		super(false);

		addStartNpc(30307);

		addTalkId(30132);
		addTalkId(30144);

		addKillId(20015);
		addKillId(20020);
		addKillId(20455);
		addKillId(20517);
		addKillId(20518);

		addQuestItem(KAROYDS_LETTER_ID, CECKTINONS_VOUCHER1_ID, CECKTINONS_VOUCHER2_ID, BONE_FRAGMENT1_ID, SOUL_CATCHER_ID, PRESERVE_OIL_ID, ZOMBIE_HEAD_ID, STEELBENDERS_HEAD_ID);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("blacksmith_karoyd_q0103_05.htm"))
		{
			st.giveItems(KAROYDS_LETTER_ID, 1);
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
		if (id == CREATED)
		{
			st.setCond(0);
		}
		if (npcId == 30307 && st.getCond() == 0)
		{
			if (st.getPlayer().getRace() != Race.darkelf)
			{
				htmltext = "blacksmith_karoyd_q0103_00.htm";
			}
			else if (st.getPlayer().getLevel() >= 10)
			{
				htmltext = "blacksmith_karoyd_q0103_03.htm";
				return htmltext;
			}
			else
			{
				htmltext = "blacksmith_karoyd_q0103_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (npcId == 30307 && st.getCond() == 0)
		{
			htmltext = "completed";
		}
		else if (id == STARTED)
		{
			if (npcId == 30307 && st.getCond() >= 1 && (st.getQuestItemsCount(KAROYDS_LETTER_ID) >= 1 || st.getQuestItemsCount(CECKTINONS_VOUCHER1_ID) >= 1 || st.getQuestItemsCount(CECKTINONS_VOUCHER2_ID) >= 1))
			{
				htmltext = "blacksmith_karoyd_q0103_06.htm";
			}
			else if (npcId == 30132 && st.getCond() == 1 && st.getQuestItemsCount(KAROYDS_LETTER_ID) == 1)
			{
				htmltext = "cecon_q0103_01.htm";
				st.setCond(2);
				st.takeItems(KAROYDS_LETTER_ID, 1);
				st.giveItems(CECKTINONS_VOUCHER1_ID, 1);
			}
			else if (npcId == 30132 && st.getCond() >= 2 && (st.getQuestItemsCount(CECKTINONS_VOUCHER1_ID) >= 1 || st.getQuestItemsCount(CECKTINONS_VOUCHER2_ID) >= 1))
			{
				htmltext = "cecon_q0103_02.htm";
			}
			else if (npcId == 30144 && st.getCond() == 2 && st.getQuestItemsCount(CECKTINONS_VOUCHER1_ID) >= 1)
			{
				htmltext = "harne_q0103_01.htm";
				st.setCond(3);
				st.takeItems(CECKTINONS_VOUCHER1_ID, 1);
				st.giveItems(CECKTINONS_VOUCHER2_ID, 1);
			}
			else if (npcId == 30144 && st.getCond() == 3 && st.getQuestItemsCount(CECKTINONS_VOUCHER2_ID) >= 1 && st.getQuestItemsCount(BONE_FRAGMENT1_ID) < 10)
			{
				htmltext = "harne_q0103_02.htm";
			}
			else if (npcId == 30144 && st.getCond() == 4 && st.getQuestItemsCount(CECKTINONS_VOUCHER2_ID) == 1 && st.getQuestItemsCount(BONE_FRAGMENT1_ID) >= 10)
			{
				htmltext = "harne_q0103_03.htm";
				st.setCond(5);
				st.takeItems(CECKTINONS_VOUCHER2_ID, 1);
				st.takeItems(BONE_FRAGMENT1_ID, 10);
				st.giveItems(SOUL_CATCHER_ID, 1);
			}
			else if (npcId == 30144 && st.getCond() == 5 && st.getQuestItemsCount(SOUL_CATCHER_ID) == 1)
			{
				htmltext = "harne_q0103_04.htm";
			}
			else if (npcId == 30132 && st.getCond() == 5 && st.getQuestItemsCount(SOUL_CATCHER_ID) == 1)
			{
				htmltext = "cecon_q0103_03.htm";
				st.setCond(6);
				st.takeItems(SOUL_CATCHER_ID, 1);
				st.giveItems(PRESERVE_OIL_ID, 1);
			}
			else if (npcId == 30132 && st.getCond() == 6 && st.getQuestItemsCount(PRESERVE_OIL_ID) == 1 && st.getQuestItemsCount(ZOMBIE_HEAD_ID) == 0 && st.getQuestItemsCount(STEELBENDERS_HEAD_ID) == 0)
			{
				htmltext = "cecon_q0103_04.htm";
			}
			else if (npcId == 30132 && st.getCond() == 7 && st.getQuestItemsCount(ZOMBIE_HEAD_ID) == 1)
			{
				htmltext = "cecon_q0103_05.htm";
				st.setCond(8);
				st.takeItems(ZOMBIE_HEAD_ID, 1);
				st.giveItems(STEELBENDERS_HEAD_ID, 1);
			}
			else if (npcId == 30132 && st.getCond() == 8 && st.getQuestItemsCount(STEELBENDERS_HEAD_ID) == 1)
			{
				htmltext = "cecon_q0103_06.htm";
			}
			else if (npcId == 30307 && st.getCond() == 8 && st.getQuestItemsCount(STEELBENDERS_HEAD_ID) == 1)
			{
				htmltext = "blacksmith_karoyd_q0103_07.htm";
				st.takeItems(STEELBENDERS_HEAD_ID, 1);

				st.giveItems(BLOODSABER_ID, 1);
				st.giveItems(ADENA_ID, 19799, false);
				st.getPlayer().addExpAndSp(46663, 3999);

				if (st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("p1q3"))
				{
					st.getPlayer().setVar("p1q3", "1", -1); // flag for helper
					st.getPlayer().sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
					st.giveItems(1060, 100); // healing potion
					for (int item = 4412; item <= 4417; item++)
					{
						st.giveItems(item, 10); // echo cry
					}
					if (st.getPlayer().getClassId().isMage())
					{
						st.playTutorialVoice("tutorial_voice_027");
						st.giveItems(5790, 3000); // newbie sps
					}
					else
					{
						st.playTutorialVoice("tutorial_voice_026");
						st.giveItems(5789, 6000); // newbie ss
					}
				}

				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if ((npcId == 20517 || npcId == 20518 || npcId == 20455) && st.getCond() == 3)
		{
			if (st.getQuestItemsCount(CECKTINONS_VOUCHER2_ID) == 1 && st.getQuestItemsCount(BONE_FRAGMENT1_ID) < 10)
			{
				if (Rnd.chance(33))
				{
					st.giveItems(BONE_FRAGMENT1_ID, 1);
					if (st.getQuestItemsCount(BONE_FRAGMENT1_ID) == 10)
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
		}
		else if ((npcId == 20015 || npcId == 20020) && st.getCond() == 6)
		{
			if (st.getQuestItemsCount(PRESERVE_OIL_ID) == 1)
			{
				if (Rnd.chance(33))
				{
					st.giveItems(ZOMBIE_HEAD_ID, 1);
					st.playSound(SOUND_MIDDLE);
					st.takeItems(PRESERVE_OIL_ID, 1);
					st.setCond(7);
				}
			}
		}

		return null;
	}
}
