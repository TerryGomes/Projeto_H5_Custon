package npc.model;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.NpcUtils;

/**
 * @author FandC
 */

@SuppressWarnings("serial")
public final class DragonVortexInstance extends NpcInstance
{
	private final int[] bosses =
	{
		25718,
		25719,
		25720,
		25721,
		25722,
		25723,
		25724
	};
	private NpcInstance boss;

	public DragonVortexInstance(int objectId, NpcTemplate template)
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

		if (command.startsWith("request_boss"))
		{
			if (ItemFunctions.getItemCount(player, 17248) > 0)
			{
				ItemFunctions.removeItem(player, 17248, 1, true, "DragonVortex");
				boss = NpcUtils.spawnSingle(bosses[Rnd.get(bosses.length)], getLoc().coordsRandomize(300, 600), getReflection());
				showChatWindow(player, "default/32871-1.htm");
			}
			else
			{
				showChatWindow(player, "default/32871-2.htm");
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	public NpcInstance getBoss()
	{
		return boss;
	}

	public void setBoss(NpcInstance _boss)
	{
		boss = _boss;
	}
}

//package npc.model;
//
//import l2mv.commons.util.Rnd;
//import l2mv.gameserver.ThreadPoolManager;
//import l2mv.gameserver.model.Player;
//import l2mv.gameserver.model.instances.NpcInstance;
//import l2mv.gameserver.templates.npc.NpcTemplate;
//import l2mv.gameserver.utils.ItemFunctions;
//import l2mv.gameserver.utils.NpcUtils;
//
///**
// * @author FandC
// */
//
//@SuppressWarnings("serial")
//public final class DragonVortexInstance extends NpcInstance
//{
//	private final int[] bosses = { 25718, 25719, 25720, 25721, 25722, 25723, 25724 };
//	private NpcInstance boss;
//
//	public DragonVortexInstance(int objectId, NpcTemplate template)
//	{
//		super(objectId, template);
//	}
//
//	@Override
//	public void onBypassFeedback(Player player, String command)
//	{
//		if (!canBypassCheck(player, this))
//			return;
//
//		if (command.startsWith("request_boss"))
//		{
//			if (getBoss() != null && !getBoss().isDead())
//			{
//				showChatWindow(player, "default/32871-3.htm");
//				return;
//			}
//
//			if (ItemFunctions.getItemCount(player, 17248) > 0)
//			{
//				ItemFunctions.removeItem(player, 17248, 1, true, "DragonVortex");
//				boss = NpcUtils.spawnSingle(bosses[Rnd.get(bosses.length)], getLoc().coordsRandomize(300, 600), getReflection());
//				ThreadPoolManager.getInstance().schedule(new Runnable()
//				{
//					@Override
//					public void run()
//					{
//						if (getBoss() != null && !getBoss().isDead())
//							setBoss(null);
//					}
//				}, 1800000);
//				showChatWindow(player, "default/32871-1.htm");
//			}
//			else
//				showChatWindow(player, "default/32871-2.htm");
//		}
//		else
//			super.onBypassFeedback(player, command);
//	}
//
//	public NpcInstance getBoss()
//	{
//		return boss;
//	}
//
//	public void setBoss(NpcInstance _boss)
//	{
//		boss = _boss;
//	}
//}