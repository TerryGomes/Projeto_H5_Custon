package ai.custom;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.Mystic;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.scripts.Functions;

public class SSQLilith extends Mystic
{
	private final String[] chat =
	{
		"You, such a fool! The victory over this war belongs to Shilen!!!",
		"How dare you try to contend against me in strength? Ridiculous.",
		"Anakim! In the name of Great Shilien, I will cut your throat!",
		"You cannot be the match of Lilith. I'll teach you a lesson!"
	};

	private long _lastChatTime = 0;
	private long _lastSkillTime = 0;

	public SSQLilith(NpcInstance actor)
	{
		super(actor);
		actor.setHasChatWindow(false);
	}

	@Override
	protected boolean thinkActive()
	{
		if (_lastChatTime < System.currentTimeMillis())
		{
			Functions.npcSay(getActor(), chat[Rnd.get(chat.length)]);
			_lastChatTime = System.currentTimeMillis() + 15 * 1000;
		}
		if (_lastSkillTime < System.currentTimeMillis())
		{
			Reflection ref = getActor().getReflection();
			if (ref != null)
			{
				NpcInstance anakim = null;
				for (NpcInstance npc : ref.getNpcs())
				{
					if (npc.getNpcId() == 32718)
					{
						anakim = npc;
						break;
					}
				}
				if (anakim != null)
				{
					getActor().broadcastPacket(new MagicSkillUse(getActor(), anakim, 6187, 1, 5000, 10));
				}
			}
			_lastSkillTime = System.currentTimeMillis() + 6500;
		}
		return true;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
	}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{
	}
}