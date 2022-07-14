package services;

import java.util.Calendar;

import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.GameObjectTasks;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.PlaySound;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.NpcUtils;
import l2mv.gameserver.utils.PositionUtils;

/**
 * @author claww
 *
 * High Five: Exchanges Explorer Hat for Birthday Hat
 */
public class Birthday extends Functions
{
	private static final int EXPLORERHAT = 10250;
	private static final int HAT = 13488; // Birthday Hat
	private static final int NPC_ALEGRIA = 32600; // Alegria

	private static final String msgSpawned = "scripts/services/Birthday-spawned.htm";

	/**
	 * Вызывается у гейткиперов
	 */
	public void summonAlegria()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();

		if (player == null || npc == null || !NpcInstance.canBypassCheck(player, player.getLastNpc()))
		{
			return;
		}

		// TODO: На оффе можно вызвать до 3х нпсов. Но зачем? о.0
		for (NpcInstance n : World.getAroundNpc(npc))
		{
			if (n.getNpcId() == NPC_ALEGRIA)
			{
				show(msgSpawned, player, npc);
				return;
			}
		}

		player.sendPacket(PlaySound.HB01);

		try
		{
			// Спаним Аллегрию где-то спереди от ГК
			int x = (int) (npc.getX() + 40 * Math.cos(npc.headingToRadians(npc.getHeading() - 32768 + 8000)));
			int y = (int) (npc.getY() + 40 * Math.sin(npc.headingToRadians(npc.getHeading() - 32768 + 8000)));

			NpcInstance alegria = NpcUtils.spawnSingle(NPC_ALEGRIA, x, y, npc.getZ(), 180000);
			alegria.setHeading(PositionUtils.calculateHeadingFrom(alegria, player));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Вызывается у NPC Alegria
	 */
	public void exchangeHat()
	{
		Player player = getSelf();
		final NpcInstance npc = getNpc();

		if (player == null || npc == null || !NpcInstance.canBypassCheck(player, player.getLastNpc()) || npc.isBusy())
		{
			return;
		}

		if (ItemFunctions.getItemCount(player, EXPLORERHAT) < 1)
		{
			show("default/32600-nohat.htm", player, npc);
			return;
		}
		ItemFunctions.removeItem(player, EXPLORERHAT, 1, true, "exchangeHat");
		ItemFunctions.addItem(player, HAT, 1, true, "exchangeHat");
		show("default/32600-successful.htm", player, npc);

		long now = System.currentTimeMillis() / 1000;
		player.setVar("Birthday", String.valueOf(now), -1);

		npc.setBusy(true);

		ThreadPoolManager.getInstance().execute(new GameObjectTasks.DeleteTask(npc));
	}

	/**
	 * Вернет true если у чара сегодня день рождения
	 * @param player
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isBirthdayToday(Player player)
	{
		if (player.getCreateTime() == 0)
		{
			return false;
		}

		Calendar create = Calendar.getInstance();
		create.setTimeInMillis(player.getCreateTime());
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());

		return create.get(Calendar.MONTH) == now.get(Calendar.MONTH) && create.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH) && create.get(Calendar.YEAR) != now.get(Calendar.YEAR);
	}
}