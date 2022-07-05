package services;

import static l2f.gameserver.model.entity.olympiad.Olympiad.OLYMPIAD_HTML_PATH;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.utils.ItemFunctions;

public class HeroItems extends Functions
{
	private static final Logger _log = LoggerFactory.getLogger(HeroItems.class);
	private static final String[][] HERO_ITEMS =
	{
		{
			"6611",
			"weapon_the_sword_of_hero_i00",
			"Infinity Blade",
			"During a critical attack, decreases one's P. Def and increases de-buff casting ability, damage shield effect, Max HP, Max MP, Max CP, and shield defense power. Also enhances damage to target during PvP.",
			"297/137",
			"Sword"
		},
		{
			"6612",
			"weapon_the_two_handed_sword_of_hero_i00",
			"Infinity Cleaver",
			"Increases Max HP, Max CP, critical power and critical chance. Inflicts extra damage when a critical attack occurs and has possibility of reflecting the skill back on the player. Also enhances damage to target during PvP.",
			"361/137",
			"Two Handed Sword"
		},
		{
			"6613",
			"weapon_the_axe_of_hero_i00",
			"Infinity Axe",
			"During a critical attack, it bestows one the ability to cause internal conflict to one's opponent. Damage shield function, Max HP, Max MP, Max CP as well as one's shield defense rate are increased. It also enhances damage to one's opponent during PvP.",
			"297/137",
			"Blunt"
		},
		{
			"6614",
			"weapon_the_mace_of_hero_i00",
			"Infinity Rod",
			"When good magic is casted upon a target, increases MaxMP, MaxCP, Casting Spd, and MP regeneration rate. Also recovers HP 100% and enhances damage to target during PvP.",
			"238/182",
			"Blunt"
		},
		{
			"6615",
			"weapon_the_hammer_of_hero_i00",
			"Infinity Crusher",
			"Increases MaxHP, MaxCP, and Atk. Spd. Stuns a target when a critical attack occurs and has possibility of reflecting the skill back on the player. Also enhances damage to target during PvP.",
			"361/137",
			"Blunt"
		},
		{
			"6616",
			"weapon_the_staff_of_hero_i00",
			"Infinity Scepter",
			"When casting good magic, it can recover HP by 100% at a certain rate, increases MAX MP, MaxCP, M. Atk., lower MP Consumption, increases the Magic Critical rate, and reduce the Magic Cancel. Enhances damage to target during PvP.",
			"290/182",
			"Blunt"
		},
		{
			"6617",
			"weapon_the_dagger_of_hero_i00",
			"Infinity Stinger",
			"Increases MaxMP, MaxCP, Atk. Spd., MP regen rate, and the success rate of Mortal and Deadly Blow from the back of the target. Silences the target when a critical attack occurs and has Vampiric Rage effect. Also enhances damage to target during PvP.",
			"260/137",
			"Dagger"
		},
		{
			"6618",
			"weapon_the_fist_of_hero_i00",
			"Infinity Fang",
			"Increases MaxHP, MaxMP, MaxCP and evasion. Stuns a target when a critical attack occurs and has possibility of reflecting the skill back on the player at a certain probability rate. Also enhances damage to target during PvP.",
			"361/137",
			"Dual Fist"
		},
		{
			"6619",
			"weapon_the_bow_of_hero_i00",
			"Infinity Bow",
			"Increases MaxMP/MaxCP and decreases re-use delay of a bow. Slows target when a critical attack occurs and has Cheap Shot effect. Also enhances damage to target during PvP.",
			"614/137",
			"Bow"
		},
		{
			"6620",
			"weapon_the_dualsword_of_hero_i00",
			"Infinity Wing",
			"When a critical attack occurs, increases MaxHP, MaxMP, MaxCP and critical chance. Silences the target and has possibility of reflecting the skill back on the target. Also enhances damage to target during PvP.",
			"361/137",
			"Dual Sword"
		},
		{
			"6621",
			"weapon_the_pole_of_hero_i00",
			"Infinity Spear",
			"During a critical attack, increases MaxHP, Max CP, Atk. Spd. and Accuracy. Casts dispel on a target and has possibility of reflecting the skill back on the target. Also enhances damage to target during PvP.",
			"297/137",
			"Pole"
		},
		{
			"9388",
			"weapon_infinity_rapier_i00",
			"Infinity Rapier",
			"Decreases the target P. Def and increases the de-buff casting ability, the damage shield ability, and the Max HP/Max MP/Max CP on a critical attack. Increases damage inflicted during PvP.A critical attack will have a chance to increase P. Atk., M. Atk., and healing power, and decrease MP consumption during skill use, for you and your party members.",
			"287/142",
			"Rapier"
		},
		{
			"9389",
			"weapon_infinity_sword_i00",
			"Infinity Sword",
			"Increases critical attack success rate/power, MaxHP, MaxCP, and damage inflicted during PvP. Also inflicts extra damage on critical attacks, and reflects debuff attacks back on enemies.",
			"342/142",
			"Ancient Sword"
		},
		{
			"9390",
			"weapon_infinity_shooter_i00",
			"Infinity Shooter",
			"Produces the following effects when a critical attack occurs: the target is slowed, decrease MP consumption for skill use, and increase Max MP/Max CP. Enhances damage done to the target during PvP.",
			"400/142",
			"Crossbow"
		}
	};

	public void rendershop(String[] val)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}

		String fileName = OLYMPIAD_HTML_PATH;
		boolean succ = true;
		if (!player.isHero())
		{
			fileName += "monument_weapon_no_hero.htm";
			succ = false;
		}
		else
		{
			for (String heroItem[] : HERO_ITEMS)
			{
				int itemId = Integer.parseInt(heroItem[0]);
				if (player.getInventory().getItemByItemId(itemId) != null || player.getWarehouse().getItemByItemId(itemId) != null)
				{
					fileName += "monument_weapon_have.htm";
					succ = false;
					break;
				}
			}
		}

		if (!succ)
		{
			npc.showChatWindow(player, fileName);
			return;
		}

		boolean isKamael = player.getRace() == Race.kamael;
		String htmltext = "";
		if (val[0].equalsIgnoreCase("list"))
		{
			htmltext = "<html><body><font color=\"LEVEL\">List of Hero Weapons:</font><table border=0 width=270><tr><td>";
			for (int i = isKamael ? 11 : 0; i < (isKamael ? HERO_ITEMS.length : HERO_ITEMS.length - 3); i++)
			{
				htmltext += "<tr><td width=32 height=45 valign=top>";
				htmltext += "<img src=icon." + HERO_ITEMS[i][1] + " width=32 height=32></td>";
				htmltext += "<td valign=top>[<a action=\"bypass -h scripts_services.HeroItems:rendershop " + i + "\">" + HERO_ITEMS[i][2] + "</a>]<br1>";
				htmltext += "Type: " + HERO_ITEMS[i][5] + ", Patk/Matk: " + HERO_ITEMS[i][4];
				htmltext += "</td></tr>";
			}
			htmltext += "</table>";
		}
		else if (Integer.parseInt(val[0]) >= 0 && Integer.parseInt(val[0]) <= HERO_ITEMS.length)
		{
			int itemIndex = Integer.parseInt(val[0]);

			// For Kamael weapon Post:
			// 2234 Will you use the selected Kamael race-only Hero Weapon?
			// For all other weapons Post:
			// 1484 Are you sure this is the Hero weapon you wish to use? Kamael race cannot use this.
			int msgId = itemIndex > 10 ? 2234 : 1484;

			htmltext = "<html><body><font color=\"LEVEL\">Item Information:</font><table border=0 width=270><tr><td>";
			htmltext += "<img src=\"L2UI.SquareWhite\" width=270 height=1>";
			htmltext += "<table border=0 width=240>";
			htmltext += "<tr><td width=32 height=45 valign=top>";
			htmltext += "<img src=icon." + HERO_ITEMS[itemIndex][1] + " width=32 height=32></td>";
			htmltext += "<td valign=top>[<a action=\"bypass -h scripts_services.HeroItems:getweapon " + HERO_ITEMS[itemIndex][0] + "\" msg=\"" + msgId + "\">" + HERO_ITEMS[itemIndex][2] + "</a>]<br1>";
			htmltext += "Type: " + HERO_ITEMS[itemIndex][5] + ", Patk/Matk: " + HERO_ITEMS[itemIndex][4] + "<br1>";
			htmltext += "</td></tr></table>";
			htmltext += "<font color=\"B09878\">" + HERO_ITEMS[itemIndex][3] + "</font>";
			htmltext += "</td></tr></table><br>";
			htmltext += "<img src=\"L2UI.SquareWhite\" width=270 height=1><br><br>";
			htmltext += "<CENTER><button value=Back action=\"bypass -h scripts_services.HeroItems:rendershop list\" width=40 height=15 back=L2UI_CT1.Button_DF fore=L2UI_CT1.Button_DF></CENTER>";

		}
		show(htmltext, player, npc);
	}

	public void getweapon(String[] var)
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		int item = Integer.parseInt(var[0]);
		if (item < 6611 && item > 6621 || item < 9388 && item > 9390)
		{
			_log.warn(player.getName() + " tried to obtain non hero item using hero weapon service. Ban him!");
			return;
		}

		NpcInstance npc = getNpc();
		if (npc == null)
		{
			return;
		}

		String fileName = OLYMPIAD_HTML_PATH;
		if (player.isHero())
		{
			boolean have = false;
			for (String heroItem[] : HERO_ITEMS)
			{
				int itemId = Integer.parseInt(heroItem[0]);
				if (player.getInventory().getItemByItemId(itemId) != null || player.getWarehouse().getItemByItemId(itemId) != null)
				{
					player.getInventory().destroyItemByItemId(itemId, -1, "HeroItems");
					fileName += "monument_weapon_have.htm";
					have = true;
					break;
				}
			}
			if (!have)
			{
				ItemFunctions.addItem(player, item, 1, true, "HeroItems");
				fileName += "monument_weapon_give.htm";
			}
		}
		else
		{
			fileName += "monument_weapon_no_hero.htm";
		}

		npc.showChatWindow(player, fileName);
	}

	public String getcir()
	{
		Player player = getSelf();
		if (player == null)
		{
			return null;
		}

		NpcInstance npc = getNpc();
		if (npc == null)
		{
			return null;
		}

		String fileName = OLYMPIAD_HTML_PATH;
		if (player.isHero())
		{
			if (player.getInventory().getItemByItemId(6842) != null || player.getWarehouse().getItemByItemId(6842) != null)
			{
				fileName += "monument_circlet_have.htm";
			}
			else
			{
				ItemFunctions.addItem(player, 6842, 1, true, "Hero Items"); // Wings of Destiny Circlet

				fileName += "monument_circlet_give.htm";
			}
		}
		else
		{
			fileName += "monument_circlet_no_hero.htm";
		}

		npc.showChatWindow(player, fileName);
		return null;
	}

	public String getcloak()
	{
		Player player = getSelf();
		if (player == null)
		{
			return null;
		}

		NpcInstance npc = getNpc();
		if (npc == null)
		{
			return null;
		}

		String fileName = OLYMPIAD_HTML_PATH;
		if (player.isHero())
		{
			if (player.getInventory().getItemByItemId(37032) != null || player.getWarehouse().getItemByItemId(37032) != null)
			{
				fileName += "monument_cloak_have.htm";
			}
			else
			{
				ItemFunctions.addItem(player, 37032, 1, true, "CloakofHero"); // Cloak of Hero

				fileName += "monument_cloak_give.htm";
			}
		}
		else
		{
			fileName += "monument_cloak_no_hero.htm";
		}

		npc.showChatWindow(player, fileName);
		return null;
	}

}