package ai.custom;

import java.util.List;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.Mystic;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.scripts.Functions;

public class SSQAnakim extends Mystic
{
	private static final String PLAYER_NAME = "%playerName%";

	private static final String[] chat =
	{
		"For the eternity of Einhasad!!!",
		"Dear Shillien's offspring! You are not capable of confronting us!",
		"I'll show you the real power of Einhasad!",
		"Dear Military Force of Light! Go destroy the offspring of Shillien!!!"
	};

	private static final String[] pms =
	{
		"My power's weakening.. Hurry and turn on the sealing device!!!",
		"All 4 sealing devices must be turned on!!!",
		"Lilith's attack is getting stronger! Go ahead and turn it on!",
		PLAYER_NAME + ", hold on. We're almost done!"
	};

	private long _lastChatTime = 0;
	private long _lastPMTime = 0;
	private long _lastSkillTime = 0;

	public SSQAnakim(NpcInstance actor)
	{
		super(actor);
		((NpcInstance) actor).setHasChatWindow(false);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
	}

	@Override
	protected boolean thinkActive()
	{
		if (_lastChatTime < System.currentTimeMillis())
		{
			Functions.npcSay(getActor(), chat[Rnd.get(chat.length)]);
			_lastChatTime = System.currentTimeMillis() + 12 * 1000;
		}
		if (_lastPMTime < System.currentTimeMillis())
		{
			Player player = getPlayer();
			if (player != null)
			{
				String text = pms[Rnd.get(pms.length)];
				if (text.contains(PLAYER_NAME))
				{
					text = text.replace(PLAYER_NAME, player.getName());
				}
				Functions.npcSayToPlayer(getActor(), player, text);
			}
			_lastPMTime = System.currentTimeMillis() + 20 * 1000;
		}
		if (_lastSkillTime < System.currentTimeMillis())
		{
			if (getLilith() != null)
			{
				getActor().broadcastPacket(new MagicSkillUse(getActor(), getLilith(), 6191, 1, 5000, 10));
			}
			_lastSkillTime = System.currentTimeMillis() + 6500;
		}
		return true;
	}

	private NpcInstance getLilith()
	{
		List<NpcInstance> around = getActor().getAroundNpc(1000, 300);
		if (around != null && !around.isEmpty())
		{
			for (NpcInstance npc : around)
			{
				if (npc.getNpcId() == 32715)
				{
					return npc;
				}
			}
		}
		return null;
	}

	private Player getPlayer()
	{
		Reflection reflection = getActor().getReflection();
		if (reflection == null)
		{
			return null;
		}
		List<Player> pl = reflection.getPlayers();
		if (pl.isEmpty())
		{
			return null;
		}
		return pl.get(0);
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