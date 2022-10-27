package l2mv.gameserver.handler.admincommands.impl;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.MonsterRace;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.DeleteObject;
import l2mv.gameserver.network.serverpackets.MonRaceInfo;
import l2mv.gameserver.network.serverpackets.PlaySound;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.Location;

@SuppressWarnings("unused")
public class AdminMonsterRace implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_mons
	}

	protected static int state = -1;

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (fullString.equalsIgnoreCase("admin_mons"))
		{
			if (!activeChar.getPlayerAccess().MonsterRace)
			{
				return false;
			}
			handleSendPacket(activeChar);
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void handleSendPacket(Player activeChar)
	{
		/*
		 * -1 0 to initial the race 0 15322 to start race 13765 -1 in middle of race
		 * -1 0 to end the race
		 * 8003 to 8027
		 */

		int[][] codes =
		{
			{
				-1,
				0
			},
			{
				0,
				15322
			},
			{
				13765,
				-1
			},
			{
				-1,
				0
			}
		};
		MonsterRace race = MonsterRace.getInstance();

		if (state == -1)
		{
			state++;
			race.newRace();
			race.newSpeeds();
			activeChar.broadcastPacket(new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds()));
		}
		else if (state == 0)
		{
			state++;
			activeChar.sendPacket(SystemMsg.THEYRE_OFF);
			activeChar.broadcastPacket(new PlaySound("S_Race"));
			// TODO исправить 121209259 - обжект айди, ток неизвестно какого обьекта (VISTALL)
			activeChar.broadcastPacket(new PlaySound(PlaySound.Type.SOUND, "ItemSound2.race_start", 1, 121209259, new Location(12125, 182487, -3559)));
			activeChar.broadcastPacket(new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds()));

			ThreadPoolManager.getInstance().schedule(new RunRace(codes, activeChar), 5000);
		}
	}

	class RunRace extends RunnableImpl
	{
		private int[][] codes;
		private Player activeChar;

		public RunRace(int[][] codes, Player activeChar)
		{
			this.codes = codes;
			this.activeChar = activeChar;
		}

		@Override
		public void runImpl() throws Exception
		{
			// int[][] speeds1 = MonsterRace.getInstance().getSpeeds();
			// MonsterRace.getInstance().newSpeeds();
			// int[][] speeds2 = MonsterRace.getInstance().getSpeeds();
			/*
			 * int[] speed = new int[8]; for(int i=0; i<8; i++) { for(int j=0; j<20;
			 * j++) { //_log.info.println("Adding "+speeds1[i][j] +" and "+
			 * speeds2[i][j]); speed[i] += (speeds1[i][j]*1);// + (speeds2[i][j]*1); }
			 * _log.info.println("Total speed for "+(i+1)+" = "+speed[i]); }
			 */

			activeChar.broadcastPacket(new MonRaceInfo(codes[2][0], codes[2][1], MonsterRace.getInstance().getMonsters(), MonsterRace.getInstance().getSpeeds()));
			ThreadPoolManager.getInstance().schedule(new RunEnd(activeChar), 30000);
		}
	}

	class RunEnd extends RunnableImpl
	{
		private Player activeChar;

		public RunEnd(Player activeChar)
		{
			this.activeChar = activeChar;
		}

		@Override
		public void runImpl() throws Exception
		{
			NpcInstance obj;

			for (int i = 0; i < 8; i++)
			{
				obj = MonsterRace.getInstance().getMonsters()[i];
				// FIXME i don't know, if it's needed (Styx)
				// L2World.removeObject(obj);
				activeChar.broadcastPacket(new DeleteObject(obj));

			}
			state = -1;
		}
	}
}