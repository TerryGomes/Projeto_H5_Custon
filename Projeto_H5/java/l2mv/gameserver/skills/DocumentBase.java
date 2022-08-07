package l2mv.gameserver.skills;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

//import l2mv.commons.crypt.CryptUtil;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.skills.effects.EffectTemplate;
import l2mv.gameserver.stats.StatTemplate;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.stats.conditions.Condition;
import l2mv.gameserver.stats.conditions.ConditionFirstEffectSuccess;
import l2mv.gameserver.stats.conditions.ConditionGameTime;
import l2mv.gameserver.stats.conditions.ConditionGameTime.CheckGameTime;
import l2mv.gameserver.stats.conditions.ConditionHasSkill;
import l2mv.gameserver.stats.conditions.ConditionLogicAnd;
import l2mv.gameserver.stats.conditions.ConditionLogicNot;
import l2mv.gameserver.stats.conditions.ConditionLogicOr;
import l2mv.gameserver.stats.conditions.ConditionPlayerAgathion;
import l2mv.gameserver.stats.conditions.ConditionPlayerClassId;
import l2mv.gameserver.stats.conditions.ConditionPlayerCubic;
import l2mv.gameserver.stats.conditions.ConditionPlayerFightClub;
import l2mv.gameserver.stats.conditions.ConditionPlayerHasBuff;
import l2mv.gameserver.stats.conditions.ConditionPlayerHasBuffId;
import l2mv.gameserver.stats.conditions.ConditionPlayerInstanceZone;
import l2mv.gameserver.stats.conditions.ConditionPlayerMaxLevel;
import l2mv.gameserver.stats.conditions.ConditionPlayerMaxPK;
import l2mv.gameserver.stats.conditions.ConditionPlayerMinLevel;
import l2mv.gameserver.stats.conditions.ConditionPlayerMinMaxDamage;
import l2mv.gameserver.stats.conditions.ConditionPlayerOlympiad;
import l2mv.gameserver.stats.conditions.ConditionPlayerPercentCp;
import l2mv.gameserver.stats.conditions.ConditionPlayerPercentHp;
import l2mv.gameserver.stats.conditions.ConditionPlayerPercentMp;
import l2mv.gameserver.stats.conditions.ConditionPlayerRace;
import l2mv.gameserver.stats.conditions.ConditionPlayerRiding;
import l2mv.gameserver.stats.conditions.ConditionPlayerRiding.CheckPlayerRiding;
import l2mv.gameserver.stats.conditions.ConditionPlayerState;
import l2mv.gameserver.stats.conditions.ConditionPlayerState.CheckPlayerState;
import l2mv.gameserver.stats.conditions.ConditionPlayerSummonSiegeGolem;
import l2mv.gameserver.stats.conditions.ConditionSlotItemId;
import l2mv.gameserver.stats.conditions.ConditionTargetActiveSkillId;
import l2mv.gameserver.stats.conditions.ConditionTargetAggro;
import l2mv.gameserver.stats.conditions.ConditionTargetCastleDoor;
import l2mv.gameserver.stats.conditions.ConditionTargetClan;
import l2mv.gameserver.stats.conditions.ConditionTargetDirection;
import l2mv.gameserver.stats.conditions.ConditionTargetForbiddenClassId;
import l2mv.gameserver.stats.conditions.ConditionTargetHasBuff;
import l2mv.gameserver.stats.conditions.ConditionTargetHasBuffId;
import l2mv.gameserver.stats.conditions.ConditionTargetHasForbiddenSkill;
import l2mv.gameserver.stats.conditions.ConditionTargetMob;
import l2mv.gameserver.stats.conditions.ConditionTargetMobId;
import l2mv.gameserver.stats.conditions.ConditionTargetNpcClass;
import l2mv.gameserver.stats.conditions.ConditionTargetPercentCp;
import l2mv.gameserver.stats.conditions.ConditionTargetPercentHp;
import l2mv.gameserver.stats.conditions.ConditionTargetPercentMp;
import l2mv.gameserver.stats.conditions.ConditionTargetPlayable;
import l2mv.gameserver.stats.conditions.ConditionTargetPlayer;
import l2mv.gameserver.stats.conditions.ConditionTargetPlayerRace;
import l2mv.gameserver.stats.conditions.ConditionTargetRace;
import l2mv.gameserver.stats.conditions.ConditionTargetSummon;
import l2mv.gameserver.stats.conditions.ConditionUsingArmor;
import l2mv.gameserver.stats.conditions.ConditionUsingItemType;
import l2mv.gameserver.stats.conditions.ConditionUsingSkill;
import l2mv.gameserver.stats.conditions.ConditionZoneType;
import l2mv.gameserver.stats.funcs.FuncTemplate;
import l2mv.gameserver.stats.triggers.TriggerInfo;
import l2mv.gameserver.stats.triggers.TriggerType;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2mv.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2mv.gameserver.utils.PositionUtils;

abstract class DocumentBase
{
	private static final Logger _log = LoggerFactory.getLogger(DocumentBase.class);

	private final File file;
	protected Map<String, Object[]> tables;

	DocumentBase(File file)
	{
		this.file = file;
		tables = new HashMap<String, Object[]>();
	}

	Document parse()
	{
		Document doc;
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			doc = factory.newDocumentBuilder().parse(file);
		}
		catch (Exception e)
		{
			_log.error("Error loading file " + file, e);
			return null;
		}
		try
		{
			parseDocument(doc);
		}
		catch (Exception e)
		{
			_log.error("Error in file " + file, e);
			return null;
		}
		return doc;
	}
//	{
//		Document doc;
//		try
//		{
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			factory.setValidating(false);
//			factory.setIgnoringComments(true);
//			FileInputStream stream = new FileInputStream(file);
//			InputStream output;
//			if ((byte) stream.read() == 0x00)
//			{
//				byte[] bytes = new byte[0];
//				output = new ByteArrayInputStream(bytes);
//				output = CryptUtil.decrypt(stream, output);
//			}
//			else
//			{
//				output = new FileInputStream(file);
//			}
//			doc = factory.newDocumentBuilder().parse(output);
//		}
//		catch (FileNotFoundException e)
//		{
//			_log.error("Didn't find " + file, e);
//			return null;
//		}
//		catch (IOException | ParserConfigurationException | SAXException e)
//		{
//			_log.error("Error loading file " + file, e);
//			return null;
//		}
//
//		try
//		{
//			parseDocument(doc);
//		}
//		catch (RuntimeException e)
//		{
//			_log.error("Error in file " + file, e);
//			return null;
//		}
//		return doc;
//	}

	protected abstract void parseDocument(Document doc);

	protected abstract Object getTableValue(String name);

	protected abstract Object getTableValue(String name, int idx);

	protected void resetTable()
	{
		tables = new HashMap<String, Object[]>();
	}

	protected void setTable(String name, Object[] table)
	{
		tables.put(name, table);
	}

	protected void parseTemplate(Node n, StatTemplate template)
	{
		n = n.getFirstChild();
		if (n == null)
		{
			return;
		}
		for (; n != null; n = n.getNextSibling())
		{
			String nodeName = n.getNodeName();
			if ("add".equalsIgnoreCase(nodeName))
			{
				attachFunc(n, template, "Add");
			}
			else if ("sub".equalsIgnoreCase(nodeName))
			{
				attachFunc(n, template, "Sub");
			}
			else if ("mul".equalsIgnoreCase(nodeName))
			{
				attachFunc(n, template, "Mul");
			}
			else if ("div".equalsIgnoreCase(nodeName))
			{
				attachFunc(n, template, "Div");
			}
			else if ("set".equalsIgnoreCase(nodeName))
			{
				attachFunc(n, template, "Set");
			}
			else if ("enchant".equalsIgnoreCase(nodeName))
			{
				attachFunc(n, template, "Enchant");
			}
			else if ("effect".equalsIgnoreCase(nodeName))
			{
				if (template instanceof EffectTemplate)
				{
					throw new RuntimeException("Nested effects");
				}
				attachEffect(n, template);
			}
			else if (template instanceof EffectTemplate)
			{
				if ("def".equalsIgnoreCase(nodeName))
				{
					parseBeanSet(n, ((EffectTemplate) template).getParam(), ((Skill) ((EffectTemplate) template).getParam().getObject("object")).getLevel());
				}
				else
				{
					Condition cond = parseCondition(n);
					if (cond != null)
					{
						((EffectTemplate) template).attachCond(cond);
					}
				}
			}
		}
	}

	protected void parseTrigger(Node n, StatTemplate template)
	{
		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("trigger".equalsIgnoreCase(n.getNodeName()))
			{
				NamedNodeMap map = n.getAttributes();

				int id = parseNumber(map.getNamedItem("id").getNodeValue()).intValue();
				int level = parseNumber(map.getNamedItem("level").getNodeValue()).intValue();
				TriggerType t = TriggerType.valueOf(map.getNamedItem("type").getNodeValue());
				double chance = parseNumber(map.getNamedItem("chance").getNodeValue()).doubleValue();

				TriggerInfo trigger = new TriggerInfo(id, level, t, chance);

				for (Node n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling())
				{
					Condition condition = parseCondition(n.getFirstChild());
					if (condition != null)
					{
						trigger.addCondition(condition);
					}
				}

				template.addTrigger(trigger);
			}
		}
	}

	protected void attachFunc(Node n, StatTemplate template, String name)
	{
		Stats stat = Stats.valueOfXml(n.getAttributes().getNamedItem("stat").getNodeValue());
		String order = n.getAttributes().getNamedItem("order").getNodeValue();
		int ord = parseNumber(order).intValue();
		Condition applyCond = parseCondition(n.getFirstChild());
		double val = 0;
		if (n.getAttributes().getNamedItem("val") != null)
		{
			val = parseNumber(n.getAttributes().getNamedItem("val").getNodeValue()).doubleValue();
		}

		template.attachFunc(new FuncTemplate(applyCond, name, stat, ord, val));
	}

	protected void attachEffect(Node n, Object template)
	{
		NamedNodeMap attrs = n.getAttributes();
		StatsSet set = new StatsSet();

		set.set("name", attrs.getNamedItem("name").getNodeValue());
		set.set("object", template);
		if (attrs.getNamedItem("count") != null)
		{
			set.set("count", parseNumber(attrs.getNamedItem("count").getNodeValue()).intValue());
		}
		if (attrs.getNamedItem("time") != null)
		{
			set.set("time", parseNumber(attrs.getNamedItem("time").getNodeValue()).intValue());
		}

		set.set("value", attrs.getNamedItem("val") != null ? parseNumber(attrs.getNamedItem("val").getNodeValue()).doubleValue() : 0.);

		set.set("abnormal", AbnormalEffect.NULL);
		set.set("abnormal2", AbnormalEffect.NULL);
		set.set("abnormal3", AbnormalEffect.NULL);
		if (attrs.getNamedItem("abnormal") != null)
		{
			AbnormalEffect ae = AbnormalEffect.getByName(attrs.getNamedItem("abnormal").getNodeValue());
			if (ae.isSpecial())
			{
				set.set("abnormal2", ae);
			}
			if (ae.isEvent())
			{
				set.set("abnormal3", ae);
			}
			else
			{
				set.set("abnormal", ae);
			}
		}

		if (attrs.getNamedItem("stackType") != null)
		{
			set.set("stackType", attrs.getNamedItem("stackType").getNodeValue());
		}
		if (attrs.getNamedItem("stackType2") != null)
		{
			set.set("stackType2", attrs.getNamedItem("stackType2").getNodeValue());
		}
		if (attrs.getNamedItem("stackOrder") != null)
		{
			set.set("stackOrder", parseNumber(attrs.getNamedItem("stackOrder").getNodeValue()).intValue());
		}

		if (attrs.getNamedItem("applyOnCaster") != null)
		{
			set.set("applyOnCaster", Boolean.valueOf(attrs.getNamedItem("applyOnCaster").getNodeValue()));
		}
		if (attrs.getNamedItem("applyOnSummon") != null)
		{
			set.set("applyOnSummon", Boolean.valueOf(attrs.getNamedItem("applyOnSummon").getNodeValue()));
		}

		if (attrs.getNamedItem("displayId") != null)
		{
			set.set("displayId", parseNumber(attrs.getNamedItem("displayId").getNodeValue()).intValue());
		}
		if (attrs.getNamedItem("displayLevel") != null)
		{
			set.set("displayLevel", parseNumber(attrs.getNamedItem("displayLevel").getNodeValue()).intValue());
		}
		if (attrs.getNamedItem("chance") != null)
		{
			set.set("chance", parseNumber(attrs.getNamedItem("chance").getNodeValue()).intValue());
		}
		if (attrs.getNamedItem("cancelOnAction") != null)
		{
			set.set("cancelOnAction", Boolean.valueOf(attrs.getNamedItem("cancelOnAction").getNodeValue()));
		}
		if (attrs.getNamedItem("isOffensive") != null)
		{
			set.set("isOffensive", Boolean.valueOf(attrs.getNamedItem("isOffensive").getNodeValue()));
		}
		if (attrs.getNamedItem("isReflectable") != null)
		{
			set.set("isReflectable", Boolean.valueOf(attrs.getNamedItem("isReflectable").getNodeValue()));
		}

		EffectTemplate lt = new EffectTemplate(set);

		parseTemplate(n, lt);
		for (Node n1 = n.getFirstChild(); n1 != null; n1 = n1.getNextSibling())
		{
			if ("triggers".equalsIgnoreCase(n1.getNodeName()))
			{
				parseTrigger(n1, lt);
			}
		}

		if (template instanceof Skill)
		{
			((Skill) template).attach(lt);
		}
	}

	protected Condition parseCondition(Node n)
	{
		while (n != null && n.getNodeType() != Node.ELEMENT_NODE)
		{
			n = n.getNextSibling();
		}
		if (n == null)
		{
			return null;
		}
		if ("and".equalsIgnoreCase(n.getNodeName()))
		{
			return parseLogicAnd(n);
		}
		if ("or".equalsIgnoreCase(n.getNodeName()))
		{
			return parseLogicOr(n);
		}
		if ("not".equalsIgnoreCase(n.getNodeName()))
		{
			return parseLogicNot(n);
		}
		if ("player".equalsIgnoreCase(n.getNodeName()))
		{
			return parsePlayerCondition(n);
		}
		if ("target".equalsIgnoreCase(n.getNodeName()))
		{
			return parseTargetCondition(n);
		}
		if ("has".equalsIgnoreCase(n.getNodeName()))
		{
			return parseHasCondition(n);
		}
		if ("using".equalsIgnoreCase(n.getNodeName()))
		{
			return parseUsingCondition(n);
		}
		if ("game".equalsIgnoreCase(n.getNodeName()))
		{
			return parseGameCondition(n);
		}
		if ("zone".equalsIgnoreCase(n.getNodeName()))
		{
			return parseZoneCondition(n);
		}
		return null;
	}

	protected Condition parseLogicAnd(Node n)
	{
		ConditionLogicAnd cond = new ConditionLogicAnd();
		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				cond.add(parseCondition(n));
			}
		}
		if (cond._conditions == null || cond._conditions.length == 0)
		{
			_log.error("Empty <and> condition in " + file);
		}
		return cond;
	}

	protected Condition parseLogicOr(Node n)
	{
		ConditionLogicOr cond = new ConditionLogicOr();
		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				cond.add(parseCondition(n));
			}
		}
		if (cond._conditions == null || cond._conditions.length == 0)
		{
			_log.error("Empty <or> condition in " + file);
		}
		return cond;
	}

	protected Condition parseLogicNot(Node n)
	{
		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				return new ConditionLogicNot(parseCondition(n));
			}
		}
		_log.error("Empty <not> condition in " + file);
		return null;
	}

	protected Condition parsePlayerCondition(Node n)
	{
		Condition cond = null;
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node a = attrs.item(i);
			String nodeName = a.getNodeName();
			if ("race".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionPlayerRace(a.getNodeValue()));
			}
			else if ("minLevel".equalsIgnoreCase(nodeName))
			{
				int lvl = parseNumber(a.getNodeValue()).intValue();
				cond = joinAnd(cond, new ConditionPlayerMinLevel(lvl));
			}
			else if ("summon_siege_golem".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionPlayerSummonSiegeGolem());
			}
			else if ("maxLevel".equalsIgnoreCase(nodeName))
			{
				int lvl = parseNumber(a.getNodeValue()).intValue();
				cond = joinAnd(cond, new ConditionPlayerMaxLevel(lvl));
			}
			else if ("maxPK".equalsIgnoreCase(nodeName))
			{
				int pk = parseNumber(a.getNodeValue()).intValue();
				cond = joinAnd(cond, new ConditionPlayerMaxPK(pk));
			}
			else if ("resting".equalsIgnoreCase(nodeName))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.RESTING, val));
			}
			else if ("moving".equalsIgnoreCase(nodeName))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.MOVING, val));
			}
			else if ("running".equalsIgnoreCase(nodeName))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.RUNNING, val));
			}
			else if ("standing".equalsIgnoreCase(nodeName))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.STANDING, val));
			}
			else if ("flying".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.FLYING, val));
			}
			else if ("flyingTransform".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.FLYING_TRANSFORM, val));
			}
			else if ("olympiad".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerOlympiad(val));
			}
			else if ("fightClub".equals(a.getNodeName()))
			{
				final boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerFightClub(val));
			}
			else if ("active_skill_id".equalsIgnoreCase(a.getNodeName()))
			{
				int skill_id = parseNumber(a.getNodeValue()).intValue();
				cond = joinAnd(cond, new ConditionTargetActiveSkillId(skill_id));
			}
			else if ("percentHP".equalsIgnoreCase(nodeName))
			{
				int percentHP = parseNumber(a.getNodeValue()).intValue();
				cond = joinAnd(cond, new ConditionPlayerPercentHp(percentHP));
			}
			else if ("percentMP".equalsIgnoreCase(nodeName))
			{
				int percentMP = parseNumber(a.getNodeValue()).intValue();
				cond = joinAnd(cond, new ConditionPlayerPercentMp(percentMP));
			}
			else if ("percentCP".equalsIgnoreCase(nodeName))
			{
				int percentCP = parseNumber(a.getNodeValue()).intValue();
				cond = joinAnd(cond, new ConditionPlayerPercentCp(percentCP));
			}
			else if ("agathion".equalsIgnoreCase(nodeName))
			{
				int agathionId = parseNumber(a.getNodeValue()).intValue();
				cond = joinAnd(cond, new ConditionPlayerAgathion(agathionId));
			}
			else if ("cubic".equalsIgnoreCase(nodeName))
			{
				int cubicId = parseNumber(a.getNodeValue()).intValue();
				cond = joinAnd(cond, new ConditionPlayerCubic(cubicId));
			}
			else if ("instance_zone".equalsIgnoreCase(nodeName))
			{
				int id = parseNumber(a.getNodeValue()).intValue();
				cond = joinAnd(cond, new ConditionPlayerInstanceZone(id));
			}
			else if ("riding".equalsIgnoreCase(nodeName))
			{
				String riding = a.getNodeValue();
				if ("strider".equalsIgnoreCase(riding))
				{
					cond = joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.STRIDER));
				}
				else if ("wyvern".equalsIgnoreCase(riding))
				{
					cond = joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.WYVERN));
				}
				else if ("none".equalsIgnoreCase(riding))
				{
					cond = joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.NONE));
				}
			}
			else if ("classId".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionPlayerClassId(a.getNodeValue().split(",")));
			}
			else if ("hasBuffId".equalsIgnoreCase(nodeName))
			{
				StringTokenizer st = new StringTokenizer(a.getNodeValue(), ";");
				int id = Integer.parseInt(st.nextToken().trim());
				int level = -1;
				if (st.hasMoreTokens())
				{
					level = Integer.parseInt(st.nextToken().trim());
				}
				cond = joinAnd(cond, new ConditionPlayerHasBuffId(id, level));
			}
			else if ("hasBuff".equalsIgnoreCase(nodeName))
			{
				StringTokenizer st = new StringTokenizer(a.getNodeValue(), ";");
				EffectType et = Enum.valueOf(EffectType.class, st.nextToken().trim());
				int level = -1;
				if (st.hasMoreTokens())
				{
					level = Integer.parseInt(st.nextToken().trim());
				}
				cond = joinAnd(cond, new ConditionPlayerHasBuff(et, level));
			}
			else if ("damage".equalsIgnoreCase(nodeName))
			{
				String[] st = a.getNodeValue().split(";");
				cond = joinAnd(cond, new ConditionPlayerMinMaxDamage(Double.parseDouble(st[0]), Double.parseDouble(st[1])));
			}
		}

		if (cond == null)
		{
			_log.error("Unrecognized <player> condition in " + file);
		}
		return cond;
	}

	protected Condition parseTargetCondition(Node n)
	{
		Condition cond = null;
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node a = attrs.item(i);
			String nodeName = a.getNodeName();
			String nodeValue = a.getNodeValue();
			if ("aggro".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetAggro(Boolean.valueOf(nodeValue)));
			}
			else if ("pvp".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetPlayable(Boolean.valueOf(nodeValue)));
			}
			else if ("player".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetPlayer(Boolean.valueOf(nodeValue)));
			}
			else if ("summon".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetSummon(Boolean.valueOf(nodeValue)));
			}
			else if ("mob".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetMob(Boolean.valueOf(nodeValue)));
			}
			else if ("mobId".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetMobId(Integer.parseInt(nodeValue)));
			}
			else if ("race".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetRace(nodeValue));
			}
			else if ("npc_class".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetNpcClass(nodeValue));
			}
			else if ("playerRace".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetPlayerRace(nodeValue));
			}
			else if ("forbiddenClassIds".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetForbiddenClassId(nodeValue.split(";")));
			}
			else if ("playerSameClan".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetClan(nodeValue));
			}
			else if ("castledoor".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetCastleDoor(Boolean.valueOf(nodeValue)));
			}
			else if ("direction".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetDirection(PositionUtils.TargetDirection.valueOf(nodeValue.toUpperCase())));
			}
			else if ("percentHP".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetPercentHp(parseNumber(a.getNodeValue()).intValue()));
			}
			else if ("percentMP".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetPercentMp(parseNumber(a.getNodeValue()).intValue()));
			}
			else if ("percentCP".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetPercentCp(parseNumber(a.getNodeValue()).intValue()));
			}
			else if ("hasBuffId".equalsIgnoreCase(nodeName))
			{
				StringTokenizer st = new StringTokenizer(nodeValue, ";");
				int id = Integer.parseInt(st.nextToken().trim());
				int level = -1;
				if (st.hasMoreTokens())
				{
					level = Integer.parseInt(st.nextToken().trim());
				}
				cond = joinAnd(cond, new ConditionTargetHasBuffId(id, level));
			}
			else if ("hasBuff".equalsIgnoreCase(nodeName))
			{
				StringTokenizer st = new StringTokenizer(nodeValue, ";");
				EffectType et = Enum.valueOf(EffectType.class, st.nextToken().trim());
				int level = -1;
				if (st.hasMoreTokens())
				{
					level = Integer.parseInt(st.nextToken().trim());
				}
				cond = joinAnd(cond, new ConditionTargetHasBuff(et, level));
			}
			else if ("hasForbiddenSkill".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionTargetHasForbiddenSkill(parseNumber(a.getNodeValue()).intValue()));
			}
		}
		if (cond == null)
		{
			_log.error("Unrecognized <target> condition in " + file);
		}
		return cond;
	}

	protected Condition parseUsingCondition(Node n)
	{
		Condition cond = null;
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node a = attrs.item(i);
			String nodeName = a.getNodeName();
			String nodeValue = a.getNodeValue();
			if ("kind".equalsIgnoreCase(nodeName) || "weapon".equalsIgnoreCase(nodeName))
			{
				long mask = 0;
				StringTokenizer st = new StringTokenizer(nodeValue, ",");
				tokens:
				while (st.hasMoreTokens())
				{
					String item = st.nextToken().trim();
					for (WeaponType wt : WeaponType.VALUES)
					{
						if (wt.toString().equalsIgnoreCase(item))
						{
							mask |= wt.mask();
							continue tokens;
						}
					}
					for (ArmorType at : ArmorType.VALUES)
					{
						if (at.toString().equalsIgnoreCase(item))
						{
							mask |= at.mask();
							continue tokens;
						}
					}
					_log.error("Invalid item kind: \"" + item + "\" in " + file);
				}
				if (mask != 0)
				{
					cond = joinAnd(cond, new ConditionUsingItemType(mask));
				}
			}
			else if ("armor".equalsIgnoreCase(nodeName))
			{
				ArmorType armor = ArmorType.valueOf(nodeValue.toUpperCase());
				cond = joinAnd(cond, new ConditionUsingArmor(armor));
			}
			else if ("skill".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionUsingSkill(Integer.parseInt(nodeValue)));
			}
			else if ("slotitem".equalsIgnoreCase(nodeName))
			{
				StringTokenizer st = new StringTokenizer(nodeValue, ";");
				int id = Integer.parseInt(st.nextToken().trim());
				int slot = Integer.parseInt(st.nextToken().trim());
				int enchant = 0;
				if (st.hasMoreTokens())
				{
					enchant = Integer.parseInt(st.nextToken().trim());
				}
				cond = joinAnd(cond, new ConditionSlotItemId(slot, id, enchant));
			}
		}
		if (cond == null)
		{
			_log.error("Unrecognized <using> condition in " + file);
		}
		return cond;
	}

	protected Condition parseHasCondition(Node n)
	{
		Condition cond = null;
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node a = attrs.item(i);
			String nodeName = a.getNodeName();
			String nodeValue = a.getNodeValue();
			if ("skill".equalsIgnoreCase(nodeName))
			{
				StringTokenizer st = new StringTokenizer(nodeValue, ";");
				Integer id = parseNumber(st.nextToken().trim()).intValue();
				int level = parseNumber(st.nextToken().trim()).shortValue();
				cond = joinAnd(cond, new ConditionHasSkill(id, level));
			}
			else if ("success".equalsIgnoreCase(nodeName))
			{
				cond = joinAnd(cond, new ConditionFirstEffectSuccess(Boolean.valueOf(nodeValue)));
			}
		}
		if (cond == null)
		{
			_log.error("Unrecognized <has> condition in " + file);
		}
		return cond;
	}

	protected Condition parseGameCondition(Node n)
	{
		Condition cond = null;
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node a = attrs.item(i);
			if ("night".equalsIgnoreCase(a.getNodeName()))
			{
				boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionGameTime(CheckGameTime.NIGHT, val));
			}
		}
		if (cond == null)
		{
			_log.error("Unrecognized <game> condition in " + file);
		}
		return cond;
	}

	protected Condition parseZoneCondition(Node n)
	{
		Condition cond = null;
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node a = attrs.item(i);
			if ("type".equalsIgnoreCase(a.getNodeName()))
			{
				cond = joinAnd(cond, new ConditionZoneType(a.getNodeValue()));
			}
		}
		if (cond == null)
		{
			_log.error("Unrecognized <zone> condition in " + file);
		}
		return cond;
	}

	protected Object[] parseTable(Node n)
	{
		NamedNodeMap attrs = n.getAttributes();
		String name = attrs.getNamedItem("name").getNodeValue();
		if (name.charAt(0) != '#')
		{
			throw new IllegalArgumentException("Table name must start with #");
		}
		StringTokenizer data = new StringTokenizer(n.getFirstChild().getNodeValue());
		List<String> array = new ArrayList<String>();
		while (data.hasMoreTokens())
		{
			array.add(data.nextToken());
		}
		Object[] res = array.toArray(new Object[array.size()]);
		setTable(name, res);
		return res;
	}

	protected void parseBeanSet(Node n, StatsSet set, int level)
	{
		try
		{
			String name = n.getAttributes().getNamedItem("name").getNodeValue().trim();
			String value = n.getAttributes().getNamedItem("val").getNodeValue().trim();
			char ch = value.length() == 0 ? ' ' : value.charAt(0);
			if (value.contains("#") && ch != '#')
			{
				for (String str : value.split("[;: ]+"))
				{
					if (str.charAt(0) == '#')
					{
						value = value.replace(str, String.valueOf(getTableValue(str, level)));
					}
				}
			}
			if (ch == '#')
			{
				Object tableVal = getTableValue(value, level);
				Number parsedVal = parseNumber(tableVal.toString());
				set.set(name, parsedVal == null ? tableVal : String.valueOf(parsedVal));
			}
			else if ((Character.isDigit(ch) || ch == '-') && !value.contains(" ") && !value.contains(";"))
			{
				set.set(name, String.valueOf(parseNumber(value)));
			}
			else
			{
				set.set(name, value);
			}
		}
		catch (DOMException e)
		{
			_log.warn(n.getAttributes().getNamedItem("name") + " " + set.get("skill_id"), e);
		}
	}

	protected Number parseNumber(String value)
	{
		if (value.charAt(0) == '#')
		{
			value = getTableValue(value).toString();
		}
		try
		{
			if (value.equalsIgnoreCase("max"))
			{
				return Double.POSITIVE_INFINITY;
			}
			if (value.equalsIgnoreCase("min"))
			{
				return Double.NEGATIVE_INFINITY;
			}

			if (value.indexOf('.') == -1)
			{
				int radix = 10;
				if (value.length() > 2 && value.substring(0, 2).equalsIgnoreCase("0x"))
				{
					value = value.substring(2);
					radix = 16;
				}
				return Integer.valueOf(value, radix);
			}
			return Double.valueOf(value);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	protected Condition joinAnd(Condition cond, Condition c)
	{
		if (cond == null)
		{
			return c;
		}
		if (cond instanceof ConditionLogicAnd)
		{
			((ConditionLogicAnd) cond).add(c);
			return cond;
		}
		ConditionLogicAnd and = new ConditionLogicAnd();
		and.add(cond);
		and.add(c);
		return and;
	}
}