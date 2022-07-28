package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.network.serverpackets.components.SysString;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class Say2 extends NpcStringContainer
{
	private ChatType _type;
	private SysString _sysString;
	private SystemMsg _systemMsg;

	private int _objectId;
	private String _charName, _text;

	public Say2(int objectId, ChatType type, SysString st, SystemMsg sm)
	{
		super(NpcString.NONE);
		this._objectId = objectId;
		this._type = type;
		this._sysString = st;
		this._systemMsg = sm;
	}

	public Say2(Creature creature, ChatType type, String text)
	{
		this(creature.getObjectId(), type, creature.getName(), NpcString.NONE, text);
	}

	public Say2(int objectId, ChatType type, String charName, String text)
	{
		this(objectId, type, charName, NpcString.NONE, text);
	}

	public Say2(int objectId, ChatType type, String charName, NpcString npcString, String... params)
	{
		super(npcString, params);
		this._objectId = objectId;
		this._type = type;
		this._charName = charName;
		this._text = params[0];
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x4A);
		this.writeD(this._objectId);
		this.writeD(this._type.ordinal());
		switch (this._type)
		{
		case SYSTEM_MESSAGE:
			this.writeD(this._sysString.getId());
			this.writeD(this._systemMsg.getId());
			break;
		default:
			this.writeS(this._charName);
			this.writeElements();
			break;
		}
		this.writeS(this._text);

		Player player = this.getClient().getActiveChar();
		if (player != null)
		{
			player.broadcastSnoop(this._type.ordinal(), this._charName, this._text);
		}
	}
}