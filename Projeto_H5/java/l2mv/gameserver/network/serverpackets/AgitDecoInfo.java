package l2mv.gameserver.network.serverpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.model.entity.residence.ClanHall;
import l2mv.gameserver.model.entity.residence.ResidenceFunction;

public class AgitDecoInfo extends L2GameServerPacket
{
	private static final Logger _log = LoggerFactory.getLogger(AgitDecoInfo.class);
	private static int[] _buff =
	{
		0,
		1,
		1,
		1,
		2,
		2,
		2,
		2,
		2,
		0,
		0,
		1,
		1,
		1,
		2,
		2,
		2,
		2,
		2
	};
	private static int[] _itCr8 =
	{
		0,
		1,
		2,
		2
	};

	/**
	 * В коментах: Первое число = присланое сервером, второе  уровень установленый в кланхолле
	 * Пример: mp recovery: 0 = 0 значит что уровень 0 прислал 0,  уровни 5 и 15 прислали 1, уровни 25, 35, 50 прислали 2
	 *
	 * кажись нигде ничего не напутал...
	 */
	private int _id;
	private int hp_recovery;
	private int mp_recovery;
	private int exp_recovery;
	private int teleport;
	private int curtains;
	private int itemCreate;
	private int support;
	private int platform;

	public AgitDecoInfo(ClanHall clanHall)
	{
		_id = clanHall.getId();

		hp_recovery = getHpRecovery(clanHall.isFunctionActive(ResidenceFunction.RESTORE_HP) ? clanHall.getFunction(ResidenceFunction.RESTORE_HP).getLevel() : 0);// hp recovery, 0 = 0, 1
																																									// = 80, 120, 180, 2
																																									// = 240, 300
		mp_recovery = getMpRecovery(clanHall.isFunctionActive(ResidenceFunction.RESTORE_MP) ? clanHall.getFunction(ResidenceFunction.RESTORE_MP).getLevel() : 0);// mp recovery, 0 = 0, 1
																																									// = 5, 15, 2 = 30,
																																									// 40
		exp_recovery = getExpRecovery(clanHall.isFunctionActive(ResidenceFunction.RESTORE_EXP) ? clanHall.getFunction(ResidenceFunction.RESTORE_EXP).getLevel() : 0);// exp recovery, 0 =
																																										// 0, 1= 15, 2 =
																																										// 25, 35, 50
		teleport = clanHall.isFunctionActive(ResidenceFunction.TELEPORT) ? clanHall.getFunction(ResidenceFunction.TELEPORT).getLevel() : 0;// teleport, 0, 1, 2
		curtains = clanHall.isFunctionActive(ResidenceFunction.CURTAIN) ? clanHall.getFunction(ResidenceFunction.CURTAIN).getLevel() : 0;// curtains, 0 = 0, 1 = 1, 2 = 2
		itemCreate = clanHall.isFunctionActive(ResidenceFunction.ITEM_CREATE) ? _itCr8[clanHall.getFunction(ResidenceFunction.ITEM_CREATE).getLevel()] : 0;// item creation 0 = 0, 1 = 1,
																																							// 2 = 2, 3
		support = clanHall.isFunctionActive(ResidenceFunction.SUPPORT) ? _buff[clanHall.getFunction(ResidenceFunction.SUPPORT).getLevel()] : 0;// assist magic, 0 = 0, 1 = 3, 2 = 5, 7, 8
		platform = clanHall.isFunctionActive(ResidenceFunction.PLATFORM) ? clanHall.getFunction(ResidenceFunction.PLATFORM).getLevel() : 0;// front platform, 0 = 0, 1 = 1, 2 = 2
	}

	/**
	 * Packet send, must be confirmed
	 * writeC(0xfd);
	 * writeD(0); // clanhall id
	 * writeC(0); // FUNC_RESTORE_HP (Fireplace)
	 * writeC(0); // FUNC_RESTORE_MP (Carpet)
	 * writeC(0); // FUNC_RESTORE_MP (Statue)
	 * writeC(0); // FUNC_RESTORE_EXP (Chandelier)
	 * writeC(0); // FUNC_TELEPORT (Mirror)
	 * writeC(0); // Crytal
	 * writeC(0); // Curtain
	 * writeC(0); // FUNC_ITEM_CREATE (Magic Curtain)
	 * writeC(0); // FUNC_SUPPORT
	 * writeC(0); // FUNC_SUPPORT (Flag)
	 * writeC(0); // Front Platform
	 * writeC(0); // FUNC_ITEM_CREATE
	 * writeD(0);
	 * writeD(0);
	 * writeD(0);
	 * writeD(0);
	 * writeD(0);
	 */
	@Override
	protected final void writeImpl()
	{
		writeC(0xfd);
		writeD(_id); // clan hall id, во всяком случае всегда приходил 31.
		writeC(hp_recovery);
		writeC(mp_recovery); // Ковер
		writeC(mp_recovery); // Статуя
		writeC(exp_recovery);
		writeC(teleport);
		writeC(0); // кристалл? Что за хрень то :)?
		writeC(curtains);
		writeC(itemCreate);
		writeC(support);
		writeC(support); // Флаг
		writeC(platform);
		writeC(itemCreate);
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(0);
	}

	private static int getHpRecovery(int percent)
	{
		switch (percent)
		{
		case 0:
			return 0;
		case 20:
		case 40:
		case 80:
		case 120:
		case 140:
			return 1;
		case 160:
		case 180:
		case 200:
		case 220:
		case 240:
		case 260:
		case 280:
		case 300:
			return 2;
		default:
			_log.warn("Unsupported percent " + percent + " in hp recovery");
			return 0;
		}
	}

	private static int getMpRecovery(int percent)
	{
		switch (percent)
		{
		case 0:
			return 0;
		case 5:
		case 10:
		case 15:
		case 20:
			return 1;
		case 25:
		case 30:
		case 35:
		case 40:
		case 45:
		case 50:
			return 2;
		default:
			_log.warn("Unsupported percent " + percent + " in mp recovery");
			return 0;
		}
	}

	private static int getExpRecovery(int percent)
	{
		switch (percent)
		{
		case 0:
			return 0;
		case 5:
		case 10:
		case 15:
		case 20:
			return 1;
		case 25:
		case 30:
		case 35:
		case 40:
		case 45:
		case 50:
			return 2;
		default:
			_log.warn("Unsupported percent " + percent + " in exp recovery");
			return 0;
		}
	}
}