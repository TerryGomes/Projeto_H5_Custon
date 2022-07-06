package l2mv.gameserver.listener.event;

import l2mv.gameserver.listener.EventListener;
import l2mv.gameserver.model.entity.events.GlobalEvent;

public interface OnStartStopListener extends EventListener
{
	void onStart(GlobalEvent event);

	void onStop(GlobalEvent event);
}
