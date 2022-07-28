package l2mv.gameserver.network.serverpackets;

import org.napile.primitive.maps.IntObjectMap;

import l2mv.gameserver.model.Player;

/**
 * @author VISTALL
 * @date 22:01/22.03.2011
 */
public class ExReceiveShowPostFriend extends L2GameServerPacket
{
	private IntObjectMap<String> _list;

	public ExReceiveShowPostFriend(Player player)
	{
		this._list = player.getPostFriends();
	}

	@Override
	public void writeImpl()
	{
		this.writeEx(0xD3);
		this.writeD(this._list.size());
		for (String t : this._list.values())
		{
			this.writeS(t);
		}
	}
}
