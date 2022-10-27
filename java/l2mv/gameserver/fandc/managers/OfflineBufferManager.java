/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2mv.gameserver.fandc.managers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.CharTemplateHolder;
import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Skill.SkillTargetType;
import l2mv.gameserver.model.Skill.SkillType;
import l2mv.gameserver.model.Zone.ZoneType;
import l2mv.gameserver.model.base.ClassId;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.model.entity.residence.ClanHall;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.TradeHelper;
import l2mv.gameserver.utils.Util;

/**
 * Manager para manejar todas las funciones del offline buffer
 * Creacion de los schemes de venta, chequeos, precios, consumos, buffeos, htmls, etc
 *
 * @author Prims
 */
public class OfflineBufferManager
{
	protected static final Logger _log = Logger.getLogger(OfflineBufferManager.class.getName());

	private static final int MAX_INTERACT_DISTANCE = 100;

	private final Map<Integer, BufferData> _buffStores = new ConcurrentHashMap<>();

	/**
	 * @return Devuelve todas las stores de buffs
	 */
	public Map<Integer, BufferData> getBuffStores()
	{
		return _buffStores;
	}

	/**
	 * Procesa el bypass para este sistema y muestra el html que corresponda
	 *
	 * @param player
	 * @param command
	 */
	public void processBypass(Player player, String command)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();

		switch (st.nextToken())
		{
		// Sets a new buff store
		case "setstore":
		{
			try
			{
				final int price = Integer.parseInt(st.nextToken());
				String title = st.nextToken();
				while (st.hasMoreTokens())
				{
					title += " " + st.nextToken();
				}
				title = title.trim();

				// Check if the player already has an active store, just in case
				if (_buffStores.containsKey(player.getObjectId()))
				{
					// player.sendMessage("This buffer already exists. Cheater?");
					break;
				}

				// Check for store
				if (player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
				{
					player.sendMessage("You already have a store");
					break;
				}

				// Check if the player can set a store
				if (!Config.BUFF_STORE_ALLOWED_CLASS_LIST.contains(player.getClassId().getId()))
				{
					player.sendMessage("Your profession is not allowed to set an Buff Store");
					break;
				}

				// Check all the conditions to see if the player can open a private store
				if (!TradeHelper.checksIfCanOpenStore(player, Player.STORE_PRIVATE_BUFF))
				{
					break;
				}

				// Check the title
				if (title.isEmpty() || title.length() >= 29)
				{
					player.sendMessage("You must put a title for this store and it must have less than 29 characters");
					throw new Exception();
				}

				// Check price limits
				if (price < 1 || price > 10000000)
				{
					player.sendMessage("The price for each buff must be between 1 and 10kk");
					throw new Exception();
				}

				// Buff Stores can only be put inside areas designated to it and in clan halls
				final ClanHall ch = ResidenceHolder.getInstance().getResidenceByObject(ClanHall.class, player);
				if (!player.isGM() && !player.isInZone(ZoneType.buff_store_only) && !player.isInZone(ZoneType.RESIDENCE) && ch == null)
				{
					player.sendMessage("You can't put a buff store here. Look for special designated zones or clan halls");
					break;
				}

				// Check for conditions
				if (player.isAlikeDead() || player.isInOlympiadMode() || player.isMounted() || player.isCastingNow() || player.getOlympiadObserveGame() != null || player.getOlympiadGame() != null || Olympiad.isRegisteredInComp(player))
				{
					player.sendMessage("You don't meet the required conditions to put a buff store right now");
					break;
				}

				final BufferData buffer = new BufferData(player, title, price, null);

				// Add all the buffs
				for (Skill skill : player.getAllSkills())
				{
					// Only active skills

					// Only buffs

					// Not triggered and hero skills
					// Not only self skills
					if (!skill.isActive() || (skill.getSkillType() != SkillType.BUFF) || skill.isHeroic() || (skill.getTargetType() == SkillTargetType.TARGET_SELF))
					{
						continue;
					}

					// Not pet skills
					// Avoid overlord skills when being a warcryer
					if ((skill.getTargetType() == SkillTargetType.TARGET_PET) || (player.getClassId().equalsOrChildOf(ClassId.doomcryer) && skill.getTargetType() == SkillTargetType.TARGET_CLAN))
					{
						continue;
					}

					// Avoid warcryer skills when being a overlord
					// Forbidden skill list
					if ((player.getClassId().equalsOrChildOf(ClassId.dominator) && (skill.getTargetType() == SkillTargetType.TARGET_PARTY || skill.getTargetType() == SkillTargetType.TARGET_ONE)) || Config.BUFF_STORE_FORBIDDEN_SKILL_LIST.contains(skill.getId()))
					{
						continue;
					}

					buffer.getBuffs().put(skill.getId(), skill);
				}

				// Case of empty buff list
				if (buffer.getBuffs().isEmpty())
				{
					player.sendMessage("You don't have any available buff to put on sale in the store");
					break;
				}

				// Add the buffer data to the array
				_buffStores.put(player.getObjectId(), buffer);

				// Sit the player, put it on store and and change the colors and titles
				player.sitDown(null);

				player.setVisibleTitleColor(Config.BUFF_STORE_TITLE_COLOR);
				player.setVisibleTitle(title);
				player.setVisibleNameColor(Config.BUFF_STORE_NAME_COLOR);
				player.broadcastUserInfo(true);

				player.setPrivateStoreType(Player.STORE_PRIVATE_BUFF);

				player.sendMessage("Your Buff Store was set succesfully");
			}
			catch (NumberFormatException e)
			{
				player.sendMessage("The price for each buff must be between 1 and 10kk");

				final NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("command/buffstore/buff_store_create.htm");
				player.sendPacket(html);
			}
			catch (Exception e)
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("command/buffstore/buff_store_create.htm");
				player.sendPacket(html);
			}
			break;
		}
		// Stops the current store
		case "stopstore":
		{
			if (player.getPrivateStoreType() != Player.STORE_PRIVATE_BUFF)
			{
				player.sendMessage("You dont have any store set right now");
				break;
			}

			// Remove the buffer from the array
			_buffStores.remove(player.getObjectId());

			// Stand the player and put the original colors and title back
			player.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
			player.standUp();

			player.setVisibleTitleColor(0);
			player.setVisibleTitle(null);
			player.setVisibleNameColor(0);
			player.broadcastUserInfo(true);

			player.sendMessage("Your Buff Store was removed succesfuly");

			break;
		}
		// Shows the buff list of the selected buffer
		case "bufflist":
		{
			try
			{
				final int playerId = Integer.parseInt(st.nextToken());
				final boolean isPlayer = (st.hasMoreTokens() ? st.nextToken().equalsIgnoreCase("player") : true);
				final int page = (st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0);

				// Check if the buffer exists
				final BufferData buffer = _buffStores.get(playerId);
				// Check if the player is in the right distance from the buffer
				if ((buffer == null) || (Util.calculateDistance(player, buffer.getOwner(), true) > MAX_INTERACT_DISTANCE))
				{
					// player.sendMessage("Too far. Cheater?");
					break;
				}

				// Check if the player has a summon before buffing
				if (!isPlayer && player.getPet() == null)
				{
					player.sendMessage("You don't have any active summon right now");

					// Send window again
					showStoreWindow(player, buffer, !isPlayer, page);
					break;
				}

				showStoreWindow(player, buffer, isPlayer, page);
			}
			catch (Exception e)
			{

			}
			break;
		}
		// Purchases a particular buff of the store
		case "purchasebuff":
		{
			try
			{
				final int playerId = Integer.parseInt(st.nextToken());
				final boolean isPlayer = (st.hasMoreTokens() ? st.nextToken().equalsIgnoreCase("player") : true);
				final int buffId = Integer.parseInt(st.nextToken());
				final int page = (st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0);

				// Check if the buffer exists
				final BufferData buffer = _buffStores.get(playerId);

				// Check if the buffer has this buff
				// Check if the player is in the right distance from the buffer
				if ((buffer == null) || !buffer.getBuffs().containsKey(buffId) || (Util.calculateDistance(player, buffer.getOwner(), true) > MAX_INTERACT_DISTANCE))
				{
					// player.sendMessage("Too far. Cheater?");
					break;
				}

				// Check if the player has a summon before buffing
				if (!isPlayer && player.getPet() == null)
				{
					player.sendMessage("You don't have any active summon right now");

					// Send window again
					showStoreWindow(player, buffer, !isPlayer, page);
					break;
				}

				// Check buffing conditions
				if (player.getPvpFlag() > 0 || player.isInCombat() || player.getKarma() > 0 || player.isAlikeDead() || player.isJailed() || player.isInOlympiadMode() || player.isCursedWeaponEquipped() || player.isInStoreMode() || player.isInTrade() || player.getEnchantScroll() != null || player.isFishing())
				{
					player.sendMessage("You don't meet the required conditions to use the buffer right now");
					break;
				}

				final double buffMpCost = (Config.BUFF_STORE_MP_ENABLED ? buffer.getBuffs().get(buffId).getMpConsume() * Config.BUFF_STORE_MP_CONSUME_MULTIPLIER : 0);

				// Check if the buffer has enough mp to sell this buff
				if (buffMpCost > 0 && buffer.getOwner().getCurrentMp() < buffMpCost)
				{
					player.sendMessage("This store doesn't have enough mp to give sell you this buff");

					// Send window again
					showStoreWindow(player, buffer, isPlayer, page);
					break;
				}

				// Clan Members of the buffer dont have to pay anything
				final int buffPrice = player.getClanId() == buffer.getOwner().getClanId() && player.getClanId() != 0 ? 0 : buffer.getBuffPrice();

				// Check if the player has enough adena to purchase this buff
				// Charge the adena needed for this buff
				if ((buffPrice > 0 && player.getAdena() < buffPrice) || (buffPrice > 0 && !player.reduceAdena(buffPrice, true, "BuffStore")))
				{
					player.sendMessage("You don't have enough adena to purchase a buff");
					break;
				}

				// Give the adena to the buffer
				if (buffPrice > 0)
				{
					buffer.getOwner().addAdena(buffPrice, true, "BuffStore");
				}

				// Reduce the buffer's mp if it consumes something
				if (buffMpCost > 0)
				{
					buffer.getOwner().reduceCurrentMp(buffMpCost, null);
				}

				// Give the target the buff
				if (isPlayer)
				{
					buffer.getBuffs().get(buffId).getEffects(player, player, false, false);
				}
				else
				{
					buffer.getBuffs().get(buffId).getEffects(player.getPet(), player.getPet(), false, false);
				}

				// Send message
				player.sendMessage("You have bought " + buffer.getBuffs().get(buffId).getName() + " from " + buffer.getOwner().getName());

				// Send the buff list again after buffing, exactly where it was before
				showStoreWindow(player, buffer, isPlayer, page);
			}
			catch (Exception e)
			{

			}
			break;
		}
		}
	}

	/**
	 * Sends the to the player the buffer store window with all the buffs and info
	 *
	 * @param player
	 * @param buffer
	 * @param isForPlayer
	 * @param page
	 */
	private void showStoreWindow(Player player, BufferData buffer, boolean isForPlayer, int page)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("command/buffstore/buff_store_buffer.htm");

		final int MAX_ENTRANCES_PER_ROW = 6; // buffs per page
		final double entrancesSize = buffer.getBuffs().size();
		final int maxPage = (int) Math.ceil(entrancesSize / MAX_ENTRANCES_PER_ROW) - 1;
		final int currentPage = Math.min(maxPage, page);

		// Creamos la lista de buffs
		final StringBuilder buffList = new StringBuilder();
		final Iterator<Skill> it = buffer.getBuffs().values().iterator();
		Skill buff;
		int i = 0;
		int baseMaxLvl;
		int enchantLvl;
		int enchantType;
		boolean changeColor = false;

		while (it.hasNext())
		{
			// Solo mostramos los buffs que sean de esta pagina
			if (i < currentPage * MAX_ENTRANCES_PER_ROW)
			{
				it.next();
				i++;
				continue;
			}

			// Si llegamos al final de la pagina salimos
			if (i >= (currentPage * MAX_ENTRANCES_PER_ROW + MAX_ENTRANCES_PER_ROW))
			{
				break;
			}

			buff = it.next();
			baseMaxLvl = SkillTable.getInstance().getBaseLevel(buff.getId());

			buffList.append("<tr>");
			buffList.append("<td fixwidth=300>");
			buffList.append("<table height=35 border=0 cellspacing=2 cellpadding=0 bgcolor=" + (changeColor ? "171612" : "23221e") + ">");
			buffList.append("<tr>");
			buffList.append("<td width=5></td>");
			buffList.append("<td width=30 align=center background=" + buff.getIcon() + "><button value=\"\" action=\"bypass -h BuffStore purchasebuff " + buffer.getOwner().getObjectId() + " " + (isForPlayer ? "player" : "summon") + " " + buff.getId() + " " + currentPage + "\" width=32 height=32 back=L2UI_CT1.ItemWindow_DF_Frame_Down fore=L2UI_CT1.ItemWindow_DF_Frame></td>");
			buffList.append("<td width=12></td>");
			if (buff.getLevel() > baseMaxLvl)
			{
				// Buffs encantados
				enchantType = (buff.getLevel() - baseMaxLvl) / buff.getEnchantLevelCount();
				enchantLvl = (buff.getLevel() - baseMaxLvl) % buff.getEnchantLevelCount();
				enchantLvl = (enchantLvl == 0 ? buff.getEnchantLevelCount() : enchantLvl);

				buffList.append("<td fixwidth=240>" + "<font name=__SYSTEMWORLDFONT color=C73232>" + buff.getName() + "<font>" + " - <font color=329231>Level</font> <font color=FFFFFF>" + baseMaxLvl + "</font>");
				buffList.append(" <br1> › <font color=F1C101 name=__SYSTEMWORLDFONT>Enchant: </font><font color=ffd969 name=CreditTextNormal>+" + enchantLvl + " " + (enchantType >= 3 ? "Power" : (enchantType >= 2 ? "Cost" : "Time")) + "</font></td>");
			}
			else
			{
				buffList.append("<td fixwidth=240>" + "<font name=__SYSTEMWORLDFONT color=C73232>" + buff.getName() + "<font>" + " - <font color=329231>Level</font> <font color=FFFFFF>" + buff.getLevel() + "</font>");
				buffList.append(" <br1> › <font color=F1C101 name=__SYSTEMWORLDFONT>Enchant: </font><font color=FFFFFF name=CreditTextNormal> None</font></td>");

			}
			buffList.append("</tr>");
			buffList.append("<tr><td></td></tr>");
			buffList.append("</table>");
			buffList.append("</td>");
			buffList.append("</tr>");

			// Espacio entre cada linea de buff
			buffList.append("<tr>");
			buffList.append("<td height=10></td>");
			buffList.append("</tr>");

			i++;
			changeColor = !changeColor;
		}

		// Make the arrows buttons
		final String previousPageButton;
		final String nextPageButton;
		if (currentPage > 0)
		{
			previousPageButton = "<button value=\"\" width=15 height=15 action=\"bypass -h BuffStore bufflist " + buffer.getOwner().getObjectId() + " " + (isForPlayer ? "player" : "summon") + " " + (currentPage - 1) + "\" back=L2UI_CT1.ItemWindow_DF_Frame_Down fore=L2UI_CT1.ItemWindow_DF_Frame>";
		}
		else
		{
			previousPageButton = "<button value=\"\" width=15 height=15 action=\"\" back=L2UI_CT1.ItemWindow_DF_Frame_Down fore=L2UI_CT1.ItemWindow_DF_Frame>";
		}

		if (currentPage < maxPage)
		{
			nextPageButton = "<button value=\"\" width=15 height=15 action=\"bypass -h BuffStore bufflist " + buffer.getOwner().getObjectId() + " " + (isForPlayer ? "player" : "summon") + " " + (currentPage + 1) + "\" back=L2UI_CT1.ItemWindow_DF_Frame_Down fore=L2UI_CT1.ItemWindow_DF_Frame>";
		}
		else
		{
			nextPageButton = "<button value=\"\" width=15 height=15 action=\"\" back=L2UI_CT1.ItemWindow_DF_Frame_Down fore=L2UI_CT1.ItemWindow_DF_Frame>";
		}

		html.replace("%bufferId%", buffer.getOwner().getObjectId());
		html.replace("%bufferClass%", Util.toProperCaseAll(CharTemplateHolder.getInstance().getTemplate(buffer.getOwner().getClassId(), false).className));
		html.replace("%bufferLvl%", (buffer.getOwner().getLevel() >= 76 && buffer.getOwner().getLevel() < 80 ? 76 : (buffer.getOwner().getLevel() >= 84 ? 84 : Math.round(buffer.getOwner().getLevel() / 10) * 10)));
		html.replace("%bufferName%", buffer.getOwner().getName());
		html.replace("%bufferMp%", (int) buffer.getOwner().getCurrentMp());
		html.replace("%buffPrice%", Util.convertToLineagePriceFormat(buffer.getBuffPrice()));
		html.replace("%target%", (isForPlayer ? "Player" : "Summon"));
		html.replace("%page%", currentPage);
		html.replace("%buffs%", buffList.toString());
		html.replace("%previousPageButton%", previousPageButton);
		html.replace("%nextPageButton%", nextPageButton);
		html.replace("%pageCount%", (currentPage + 1) + "/" + (maxPage + 1));

		player.sendPacket(html);
	}

	public static OfflineBufferManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final OfflineBufferManager _instance = new OfflineBufferManager();
	}

	// Clase donde se guardan todos los datos de cada offline buffer activo
	public static class BufferData
	{
		private final Player _owner;
		private final String _saleTitle;
		private final int _buffPrice;
		private final Map<Integer, Skill> _buffs = new HashMap<>();

		public BufferData(Player player, String title, int price, List<Skill> buffs)
		{
			_owner = player;
			_saleTitle = title;
			_buffPrice = price;
			if (buffs != null)
			{
				for (Skill buff : buffs)
				{
					_buffs.put(buff.getId(), buff);
				}
			}
		}

		public Player getOwner()
		{
			return _owner;
		}

		public String getSaleTitle()
		{
			return _saleTitle;
		}

		public int getBuffPrice()
		{
			return _buffPrice;
		}

		public Map<Integer, Skill> getBuffs()
		{
			return _buffs;
		}
	}
}
