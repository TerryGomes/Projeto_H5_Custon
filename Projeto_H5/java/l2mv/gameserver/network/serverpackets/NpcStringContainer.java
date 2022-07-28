package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.network.serverpackets.components.NpcString;

/**
 * @author VISTALL
 * @date 16:43/25.03.2011
 */
public abstract class NpcStringContainer extends L2GameServerPacket
{
	private final NpcString _npcString;
	private final String[] _parameters = new String[5];

	protected NpcStringContainer(NpcString npcString, String... arg)
	{
		this._npcString = npcString;
		System.arraycopy(arg, 0, this._parameters, 0, arg.length);
	}

	protected void writeElements()
	{
		this.writeD(this._npcString.getId());
		for (String st : this._parameters)
		{
			this.writeS(st);
		}
	}
}
