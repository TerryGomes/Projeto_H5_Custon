import l2f.gameserver.Config;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.data.xml.holder.BuyListHolder;
import l2f.gameserver.data.xml.holder.EventHolder;
import l2f.gameserver.data.xml.holder.MultiSellHolder;
import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.handler.bbs.CommunityBoardManager;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.SubClass;
import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.model.entity.SevenSigns;
import l2f.gameserver.model.entity.events.EventType;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubEventManager;
import l2f.gameserver.model.entity.events.impl.AbstractFightClub;
import l2f.gameserver.model.entity.olympiad.Olympiad;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.ExBuySellList;
import l2f.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import l2f.gameserver.network.serverpackets.ExShowVariationMakeWindow;
import l2f.gameserver.network.serverpackets.HennaEquipList;
import l2f.gameserver.network.serverpackets.HennaUnequipList;
import l2f.gameserver.network.serverpackets.HideBoard;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.WarehouseFunctions;

public class Util extends Functions
{
	public void Gatekeeper(String[] param)
	{
		if (param.length < 4)
		{
			throw new IllegalArgumentException();
		}

		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		long price = Long.parseLong(param[param.length - 1]);

		if (!NpcInstance.canBypassCheck(player, player.getLastNpc()))
		{
			return;
		}

		if (price > 0 && player.getAdena() < price)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		if (player.isJailed())
		{
			player.sendMessage("You cannot escape from Jail!");
			return;
		}

		if (player.getActiveWeaponFlagAttachment() != null)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
			return;
		}

		if (player.getMountType() == 2)
		{
			player.sendMessage("Teleportation riding a Wyvern is not possible.");
			return;
		}

		/*
		 * Gag, npc Mozella not TPshit chars that exceed specified in the config file
		 * Off Like> = 56 lvl, to limit the data set to lvl'a altsettings.ini.
		 */
		final int npcId = (player.getLastNpc() != null ? player.getLastNpc().getNpcId() : 0);
		switch (npcId)
		{
		case 30483:
			if (player.getLevel() >= Config.CRUMA_GATEKEEPER_LVL)
			{
				show("teleporter/30483-no.htm", player);
				return;
			}
			break;
		case 32864:
		case 32865:
		case 32866:
		case 32867:
		case 32868:
		case 32869:
		case 32870:
			if ((player.getKarma() > 0) || (player.getPvpFlag() > 0))
			{
				show("I'm sorry, but you cannot use my services right now.", player);
				return;
			}
			if (player.getLevel() < 80)
			{
				show("teleporter/" + npcId + "-no.htm", player);
				return;
			}
			break;
		}

		if (!player.isInPeaceZone() && (player.isInCombat() || player.getPvpFlag() > 0))
		{
			player.sendMessage("You cannot teleport in this state!");
			return;
		}

		int x = Integer.parseInt(param[0]);
		int y = Integer.parseInt(param[1]);
		int z = Integer.parseInt(param[2]);
		int castleId = param.length > 4 ? Integer.parseInt(param[3]) : 0;

		if (player.getReflection().isDefault())
		{
			Castle castle = castleId > 0 ? ResidenceHolder.getInstance().getResidence(Castle.class, castleId) : null;
			if (castle != null && castle.getSiegeEvent().isInProgress())
			{
				player.sendPacket(Msg.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
				return;
			}
		}

		// Synerge - Epidos cube on teleport should add allowed player to beleth zone, so other players cannot exploit it
		// if (npcId == 32376)
		// BelethManager._allowedPlayers.add(player.getObjectId());

		Location pos = Location.findPointToStay(x, y, z, 50, 100, player.getGeoIndex());

		if (price > 0)
		{
			player.reduceAdena(price, true, "Gatekeeper");
		}
		player.teleToLocation(pos);
	}

	public void CommunityGatekeeper(String[] param)
	{
		if (param.length < 4)
		{
			System.out.println("Wrong CommunityGatekeeper Bypass: " + l2f.gameserver.utils.Util.joinArrayWithCharacter(param, " "));
			throw new IllegalArgumentException();
		}

		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		long price = Long.parseLong(param[param.length - 1]);

		if (price > 0 && player.getAdena() < price)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		if (player.isJailed())
		{
			player.sendMessage("You cannot escape from Jail!");
			return;
		}

		if (player.getActiveWeaponFlagAttachment() != null)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
			return;
		}

		if (player.getMountType() == 2)
		{
			player.sendMessage("Teleportation riding a Wyvern is not possible.");
			return;
		}

		if (!player.isInPeaceZone()
					&& (player.isInCombat() || player.getPvpFlag() > 0 || !player.getReflection().equals(ReflectionManager.DEFAULT) || player.isInOlympiadMode() || Olympiad.isRegistered(player)))
		{
			player.sendMessage("You cannot teleport in this state!");
			return;
		}

		int x = Integer.parseInt(param[0]);
		int y = Integer.parseInt(param[1]);
		int z = Integer.parseInt(param[2]);
		int castleId = param.length > 4 ? Integer.parseInt(param[3]) : 0;
		final boolean closeTutorial = param.length > 5;

		if (player.getReflection().isDefault())
		{
			Castle castle = castleId > 0 ? ResidenceHolder.getInstance().getResidence(Castle.class, castleId) : null;
			if (castle != null && castle.getSiegeEvent().isInProgress())
			{
				player.sendPacket(Msg.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
				return;
			}
		}

		Location pos = Location.findPointToStay(x, y, z, 50, 100, player.getGeoIndex());

		// Synerge - Extra parameter to close tutorial
		if (closeTutorial)
		{
			final QuestState qs = player.getQuestState("_255_Tutorial");
			if (qs != null)
			{
				qs.closeTutorial();
			}
		}

		player.sendPacket(new HideBoard());
		if (price > 0)
		{
			player.reduceAdena(price, true, "CommunityGatekeeper");
		}
		player.teleToLocation(pos);
	}

	public void SSGatekeeper(String[] param)
	{
		if (param.length < 4)
		{
			throw new IllegalArgumentException();
		}

		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		int type = Integer.parseInt(param[3]);

		if (!NpcInstance.canBypassCheck(player, player.getLastNpc()))
		{
			return;
		}

		if (player.isJailed())
		{
			player.sendMessage("You cannot escape from Jail!");
			return;
		}

		if (type > 0)
		{
			int player_cabal = SevenSigns.getInstance().getPlayerCabal(player);
			int period = SevenSigns.getInstance().getCurrentPeriod();
			if (period == SevenSigns.PERIOD_COMPETITION && player_cabal == SevenSigns.CABAL_NULL)
			{
				player.sendPacket(Msg.USED_ONLY_DURING_A_QUEST_EVENT_PERIOD);
				return;
			}

			int winner;
			if (period == SevenSigns.PERIOD_SEAL_VALIDATION && (winner = SevenSigns.getInstance().getCabalHighestScore()) != SevenSigns.CABAL_NULL)
			{
				if ((winner != player_cabal) || (type == 1 && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE) != player_cabal))
				{
					return;
				}
				if (type == 2 && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS) != player_cabal)
				{
					return;
				}
			}
		}

		player.teleToLocation(Integer.parseInt(param[0]), Integer.parseInt(param[1]), Integer.parseInt(param[2]));
	}

	public void QuestGatekeeper(String[] param)
	{
		if (param.length < 5)
		{
			throw new IllegalArgumentException();
		}

		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		long count = Long.parseLong(param[3]);
		int item = Integer.parseInt(param[4]);

		if (!NpcInstance.canBypassCheck(player, player.getLastNpc()))
		{
			return;
		}

		if (player.isJailed())
		{
			player.sendMessage("You cannot escape from Jail!");
			return;
		}

		if (count > 0)
		{
			if (!player.getInventory().destroyItemByItemId(item, count, "QuestGatekeeper"))
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return;
			}
			player.sendPacket(SystemMessage2.removeItems(item, count));
		}

		int x = Integer.parseInt(param[0]);
		int y = Integer.parseInt(param[1]);
		int z = Integer.parseInt(param[2]);

		Location pos = Location.findPointToStay(x, y, z, 20, 70, player.getGeoIndex());

		player.teleToLocation(pos);
	}

	public void ReflectionGatekeeper(String[] param)
	{
		if (param.length < 5)
		{
			throw new IllegalArgumentException();
		}

		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (player.isJailed())
		{
			player.sendMessage("You cannot escape from Jail!");
			return;
		}

		player.setReflection(Integer.parseInt(param[4]));

		Gatekeeper(param);
	}

	public void CommunityMultisell(String[] param)
	{
		if (!getSelf().isInZonePeace())
		{
			getSelf().sendMessage("It can be used only in Peaceful zone!");
			return;
		}

		if (getSelf().isJailed())
		{
			getSelf().sendMessage("You cannot do it in Jail!");
			return;
		}

		String listId = param[0];
		MultiSellHolder.getInstance().SeparateAndSend(Integer.parseInt(listId), getSelf(), 0);
	}

	public void CommunitySell()
	{
		if (!getSelf().isInZonePeace())
		{
			getSelf().sendMessage("It can be used only in Peaceful zone!");
			return;
		}
		if (getSelf().isJailed())
		{
			getSelf().sendMessage("You cannot do it in Jail!");
			return;
		}

		BuyListHolder.NpcTradeList list = BuyListHolder.getInstance().getBuyList(0);
		if (list == null)
		{
			getSelf().sendPacket(new ExBuySellList.BuyList(list, getSelf(), 0), new ExBuySellList.SellRefundList(getSelf(), false));
		}
	}

	public void CommunityAugment()
	{
		if (!getSelf().isInZonePeace())
		{
			getSelf().sendMessage("It can be used only in Peaceful zone!");
			return;
		}
		getSelf().sendPacket(Msg.SELECT_THE_ITEM_TO_BE_AUGMENTED, ExShowVariationMakeWindow.STATIC);
	}

	public void CommunityRemoveAugment()
	{
		if (!getSelf().isInZonePeace())
		{
			getSelf().sendMessage("It can be used only in Peaceful zone!");
			return;
		}
		getSelf().sendPacket(Msg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION, ExShowVariationCancelWindow.STATIC);
	}

	public void CommunityPrivateWarehouseDeposit()
	{
		if (!getSelf().isInZonePeace())
		{
			getSelf().sendMessage("It can be used only in Peaceful zone!");
			return;
		}
		if (getSelf().isJailed())
		{
			getSelf().sendMessage("You cannot do it in Jail!");
			return;
		}

		WarehouseFunctions.showDepositWindow(getSelf());
	}

	public void CommunityPrivateWarehouseRetrieve()
	{
		if (!getSelf().isInZonePeace())
		{
			getSelf().sendMessage("It can be used only in Peaceful zone!");
			return;
		}
		if (getSelf().isJailed())
		{
			getSelf().sendMessage("You cannot do it in Jail!");
			return;
		}

		WarehouseFunctions.showRetrieveWindow(getSelf(), 0);
	}

	public void CommunityClanWarehouseDeposit()
	{
		if (!getSelf().isInZonePeace())
		{
			getSelf().sendMessage("It can be used only in Peaceful zone!");
			return;
		}
		if (getSelf().isJailed())
		{
			getSelf().sendMessage("You cannot do it in Jail!");
			return;
		}

		WarehouseFunctions.showDepositWindowClan(getSelf());
	}

	public void CommunityClanWarehouseWithdraw()
	{
		if (!getSelf().isInZonePeace())
		{
			getSelf().sendMessage("It can be used only in Peaceful zone!");
			return;
		}
		if (getSelf().isJailed())
		{
			getSelf().sendMessage("You cannot do it in Jail!");
			return;
		}

		WarehouseFunctions.showWithdrawWindowClan(getSelf(), 0);
	}

	public void CommunityDrawSymbol()
	{
		if (!getSelf().isInZonePeace())
		{
			getSelf().sendMessage("It can be used only in Peaceful zone!");
			return;
		}
		getSelf().sendPacket(new HennaEquipList(getSelf()));
	}

	public void CommunityRemoveSymbol()
	{
		if (!getSelf().isInZonePeace())
		{
			getSelf().sendMessage("It can be used only in Peaceful zone!");
			return;
		}
		getSelf().sendPacket(new HennaUnequipList(getSelf()));
	}

	public void CommunityCert65()
	{
		SubClass clzz = getSelf().getActiveClass();
		if (!checkCertificationCondition(65, SubClass.CERTIFICATION_65))
		{
			return;
		}

		Functions.addItem(getSelf(), 10280, 1, "CommunityCert65");
		clzz.addCertification(SubClass.CERTIFICATION_65);
		getSelf().store(true);
		CommunityBoardManager.getInstance().getCommunityHandler("_bbsChooseCertificate").onBypassCommand(getSelf(), "_bbsChooseCertificate");
	}

	public void CommunityCert70()
	{
		SubClass clzz = getSelf().getActiveClass();
		if (!checkCertificationCondition(70, SubClass.CERTIFICATION_70))
		{
			return;
		}

		Functions.addItem(getSelf(), 10280, 1, "CommunityCert70");
		clzz.addCertification(SubClass.CERTIFICATION_70);
		getSelf().store(true);
		CommunityBoardManager.getInstance().getCommunityHandler("_bbsChooseCertificate").onBypassCommand(getSelf(), "_bbsChooseCertificate");
	}

	public void CommunityCert75Class()
	{
		SubClass clzz = getSelf().getActiveClass();
		if (!checkCertificationCondition(75, SubClass.CERTIFICATION_75))
		{
			return;
		}

		ClassId cl = ClassId.VALUES[clzz.getClassId()];
		if (cl.getType2() == null)
		{
			return;
		}

		Functions.addItem(getSelf(), cl.getType2().getCertificateId(), 1, "CommunityCert75Class");
		clzz.addCertification(SubClass.CERTIFICATION_75);
		getSelf().store(true);
		CommunityBoardManager.getInstance().getCommunityHandler("_bbsChooseCertificate").onBypassCommand(getSelf(), "_bbsChooseCertificate");
	}

	public void CommunityCert75Master()
	{
		SubClass clzz = getSelf().getActiveClass();
		if (!checkCertificationCondition(75, SubClass.CERTIFICATION_75))
		{
			return;
		}

		Functions.addItem(getSelf(), 10612, 1, "CommunityCert75Master"); // master ability
		clzz.addCertification(SubClass.CERTIFICATION_75);
		getSelf().store(true);
		CommunityBoardManager.getInstance().getCommunityHandler("_bbsChooseCertificate").onBypassCommand(getSelf(), "_bbsChooseCertificate");
	}

	public void CommunityCert80()
	{
		SubClass clzz = getSelf().getActiveClass();
		if (!checkCertificationCondition(80, SubClass.CERTIFICATION_80))
		{
			return;
		}

		ClassId cl = ClassId.VALUES[clzz.getClassId()];
		if (cl.getType2() == null)
		{
			return;
		}

		Functions.addItem(getSelf(), cl.getType2().getTransformationId(), 1, "CommunityCert80");
		clzz.addCertification(SubClass.CERTIFICATION_80);
		getSelf().store(true);
		CommunityBoardManager.getInstance().getCommunityHandler("_bbsChooseCertificate").onBypassCommand(getSelf(), "_bbsChooseCertificate");
	}

	private boolean checkCertificationCondition(int requiredLevel, int certificationIndex)
	{
		boolean failed = false;
		if (getSelf().getLevel() < requiredLevel)
		{
			getSelf().sendMessage("Your Level is too low!");
			failed = true;
		}
		SubClass clazz = getSelf().getActiveClass();
		if (!failed && clazz.isCertificationGet(certificationIndex))
		{
			getSelf().sendMessage("You already have this Certification!");
			failed = true;
		}

		if (failed)
		{
			CommunityBoardManager.getInstance().getCommunityHandler("_bbsfile").onBypassCommand(getSelf(), "_bbsfile:smallNpcs/subclassChanger");
			return false;
		}
		return true;
	}

	public void TokenJump(String[] param)
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (player.getLevel() <= 19)
		{
			QuestGatekeeper(param);
		}
		else
		{
			show("Only for newbies", player);
		}
	}

	public void NoblessTeleport()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (player.isNoble() || Config.ALLOW_NOBLE_TP_TO_ALL)
		{
			show("scripts/noble.htm", player);
		}
		else
		{
			show("scripts/nobleteleporter-no.htm", player);
		}
	}

	public void PayPage(String[] param)
	{
		if (param.length < 2)
		{
			throw new IllegalArgumentException();
		}

		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		String page = param[0];
		int item = Integer.parseInt(param[1]);
		long price = Long.parseLong(param[2]);

		if (getItemCount(player, item) < price)
		{
			player.sendPacket(item == 57 ? Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA : SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		removeItem(player, item, price, "PayPage");
		show(page, player);
	}

	public void MakeEchoCrystal(String[] param)
	{
		if (param.length < 2)
		{
			throw new IllegalArgumentException();
		}

		Player player = getSelf();
		if ((player == null) || !NpcInstance.canBypassCheck(player, player.getLastNpc()))
		{
			return;
		}

		int crystal = Integer.parseInt(param[0]);
		int score = Integer.parseInt(param[1]);

		if (crystal < 4411 || crystal > 4417)
		{
			return;
		}

		if (getItemCount(player, score) == 0)
		{
			player.getLastNpc().onBypassFeedback(player, "Chat 1");
			return;
		}

		if (getItemCount(player, 57) < 200)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		removeItem(player, 57, 200, "MakeEchoCrystal");
		addItem(player, crystal, 1, "MakeEchoCrystal");
	}

	public void TakeNewbieWeaponCoupon()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.ALT_ALLOW_SHADOW_WEAPONS)
		{
			show(new CustomMessage("common.Disabled", player), player);
			return;
		}
		if (player.getLevel() > 19 || player.getClassId().getLevel() > 1)
		{
			show("Your level is too high!", player);
			return;
		}
		if (player.getLevel() < 6)
		{
			show("Your level is too low!", player);
			return;
		}
		if (player.getVarB("newbieweapon"))
		{
			show("Your already got your newbie weapon!", player);
			return;
		}
		addItem(player, 7832, 5, "TakeNewbieWeaponCoupon");
		player.setVar("newbieweapon", "true", -1);
	}

	public void TakeAdventurersArmorCoupon()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.ALT_ALLOW_SHADOW_WEAPONS)
		{
			show(new CustomMessage("common.Disabled", player), player);
			return;
		}
		if (player.getLevel() > 39 || player.getClassId().getLevel() > 2)
		{
			show("Your level is too high!", player);
			return;
		}
		if (player.getLevel() < 20 || player.getClassId().getLevel() < 2)
		{
			show("Your level is too low!", player);
			return;
		}
		if (player.getVarB("newbiearmor"))
		{
			show("Your already got your newbie weapon!", player);
			return;
		}
		addItem(player, 7833, 1, "TakeAdventurersArmorCoupon");
		player.setVar("newbiearmor", "true", -1);
	}

	public void enter_dc()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null || !NpcInstance.canBypassCheck(player, npc))
		{
			return;
		}

		player.setVar("DCBackCoords", player.getLoc().toXYZString(), -1);
		player.teleToLocation(-114582, -152635, -6742);
	}

	public void exit_dc()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null || !NpcInstance.canBypassCheck(player, npc))
		{
			return;
		}

		String var = player.getVar("DCBackCoords");
		if (var == null || var.isEmpty())
		{
			player.teleToLocation(new Location(43768, -48232, -800), 0);
			return;
		}
		player.teleToLocation(Location.parseLoc(var), 0);
		player.unsetVar("DCBackCoords");
	}

	public void addPlayerToTvT()
	{
		Player player = getSelf();

		AbstractFightClub event = (AbstractFightClub) EventHolder.getInstance().getEvent(EventType.FIGHT_CLUB_EVENT, 2);

		FightClubEventManager.getInstance().trySignForEvent(player, event, true);
	}
}