package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;

/**
 * Этот пакет отвечает за анимацию высасывания душ из трупов
 * @author SYS
 */
public class SpawnEmitter extends L2GameServerPacket
{
	private int _monsterObjId;
	private int _playerObjId;

	public SpawnEmitter(NpcInstance monster, Player player)
	{
		this._playerObjId = player.getObjectId();
		this._monsterObjId = monster.getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		// ddd
		this.writeEx(0x5d);

		this.writeD(this._monsterObjId);
		this.writeD(this._playerObjId);
		this.writeD(0x00); // unk
	}
}