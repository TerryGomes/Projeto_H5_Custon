//package zones;
//
//import java.util.concurrent.ScheduledFuture;
//
//import l2f.commons.threading.RunnableImpl;
//import l2f.gameserver.ThreadPoolManager;
//import l2f.gameserver.listener.zone.OnZoneEnterLeaveListener;
//import l2f.gameserver.model.Creature;
//import l2f.gameserver.model.Player;
//import l2f.gameserver.model.Zone;
//import l2f.gameserver.scripts.ScriptFile;
//import l2f.gameserver.utils.ReflectionUtils;
//
///**
// * @author FandC
// */
//
//public class PvPZone implements ScriptFile
//{
//	private static ZoneListener _zoneListener;
//
//	@Override
//	public void onLoad()
//	{
//		_zoneListener = new ZoneListener();
//		Zone zone = ReflectionUtils.getZone("[pvp_zone_toi]");
//		zone.addListener(_zoneListener);
//	}
//
//	@Override
//	public void onReload()
//	{
//		// on reload
//	}
//
//	@Override
//	public void onShutdown()
//	{
//		// on shutdown
//	}
//
//	public class ZoneListener implements OnZoneEnterLeaveListener
//	{
//		@Override
//		public void onZoneEnter(Zone zone, Creature cha)
//		{
//			ScheduledFuture<?> _checkTask = null;
//			if (zone.getParams() == null || !cha.isPlayable())
//				return;
//
//			if (!cha.isPlayer())
//				return;
//
//			cha.getPlayer().sendMessage("You have entered in a PvP Zone!");
//			cha.startPvPFlag(null);
//			cha.getPlayer().sendMessage("You are now flagged!");
//
//			_checkTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new checkZone((Player)cha, zone, _checkTask), 60000, 60000);
//		}
//
//
//		public class checkZone extends RunnableImpl
//		{
//			Player _cha;
//			Zone _zone;
//			ScheduledFuture<?> _checkTask;
//
//			public checkZone(Player cha, Zone zone, ScheduledFuture<?> checkTask)
//			{
//				_cha = cha;
//				_zone = zone;
//				_checkTask = checkTask;
//			}
//
//			@Override
//			public void runImpl() throws Exception
//			{
//				if(_cha.isInZone(_zone))
//				{
//					_cha.startPvPFlag(null);
//				}
//				else
//				{
//					_checkTask.cancel(true);
//					_checkTask = null;
//				}
//			}
//
//		}
//
//		@Override
//		public void onZoneLeave(Zone zone, Creature cha)
//		{
//			if (cha == null)
//				return;
//			cha.getPlayer().sendMessage("You have left the PvP Zone !");
//			cha.getPlayer().stopPvPFlag();
//		}
//
//	}
//}
