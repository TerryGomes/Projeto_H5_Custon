package l2f.gameserver.model;

import l2f.commons.lang.reference.HardReference;
import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.tables.SkillTable;

public class DeathPenalty
{
	private static final int _skillId = 5076;
	private static final int _fortuneOfNobleseSkillId = 1325;
	private static final int _charmOfLuckSkillId = 2168;

	private HardReference<Player> _playerRef;
	private int _level;
	private boolean _hasCharmOfLuck;

	public DeathPenalty(Player player, int level)
	{
		_playerRef = player.getRef();
		_level = level;
	}

	public Player getPlayer()
	{
		return _playerRef.get();
	}

	/*
	 * For common usage
	 */
	public int getLevel()
	{
		// Some checks if admin set incorrect value at database
		if (_level > 15)
		{
			_level = 15;
		}

		if (_level < 0)
		{
			_level = 0;
		}

		return Config.ALLOW_DEATH_PENALTY_C5 ? _level : 0;
	}

	/*
	 * Used only when saving DB if admin for some reasons disabled it in config after it was enabled.
	 * In if we will use getLevel() it will be reseted to 0
	 */
	public int getLevelOnSaveDB()
	{
		if (_level > 15)
		{
			_level = 15;
		}

		if (_level < 0)
		{
			_level = 0;
		}

		return _level;
	}

	public void notifyDead(Creature killer)
	{
		if (!Config.ALLOW_DEATH_PENALTY_C5)
		{
			return;
		}

		if (_hasCharmOfLuck)
		{
			_hasCharmOfLuck = false;
			return;
		}

		if (killer == null || killer.isPlayable())
		{
			return;
		}

		Player player = getPlayer();
		if (player == null || player.getLevel() <= 9)
		{
			return;
		}

		int karmaBonus = player.getKarma() / Config.ALT_DEATH_PENALTY_C5_KARMA_PENALTY;
		if (karmaBonus < 0)
		{
			karmaBonus = 0;
		}

		if (Rnd.chance(Config.ALT_DEATH_PENALTY_C5_CHANCE + karmaBonus))
		{
			addLevel();
		}
	}

	public void restore(Player player)
	{
		Skill remove = player.getKnownSkill(_skillId);
		if (remove != null)
		{
			player.removeSkill(remove, true);
		}

		if (!Config.ALLOW_DEATH_PENALTY_C5)
		{
			return;
		}

		if (getLevel() > 0)
		{
			player.addSkill(SkillTable.getInstance().getInfo(_skillId, getLevel()), false);
			player.sendPacket(new SystemMessage2(SystemMsg.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addInteger(getLevel()));
		}
		player.sendEtcStatusUpdate();
		player.updateStats();
	}

	public void addLevel()
	{
		Player player = getPlayer();
		if (player == null || getLevel() >= 15 || player.isGM())
		{
			return;
		}

		if (getLevel() != 0)
		{
			Skill remove = player.getKnownSkill(_skillId);
			if (remove != null)
			{
				player.removeSkill(remove, true);
			}
		}

		_level++;

		player.addSkill(SkillTable.getInstance().getInfo(_skillId, getLevel()), false);
		player.sendPacket(new SystemMessage2(SystemMsg.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addInteger(getLevel()));
		player.sendEtcStatusUpdate();
		player.updateStats();
	}

	public void reduceLevel()
	{
		Player player = getPlayer();
		if (player == null || getLevel() <= 0)
		{
			return;
		}

		Skill remove = player.getKnownSkill(_skillId);
		if (remove != null)
		{
			player.removeSkill(remove, true);
		}

		_level--;

		if (getLevel() > 0)
		{
			player.addSkill(SkillTable.getInstance().getInfo(_skillId, getLevel()), false);
			player.sendPacket(new SystemMessage2(SystemMsg.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addInteger(getLevel()));
		}
		else
		{
			player.sendPacket(SystemMsg.THE_DEATH_PENALTY_HAS_BEEN_LIFTED);
		}

		player.sendEtcStatusUpdate();
		player.updateStats();
	}

	public void checkCharmOfLuck()
	{
		Player player = getPlayer();
		if (player != null)
		{
			for (Effect e : player.getEffectList().getAllEffects())
			{
				if (e.getSkill().getId() == _charmOfLuckSkillId || e.getSkill().getId() == _fortuneOfNobleseSkillId)
				{
					_hasCharmOfLuck = true;
					return;
				}
			}
		}

		_hasCharmOfLuck = false;
	}
}