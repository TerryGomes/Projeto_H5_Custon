package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.network.serverpackets.components.NpcString;

public class ExShowScreenMessage extends NpcStringContainer
{
	public static enum ScreenMessageAlign
	{
		TOP_LEFT, TOP_CENTER, TOP_RIGHT, MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT, BOTTOM_CENTER, BOTTOM_RIGHT,
	}

	public static final int SYSMSG_TYPE = 0;
	public static final int STRING_TYPE = 1;

	private final int _type, _sysMessageId;
	private final boolean _big_font, _effect;
	private final ScreenMessageAlign _text_align;
	private final int _time;

	// @Deprecated
	public ExShowScreenMessage(String text, int time, ScreenMessageAlign text_align, boolean big_font)
	{
		this(text, time, text_align, big_font, 1, -1, false);
	}

	public ExShowScreenMessage(String text, int time, ScreenMessageAlign text_align, boolean big_font, int type, int messageId, boolean showEffect)
	{
		super(NpcString.NONE, text);
		this._type = type;
		this._sysMessageId = messageId;
		this._time = time;
		this._text_align = text_align;
		this._big_font = big_font;
		this._effect = showEffect;
	}

	public ExShowScreenMessage(NpcString t, int time, ScreenMessageAlign text_align, String... params)
	{
		this(t, time, text_align, true, STRING_TYPE, -1, false, params);
	}

	public ExShowScreenMessage(NpcString npcString, int time, ScreenMessageAlign text_align, boolean big_font, String... params)
	{
		this(npcString, time, text_align, big_font, STRING_TYPE, -1, false, params);
	}

	public ExShowScreenMessage(NpcString npcString, int time, ScreenMessageAlign text_align, boolean big_font, boolean showEffect, String... params)
	{
		this(npcString, time, text_align, big_font, STRING_TYPE, -1, showEffect, params);
	}

	public ExShowScreenMessage(NpcString npcString, int time, ScreenMessageAlign text_align, boolean big_font, int type, int systemMsg, boolean showEffect, String... params)
	{
		super(npcString, params);
		this._type = type;
		this._sysMessageId = systemMsg;
		this._time = time;
		this._text_align = text_align;
		this._big_font = big_font;
		this._effect = showEffect;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x39);
		this.writeD(this._type); // 0 - system messages, 1 - your defined text
		this.writeD(this._sysMessageId); // system message id (_type must be 0 otherwise no effect)
		this.writeD(this._text_align.ordinal() + 1); // размещение текста
		this.writeD(0x00); // ?
		this.writeD(this._big_font ? 0 : 1); // размер текста
		this.writeD(0x00); // ?
		this.writeD(0x00); // ?
		this.writeD(this._effect ? 1 : 0); // upper effect (0 - disabled, 1 enabled) - _position must be 2 (center) otherwise no effect
		this.writeD(this._time); // время отображения сообщения в милисекундах
		this.writeD(0x01); // ?
		this.writeElements();
	}
}