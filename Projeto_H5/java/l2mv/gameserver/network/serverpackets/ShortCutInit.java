package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.instances.player.ShortCut;

public class ShortCutInit extends ShortCutPacket
{
	private List<ShortcutInfo> _shortCuts = Collections.emptyList();

	public ShortCutInit(Player pl)
	{
		Collection<ShortCut> shortCuts = pl.getAllShortCuts();
		_shortCuts = new ArrayList<ShortcutInfo>(shortCuts.size());
		for (ShortCut shortCut : shortCuts)
		{
			_shortCuts.add(convert(pl, shortCut));
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x45);
		writeD(_shortCuts.size());

		for (ShortcutInfo sc : _shortCuts)
		{
			sc.write(this);
		}
	}
}