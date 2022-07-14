package quests;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 */

public class _711_PathToBecomingALordInnadril extends Quest implements ScriptFile
{
	private static final int Neurath = 35316;
	private static final int IasonHeine = 30969;

	private static final int InnadrilCastle = 6;
	private static final int[] mobs =
	{
		20789,
		20790,
		20791,
		20792,
		20793,
		20804,
		20805,
		20806,
		20807,
		20808
	};

	public _711_PathToBecomingALordInnadril()
	{
		super(false);
		addStartNpc(Neurath);
		addTalkId(IasonHeine);
		addKillId(mobs);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Castle castle = ResidenceHolder.getInstance().getResidence(InnadrilCastle);
		Player castleOwner = castle.getOwner().getLeader().getPlayer();
		String htmltext = event;
		if (event.equals("neurath_q711_03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("neurath_q711_05.htm"))
		{
			st.setCond(2);
		}
		else if (event.equals("neurath_q711_08.htm"))
		{
			if (isLordAvailable(2, st))
			{
				castleOwner.getQuestState(getClass()).set("confidant", String.valueOf(st.getPlayer().getObjectId()), true);
				castleOwner.getQuestState(getClass()).setCond(3);
				st.setState(STARTED);
			}
			else
			{
				htmltext = "neurath_q711_07a.htm";
			}

		}
		else if (event.equals("heine_q711_03.htm"))
		{
			if (isLordAvailable(3, st))
			{
				castleOwner.getQuestState(getClass()).setCond(4);
			}
			else
			{
				htmltext = "heine_q711_00a.htm";
			}
		}
		else if (event.equals("neurath_q711_12.htm"))
		{
			Functions.npcSay(npc, NpcString.S1_HAS_BECOME_THE_LORD_OF_THE_TOWN_OF_INNADRIL, st.getPlayer().getName());
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
		int cond = st.getCond();
		Castle castle = ResidenceHolder.getInstance().getResidence(InnadrilCastle);
		if (castle.getOwner() == null)
		{
			return "Castle has no lord";
		}
		Player castleOwner = castle.getOwner().getLeader().getPlayer();

		if (npcId == Neurath)
		{
			switch (cond)
			{
			case 0:
				if (castleOwner == st.getPlayer())
				{
					if (castle.getDominion().getLordObjectId() != st.getPlayer().getObjectId())
					{
						htmltext = "neurath_q711_01.htm";
					}
					else
					{
						htmltext = "neurath_q711_00.htm";
						st.exitCurrentQuest(true);
					}
				}
				else if (isLordAvailable(2, st))
				{
					if (castleOwner.isInRangeZ(npc, 200))
					{
						htmltext = "neurath_q711_07.htm";
					}
					else
					{
						htmltext = "neurath_q711_07a.htm";
					}
				}
				else if (st.getState() == STARTED)
				{
					htmltext = "neurath_q711_00b.htm";
				}
				else
				{
					htmltext = "neurath_q711_00a.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				htmltext = "neurath_q711_04.htm";
				break;
			case 2:
				htmltext = "neurath_q711_06.htm";
				break;
			case 3:
				htmltext = "neurath_q711_09.htm";
				break;
			case 4:
				st.setCond(5);
				htmltext = "neurath_q711_10.htm";
				break;
			case 5:
				htmltext = "neurath_q711_10.htm";
				break;
			case 6:
				htmltext = "neurath_q711_11.htm";
				break;
			default:
				break;
			}
		}
		else if (npcId == IasonHeine)
		{
			if (st.getState() == STARTED && cond == 0)
			{
				if (isLordAvailable(3, st))
				{
					if (Integer.parseInt(castleOwner.getQuestState(this.getClass()).get("confidant")) == st.getPlayer().getObjectId())
					{
						htmltext = "heine_q711_01.htm";
					}
					else
					{
						htmltext = "heine_q711_00.htm";
					}
				}
				else if (isLordAvailable(4, st))
				{
					if (Integer.parseInt(castleOwner.getQuestState(this.getClass()).get("confidant")) == st.getPlayer().getObjectId())
					{
						htmltext = "heine_q711_03.htm";
					}
					else
					{
						htmltext = "heine_q711_00.htm";
					}
				}
				else
				{
					htmltext = "heine_q711_00a.htm";
				}
			}
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 5)
		{
			if (st.getInt("mobs") < 99)
			{
				st.set("mobs", st.getInt("mobs") + 1);
			}
			else
			{
				st.setCond(6);
			}
		}
		return null;
	}

	private boolean isLordAvailable(int cond, QuestState st)
	{
		Castle castle = ResidenceHolder.getInstance().getResidence(InnadrilCastle);
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