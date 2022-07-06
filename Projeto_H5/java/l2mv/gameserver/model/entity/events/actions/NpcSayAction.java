package l2mv.gameserver.model.entity.events.actions;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.entity.events.EventAction;
import l2mv.gameserver.model.entity.events.GlobalEvent;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.NpcSay;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.utils.MapUtils;

public class NpcSayAction implements EventAction
{
	private int _npcId;
	private int _range;
	private ChatType _chatType;
	private NpcString _text;

	public NpcSayAction(int npcId, int range, ChatType type, NpcString string)
	{
		_npcId = npcId;
		_range = range;
		_chatType = type;
		_text = string;
	}

	@Override
	public void call(GlobalEvent event)
	{
		NpcInstance npc = GameObjectsStorage.getByNpcId(_npcId);
		if (npc == null)
		{
			return;
		}

		if (_range <= 0)
		{
			int rx = MapUtils.regionX(npc);
			int ry = MapUtils.regionY(npc);
			int offset = Config.SHOUT_OFFSET;

			for (Player player : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (npc.getReflection() != player.getReflection())
				{
					continue;
				}

				int tx = MapUtils.regionX(player);
				int ty = MapUtils.regionY(player);

				if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset)
				{
					packet(npc, player);
				}
			}
		}
		else
		{
			for (Player player : World.getAroundPlayers(npc, _range, Math.max(_range / 2, 200)))
			{
				if (npc.getReflection() == player.getReflection())
				{
					packet(npc, player);
				}
			}
		}
	}

	private void packet(NpcInstance npc, Player player)
	{
		player.sendPacket(new NpcSay(npc, _chatType, _text));
	}
}
