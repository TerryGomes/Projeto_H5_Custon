package l2mv.gameserver.templates.mapregion;

import java.util.List;

import l2mv.gameserver.utils.Location;

public class RestartPoint
{
	private final String _name;
	private final int _bbs;
	private final int _msgId;
	private final List<Location> _restartPoints;
	private final List<Location> _PKrestartPoints;

	public RestartPoint(String name, int bbs, int msgId, List<Location> restartPoints, List<Location> PKrestartPoints)
	{
		_name = name;
		_bbs = bbs;
		_msgId = msgId;
		_restartPoints = restartPoints;
		_PKrestartPoints = PKrestartPoints;
	}

	public String getName()
	{
		return _name;
	}

	public String getNameLoc()
	{
		if (getName().equalsIgnoreCase("[aden_town]"))
		{
			return "Aden town";
		}
		if (getName().equalsIgnoreCase("[oren_castle_town]"))
		{
			return "Oren town";
		}
		if (getName().equalsIgnoreCase("[giran_castle_town]") || getName().equalsIgnoreCase("[giran_habor]"))
		{
			return "Giran town";
		}
		if (getName().equalsIgnoreCase("[heiness_town]"))
		{
			return "Heiness town";
		}
		if (getName().equalsIgnoreCase("[dion_castle_town]") || getName().equalsIgnoreCase("[floran_town]"))
		{
			return "Dion town";
		}
		if (getName().equalsIgnoreCase("[gludio_castle_town]") || getName().equalsIgnoreCase("[DMZ]"))
		{
			return "Gludio town";
		}
		if (getName().equalsIgnoreCase("[gludin_town]"))
		{
			return "Gludin town";
		}
		if (getName().equalsIgnoreCase("[darkelf_town]"))
		{
			return "Darkelf town";
		}
		if (getName().equalsIgnoreCase("[elf_town]"))
		{
			return "Elf town";
		}
		if (getName().equalsIgnoreCase("[talking_island_town]"))
		{
			return "Talking Island town";
		}
		if (getName().equalsIgnoreCase("[godard_town]"))
		{
			return "Godard town";
		}
		if (getName().equalsIgnoreCase("[town_of_schuttgart]"))
		{
			return "Schuttgart town";
		}
		if (getName().equalsIgnoreCase("[rune_town]"))
		{
			return "Rune town";
		}
		if (getName().equalsIgnoreCase("[kamael_town]"))
		{
			return "Kamael town";
		}
		if (getName().equalsIgnoreCase("[dwarf_town]"))
		{
			return "Dwarf town";
		}
		if (getName().equalsIgnoreCase("[orc_town]"))
		{
			return "Orc town";
		}
		if (getName().equalsIgnoreCase("[hunter_town]"))
		{
			return "Hunter town";
		}
		return getName();
	}

	public int getBbs()
	{
		return _bbs;
	}

	public int getMsgId()
	{
		return _msgId;
	}

	public List<Location> getRestartPoints()
	{
		return _restartPoints;
	}

	public List<Location> getPKrestartPoints()
	{
		return _PKrestartPoints;
	}
}
