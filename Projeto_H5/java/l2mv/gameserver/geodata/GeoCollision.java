package l2mv.gameserver.geodata;

import l2mv.commons.geometry.Shape;

public interface GeoCollision
{
	public Shape getShape();

	public byte[][] getGeoAround();

	public void setGeoAround(byte[][] geo);

	public boolean isConcrete();
}
