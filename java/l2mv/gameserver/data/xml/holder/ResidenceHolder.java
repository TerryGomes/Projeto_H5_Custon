package l2mv.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

import l2mv.commons.data.xml.AbstractHolder;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.entity.residence.Residence;

/**
 * @author VISTALL
 * @date 0:17/12.02.2011
 */
@SuppressWarnings("unchecked")
public final class ResidenceHolder extends AbstractHolder
{
	private static ResidenceHolder _instance = new ResidenceHolder();

	private IntObjectMap<Residence> _residences = new TreeIntObjectMap<Residence>();

	@SuppressWarnings("rawtypes")
	private Map<Class, List<Residence>> _fastResidencesByType = new HashMap<Class, List<Residence>>(4);

	public static ResidenceHolder getInstance()
	{
		return _instance;
	}

	private ResidenceHolder()
	{
		//
	}

	public void addResidence(Residence r)
	{
		_residences.put(r.getId(), r);
	}

	public <R extends Residence> R getResidence(int id)
	{
		return (R) _residences.get(id);
	}

	public <R extends Residence> R getResidence(Class<R> type, int id)
	{
		Residence r = getResidence(id);
		if (r == null || r.getClass() != type)
		{
			return null;
		}

		return (R) r;
	}

	public <R extends Residence> List<R> getResidenceList(Class<R> t)
	{
		return (List<R>) _fastResidencesByType.get(t);
	}

	public Collection<Residence> getResidences()
	{
		return _residences.values();
	}

	public <R extends Residence> R getResidenceByObject(Class<? extends Residence> type, GameObject object)
	{
		return (R) getResidenceByCoord(type, object.getX(), object.getY(), object.getZ(), object.getReflection());
	}

	public <R extends Residence> R getResidenceByCoord(Class<R> type, int x, int y, int z, Reflection ref)
	{
		Collection<Residence> residences = type == null ? getResidences() : (Collection<Residence>) getResidenceList(type);
		for (Residence residence : residences)
		{
			if (residence.checkIfInZone(x, y, z, ref))
			{
				return (R) residence;
			}
		}
		return null;
	}

	public <R extends Residence> R findNearestResidence(Class<R> clazz, int x, int y, int z, Reflection ref, int offset)
	{
		Residence residence = getResidenceByCoord(clazz, x, y, z, ref);
		if (residence == null)
		{
			double closestDistance = offset;
			double distance;
			for (Residence r : getResidenceList(clazz))
			{
				distance = r.getZone().findDistanceToZone(x, y, z, false);
				if (closestDistance > distance)
				{
					closestDistance = distance;
					residence = r;
				}
			}
		}
		return (R) residence;
	}

	public void callInit()
	{
		for (Residence r : getResidences())
		{
			r.init();
		}
	}

	private void buildFastLook()
	{
		for (Residence residence : _residences.values())
		{
			List<Residence> list = _fastResidencesByType.get(residence.getClass());
			if (list == null)
			{
				_fastResidencesByType.put(residence.getClass(), (list = new ArrayList<Residence>()));
			}
			list.add(residence);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void log()
	{
		buildFastLook();
		info("total size: " + _residences.size());
		for (Map.Entry<Class, List<Residence>> entry : _fastResidencesByType.entrySet())
		{
			info(" - load " + entry.getValue().size() + " " + entry.getKey().getSimpleName().toLowerCase() + "(s).");
		}
	}

	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		_residences.clear();
		_fastResidencesByType.clear();
	}
}
