package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;

/**
 * sample
 * 06 8f19904b 2522d04b 00000000 80 950c0000 4af50000 08f2ffff 0000    - 0 damage (missed 0x80)
 * 06 85071048 bc0e504b 32000000 10 fc41ffff fd240200 a6f5ffff 0100 bc0e504b 33000000 10                                     3....

 * format
 * dddc dddh (ddc)
 *
 */
public class Attack extends L2GameServerPacket
{
	private class Hit
	{
		int _targetId, _damage, _flags;

		Hit(GameObject target, int damage, boolean miss, boolean crit, boolean shld)
		{
			this._targetId = target.getObjectId();
			this._damage = damage;
			if (Attack.this._soulshot)
			{
				this._flags |= 0x10 | Attack.this._grade;
			}
			if (crit)
			{
				this._flags |= 0x20;
			}
			if (shld)
			{
				this._flags |= 0x40;
			}
			if (miss)
			{
				this._flags |= 0x80;
			}
		}
	}

	public final int _attackerId;
	public final boolean _soulshot;
	private final int _grade;
	private final int _x, _y, _z, _tx, _ty, _tz;
	private Hit[] hits;

	public Attack(Creature attacker, Creature target, boolean ss, int grade)
	{
		this._attackerId = attacker.getObjectId();
		this._soulshot = ss;
		this._grade = grade;
		this._x = attacker.getX();
		this._y = attacker.getY();
		this._z = attacker.getZ();
		this._tx = target.getX();
		this._ty = target.getY();
		this._tz = target.getZ();
		this.hits = new Hit[0];
	}

	/**
	 * Add this hit (target, damage, miss, critical, shield) to the Server-Client packet Attack.<BR><BR>
	 */
	public void addHit(GameObject target, int damage, boolean miss, boolean crit, boolean shld)
	{
		// Get the last position in the hits table
		int pos = this.hits.length;

		// Create a new Hit object
		Hit[] tmp = new Hit[pos + 1];

		// Add the new Hit object to hits table
		System.arraycopy(this.hits, 0, tmp, 0, this.hits.length);
		tmp[pos] = new Hit(target, damage, miss, crit, shld);
		this.hits = tmp;
	}

	/**
	 * Return True if the Server-Client packet Attack conatins at least 1 hit.<BR><BR>
	 */
	public boolean hasHits()
	{
		return this.hits.length > 0;
	}

	@Override
	protected final void writeImpl()
	{
		Player activeChar = this.getClient().getActiveChar();

		boolean shouldSeeShots = !(activeChar != null && activeChar.isNotShowBuffAnim());

		this.writeC(0x33);

		this.writeD(this._attackerId);
		this.writeD(this.hits[0]._targetId);
		this.writeD(this.hits[0]._damage);
		this.writeC(shouldSeeShots ? this.hits[0]._flags : 0);
		this.writeD(this._x);
		this.writeD(this._y);
		this.writeD(this._z);
		this.writeH(this.hits.length - 1);
		for (int i = 1; i < this.hits.length; i++)
		{
			this.writeD(this.hits[i]._targetId);
			this.writeD(this.hits[i]._damage);
			this.writeC(shouldSeeShots ? this.hits[i]._flags : 0);
		}
		this.writeD(this._tx);
		this.writeD(this._ty);
		this.writeD(this._tz);
	}
}