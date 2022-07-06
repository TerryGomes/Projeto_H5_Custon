package l2mv.gameserver.listener.actor.ai;

import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.listener.AiListener;
import l2mv.gameserver.model.Creature;

public interface OnAiEventListener extends AiListener
{
	public void onAiEvent(Creature actor, CtrlEvent evt, Object[] args);
}
