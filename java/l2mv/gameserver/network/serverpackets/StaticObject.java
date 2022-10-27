package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.DoorInstance;
import l2mv.gameserver.model.instances.StaticObjectInstance;

public class StaticObject extends L2GameServerPacket
{
	private final int _staticObjectId;
	private final int _objectId;
	private final int _type;
	private final int _isTargetable;
	private final int _meshIndex;
	private final int _isClosed;
	private final int _isEnemy;
	private final int _maxHp;
	private final int _currentHp;
	private final int _showHp;
	private final int _damageGrade;

	public StaticObject(StaticObjectInstance obj)
	{
		this._staticObjectId = obj.getUId();
		this._objectId = obj.getObjectId();
		this._type = 0;
		this._isTargetable = 1;
		this._meshIndex = obj.getMeshIndex();
		this._isClosed = 0;
		this._isEnemy = 0;
		this._maxHp = 0;
		this._currentHp = 0;
		this._showHp = 0;
		this._damageGrade = 0;
	}

	public StaticObject(DoorInstance door, Player player)
	{
		this._staticObjectId = door.getDoorId();
		this._objectId = door.getObjectId();
		this._type = 1;
		this._isTargetable = door.getTemplate().isTargetable() ? 1 : 0;
		this._meshIndex = 1;
		this._isClosed = door.isOpen() ? 0 : 1; // opened 0 /closed 1
		this._isEnemy = door.isAutoAttackable(player) ? 1 : 0;
		this._currentHp = (int) door.getCurrentHp();
		this._maxHp = door.getMaxHp();
		this._showHp = door.isHPVisible() ? 1 : 0; // TODO [G1ta0] статус двери для осаждающих
		this._damageGrade = door.getDamage();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x9f);
		this.writeD(this._staticObjectId);
		this.writeD(this._objectId);
		this.writeD(this._type);
		this.writeD(this._isTargetable);
		this.writeD(this._meshIndex);
		this.writeD(this._isClosed);
		this.writeD(this._isEnemy);
		this.writeD(this._currentHp);
		this.writeD(this._maxHp);
		this.writeD(this._showHp);
		this.writeD(this._damageGrade);
	}
}