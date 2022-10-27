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
package l2mv.gameserver.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to show how you can sort a java.util.Map for values.<br>
 * This also takes care of null and duplicate values present in the map.
 */
@SuppressWarnings("unchecked")
public class ValueSortMap
{
	public Map<Integer, Integer> sortThis(Map<Integer, Integer> map, boolean asc)
	{
		return sortMapByValue(map, asc);
	}

	/**
	 * This method returns the new LinkedHashMap sorted with values for passed Comparator.<br>
	 * If null values exist they will be put in the last of the returned LinkedHashMap.<br>
	 * If there are duplicate values they will come together at the values ordering order but ordering between same multiple values is random.<br>
	 * Passed Map will be intact.
	 *
	 * @param inMap Map to be sorted
	 * @param comparator Values will be sorted as per passed Comparator
	 * @return LinkedHashMap Sorted new LinkedHashMap
	 */
	@SuppressWarnings("rawtypes")
	public static LinkedHashMap sortMapByValue(Map inMap, Comparator comparator)
	{
		return sortMapByValue(inMap, comparator, null);
	}

	/**
	 * This method returns the new LinkedHashMap sorted with values for passed ascendingOrder.<br>
	 * If null values exist they will be put in the last for true value of ascendingOrder or will be put on top of the returned LinkedHashMap for false value of ascendingOrder.<br>
	 * If there are duplicate values they will come together at the values ordering order but ordering between same multiple values is random.<br>
	 * Passed Map will be intact.
	 *
	 * @param inMap Map to be sorted
	 * @param ascendingOrder Values will be sorted as per value of ascendingOrder
	 * @return LinkedHashMap Sorted new LinkedHashMap
	 */
	@SuppressWarnings("rawtypes")
	public static LinkedHashMap sortMapByValue(Map inMap, boolean ascendingOrder)
	{
		return sortMapByValue(inMap, null, ascendingOrder);
	}

	/**
	 * This method returns the new LinkedHashMap sorted with values in ascending order.<br>
	 * If null values exist they will be put in the last of the returned LinkedHashMap.<br>
	 * If there are duplicate values they will come together at the values ordering order but ordering between same multiple values is random.<br>
	 * Passed Map will be intact.
	 *
	 * @param inMap Map to be sorted
	 * @return LinkedHashMap Sorted new LinkedHashMap
	 */
	@SuppressWarnings("rawtypes")
	public static LinkedHashMap sortMapByValue(Map inMap)
	{
		return sortMapByValue(inMap, null, null);
	}

	/**
	 * This method returns the new LinkedHashMap sorted with values.<br>
	 * Values will be sorted as value of passed comparator if ascendingOrder is null or in order of passed ascendingOrder if it is not null.<br>
	 * If null values exist they will be put in the last for true value of ascendingOrder or will be put on top of the returned LinkedHashMap for false value of ascendingOrder.<br>
	 * If there are duplicate values they will come together at the values ordering order but ordering between same multiple values is random.<br>
	 * Passed Map will be intact.
	 *
	 * @param inMap Map to be sorted
	 * @param comparator Values will be sorted as per passed Comparator
	 * @param ascendingOrder Values will be sorted as per value of ascendingOrder
	 * @return LinkedHashMap Sorted new LinkedHashMap
	 */
	@SuppressWarnings("rawtypes")
	private static LinkedHashMap sortMapByValue(Map inMap, Comparator comparator, Boolean ascendingOrder)
	{
		final int iSize = inMap.size();
		// Create new LinkedHashMap that need to be returned
		final LinkedHashMap sortedMap = new LinkedHashMap(iSize);
		final Collection values = inMap.values();
		final ArrayList valueList = new ArrayList(values); // To get List of all values in passed Map
		final HashSet distinctValues = new HashSet(values); // To know the distinct values in passed Map
		// Do handing for null values. remove them from the list that will be used for sorting
		int iNullValueCount = 0; // Total number of null values present in passed Map
		if (distinctValues.contains(null))
		{
			distinctValues.remove(null);
			for (int i = 0; i < valueList.size(); i++)
			{
				if (valueList.get(i) == null)
				{
					valueList.remove(i);
					iNullValueCount++;
					i--;
					continue;
				}
			}
		}
		// Sort the values of the passed Map
		if (ascendingOrder == null)
		{
			// If Boolean ascendingOrder is null, use passed comparator for order of sorting values
			Collections.sort(valueList, comparator);
		}
		else if (ascendingOrder)
		{
			// If Boolean ascendingOrder is not null and is true, sort values in ascending order
			Collections.sort(valueList);
		}
		else
		{
			// If Boolean ascendingOrder is not null and is false, sort values in descending order
			Collections.sort(valueList);
			Collections.reverse(valueList);
		}
		// Check if there are multiple same values exist in passed Map (not considering null values)
		boolean bAllDistinct = true;
		if (iSize != distinctValues.size() + iNullValueCount)
		{
			bAllDistinct = false;
		}
		Object key = null, value = null, sortedValue;
		Set keySet = null;
		Iterator itKeyList = null;
		final HashMap hmTmpMap = new HashMap(iSize);
		final HashMap hmNullValueMap = new HashMap();
		if (bAllDistinct)
		{
			// There are no multiple same values in the passed map (without consedring null)
			keySet = inMap.keySet();
			itKeyList = keySet.iterator();
			while (itKeyList.hasNext())
			{
				key = itKeyList.next();
				value = inMap.get(key);
				if (value != null)
				{
					hmTmpMap.put(value, key); // Prepare new temp HashMap with value=key combination
				}
				else
				{
					hmNullValueMap.put(key, value); // Keep all null values in a new temp Map
				}
			}
			if (ascendingOrder != null && !ascendingOrder)
			{
				// As it is descending order, Add Null Values in first place of the LinkedHasMap
				sortedMap.putAll(hmNullValueMap);
			}
			// Put all not null values in returning LinkedHashMap
			for (int i = 0; i < valueList.size(); i++)
			{
				value = valueList.get(i);
				key = hmTmpMap.get(value);
				sortedMap.put(key, value);
			}
			if (ascendingOrder == null || ascendingOrder)
			{
				// Add Null Values in the last of the LinkedHasMap
				sortedMap.putAll(hmNullValueMap);
			}
		}
		else
		{
			// There are some multiple values (with out considering null)
			keySet = inMap.keySet();
			itKeyList = keySet.iterator();
			while (itKeyList.hasNext())
			{
				key = itKeyList.next();
				value = inMap.get(key);
				if (value != null)
				{
					hmTmpMap.put(key, value); // Prepare new temp HashMap with key=value combination
				}
				else
				{
					hmNullValueMap.put(key, value); // Keep all null values in a new temp Map
				}
			}
			if (ascendingOrder != null && !ascendingOrder)
			{
				// As it is descending order, Add Null Values in first place of the LinkedHasMap
				sortedMap.putAll(hmNullValueMap);
			}
			// Put all not null values in returning LinkedHashMap
			for (int i = 0; i < valueList.size(); i++)
			{
				sortedValue = valueList.get(i);
				// Search this value in temp HashMap and if found remove it
				keySet = hmTmpMap.keySet();
				itKeyList = keySet.iterator();
				while (itKeyList.hasNext())
				{
					key = itKeyList.next();
					value = hmTmpMap.get(key);
					if (value.equals(sortedValue))
					{
						sortedMap.put(key, value);
						hmTmpMap.remove(key);
						break;
					}
				}
			}
			if (ascendingOrder == null || ascendingOrder)
			{
				// Add Null Values in the last of the LinkedHasMap
				sortedMap.putAll(hmNullValueMap);
			}
		}
		return sortedMap;
	}
}
