package l2f.gameserver.model.instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.data.xml.holder.CharTemplateHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.network.serverpackets.AutoAttackStart;
import l2f.gameserver.network.serverpackets.CharInfo;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;
import l2f.gameserver.skills.effects.EffectCubic;
import l2f.gameserver.tables.FakePlayersTable;
import l2f.gameserver.templates.PlayerTemplate;
import l2f.gameserver.templates.npc.FakePlayerTemplate;
import l2f.gameserver.templates.npc.IFakePlayer;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;

public class FakePcInstance extends NpcInstance implements IFakePlayer
{
	private static final Logger LOG = LoggerFactory.getLogger(FakePcInstance.class);

	private static final Location NO_FISH_LOCATION = new Location(0, 0, 0);
	private static final int WEAPON_GLOW_AUGMENT = 531041839;

	private FakePlayerTemplate _fakeTemplate;

	public FakePcInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		setFakeTemplate(template.getFakePlayerTemplate());
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		if (getFakeTemplate() == null)
		{
			return Collections.emptyList();
		}
		final List<L2GameServerPacket> list = new ArrayList<L2GameServerPacket>(3);
		list.add(new CharInfo(this));
		if (isInCombat())
		{
			list.add(new AutoAttackStart(getObjectId()));
		}
		if (isMoving || isFollow)
		{
			list.add(movePacket());
		}
		return list;
	}

	@Override
	public void broadcastCharInfoImpl()
	{
		if (getFakeTemplate() == null)
		{
			return;
		}
		for (Player player : World.getAroundPlayers(this))
		{
			player.sendPacket(new CharInfo(this));
		}
	}

	@Override
	protected void onDelete()
	{
		if (_fakeTemplate != null)
		{
			FakePlayersTable.getRealTimeFakePlayers().remove(_fakeTemplate.getName());
		}

		super.onDelete();
	}

	public void setFakeTemplate(FakePlayerTemplate fakeTemplate)
	{
		_fakeTemplate = fakeTemplate;
		if (_fakeTemplate == null)
		{
			LOG.error("Fake Player Template is NULL for npc: " + getTemplate().name + " Id: " + getTemplate().npcId);
		}
		// Synerge - Add the fake to the table
		else
		{
			FakePlayersTable.getRealTimeFakePlayers().add(_fakeTemplate.getName());
		}
	}

	private FakePlayerTemplate getFakeTemplate()
	{
		return _fakeTemplate;
	}

	@Override
	public String getName()
	{
		return getFakeTemplate().getName();
	}

	@Override
	public String getTitle()
	{
		return getFakeTemplate().getTitle();
	}

	@Override
	public Location getLocation()
	{
		return getLoc();
	}

	@Override
	public int getPvPFlag()
	{
		return getFakeTemplate().isHasPvpFlag() ? 1 : 0;
	}

	@Override
	public int getRecommendations()
	{
		return getFakeTemplate().getRecommends();
	}

	@Override
	public int getNameColor()
	{
		return getFakeTemplate().getNameColor();
	}

	@Override
	public int getTitleColor()
	{
		return getFakeTemplate().getTitleColor();
	}

	@Override
	public int getRace()
	{
		return getFakeTemplate().getRace().ordinal();
	}

	@Override
	public int getSex()
	{
		return getFakeTemplate().getSex();
	}

	@Override
	public int getBaseClass()
	{
		return getFakeTemplate().getClassId();
	}

	@Override
	public int getClassId()
	{
		return getFakeTemplate().getClassId();
	}

	@Override
	public double getCollisionRadius()
	{
		final PlayerTemplate template = CharTemplateHolder.getInstance().getTemplate(getClassId(), getSex() == 1);
		return template.collisionRadius;
	}

	@Override
	public double getCollisionHeight()
	{
		final PlayerTemplate template = CharTemplateHolder.getInstance().getTemplate(getClassId(), getSex() == 1);
		return template.collisionHeight;
	}

	@Override
	public int getHairStyle()
	{
		return getFakeTemplate().getHairStyle();
	}

	@Override
	public int getHairColor()
	{
		return getFakeTemplate().getHairColor();
	}

	@Override
	public int getFace()
	{
		return getFakeTemplate().getFace();
	}

	@Override
	public int getNoble()
	{
		return getFakeTemplate().isHero() ? 1 : 0;
	}

	@Override
	public int getHero()
	{
		return getFakeTemplate().isHero() ? 1 : 0;
	}

	@Override
	public boolean isInvis()
	{
		return false;
	}

	@Override
	public int getTransform()
	{
		return 0;
	}

	@Override
	public int getCursedWeaponLevel()
	{
		return 0;
	}

	@Override
	public int getAgathion()
	{
		return getFakeTemplate().getAgathion();
	}

	@Override
	public EffectCubic[] getCubics()
	{
		return new EffectCubic[0];
	}

	@Override
	public int getFishing()
	{
		return 0;
	}

	@Override
	public Location getFishLocation()
	{
		return FakePcInstance.NO_FISH_LOCATION;
	}

	@Override
	public int getMountType()
	{
		return 0;
	}

	@Override
	public int getMountId()
	{
		return 0;
	}

	@Override
	public int getSit()
	{
		return getFakeTemplate().isSitting() ? 0 : 1;
	}

	@Override
	public int getRun()
	{
		return getFakeTemplate().isRunning() ? 1 : 0;
	}

	@Override
	public int getCombat()
	{
		return getFakeTemplate().isInCombat() ? 1 : 0;
	}

	@Override
	public int getDead()
	{
		return getFakeTemplate().isDead() ? 1 : 0;
	}

	@Override
	public int getPrivateStore()
	{
		return getFakeTemplate().isStore() ? 1 : 0;
	}

	@Override
	public double getSpeedMove()
	{
		return getMovementSpeedMultiplier();
	}

	@Override
	public double getSpeedAttack()
	{
		return getAttackSpeedMultiplier();
	}

	@Override
	public int getSwimSpd()
	{
		return getSwimSpeed();
	}

	@Override
	public int getFlySpd()
	{
		return 0;
	}

	@Override
	public int getFlyWalkSpd()
	{
		return 0;
	}

	@Override
	public int[][] getInventory()
	{
		final boolean weaponGlow = getFakeTemplate().getWeaponGlow();
		final Map<Integer, Integer> templateInventory = getFakeTemplate().getInventory();
		final int[][] inventory = new int[26][2];
		for (int paperdollId : CharInfo.PAPERDOLL_ORDER)
		{
			inventory[paperdollId][0] = (templateInventory.containsKey(paperdollId) ? templateInventory.get(paperdollId) : 0);
			inventory[paperdollId][1] = ((paperdollId == 7 || paperdollId == 14) && weaponGlow ? WEAPON_GLOW_AUGMENT : 0);
		}
		return inventory;
	}

	@Override
	public int getEnchant()
	{
		return getFakeTemplate().getWeaponEnchant();
	}

	@Override
	public boolean isPartyRoomLeader()
	{
		return false;
	}

	@Override
	public int getClanId()
	{
		return 0;
	}

	@Override
	public int getClanCrestId()
	{
		return 0;
	}

	@Override
	public int getLargeClanCrestId()
	{
		return 0;
	}

	@Override
	public int getAllyId()
	{
		return 0;
	}

	@Override
	public int getAllyCrestId()
	{
		return 0;
	}

	@Override
	public int getPledgeClass()
	{
		return 0;
	}

	@Override
	public int getPledgeType()
	{
		return 0;
	}

	@Override
	public int getClanReputationScore()
	{
		return 0;
	}

	@Override
	public int getClanBoatObjectId()
	{
		return 0;
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return false;
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}

	@Override
	public boolean isFakePlayer()
	{
		return true;
	}
}
