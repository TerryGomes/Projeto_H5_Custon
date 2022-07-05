package l2f.gameserver.listener.game;

import l2f.gameserver.listener.GameListener;

/**
 * @author VISTALL
 * @date 7:12/19.05.2011
 */
public interface OnSSPeriodListener extends GameListener
{
	public void onPeriodChange(int val);
}
