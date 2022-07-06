package l2mv.gameserver.model.entity.forum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.clientpackets.Say2C;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.utils.BatchStatement;

public class ShoutboxHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(ShoutboxHandler.class);

	private final Set<Long> shoutIdsShownInGame = new CopyOnWriteArraySet<Long>();
	private final List<ForumShout> shoutsToStore = new CopyOnWriteArrayList<ForumShout>();
	private long lastRestoredShoutId = -1L;
	private long biggestShoutId;

	public void newInGameShout(Player activeChar, String text)
	{
		final String replacedText = replaceShiftItems(activeChar, text);
		if (!ConfigHolder.getPattern("ShoutboxGameToForumPattern").matcher(replacedText).matches())
		{
			return;
		}

		final long idShout = getNewId();
		final ForumMember member = activeChar.getForumMember();
		final String writerName = ConfigHolder.getString("ShoutboxGamePrefix") + member.getMemberName();
		final ForumShout shout = new ForumShout(idShout, member.getMemberId(), writerName, member.getMemberGroup(), replacedText, System.currentTimeMillis());
		shoutIdsShownInGame.add(idShout);
		shoutsToStore.add(shout);
	}

	private static String replaceShiftItems(Player activeChar, String text)
	{
		String newText = text;
		final Matcher m = Say2C.EX_ITEM_LINK_PATTERN.matcher(newText);
		while (m.find())
		{
			final int objectId = Integer.parseInt(m.group(1));
			final ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
			if (item == null)
			{
				newText = newText.replace(m.group(0) + "\b", "");
			}
			else
			{
				final String itemName = item.getName() + (item.getEnchantLevel() > 0 ? " +" + item.getEnchantLevel() : "");
				newText = newText.replace(m.group(0) + "\b", itemName + " ");
			}
		}
		if (newText.contains("\b"))
		{
			return newText.substring(0, newText.indexOf(8));
		}
		return newText;
	}

	private long getNewId()
	{
		if (lastRestoredShoutId + 5L > biggestShoutId)
		{
			biggestShoutId = lastRestoredShoutId + 10L;
		}
		return ++biggestShoutId;
	}

	public void synchronizeShoutbox(Connection con)
	{
		if (ConfigHolder.getBool("AllowShoutboxFromForum"))
		{
			final long restoringSinceShoutId = lastRestoredShoutId;
			final List<L2GameServerPacket> shoutsToAnnounce = new ArrayList<L2GameServerPacket>();
			try (PreparedStatement statement = con.prepareStatement("SELECT ID_SHOUT, realName, message FROM smf_shoutbox WHERE ID_SHOUT > ?"))
			{
				statement.setLong(1, lastRestoredShoutId);
				try (ResultSet rset = statement.executeQuery())
				{
					while (rset.next())
					{
						final long shoutId = rset.getLong("ID_SHOUT");
						if (!shoutIdsShownInGame.contains(shoutId))
						{
							final String name = rset.getString("realName");
							String message = rset.getString("message");
							message = replaceForumMessage(message);
							if (!message.contains("&#") && message.length() < ConfigHolder.getInt("ChatMessageLimit"))
							{
								for (ChatType chatType : ConfigHolder.getArray("ShoutboxFromForumChatTypes", ChatType.class))
								{
									final L2GameServerPacket cs = new Say2(0, chatType, ConfigHolder.getString("ShoutboxForumPrefix") + name, message);
									shoutsToAnnounce.add(cs);
								}
								shoutIdsShownInGame.add(shoutId);
							}
							if (shoutId <= lastRestoredShoutId)
							{
								continue;
							}
							lastRestoredShoutId = shoutId;
						}
					}
				}
			}
			catch (SQLException e)
			{
				LOG.error("Error while restoring Shouts from Forum Shoutbox", e);
			}
			if (restoringSinceShoutId >= 0L)
			{
				for (Player player : GameObjectsStorage.getAllPlayersCopy())
				{
					if (!player.isBlockAll())
					{
						player.sendPacket(shoutsToAnnounce);
					}
				}
			}
		}
		else
		{
			try (PreparedStatement statement2 = con.prepareStatement("SELECT ID_SHOUT FROM smf_shoutbox ORDER BY ID_SHOUT DESC"); ResultSet rset2 = statement2.executeQuery())
			{
				if (rset2.next())
				{
					final long shoutId2 = rset2.getLong("ID_SHOUT");
					if (shoutId2 > lastRestoredShoutId)
					{
						lastRestoredShoutId = shoutId2;
					}
				}
			}
			catch (SQLException e2)
			{
				LOG.error("Error while getting biggest Shout Id from smf_shoutbox!", e2);
			}
		}

		if (ConfigHolder.getBool("AllowSboutboxFromGame"))
		{
			try (PreparedStatement statement2 = BatchStatement.createPreparedStatement(con, "REPLACE INTO smf_shoutbox VALUES (?,?,?,?,?,?,?)"))
			{
				for (ForumShout shout : shoutsToStore)
				{
					shoutsToStore.remove(shout);
					statement2.setLong(1, shout.getIdShout());
					statement2.setInt(2, shout.getIdMember());
					statement2.setString(3, shout.getShoutWriter());
					statement2.setString(4, shout.getWriterGroup() == ForumMemberGroup.ADMINISTRATOR ? ConfigHolder.getString("ShoutboxAdminColor") : "");
					statement2.setString(5, "");
					statement2.setString(6, shout.getMessage());
					statement2.setLong(7, shout.getCreationTime() / 1000L);
					statement2.addBatch();
				}
				statement2.executeBatch();
				con.commit();
			}
			catch (SQLException e2)
			{
				LOG.error("Error while Storing Shouts to Forum Shoutbox", e2);
			}
		}
	}

	private static String replaceForumMessage(String message)
	{
		String newMessage = message.replace("&#039;", "'");
		newMessage = newMessage.replace("&quot;", "\"");
		newMessage = newMessage.replace("&quot;", "\"");
		newMessage = ForumHandler.replaceTags(newMessage, '<', '>', new String[0]);
		newMessage = newMessage.replace("&lt;", "<");
		newMessage = newMessage.replace("&gt;", ">");
		return newMessage;
	}

	public static ShoutboxHandler getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final ShoutboxHandler instance = new ShoutboxHandler();
	}
}
