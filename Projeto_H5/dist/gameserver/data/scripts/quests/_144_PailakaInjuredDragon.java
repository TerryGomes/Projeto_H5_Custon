package quests;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.ReflectionUtils;

public class _144_PailakaInjuredDragon extends Quest implements ScriptFile
{
	// NPC
	private static final int KETRAOSHAMAN = 32499;
	private static final int KOSUPPORTER = 32502;
	private static final int KOIO = 32509;
	private static final int KOSUPPORTER2 = 32512;

	private static final int VSWARRIOR1 = 18636;
	private static final int VSWARRIOR2 = 18642;
	private static final int VSCOMMAO1 = 18646;
	private static final int VSCOMMAO2 = 18654;
	private static final int VSGMAG1 = 18649;
	private static final int VSGMAG2 = 18650;
	private static final int VSHGAPG1 = 18655;
	private static final int VSHGAPG2 = 18657;

	private static final int[] Pailaka3rd = new int[]
	{
		18635,
		VSWARRIOR1,
		18638,
		18639,
		18640,
		18641,
		VSWARRIOR2,
		18644,
		18645,
		VSCOMMAO1,
		18648,
		VSGMAG1,
		VSGMAG2,
		18652,
		18653,
		VSCOMMAO2,
		VSHGAPG1,
		18656,
		VSHGAPG2,
		18658,
		18659
	};

	private static final int[] Antelopes = new int[]
	{
		18637,
		18643,
		18647,
		18651
	};

	// BOSS
	private static final int LATANA = 18660;

	// ITEMS
	private static final int ScrollOfEscape = 736;
	private static final int SPEAR = 13052;
	private static final int ENCHSPEAR = 13053;
	private static final int LASTSPEAR = 13054;
	private static final int STAGE1 = 13056;
	private static final int STAGE2 = 13057;

	private static final int[] PAILAKA3DROP =
	{
		8600,
		8601,
		8603,
		8604
	};
	private static final int[] ANTELOPDROP =
	{
		13032,
		13033
	};

	// REWARDS
	private static final int PSHIRT = 13296;

	private static final int[][] BUFFS =
	{
		{
			4357,
			2
		}, // Haste Lv2
		{
			4342,
			2
		}, // Wind Walk Lv2
		{
			4356,
			3
		}, // Empower Lv3
		{
			4355,
			3
		}, // Acumen Lv3
		{
			4351,
			6
		}, // Concentration Lv6
		{
			4345,
			3
		}, // Might Lv3
		{
			4358,
			3
		}, // Guidance Lv3
		{
			4359,
			3
		}, // Focus Lv3
		{
			4360,
			3
		}, // Death Wisper Lv3
		{
			4352,
			2
		}, // Berserker Spirit Lv2
		{
			4354,
			4
		}, // Vampiric Rage Lv4
		{
			4347,
			6
		} // Blessed Body Lv6
	};

	private static final int izId = 45;

	public _144_PailakaInjuredDragon()
	{
		super(false);

		addStartNpc(KETRAOSHAMAN);
		addTalkId(KOSUPPORTER, KOIO, KOSUPPORTER2);
		addAttackId(LATANA, VSWARRIOR1, VSWARRIOR2, VSCOMMAO1, VSCOMMAO2, VSGMAG1, VSGMAG2, VSHGAPG1, VSHGAPG2);
		addKillId(LATANA);
		addKillId(Pailaka3rd);
		addKillId(Antelopes);
		addQuestItem(STAGE1, STAGE2, SPEAR, ENCHSPEAR, LASTSPEAR, 13033, 13032);
	}

	private void makeBuff(NpcInstance npc, Player player, int skillId, int level)
	{
		List<Creature> target = new ArrayList<Creature>();
		target.add(player);
		npc.broadcastPacket(new MagicSkillUse(npc, player, skillId, level, 0, 0));
		npc.callSkill(SkillTable.getInstance().getInfo(skillId, level), target, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if (event.equalsIgnoreCase("Enter"))
		{
			enterInstance(player);
			return null;
		}
		else if (event.startsWith("buff"))
		{
			int[] skill = BUFFS[Integer.parseInt(event.split("buff")[1])];
			if (st.getInt("spells") < 4)
			{
				makeBuff(npc, player, skill[0], skill[1]);
				st.set("spells", "" + (st.getInt("spells") + 1));
				htmltext = "32509-06.htm";
				return htmltext;
			}
			if (st.getInt("spells") == 4)
			{
				makeBuff(npc, player, skill[0], skill[1]);
				st.set("spells", "5");
				htmltext = "32509-05.htm";
				return htmltext;
			}
		}
		else if (event.equalsIgnoreCase("Support"))
		{
			if (st.getInt("spells") < 5)
			{
				htmltext = "32509-06.htm";
			}
			else
			{
				htmltext = "32509-04.htm";
			}
			return htmltext;
		}
		else if (event.equalsIgnoreCase("32499-02.htm"))
		{
			st.set("spells", "0");
			st.set("stage", "1");
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("32499-05.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("32502-05.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(SPEAR, 1);
		}
		else if (event.equalsIgnoreCase("32512-02.htm"))
		{
			st.takeItems(SPEAR, 1);
			st.takeItems(ENCHSPEAR, 1);
			st.takeItems(LASTSPEAR, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		int id = st.getState();
		Player player = st.getPlayer();
		switch (npcId)
		{
		case KETRAOSHAMAN:
			if (cond == 0)
			{
				if (player.getLevel() < 73 || player.getLevel() > 77)
				{
					htmltext = "32499-no.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					return "32499-01.htm";
				}
			}
			else if (id == COMPLETED)
			{
				htmltext = "32499-no.htm";
			}
			else if (cond == 1 || cond == 2 || cond == 3)
			{
				htmltext = "32499-06.htm";
			}
			else
			{
				htmltext = "32499-07.htm";
			}
			break;
		case KOSUPPORTER:
			if (cond == 1 || cond == 2)
			{
				htmltext = "32502-01.htm";
			}
			else
			{
				htmltext = "32502-05.htm";
			}
			break;
		case KOIO:
			if (st.getQuestItemsCount(SPEAR) > 0 && st.getQuestItemsCount(STAGE1) == 0)
			{
				htmltext = "32509-01.htm";
			}
			if (st.getQuestItemsCount(ENCHSPEAR) > 0 && st.getQuestItemsCount(STAGE2) == 0)
			{
				htmltext = "32509-01.htm";
			}
			if (st.getQuestItemsCount(SPEAR) == 0 && st.getQuestItemsCount(STAGE1) > 0)
			{
				htmltext = "32509-07.htm";
			}
			if (st.getQuestItemsCount(ENCHSPEAR) == 0 && st.getQuestItemsCount(STAGE2) > 0)
			{
				htmltext = "32509-07.htm";
			}
			if (st.getQuestItemsCount(SPEAR) == 0 && st.getQuestItemsCount(ENCHSPEAR) == 0)
			{
				htmltext = "32509-07.htm";
			}
			if (st.getQuestItemsCount(STAGE1) == 0 && st.getQuestItemsCount(STAGE2) == 0)
			{
				htmltext = "32509-01.htm";
			}
			if (st.getQuestItemsCount(SPEAR) > 0 && st.getQuestItemsCount(STAGE1) > 0)
			{
				st.takeItems(SPEAR, 1);
				st.takeItems(STAGE1, 1);
				st.giveItems(ENCHSPEAR, 1);
				htmltext = "32509-02.htm";
			}
			if (st.getQuestItemsCount(ENCHSPEAR) > 0 && st.getQuestItemsCount(STAGE2) > 0)
			{
				st.takeItems(ENCHSPEAR, 1);
				st.takeItems(STAGE2, 1);
				st.giveItems(LASTSPEAR, 1);
				htmltext = "32509-03.htm";
			}
			if (st.getQuestItemsCount(LASTSPEAR) > 0)
			{
				htmltext = "32509-03.htm";
			}
			break;
		case KOSUPPORTER2:
			if (cond == 4)
			{
				st.giveItems(ScrollOfEscape, 1);
				st.giveItems(PSHIRT, 1);
				st.addExpAndSp(28000000, 2850000);
				st.setCond(5);
				st.setState(COMPLETED);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
				player.setVitality(Config.VITALITY_LEVELS[4]);
				player.getReflection().startCollapseTimer(60000);
				htmltext = "32512-01.htm";
			}
			else if (id == COMPLETED)
			{
				htmltext = "32512-03.htm";
			}
			break;
		default:
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int refId = player.getReflectionId();

		switch (npcId)
		{
		case VSWARRIOR1:
		case VSWARRIOR2:
			if (st.getInt("stage") == 1)
			{
				st.set("stage", "2");
			}
			break;
		case VSCOMMAO1:
		case VSCOMMAO2:
			if (st.getInt("stage") == 2)
			{
				st.set("stage", "3");
			}
			if (st.getQuestItemsCount(SPEAR) > 0 && st.getQuestItemsCount(STAGE1) == 0)
			{
				st.giveItems(STAGE1, 1);
			}
			break;
		case VSGMAG1:
		case VSGMAG2:
			if (st.getInt("stage") == 3)
			{
				st.set("stage", "4");
			}
			if (st.getQuestItemsCount(ENCHSPEAR) > 0 && st.getQuestItemsCount(STAGE2) == 0)
			{
				st.giveItems(STAGE2, 1);
			}
			break;
		case VSHGAPG1:
		case VSHGAPG2:
			if (st.getInt("stage") == 4)
			{
				st.set("stage", "5");
			}
			break;
		case LATANA:
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
			addSpawnToInstance(KOSUPPORTER2, npc.getLoc(), 0, refId);
			break;
		}

		if (ArrayUtils.contains(Pailaka3rd, npcId))
		{
			if (Rnd.get(100) < 30)
			{
				st.dropItem(npc, PAILAKA3DROP[Rnd.get(PAILAKA3DROP.length)], 1);
			}
		}

		if (ArrayUtils.contains(Antelopes, npcId))
		{
			st.dropItem(npc, ANTELOPDROP[Rnd.get(ANTELOPDROP.length)], Rnd.get(1, 10));
		}

		return null;
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		switch (npcId)
		{
		case VSCOMMAO1:
		case VSCOMMAO2:
			if (st.getInt("stage") < 2)
			{
				player.teleToLocation(122789, -45692, -3036);
				return null;
			}
			break;
		case VSGMAG1:
		case VSGMAG2:
			if (st.getInt("stage") == 1)
			{
				player.teleToLocation(122789, -45692, -3036);
				return null;
			}
			else if (st.getInt("stage") == 2)
			{
				player.teleToLocation(116948, -46445, -2673);
				return null;
			}
			break;
		case VSHGAPG1:
		case VSHGAPG2:
			if (st.getInt("stage") == 1)
			{
				player.teleToLocation(122789, -45692, -3036);
				return null;
			}
			else if (st.getInt("stage") == 2)
			{
				player.teleToLocation(116948, -46445, -2673);
				return null;
			}
			else if (st.getInt("stage") == 3)
			{
				player.teleToLocation(112445, -44118, -2700);
				return null;
			}
			break;
		case LATANA:
			if (st.getInt("stage") == 1)
			{
				player.teleToLocation(122789, -45692, -3036);
				return null;
			}
			else if (st.getInt("stage") == 2)
			{
				player.teleToLocation(116948, -46445, -2673);
				return null;
			}
			else if (st.getInt("stage") == 3)
			{
				player.teleToLocation(112445, -44118, -2700);
				return null;
			}
			else if (st.getInt("stage") == 4)
			{
				player.teleToLocation(109947, -41433, -2311);
				return null;
			}
			break;
		}
		return null;
	}

	private void enterInstance(Player player)
	{
		Reflection r = player.getActiveReflection();
		if (r != null)
		{
			if (player.canReenterInstance(izId))
			{
				player.teleToLocation(r.getTeleportLoc(), r);
			}
		}
		else if (player.canEnterInstance(izId))
		{
			ReflectionUtils.enterReflection(player, izId);
		}
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}