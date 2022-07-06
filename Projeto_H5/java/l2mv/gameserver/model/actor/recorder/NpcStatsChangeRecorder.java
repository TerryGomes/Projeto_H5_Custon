/*	*/
package l2mv.gameserver.model.actor.recorder;

/*	*/
/*	*/ import l2mv.gameserver.model.instances.NpcInstance;

/*	*/
/*	*/ public class NpcStatsChangeRecorder extends CharStatsChangeRecorder<NpcInstance>
/*	*/ {
	/*	*/ public NpcStatsChangeRecorder(NpcInstance actor)
	/*	*/ {
		/* 9 */ super(actor);
		/*	*/ }

	/*	*/
	/*	*/ @Override
	protected void onSendChanges()
	/*	*/ {
		/* 15 */ super.onSendChanges();
		/*	*/
		/* 17 */ if ((_changes & 0x1) == 1)
		{
			/* 18 */ ((NpcInstance) _activeChar).broadcastCharInfo();
			/*	*/
		}
	}
	/*	*/ }