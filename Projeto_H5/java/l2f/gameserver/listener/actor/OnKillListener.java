package l2f.gameserver.listener.actor;

import l2f.gameserver.listener.CharListener;
import l2f.gameserver.model.Creature;

public interface OnKillListener extends CharListener
{
	public void onKill(Creature actor, Creature victim);

	/**
	 * FIXME [VISTALL]
	 * When the player is added to OnKillListener, it is not added to the sum, and you need to manually add
	 * But with resumon, it is difficult to trace
	 * If you return a true, then the killer will take a player, and on it call onKill
	 * @return
	 */
	public boolean ignorePetOrSummon();
}
