package l2mv.gameserver.cache;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2mv.commons.formats.dds.DDSConverter;
import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.PledgeCrest;
import l2mv.gameserver.utils.Util;

/**
 * @author Bonux
 **/
public class ImagesCache
{
	private static final Logger _log = LoggerFactory.getLogger(ImagesCache.class);
	private static final int[] SIZES = new int[]
	{
		1,
		2,
		4,
		8,
		16,
		32,
		64,
		128,
		256,
		512,
		1024
	};
	private static final int MAX_SIZE = SIZES[SIZES.length - 1];
	private static final String CREST_IMAGE_KEY_WORD = "Crest.crest_";
	public static final Pattern HTML_PATTERN = Pattern.compile("%image:(.*?)%", Pattern.DOTALL);

	private final static ImagesCache _instance = new ImagesCache();

	public final static ImagesCache getInstance()
	{
		return _instance;
	}

	private final Map<String, Integer> _imagesId = new HashMap<>();
	/** Получение изображения по ID */
	private final TIntObjectMap<byte[]> _images = new TIntObjectHashMap<>();

	/** Блокировка для чтения/записи объектов из "кэша" */
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = this.lock.readLock();
	private final Lock writeLock = this.lock.writeLock();

	private ImagesCache()
	{
		this.load();
	}

	public void load()
	{
		_log.info("ImagesCache: Loading images...");

		File dir = new File(Config.DATAPACK_ROOT, "/data/images");
		if (!dir.exists() || !dir.isDirectory())
		{
			_log.info("ImagesCache: Files missing, loading aborted.");
			return;
		}

		int count = this.loadImagesDir(dir);

		_log.info("ImagesCache: Loaded " + count + " images");
	}

	private int loadImagesDir(File dir)
	{
		int count = 0;
		for (File file : dir.listFiles())
		{
			if (file.isDirectory())
			{
				count += this.loadImagesDir(file);
				continue;
			}

			if (!checkImageFormat(file))
			{
				continue;
			}

			String fileName = file.getName();

			if (this._imagesId.containsKey(fileName.toLowerCase()))
			{
				_log.warn("Duplicate image name \"" + fileName + "\". Replacing with " + file.getPath());
				continue;
			}

			BufferedImage image = resizeImage(file);
			if (image == null)
			{
				continue;
			}

			try
			{
				ByteBuffer buffer = DDSConverter.convertToDxt1NoTransparency(image);
				byte[] array = buffer.array();
				int imageId = Math.abs(new HashCodeBuilder(15, 87).append(fileName).append(array).toHashCode());

				this._imagesId.put(fileName.toLowerCase(), imageId);
				this._images.put(imageId, array);
			}
			catch (Exception e)
			{
				_log.error("ImagesChache: Error while loading " + fileName + " (" + image.getWidth() + "x" + image.getHeight() + ") image.", e);
			}

			// _log.info("ImagesCache: Loaded " + fileName + " (" + image.getWidth() + "x" + image.getHeight() + ") image.");

			count++;
		}
		return count;
	}

	private static BufferedImage resizeImage(File file)
	{
		BufferedImage image;
		try
		{
			image = ImageIO.read(file);
		}
		catch (IOException ioe)
		{
			_log.error("ImagesCache: Error while resizing " + file.getName() + " image.");
			return null;
		}

		if (image == null)
		{
			return null;
		}

		int width = image.getWidth();
		int height = image.getHeight();

		int resizedWidth = width;
		if (width > MAX_SIZE)
		{
			resizedWidth = MAX_SIZE;
		}
		else
		{
			for (int size : SIZES)
			{
				if (size < width)
				{
					continue;
				}

				resizedWidth = size;
				break;
			}
		}

		int resizedHeight = height;
		if (height > MAX_SIZE)
		{
			resizedHeight = MAX_SIZE;
		}
		else
		{
			for (int size : SIZES)
			{
				if (size < height)
				{
					continue;
				}

				resizedHeight = size;
				break;
			}
		}

		if (resizedWidth != width || resizedHeight != height)
		{
			BufferedImage resizedImage = new BufferedImage(resizedWidth, resizedHeight, image.getType());
			Graphics2D g = resizedImage.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), Color.BLACK, null);
			g.dispose();
			return resizedImage;
		}
		return image;
	}

	public int getImageId(String val)
	{
		int imageId = -1;

		this.readLock.lock();
		try
		{
			if (this._imagesId.get(val.toLowerCase()) != null)
			{
				imageId = this._imagesId.get(val.toLowerCase());
			}
		}
		finally
		{
			this.readLock.unlock();
		}

		return imageId;
	}

	public byte[] getImage(int imageId)
	{
		byte[] image = null;

		this.readLock.lock();
		try
		{
			image = this._images.get(imageId);
		}
		finally
		{
			this.readLock.unlock();
		}

		return image;
	}

	private static boolean checkImageFormat(File file)
	{
		String filename = file.getName();
		int dotPos = filename.lastIndexOf(".");
		String format = filename.substring(dotPos);
		if (format.endsWith(".jpg") || format.endsWith(".png") || format.endsWith(".bmp"))
		{
			return true;
		}
		return false;
	}

	/**
	 * Sending All Images that are needed to open HTML to the player
	 * @param html page that may contain images
	 * @param player that will receive images
	 * @return Returns true if images were sent to the player
	 */
	public String sendUsedImages(String html, Player player)
	{
		if (!Config.ALLOW_SENDING_IMAGES || player == null || player.getNetConnection() == null || player.isPhantom())
		{
			return html;
		}

		// We must also replace all the crests_1 on the html to fit the current player serverid, or he wont be able to see the images
		html = html.replaceAll("Crest.crest_1_", "Crest.crest_" + player.getNetConnection().getServerId() + "_");

		// We must first replace all the images to crests format, things like %image:serverImage% to Crest.crest_1_32423
		Matcher m = HTML_PATTERN.matcher(html);
		while (m.find())
		{
			final String imageName = m.group(1);
			final int imageId = ImagesCache.getInstance().getImageId(imageName);
			html = html.replaceAll("%image:" + imageName + "%", "Crest.crest_" + player.getNetConnection().getServerId() + Config.REQUEST_ID + "_" + imageId);
			byte[] image = ImagesCache.getInstance().getImage(imageId);
			if (image != null)
			{
				player.sendPacket(new PledgeCrest(imageId, image));
			}
		}

		final char[] charArray = html.toCharArray();
		int lastIndex = 0;
		boolean hasSentImages = false;

		// Then we look for crests in the html and send them
		while (lastIndex != -1)
		{
			lastIndex = html.indexOf(CREST_IMAGE_KEY_WORD, lastIndex);

			if (lastIndex != -1)
			{
				final int start = lastIndex + CREST_IMAGE_KEY_WORD.length() + 2;
				final int end = getFileNameEnd(charArray, start);
				final int imageId = Integer.parseInt(html.substring(start, end));

				// Send the image to the player
				if (sendImageToPlayer(player, imageId))
				{
					hasSentImages = true;
				}
				lastIndex = end;
			}
		}

		// Synerge - To differenciate sent crests we add a CREST in the beggining of the html
		/*
		 * if (hasSentImages)
		 * html = "CREST" + html;
		 */

		return html;
	}

	/**
	 * Sending Image as PledgeCrest to a player If image was already sent once to the player, it's skipping this part Saved images data is in player Quick Vars as Key: "Image"+imageId Value: true
	 * @param player that will receive image
	 * @param imageId Id of the image
	 * @return Returns true if the image was sent
	 */
	public boolean sendImageToPlayer(Player player, int imageId)
	{
		if (!Config.ALLOW_SENDING_IMAGES || player.wasImageLoaded(imageId))
		{
			return false;
		}

		if (_images.containsKey(imageId))
		{
			player.addLoadedImage(imageId);
			player.sendPacket(new PledgeCrest(imageId, _images.get(imageId)));
			return true;
		}

		return false;
	}

	/**
	 * Getting end of Image File Name(name is always numbers)
	 * @param charArray whole text
	 * @param start place
	 * @return whole name
	 */
	private static int getFileNameEnd(char[] charArray, int start)
	{
		int stop = start;
		for (; stop < charArray.length; stop++)
		{
			if (!Util.isInteger(charArray[stop]))
			{
				return stop;
			}
		}
		return stop;
	}
}
