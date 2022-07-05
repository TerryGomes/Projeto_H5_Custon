package quests;

import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;

public class _712_PathToBecomingALordOren extends Quest implements ScriptFile
{
	private static final int Brasseur = 35226;
	private static final int Croop = 30676;
	private static final int Marty = 30169;
	private static final int Valleria = 30176;

	private static final int NebuliteOrb = 13851;

	private static final int[] OelMahims =
	{
		20575,
		20576
	};

	private static final int OrenCastle = 4;

	public _712_PathToBecomingALordOren()
	{
		super(false);
		addStartNpc(Brasseur, Marty);
		addTalkId(Croop, Marty, Valleria);
		addQuestItem(NebuliteOrb);
		addKillId(OelMahims);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Castle castle = ResidenceHolder.getInstance().getResidence(OrenCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		Player castleOwner = castle.getOwner().getLeader().getPlayer();
		if (event.equals("brasseur_q712_03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("croop_q712_03.htm"))
		{
			st.setCond(3);
		}
		else if (event.equals("marty_q712_02.htm"))
		{
			if (isLordAvailable(3, st))
			{
				castleOwner.getQuestState(getClass()).setCond(4);
				st.setState(STARTED);
			}
		}
		else if (event.equals("valleria_q712_02.htm"))
		{
			if (isLordAvailable(4, st))
			{
				castleOwner.getQuestState(getClass()).setCond(5);
				st.exitCurrentQuest(true);
			}
		}
		else if (event.equals("croop_q712_05.htm"))
		{
			st.setCond(6);
		}
		else if (event.equals("croop_q712_07.htm"))
		{
			st.setCond(8);
		}
		else if (event.equals("brasseur_q712_06.htm"))
		{
			Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_OREN, st.getPlayer().getName());
			castle.getDominion().changeOwner(castleOwner.getClan());
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();
		Castle castle = ResidenceHolder.getInstance().getResidence(OrenCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		Player castleOwner = castle.getOwner().getLeader().getPlayer();

		switch (npcId)
		{
		case Brasseur:
			switch (cond)
			{
			case 0:
				if (castleOwner == st.getPlayer())
				{
					if (castle.getDominion().getLordObjectId() != st.getPlayer().getObjectId())
					{
						htmltext = "brasseur_q712_01.htm";
					}
					else
					{
						htmltext = "brasseur_q712_00.htm";
						st.exitCurrentQuest(true);
					}
				}
				else
				{
					htmltext = "brasseur_q712_00a.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				st.setCond(2);
				htmltext = "brasseur_q712_04.htm";
				break;
			case 2:
				htmltext = "brasseur_q712_04.htm";
				break;
			case 8:
				htmltext = "brasseur_q712_05.htm";
				break;
			default:
				break;
			}
			break;
		case Croop:
			switch (cond)
			{
			case 2:
				htmltext = "croop_q712_01.htm";
				break;
			case 3:
			case 4:
				htmltext = "croop_q712_03.htm";
				break;
			case 5:
				htmltext = "croop_q712_04.htm";
				break;
			case 6:
				htmltext = "croop_q712_05.htm";
				break;
			case 7:
				htmltext = "croop_q712_06.htm";
				break;
			case 8:
				htmltext = "croop_q712_08.htm";
				break;
			default:
				break;
			}
			break;
		case Marty:
			if (cond == 0)
			{
				if (isLordAvailable(3, st))
				{
					htmltext = "marty_q712_01.htm";
				}
				else
				{
					htmltext = "marty_q712_00.htm";
				}
			}
			break;
		case Valleria:
			if (st.getState() == STARTED && isLordAvailable(4, st))
			{
				htmltext = "valleria_q712_01.htm";
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
		if (st.getCond() == 6)
		{
			if (st.getQuestItemsCount(NebuliteOrb) < 300)
			{
				st.giveItems(NebuliteOrb, 1);
			}
			if (st.getQuestItemsCount(NebuliteOrb) >= 300)
			{
				st.setCond(7);
			}
		}
		return null;
	}

	private boolean isLordAvailable(int cond, QuestState st)
	{
		Castle castle = ResidenceHolder.getInstance().getResidence(OrenCastle);
		Clan owner = castle.getOwner();
		Player castleOwner = castle.getOwner().getLeader().getPlayer();
		if (owner != null)
		{
			if (castleOwner != null && castleOwner != st.getPlayer() && owner == st.getPlayer().getClan() && castleOwner.getQuestState(getClass()) != null && castleOwner.getQuestState(getClass()).getCond() == cond)
			{
				return true;
			}
		}
		return false;
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
