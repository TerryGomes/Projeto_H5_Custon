package l2mv.gameserver.model.reward;

public class RewardItem
{
	public final int itemId;
	public long count;
	public boolean isAdena;
	public int enchantLvl;

	public RewardItem(int itemId)
	{
		this.itemId = itemId;
		count = 1;
		enchantLvl = 0;
	}
}
