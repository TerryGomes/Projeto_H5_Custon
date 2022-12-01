package l2mv.gameserver.handler.voicecommands.impl;

import java.util.HashMap;

import l2mv.gameserver.handler.items.IItemHandler;
import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.components.ChatType;

public class ACP implements IVoicedCommandHandler
{
	private static String[] COMMANDS =
	{
		"acpon",
		"acpoff"
	};

	// @VoicedCommand "ACP": items id's
	private static int ID_HEAL_CP = 5592;
	private static int ID_HEAL_MP = 728;
	private static int ID_HEAL_HP = 1539;
	// System enabled or disabled
	// Can be relocated into Config file in order to avoid compiling
	private static boolean ACP_ON = true;

	// ACP system requirements of level for character
	// Can be relocated into Config file in order to avoid compiling
	private static int ACP_MIN_LVL = 0;
	private static int ACP_HP_LVL = 1;
	private static int ACP_CP_LVL = 1;
	private static int ACP_MP_LVL = 1;
	// ACP system re-use time in mili-seconds
	private static int ACP_MILI_SECONDS_FOR_LOOP = 1000;

	// ACP system CP/HP/MP
	private static boolean ACP_CP = true;
	private static boolean ACP_MP = true;
	private static boolean ACP_HP = true;
	private static HashMap<String, Thread> userAcpMap = new HashMap<String, Thread>();

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		// Check if player exists in the world
		if (activeChar == null)
		{
			return false;
		}

		// Synerge - Only for premiums
		if (activeChar.getNetConnection().getBonus() < 1)
		{
			activeChar.sendPacket(new Say2(activeChar.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Donation", "You need premium status to use this function"));
			return false;
		}

		if (command.equals("acpon"))
		{
			if (!ACP_ON)
			{
				activeChar.sendMessage("Function has been disabled in server!");
				return false;
			}
			else if (userAcpMap.containsKey(activeChar.toString()))
			{
				activeChar.sendMessage("[ACP]: Already enabled!");
			}
			else
			{
				activeChar.sendMessage("[ACP]: System has been enabled!");
				Thread t = new Thread(new AcpHealer(activeChar));
				userAcpMap.put(activeChar.toString(), t);
				t.start();
				return true;
			}
		}
		else if (command.equals("acpoff"))
		{
			if (!userAcpMap.containsKey(activeChar.toString()))
			{
				activeChar.sendMessage("[ACP]: System has not been enabled!");
			}
			else
			{
				userAcpMap.remove(activeChar.toString()) // here we get thread and remove it from map
																																.interrupt(); // and interrupt it
				activeChar.sendMessage("[ACP]: System has been disabled!");
			}
		}
		return false;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}

	private class AcpHealer implements Runnable
	{
		private final Player _activeChar;

		public AcpHealer(Player activeChar)
		{
			_activeChar = activeChar;
		}

		@Override
		public void run()
		{
			try
			{
				while (true)
				{
					// Check for level requirements
					if (_activeChar.getLevel() >= ACP_MIN_LVL)
					{
						ItemInstance cpBottle = _activeChar.getInventory().getItemByItemId(ID_HEAL_CP);
						ItemInstance hpBottle = _activeChar.getInventory().getItemByItemId(ID_HEAL_HP);
						ItemInstance mpBottle = _activeChar.getInventory().getItemByItemId(ID_HEAL_MP);

						if (hpBottle != null && hpBottle.getCount() > 0)
						{
							// Check condition of stats(HP)
							if ((_activeChar.getCurrentHp() / _activeChar.getMaxHp()) * 100 < ACP_HP_LVL && ACP_HP)
							{
								IItemHandler handlerHP = hpBottle.getTemplate().getHandler();
								if (handlerHP != null)
								{
									handlerHP.useItem(_activeChar, hpBottle, false);
									_activeChar.sendMessage("[ACP]: HP has been restored.");
								}
							}
							// Check condition of stats(CP)
							if (cpBottle != null && cpBottle.getCount() > 0)
							{
								if ((_activeChar.getCurrentCp() / _activeChar.getMaxCp()) * 100 < ACP_CP_LVL && ACP_CP)
								{
									IItemHandler handlerCP = cpBottle.getTemplate().getHandler();
									if (handlerCP != null)
									{
										handlerCP.useItem(_activeChar, cpBottle, false);
										_activeChar.sendMessage("[ACP]: CP has been restored.");
									}
								}
							}
							// Check condition of stats(MP)
							if (mpBottle != null && mpBottle.getCount() > 0)
							{
								if ((_activeChar.getCurrentMp() / _activeChar.getMaxMp()) * 100 < ACP_MP_LVL && ACP_MP)
								{
									IItemHandler handlerMP = mpBottle.getTemplate().getHandler();
									if (handlerMP != null)
									{
										handlerMP.useItem(_activeChar, mpBottle, false);
										_activeChar.sendMessage("[ACP]: MP has been restored.");
									}
								}
							}
						}
						else
						{
							_activeChar.sendMessage("You don't have nothing to regenerate.");
							return;
						}
					}
					Thread.sleep(ACP_MILI_SECONDS_FOR_LOOP);
				}
			}
			catch (InterruptedException e)
			{
				// nothing
			}
			catch (Exception e)
			{
				Thread.currentThread().interrupt();
			}
			finally
			{
				userAcpMap.remove(_activeChar.toString());
			}
		}
	}
}
