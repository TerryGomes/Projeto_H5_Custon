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
package l2f.gameserver.com.l2jserver.gameserver.masteriopack.imageconverter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2f.gameserver.model.Player;
import l2f.gameserver.masteriopack.rankpvpsystem.RPSConfig;
import l2f.gameserver.masteriopack.rankpvpsystem.Rank;
import l2f.gameserver.masteriopack.rankpvpsystem.RankTable;

/**
 * This class contains all of the rank images as buffered files.
 * @author Masterio
 */
public class ServerSideImage
{
	public static final Logger log = Logger.getLogger(ServerSideImage.class.getName());

	private static ServerSideImage _instance = null;

	/** <rank_id, converted image data as byte array> */
	private static Map<Integer, byte[]> _iconImages = new HashMap<>();
	private static Map<Integer, byte[]> _nameImages = new HashMap<>();
	private static Map<Integer, byte[]> _expImages = new HashMap<>();

	private ServerSideImage()
	{
		if (RPSConfig.SERVER_SIDE_IMAGES_ENABLED)
		{
			load();
		}
		else
		{
			log.info(" - ImageTable: Images will be requested from Client.");
		}
	}

	public static ServerSideImage getInstance()
	{
		if (_instance == null)
		{
			_instance = new ServerSideImage();
		}

		return _instance;
	}

	private void load()
	{
		Map<Integer, Rank> rankList = RankTable.getInstance().getRankList();

		for (Map.Entry<Integer, Rank> e : rankList.entrySet())
		{
			String src = null;
			File image = null;

			try
			{
				// set icon images:
				src = "rank_pvp_system/rank/rank_" + e.getValue().getId();
				image = new File("data/images/" + src + ".png");
				_iconImages.put(e.getValue().getId(), DDSConverter.convertToDDS(image).array());

				// set name images:
				src = "rank_pvp_system/rank_name/rank_name_" + e.getValue().getId();
				image = new File("data/images/" + src + ".png");
				_nameImages.put(e.getValue().getId(), DDSConverter.convertToDDS(image).array());

			}
			catch (Exception e1)
			{
				log.info(e1.getMessage());
				return;
			}
		}

		for (int i = 0; i <= 100; i++)
		{
			String src = null;
			File image = null;

			try
			{
				// set exp images:
				src = "rank_pvp_system/exp/exp_" + i;
				image = new File("data/images/" + src + ".png");
				_expImages.put(i, DDSConverter.convertToDDS(image).array());
			}
			catch (Exception e1)
			{
				log.info(e1.getMessage());
				return;
			}
		}

		log.info(" - ImageTable: Data loaded. " + (_iconImages.size()) + " icons, " + _nameImages.size() + " names and " + _expImages.size() + " exp images.");
	}

	public String getExpImageHtmlTag(Player player, int expId, int width, int height)
	{
		String tb = "";

		if (RPSConfig.SERVER_SIDE_IMAGES_ENABLED)
		{
			try
			{
				int id = 400000000 + expId + (RPSConfig.IMAGE_PREFIX * 200);
				ImageServerPacket packet = new ImageServerPacket(id, _expImages.get(expId));
				player.sendPacket(packet);

				tb += "<img src=\"Crest.crest_" + expId + "_" + id + "\" width=" + width + " height=" + height + ">";
			}
			catch (Exception e)
			{
				log.info(e.getMessage());
			}
		}
		else
		{
			tb = "<img src=\"RPS_byMasterio.exp_" + expId + "\" width=" + width + " height=" + height + ">";
		}

		return tb;
	}

	public String getRankIconImageHtmlTag(Player player, int rankId, int width, int height)
	{
		String tb = "";

		if (RPSConfig.SERVER_SIDE_IMAGES_ENABLED)
		{
			try
			{
				int id = 500000000 + rankId + (RPSConfig.IMAGE_PREFIX * 200);
				ImageServerPacket packet = new ImageServerPacket(id, _iconImages.get(rankId));
				player.sendPacket(packet);

				tb += "<img src=\"Crest.crest_" + rankId + "_" + id + "\" width=" + width + " height=" + height + ">";
			}
			catch (Exception e)
			{
				log.info(e.getMessage());
			}
		}
		else
		{
			tb = "<img src=\"RPS_byMasterio.rank_" + rankId + "\" width=" + width + " height=" + height + ">";
		}

		return tb;
	}

	public String getRankNameImageHtmlTag(Player player, int rankId, int width, int height)
	{
		String tb = "";

		if (RPSConfig.SERVER_SIDE_IMAGES_ENABLED)
		{
			try
			{
				int id = 600000000 + rankId + (RPSConfig.IMAGE_PREFIX * 200);
				ImageServerPacket packet = new ImageServerPacket(id, _nameImages.get(rankId));
				player.sendPacket(packet);

				tb += "<img src=\"Crest.crest_" + rankId + "_" + id + "\" width=" + width + " height=" + height + ">";
			}
			catch (Exception e)
			{
				log.info(e.getMessage());
			}
		}
		else
		{
			tb = "<img src=\"RPS_byMasterio.rank_name_" + rankId + "\" width=" + width + " height=" + height + ">";
		}

		return tb;
	}

	public Map<Integer, byte[]> getIconImages()
	{
		return _iconImages;
	}

	public void setIconImages(HashMap<Integer, byte[]> iconImages)
	{
		_iconImages = iconImages;
	}

	public Map<Integer, byte[]> getNameImages()
	{
		return _nameImages;
	}

	public void setNameImages(HashMap<Integer, byte[]> nameImages)
	{
		_nameImages = nameImages;
	}

	public Map<Integer, byte[]> getExpImages()
	{
		return _expImages;
	}

	public void setExpImages(HashMap<Integer, byte[]> expImages)
	{
		_expImages = expImages;
	}
}
