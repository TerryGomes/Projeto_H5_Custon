package l2mv.gameserver.network.serverpackets.components;

/**
 * @author VISTALL
 * @date  12:48/29.12.2010
 */
public enum ChatType
{
	ALL, // 0
	SHOUT, // 1 !
	TELL, // 2 "
	PARTY, // 3 #
	CLAN, // 4 @
	GM, // 5
	PETITION_PLAYER, // 6 used for petition
	PETITION_GM, // 7 * used for petition
	TRADE, // 8 +
	ALLIANCE, // 9 $
	ANNOUNCEMENT, // 10
	SYSTEM_MESSAGE, // 11 Dont use with plain string... gives critical error :D
	L2FRIEND, MSNCHAT, PARTY_ROOM, // 14
	COMMANDCHANNEL_ALL, // 15 ``
	COMMANDCHANNEL_COMMANDER, // 16 `
	HERO_VOICE, // 17 %
	CRITICAL_ANNOUNCE, // 18
	SCREEN_ANNOUNCE, BATTLEFIELD, // 20 ^
	MPCC_ROOM, NPC_ALL, NPC_SHOUT;

	public static final ChatType[] VALUES = values();

	public static ChatType getTypeFromName(String name)
	{
		for (ChatType type : values())
		{
			if (type.name().equalsIgnoreCase(name))
			{
				return type;
			}
		}

		return ALL;
	}
}
