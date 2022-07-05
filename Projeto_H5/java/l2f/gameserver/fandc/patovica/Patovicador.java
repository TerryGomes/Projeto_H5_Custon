/*
 * Copyright (C) 2004-2013 L2J Server
 * This file is part of L2J Server.
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2f.gameserver.fandc.patovica;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Parte de la seguridad del paquete. Encriptamos textos
 *
 * @author Synerge
 */
public final class Patovicador
{
	private static SecretKeyFactory KEY_FACTORY = null;
	private static SecretKey KEY = null;
	private static String CODING = "TSE7";
	private static String ORIGINAL_KEY = "vvvQQQvvv";
	private static String ALGORITHM = "CDR";

	private static final Base64.Encoder _base64encoder = Base64.getEncoder();
	private static final Base64.Decoder _base64decoder = Base64.getDecoder();

	public Patovicador()
	{
		try
		{
			// Armamos la key correcta sumando 1 a cada char para obtener wwwRRRwww
			String key = "";
			for (char a : ORIGINAL_KEY.toCharArray())
			{
				key += (char) (a + 1);
			}
			// Armamos el algoritmo correcto sumando 1 a cada char para obtener DES
			String algo = "";
			for (char a : ALGORITHM.toCharArray())
			{
				algo += (char) (a + 1);
			}
			ALGORITHM = algo;
			// Armamos el coding correcto sumando 1 a cada char para obtener UTF8
			algo = "";
			for (char a : CODING.toCharArray())
			{
				algo += (char) (a + 1);
			}
			CODING = algo;

			final DESKeySpec KEY_SPEC = new DESKeySpec(key.getBytes(CODING));
			KEY_FACTORY = SecretKeyFactory.getInstance(ALGORITHM);
			KEY = KEY_FACTORY.generateSecret(KEY_SPEC);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Establece una nueva key para la encriptacion/desencriptacion
	 *
	 * @param key
	 */
	public void setPatollave(String key)
	{
		try
		{
			final DESKeySpec KEY_SPEC = new DESKeySpec(key.getBytes(CODING));
			KEY = KEY_FACTORY.generateSecret(KEY_SPEC);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param input
	 * @return Encripta el texto pasado con la key seleccionada
	 */
	public String patovicar(String input)
	{
		try
		{
			final byte[] cleartext = input.getBytes(CODING);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, KEY);
			return _base64encoder.encodeToString(cipher.doFinal(cleartext));
		}
		catch (Exception e)
		{
		}

		return null;
	}

	/**
	 * @param input
	 * @return Desencripta el texto pasado con la key seleccionada
	 */
	public String despatovicar(String input)
	{
		try
		{
			final byte[] encrypedPwdBytes = _base64decoder.decode(input);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, KEY);
			return new String(cipher.doFinal(encrypedPwdBytes));
		}
		catch (Exception e)
		{
		}

		return null;
	}

	public static Patovicador getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final Patovicador _instance = new Patovicador();
	}
}
