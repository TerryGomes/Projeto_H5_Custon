package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _186_ContractExecution extends Quest implements ScriptFile
{
	private static final int Luka = 31437;
	private static final int Lorain = 30673;
	private static final int Nikola = 30621;

	private static final int Certificate = 10362;
	private static final int MetalReport = 10366;
	private static final int Accessory = 10367;

	private static final int LetoLizardman = 20577;
	private static final int LetoLizardmanArcher = 20578;
	private static final int LetoLizardmanSoldier = 20579;
	private static final int LetoLizardmanWarrior = 20580;
	private static final int LetoLizardmanShaman = 20581;
	private static final int LetoLizardmanOverlord = 20582;
	private static final int TimakOrc = 20583;

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

	public _186_ContractExecution()
	{
		super(false);

		addTalkId(Luka, Nikola, Lorain);
		addFirstTalkId(Lorain);
		addKillId(LetoLizardman, LetoLizardmanArcher, LetoLizardmanSoldier, LetoLizardmanWarrior, LetoLizardmanShaman, LetoLizardmanOverlord, TimakOrc);
		addQuestItem(Certificate, MetalReport, Accessory);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("researcher_lorain_q0186_03.htm"))
		{
			st.playSound(SOUND_ACCEPT);
			st.setCond(1);
			st.takeItems(Certificate, -1);
			st.giveItems(MetalReport, 1);
		}
		else if (event.equalsIgnoreCase("maestro_nikola_q0186_03.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("blueprint_seller_luka_q0186_06.htm"))
		{
			st.giveItems(ADENA_ID, 105083);
			st.addExpAndSp(285935, 18711);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (st.getState() == STARTED)
		{
			switch (npcId)
			{
			case Lorain:
				if (cond == 0)
				{
					if (st.getPlayer().getLevel() < 41)
					{
						htmltext = "researcher_lorain_q0186_02.htm";
					}
					else
					{
						htmltext = "researcher_lorain_q0186_01.htm";
					}
				}
				else if (cond == 1)
				{
					htmltext = "researcher_lorain_q0186_04.htm";
				}
				break;
			case Nikola:
				if (cond == 1)
				{
					htmltext = "maestro_nikola_q0186_01.htm";
				}
				else if (cond == 2)
				{
					htmltext = "maestro_nikola_q0186_04.htm";
				}
				break;
			case Luka:
				if (st.getQuestItemsCount(Accessory) <= 0)
				{
					htmltext = "blueprint_seller_luka_q0186_01.htm";
				}
				else
				{
					htmltext = "blueprint_seller_luka_q0186_02.htm";
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
		if (st.getState() == STARTED && st.getQuestItemsCount(Accessory) <= 0 && st.getCond() == 2 && Rnd.get(5) == 0)
		{
			st.playSound(SOUND_MIDDLE);
			st.giveItems(Accessory, 1);
		}
		return null;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState qs = player.getQuestState(_184_NikolasCooperationContract.class);
		if (qs != null && qs.isCompleted() && player.getQuestState(getClass()) == null)
		{
			newQuestState(player, STARTED);
		}
		return "";
	}
}