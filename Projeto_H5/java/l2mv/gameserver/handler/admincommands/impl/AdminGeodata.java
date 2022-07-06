package l2mv.gameserver.handler.admincommands.impl;

import l2mv.gameserver.Config;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.utils.GeodataUtils;

public class AdminGeodata implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_geo_z, admin_geo_type, admin_geo_nswe, admin_geo_los, admin_geo_load, admin_geo_dump, admin_geo_trace, admin_geo_map, admin_geogrid
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanReload)
		{
			return false;
		}

		switch (command)
		{
		case admin_geogrid:
		{
			GeodataUtils.debugGrid(activeChar);
			break;
		}
		case admin_geo_z:
			activeChar.sendMessage("GeoEngine: Geo_Z = " + GeoEngine.getHeight(activeChar.getLoc(), activeChar.getReflectionId()) + " Loc_Z = " + activeChar.getZ());
			break;
		case admin_geo_type:
			int type = GeoEngine.getType(activeChar.getX(), activeChar.getY(), activeChar.getReflectionId());
			activeChar.sendMessage("GeoEngine: Geo_Type = " + type);
			break;
		case admin_geo_nswe:
			String result = "";
			byte nswe = GeoEngine.getNSWE(activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getReflectionId());
			if ((nswe & 8) == 0)
			{
				result += " N";
			}
			if ((nswe & 4) == 0)
			{
				result += " S";
			}
			if ((nswe & 2) == 0)
			{
				result += " W";
			}
			if ((nswe & 1) == 0)
			{
				result += " E";
			}
			activeChar.sendMessage("GeoEngine: Geo_NSWE -> " + nswe + "->" + result);
			break;
		case admin_geo_los:
			if (activeChar.getTarget() != null)
			{
				if (GeoEngine.canSeeTarget(activeChar, activeChar.getTarget(), false))
				{
					activeChar.sendMessage("GeoEngine: Can See Target");
				}
				else
				{
					activeChar.sendMessage("GeoEngine: Can't See Target");
				}
			}
			else
			{
				activeChar.sendMessage("None Target!");
			}
			break;
		case admin_geo_load:
			if (wordList.length != 3)
			{
				activeChar.sendMessage("Usage: //geo_load <regionX> <regionY>");
			}
			else
			{
				try
				{
					byte rx = Byte.parseByte(wordList[1]);
					byte ry = Byte.parseByte(wordList[2]);
					if (GeoEngine.LoadGeodataFile(rx, ry))
					{
						GeoEngine.LoadGeodata(rx, ry, 0);
						activeChar.sendMessage("GeoEngine: Region [" + rx + "," + ry + "] loaded.");
					}
					else
					{
						activeChar.sendMessage("GeoEngine: Region [" + rx + "," + ry + "] not loaded.");
					}
				}
				catch (Exception e)
				{
					activeChar.sendMessage(new CustomMessage("common.Error", activeChar));
				}
			}
			break;
		case admin_geo_dump:
			if (wordList.length > 2)
			{
				GeoEngine.DumpGeodataFileMap(Byte.parseByte(wordList[1]), Byte.parseByte(wordList[2]));
				activeChar.sendMessage("Geo square saved " + wordList[1] + "_" + wordList[2]);
			}
			GeoEngine.DumpGeodataFile(activeChar.getX(), activeChar.getY());
			activeChar.sendMessage("Actual geo square saved.");
			break;
		case admin_geo_trace:
			if (wordList.length < 2)
			{
				activeChar.sendMessage("Usage: //geo_trace on|off");
				return false;
			}
			if (wordList[1].equalsIgnoreCase("on"))
			{
				activeChar.setVar("trace", "1", -1);
			}
			else if (wordList[1].equalsIgnoreCase("off"))
			{
				activeChar.unsetVar("trace");
			}
			else
			{
				activeChar.sendMessage("Usage: //geo_trace on|off");
			}
			break;
		case admin_geo_map:
			int x = (activeChar.getX() - World.MAP_MIN_X >> 15) + Config.GEO_X_FIRST;
			int y = (activeChar.getY() - World.MAP_MIN_Y >> 15) + Config.GEO_Y_FIRST;

			activeChar.sendMessage("GeoMap: " + x + "_" + y);
			break;
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}