package services;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.MagicSkillUse;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.tables.SkillTable;

public class SupportMagic extends Functions
{
	private final static int[][] _mageBuff = new int[][]
	{
		// minlevel maxlevel skill skilllevel
		{
			6,
			75,
			4322,
			1
		}, // windwalk
		{
			6,
			75,
			4323,
			1
		}, // shield
		{
			6,
			75,
			5637,
			1
		}, // Magic Barrier 1
		{
			6,
			75,
			4328,
			1
		}, // blessthesoul
		{
			6,
			75,
			4329,
			1
		}, // acumen
		{
			6,
			75,
			4330,
			1
		}, // concentration
		{
			6,
			75,
			4331,
			1
		}, // empower
		{
			16,
			34,
			4338,
			1
		}, // life cubic
	};

	private final static int[][] _warrBuff = new int[][]
	{
		// minlevel maxlevel skill
		{
			6,
			75,
			4322,
			1
		}, // windwalk
		{
			6,
			75,
			4323,
			1
		}, // shield
		{
			6,
			75,
			5637,
			1
		}, // Magic Barrier 1
		{
			6,
			75,
			4324,
			1
		}, // btb
		{
			6,
			75,
			4325,
			1
		}, // vampirerage
		{
			6,
			75,
			4326,
			1
		}, // regeneration
		{
			6,
			39,
			4327,
			1
		}, // haste 1
		{
			40,
			75,
			5632,
			1
		}, // haste 2
		{
			16,
			34,
			4338,
			1
		}, // life cubic
	};

	private final static int[][] _summonBuff = new int[][]
	{
		// minlevel maxlevel skill
		{
			6,
			75,
			4322,
			1
		}, // windwalk
		{
			6,
			75,
			4323,
			1
		}, // shield
		{
			6,
			75,
			5637,
			1
		}, // Magic Barrier 1
		{
			6,
			75,
			4324,
			1
		}, // btb
		{
			6,
			75,
			4325,
			1
		}, // vampirerage
		{
			6,
			75,
			4326,
			1
		}, // regeneration
		{
			6,
			75,
			4328,
			1
		}, // blessthesoul
		{
			6,
			75,
			4329,
			1
		}, // acumen
		{
			6,
			75,
			4330,
			1
		}, // concentration
		{
			6,
			75,
			4331,
			1
		}, // empower
		{
			6,
			39,
			4327,
			1
		}, // haste 1
		{
			40,
			75,
			5632,
			1
		}, // haste 2
	};

	private final static int minSupLvl = 6;
	private final static int maxSupLvl = 75;

	public void getSupportMagic()
	{
		doSupportMagic(getNpc(), getSelf(), false);
	}

	public void getSupportServitorMagic()
	{
		doSupportMagic(getNpc(), getSelf(), true);
	}

	public void getProtectionBlessing()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();

		// Не выдаём блессиг протекшена ПКшникам.
		if (player == null || npc == null || !npc.isInRange(player, 1000L) || (player.getKarma() > 0))
		{
			return;
		}
		if (player.getLevel() > 39 || player.getClassId().getLevel() >= 3)
		{
			show("default/newbie_blessing_no.htm", player, npc);
			return;
		}
		npc.doCast(SkillTable.getInstance().getInfo(5182, 1), player, true);
	}

	public static void doSupportMagic(NpcInstance npc, Player player, boolean servitor)
	{
		// Prevent a cursed weapon weilder of being buffed
		if (player == null || npc == null || !npc.isInRange(player, 1000L) || player.isCursedWeaponEquipped())
		{
			return;
		}
		int lvl = player.getLevel();

		if (servitor && (player.getPet() == null || !player.getPet().isSummon()))
		{
			show("default/newbie_nosupport_servitor.htm", player, npc);
			return;
		}
		else
		{
			if (lvl < minSupLvl)
			{
				show("default/newbie_nosupport_min.htm", player, npc);
				return;
			}
			if (lvl > maxSupLvl)
			{
				show("default/newbie_nosupport_max.htm", player, npc);
				return;
			}
		}

		List<Creature> target = new ArrayList<Creature>();

		if (servitor)
		{
			target.add(player.getPet());

			for (int[] buff : _summonBuff)
			{
				if (lvl >= buff[0] && lvl <= buff[1])
				{
					npc.broadcastPacket(new MagicSkillUse(npc, player.getPet(), buff[2], buff[3], 0, 0));
					npc.callSkill(SkillTable.getInstance().getInfo(buff[2], buff[3]), target, true);
				}
			}
		}
		else
		{
			target.add(player);

			if (!player.isMageClass() || player.getTemplate().race == Race.orc)
			{
				for (int[] buff : _warrBuff)
				{
					if (lvl >= buff[0] && lvl <= buff[1])
					{
						npc.broadcastPacket(new MagicSkillUse(npc, player, buff[2], buff[3], 0, 0));
						npc.callSkill(SkillTable.getInstance().getInfo(buff[2], buff[3]), target, true);
					}
				}
			}
			else
			{
				for (int[] buff : _mageBuff)
				{
					if (lvl >= buff[0] && lvl <= buff[1])
					{
						npc.broadcastPacket(new MagicSkillUse(npc, player, buff[2], buff[3], 0, 0));
						npc.callSkill(SkillTable.getInstance().getInfo(buff[2], buff[3]), target, true);
					}
				}
			}
		}
	}

}
