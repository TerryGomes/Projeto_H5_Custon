package services;

import l2f.gameserver.ConfigHolder;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.tables.ClanTable;

public class ClanPanel extends Functions
{
	public void online()
	{
		if (!ConfigHolder.getBool("AllowClanListPage"))
		{
			return;
		}

		online(new String[]
		{
			"-1",
			"1"
		});
	}

	public void online(String[] param)
	{
		if (!ConfigHolder.getBool("AllowClanListPage") || (param.length < 1))
		{
			return;
		}

		final Player player = getSelf();
		if (player == null)
		{
			return;
		}

		final int clan_id = Integer.parseInt(param[0]);
		int page = 1;
		if (param.length > 1)
		{
			page = Integer.parseInt(param[1]);
		}

		final Clan clan = ClanTable.getInstance().getClan(clan_id);
		if (clan != null)
		{
			String html = HtmCache.getInstance().getNotNull("scripts/services/ClanPanel/clan_online.htm", player);
			final String template = HtmCache.getInstance().getNotNull("scripts/services/ClanPanel/clan_online_template.htm", player);
			String list = "";
			final StringBuilder data = new StringBuilder();
			int current = 1;
			final int start = (page - 1) * 10;
			for (int end = Math.min(page * 10, clan.getOnlineMembers(0).size()), i = start; i < end; ++i)
			{
				final Player member = clan.getOnlineMembers(0).get(i);
				list = template;
				list = list.replace("<?name?>", member.getName());
				list = list.replace("<?level?>", String.valueOf(member.getLevel()));
				list = list.replace("<?color?>", current % 2 == 0 ? "666666" : "999999");
				list = list.replace("<?icon?>", getClanClassIcon(member.getClassId()));
				final String unity = member.getClan().getUnitName(member.getPledgeType());
				list = list.replace("<?unity?>", unity.length() > 10 ? unity.substring(0, 8) + "..." : unity);
				data.append(list);
				++current;
			}
			html = html.replace("<?navigate?>", parseNavigate(clan, page));
			html = html.replace("<?data?>", data);
			html = html.replace("<?name?>", clan.getName());
			html = html.replace("<?count?>", String.valueOf(clan.getOnlineMembers(0).size()));
			this.show(html, player);
		}
	}

	private String parseNavigate(Clan clan, int page)
	{
		final StringBuilder pg = new StringBuilder();
		final double size = clan.getOnlineMembers(0).size();
		final double inpage = 10.0;
		if (size > inpage)
		{
			final double max = Math.ceil(size / inpage);
			pg.append("<center><table width=25 border=0><tr>");
			int line = 1;
			for (int current = 1; current <= max; ++current)
			{
				if (page == current)
				{
					pg.append("<td width=25 align=center><button value=\"[").append(current).append("]\" width=38 height=25 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td>");
				}
				else
				{
					pg.append("<td width=25 align=center><button value=\"").append(current).append("\" action=\"bypass -h scripts_services.ClanPanel:online " + clan.getClanId() + " ").append(current)
								.append("\" width=28 height=25 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td>");
				}
				if (line == 22)
				{
					pg.append("</tr><tr>");
					line = 0;
				}
				++line;
			}
			pg.append("</tr></table></center>");
		}
		return pg.toString();
	}

	private static String getClanClassIcon(ClassId classid)
	{
		if (classid.getType2() == null)
		{
			return classid.isMage() ? "L2UI_CH3.party_styleicon1_2" : "L2UI_CH3.party_styleicon1_1";
		}

		final String ending = classid.getLevel() == 4 ? "_3" : "";
		switch (classid.getType2())
		{
		case Warrior:
		{
			return "L2UI_CH3.party_styleicon1" + ending;
		}
		case Rogue:
		{
			return "L2UI_CH3.party_styleicon2" + ending;
		}
		case Knight:
		{
			return "L2UI_CH3.party_styleicon3" + ending;
		}
		case Healer:
		{
			return "L2UI_CH3.party_styleicon6" + ending;
		}
		case Enchanter:
		{
			return "L2UI_CH3.party_styleicon4" + ending;
		}
		case Summoner:
		{
			return "L2UI_CH3.party_styleicon7" + ending;
		}
		case Wizard:
		{
			return "L2UI_CH3.party_styleicon5" + ending;
		}
		default:
		{
			return "L2UI_CH3.party_styleicon";
		}
		}
	}
}
