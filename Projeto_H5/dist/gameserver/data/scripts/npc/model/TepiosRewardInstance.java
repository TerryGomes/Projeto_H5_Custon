package npc.model;

import instances.SufferingHallAttack;
import instances.SufferingHallDefence;
import l2f.commons.util.Rnd;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.ItemFunctions;
import quests._694_BreakThroughTheHallOfSuffering;
import quests._695_DefendtheHallofSuffering;

/**
 * @author pchayka
 */
public class TepiosRewardInstance extends NpcInstance
{
	private static final int MARK_OF_KEUCEREUS_STAGE_1 = 13691;
	private static final int MARK_OF_KEUCEREUS_STAGE_2 = 13692;
	private static final int SOE = 736; // Scroll of Escape
	private static final int SUPPLIES1 = 13777; // Jewel Ornamented Duel Supplies
	private static final int SUPPLIES2 = 13778; // Mother-of-Pearl Ornamented Duel Supplies
	private static final int SUPPLIES3 = 13779; // Gold-Ornamented Duel Supplies
	private static final int SUPPLIES4 = 13780; // Silver-Ornamented Duel Supplies
	private static final int SUPPLIES5 = 13781; // Bronze-Ornamented Duel Supplies
	private static final int[] SUPPLIES6_10 =
	{
		13782, // Non-Ornamented Duel Supplies
		13783, // Weak-Looking Duel Supplies
		13784, // Sad-Looking Duel Supplies
		13785, // Poor-Looking Duel Supplies
		13786 // Worthless Duel Supplies
	};
	private boolean _gotReward = false;

	public TepiosRewardInstance(int objectId, NpcTemplate template)
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

		if (command.equalsIgnoreCase("getreward"))
		{
			if (_gotReward)
			{
				return;
			}

			if (player.isInParty() && player.getParty().getLeader() != player)
			{
				showChatWindow(player, 1);
				return;
			}

			int time = 0;
			if (getReflection().getInstancedZoneId() == 115)
			{
				time = ((SufferingHallAttack) getReflection()).timeSpent;
			}
			else if (getReflection().getInstancedZoneId() == 116)
			{
				time = ((SufferingHallDefence) getReflection()).timeSpent;
			}

			for (Player p : getReflection().getPlayers())
			{
				if (ItemFunctions.getItemCount(p, MARK_OF_KEUCEREUS_STAGE_1) < 1 && ItemFunctions.getItemCount(p, MARK_OF_KEUCEREUS_STAGE_2) < 1)
				{
					ItemFunctions.addItem(p, MARK_OF_KEUCEREUS_STAGE_1, 1, true, "TepiosRewardInstance");
				}
				ItemFunctions.addItem(p, SOE, 1, true, "TepiosRewardInstance");

				if (time > 0)
				{
					if (time <= 20 * 60 + 59)
					{
						ItemFunctions.addItem(p, SUPPLIES1, 1, true, "TepiosRewardInstance");
					}
					else if (time > 20 * 60 + 59 && time <= 22 * 60 + 59)
					{
						ItemFunctions.addItem(p, SUPPLIES2, 1, true, "TepiosRewardInstance");
					}
					else if (time > 22 * 60 + 59 && time <= 24 * 60 + 59)
					{
						ItemFunctions.addItem(p, SUPPLIES3, 1, true, "TepiosRewardInstance");
					}
					else if (time > 24 * 60 + 59 && time <= 26 * 60 + 59)
					{
						ItemFunctions.addItem(p, SUPPLIES4, 1, true, "TepiosRewardInstance");
					}
					else if (time > 26 * 60 + 59 && time <= 28 * 60 + 59)
					{
						ItemFunctions.addItem(p, SUPPLIES5, 1, true, "TepiosRewardInstance");
					}
					else if (time > 26 * 60 + 59)
					{
						ItemFunctions.addItem(p, SUPPLIES6_10[Rnd.get(SUPPLIES6_10.length)], 1, true, "TepiosRewardInstance");
					}
				}
				QuestState qs = p.getQuestState(_694_BreakThroughTheHallOfSuffering.class);
				QuestState qs2 = p.getQuestState(_695_DefendtheHallofSuffering.class);
				if (qs != null && getReflection().getInstancedZoneId() == 115)
				{
					qs.exitCurrentQuest(true);
				}
				if (qs2 != null && getReflection().getInstancedZoneId() == 116)
				{
					qs2.exitCurrentQuest(true);
				}
			}
			_gotReward = true;
			showChatWindow(player, 2);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String htmlpath;
		if (val == 0)
		{
			if (_gotReward)
			{
				htmlpath = "default/32530-3.htm";
			}
			else
			{
				htmlpath = "default/32530.htm";
			}
		}
		else
		{
			return super.getHtmlPath(npcId, val, player);
		}
		return htmlpath;
	}
}