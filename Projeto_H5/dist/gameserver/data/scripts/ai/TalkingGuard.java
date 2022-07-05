package ai;

import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.ai.Guard;
import l2f.gameserver.geodata.GeoEngine;
import l2f.gameserver.model.AggroList;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Functions;

public class TalkingGuard extends Guard
{
	private boolean _crazyState;
	private long _lastAggroSay;
	private long _lastNormalSay;
	private static final int _crazyChance = Config.TalkGuardChance;
	private static final int _sayNormalChance = Config.TalkNormalChance;
	private static final long _sayNormalPeriod = Config.TalkNormalPeriod * 6000;
	private static final long _sayAggroPeriod = Config.TalkAggroPeriod * 6000;

	// Phrases that can pronounce Guard, when it begins to attack PCs
	private static final String[] _sayAggroText =
	{
		"Do not walk away from me {name}, don't make me or have to kill you!",
		"{name}, Losing in PvP again?",
		"La-la-la, I'm crazy.. Who will I kill next?",
		"You dare step on my shoe, {name}, You just made my list.. of people to kill!",
		"I'm terrified of this administrator.. Do you know what I mean {name}?",
		"I stand here cold.. alone.. protecting this great city.. Monsters are lurking about.",
		"Dear future victom.. I win you lose Muahaha.",
		"Hey {name} did you know guards have a really high suicide rate?",
		"{name}, Halt! Stand and deliver.",
		"{name}, Just die already.. Bsoe is for losers.",
		"{name}, How would you like to die? Quickly and easily, or slowly and painfully?",
		"{name}, PvP with me or are you just pissed?",
		"{name}, I'll kill you softly!",
		"{name}, I eat the hearts of my victoms!",
		"Prepare to die, {name}!",
		"{name}, You fight like a girl!",
		"{name}, You should start praying when you see me coming.. Although by then it will be too late."
	};
	// Phrases that can pronounce Guard, addressing them passing by male players
	private static final String[] _sayNormalTextM =
	{
		"{name}, Who goes there?",
		"{name}, Hello!",
		"{name}, Hi!",
		"{name}, Hello Ugly.",
		"{name}, What a beautiful day.",
		"{name}, Have a successful hunt.",
		"{name}, May the force be with you.",
		"{name}, Love to vote for Server... Better than my last employers.",
		"{name}, You make me dream of nightmares.",
		"{name}, I know you - you're the one they talk about... The great slayer of innocent monsters.",
		"{name}, PvP or are just pissed?",
		"{name}, Hey buddy you dropped your purse.",
		"{name}, I will never go on a date with you.. Although is your sister available?",
		"{name}, Vote for Server!"
	};
	// Phrases that can pronounce Guard, addressing them passing by female players
	private static final String[] _sayNormalTextF =
	{
		"{name}, Hello Beautiful!",
		"{name}, Wow.. Wait!! Theif!! {name} stole my heart!!",
		"{name}, Walk away from me?? What too afraid to hangout with a real man?",
		"{name}, Hi!",
		"{name}, Just voted and got some cool things man.. You vote yet?",
		"{name}, Women... need to stay in the kitchen am I right??",
		"{name}, Hey! You stop!! You're the one sleeping with my wife?! Prepare for death!!",
		"{name}, Damn what a nice A**.",
		"{name}, Oh what legs...",
		"{name}, Baby come sit on papa's lap.",
		"{name}, Dang what a woman... I'd have a field day with you.",
		"{name}, You free tonight babe?",
		"{name}, Do you agree Crack is wack? You don't?? Can I buy some?",
		"{name}, Hey do you have some adena? Just need a few for the bus come on."
	};

	public TalkingGuard(NpcInstance actor)
	{
		super(actor);
		MAX_PURSUE_RANGE = 600;
		_crazyState = false;
		_lastAggroSay = 0;
		_lastNormalSay = 0;
	}

	@Override
	protected void onEvtSpawn()
	{
		_lastAggroSay = 0;
		_lastNormalSay = 0;
		_crazyState = Rnd.chance(_crazyChance) ? true : false;
		super.onEvtSpawn();
	}

	@Override
	protected boolean checkAggression(Creature target, boolean avoidAttack)
	{
		if (_crazyState)
		{
			NpcInstance actor = getActor();
			Player player = target.getPlayer();
			if (actor == null || actor.isDead() || player == null || player.isGM())
			{
				return false;
			}
			if (Rnd.chance(_sayNormalChance))
			{
				if (target.isPlayer() && target.getKarma() <= 0 && (_lastNormalSay + _sayNormalPeriod < System.currentTimeMillis()) && actor.isInRange(target, 250L))
				{
					Functions.npcSay(actor, target.getPlayer().getSex() == 0 ? _sayNormalTextM[Rnd.get(_sayNormalTextM.length)].replace("{name}", target.getName())
								: _sayNormalTextF[Rnd.get(_sayNormalTextF.length)].replace("{name}", target.getName()));
					_lastNormalSay = System.currentTimeMillis();
				}
			}
			if ((target.getKarma() <= 0) || (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) || (_globalAggro < 0L))
			{
				return false;
			}
			AggroList.AggroInfo ai = actor.getAggroList().get(target);
			if (ai != null && ai.hate > 0)
			{
				if (!target.isInRangeZ(actor.getSpawnedLoc(), MAX_PURSUE_RANGE))
				{
					return false;
				}
			}
			else if (!target.isInRangeZ(actor.getSpawnedLoc(), 600))
			{
				return false;
			}
			if ((target.isPlayable() && !canSeeInSilentMove((Playable) target)) || !GeoEngine.canSeeTarget(actor, target, false))
			{
				return false;
			}
			if (target.isPlayer() && ((Player) target).isInvisible())
			{
				return false;
			}

			if (!avoidAttack)
			{
				if ((target.isSummon() || target.isPet()) && target.getPlayer() != null)
				{
					actor.getAggroList().addDamageHate(target.getPlayer(), 0, 1);
				}
				actor.getAggroList().addDamageHate(target, 0, 2);
				startRunningTask(2000);
				if (_lastAggroSay + _sayAggroPeriod < System.currentTimeMillis())
				{
					Functions.npcSay(actor, _sayAggroText[Rnd.get(_sayAggroText.length)].replace("{name}", target.getPlayer().getName()));
					_lastAggroSay = System.currentTimeMillis();
				}

				setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
			}
			return true;
		}
		else
		{
			super.checkAggression(target, avoidAttack);
		}
		return false;
	}
}