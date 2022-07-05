package ai;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.instancemanager.games.HandysBlockCheckerManager;
import l2f.gameserver.instancemanager.games.HandysBlockCheckerManager.ArenaParticipantsHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.entity.BlockCheckerEngine;
import l2f.gameserver.model.instances.BlockInstance;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.ExCubeGameChangePoints;
import l2f.gameserver.network.serverpackets.ExCubeGameExtendedChangePoints;
import l2f.gameserver.utils.ItemFunctions;
import l2f.gameserver.utils.Location;

/**
 * @author n0nam3
 */
public class HandysBlock extends DefaultAI
{
	public HandysBlock(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster)
	{
		BlockInstance actor = (BlockInstance) getActor();
		if ((caster == null) || !caster.isPlayer())
		{
			return;
		}
		Player player = caster.getPlayer();
		int arena = player.getBlockCheckerArena();
		if (arena == -1 || arena > 3)
		{
			return;
		}

		if (player.getTarget() == actor)
		{
			if (skill.getId() == 5852 || skill.getId() == 5853)
			{
				ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(arena);

				if (holder.getPlayerTeam(player) == 0 && !actor.isRed())
				{
					actor.changeColor();
					increaseTeamPointsAndSend(player, holder.getEvent());
				}
				else if (holder.getPlayerTeam(player) == 1 && actor.isRed())
				{
					actor.changeColor();
					increaseTeamPointsAndSend(player, holder.getEvent());
				}
				else
				{
					return;
				}

				// 30% chance to drop the event items
				int random = Rnd.get(100);
				// Bond
				if (random > 69 && random <= 84)
				{
					dropItem(actor, 13787, holder.getEvent(), player);
				}
				else if (random > 84)
				{
					dropItem(actor, 13788, holder.getEvent(), player);
				}
			}
		}
	}

	private void increaseTeamPointsAndSend(Player player, BlockCheckerEngine eng)
	{
		int team = eng.getHolder().getPlayerTeam(player);
		eng.increasePlayerPoints(player, team);

		int timeLeft = (int) ((eng.getStarterTime() - System.currentTimeMillis()) / 1000);
		boolean isRed = eng.getHolder().getRedPlayers().contains(player);

		ExCubeGameChangePoints changePoints = new ExCubeGameChangePoints(timeLeft, eng.getBluePoints(), eng.getRedPoints());
		ExCubeGameExtendedChangePoints secretPoints = new ExCubeGameExtendedChangePoints(timeLeft, eng.getBluePoints(), eng.getRedPoints(), isRed, player, eng.getPlayerPoints(player, isRed));

		eng.getHolder().broadCastPacketToTeam(changePoints);
		eng.getHolder().broadCastPacketToTeam(secretPoints);
	}

	private void dropItem(NpcInstance block, int id, BlockCheckerEngine eng, Player player)
	{
		ItemInstance drop = ItemFunctions.createItem(id);
		drop.dropToTheGround(block, Location.findPointToStay(block, 50));
		eng.addNewDrop(drop);
	}
}