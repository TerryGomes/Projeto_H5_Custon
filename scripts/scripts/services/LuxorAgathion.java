package services;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;

/**
 * Используется в Luxor Shop NPC Alexandria (id: 30098), для продажи Agathion-ов.
 *
 * @Author: SYS
 * @Date: 11/06/2008
 */
public class LuxorAgathion extends Functions
{
	// Ингридиенты:
	private static final int[][] INGRIDIENTS =
	{
		{
			6471,
			25
		}, // 25 Big Red Nimble Fish
		{
			5094,
			50
		}, // 50 Great Codrans
		{
			9814,
			4
		}, // 4 Memento Moris
		{
			9816,
			5
		}, // 5 Earth Eggs
		{
			9817,
			5
		}, // 5 Nonliving Nuclei
		{
			9815,
			3
		}, // 3 Dragon Hearts
		{
			57,
			7500000
		} // 7,500,000 Adena
	};

	private static final int OldAgathion = 10408;
	private static final int ShadowPurpleVikingCirclet = 10315;
	private static final int ShadowGoldenVikingCirclet = 10321;

	// Продукция:
	private static int[] ANGEL_BRACELET_IDS = new int[]
	{
		10320,
		10316,
		10317,
		10318,
		10319
	};
	private static int[] DEVIL_BRACELET_IDS = new int[]
	{
		10326,
		10322,
		10323,
		10324,
		10325
	};

	private static int SUCCESS_RATE = 60; // 60% шанс успеха
	private static int RARE_RATE = 5; // 5% шанс выпадения редкого агашена со скилами

	public void angelAgathion()
	{
		agathion(ANGEL_BRACELET_IDS, 1);
	}

	public void devilAgathion()
	{
		agathion(DEVIL_BRACELET_IDS, 2);
	}

	private void agathion(int braceletes[], int type)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}

		for (int[] ingridient : INGRIDIENTS)
		{
			if (getItemCount(player, ingridient[0]) < ingridient[1])
			{
				show("merchant/30098-2.htm", player, npc);
				return;
			}
		}

		for (int[] ingridient : INGRIDIENTS)
		{
			removeItem(player, ingridient[0], ingridient[1], "LuxorAgathion");
		}

		if (!Rnd.chance(SUCCESS_RATE))
		{
			addItem(player, OldAgathion, 1, "LuxorAgathion");
			if (type == 1)
			{
				addItem(player, ShadowPurpleVikingCirclet, 1, "LuxorAgathion");
			}
			else
			{
				addItem(player, ShadowGoldenVikingCirclet, 1, "LuxorAgathion");
			}
			show("merchant/30098-3.htm", player, npc);
			return;
		}

		addItem(player, braceletes[Rnd.chance(RARE_RATE) ? 0 : Rnd.get(1, braceletes.length - 1)], 1, "LuxorAgathion");
		show("merchant/30098-4.htm", player, npc);
	}
}