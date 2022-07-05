package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2f.gameserver.scripts.ScriptFile;

public class _105_SkirmishWithOrcs extends Quest implements ScriptFile
{
	// NPC
	private static final int Kendell = 30218;
	// QuestItem
	private static final int Kendells1stOrder = 1836;
	private static final int Kendells2stOrder = 1837;
	private static final int Kendells3stOrder = 1838;
	private static final int Kendells4stOrder = 1839;
	private static final int Kendells5stOrder = 1840;
	private static final int Kendells6stOrder = 1841;
	private static final int Kendells7stOrder = 1842;
	private static final int Kendells8stOrder = 1843;
	private static final int KabooChiefs1stTorque = 1844;
	private static final int KabooChiefs2stTorque = 1845;
	private static final int RED_SUNSET_SWORD = 981;
	private static final int RED_SUNSET_STAFF = 754;
	// Item
	// NPC
	private static final int KabooChiefUoph = 27059;
	private static final int KabooChiefKracha = 27060;
	private static final int KabooChiefBatoh = 27061;
	private static final int KabooChiefTanukia = 27062;
	private static final int KabooChiefTurel = 27064;
	private static final int KabooChiefRoko = 27065;
	private static final int KabooChiefKamut = 27067;
	private static final int KabooChiefMurtika = 27068;

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

	public _105_SkirmishWithOrcs()
	{
		super(false);

		addStartNpc(Kendell);

		addKillId(KabooChiefUoph);
		addKillId(KabooChiefKracha);
		addKillId(KabooChiefBatoh);
		addKillId(KabooChiefTanukia);
		addKillId(KabooChiefTurel);
		addKillId(KabooChiefRoko);
		addKillId(KabooChiefKamut);
		addKillId(KabooChiefMurtika);

		addQuestItem(new int[]
		{
			Kendells1stOrder,
			Kendells2stOrder,
			Kendells3stOrder,
			Kendells4stOrder,
			Kendells5stOrder,
			Kendells6stOrder,
			Kendells7stOrder,
			Kendells8stOrder,
			KabooChiefs1stTorque,
			KabooChiefs2stTorque
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("sentinel_kendnell_q0105_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			if (st.getQuestItemsCount(Kendells1stOrder) + st.getQuestItemsCount(Kendells2stOrder) + st.getQuestItemsCount(Kendells3stOrder) + st.getQuestItemsCount(Kendells4stOrder) == 0)
			{
				int n = Rnd.get(4);
				switch (n)
				{
				case 0:
					st.giveItems(Kendells1stOrder, 1, false);
					break;
				case 1:
					st.giveItems(Kendells2stOrder, 1, false);
					break;
				case 2:
					st.giveItems(Kendells3stOrder, 1, false);
					break;
				default:
					st.giveItems(Kendells4stOrder, 1, false);
					break;
				}
			}
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if (cond == 0)
		{
			if (st.getPlayer().getRace() != Race.elf)
			{
				htmltext = "sentinel_kendnell_q0105_00.htm";
				st.exitCurrentQuest(true);
			}
			else if (st.getPlayer().getLevel() < 10)
			{
				htmltext = "sentinel_kendnell_q0105_10.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "sentinel_kendnell_q0105_02.htm";
			}
		}
		else if (cond == 1 && st.getQuestItemsCount(Kendells1stOrder) + st.getQuestItemsCount(Kendells2stOrder) + st.getQuestItemsCount(Kendells3stOrder) + st.getQuestItemsCount(Kendells4stOrder) != 0)
		{
			htmltext = "sentinel_kendnell_q0105_05.htm";
		}
		else if (cond == 2 && st.getQuestItemsCount(KabooChiefs1stTorque) != 0)
		{
			htmltext = "sentinel_kendnell_q0105_06.htm";
			st.takeItems(Kendells1stOrder, -1);
			st.takeItems(Kendells2stOrder, -1);
			st.takeItems(Kendells3stOrder, -1);
			st.takeItems(Kendells4stOrder, -1);
			st.takeItems(KabooChiefs1stTorque, 1);
			int n = Rnd.get(4);
			switch (n)
			{
			case 0:
				st.giveItems(Kendells5stOrder, 1, false);
				break;
			case 1:
				st.giveItems(Kendells6stOrder, 1, false);
				break;
			case 2:
				st.giveItems(Kendells7stOrder, 1, false);
				break;
			default:
				st.giveItems(Kendells8stOrder, 1, false);
				break;
			}
			st.setCond(3);
			st.setState(STARTED);
		}
		else if (cond == 3 && st.getQuestItemsCount(Kendells5stOrder) + st.getQuestItemsCount(Kendells6stOrder) + st.getQuestItemsCount(Kendells7stOrder) + st.getQuestItemsCount(Kendells8stOrder) == 1)
		{
			htmltext = "sentinel_kendnell_q0105_07.htm";
		}
		else if (cond == 4 && st.getQuestItemsCount(KabooChiefs2stTorque) > 0)
		{
			htmltext = "sentinel_kendnell_q0105_08.htm";
			st.takeItems(Kendells5stOrder, -1);
			st.takeItems(Kendells6stOrder, -1);
			st.takeItems(Kendells7stOrder, -1);
			st.takeItems(Kendells8stOrder, -1);
			st.takeItems(KabooChiefs2stTorque, -1);

			if (st.getPlayer().getClassId().isMage())
			{
				st.giveItems(RED_SUNSET_STAFF, 1, false);
			}
			else
			{
				st.giveItems(RED_SUNSET_SWORD, 1, false);
			}

			st.giveItems(ADENA_ID, 17599, false);
			st.getPlayer().addExpAndSp(41478, 3555);

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
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (cond == 1 && st.getQuestItemsCount(KabooChiefs1stTorque) == 0)
		{
			if (npcId == KabooChiefUoph && st.getQuestItemsCount(Kendells1stOrder) > 0)
			{
				st.giveItems(KabooChiefs1stTorque, 1, false);
			}
			else if (npcId == KabooChiefKracha && st.getQuestItemsCount(Kendells2stOrder) > 0)
			{
				st.giveItems(KabooChiefs1stTorque, 1, false);
			}
			else if (npcId == KabooChiefBatoh && st.getQuestItemsCount(Kendells3stOrder) > 0)
			{
				st.giveItems(KabooChiefs1stTorque, 1, false);
			}
			else if (npcId == KabooChiefTanukia && st.getQuestItemsCount(Kendells4stOrder) > 0)
			{
				st.giveItems(KabooChiefs1stTorque, 1, false);
			}
			if (st.getQuestItemsCount(KabooChiefs1stTorque) > 0)
			{
				st.setCond(2);
				st.setState(STARTED);
				st.playSound(SOUND_MIDDLE);
			}
		}
		else if (cond == 3 && st.getQuestItemsCount(KabooChiefs2stTorque) == 0)
		{
			if (npcId == KabooChiefTurel && st.getQuestItemsCount(Kendells5stOrder) > 0)
			{
				st.giveItems(KabooChiefs2stTorque, 1, false);
			}
			else if (npcId == KabooChiefRoko && st.getQuestItemsCount(Kendells6stOrder) > 0)
			{
				st.giveItems(KabooChiefs2stTorque, 1, false);
			}
			else if (npcId == KabooChiefKamut && st.getQuestItemsCount(Kendells7stOrder) > 0)
			{
				st.giveItems(KabooChiefs2stTorque, 1, false);
			}
			else if (npcId == KabooChiefMurtika && st.getQuestItemsCount(Kendells8stOrder) > 0)
			{
				st.giveItems(KabooChiefs2stTorque, 1, false);
			}
			if (st.getQuestItemsCount(KabooChiefs2stTorque) > 0)
			{
				st.setCond(4);
				st.setState(STARTED);
				st.playSound(SOUND_MIDDLE);
			}
		}
		return null;
	}
}
