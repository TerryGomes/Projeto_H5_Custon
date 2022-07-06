package l2mv.gameserver.handler.chat;

import l2mv.gameserver.network.serverpackets.components.ChatType;

public interface IChatHandler
{
	void say();

	ChatType getType();
}
