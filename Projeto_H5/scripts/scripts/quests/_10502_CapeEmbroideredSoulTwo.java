package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _10502_CapeEmbroideredSoulTwo extends Quest implements ScriptFile
{
	// NPC's
	private static final int OLF_ADAMS = 32612;
	// Mob's
	private static final int FREYA_NORMAL = 29179;
	private static final int FREYA_HARD = 29180;
	// Quest Item's
	private static final int SOUL_FREYA = 21723;
	// Item's
	private static final int CLOAK_FREYA = 21720;

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

	public _10502_CapeEmbroideredSoulTwo()
	{
		super(PARTY_ALL);
		addStartNpc(OLF_ADAMS);
		addTalkId(OLF_ADAMS);
		addKillId(FREYA_NORMAL, FREYA_HARD);
		addQuestItem(SOUL_FREYA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("olf_adams_q10502_02.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		switch (cond)
		{
		case 0:
			if (st.getPlayer().getLevel() >= 82)
			{
				htmltext = "olf_adams_q10502_01.htm";
			}
			else
			{
				htmltext = "olf_adams_q10502_00.htm";
				st.exitCurrentQuest(true);
			}
			break;
		case 1:
			htmltext = "olf_adams_q10502_03.htm";
			break;
		case 2:
			if (st.getQuestItemsCount(SOUL_FREYA) < 20)
			{
				st.setCond(1);
				htmltext = "olf_adams_q10502_03.htm";
			}
			else
			{
				st.takeItems(SOUL_FREYA, -1);
				st.giveItems(CLOAK_FREYA, 1);
				st.playSound(SOUND_FINISH);
				htmltext = "olf_adams_q10502_04.htm";
				st.exitCurrentQuest(false);
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
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (cond == 1 && (npcId == FREYA_NORMAL || npcId == FREYA_HARD))
		{
			if (st.getQuestItemsCount(SOUL_FREYA) < 20)
			{
				st.giveItems(SOUL_FREYA, Rnd.get(1, 3), false);
			}
			if (st.getQuestItemsCount(SOUL_FREYA) >= 20)
			{
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
			}
		}
		return null;
	}
}