/*	*/
package l2f.gameserver.model.actor.recorder;

/*	*/
/*	*/ import l2f.gameserver.model.Summon;

/*	*/
/*	*/ public class SummonStatsChangeRecorder extends CharStatsChangeRecorder<Summon>
/*	*/ {
	/*	*/ public SummonStatsChangeRecorder(Summon actor)
	/*	*/ {
		/* 9 */ super(actor);
		/*	*/ }

	/*	*/
	/*	*/ @Override
	protected void onSendChanges()
	/*	*/ {
		/* 15 */ super.onSendChanges();
		/*	*/
		/* 17 */ if ((_changes & 0x2) == 2)
		{
			/* 18 */ ((Summon) _activeChar).sendPetInfo();
		}
		else if ((_changes & 0x1) == 1)
		{
			/* 20 */ ((Summon) _activeChar).broadcastCharInfo();
			/*	*/
		}
	}
	/*	*/ }