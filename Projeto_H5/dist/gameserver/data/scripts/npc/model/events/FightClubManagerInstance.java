package npc.model.events;

import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

import l2f.gameserver.Config;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.ItemFunctions;

public class FightClubManagerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final String HTML_INDEX = "scripts/events/fightclub/index.htm";
	private static final String HTML_ACCEPT = "scripts/events/fightclub/accept.htm";
	private static final String HTML_MAKEBATTLE = "scripts/events/fightclub/makebattle.htm";
	private static final String HTML_INFO = "scripts/events/fightclub/info.htm";
	private static final String HTML_DISABLED = "scripts/events/fightclub/disabled.htm";
	private static final String HTML_LIST = "scripts/events/fightclub/fightslist.htm";
	private static final String HTML_RESULT = "scripts/events/fightclub/result.htm";

	public FightClubManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (!Config.FIGHT_CLUB_ENABLED)
		{
			player.sendPacket(new NpcHtmlMessage(player, this, HtmCache.getInstance().getNotNull(HTML_DISABLED, player), 0));
			return;
		}

		if (command.equalsIgnoreCase("index"))
		{
			player.sendPacket(new NpcHtmlMessage(player, this, HtmCache.getInstance().getNotNull(HTML_INDEX, player), 0));
		}

		else if (command.equalsIgnoreCase("makebattle"))
		{
			player.sendPacket(makeBattleHtml(player));
		}

		else if (command.equalsIgnoreCase("info"))
		{
			player.sendPacket(new NpcHtmlMessage(player, this, HtmCache.getInstance().getNotNull(HTML_INFO, player), 0));
		}

		else
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			String pageName = st.nextToken();
			if (pageName.equalsIgnoreCase("addbattle"))
			{
				int count = 0;
				try
				{
					count = Integer.parseInt(st.nextToken());
					if (count == 0)
					{
						sendResult(player, "Error!", "You did not enter the number or wrong number.");
						return;
					}
				}
				catch (NumberFormatException e)
				{
					sendResult(player, "Error!", "You did not enter the number or wrong number.");
					return;
				}
				String itemName = StringUtils.EMPTY;
				if (st.hasMoreTokens())
				{
					itemName = st.nextToken();
					while (st.hasMoreTokens())
					{
						itemName += " " + st.nextToken();
					}
				}
				Object[] objects =
				{
					player,
					itemName,
					count
				};
				final String respone = (String) callScripts("addApplication", objects);
				if ("OK".equalsIgnoreCase(respone))
				{
					sendResult(player, "Completed!", "You have created an application for participation.<br>Your bet - <font color=\"LEVEL\">" + String.valueOf(objects[2]) + " " + String.valueOf(objects[1])
								+ "</font><br><center>Good luck!</center>");
				}
				else if ("NoItems".equalsIgnoreCase(respone))
				{
					sendResult(player, "Error!", "You are not required or missing items!");
					return;
				}
				else if ("reg".equalsIgnoreCase(respone))
				{
					sendResult(player, "Error!", "You are already registered! If you wish to bid, remove the old registration.");
					return;
				}
			}
			else if (pageName.equalsIgnoreCase("delete"))
			{
				Object[] playerObject =
				{
					player
				};
				if ((Boolean) callScripts("isRegistered", playerObject))
				{
					callScripts("deleteRegistration", playerObject);
					sendResult(player, "Completed!", "<center>You are removed from the list of registration.</center>");
				}
				else
				{
					sendResult(player, "Error!", "<center>You have not been registered to participate.</center>");
				}
			}
			else if (pageName.equalsIgnoreCase("openpage"))
			{
				player.sendPacket(makeOpenPage(player, Integer.parseInt(st.nextToken())));
			}
			else if (pageName.equalsIgnoreCase("tryaccept"))
			{
				player.sendPacket(makeAcceptHtml(player, Long.parseLong(st.nextToken())));
			}
			else if (pageName.equalsIgnoreCase("accept"))
			{
				accept(player, Long.parseLong(st.nextToken()));
			}
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		player.sendPacket(new NpcHtmlMessage(player, this, HtmCache.getInstance().getNotNull(HTML_INDEX, player), val));
	}

	private NpcHtmlMessage makeOpenPage(Player player, int pageId)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(HTML_LIST);

		StringBuilder sb = new StringBuilder();

		final int count = (Integer) callScripts("getRatesCount", new Object[0]);

		int num = pageId * Config.PLAYERS_PER_PAGE;
		if (num > count)
		{
			num = count;
		}
		if (count > 0)
		{
			sb.append("<table width=300>");

			for (int i = pageId * Config.PLAYERS_PER_PAGE - Config.PLAYERS_PER_PAGE; i < num; i++)
			{
				Object[] index =
				{
					i
				};
				Rate rate = (Rate) callScripts("getRateByIndex", index);
				sb.append("<tr>");
				sb.append("<td align=center width=95>");
				sb.append("<a action=\"bypass -h npc_%objectId%_tryaccept ").append(rate.getStoredId()).append("\">");
				sb.append("<font color=\"ffff00\">").append(rate.getPlayerName()).append("</font></a></td>");
				sb.append("<td align=center width=70>").append(rate.getPlayerLevel()).append("</td>");
				sb.append("<td align=center width=100><font color=\"ff0000\">");
				sb.append(rate.getPlayerClass()).append("</font></td>");
				sb.append("<td align=center width=135><font color=\"00ff00\">");
				sb.append(rate.getItemCount()).append(" ").append(rate.getItemName());
				sb.append("</font></td></tr>");
			}

			sb.append("</table><br><br><br>");
			int pg = getPagesCount(count);
			sb.append("page:&nbsp;");
			for (int i = 1; i <= pg; i++)
			{
				if (i == pageId)
				{
					sb.append(i).append("&nbsp;");
				}
				else
				{
					sb.append("<a action=\"bypass -h npc_%objectId%_openpage ").append(i).append("\">").append(i).append("</a>&nbsp;");
				}
			}
		}
		else
		{
			sb.append("<br><center>Rates have not yet done</center>");
		}
		html.replace("%data%", sb.toString());

		return html;
	}

	private NpcHtmlMessage makeBattleHtml(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(HTML_MAKEBATTLE);
		html.replace("%items%", (String) callScripts("getItemsList", new Object[0]));

		return html;
	}

	private NpcHtmlMessage makeAcceptHtml(Player player, long storedId)
	{
		Object[] id =
		{
			storedId
		};
		Rate rate = (Rate) callScripts("getRateByStoredId", id);
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(HTML_ACCEPT);
		html.replace("%name%", rate.getPlayerName());
		html.replace("%class%", rate.getPlayerClass());
		html.replace("%level%", String.valueOf(rate.getPlayerLevel()));
		html.replace("%rate%", rate.getItemCount() + " " + rate.getItemName());
		html.replace("%storedId%", String.valueOf(rate.getStoredId()));
		return html;
	}

	private void accept(Player player, long storedId)
	{
		final Object[] data =
		{
			GameObjectsStorage.getAsPlayer(storedId),
			player
		};
		if (player.getStoredId() == storedId)
		{
			sendResult(player, "Error!", "You can not call the fight itself.");
			return;
		}
		// TODO: Проверка на айтемы... Берем пример с doStart
		// if(Functions.getItemCount(player, _))
		if ((Boolean) callScripts("requestConfirmation", data))
		{
			sendResult(player, "Attention!", "You have sent a request to the opponent. If all conditions match, you will move into the arena<br><center><font color=\"LEVEL\">Good luck!</font></center><br>");
		}
	}

	private void sendResult(Player player, String title, String text)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(HtmCache.getInstance().getNotNull(HTML_RESULT, player));
		html.replace("%title%", title);
		html.replace("%text%", text);
		player.sendPacket(html);
	}

	private int getPagesCount(int count)
	{
		if (count % Config.PLAYERS_PER_PAGE > 0)
		{
			return count / Config.PLAYERS_PER_PAGE + 1;
		}
		return count / Config.PLAYERS_PER_PAGE;
	}

	private Object callScripts(String methodName, Object[] args)
	{
		return Functions.callScripts("events.FightClub.FightClubManager", methodName, args);
	}

	public static class Rate
	{
		private String playerName;
		private int playerLevel;
		private String playerClass;
		private int _itemId;
		private String itemName;
		private int _itemCount;
		private long playerStoredId;

		public Rate(Player player, int itemId, int itemCount)
		{
			playerName = player.getName();
			playerLevel = player.getLevel();
			playerClass = player.getClassId().name();
			_itemId = itemId;
			_itemCount = itemCount;
			itemName = ItemFunctions.createItem(itemId).getTemplate().getName();
			playerStoredId = player.getStoredId();
		}

		public String getPlayerName()
		{
			return playerName;
		}

		public int getPlayerLevel()
		{
			return playerLevel;
		}

		public String getPlayerClass()
		{
			return playerClass;
		}

		public int getItemId()
		{
			return _itemId;
		}

		public int getItemCount()
		{
			return _itemCount;
		}

		public String getItemName()
		{
			return itemName;
		}

		public long getStoredId()
		{
			return playerStoredId;
		}
	}
}