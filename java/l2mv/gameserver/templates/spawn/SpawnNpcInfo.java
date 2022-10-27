package l2mv.gameserver.templates.spawn;

import l2mv.commons.collections.MultiValueSet;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class SpawnNpcInfo
{
	private final NpcTemplate _template;
	private final int _max;
	private final MultiValueSet<String> _parameters;

	public SpawnNpcInfo(int npcId, int max, MultiValueSet<String> set)
	{
		_template = NpcHolder.getInstance().getTemplate(npcId);
		_max = max;
		_parameters = set;
	}

	public NpcTemplate getTemplate()
	{
		return _template;
	}

	public int getMax()
	{
		return _max;
	}

	public MultiValueSet<String> getParameters()
	{
		return _parameters;
	}
}
