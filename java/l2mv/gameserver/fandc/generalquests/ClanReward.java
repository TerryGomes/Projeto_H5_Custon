///*
// * Copyright (C) 2004-2013 L2J Server
// *
// * This file is part of L2J Server.
// *
// * L2J Server is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * L2J Server is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program. If not, see <http://www.gnu.org/licenses/>.
// */
//package l2mv.gameserver.fandc.generalquests;
//
//import java.util.Collection;
//
//import l2mv.gameserver.Config;
//import l2mv.gameserver.cache.Msg;
//import l2mv.gameserver.data.xml.holder.SkillAcquireHolder;
//import l2mv.gameserver.listener.actor.player.OnPlayerEnterListener;
//import l2mv.gameserver.model.Player;
//import l2mv.gameserver.model.Skill;
//import l2mv.gameserver.model.SkillLearn;
//import l2mv.gameserver.model.base.AcquireType;
//import l2mv.gameserver.model.instances.NpcInstance;
//import l2mv.gameserver.model.pledge.Clan;
//import l2mv.gameserver.model.pledge.UnitMember;
//import l2mv.gameserver.model.quest.Quest;
//import l2mv.gameserver.model.quest.QuestState;
//import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
//import l2mv.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
//import l2mv.gameserver.network.serverpackets.PledgeShowInfoUpdate;
//import l2mv.gameserver.network.serverpackets.PledgeStatusChanged;
//import l2mv.gameserver.tables.SkillTable;
//import l2mv.gameserver.utils.SiegeUtils;
//
///**
// * @author UnAfraid
// */
//public class ClanReward extends Quest implements OnPlayerEnterListener
//{
//	private ClanReward()
//	{
//		if (!Config.ENABLE_CLAN_REWARD)
//			return;
//
//		addEventId(HookType.ON_CLAN_CREATE);
//		addEventId(HookType.ON_CLAN_JOIN);
//		addEventId(HookType.ON_ENTER_WORLD);
//	}
//
//	@Override
//    public int getQuestIntId()
//	{
//		// Random quest id
//		return 35010;
//	}
//
//	@Override
//	public void onPlayerEnter(Player player)
//	{
//		QuestState qs = player.getQuestState(getClass());
//		qs.startQuestTimer("REWARD", 5000, null);
//	}
//
//	@Override
//	public void onClanCreated(Player player)
//	{
//		QuestState qs = player.getQuestState(getClass());
//		qs.startQuestTimer("REWARD", 1000, null);
//	}
//
//	@Override
//	public void onClanJoin(Player player)
//	{
//		QuestState qs = player.getQuestState(getClass());
//		qs.startQuestTimer("REWARD", 2000, null);
//	}
//
//	@Override
//	public String onEvent(String event, QuestState st, NpcInstance npc)
//	{
//		final Player player = st.getPlayer();
//
//		switch (event)
//		{
//			case "REWARD":
//			{
//				if ((player.getClan() == null) || isRewardedAlready(player))
//				{
//					return null;
//				}
//
//				if (isValidForReward(player))
//				{
//					giveClanReward(player);
//					String message = "Your clan have been rewarded with: ";
//					if (Config.CLAN_REWARD_LEVEL > 0)
//					{
//						message += Config.CLAN_REWARD_LEVEL + " level, ";
//					}
//					if (Config.CLAN_REWARD_REPUTATION > 0)
//					{
//						message += Config.CLAN_REWARD_REPUTATION + " reputation, ";
//					}
//					if (Config.CLAN_REWARD_SKILLS)
//					{
//						message += player.getClan().getSkills().size() + " skills ";
//					}
//					message += ".";
//					sendScreenMessage(player, message);
//				}
//				else if (!isRewardedAlready(player))
//				{
//					int playersNeeded = Config.CLAN_REWARD_MIN_ONLINE_FOR_REWARD - player.getClan().getOnlineMembers().size();
//					if (playersNeeded > 0)
//					{
//						sendScreenMessage(player, "Reward will be given when you reach " + player.getClan().getOnlineMembers().size() + "/" + Config.CLAN_REWARD_MIN_ONLINE_FOR_REWARD + " players online (" + playersNeeded + " more).");
//					}
//				}
//				break;
//			}
//		}
//		return super.onEvent(event, st, npc);
//	}
//
//	private boolean isRewardedAlready(Player player)
//	{
//		return player.getClan().getLevel() >= Config.CLAN_REWARD_MAX_LEVEL_FOR_REWARD;
//	}
//
//	private boolean isValidForReward(Player player)
//	{
//		final Clan clan = player.getClan();
//		if (clan.getLevel() < Config.CLAN_REWARD_MIN_LEVEL_FOR_REWARD)
//		{
//			return false;
//		}
//		else if (clan.getLevel() > Config.CLAN_REWARD_MAX_LEVEL_FOR_REWARD)
//		{
//			return false;
//		}
//		else if (player.getClan().getOnlineMembers().size() < Config.CLAN_REWARD_MIN_ONLINE_FOR_REWARD)
//		{
//			return false;
//		}
//		return true;
//	}
//
//	private void giveClanReward(Player player)
//	{
//		final Clan clan = player.getClan();
//		if (Config.CLAN_REWARD_LEVEL > 0)
//		{
//			clan.setLevel(Config.CLAN_REWARD_LEVEL);
//			clan.updateClanInDB();
//
//			player.broadcastCharInfo();
//
//			if (clan.getLevel() >= 4)
//				SiegeUtils.addSiegeSkills(player);
//
//			if (clan.getLevel() == 5)
//				player.sendPacket(Msg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);
//
//			// notify all the members about it
//			PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(clan);
//			PledgeStatusChanged ps = new PledgeStatusChanged(clan);
//			for (UnitMember mbr : clan)
//			{
//				if (mbr.isOnline())
//				{
//					mbr.getPlayer().updatePledgeClass();
//					mbr.getPlayer().sendPacket(Msg.CLANS_SKILL_LEVEL_HAS_INCREASED, pu, ps);
//					mbr.getPlayer().broadcastCharInfo();
//				}
//			}
//		}
//		if (Config.CLAN_REWARD_REPUTATION > 0)
//		{
//			clan.incReputation(Config.CLAN_REWARD_REPUTATION, true, "ClanReward");
//		}
//
//		if (Config.CLAN_REWARD_SKILLS)
//		{
//			// Give clan skills
//			Skill skill = null;
//			for (int i = 0; i < 10; i++) // Lazy hack to give clan skills at max level for the specific clan level.
//			{
//				Collection<SkillLearn> clanSkills = SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.CLAN, null, clan.getLevel());
//				for (SkillLearn sl : clanSkills)
//				{
//					skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
//					clan.addSkill(skill, true);
//				}
//			}
//			clan.broadcastSkillListToOnlineMembers();
//		}
//	}
//
//	private void sendScreenMessage(Player player, String message)
//	{
//		player.getClan().broadcastToOnlineMembers(new ExShowScreenMessage(message, 10000, ScreenMessageAlign.BOTTOM_RIGHT, false));
//	}
//}
