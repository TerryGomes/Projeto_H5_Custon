package l2f.gameserver.handler.voicecommands.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.BatchStatement;
import l2f.gameserver.vote.VoteRead;

public class VoteReward implements IVoicedCommandHandler, ScriptFile
{
	private static enum ValueType
	{
		ACCOUNT_NAME, IP_ADRESS, HWID
	}

	private static final String[] COMMANDS_LIST = new String[]
	{
		// "getreward"
	};

	// Rewards
	private static final int[][] BLESSED_ENCHANTS_CATEGORY =
	{
		{
			6673,
			1
		}
	};

	private static final int[] PERMANENT_CATEGORY =
	{
		40000, // Vote Rune
		1
	};

	private static final int[][] MISC_CATEGORY =
	{
		{
			6673,
			2
		},
		{
			6673,
			3
		},
		{
			6673,
			4
		}
	};
	private static final double[][] RANDOM_CATEGORY =
	{
		{
			6577, // 1 Blessed Enchant Weapon S
			1,
			0.05
		},
		{
			6578, // 1 Blessed Enchant Armor S
			1,
			0.233
		},
		{
			14169, // 1 Top Life Stone Level 84
			1,
			1.25
		},
		{
			14168, // 1 High Life Stone Level 84
			1,
			2.0
		},
		{
			13073, // 1 Giant's Codex - Mastery
			1,
			3.333
		},
		{
			959, // 1 Enchant Weapon S
			1,
			3.0
		},
		{
			6622, // 1 Giant's Codex
			3,
			8.0
		},
		{
			960, // 1 Enchant Armor S
			1,
			8.0
		},
		{
			9552, // 1 Fire Crystal
			1,
			8.0
		},
		{
			9553, // 1 Water Crystal
			1,
			8.0
		},
		{
			9556, // 1 Dark Crystal
			1,
			8.0
		},
		{
			9557, // 1 Holy Crystal
			1,
			8.0
		},
		{
			9554, // 1 Earth Crystal
			1,
			8.0
		},
		{
			9555, // 1 Wind Crystal
			1,
			8.0
		},
		{
			9546, // 1 Fire Stone
			2,
			20.0
		},
		{
			9547, // 1 Water Stone
			2,
			20.0
		},
		{
			9548, // 1 Earth Stone
			2,
			20.0
		},
		{
			9549, // 1 Wind Stone
			2,
			20.0
		},
		{
			9550, // 1 Dark Stone
			2,
			20.0
		},
		{
			9551, // 1 Holy Stone
			2,
			100.0
		},
	};

	private static final long VOTE_COMMAND_REUSE = 5 * 60 * 1000L; // 5 Minutes
	private static final long VOTE_PENALTY = 12 * 60 * 60 * 1000L; // 12 Hours

	public static final Map<Integer, Long> _votePlayerReuses = new ConcurrentHashMap<>();
	public static final Map<String, Long> _accountPenalties = new ConcurrentHashMap<>();
	public static final Map<String, Long> _ipPenalties = new ConcurrentHashMap<>();
	public static final Map<String, Long> _hwidPenalties = new ConcurrentHashMap<>();

	public VoteReward()
	{
		// If there is a set vote reward message, schedule it
		if (!Config.VOTE_REWARD_MSG.isEmpty())
		{
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new VoteAnnounceTask(), 5 * 60 * 1000, Config.ANNOUNCE_VOTE_DELAY * 1000);
		}

		// Restore from the db all the penalties of the votes, it doesn't matter if its 0. So we can do it only once at start
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT * FROM vote_system");)
		{
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					final String value = rset.getString("value");
					final long time = rset.getLong("penalty_time");

					switch (rset.getInt("value_type"))
					{
					// Account Name
					case 0:
					{
						_accountPenalties.put(value, time);
						break;
					}
					// Ip Address
					case 1:
					{
						_ipPenalties.put(value, time);
						break;
					}
					// Hwid
					case 2:
					{
						_hwidPenalties.put(value, time);
						break;
					}
					}
				}
			}
		}
		catch (Exception e)
		{
		}
	}

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String params)
	{
		if (command.equalsIgnoreCase("getreward"))
		{
			try
			{
				// No connection, no vote
				if (activeChar.getNetConnection() == null)
				{
					return false;
				}

				if (!Config.ENABLE_VOTE)
				{
					activeChar.sendMessage("Voting is currently disabled!");
					return false;
				}

				// Min lvl 40
				if (activeChar.getLevel() < 40)
				{
					activeChar.sendMessage("You need to be at least level 40 to use this command.");
					return false;
				}

				final long currentTime = System.currentTimeMillis();

				// Prims - Check if voting is not blocked. If a web connection ocurrs, then the vote will be block for everyone for 15 minutes
				if (VoteRead._siteBlockTime >= currentTime)
				{
					activeChar.sendMessage("There are problems with the connection to the vote site, so it has been disabled for some minutes. Try again later");
					return false;
				}

				// Check player vote reuse
				if (activeChar.getAccessLevel() < 1 && _votePlayerReuses.containsKey(activeChar.getObjectId()))
				{
					if (_votePlayerReuses.get(activeChar.getObjectId()) > currentTime)
					{
						activeChar.sendMessage("You can use this command only once every 5 minutes.");
						return false;
					}
				}

				_votePlayerReuses.put(activeChar.getObjectId(), currentTime + VOTE_COMMAND_REUSE);

				// Getting IP of client, here we will have to check for HWID when we have LAMEGUARD
				final String IPClient = activeChar.getIP();
				final String HWID = (activeChar.getHWID() != null ? activeChar.getHWID() : "");

				// Check the penalties of the player to see if he can vote again
				if (!checkPlayerPenalties(activeChar, IPClient, HWID, true))
				{
					return false;
				}

				// Return 0 if he didnt voted. Date when he voted on website
				final long dateHeVotedOnWebsite = VoteRead.checkVotedIP(IPClient);
				if (dateHeVotedOnWebsite < 1)
				{
					activeChar.sendMessage("To claim reward, you need to vote on all banners!");
					activeChar.sendPacket(new ExShowScreenMessage("To claim reward, you need to vote on all banners!", 4000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, true));
					return false;
				}

				// Add the vote penalty to the player
				addNewPlayerPenalty(activeChar, IPClient, HWID);

				// Give the rewards
				giveRewards(activeChar);
				activeChar.sendMessage("Successfully rewarded.");

				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	// Thread to send to all players that didn't voted yet to vote for the server
	protected static class VoteAnnounceTask implements Runnable
	{
		@Override
		public void run()
		{
			if (Config.VOTE_REWARD_MSG.isEmpty())
			{
				return;
			}

			final Say2 announce = new Say2(0, ChatType.ANNOUNCEMENT, "", Config.VOTE_REWARD_MSG);

			final Iterable<Player> world = GameObjectsStorage.getAllPlayersForIterate();
			for (Player player : world)
			{

				// No offline or store mode
				// If the player has an active penalty means that he already voted
				if (player == null || player.getNetConnection() == null || player.isInStoreMode() || !checkPlayerPenalties(player, player.getIP(), player.getHWID(), false))
				{
					continue;
				}

				player.sendPacket(announce);
			}
		}
	}

	/**
	 * Gives to the player all the vote rewards
	 *
	 * @param player
	 */
	protected static void giveRewards(Player player)
	{
		player.getCounters().timesVoted++;

		// First give the permanent item
		Functions.addItem(player, PERMANENT_CATEGORY[0], PERMANENT_CATEGORY[1], "VoteReward Permanent");

		// First give the vote main random reward
		final int[] reward = getReward();
		Functions.addItem(player, reward[0], reward[1], "VoteReward Main");

		// Then give some random rewards
		for (double[] item : RANDOM_CATEGORY)
		{
			if (Rnd.chance(item[2]))
			{
				Functions.addItem(player, (int) item[0], (long) item[1], "Vote Random Reward");
				return;
			}
		}
	}

	/**
	 * Puts new penalties for the account name, ip and hwid of the player after he succesfully voted
	 *
	 * @param activeChar
	 * @param IPClient
	 * @param HWID
	 */
	protected static void addNewPlayerPenalty(Player activeChar, String IPClient, String HWID)
	{
		final long newPenalty = System.currentTimeMillis() + VOTE_PENALTY;
		_accountPenalties.put(activeChar.getAccountName(), newPenalty);
		_ipPenalties.put(IPClient, newPenalty);
		_hwidPenalties.put(HWID, newPenalty);

		// Also store the penalties in the db
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = BatchStatement.createPreparedStatement(con, "REPLACE INTO vote_system(value_type, value, penalty_time) VALUES (?, ?, ?)"))
		{
			final String[] values = new String[]
			{
				activeChar.getAccountName(),
				IPClient,
				HWID
			};
			for (ValueType type : ValueType.values())
			{
				statement.setInt(1, type.ordinal());
				statement.setString(2, values[type.ordinal()]);
				statement.setLong(3, newPenalty);
				statement.addBatch();
			}

			statement.executeBatch();
		}
		catch (Exception e)
		{
		}
	}

	/**
	 * @param activeChar
	 * @param IPClient
	 * @param HwID
	 * @param sendMessage
	 * @return Returns true if the player doesn't have an active penalty after voting
	 */
	public static boolean checkPlayerPenalties(Player activeChar, String IPClient, String HwID, boolean sendMessage)
	{
		final long accountPenalty = checkPenalty(ValueType.ACCOUNT_NAME, activeChar.getAccountName());
		final long ipPenalty = checkPenalty(ValueType.IP_ADRESS, IPClient);
		final long hwidPenalty = checkPenalty(ValueType.HWID, HwID);

		final int penalty = (int) ((Math.max(accountPenalty, Math.max(ipPenalty, hwidPenalty)) - System.currentTimeMillis()) / (60 * 1000L));

		if (penalty > 0)
		{
			if (sendMessage)
			{
				if (penalty > 60)
				{
					activeChar.sendMessage("You can vote only once every 12 hours. You still have to wait " + (penalty / 60) + " hours " + (penalty % 60) + " minutes.");
				}
				else
				{
					activeChar.sendMessage("You can vote only once every 12 hours. You still have to wait " + penalty + " minutes.");
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * @param type
	 * @param value
	 * @return Returns the penalty of a particular type and value if it exists
	 */
	private static long checkPenalty(ValueType type, String value)
	{
		switch (type)
		{
		case ACCOUNT_NAME:
		{
			if (_accountPenalties.containsKey(value))
			{
				return _accountPenalties.get(value);
			}
			break;
		}
		case IP_ADRESS:
		{
			if (_ipPenalties.containsKey(value))
			{
				return _ipPenalties.get(value);
			}
			break;
		}
		case HWID:
		{
			if (_hwidPenalties.containsKey(value))
			{
				return _hwidPenalties.get(value);
			}
			break;
		}
		}

		return 0;
	}

	public static int[] getReward()
	{
		return MISC_CATEGORY[Rnd.get(MISC_CATEGORY.length)];
	}

	@Override
	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	@Override
	public void onReload()
	{
		//
	}

	@Override
	public void onShutdown()
	{
		//
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS_LIST;
	}
}
