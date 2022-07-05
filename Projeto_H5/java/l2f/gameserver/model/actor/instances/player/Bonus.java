package l2f.gameserver.model.actor.instances.player;

public class Bonus
{
	public static final int NO_BONUS = 0;
	public static final int BONUS_GLOBAL_ON_AUTHSERVER = 1;
	public static final int BONUS_GLOBAL_ON_GAMESERVER = 2;
	private double rateXp = 1.;
	private double rateSp = 1.;
	private double dropSiege = 1.;
	private double questRewardRate = 1.;
	private double questDropRate = 1.;
	private double dropAdena = 1.;
	private double dropItems = 1.;
	private double dropSpoil = 1.;
	private double weight;
	private int craftChance;
	private int masterWorkChance;
	private int _attributeChance;
	private int bonusExpire;

	public double getRateXp()
	{
		return rateXp;
	}

	public void setRateXp(double rateXp)
	{
		this.rateXp = rateXp;
	}

	public double getRateSp()
	{
		return rateSp;
	}

	public void setRateSp(double rateSp)
	{
		this.rateSp = rateSp;
	}

	public double getQuestRewardRate()
	{
		return questRewardRate;
	}

	public void setQuestRewardRate(double questRewardRate)
	{
		this.questRewardRate = questRewardRate;
	}

	public double getQuestDropRate()
	{
		return questDropRate;
	}

	public void setQuestDropRate(double questDropRate)
	{
		this.questDropRate = questDropRate;
	}

	public double getDropAdena()
	{
		return dropAdena;
	}

	public void setDropAdena(double dropAdena)
	{
		this.dropAdena = dropAdena;
	}

	public double getDropItems()
	{
		return dropItems;
	}

	public void setDropItems(double dropItems)
	{
		this.dropItems = dropItems;
	}

	public double getDropSpoil()
	{
		return dropSpoil;
	}

	public void setDropSpoil(double dropSpoil)
	{
		this.dropSpoil = dropSpoil;
	}

	public int getBonusExpire()
	{
		return bonusExpire;
	}

	public void setBonusExpire(int bonusExpire)
	{
		this.bonusExpire = bonusExpire;
	}

	public double getDropSiege()
	{
		return dropSiege;
	}

	public void setDropSiege(double dropSiege)
	{
		this.dropSiege = dropSiege;
	}

	public double getCraftChance()
	{
		return craftChance;
	}

	public void setCraftChance(int craftChance)
	{
		this.craftChance = craftChance;
	}

	public double getMasterWorkChance()
	{
		return masterWorkChance;
	}

	public void setMasterWorkChance(int masterWorkChance)
	{
		this.masterWorkChance = masterWorkChance;
	}

	public double getWeight()
	{
		return weight;
	}

	public void setWeight(double weight)
	{
		this.weight = weight;
	}

	public int getAttributeChance()
	{
		return _attributeChance;
	}

	public void setAttributeChance(int chance)
	{
		_attributeChance = chance;
	}
}
