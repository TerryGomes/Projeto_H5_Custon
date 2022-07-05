package l2f.gameserver.network;

import java.nio.ByteBuffer;
import java.util.List;

import l2f.commons.net.nio.impl.MMOConnection;
import l2f.commons.net.nio.impl.ReceivablePacket;
import l2f.gameserver.Config;
import l2f.gameserver.GameServer;
import l2f.gameserver.network.loginservercon.SessionKey;
import l2f.gameserver.network.serverpackets.CharacterSelectionInfo;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;
import l2f.gameserver.security.FakePlayersEngine;

/**
 * Created by Michał on 18.01.14.
 */
public class FakeGameClient extends GameClient
{
	public static GamePacketHandler packetHandler;
	private final FakePlayersEngine.FakePlayer fakePlayer;

	public FakeGameClient(MMOConnection<GameClient> con)
	{
		super(con);

		fakePlayer = FakePlayersEngine.getNewFakePlayer();
		if (fakePlayer == null)
		{
			return;
		}

		_state = GameClientState.CONNECTED;

		onCreation();
		setSessionId(new SessionKey(1, 2, 3, 4));
		setLoginName(fakePlayer.getAccountName());
	}

	private void onCreation()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				sendPacket(createPacket((byte) 0x0e).putInt(270));// ProtocolVersion

				setState(GameClientState.AUTHED);
				setCharSelection(CharacterSelectionInfo.loadCharacterSelectInfo(fakePlayer.getAccountName()));
				setRevision(Config.MIN_PROTOCOL_REVISION);

				// We are in Lobby
				sendPacket(createPacket((byte) 0x12).putInt(getCharSlot()));// CharacterSelected
				sendPacket(createPacket((byte) 0xd0).putShort((short) 0x01));// RequestManorList
				sendPacket(createPacket((byte) 0xd0).putShort((short) 0x3d));// RequestAllFortressInfo
				sendPacket(createPacket((byte) 0xd0).putShort((short) 0x21));// RequestKeyMapping
				sendPacket(createPacket((byte) 0x11));// EnterWorld
				sendPacket(createPacket((byte) 0x65).putInt(fakePlayer.getClanId()));// RequestPledgeInfo
			}
		}).start();
	}

	private ByteBuffer createPacket(byte packetId)
	{
		ByteBuffer buffer = ByteBuffer.allocate(65536);
		buffer.put(packetId);
		return buffer;
	}

	private void sendPacket(ByteBuffer buffer)
	{
		buffer.position(0);
		ReceivablePacket<GameClient> packet = packetHandler.handlePacket(buffer, this);
		if (packet != null)
		{
			GameServer.getInstance().getSelectorThreads()[0].readPacket(packet, buffer, this);
		}
		try
		{
			Thread.sleep(1000L);
		}
		catch (InterruptedException e)
		{
		}
	}

	private int getCharSlot()
	{
		return getSlotForObjectId(fakePlayer.getObjectId());
	}

	public static void setGamePacketHandler(GamePacketHandler handler)
	{
		packetHandler = handler;
	}

	@Override
	public void sendPacket(L2GameServerPacket gsp)
	{
		System.currentTimeMillis();
	}

	@Override
	public void sendPacket(L2GameServerPacket... gsp)
	{
		System.currentTimeMillis();
	}

	@Override
	public void sendPackets(List<L2GameServerPacket> gsp)
	{
		System.currentTimeMillis();
	}

	@Override
	public boolean isConnected()
	{
		return true;
	}

	@Override
	public void closeNow(boolean error)
	{
	}

	@Override
	public void close(L2GameServerPacket gsp)
	{
	}
}
