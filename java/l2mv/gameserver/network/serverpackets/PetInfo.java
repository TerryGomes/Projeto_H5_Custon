package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Summon;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.entity.events.impl.AbstractFightClub;
import l2mv.gameserver.tables.PetDataTable;
import l2mv.gameserver.utils.Location;

public class PetInfo extends L2GameServerPacket
{
	private int _runSpd, _walkSpd, MAtkSpd, PAtkSpd, pvp_flag, karma, rideable;
	private int _type, obj_id, npc_id, runing, incombat, dead, _sp, level, _abnormalEffect, _abnormalEffect2;
	private int curFed, maxFed, curHp, maxHp, curMp, maxMp, curLoad, maxLoad;
	private int PAtk, PDef, MAtk, MDef, Accuracy, Evasion, Crit, sps, ss, type, _showSpawnAnimation;
	private Location _loc;
	private double col_redius, col_height;
	private long exp, exp_this_lvl, exp_next_lvl;
	private String _name, title;
	private TeamType _team;

	public PetInfo(Summon summon)
	{
		this._type = summon.getSummonType();
		this.obj_id = summon.getObjectId();
		this.npc_id = summon.getTemplate().npcId;
		this._loc = summon.getLoc();
		this.MAtkSpd = summon.getMAtkSpd();
		this.PAtkSpd = summon.getPAtkSpd();
		this._runSpd = summon.getRunSpeed();
		this._walkSpd = summon.getWalkSpeed();
		this.col_redius = summon.getColRadius();
		this.col_height = summon.getColHeight();
		this.runing = summon.isRunning() ? 1 : 0;
		this.incombat = summon.isInCombat() ? 1 : 0;
		this.dead = summon.isAlikeDead() ? 1 : 0;
		this._name = summon.getName().equalsIgnoreCase(summon.getTemplate().name) ? "" : summon.getName();
		this.title = summon.getTitle();
		this.pvp_flag = summon.getPvpFlag();
		this.karma = summon.getKarma();
		this.curFed = summon.getCurrentFed();
		this.maxFed = summon.getMaxFed();
		this.curHp = (int) summon.getCurrentHp();
		this.maxHp = summon.getMaxHp();
		this.curMp = (int) summon.getCurrentMp();
		this.maxMp = summon.getMaxMp();
		this._sp = summon.getSp();
		this.level = summon.getLevel();
		this.exp = summon.getExp();
		this.exp_this_lvl = summon.getExpForThisLevel();
		this.exp_next_lvl = summon.getExpForNextLevel();
		this.curLoad = summon.isPet() ? summon.getInventory().getTotalWeight() : 0;
		this.maxLoad = summon.getMaxLoad();
		this.PAtk = summon.getPAtk(null);
		this.PDef = summon.getPDef(null);
		this.MAtk = summon.getMAtk(null, null);
		this.MDef = summon.getMDef(null, null);
		this.Accuracy = summon.getAccuracy();
		this.Evasion = summon.getEvasionRate(null);
		this.Crit = summon.getCriticalHit(null, null);
		this._abnormalEffect = summon.getAbnormalEffect();
		this._abnormalEffect2 = summon.getAbnormalEffect2();
		// В режиме трансформации значек mount/dismount не отображается
		if (summon.getPlayer().getTransformation() != 0)
		{
			this.rideable = 0; // not rideable
		}
		else
		{
			this.rideable = PetDataTable.isMountable(this.npc_id) ? 1 : 0;
		}
		this._team = summon.getTeam();
		this.ss = summon.getSoulshotConsumeCount();
		this.sps = summon.getSpiritshotConsumeCount();
		this._showSpawnAnimation = summon.getSpawnAnimation();
		this.type = summon.getFormId();

		if (summon.getPlayer().isInFightClub())
		{
			AbstractFightClub fightClubEvent = summon.getPlayer().getFightClubEvent();
			this._name = fightClubEvent.getVisibleName(summon.getPlayer(), this._name, false);
			this.title = fightClubEvent.getVisibleTitle(summon.getPlayer(), this.title, false);
		}
	}

	public PetInfo update(Integer _showSpawnAnimation)
	{
		this._showSpawnAnimation = _showSpawnAnimation;
		return this;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xb2);
		this.writeD(this._type);
		this.writeD(this.obj_id);
		this.writeD(this.npc_id + 1000000);
		this.writeD(0); // 1=attackable
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeD(this._loc.h);
		this.writeD(0);
		this.writeD(this.MAtkSpd);
		this.writeD(this.PAtkSpd);
		this.writeD(this._runSpd);
		this.writeD(this._walkSpd);
		this.writeD(this._runSpd/* _swimRunSpd */);
		this.writeD(this._walkSpd/* _swimWalkSpd */);
		this.writeD(this._runSpd/* _flRunSpd */);
		this.writeD(this._walkSpd/* _flWalkSpd */);
		this.writeD(this._runSpd/* _flyRunSpd */);
		this.writeD(this._walkSpd/* _flyWalkSpd */);
		this.writeF(1/* _cha.getProperMultiplier() */);
		this.writeF(1/* _cha.getAttackSpeedMultiplier() */);
		this.writeF(this.col_redius);
		this.writeF(this.col_height);
		this.writeD(0); // right hand weapon
		this.writeD(0);
		this.writeD(0); // left hand weapon
		this.writeC(1); // name above char 1=true ... ??
		this.writeC(this.runing); // running=1
		this.writeC(this.incombat); // attacking 1=true
		this.writeC(this.dead); // dead 1=true
		this.writeC(this._showSpawnAnimation); // invisible ?? 0=false 1=true 2=summoned (only works if model has a summon animation)
		this.writeD(-1);
		this.writeS(this._name);
		this.writeD(-1);
		this.writeS(this.title);
		this.writeD(1);
		this.writeD(this.pvp_flag); // 0=white, 1=purple, 2=purpleblink, if its greater then karma = purple
		this.writeD(this.karma); // hmm karma ??
		this.writeD(this.curFed); // how fed it is
		this.writeD(this.maxFed); // max fed it can be
		this.writeD(this.curHp); // current hp
		this.writeD(this.maxHp); // max hp
		this.writeD(this.curMp); // current mp
		this.writeD(this.maxMp); // max mp
		this.writeD(this._sp); // sp
		this.writeD(this.level);// lvl
		this.writeQ(this.exp);
		this.writeQ(this.exp_this_lvl); // 0% absolute value
		this.writeQ(this.exp_next_lvl); // 100% absoulte value
		this.writeD(this.curLoad); // weight
		this.writeD(this.maxLoad); // max weight it can carry
		this.writeD(this.PAtk);// patk
		this.writeD(this.PDef);// pdef
		this.writeD(this.MAtk);// matk
		this.writeD(this.MDef);// mdef
		this.writeD(this.Accuracy);// accuracy
		this.writeD(this.Evasion);// evasion
		this.writeD(this.Crit);// critical
		this.writeD(this._runSpd);// speed
		this.writeD(this.PAtkSpd);// atkspeed
		this.writeD(this.MAtkSpd);// casting speed
		this.writeD(this._abnormalEffect); // c2 abnormal visual effect... bleed=1; poison=2; bleed?=4;
		this.writeH(this.rideable);
		this.writeC(0); // c2
		this.writeH(0); // ??
		this.writeC(this._team.ordinal()); // team aura (1 = blue, 2 = red)
		this.writeD(this.ss);
		this.writeD(this.sps);
		this.writeD(this.type);
		this.writeD(this._abnormalEffect2);
	}
}