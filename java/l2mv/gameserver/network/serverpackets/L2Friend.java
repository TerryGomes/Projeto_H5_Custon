package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class L2Friend extends L2GameServerPacket
{
	private boolean _add, _online;
	private String _name;
	private int _object_id;

	public L2Friend(Player player, boolean add)
	{
		this._add = add;
		this._name = player.getName();
		this._object_id = player.getObjectId();
		this._online = true;
	}

	public L2Friend(String name, boolean add, boolean online, int object_id)
	{
		this._name = name;
		this._add = add;
		this._object_id = object_id;
		this._online = online;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x76);
		this.writeD(this._add ? 1 : 3); // 1 - добавить друга в спикок, 3 удалить друга со списка
		this.writeD(0); // и снова тут идет ID персонажа в списке оффа, не object id
		this.writeS(this._name);
		this.writeD(this._online ? 1 : 0); // онлайн или оффлайн
		this.writeD(this._object_id); // object_id if online
	}
}