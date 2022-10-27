package ai;

import java.util.List;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.entity.SevenSigns;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.SkillTable;

public class CabaleBuffer extends DefaultAI
{
	private static final int PREACHER_FIGHTER_SKILL_ID = 4361;
	private static final int PREACHER_MAGE_SKILL_ID = 4362;
	private static final int ORATOR_FIGHTER_SKILL_ID = 4364;
	private static final int ORATOR_MAGE_SKILL_ID = 4365;

	private long _castVar = 0;
	private long _buffVar = 0;
	private static final long castDelay = 60 * 1000L;
	private static final long buffDelay = 1000L;

	/** Messages of NPCs **/
	private static final NpcString[] preacherText =
	{
		NpcString.THIS_WORLD_WILL_SOON_BE_ANNIHILATED,
		NpcString.ALL_IS_LOST__PREPARE_TO_MEET_THE_GODDESS_OF_DEATH,
		NpcString.ALL_IS_LOST__THE_PROPHECY_OF_DESTRUCTION_HAS_BEEN_FULFILLED,
		NpcString.THE_END_OF_TIME_HAS_COME__THE_PROPHECY_OF_DESTRUCTION_HAS_BEEN_FULFILLED
	};

	private static final NpcString[] oratorText =
	{
		NpcString.THE_DAY_OF_JUDGMENT_IS_NEAR,
		NpcString.THE_PROPHECY_OF_DARKNESS_HAS_BEEN_FULFILLED,
		NpcString.AS_FORETOLD_IN_THE_PROPHECY_OF_DARKNESS__THE_ERA_OF_CHAOS_HAS_BEGUN,
		NpcString.THE_PROPHECY_OF_DARKNESS_HAS_COME_TO_PASS
	};

	public CabaleBuffer(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor.isDead())
		{
			return true;
		}

		int winningCabal = SevenSigns.getInstance().getCabalHighestScore();

		if (winningCabal == SevenSigns.CABAL_NULL)
		{
			return true;
		}

		int losingCabal = SevenSigns.CABAL_NULL;

		if (winningCabal == SevenSigns.CABAL_DAWN)
		{
			losingCabal = SevenSigns.CABAL_DUSK;
		}
		else if (winningCabal == SevenSigns.CABAL_DUSK)
		{
			losingCabal = SevenSigns.CABAL_DAWN;
		}

		if (_castVar + castDelay < System.currentTimeMillis())
		{
			_castVar = System.currentTimeMillis();
			Functions.npcSay(actor, actor.getNpcId() == SevenSigns.ORATOR_NPC_ID ? oratorText[Rnd.get(oratorText.length)] : preacherText[Rnd.get(preacherText.length)]);
		}
		/**
		 * For each known player in range, cast either the positive or negative buff.
		 * <BR>
		 * The stats affected depend on the player type, either a fighter or a mystic.
		 * <BR>
		 * Curse of Destruction (Loser)
		 *  - Fighters: -25% Accuracy, -25% Effect Resistance
		 *  - Mystics: -25% Casting Speed, -25% Effect Resistance
		 *
		 * Blessing of Prophecy (Winner)
		 *  - Fighters: +25% Max Load, +25% Effect Resistance
		 *  - Mystics: +25% Magic Cancel Resist, +25% Effect Resistance
		 */
		if (_buffVar + buffDelay < System.currentTimeMillis())
		{
			_buffVar = System.currentTimeMillis();
			for (Player player : World.getAroundPlayers(actor, 300, 200))
			{
				if (player == null)
				{
					continue;
				}
				int playerCabal = SevenSigns.getInstance().getPlayerCabal(player);
				int i0 = Rnd.get(100);
				int i1 = Rnd.get(10000);
				if (playerCabal == winningCabal && actor.getNpcId() == SevenSigns.ORATOR_NPC_ID)
				{
					if (player.isMageClass())
					{
						List<Effect> effects = player.getEffectList().getEffectsBySkillId(ORATOR_MAGE_SKILL_ID);
						if (effects == null || effects.size() <= 0)
						{
							if (i1 < 1)
							{
								Functions.npcSay(actor, NpcString.I_BESTOW_UPON_YOU_A_BLESSING);
							}

							Skill skill = SkillTable.getInstance().getInfo(ORATOR_MAGE_SKILL_ID, 1);
							if (skill != null)
							{
								actor.altUseSkill(skill, player);
							}
						}
						else if (i0 < 5)
						{
							if (i1 < 500)
							{
								Functions.npcSay(actor, NpcString.S1__I_GIVE_YOU_THE_BLESSING_OF_PROPHECY, player.getName());
							}

							Skill skill = SkillTable.getInstance().getInfo(ORATOR_MAGE_SKILL_ID, 2);
							if (skill != null)
							{
								actor.altUseSkill(skill, player);
							}
						}
					}
					else
					{
						List<Effect> effects = player.getEffectList().getEffectsBySkillId(ORATOR_FIGHTER_SKILL_ID);
						if (effects == null || effects.size() <= 0)
						{
							if (i1 < 1)
							{
								Functions.npcSay(actor, NpcString.HERALD_OF_THE_NEW_ERA__OPEN_YOUR_EYES);
							}

							Skill skill = SkillTable.getInstance().getInfo(ORATOR_FIGHTER_SKILL_ID, 1);
							if (skill != null)
							{
								actor.altUseSkill(skill, player);
							}
						}
						else if (i0 < 5)
						{
							if (i1 < 500)
							{
								Functions.npcSay(actor, NpcString.S1__I_BESTOW_UPON_YOU_THE_AUTHORITY_OF_THE_ABYSS, player.getName());
							}

							Skill skill = SkillTable.getInstance().getInfo(ORATOR_FIGHTER_SKILL_ID, 2);
							if (skill != null)
							{
								actor.altUseSkill(skill, player);
							}
						}
					}
				}
				else if (playerCabal == losingCabal && actor.getNpcId() == SevenSigns.PREACHER_NPC_ID)
				{
					if (player.isMageClass())
					{
						List<Effect> effects = player.getEffectList().getEffectsBySkillId(PREACHER_MAGE_SKILL_ID);
						if (effects == null || effects.size() <= 0)
						{
							if (i1 < 1)
							{
								Functions.npcSay(actor, NpcString.YOU_DONT_HAVE_ANY_HOPE__YOUR_END_HAS_COME);
							}

							Skill skill = SkillTable.getInstance().getInfo(PREACHER_MAGE_SKILL_ID, 1);
							if (skill != null)
							{
								actor.altUseSkill(skill, player);
							}
						}
						else if (i0 < 5)
						{
							if (i1 < 500)
							{
								Functions.npcSay(actor, NpcString.A_CURSE_UPON_YOU);
							}

							Skill skill = SkillTable.getInstance().getInfo(PREACHER_MAGE_SKILL_ID, 2);
							if (skill != null)
							{
								actor.altUseSkill(skill, player);
							}
						}
					}
					else
					{
						List<Effect> effects = player.getEffectList().getEffectsBySkillId(PREACHER_FIGHTER_SKILL_ID);
						if (effects == null || effects.size() <= 0)
						{
							if (i1 < 1)
							{
								Functions.npcSay(actor, NpcString.S1__YOU_BRING_AN_ILL_WIND, player.getName());
							}

							Skill skill = SkillTable.getInstance().getInfo(PREACHER_FIGHTER_SKILL_ID, 1);
							if (skill != null)
							{
								actor.altUseSkill(skill, player);
							}
						}
						else if (i0 < 5)
						{
							if (i1 < 500)
							{
								Functions.npcSay(actor, NpcString.S1__YOU_MIGHT_AS_WELL_GIVE_UP, player.getName());
							}

							Skill skill = SkillTable.getInstance().getInfo(PREACHER_FIGHTER_SKILL_ID, 2);
							if (skill != null)
							{
								actor.altUseSkill(skill, player);
							}
						}
					}
				}
			}
		}

		return false;
	}
}