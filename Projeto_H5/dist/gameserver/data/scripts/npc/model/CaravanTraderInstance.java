package npc.model;

import java.util.List;
import java.util.StringTokenizer;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.data.xml.holder.MultiSellHolder;
import l2f.gameserver.instancemanager.HellboundManager;
import l2f.gameserver.instancemanager.ServerVariables;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.ReflectionUtils;
import l2f.gameserver.utils.Util;

/**
 * @author pchayka
 */

public final class CaravanTraderInstance extends NpcInstance
{
	private static final int NativeTreasure = 9684;
	private static final int HolyWater = 9673;
	private static final int DarionsBadge = 9674;
	private static final int FirstMark = 9850;
	private static final int SecondMark = 9851;
	private static final int ThirdMark = 9852;
	private static final int ForthMark = 9853;

	private static final int ScorpionPoisonStinger = 10012;
	private static final int MarkOfBetrayal = 9676;
	private static final int MagicBottle = 9672;
	private static final int NativeHelmet = 9669;
	private static final int NativeTunic = 9670;
	private static final int NativePants = 9671;

	private static final int LifeForce = 9681;
	private static final int DimLifeForce = 9680;
	private static final int ContainedLifeForce = 9682;

	private static final int FieryDemonBloodSkill = 2357;

	public CaravanTraderInstance(int objectId, NpcTemplate template)
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

		if (command.startsWith("Chat")) // general
		{
			int val = 0;
			try
			{
				val = Integer.parseInt(command.substring(5));
			}
			catch (IndexOutOfBoundsException ioobe)
			{
			}
			catch (NumberFormatException nfe)
			{
			}
			showDialog(player, getHtmlPath(getNpcId(), val, player));
			return;
		}
		else if (command.startsWith("give_treasures")) // Jude
		{
			if (player.getInventory().getCountOf(NativeTreasure) >= 40)
			{
				Functions.removeItem(player, NativeTreasure, 40, "CaravanTraderInstance");
				ServerVariables.set("HB_judesBoxes", true);
				showDialog(player, getHtmlPath(getNpcId(), 3, player));
				return;
			}
			else
			{
				showDialog(player, getHtmlPath(getNpcId(), 4, player));
				return;
			}
		}
		else if (command.startsWith("buy_holy_water")) // Bernarde
		{
			if (player.getInventory().getCountOf(HolyWater) >= 1)
			{
				showDialog(player, getHtmlPath(getNpcId(), 10, player));
				return;
			}
			if (player.getInventory().getCountOf(DarionsBadge) >= 5)
			{
				Functions.removeItem(player, DarionsBadge, 5, "CaravanTraderInstance");
				Functions.addItem(player, HolyWater, 1, "CaravanTraderInstance");
				showDialog(player, getHtmlPath(getNpcId(), 6, player));
				return;
			}
			else
			{
				showDialog(player, getHtmlPath(getNpcId(), 3, player));
				return;
			}
		}
		else if (command.startsWith("one_treasure")) // Bernarde
		{
			if (player.getInventory().getCountOf(NativeTreasure) >= 1 && !ServerVariables.getBool("HB_bernardBoxes", false))
			{
				Functions.removeItem(player, NativeTreasure, 1, "CaravanTraderInstance");
				ServerVariables.set("HB_bernardBoxes", true);
				showDialog(player, getHtmlPath(getNpcId(), 8, player));
				return;
			}
			else
			{
				showDialog(player, getHtmlPath(getNpcId(), 9, player));
				return;
			}
		}
		else if (command.startsWith("request_1_badge")) // Falk
		{
			if (hasProperMark(player, 1)) // has any mark
			{
				showDialog(player, getHtmlPath(getNpcId(), 3, player));
				return;
			}
			if (player.getInventory().getCountOf(DarionsBadge) >= 20) // trade mark
			{
				Functions.removeItem(player, DarionsBadge, 20, "CaravanTraderInstance");
				Functions.addItem(player, FirstMark, 1, "CaravanTraderInstance");
				showDialog(player, getHtmlPath(getNpcId(), 4, player));
				return;
			}
			else
			// not enough badges
			{
				showDialog(player, getHtmlPath(getNpcId(), 5, player));
				return;
			}
		}
		else if (command.startsWith("bdgc"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
				if (!st.hasMoreTokens())
				{
					return;
				}
				String param = st.nextToken();
				if (param.length() < 1 || !Util.isNumber(param))
				{
					player.sendMessage("Incorrect count");
					return;
				}
				int val = Integer.parseInt(param);
				if (val <= 0)
				{
					player.sendMessage("Incorrect count");
					return;
				}
				if (player.getInventory().getCountOf(DarionsBadge) < val)
				{
					showDialog(player, getHtmlPath(getNpcId(), 2, player));
					return;
				}
				Functions.removeItem(player, DarionsBadge, val, "CaravanTraderInstance");
				HellboundManager.addConfidence(val * 10L);
				showDialog(player, getHtmlPath(getNpcId(), 3, player));
				return;
			}
			catch (NumberFormatException nfe)
			{
				showDialog(player, getHtmlPath(getNpcId(), 4, player));
				return;
			}
		}
		else if (command.startsWith("buy_magic_bottle")) // Kief
		{
			if (player.getInventory().getCountOf(ScorpionPoisonStinger) >= 20 && hasProperMark(player, 1))
			{
				Functions.removeItem(player, ScorpionPoisonStinger, 20, "CaravanTraderInstance");
				Functions.addItem(player, MagicBottle, 1, "CaravanTraderInstance");
				showDialog(player, getHtmlPath(getNpcId(), 6, player));
				return;
			}
			else
			// not enough
			{
				showDialog(player, getHtmlPath(getNpcId(), 7, player));
				return;
			}
		}
		else if (command.startsWith("cntf"))
		{
			try
			{
				int val = Integer.parseInt(command.substring(5));
				if (val <= 0)
				{
					return;
				}

				switch (val)
				{
				case 1:
					if (player.getInventory().getCountOf(LifeForce) < 10)
					{
						showDialog(player, getHtmlPath(getNpcId(), 2, player));
						return;
					}
					Functions.removeItem(player, LifeForce, 10, "CaravanTraderInstance");
					HellboundManager.addConfidence(100);
					showDialog(player, getHtmlPath(getNpcId(), 3, player));
					break;
				case 2:
					if (player.getInventory().getCountOf(DimLifeForce) < 5)
					{
						showDialog(player, getHtmlPath(getNpcId(), 2, player));
						return;
					}
					Functions.removeItem(player, DimLifeForce, 5, "CaravanTraderInstance");
					HellboundManager.addConfidence(100);
					showDialog(player, getHtmlPath(getNpcId(), 3, player));
					break;
				case 3:
					if (player.getInventory().getCountOf(ContainedLifeForce) < 1)
					{
						showDialog(player, getHtmlPath(getNpcId(), 2, player));
						return;
					}
					Functions.removeItem(player, ContainedLifeForce, 1, "CaravanTraderInstance");
					HellboundManager.addConfidence(50);
					showDialog(player, getHtmlPath(getNpcId(), 3, player));
					break;
				}
			}
			catch (NumberFormatException nfe)
			{
				return;
			}
		}
		else if (command.startsWith("getc"))
		{
			try
			{
				int val = Integer.parseInt(command.substring(5));
				if (val <= 0)
				{
					return;
				}

				if (player.getInventory().getCountOf(DarionsBadge) < 10)
				{
					showDialog(player, getHtmlPath(getNpcId(), 3, player));
					return;
				}
				switch (val)
				{
				case 1:
					Functions.removeItem(player, DarionsBadge, 10, "CaravanTraderInstance");
					Functions.addItem(player, NativeHelmet, 1, "CaravanTraderInstance");
					showDialog(player, getHtmlPath(getNpcId(), 4, player));
					break;
				case 2:
					Functions.removeItem(player, DarionsBadge, 10, "CaravanTraderInstance");
					Functions.addItem(player, NativeTunic, 1, "CaravanTraderInstance");
					showDialog(player, getHtmlPath(getNpcId(), 4, player));
					break;
				case 3:
					Functions.removeItem(player, DarionsBadge, 10, "CaravanTraderInstance");
					Functions.addItem(player, NativePants, 1, "CaravanTraderInstance");
					showDialog(player, getHtmlPath(getNpcId(), 4, player));
					break;
				}
			}
			catch (NumberFormatException nfe)
			{
				return;
			}
		}
		else if (command.startsWith("get_second")) // Hude
		{
			if (player.getInventory().getCountOf(FirstMark) >= 1 && player.getInventory().getCountOf(MarkOfBetrayal) >= 30 && player.getInventory().getCountOf(ScorpionPoisonStinger) >= 60)
			{
				Functions.removeItem(player, FirstMark, 1, "CaravanTraderInstance");
				Functions.removeItem(player, MarkOfBetrayal, 30, "CaravanTraderInstance");
				Functions.removeItem(player, ScorpionPoisonStinger, 60, "CaravanTraderInstance");
				Functions.addItem(player, SecondMark, 1, "CaravanTraderInstance");
				showDialog(player, getHtmlPath(getNpcId(), 3, player));
				return;
			}
			else
			{
				showDialog(player, getHtmlPath(getNpcId(), 4, player));
				return;
			}
		}
		else if (command.startsWith("secret_med")) // Hude
		{
			MultiSellHolder.getInstance().SeparateAndSend(250980014, player, 0);
			return;
		}
		else if (command.startsWith("get_third")) // Hude
		{
			if (player.getInventory().getCountOf(SecondMark) >= 1 && player.getInventory().getCountOf(LifeForce) >= 56 && player.getInventory().getCountOf(ContainedLifeForce) >= 14)
			{
				Functions.removeItem(player, SecondMark, 1, "CaravanTraderInstance");
				Functions.removeItem(player, LifeForce, 56, "CaravanTraderInstance");
				Functions.removeItem(player, ContainedLifeForce, 14, "CaravanTraderInstance");
				Functions.addItem(player, ThirdMark, 1, "CaravanTraderInstance");
				Functions.addItem(player, 9994, 1, "CaravanTraderInstance"); // Hellbound Map
				showDialog(player, getHtmlPath(getNpcId(), 6, player));
				return;
			}
			else
			{
				showDialog(player, getHtmlPath(getNpcId(), 4, player));
				return;
			}
		}
		else if (command.startsWith("s80_trade")) // Hude
		{
			MultiSellHolder.getInstance().SeparateAndSend(250980013, player, 0);
			return;
		}
		else if (command.startsWith("try_open_door")) // Traitor
		{
			if (player.getInventory().getCountOf(MarkOfBetrayal) >= 10)
			{
				Functions.removeItem(player, MarkOfBetrayal, 10, "CaravanTraderInstance");
				ReflectionUtils.getDoor(19250003).openMe();
				ReflectionUtils.getDoor(19250004).openMe();
				ThreadPoolManager.getInstance().schedule(new CloseDoor(), 60 * 1000L);
			}
			else
			{
				showDialog(player, getHtmlPath(getNpcId(), 4, player));
				return;
			}
		}
		else if (command.startsWith("supply_badges")) // Native Slave
		{
			if (player.getInventory().getCountOf(DarionsBadge) >= 5)
			{
				Functions.removeItem(player, DarionsBadge, 5, "CaravanTraderInstance");
				HellboundManager.addConfidence(20);
				showDialog(player, getHtmlPath(getNpcId(), 2, player));
				return;
			}
			else
			{
				showDialog(player, getHtmlPath(getNpcId(), 3, player));
				return;
			}
		}
		else if (command.startsWith("tully_entrance")) // Deltuva
		{
			if (player.isQuestCompleted("_132_MatrasCuriosity"))
			{
				player.teleToLocation(new Location(17947, 283205, -9696));
				return;
			}
			else
			{
				showDialog(player, getHtmlPath(getNpcId(), 1, player));
				return;
			}
		}
		else if (command.startsWith("infinitum_entrance")) // Jerian
		{
			if (player.getParty() == null || !player.getParty().isLeader(player))
			{
				showDialog(player, getHtmlPath(getNpcId(), 1, player));
				return;
			}

			List<Player> members = player.getParty().getMembers();
			for (Player member : members)
			{
				if (member == null || !isInRange(member, 500) || member.getEffectList().getEffectsBySkillId(FieryDemonBloodSkill) == null)
				{
					showDialog(player, getHtmlPath(getNpcId(), 2, player));
					return;
				}
			}

			for (Player member : members)
			{
				member.teleToLocation(new Location(-22204, 277056, -15045));
			}
			return;
		}
		else if (command.startsWith("tully_dorian_entrance")) // Dorian
		{
			if (player.getParty() == null || !player.getParty().isLeader(player))
			{
				showDialog(player, getHtmlPath(getNpcId(), 2, player));
				return;
			}
			List<Player> members = player.getParty().getMembers();

			for (Player member : members)
			{
				if (member == null || !isInRange(member, 500) || !member.isQuestCompleted("_132_MatrasCuriosity"))
				{
					showDialog(player, getHtmlPath(getNpcId(), 1, player));
					return;
				}
			}

			for (Player member : members)
			{
				member.teleToLocation(new Location(-13400, 272827, -15304));
			}
		}
		else if (command.startsWith("enter_urban")) // Kanaf - urban area instance
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if (player.canReenterInstance(2))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if (player.canEnterInstance(2))
			{
				ReflectionUtils.enterReflection(player, 2);
			}
		}
		else
		{ // Kanaf - urban area instance
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		String htmlpath = null;
		switch (getNpcId())
		{
		case 32356: // Jude
			if (HellboundManager.getHellboundLevel() <= 1)
			{
				htmlpath = getHtmlPath(getNpcId(), 0, player);
			}
			else if (HellboundManager.getHellboundLevel() == 5)
			{
				htmlpath = getHtmlPath(getNpcId(), 5, player);
			}
			else if (!ServerVariables.getBool("HB_judesBoxes", false))
			{
				htmlpath = getHtmlPath(getNpcId(), 1, player);
			}
			else
			{
				htmlpath = getHtmlPath(getNpcId(), 2, player);
			}
			break;
		case 32300: // Bernarde
			if (player.getTransformation() != 101)
			{
				htmlpath = getHtmlPath(getNpcId(), 5, player);
			}
			else if (HellboundManager.getHellboundLevel() < 2)
			{
				htmlpath = getHtmlPath(getNpcId(), 0, player);
			}
			else if (HellboundManager.getHellboundLevel() == 2)
			{
				htmlpath = getHtmlPath(getNpcId(), 1, player);
			}
			else if (HellboundManager.getHellboundLevel() == 3 && !ServerVariables.getBool("HB_bernardBoxes", false))
			{
				htmlpath = getHtmlPath(getNpcId(), 2, player);
			}
			else if (HellboundManager.getHellboundLevel() >= 3)
			{
				htmlpath = getHtmlPath(getNpcId(), 7, player);
			}
			break;
		case 32297: // Falk
			if (HellboundManager.getHellboundLevel() <= 1)
			{
				htmlpath = getHtmlPath(getNpcId(), 0, player);
			}
			else if (HellboundManager.getHellboundLevel() > 1)
			{
				htmlpath = getHtmlPath(getNpcId(), 1, player);
			}
			break;
		case 32354: // Kief
			if (HellboundManager.getHellboundLevel() <= 1)
			{
				htmlpath = getHtmlPath(getNpcId(), 0, player);
			}
			else if (HellboundManager.getHellboundLevel() == 2 || HellboundManager.getHellboundLevel() == 3)
			{
				htmlpath = getHtmlPath(getNpcId(), 1, player);
			}
			else if (HellboundManager.getHellboundLevel() == 6)
			{
				htmlpath = getHtmlPath(getNpcId(), 9, player);
			}
			else if (HellboundManager.getHellboundLevel() == 7)
			{
				htmlpath = getHtmlPath(getNpcId(), 10, player);
			}
			else if (HellboundManager.getHellboundLevel() > 7)
			{
				htmlpath = getHtmlPath(getNpcId(), 5, player);
			}
			else
			{
				htmlpath = getHtmlPath(getNpcId(), 8, player);
			}
			break;
		case 32345: // Buron
			if (HellboundManager.getHellboundLevel() <= 1)
			{
				htmlpath = getHtmlPath(getNpcId(), 0, player);
			}
			else if (HellboundManager.getHellboundLevel() == 5)
			{
				htmlpath = getHtmlPath(getNpcId(), 7, player);
			}
			else if (HellboundManager.getHellboundLevel() == 6)
			{
				htmlpath = getHtmlPath(getNpcId(), 5, player);
			}
			else if (HellboundManager.getHellboundLevel() == 8)
			{
				htmlpath = getHtmlPath(getNpcId(), 6, player);
			}
			else
			{
				htmlpath = getHtmlPath(getNpcId(), 1, player);
			}
			break;
		case 32355: // Solomon
			if (HellboundManager.getHellboundLevel() == 5)
			{
				htmlpath = getHtmlPath(getNpcId(), 1, player);
			}
			else
			{
				htmlpath = getHtmlPath(getNpcId(), 0, player);
			}
			break;
		case 32298: // Hude
			if (HellboundManager.getHellboundLevel() <= 1)
			{
				htmlpath = getHtmlPath(getNpcId(), 0, player);
			}
			else if (!hasProperMark(player, 1))
			{
				htmlpath = getHtmlPath(getNpcId(), 1, player);
			}
			else if (player.getInventory().getCountOf(FirstMark) > 0)
			{
				htmlpath = getHtmlPath(getNpcId(), 2, player);
			}
			else if (player.getInventory().getCountOf(SecondMark) > 0)
			{
				htmlpath = getHtmlPath(getNpcId(), 5, player);
			}
			else if (player.getInventory().getCountOf(ThirdMark) > 0)
			{
				htmlpath = getHtmlPath(getNpcId(), 8, player);
			}
			break;
		case 32364: // Traitor
			if (HellboundManager.getHellboundLevel() == 5)
			{
				htmlpath = getHtmlPath(getNpcId(), 0, player);
			}
			else
			{
				htmlpath = getHtmlPath(getNpcId(), 6, player);
			}
			break;
		case 32357: // Native Slave
			if (HellboundManager.getHellboundLevel() == 9)
			{
				htmlpath = getHtmlPath(getNpcId(), 1, player);
			}
			else if (HellboundManager.getHellboundLevel() == 10)
			{
				htmlpath = getHtmlPath(getNpcId(), 4, player);
			}
			else
			{
				htmlpath = getHtmlPath(getNpcId(), 0, player);
			}
			break;
		case 32346: // Kanaf
			if (HellboundManager.getHellboundLevel() >= 10)
			{
				htmlpath = getHtmlPath(getNpcId(), 0, player);
			}
			else
			{
				htmlpath = getHtmlPath(getNpcId(), 3, player);
			}
			break;
		case 32313: // Deltuva
			if (HellboundManager.getHellboundLevel() >= 11)
			{
				htmlpath = getHtmlPath(getNpcId(), 0, player);
			}
			else
			{
				htmlpath = getHtmlPath(getNpcId(), 2, player);
			}
			break;
		case 32302: // Jerian
			if (HellboundManager.getHellboundLevel() >= 11)
			{
				htmlpath = getHtmlPath(getNpcId(), 0, player);
			}
			else
			{
				htmlpath = getHtmlPath(getNpcId(), 3, player);
			}
			break;
		case 32373: // Dorian
			if (HellboundManager.getHellboundLevel() >= 11)
			{
				htmlpath = getHtmlPath(getNpcId(), 0, player);
			}
			else
			{
				htmlpath = getHtmlPath(getNpcId(), 3, player);
			}
			break;
		}
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(htmlpath);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		return "hellbound/" + pom + ".htm";
	}

	private void showDialog(Player player, String path)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile(path);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
	}

	private boolean hasProperMark(Player player, int mark)
	{
		switch (mark)
		{
		case 1:
			if (player.getInventory().getCountOf(FirstMark) != 0 || player.getInventory().getCountOf(SecondMark) != 0 || player.getInventory().getCountOf(ThirdMark) != 0 || player.getInventory().getCountOf(ForthMark) != 0)
			{
				return true;
			}
			break;
		case 2:
			if (player.getInventory().getCountOf(SecondMark) != 0 || player.getInventory().getCountOf(ThirdMark) != 0 || player.getInventory().getCountOf(ForthMark) != 0)
			{
				return true;
			}
			break;
		case 3:
			if (player.getInventory().getCountOf(ThirdMark) != 0 || player.getInventory().getCountOf(ForthMark) != 0)
			{
				return true;
			}
			break;
		case 4:
			if (player.getInventory().getCountOf(ForthMark) != 0)
			{
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}

	private class CloseDoor extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			ReflectionUtils.getDoor(19250003).closeMe();
			ReflectionUtils.getDoor(19250004).closeMe();
		}
	}
}