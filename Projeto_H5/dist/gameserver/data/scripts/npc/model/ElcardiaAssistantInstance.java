package npc.model;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */

public final class ElcardiaAssistantInstance extends NpcInstance
{
	private final static int[][] _elcardiaBuff = new int[][]
	{
		// ID, warrior = 0, mage = 1, both = 2
		{
			6714,
			2
		}, // Wind Walk of Elcadia
			// {6715, 0}, // Haste of Elcadia
			// {6716, 0}, // Might of Elcadia
			// {6717, 1}, // Berserker Spirit of Elcadia
			// {6718, 0}, // Death Whisper of Elcadia
		{
			6719,
			1
		}, // Guidance of Elcadia
		{
			6720,
			2
		}, // Focus of Elcadia
		{
			6721,
			0
		}, // Empower of Elcadia
		{
			6722,
			0
		}, // Acumen of Elcadia
		{
			6723,
			0
		}, // Concentration of Elcadia
			// {6727, 2}, // Vampiric Rage of Elcadia
			// {6729, 2}, // Resist Holy of Elcadia

	};

	public ElcardiaAssistantInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.equalsIgnoreCase("request_blessing"))
		{
			// temporary implementation
			List<Creature> target = new ArrayList<Creature>();
			target.add(player);
			for (int[] buff : _elcardiaBuff)
			{
				callSkill(SkillTable.getInstance().getInfo(buff[0], 1), target, true);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}
