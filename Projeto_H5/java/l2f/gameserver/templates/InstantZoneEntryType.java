package l2f.gameserver.templates;

import l2f.gameserver.data.xml.holder.InstantZoneHolder;
import l2f.gameserver.instancemanager.QuestManager;
import l2f.gameserver.model.CommandChannel;
import l2f.gameserver.model.Party;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.utils.ItemFunctions;

public enum InstantZoneEntryType
{
	SOLO
	{
		@Override
		public boolean canEnter(Player player, InstantZone instancedZone)
		{
			if (player.isInParty())
			{
				player.sendPacket(SystemMsg.A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA);
				return false;
			}

			SystemMsg msg = checkPlayer(player, instancedZone);
			if (msg != null)
			{
				if (msg.size() > 0)
				{
					player.sendPacket(new SystemMessage2(msg).addName(player));
				}
				else
				{
					player.sendPacket(msg);
				}
				return false;
			}
			return true;
		}

		@Override
		public boolean canReEnter(Player player, InstantZone instancedZone)
		{
			if (player.isCursedWeaponEquipped() || player.isInFlyingTransform())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
				return false;
			}
			return true;
		}
	},
	PARTY
	{
		@Override
		public boolean canEnter(Player player, InstantZone instancedZone)
		{
			Party party = player.getParty();
			if (party == null)
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
				return false;
			}
			if (!party.isLeader(player))
			{
				player.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
				return false;
			}
			if (party.size() < instancedZone.getMinParty() && (party.getLeader() == null || !party.getLeader().isGM()))
			{
				player.sendPacket(new SystemMessage2(SystemMsg.YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANT_ZONE).addInteger(instancedZone.getMinParty()));
				return false;
			}
			if (party.size() > instancedZone.getMaxParty())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
				return false;
			}

			for (Player member : party.getMembers())
			{
				if (!player.isInRange(member, 500))
				{
					party.sendPacket(new SystemMessage2(SystemMsg.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addName(member));
					return false;
				}

				SystemMsg msg = checkPlayer(member, instancedZone);
				if (msg != null)
				{
					if (msg.size() > 0)
					{
						party.sendPacket(new SystemMessage2(msg).addName(member));
					}
					else
					{
						member.sendPacket(msg);
					}
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean canReEnter(Player player, InstantZone instanceZone)
		{
			Party party = player.getParty();
			if (party == null)
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
				return false;
			}
			if (party.size() > instanceZone.getMaxParty())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
				return false;
			}
			if (player.isCursedWeaponEquipped() || player.isInFlyingTransform())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
				return false;
			}
			return true;
		}
	},
	COMMAND_CHANNEL
	{
		@Override
		public boolean canEnter(Player player, InstantZone instancedZone)
		{
			Party party = player.getParty();
			// Check in case, <= 9 players is needed(impossible to make Command Channel)
			if (party != null && ((party.getLeader() != null && party.getLeader().isGM()) || (instancedZone.getMinParty() <= 9 && !party.isInCommandChannel())))
			{
				return PARTY.canEnter(player, instancedZone);
			}

			if (party == null || party.getCommandChannel() == null)
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL);
				return false;
			}
			CommandChannel cc = party.getCommandChannel();
			if (cc.size() < instancedZone.getMinParty())
			{
				player.sendPacket(new SystemMessage2(SystemMsg.YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANT_ZONE).addInteger(instancedZone.getMinParty()));
				return false;
			}
			if (cc.size() > instancedZone.getMaxParty())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
				return false;
			}
			for (Player member : cc)
			{
				if (!player.isInRange(member, 500))
				{
					cc.sendPacket(new SystemMessage2(SystemMsg.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addName(member));
					return false;
				}

				SystemMsg msg = checkPlayer(member, instancedZone);
				if (msg != null)
				{
					if (msg.size() > 0)
					{
						cc.sendPacket(new SystemMessage2(msg).addName(member));
					}
					else
					{
						member.sendPacket(msg);
					}
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean canReEnter(Player player, InstantZone instanceZone)
		{
			Party commparty = player.getParty();

			// Check in case, <= 9 players is needed(impossible to make Command Channel)
			if (instanceZone.getMinParty() <= 9 && player.getParty() != null && !player.getParty().isInCommandChannel())
			{
				return PARTY.canReEnter(player, instanceZone);
			}

			if (commparty == null || commparty.getCommandChannel() == null)
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL);
				return false;
			}
			CommandChannel cc = commparty.getCommandChannel();
			if (cc.size() > instanceZone.getMaxParty())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
				return false;
			}
			if (player.isCursedWeaponEquipped() || player.isInFlyingTransform())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
				return false;
			}
			return true;
		}
	};

	public abstract boolean canEnter(Player player, InstantZone instancedZone);

	public abstract boolean canReEnter(Player player, InstantZone instancedZone);

	private static SystemMsg checkPlayer(Player player, InstantZone instancedZone)
	{
		if (player.getActiveReflection() != null)
		{
			return SystemMsg.YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON;
		}

		if (player.getLevel() < instancedZone.getMinLevel() || player.getLevel() > instancedZone.getMaxLevel())
		{
			return SystemMsg.C1S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY;
		}

		if (player.isCursedWeaponEquipped() || player.isInFlyingTransform())
		{
			return SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS;
		}

		if (InstantZoneHolder.getInstance().getMinutesToNextEntrance(instancedZone.getId(), player) > 0)
		{
			return SystemMsg.C1_MAY_NOT_REENTER_YET;
		}

		if (instancedZone.getRemovedItemId() > 0 && instancedZone.getRemovedItemNecessity() && ItemFunctions.getItemCount(player, instancedZone.getRemovedItemId()) < 1)
		{
			return SystemMsg.C1S_ITEM_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED;
		}

		if (instancedZone.getRequiredQuestId() > 0)
		{
			Quest q = QuestManager.getQuest(instancedZone.getRequiredQuestId());
			QuestState qs = player.getQuestState(q.getClass());
			if (qs == null || qs.getState() != Quest.STARTED)
			{
				return SystemMsg.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED;
			}
		}

		// Synerge - Player cannot enter an instance if he is still teleporting
		if (player.isTeleporting())
		{
			return SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS;
		}

		return null;
	}
}