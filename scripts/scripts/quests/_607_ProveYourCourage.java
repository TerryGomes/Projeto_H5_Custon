package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _607_ProveYourCourage extends Quest implements ScriptFile
{
	private final static int KADUN_ZU_KETRA = 31370;
	private final static int VARKAS_HERO_SHADITH = 25309;

	// Quest items
	private final static int HEAD_OF_SHADITH = 7235;
	private final static int TOTEM_OF_VALOR = 7219;

	// etc
	@SuppressWarnings("unused")
	private final static int MARK_OF_KETRA_ALLIANCE1 = 7211;
	@SuppressWarnings("unused")
	private final static int MARK_OF_KETRA_ALLIANCE2 = 7212;
	private final static int MARK_OF_KETRA_ALLIANCE3 = 7213;
	private final static int MARK_OF_KETRA_ALLIANCE4 = 7214;
	private final static int MARK_OF_KETRA_ALLIANCE5 = 7215;

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

	public _607_ProveYourCourage()
	{
		super(true);

		addStartNpc(KADUN_ZU_KETRA);
		addKillId(VARKAS_HERO_SHADITH);

		addQuestItem(HEAD_OF_SHADITH);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("quest_accept"))
		{
			htmltext = "elder_kadun_zu_ketra_q0607_0104.htm";
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("607_3"))
		{
			if (st.getQuestItemsCount(HEAD_OF_SHADITH) >= 1)
			{
				htmltext = "elder_kadun_zu_ketra_q0607_0201.htm";
				st.takeItems(HEAD_OF_SHADITH, -1);
				st.giveItems(TOTEM_OF_VALOR, 1);
				st.addExpAndSp(0, 10000);
				st.unset("cond");
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "elder_kadun_zu_ketra_q0607_0106.htm";
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
				if (st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE3) == 1 || st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE4) == 1 || st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE5) == 1)
				{
					htmltext = "elder_kadun_zu_ketra_q0607_0101.htm";
				}
				else
				{
					htmltext = "elder_kadun_zu_ketra_q0607_0102.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "elder_kadun_zu_ketra_q0607_0103.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (cond == 1 && st.getQuestItemsCount(HEAD_OF_SHADITH) == 0)
		{
			htmltext = "elder_kadun_zu_ketra_q0607_0106.htm";
		}
		else if (cond == 2 && st.getQuestItemsCount(HEAD_OF_SHADITH) >= 1)
		{
			htmltext = "elder_kadun_zu_ketra_q0607_0105.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if (npcId == VARKAS_HERO_SHADITH && st.getCond() == 1)
		{
			st.giveItems(HEAD_OF_SHADITH, 1);
			st.setCond(2);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}