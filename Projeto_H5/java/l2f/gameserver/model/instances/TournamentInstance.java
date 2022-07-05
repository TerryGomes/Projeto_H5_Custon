package l2f.gameserver.model.instances;

import l2f.gameserver.fandc.tournament.TournamentHolder;
import l2f.gameserver.fandc.tournament.model.AbstractTournament;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.templates.npc.NpcTemplate;

/**
 * @author Kara`
 * @version 1.0
 */
public class TournamentInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public TournamentInstance(final int objectId, final NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(final Player player, final String command)
	{
		if (command.startsWith("match"))
		{
			int id = Integer.parseInt(command.substring(6));

			final AbstractTournament tournament = TournamentHolder.getTournament(id);

			if (tournament == null)
			{
				player.sendMessage("Tournament does not exist.");
				return;
			}

			tournament.handleRegUnReg(player);
		}
	}

	@Override
	public void showChatWindow(final Player player, final int val, Object... objects)
	{
		final StringBuilder tb = new StringBuilder();

		tb.append("<html><title>Tournament Event</title><body>");
		tb.append("<center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></center><br>");
		tb.append("<center><font color=\"LEVEL\">Tournament Event</font></center><br>");
		tb.append("<font color=\"00CCFF\"><b>Event Information</b></font><br1>");
		tb.append("Team vs team <font color=\"00FF00\">full buffs</font> event.<br1>");
		tb.append("The goal is to defeat the enemy team.<br1>");

		if (player.isInTournament())
		{
			tb.append("<center><button action=\"bypass -h npc_" + getObjectId() + "_match " + player.getTournament().getTeamSize() + " \" value=\"" + "Unregister"
						+ "\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\">");
		}
		else
		{
			for (final AbstractTournament match : TournamentHolder.getTournaments())
			{
				tb.append("<center><button action=\"bypass -h npc_" + getObjectId() + "_match " + match.getTeamSize() + " \" value=\"" + match.getTeamSize() + " vs " + match.getTeamSize()
							+ "\" width=75 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\">");
			}
		}

		tb.append("</body></html>");
		final NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
		msg.setHtml(tb.toString());
		msg.replace("%name%", player.getName());
		player.sendPacket(msg);
	}
}