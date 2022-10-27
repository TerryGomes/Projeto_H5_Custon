package l2mv.gameserver.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.instancemanager.CastleManorManager;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.manor.CropProcure;

public class Manor
{
	private static final Logger _log = LoggerFactory.getLogger(Manor.class);
	private static Manor _instance;

	private static Map<Integer, SeedData> _seeds;

	public Manor()
	{
		_seeds = new ConcurrentHashMap<Integer, SeedData>();
		parseData();
	}

	public static Manor getInstance()
	{
		if (_instance == null)
		{
			_instance = new Manor();
		}
		return _instance;
	}

	public List<Integer> getAllCrops()
	{
		List<Integer> crops = new ArrayList<Integer>();
		for (SeedData seed : _seeds.values())
		{
			if (!crops.contains(seed.getCrop()) && seed.getCrop() != 0 && !crops.contains(seed.getCrop()))
			{
				crops.add(seed.getCrop());
			}
		}
		return crops;
	}

	public Map<Integer, SeedData> getAllSeeds()
	{
		return _seeds;
	}

	public int getSeedBasicPrice(int seedId)
	{
		ItemTemplate seedItem = ItemHolder.getInstance().getTemplate(seedId);
		if (seedItem != null)
		{
			return seedItem.getReferencePrice();
		}
		return 0;
	}

	public int getSeedBasicPriceByCrop(int cropId)
	{
		for (SeedData seed : _seeds.values())
		{
			if (seed.getCrop() == cropId)
			{
				return getSeedBasicPrice(seed.getId());
			}
		}
		return 0;
	}

	public int getCropBasicPrice(int cropId)
	{
		ItemTemplate cropItem = ItemHolder.getInstance().getTemplate(cropId);
		if (cropItem != null)
		{
			return cropItem.getReferencePrice();
		}
		return 0;
	}

	public int getMatureCrop(int cropId)
	{
		for (SeedData seed : _seeds.values())
		{
			if (seed.getCrop() == cropId)
			{
				return seed.getMature();
			}
		}
		return 0;
	}

	/**
	 * Returns price which lord pays to buy one seed
	 * @param seedId
	 * @return seed price
	 */
	public long getSeedBuyPrice(int seedId)
	{
		long buyPrice = getSeedBasicPrice(seedId) / 10;
		return buyPrice >= 0 ? buyPrice : 1;
	}

	public int getSeedMinLevel(int seedId)
	{
		SeedData seed = _seeds.get(seedId);
		if (seed != null)
		{
			return seed.getLevel() - 5;
		}
		return -1;
	}

	public int getSeedMaxLevel(int seedId)
	{
		SeedData seed = _seeds.get(seedId);
		if (seed != null)
		{
			return seed.getLevel() + 5;
		}
		return -1;
	}

	public int getSeedLevelByCrop(int cropId)
	{
		for (SeedData seed : _seeds.values())
		{
			if (seed.getCrop() == cropId)
			{
				return seed.getLevel();
			}
		}
		return 0;
	}

	public int getSeedLevel(int seedId)
	{
		SeedData seed = _seeds.get(seedId);
		if (seed != null)
		{
			return seed.getLevel();
		}
		return -1;
	}

	public boolean isAlternative(int seedId)
	{
		for (SeedData seed : _seeds.values())
		{
			if (seed.getId() == seedId)
			{
				return seed.isAlternative();
			}
		}
		return false;
	}

	public int getCropType(int seedId)
	{
		SeedData seed = _seeds.get(seedId);
		if (seed != null)
		{
			return seed.getCrop();
		}
		return -1;
	}

	public synchronized int getRewardItem(int cropId, int type)
	{
		for (SeedData seed : _seeds.values())
		{
			if (seed.getCrop() == cropId)
			{
				return seed.getReward(type); // there can be several
			}
		}
		// seeds with same crop, but
		// reward should be the same for
		// all
		return -1;
	}

	public synchronized long getRewardAmountPerCrop(int castle, int cropId, int type)
	{
		final CropProcure cs = ResidenceHolder.getInstance().getResidence(Castle.class, castle).getCropProcure(CastleManorManager.PERIOD_CURRENT).get(cropId);
		for (SeedData seed : _seeds.values())
		{
			if (seed.getCrop() == cropId)
			{
				return cs.getPrice() / getCropBasicPrice(seed.getReward(type));
			}
		}
		return -1;
	}

	public synchronized int getRewardItemBySeed(int seedId, int type)
	{
		SeedData seed = _seeds.get(seedId);
		if (seed != null)
		{
			return seed.getReward(type);
		}
		return 0;
	}

	/**
	 * Return all crops which can be purchased by given castle
	 *
	 * @param castleId
	 * @return
	 */
	public List<Integer> getCropsForCastle(int castleId)
	{
		List<Integer> crops = new ArrayList<Integer>();
		for (SeedData seed : _seeds.values())
		{
			if (seed.getManorId() == castleId && !crops.contains(seed.getCrop()))
			{
				crops.add(seed.getCrop());
			}
		}
		return crops;
	}

	/**
	 * Return list of seed ids, which belongs to castle with given id
	 * @param castleId - id of the castle
	 * @return seedIds - list of seed ids
	 */
	public List<Integer> getSeedsForCastle(int castleId)
	{
		List<Integer> seedsID = new ArrayList<Integer>();
		for (SeedData seed : _seeds.values())
		{
			if (seed.getManorId() == castleId && !seedsID.contains(seed.getId()))
			{
				seedsID.add(seed.getId());
			}
		}
		return seedsID;
	}

	/**
	 * Returns castle id where seed can be sowned<br>
	 * @param seedId
	 * @return castleId
	 */
	public int getCastleIdForSeed(int seedId)
	{
		SeedData seed = _seeds.get(seedId);
		if (seed != null)
		{
			return seed.getManorId();
		}
		return 0;
	}

	public long getSeedSaleLimit(int seedId)
	{
		SeedData seed = _seeds.get(seedId);
		if (seed != null)
		{
			return seed.getSeedLimit();
		}
		return 0;
	}

	public long getCropPuchaseLimit(int cropId)
	{
		for (SeedData seed : _seeds.values())
		{
			if (seed.getCrop() == cropId)
			{
				return seed.getCropLimit();
			}
		}
		return 0;
	}

	public class SeedData
	{
		private int _id;
		private int _level; // seed level
		private int _crop; // crop type
		private int _mature; // mature crop type
		private int _type1;
		private int _type2;
		private int _manorId; // id of manor (castle id) where seed can be farmed
		private int _isAlternative;
		private long _limitSeeds;
		private long _limitCrops;

		public SeedData(int level, int crop, int mature)
		{
			_level = level;
			_crop = crop;
			_mature = mature;
		}

		public void setData(int id, int t1, int t2, int manorId, int isAlt, long lim1, long lim2)
		{
			_id = id;
			_type1 = t1;
			_type2 = t2;
			_manorId = manorId;
			_isAlternative = isAlt;
			_limitSeeds = lim1;
			_limitCrops = lim2;
		}

		public int getManorId()
		{
			return _manorId;
		}

		public int getId()
		{
			return _id;
		}

		public int getCrop()
		{
			return _crop;
		}

		public int getMature()
		{
			return _mature;
		}

		public int getReward(int type)
		{
			return type == 1 ? _type1 : _type2;
		}

		public int getLevel()
		{
			return _level;
		}

		public boolean isAlternative()
		{
			return _isAlternative == 1;
		}

		public long getSeedLimit()
		{
			return _limitSeeds;
		}

		public long getCropLimit()
		{
			return _limitCrops;
		}
	}

	private void parseData()
	{
		File seedData = new File(Config.DATAPACK_ROOT, "data/seeds.csv");
		try (LineNumberReader lnr = new LineNumberReader(new BufferedReader(new FileReader(seedData))))
		{
			String line = null;
			while ((line = lnr.readLine()) != null)
			{
				if (line.trim().length() == 0 || line.startsWith("#"))
				{
					continue;
				}
				SeedData seed = parseList(line);
				_seeds.put(seed.getId(), seed);
			}

			_log.info("ManorManager: Loaded " + _seeds.size() + " seeds");
		}
		catch (FileNotFoundException e)
		{
			_log.info("seeds.csv is missing in data folder!", e);
		}
		catch (IOException e)
		{
			_log.error("Error while loading seeds!", e);
		}
	}

	private SeedData parseList(String line)
	{
		StringTokenizer st = new StringTokenizer(line, ";");

		int seedId = Integer.parseInt(st.nextToken()); // seed id
		int level = Integer.parseInt(st.nextToken()); // seed level
		int cropId = Integer.parseInt(st.nextToken()); // crop id
		int matureId = Integer.parseInt(st.nextToken()); // mature crop id
		int type1R = Integer.parseInt(st.nextToken()); // type I reward
		int type2R = Integer.parseInt(st.nextToken()); // type II reward
		int manorId = Integer.parseInt(st.nextToken()); // id of manor, where seed can be farmed
		int isAlt = Integer.parseInt(st.nextToken()); // alternative seed
		long limitSeeds = Math.round(Integer.parseInt(st.nextToken()) * Config.RATE_MANOR); // limit for seeds
		long limitCrops = Math.round(Integer.parseInt(st.nextToken()) * Config.RATE_MANOR); // limit for crops

		SeedData seed = new SeedData(level, cropId, matureId);
		seed.setData(seedId, type1R, type2R, manorId, isAlt, limitSeeds, limitCrops);

		return seed;
	}
}
