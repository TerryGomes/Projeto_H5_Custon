package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.instances.NpcInstance;

public class MonRaceInfo extends L2GameServerPacket
{
	private int _unknown1;
	private int _unknown2;
	private NpcInstance[] _monsters;
	private int[][] _speeds;

	public MonRaceInfo(int unknown1, int unknown2, NpcInstance[] monsters, int[][] speeds)
	{
		/*
		 * -1 0 to initial the race
		 * 0 15322 to start race
		 * 13765 -1 in middle of race
		 * -1 0 to end the race
		 */
		this._unknown1 = unknown1;
		this._unknown2 = unknown2;
		this._monsters = monsters;
		this._speeds = speeds;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xe3);

		this.writeD(this._unknown1);
		this.writeD(this._unknown2);
		this.writeD(8);

		for (int i = 0; i < 8; i++)
		{
			// _log.info.println("MOnster "+(i+1)+" npcid "+_monsters[i].getNpcTemplate().getNpcId());
			this.writeD(this._monsters[i].getObjectId()); // npcObjectID
			this.writeD(this._monsters[i].getTemplate().npcId + 1000000); // npcID
			this.writeD(14107); // origin X
			this.writeD(181875 + 58 * (7 - i)); // origin Y
			this.writeD(-3566); // origin Z
			this.writeD(12080); // end X
			this.writeD(181875 + 58 * (7 - i)); // end Y
			this.writeD(-3566); // end Z
			this.writeF(this._monsters[i].getColHeight()); // coll. height
			this.writeF(this._monsters[i].getColRadius()); // coll. radius
			this.writeD(120); // ?? unknown
			for (int j = 0; j < 20; j++)
			{
				this.writeC(this._unknown1 == 0 ? this._speeds[i][j] : 0);
			}
			this.writeD(0);
			this.writeD(0x00); // ? GraciaFinal
		}
	}
}