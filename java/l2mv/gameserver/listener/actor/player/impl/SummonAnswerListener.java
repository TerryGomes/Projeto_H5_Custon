package l2mv.gameserver.listener.actor.player.impl;

import l2mv.commons.lang.reference.HardReference;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 11:28/15.04.2011
 */
public class SummonAnswerListener implements OnAnswerListener
{
	private HardReference<Player> _playerRef;
	private Location _location;
	private long _count;

	public SummonAnswerListener(Player player, Location loc, long count)
	{
		_playerRef = player.getRef();
		_location = loc;
		_count = count;
	}

	@Override
	public void sayYes()
	{
		Player player = _playerRef.get();
		if (player == null)
		{
			return;
		}

		player.abortAttack(true, true);
		player.abortCast(true, true);
		player.stopMove();
		if (_count > 0)
		{
			if (player.getInventory().destroyItemByItemId(8615, _count, "SummonPlayer"))
			{
				player.sendPacket(SystemMessage2.removeItems(8615, _count));
				player.teleToLocation(_location);
			}
			else
			{
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			}
		}
		else
		{
			player.teleToLocation(_location);
		}
	}

	@Override
	public void sayNo()
	{
		//
	}
}
