package l2mv.gameserver.network.clientpackets;

public class RequestTeleport extends L2GameClientPacket
{
	private int unk, _type, unk2, unk3, unk4;

	@Override
	protected void readImpl()
	{
		this.unk = this.readD();
		this._type = this.readD();
		if (this._type == 2)
		{
			this.unk2 = this.readD();
			this.unk3 = this.readD();
		}
		else if (this._type == 3)
		{
			this.unk2 = this.readD();
			this.unk3 = this.readD();
			this.unk4 = this.readD();
		}
	}

	@Override
	protected void runImpl()
	{
		// TODO not implemented
	}
}