package l2mv.gameserver.network.serverpackets;

import java.util.Random;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.tables.FakePlayersTable;

public final class SendStatus extends L2GameServerPacket
{
	private static final long MIN_UPDATE_PERIOD = 30000;
	private static int online_players = 0;
	private static int max_online_players = 0;
	private static int online_priv_store = 0;
	private static long last_update = 0;

	public SendStatus()
	{
		if (System.currentTimeMillis() - last_update < MIN_UPDATE_PERIOD)
		{
			return;
		}
		last_update = System.currentTimeMillis();
		int i = 0;
		int j = 0;
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			i++;
			if (player.isInStoreMode() && (!Config.SENDSTATUS_TRADE_JUST_OFFLINE || player.isInOfflineMode()))
			{
				j++;
			}
		}
		online_players = i + FakePlayersTable.getFakePlayersCount();
		online_priv_store = (int) Math.floor(j * Config.SENDSTATUS_TRADE_MOD);
		max_online_players = Math.max(max_online_players, online_players);
	}

	@Override
	protected final void writeImpl()
	{
		Random ppc = new Random();

		this.writeC(0x00); // Packet ID
		this.writeD(0x01); // World ID
		this.writeD(max_online_players); // Max Online
		this.writeD(online_players); // Current Online
		this.writeD(online_players); // Current Online
		this.writeD(online_priv_store); // Priv.Store Chars
		if (Config.RWHO_SEND_TRASH)
		{
			this.writeH(0x30);
			this.writeH(0x2C);
			this.writeH(0x36);
			this.writeH(0x2C);

			if (Config.RWHO_ARRAY[12] == Config.RWHO_KEEP_STAT)
			{
				int z;
				z = ppc.nextInt(6);
				if (z == 0)
				{
					z += 2;
				}
				for (int x = 0; x < 8; x++)
				{
					if (x == 4)
					{
						Config.RWHO_ARRAY[x] = 44;
					}
					else
					{
						Config.RWHO_ARRAY[x] = 51 + ppc.nextInt(z);
					}
				}
				Config.RWHO_ARRAY[11] = 37265 + ppc.nextInt(z * 2 + 3);
				Config.RWHO_ARRAY[8] = 51 + ppc.nextInt(z);
				z = 36224 + ppc.nextInt(z * 2);
				Config.RWHO_ARRAY[9] = z;
				Config.RWHO_ARRAY[10] = z;
				Config.RWHO_ARRAY[12] = 1;
			}

			for (int z = 0; z < 8; z++)
			{
				if (z == 3)
				{
					Config.RWHO_ARRAY[z] -= 1;
				}
				this.writeH(Config.RWHO_ARRAY[z]);
			}
			this.writeD(Config.RWHO_ARRAY[8]);
			this.writeD(Config.RWHO_ARRAY[9]);
			this.writeD(Config.RWHO_ARRAY[10]);
			this.writeD(Config.RWHO_ARRAY[11]);
			Config.RWHO_ARRAY[12]++;
			this.writeD(0x00);
			this.writeD(0x02);
		}
	}
}