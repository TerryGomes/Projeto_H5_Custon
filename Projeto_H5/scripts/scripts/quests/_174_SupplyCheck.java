package quests;

import l2mv.gameserver.model.base.ClassId;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2mv.gameserver.scripts.ScriptFile;

public class _174_SupplyCheck extends Quest implements ScriptFile
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

	int Marcela = 32173;
	int Benis = 32170; // warehouse keeper
	int Nika = 32167; // grocerer
	// int Erinu = 32164; // weapon seller
	// int Casca = 32139; // vice hierarch

	int WarehouseManifest = 9792;
	int GroceryStoreManifest = 9793;
	// int WeaponShopManifest = 9794;
	// int SupplyReport = 9795;

	int WoodenBreastplate = 23;
	int WoodenGaiters = 2386;
	int LeatherTunic = 429;
	int LeatherStockings = 464;
	int WoodenHelmet = 43;
	int LeatherShoes = 37;
	int Gloves = 49;

	public _174_SupplyCheck()
	{
		super(false);

		addStartNpc(Marcela);
		addTalkId(Benis, Nika); // Erinu, Casca
		addQuestItem(WarehouseManifest, GroceryStoreManifest); // WeaponShopManifest, SupplyReport
	}

	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("zerstorer_morsell_q0174_04.htm"))
		{
			qs.setCond(1);
			qs.setState(STARTED);
			qs.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if (npcId == Marcela)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() == 1)
				{
					st.exitCurrentQuest(true);
					htmltext = "zerstorer_morsell_q0174_02.htm";
				}
				else
				{
					htmltext = "zerstorer_morsell_q0174_01.htm";
				}
				break;
			case 1:
				htmltext = "zerstorer_morsell_q0174_05.htm";
				break;
			case 2:
				st.setCond(3);
				st.takeItems(WarehouseManifest, -1);
				htmltext = "zerstorer_morsell_q0174_06.htm";
				break;
			case 3:
				htmltext = "zerstorer_morsell_q0174_07.htm";
				break;
			case 4:
				if (st.getPlayer().getClassId().isMage() && !st.getPlayer().getClassId().equalsOrChildOf(ClassId.orcMage))
				{
					st.giveItems(LeatherTunic, 1);
					st.giveItems(LeatherStockings, 1);
				}
				else
				{
					st.giveItems(WoodenBreastplate, 1);
					st.giveItems(WoodenGaiters, 1);
				}
				st.giveItems(WoodenHelmet, 1);
				st.giveItems(LeatherShoes, 1);
				st.giveItems(Gloves, 1);
				st.giveItems(ADENA_ID, 2466, true);
				st.getPlayer().addExpAndSp(5672, 446);
				if (st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("ng1"))
				{
					st.getPlayer().sendPacket(new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
				}
				st.exitCurrentQuest(false);
				htmltext = "zerstorer_morsell_q0174_12.htm";
				break;
			default:
				break;
			}
			/*
			 * {
			 * st.setCond(5);
			 * st.takeItems(GroceryStoreManifest, -1);
			 * htmltext = "zerstorer_morsell_q0174_08.htm";
			 * }
			 * else if (cond == 5)
			 * htmltext = "zerstorer_morsell_q0174_09.htm";
			 * else if (cond == 6)
			 * {
			 * st.setCond(7);
			 * st.takeItems(WeaponShopManifest, -1);
			 * st.giveItems(SupplyReport, 1);
			 * htmltext = "zerstorer_morsell_q0174_10.htm";
			 * }
			 * else if (cond == 7)
			 * htmltext = "zerstorer_morsell_q0174_11.htm";
			 * else if (cond == 8)
			 * {
			 * if (st.getPlayer().getClassId().isMage() && !st.getPlayer().getClassId().equalsOrChildOf(ClassId.orcMage))
			 * {
			 * st.giveItems(LeatherTunic, 1);
			 * st.giveItems(LeatherStockings, 1);
			 * }
			 * else
			 * {
			 * st.giveItems(WoodenBreastplate, 1);
			 * st.giveItems(WoodenGaiters, 1);
			 * }
			 * st.giveItems(WoodenHelmet, 1);
			 * st.giveItems(LeatherShoes, 1);
			 * st.giveItems(Gloves, 1);
			 * st.giveItems(ADENA_ID, 2466, true);
			 * st.getPlayer().addExpAndSp(5672, 446, false, false);
			 * if (st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("ng1"))
			 * st.getPlayer().sendPacket(new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
			 * st.exitCurrentQuest(false);
			 * htmltext = "zerstorer_morsell_q0174_12.htm";
			 * }
			 */
		}

		else if (npcId == Benis)
		{
			if (cond == 1)
			{
				st.setCond(2);
				st.giveItems(WarehouseManifest, 1);
				htmltext = "warehouse_keeper_benis_q0174_01.htm";
			}
			else
			{
				htmltext = "warehouse_keeper_benis_q0174_02.htm";
			}
		}
		else if (npcId == Nika)
		{
			if (cond < 3)
			{
				htmltext = "subelder_casca_q0174_01.htm";
			}
			else if (cond == 3)
			{
				st.setCond(4);
				st.giveItems(GroceryStoreManifest, 1);
				htmltext = "trader_neagel_q0174_02.htm";
			}
			else
			{
				htmltext = "trader_neagel_q0174_03.htm";
			}
		}
		/*
		 * else if (npcId == Erinu)
		 * if (cond < 5)
		 * htmltext = "subelder_casca_q0174_01.htm";
		 * else if (cond == 5)
		 * {
		 * st.setCond(6);
		 * st.giveItems(WeaponShopManifest, 1);
		 * htmltext = "trader_erinu_q0174_02.htm";
		 * }
		 * else
		 * htmltext = "subelder_casca_q0174_03.htm";
		 * else if (npcId == Casca)
		 * if (cond < 7)
		 * htmltext = "subelder_casca_q0174_01.htm";
		 * else if (cond == 7)
		 * {
		 * st.setCond(8);
		 * st.takeItems(SupplyReport, -1);
		 * htmltext = "subelder_casca_q0174_02.htm";
		 * }
		 * else
		 * htmltext = "subelder_casca_q0174_03.htm";
		 */
		return htmltext;
	}
}