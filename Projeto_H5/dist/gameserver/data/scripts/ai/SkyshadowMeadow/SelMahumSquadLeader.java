package ai.SkyshadowMeadow;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.ChangeWaitType;
import l2mv.gameserver.network.serverpackets.SocialAction;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.Location;

/**
 * @author claww
  * - AI for mobs Sel Mahum Squad Leader (22 786, 22 787, 22 788).
  * - When the fire appears Katel (18,933) nptsy sbigayutsya then sit down and put food on the character's head.
  * - When the fire comes on (18927), is a 30% chance that they will want to sleep and come running to him, and sleep will appear above his head.
  * - Before you rush to eat, shout to chat.
  * - When the characters are over the head, to be a significant decrease.
  * - AI is tested and works.
 */
public class SelMahumSquadLeader extends Fighter
{
	private boolean _firstTime1 = true;
	private boolean _firstTime2 = true;
	private boolean _firstTime3 = true;
	private boolean _firstTime4 = true;
	private boolean _firstTime5 = true;
	private boolean statsIsChanged = false;
	public static final NpcString[] _text =
	{
		NpcString.SCHOOL5,
		NpcString.SCHOOL6
	};

	public SelMahumSquadLeader(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return true;
		}

		if (_def_think)
		{
			doTask();
			return true;
		}

		if (!statsIsChanged)
		{
			switch (actor.getNpcState())
			{
			case 1:
			{
				actor.doCast(SkillTable.getInstance().getInfo(6332, 1), actor, true);
				statsIsChanged = true;
				break;
			}
			case 2:
			{
				actor.doCast(SkillTable.getInstance().getInfo(6331, 1), actor, true);
				statsIsChanged = true;
				break;
			}
			}
		}

		if (!_firstTime2)
		{
			actor.broadcastPacket(new SocialAction(getActor().getObjectId(), 2));
			actor.broadcastPacket(new ChangeWaitType(getActor(), 0));
			actor.setNpcState((byte) 1);
			_firstTime2 = true;
		}

		if (!_firstTime4)
		{
			actor.broadcastPacket(new SocialAction(getActor().getObjectId(), 2));
			actor.broadcastPacket(new ChangeWaitType(getActor(), 0));
			actor.setNpcState((byte) 2);
			_firstTime4 = true;
		}

		for (NpcInstance npc : getActor().getAroundNpc(600, 600))
		{
			Location loc = Location.findPointToStay(npc, 100, 200);
			if (npc != null && npc.getNpcId() == 18933)
			{
				if (_firstTime1)
				{
					_firstTime1 = false;
					actor.setRunning();
					addTaskMove(loc, true);
					if (_firstTime5)
					{
						_firstTime5 = false;
						Functions.npcSay(actor, _text[Rnd.get(_text.length)]);
					}
					if (_firstTime2)
					{
						_firstTime2 = false;
						ThreadPoolManager.getInstance().schedule(new Go(), Rnd.get(20, 30) * 1000);
					}
				}
			}
		}

		for (NpcInstance npc : getActor().getAroundNpc(600, 600))
		{
			Location loc = Location.findPointToStay(npc, 100, 200);
			if (npc != null && npc.getNpcId() == 18927 && npc.getNpcState() == 1)
			{
				if (Rnd.chance(30))
				{
					if (_firstTime3)
					{
						_firstTime3 = false;
						actor.setRunning();
						addTaskMove(loc, true);
						if (_firstTime4)
						{
							_firstTime4 = false;
							ThreadPoolManager.getInstance().schedule(new Go(), Rnd.get(20, 30) * 1000);
						}
					}
				}
				else if (Rnd.chance(20))
				{
					actor.setNpcState((byte) 2);
					ThreadPoolManager.getInstance().schedule(new Stop(), Rnd.get(20, 30) * 1000);
				}
			}
		}

		return true;
	}

	private class Go extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			Location loc = Location.findPointToStay(actor, 100, 200);

			actor.setNpcState((byte) 3);
			actor.setRunning();
			addTaskMove(loc, true);
			_firstTime1 = true;
			_firstTime3 = true;
		}
	}

	private class Stop extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			actor.setNpcState((byte) 3);
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_firstTime1 = true;
		_firstTime2 = true;
		_firstTime3 = true;
		_firstTime4 = true;
		_firstTime5 = true;
		super.onEvtDead(killer);
	}
}