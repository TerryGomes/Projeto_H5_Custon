package l2f.gameserver.model.reward;

/**
 * @author VISTALL
 * @date  16:20/14.12.2010
 */
public enum RewardType
{
	RATED_GROUPED, // additional_make_multi_list
	NOT_RATED_NOT_GROUPED, // additional_make_list
	NOT_RATED_GROUPED, // ex_item_drop_list
	SWEEP; // corpse_make_list

	public static final RewardType[] VALUES = values();
}
