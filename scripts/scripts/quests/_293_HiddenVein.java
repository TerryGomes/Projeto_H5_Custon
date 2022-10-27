package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2mv.gameserver.scripts.ScriptFile;

public class _293_HiddenVein extends Quest implements ScriptFile
{
	// NPCs
	private static int Filaur = 30535;
	private static int Chichirin = 30539;
	// Mobs
	private static int Utuku_Orc = 20446;
	private static int Utuku_Orc_Archer = 20447;
	private static int Utuku_Orc_Grunt = 20448;
	// Quest Items
	private static int Chrysolite_Ore = 1488;
	private static int Torn_Map_Fragment = 1489;
	private static int Hidden_Ore_Map = 1490;
	// Chances
	private static int Torn_Map_Fragment_Chance = 5;
	private static int Chrysolite_Ore_Chance = 45;

	public _293_HiddenVein()
	{
		super(false);
		addStartNpc(Filaur);
		addTalkId(Chichirin);
		addKillId(Utuku_Orc);
		addKillId(Utuku_Orc_Archer);
		addKillId(Utuku_Orc_Grunt);
		addQuestItem(Chrysolite_Ore);
		addQuestItem(Torn_Map_Fragment);
		addQuestItem(Hidden_Ore_Map);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		if (event.equalsIgnoreCase("elder_filaur_q0293_03.htm") && _state == CREATED)
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("elder_filaur_q0293_06.htm") && _state == STARTED)
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		else if (event.equalsIgnoreCase("chichirin_q0293_03.htm") && _state == STARTED)
		{
			if (st.getQuestItemsCount(Torn_Map_Fragment) < 4)
			{
				return "chichirin_q0293_02.htm";
			}
			st.takeItems(Torn_Map_Fragment, 4);
			st.giveItems(Hidden_Ore_Map, 1);
		}

		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int _state = st.getState();
		int npcId = npc.getNpcId();

		if (_state == CREATED)
		{
			if (npcId != Filaur)
			{
				return "noquest";
			}
			if (st.getPlayer().getRace() != Race.dwarf)
			{
				st.exitCurrentQuest(true);
				return "elder_filaur_q0293_00.htm";
			}
			if (st.getPlayer().getLevel() < 6)
			{
				st.exitCurrentQuest(true);
				return "elder_filaur_q0293_01.htm";
			}
			st.setCond(0);
			return "elder_filaur_q0293_02.htm";
		}

		if (_state != STARTED)
		{
			return "noquest";
		}

		if (npcId == Filaur)
		{
			long Chrysolite_Ore_count = st.getQuestItemsCount(Chrysolite_Ore);
			long Hidden_Ore_Map_count = st.getQuestItemsCount(Hidden_Ore_Map);
			long reward = st.getQuestItemsCount(Chrysolite_Ore) * 10 + st.getQuestItemsCount(Hidden_Ore_Map) * 1000L;
			if (reward == 0)
			{
				return "elder_filaur_q0293_04.htm";
			}

			if (Chrysolite_Ore_count > 0)
			{
				st.takeItems(Chrysolite_Ore, -1);
			}
			if (Hidden_Ore_Map_count > 0)
			{
				st.takeItems(Hidden_Ore_Map, -1);
			}
			st.giveItems(ADENA_ID, reward);

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

			return Chrysolite_Ore_count > 0 && Hidden_Ore_Map_count > 0 ? "elder_filaur_q0293_09.htm" : Hidden_Ore_Map_count > 0 ? "elder_filaur_q0293_08.htm" : "elder_filaur_q0293_05.htm";
		}

		if (npcId == Chichirin)
		{
			return "chichirin_q0293_01.htm";
		}

		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getState() != STARTED)
		{
			return null;
		}

		if (Rnd.chance(Torn_Map_Fragment_Chance))
		{
			qs.giveItems(Torn_Map_Fragment, 1);
			qs.playSound(SOUND_ITEMGET);
		}
		else if (Rnd.chance(Chrysolite_Ore_Chance))
		{
			qs.giveItems(Chrysolite_Ore, 1);
			qs.playSound(SOUND_ITEMGET);
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