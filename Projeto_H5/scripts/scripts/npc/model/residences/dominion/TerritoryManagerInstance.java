package npc.model.residences.dominion;

import l2mv.gameserver.data.xml.holder.MultiSellHolder;
import l2mv.gameserver.instancemanager.QuestManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.model.entity.residence.Dominion;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.SkillList;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.HtmlUtils;
import l2mv.gameserver.utils.ItemFunctions;
import quests._234_FatesWhisper;
import quests._235_MimirsElixir;
import quests._236_SeedsOfChaos;

public class TerritoryManagerInstance extends NpcInstance
{
	public TerritoryManagerInstance(int objectId, NpcTemplate template)
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

		Dominion dominion = getDominion();
		DominionSiegeEvent siegeEvent = dominion.getSiegeEvent();
		int npcId = getNpcId();
		int badgeId = 13676 + dominion.getId();

		if (command.equalsIgnoreCase("buyspecial"))
		{
			if (Functions.getItemCount(player, badgeId) < 1)
			{
				showChatWindow(player, 1);
			}
			else
			{
				MultiSellHolder.getInstance().SeparateAndSend(npcId, player, 0);
			}
		}
		else if (command.equalsIgnoreCase("buyNobless"))
		{
			if (player.isNoble())
			{
				showChatWindow(player, 9);
				return;
			}
			if (player.consumeItem(badgeId, 100L))
			{
				Quest q = QuestManager.getQuest(_234_FatesWhisper.class);
				QuestState qs = player.getQuestState(q.getClass());
				if (qs != null)
				{
					qs.exitCurrentQuest(true);
				}
				q.newQuestState(player, Quest.COMPLETED);

				if (player.getRace() == Race.kamael)
				{
					q = QuestManager.getQuest(_236_SeedsOfChaos.class);
					qs = player.getQuestState(q.getClass());
					if (qs != null)
					{
						qs.exitCurrentQuest(true);
					}
					q.newQuestState(player, Quest.COMPLETED);
				}
				else
				{
					q = QuestManager.getQuest(_235_MimirsElixir.class);
					qs = player.getQuestState(q.getClass());
					if (qs != null)
					{
						qs.exitCurrentQuest(true);
					}
					q.newQuestState(player, Quest.COMPLETED);
				}

				Olympiad.addNoble(player);
				player.setNoble(true);
				player.updatePledgeClass();
				player.updateNobleSkills();
				player.sendPacket(new SkillList(player));
				player.broadcastUserInfo(true);
				showChatWindow(player, 10);
			}
			else
			{
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			}
		}
		else if (command.equalsIgnoreCase("calculate"))
		{
			if (!player.isQuestContinuationPossible(true))
			{
				return;
			}
			int[] rewards = siegeEvent.calculateReward(player);
			if (rewards == null || rewards[0] == 0)
			{
				showChatWindow(player, 4);
				return;
			}

			NpcHtmlMessage html = new NpcHtmlMessage(player, this, getHtmlPath(npcId, 5, player), 5);
			html.replace("%territory%", HtmlUtils.htmlResidenceName(dominion.getId()));
			html.replace("%badges%", String.valueOf(rewards[0]));
			html.replace("%adena%", String.valueOf(rewards[1]));
			html.replace("%fame%", String.valueOf(rewards[2]));
			player.sendPacket(html);
		}
		else if (command.equalsIgnoreCase("recivelater"))
		{
			showChatWindow(player, getHtmlPath(npcId, 6, player));
		}
		else if (command.equalsIgnoreCase("recive"))
		{
			int[] rewards = siegeEvent.calculateReward(player);
			if (rewards == null || rewards[0] == 0)
			{
				showChatWindow(player, 4);
				return;
			}

			ItemFunctions.addItem(player, badgeId, rewards[0], true, "TerritoryManager");
			ItemFunctions.addItem(player, ItemTemplate.ITEM_ID_ADENA, rewards[1], true, "TerritoryManager");
			if (rewards[2] > 0)
			{
				player.setFame(player.getFame() + rewards[2], "CalcBadges:" + dominion.getId());
			}

			siegeEvent.clearReward(player.getObjectId());
			showChatWindow(player, 7);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		if (player.getLevel() < 40 || player.getClassId().getLevel() <= 2)
		{
			val = 8;
		}
		return val == 0 ? "residence2/dominion/TerritoryManager.htm" : "residence2/dominion/TerritoryManager-" + val + ".htm";
	}
}