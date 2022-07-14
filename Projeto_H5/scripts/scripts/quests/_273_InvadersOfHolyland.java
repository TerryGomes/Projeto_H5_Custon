package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2mv.gameserver.scripts.ScriptFile;

public class _273_InvadersOfHolyland extends Quest implements ScriptFile
{
	public final int BLACK_SOULSTONE = 1475;
	public final int RED_SOULSTONE = 1476;

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

	public _273_InvadersOfHolyland()
	{
		super(false);

		addStartNpc(30566);
		addKillId(new int[]
		{
			20311,
			20312,
			20313
		});
		addQuestItem(new int[]
		{
			BLACK_SOULSTONE,
			RED_SOULSTONE
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("atuba_chief_varkees_q0273_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("atuba_chief_varkees_q0273_07.htm"))
		{
			st.setCond(0);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else if (event.equals("atuba_chief_varkees_q0273_08.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
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
			if (st.getPlayer().getRace() != Race.orc)
			{
				htmltext = "atuba_chief_varkees_q0273_00.htm";
				st.exitCurrentQuest(true);
			}
			else if (st.getPlayer().getLevel() < 6)
			{
				htmltext = "atuba_chief_varkees_q0273_01.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "atuba_chief_varkees_q0273_02.htm";
				return htmltext;
			}
		}
		else if (cond > 0)
		{
			if (st.getQuestItemsCount(BLACK_SOULSTONE) == 0 && st.getQuestItemsCount(RED_SOULSTONE) == 0)
			{
				htmltext = "atuba_chief_varkees_q0273_04.htm";
			}
			else
			{
				long adena = 0;
				if (st.getQuestItemsCount(BLACK_SOULSTONE) > 0)
				{
					htmltext = "atuba_chief_varkees_q0273_05.htm";
					adena += st.getQuestItemsCount(BLACK_SOULSTONE) * 5;
				}
				if (st.getQuestItemsCount(RED_SOULSTONE) > 0)
				{
					htmltext = "atuba_chief_varkees_q0273_06.htm";
					adena += st.getQuestItemsCount(RED_SOULSTONE) * 50;
				}
				st.takeAllItems(BLACK_SOULSTONE, RED_SOULSTONE);
				st.giveItems(ADENA_ID, adena);

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

				st.exitCurrentQuest(true);
				st.playSound(SOUND_FINISH);
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch (npcId)
		{
		case 20311:
			if (cond == 1)
			{
				if (Rnd.chance(90))
				{
					st.giveItems(BLACK_SOULSTONE, 1);
				}
				else
				{
					st.giveItems(RED_SOULSTONE, 1);
				}
				st.playSound(SOUND_ITEMGET);
			}
			break;
		case 20312:
			if (cond == 1)
			{
				if (Rnd.chance(87))
				{
					st.giveItems(BLACK_SOULSTONE, 1);
				}
				else
				{
					st.giveItems(RED_SOULSTONE, 1);
				}
				st.playSound(SOUND_ITEMGET);
			}
			break;
		case 20313:
			if (cond == 1)
			{
				if (Rnd.chance(77))
				{
					st.giveItems(BLACK_SOULSTONE, 1);
				}
				else
				{
					st.giveItems(RED_SOULSTONE, 1);
				}
				st.playSound(SOUND_ITEMGET);
			}
			break;
		default:
			break;
		}
		return null;
	}
}