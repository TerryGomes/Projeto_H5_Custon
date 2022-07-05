package l2f.gameserver.handler.chat;

import l2f.gameserver.network.serverpackets.components.ChatType;

public interface IChatHandler
{
	void say();

	ChatType getType();
}
