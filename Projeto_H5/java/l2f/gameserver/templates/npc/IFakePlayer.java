package l2f.gameserver.templates.npc;

import l2f.gameserver.model.base.TeamType;
import l2f.gameserver.skills.effects.EffectCubic;
import l2f.gameserver.utils.Location;

public interface IFakePlayer
{
	int getObjectId();

	Location getLocation();

	String getName();

	String getTitle();

	int getPvPFlag();

	int getKarma();

	int getRecommendations();

	int getNameColor();

	int getTitleColor();

	int getRace();

	int getSex();

	int getBaseClass();

	int getClassId();

	double getCollisionRadius();

	double getCollisionHeight();

	int getHairStyle();

	int getHairColor();

	int getFace();

	int getAbnormalEffect();

	int getAbnormalEffect2();

	int getNoble();

	int getHero();

	boolean isInvis();

	int getTransform();

	int getCursedWeaponLevel();

	int getAgathion();

	EffectCubic[] getCubics();

	TeamType getTeam();

	int getFishing();

	Location getFishLocation();

	int getMountType();

	int getMountId();

	int getSit();

	int getRun();

	int getCombat();

	int getDead();

	int getPrivateStore();

	int getMAtkSpd();

	int getPAtkSpd();

	int getRunSpeed();

	int getWalkSpeed();

	double getSpeedMove();

	double getSpeedAttack();

	int getSwimSpd();

	int getFlySpd();

	int getFlyWalkSpd();

	boolean isFlying();

	int[][] getInventory();

	int getEnchant();

	boolean isPartyRoomLeader();

	int getClanId();

	int getClanCrestId();

	int getLargeClanCrestId();

	int getAllyId();

	int getAllyCrestId();

	int getPledgeClass();

	int getPledgeType();

	int getClanReputationScore();

	int getClanBoatObjectId();
}
