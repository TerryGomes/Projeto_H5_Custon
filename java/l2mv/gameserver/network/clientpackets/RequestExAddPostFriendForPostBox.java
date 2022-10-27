package l2mv.gameserver.network.clientpackets;

import org.napile.primitive.maps.IntObjectMap;

import l2mv.gameserver.Config;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.dao.CharacterPostFriendDAO;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.ExConfirmAddingPostFriend;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

/**
 * @author VISTALL
 * @date 21:06/22.03.2011
 */
public class RequestExAddPostFriendForPostBox extends L2GameClientPacket
{
	private String _name;

	@Override
	protected void readImpl()
	{
		this._name = this.readS(Config.CNAME_MAXLEN);
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		int targetObjectId = CharacterDAO.getInstance().getObjectIdByName(this._name);
		if (targetObjectId == 0)
		{
			player.sendPacket(new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.NAME_IS_NOT_EXISTS));
			return;
		}

		if (this._name.equalsIgnoreCase(player.getName()))
		{
			player.sendPacket(new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.NAME_IS_NOT_REGISTERED));
			return;
		}

		IntObjectMap<String> postFriend = player.getPostFriends();
		if (postFriend.size() >= Player.MAX_POST_FRIEND_SIZE)
		{
			player.sendPacket(new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.LIST_IS_FULL));
			return;
		}

		if (postFriend.containsKey(targetObjectId))
		{
			player.sendPacket(new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.ALREADY_ADDED));
			return;
		}

		CharacterPostFriendDAO.getInstance().insert(player, targetObjectId);
		postFriend.put(targetObjectId, CharacterDAO.getNameByObjectId(targetObjectId));

		player.sendPacket(new SystemMessage2(SystemMsg.S1_WAS_SUCCESSFULLY_ADDED_TO_YOUR_CONTACT_LIST).addString(this._name), new ExConfirmAddingPostFriend(this._name, ExConfirmAddingPostFriend.SUCCESS));
	}
}
