package l2mv.gameserver.network.clientpackets;

import l2mv.commons.lang.ArrayUtils;
import l2mv.gameserver.data.xml.holder.SkillAcquireHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SkillLearn;
import l2mv.gameserver.model.base.AcquireType;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.AcquireSkillInfo;
import l2mv.gameserver.tables.SkillTable;

/**
 * Reworked: VISTALL
 */
public class RequestAquireSkillInfo extends L2GameClientPacket
{
	private int _id;
	private int _level;
	private AcquireType _type;

	@Override
	protected void readImpl()
	{
		this._id = this.readD();
		this._level = this.readD();
		this._type = ArrayUtils.valid(AcquireType.VALUES, this.readD());
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null || player.getTransformation() != 0 || SkillTable.getInstance().getInfo(this._id, this._level) == null || this._type == null)
		{
			return;
		}

		NpcInstance trainer = player.getLastNpc();
		if ((trainer == null || player.getDistance(trainer.getX(), trainer.getY()) > Creature.INTERACTION_DISTANCE) && !player.isGM())
		{
			return;
		}

		SkillLearn skillLearn = SkillAcquireHolder.getInstance().getSkillLearn(player, this._id, this._level, this._type);
		if (skillLearn == null)
		{
			return;
		}

		this.sendPacket(new AcquireSkillInfo(this._type, skillLearn));
	}
}