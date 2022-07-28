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
		this._obj = cha;
		this._npcId = cha.getPolyId();
		NpcTemplate template = NpcHolder.getInstance().getTemplate(this._npcId);
		this._rhand = 0;
		this._lhand = 0;
		this._isSummoned = false;
		this.colRadius = template.collisionRadius;
		this.colHeight = template.collisionHeight;
		this._x = this._obj.getX();
		this._y = this._obj.getY();
		this._z = this._obj.getZ();
		this._rhand = template.rhand;
		this._lhand = template.lhand;
		this._heading = cha.getHeading();
		this._mAtkSpd = cha.getMAtkSpd();
		this._pAtkSpd = cha.getPAtkSpd();
		this._runSpd = cha.getRunSpeed();
		this._walkSpd = cha.getWalkSpeed();
		this._swimRunSpd = this._flRunSpd = this._flyRunSpd = this._runSpd;
		this._swimWalkSpd = this._flWalkSpd = this._flyWalkSpd = this._walkSpd;
		this._isRunning = cha.isRunning();
		this._isInCombat = cha.isInCombat();
		this._isAlikeDead = cha.isAlikeDead();
		this._name = cha.getName();
		this._title = cha.getTitle();
		this._abnormalEffect = cha.getAbnormalEffect();
		this._abnormalEffect2 = cha.getAbnormalEffect2();
		this._team = cha.getTeam();

		if (cha.isInFightClub())
		{
			AbstractFightClub fightClubEvent = cha.getFightClubEvent();
			this._name = fightClubEvent.getVisibleName(cha, this._name, false);
			this._title = fightClubEvent.getVisibleTitle(cha, this._title, false);
		}
	}

	@Override
	protected final void writeImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		this.writeC(0x0c);
		this.writeD(this._obj.getObjectId());
		this.writeD(this._npcId + 1000000); // npctype id
		this.writeD(0x00);// _activeChar.getKarma() > 0 ? 1 : 0
		this.writeD(this._x);
		this.writeD(this._y);
		this.writeD(this._z);
		this.writeD(this._heading);
		this.writeD(0x00);
		this.writeD(this._mAtkSpd);
		this.writeD(this._pAtkSpd);
		this.writeD(this._runSpd);
		this.writeD(this._walkSpd);
		this.writeD(this._swimRunSpd/* 0x32 */); // swimspeed
		this.writeD(this._swimWalkSpd/* 0x32 */); // swimspeed
		this.writeD(this._flRunSpd);
		this.writeD(this._flWalkSpd);
		this.writeD(this._flyRunSpd);
		this.writeD(this._flyWalkSpd);
		this.writeF(1/* _cha.getProperMultiplier() */);
		this.writeF(1/* _cha.getAttackSpeedMultiplier() */);
		this.writeF(this.colRadius);
		this.writeF(this.colHeight);
		this.writeD(this._rhand); // right hand weapon
		this.writeD(0); // chest
		this.writeD(this._lhand); // left hand weapon
		this.writeC(1); // name above char 1=true ... ??
		this.writeC(this._isRunning ? 1 : 0);
		this.writeC(this._isInCombat ? 1 : 0);
		this.writeC(this._isAlikeDead ? 1 : 0);
		this.writeC(this._isSummoned ? 2 : 0); // invisible ?? 0=false 1=true 2=summoned (only works if model has a summon animation)
		this.writeD(-1); // High Five NPCString ID
		this.writeS(this._name);
		this.writeD(-1); // High Five NPCString ID
		this.writeS(this._title);// gmSeeInvis ? "Invisible" : _activeChar.getAppearance().getVisibleTitle()
		this.writeD(0);// _activeChar.getAppearance().getTitleColor() - 0 - client default
		this.writeD(0); // pvp flag
		this.writeD(0000); // hmm karma ??

		this.writeD(this._abnormalEffect);

		this.writeD(0000); // clan id
		this.writeD(0000); // crest id
		this.writeD(0000); // ally id
		this.writeD(0000); // all crest
		this.writeC(0000); // is Flying 2 / 0
		this.writeC(this._team.ordinal()); // Team
		this.writeF(this.colRadius); // colRadius
		this.writeF(this.colHeight); // colHeight
		this.writeD(0x00); // enchant effect
		this.writeD(0x00); // is Flying again?
		this.writeD(0x00);
		this.writeD(0x00); // CT1.5 Pet form and skills, Color effect

		this.writeC(0x01); // targetable
		this.writeC(0x01); // show name
		this.writeD(this._abnormalEffect2);
		this.writeD(0x00);
	}
}