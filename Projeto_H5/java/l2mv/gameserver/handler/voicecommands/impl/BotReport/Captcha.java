package l2mv.gameserver.handler.voicecommands.impl.BotReport;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.PledgeCrest;
import l2mv.gameserver.vote.DDSConverter;

/**
 * Class that handles Generating and Sending Captcha Image to the Player
 */
public class Captcha
{
	private static final char[] CAPTCHA_TEXT_POSSIBILITIES =
	{
		'A',
		'B',
		'C',
		'D',
		'E',
		'F',
		'G',
		'H',
		'K',
		'L',
		'M',
		'P',
		'R',
		'S',
		'T',
		'U',
		'W',
		'X',
		'Y',
		'Z'
	};
	private static final int CAPTCHA_WORD_LENGTH = 5;
	private static final int CAPTCHA_MIN_ID = 1900000000;
	private static final int CAPTCHA_MAX_ID = 2000000000;

	/**
	 * Generation new Captcha ID
	 * Generation random Captcha Text
	 * Generating BufferedImage
	 * Sending BufferedImage as PledgeCrest to the Target
	 * Sending HTML Window with Captcha to the player
	 * @param target that will receive image and html Window
	 * @return Captcha Text that player will try to write on Text Box
	 */
	public static String sendCaptcha(Player target)
	{
		int captchaId = generateRandomCaptchaId();
		char[] captchaText = generateCaptchaText();

		BufferedImage image = generateCaptcha(captchaText);
		PledgeCrest packet = new PledgeCrest(captchaId, DDSConverter.convertToDDS(image).array());
		target.sendPacket(packet);

		sendCaptchaWindow(target, captchaId);

		return String.valueOf(captchaText);
	}

	/**
	 * Getting data/html-en/captcha.htm HTML
	 * Replacing %captchaId% and %time%
	 * Sending it as HTML window
	 * @param target Player that will receive html
	 * @param captchaId ID of the image to replace
	 */
	private static void sendCaptchaWindow(Player target, int captchaId)
	{
		String text = HtmCache.getInstance().getNotNull("captcha.htm", target);
		text = text.replace("%captchaId%", String.valueOf(captchaId));
		text = text.replace("%time%", String.valueOf(Config.CAPTCHA_ANSWER_SECONDS));
		NpcHtmlMessage msg = new NpcHtmlMessage(0);
		msg.setHtml(text);
		target.sendPacket(msg);
	}

	private static char[] generateCaptchaText()
	{
		char[] text = new char[5];
		for (int i = 0; i < CAPTCHA_WORD_LENGTH; i++)
		{
			text[i] = CAPTCHA_TEXT_POSSIBILITIES[Rnd.get(CAPTCHA_TEXT_POSSIBILITIES.length)];
		}
		return text;
	}

	private static int generateRandomCaptchaId()
	{
		return Rnd.get(CAPTCHA_MIN_ID, CAPTCHA_MAX_ID);
	}

	private static BufferedImage generateCaptcha(char[] text)
	{
		Color textColor = new Color(38, 213, 30);
		Color circleColor = new Color(73, 100, 151);
		Font textFont = new Font("comic sans ms", Font.BOLD, 24);
		int charsToPrint = 5;
		int width = 256;
		int height = 64;
		int circlesToDraw = 8;
		float horizMargin = 20.0f;
		double rotationRange = 0.7; // this is radians
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = (Graphics2D) bufferedImage.getGraphics();

		// Draw an oval
		g.setColor(new Color(30, 31, 31));
		g.fillRect(0, 0, width, height);

		g.setColor(circleColor);
		for (int i = 0; i < circlesToDraw; i++)
		{
			int circleRadius = (int) (Math.random() * height / 2.0);
			int circleX = (int) (Math.random() * width - circleRadius);
			int circleY = (int) (Math.random() * height - circleRadius);
			g.drawOval(circleX, circleY, circleRadius * 2, circleRadius * 2);
		}

		g.setColor(textColor);
		g.setFont(textFont);

		FontMetrics fontMetrics = g.getFontMetrics();
		int maxAdvance = fontMetrics.getMaxAdvance();
		int fontHeight = fontMetrics.getHeight();

		float spaceForLetters = -horizMargin * 2.0F + width;
		float spacePerChar = spaceForLetters / (charsToPrint - 1.0f);

		for (int i = 0; i < charsToPrint; i++)
		{
			char characterToShow = text[i];

			// this is a separate canvas used for the character so that
			// we can rotate it independently
			int charWidth = fontMetrics.charWidth(characterToShow);
			int charDim = Math.max(maxAdvance, fontHeight);
			int halfCharDim = charDim / 2;

			BufferedImage charImage = new BufferedImage(charDim, charDim, BufferedImage.TYPE_INT_ARGB);
			Graphics2D charGraphics = charImage.createGraphics();
			charGraphics.translate(halfCharDim, halfCharDim);
			double angle = (Math.random() - 0.5) * rotationRange;
			charGraphics.transform(AffineTransform.getRotateInstance(angle));
			charGraphics.translate(-halfCharDim, -halfCharDim);
			charGraphics.setColor(textColor);
			charGraphics.setFont(textFont);

			int charX = (int) (0.5 * charDim - 0.5 * charWidth);
			charGraphics.drawString(String.valueOf(characterToShow), charX, (charDim - fontMetrics.getAscent()) / 2 + fontMetrics.getAscent());

			float x = horizMargin + spacePerChar * i - charDim / 2.0f;
			int y = (height - charDim) / 2;
			g.drawImage(charImage, (int) x, y, charDim, charDim, null, null);

			charGraphics.dispose();
		}

		g.dispose();

		return bufferedImage;
	}
}
