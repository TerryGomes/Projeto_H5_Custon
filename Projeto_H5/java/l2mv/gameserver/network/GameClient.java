package l2mv.gameserver.network;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lameguard.session.LameClientV195;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.net.nio.impl.MMOClient;
import l2mv.commons.net.nio.impl.MMOConnection;
import l2mv.gameserver.Config;
import l2mv.gameserver.SecondaryPasswordAuth;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.CharSelectInfoPackage;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.loginservercon.SessionKey;
import l2mv.gameserver.network.loginservercon.gspackets.PlayerLogout;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class GameClient extends MMOClient<MMOConnection<GameClient>> implements LameClientV195
{
	private static final Logger _log = LoggerFactory.getLogger(GameClient.class);
	public static final String NO_IP = "?.?.?.?";
	private SecondaryPasswordAuth _secondaryAuth;
	public static boolean SESSION_OK = MMOClient.SESSION_OK;

	public GameCrypt _crypt = null;

	public GameClientState _state;

	private String _HWID = "NO-SMART-GUARD-ENABLED";
	private String _fileId = "";
	private int _systemVer = -1;
	private int _serverId;

	public static enum GameClientState
	{
		CONNECTED, AUTHED, IN_GAME, DISCONNECTED
	}

	/** Данные аккаунта */
	private String _login;
	private int _bonus = 0;
	private int _bonusExpire;

	private Player _activeChar;
	private SessionKey _sessionKey;
	private String _ip = NO_IP;
	private int revision = 0;
	private boolean _gameGuardOk = false;
	// private SecondaryPasswordAuth _secondaryAuth;

	private final List<Integer> _charSlotMapping = new ArrayList<Integer>();

	public GameClient(MMOConnection<GameClient> con)
	{
		super(con);

		_state = GameClientState.CONNECTED;
		_crypt = new GameCrypt();
		if (con != null)
		{
			_ip = con.getSocket().getInetAddress().getHostAddress();
		}
	}

	@Override
	protected void onDisconnection()
	{
		setState(GameClientState.DISCONNECTED);
		final Player player = getActiveChar();
		setActiveChar(null);

		if (player != null && player.getNetConnection() != null)
		{
			player.setNetConnection(null);
			player.scheduleDelete();
		}

		if (getSessionKey() != null)
		{
			if (isAuthed())
			{
				AuthServerCommunication.getInstance().removeAuthedClient(getLogin());
				AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(getLogin()));
			}
			else
			{
				AuthServerCommunication.getInstance().removeWaitingClient(getLogin());
			}
		}
	}

	@Override
	protected void onForcedDisconnection()
	{
		// TODO Auto-generated method stub

	}

	public void markRestoredChar(int charSlot)
	{
		final int objId = getObjectIdForSlot(charSlot);
		if (objId < 0)
		{
			return;
		}
		if (_activeChar != null && _activeChar.getObjectId() == objId)
		{
			_activeChar.setDeleteTimer(0);
		}
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE obj_id=?"))
		{
			statement.setInt(1, objId);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("Error on markRestoredChar ", e);
		}
	}

	public void markToDeleteChar(int charSlot)
	{
		int objId = getObjectIdForSlot(charSlot);
		if (objId < 0)
		{
			return;
		}

		if ((_activeChar != null) && (_activeChar.getObjectId() == objId))
		{
			_activeChar.setDeleteTimer((int) (System.currentTimeMillis() / 1000));
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE characters SET deletetime=? WHERE obj_id=?"))
		{
			statement.setLong(1, (int) (System.currentTimeMillis() / 1000L));
			statement.setInt(2, objId);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.error("error on markToDeleteChar:", e);
		}
	}

	public void deleteChar(int charslot)
	{
		// have to make sure active character must be nulled
		if (_activeChar != null)
		{
			return;
		}

		int objid = getObjectIdForSlot(charslot);
		if (objid == -1)
		{
			return;
		}

		CharacterDAO.getInstance().deleteCharByObjId(objid);
	}

	public Player loadCharFromDisk(int charSlot, int objectId)
	{
		if (objectId == -1)
		{
			return null;
		}
		Player character = null;
		final Player oldPlayer = GameObjectsStorage.getPlayer(objectId);
		if (oldPlayer != null)
		{
			if (oldPlayer.isInOfflineMode() || oldPlayer.isLogoutStarted())
			{
				oldPlayer.kick();// Kicking Offline Shop Player
				return null;
			}
			else
			{
				oldPlayer.sendPacket(SystemMsg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
				// Kicking real player that was on the char
				GameClient oldClient = oldPlayer.getNetConnection();
				if (oldClient != null)
				{
					oldClient.setActiveChar(null);
					oldClient.closeNow(false);
				}
				oldPlayer.setNetConnection(this);
				character = oldPlayer;
			}
		}

		if (character == null)
		{
			character = Player.restore(objectId);
		}

		if (character != null)
		{
			setActiveChar(character);
		}
		else
		{
			_log.warn("could not restore obj_id: " + objectId + " in slot:" + charSlot);
		}

		return character;
	}

	public int getObjectIdForSlot(int charslot)
	{
		if ((charslot < 0) || (charslot >= _charSlotMapping.size()))
		{
			_log.warn(getLogin() + " tried to modify Character in slot " + charslot + " but no characters exits at that slot.");
			return -1;
		}
		return _charSlotMapping.get(charslot);
	}

	public int getSlotForObjectId(int objectId)
	{
		return _charSlotMapping.indexOf(objectId);
	}

	public Player getActiveChar()
	{
		return _activeChar;
	}

	/**
	 * @return Returns the sessionId.
	 */
	public SessionKey getSessionKey()
	{
		return _sessionKey;
	}

	public String getLogin()
	{
		return _login;
	}

	public void setLoginName(String loginName)
	{
		_login = loginName;
		if (Config.SECOND_AUTH_ENABLED)
		{
			_secondaryAuth = new SecondaryPasswordAuth(this);
		}
	}

	public void setActiveChar(Player player)
	{
		_activeChar = player;
		if (player != null)
		{
			player.setNetConnection(this);
		}
	}

	public void setSessionId(SessionKey sessionKey)
	{
		_sessionKey = sessionKey;
	}

	public void setCharSelection(CharSelectInfoPackage[] chars)
	{
		_charSlotMapping.clear();

		for (CharSelectInfoPackage element : chars)
		{
			final int objectId = element.getObjectId();
			_charSlotMapping.add(objectId);
		}
	}

	public int getRevision()
	{
		return revision;
	}

	public void setRevision(int revision)
	{
		this.revision = revision;
	}

	@Override
	public boolean encrypt(ByteBuffer buf, int size)
	{
		_crypt.encrypt(buf.array(), buf.position(), size);
		buf.position(buf.position() + size);
		return true;
	}

	@Override
	public boolean decrypt(ByteBuffer buf, int size)
	{
		final boolean ret = _crypt.decrypt(buf.array(), buf.position(), size);
		return ret;
	}

	public void sendPacket(L2GameServerPacket gsp)
	{
		if (isConnected())
		{
			getConnection().sendPacket(gsp);
		}
	}

	public void sendPacket(L2GameServerPacket... gsp)
	{
		if (isConnected())
		{
			getConnection().sendPacket(gsp);
		}
	}

	public void sendPackets(List<L2GameServerPacket> gsp)
	{
		if (isConnected())
		{
			getConnection().sendPackets(gsp);
		}
	}

	public void close(L2GameServerPacket gsp)
	{
		if (isConnected())
		{
			getConnection().close(gsp);
		}
	}

	public String getIpAddr()
	{
		return _ip;
	}

	public byte[] enableCrypt()
	{
		byte[] key = BlowFishKeygen.getRandomKey();
		_crypt.setKey(key);

		return key;
	}

	public int getBonus()
	{
		return _bonus;
	}

	public int getBonusExpire()
	{
		return _bonusExpire;
	}

	public void setBonus(int bonus)
	{
		_bonus = bonus;
	}

	public void setBonusExpire(int bonusExpire)
	{
		_bonusExpire = bonusExpire;
	}

	public GameClientState getState()
	{
		return _state;
	}

	public void setState(GameClientState state)
	{
		_state = state;
	}

	private int _failedPackets = 0;
	private int _unknownPackets = 0;

	public void onPacketReadFail()
	{
		if (_failedPackets++ >= 10)
		{
			_log.warn("Too many client packet fails, connection closed : " + this);
			closeNow(true);
		}
	}

	public void onUnknownPacket()
	{
		if (_unknownPackets++ >= 10)
		{
			_log.warn("Too many client unknown packets, connection closed : " + this);
			closeNow(true);
		}
	}

	@Override
	public String toString()
	{
		return _state + " IP: " + getIpAddr() + (_login == null ? "" : " Account: " + _login) + (_activeChar == null ? "" : " Player : " + _activeChar);
	}

	public SecondaryPasswordAuth getSecondaryAuth()
	{
		return _secondaryAuth;
	}

	public void setGameGuardOk(boolean gameGuardOk)
	{
		_gameGuardOk = gameGuardOk;
	}

	public boolean isGameGuardOk()
	{
		return _gameGuardOk;
	}

	private static byte[] _keyClientEn = new byte[8];

	public static void setKeyClientEn(byte[] key)
	{
		_keyClientEn = key;
	}

	public static byte[] getKeyClientEn()
	{
		return _keyClientEn;
	}

	private int _instanceCount;

	@Override
	public void setInstanceCount(int i)
	{
		_instanceCount = i;
	}

	@Override
	public int getInstanceCount()
	{
		return _instanceCount;
	}

	@Override
	public void setPatchVersion(int i)
	{
		_systemVer = i;
	}

	@Override
	public int getPatchVersion()
	{
		return _systemVer;
	}

	public String getFileId()
	{
		return _fileId;
	}

	private boolean _isProtected;

	@Override
	public void setProtected(boolean isProtected)
	{
		_isProtected = isProtected;
	}

	@Override
	public boolean isProtected()
	{
		return _isProtected;
	}

	@Override
	public void setHWID(String hwid)
	{
		_HWID = hwid;
	}

	@Override
	public String getHWID()
	{
		return _HWID;
	}

	public void setFileId(String fileId)
	{
		_fileId = fileId;
	}

	public int getServerId()
	{
		return _serverId;
	}

	public void setServerId(int serverId)
	{
		_serverId = serverId;
	}
}