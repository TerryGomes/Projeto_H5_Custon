package l2f.gameserver.listener.actor.ai;

import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.listener.AiListener;
import l2f.gameserver.model.Creature;

public interface OnAiIntentionListener extends AiListener
{
	public void onAiIntention(Creature actor, CtrlIntention intention, Object arg0, Object arg1);
}
