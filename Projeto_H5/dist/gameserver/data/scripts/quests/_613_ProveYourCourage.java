package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _613_ProveYourCourage extends Quest implements ScriptFile
{
	private final static int DURAI = 31377;
	private final static int KETRAS_HERO_HEKATON = 25299;

	// Quest items
	private final static int HEAD_OF_HEKATON = 7240;
	private final static int FEATHER_OF_VALOR = 7229;

	// etc
	@SuppressWarnings("unused")
	private final static int MARK_OF_VARKA_ALLIANCE1 = 7221;
	@SuppressWarnings("unused")
	private final static int MARK_OF_VARKA_ALLIANCE2 = 7222;
	private final static int MARK_OF_VARKA_ALLIANCE3 = 7223;
	private final static int MARK_OF_VARKA_ALLIANCE4 = 7224;
	private final static int MARK_OF_VARKA_ALLIANCE5 = 7225;

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

	public _613_ProveYourCourage()
	{
		super(true);

		addStartNpc(DURAI);
		addKillId(KETRAS_HERO_HEKATON);

		addQuestItem(HEAD_OF_HEKATON);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("quest_accept"))
		{
			htmltext = "elder_ashas_barka_durai_q0613_0104.htm";
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("613_3"))
		{
			if (st.getQuestItemsCount(HEAD_OF_HEKATON) >= 1)
			{
				htmltext = "elder_ashas_barka_durai_q0613_0201.htm";
				st.takeItems(HEAD_OF_HEKATON, -1);
				st.giveItems(FEATHER_OF_VALOR, 1);
				st.addExpAndSp(0, 10000);
				st.unset("cond");
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "elder_ashas_barka_durai_q0613_0106.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if (cond == 0)
		{
			if (st.getPlayer().getLevel() >= 75)
			{
				if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE3) == 1 || st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE4) == 1 || st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE5) == 1)
				{
					htmltext = "elder_ashas_barka_durai_q0613_0101.htm";
				}
				else
				{
					htmltext = "elder_ashas_barka_durai_q0613_0102.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "elder_ashas_barka_durai_q0613_0103.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (cond == 1 && st.getQuestItemsCount(HEAD_OF_HEKATON) == 0)
		{
			htmltext = "elder_ashas_barka_durai_q0613_0106.htm";
		}
		else if (cond == 2 && st.getQuestItemsCount(HEAD_OF_HEKATON) >= 1)
		{
			htmltext = "elder_ashas_barka_durai_q0613_0105.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if (npcId == KETRAS_HERO_HEKATON && st.getCond() == 1)
		{
			st.giveItems(HEAD_OF_HEKATON, 1);
			st.setCond(2);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}