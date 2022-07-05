package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _162_CurseOfUndergroundFortress extends Quest implements ScriptFile
{
	int BONE_FRAGMENT3 = 1158;
	int ELF_SKULL = 1159;
	int BONE_SHIELD = 625;

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

	public _162_CurseOfUndergroundFortress()
	{
		super(false);

		addStartNpc(30147);

		addTalkId(30147);

		addKillId(20033);
		addKillId(20345);
		addKillId(20371);
		addKillId(20463);
		addKillId(20464);
		addKillId(20504);

		addQuestItem(new int[]
		{
			ELF_SKULL,
			BONE_FRAGMENT3
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("30147-04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			htmltext = "30147-04.htm";
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
			if (st.getPlayer().getRace() == Race.darkelf)
			{
				htmltext = "30147-00.htm";
			}
			else if (st.getPlayer().getLevel() >= 12)
			{
				htmltext = "30147-02.htm";
			}
			else
			{
				htmltext = "30147-01.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (cond == 1 && st.getQuestItemsCount(ELF_SKULL) + st.getQuestItemsCount(BONE_FRAGMENT3) < 13)
		{
			htmltext = "30147-05.htm";
		}
		else if (cond == 2 && st.getQuestItemsCount(ELF_SKULL) + st.getQuestItemsCount(BONE_FRAGMENT3) >= 13)
		{
			htmltext = "30147-06.htm";
			st.giveItems(BONE_SHIELD, 1);
			st.addExpAndSp(22652, 1004);
			st.giveItems(ADENA_ID, 24000);
			st.takeItems(ELF_SKULL, -1);
			st.takeItems(BONE_FRAGMENT3, -1);
			st.setCond(0);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if ((npcId == 20463 || npcId == 20464 || npcId == 20504) && cond == 1 && Rnd.chance(25) && st.getQuestItemsCount(BONE_FRAGMENT3) < 10)
		{
			st.giveItems(BONE_FRAGMENT3, 1);
			if (st.getQuestItemsCount(BONE_FRAGMENT3) == 10)
			{
				st.playSound(SOUND_MIDDLE);
			}
			else
			{
				st.playSound(SOUND_ITEMGET);
			}
		}
		else if ((npcId == 20033 || npcId == 20345 || npcId == 20371) && cond == 1 && Rnd.chance(25) && st.getQuestItemsCount(ELF_SKULL) < 3)
		{
			st.giveItems(ELF_SKULL, 1);
			if (st.getQuestItemsCount(ELF_SKULL) == 3)
			{
				st.playSound(SOUND_MIDDLE);
			}
			else
			{
				st.playSound(SOUND_ITEMGET);
			}
		}
		if (st.getQuestItemsCount(BONE_FRAGMENT3) == 10 && st.getQuestItemsCount(ELF_SKULL) == 3)
		{
			st.setCond(2);
		}
		return null;
	}
}