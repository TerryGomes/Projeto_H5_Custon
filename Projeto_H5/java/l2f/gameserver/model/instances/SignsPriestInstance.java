package l2f.gameserver.model.instances;

import java.util.Calendar;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.time.cron.SchedulingPattern;
import l2f.gameserver.Config;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.SevenSigns;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.tables.ClanTable;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.ItemFunctions;

public class SignsPriestInstance extends NpcInstance
{
	private static final Logger _log = LoggerFactory.getLogger(SignsPriestInstance.class);

	public SignsPriestInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	private void showChatWindow(Player player, int val, String suffix, boolean isDescription)
	{
		String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH;
		filename += isDescription ? "desc_" + val : "signs_" + val;
		filename += suffix != null ? "_" + suffix + ".htm" : ".htm";
		showChatWindow(player, filename);
	}

	private boolean getPlayerAllyHasCastle(Player player)
	{
		Clan playerClan = player.getClan();

		if (playerClan == null)
		{
			return false;
		}

		// If castle ownage check is clan-based rather than ally-based,
		// check if the player's clan has a castle and return the result.
		if (!Config.ALT_GAME_REQUIRE_CLAN_CASTLE)
		{
			int allyId = playerClan.getAllyId();

			// The player's clan is not in an alliance, so return false.
			if (allyId != 0)
			{
				// Check if another clan in the same alliance owns a castle,
				// by traversing the list of clans and act accordingly.
				Clan[] clanList = ClanTable.getInstance().getClans();

				for (Clan clan : clanList)
				{
					if (clan.getAllyId() == allyId)
					{
						if (clan.getCastle() > 0)
						{
							return true;
						}
					}
				}
			}
		}
		return playerClan.getCastle() > 0;
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if ((getNpcId() == 31113) || (getNpcId() == 31126))
		{
			if ((SevenSigns.getInstance().getPlayerCabal(player) == SevenSigns.CABAL_NULL) && !player.isGM())
			{
				return;
			}
		}

		// first do the common stuff and handle the commands that all NPC classes know
		super.onBypassFeedback(player, command);

		if (command.startsWith("SevenSignsDesc"))
		{
			int val = Integer.parseInt(command.substring(15));

			showChatWindow(player, val, null, true);
		}
		else if (command.startsWith("SevenSigns"))
		{
			SystemMessage sm;
			String path;
			int cabal = SevenSigns.CABAL_NULL;
			int stoneType = 0;
			// int inventorySize = player.getInventory().getSize() + 1;
			ItemInstance ancientAdena = player.getInventory().getItemByItemId(SevenSigns.ANCIENT_ADENA_ID);
			long ancientAdenaAmount = ancientAdena == null ? 0 : ancientAdena.getCount();
			int val = Integer.parseInt(command.substring(11, 12).trim());

			if (command.length() > 12)
			{
				val = Integer.parseInt(command.substring(11, 13).trim());
			}

			if (command.length() > 13)
			{
				try
				{
					cabal = Integer.parseInt(command.substring(14, 15).trim());
				}
				catch (Exception e)
				{
					try
					{
						cabal = Integer.parseInt(command.substring(13, 14).trim());
					}
					catch (Exception e2)
					{
					}
				}
			}

			switch (val)
			{
			case 2: // Purchase Record of the Seven Signs
				if (!player.getInventory().validateCapacity(1))
				{
					player.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
					return;
				}

				if (SevenSigns.RECORD_SEVEN_SIGNS_COST > player.getAdena())
				{
					player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
					return;
				}

				player.reduceAdena(SevenSigns.RECORD_SEVEN_SIGNS_COST, true, "7S Record");
				player.getInventory().addItem(ItemFunctions.createItem(SevenSigns.RECORD_SEVEN_SIGNS_ID), "Seven Signs Record");
				player.sendPacket(SystemMessage2.obtainItems(SevenSigns.RECORD_SEVEN_SIGNS_ID, 1, 0));

				break;
			case 3: // Join Cabal Intro 1
			case 8: // Festival of Darkness Intro - SevenSigns x [0]1
				cabal = SevenSigns.getInstance().getPriestCabal(getNpcId());
				showChatWindow(player, val, SevenSigns.getCabalShortName(cabal), false);
				break;
			case 10: // Teleport Locations List
				cabal = SevenSigns.getInstance().getPriestCabal(getNpcId());
				if (SevenSigns.getInstance().isSealValidationPeriod())
				{
					showChatWindow(player, val, "", false);
				}
				else
				{
					showChatWindow(player, val, getParameters().getString("town", "no"), false);
				}
				break;
			case 4: // Join a Cabal - SevenSigns 4 [0]1 x
				int newSeal = Integer.parseInt(command.substring(15));
				int oldCabal = SevenSigns.getInstance().getPlayerCabal(player);

				if (oldCabal != SevenSigns.CABAL_NULL)
				{
					player.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2SignsPriestInstance.AlreadyMember", player).addString(SevenSigns.getCabalName(cabal)));
					return;
				}
				if (player.getClassId().level() == 0)
				{
					player.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2SignsPriestInstance.YouAreNewbie", player));
					break;
				}

				else if (player.getClassId().level() >= 2)
				{
					if (Config.ALT_GAME_REQUIRE_CASTLE_DAWN)
					{
						if (getPlayerAllyHasCastle(player))
						{
							if (cabal == SevenSigns.CABAL_DUSK)
							{
								player.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2SignsPriestInstance.CastleOwning", player));
								return;
							}
						}
						else
						/*
						 * If the player is trying to join the Lords of Dawn, check if they are carrying a Lord's certificate. If not then try to take the required amount of adena
						 * instead.
						 */
						if (cabal == SevenSigns.CABAL_DAWN)
						{
							boolean allowJoinDawn = false;

							if (Functions.getItemCount(player, SevenSigns.CERTIFICATE_OF_APPROVAL_ID) > 0)
							{
								Functions.removeItem(player, SevenSigns.CERTIFICATE_OF_APPROVAL_ID, 1, "Certificate of Approval");
								allowJoinDawn = true;
							}
							else if (Config.ALT_GAME_ALLOW_ADENA_DAWN && (player.getAdena() >= SevenSigns.ADENA_JOIN_DAWN_COST))
							{
								player.reduceAdena(SevenSigns.ADENA_JOIN_DAWN_COST, true, "7S Dawn Join");
								allowJoinDawn = true;
							}

							if (!allowJoinDawn)
							{
								if (Config.ALT_GAME_ALLOW_ADENA_DAWN)
								{
									player.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2SignsPriestInstance.CastleOwningCertificate", player));
								}
								else
								{
									player.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2SignsPriestInstance.CastleOwningCertificate2", player));
								}
								return;
							}
						}
					}
				}

				SevenSigns.getInstance().setPlayerInfo(player.getObjectId(), cabal, newSeal);
				if (cabal == SevenSigns.CABAL_DAWN)
				{
					player.sendPacket(Msg.YOU_WILL_PARTICIPATE_IN_THE_SEVEN_SIGNS_AS_A_MEMBER_OF_THE_LORDS_OF_DAWN); // Joined Dawn
				}
				else
				{
					player.sendPacket(Msg.YOU_WILL_PARTICIPATE_IN_THE_SEVEN_SIGNS_AS_A_MEMBER_OF_THE_REVOLUTIONARIES_OF_DUSK); // Joined Dusk
				}

				// Show a confirmation message to the user, indicating which seal they chose.
				switch (newSeal)
				{
				case SevenSigns.SEAL_AVARICE:
					player.sendPacket(Msg.YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_AVARICE_DURING_THIS_QUEST_EVENT_PERIOD);
					break;
				case SevenSigns.SEAL_GNOSIS:
					player.sendPacket(Msg.YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_GNOSIS_DURING_THIS_QUEST_EVENT_PERIOD);
					break;
				case SevenSigns.SEAL_STRIFE:
					player.sendPacket(Msg.YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_STRIFE_DURING_THIS_QUEST_EVENT_PERIOD);
					break;
				}
				showChatWindow(player, 4, SevenSigns.getCabalShortName(cabal), false);
				break;
			case 6: // Contribute Seal Stones - SevenSigns 6 x
				stoneType = Integer.parseInt(command.substring(13));
				ItemInstance redStones = player.getInventory().getItemByItemId(SevenSigns.SEAL_STONE_RED_ID);
				long redStoneCount = redStones == null ? 0 : redStones.getCount();
				ItemInstance greenStones = player.getInventory().getItemByItemId(SevenSigns.SEAL_STONE_GREEN_ID);
				long greenStoneCount = greenStones == null ? 0 : greenStones.getCount();
				ItemInstance blueStones = player.getInventory().getItemByItemId(SevenSigns.SEAL_STONE_BLUE_ID);
				long blueStoneCount = blueStones == null ? 0 : blueStones.getCount();
				long contribScore = SevenSigns.getInstance().getPlayerContribScore(player);
				boolean stonesFound = false;

				if (contribScore == SevenSigns.MAXIMUM_PLAYER_CONTRIB)
				{
					player.sendPacket(Msg.CONTRIBUTION_LEVEL_HAS_EXCEEDED_THE_LIMIT_YOU_MAY_NOT_CONTINUE);
				}
				else
				{
					long redContribCount = 0;
					long greenContribCount = 0;
					long blueContribCount = 0;

					switch (stoneType)
					{
					case 1:
						blueContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - contribScore) / SevenSigns.BLUE_CONTRIB_POINTS;
						if (blueContribCount > blueStoneCount)
						{
							blueContribCount = blueStoneCount;
						}
						break;
					case 2:
						greenContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - contribScore) / SevenSigns.GREEN_CONTRIB_POINTS;
						if (greenContribCount > greenStoneCount)
						{
							greenContribCount = greenStoneCount;
						}
						break;
					case 3:
						redContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - contribScore) / SevenSigns.RED_CONTRIB_POINTS;
						if (redContribCount > redStoneCount)
						{
							redContribCount = redStoneCount;
						}
						break;
					case 4:
						long tempContribScore = contribScore;
						redContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - tempContribScore) / SevenSigns.RED_CONTRIB_POINTS;
						if (redContribCount > redStoneCount)
						{
							redContribCount = redStoneCount;
						}
						tempContribScore += redContribCount * SevenSigns.RED_CONTRIB_POINTS;
						greenContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - tempContribScore) / SevenSigns.GREEN_CONTRIB_POINTS;
						if (greenContribCount > greenStoneCount)
						{
							greenContribCount = greenStoneCount;
						}
						tempContribScore += greenContribCount * SevenSigns.GREEN_CONTRIB_POINTS;
						blueContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - tempContribScore) / SevenSigns.BLUE_CONTRIB_POINTS;
						if (blueContribCount > blueStoneCount)
						{
							blueContribCount = blueStoneCount;
						}
						break;
					}
					if (redContribCount > 0)
					{
						if (player.getInventory().destroyItemByItemId(SevenSigns.SEAL_STONE_RED_ID, redContribCount, "AA Convert"))
						{
							stonesFound = true;
						}
					}
					if (greenContribCount > 0)
					{
						if (player.getInventory().destroyItemByItemId(SevenSigns.SEAL_STONE_GREEN_ID, greenContribCount, "AA Convert"))
						{
							stonesFound = true;
						}
					}
					if (blueContribCount > 0)
					{
						ItemInstance temp = player.getInventory().getItemByItemId(SevenSigns.SEAL_STONE_BLUE_ID);
						if (player.getInventory().destroyItemByItemId(SevenSigns.SEAL_STONE_BLUE_ID, blueContribCount, "AA Convert"))
						{
							stonesFound = true;
						}
					}

					if (!stonesFound)
					{
						player.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2SignsPriestInstance.DontHaveAnySSType", player));
						return;
					}

					contribScore = SevenSigns.getInstance().addPlayerStoneContrib(player, blueContribCount, greenContribCount, redContribCount);
					sm = new SystemMessage(SystemMessage.YOUR_CONTRIBUTION_SCORE_IS_INCREASED_BY_S1);
					sm.addNumber(contribScore);
					player.sendPacket(sm);

					showChatWindow(player, 6, null, false);
				}
				break;
			case 7: // Exchange Ancient Adena for Adena - SevenSigns 7 xxxxxxx
				long ancientAdenaConvert = 0;
				try
				{
					ancientAdenaConvert = Long.parseLong(command.substring(13).trim());
				}
				catch (NumberFormatException e)
				{
					player.sendMessage(new CustomMessage("common.IntegerAmount", player));
					return;
				}
				catch (StringIndexOutOfBoundsException e)
				{
					player.sendMessage(new CustomMessage("common.IntegerAmount", player));
					return;
				}

				if ((ancientAdenaAmount < ancientAdenaConvert) || (ancientAdenaConvert < 1))
				{
					player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
					return;
				}

				if (player.getInventory().destroyItemByItemId(SevenSigns.ANCIENT_ADENA_ID, ancientAdenaConvert, "AA Convert"))
				{
					player.addAdena(ancientAdenaConvert, "AA Convert");
					player.sendPacket(SystemMessage2.removeItems(5575, ancientAdenaConvert));
					player.sendPacket(SystemMessage2.obtainItems(57, ancientAdenaConvert, 0));
				}
				break;
			case 9: // Receive Contribution Rewards
				int playerCabal = SevenSigns.getInstance().getPlayerCabal(player);
				int winningCabal = SevenSigns.getInstance().getCabalHighestScore();

				if (SevenSigns.getInstance().isSealValidationPeriod() && (playerCabal == winningCabal))
				{
					int ancientAdenaReward = SevenSigns.getInstance().getAncientAdenaReward(player, true);

					if (ancientAdenaReward < 3)
					{
						showChatWindow(player, 9, "b", false);
						return;
					}

					ancientAdena = ItemFunctions.createItem(SevenSigns.ANCIENT_ADENA_ID);
					ancientAdena.setCount(ancientAdenaReward);
					player.getInventory().addItem(ancientAdena, "Receive Contribution Rewards");
					player.sendPacket(SystemMessage2.obtainItems(SevenSigns.ANCIENT_ADENA_ID, ancientAdenaReward, 0));
					showChatWindow(player, 9, "a", false);
				}
				break;
			case 11: // Teleport to Hunting Grounds - deprecated, instead use scripts_Util:QuestGatekeeper x y x 5575 price
				try
				{
					String portInfo = command.substring(14).trim();

					StringTokenizer st = new StringTokenizer(portInfo);
					int x = Integer.parseInt(st.nextToken());
					int y = Integer.parseInt(st.nextToken());
					int z = Integer.parseInt(st.nextToken());
					long ancientAdenaCost = Long.parseLong(st.nextToken());

					if (ancientAdenaCost > 0)
					{
						if (!player.getInventory().destroyItemByItemId(SevenSigns.ANCIENT_ADENA_ID, ancientAdenaCost, "AA Convert"))
						{
							player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
							return;
						}
					}
					player.teleToLocation(x, y, z);
				}
				catch (Exception e)
				{
					_log.warn("SevenSigns: Error occurred while teleporting player: " + e);
				}
				break;
			case 17: // Exchange Seal Stones for Ancient Adena (Type Choice) - SevenSigns 17 x
				stoneType = Integer.parseInt(command.substring(14));
				int stoneId = 0;
				long stoneCount = 0;
				int stoneValue = 0;
				String stoneColor = null;
				String content;

				// FIXME item-API
				if (stoneType == 4)
				{
					ItemInstance BlueStoneInstance = player.getInventory().getItemByItemId(SevenSigns.SEAL_STONE_BLUE_ID);
					long bcount = BlueStoneInstance != null ? BlueStoneInstance.getCount() : 0;
					ItemInstance GreenStoneInstance = player.getInventory().getItemByItemId(SevenSigns.SEAL_STONE_GREEN_ID);
					long gcount = GreenStoneInstance != null ? GreenStoneInstance.getCount() : 0;
					ItemInstance RedStoneInstance = player.getInventory().getItemByItemId(SevenSigns.SEAL_STONE_RED_ID);
					long rcount = RedStoneInstance != null ? RedStoneInstance.getCount() : 0;
					long ancientAdenaReward = SevenSigns.calcAncientAdenaReward(bcount, gcount, rcount);
					if (ancientAdenaReward > 0)
					{
						if (BlueStoneInstance != null)
						{
							player.getInventory().destroyItem(BlueStoneInstance, bcount, "AA Convert");
							player.sendPacket(SystemMessage2.removeItems(SevenSigns.SEAL_STONE_BLUE_ID, bcount));
						}
						if (GreenStoneInstance != null)
						{
							player.getInventory().destroyItem(GreenStoneInstance, gcount, "AA Convert");
							player.sendPacket(SystemMessage2.removeItems(SevenSigns.SEAL_STONE_GREEN_ID, gcount));
						}
						if (RedStoneInstance != null)
						{
							player.getInventory().destroyItem(RedStoneInstance, rcount, "AA Convert");
							player.sendPacket(SystemMessage2.removeItems(SevenSigns.SEAL_STONE_RED_ID, rcount));
						}

						ancientAdena = ItemFunctions.createItem(SevenSigns.ANCIENT_ADENA_ID);
						ancientAdena.setCount(ancientAdenaReward);
						player.getInventory().addItem(ancientAdena, "AA Convert");
						player.sendPacket(SystemMessage2.obtainItems(SevenSigns.ANCIENT_ADENA_ID, ancientAdenaReward, 0));
					}
					else
					{
						player.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2SignsPriestInstance.DontHaveAnySS", player));
					}
					break;
				}

				switch (stoneType)
				{
				case 1:
					stoneColor = "blue";
					stoneId = SevenSigns.SEAL_STONE_BLUE_ID;
					stoneValue = SevenSigns.SEAL_STONE_BLUE_VALUE;
					break;
				case 2:
					stoneColor = "green";
					stoneId = SevenSigns.SEAL_STONE_GREEN_ID;
					stoneValue = SevenSigns.SEAL_STONE_GREEN_VALUE;
					break;
				case 3:
					stoneColor = "red";
					stoneId = SevenSigns.SEAL_STONE_RED_ID;
					stoneValue = SevenSigns.SEAL_STONE_RED_VALUE;
					break;
				}
				ItemInstance stoneInstance = player.getInventory().getItemByItemId(stoneId);

				if (stoneInstance != null)
				{
					stoneCount = stoneInstance.getCount();
				}

				path = SevenSigns.SEVEN_SIGNS_HTML_PATH + "signs_17.htm";
				content = HtmCache.getInstance().getNotNull(path, player);

				if (content != null)
				{
					content = content.replaceAll("%stoneColor%", stoneColor);
					content = content.replaceAll("%stoneValue%", String.valueOf(stoneValue));
					content = content.replaceAll("%stoneCount%", String.valueOf(stoneCount));
					content = content.replaceAll("%stoneItemId%", String.valueOf(stoneId));

					NpcHtmlMessage html = new NpcHtmlMessage(player, this);
					html.setHtml(content);
					player.sendPacket(html);
				}
				else
				{
					_log.warn("Problem with HTML text " + SevenSigns.SEVEN_SIGNS_HTML_PATH + "signs_17.htm: " + path);
				}
				break;
			case 18: // Exchange Seal Stones for Ancient Adena - SevenSigns 18 xxxx xxxxxx
				int convertStoneId = Integer.parseInt(command.substring(14, 18));
				long convertCount = 0;

				try
				{
					convertCount = Long.parseLong(command.substring(19).trim());
				}
				catch (Exception NumberFormatException)
				{
					player.sendMessage(new CustomMessage("common.IntegerAmount", player));
					break;
				}

				ItemInstance convertItem = player.getInventory().getItemByItemId(convertStoneId);
				if (convertItem == null)
				{
					player.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2SignsPriestInstance.DontHaveAnySSType", player));
					break;
				}

				long totalCount = convertItem.getCount();
				long ancientAdenaReward = 0;
				if ((convertCount <= totalCount) && (convertCount > 0))
				{
					switch (convertStoneId)
					{
					case SevenSigns.SEAL_STONE_BLUE_ID:
						ancientAdenaReward = SevenSigns.calcAncientAdenaReward(convertCount, 0, 0);
						break;
					case SevenSigns.SEAL_STONE_GREEN_ID:
						ancientAdenaReward = SevenSigns.calcAncientAdenaReward(0, convertCount, 0);
						break;
					case SevenSigns.SEAL_STONE_RED_ID:
						ancientAdenaReward = SevenSigns.calcAncientAdenaReward(0, 0, convertCount);
						break;
					}

					if (player.getInventory().destroyItemByItemId(convertStoneId, convertCount, "AA Convert"))
					{
						ancientAdena = ItemFunctions.createItem(SevenSigns.ANCIENT_ADENA_ID);
						ancientAdena.setCount(ancientAdenaReward);
						player.getInventory().addItem(ancientAdena, "AA Convert");
						player.sendPacket(SystemMessage2.removeItems(convertStoneId, convertCount), SystemMessage2.obtainItems(SevenSigns.ANCIENT_ADENA_ID, ancientAdenaReward, 0));
					}
				}
				else
				{
					player.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2SignsPriestInstance.DontHaveSSAmount", player));
				}
				break;
			case 19: // Seal Information (for when joining a cabal)
				int chosenSeal = Integer.parseInt(command.substring(16));
				String fileSuffix = SevenSigns.getSealName(chosenSeal, true) + "_" + SevenSigns.getCabalShortName(cabal);

				showChatWindow(player, val, fileSuffix, false);
				break;
			case 20: // Seal Status (for when joining a cabal)
				StringBuilder contentBuffer = new StringBuilder("<html><body><font color=\"LEVEL\">[Seal Status]</font><br>");

				for (int i = 1; i < 4; i++)
				{
					int sealOwner = SevenSigns.getInstance().getSealOwner(i);
					if (sealOwner != SevenSigns.CABAL_NULL)
					{
						contentBuffer.append("[" + SevenSigns.getSealName(i, false) + ": " + SevenSigns.getCabalName(sealOwner) + "]<br>");
					}
					else
					{
						contentBuffer.append("[" + SevenSigns.getSealName(i, false) + ": Nothingness]<br>");
					}
				}

				contentBuffer.append("<a action=\"bypass -h npc_" + getObjectId() + "_SevenSigns 3 " + cabal + "\">Go back.</a></body></html>");

				NpcHtmlMessage html2 = new NpcHtmlMessage(player, this);
				html2.setHtml(contentBuffer.toString());
				player.sendPacket(html2);
				break;
			case 21: // Exchange Adena for Ancient Adena - High Five
				if (player.getLevel() < 60)
				{
					showChatWindow(player, 20, null, false);
					return;
				}
				if (player.getVarInt("bmarketadena", 0) >= 500000)
				{
					showChatWindow(player, 21, null, false);
					return;
				}
				Calendar sh = Calendar.getInstance();
				sh.set(Calendar.HOUR_OF_DAY, 20);
				sh.set(Calendar.MINUTE, 00);
				sh.set(Calendar.SECOND, 00);
				Calendar eh = Calendar.getInstance();
				eh.set(Calendar.HOUR_OF_DAY, 23);
				eh.set(Calendar.MINUTE, 59);
				eh.set(Calendar.SECOND, 59);
				if ((System.currentTimeMillis() > sh.getTimeInMillis()) && (System.currentTimeMillis() < eh.getTimeInMillis()))
				{
					showChatWindow(player, 23, null, false);
				}
				else
				{
					showChatWindow(player, 22, null, false);
				}
				break;
			case 22:
				long adenaConvert;
				int tradeMult = 4;
				int limit = 500000;
				try
				{
					adenaConvert = Long.parseLong(command.substring(14).trim());
				}
				catch (NumberFormatException e)
				{
					player.sendMessage(new CustomMessage("common.IntegerAmount", player));
					return;
				}
				catch (StringIndexOutOfBoundsException e)
				{
					player.sendMessage(new CustomMessage("common.IntegerAmount", player));
					return;
				}
				long adenaAmount = ItemFunctions.getItemCount(player, 57);
				int amountLimit = player.getVarInt("bmarketadena", 0);
				long result = adenaConvert / tradeMult;
				if ((adenaAmount < adenaConvert) || (adenaConvert < tradeMult)) // adenaConvert < tradeMult i.e. can't exchange if no AA will be given
				{
					player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
					return;
				}
				if (result > (limit - amountLimit))
				{
					player.sendMessage(new CustomMessage("common.LimitedAmount", player).addNumber(500000));
					return;
				}
				if (ItemFunctions.removeItem(player, 57, adenaConvert, true, "AA Convert") == adenaConvert)
				{
					SchedulingPattern reset = new SchedulingPattern("30 6 * * *");
					player.setVar("bmarketadena", player.getVarInt("bmarketadena") + result, reset.next(System.currentTimeMillis()));
					ItemFunctions.addItem(player, SevenSigns.ANCIENT_ADENA_ID, result, true, "AA Convert");
					showChatWindow(player, 24, null, false);
				}
				break;
			default:
				// 1 = Purchase Record Intro
				// 5 = Contrib Seal Stones Intro
				// 16 = Choose Type of Seal Stones to Convert

				showChatWindow(player, val, null, false);
				break;
			}
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		int npcId = getTemplate().npcId;

		String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH;

		int sealAvariceOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE);
		int sealGnosisOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS);
		int playerCabal = SevenSigns.getInstance().getPlayerCabal(player);
		boolean isSealValidationPeriod = SevenSigns.getInstance().isSealValidationPeriod();
		int compWinner = SevenSigns.getInstance().getCabalHighestScore();

		switch (npcId)
		{
		case 31078:
		case 31079:
		case 31080:
		case 31081:
		case 31082: // Dawn Priests
		case 31083:
		case 31084:
		case 31168:
		case 31692:
		case 31694:
		case 31997:
			switch (playerCabal)
			{
			case SevenSigns.CABAL_DAWN:
				if (isSealValidationPeriod)
				{
					if (compWinner == SevenSigns.CABAL_DAWN)
					{
						if (compWinner != sealGnosisOwner)
						{
							filename += "dawn_priest_2c.htm";
						}
						else
						{
							filename += "dawn_priest_2a.htm";
						}
					}
					else
					{
						filename += "dawn_priest_2b.htm";
					}
				}
				else
				{
					filename += "dawn_priest_1b.htm";
				}
				break;
			case SevenSigns.CABAL_DUSK:
				if (isSealValidationPeriod)
				{
					filename += "dawn_priest_3b.htm";
				}
				else
				{
					filename += "dawn_priest_3a.htm";
				}
				break;
			default:
				if (isSealValidationPeriod)
				{
					if (compWinner == SevenSigns.CABAL_DAWN)
					{
						filename += "dawn_priest_4.htm";
					}
					else
					{
						filename += "dawn_priest_2b.htm";
					}
				}
				else
				{
					filename += "dawn_priest_1a.htm";
				}
				break;
			}
			break;
		case 31085:
		case 31086:
		case 31087:
		case 31088: // Dusk Priest
		case 31089:
		case 31090:
		case 31091:
		case 31169:
		case 31693:
		case 31695:
		case 31998:
			switch (playerCabal)
			{
			case SevenSigns.CABAL_DUSK:
				if (isSealValidationPeriod)
				{
					if (compWinner == SevenSigns.CABAL_DUSK)
					{
						if (compWinner != sealGnosisOwner)
						{
							filename += "dusk_priest_2c.htm";
						}
						else
						{
							filename += "dusk_priest_2a.htm";
						}
					}
					else
					{
						filename += "dusk_priest_2b.htm";
					}
				}
				else
				{
					filename += "dusk_priest_1b.htm";
				}
				break;
			case SevenSigns.CABAL_DAWN:
				if (isSealValidationPeriod)
				{
					filename += "dusk_priest_3b.htm";
				}
				else
				{
					filename += "dusk_priest_3a.htm";
				}
				break;
			default:
				if (isSealValidationPeriod)
				{
					if (compWinner == SevenSigns.CABAL_DUSK)
					{
						filename += "dusk_priest_4.htm";
					}
					else
					{
						filename += "dusk_priest_2b.htm";
					}
				}
				else
				{
					filename += "dusk_priest_1a.htm";
				}
				break;
			}
			break;
		case 31092: // Black Marketeer of Mammon
			filename += "blkmrkt_1.htm";
			break;
		case 31113: // Merchant of Mammon
			if (!player.isGM())
			{
				switch (compWinner)
				{
				case SevenSigns.CABAL_DAWN:
					if ((playerCabal != compWinner) || (playerCabal != sealAvariceOwner))
					{
						filename += "mammmerch_2.htm";
						return;
					}
					break;
				case SevenSigns.CABAL_DUSK:
					if ((playerCabal != compWinner) || (playerCabal != sealAvariceOwner))
					{
						filename += "mammmerch_2.htm";
						return;
					}
					break;
				}
			}
			filename += "mammmerch_1.htm";
			break;
		case 31126: // Blacksmith of Mammon
			if (!player.isGM())
			{
				switch (compWinner)
				{
				case SevenSigns.CABAL_DAWN:
					if ((playerCabal != compWinner) || (playerCabal != sealGnosisOwner))
					{
						filename += "mammblack_2.htm";
						return;
					}
					break;
				case SevenSigns.CABAL_DUSK:
					if ((playerCabal != compWinner) || (playerCabal != sealGnosisOwner))
					{
						filename += "mammblack_2.htm";
						return;
					}
					break;
				}
			}
			filename += "mammblack_1.htm";
			break;
		default:
			filename = getHtmlPath(npcId, val, player);
		}

		player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
	}
}