package l2mv.gameserver.network.clientpackets;

@SuppressWarnings("unused")
public class RequestSendMsnChatLog extends L2GameClientPacket
{
	private int unk3;
	private String unk, unk2;

	@Override
	protected void runImpl()
	{
		// _log.info.println(getType() + " :: " + unk + " :: " + unk2 + " :: " + unk3);
	}

	/**
	 * format: SSd
	 */
	@Override
	protected void readImpl()
	{
		this.unk = this.readS();
		this.unk2 = this.readS();
		this.unk3 = this.readD();
	}
}