package handler.bypass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.cache.ImagesCache;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.handler.bypass.BypassHandler;
import l2mv.gameserver.handler.bypass.IBypassHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.tournament.ActiveBattleManager;
import l2mv.gameserver.model.entity.tournament.BattleInstance;
import l2mv.gameserver.model.entity.tournament.BattleRecord;
import l2mv.gameserver.model.entity.tournament.BattleScheduleManager;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.instances.SchemeBufferInstance;
import l2mv.gameserver.network.serverpackets.TutorialCloseHtml;
import l2mv.gameserver.network.serverpackets.TutorialEnableClientEvent;
import l2mv.gameserver.network.serverpackets.TutorialShowHtml;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.templates.item.ItemTemplate;

/**
 * Bypass support for tournament system
 *
 * @author Synerge
 */
public class BypassTournament implements ScriptFile, IBypassHandler
{
	@Override
	public String[] getBypasses()
	{
		return new String[]
		{
			"tournament"
		};
	}

	@Override
	public void onBypassFeedback(NpcInstance npc, Player player, String command)
	{
		final BattleRecord battleRecord = BattleScheduleManager.getInstance().getBattle(player);
		if (battleRecord == null)
		{
			player.sendPacket(new TutorialEnableClientEvent(0));
			player.sendPacket(TutorialCloseHtml.STATIC);
			return;
		}
		final BattleInstance battleInstance = battleRecord.getBattleInstance();
		if (battleInstance == null || battleInstance.isBattleOver() || !battleInstance.isFighter(player) || battleInstance.isFightTime())
		{
			player.sendPacket(new TutorialEnableClientEvent(0));
			player.sendPacket(TutorialCloseHtml.STATIC);
			return;
		}

		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		switch (st.nextToken())
		{
		case "main":
		{
			String html = HtmCache.getInstance().getNotNull("tournament/main.htm", player);
			html = ImagesCache.getInstance().sendUsedImages(html, player); // Enviamos imagenes
			player.sendPacket(new TutorialShowHtml(html));
			break;
		}
		case "buffer":
		{
			if (ConfigHolder.getBool("TournamentAllowBuffer"))
			{
				SchemeBufferInstance.showWindow(player);
			}
			break;
		}
		case "showChooseArmor":
		{
			String html = HtmCache.getInstance().getNotNull("tournament/chooseArmor.htm", player);

			final StringBuilder sb = new StringBuilder();
			boolean nextColor = false;
			for (ItemTemplate item : getChests())
			{
				sb.append("<table width=273 height=32 bgcolor=" + (nextColor ? "1b1916" : "2a2525") + ">");
				sb.append("<tr>");
				sb.append("<td width=40 align=center>");
				sb.append("<table cellspacing=0 cellpadding=0 width=32 height=32 background=" + item.getIcon() + ">");
				sb.append("<tr>");
				sb.append("<td width=32 height=32 align=center valign=top>");
				sb.append("<img src=Btns.nice_frame width=32 height=32 />");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("<td fixwidth=193>");
				sb.append("<font color=c1c1c1 name=hs12>" + item.getName() + "</font>");
				sb.append("</td>");
				sb.append("<td width=40 align=center>");
				sb.append("<button action=\"bypass -h tournament chooseArmor " + item.getItemId() + "\" width=32 height=32 back=L2UI_CT1.MiniMap_DF_PlusBtn_Red_Down fore=L2UI_CT1.MiniMap_DF_PlusBtn_Red />");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");

				nextColor = !nextColor;
			}

			html = html.replace("%list%", sb.toString());
			html = ImagesCache.getInstance().sendUsedImages(html, player); // Enviamos imagenes
			player.sendPacket(new TutorialShowHtml(html));
			break;
		}
		case "showChooseWeapon":
		{
			String html = HtmCache.getInstance().getNotNull("tournament/chooseWeapon.htm", player);

			final StringBuilder sb = new StringBuilder();
			boolean nextColor = false;
			for (ItemTemplate item : getWeaponsSingleSA())
			{
				sb.append("<table width=273 height=32 bgcolor=" + (nextColor ? "1b1916" : "2a2525") + ">");
				sb.append("<tr>");
				sb.append("<td width=40 align=center>");
				sb.append("<table cellspacing=0 cellpadding=0 width=32 height=32 background=" + item.getIcon() + ">");
				sb.append("<tr>");
				sb.append("<td width=32 height=32 align=center valign=top>");
				sb.append("<img src=Btns.nice_frame width=32 height=32 />");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("<td fixwidth=193>");
				sb.append("<font color=c1c1c1 name=hs12>" + item.getName() + "</font>");
				sb.append("</td>");
				sb.append("<td width=40 align=center>");
				sb.append("<button action=\"bypass -h tournament showChooseSA " + item.getItemId() + "\" width=32 height=32 back=L2UI_CT1.MiniMap_DF_PlusBtn_Red_Down fore=L2UI_CT1.MiniMap_DF_PlusBtn_Red />");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");

				nextColor = !nextColor;
			}

			html = html.replace("%list%", sb.toString());
			html = ImagesCache.getInstance().sendUsedImages(html, player); // Enviamos imagenes
			player.sendPacket(new TutorialShowHtml(html));
			break;
		}
		case "showChooseSA":
		{
			final int weaponId = Integer.parseInt(st.nextToken());

			String html = HtmCache.getInstance().getNotNull("tournament/chooseWeaponSA.htm", player);

			final StringBuilder sb = new StringBuilder();
			boolean nextColor = false;
			for (ItemTemplate item : getWeaponSpecialAbilities(weaponId))
			{
				sb.append("<table width=273 height=32 bgcolor=" + (nextColor ? "1b1916" : "2a2525") + ">");
				sb.append("<tr>");
				sb.append("<td width=40 align=center>");
				sb.append("<table cellspacing=0 cellpadding=0 width=32 height=32 background=" + item.getIcon() + ">");
				sb.append("<tr>");
				sb.append("<td width=32 height=32 align=center valign=top>");
				sb.append("<img src=Btns.nice_frame width=32 height=32 />");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				sb.append("<td fixwidth=193>");
				sb.append("<font color=c1c1c1 name=hs12>" + item.getName() + "</font>");
				sb.append("</td>");
				sb.append("<td width=40 align=center>");
				sb.append("<button action=\"bypass -h tournament chooseWeapon " + item.getItemId() + "\" width=32 height=32 back=L2UI_CT1.MiniMap_DF_PlusBtn_Red_Down fore=L2UI_CT1.MiniMap_DF_PlusBtn_Red />");
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");

				nextColor = !nextColor;
			}

			html = html.replace("%list%", sb.toString());
			html = ImagesCache.getInstance().sendUsedImages(html, player); // Enviamos imagenes
			player.sendPacket(new TutorialShowHtml(html));
			break;
		}
		case "chooseWeapon":
		{
			final int weaponId = Integer.parseInt(st.nextToken());

			if (ArrayUtils.contains(ConfigHolder.getIntArray("TournamentWeaponsToBeChosen"), weaponId))
			{
				ActiveBattleManager.deleteItems(battleInstance, player, ConfigHolder.getIntArray("TournamentWeaponsToBeChosen"), "ChoosingDifferentWeapon");
				ActiveBattleManager.addItem(battleInstance, player, weaponId, true, true, "ChoosingWeapon");
			}

			onBypassFeedback(null, player, "tournament main");
			break;
		}
		case "chooseArmor":
		{
			final int chestId = Integer.parseInt(st.nextToken());

			final int[] chosenSet = getArrayContains(ConfigHolder.getMultiIntArray("TournamentArmorsToBeChosen"), chestId);
			if (chosenSet != null)
			{
				for (int[] set : ConfigHolder.getMultiIntArray("TournamentArmorsToBeChosen"))
				{
					ActiveBattleManager.deleteItems(battleInstance, player, set, "ChoosingDifferentArmor");
				}
				for (int setItemId : chosenSet)
				{
					ActiveBattleManager.addItem(battleInstance, player, setItemId, true, false, "ChoosingArmor");
				}
			}

			onBypassFeedback(null, player, "tournament main");
			break;
		}
		}
	}

	private static Collection<ItemTemplate> getWeaponsSingleSA()
	{
		final int[] ids = ConfigHolder.getIntArray("TournamentWeaponsToBeChosen");
		final Map<String, ItemTemplate> itemsByName = new HashMap<String, ItemTemplate>(ids.length);
		for (int id : ids)
		{
			final ItemTemplate template = ItemHolder.getInstance().getTemplate(id);
			if (template != null && !itemsByName.containsKey(template.getName()))
			{
				itemsByName.put(template.getName(), template);
			}
		}
		return itemsByName.values();
	}

	private static List<ItemTemplate> getWeaponSpecialAbilities(int weaponId)
	{
		final ItemTemplate item = ItemHolder.getInstance().getTemplate(weaponId);
		if (item == null)
		{
			return Collections.emptyList();
		}
		final List<ItemTemplate> weapons = new ArrayList<ItemTemplate>(3);
		for (int id : ConfigHolder.getIntArray("TournamentWeaponsToBeChosen"))
		{
			final ItemTemplate template = ItemHolder.getInstance().getTemplate(id);
			if (template != null && template.getName().equals(item.getName()))
			{
				weapons.add(template);
			}
		}
		return weapons;
	}

	private static List<ItemTemplate> getChests()
	{
		final int[][] sets = ConfigHolder.getMultiIntArray("TournamentArmorsToBeChosen");
		final List<ItemTemplate> setTemplates = new ArrayList<ItemTemplate>(sets.length);
		for (int[] set : sets)
		{
			final int chestId = getChestId(set);
			if (chestId > 0)
			{
				final ItemTemplate chest = ItemHolder.getInstance().getTemplate(chestId);
				if (chest != null)
				{
					setTemplates.add(chest);
				}
			}
		}
		return setTemplates;
	}

	private static int getChestId(int[] ids)
	{
		for (int id : ids)
		{
			final ItemTemplate template = ItemHolder.getInstance().getTemplate(id);
			if (template != null && (template.getBodyPart() == 1024 || template.getBodyPart() == 32768))
			{
				return id;
			}
		}
		return -1;
	}

	private static int[] getArrayContains(int[][] multiArray, int number)
	{
		for (int[] array : multiArray)
		{
			if (ArrayUtils.contains(array, number))
			{
				return array;
			}
		}
		return null;
	}

	@Override
	public void onLoad()
	{
		BypassHandler.getInstance().registerBypass(this);
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}
}
