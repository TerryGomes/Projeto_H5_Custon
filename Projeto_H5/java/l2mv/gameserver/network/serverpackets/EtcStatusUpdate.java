package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class EtcStatusUpdate extends L2GameServerPacket
{
	/**
	 *
	 * Packet for lvl 3 client buff line
	 *
	 * Example:(C4)
	 * F9 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 - empty statusbar
	 * F9 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 - increased force lvl 1
	 * F9 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 - weight penalty lvl 1
	 * F9 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 - chat banned
	 * F9 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 - Danger Area lvl 1
	 * F9 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 - lvl 1 grade penalty
	 *
	 * packet format: cdd //and last three are ddd???
	 *
	 * Some test results:
	 * F9 07 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 - lvl 7 increased force lvl 4 weight penalty
	 *
	 * Example:(C5 709)
	 * F9 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 0F 00 00 00 - lvl 1 charm of courage lvl 15 Death Penalty
	 *
	 *
	 * NOTE:
	 * End of buff:
	 * You must send empty packet
	 * F9 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
	 * to remove the statusbar or just empty value to remove some icon.
	 */

	private int IncreasedForce, WeightPenalty, MessageRefusal, DangerArea;
	private int armorExpertisePenalty, weaponExpertisePenalty, CharmOfCourage, DeathPenaltyLevel, ConsumedSouls;

	public EtcStatusUpdate(Player player)
	{
		this.IncreasedForce = player.getIncreasedForce();
		this.WeightPenalty = player.getWeightPenalty();
		this.MessageRefusal = player.getMessageRefusal() || player.getNoChannel() != 0 || player.isBlockAll() ? 1 : 0;
		this.DangerArea = player.isInDangerArea() ? 1 : 0;
		this.armorExpertisePenalty = player.getArmorsExpertisePenalty();
		this.weaponExpertisePenalty = player.getWeaponsExpertisePenalty();
		this.CharmOfCourage = player.isCharmOfCourage() ? 1 : 0;
		this.DeathPenaltyLevel = player.getDeathPenalty() == null ? 0 : player.getDeathPenalty().getLevel();
		this.ConsumedSouls = player.getConsumedSouls();
	}

	@Override
	protected final void writeImpl()
	{
		// dddddddd
		this.writeC(0xf9); // Packet type
		this.writeD(this.IncreasedForce); // skill id 4271, 7 lvl
		this.writeD(this.WeightPenalty); // skill id 4270, 4 lvl
		this.writeD(this.MessageRefusal); // skill id 4269, 1 lvl
		this.writeD(this.DangerArea); // skill id 4268, 1 lvl
		this.writeD(this.weaponExpertisePenalty); // weapon grade penalty, skill 6209 in epilogue
		this.writeD(this.armorExpertisePenalty); // armor grade penalty, skill 6213 in epilogue
		this.writeD(this.CharmOfCourage); // Charm of Courage, "Prevents experience value decreasing if killed during a siege war".
		this.writeD(this.DeathPenaltyLevel); // Death Penalty max lvl 15, "Combat ability is decreased due to death."
		this.writeD(this.ConsumedSouls);
	}
}