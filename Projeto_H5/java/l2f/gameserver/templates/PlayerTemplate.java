package l2f.gameserver.templates;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.model.base.Race;
import l2f.gameserver.templates.item.CreateItem;
import l2f.gameserver.utils.Location;

public class PlayerTemplate extends CharTemplate
{
	/** The Class<?> object of the L2Player */
	public final ClassId classId;

	public final Race race;
	public final String className;

	public final Location spawnLoc = new Location();

	public final boolean isMale;

	public final int classBaseLevel;
	public final double lvlHpAdd;
	public final double lvlHpMod;
	public final double lvlCpAdd;
	public final double lvlCpMod;
	public final double lvlMpAdd;
	public final double lvlMpMod;

	private List<CreateItem> _items;

	public PlayerTemplate(int id, StatsSet set, boolean isMale, List<CreateItem> items)
	{
		super(set);
		classId = ClassId.VALUES[id];
		race = Race.values()[set.getInteger("raceId")];
		className = set.getString("name");

		spawnLoc.set(new Location(set.getInteger("spawnX"), set.getInteger("spawnY"), set.getInteger("spawnZ")));

		this.isMale = isMale;

		classBaseLevel = set.getInteger("classBaseLevel");
		lvlHpAdd = set.getDouble("lvlHpAdd");
		lvlHpMod = set.getDouble("lvlHpMod");
		lvlCpAdd = set.getDouble("lvlCpAdd");
		lvlCpMod = set.getDouble("lvlCpMod");
		lvlMpAdd = set.getDouble("lvlMpAdd");
		lvlMpMod = set.getDouble("lvlMpMod");

		_items = new ArrayList<CreateItem>();
		_items.addAll(items);
	}

	/**
	 *
	 * @return itemIds of all the starter equipment
	 */
	public List<CreateItem> getItems()
	{
		return _items;
	}
}