package l2mv.gameserver.network.clientpackets;

import org.apache.commons.lang3.StringUtils;
import org.napile.primitive.maps.IntObjectMap;

import l2mv.gameserver.dao.CharacterPostFriendDAO;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class RequestExDeletePostFriendForPostBox extends L2GameClientPacket
{
	private String _name;

	@Override
	protected void readImpl()
	{
		this._name = this.readS();
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if ((player == null) || StringUtils.isEmpty(this._name))
		{
			return;
		}

		int key = 0;
		IntObjectMap<String> postFriends = player.getPostFriends();
		for (IntObjectMap.Entry<String> entry : postFriends.entrySet())
		{
			if (entry.getValue().equalsIgnoreCase(this._name))
			{
				key = entry.getKey();
			}
		}

		if (key == 0)
		{
			player.sendPacket(SystemMsg.THE_NAME_IS_NOT_CURRENTLY_REGISTERED);
			return;
		}

		player.getPostFriends().remove(key);

		CharacterPostFriendDAO.delete(player, key);
		player.sendPacket(new SystemMessage2(SystemMsg.S1_WAS_SUCCESSFULLY_DELETED_FROM_YOUR_CONTACT_LIST).addString(this._name));
	}
}
