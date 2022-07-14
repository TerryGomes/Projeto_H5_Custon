package npc.model.residences.dominion;

import java.util.StringTokenizer;

import l2mv.gameserver.data.xml.holder.MultiSellHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.entity.residence.Dominion;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.ExShowDominionRegistry;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class MercenaryCaptainInstance extends NpcInstance
{
	public MercenaryCaptainInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		Dominion dominion = getDominion();

		if (command.equalsIgnoreCase("territory_register"))
		{
			player.sendPacket(new ExShowDominionRegistry(player, dominion));
		}
		else if (command.startsWith("certificate_multisell"))
		{
			StringTokenizer tokenizer = new StringTokenizer(command);
			tokenizer.nextToken();
			int certification = Integer.parseInt(tokenizer.nextToken());
			int multisell = Integer.parseInt(tokenizer.nextToken());

			if (player.getInventory().getCountOf(certification) > 0)
			{
				MultiSellHolder.getInstance().SeparateAndSend(multisell, player, getCastle().getTaxRate());
			}
			else
			{
				showChatWindow(player, 25);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		if (player.getLevel() < 40 || player.getClassId().getLevel() <= 2)
		{
			val = 26;
		}
		else
		{
			Castle castle = getCastle();
			Dominion dominion = getDominion();

			if (castle.getOwner() != null && player.getClan() == castle.getOwner() || dominion.getLordObjectId() == player.getObjectId())
			{
				if (castle.getSiegeEvent().isInProgress() || dominion.getSiegeEvent().isInProgress())
				{
					val = 21;
				}
				else
				{
					val = 7;
				}
			}
			else if (castle.getSiegeEvent().isInProgress() || dominion.getSiegeEvent().isInProgress())
			{
				val = 22;
			}
		}

		if (val == 0)
		{
			val = 1;
		}
		return val > 9 ? "residence2/dominion/gludio_merc_captain0" + val + ".htm" : "residence2/dominion/gludio_merc_captain00" + val + ".htm";
	}
}