//package services;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import l2mv.commons.dao.JdbcEntityState;
//import l2mv.commons.dbutils.DbUtils;
//import l2mv.gameserver.Config;
//import l2mv.gameserver.dao.CharacterDAO;
//import l2mv.gameserver.data.htm.HtmCache;
//import l2mv.gameserver.data.xml.holder.ExchangeItemHolder;
//import l2mv.gameserver.database.DatabaseFactory;
//import l2mv.gameserver.model.GameObjectsStorage;
//import l2mv.gameserver.model.Player;
//import l2mv.gameserver.model.SubClass;
//import l2mv.gameserver.model.base.Element;
//import l2mv.gameserver.model.base.Experience;
//import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
//import l2mv.gameserver.model.entity.olympiad.Olympiad;
//import l2mv.gameserver.model.exchange.Change;
//import l2mv.gameserver.model.exchange.Variant;
//import l2mv.gameserver.model.items.ItemAttributes;
//import l2mv.gameserver.model.items.ItemInstance;
//import l2mv.gameserver.model.items.PcInventory;
//import l2mv.gameserver.model.pledge.Clan;
//import l2mv.gameserver.model.pledge.SubUnit;
//import l2mv.gameserver.network.clientpackets.CharacterCreate;
//import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
//import l2mv.gameserver.network.loginservercon.gspackets.ChangeAccessLevel;
//import l2mv.gameserver.network.serverpackets.InventoryUpdate;
//import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
//import l2mv.gameserver.network.serverpackets.MagicSkillUse;
//import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
//import l2mv.gameserver.network.serverpackets.PledgeShowInfoUpdate;
//import l2mv.gameserver.network.serverpackets.PledgeStatusChanged;
//import l2mv.gameserver.network.serverpackets.SkillList;
//import l2mv.gameserver.network.serverpackets.SystemMessage;
//import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
//import l2mv.gameserver.network.serverpackets.components.SystemMsg;
//import l2mv.gameserver.scripts.Functions;
//import l2mv.gameserver.tables.ClanTable;
//import l2mv.gameserver.templates.item.ItemTemplate;
//import l2mv.gameserver.utils.AutoBan;
//import l2mv.gameserver.utils.ItemFunctions;
//import l2mv.gameserver.utils.Log;
//import l2mv.gameserver.utils.Util;
//
//public class DonateFunction extends Functions
//{
//	public void level_index(String[] paramArrayOfString)
//	{
//		Player localPlayer = getSelf();
//		int i = 10639;
//		long l = 50L;
//		if (localPlayer.getLevel() < 79)
//		{
//			l = 100L;
//		}
//		NpcHtmlMessage localNpcHtmlMessage = new NpcHtmlMessage(0).setFile("scripts/services/DonateFunction/level/index.htm");
//		localNpcHtmlMessage.replace("%price_up%", Util.formatPay(localPlayer, l, i));
//		localNpcHtmlMessage.replace("%sub_up%", Util.formatPay(localPlayer, 35L, i));
//		localPlayer.sendPacket(localNpcHtmlMessage);
//	}
//
//	public void level_up(String[] paramArrayOfString)
//	{
//		Player localPlayer = getSelf();
//		String str = "";
//		if (paramArrayOfString.length > 0)
//		{
//			str = paramArrayOfString[0];
//		}
//		int i = -1;
//		long l = 0L;
//		boolean bool = str.equals("SUB");
//		if (!bool)
//		{
//			i = Config.DONATE_LEVEL_UP[0];
//			if (localPlayer.getLevel() < 79)
//			{
//				l = Config.DONATE_LEVEL_UP[1];
//			}
//			else
//			{
//				l = Config.DONATE_LEVEL_UP[2];
//			}
//		}
//		else
//		{
//			i = Config.DONATE_LEVEL_UP_SUB[0];
//			l = Config.DONATE_LEVEL_UP_SUB[1];
//		}
//		if ((bool) && (localPlayer.getActiveClass().isBase()))
//		{
//			localPlayer.sendMessage("For level up subclass, you must active sub!");
//			return;
//		}
//		if (Util.getPay(localPlayer, i, l, true))
//		{
//			Long localLong = Long.valueOf(Experience.getExpForLevel(bool ? Config.DONATE_LEVEL_UP_SUB[2] : Config.DONATE_LEVEL_UP[3]) - localPlayer.getExp());
//			localPlayer.addExpAndSp(localLong.longValue(), 2147483647L);
//			localPlayer.broadcastPacket(new L2GameServerPacket[]
//			{
//				new MagicSkillUse(localPlayer, localPlayer, 6696, 1, 1000, 0L)
//			});
//		}
//	}
//
//	public void recommends(String[] paramArrayOfString)
//	{
//		Player localPlayer = getSelf();
//		int i = Config.DONATE_RECOMMENDS[2];
//		if (localPlayer.getRecomHave() >= i)
//		{
//			localPlayer.sendMessage("You have already " + Util.formatAdena(i) + " Reccomends!");
//			return;
//		}
//		if (Util.getPay(localPlayer, Config.DONATE_RECOMMENDS[0], Config.DONATE_RECOMMENDS[1], true))
//		{
//			localPlayer.sendMessage("You buy " + Util.formatAdena(i) + " Recommends!");
//			localPlayer.addRecomHave(i);
//			localPlayer.broadcastPacket(new L2GameServerPacket[]
//			{
//				new MagicSkillUse(localPlayer, localPlayer, 6696, 1, 1000, 0L)
//			});
//		}
//	}
//
//	public void clanreputation(String[] paramArrayOfString)
//	{
//		Player localPlayer = getSelf();
//		Clan localClan = localPlayer.getClan();
//		if (localClan == null)
//		{
//			localPlayer.sendMessage("You must be in clan!");
//			return;
//		}
//		if (localClan.getLevel() < 5)
//		{
//			localPlayer.sendMessage("Clan reputation can buy only clan with level 5 or higher!");
//			return;
//		}
//		int i = Config.DONATE_CLAN_REPUTATION[2];
//		if (Util.getPay(localPlayer, Config.DONATE_CLAN_REPUTATION[0], Config.DONATE_CLAN_REPUTATION[1], true))
//		{
//			localPlayer.sendMessage("You buy " + Util.formatAdena(i) + " Clan point!");
//			localClan.incReputation(i, false, "DonateFunction");
//			localPlayer.broadcastPacket(new L2GameServerPacket[]
//			{
//				new MagicSkillUse(localPlayer, localPlayer, 6696, 1, 1000, 0L)
//			});
//		}
//	}
//
//	public void clanlevel(String[] paramArrayOfString)
//	{
//		Player localPlayer = getSelf();
//		int i = Config.DONATE_CLAN_LEVEL_ITEM;
//		Clan localClan = localPlayer.getClan();
//		if (localClan == null)
//		{
//			localPlayer.sendMessage("You must be in clan!");
//			return;
//		}
//		NpcHtmlMessage localNpcHtmlMessage = new NpcHtmlMessage(0).setFile("scripts/services/DonateFunction/level/clan.htm");
//		for (int j = 0; j <= 3; j++)
//		{
//			int k = A(localClan.getLevel() + 1, j);
//			localNpcHtmlMessage.replace("%level_" + j + "%", k == 0 ? "<font color=FF0000>Level is block!</font>" : Util.formatPay(localPlayer, k, i));
//		}
//		localPlayer.sendPacket(localNpcHtmlMessage);
//	}
//
//	private int A(int paramInt1, int paramInt2)
//	{
//		int i = 0;
//		int[] arrayOfInt;
//		switch (paramInt1)
//		{
//			case 9:
//				arrayOfInt = new int[]
//				{
//					0,
//					250,
//					500,
//					750
//				};
//				i = arrayOfInt[paramInt2];
//				break;
//			case 10:
//				arrayOfInt = new int[]
//				{
//					0,
//					0,
//					250,
//					500
//				};
//				i = arrayOfInt[paramInt2];
//				break;
//			case 11:
//				arrayOfInt = new int[]
//				{
//					0,
//					0,
//					0,
//					250
//				};
//				i = arrayOfInt[paramInt2];
//				break;
//			case 12:
//				arrayOfInt = new int[]
//				{
//					0,
//					0,
//					0,
//					0
//				};
//				i = arrayOfInt[paramInt2];
//				break;
//			default:
//				arrayOfInt = new int[]
//				{
//					300,
//					550,
//					800,
//					1050
//				};
//				i = arrayOfInt[paramInt2];
//				break;
//		}
//		return i;
//	}
//
//	public void clanlevelup(String[] paramArrayOfString)
//	{
//		if ((paramArrayOfString.length < 1) || (!Util.isNumber(paramArrayOfString[0])))
//		{
//			return;
//		}
//		Player localPlayer1 = getSelf();
//		Clan localClan = localPlayer1.getClan();
//		if (localClan == null)
//		{
//			return;
//		}
//		int[] arrayOfInt =
//		{
//			8,
//			9,
//			10,
//			11
//		};
//		int i = Integer.parseInt(paramArrayOfString[0]);
//		if ((i < 0) || (i > arrayOfInt.length))
//		{
//			return;
//		}
//		int j = A(localClan.getLevel() + 1, i);
//		if (j == 0)
//		{
//			return;
//		}
//		if (Util.getPay(localPlayer1, Config.DONATE_CLAN_LEVEL_ITEM, j, true))
//		{
//			localClan.setLevel(arrayOfInt[i]);
//			localClan.updateClanInDB();
//			PledgeShowInfoUpdate localPledgeShowInfoUpdate = new PledgeShowInfoUpdate(localClan);
//			PledgeStatusChanged localPledgeStatusChanged = new PledgeStatusChanged(localClan);
//			Iterator localIterator = localClan.getOnlineMembers(0).iterator();
//			while (localIterator.hasNext())
//			{
//				Player localPlayer2 = (Player) localIterator.next();
//				localPlayer2.updatePledgeClass();
//				localPlayer2.sendPacket(new IStaticPacket[]
//				{
//					SystemMsg.YOUR_CLANS_LEVEL_HAS_INCREASED,
//					localPledgeShowInfoUpdate,
//					localPledgeStatusChanged
//				});
//				localPlayer2.broadcastUserInfo(true);
//			}
//		}
//	}
//
//	public void fame(String[] paramArrayOfString)
//	{
//		Player localPlayer = getSelf();
//		int i = localPlayer.getFame();
//		if (i >= Config.LIM_FAME)
//		{
//			localPlayer.sendMessage("You have maximum size of fame!");
//			return;
//		}
//		int j = Config.DONATE_FAME[2];
//		if (Util.getPay(localPlayer, Config.DONATE_FAME[0], Config.DONATE_FAME[1], true))
//		{
//			localPlayer.sendMessage("You buy " + Util.formatAdena(j) + " Fame point!");
//			localPlayer.setFame(i + j, "DonateFunction");
//			localPlayer.broadcastPacket(new L2GameServerPacket[]
//			{
//				new MagicSkillUse(localPlayer, localPlayer, 6696, 1, 1000, 0L)
//			});
//		}
//	}
//
//	public void olf(String[] paramArrayOfString)
//	{
//		Player localPlayer = getSelf();
//		int i = localPlayer.getInventory().getPaperdollItemId(0);
//		int j = 21580;
//		if (i != j)
//		{
//			localPlayer.sendMessage("Olf's T-shirt must be equiped!");
//			return;
//		}
//		NpcHtmlMessage localNpcHtmlMessage = new NpcHtmlMessage(0).setFile("scripts/services/DonateFunction/transfer/olf.htm");
//		localNpcHtmlMessage.replace("%price%", Util.formatPay(localPlayer, Config.SERVICES_OLF_TRANSFER_ITEM[1], Config.SERVICES_OLF_TRANSFER_ITEM[0]));
//		localPlayer.sendPacket(localNpcHtmlMessage);
//	}
//
//	public void olf_send(String[] paramArrayOfString)
//	{
//		if (paramArrayOfString.length < 1)
//		{
//			return;
//		}
//		Player localPlayer1 = getSelf();
//		int i = 21580;
//		ItemInstance localItemInstance1 = localPlayer1.getInventory().getPaperdollItem(0);
//		if ((localItemInstance1 == null) || (localItemInstance1.getItemId() != i))
//		{
//			localPlayer1.sendMessage("Olf's T-shirt must be equiped!");
//			return;
//		}
//		String str = paramArrayOfString[0];
//		Player localPlayer2 = GameObjectsStorage.getPlayer(str);
//		if (localPlayer2 == null)
//		{
//			localPlayer1.sendMessage("Can't find player " + str + " in game!");
//			return;
//		}
//		if ((Util.getPay(localPlayer1, Config.DONATE_TRANSFER_OLF[0], Config.DONATE_TRANSFER_OLF[1], true)) && (localPlayer1.getInventory().destroyItemByObjectId(localItemInstance1.getObjectId(), localItemInstance1.getCount(), "L2CCCP Services: Olf Transfer!")))
//		{
//			PcInventory localPcInventory = localPlayer2.getInventory();
//			ItemInstance localItemInstance2 = ItemFunctions.createItem(i);
//			localItemInstance2.setEnchantLevel(localItemInstance1.getEnchantLevel());
//			localPcInventory.addItem(localItemInstance2, "L2CCCP Services: Olf Transfer!");
//			localItemInstance2.setJdbcState(JdbcEntityState.UPDATED);
//			localItemInstance2.update();
//			if (ItemFunctions.checkIfCanEquip(localPlayer1, localItemInstance2) == null)
//			{
//				localPcInventory.equipItem(localItemInstance2);
//			}
//			localPlayer1.sendMessage("You transfer Olf's T-shirt to player " + localPlayer2.getName());
//			localPlayer2.sendMessage("Player " + localPlayer1.getName() + " send you Olf's T-shirt. (Is Automatically equiped)");
//		}
//	}
//
//	public void cloak(String[] paramArrayOfString)
//	{
//		Player localPlayer = getSelf();
//		int i = localPlayer.getInventory().getPaperdollItemId(13);
//		if (!A(i))
//		{
//			localPlayer.sendMessage("Soul cloak must be equiped!");
//			return;
//		}
//		NpcHtmlMessage localNpcHtmlMessage = new NpcHtmlMessage(0).setFile("scripts/services/DonateFunction/transfer/cloaks.htm");
//		localNpcHtmlMessage.replace("%price%", Util.formatPay(localPlayer, Config.DONATE_TRANSFER_CLOAK[1], Config.DONATE_TRANSFER_CLOAK[0]));
//		localPlayer.sendPacket(localNpcHtmlMessage);
//	}
//
//	public void cloak_send(String[] paramArrayOfString)
//	{
//		if (paramArrayOfString.length < 1)
//		{
//			return;
//		}
//		Player localPlayer1 = getSelf();
//		ItemInstance localItemInstance1 = localPlayer1.getInventory().getPaperdollItem(13);
//		if (!A(localItemInstance1.getItemId()))
//		{
//			localPlayer1.sendMessage("Soul cloak must be equiped!");
//			return;
//		}
//		String str = paramArrayOfString[0];
//		Player localPlayer2 = GameObjectsStorage.getPlayer(str);
//		if (localPlayer2 == null)
//		{
//			localPlayer1.sendMessage("Can't find player " + str + " in game!");
//			return;
//		}
//		if (Util.getPay(localPlayer1, Config.DONATE_TRANSFER_CLOAK[0], Config.DONATE_TRANSFER_CLOAK[1], true))
//		{
//			int i = localItemInstance1.getItemId();
//			if (localPlayer1.getInventory().destroyItemByObjectId(localItemInstance1.getObjectId(), localItemInstance1.getCount(), "L2CCCP Services: Soul Cloak Transfer!"))
//			{
//				PcInventory localPcInventory = localPlayer2.getInventory();
//				ItemInstance localItemInstance2 = ItemFunctions.createItem(i);
//				localItemInstance2.setEnchantLevel(localItemInstance1.getEnchantLevel());
//				localPcInventory.addItem(localItemInstance2, "L2CCCP Services: Soul Cloak Transfer!");
//				localItemInstance2.setJdbcState(JdbcEntityState.UPDATED);
//				localItemInstance2.update();
//				localPlayer1.sendMessage("You transfer " + localItemInstance2.getName() + " to player " + localPlayer2.getName());
//				if (ItemFunctions.checkIfCanEquip(localPlayer1, localItemInstance2) == null)
//				{
//					localPcInventory.equipItem(localItemInstance2);
//					localPlayer2.sendMessage("Player " + localPlayer1.getName() + " send you " + localItemInstance2.getName() + ". (Is Automatically equiped)");
//				}
//				else
//				{
//					localPlayer2.sendMessage("Player " + localPlayer1.getName() + " send you " + localItemInstance2.getName() + ". (Is in inventory)");
//				}
//			}
//		}
//	}
//
//	private boolean A(int paramInt)
//	{
//		int[] arrayOfInt1 =
//		{
//			21719,
//			21720,
//			21721
//		};
//		for (int k : arrayOfInt1)
//		{
//			if (paramInt == k)
//			{
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public void noble(String[] paramArrayOfString)
//	{
//		Player localPlayer = getSelf();
//		if ((localPlayer.getLevel() < 75) && (localPlayer.getActiveClass().isBase()))
//		{
//			localPlayer.sendMessage("You need be over 75 level to purchase noblesse!");
//			return;
//		}
//		if (localPlayer.isNoble())
//		{
//			localPlayer.sendMessage("You are noble!");
//			return;
//		}
//		if (Util.getPay(localPlayer, Config.DONATE_NOBLE[0], Config.DONATE_NOBLE[1], true))
//		{
//			localPlayer.sendMessage("You buy Nobless!");
//			Olympiad.addNoble(localPlayer);
//			localPlayer.setNoble(true);
//			localPlayer.updatePledgeClass();
//			localPlayer.updateNobleSkills();
//			localPlayer.sendPacket(new SkillList(localPlayer));
//			localPlayer.broadcastUserInfo(true);
//			localPlayer.broadcastPacket(new L2GameServerPacket[]
//			{
//				new MagicSkillUse(localPlayer, localPlayer, 6696, 1, 1000, 0L)
//			});
//		}
//	}
//
//	public void rename()
//	{
//		Player localPlayer = getSelf();
//		NpcHtmlMessage localNpcHtmlMessage = new NpcHtmlMessage(0).setFile("scripts/services/DonateFunction/rename/name.htm");
//		localNpcHtmlMessage.replace("%price%", Util.formatPay(localPlayer, Config.DONATE_RENAME[1], Config.DONATE_RENAME[0]));
//		localPlayer.sendPacket(localNpcHtmlMessage);
//	}
//
//	public void rename(String[] paramArrayOfString)
//	{
//		if ((paramArrayOfString != null) && (!paramArrayOfString[0].isEmpty()))
//		{
//			A(paramArrayOfString[0]);
//		}
//	}
//
//	public void rename_clan()
//	{
//		Player localPlayer = getSelf();
//		localPlayer.sendMessage("Clan name must be not empty!");
//	}
//
//	public void rename_clan(String[] paramArrayOfString)
//	{
//		if ((paramArrayOfString != null) && (!paramArrayOfString[0].isEmpty()))
//		{
//			C(paramArrayOfString[0]);
//		}
//	}
//
//	private void C(String paramString)
//	{
//		Player localPlayer = getSelf();
//		if ((localPlayer.getClan() == null) || (!localPlayer.isClanLeader()))
//		{
//			localPlayer.sendPacket(new SystemMessage(SystemMsg.S1_IS_NOT_A_CLAN_LEADER).addName(localPlayer));
//			return;
//		}
//		if (!Util.isMatchingRegexp(paramString, Config.CLAN_NAME_TEMPLATE))
//		{
//			localPlayer.sendMessage("Incorrect name!");
//			return;
//		}
//		if (ClanTable.getInstance().getClanByName(paramString) != null)
//		{
//			localPlayer.sendMessage("This name is used!");
//			return;
//		}
//		if (localPlayer.getEvent(SiegeEvent.class) != null)
//		{
//			localPlayer.sendMessage("You can't change clan name in siege!");
//			return;
//		}
//		if (Util.getPay(localPlayer, Config.DONATE_RENAME_CLAN[0], Config.DONATE_RENAME_CLAN[1], true))
//		{
//			SubUnit localSubUnit = localPlayer.getClan().getSubUnit(0);
//			String str = localSubUnit.getName();
//			localSubUnit.setName(paramString, true);
//			localPlayer.getClan().broadcastClanStatus(true, true, false);
//			Log.add("Clan " + str + " renamed to " + paramString, "NameChange");
//			localPlayer.sendMessage("You change clan name at " + str + " to " + paramString + "!");
//		}
//	}
//
//	private void A(String paramString)
//	{
//		Player localPlayer = getSelf();
//		if ((paramString == null) || (paramString.isEmpty()))
//		{
//			localPlayer.sendMessage("Name is null, try again!");
//			return;
//		}
//		if (localPlayer.isHero())
//		{
//			localPlayer.sendMessage("Hero can't change name!");
//			return;
//		}
//		if (localPlayer.isClanLeader())
//		{
//			localPlayer.sendMessage("Clan leader can't change name!");
//			return;
//		}
//		if (localPlayer.getEvent(SiegeEvent.class) != null)
//		{
//			localPlayer.sendMessage("You can't change name in siege!");
//			return;
//		}
//		if ((!CharacterCreate.checkName(paramString)) && (!Config.SERVICES_CHANGE_NICK_ALLOW_SYMBOL))
//		{
//			localPlayer.sendMessage("Incorrect name!");
//			return;
//		}
//		if (CharacterDAO.getInstance().getObjectIdByName(paramString) > 0)
//		{
//			localPlayer.sendMessage("This name is used!");
//			return;
//		}
//		if (Util.getPay(localPlayer, Config.DONATE_RENAME[0], Config.DONATE_RENAME[1], true))
//		{
//			String str = localPlayer.getName();
//			localPlayer.reName(paramString, true);
//			Log.add("Character " + str + " renamed to " + paramString, "NameChange");
//			localPlayer.sendMessage("You change name at " + str + " to " + paramString + "!");
//		}
//	}
//
//	public void change_page(String[] paramArrayOfString)
//  {
//    Player localPlayer = getSelf();
//    A(localPlayer, true);
//    A(localPlayer, -1);
//    NpcHtmlMessage localNpcHtmlMessage = new NpcHtmlMessage(0).setFile("scripts/services/DonateFunction/exchange/page.htm");
//    String str1 = HtmCache.getInstance().getNotNull("scripts/services/DonateFunction/exchange/template.htm", localPlayer);
//    String str2 = "";
//    String str3 = "";
//    ArrayList localArrayList = new ArrayList();
//    Change localChange;
//    for (ItemInstance localItemInstance : localPlayer.getInventory().getPaperdollItems()) {
//      if (localItemInstance != null)
//      {
//        localChange = ExchangeItemHolder.getInstance().getChanges(localItemInstance.getItemId());
//        if (localChange != null) {
//          localArrayList.add(localChange);
//        }
//      }
//    }
//    int i = 6;
//    ??? = (!paramArrayOfString[0].isEmpty()) && (Util.isNumber(paramArrayOfString[0])) ? Integer.parseInt(paramArrayOfString[0]) : 1;
//    ??? = 0;
//    for (int m = (??? - 1) * 6; m < localArrayList.size(); m++)
//    {
//      localChange = (Change)localArrayList.get(m);
//      str2 = str1;
//      str2 = str2.replace("{bypass}", "bypass -h scripts_services.DonateFunction:change_list " + localChange.getId());
//      str2 = str2.replace("{name}", localChange.getName());
//      str2 = str2.replace("{icon}", localChange.getIcon());
//      str2 = str2.replace("{cost}", "<font color=99CC66>Cost:</font> " + Util.formatPay(localPlayer, localChange.getCostCount(), localChange.getCostId()));
//      str3 = str3 + str2;
//      ???++;
//      if (??? >= 6) {
//        break;
//      }
//    }
//    double d = Math.ceil(localArrayList.size() / 6.0D);
//    int n = 1;
//    String str4 = "";
//    for (int i1 = 1; i1 <= d; i1++)
//    {
//      if (i1 == ???) {
//        str4 = str4 + "<td width=25 align=center valign=top><button value=\"[" + i1 + "]\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
//      } else {
//        str4 = str4 + "<td width=25 align=center valign=top><button value=\"" + i1 + "\" action=\"bypass -h scripts_services.DonateFunction:change_page " + i1 + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
//      }
//      if (n % 7 == 0) {
//        str4 = str4 + "</tr><tr>";
//      }
//      n++;
//    }
//    if (n == 2) {
//      str4 = "<td width=30 align=center valign=top>...</td>";
//    }
//    localNpcHtmlMessage.replace("%list%", str3);
//    localNpcHtmlMessage.replace("%navigation%", str4);
//    localPlayer.sendPacket(localNpcHtmlMessage);
//  }
//
//	public void change_list(String[] paramArrayOfString)
//	{
//		Player localPlayer = getSelf();
//		A(localPlayer, -1);
//		A(localPlayer, true);
//		if ((paramArrayOfString[0].isEmpty()) || (!Util.isNumber(paramArrayOfString[0])))
//		{
//			return;
//		}
//		int i = Integer.parseInt(paramArrayOfString[0]);
//		NpcHtmlMessage localNpcHtmlMessage = new NpcHtmlMessage(0).setFile("scripts/services/DonateFunction/exchange/list.htm");
//		String str1 = HtmCache.getInstance().getNotNull("scripts/services/DonateFunction/exchange/template.htm", localPlayer);
//		String str2 = "";
//		String str3 = "";
//		Change localChange = ExchangeItemHolder.getInstance().getChanges(i);
//		if (localChange == null)
//		{
//			return;
//		}
//		localPlayer.addQuickVar("exchange", Integer.valueOf(i));
//		List localList = localChange.getList();
//		int j = 6;
//		int k = (paramArrayOfString.length > 1) && (!paramArrayOfString[1].isEmpty()) && (Util.isNumber(paramArrayOfString[1])) ? Integer.parseInt(paramArrayOfString[1]) : 1;
//		int m = 0;
//		for (int n = (k - 1) * 6; n < localList.size(); n++)
//		{
//			Variant localVariant = (Variant) localList.get(n);
//			str2 = str1;
//			str2 = str2.replace("{bypass}", "bypass -h scripts_services.DonateFunction:change_open " + localVariant.getNumber());
//			str2 = str2.replace("{name}", localVariant.getName());
//			str2 = str2.replace("{icon}", localVariant.getIcon());
//			str2 = str2.replace("{cost}", "<font color=99CC66>Cost:</font> " + Util.formatPay(localPlayer, localChange.getCostCount(), localChange.getCostId()));
//			str3 = str3 + str2;
//			m++;
//			if (m >= 6)
//			{
//				break;
//			}
//		}
//		double d = Math.ceil(localList.size() / 6.0D);
//		int i1 = 1;
//		String str4 = "";
//		for (int i2 = 1; i2 <= d; i2++)
//		{
//			if (i2 == k)
//			{
//				str4 = str4 + "<td width=25 align=center valign=top><button value=\"[" + i2 + "]\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
//			}
//			else
//			{
//				str4 = str4 + "<td width=25 align=center valign=top><button value=\"" + i2 + "\" action=\"bypass -h scripts_services.DonateFunction:change_list " + i + " " + i2 + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
//			}
//			if (i1 % 7 == 0)
//			{
//				str4 = str4 + "</tr><tr>";
//			}
//			i1++;
//		}
//		if (i1 == 2)
//		{
//			str4 = "<td width=30 align=center valign=top>...</td>";
//		}
//		localNpcHtmlMessage.replace("%list%", str3);
//		localNpcHtmlMessage.replace("%navigation%", str4);
//		localNpcHtmlMessage.replace("%choice%", localChange.getName());
//		localPlayer.sendPacket(localNpcHtmlMessage);
//	}
//
//	public void change_att(String[] paramArrayOfString)
//	{
//		if ((paramArrayOfString[0].isEmpty()) || (!Util.isNumber(paramArrayOfString[0])))
//		{
//			return;
//		}
//		Player localPlayer = getSelf();
//		int i = localPlayer.getQuickVarI("exchange_obj", new int[]
//		{
//			-1
//		});
//		if (i == -1)
//		{
//			return;
//		}
//		ItemInstance localItemInstance = localPlayer.getInventory().getItemByObjectId(i);
//		if (localItemInstance == null)
//		{
//			return;
//		}
//		int j = localPlayer.getQuickVarI("exchange_number", new int[]
//		{
//			-1
//		});
//		if (j == -1)
//		{
//			return;
//		}
//		int k = Integer.parseInt(paramArrayOfString[0]);
//		Element localElement = Element.getElementById(k);
//		if (localElement != Element.NONE)
//		{
//			localPlayer.addQuickVar("ex_att_" + k, Integer.valueOf(localItemInstance.getAttributeElementValue()));
//			localPlayer.addQuickVar("ex_att", Integer.valueOf(k));
//			A(localPlayer, k);
//		}
//		change_open(new String[]
//		{
//			String.valueOf(j)
//		});
//	}
//
//	private void A(Player paramPlayer, int paramInt)
//	{
//		for (Element localElement : Element.VALUES)
//		{
//			if (localElement.getId() != paramInt)
//			{
//				paramPlayer.deleteQuickVar("ex_att_" + localElement.getId());
//			}
//		}
//		if (paramInt == -1)
//		{
//			paramPlayer.deleteQuickVar("ex_att");
//		}
//	}
//
//	public void change_open(String[] paramArrayOfString)
//  {
//    Player localPlayer = getSelf();
//    int i = localPlayer.getQuickVarI("exchange", new int[] { -1 });
//    if ((i == -1) || (paramArrayOfString[0].isEmpty()) || (!Util.isNumber(paramArrayOfString[0]))) {
//      return;
//    }
//    int j = Integer.parseInt(paramArrayOfString[0]);
//    Object localObject1 = null;
//    Change localChange = null;
//    String str;
//    for (str : localPlayer.getInventory().getPaperdollItems()) {
//      if (str != null)
//      {
//        localChange = ExchangeItemHolder.getInstance().getChanges(str.getItemId());
//        if ((localChange != null) && (localChange.getId() == i))
//        {
//          localObject1 = str;
//          break;
//        }
//      }
//    }
//    if (localObject1 == null) {
//      return;
//    }
//    ??? = localChange.getVariant(j);
//    if (??? == null) {
//      return;
//    }
//    A(localPlayer, false);
//    localPlayer.addQuickVar("exchange_obj", Integer.valueOf(((ItemInstance)localObject1).getObjectId()));
//    localPlayer.addQuickVar("exchange_new", Integer.valueOf(((Variant)???).getId()));
//    localPlayer.addQuickVar("exchange_attribute", Boolean.valueOf(localChange.attChange()));
//    if (localChange.attChange()) {
//      localPlayer.addQuickVar("exchange_number", Integer.valueOf(((Variant)???).getNumber()));
//    }
//    NpcHtmlMessage localNpcHtmlMessage = new NpcHtmlMessage(0).setFile("scripts/services/DonateFunction/exchange/general.htm");
//    localNpcHtmlMessage.replace("%my_name%", ((ItemInstance)localObject1).getName());
//    localNpcHtmlMessage.replace("%my_ench%", "+" + ((ItemInstance)localObject1).getEnchantLevel());
//    localNpcHtmlMessage.replace("%my_icon%", ((ItemInstance)localObject1).getTemplate().getIcon());
//    ItemAttributes localItemAttributes = ((ItemInstance)localObject1).getAttributes();
//    if ((!localChange.attChange()) || (((ItemInstance)localObject1).getAttributeElementValue() == 0))
//    {
//      str = HtmCache.getInstance().getNotNull("scripts/services/DonateFunction/exchange/att_info.htm", localPlayer);
//      str = str.replace("%Earth%", String.valueOf(localItemAttributes.getEarth()));
//      str = str.replace("%Fire%", String.valueOf(localItemAttributes.getFire()));
//      str = str.replace("%Holy%", String.valueOf(localItemAttributes.getHoly()));
//      str = str.replace("%Unholy%", String.valueOf(localItemAttributes.getUnholy()));
//      str = str.replace("%Water%", String.valueOf(localItemAttributes.getWater()));
//      str = str.replace("%Wind%", String.valueOf(localItemAttributes.getWind()));
//      localNpcHtmlMessage.replace("%att_info%", str);
//    }
//    else
//    {
//      str = HtmCache.getInstance().getNotNull("scripts/services/DonateFunction/exchange/att_change.htm", localPlayer);
//      if (localPlayer.getQuickVarI("ex_att", new int[] { -1 }) == -1)
//      {
//        str = str.replace("%Earth%", String.valueOf(localItemAttributes.getEarth()));
//        str = str.replace("%Fire%", String.valueOf(localItemAttributes.getFire()));
//        str = str.replace("%Holy%", String.valueOf(localItemAttributes.getHoly()));
//        str = str.replace("%Unholy%", String.valueOf(localItemAttributes.getUnholy()));
//        str = str.replace("%Water%", String.valueOf(localItemAttributes.getWater()));
//        str = str.replace("%Wind%", String.valueOf(localItemAttributes.getWind()));
//      }
//      else
//      {
//        str = str.replace("%Fire%", String.valueOf(localPlayer.getQuickVarI("ex_att_0", new int[] { 0 })));
//        str = str.replace("%Water%", String.valueOf(localPlayer.getQuickVarI("ex_att_1", new int[] { 0 })));
//        str = str.replace("%Wind%", String.valueOf(localPlayer.getQuickVarI("ex_att_2", new int[] { 0 })));
//        str = str.replace("%Earth%", String.valueOf(localPlayer.getQuickVarI("ex_att_3", new int[] { 0 })));
//        str = str.replace("%Holy%", String.valueOf(localPlayer.getQuickVarI("ex_att_4", new int[] { 0 })));
//        str = str.replace("%Unholy%", String.valueOf(localPlayer.getQuickVarI("ex_att_5", new int[] { 0 })));
//      }
//      localNpcHtmlMessage.replace("%att_info%", str);
//    }
//    localNpcHtmlMessage.replace("%cost%", Util.formatPay(localPlayer, localChange.getCostCount(), localChange.getCostId()));
//    localNpcHtmlMessage.replace("%new_name%", ((Variant)???).getName());
//    localNpcHtmlMessage.replace("%new_icon%", ((Variant)???).getIcon());
//    localNpcHtmlMessage.replace("%new_id%", String.valueOf(i));
//    localPlayer.sendPacket(localNpcHtmlMessage);
//  }
//
//	public void exchange()
//	{
//		Player localPlayer = getSelf();
//		int i = localPlayer.getQuickVarI("exchange_obj", new int[]
//		{
//			-1
//		});
//		if (i == -1)
//		{
//			return;
//		}
//		int j = localPlayer.getQuickVarI("exchange_new", new int[]
//		{
//			-1
//		});
//		if (j == -1)
//		{
//			return;
//		}
//		boolean bool = localPlayer.getQuickVarB("exchange_attribute", new boolean[]
//		{
//			false
//		});
//		PcInventory localPcInventory = localPlayer.getInventory();
//		ItemInstance localItemInstance1 = localPcInventory.getItemByObjectId(i);
//		if (localItemInstance1 == null)
//		{
//			return;
//		}
//		Change localChange = ExchangeItemHolder.getInstance().getChanges(j);
//		if ((Util.getPay(localPlayer, localChange.getCostId(), localChange.getCostCount(), true)) && (localPcInventory.destroyItemByObjectId(localItemInstance1.getObjectId(), localItemInstance1.getCount(), "L2CCCP Donate System")))
//		{
//			ItemInstance localItemInstance2 = ItemFunctions.createItem(j);
//			localItemInstance2.setEnchantLevel(localItemInstance1.getEnchantLevel());
//			localItemInstance2.setAugmentationId(localItemInstance1.getAugmentationId());
//			int k = localPlayer.getQuickVarI("ex_att", new int[]
//			{
//				-1
//			});
//			Object localObject;
//			int m;
//			if ((bool) && (k != -1))
//			{
//				localObject = Element.getElementById(k);
//				m = localItemInstance1.getAttributeElementValue();
//				if (m > 0)
//				{
//					localItemInstance2.setAttributeElement((Element) localObject, m);
//				}
//			}
//			else
//			{
//				for (Element localElement : Element.VALUES)
//				{
//					int i1 = localItemInstance1.getAttributes().getValue(localElement);
//					if (i1 > 0)
//					{
//						localItemInstance2.setAttributeElement(localElement, i1);
//					}
//				}
//			}
//			localPcInventory.addItem(localItemInstance2, "L2CCCP Donate System");
//			localItemInstance2.setJdbcState(JdbcEntityState.UPDATED);
//			localItemInstance2.update();
//			if (ItemFunctions.checkIfCanEquip(localPlayer, localItemInstance2) == null)
//			{
//				localPcInventory.equipItem(localItemInstance2);
//			}
//			localPlayer.sendMessage("You exchange item " + localItemInstance1.getName() + " to " + localItemInstance2.getName());
//		}
//		A(localPlayer, true);
//		A(localPlayer, -1);
//	}
//
//	public void olf_manipulation(String[] paramArrayOfString)
//	{
//		Player localPlayer = getSelf();
//		ItemInstance localItemInstance = localPlayer.getInventory().getPaperdollItem(0);
//		int i = 21580;
//		NpcHtmlMessage localNpcHtmlMessage;
//		String str1;
//		String str2;
//		String str3;
//		if ((localItemInstance == null) || (localItemInstance.getItemId() != i))
//		{
//			localNpcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/DonateFunction/olf/buy.htm");
//			str1 = HtmCache.getInstance().getNotNull("scripts/services/DonateFunction/olf/buy_template.htm", localPlayer);
//			str2 = "";
//			str3 = "";
//			for (int j = 0; j < Config.DONATE_OLF_VARIANTS.length; j++)
//			{
//				str2 = str1;
//				str2 = str2.replace("{bypass}", "bypass -h scripts_services.DonateFunction:olf_buy " + j);
//				str2 = str2.replace("{enchant}", String.valueOf(Config.DONATE_OLF_VARIANTS[j]));
//				str2 = str2.replace("{cost}", "<font color=99CC66>Cost:</font> " + Util.formatPay(localPlayer, Config.DONATE_OLF_VARIANTS_PRICE[j], Config.DONATE_OLF_ITEM));
//				str3 = str3 + str2;
//			}
//			localNpcHtmlMessage.replace("%list%", str3);
//			localPlayer.sendPacket(localNpcHtmlMessage);
//		}
//		else
//		{
//			if (localItemInstance.getEnchantLevel() >= Config.DONATE_OLF_ENCHATED[(Config.DONATE_OLF_ENCHATED.length - 1)])
//			{
//				localPlayer.sendMessage("No more option for enchant Olf's Shirt, if you want buy Olf's Shirt, unequip underwear!");
//				return;
//			}
//			localNpcHtmlMessage = new NpcHtmlMessage(0).setFile("scripts/services/DonateFunction/olf/enchant.htm");
//			str1 = HtmCache.getInstance().getNotNull("scripts/services/DonateFunction/olf/enchant_template.htm", localPlayer);
//			str2 = "";
//			str3 = "";
//			int[] arrayOfInt = B(localItemInstance.getEnchantLevel());
//			for (int k = 0; k < arrayOfInt.length; k++)
//			{
//				if (arrayOfInt[k] != -1)
//				{
//					str2 = str1;
//					str2 = str2.replace("{bypass}", "bypass -h scripts_services.DonateFunction:olf_enchant " + k);
//					str2 = str2.replace("{enchant}", String.valueOf(Config.DONATE_OLF_ENCHATED[k]));
//					str2 = str2.replace("{cost}", "<font color=99CC66>Cost:</font> " + Util.formatPay(localPlayer, arrayOfInt[k], Config.DONATE_OLF_ITEM));
//					str3 = str3 + str2;
//				}
//			}
//			localNpcHtmlMessage.replace("%list%", str3);
//			localPlayer.sendPacket(localNpcHtmlMessage);
//		}
//	}
//
//	public void olf_buy(String[] paramArrayOfString)
//	{
//		if ((paramArrayOfString[0].isEmpty()) || (!Util.isNumber(paramArrayOfString[0])))
//		{
//			return;
//		}
//		Player localPlayer = getSelf();
//		int i = Integer.parseInt(paramArrayOfString[0]);
//		if (i >= Config.DONATE_OLF_VARIANTS.length)
//		{
//			return;
//		}
//		if (Util.getPay(localPlayer, Config.DONATE_OLF_ITEM, Config.DONATE_OLF_VARIANTS_PRICE[i], true))
//		{
//			ItemInstance localItemInstance = ItemFunctions.createItem(21580);
//			PcInventory localPcInventory = localPlayer.getInventory();
//			localItemInstance.setEnchantLevel(Config.DONATE_OLF_VARIANTS[i]);
//			localPcInventory.addItem(localItemInstance, "L2CCCP Donation System");
//			localItemInstance.setJdbcState(JdbcEntityState.UPDATED);
//			localItemInstance.update();
//			if (ItemFunctions.checkIfCanEquip(localPlayer, localItemInstance) == null)
//			{
//				localPcInventory.equipItem(localItemInstance);
//			}
//			localPlayer.sendMessage("You buy Olf's Shirt, Enchant: +" + Config.DONATE_OLF_VARIANTS[i]);
//		}
//	}
//
//	public void olf_enchant(String[] paramArrayOfString)
//	{
//		if ((paramArrayOfString[0].isEmpty()) || (!Util.isNumber(paramArrayOfString[0])))
//		{
//			return;
//		}
//		Player localPlayer = getSelf();
//		ItemInstance localItemInstance = localPlayer.getInventory().getPaperdollItem(0);
//		int i = Integer.parseInt(paramArrayOfString[0]);
//		int[] arrayOfInt = B(localItemInstance.getEnchantLevel());
//		if (i >= arrayOfInt.length)
//		{
//			return;
//		}
//		if ((localItemInstance != null) && (localItemInstance.getItemId() == 21580) && (Util.getPay(localPlayer, Config.DONATE_OLF_ITEM, arrayOfInt[i], true)))
//		{
//			localPlayer.getInventory().unEquipItem(localItemInstance);
//			localItemInstance.setEnchantLevel(Config.DONATE_OLF_ENCHATED[i]);
//			localItemInstance.setJdbcState(JdbcEntityState.UPDATED);
//			localItemInstance.update();
//			localPlayer.getInventory().equipItem(localItemInstance);
//			localPlayer.sendPacket(new InventoryUpdate().addModifiedItem(localItemInstance));
//			localPlayer.broadcastCharInfo();
//		}
//	}
//
//	private int[] B(int paramInt)
//	{
//		switch (paramInt)
//		{
//			case 5:
//				return Config.DONATE_OLF_5;
//			case 6:
//				return Config.DONATE_OLF_6;
//			case 7:
//				return Config.DONATE_OLF_7;
//			case 8:
//				return Config.DONATE_OLF_8;
//			case 9:
//				return Config.DONATE_OLF_9;
//		}
//		return Config.DONATE_OLF_DEFAULT;
//	}
//
//	private void A(Player paramPlayer, boolean paramBoolean)
//	{
//		if (paramBoolean)
//		{
//			paramPlayer.deleteQuickVar("exchange");
//		}
//		paramPlayer.deleteQuickVar("exchange_obj");
//		paramPlayer.deleteQuickVar("exchange_new");
//		paramPlayer.deleteQuickVar("exchange_attribute");
//	}
//
//	public void unban()
//	{
//		Player localPlayer = getSelf();
//		NpcHtmlMessage localNpcHtmlMessage = new NpcHtmlMessage(0).setFile("scripts/services/DonateFunction/unban/index.htm");
//		localNpcHtmlMessage.replace("%price%", Util.formatPay(localPlayer, Config.DONATE_UNBAN[1], Config.DONATE_UNBAN[0]));
//		localPlayer.sendPacket(localNpcHtmlMessage);
//	}
//
//	public void unban(String[] paramArrayOfString)
//	{
//		if ((paramArrayOfString != null) && (!paramArrayOfString[0].isEmpty()))
//		{
//			B(paramArrayOfString[0]);
//		}
//	}
//
//	private void B(String paramString)
//	{
//		Player localPlayer = getSelf();
//		if ((Util.getPay(localPlayer, Config.DONATE_UNBAN[0], Config.DONATE_UNBAN[1], true)) && (!D(paramString)))
//		{
//			ItemFunctions.addItem(localPlayer, Config.DONATE_UNBAN[0], Config.DONATE_UNBAN[1], true, "Donate System: Unban refund");
//			localPlayer.sendMessage("Can't find account or account and character is not is ban!");
//		}
//	}
//
//	private boolean D(String paramString)
//	{
//		boolean bool = false;
//		String str1 = "SELECT `obj_id`, `accesslevel`, `char_name` FROM `characters` WHERE `account_name` = ?";
//		String str2 = "UPDATE `characters` SET `accesslevel`='0' WHERE `obj_Id` = ?";
//		Player localPlayer = getSelf();
//		Connection localConnection = null;
//		PreparedStatement localPreparedStatement1 = null;
//		ResultSet localResultSet = null;
//		try
//		{
//			localConnection = DatabaseFactory.getInstance().getConnection();
//			localPreparedStatement1 = localConnection.prepareStatement("SELECT `obj_id`, `accesslevel`, `char_name` FROM `characters` WHERE `account_name` = ?");
//			localPreparedStatement1.setString(1, paramString);
//			localResultSet = localPreparedStatement1.executeQuery();
//			while (localResultSet.next())
//			{
//				int i = localResultSet.getInt("accesslevel");
//				int j = localResultSet.getInt("obj_id");
//				String str3 = localResultSet.getString("char_name");
//				localPlayer.sendMessage("Find character: " + str3 + ", Access: " + i);
//				if (i < 0)
//				{
//					PreparedStatement localPreparedStatement2 = localConnection.prepareStatement("UPDATE `characters` SET `accesslevel`='0' WHERE `obj_Id` = ?");
//					localPreparedStatement2.setInt(1, j);
//					localPreparedStatement2.execute();
//					localPreparedStatement2.close();
//					localPlayer.sendMessage("Unban engine: Character " + str3 + " has been unbanned success!");
//				}
//				if (AutoBan.checkIsBanned(j))
//				{
//					AutoBan.unBaned(j);
//					localPlayer.sendMessage("Unban engine: Character " + str3 + " has remove from bans table!");
//				}
//				AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(paramString, 0, 0));
//				localPlayer.sendMessage("Unban engine: Account " + paramString + " has been unbanned!");
//				bool = true;
//			}
//		}
//		catch (Exception localException)
//		{
//			localException.printStackTrace();
//		}
//		finally
//		{
//			DbUtils.closeQuietly(localConnection, localPreparedStatement1, localResultSet);
//		}
//		return bool;
//	}
//}
