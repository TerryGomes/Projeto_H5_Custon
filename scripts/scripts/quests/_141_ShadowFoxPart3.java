package quests;

import l2mv.gameserver.instancemanager.QuestManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _141_ShadowFoxPart3 extends Quest implements ScriptFile
{
	// NPC
	private final static int NATOOLS = 30894;

	// Items
	private final static int REPORT = 10350;

	// Monsters
	private final static int CrokianWarrior = 20791;
	private final static int Farhite = 20792;
	private final static int Alligator = 20135;

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

	public _141_ShadowFoxPart3()
	{
		super(false);

		// Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
		addFirstTalkId(NATOOLS);
		addTalkId(NATOOLS);
		addQuestItem(REPORT);
		addKillId(CrokianWarrior, Farhite, Alligator);
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState qs = player.getQuestState(_140_ShadowFoxPart2.class);
		if (qs != null && qs.isCompleted() && player.getQuestState(getClass()) == null)
		{
			newQuestState(player, STARTED);
		}
		return "";
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30894-02.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30894-04.htm"))
		{
			st.setCond(2);
			st.setState(STARTED);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30894-15.htm"))
		{
			st.setCond(4);
			st.setState(STARTED);
			st.unset("talk");
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30894-18.htm"))
		{
			if (st.getInt("reward") != 1)
			{
				st.playSound(SOUND_FINISH);
				st.giveItems(ADENA_ID, 88888);
				st.addExpAndSp(278005, 17058);
				st.set("reward", "1");
				htmltext = "select.htm";
			}
			else
			{
				htmltext = "select.htm";
			}
		}
		else if (event.equalsIgnoreCase("dawn"))
		{
			Quest q1 = QuestManager.getQuest(_142_FallenAngelRequestOfDawn.class);
			if (q1 != null)
			{
				st.exitCurrentQuest(false);
				QuestState qs1 = q1.newQuestState(st.getPlayer(), STARTED);
				q1.notifyEvent("start", qs1, npc);
				return null;
			}
		}
		else if (event.equalsIgnoreCase("dusk"))
		{
			Quest q1 = QuestManager.getQuest(_143_FallenAngelRequestOfDusk.class);
			if (q1 != null)
			{
				st.exitCurrentQuest(false);
				QuestState qs1 = q1.newQuestState(st.getPlayer(), STARTED);
				q1.notifyEvent("start", qs1, npc);
				return null;
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		String htmltext = "noquest";
		switch (cond)
		{
		case 0:
			if (st.getPlayer().getLevel() >= 37)
			{
				htmltext = "30894-01.htm";
			}
			else
			{
				htmltext = "30894-00.htm";
			}
			break;
		case 1:
			htmltext = "30894-02.htm";
			break;
		case 2:
			htmltext = "30894-05.htm";
			break;
		case 3:
			if (st.getInt("talk") == 1)
			{
				htmltext = "30894-07.htm";
			}
			else
			{
				htmltext = "30894-06.htm";
				st.takeItems(REPORT, -1);
				st.set("talk", "1");
			}
			break;
		case 4:
			htmltext = "30894-16.htm";
			break;
		default:
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 2 && st.rollAndGive(REPORT, 1, 1, 30, 80 * npc.getTemplate().rateHp))
		{
			st.setCond(3);
		}
		return null;
	}
}