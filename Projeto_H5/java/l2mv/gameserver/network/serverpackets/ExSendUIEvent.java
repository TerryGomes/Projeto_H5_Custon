package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.components.NpcString;

public class ExSendUIEvent extends NpcStringContainer
{
	private int _objectId;
	private boolean _isHide;
	private boolean _isIncrease;
	private int _startTime;
	private int _endTime;

	public ExSendUIEvent(Player player, boolean isHide, boolean isIncrease, int startTime, int endTime, String... params)
	{
		this(player, isHide, isIncrease, startTime, endTime, NpcString.NONE, params);
	}

	public ExSendUIEvent(Player player, boolean isHide, boolean isIncrease, int startTime, int endTime, NpcString npcString, String... params)
	{
		super(npcString, params);
		this._objectId = player.getObjectId();
		this._isHide = isHide;
		this._isIncrease = isIncrease;
		this._startTime = startTime;
		this._endTime = endTime;
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0xFE);
		this.writeH(0x8E);
		this.writeD(this._objectId);
		this.writeD(this._isHide ? 0x01 : 0x00); // 0: show timer, 1: hide timer
		this.writeD(0x00); // unknown
		this.writeD(0x00); // unknown
		this.writeS(this._isIncrease ? "1" : "0"); // "0": count negative, "1": count positive
		this.writeS(String.valueOf(this._startTime / 60)); // timer starting minute(s)
		this.writeS(String.valueOf(this._startTime % 60)); // timer starting second(s)
		this.writeS(String.valueOf(this._endTime / 60)); // timer length minute(s) (timer will disappear 10 seconds before it ends)
		this.writeS(String.valueOf(this._endTime % 60)); // timer length second(s) (timer will disappear 10 seconds before it ends)
		this.writeElements();
	}
}