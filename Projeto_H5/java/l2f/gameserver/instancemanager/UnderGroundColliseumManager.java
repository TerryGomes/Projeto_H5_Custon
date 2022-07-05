package l2f.gameserver.instancemanager;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.model.Zone;
import l2f.gameserver.model.Zone.ZoneType;
import l2f.gameserver.model.entity.Coliseum;
import l2f.gameserver.utils.ReflectionUtils;

// TODO: zones for underground coliseum, restart points, spawn points etc.
// TODO: unregister and safe checks on register.
public class UnderGroundColliseumManager
{
	private static final Logger _log = LoggerFactory.getLogger(UnderGroundColliseumManager.class);

	private static UnderGroundColliseumManager _instance;
	private HashMap<String, Coliseum> _coliseums;

	public static UnderGroundColliseumManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new UnderGroundColliseumManager();
		}
		return _instance;
	}

	public UnderGroundColliseumManager()
	{
		List<Zone> zones = ReflectionUtils.getZonesByType(ZoneType.UnderGroundColiseum);
		if (zones.isEmpty())
		{
			_log.info("Not found zones for UnderGround Colliseum!!!");
		}
		else
		{
			for (Zone zone : zones)
			{
				getColiseums().put(zone.getName(), new Coliseum());
			}
		}
		_log.info("Loaded: " + getColiseums().size() + " UnderGround Colliseums.");
	}

	public HashMap<String, Coliseum> getColiseums()
	{
		if (_coliseums == null)
		{
			_coliseums = new HashMap<String, Coliseum>();
		}
		return _coliseums;
	}

	public Coliseum getColiseumByLevelLimit(int limit)
	{
		for (Coliseum coliseum : _coliseums.values())
		{
			if (coliseum.getMaxLevel() == limit)
			{
				return coliseum;
			}
		}
		return null;
	}
}