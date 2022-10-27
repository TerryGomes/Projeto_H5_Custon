package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2mv.gameserver.scripts.ScriptFile;

public class _281_HeadForTheHills extends Quest implements ScriptFile
{
	// NPC
	public final int Marcela = 32173;

	// Mobs
	public final int GreenGoblin = 22234;
	public final int MountainWerewolf = 22235;
	public final int MuertosArcher = 22236;
	public final int MountainFungus = 22237;
	public final int MountainWerewolfChief = 22238;
	public final int MuertosGuard = 22239;

	// QuestItem
	public final int HillsOfGoldMonsterClaw = 9796;
	// Items
	public final int ScrollOfEscape = 736;
	public final int SoulshotNoGradeforBeginners = 5789;

	// Drop Cond
	// # [ID, CHANCE]
	public final int[][] DROPLIST =
	{
		{
			GreenGoblin,
			70
		},
		{
			MountainWerewolf,
			75
		},
		{
			MuertosArcher,
			80
		},
		{
			MountainFungus,
			70
		},
		{
			MountainWerewolfChief,
			90
		},
		{
			MuertosGuard,
			90
		}
	};

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

	public _281_HeadForTheHills()
	{
		super(false);
		addStartNpc(Marcela);

		for (int[] element : DROPLIST)
		{
			addKillId(element[0]);
		}

		addQuestItem(HillsOfGoldMonsterClaw);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("zerstorer_morsell_q0281_03.htm"))
		{
			if (st.getCond() == 0)
			{
				st.setCond(1);
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
			}
		}
		else if (event.equalsIgnoreCase("adena"))
		{
			st.giveItems(ADENA_ID, st.getQuestItemsCount(HillsOfGoldMonsterClaw) * 50, false);
			st.takeItems(HillsOfGoldMonsterClaw, -1);
			tryGiveOneTimeRevard(st);
			htmltext = "zerstorer_morsell_q0281_06.htm";
		}
		else if (event.equalsIgnoreCase("soe"))
		{
			if (st.getQuestItemsCount(HillsOfGoldMonsterClaw) >= 50)
			{
				st.takeItems(HillsOfGoldMonsterClaw, 50);
				st.giveItems(ScrollOfEscape, 5, false);
				tryGiveOneTimeRevard(st);
				htmltext = "zerstorer_morsell_q0281_06.htm";
			}
			else
			{
				htmltext = "zerstorer_morsell_q0281_04.htm";
			}
		}
		else if (event.equalsIgnoreCase("zerstorer_morsell_q0281_09.htm"))
		{
			st.exitCurrentQuest(true);
		}

		return htmltext;
	}

	private void tryGiveOneTimeRevard(QuestState st)
	{
		if (st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("p1q2"))
		{
			st.getPlayer().setVar("p1q2", "1", -1);
			st.getPlayer().sendPacket(new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n                  Go find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
			QuestState qs = st.getPlayer().getQuestState(_255_Tutorial.class);
			if (qs != null && qs.getInt("Ex") != 10)
			{
				st.showQuestionMark(26);
				qs.set("Ex", "10");
				if (st.getPlayer().getClassId().isMage())
				{
					st.playTutorialVoice("tutorial_voice_027");
					st.giveItems(5790, 3000);
				}
				else
				{
					st.playTutorialVoice("tutorial_voice_026");
					st.giveItems(5789, 6000);
				}
			}
		}
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		int cond = 0;
		if (id != CREATED)
		{
			cond = st.getCond();
		}
		if (npcId == Marcela)
		{
			if (st.getPlayer().getLevel() < 6)
			{
				htmltext = "zerstorer_morsell_q0281_02.htm";
				st.exitCurrentQuest(true);
			}
			else if (cond == 0)
			{
				htmltext = "zerstorer_morsell_q0281_01.htm";
			}
			else if (cond == 1 && st.getQuestItemsCount(HillsOfGoldMonsterClaw) > 0)
			{
				htmltext = "zerstorer_morsell_q0281_05.htm";
			}
			else
			{
				htmltext = "zerstorer_morsell_q0281_03.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (cond != 1)
		{
			return null;
		}
		for (int[] element : DROPLIST)
		{
			if (npcId == element[0])
			{
				st.rollAndGive(HillsOfGoldMonsterClaw, 1, element[1]);
				return null;
			}
		}
		return null;
	}
}