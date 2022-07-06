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
		_objectId = objectId;
		_type = type;
		_sysString = st;
		_systemMsg = sm;
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
		_objectId = objectId;
		_type = type;
		_charName = charName;
		_text = params[0];
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x4A);
		writeD(_objectId);
		writeD(_type.ordinal());
		switch (_type)
		{
		case SYSTEM_MESSAGE:
			writeD(_sysString.getId());
			writeD(_systemMsg.getId());
			break;
		default:
			writeS(_charName);
			writeElements();
			break;
		}
		writeS(_text);

		Player player = getClient().getActiveChar();
		if (player != null)
		{
			player.broadcastSnoop(_type.ordinal(), _charName, _text);
		}
	}
}