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
package l2mv.gameserver.masteriopack.rankpvpsystem;

import java.util.logging.Logger;

import l2mv.gameserver.model.Player;

/**
 * @author Masterio
 */
public class RPSBypass
{
	public static final Logger log = Logger.getLogger(RPSBypass.class.getName());

	public static void executeCommand(Player activeChar, String command)
	{
		/*
		 * Commands:
		 * RPS.PS - PvP Status window.
		 * RPS.DS - Death Status window.
		 * RPS.RPC:<pageNo> - RPS Exchange window (default page number is: 1).
		 * RPS.RPCReward:<rewardId>,<pageNo> - Give RPC Reward for current player, and show RPC Exchange on pageNo. [The reward list is constant]
		 * RPS.RPCRewardConfirm:<rewardId>,<pageNo> - Confirm page for get reward.
		 * RPS.RewardList:<rankId>,<pageNo> - Rank Reward List for kill player with rank id, and show at pageNo.
		 */

		if (!RPSConfig.RANK_PVP_SYSTEM_ENABLED)
		{
			return;
		}

		String param = command.split("\\.", 2)[1];

		RPSCookie pc = activeChar.getRPSCookie();

		// reset death status:
		if (!activeChar.isDead())
		{
			pc.setDeathStatus(null);
		}

		if (param.equals("PS")) // PvP Status
		{
			RPSHtmlPvpStatus.sendPage(activeChar, pc.getTarget());
		}
		else if (param.equals("DS")) // Death Status
		{
			if (pc.isDeathStatusActive() && pc.getDeathStatus().isValid())
			{
				pc.getDeathStatus().sendPage(activeChar);
			}
			else if (pc.getTarget() != null)
			{
				RPSHtmlPvpStatus.sendPage(activeChar, pc.getTarget());
				activeChar.sendMessage("You can see Death Status only when you are dead!");
			}
			else // if !pc.isDeathStatusActive()
			{
				activeChar.sendMessage("You can see Death Status only when you are dead!");
			}
		}
		else if (param.startsWith("RPC:"))
		{
			String param2 = command.split(":", 2)[1].trim();

			int pageNo = 1;

			try
			{
				pageNo = Integer.valueOf(param2);
			}
			catch (Exception e)
			{
				log.info(e.getMessage());
			}

			if (pageNo < 1)
			{
				pageNo = 1;
			}

			RPSHtmlRPCRewardList.sendPage(activeChar, pageNo);
		}
		else if (param.startsWith("RPCReward:"))
		{
			try
			{
				int rpcRewardId = Integer.valueOf(command.split(":", 2)[1].trim().split(",", 2)[0].trim());
				int pageNo = Integer.valueOf(command.split(":", 2)[1].trim().split(",", 2)[1].trim());

				RPCReward rpcReward = RPCRewardTable.getInstance().getRpcRewardList().get(rpcRewardId);

				RPCRewardTable.getInstance().giveReward(activeChar, rpcReward);

				RPSHtmlRPCRewardList.sendPage(activeChar, pageNo);
			}
			catch (Exception e)
			{
				log.info(e.getMessage());
			}
		}
		else if (param.startsWith("RPCRewardConfirm:"))
		{
			try
			{
				int rpcRewardId = Integer.valueOf(command.split(":", 2)[1].trim().split(",", 2)[0].trim());
				int pageNo = Integer.valueOf(command.split(":", 2)[1].trim().split(",", 2)[1].trim());

				RPSHtmlRPCRewardList.getConfirmPage(activeChar, pageNo, rpcRewardId);
			}
			catch (Exception e)
			{
				log.info(e.getMessage());
			}
		}
		else if (param.startsWith("RewardList:"))
		{
			try
			{
				int rankId = Integer.valueOf(command.split(":", 2)[1].trim().split(",", 2)[0].trim());
				int pageNo = Integer.valueOf(command.split(":", 2)[1].trim().split(",", 2)[1].trim());

				RPCHtmlRewardList.sendPage(activeChar, pageNo, rankId);
			}
			catch (Exception e)
			{
				log.info(e.getMessage());
			}
		}
	}
}
