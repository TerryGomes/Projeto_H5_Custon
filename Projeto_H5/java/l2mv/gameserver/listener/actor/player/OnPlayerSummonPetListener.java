package l2mv.gameserver.listener.actor.player;

import l2mv.gameserver.listener.PlayerListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Summon;

public interface OnPlayerSummonPetListener extends PlayerListener
{
	void onSummonPet(Player p0, Summon p1);
}
