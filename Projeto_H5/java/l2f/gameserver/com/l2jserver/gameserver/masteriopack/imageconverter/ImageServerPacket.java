/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2f.gameserver.com.l2jserver.gameserver.masteriopack.imageconverter;

import l2f.gameserver.network.serverpackets.L2GameServerPacket;

/**
 * Packet used as PledgeCrest packet.
 * @author Masterio
 */
public class ImageServerPacket extends L2GameServerPacket
{
	private final int _crestId;
	private final byte[] _data;

	public ImageServerPacket(int crestId, byte[] data)
	{
		_crestId = crestId;
		_data = data;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x6a); // IL client: 0x6c, H5 client: 0x6a
		writeD(_crestId);

		if (_data != null)
		{
			writeD(_data.length);
			writeB(_data);
		}
		else
		{
			writeD(0);
		}
	}
}
