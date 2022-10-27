package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.base.EnchantSkillLearn;
import l2mv.gameserver.tables.SkillTreeTable;

public class ExEnchantSkillInfo extends L2GameServerPacket
{
	private List<Integer> _routes;

	private int _id, _level, _canAdd, canDecrease;

	public ExEnchantSkillInfo(int id, int level)
	{
		this._routes = new ArrayList<Integer>();
		this._id = id;
		this._level = level;

		// skill already enchanted?
		if (this._level > 100)
		{
			this.canDecrease = 1;
			// get detail for next level
			EnchantSkillLearn esd = SkillTreeTable.getSkillEnchant(this._id, this._level + 1);

			// if it exists add it
			if (esd != null)
			{
				this.addEnchantSkillDetail(esd.getLevel());
				this._canAdd = 1;
			}

			for (EnchantSkillLearn el : SkillTreeTable.getEnchantsForChange(this._id, this._level))
			{
				this.addEnchantSkillDetail(el.getLevel());
			}
		}
		else
		{
			// not already enchanted
			for (EnchantSkillLearn esd : SkillTreeTable.getFirstEnchantsForSkill(this._id))
			{
				this.addEnchantSkillDetail(esd.getLevel());
				this._canAdd = 1;
			}
		}
	}

	public void addEnchantSkillDetail(int level)
	{
		this._routes.add(level);
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x2a);

		this.writeD(this._id);
		this.writeD(this._level);
		this.writeD(this._canAdd); // can add enchant
		this.writeD(this.canDecrease); // can decrease enchant

		this.writeD(this._routes.size());
		for (Integer route : this._routes)
		{
			this.writeD(route);
		}
	}
}