package quests;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.instancemanager.SpawnManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Spawner;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.MagicSkillUse;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.templates.spawn.PeriodOfDay;

public class _653_WildMaiden extends Quest implements ScriptFile
{
	// Npc
	public final int SUKI = 32013;
	public final int GALIBREDO = 30181;

	// Items
	public final int SOE = 736;

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

	public _653_WildMaiden()
	{
		super(false);

		addStartNpc(SUKI);

		addTalkId(SUKI);
		addTalkId(GALIBREDO);
	}

	private NpcInstance findNpc(int npcId, Player player)
	{
		NpcInstance instance = null;
		List<NpcInstance> npclist = new ArrayList<NpcInstance>();
		for (Spawner spawn : SpawnManager.getInstance().getSpawners(PeriodOfDay.NONE.name()))
		{
			if (spawn.getCurrentNpcId() == npcId)
			{
				instance = spawn.getLastSpawn();
				npclist.add(instance);
			}
		}

		for (NpcInstance npc : npclist)
		{
			if (player.isInRange(npc, 1600))
			{
				return npc;
			}
		}

		return instance;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();
		if (event.equalsIgnoreCase("spring_girl_sooki_q0653_03.htm"))
		{
			if (st.getQuestItemsCount(SOE) > 0)
			{
				st.setCond(1);
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
				st.takeItems(SOE, 1);
				htmltext = "spring_girl_sooki_q0653_04a.htm";
				NpcInstance n = findNpc(SUKI, player);
				n.broadcastPacket(new MagicSkillUse(n, n, 2013, 1, 20000, 0));
				st.startQuestTimer("suki_timer", 20000);
			}
		}
		else if (event.equalsIgnoreCase("spring_girl_sooki_q0653_03.htm"))
		{
			st.exitCurrentQuest(false);
			st.playSound(SOUND_GIVEUP);
		}
		else if (event.equalsIgnoreCase("suki_timer"))
		{
			NpcInstance n = findNpc(SUKI, player);
			n.deleteMe();
			htmltext = null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";

		int npcId = npc.getNpcId();
		int id = st.getState();
		if (npcId == SUKI && id == CREATED)
		{
			if (st.getPlayer().getLevel() >= 36)
			{
				htmltext = "spring_girl_sooki_q0653_01.htm";
			}
			else
			{
				htmltext = "spring_girl_sooki_q0653_01a.htm";
				st.exitCurrentQuest(false);
			}
		}
		else if (npcId == GALIBREDO && st.getCond() == 1)
		{
			htmltext = "galicbredo_q0653_01.htm";
			st.giveItems(ADENA_ID, 2883);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}
}