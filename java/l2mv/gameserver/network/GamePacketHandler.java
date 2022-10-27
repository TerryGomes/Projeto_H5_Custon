package l2mv.gameserver.network;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.net.nio.impl.IClientFactory;
import l2mv.commons.net.nio.impl.IMMOExecutor;
import l2mv.commons.net.nio.impl.IPacketHandler;
import l2mv.commons.net.nio.impl.MMOConnection;
import l2mv.commons.net.nio.impl.ReceivablePacket;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.network.clientpackets.*;

public final class GamePacketHandler implements IPacketHandler<GameClient>, IClientFactory<GameClient>, IMMOExecutor<GameClient>
{
	private static final Logger _log = LoggerFactory.getLogger(GamePacketHandler.class);

	@Override
	public ReceivablePacket<GameClient> handlePacket(ByteBuffer buf, GameClient client)
	{
		int id = buf.get() & 0xFF;

		ReceivablePacket<GameClient> msg = null;

		try
		{
			int id2 = 0;
			parada:
			switch (client.getState())
			{
			case CONNECTED:
				switch (id)
				{
				case 0x00:
					msg = new RequestStatus(); // RequestGameStart
					break parada;
				case 0x0e:
					msg = new ProtocolVersion();
					break parada;
				case 0x2b:
					msg = new AuthLogin();
					break parada;
				case 0xCB:
					// msg = new GameGuardReply();
					break parada;
				case 0x9F:
					break parada;
				default:
					client.onUnknownPacket();
					break parada;
				}
			case AUTHED:
				switch (id)
				{
				case 0x00:
					msg = new Logout();
					break parada;
				case 0x0c:
					msg = new CharacterCreate(); // RequestCharacterCreate();
					break parada;
				case 0x0d:
					msg = new CharacterDelete(); // RequestCharacterDelete();
					break parada;
				case 0x12:
					msg = new CharacterSelected(); // CharacterSelect();
					break parada;
				case 0x13:
					msg = new NewCharacter(); // RequestNewCharacter();
					break parada;
				case 0x7b:
					msg = new CharacterRestore(); // RequestCharacterRestore();
					break parada;
				case 0xCB:
					// msg = new GameGuardReply();
					break parada;
				case 0xd0:
					int id3 = buf.getShort() & 0xffff;
					switch (id3)
					{
					case 0x36:
						msg = new GotoLobby();
						break parada;
					case 0x93:
						msg = new RequestEx2ndPasswordCheck();
						break parada;
					case 0x94:
						msg = new RequestEx2ndPasswordVerify();
						break parada;
					case 0x95:
						msg = new RequestEx2ndPasswordReq();
						break parada;
					default:
						client.onUnknownPacket();
						break parada;
					}
				default:
					client.onUnknownPacket();
					break parada;
				}

			case ENTER_GAME:
				switch (id)
				{
				case 0x00:
					msg = new Logout();
					break parada;
				case 0x11:
					msg = new EnterWorld();
					break parada;
				case 0xb1:
					msg = new NetPing();
					break parada;
				case 0xcb:
					// msg = new ReplyGameGuardQuery();
					break parada;
				case 0xd0:
					int id3 = buf.getShort() & 0xffff;
					switch (id3)
					{
					case 0x01:
						msg = new RequestManorList();
						break parada;
					case 0x21:
						msg = new RequestKeyMapping();
						break parada;
					case 0x3E:
						msg = new RequestAllAgitInfo();
						break parada;
					case 0x3C:
						msg = new RequestAllCastleInfo();
						break parada;
					case 0x3D:
						msg = new RequestAllFortressInfo();
						break parada;
					default:
						client.onUnknownPacket();
						break parada;
					}
				default:
					client.onUnknownPacket();
					break parada;
				}

			case IN_GAME:
				switch (id)
				{
				case 0x00:
					msg = new Logout();
					break parada;
				case 0x01:
					msg = new AttackRequest();
					break parada;
				case 0x03:
					msg = new RequestStartPledgeWar();
					break parada;
				case 0x04:
					msg = new RequestReplyStartPledgeWar();
					break parada;
				case 0x05:
					msg = new RequestStopPledgeWar();
					break parada;
				case 0x06:
					msg = new RequestReplyStopPledgeWar();
					break parada;
				case 0x07:
					msg = new RequestSurrenderPledgeWar();
					break parada;
				case 0x08:
					msg = new RequestReplySurrenderPledgeWar();
					break parada;
				case 0x09:
					msg = new RequestSetPledgeCrest();
					break parada;
				case 0x0b:
					msg = new RequestGiveNickName();
					break parada;
				case 0x0f:
					msg = new MoveBackwardToLocation();
					break parada;
				case 0x10:
					// msg = new Say(); Format: cS // verificar ?
					break parada;
				case 0x11:
					msg = new EnterWorld();
					break parada;
				case 0x14:
					msg = new RequestItemList();
					break parada;
				case 0x15:
					// msg = new RequestEquipItem(); // // verificar ?
					break parada;
				case 0x16:
					msg = new RequestUnEquipItem();
					break parada;
				case 0x17:
					msg = new RequestDropItem();
					break parada;
				case 0x19:
					msg = new UseItem();
					break parada;
				case 0x1a:
					msg = new TradeRequest();
					break parada;
				case 0x1b:
					msg = new AddTradeItem();
					break parada;
				case 0x1c:
					msg = new TradeDone();
					break parada;
				case 0x1f:
					msg = new Action();
					break parada;
				case 0x22:
					// msg = new RequestLinkHtml();
					break parada;
				case 0x23:
					msg = new RequestBypassToServer();
					break parada;
				case 0x24:
					msg = new RequestBBSwrite(); // RequestBBSWrite();
					break parada;
				case 0x25:
					msg = new RequestCreatePledge();
					break parada;
				case 0x26:
					msg = new RequestJoinPledge();
					break parada;
				case 0x27:
					msg = new RequestAnswerJoinPledge();
					break parada;
				case 0x28:
					msg = new RequestWithdrawalPledge();
					break parada;
				case 0x29:
					msg = new RequestOustPledgeMember();
					break parada;
				case 0x2c:
					msg = new RequestGetItemFromPet();
					break parada;
				case 0x2e:
					msg = new RequestAllyInfo();
					break parada;
				case 0x2f:
					msg = new RequestCrystallizeItem();
					break parada;
				case 0x30:
					// msg = new RequestPrivateStoreSellManageList(); // RequestPrivateStoreManage, устарел verificar
					break parada;
				case 0x31:
					msg = new SetPrivateStoreSellList();
					break parada;
				case 0x33:
					msg = new RequestTeleport();
					break parada;
				case 0x34:
					// msg = new RequestSocialAction();SocialAction
					break parada;
				case 0x35:
					// ChangeMoveType, устарел
					break parada;
				case 0x36:
					// ChangeWaitType, устарел
					break parada;
				case 0x37:
					msg = new RequestSellItem();
					break parada;
				case 0x38:
					msg = new RequestMagicSkillList();// UserAck
					break parada;
				case 0x39:
					msg = new RequestMagicSkillUse();
					break parada;
				case 0x3a:
					msg = new Appearing(); // Appering();
					break parada;
				case 0x3b:
					if (Config.ALLOW_WAREHOUSE)
					{
						msg = new SendWareHouseDepositList();
					}
					break parada;
				case 0x3c:
					msg = new SendWareHouseWithDrawList();
					break parada;
				case 0x3d:
					msg = new RequestShortCutReg();
					break parada;
				case 0x3e:
					// msg = new RequestShortCutUse(); // verificar
					break parada;
				case 0x3f:
					msg = new RequestShortCutDel();
					break parada;
				case 0x40:
					msg = new RequestBuyItem();
					break parada;
				case 0x41:
					// msg = new RequestDismissPledge(); //verificar
					break parada;
				case 0x42:
					msg = new RequestJoinParty();
					break parada;
				case 0x43:
					msg = new RequestAnswerJoinParty();
					break parada;
				case 0x44:
					msg = new RequestWithDrawalParty();
					break parada;
				case 0x45:
					msg = new RequestOustPartyMember();
					break parada;
				case 0x46:
					msg = new RequestDismissParty();
					break parada;
				case 0x47:
					msg = new CannotMoveAnymore();
					break parada;
				case 0x48:
					msg = new RequestTargetCanceld();
					break parada;
				case 0x49:
					msg = new Say2C();
					break parada;
				case 0x4d:
					msg = new RequestPledgeMemberList();
					break parada;
				case 0x4f:
					// msg = new RequestMagicItem(); // verificar
					break parada;
				case 0x50:
					msg = new RequestSkillList(); // trigger
					break parada;
				case 0x52:
					msg = new MoveWithDelta();
					break parada;
				case 0x53:
					msg = new RequestGetOnVehicle();
					break parada;
				case 0x54:
					msg = new RequestGetOffVehicle();
					break parada;
				case 0x55:
					msg = new AnswerTradeRequest();
					break parada;
				case 0x56:
					msg = new RequestActionUse();
					break parada;
				case 0x57:
					msg = new RequestRestart();
					break parada;
				case 0x58:
					msg = new RequestSiegeInfo();
					break parada;
				case 0x59:
					msg = new ValidatePosition();
					break parada;
				case 0x5a:
					msg = new RequestSEKCustom();
					break parada;
				case 0x5b:
					msg = new StartRotatingC();
					break parada;
				case 0x5c:
					msg = new FinishRotatingC();
					break parada;
				case 0x5e:
					msg = new RequestShowBoard();
					break parada;
				case 0x5f:
					msg = new RequestEnchantItem();
					break parada;
				case 0x60:
					msg = new RequestDestroyItem();
					break parada;
				case 0x62:
					msg = new RequestQuestList();
					break parada;
				case 0x63:
					msg = new RequestQuestAbort(); // RequestDestroyQuest();
					break parada;
				case 0x65:
					msg = new RequestPledgeInfo();
					break parada;
				case 0x66:
					msg = new RequestPledgeExtendedInfo();
					break parada;
				case 0x67:
					msg = new RequestPledgeCrest();
					break parada;
				case 0x69:
					// msg = new RequestSurrenderPersonally(); // verificar
					break parada;
				case 0x6a:
					// msg = new RequestFriendInfoList(); //verificar
					break parada;
				case 0x6b:
					msg = new RequestSendL2FriendSay();
					break parada;
				case 0x6c:
					msg = new RequestShowMiniMap(); // RequestOpenMinimap();
					break parada;
				case 0x6d:
					msg = new RequestSendMsnChatLog();
					break parada;
				case 0x6e:
					msg = new RequestReload(); // record video
					break parada;
				case 0x6f:
					msg = new RequestHennaEquip();
					break parada;
				case 0x70:
					msg = new RequestHennaUnequipList();
					break parada;
				case 0x71:
					msg = new RequestHennaUnequipInfo();
					break parada;
				case 0x72:
					msg = new RequestHennaUnequip();
					break parada;
				case 0x73:
					msg = new RequestAquireSkillInfo(); // RequestAcquireSkillInfo();
					break parada;
				case 0x74:
					msg = new SendBypassBuildCmd();
					break parada;
				case 0x75:
					msg = new RequestMoveToLocationInVehicle();
					break parada;
				case 0x76:
					msg = new CannotMoveAnymoreInVehicle();
					break parada;
				case 0x77:
					msg = new RequestFriendInvite();
					break parada;
				case 0x78:
					msg = new RequestFriendAddReply();
					break parada;
				case 0x7a:
					msg = new RequestFriendDel();
					break parada;
				case 0x7c:
					msg = new RequestAquireSkill();
					break parada;
				case 0x7d:
					msg = new RequestRestartPoint();
					break parada;
				case 0x7e:
					msg = new RequestGMCommand();
					break parada;
				case 0x7f:
					msg = new RequestPartyMatchConfig();
					break parada;
				case 0x80:
					msg = new RequestPartyMatchList();
					break parada;
				case 0x81:
					msg = new RequestPartyMatchDetail();
					break parada;
				case 0x83:
					msg = new RequestPrivateStoreBuy();
					break parada;
				case 0x84:
					// msg = new RequestReviveReply(); // verificar
					break parada;
				case 0x85:
					msg = new RequestTutorialLinkHtml();
					break parada;
				case 0x86:
					msg = new RequestTutorialPassCmdToServer();
					break parada;
				case 0x87:
					msg = new RequestTutorialQuestionMark(); // RequestTutorialQuestionMarkPressed();
					break parada;
				case 0x88:
					msg = new RequestTutorialClientEvent();
					break parada;
				case 0x89:
					msg = new RequestPetition();
					break parada;
				case 0x8a:
					msg = new RequestPetitionCancel();
					break parada;
				case 0x8b:
					msg = new RequestGmList();
					break parada;
				case 0x8c:
					msg = new RequestJoinAlly();
					break parada;
				case 0x8d:
					msg = new RequestAnswerJoinAlly();
					break parada;
				case 0x8e:
					msg = new RequestWithdrawAlly();
					break parada;
				case 0x8f:
					msg = new RequestOustAlly();
					break parada;
				case 0x90:
					msg = new RequestDismissAlly();
					break parada;
				case 0x91:
					msg = new RequestSetAllyCrest();
					break parada;
				case 0x92:
					msg = new RequestAllyCrest();
					break parada;
				case 0x93:
					msg = new RequestChangePetName();
					break parada;
				case 0x94:
					msg = new RequestPetUseItem();
					break parada;
				case 0x95:
					msg = new RequestGiveItemToPet();
					break parada;
				case 0x96:
					msg = new RequestPrivateStoreQuitSell();
					break parada;
				case 0x97:
					msg = new SetPrivateStoreMsgSell();
					break parada;
				case 0x98:
					msg = new RequestPetGetItem();
					break parada;
				case 0x99:
					msg = new RequestPrivateStoreBuyManage();
					break parada;
				case 0x9a:
					msg = new SetPrivateStoreBuyList();
					break parada;
				case 0x9b:
					// msg = new ReplyStopAllianceWar();
					break parada;
				case 0x9c:
					msg = new RequestPrivateStoreQuitBuy();
					break parada;
				case 0x9d:
					msg = new SetPrivateStoreMsgBuy();
					break parada;
				case 0x9f:
					msg = new RequestPrivateStoreBuySellList();
					break parada;
				case 0xa0:
					msg = new RequestTimeCheck();
					break parada;
				case 0xa6:
					msg = new RequestSkillCoolTime(); // Deprecated ? verificar
					break parada;
				case 0xa7:
					msg = new RequestPackageSendableItemList();
					break parada;
				case 0xa8:
					msg = new RequestPackageSend();
					break parada;
				case 0xa9:
					msg = new RequestBlock();
					break parada;
				case 0xaa:
					// msg = new RequestCastleSiegeInfo(); // verificar
					break parada;
				case 0xab:
					msg = new RequestCastleSiegeAttackerList();
					break parada;
				case 0xac:
					msg = new RequestCastleSiegeDefenderList();
					break parada;
				case 0xad:
					msg = new RequestJoinCastleSiege();
					break parada;
				case 0xae:
					msg = new RequestConfirmCastleSiegeWaitingList();
					break parada;
				case 0xaf:
					msg = new RequestSetCastleSiegeTime();
					break parada;
				case 0xb0:
					msg = new RequestMultiSellChoose();
					break parada;
				case 0xb1:
					msg = new NetPing();
					break parada;
				case 0xb2:
					msg = new RequestRemainTime();
					break parada;
				case 0xb3:
					msg = new BypassUserCmd();
					break parada;
				case 0xb4:
					msg = new SnoopQuit();
					break parada;
				case 0xb5:
					msg = new RequestRecipeBookOpen();
					break parada;
				case 0xb6:
					msg = new RequestRecipeItemDelete();
					break parada;
				case 0xb7:
					msg = new RequestRecipeItemMakeInfo();
					break parada;
				case 0xb8:
					msg = new RequestRecipeItemMakeSelf();
					break parada;
				case 0xb9:
					// msg = new RequestRecipeShopManageList(); deprecated // verificar
					break parada;
				case 0xba:
					msg = new RequestRecipeShopMessageSet();
					break parada;
				case 0xbb:
					msg = new RequestRecipeShopListSet();
					break parada;
				case 0xbc:
					msg = new RequestRecipeShopManageQuit();
					break parada;
				case 0xbd:
					msg = new RequestRecipeShopManageCancel();
					break parada;
				case 0xbe:
					msg = new RequestRecipeShopMakeInfo();
					break parada;
				case 0xbf:
					msg = new RequestRecipeShopMakeDo();
					break parada;
				case 0xc0:
					msg = new RequestRecipeShopSellList();
					break parada;
				case 0xc1:
					msg = new RequestObserverEnd();
					break parada;
				case 0xc2:
					// msg = new VoteSociality(); // Recommend
					break parada;
				case 0xc3:
					msg = new RequestHennaList(); // RequestHennaItemList();
					break parada;
				case 0xc4:
					msg = new RequestHennaItemInfo();
					break parada;
				case 0xc5:
					msg = new RequestBuySeed();
					break parada;
				case 0xc6:
					msg = new ConfirmDlg();
					break parada;
				case 0xc7:
					msg = new RequestPreviewItem();
					break parada;
				case 0xc8:
					msg = new RequestSSQStatus();
					break parada;
				case 0xc9:
					msg = new PetitionVote();
					break parada;
				case 0xcb:
					// msg = new GameGuardReply();
					break parada;
				case 0xcc:
					msg = new RequestPledgePower();
					break parada;
				case 0xcd:
					msg = new RequestMakeMacro();
					break parada;
				case 0xce:
					msg = new RequestDeleteMacro();
					break parada;
				case 0xcf:
					msg = new RequestProcureCrop(); // ?
					break parada;
				case 0xd0:
					int id3 = buf.getShort() & 0xffff;
					switch (id3)
					{
					case 0x01:
						msg = new RequestManorList();
						break parada;
					case 0x02:
						msg = new RequestProcureCropList();
						break parada;
					case 0x03:
						msg = new RequestSetSeed();
						break parada;
					case 0x04:
						msg = new RequestSetCrop();
						break parada;
					case 0x05:
						msg = new RequestWriteHeroWords();
						break parada;
					case 0x06:
						msg = new RequestExMPCCAskJoin(); // RequestExAskJoinMPCC();
						break parada;
					case 0x07:
						msg = new RequestExMPCCAcceptJoin(); // RequestExAcceptJoinMPCC();
						break parada;
					case 0x08:
						msg = new RequestExOustFromMPCC();
						break parada;
					case 0x09:
						msg = new RequestOustFromPartyRoom();
						break parada;
					case 0x0a:
						msg = new RequestDismissPartyRoom();
						break parada;
					case 0x0b:
						msg = new RequestWithdrawPartyRoom();
						break parada;
					case 0x0c:
						msg = new RequestHandOverPartyMaster();
						break parada;
					case 0x0d:
						msg = new RequestAutoSoulShot();
						break parada;
					case 0x0e:
						msg = new RequestExEnchantSkillInfo();
						break parada;
					case 0x0f:
						msg = new RequestExEnchantSkill();
						break parada;
					case 0x10:
						msg = new RequestPledgeCrestLarge();
						break parada;
					case 0x11:
						msg = new RequestSetPledgeCrestLarge();
						break parada;
					case 0x12:
						msg = new RequestPledgeSetAcademyMaster();
						break parada;
					case 0x13:
						msg = new RequestPledgePowerGradeList();
						break parada;
					case 0x14:
						msg = new RequestPledgeMemberPowerInfo();
						break parada;
					case 0x15:
						msg = new RequestPledgeSetMemberPowerGrade();
						break parada;
					case 0x16:
						msg = new RequestPledgeMemberInfo();
						break parada;
					case 0x17:
						msg = new RequestPledgeWarList();
						break parada;
					case 0x18:
						msg = new RequestExFishRanking();
						break parada;
					case 0x19:
						msg = new RequestPCCafeCouponUse();
						break parada;
					case 0x1a:
						// msg = new RequestExOrcMove(); // verificar
						break parada;
					case 0x1b:
						msg = new RequestDuelStart();
						break parada;
					case 0x1c:
						msg = new RequestDuelAnswerStart();
						break parada;
					case 0x1d:
						msg = new RequestTutorialClientEvent();
						break parada;
					case 0x1e:
						msg = new RequestExRqItemLink(); // chat item links
						break parada;
					case 0x1f:
						// msg = new RequestCannotMoveAnymoreAirShip(); verificar

						// CanNotMoveAnymore(AirShip)
						// format: (ch)ddddd
						break parada;
					case 0x20:
						msg = new RequestExMoveToLocationInAirShip();
						break parada;
					case 0x21:
						msg = new RequestKeyMapping();
						break parada;
					case 0x22:
						msg = new RequestSaveKeyMapping();
						break parada;
					case 0x23:
						msg = new RequestExRemoveItemAttribute();
						break parada;
					case 0x24:
						msg = new RequestSaveInventoryOrder(); // сохранение порядка инвентаря
						break parada;
					case 0x25:
						msg = new RequestExitPartyMatchingWaitingRoom();
						break parada;
					case 0x26:
						msg = new RequestConfirmTargetItem();
						break parada;
					case 0x27:
						msg = new RequestConfirmRefinerItem();
						break parada;
					case 0x28:
						msg = new RequestConfirmGemStone();
						break parada;
					case 0x29:
						msg = new RequestOlympiadObserverEnd();
						break parada;
					case 0x2a:
						msg = new RequestCursedWeaponList();
						break parada;
					case 0x2b:
						msg = new RequestCursedWeaponLocation();
						break parada;
					case 0x2c:
						msg = new RequestPledgeReorganizeMember();
						break parada;
					case 0x2d:
						msg = new RequestExMPCCShowPartyMembersInfo();
						break parada;
					case 0x2e:
						msg = new RequestExOlympiadObserverEnd(); // не уверен (в клиенте называется RequestOlympiadMatchList)
						break parada;
					case 0x2f:
						msg = new RequestAskJoinPartyRoom();
						break parada;
					case 0x30:
						msg = new AnswerJoinPartyRoom();
						break parada;
					case 0x31:
						msg = new RequestListPartyMatchingWaitingRoom();
						break parada;
					case 0x32:
						msg = new RequestExEnchantSkillSafe();
						break parada;
					case 0x33:
						msg = new RequestExEnchantSkillUntrain();
						break parada;
					case 0x34:
						msg = new RequestExEnchantSkillRouteChange();
						break parada;
					case 0x35:
						msg = new RequestEnchantItemAttribute();
						break parada;
					case 0x36:
						msg = new RequestExGetOnAirShip(); // verificar
						break parada;
					case 0x37:
						msg = new RequestExGetOffAirShip(); // verificar
						break parada;
					case 0x38:
						msg = new RequestExMoveToLocationAirShip();
						break parada;
					case 0x39:
						msg = new RequestBidItemAuction();
						break parada;
					case 0x3a:
						msg = new RequestInfoItemAuction();
						break parada;
					case 0x3b:
						msg = new RequestExChangeName();
						break parada;
					case 0x3c:
						msg = new RequestAllCastleInfo();
						break parada;
					case 0x3d:
						msg = new RequestAllFortressInfo();
						break parada;
					case 0x3e:
						msg = new RequestAllAgitInfo();
						break parada;
					case 0x3f:
						msg = new RequestFortressSiegeInfo();
						break parada;
					case 0x40:
						msg = new RequestGetBossRecord();
						break parada;
					case 0x41:
						msg = new RequestRefine();
						break parada;
					case 0x42:
						msg = new RequestConfirmCancelItem();
						break parada;
					case 0x43:
						msg = new RequestRefineCancel();
						break parada;
					case 0x44:
						msg = new RequestExMagicSkillUseGround();
						break parada;
					case 0x45:
						msg = new RequestDuelSurrender();
						break parada;
					case 0x46:
						msg = new RequestExEnchantSkillInfoDetail();
						break parada;
					case 0x48:
						msg = new RequestFortressMapInfo();
						break parada;
					case 0x49:
						msg = new RequestPVPMatchRecord();
						break parada;
					case 0x4a:
						msg = new SetPrivateStoreWholeMsg();
						break parada;
					case 0x4b:
						msg = new RequestDispel();
						break parada;
					case 0x4c:
						msg = new RequestExTryToPutEnchantTargetItem();
						break parada;
					case 0x4d:
						msg = new RequestExTryToPutEnchantSupportItem();
						break parada;
					case 0x4e:
						msg = new RequestExCancelEnchantItem();
						break parada;
					case 0x4f:
						msg = new RequestChangeNicknameColor();
						break parada;
					case 0x50:
						msg = new RequestResetNickname();
						break parada;
					case 0x51:
						int id4 = buf.getInt();
						switch (id4)
						{
						case 0x00:
							msg = new RequestBookMarkSlotInfo();
							break parada;
						case 0x01:
							msg = new RequestSaveBookMarkSlot();
							break parada;
						case 0x02:
							msg = new RequestModifyBookMarkSlot();
							break parada;
						case 0x03:
							msg = new RequestDeleteBookMarkSlot();
							break parada;
						case 0x04:
							msg = new RequestTeleportBookMark();
							break parada;
						case 0x05:
							msg = new RequestChangeBookMarkSlot();
							break parada;
						default:
							client.onUnknownPacket();
							break parada;
						}
					case 0x52:
						msg = new RequestWithDrawPremiumItem();
						break parada;
					case 0x53:
						msg = new RequestExJump();
						break parada;
					case 0x54:
						msg = new RequestExStartShowCrataeCubeRank();
						break parada;
					case 0x55:
						msg = new RequestExStopShowCrataeCubeRank();
						break parada;
					case 0x56:
						msg = new NotifyStartMiniGame();
						break parada;
					case 0x57:
						msg = new RequestExJoinDominionWar();
						break parada;
					case 0x58:
						msg = new RequestExDominionInfo();
						break parada;
					case 0x59:
						msg = new RequestExCleftEnter();
						break parada;
					case 0x5A:
						msg = new RequestExCubeGameChangeTeam();
						break parada;
					case 0x5B:
						msg = new RequestExEndScenePlayer();
						break parada;
					case 0x5C:
						msg = new RequestExCubeGameReadyAnswer();
						break parada;
					case 0x5D:
						msg = new RequestExListMpccWaiting();
						break parada;
					case 0x5E:
						msg = new RequestExManageMpccRoom();
						break parada;
					case 0x5F:
						msg = new RequestExJoinMpccRoom();
						break parada;
					case 0x60:
						msg = new RequestExOustFromMpccRoom();
						break parada;
					case 0x61:
						msg = new RequestExDismissMpccRoom();
						break parada;
					case 0x62:
						msg = new RequestExWithdrawMpccRoom();
						break parada;
					case 0x63:
						msg = new RequestExSeedPhase();
						break parada;
					case 0x64:
						msg = new RequestExMpccPartymasterList();
						break parada;
					case 0x65:
						msg = new RequestExPostItemList();
						break parada;
					case 0x66:
						msg = new RequestExSendPost();
						break parada;
					case 0x67:
						msg = new RequestExRequestReceivedPostList();
						break parada;
					case 0x68:
						msg = new RequestExDeleteReceivedPost();
						break parada;
					case 0x69:
						msg = new RequestExRequestReceivedPost();
						break parada;
					case 0x6A:
						msg = new RequestExReceivePost();
						break parada;
					case 0x6B:
						msg = new RequestExRejectPost();
						break parada;
					case 0x6C:
						msg = new RequestExRequestSentPostList();
						break parada;
					case 0x6D:
						msg = new RequestExDeleteSentPost();
						break parada;
					case 0x6E:
						msg = new RequestExRequestSentPost();
						break parada;
					case 0x6F:
						msg = new RequestExCancelSentPost();
						break parada;
					case 0x70:
						msg = new RequestExShowNewUserPetition();
						break parada;
					case 0x71:
						msg = new RequestExShowStepTwo();
						break parada;
					case 0x72:
						msg = new RequestExShowStepThree();
						break parada;
					case 0x73:
						// msg = new RequestExConnectToRaidServer(); //ExRaidReserveResult verificar
						break parada;
					case 0x74:
						// msg = new RequestExReturnFromRaidServer(); //ExRaidReserveResult verificar
						break parada;
					case 0x75:
						msg = new RequestExRefundItem();
						break parada;
					case 0x76:
						msg = new RequestExBuySellUIClose();
						break parada;
					case 0x77:
						msg = new RequestExEventMatchObserverEnd();
						break parada;
					case 0x78:
						msg = new RequestPartyLootModification();
						break parada;
					case 0x79:
						msg = new AnswerPartyLootModification();
						break parada;
					case 0x7A:
						msg = new AnswerCoupleAction();
						break parada;
					case 0x7B:
						msg = new RequestExBR_EventRankerList();
						break parada;
					case 0x7C:
						// msg = new RequestAskMemberShip(); // verificar
						break parada;
					case 0x7D:
						msg = new RequestAddExpandQuestAlarm();
						break parada;
					case 0x7E:
						msg = new RequestVoteNew();
						break parada;
					case 0x83:
						int id5 = buf.getInt();
						switch (id5)
						{
						case 0x01:
							// msg = new RequestExAgitInitialize(); //msg = new RequestExAgitInitialize chd 0x01
							break parada;
						case 0x02:
							// msg = new RequestExAgitDetailInfo();// msg = new RequestExAgitDetailInfo chdcd 0x02
							break parada;
						case 0x03:
							// msg = new RequestExMyAgitState();// msg = new RequestExMyAgitState chd 0x03
							break parada;
						case 0x04:
							// msg = new RequestExRegisterAgitForBidStep1();// msg = new RequestExRegisterAgitForBidStep1 chd 0x04
							break parada;
						case 0x05:
							// msg = new RequestExRegisterAgitForBidStep3(); // msg = new RequestExRegisterAgitForBidStep1(); break parada;
						case 0x07:
							// msg = new RequestExConfirmCancelRegisteringAgit();// msg = new RequestExConfirmCancelRegisteringAgit chd 0x07
							break parada;
						case 0x08:
							// msg = new RequestExProceedCancelRegisteringAgit();// msg = new RequestExProceedCancelRegisteringAgit chd 0x08
							break parada;
						case 0x09:
							// msg = new RequestExConfirmCancelAgitLot(); // msg = new RequestExConfirmCancelAgitBid chdd 0x09
							break parada;
						case 0x0A:
							// msg = new RequestExProceedCancelAgitLot(); // msg = new RequestExApplyForBidStep1 chdd 0x0D
							break parada;
						case 0x0D:
							// msg = new RequestExApplyForBidStep1(); // msg = new RequestExApplyForBidStep2 chddQ 0x0E
							break parada;
						case 0x0E:
							// msg = new RequestExApplyForBidStep2(); // msg = new RequestExApplyForBidStep3 chddQ 0x0F
							break parada;
						case 0x0F:
							// msg = new RequestExApplyForBidStep3(); // msg = new RequestExConfirmCancelAgitLot chdc 0x09
							break parada;
						case 0x10:
							// msg = new RequestExReBid(); // msg = new RequestExProceedCancelAgitLot chdc 0x0A
							break parada;
						case 0x11:
							// msg = new RequestExAgitListForLot(); // msg = new RequestExProceedCancelAgitBid chdd 0x0A
							break parada;
						case 0x12:
							// msg = new RequestExApplyForAgitLotStep1(); // msg = new RequestExProceedCancelAgitBid chdd 0x0A
							break parada;
						case 0x13:
							// msg = new RequestExApplyForAgitLotStep2(); // msg = new RequestExProceedCancelAgitBid chdd 0x0A
							break parada;
						case 0x14:
							// msg = new RequestExAgitListForBid(); // msg = new RequestExProceedCancelAgitBid chdd 0x0A
							break parada;
						}
						break parada;
					case 0x84:
						msg = new RequestExAddPostFriendForPostBox();
						break parada;
					case 0x85:
						msg = new RequestExDeletePostFriendForPostBox();
						break parada;
					case 0x86:
						msg = new RequestExShowPostFriendListForPostBox();
						break parada;
					case 0x87:
						msg = new RequestExFriendListForPostBox();
						break parada;
					case 0x88:
						msg = new RequestOlympiadMatchList();
						break parada;
					case 0x89:
						msg = new RequestExBR_GamePoint();
						break parada;
					case 0x8A:
						msg = new RequestExBR_ProductList();
						break parada;
					case 0x8B:
						msg = new RequestExBR_ProductInfo();
						break parada;
					case 0x8C:
						msg = new RequestExBR_BuyProduct();
						break parada;
					case 0x8D:
						msg = new RequestExBR_RecentProductList();
						break parada;
					case 0x8E:
						msg = new RequestBR_MiniGameLoadScores();
						break parada;
					case 0x8F:
						msg = new RequestBR_MiniGameInsertScore();
						break parada;
					case 0x90:
						msg = new RequestExBR_LectureMark();
						break parada;
					case 0x91:
						msg = new RequestGoodsInventoryInfo();
						break parada;
					case 0x92:
						// msg = new RequestUseGoodsInventoryItem(); // verificar
						break parada;
					case 0x96:
						// msg = new RequestHardWareInfo(); // verificar
						break parada;
					default:
						client.onUnknownPacket();
						break parada;
					}
				default:
				{
					client.onUnknownPacket();
					break parada;
				}
				}
			default:
				break;
			}
		}
		catch (BufferUnderflowException e)
		{
			client.onPacketReadFail();
		}

		if (Config.ALLOW_JUST_MOVING && msg != null && !msg.getClass().equals(MoveBackwardToLocation.class) && !msg.getClass().equals(SendBypassBuildCmd.class))
		{
			if (client.getActiveChar() != null)
			{
				client.getActiveChar().sendActionFailed();
			}
			return null;
		}

		/*
		 * // Synerge - Support for real character block. If the character is blocked, we wont be able to do anything until its unblocked. Certain packets do not have this condition
		 * if (msg != null && !msg.canBeUsedWhileBlocked() && client.getActiveChar() != null && !client.getActiveChar().isInObserverMode() && client.getActiveChar().isFullBlocked())
		 * return null;
		 */

		return msg;
	}

	@Override
	public GameClient create(MMOConnection<GameClient> con)
	{
		return new GameClient(con);
	}

	@Override
	public void execute(Runnable r)
	{
		ThreadPoolManager.getInstance().execute(r);
	}
}