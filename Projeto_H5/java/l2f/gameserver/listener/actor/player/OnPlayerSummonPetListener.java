package l2f.gameserver.listener.actor.player;

import l2f.gameserver.listener.PlayerListener;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Summon;

public interface OnPlayerSummonPetListener extends PlayerListener
{
	void onSummonPet(Player p0, Summon p1);
}
