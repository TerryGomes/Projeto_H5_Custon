package l2f.gameserver.templates.item.support;

public class EnchantScroll extends EnchantItem
{
	private final FailResultType _resultType;
	private final boolean _visualEffect;

	public EnchantScroll(int itemId, int chance, int maxEnchant, FailResultType resultType, boolean visualEffect)
	{
		super(itemId, chance, maxEnchant);
		_resultType = resultType;
		_visualEffect = visualEffect;
	}

	public FailResultType getResultType()
	{
		return _resultType;
	}

	public boolean isHasVisualEffect()
	{
		return _visualEffect;
	}
}
