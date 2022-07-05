package l2f.gameserver.model.instances;

import l2f.gameserver.model.Player;
import l2f.gameserver.templates.npc.NpcTemplate;

public final class TrainerInstance extends NpcInstance // deprecated?
{
	public TrainerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom = "";
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}

		return "trainer/" + pom + ".htm";
	}
}
