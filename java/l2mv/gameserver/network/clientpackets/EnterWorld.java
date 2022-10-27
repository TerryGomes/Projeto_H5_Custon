package l2mv.gameserver.network.clientpackets;

import java.util.Calendar;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Announcements;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.dao.MailDAO;
import l2mv.gameserver.data.StringHolder;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.fandc.datatables.OfflineBuffersTable;
import l2mv.gameserver.hwid.HwidEngine;
import l2mv.gameserver.hwid.HwidGamer;
import l2mv.gameserver.instancemanager.AutoHuntingManager;
import l2mv.gameserver.instancemanager.CoupleManager;
import l2mv.gameserver.instancemanager.CursedWeaponsManager;
import l2mv.gameserver.instancemanager.PetitionManager;
import l2mv.gameserver.instancemanager.PlayerMessageStack;
import l2mv.gameserver.instancemanager.QuestManager;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2mv.gameserver.masteriopack.rankpvpsystem.RPSConfig;
import l2mv.gameserver.masteriopack.rankpvpsystem.RankPvpSystem;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Summon;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.base.InvisibleType;
import l2mv.gameserver.model.entity.Hero;
import l2mv.gameserver.model.entity.SevenSigns;
import l2mv.gameserver.model.entity.CCPHelpers.CCPSecondaryPassword;
import l2mv.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.model.entity.residence.ClanHall;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.SubUnit;
import l2mv.gameserver.model.pledge.UnitMember;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.GameClient.GameClientState;
import l2mv.gameserver.network.serverpackets.ChangeWaitType;
import l2mv.gameserver.network.serverpackets.ClientSetTime;
import l2mv.gameserver.network.serverpackets.ConfirmDlg;
import l2mv.gameserver.network.serverpackets.Die;
import l2mv.gameserver.network.serverpackets.EtcStatusUpdate;
import l2mv.gameserver.network.serverpackets.ExAutoSoulShot;
import l2mv.gameserver.network.serverpackets.ExBR_PremiumState;
import l2mv.gameserver.network.serverpackets.ExBasicActionList;
import l2mv.gameserver.network.serverpackets.ExGoodsInventoryChangedNotify;
import l2mv.gameserver.network.serverpackets.ExMPCCOpen;
import l2mv.gameserver.network.serverpackets.ExNoticePostArrived;
import l2mv.gameserver.network.serverpackets.ExNotifyPremiumItem;
import l2mv.gameserver.network.serverpackets.ExPCCafePointInfo;
import l2mv.gameserver.network.serverpackets.ExReceiveShowPostFriend;
import l2mv.gameserver.network.serverpackets.ExSetCompassZoneCode;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2mv.gameserver.network.serverpackets.HennaInfo;
import l2mv.gameserver.network.serverpackets.L2FriendList;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
import l2mv.gameserver.network.serverpackets.MagicSkillLaunched;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.PartySmallWindowAll;
import l2mv.gameserver.network.serverpackets.PartySpelled;
import l2mv.gameserver.network.serverpackets.PetInfo;
import l2mv.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import l2mv.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import l2mv.gameserver.network.serverpackets.PledgeSkillList;
import l2mv.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import l2mv.gameserver.network.serverpackets.PrivateStoreMsgSell;
import l2mv.gameserver.network.serverpackets.QuestList;
import l2mv.gameserver.network.serverpackets.RecipeShopMsg;
import l2mv.gameserver.network.serverpackets.RelationChanged;
import l2mv.gameserver.network.serverpackets.Ride;
import l2mv.gameserver.network.serverpackets.SSQInfo;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.ShortCutInit;
import l2mv.gameserver.network.serverpackets.SkillCoolTime;
import l2mv.gameserver.network.serverpackets.SkillList;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.skills.AbnormalEffect;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.AccountEmail;
import l2mv.gameserver.utils.GameStats;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Log;
import l2mv.gameserver.utils.Strings;
import l2mv.gameserver.utils.TimeUtils;
import l2mv.gameserver.utils.TradeHelper;

public class EnterWorld extends L2GameClientPacket
{
	private static final Object _lock = new Object();

	private static final Logger LOG = LoggerFactory.getLogger(EnterWorld.class);

	@Override
	protected void readImpl()
	{
		// readS(); - client always sends the String "narcasse"
	}

	@Override
	protected void runImpl()
	{
		long lastAccess = 0;
		final GameClient client = this.getClient();
		Player activeChar = client.getActiveChar();

		if (activeChar == null || Config.AUTH_SERVER_GM_ONLY && !activeChar.isGM())
		{
			client.closeNow(false);
			return;
		}

		int myObjectId = activeChar.getObjectId();
		Long myStoreId = activeChar.getStoredId();

		synchronized (_lock)// TODO [G1ta0] Th is for garbage, and why is it here?
		{
			for (Player cha : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (myStoreId == cha.getStoredId())
				{
					continue;
				}
				try
				{
					if (cha.getObjectId() == myObjectId)
					{
						LOG.warn("Double EnterWorld for char: " + activeChar.getName());
						cha.kick();
						return;
					}
				}
				catch (RuntimeException e)
				{
					LOG.error("Error while kicking copyed Char!", e);
				}
			}
		}

		if (client != null && Config.ALLOW_HWID_ENGINE)
		{
			// Checking hwid lock
			if (activeChar.getHwidLock() != null && !client.getHWID().equals(activeChar.getHwidLock()))
			{
				Log.logClientProtection(activeChar.toString() + " Failed to enter the game. Message: Character is locked by HWID!");
				HwidEngine.getInstance().logFailedLogin(activeChar);
				activeChar.sendPacket(new ExShowScreenMessage("Character is locked by HWID!", 30000, ScreenMessageAlign.TOP_CENTER, true));

				ThreadPoolManager.getInstance().schedule(() ->
				{
					client.closeNow(false);
				}, 3000L);
				return;
			}

			// Synerge - Check if the max instances for smartguard are reached. Somehow people bypass the smartguard check, so we do it here again manually
//			int instancesFound = 0;
//			for (Player cha : GameObjectsStorage.getAllPlayersForIterate())
//			{
//				if (!activeChar.getHWID().equalsIgnoreCase(cha.getHWID()))
//					continue;
//
//				instancesFound++;
//				if (instancesFound >= GuardConfig.MaxInstances)
//				{
//					Log.logClientProtection(activeChar.toString() + " bypassed smartguard max instance check trying to log more than the limit. Kicked from the game");
//					cha.kick();
//					return;
//				}
//			}

			// Logging in
			HwidGamer gamer = HwidEngine.getInstance().newPlayer(activeChar);
			activeChar.setHwidGamer(gamer);
			gamer.logToPlayer(activeChar.getObjectId(), "Successfully logged to game!");
		}

		GameStats.incrementPlayerEnterGame();

		Log.logEnterWorld(activeChar);

		boolean first = activeChar.entering;

		if (!activeChar.isHero() && !activeChar.isFakeHero())
		{
			for (ItemInstance item : activeChar.getInventory().getItems())
			{
				if (item.isHeroWeapon())
				{
					activeChar.getInventory().destroyItem(item, "CheckHeroWeapon");
				}
			}
			ItemFunctions.removeItem(activeChar, 6842, 1, true, "RemoveCirclet");
			ItemFunctions.removeItem(activeChar, 37032, 1, true, "removeCloak");
		}

		if (activeChar.getTitle().equals("*Away*"))
		{
			activeChar.setTitle(null);
			activeChar.setTitleColor(Player.DEFAULT_TITLE_COLOR);
		}

		if (RPSConfig.RANK_PVP_SYSTEM_ENABLED)
		{
			RankPvpSystem.updateNickAndTitleColor(activeChar, null);
		}

		if (first)
		{
			activeChar.setUptime(System.currentTimeMillis());
			activeChar.setOnlineStatus(true);
			lastAccess = activeChar.getLastAccess();
			if (activeChar.getPlayerAccess().GodMode && !Config.SHOW_GM_LOGIN)
			{
				activeChar.setInvisibleType(InvisibleType.EFFECT);
				activeChar.startAbnormalEffect(AbnormalEffect.STEALTH);
				activeChar.sendUserInfo(true);
				if (activeChar.isGM())
				{
					World.removeObjectFromPlayers(activeChar);
				}
			}
			activeChar.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);

			// Synerge - We also give player spawn protection, so he cannot recieve damage until he does something
			activeChar.setSpawnProtection(System.currentTimeMillis() + Player.SPAWN_PROTECTION_TIME);

			activeChar.spawnMe();
			activeChar.setPendingOlyEnd(false);

			if (activeChar.isInStoreMode() && !activeChar.isInBuffStore())
			{
				if (!TradeHelper.checksIfCanOpenStore(activeChar, activeChar.getPrivateStoreType()))
				{
					activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
					activeChar.broadcastCharInfo();
				}
			}
			// Prims - If its in a buff store, remove it on login
			else if (activeChar.isInBuffStore())
			{
				activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
				activeChar.broadcastCharInfo();
			}

			activeChar.setRunning();
			activeChar.standUp();
			activeChar.startTimers();
		}

		if (client.getState() == GameClientState.ENTER_GAME)
		{
			client.setState(GameClientState.IN_GAME);
		}

		boolean isPremium = activeChar.hasBonus();
		activeChar.sendPacket(new ExBR_PremiumState(activeChar, isPremium));
		if (!isPremium)
		{
			activeChar.stopBonusTask(false);
		}
		if (Config.ENTER_WORLD_ANNOUNCEMENTS_HERO_LOGIN)
		{
			if ((activeChar.isHero()) || (activeChar.isFakeHero()))
			{
				Announcements.getInstance().announceToAll(new CustomMessage("Hero {0} entered the game.").addString(activeChar.getName()).toString());
			}
		}
		if (Config.ENTER_WORLD_ANNOUNCEMENTS_LORD_LOGIN)
		{
			if ((activeChar.getClan() != null) && (activeChar.isClanLeader()) && (activeChar.getClan().getCastle() != 0))
			{
				int id = activeChar.getCastle().getId();
				Announcements.getInstance().announceToAll(new CustomMessage("Lord {0} the owner of the castle {1} entered the game.").addString(activeChar.getName()).addString(new CustomMessage("common.castle." + id, activeChar).toString()).toString());
			}
		}
		activeChar.getMacroses().sendUpdate();
		activeChar.sendPacket(new SSQInfo(), new HennaInfo(activeChar));
		activeChar.sendItemList(false);
		activeChar.sendPacket(new ShortCutInit(activeChar), new SkillList(activeChar), new SkillCoolTime(activeChar));
		activeChar.sendPacket(SystemMsg.WELCOME_TO_THE_WORLD_OF_LINEAGE_II);

		// New char is Hero
		if (Config.NEW_CHAR_IS_HERO)
		{
			activeChar.setHero(true);
			Hero.addSkills(activeChar);
		}

		// Add Hero SKills on enter if character log in
		if (activeChar.isFakeHero())
		{
			Hero.addSkills(activeChar);
		}

		// New char is NOBLE
		if (Config.NEW_CHAR_IS_NOBLE && !activeChar.isNoble())
		{
			Olympiad.addNoble(activeChar);
			activeChar.setNoble(true);
			activeChar.updatePledgeClass();
			activeChar.updateNobleSkills();
			activeChar.sendPacket(new SkillList(activeChar));
			activeChar.broadcastUserInfo(true);
		}

		if (Config.HTML_WELCOME)
		{
			String html = HtmCache.getInstance().getNotNull("welcome.htm", activeChar);
			NpcHtmlMessage msg = new NpcHtmlMessage(5);
			html.replace("%name%", activeChar.getName());
			msg.setHtml(Strings.bbParse(html));
			activeChar.sendPacket(msg);
		}

		Announcements.getInstance().showAnnouncements(activeChar);

		if (first)
		{
			activeChar.getListeners().onEnter();
		}

		SevenSigns.getInstance().sendCurrentPeriodMsg(activeChar);

		if (first && activeChar.getCreateTime() > 0L)
		{
			Calendar create = Calendar.getInstance();
			create.setTimeInMillis(activeChar.getCreateTime());
			Calendar now = Calendar.getInstance();

			int day = create.get(Calendar.DAY_OF_MONTH);
			if (create.get(Calendar.MONTH) == Calendar.FEBRUARY && day == 29)
			{
				day = 28;
			}

			int myBirthdayReceiveYear = activeChar.getVarInt(Player.MY_BIRTHDAY_RECEIVE_YEAR, 0);
			if (create.get(Calendar.MONTH) == now.get(Calendar.MONTH) && create.get(Calendar.DAY_OF_MONTH) == day)
			{
				if ((myBirthdayReceiveYear == 0 && create.get(Calendar.YEAR) != now.get(Calendar.YEAR)) || myBirthdayReceiveYear > 0 && myBirthdayReceiveYear != now.get(Calendar.YEAR))
				{
					Mail mail = new Mail();
					mail.setSenderId(1);
					mail.setSenderName(StringHolder.getInstance().getNotNull(activeChar, "birthday.npc"));
					mail.setReceiverId(activeChar.getObjectId());
					mail.setReceiverName(activeChar.getName());
					mail.setTopic(StringHolder.getInstance().getNotNull(activeChar, "birthday.title"));
					mail.setBody(StringHolder.getInstance().getNotNull(activeChar, "birthday.text"));

					ItemInstance item = ItemFunctions.createItem(21169);
					item.setLocation(ItemInstance.ItemLocation.MAIL);
					item.setCount(1L);
					item.save();

					mail.addAttachment(item);
					mail.setUnread(true);
					mail.setType(Mail.SenderType.BIRTHDAY);
					mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
					mail.save();

					activeChar.setVar(Player.MY_BIRTHDAY_RECEIVE_YEAR, String.valueOf(now.get(Calendar.YEAR)), -1);
				}
			}
		}

		if (activeChar.getClan() != null)
		{
			notifyClanMembers(activeChar);

			activeChar.sendPacket(activeChar.getClan().listAll());
			activeChar.sendPacket(new PledgeShowInfoUpdate(activeChar.getClan()), new PledgeSkillList(activeChar.getClan()));
		}

		// engage and notify Partner
		if (first && Config.ALLOW_WEDDING)
		{
			CoupleManager.getInstance().engage(activeChar);
			CoupleManager.getInstance().notifyPartner(activeChar);
		}

		if (Config.ENABLE_AUTO_HUNTING_REPORT)
		{
			AutoHuntingManager.getInstance().onEnter(activeChar);
		}

		if (first)
		{
			activeChar.getFriendList().notifyFriends(true);
			loadTutorial(activeChar);
			activeChar.restoreDisableSkills();

			if (activeChar.getVar("Para") != null)
			{
				if (!activeChar.isBlocked())
				{
					activeChar.block();
				}
				activeChar.startAbnormalEffect(AbnormalEffect.HOLD_1);
				activeChar.abortAttack(true, false);
				activeChar.abortCast(true, false);
				activeChar.sendPacket(new Say2(activeChar.getObjectId(), ChatType.TELL, "Paralyze", "You are paralyzed for " + (activeChar.getVarTimeToExpire("Para") / 60000L) + " more minutes!"));
			}

			if (Config.ALLOW_MAIL_OPTION)
			{
				AccountEmail.checkEmail(activeChar);
			}
		}

		this.sendPacket(new L2FriendList(activeChar), new QuestList(activeChar), new ExBasicActionList(activeChar), new EtcStatusUpdate(activeChar));

		activeChar.checkHpMessages(activeChar.getMaxHp(), activeChar.getCurrentHp());
		activeChar.checkDayNightMessages();

		if (Config.PETITIONING_ALLOWED)
		{
			PetitionManager.getInstance().checkPetitionMessages(activeChar);
		}

		if (!first)
		{
			if (activeChar.isCastingNow())
			{
				Creature castingTarget = activeChar.getCastingTarget();
				Skill castingSkill = activeChar.getCastingSkill();
				long animationEndTime = activeChar.getAnimationEndTime();

				if (castingSkill != null && castingTarget != null && castingTarget.isCreature() && activeChar.getAnimationEndTime() > 0L)
				{
					this.sendPacket(new MagicSkillUse(activeChar, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0));
				}
			}

			if (activeChar.isInBoat())
			{
				activeChar.sendPacket(activeChar.getBoat().getOnPacket(activeChar, activeChar.getInBoatPosition()));
			}

			if (activeChar.isMoving || activeChar.isFollow)
			{
				this.sendPacket(activeChar.movePacket());
			}

			if (activeChar.getMountNpcId() != 0)
			{
				this.sendPacket(new Ride(activeChar));
			}

			if (activeChar.isFishing())
			{
				activeChar.stopFishing();
			}
		}

		activeChar.entering = false;
		activeChar.sendUserInfo(true);

		if (activeChar.isSitting())
		{
			activeChar.sendPacket(new ChangeWaitType(activeChar, ChangeWaitType.WT_SITTING));
		}

		if (activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			if (activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_BUY)
			{
				this.sendPacket(new PrivateStoreMsgBuy(activeChar));
			}
			else if (activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_SELL || activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE)
			{
				this.sendPacket(new PrivateStoreMsgSell(activeChar));
			}
			else if (activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_MANUFACTURE)
			{
				this.sendPacket(new RecipeShopMsg(activeChar));
			}
		}

		if (activeChar.isDead())
		{
			this.sendPacket(new Die(activeChar));
		}

		activeChar.unsetVar("offline");

		// just in case
		activeChar.sendActionFailed();

		if (first && activeChar.isGM() && Config.SAVE_GM_EFFECTS && activeChar.getPlayerAccess().CanUseGMCommand)
		{
			// gmspeed
			try
			{
				int var_gmspeed = Integer.parseInt(activeChar.getVar("gm_gmspeed"));
				if (var_gmspeed >= 1 && var_gmspeed <= 4)
				{
					Skill skill = SkillTable.getInstance().getInfo(7029, var_gmspeed);
					activeChar.doCast(skill, activeChar, true);
				}
			}
			catch (NumberFormatException e)
			{
				// LOG.error("Error while loading gmSpeed var ", e);
			}
			// silence
			if (activeChar.getVarB("gm_silence"))
			{
				activeChar.setMessageRefusal(true);
				activeChar.sendPacket(SystemMsg.MESSAGE_REFUSAL_MODE);
			}
			// invul
			if (activeChar.getVarB("gm_invul"))
			{
				activeChar.setIsInvul(true);
				activeChar.sendMessage(activeChar.getName() + " is now Spartan !!!");
			}
		}

		if (first && activeChar.isInJail())
		{
			long period = activeChar.getVarTimeToExpire("jailed");
			if (period == -1)
			{
				activeChar.sendPacket(new Say2(0, ChatType.TELL, "Administration", " You are jailed forever !"));
			}
			else
			{
				period /= 1000; // to seconds
				period /= 60; // to minutes

				activeChar.sendPacket(new Say2(0, ChatType.TELL, "Administration", "Sit left " + TimeUtils.minutesToFullString((int) period)));
			}
		}
		PlayerMessageStack.getInstance().CheckMessages(activeChar);

		this.sendPacket(ClientSetTime.STATIC, new ExSetCompassZoneCode(activeChar));

		Pair<Integer, OnAnswerListener> entry = activeChar.getAskListener(false);
		if (entry != null && entry.getValue() instanceof ReviveAnswerListener)
		{
			this.sendPacket(new ConfirmDlg(SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, 0).addString("Other player").addString("some"));
		}

		if (activeChar.isCursedWeaponEquipped())
		{
			CursedWeaponsManager.getInstance().showUsageTime(activeChar, activeChar.getCursedWeaponEquippedId());
		}

		if (first)
		{
			if (Config.BUFF_STORE_ENABLED)
			{
				OfflineBuffersTable.getInstance().onLogin(activeChar);
			}
		}

		if (first)
		{
			activeChar.sendUserInfo(); // Display right in clan
		}
		else
		{
			// Characters left while viewing
			if (activeChar.isInObserverMode())
			{
				if (activeChar.getObserverMode() == Player.OBSERVER_LEAVING)
				{
					activeChar.returnFromObserverMode();
				}
				else if (activeChar.getOlympiadObserveGame() != null)
				{
					activeChar.leaveOlympiadObserverMode(true);
				}
				else
				{
					activeChar.leaveObserverMode();
				}
			}
			else if (activeChar.isVisible())
			{
				World.showObjectsToPlayer(activeChar);
			}

			if (activeChar.getPet() != null)
			{
				this.sendPacket(new PetInfo(activeChar.getPet()));
			}

			if (activeChar.isInParty())
			{
				Summon memberPet;
				// sends new member party window for all members
				// we do all actions before adding member to a list, this speeds
				// things up a little
				this.sendPacket(new PartySmallWindowAll(activeChar.getParty(), activeChar));

				for (Player member : activeChar.getParty().getMembers())
				{
					if (member != activeChar)
					{
						this.sendPacket(new PartySpelled(member, true));
						if ((memberPet = member.getPet()) != null)
						{
							this.sendPacket(new PartySpelled(memberPet, true));
						}

						this.sendPacket(RelationChanged.update(activeChar, member, activeChar));
					}
				}

				// If the party is in the CC, the newcomer send the package open
				// the CC
				if (activeChar.getParty().isInCommandChannel())
				{
					this.sendPacket(ExMPCCOpen.STATIC);
				}
			}

			for (int shotId : activeChar.getAutoSoulShot())
			{
				this.sendPacket(new ExAutoSoulShot(shotId, true));
			}

			for (Effect e : activeChar.getEffectList().getAllFirstEffects())
			{
				if (e.getSkill().isToggle())
				{
					this.sendPacket(new MagicSkillLaunched(activeChar.getObjectId(), e.getSkill().getId(), e.getSkill().getLevel(), activeChar));
				}
			}

			activeChar.broadcastCharInfo();
		}

		activeChar.updateEffectIcons();
		activeChar.updateStats();

		if (activeChar.getVarB("soulshot"))
		{
			ItemInstance item = activeChar.getActiveWeaponInstance();
			if (item != null)
			{
				switch (item.getCrystalType().cry)
				{
				case (ItemTemplate.CRYSTAL_NONE):
				{
					boolean bActive = false;
					ItemInstance shot = activeChar.getInventory().getItemByItemId(5789); // Beginner Soulshot
					if (shot != null)
					{
						activeChar.addAutoSoulShot(5789);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					shot = activeChar.getInventory().getItemByItemId(1835); // Soulshot no grade
					if (shot != null)
					{
						activeChar.addAutoSoulShot(1835);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					shot = activeChar.getInventory().getItemByItemId(5790); // Beginner spiritshot
					if (shot != null)
					{
						activeChar.addAutoSoulShot(5790);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					shot = activeChar.getInventory().getItemByItemId(2509); // Spiritshot no grade
					if (shot != null)
					{
						activeChar.addAutoSoulShot(2509);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					shot = activeChar.getInventory().getItemByItemId(3947); // Blessed spiritshot no grade
					if (shot != null)
					{
						activeChar.addAutoSoulShot(3947);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					if (bActive)
					{
						activeChar.autoShot();
					}

				}

				case (ItemTemplate.CRYSTAL_D):
				{
					boolean bActive = false;
					ItemInstance shot = activeChar.getInventory().getItemByItemId(1463); // Soulshot d
					if (shot != null)
					{
						activeChar.addAutoSoulShot(1463);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					shot = activeChar.getInventory().getItemByItemId(3948); // Blessed Spiritshot d
					if (shot != null)
					{
						activeChar.addAutoSoulShot(3948);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					if (bActive)
					{
						activeChar.autoShot();
					}
				}

				case (ItemTemplate.CRYSTAL_C):
				{
					boolean bActive = false;
					ItemInstance shot = activeChar.getInventory().getItemByItemId(1464); // Soulshot c
					if (shot != null)
					{
						activeChar.addAutoSoulShot(1464);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					shot = activeChar.getInventory().getItemByItemId(3949); // Blessed Spiritshot c
					if (shot != null)
					{
						activeChar.addAutoSoulShot(3949);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					if (bActive)
					{
						activeChar.autoShot();
					}

				}
				case (ItemTemplate.CRYSTAL_B):
				{
					boolean bActive = false;
					ItemInstance shot = activeChar.getInventory().getItemByItemId(1465); // Soulshot b
					if (shot != null)
					{
						activeChar.addAutoSoulShot(1465);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					shot = activeChar.getInventory().getItemByItemId(3950); // Blessed Spiritshot b
					if (shot != null)
					{
						activeChar.addAutoSoulShot(3950);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					if (bActive)
					{
						activeChar.autoShot();
					}

				}
				case (ItemTemplate.CRYSTAL_A):
				{
					boolean bActive = false;
					ItemInstance shot = activeChar.getInventory().getItemByItemId(1466); // Soulshot a
					if (shot != null)
					{
						activeChar.addAutoSoulShot(1466);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					shot = activeChar.getInventory().getItemByItemId(3951); // Blessed Spiritshot a
					if (shot != null)
					{
						activeChar.addAutoSoulShot(3951);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					if (bActive)
					{
						activeChar.autoShot();
					}

				}

				case (ItemTemplate.CRYSTAL_S):
				{
					boolean bActive = false;
					ItemInstance shot = activeChar.getInventory().getItemByItemId(1467); // Soulshot s
					if (shot != null)
					{
						activeChar.addAutoSoulShot(1467);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					shot = activeChar.getInventory().getItemByItemId(3952); // Blessed Spiritshot s
					if (shot != null)
					{
						activeChar.addAutoSoulShot(3952);
						activeChar.sendPacket(new ExAutoSoulShot(shot.getItemId(), true));
						activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(shot.getName()));
						bActive = true;
					}
					if (bActive)
					{
						activeChar.autoShot();
					}

				}
				}
			}
		}

		if (Config.ALT_PCBANG_POINTS_ENABLED)
		{
			activeChar.sendPacket(new ExPCCafePointInfo(activeChar, 0, 1, 2, 12));
		}

		if (!activeChar.getPremiumItemList().isEmpty())
		{
			activeChar.sendPacket(Config.GOODS_INVENTORY_ENABLED ? ExGoodsInventoryChangedNotify.STATIC : ExNotifyPremiumItem.STATIC);
		}

		if (activeChar.getVarB("HeroPeriod") && Config.SERVICES_HERO_SELL_ENABLED)
		{
			activeChar.setHero(true);
		}

		activeChar.sendVoteSystemInfo();
		activeChar.sendPacket(new ExReceiveShowPostFriend(activeChar));
		activeChar.getNevitSystem().onEnterWorld();

		this.checkNewMail(activeChar);

		String lastAccessDate = TimeUtils.convertDateToString(lastAccess * 1000);

		String ip = activeChar.getVar("LastIP");
		if (ip != null && !ip.isEmpty() && activeChar.getIP() != null)
		{
			if (!activeChar.getIP().equalsIgnoreCase(ip))
			{
				activeChar.sendPacket(new Say2(activeChar.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "SYS", "You are logging in from another IP. Last access: " + lastAccessDate));

				if (Config.ALLOW_MAIL_OPTION)
				{
					AccountEmail.verifyEmail(activeChar, null); // Send an e-mail verification html to this character so he can play only when he verifies his e-mail.
				}
				else
				{
					activeChar.setVar("LastIP", activeChar.getIP()); // Handled in verifyEmail if the above is ran. It is used to not abuse character relog to escape the verifyEmail.
				}
			}
		}
		else
		{
			// IP is null or empty, must populate the var for the next time.
			activeChar.setVar("LastIP", activeChar.getIP());
		}

		String hwid = activeChar.getVar("LastHWID");
		if (hwid != null && !hwid.isEmpty() && activeChar.getHWID() != null)
		{
			if (!activeChar.getHWID().equalsIgnoreCase(hwid))
			{
				activeChar.sendPacket(new Say2(activeChar.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "SYS", "You are logging in from another PC. Last access: " + lastAccessDate));
				activeChar.setVar("LastHWID", activeChar.getHWID());
			}
		}
		else // HWID is null or empty, must populate the var for the next time.
		if (activeChar.getHWID() != null)
		{
			activeChar.setVar("LastHWID", activeChar.getHWID());
		}
	}

	private static void notifyClanMembers(Player activeChar)
	{
		Clan clan = activeChar.getClan();
		SubUnit subUnit = activeChar.getSubUnit();
		if (clan == null || subUnit == null)
		{
			return;
		}

		UnitMember member = subUnit.getUnitMember(activeChar.getObjectId());
		if (member == null)
		{
			return;
		}

		member.setPlayerInstance(activeChar, false);

		int sponsor = activeChar.getSponsor();
		int apprentice = activeChar.getApprentice();
		L2GameServerPacket msg = new SystemMessage2(SystemMsg.CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME).addName(activeChar);
		IStaticPacket memberUpdate = new PledgeShowMemberListUpdate(activeChar);

		for (Player clanMember : clan.getOnlineMembers(activeChar.getObjectId()))
		{
			clanMember.sendPacket(memberUpdate);
			if (clanMember.getObjectId() == sponsor)
			{
				clanMember.sendPacket(new SystemMessage2(SystemMsg.YOUR_APPRENTICE_C1_HAS_LOGGED_OUT).addName(activeChar));
			}
			else if (clanMember.getObjectId() == apprentice)
			{
				clanMember.sendPacket(new SystemMessage2(SystemMsg.YOUR_SPONSOR_C1_HAS_LOGGED_IN).addName(activeChar));
			}
			else
			{
				clanMember.sendPacket(msg);
			}
		}

		if (!activeChar.isClanLeader())
		{
			return;
		}

		ClanHall clanHall = clan.getHasHideout() > 0 ? ResidenceHolder.getInstance().getResidence(ClanHall.class, clan.getHasHideout()) : null;
		if (clanHall == null || clanHall.getAuctionLength() != 0 || (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class))
		{
			return;
		}

		if (clan.getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA) < clanHall.getRentalFee())
		{
			activeChar.sendPacket(new SystemMessage2(SystemMsg.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_ME_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW).addLong(clanHall.getRentalFee()));
		}
	}

	public static void loadTutorial(Player player)
	{
		Quest q = QuestManager.getQuest(255);
		if (q != null)
		{
			if (CCPSecondaryPassword.hasPassword(player))
			{
				player.processQuestEvent(q.getName(), "CheckPass", null, false);
			}
			else
			{
				player.processQuestEvent(q.getName(), "ProposePass", null, false);
			}
			/*
			 * else if (player.getLevel() == 1 || Rnd.get(10) == 1)
			 * {
			 * player.processQuestEvent(q.getName(), "ProposePass", null, false);
			 * }
			 * else
			 * {
			 * player.processQuestEvent(q.getName(), "UC", null, false);
			 * }
			 */
			player.processQuestEvent(q.getName(), "OpenClassMaster", null, false);
			player.processQuestEvent(q.getName(), "ShowChangeLog", null, false);
		}
	}

	private void checkNewMail(Player activeChar)
	{
		for (Mail mail : MailDAO.getInstance().getReceivedMailByOwnerId(activeChar.getObjectId()))
		{
			if (mail.isUnread())
			{
				this.sendPacket(ExNoticePostArrived.STATIC_FALSE);
				break;
			}
		}
	}

	/**
	 * This method will get the correct soulshot/spirishot and activate it for the current weapon if it's over the minimum.
	 * @param activeChar
	 * @author Zoey76
	 */
	public static void verifyAndLoadShots(Player activeChar)
	{
		int soulId = -1;
		int spiritId = -1;
		int bspiritId = -1;

		if (!activeChar.isDead() && activeChar.getActiveWeaponItem() != null)
		{
			switch (activeChar.getActiveWeaponItem().getCrystalType())
			{
			case NONE:
				soulId = 1835;
				spiritId = 2509;
				bspiritId = 3947;
				break;
			case D:
				soulId = 1463;
				spiritId = 2510;
				bspiritId = 3948;
				break;
			case C:
				soulId = 1464;
				spiritId = 2511;
				bspiritId = 3949;
				break;
			case B:
				soulId = 1465;
				spiritId = 2512;
				bspiritId = 3950;
				break;
			case A:
				soulId = 1466;
				spiritId = 2513;
				bspiritId = 3951;
				break;
			case S:
			case S80:
			case S84:
				soulId = 1467;
				spiritId = 2514;
				bspiritId = 3952;
				break;
			}

			// Soulshots.
			if ((soulId > -1) && activeChar.getInventory().getCountOf(soulId) > 100)
			{
				activeChar.addAutoSoulShot(soulId);
				activeChar.sendPacket(new ExAutoSoulShot(soulId, true));
				// Message
				L2GameServerPacket msg = new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addItemName(soulId);
				activeChar.sendPacket(msg);
			}

			// Blessed Spirishots first, then Spirishots.
			if ((bspiritId > -1) && activeChar.getInventory().getCountOf(bspiritId) > 100)
			{
				activeChar.addAutoSoulShot(bspiritId);
				activeChar.sendPacket(new ExAutoSoulShot(bspiritId, true));
				// Message
				L2GameServerPacket msg = new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addItemName(bspiritId);
				activeChar.sendPacket(msg);
			}
			else if ((spiritId > -1) && activeChar.getInventory().getCountOf(spiritId) > 100)
			{
				activeChar.addAutoSoulShot(spiritId);
				activeChar.sendPacket(new ExAutoSoulShot(spiritId, true));
				// Message
				L2GameServerPacket msg = new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addItemName(spiritId);
				activeChar.sendPacket(msg);
			}

			activeChar.autoShot();
		}
	}

	// Synerge - This packet can be used while the character is blocked
	@Override
	public boolean canBeUsedWhileBlocked()
	{
		return true;
	}
}