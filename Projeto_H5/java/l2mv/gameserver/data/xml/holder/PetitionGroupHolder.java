package l2mv.gameserver.data.xml.holder;

import java.util.Collection;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

import l2mv.commons.data.xml.AbstractHolder;
import l2mv.gameserver.model.petition.PetitionMainGroup;

/**
 * @author VISTALL
 * @date 7:22/25.07.2011
 */
public class PetitionGroupHolder extends AbstractHolder
{
	private static PetitionGroupHolder _instance = new PetitionGroupHolder();

	private IntObjectMap<PetitionMainGroup> _petitionGroups = new HashIntObjectMap<PetitionMainGroup>();

	public static PetitionGroupHolder getInstance()
	{
		return _instance;
	}

	private PetitionGroupHolder()
	{
	}

	public void addPetitionGroup(PetitionMainGroup g)
	{
		_petitionGroups.put(g.getId(), g);
	}

	public PetitionMainGroup getPetitionGroup(int val)
	{
		return _petitionGroups.get(val);
	}

	public Collection<PetitionMainGroup> getPetitionGroups()
	{
		return _petitionGroups.values();
	}

	@Override
	public int size()
	{
		return _petitionGroups.size();
	}

	@Override
	public void clear()
	{
		_petitionGroups.clear();
	}
}
