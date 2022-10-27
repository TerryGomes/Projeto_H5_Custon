package l2mv.gameserver.network.serverpackets;

import org.apache.commons.lang3.StringUtils;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Summon;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.pledge.Alliance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.utils.Location;

public class NpcInfo extends L2GameServerPacket
{
	private boolean can_writeImpl = false;
	private int _npcObjId, _npcId, running, incombat, dead, _showSpawnAnimation;
	private int _runSpd, _walkSpd, _mAtkSpd, _pAtkSpd, _rhand, _lhand, _enchantEffect;
	private int karma, pvp_flag, _abnormalEffect, _abnormalEffect2, clan_id, clan_crest_id, ally_id, ally_crest_id, _formId, _titleColor;
	private double colHeight, colRadius, currentColHeight, currentColRadius;
	private boolean _isAttackable, _isNameAbove, isFlying;
	private Location _loc;
	private String _name = StringUtils.EMPTY;
	private String _title = StringUtils.EMPTY;
	private boolean _showName;
	private int _state;
	private NpcString _nameNpcString = NpcString.NONE;
	private NpcString _titleNpcString = NpcString.NONE;
	private TeamType _team;

	public NpcInfo(NpcInstance cha, Creature attacker)
	{
		this._npcId = cha.getDisplayId() != 0 ? cha.getDisplayId() : cha.getTemplate().npcId;
		this._isAttackable = attacker != null && cha.isAutoAttackable(attacker);
		this._rhand = cha.getRightHandItem();
		this._lhand = cha.getLeftHandItem();

		// Synerge - Support for npcs with enchanted weapons
		this._enchantEffect = cha.getTemplate().getEnchantLvl();
		if (Config.SERVER_SIDE_NPC_NAME || cha.getTemplate().displayId != 0 || cha.getName() != cha.getTemplate().name)
		{
			this._name = cha.getName();
		}
		if (Config.SERVER_SIDE_NPC_TITLE || cha.getTemplate().displayId != 0 || cha.getTitle() != cha.getTemplate().title)
		{
			this._title = cha.getTitle();
			if (Config.SERVER_SIDE_NPC_TITLE_ETC)
			{
				if (cha.isMonster())
				{
					if (this._title.isEmpty())
					{
						this._title = "LvL " + cha.getLevel();
					}
				}
			}
		}
		this._showSpawnAnimation = cha.getSpawnAnimation();
		this._showName = cha.isShowName();
		this._state = cha.getNpcState();
		this._nameNpcString = cha.getNameNpcString();
		this._titleNpcString = cha.getTitleNpcString();

		this.common(cha);
	}

	public NpcInfo(Summon cha, Creature attacker)
	{
		if (cha.getPlayer() != null && cha.getPlayer().isInvisible())
		{
			return;
		}

		this._npcId = cha.getTemplate().npcId;
		this._isAttackable = cha.isAutoAttackable(attacker);
		this._rhand = 0;
		this._lhand = 0;
		this._enchantEffect = 0;
		this._showName = true;
		this._name = cha.getName();
		this._title = cha.getTitle();
		this._showSpawnAnimation = cha.getSpawnAnimation();

		this.common(cha);
	}

	private void common(Creature cha)
	{
		this.colHeight = cha.getTemplate().collisionHeight;
		this.colRadius = cha.getTemplate().collisionRadius;
		this.currentColHeight = cha.getColHeight();
		this.currentColRadius = cha.getColRadius();
		this._npcObjId = cha.getObjectId();
		this._loc = cha.getLoc();
		this._mAtkSpd = cha.getMAtkSpd();
		//
		Clan clan = cha.getClan();
		Alliance alliance = clan == null ? null : clan.getAlliance();
		//
		this.clan_id = clan == null ? 0 : clan.getClanId();
		this.clan_crest_id = clan == null ? 0 : clan.getCrestId();
		//
		this.ally_id = alliance == null ? 0 : alliance.getAllyId();
		this.ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();

		this._runSpd = cha.getRunSpeed();
		this._walkSpd = cha.getWalkSpeed();
		this.karma = cha.getKarma();
		this.pvp_flag = cha.getPvpFlag();
		this._pAtkSpd = cha.getPAtkSpd();
		this.running = cha.isRunning() ? 1 : 0;
		this.incombat = cha.isInCombat() ? 1 : 0;
		this.dead = cha.isAlikeDead() ? 1 : 0;
		this._abnormalEffect = cha.getAbnormalEffect();
		this._abnormalEffect2 = cha.getAbnormalEffect2();
		this.isFlying = cha.isFlying();
		this._team = cha.getTeam();
		this._formId = cha.getFormId();
		this._isNameAbove = cha.isNameAbove();
		this._titleColor = (cha.isSummon() || cha.isPet()) ? 1 : 0;

		this.can_writeImpl = true;
	}

	public NpcInfo update(Integer _showSpawnAnimation)
	{
		this._showSpawnAnimation = _showSpawnAnimation;
		return this;
	}

	@Override
	protected final void writeImpl()
	{
		if (!this.can_writeImpl)
		{
			return;
		}

		this.writeC(0x0c);
		// ddddddddddddddddddffffdddcccccSSddddddddccffddddccd
		this.writeD(this._npcObjId);
		this.writeD(this._npcId + 1000000); // npctype id c4
		this.writeD(this._isAttackable ? 1 : 0);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z + Config.CLIENT_Z_SHIFT);
		this.writeD(this._loc.h);
		this.writeD(0x00);
		this.writeD(this._mAtkSpd);
		this.writeD(this._pAtkSpd);
		this.writeD(this._runSpd);
		this.writeD(this._walkSpd);
		this.writeD(this._runSpd /* _swimRunSpd *//* 0x32 */); // swimspeed
		this.writeD(this._walkSpd/* _swimWalkSpd *//* 0x32 */); // swimspeed
		this.writeD(this._runSpd/* _flRunSpd */);
		this.writeD(this._walkSpd/* _flWalkSpd */);
		this.writeD(this._runSpd/* _flyRunSpd */);
		this.writeD(this._walkSpd/* _flyWalkSpd */);
		this.writeF(1.100000023841858); // взято из клиента
		this.writeF(this._pAtkSpd / 277.478340719);
		this.writeF(this.colRadius);
		this.writeF(this.colHeight);
		this.writeD(this._rhand); // right hand weapon
		this.writeD(0); // TODO chest
		this.writeD(this._lhand); // left hand weapon
		this.writeC(this._isNameAbove ? 1 : 0); // 2.2: name above char 1=true ... ??; 2.3: 1 - normal, 2 - dead
		this.writeC(this.running);
		this.writeC(this.incombat);
		this.writeC(this.dead);
		this.writeC(this._showSpawnAnimation); // invisible ?? 0=false 1=true 2=summoned (only works if model has a summon animation)
		this.writeD(this._nameNpcString.getId());
		this.writeS(this._name);
		this.writeD(this._titleNpcString.getId());
		this.writeS(this._title);
		this.writeD(this._titleColor); // 0- светло зеленый титул(моб), 1 - светло синий(пет)/отображение текущего МП
		this.writeD(this.pvp_flag);
		this.writeD(this.karma); // hmm karma ??
		this.writeD(this._abnormalEffect); // C2
		this.writeD(this.clan_id);
		this.writeD(this.clan_crest_id);
		this.writeD(this.ally_id);
		this.writeD(this.ally_crest_id);
		this.writeC(this.isFlying ? 2 : 0); // C2
		this.writeC(this._team.ordinal()); // team aura 1-blue, 2-red
		this.writeF(this.currentColRadius); // тут что-то связанное с colRadius
		this.writeF(this.currentColHeight); // тут что-то связанное с colHeight
		this.writeD(this._enchantEffect); // C4
		this.writeD(0x00); // writeD(_npc.isFlying() ? 1 : 0); // C6
		this.writeD(0x00);
		this.writeD(this._formId);// great wolf type
		this.writeC(this._showName ? 0x01 : 0x00); // show name
		this.writeC(this._showName ? 0x01 : 0x00); // show title
		this.writeD(this._abnormalEffect2);
		this.writeD(this._state);
	}
}