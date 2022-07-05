package npc.model.residences.fortress.siege;

import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 15:41/15.04.2011
 */
public class BackupPowerUnitInstance extends NpcInstance
{
	public static final int LIMIT = 3;

	public static final int COND_NO_ENTERED = 0;
	public static final int COND_ENTERED = 1;
	public static final int COND_ALL_OK = 2;
	public static final int COND_FAIL = 3;
	public static final int COND_TIMEOUT = 4;

	private int[] _generated = new int[LIMIT];
	private int _index;
	private int _tryCount;
	private long _invalidatePeriod;

	public BackupPowerUnitInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this) || (_index >= LIMIT))
		{
			return;
		}

		StringTokenizer token = new StringTokenizer(command);
		token.nextToken(); // step
		int val = Integer.parseInt(token.nextToken());

		if (player.getClassId() == ClassId.warsmith || player.getClassId() == ClassId.maestro)
		{
			if (_tryCount == 0)
			{
				_tryCount++;
			}
			else
			{
				_index++;
			}
		}
		else if (_generated[_index] == val)
		{
			_index++;
		}
		else
		{
			_tryCount++;
		}

		showChatWindow(player, 0);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		generate();
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		NpcHtmlMessage message = new NpcHtmlMessage(player, this);

		if (_invalidatePeriod > 0 && _invalidatePeriod < System.currentTimeMillis())
		{
			generate();
		}

		int cond = getCond();
		switch (cond)
		{
		case COND_ALL_OK:
			message.setFile("residence2/fortress/fortress_subpower002.htm");
			onDecay();
			break;
		case COND_TIMEOUT:
			message.setFile("residence2/fortress/fortress_subpower003.htm");
			break;
		case COND_FAIL:
			message.setFile("residence2/fortress/fortress_subpower003.htm");
			_invalidatePeriod = System.currentTimeMillis() + 30000L;
			break;
		case COND_ENTERED:
			message.setFile("residence2/fortress/fortress_subpower004.htm");
			message.replaceNpcString("%password%", _index == 0 ? NpcString.THE_MARKS_HAVE_NOT_BEEN_ASSEMBLED : _index == 1 ? NpcString.THE_1ST_MARK_IS_CORRECT : NpcString.THE_2ND_MARK_IS_CORRECT);
			message.replaceNpcString("%try_count%", NpcString.ATTEMPT_S1__3_IS_IN_PROGRESS, _tryCount);
			break;
		case COND_NO_ENTERED:
			message.setFile("residence2/fortress/fortress_subpower001.htm");
			break;
		}
		player.sendPacket(message);
	}

	private void generate()
	{
		_invalidatePeriod = 0;
		_tryCount = 0;
		_index = 0;

		for (int i = 0; i < _generated.length; i++)
		{
			_generated[i] = -1;
		}

		int j = 0;
		while (j != LIMIT)
		{
			int val = Rnd.get(0, LIMIT);
			if (ArrayUtils.contains(_generated, val))
			{
				continue;
			}
			_generated[j++] = val;
		}
	}

	private int getCond()
	{
		if (_invalidatePeriod > System.currentTimeMillis())
		{
			return COND_TIMEOUT;
		}
		else if (_tryCount >= LIMIT)
		{ // максимум лимит
			return COND_FAIL;
		}
		else if (_index == 0 && _tryCount == 0)
		{ // ищо ничего никто не клацал
			return COND_NO_ENTERED;
		}
		else if (_index == LIMIT)
		{ // все верно
			return COND_ALL_OK;
		}
		else
		{ // все верно
			return COND_ENTERED;
		}
	}

	public int[] getGenerated()
	{
		return _generated;
	}
}
