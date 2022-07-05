package quests;

import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.Location;

/**
 * @author: pchayka
 * @date: 09.06.2010
 */
public class _182_NewRecruits extends Quest implements ScriptFile
{
	// NPC's
	private static int Kekropus = 32138;
	private static int Mother_Nornil = 32239;
	// ITEMS
	private static int Ring_of_Devotion = 10124;
	private static int Red_Crescent_Earring = 10122;
	// teleport to garden w/o instance initialize
	private static final Location TELEPORT_POSITION = new Location(-119544, 87176, -12619);

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

	public _182_NewRecruits()
	{
		super(false);

		addStartNpc(Kekropus);
		addTalkId(Kekropus);
		addTalkId(Mother_Nornil);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int cond = st.getCond();
		String htmltext = event;

		if (event.equals("take") && cond == 0)
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			htmltext = "kekropus_q182_2.htm";
		}
		else if (event.equals("mother_nornil_q182_2.htm") && cond == 1)
		{
			st.giveItems(Ring_of_Devotion, 2);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		else if (event.equals("mother_nornil_q182_3.htm") && cond == 1)
		{
			st.giveItems(Red_Crescent_Earring, 2);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		else if (event.equals("EnterNornilsGarden") && cond == 1)
		{
			st.getPlayer().teleToLocation(TELEPORT_POSITION);
		}

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if (npcId == Kekropus)
		{
			if (cond == 0 && st.getPlayer().getRace() != Race.kamael && st.getPlayer().getLevel() >= 17)
			{
				htmltext = "kekropus_q182_1.htm";
			}
			else
			{
				htmltext = "kekropus_q182_1a.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (npcId == Mother_Nornil)
		{
			if (cond == 1)
			{
				htmltext = "mother_nornil_q182_1.htm";
			}
		}
		return htmltext;
	}
}