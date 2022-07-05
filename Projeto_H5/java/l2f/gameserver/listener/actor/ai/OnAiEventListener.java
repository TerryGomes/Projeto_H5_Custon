package l2f.gameserver.listener.actor.ai;

import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.listener.AiListener;
import l2f.gameserver.model.Creature;

public interface OnAiEventListener extends AiListener
{
	public void onAiEvent(Creature actor, CtrlEvent evt, Object[] args);
}
