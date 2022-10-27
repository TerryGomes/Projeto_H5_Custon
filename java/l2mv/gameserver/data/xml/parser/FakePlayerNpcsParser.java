package l2mv.gameserver.data.xml.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

//import l2mv.commons.crypt.CryptUtil;
import l2mv.commons.data.xml.AbstractFileParser;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.FakePlayerNpcsHolder;
import l2mv.gameserver.skills.AbnormalEffect;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.templates.npc.FakePlayerTemplate;

public final class FakePlayerNpcsParser extends AbstractFileParser<FakePlayerNpcsHolder>
{
	private static final int[] EMPTY_CUBICS = new int[0];

	private FakePlayerNpcsParser()
	{
		super(FakePlayerNpcsHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/fake_player_npcs.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "fake_player_npcs.dtd";
	}

	@Override
	protected void readData(Element rootElement)
	{
		final Iterator<Element> npcIterator = rootElement.elementIterator();
		while (npcIterator.hasNext())
		{
			final Element npcData = npcIterator.next();
			final int templateId = Integer.parseInt(npcData.attributeValue("template_id"));
			final String name = npcData.attributeValue("name");
			final String title = npcData.attributeValue("title");
			final StatsSet set = new StatsSet();
			addNameValues(npcData, set);
			addBodyValues(npcData, set);
			addAnimationValues(npcData, set);
			addEffectValues(npcData, set);
			final Map<Integer, Integer> inventory = parseInventory(npcData);
			final FakePlayerTemplate template = new FakePlayerTemplate(templateId, name, title, set, inventory);
			getHolder().addTemplate(template);
		}
	}

	public void saveNewTemplate(FakePlayerTemplate template) throws IOException, DocumentException
	{
		final Document document = _reader.read(getXMLFile());
//		final Document document = _reader.read(CryptUtil.decryptOnDemand(getXMLFile()));
		writeTemplateToDocument(document, template);
		final OutputFormat format = OutputFormat.createPrettyPrint();
		final XMLWriter writer = new XMLWriter(new FileOutputStream(getXMLFile()), format);
		writer.write(document);
	}

	private static void writeTemplateToDocument(Document document, FakePlayerTemplate template)
	{
		final Element newTemplate = document.getRootElement().addElement("npc");
		newTemplate.addAttribute("template_id", String.valueOf(template.getTemplateId())).addAttribute("name", template.getName()).addAttribute("title", template.getTitle());
		final Element nameElement = newTemplate.addElement("name");
		nameElement.addAttribute("pvp_flag", template.isHasPvpFlag() ? "True" : "False");
		nameElement.addAttribute("karma", String.valueOf(template.getKarma()));
		nameElement.addAttribute("recommends", String.valueOf(template.getRecommends()));
		nameElement.addAttribute("name_color", String.valueOf(template.getNameColor()));
		nameElement.addAttribute("title_color", String.valueOf(template.getTitleColor()));
		final Element bodyElement = newTemplate.addElement("body");
		bodyElement.addAttribute("race", template.getRace().toString());
		bodyElement.addAttribute("sex", (template.getSex() == 0) ? "male" : "female");
		bodyElement.addAttribute("class_id", String.valueOf(template.getClassId()));
		bodyElement.addAttribute("hair_style", String.valueOf(template.getHairStyle()));
		bodyElement.addAttribute("hair_color", String.valueOf(template.getHairColor()));
		bodyElement.addAttribute("face", String.valueOf(template.getFace()));
		final Element animationElement = newTemplate.addElement("animation");
		animationElement.addAttribute("sit", template.isSitting() ? "True" : "False");
		animationElement.addAttribute("run", template.isRunning() ? "True" : "False");
		animationElement.addAttribute("combat", template.isInCombat() ? "True" : "False");
		animationElement.addAttribute("dead", template.isDead() ? "True" : "False");
		animationElement.addAttribute("store", template.isStore() ? "True" : "False");
		final Element effectElement = newTemplate.addElement("effect");
		effectElement.addAttribute("weapon_enchant", String.valueOf(template.getWeaponEnchant()));
		effectElement.addAttribute("weapon_glow", template.getWeaponGlow() ? "True" : "False");
		effectElement.addAttribute("abnormal", String.valueOf(template.getAbnormal()));
		effectElement.addAttribute("abnormal2", String.valueOf(template.getAbnormal2()));
		effectElement.addAttribute("hero", template.isHero() ? "True" : "False");
		effectElement.addAttribute("agathion", String.valueOf(template.getAgathion()));
		effectElement.addAttribute("cubics", prepareIntArray(template.getCubics()));
		effectElement.addAttribute("team", template.getTeam().toString());
		final Element itemsElement = newTemplate.addElement("items");
		itemsElement.addAttribute("weapon", template.getInventory().containsKey(7) ? String.valueOf(template.getInventory().get(7)) : "");
		itemsElement.addAttribute("shield", template.getInventory().containsKey(8) ? String.valueOf(template.getInventory().get(8)) : "");
		itemsElement.addAttribute("chest", template.getInventory().containsKey(10) ? String.valueOf(template.getInventory().get(10)) : "");
		itemsElement.addAttribute("leggings", template.getInventory().containsKey(11) ? String.valueOf(template.getInventory().get(11)) : "");
		itemsElement.addAttribute("gloves", template.getInventory().containsKey(9) ? String.valueOf(template.getInventory().get(9)) : "");
		itemsElement.addAttribute("boots", template.getInventory().containsKey(12) ? String.valueOf(template.getInventory().get(12)) : "");
		itemsElement.addAttribute("cloak", template.getInventory().containsKey(13) ? String.valueOf(template.getInventory().get(13)) : "");
		itemsElement.addAttribute("accessory_head", template.getInventory().containsKey(15) ? String.valueOf(template.getInventory().get(15)) : "");
		itemsElement.addAttribute("accessory_face", template.getInventory().containsKey(16) ? String.valueOf(template.getInventory().get(16)) : "");
	}

	private static void addNameValues(Element npcData, StatsSet set)
	{
		final Iterator<Element> parameterIterator = npcData.elementIterator("name");
		while (parameterIterator.hasNext())
		{
			final Element param = parameterIterator.next();
			set.put("hasPvpFlag", Boolean.parseBoolean(param.attributeValue("pvp_flag")));
			set.put("karma", Integer.parseInt(param.attributeValue("karma")));
			set.put("recommends", Integer.parseInt(param.attributeValue("recommends")));
			set.put("nameColor", param.attributeValue("name_color").isEmpty() ? Config.NORMAL_NAME_COLOUR : Integer.parseInt(param.attributeValue("name_color")));
			set.put("titleColor", param.attributeValue("title_color").isEmpty() ? 16777079 : Integer.parseInt(param.attributeValue("title_color")));
		}
	}

	private static void addBodyValues(Element npcData, StatsSet set)
	{
		final Iterator<Element> parameterIterator = npcData.elementIterator("body");
		while (parameterIterator.hasNext())
		{
			final Element param = parameterIterator.next();
			set.put("race", param.attributeValue("race"));
			set.put("sex", param.attributeValue("sex").equalsIgnoreCase("male") ? 0 : 1);
			set.put("classId", Integer.parseInt(param.attributeValue("class_id")));
			set.put("hairStyle", Integer.parseInt(param.attributeValue("hair_style")));
			set.put("hairColor", Integer.parseInt(param.attributeValue("hair_color")));
			set.put("face", Integer.parseInt(param.attributeValue("face")));
		}
	}

	private static void addAnimationValues(Element npcData, StatsSet set)
	{
		final Iterator<Element> parameterIterator = npcData.elementIterator("animation");
		while (parameterIterator.hasNext())
		{
			final Element param = parameterIterator.next();
			set.put("isSitting", Boolean.parseBoolean(param.attributeValue("sit")));
			set.put("isRunning", Boolean.parseBoolean(param.attributeValue("run")));
			set.put("inCombat", Boolean.parseBoolean(param.attributeValue("combat")));
			set.put("isDead", Boolean.parseBoolean(param.attributeValue("dead")));
			set.put("isStore", Boolean.parseBoolean(param.attributeValue("store")));
		}
	}

	private static void addEffectValues(Element npcData, StatsSet set)
	{
		final Iterator<Element> parameterIterator = npcData.elementIterator("effect");
		while (parameterIterator.hasNext())
		{
			final Element param = parameterIterator.next();
			set.put("weaponEnchant", Integer.parseInt(param.attributeValue("weapon_enchant")));
			set.put("weaponGlow", Boolean.parseBoolean(param.attributeValue("weapon_glow")));
			set.put("abnormal", parseAbnormal(param.attributeValue("abnormal")));
			set.put("abnormal2", parseAbnormal(param.attributeValue("abnormal2")));
			set.put("isHero", Boolean.parseBoolean(param.attributeValue("hero")));
			set.put("agathion", Integer.parseInt(param.attributeValue("agathion")));
			set.put("cubics", parseIntArray(param.attributeValue("cubics")));
			set.put("team", param.attributeValue("team"));
		}
	}

	private static Map<Integer, Integer> parseInventory(Element npcData)
	{
		final Map<Integer, Integer> inventory = new HashMap<Integer, Integer>(9);
		final Iterator<Element> parameterIterator = npcData.elementIterator("items");
		while (parameterIterator.hasNext())
		{
			final Element param = parameterIterator.next();
			if (!param.attributeValue("weapon").isEmpty())
			{
				inventory.put(7, Integer.parseInt(param.attributeValue("weapon")));
			}
			if (!param.attributeValue("shield").isEmpty())
			{
				inventory.put(8, Integer.parseInt(param.attributeValue("shield")));
			}
			if (!param.attributeValue("chest").isEmpty())
			{
				inventory.put(10, Integer.parseInt(param.attributeValue("chest")));
			}
			if (!param.attributeValue("leggings").isEmpty())
			{
				inventory.put(11, Integer.parseInt(param.attributeValue("leggings")));
			}
			if (!param.attributeValue("gloves").isEmpty())
			{
				inventory.put(9, Integer.parseInt(param.attributeValue("gloves")));
			}
			if (!param.attributeValue("boots").isEmpty())
			{
				inventory.put(12, Integer.parseInt(param.attributeValue("boots")));
			}
			if (!param.attributeValue("cloak").isEmpty())
			{
				inventory.put(13, Integer.parseInt(param.attributeValue("cloak")));
			}
			if (!param.attributeValue("accessory_head").isEmpty())
			{
				inventory.put(15, Integer.parseInt(param.attributeValue("accessory_head")));
			}
			if (!param.attributeValue("accessory_face").isEmpty())
			{
				inventory.put(16, Integer.parseInt(param.attributeValue("accessory_face")));
			}
		}
		return inventory;
	}

	private static int parseAbnormal(String text)
	{
		if (text.isEmpty())
		{
			return 0;
		}
		final String[] abnormalNames = text.split(";");
		int finalEffect = 0;
		for (String abnormal : abnormalNames)
		{
			if (StringUtils.isNumeric(abnormal))
			{
				finalEffect |= Integer.parseInt(abnormal);
			}
			else
			{
				finalEffect |= AbnormalEffect.valueOf(abnormal).getMask();
			}
		}
		return finalEffect;
	}

	private static int[] parseIntArray(String text)
	{
		if (text.isEmpty())
		{
			return FakePlayerNpcsParser.EMPTY_CUBICS;
		}
		final String[] splitText = text.split(";");
		final int[] array = new int[splitText.length];
		for (int i = 0; i < array.length; ++i)
		{
			array[i] = Integer.parseInt(splitText[i]);
		}
		return array;
	}

	private static String prepareIntArray(int[] array)
	{
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < array.length; ++i)
		{
			if (i > 0)
			{
				builder.append(";");
			}
			builder.append(array[i]);
		}
		return builder.toString();
	}

	public static FakePlayerNpcsParser getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final FakePlayerNpcsParser _instance = new FakePlayerNpcsParser();
	}
}
