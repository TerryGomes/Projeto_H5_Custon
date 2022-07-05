package l2f.gameserver.listener.event;

import l2f.gameserver.listener.EventListener;
import l2f.gameserver.model.entity.events.GlobalEvent;

public interface OnStartStopListener extends EventListener
{
	void onStart(GlobalEvent event);

	void onStop(GlobalEvent event);
}
