package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.items.ItemInstance;

/**
 * @author VISTALL
 */
public class ExBaseAttributeCancelResult extends L2GameServerPacket
{
	private boolean _result;
	private int _objectId;
	private Element _element;

	public ExBaseAttributeCancelResult(boolean result, ItemInstance item, Element element)
	{
		this._result = result;
		this._objectId = item.getObjectId();
		this._element = element;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x75);
		this.writeD(this._result);
		this.writeD(this._objectId);
		this.writeD(this._element.getId());
	}
}