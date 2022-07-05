package l2f.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.SkillLearn;
import l2f.gameserver.model.base.AcquireType;
import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.model.pledge.SubUnit;

/**
 * @author: VISTALL
 * @date:  20:55/30.11.2010
 */
public final class SkillAcquireHolder extends AbstractHolder
{
	private static final SkillAcquireHolder _instance = new SkillAcquireHolder();

	public static SkillAcquireHolder getInstance()
	{
		return _instance;
	}

	// классовые зависимости
	private final TIntObjectHashMap<List<SkillLearn>> _normalSkillTree = new TIntObjectHashMap<List<SkillLearn>>();
	private final TIntObjectHashMap<List<SkillLearn>> _transferSkillTree = new TIntObjectHashMap<List<SkillLearn>>();
	// расовые зависимости
	private final TIntObjectHashMap<List<SkillLearn>> _fishingSkillTree = new TIntObjectHashMap<List<SkillLearn>>();
	private final TIntObjectHashMap<List<SkillLearn>> _transformationSkillTree = new TIntObjectHashMap<List<SkillLearn>>();
	// без зависимостей
	private final List<SkillLearn> _certificationSkillTree = new ArrayList<SkillLearn>();
	private final List<SkillLearn> _collectionSkillTree = new ArrayList<SkillLearn>();
	private final List<SkillLearn> _pledgeSkillTree = new ArrayList<SkillLearn>();
	private final List<SkillLearn> _subUnitSkillTree = new ArrayList<SkillLearn>();

	public int getMinLevelForNewSkill(Player player, AcquireType type)
	{
		List<SkillLearn> skills;
		switch (type)
		{
		case NORMAL:
			skills = _normalSkillTree.get(player.getActiveClassId());
			if (skills == null)
			{
				info("skill tree for class " + player.getActiveClassId() + " is not defined !");
				return 0;
			}
			break;
		case TRANSFORMATION:
			skills = _transformationSkillTree.get(player.getRace().ordinal());
			if (skills == null)
			{
				info("skill tree for race " + player.getRace().ordinal() + " is not defined !");
				return 0;
			}
			break;
		case FISHING:
			skills = _fishingSkillTree.get(player.getRace().ordinal());
			if (skills == null)
			{
				info("skill tree for race " + player.getRace().ordinal() + " is not defined !");
				return 0;
			}
			break;
		default:
			return 0;
		}
		int minlevel = 0;
		for (SkillLearn temp : skills)
		{
			if (temp.getMinLevel() > player.getLevel())
			{
				if (minlevel == 0 || temp.getMinLevel() < minlevel)
				{
					minlevel = temp.getMinLevel();
				}
			}
		}
		return minlevel;
	}

	public Collection<SkillLearn> getAvailableSkills(Player player, AcquireType type)
	{
		return getAvailableSkills(player, type, null);
	}

	public Collection<SkillLearn> getAvailableSkills(Player player, AcquireType type, SubUnit subUnit)
	{
		Collection<SkillLearn> skills;
		switch (type)
		{
		case NORMAL:
			skills = _normalSkillTree.get(player.getActiveClassId());
			if (skills == null)
			{
				info("skill tree for class " + player.getActiveClassId() + " is not defined !");
				return Collections.emptyList();
			}
			return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel());
		case COLLECTION:
			skills = _collectionSkillTree;
			if (skills == null)
			{
				info("skill tree for class " + player.getActiveClassId() + " is not defined !");
				return Collections.emptyList();
			}
			return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel());
		case TRANSFORMATION:
			skills = _transformationSkillTree.get(player.getRace().ordinal());
			if (skills == null)
			{
				info("skill tree for race " + player.getRace().ordinal() + " is not defined !");
				return Collections.emptyList();
			}
			return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel());
		case TRANSFER_EVA_SAINTS:
		case TRANSFER_SHILLIEN_SAINTS:
		case TRANSFER_CARDINAL:
			skills = _transferSkillTree.get(type.transferClassId());
			if (skills == null)
			{
				info("skill tree for class " + type.transferClassId() + " is not defined !");
				return Collections.emptyList();
			}
			if (player == null)
			{
				return skills;
			}
			else
			{
				Map<Integer, SkillLearn> skillLearnMap = new TreeMap<Integer, SkillLearn>();
				for (SkillLearn temp : skills)
				{
					if (temp.getMinLevel() <= player.getLevel())
					{
						int knownLevel = player.getSkillLevel(temp.getId());
						if (knownLevel == -1)
						{
							skillLearnMap.put(temp.getId(), temp);
						}
					}
				}

				return skillLearnMap.values();
			}
		case FISHING:
			skills = _fishingSkillTree.get(player.getRace().ordinal());
			if (skills == null)
			{
				info("skill tree for race " + player.getRace().ordinal() + " is not defined !");
				return Collections.emptyList();
			}
			return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel());
		case CLAN:
			skills = _pledgeSkillTree;
			Collection<Skill> skls = player.getClan().getSkills(); // TODO [VISTALL] придумать другой способ

			return getAvaliableList(skills, skls.toArray(new Skill[skls.size()]), player.getClan().getLevel());
		case SUB_UNIT:
			skills = _subUnitSkillTree;
			Collection<Skill> st = subUnit.getSkills(); // TODO [VISTALL] придумать другой способ

			return getAvaliableList(skills, st.toArray(new Skill[st.size()]), player.getClan().getLevel());
		case CERTIFICATION:
			skills = _certificationSkillTree;
			if (player == null)
			{
				return skills;
			}
			else
			{
				return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel());
			}
		default:
			return Collections.emptyList();
		}
	}

	public Collection<SkillLearn> getAvailableSkills(Player player, AcquireType type, SubUnit subUnit, int level)
	{
		Collection<SkillLearn> skills;
		switch (type)
		{
		case NORMAL:
			skills = _normalSkillTree.get(player.getActiveClassId());
			if (skills == null)
			{
				info("skill tree for class " + player.getActiveClassId() + " is not defined !");
				return Collections.emptyList();
			}
			return getAvaliableList(skills, player.getAllSkillsArray(), level);
		case COLLECTION:
			skills = _collectionSkillTree;
			if (skills == null)
			{
				info("skill tree for class " + player.getActiveClassId() + " is not defined !");
				return Collections.emptyList();
			}
			return getAvaliableList(skills, player.getAllSkillsArray(), level);
		case TRANSFORMATION:
			skills = _transformationSkillTree.get(player.getRace().ordinal());
			if (skills == null)
			{
				info("skill tree for race " + player.getRace().ordinal() + " is not defined !");
				return Collections.emptyList();
			}
			return getAvaliableList(skills, player.getAllSkillsArray(), level);
		case TRANSFER_EVA_SAINTS:
		case TRANSFER_SHILLIEN_SAINTS:
		case TRANSFER_CARDINAL:
			skills = _transferSkillTree.get(type.transferClassId());
			if (skills == null)
			{
				info("skill tree for class " + type.transferClassId() + " is not defined !");
				return Collections.emptyList();
			}
			if (player == null)
			{
				return skills;
			}
			else
			{
				Map<Integer, SkillLearn> skillLearnMap = new TreeMap<Integer, SkillLearn>();
				for (SkillLearn temp : skills)
				{
					if (temp.getMinLevel() <= player.getLevel())
					{
						int knownLevel = player.getSkillLevel(temp.getId());
						if (knownLevel == -1)
						{
							skillLearnMap.put(temp.getId(), temp);
						}
					}
				}

				return skillLearnMap.values();
			}
		case FISHING:
			skills = _fishingSkillTree.get(player.getRace().ordinal());
			if (skills == null)
			{
				info("skill tree for race " + player.getRace().ordinal() + " is not defined !");
				return Collections.emptyList();
			}
			return getAvaliableList(skills, player.getAllSkillsArray(), level);
		case CLAN:
			skills = _pledgeSkillTree;
			Collection<Skill> skls = player.getClan().getSkills(); // TODO [VISTALL] придумать другой способ

			return getAvaliableList(skills, skls.toArray(new Skill[skls.size()]), level);
		case SUB_UNIT:
			skills = _subUnitSkillTree;
			Collection<Skill> st = subUnit.getSkills(); // TODO [VISTALL] придумать другой способ

			return getAvaliableList(skills, st.toArray(new Skill[st.size()]), level);
		case CERTIFICATION:
			skills = _certificationSkillTree;
			if (player == null)
			{
				return skills;
			}
			else
			{
				return getAvaliableList(skills, player.getAllSkillsArray(), level);
			}
		default:
			return Collections.emptyList();
		}
	}

	private Collection<SkillLearn> getAvaliableList(Collection<SkillLearn> skillLearns, Skill[] skills, int level)
	{
		Map<Integer, SkillLearn> skillLearnMap = new TreeMap<Integer, SkillLearn>();
		for (SkillLearn temp : skillLearns)
		{
			if (temp.getMinLevel() <= level)
			{
				boolean knownSkill = false;
				for (int j = 0; j < skills.length && !knownSkill; j++)
				{
					if (skills[j].getId() == temp.getId())
					{
						knownSkill = true;
						if (skills[j].getLevel() == temp.getLevel() - 1)
						{
							skillLearnMap.put(temp.getId(), temp);
						}
					}
				}
				if (!knownSkill && temp.getLevel() == 1)
				{
					skillLearnMap.put(temp.getId(), temp);
				}
			}
		}

		return skillLearnMap.values();
	}

	public SkillLearn getSkillLearn(Player player, int id, int level, AcquireType type)
	{
		List<SkillLearn> skills;
		switch (type)
		{
		case NORMAL:
			skills = _normalSkillTree.get(player.getActiveClassId());
			break;
		case COLLECTION:
			skills = _collectionSkillTree;
			break;
		case TRANSFORMATION:
			skills = _transformationSkillTree.get(player.getRace().ordinal());
			break;
		case TRANSFER_CARDINAL:
		case TRANSFER_SHILLIEN_SAINTS:
		case TRANSFER_EVA_SAINTS:
			skills = _transferSkillTree.get(player.getActiveClassId());
			break;
		case FISHING:
			skills = _fishingSkillTree.get(player.getRace().ordinal());
			break;
		case CLAN:
			skills = _pledgeSkillTree;
			break;
		case SUB_UNIT:
			skills = _subUnitSkillTree;
			break;
		case CERTIFICATION:
			skills = _certificationSkillTree;
			break;
		default:
			return null;
		}

		if (skills == null)
		{
			return null;
		}

		for (SkillLearn temp : skills)
		{
			if (temp.getLevel() == level && temp.getId() == id)
			{
				return temp;
			}
		}

		return null;
	}

	public boolean isSkillPossible(Player player, Skill skill, AcquireType type)
	{
		Clan clan = null;
		List<SkillLearn> skills;
		switch (type)
		{
		case NORMAL:
			skills = _normalSkillTree.get(player.getActiveClassId());
			break;
		case COLLECTION:
			skills = _collectionSkillTree;
			break;
		case TRANSFORMATION:
			skills = _transformationSkillTree.get(player.getRace().ordinal());
			break;
		case FISHING:
			skills = _fishingSkillTree.get(player.getRace().ordinal());
			break;
		case TRANSFER_CARDINAL:
		case TRANSFER_EVA_SAINTS:
		case TRANSFER_SHILLIEN_SAINTS:
			int transferId = type.transferClassId();
			if (player.getActiveClassId() != transferId)
			{
				return false;
			}

			skills = _transferSkillTree.get(transferId);
			break;
		case CLAN:
			clan = player.getClan();
			if (clan == null)
			{
				return false;
			}
			skills = _pledgeSkillTree;
			break;
		case SUB_UNIT:
			clan = player.getClan();
			if (clan == null)
			{
				return false;
			}

			skills = _subUnitSkillTree;
			break;
		case CERTIFICATION:
			skills = _certificationSkillTree;
			break;
		default:
			return false;
		}

		return isSkillPossible(skills, skill);
	}

	private boolean isSkillPossible(Collection<SkillLearn> skills, Skill skill)
	{
		for (SkillLearn learn : skills)
		{
			if (learn.getId() == skill.getId() && learn.getLevel() <= skill.getLevel())
			{
				return true;
			}
		}
		return false;
	}

	public boolean isSkillPossible(Player player, Skill skill)
	{
		for (AcquireType aq : AcquireType.VALUES)
		{
			if (isSkillPossible(player, skill, aq))
			{
				return true;
			}
		}

		return false;
	}

	public List<SkillLearn> getSkillLearnListByItemId(Player player, int itemId)
	{
		List<SkillLearn> learns = _normalSkillTree.get(player.getActiveClassId());
		if (learns == null)
		{
			return Collections.emptyList();
		}

		List<SkillLearn> l = new ArrayList<SkillLearn>(1);
		for (SkillLearn $i : learns)
		{
			if ($i.getItemId() == itemId)
			{
				l.add($i);
			}
		}

		return l;
	}

	public List<SkillLearn> getAllNormalSkillTreeWithForgottenScrolls()
	{
		List<SkillLearn> a = new ArrayList<SkillLearn>();
		for (TIntObjectIterator<List<SkillLearn>> i = _normalSkillTree.iterator(); i.hasNext();)
		{
			i.advance();
			for (SkillLearn learn : i.value())
			{
				if (learn.getItemId() > 0 && learn.isClicked())
				{
					a.add(learn);
				}
			}
		}

		return a;
	}

	public void addAllNormalSkillLearns(TIntObjectHashMap<List<SkillLearn>> map)
	{
		int classID;

		for (ClassId classId : ClassId.VALUES)
		{
			if (classId.name().startsWith("dummyEntry"))
			{
				continue;
			}

			classID = classId.getId();

			List<SkillLearn> temp;

			temp = map.get(classID);
			if (temp == null)
			{
				info("Not found NORMAL skill learn for class " + classID);
				continue;
			}

			_normalSkillTree.put(classId.getId(), temp);

			ClassId secondparent = classId.getParent(1);
			if (secondparent == classId.getParent(0))
			{
				secondparent = null;
			}

			classId = classId.getParent(0);

			while (classId != null)
			{
				List<SkillLearn> parentList = _normalSkillTree.get(classId.getId());
				temp.addAll(parentList);

				classId = classId.getParent(0);
				if (classId == null && secondparent != null)
				{
					classId = secondparent;
					secondparent = secondparent.getParent(1);
				}
			}
		}
	}

	public void addAllFishingLearns(int race, List<SkillLearn> s)
	{
		_fishingSkillTree.put(race, s);
	}

	public void addAllTransferLearns(int classId, List<SkillLearn> s)
	{
		_transferSkillTree.put(classId, s);
	}

	public void addAllTransformationLearns(int race, List<SkillLearn> s)
	{
		_transformationSkillTree.put(race, s);
	}

	public void addAllCertificationLearns(List<SkillLearn> s)
	{
		_certificationSkillTree.addAll(s);
	}

	public void addAllCollectionLearns(List<SkillLearn> s)
	{
		_collectionSkillTree.addAll(s);
	}

	public void addAllSubUnitLearns(List<SkillLearn> s)
	{
		_subUnitSkillTree.addAll(s);
	}

	public void addAllPledgeLearns(List<SkillLearn> s)
	{
		_pledgeSkillTree.addAll(s);
	}

	@Override
	public void log()
	{
		info("load " + sizeTroveMap(_normalSkillTree) + " normal learns for " + _normalSkillTree.size() + " classes.");
		info("load " + sizeTroveMap(_transferSkillTree) + " transfer learns for " + _transferSkillTree.size() + " classes.");
		//
		info("load " + sizeTroveMap(_transformationSkillTree) + " transformation learns for " + _transformationSkillTree.size() + " races.");
		info("load " + sizeTroveMap(_fishingSkillTree) + " fishing learns for " + _fishingSkillTree.size() + " races.");
		//
		info("load " + _certificationSkillTree.size() + " certification learns.");
		info("load " + _collectionSkillTree.size() + " collection learns.");
		info("load " + _pledgeSkillTree.size() + " pledge learns.");
		info("load " + _subUnitSkillTree.size() + " sub unit learns.");
	}

	@Deprecated
	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		_normalSkillTree.clear();
		_fishingSkillTree.clear();
		_transferSkillTree.clear();
		_certificationSkillTree.clear();
		_collectionSkillTree.clear();
		_pledgeSkillTree.clear();
		_subUnitSkillTree.clear();
	}

	private int sizeTroveMap(TIntObjectHashMap<List<SkillLearn>> a)
	{
		int i = 0;
		for (TIntObjectIterator<List<SkillLearn>> iterator = a.iterator(); iterator.hasNext();)
		{
			iterator.advance();
			i += iterator.value().size();
		}

		return i;
	}
}
