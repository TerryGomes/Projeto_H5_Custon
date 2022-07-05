/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2f.gameserver.masteriopack.rankpvpsystem;

/**
 * This class contains all possible states for database updater.
 * @author Masterio
 */
public class DBStatus
{
	/**
	 * The NONE status mean the object is not changed in model and the update of the database is not required.
	 */
	public static final byte NONE = 0;
	/**
	 * The INSERTED status mean the object is added to model and the object data need to be insert into database.
	 */
	public static final byte INSERTED = 1;
	/**
	 * The UPDATED status mean the object is updated in model and the update of the database is required.
	 */
	public static final byte UPDATED = 2;
	/**
	 * The DELETED status mean the object is deleted from model and the object data need to be delete from database.
	 */
	// public static final byte DELETED = 3; // UNSUSED
}
