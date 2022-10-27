package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.templates.PlayerTemplate;

public class NewCharacterSuccess extends L2GameServerPacket
{
	// dddddddddddddddddddd
	private List<PlayerTemplate> _chars = new ArrayList<PlayerTemplate>();

	public void addChar(PlayerTemplate template)
	{
		this._chars.add(template);
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x0d);
		this.writeD(this._chars.size());

		for (PlayerTemplate temp : this._chars)
		{
			this.writeD(temp.race.ordinal());
			this.writeD(temp.classId.getId());
			this.writeD(0x46);
			this.writeD(temp.baseSTR);
			this.writeD(0x0a);
			this.writeD(0x46);
			this.writeD(temp.baseDEX);
			this.writeD(0x0a);
			this.writeD(0x46);
			this.writeD(temp.baseCON);
			this.writeD(0x0a);
			this.writeD(0x46);
			this.writeD(temp.baseINT);
			this.writeD(0x0a);
			this.writeD(0x46);
			this.writeD(temp.baseWIT);
			this.writeD(0x0a);
			this.writeD(0x46);
			this.writeD(temp.baseMEN);
			this.writeD(0x0a);
		}
	}
}