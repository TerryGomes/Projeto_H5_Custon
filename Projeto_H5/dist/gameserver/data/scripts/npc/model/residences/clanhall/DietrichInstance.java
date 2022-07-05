package npc.model.residences.clanhall;

import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 20:05/07.05.2011
 * 35408
 */
public class DietrichInstance extends _34BossMinionInstance
{
	public DietrichInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public NpcString spawnChatSay()
	{
		return NpcString.SOLDIERS_OF_GUSTAV_GO_FORTH_AND_DESTROY_THE_INVADERS;
	}

	@Override
	public NpcString teleChatSay()
	{
		return NpcString.AH_THE_BITTER_TASTE_OF_DEFEAT_I_FEAR_MY_TORMENTS_ARE_NOT_OVER;
	}
}
