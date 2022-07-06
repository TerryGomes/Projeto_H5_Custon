package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.entity.events.impl.AbstractFightClub;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class NpcInfoPoly extends L2GameServerPacket
{
	// ddddddddddddddddddffffdddcccccSSddd dddddccffddddccd
	private Creature _obj;
	private int _x, _y, _z, _heading;
	private int _npcId;
	private boolean _isSummoned, _isRunning, _isInCombat, _isAlikeDead;
	private int _mAtkSpd, _pAtkSpd;
	private int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, _flRunSpd, _flWalkSpd, _flyRunSpd, _flyWalkSpd;
	private int _rhand, _lhand;
	private String _name, _title;
	private int _abnormalEffect, _abnormalEffect2;
	private double colRadius, colHeight;
	private TeamType _team;

	public NpcInfoPoly(Player cha)
	{
		_obj = cha;
		_npcId = cha.getPolyId();
		NpcTemplate template = NpcHolder.getInstance().getTemplate(_npcId);
		_rhand = 0;
		_lhand = 0;
		_isSummoned = false;
		colRadius = template.collisionRadius;
		colHeight = template.collisionHeight;
		_x = _obj.getX();
		_y = _obj.getY();
		_z = _obj.getZ();
		_rhand = template.rhand;
		_lhand = template.lhand;
		_heading = cha.getHeading();
		_mAtkSpd = cha.getMAtkSpd();
		_pAtkSpd = cha.getPAtkSpd();
		_runSpd = cha.getRunSpeed();
		_walkSpd = cha.getWalkSpeed();
		_swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
		_swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
		_isRunning = cha.isRunning();
		_isInCombat = cha.isInCombat();
		_isAlikeDead = cha.isAlikeDead();
		_name = cha.getName();
		_title = cha.getTitle();
		_abnormalEffect = cha.getAbnormalEffect();
		_abnormalEffect2 = cha.getAbnormalEffect2();
		_team = cha.getTeam();

		if (cha.isInFightClub())
		{
			AbstractFightClub fightClubEvent = cha.getFightClubEvent();
			_name = fightClubEvent.getVisibleName(cha, _name, false);
			_title = fightClubEvent.getVisibleTitle(cha, _title, false);
		}
	}

	@Override
	protected final void writeImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		writeC(0x0c);
		writeD(_obj.getObjectId());
		writeD(_npcId + 1000000); // npctype id
		writeD(0x00);// _activeChar.getKarma() > 0 ? 1 : 0
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(_heading);
		writeD(0x00);
		writeD(_mAtkSpd);
		writeD(_pAtkSpd);
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_swimRunSpd/* 0x32 */); // swimspeed
		writeD(_swimWalkSpd/* 0x32 */); // swimspeed
		writeD(_flRunSpd);
		writeD(_flWalkSpd);
		writeD(_flyRunSpd);
		writeD(_flyWalkSpd);
		writeF(1/* _cha.getProperMultiplier() */);
		writeF(1/* _cha.getAttackSpeedMultiplier() */);
		writeF(colRadius);
		writeF(colHeight);
		writeD(_rhand); // right hand weapon
		writeD(0); // chest
		writeD(_lhand); // left hand weapon
		writeC(1); // name above char 1=true ... ??
		writeC(_isRunning ? 1 : 0);
		writeC(_isInCombat ? 1 : 0);
		writeC(_isAlikeDead ? 1 : 0);
		writeC(_isSummoned ? 2 : 0); // invisible ?? 0=false 1=true 2=summoned (only works if model has a summon animation)
		writeD(-1); // High Five NPCString ID
		writeS(_name);
		writeD(-1); // High Five NPCString ID
		writeS(_title);// gmSeeInvis ? "Invisible" : _activeChar.getAppearance().getVisibleTitle()
		writeD(0);// _activeChar.getAppearance().getTitleColor() - 0 - client default
		writeD(0); // pvp flag
		writeD(0000); // hmm karma ??

		writeD(_abnormalEffect);

		writeD(0000); // clan id
		writeD(0000); // crest id
		writeD(0000); // ally id
		writeD(0000); // all crest
		writeC(0000); // is Flying 2 / 0
		writeC(_team.ordinal()); // Team
		writeF(colRadius); // colRadius
		writeF(colHeight); // colHeight
		writeD(0x00); // enchant effect
		writeD(0x00); // is Flying again?
		writeD(0x00);
		writeD(0x00); // CT1.5 Pet form and skills, Color effect

		writeC(0x01); // targetable
		writeC(0x01); // show name
		writeD(_abnormalEffect2);
		writeD(0x00);
	}
}