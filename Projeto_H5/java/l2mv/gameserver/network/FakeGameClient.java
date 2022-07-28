package l2mv.gameserver.network;

import java.nio.ByteBuffer;
import java.util.List;

import l2mv.commons.net.nio.impl.MMOConnection;
import l2mv.commons.net.nio.impl.ReceivablePacket;
import l2mv.gameserver.Config;
import l2mv.gameserver.GameServer;
import l2mv.gameserver.network.loginservercon.SessionKey;
import l2mv.gameserver.network.serverpackets.CharacterSelectionInfo;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
import l2mv.gameserver.security.FakePlayersEngine;

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

		this.fakePlayer = FakePlayersEngine.getNewFakePlayer();
		if (this.fakePlayer == null)
		{
			return;
		}

		this._state = GameClientState.CONNECTED;

		this.onCreation();
		this.setSessionId(new SessionKey(1, 2, 3, 4));
		this.setLoginName(this.fakePlayer.getAccountName());
	}

	private void onCreation()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				FakeGameClient.this.sendPacket(FakeGameClient.this.createPacket((byte) 0x0e).putInt(270));// ProtocolVersion

				FakeGameClient.this.setState(GameClientState.AUTHED);
				FakeGameClient.this.setCharSelection(CharacterSelectionInfo.loadCharacterSelectInfo(FakeGameClient.this.fakePlayer.getAccountName()));
				FakeGameClient.this.setRevision(Config.MIN_PROTOCOL_REVISION);

				// We are in Lobby
				FakeGameClient.this.sendPacket(FakeGameClient.this.createPacket((byte) 0x12).putInt(FakeGameClient.this.getCharSlot()));// CharacterSelected
				FakeGameClient.this.sendPacket(FakeGameClient.this.createPacket((byte) 0xd0).putShort((short) 0x01));// RequestManorList
				FakeGameClient.this.sendPacket(FakeGameClient.this.createPacket((byte) 0xd0).putShort((short) 0x3d));// RequestAllFortressInfo
				FakeGameClient.this.sendPacket(FakeGameClient.this.createPacket((byte) 0xd0).putShort((short) 0x21));// RequestKeyMapping
				FakeGameClient.this.sendPacket(FakeGameClient.this.createPacket((byte) 0x11));// EnterWorld
				FakeGameClient.this.sendPacket(FakeGameClient.this.createPacket((byte) 0x65).putInt(FakeGameClient.this.fakePlayer.getClanId()));// RequestPledgeInfo
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
		return this.getSlotForObjectId(this.fakePlayer.getObjectId());
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
