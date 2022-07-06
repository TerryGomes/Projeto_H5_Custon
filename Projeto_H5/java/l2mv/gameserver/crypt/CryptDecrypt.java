package l2mv.gameserver.crypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptDecrypt
{
	public static String encryptBlowfish(String to_encrypt, String strkey)
	{
		try
		{
			SecretKeySpec key = new SecretKeySpec(strkey.getBytes(), "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return new String(cipher.doFinal(to_encrypt.getBytes()));
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static String decryptBlowfish(String to_decrypt, String strkey)
	{
		try
		{
			SecretKeySpec key = new SecretKeySpec(strkey.getBytes(), "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decrypted = cipher.doFinal(to_decrypt.getBytes());
			return new String(decrypted);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static byte[] encryptBlowfish(byte[] to_encrypt, String strkey, int len)
	{
		try
		{
			SecretKeySpec key = new SecretKeySpec(strkey.getBytes(), "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] new_encrypt = new byte[len];
			new_encrypt = cipher.doFinal(to_encrypt);
			return new_encrypt;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static byte[] decryptBlowfish(byte[] to_decrypt, String strkey, int len)
	{
		try
		{
			SecretKeySpec key = new SecretKeySpec(strkey.getBytes(), "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decrypted = new byte[len];
			decrypted = cipher.doFinal(to_decrypt);
			return decrypted;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static byte[] encryptBlowfish(byte[] to_encrypt, byte[] strkey, int len)
	{
		try
		{
			SecretKeySpec key = new SecretKeySpec(strkey, "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] new_encrypt = new byte[len];
			new_encrypt = cipher.doFinal(to_encrypt);
			return new_encrypt;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static byte[] decryptBlowfish(byte[] to_decrypt, byte[] strkey, int len)
	{
		try
		{
			SecretKeySpec key = new SecretKeySpec(strkey, "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decrypted = new byte[len];
			decrypted = cipher.doFinal(to_decrypt);
			return decrypted;
		}
		catch (Exception e)
		{
			return null;
		}
	}
}