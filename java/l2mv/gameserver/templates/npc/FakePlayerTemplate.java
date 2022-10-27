package l2mv.gameserver.templates.npc;

import java.util.HashMap;
import java.util.Map;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.templates.StatsSet;

public class FakePlayerTemplate
{
	private final int templateId;
	private final String name;
	private final String title;
	private final boolean hasPvpFlag;
	private final int karma;
	private final int recommends;
	private final int nameColor;
	private final int titleColor;
	private final Race race;
	private final int sex;
	private final int classId;
	private final int hairStyle;
	private final int hairColor;
	private final int face;
	private final boolean isSitting;
	private final boolean isRunning;
	private final boolean inCombat;
	private final boolean isDead;
	private final boolean isStore;
	private final int weaponEnchant;
	private final boolean weaponGlow;
	private final int abnormal;
	private final int abnormal2;
	private final boolean isHero;
	private final int agathion;
	private final int[] cubics;
	private final TeamType team;
	private final Map<Integer, Integer> inventory;

	public FakePlayerTemplate(int templateId, String name, String title, StatsSet parameters, Map<Integer, Integer> inventory)
	{
		this.templateId = templateId;
		this.name = name;
		this.title = title;
		this.inventory = inventory;
		hasPvpFlag = parameters.getBool("hasPvpFlag");
		karma = parameters.getInteger("karma");
		recommends = parameters.getInteger("recommends");
		nameColor = parameters.getInteger("nameColor");
		titleColor = parameters.getInteger("titleColor");
		race = Race.valueOf(parameters.getString("race"));
		sex = parameters.getInteger("sex");
		classId = parameters.getInteger("classId");
		hairStyle = parameters.getInteger("hairStyle");
		hairColor = parameters.getInteger("hairColor");
		face = parameters.getInteger("face");
		isSitting = parameters.getBool("isSitting");
		isRunning = parameters.getBool("isRunning");
		inCombat = parameters.getBool("inCombat");
		isDead = parameters.getBool("isDead");
		isStore = parameters.getBool("isStore");
		weaponEnchant = parameters.getInteger("weaponEnchant");
		weaponGlow = parameters.getBool("weaponGlow");
		abnormal = parameters.getInteger("abnormal");
		abnormal2 = parameters.getInteger("abnormal2");
		isHero = parameters.getBool("isHero");
		agathion = parameters.getInteger("agathion");
		cubics = parameters.getIntegerArray("cubics");
		team = TeamType.valueOf(parameters.getString("team"));
	}

	public FakePlayerTemplate(int templateId, String name, String title, Player player)
	{
		this.templateId = templateId;
		this.name = name;
		this.title = title;
		hasPvpFlag = (player.getPvpFlag() > 0);
		karma = player.getKarma();
		recommends = player.getRecomHave();
		nameColor = player.getNameColor();
		titleColor = player.getTitleColor();
		race = player.getRace();
		sex = player.getSex();
		classId = player.getBaseClassId();
		hairStyle = player.getHairStyle();
		hairColor = player.getHairColor();
		face = player.getFace();
		isSitting = player.isSitting();
		isRunning = player.isRunning();
		inCombat = player.isInCombat();
		isDead = player.isDead();
		isStore = player.isInStoreMode();
		weaponEnchant = player.getEnchantEffect();
		weaponGlow = false;
		abnormal = player.getAbnormalEffect();
		abnormal2 = player.getAbnormalEffect2();
		isHero = player.isHero();
		agathion = 0;
		cubics = new int[0];
		team = player.getTeam();
		inventory = new HashMap<Integer, Integer>(26);
		for (int slot : Inventory.PAPERDOLL_ORDER)
		{
			final ItemInstance item = player.getInventory().getPaperdollItem(slot);
			if (item != null)
			{
				if (item.getVisualItemId() > 0)
				{
					inventory.put(slot, item.getVisualItemId());
				}
				else
				{
					inventory.put(slot, item.getItemId());
				}
			}
		}
	}

	public int getTemplateId()
	{
		return templateId;
	}

	public String getName()
	{
		return name;
	}

	public String getTitle()
	{
		return title;
	}

	public boolean isHasPvpFlag()
	{
		return hasPvpFlag;
	}

	public int getKarma()
	{
		return karma;
	}

	public int getRecommends()
	{
		return recommends;
	}

	public int getNameColor()
	{
		return nameColor;
	}

	public int getTitleColor()
	{
		return titleColor;
	}

	public Race getRace()
	{
		return race;
	}

	public int getSex()
	{
		return sex;
	}

	public int getClassId()
	{
		return classId;
	}

	public int getHairStyle()
	{
		return hairStyle;
	}

	public int getHairColor()
	{
		return hairColor;
	}

	public int getFace()
	{
		return face;
	}

	public boolean isSitting()
	{
		return isSitting;
	}

	public boolean isRunning()
	{
		return isRunning;
	}

	public boolean isInCombat()
	{
		return inCombat;
	}

	public boolean isDead()
	{
		return isDead;
	}

	public boolean isStore()
	{
		return isStore;
	}

	public int getWeaponEnchant()
	{
		return weaponEnchant;
	}

	public boolean getWeaponGlow()
	{
		return weaponGlow;
	}

	public int getAbnormal()
	{
		return abnormal;
	}

	public int getAbnormal2()
	{
		return abnormal2;
	}

	public boolean isHero()
	{
		return isHero;
	}

	public int getAgathion()
	{
		return agathion;
	}

	public int[] getCubics()
	{
		return cubics;
	}

	public TeamType getTeam()
	{
		return team;
	}

	public Map<Integer, Integer> getInventory()
	{
		return inventory;
	}
}
