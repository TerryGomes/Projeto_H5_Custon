package l2f.gameserver.data.xml.parser;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

import l2f.commons.data.xml.AbstractDirParser;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.TeleportLocation;
import l2f.gameserver.model.entity.SevenSigns;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.entity.residence.Fortress;
import l2f.gameserver.model.entity.residence.Residence;
import l2f.gameserver.model.entity.residence.ResidenceFunction;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.templates.StatsSet;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.templates.item.support.MerchantGuard;
import l2f.gameserver.utils.Location;

public final class ResidenceParser extends AbstractDirParser<ResidenceHolder>
{
	private static ResidenceParser _instance = new ResidenceParser();

	public static ResidenceParser getInstance()
	{
		return _instance;
	}

	private ResidenceParser()
	{
		super(ResidenceHolder.getInstance());
	}

	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/residences/");
	}

	@Override
	public boolean isIgnored(File f)
	{
		return false;
	}

	@Override
	public String getDTDFileName()
	{
		return "residence.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		String impl = rootElement.attributeValue("impl");
		Class<?> clazz = null;

		StatsSet set = new StatsSet();
		for (Iterator<Attribute> iterator = rootElement.attributeIterator(); iterator.hasNext();)
		{
			Attribute element = iterator.next();
			set.set(element.getName(), element.getValue());
		}

		Residence residence = null;
		try
		{
			clazz = Class.forName("l2f.gameserver.model.entity.residence." + impl);
			Constructor<?> constructor = clazz.getConstructor(StatsSet.class);
			residence = (Residence) constructor.newInstance(set);
			getHolder().addResidence(residence);
		}
		catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			error("fail to init: " + getCurrentFileName(), e);
			return;
		}

		for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();
			String nodeName = element.getName();
			int level = element.attributeValue("level") == null ? 0 : Integer.valueOf(element.attributeValue("level"));
			int lease = (int) ((element.attributeValue("lease") == null ? 0 : Integer.valueOf(element.attributeValue("lease"))) * Config.RESIDENCE_LEASE_FUNC_MULTIPLIER);
			int npcId = element.attributeValue("npcId") == null ? 0 : Integer.valueOf(element.attributeValue("npcId"));
			int listId = element.attributeValue("listId") == null ? 0 : Integer.valueOf(element.attributeValue("listId"));

			ResidenceFunction function = null;
			if (nodeName.equalsIgnoreCase("teleport"))
			{
				function = checkAndGetFunction(residence, ResidenceFunction.TELEPORT);
				List<TeleportLocation> targets = new ArrayList<TeleportLocation>();
				for (Iterator<Element> it2 = element.elementIterator(); it2.hasNext();)
				{
					Element teleportElement = it2.next();
					if ("target".equalsIgnoreCase(teleportElement.getName()))
					{
						int npcStringId = Integer.parseInt(teleportElement.attributeValue("name"));
						long price = Long.parseLong(teleportElement.attributeValue("price"));
						int itemId = teleportElement.attributeValue("item") == null ? ItemTemplate.ITEM_ID_ADENA : Integer.parseInt(teleportElement.attributeValue("item"));
						String nameString = teleportElement.attributeValue("StringName");
						String nameStringLang = teleportElement.attributeValue("StringNameLang");
						TeleportLocation loc = new TeleportLocation(itemId, price, npcStringId, nameString, nameStringLang, 0);
						loc.set(Location.parseLoc(teleportElement.attributeValue("loc")));
						targets.add(loc);
					}
				}
				function.addTeleports(level, targets.toArray(new TeleportLocation[targets.size()]));
			}
			else if (nodeName.equalsIgnoreCase("support"))
			{
				if (level > 9 && !Config.ALT_CH_ALLOW_1H_BUFFS)
				{
					continue;
				}
				function = checkAndGetFunction(residence, ResidenceFunction.SUPPORT);
				function.addBuffs(level);
			}
			else if (nodeName.equalsIgnoreCase("item_create"))
			{
				function = checkAndGetFunction(residence, ResidenceFunction.ITEM_CREATE);
				function.addBuylist(level, new int[]
				{
					npcId,
					listId
				});
			}
			else if (nodeName.equalsIgnoreCase("curtain"))
			{
				function = checkAndGetFunction(residence, ResidenceFunction.CURTAIN);
			}
			else if (nodeName.equalsIgnoreCase("platform"))
			{
				function = checkAndGetFunction(residence, ResidenceFunction.PLATFORM);
			}
			else if (nodeName.equalsIgnoreCase("restore_exp"))
			{
				function = checkAndGetFunction(residence, ResidenceFunction.RESTORE_EXP);
			}
			else if (nodeName.equalsIgnoreCase("restore_hp"))
			{
				function = checkAndGetFunction(residence, ResidenceFunction.RESTORE_HP);
			}
			else if (nodeName.equalsIgnoreCase("restore_mp"))
			{
				function = checkAndGetFunction(residence, ResidenceFunction.RESTORE_MP);
			}
			else if (nodeName.equalsIgnoreCase("skills"))
			{
				for (Iterator<Element> nextIterator = element.elementIterator(); nextIterator.hasNext();)
				{
					Element nextElement = nextIterator.next();
					int id2 = Integer.parseInt(nextElement.attributeValue("id"));
					int level2 = Integer.parseInt(nextElement.attributeValue("level"));

					Skill skill = SkillTable.getInstance().getInfo(id2, level2);
					if (skill != null)
					{
						residence.addSkill(skill);
					}
				}
			}
			else if (nodeName.equalsIgnoreCase("banish_points"))
			{
				for (Iterator<Element> banishPointsIterator = element.elementIterator(); banishPointsIterator.hasNext();)
				{
					Location loc = Location.parse(banishPointsIterator.next());

					residence.addBanishPoint(loc);
				}
			}
			else if (nodeName.equalsIgnoreCase("owner_restart_points"))
			{
				for (Iterator<Element> ownerRestartPointsIterator = element.elementIterator(); ownerRestartPointsIterator.hasNext();)
				{
					Location loc = Location.parse(ownerRestartPointsIterator.next());

					residence.addOwnerRestartPoint(loc);
				}
			}
			else if (nodeName.equalsIgnoreCase("other_restart_points"))
			{
				for (Iterator<Element> otherRestartPointsIterator = element.elementIterator(); otherRestartPointsIterator.hasNext();)
				{
					Location loc = Location.parse(otherRestartPointsIterator.next());

					residence.addOtherRestartPoint(loc);
				}
			}
			else if (nodeName.equalsIgnoreCase("chaos_restart_points"))
			{
				for (Iterator<Element> chaosRestartPointsIterator = element.elementIterator(); chaosRestartPointsIterator.hasNext();)
				{
					Location loc = Location.parse(chaosRestartPointsIterator.next());

					residence.addChaosRestartPoint(loc);
				}
			}
			else if (nodeName.equalsIgnoreCase("related_fortresses"))
			{
				for (Iterator<Element> subElementIterator = element.elementIterator(); subElementIterator.hasNext();)
				{
					Element subElement = subElementIterator.next();
					if (subElement.getName().equalsIgnoreCase("domain"))
					{
						((Castle) residence).addRelatedFortress(Fortress.DOMAIN, Integer.parseInt(subElement.attributeValue("fortress")));
					}
					else if (subElement.getName().equalsIgnoreCase("boundary"))
					{
						((Castle) residence).addRelatedFortress(Fortress.BOUNDARY, Integer.parseInt(subElement.attributeValue("fortress")));
					}
				}
			}
			else if (nodeName.equalsIgnoreCase("merchant_guards"))
			{
				for (Iterator<Element> subElementIterator = element.elementIterator(); subElementIterator.hasNext();)
				{
					Element subElement = subElementIterator.next();

					int itemId = Integer.parseInt(subElement.attributeValue("item_id"));
					int npcId2 = Integer.parseInt(subElement.attributeValue("npc_id"));
					int maxGuard = Integer.parseInt(subElement.attributeValue("max"));
					IntSet intSet = new HashIntSet(3);
					String[] ssq = subElement.attributeValue("ssq").split(";");
					for (String q : ssq)
					{
						if (q.equalsIgnoreCase("cabal_null"))
						{
							intSet.add(SevenSigns.CABAL_NULL);
						}
						else if (q.equalsIgnoreCase("cabal_dusk"))
						{
							intSet.add(SevenSigns.CABAL_DUSK);
						}
						else if (q.equalsIgnoreCase("cabal_dawn"))
						{
							intSet.add(SevenSigns.CABAL_DAWN);
						}
						else
						{
							error("Unknown ssq type: " + q + "; file: " + getCurrentFileName());
						}
					}

					((Castle) residence).addMerchantGuard(new MerchantGuard(itemId, npcId2, maxGuard, intSet));
				}
			}

			if (function != null)
			{
				function.addLease(level, lease);
			}
		}
	}

	private ResidenceFunction checkAndGetFunction(Residence residence, int type)
	{
		ResidenceFunction function = residence.getFunction(type);
		if (function == null)
		{
			function = new ResidenceFunction(residence.getId(), type);
			residence.addFunction(function);
		}
		return function;
	}
}
