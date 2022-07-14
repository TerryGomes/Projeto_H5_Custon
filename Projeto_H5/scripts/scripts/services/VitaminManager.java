package services;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;

public class VitaminManager extends Functions
{
	private static final int PetCoupon = 13273;
	private static final int SpecialPetCoupon = 14065;

	private static final int WeaselNeck = 13017;
	private static final int PrincNeck = 13018;
	private static final int BeastNeck = 13019;
	private static final int FoxNeck = 13020;

	private static final int KnightNeck = 13548;
	private static final int SpiritNeck = 13549;
	private static final int OwlNeck = 13550;
	private static final int TurtleNeck = 13551;

	public void giveWeasel()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();

		String htmltext;
		if (getItemCount(player, PetCoupon) > 0)
		{
			removeItem(player, PetCoupon, 1, "VitaminManager");
			addItem(player, WeaselNeck, 1, "VitaminManager");
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
		{
			htmltext = npc.getNpcId() + "-no.htm";
		}

		npc.showChatWindow(player, "default/" + htmltext);
	}

	public void givePrinc()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();

		String htmltext;
		if (getItemCount(player, PetCoupon) > 0)
		{
			removeItem(player, PetCoupon, 1, "givePrinc");
			addItem(player, PrincNeck, 1, "givePrinc");
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
		{
			htmltext = npc.getNpcId() + "-no.htm";
		}

		npc.showChatWindow(player, "default/" + htmltext);
	}

	public void giveBeast()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();

		String htmltext;
		if (getItemCount(player, PetCoupon) > 0)
		{
			removeItem(player, PetCoupon, 1, "giveBeast");
			addItem(player, BeastNeck, 1, "giveBeast");
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
		{
			htmltext = npc.getNpcId() + "-no.htm";
		}

		npc.showChatWindow(player, "default/" + htmltext);
	}

	public void giveFox()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();

		String htmltext;
		if (getItemCount(player, PetCoupon) > 0)
		{
			removeItem(player, PetCoupon, 1, "giveFox");
			addItem(player, FoxNeck, 1, "giveFox");
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
		{
			htmltext = npc.getNpcId() + "-no.htm";
		}

		npc.showChatWindow(player, "default/" + htmltext);
	}

	public void giveKnight()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();

		String htmltext;
		if (getItemCount(player, SpecialPetCoupon) > 0)
		{
			removeItem(player, SpecialPetCoupon, 1, "giveKnight");
			addItem(player, KnightNeck, 1, "giveKnight");
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
		{
			htmltext = npc.getNpcId() + "-no.htm";
		}

		npc.showChatWindow(player, "default/" + htmltext);
	}

	public void giveSpirit()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();

		String htmltext;
		if (getItemCount(player, SpecialPetCoupon) > 0)
		{
			removeItem(player, SpecialPetCoupon, 1, "giveSpirit");
			addItem(player, SpiritNeck, 1, "giveSpirit");
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
		{
			htmltext = npc.getNpcId() + "-no.htm";
		}

		npc.showChatWindow(player, "default/" + htmltext);
	}

	public void giveOwl()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();

		String htmltext;
		if (getItemCount(player, SpecialPetCoupon) > 0)
		{
			removeItem(player, SpecialPetCoupon, 1, "giveOwl");
			addItem(player, OwlNeck, 1, "giveOwl");
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
		{
			htmltext = npc.getNpcId() + "-no.htm";
		}

		npc.showChatWindow(player, "default/" + htmltext);
	}

	public void giveTurtle()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();

		String htmltext;
		if (getItemCount(player, SpecialPetCoupon) > 0)
		{
			removeItem(player, SpecialPetCoupon, 1, "giveTurtle");
			addItem(player, TurtleNeck, 1, "giveTurtle");
			htmltext = npc.getNpcId() + "-ok.htm";
		}
		else
		{
			htmltext = npc.getNpcId() + "-no.htm";
		}

		npc.showChatWindow(player, "default/" + htmltext);
	}
}