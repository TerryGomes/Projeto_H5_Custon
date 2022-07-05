package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

/**
 * @author B0nux
 * http://l2wiki.info/Способ_сборки_Лавазавра
 */
public class _901_HowLavasaurusesAreMade extends Quest implements ScriptFile
{
	// NPC's
	private static final int ROONEY = 32049; // Rooney Blacksmith of Wind
	// Item's
	private static final int TOTEM_OF_BODY = 21899; // Totem of Body
	private static final int TOTEM_OF_SPIRIT = 21900; // Totem of Spirit
	private static final int TOTEM_OF_COURAGE = 21901; // Totem of Courage
	private static final int TOTEM_OF_FORTITUDE = 21902; // Totem of Fortitude

	// Quest Item's
	private static final int LAVASAURUS_STONE_FRAGMENT = 21909; // Lavasaurus Stone Fragment
	private static final int LAVASAURUS_HEAD_FRAGMENT = 21910; // Lavasaurus Head Fragment
	private static final int LAVASAURUS_BODY_FRAGMENT = 21911; // Lavasaurus Body Fragment
	private static final int LAVASAURUS_HORN_FRAGMENT = 21912; // Lavasaurus Horn Fragment
	// Monster's
	private static final int[] KILLING_MONSTERS = new int[]
	{
		18799,
		18800,
		18801,
		18802,
		18803
	};
	// Chance's
	private static final int DROP_CHANCE = 5;

	public _901_HowLavasaurusesAreMade()
	{
		super(PARTY_ONE); // TODO: мб PARTY_ALL?
		addStartNpc(ROONEY);
		addTalkId(ROONEY);
		addQuestItem(LAVASAURUS_STONE_FRAGMENT, LAVASAURUS_HEAD_FRAGMENT, LAVASAURUS_BODY_FRAGMENT, LAVASAURUS_HORN_FRAGMENT);
		addKillId(KILLING_MONSTERS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("blacksmith_rooney_q901_03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("blacksmith_rooney_q901_12a.htm"))
		{
			st.giveItems(TOTEM_OF_BODY, 1);
			st.playSound(SOUND_FINISH);
			st.setState(COMPLETED);
			st.exitCurrentQuest(this);
		}
		else if (event.equalsIgnoreCase("blacksmith_rooney_q901_12b.htm"))
		{
			st.giveItems(TOTEM_OF_SPIRIT, 1);
			st.playSound(SOUND_FINISH);
			st.setState(COMPLETED);
			st.exitCurrentQuest(this);
		}
		else if (event.equalsIgnoreCase("blacksmith_rooney_q901_12c.htm"))
		{
			st.giveItems(TOTEM_OF_FORTITUDE, 1);
			st.playSound(SOUND_FINISH);
			st.setState(COMPLETED);
			st.exitCurrentQuest(this);
		}
		else if (event.equalsIgnoreCase("blacksmith_rooney_q901_12d.htm"))
		{
			st.giveItems(TOTEM_OF_COURAGE, 1);
			st.playSound(SOUND_FINISH);
			st.setState(COMPLETED);
			st.exitCurrentQuest(this);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == ROONEY)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 76)
				{
					if (st.isNowAvailable())
					{
						htmltext = "blacksmith_rooney_q901_01.htm";
					}
					else
					{
						htmltext = "blacksmith_rooney_q901_01n.htm";
					}
				}
				else
				{
					htmltext = "blacksmith_rooney_q901_00.htm";
				}
				break;
			case 1:
				htmltext = "blacksmith_rooney_q901_04.htm";
				break;
			case 2:
				if (st.getInt("collect") == 1)
				{
					htmltext = "blacksmith_rooney_q901_07.htm";
				}
				else if (st.haveQuestItem(LAVASAURUS_STONE_FRAGMENT, 10) && st.haveQuestItem(LAVASAURUS_HEAD_FRAGMENT, 10) && st.haveQuestItem(LAVASAURUS_BODY_FRAGMENT, 10)
							&& st.haveQuestItem(LAVASAURUS_HORN_FRAGMENT, 10))
				{
					htmltext = "blacksmith_rooney_q901_05.htm";
					st.takeAllItems(LAVASAURUS_STONE_FRAGMENT, LAVASAURUS_HEAD_FRAGMENT, LAVASAURUS_BODY_FRAGMENT, LAVASAURUS_HORN_FRAGMENT);
					st.set("collect", 1);
				}
				else
				{
					htmltext = "blacksmith_rooney_q901_06.htm";
				}
				break;
			default:
				break;
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 1)
		{
			if (!ArrayUtils.contains(KILLING_MONSTERS, npc.getNpcId()))
			{
				return null;
			}

			if (!st.haveQuestItem(LAVASAURUS_STONE_FRAGMENT, 10))
			{
				st.rollAndGive(LAVASAURUS_STONE_FRAGMENT, 1, DROP_CHANCE);
			}
			if (!st.haveQuestItem(LAVASAURUS_HEAD_FRAGMENT, 10))
			{
				st.rollAndGive(LAVASAURUS_HEAD_FRAGMENT, 1, DROP_CHANCE);
			}
			if (!st.haveQuestItem(LAVASAURUS_BODY_FRAGMENT, 10))
			{
				st.rollAndGive(LAVASAURUS_BODY_FRAGMENT, 1, DROP_CHANCE);
			}
			if (!st.haveQuestItem(LAVASAURUS_HORN_FRAGMENT, 10))
			{
				st.rollAndGive(LAVASAURUS_HORN_FRAGMENT, 1, DROP_CHANCE);
			}

			if (st.haveQuestItem(LAVASAURUS_STONE_FRAGMENT, 10) && st.haveQuestItem(LAVASAURUS_HEAD_FRAGMENT, 10) && st.haveQuestItem(LAVASAURUS_BODY_FRAGMENT, 10)
						&& st.haveQuestItem(LAVASAURUS_HORN_FRAGMENT, 10))
			{
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
			}
		}
		return null;
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