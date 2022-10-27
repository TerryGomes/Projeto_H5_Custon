package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.Location;

/**
 * @author SYS
 * @date 08/9/2007
 * Format: chdddddc
 *
 * Пример пакета:
 * D0
 * 2F 00
 * E4 35 00 00 x
 * 62 D1 02 00 y
 * 22 F2 FF FF z
 * 90 05 00 00 skill id
 * 00 00 00 00 ctrlPressed
 * 00 shiftPressed
 */
public class RequestExMagicSkillUseGround extends L2GameClientPacket
{
	private Location _loc = new Location();
	private int _skillId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;

	/**
	 * packet type id 0xd0
	 */
	@Override
	protected void readImpl()
	{
		this._loc.x = this.readD();
		this._loc.y = this.readD();
		this._loc.z = this.readD();
		this._skillId = this.readD();
		this._ctrlPressed = this.readD() != 0;
		this._shiftPressed = this.readC() != 0;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		Skill skill = SkillTable.getInstance().getInfo(this._skillId, activeChar.getSkillLevel(this._skillId));
		if (skill != null)
		{
			// В режиме трансформации доступны только скилы трансформы
			if ((skill.getAddedSkills().length == 0) || (activeChar.getTransformation() != 0 && !activeChar.getAllSkills().contains(skill)))
			{
				return;
			}

			if (!activeChar.isInRange(this._loc, skill.getCastRange()))
			{
				activeChar.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
				activeChar.sendActionFailed();
				return;
			}

			Creature target = skill.getAimingTarget(activeChar, activeChar.getTarget());

			if (skill.checkCondition(activeChar, target, this._ctrlPressed, this._shiftPressed, true))
			{
				activeChar.setGroundSkillLoc(this._loc);
				activeChar.getAI().Cast(skill, target, this._ctrlPressed, this._shiftPressed);
			}
			else
			{
				activeChar.sendActionFailed();
			}
		}
		else
		{
			activeChar.sendActionFailed();
		}
	}
}