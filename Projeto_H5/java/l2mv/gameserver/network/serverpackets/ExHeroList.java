package l2mv.gameserver.network.serverpackets;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import l2mv.gameserver.model.entity.Hero;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.templates.StatsSet;

/**
 * Format: (ch) d [SdSdSdd]
 * d: size
 * [
 * S: hero name
 * d: hero class ID
 * S: hero clan name
 * d: hero clan crest id
 * S: hero ally name
 * d: hero Ally id
 * d: count
 * ]
 */
public class ExHeroList extends L2GameServerPacket
{
	private Map<Integer, StatsSet> _heroList;

	public ExHeroList()
	{
		this._heroList = Hero.getInstance().getHeroes();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x79);

		this.writeD(this._heroList.size());
		for (StatsSet hero : this._heroList.values())
		{
			this.writeS(hero.getString(Olympiad.CHAR_NAME));
			this.writeD(hero.getInteger(Olympiad.CLASS_ID));
			this.writeS(hero.getString(Hero.CLAN_NAME, StringUtils.EMPTY));
			this.writeD(hero.getInteger(Hero.CLAN_CREST, 0));
			this.writeS(hero.getString(Hero.ALLY_NAME, StringUtils.EMPTY));
			this.writeD(hero.getInteger(Hero.ALLY_CREST, 0));
			this.writeD(hero.getInteger(Hero.COUNT));
		}
	}
}