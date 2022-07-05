/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2f.gameserver.masteriopack.rankpvpsystem;

import java.util.Calendar;

/**
 * @author Masterio
 */
public final class RPSUtil
{
	public static final String getClassName(int classId)
	{
		switch (classId)
		{
		case 0:
			return "Human Fighter";
		case 1:
			return "Warrior";
		case 2:
			return "Gladiator";
		case 3:
			return "Warlord";
		case 4:
			return "Human Knight";
		case 5:
			return "Paladin";
		case 6:
			return "Dark Avenger";
		case 7:
			return "Rogue";
		case 8:
			return "Treasure Hunter";
		case 9:
			return "Hawkeye";
		case 10:
			return "Human Mystic";
		case 11:
			return "Human Wizard";
		case 12:
			return "Sorcerer";
		case 13:
			return "Necromancer";
		case 14:
			return "Warlock";
		case 15:
			return "Cleric";
		case 16:
			return "Bishop";
		case 17:
			return "Prophet";
		case 18:
			return "Elven Fighter";
		case 19:
			return "Elven Knight";
		case 20:
			return "Temple Knight";
		case 21:
			return "Sword Singer";
		case 22:
			return "Elven Scout";
		case 23:
			return "Plains Walker";
		case 24:
			return "Silver Ranger";
		case 25:
			return "Elven Mystic";
		case 26:
			return "Elven Wizard";
		case 27:
			return "Spellsinger";
		case 28:
			return "Elemental Summoner";
		case 29:
			return "Elven Oracle";
		case 30:
			return "Elven Elder";
		case 31:
			return "Dark Fighter";
		case 32:
			return "Palus Knight";
		case 33:
			return "Shillien Knight";
		case 34:
			return "Bladedancer";
		case 35:
			return "Assassin";
		case 36:
			return "Abyss Walker";
		case 37:
			return "Phantom Ranger";
		case 38:
			return "Dark Mystic";
		case 39:
			return "Dark Wizard";
		case 40:
			return "Spellhowler";
		case 41:
			return "Phantom Summoner";
		case 42:
			return "Shillien Oracle";
		case 43:
			return "Shillien Elder";
		case 44:
			return "Orc Fighter";
		case 45:
			return "Orc Raider";
		case 46:
			return "Destroyer";
		case 47:
			return "Monk";
		case 48:
			return "Tyrant";
		case 49:
			return "Orc Mystic";
		case 50:
			return "Orc Shaman";
		case 51:
			return "Overlord";
		case 52:
			return "Warcryer";
		case 53:
			return "Dwarf Fighter";
		case 54:
			return "Scavenger";
		case 55:
			return "Bounty Hunter";
		case 56:
			return "Artisan";
		case 57:
			return "Warsmith";
		// Third Classes -->
		case 88:
			return "Duelist";
		case 89:
			return "Dreadnought";
		case 90:
			return "Phoenix Knight";
		case 91:
			return "Hell Knight";
		case 92:
			return "Sagittarius";
		case 93:
			return "Adventurer";
		case 94:
			return "Archmage";
		case 95:
			return "Soultaker";
		case 96:
			return "Arcana Lord";
		case 97:
			return "Cardinal";
		case 98:
			return "Hierophant";
		case 99:
			return "Eva's Templar";
		case 100:
			return "Sword Muse";
		case 101:
			return "Wind Rider";
		case 102:
			return "Moonlight Sentinel";
		case 103:
			return "Mystic Muse";
		case 104:
			return "Elemental Master";
		case 105:
			return "Eva's Saint";
		case 106:
			return "Shillien Templar";
		case 107:
			return "Spectral Dancer";
		case 108:
			return "Ghost Hunter";
		case 109:
			return "Ghost Sentinel";
		case 110:
			return "Storm Screamer";
		case 111:
			return "Spectral Master";
		case 112:
			return "Shillien Saint";
		case 113:
			return "Titan";
		case 114:
			return "Grand Khavatari";
		case 115:
			return "Dominator";
		case 116:
			return "Doom Cryer";
		case 117:
			return "Fortune Seeker";
		case 118:
			return "Maestro";

		case 123:
			return "Male Kamael Soldier";
		case 124:
			return "Female Kamael Soldier";
		case 125:
			return "Trooper";
		case 126:
			return "Warder";
		case 127:
			return "Berserker";
		case 128:
			return "Male Soul Breaker";
		case 129:
			return "Female Soul Breaker";
		case 130:
			return "Arbalester";
		case 131:
			return "Doombringer";
		case 132:
			return "Male Soul Hound";
		case 133:
			return "Female Soul Hound";
		case 134:
			return "Trickster";
		case 135:
			return "Inspector";
		case 136:
			return "Judicator";

		default:
			return "Unknown";
		}
	}

	public static String preparePrice(long price)
	{

		String preparePrice = String.valueOf(price);
		String priceTmp = "";
		int j = 0;

		for (int i = (preparePrice.length() - 1); i >= 0; i--)
		{
			j++;
			if (j == 4 || j == 7 || j == 10 || j == 13 || j == 16 || j == 19 || j == 21)
			{
				priceTmp = preparePrice.charAt(i) + "," + priceTmp;
			}
			else
			{
				priceTmp = preparePrice.charAt(i) + priceTmp;
			}
		}

		return priceTmp;
	}

	public static String dateToString(Calendar time)
	{
		String t = "";

		int d = time.get(Calendar.DAY_OF_MONTH);
		if (d < 10)
		{
			t += "0" + d;
		}
		else
		{
			t += d;
		}

		int m = time.get(Calendar.MONTH) + 1;
		if (m < 10)
		{
			t += ".0" + m;
		}
		else
		{
			t += "." + m;
		}

		t += "." + time.get(Calendar.YEAR);

		return t;
	}

	public static String dateToString(long timeL)
	{
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(timeL);

		String t = "";

		int d = time.get(Calendar.DAY_OF_MONTH);
		if (d < 10)
		{
			t += "0" + d;
		}
		else
		{
			t += d;
		}

		int m = time.get(Calendar.MONTH) + 1;
		if (m < 10)
		{
			t += ".0" + m;
		}
		else
		{
			t += "." + m;
		}

		t += "." + time.get(Calendar.YEAR);

		return t;
	}

	public static String timeToString(Calendar time)
	{
		String t = "" + time.get(Calendar.HOUR_OF_DAY);

		int m = time.get(Calendar.MINUTE);
		if (m < 10)
		{
			t += ":0" + m;
		}
		else
		{
			t += ":" + m;
		}

		int s = time.get(Calendar.SECOND);
		if (s < 10)
		{
			t += ":0" + s;
		}
		else
		{
			t += ":" + s;
		}

		return t;
	}

	public static String timeToString(long timeL)
	{
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(timeL);
		String t = "" + time.get(Calendar.HOUR_OF_DAY);

		int m = time.get(Calendar.MINUTE);
		if (m < 10)
		{
			t += ":0" + m;
		}
		else
		{
			t += ":" + m;
		}

		int s = time.get(Calendar.SECOND);
		if (s < 10)
		{
			t += ":0" + s;
		}
		else
		{
			t += ":" + s;
		}

		return t;
	}
}