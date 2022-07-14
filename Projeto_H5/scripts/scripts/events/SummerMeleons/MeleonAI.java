package events.SummerMeleons;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.reward.RewardData;
import l2mv.gameserver.model.reward.RewardItem;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Log;
import npc.model.MeleonInstance;

public class MeleonAI extends Fighter
{
	public class PolimorphTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			MeleonInstance actor = getActor();
			if (actor == null)
			{
				return;
			}
			SimpleSpawner spawn = null;

			try
			{
				spawn = new SimpleSpawner(NpcHolder.getInstance().getTemplate(_npcId));
				spawn.setLoc(actor.getLoc());
				NpcInstance npc = spawn.doSpawn(true);
				npc.setAI(new MeleonAI(npc));
				((MeleonInstance) npc).setSpawner(actor.getSpawner());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			_timeToUnspawn = Long.MAX_VALUE;
			actor.deleteMe();
		}
	}

	protected static final RewardData[] _dropList = new RewardData[]
	{
		new RewardData(1539, 1, 5, 15000), // Greater Healing Potion
		new RewardData(1374, 1, 3, 15000), // Greater Haste Potion

		new RewardData(4411, 1, 1, 5000), // Echo Crystal - Theme of Journey
		new RewardData(4412, 1, 1, 5000), // Echo Crystal - Theme of Battle
		new RewardData(4413, 1, 1, 5000), // Echo Crystal - Theme of Love
		new RewardData(4414, 1, 1, 5000), // Echo Crystal - Theme of Solitude
		new RewardData(4415, 1, 1, 5000), // Echo Crystal - Theme of the Feast
		new RewardData(4416, 1, 1, 5000), // Echo Crystal - Theme of Celebration
		new RewardData(4417, 1, 1, 5000), // Echo Crystal - Theme of Comedy
		new RewardData(5010, 1, 1, 5000), // Echo Crystal - Theme of Victory

		new RewardData(1458, 10, 30, 13846), // Crystal: D-Grade 1.3%
		new RewardData(1459, 10, 30, 3000), // Crystal: C-Grade 0.3%
		new RewardData(1460, 10, 30, 1000), // Crystal: B-Grade 0.1%
		new RewardData(1461, 10, 30, 600), // Crystal: A-Grade 0.06%
		new RewardData(1462, 10, 30, 360), // Crystal: S-Grade 0.036%

		new RewardData(4161, 1, 1, 5000), // Recipe: Blue Wolf Tunic
		new RewardData(4182, 1, 1, 5000), // Recipe: Great Sword
		new RewardData(4174, 1, 1, 5000), // Recipe: Zubei's Boots
		new RewardData(4166, 1, 1, 5000), // Recipe: Doom Helmet

		new RewardData(8660, 1, 1, 1000), // Demon Horns 0.1%
		new RewardData(8661, 1, 1, 1000), // Mask of Spirits 0.1%
		new RewardData(4393, 1, 1, 300), // Calculator 0.03%
		new RewardData(7836, 1, 1, 200), // Santa's Hat 0.02%
		new RewardData(5590, 1, 1, 200), // Squeaking Shoes 0.02%
		new RewardData(7058, 1, 1, 50), // Chrono Darbuka 0.005%
		new RewardData(8350, 1, 1, 50), // Chrono Maracas 0.005%
		new RewardData(5133, 1, 1, 50), // Chrono Unitus 0.005%
		new RewardData(5817, 1, 1, 50), // Chrono Campana 0.005%
		new RewardData(9140, 1, 1, 30), // Salvation Bow 0.003%

		// Призрачные аксессуары - шанс 0.01%
		new RewardData(9177, 1, 1, 100), // Teddy Bear Hat - Blessed Resurrection Effect
		new RewardData(9178, 1, 1, 100), // Piggy Hat - Blessed Resurrection Effect
		new RewardData(9179, 1, 1, 100), // Jester Hat - Blessed Resurrection Effect
		new RewardData(9180, 1, 1, 100), // Wizard's Hat - Blessed Resurrection Effect
		new RewardData(9181, 1, 1, 100), // Dapper Cap - Blessed Resurrection Effect
		new RewardData(9182, 1, 1, 100), // Romantic Chapeau - Blessed Resurrection Effect
		new RewardData(9183, 1, 1, 100), // Iron Circlet - Blessed Resurrection Effect
		new RewardData(9184, 1, 1, 100), // Teddy Bear Hat - Blessed Escape Effect
		new RewardData(9185, 1, 1, 100), // Piggy Hat - Blessed Escape Effect
		new RewardData(9186, 1, 1, 100), // Jester Hat - Blessed Escape Effect
		new RewardData(9187, 1, 1, 100), // Wizard's Hat - Blessed Escape Effect
		new RewardData(9188, 1, 1, 100), // Dapper Cap - Blessed Escape Effect
		new RewardData(9189, 1, 1, 100), // Romantic Chapeau - Blessed Escape Effect
		new RewardData(9190, 1, 1, 100), // Iron Circlet - Blessed Escape Effect
		new RewardData(9191, 1, 1, 100), // Teddy Bear Hat - Big Head
		new RewardData(9192, 1, 1, 100), // Piggy Hat - Big Head
		new RewardData(9193, 1, 1, 100), // Jester Hat - Big Head
		new RewardData(9194, 1, 1, 100), // Wizard Hat - Big Head
		new RewardData(9195, 1, 1, 100), // Dapper Hat - Big Head
		new RewardData(9196, 1, 1, 100), // Romantic Chapeau - Big Head
		new RewardData(9197, 1, 1, 100), // Iron Circlet - Big Head
		new RewardData(9198, 1, 1, 100), // Teddy Bear Hat - Firework
		new RewardData(9199, 1, 1, 100), // Piggy Hat - Firework
		new RewardData(9200, 1, 1, 100), // Jester Hat - Firework
		new RewardData(9201, 1, 1, 100), // Wizard's Hat - Firework
		new RewardData(9202, 1, 1, 100), // Dapper Hat - Firework
		new RewardData(9203, 1, 1, 100), // Romantic Chapeau - Firework
		new RewardData(9204, 1, 1, 100), // Iron Circlet - Firework

		new RewardData(9146, 1, 3, 5000), // Scroll of Guidance 0.5%
		new RewardData(9147, 1, 3, 5000), // Scroll of Death Whisper 0.5%
		new RewardData(9148, 1, 3, 5000), // Scroll of Focus 0.5%
		new RewardData(9149, 1, 3, 5000), // Scroll of Acumen 0.5%
		new RewardData(9150, 1, 3, 5000), // Scroll of Haste 0.5%
		new RewardData(9151, 1, 3, 5000), // Scroll of Agility 0.5%
		new RewardData(9152, 1, 3, 5000), // Scroll of Empower 0.5%
		new RewardData(9153, 1, 3, 5000), // Scroll of Might 0.5%
		new RewardData(9154, 1, 3, 5000), // Scroll of Wind Walk 0.5%
		new RewardData(9155, 1, 3, 5000), // Scroll of Shield 0.5%
		new RewardData(9156, 1, 3, 2000), // BSoE 0.2%
		new RewardData(9157, 1, 3, 1000), // BRES 0.1%

		new RewardData(955, 1, 1, 400), // EWD 0.04%
		new RewardData(956, 1, 1, 2000), // EAD 0.2%
		new RewardData(951, 1, 1, 300), // EWC 0.03%
		new RewardData(952, 1, 1, 1500), // EAC 0.15%
		new RewardData(947, 1, 1, 200), // EWB 0.02%
		new RewardData(948, 1, 1, 1000), // EAB 0.1%
		new RewardData(729, 1, 1, 100), // EWA 0.01%
		new RewardData(730, 1, 1, 500), // EAA 0.05%
		new RewardData(959, 1, 1, 50), // EWS 0.005%
		new RewardData(960, 1, 1, 300) // EAS 0.03%
	};

	public final static int Young_Watermelon = 13271;
	public final static int Rain_Watermelon = 13273;
	public final static int Defective_Watermelon = 13272;
	public final static int Young_Honey_Watermelon = 13275;
	public final static int Rain_Honey_Watermelon = 13277;
	public final static int Defective_Honey_Watermelon = 13276;
	public final static int Large_Rain_Watermelon = 13274;
	public final static int Large_Rain_Honey_Watermelon = 13278;

	public final static int Squash_Level_up = 4513;
	public final static int Squash_Poisoned = 4514;

	private static final String[] textOnSpawn = new String[]
	{
		"scripts.events.SummerMeleons.MeleonAI.textOnSpawn.0",
		"scripts.events.SummerMeleons.MeleonAI.textOnSpawn.1",
		"scripts.events.SummerMeleons.MeleonAI.textOnSpawn.2"
	};

	private static final String[] textOnAttack = new String[]
	{
		"Who me bites? Ah! Ouch! Hey you, now I'm going to ask you!",
		"Ha-ha-ha, I grew all the envy, look!",
		"You do muff? Get into the fruit can not!",
		"That's what you calculate their punches? Look for better targeting teachers ...",
		"Do not waste your time, I'm immortal!",
		"Ha! True pleasant sound?",
		"As long as you attack me growth, and grow up, you'll be up to two times!",
		"You beat or tickle? Can not make it ... pathetic attempts!",
		"Only musical weapon opens watermelon. Thy blunt weapon is not help!"
	};

	private static final String[] textTooFast = new String[]
	{
		"This is a blow! That's technique!",
		"Hey you! Your skills are deplorable, my grandmother fights better! Ha-ha-ha!",
		"Come on strike once more, and again!",
		"I am your house Shatal pipe!",
		"Hey, and Semyon is? A five adena? A call? Hahaha!",
		"What kind of obscenity! Come without these jokes!",
		"Show imagination, come back, what are you trample!",
		"Wake as you leave, you are quite dull and boring ..."
	};

	private static final String[] textSuccess0 = new String[]
	{
		"Watermelon grows well if the water it thoroughly, you know this secret, is not it?",
		"That's what I nectar, and there is always some slop!",
		"I see, I see, this is China, O my God, I'm a Chinese watermelon!",
		"Let's pour more, between the first and second pereryvchik small!",
		"Refueling on the fly! quite dull and boring ... "
	};

	private static final String[] textFail0 = new String[]
	{
		"Are you deaf? Nectar I need, not what you lesh!",
		"You're such a loser, and you look like a cheerful! I need nectar, pour quality, not to get shish!",
		"Once again, fail, how long can? You want me to laugh?"
	};

	private static final String[] textSuccess1 = new String[]
	{
		"Now sing! Arbuuuuuu-uh-uh!",
		"That's so good, so very good, do not stop!",
		"I rise quickly, have time to rebound? Ha!",
		"You're a master of his craft! Go on, please!"
	};

	private static final String[] textFail1 = new String[]
	{
		"Strike while the iron on the spot! Otherwise, no you gingerbread.",
		"Wally! Ignoramus! Boobies! Loser! Again you fed me slop!",
		"Let's activity changes hill, watering properly, what kind of pathetic attempts?",
		"You want me to so he died? Come Grow Right!"
	};

	private static final String[] textSuccess2 = new String[]
	{
		"There! There! Come on, and soon I will love you forever!",
		"At this rate, I will be the emperor of watermelons!",
		"Very good, I put you credit for the agricultural economy, you have the mind to grow!"
	};

	private static final String[] textFail2 = new String[]
	{
		"And you do local? Watermelon you've seen in your eyes? It is a failure!",
		"I'll give you a sign Loser of the Year, only the loser may well fail in doing so easy!",
		"Well, Feed me, huh? Normally only, not here this dubious nectar ...",
		"And you're not a terrorist event? Could you have hunger morish? What do you want?"
	};

	private static final String[] textSuccess3 = new String[]
	{
		"Life is getting better, do not be sorry lei!",
		"You taught this mom do you have a great work!",
		"And why do you have of growth? Are you? I will be very juicy watermelon!"
	};

	private static final String[] textFail3 = new String[]
	{
		"Is that water lapped the sewer? Do you understand what the nectar!",
		"Gods, save me from this sad sack, he's all the spoils!"
	};

	private static final String[] textSuccess4 = new String[]
	{
		"That's a charge! Have you slipped into nectar? There are exactly 40 degrees! Ahahaha, I get drunk!",
		"You're risking not grow watermelon, and the whole rocket! Pours, come again!"
	};

	private static final String[] textFail4 = new String[]
	{
		"Oh how I want to drink ... Nectar, please ... ",
		" Lay nectar here and see what happens!"
	};

	private int _npcId;
	private int _nectar;
	private int _tryCount;
	private long _lastNectarUse;
	private long _timeToUnspawn;

	private ScheduledFuture<?> _polimorphTask;

	private static int NECTAR_REUSE = 3000;

	public MeleonAI(NpcInstance actor)
	{
		super(actor);
		_npcId = getActor().getNpcId();
		Functions.npcSayCustomMessage(getActor(), textOnSpawn[Rnd.get(textOnSpawn.length)]);
		_timeToUnspawn = System.currentTimeMillis() + 120000;
	}

	@Override
	protected boolean thinkActive()
	{
		if (System.currentTimeMillis() > _timeToUnspawn)
		{
			_timeToUnspawn = Long.MAX_VALUE;
			if (_polimorphTask != null)
			{
				_polimorphTask.cancel(false);
				_polimorphTask = null;
			}
			MeleonInstance actor = getActor();
			actor.deleteMe();
		}

		return false;
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster)
	{
		MeleonInstance actor = getActor();
		if (actor == null || skill.getId() != 2005 || (actor.getNpcId() != Young_Watermelon && actor.getNpcId() != Young_Honey_Watermelon))
		{
			return;
		}

		switch (_tryCount)
		{
		case 0:
			_tryCount++;
			_lastNectarUse = System.currentTimeMillis();
			if (Rnd.chance(50))
			{
				_nectar++;
				Functions.npcSay(actor, textSuccess0[Rnd.get(textSuccess0.length)]);
				actor.broadcastPacket(new MagicSkillUse(actor, actor, Squash_Level_up, 1, NECTAR_REUSE, 0));
			}
			else
			{
				Functions.npcSay(actor, textFail0[Rnd.get(textFail0.length)]);
				actor.broadcastPacket(new MagicSkillUse(actor, actor, Squash_Poisoned, 1, NECTAR_REUSE, 0));
			}
			break;
		case 1:
			if (System.currentTimeMillis() - _lastNectarUse < NECTAR_REUSE)
			{
				Functions.npcSay(actor, textTooFast[Rnd.get(textTooFast.length)]);
				return;
			}
			_tryCount++;
			_lastNectarUse = System.currentTimeMillis();
			if (Rnd.chance(50))
			{
				_nectar++;
				Functions.npcSay(actor, textSuccess1[Rnd.get(textSuccess1.length)]);
				actor.broadcastPacket(new MagicSkillUse(actor, actor, Squash_Level_up, 1, NECTAR_REUSE, 0));
			}
			else
			{
				Functions.npcSay(actor, textFail1[Rnd.get(textFail1.length)]);
				actor.broadcastPacket(new MagicSkillUse(actor, actor, Squash_Poisoned, 1, NECTAR_REUSE, 0));
			}
			break;
		case 2:
			if (System.currentTimeMillis() - _lastNectarUse < NECTAR_REUSE)
			{
				Functions.npcSay(actor, textTooFast[Rnd.get(textTooFast.length)]);
				return;
			}
			_tryCount++;
			_lastNectarUse = System.currentTimeMillis();
			if (Rnd.chance(50))
			{
				_nectar++;
				Functions.npcSay(actor, textSuccess2[Rnd.get(textSuccess2.length)]);
				actor.broadcastPacket(new MagicSkillUse(actor, actor, Squash_Level_up, 1, NECTAR_REUSE, 0));
			}
			else
			{
				Functions.npcSay(actor, textFail2[Rnd.get(textFail2.length)]);
				actor.broadcastPacket(new MagicSkillUse(actor, actor, Squash_Poisoned, 1, NECTAR_REUSE, 0));
			}
			break;
		case 3:
			if (System.currentTimeMillis() - _lastNectarUse < NECTAR_REUSE)
			{
				Functions.npcSay(actor, textTooFast[Rnd.get(textTooFast.length)]);
				return;
			}
			_tryCount++;
			_lastNectarUse = System.currentTimeMillis();
			if (Rnd.chance(50))
			{
				_nectar++;
				Functions.npcSay(actor, textSuccess3[Rnd.get(textSuccess3.length)]);
				actor.broadcastPacket(new MagicSkillUse(actor, actor, Squash_Level_up, 1, NECTAR_REUSE, 0));
			}
			else
			{
				Functions.npcSay(actor, textFail3[Rnd.get(textFail3.length)]);
				actor.broadcastPacket(new MagicSkillUse(actor, actor, Squash_Poisoned, 1, NECTAR_REUSE, 0));
			}
			break;
		case 4:
			if (System.currentTimeMillis() - _lastNectarUse < NECTAR_REUSE)
			{
				Functions.npcSay(actor, textTooFast[Rnd.get(textTooFast.length)]);
				return;
			}
			_tryCount++;
			_lastNectarUse = System.currentTimeMillis();
			if (Rnd.chance(50))
			{
				_nectar++;
				Functions.npcSay(actor, textSuccess4[Rnd.get(textSuccess4.length)]);
				actor.broadcastPacket(new MagicSkillUse(actor, actor, Squash_Level_up, 1, NECTAR_REUSE, 0));
			}
			else
			{
				Functions.npcSay(actor, textFail4[Rnd.get(textFail4.length)]);
				actor.broadcastPacket(new MagicSkillUse(actor, actor, Squash_Poisoned, 1, NECTAR_REUSE, 0));
			}
			if (_npcId == Young_Watermelon)
			{
				if (_nectar < 3)
				{
					_npcId = Defective_Watermelon;
				}
				else if (_nectar == 5)
				{
					_npcId = Large_Rain_Watermelon;
				}
				else
				{
					_npcId = Rain_Watermelon;
				}
			}
			else if (_npcId == Young_Honey_Watermelon)
			{
				if (_nectar < 3)
				{
					_npcId = Defective_Honey_Watermelon;
				}
				else if (_nectar == 5)
				{
					_npcId = Large_Rain_Honey_Watermelon;
				}
				else
				{
					_npcId = Rain_Honey_Watermelon;
				}
			}

			_polimorphTask = ThreadPoolManager.getInstance().schedule(new PolimorphTask(), NECTAR_REUSE);
			break;
		}
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		MeleonInstance actor = getActor();
		if (actor != null && Rnd.chance(5))
		{
			Functions.npcSay(actor, textOnAttack[Rnd.get(textOnAttack.length)]);
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_tryCount = -1;
		MeleonInstance actor = getActor();
		if (actor == null)
		{
			return;
		}

		double dropMod = 1.5;

		switch (_npcId)
		{
		case Defective_Watermelon:
			dropMod *= 1;
			Functions.npcSay(actor, "Watermelon opens!");
			Functions.npcSay(actor, "Oho-ho! Yes, there is a pittance, try to better!");
			break;
		case Rain_Watermelon:
			dropMod *= 2;
			Functions.npcSay(actor, "Watermelon opens!");
			Functions.npcSay(actor, "Ah-ah-ah! good catch!");
			break;
		case Large_Rain_Watermelon:
			dropMod *= 4;
			Functions.npcSay(actor, "Watermelon opens!");
			Functions.npcSay(actor, "Wow! what treasures!");
			break;
		case Defective_Honey_Watermelon:
			dropMod *= 12.5;
			Functions.npcSay(actor, "Watermelon opens!");
			Functions.npcSay(actor, "Spent a lot, and fished little!");
			break;
		case Rain_Honey_Watermelon:
			dropMod *= 25;
			Functions.npcSay(actor, "Watermelon opens!");
			Functions.npcSay(actor, "Boom-boom-boom! good catch!");
			break;
		case Large_Rain_Honey_Watermelon:
			dropMod *= 50;
			Functions.npcSay(actor, "Watermelon opens!");
			Functions.npcSay(actor, "Fanfare! You opened a giant watermelon! Untold riches on earth! Catch them!");
			break;
		default:
			dropMod *= 0;
			Functions.npcSay(actor, "I did not give anything to you, if I die like this...");
			Functions.npcSay(actor, "This disgrace will cover your name forever...");
			break;
		}

		super.onEvtDead(actor);

		if (dropMod > 0)
		{
			if (_polimorphTask != null)
			{
				_polimorphTask.cancel(false);
				_polimorphTask = null;
				Log.add("SummerMeleons :: Player " + actor.getSpawner().getName() + " tried to use cheat (SquashAI clone): killed " + actor + " after polymorfing started", "illegal-actions");
				return; // при таких вариантах ничего не даем
			}

			for (RewardData d : _dropList)
			{
				List<RewardItem> itd = d.roll(null, dropMod);
				for (RewardItem i : itd)
				{
					actor.dropItem(actor.getSpawner(), i.itemId, i.count);
				}
			}
		}
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	public MeleonInstance getActor()
	{
		return (MeleonInstance) super.getActor();
	}
}