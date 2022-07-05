package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.instancemanager.HellboundManager;
import l2f.gameserver.instancemanager.ServerVariables;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

/**
 * @author Diamond changed Magistr & KilRoy
 */
public class _131_BirdInACage extends Quest implements ScriptFile
{
	// NPC's
	private static int KANIS = 32264;
	private static int PARME = 32271;
	// ITEMS
	private static int KANIS_ECHO_CRY = 9783;
	private static int PARMES_LETTER = 9784;

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

	public _131_BirdInACage()
	{
		super(false);

		addStartNpc(KANIS);
		addTalkId(PARME);

		addQuestItem(KANIS_ECHO_CRY);
		addQuestItem(PARMES_LETTER);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int cond = st.getCond();
		String htmltext = event;

		if (event.equals("priest_kanis_q0131_04.htm") && cond == 0)
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("priest_kanis_q0131_12.htm") && cond == 1)
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(KANIS_ECHO_CRY, 1);
		}
		else if (event.equals("parme_131y_q0131_04.htm") && cond == 2)
		{
			st.setCond(3);
			st.giveItems(PARMES_LETTER, 1);
			st.playSound(SOUND_MIDDLE);
			st.getPlayer().teleToLocation(143472 + Rnd.get(-100, 100), 191040 + Rnd.get(-100, 100), -3696);
		}
		else if (event.equals("priest_kanis_q0131_17.htm") && cond == 3)
		{
			st.playSound(SOUND_MIDDLE);
			st.takeItems(PARMES_LETTER, -1);
		}
		else if (event.equals("priest_kanis_q0131_19.htm") && cond == 3)
		{
			st.playSound(SOUND_FINISH);
			st.takeItems(KANIS_ECHO_CRY, -1);
			st.addExpAndSp(250677, 25019);
			st.exitCurrentQuest(false);
			if (HellboundManager.getHellboundLevel() == 0)
			{
				ServerVariables.set("HellboundConfidence", 1);
			}
		}
		else if (event.equals("meet") && cond == 2)
		{
			st.getPlayer().teleToLocation(153736, 142056, -9744);
		}

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if (npcId == KANIS)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 78)
				{
					htmltext = "priest_kanis_q0131_01.htm";
				}
				else
				{
					htmltext = "priest_kanis_q0131_02.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				htmltext = "priest_kanis_q0131_05.htm";
				break;
			case 2:
				htmltext = "priest_kanis_q0131_13.htm";
				break;
			case 3:
				if (st.getQuestItemsCount(PARMES_LETTER) > 0)
				{
					htmltext = "priest_kanis_q0131_16.htm";
				}
				else
				{
					htmltext = "priest_kanis_q0131_17.htm";
				}
				break;
			default:
				break;
			}
		}
		else if (npcId == PARME && cond == 2)
		{
			htmltext = "parme_131y_q0131_02.htm";
		}

		return htmltext;
	}
}