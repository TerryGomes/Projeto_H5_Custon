package quests;

import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.scripts.ScriptFile;

public class _104_SpiritOfMirror extends Quest implements ScriptFile
{
	static final int GALLINS_OAK_WAND = 748;
	static final int WAND_SPIRITBOUND1 = 1135;
	static final int WAND_SPIRITBOUND2 = 1136;
	static final int WAND_SPIRITBOUND3 = 1137;
	static final int WAND_OF_ADEPT = 747;

	static final SystemMessage2 CACHE_SYSMSG_GALLINS_OAK_WAND = SystemMessage2.removeItems(GALLINS_OAK_WAND, 1);

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

	public _104_SpiritOfMirror()
	{
		super(PARTY_NONE);
		addStartNpc(30017);
		addTalkId(30041, 30043, 30045);
		addKillId(27003, 27004, 27005);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("gallin_q0104_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(GALLINS_OAK_WAND, 3);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == 30017)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getRace() != Race.human)
				{
					htmltext = "gallin_q0104_00.htm";
					st.exitCurrentQuest(true);
				}
				else if (st.getPlayer().getLevel() >= 10)
				{
					htmltext = "gallin_q0104_02.htm";
					return htmltext;
				}
				else
				{
					htmltext = "gallin_q0104_06.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1 && st.getQuestItemsCount(GALLINS_OAK_WAND) >= 1
						&& (st.getQuestItemsCount(WAND_SPIRITBOUND1) == 0 || st.getQuestItemsCount(WAND_SPIRITBOUND2) == 0 || st.getQuestItemsCount(WAND_SPIRITBOUND3) == 0))
			{
				htmltext = "gallin_q0104_04.htm";
			}
			else if (cond == 3 && st.getQuestItemsCount(WAND_SPIRITBOUND1) >= 1 && st.getQuestItemsCount(WAND_SPIRITBOUND2) >= 1 && st.getQuestItemsCount(WAND_SPIRITBOUND3) >= 1)
			{
				st.takeAllItems(WAND_SPIRITBOUND1, WAND_SPIRITBOUND2, WAND_SPIRITBOUND3);

				st.giveItems(WAND_OF_ADEPT, 1);
				st.giveItems(ADENA_ID, 16866, false);
				st.getPlayer().addExpAndSp(39750, 3407);

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

				htmltext = "gallin_q0104_05.htm";
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
			}
		}
		else if ((npcId == 30041 || npcId == 30043 || npcId == 30045) && cond == 1)
		{
			if (npcId == 30041 && st.getInt("id1") == 0)
			{
				st.set("id1", "1");
			}
			htmltext = "arnold_q0104_01.htm";
			if (npcId == 30043 && st.getInt("id2") == 0)
			{
				st.set("id2", "1");
			}
			htmltext = "johnson_q0104_01.htm";
			if (npcId == 30045 && st.getInt("id3") == 0)
			{
				st.set("id3", "1");
			}
			htmltext = "ken_q0104_01.htm";
			if (st.getInt("id1") + st.getInt("id2") + st.getInt("id3") == 3)
			{
				st.setCond(2);
				st.unset("id1");
				st.unset("id2");
				st.unset("id3");
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();

		if ((cond == 1 || cond == 2) && st.getPlayer().getActiveWeaponInstance() != null && st.getPlayer().getActiveWeaponInstance().getItemId() == GALLINS_OAK_WAND)
		{
			ItemInstance weapon = st.getPlayer().getActiveWeaponInstance();
			if (npcId == 27003 && st.getQuestItemsCount(WAND_SPIRITBOUND1) == 0)
			{
				if (st.getPlayer().getInventory().destroyItem(weapon, 1L, "_104_SpiritOfMirror"))
				{
					st.giveItems(WAND_SPIRITBOUND1, 1);
					st.getPlayer().sendPacket(CACHE_SYSMSG_GALLINS_OAK_WAND);
					long Collect = st.getQuestItemsCount(WAND_SPIRITBOUND1) + st.getQuestItemsCount(WAND_SPIRITBOUND2) + st.getQuestItemsCount(WAND_SPIRITBOUND3);
					if (Collect == 3)
					{
						st.setCond(3);
						st.playSound(SOUND_MIDDLE);
					}
					else
					{
						st.playSound(SOUND_ITEMGET);
					}
				}
			}
			else if (npcId == 27004 && st.getQuestItemsCount(WAND_SPIRITBOUND2) == 0)
			{
				if (st.getPlayer().getInventory().destroyItem(weapon, 1L, "_104_SpiritOfMirror"))
				{
					st.giveItems(WAND_SPIRITBOUND2, 1);
					st.getPlayer().sendPacket(CACHE_SYSMSG_GALLINS_OAK_WAND);
					long Collect = st.getQuestItemsCount(WAND_SPIRITBOUND1) + st.getQuestItemsCount(WAND_SPIRITBOUND2) + st.getQuestItemsCount(WAND_SPIRITBOUND3);
					if (Collect == 3)
					{
						st.setCond(3);
						st.playSound(SOUND_MIDDLE);
					}
					else
					{
						st.playSound(SOUND_ITEMGET);
					}
				}
			}
			else if (npcId == 27005 && st.getQuestItemsCount(WAND_SPIRITBOUND3) == 0)
			{
				if (st.getPlayer().getInventory().destroyItem(weapon, 1L, "_104_SpiritOfMirror"))
				{
					st.giveItems(WAND_SPIRITBOUND3, 1);
					st.getPlayer().sendPacket(CACHE_SYSMSG_GALLINS_OAK_WAND);
					long Collect = st.getQuestItemsCount(WAND_SPIRITBOUND1) + st.getQuestItemsCount(WAND_SPIRITBOUND2) + st.getQuestItemsCount(WAND_SPIRITBOUND3);
					if (Collect == 3)
					{
						st.setCond(3);
						st.playSound(SOUND_MIDDLE);
					}
					else
					{
						st.playSound(SOUND_ITEMGET);
					}
				}
			}
		}
		return null;
	}
}