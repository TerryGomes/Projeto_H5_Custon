package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.dao.ClickersSignatureDao;

public class ClickersSignatures extends L2GameServerPacket
{
	private final int[] signatures;

	public ClickersSignatures()
	{
		this.signatures = ClickersSignatureDao.getInstance().getSignatures();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x0E);
		this.writeD(this.signatures.length);

		for (int sig : this.signatures)
		{
			this.writeD(sig);
		}
	}
}