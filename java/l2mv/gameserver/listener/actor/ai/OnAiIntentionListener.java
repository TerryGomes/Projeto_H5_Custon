package l2mv.gameserver.listener.actor.ai;

import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.listener.AiListener;
import l2mv.gameserver.model.Creature;

public interface OnAiIntentionListener extends AiListener
{
	public void onAiIntention(Creature actor, CtrlIntention intention, Object arg0, Object arg1);
}
