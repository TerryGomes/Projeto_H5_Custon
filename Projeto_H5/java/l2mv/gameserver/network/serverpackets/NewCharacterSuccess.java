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
		_chars.add(template);
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x0d);
		writeD(_chars.size());

		for (PlayerTemplate temp : _chars)
		{
			writeD(temp.race.ordinal());
			writeD(temp.classId.getId());
			writeD(0x46);
			writeD(temp.baseSTR);
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.baseDEX);
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.baseCON);
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.baseINT);
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.baseWIT);
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.baseMEN);
			writeD(0x0a);
		}
	}
}