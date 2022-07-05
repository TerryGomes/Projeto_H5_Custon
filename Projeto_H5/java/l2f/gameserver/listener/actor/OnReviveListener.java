package l2f.gameserver.listener.actor;

import l2f.gameserver.listener.CharListener;
import l2f.gameserver.model.Creature;

/**
 * @author VISTALL
 */
public interface OnReviveListener extends CharListener
{
	public void onRevive(Creature actor);
}
