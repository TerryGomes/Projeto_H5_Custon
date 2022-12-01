package l2mv.gameserver.handler.admincommands.impl;

import java.io.File;
import java.util.Scanner;
import java.util.StringTokenizer;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.EventHolder;
import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.idfactory.IdFactory;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.EventType;
import l2mv.gameserver.model.entity.events.fightclubmanager.FightClubEventManager;
import l2mv.gameserver.model.entity.events.impl.AbstractFightClub;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.Util;

public class AdminEvents implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_event_draw,
		admin_events,
		admin_start_event
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().IsEventGm)
		{
			return false;
		}

		switch (command)
		{
		case admin_event_draw:
		{
			int adenaId = 57;
			int adenaMinCount = 1;
			int adenaMaxCount = 1;
			StringTokenizer st = new StringTokenizer(fullString);
			st.nextToken();
			String fieldName = "";
			try
			{
				int token = 0;
				while (st.hasMoreTokens())
				{
					token++;
					String param = st.nextToken();
					if (token == 1)
					{
						fieldName = param;
					}
					else if (Util.isDigit(param) && (token == 2))
					{
						adenaId = Integer.parseInt(param);
					}
					else if (Util.isDigit(param) && (token == 3))
					{
						adenaMinCount = Integer.parseInt(param);
					}
					else if (Util.isDigit(param) && (token == 4))
					{
						adenaMaxCount = Integer.parseInt(param);
					}
				}
				if (fieldName.startsWith("random"))
				{
					int dx = 1;
					int dy = 1;
					int fill = 100;
					String[] fnSplit = fieldName.split("_");
					for (int i = 0; i < fnSplit.length; i++)
					{
						switch (i)
						{
						case 1:
							dx = Integer.parseInt(fnSplit[i]);
							break;
						case 2:
							dy = Integer.parseInt(fnSplit[i]);
							break;
						case 3:
							fill = Integer.parseInt(fnSplit[i]);
							break;
						default:
							break;
						}
					}
					drawText(activeChar, generateRandom(dx, dy, fill), 5, 5, adenaId, adenaMinCount, adenaMaxCount);
				}
				else
				{
					File file = Config.findResource("/data/events/eventdraw/" + fieldName + ".txt");
					if (!file.exists())
					{
						activeChar.sendMessage("File: " + file.getPath() + " doesnt exist");
					}
					else
					{
						try
						{
							Scanner s = new Scanner(file);
							StringBuilder sb = new StringBuilder((int) file.length());
							while (s.hasNextLine())
							{
								String line = s.nextLine();
								if (line.isEmpty())
								{
									continue;
								}

								sb.append(line);
								sb.append(";");
							}
							s.close();
							drawText(activeChar, sb.toString(), 5, 5, adenaId, adenaMinCount, adenaMaxCount);
						}
						catch (Exception e)
						{
							activeChar.sendMessage("Error opening the file " + file.getPath() + "\n. Message: " + e.getMessage());
							activeChar.sendMessage(e.getStackTrace()[0].toString());
							activeChar.sendMessage(e.getStackTrace()[1].toString());
							activeChar.sendMessage(e.getStackTrace()[2].toString());
							e.printStackTrace();
							return false;
						}
					}
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("There was an error with the command " + fieldName);
				e.printStackTrace();
			}
			break;
		}
		case admin_events:
			if (wordList.length == 1)
			{
				activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/events/events.htm"));
			}
			else
			{
				activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/events/" + wordList[1].trim()));
			}
			break;
		case admin_start_event:
			int id;
			try
			{
				id = Integer.parseInt(wordList[1]);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Use it like that: //start_event id(Id can be found in dir: data/events/fight_club)");
				return false;
			}

			AbstractFightClub event = EventHolder.getInstance().getEvent(EventType.FIGHT_CLUB_EVENT, id);
			FightClubEventManager.getInstance().startEventCountdown(event, false);
			activeChar.sendMessage("Event Started!");
			break;
		}

		return true;
	}

	private void drawText(Player player, String toDraw, int linePointOffset, int lineToLineOffset, int adenaId, long adenaMinCount, long adenaMaxCount)
	{
		int[] startXYZ = new int[3];
		startXYZ[0] = player.getX();
		startXYZ[1] = player.getY();
		startXYZ[2] = player.getZ();

		int destX = player.getX();
		int destY = player.getY();

		String[] toDrawLines = toDraw.split(";");
		int itemIdToDrop = 0;

		for (String toDrawLine : toDrawLines)
		{
			for (int i = 0; i < toDrawLine.length(); i++) // Length
			{
				switch (toDrawLine.charAt(i))
				{
				case 'A':
				case '#':
				case '1':
					itemIdToDrop = adenaId;
					break;
				case 'S':
					itemIdToDrop = 1785;
					break;
				case 'd':
					itemIdToDrop = 1463;
					break;
				case 'c':
					itemIdToDrop = 1464;
					break;
				case 'b':
					itemIdToDrop = 1465;
					break;
				case 'a':
					itemIdToDrop = 1466;
					break;
				case 's':
					itemIdToDrop = 1467;
					break;
				case 'n':
					itemIdToDrop = 1835;
					break;
				default:
					itemIdToDrop = 0;

				}

				// Drop an item at this point of the line
				dropEventItem(player, itemIdToDrop, Rnd.get(adenaMinCount, adenaMaxCount), destX, destY, startXYZ[2], player.getReflectionId());

				destX += linePointOffset;
			}

			// Start a new line, nullfy destX
			destY += lineToLineOffset;
			destX = startXYZ[0];
		}
	}

	private void dropEventItem(Player player, int itemId, long num, int x, int y, int z, int reflectionId)
	{
		if (itemId == 0)
		{
			return;
		}

		ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
		item.setCount(num);
		item.setReflection(reflectionId);
		item.setTimeToDeleteAfterDrop(285000);
		item.dropToTheGround(player, new Location(x, y, z));
	}

	private String generateRandom(int dx, int dy, int fillPercent)
	{
		StringBuilder sb = new StringBuilder((dx * dy) + dy);
		for (int y = 0; y < dy; y++)
		{
			for (int x = 0; x < dx; x++)
			{
				if (Rnd.get(100) < fillPercent)
				{
					sb.append("#");
				}
				else
				{
					sb.append(" ");
				}
			}
			sb.append(";");
		}

		return sb.toString();
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}