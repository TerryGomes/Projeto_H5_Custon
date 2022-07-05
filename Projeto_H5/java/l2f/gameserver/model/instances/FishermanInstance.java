package l2f.gameserver.model.instances;

import l2f.gameserver.Config;
import l2f.gameserver.instancemanager.games.FishingChampionShipManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.templates.npc.NpcTemplate;

public class FishermanInstance extends MerchantInstance
{
	public FishermanInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom = "";

		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}

		return "fisherman/" + pom + ".htm";
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}
		if (command.equalsIgnoreCase("FishingSkillList"))
		{
			showFishingSkillList(player);
		}
		else if (command.startsWith("FishingChampionship") && Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
		{
			FishingChampionShipManager.getInstance().showChampScreen(player, this);
		}
		else if (command.startsWith("FishingReward") && Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
		{
			FishingChampionShipManager.getInstance().getReward(player);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}