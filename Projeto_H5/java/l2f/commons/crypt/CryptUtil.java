package l2f.commons.crypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.log4j.Logger;

import l2f.commons.util.Base64;

public class CryptUtil
{
	private static final Logger _log = Logger.getLogger(CryptUtil.class);
	private static Cipher _encCipher;
	private static Cipher _decCipher;
	private static SecretKey _key = null;
	private static final byte[] _salt =
	{
		-116,
		30,
		-95,
		-101,
		2,
		112,
		2,
		93
	};

	private static boolean _initiated = false;

	private static void init()
	{
		if (_initiated)
		{
			return;
		}
		try
		{
			KeySpec keySpec = new PBEKeySpec("ALNF__etJIEHFVI#@$234JjJ&R(#*&?45?[:F{EWKF3DFGSDJ343HDFP345MVCSND85445VNSKJ".toCharArray(), _salt, 19);
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(_salt, 19);
			_key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

			_encCipher = Cipher.getInstance(_key.getAlgorithm());
			_decCipher = Cipher.getInstance(_key.getAlgorithm());

			_encCipher.init(1, _key, paramSpec);
			_decCipher.init(2, _key, paramSpec);
		}
		catch (Exception e)
		{
			_log.error("Cannot init crypto engine.", e);
		}

		_initiated = true;
	}

	public static String encrypt(String data)
	{
		init();
		try
		{
			return Base64.encodeBytes(_encCipher.doFinal(data.getBytes("UTF8")));
		}
		catch (Exception e)
		{
			_log.error("Cannot encrypt data.", e);
		}
		return null;
	}

	public static String decrypt(String data)
	{
		init();
		try
		{
			String decoded = new String(_decCipher.doFinal(Base64.decode(data)), "UTF8");

			return decoded;
		}
		catch (Exception e)
		{
			_log.error("Cannot decrypt data.", e);
		}

		return null;
	}

	public static void encrypt(InputStream in, OutputStream out)
	{
		init();
		out = new CipherOutputStream(out, _encCipher);
		try
		{
			int num;
			byte[] buffer = new byte[1024];
			while ((num = in.read(buffer)) >= 0)
			{
				out.write(buffer, 0, num);
			}
			out.flush();
			out.close();
		}
		catch (IOException e)
		{
			_log.error("Cannot write encrypted file.", e);
		}
	}

	public static InputStream decrypt(InputStream input, InputStream readable)
	{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		decrypt(input, output);
		return new ByteArrayInputStream(output.toByteArray());
	}

	@SuppressWarnings("resource")
	public static InputStream decryptOnDemand(File file) throws IOException
	{
		InputStream output;
		InputStream input = new FileInputStream(file);

		if ((byte) input.read() == 0)
		{
			byte[] bytes = new byte[0];
			output = new ByteArrayInputStream(bytes);
			output = decrypt(input, output);
			output.reset();
		}
		else
		{
			output = new FileInputStream(file);
		}
		return output;
	}

	@SuppressWarnings("resource")
	public static InputStream decryptOnDemand(InputStream input) throws IOException
	{
		InputStream output;
		if ((byte) input.read() == 0)
		{
			byte[] bytes = new byte[0];
			output = new ByteArrayInputStream(bytes);
			output = decrypt(input, output);
		}
		else
		{
			output = input;
		}
		output.reset();
		return output;
	}

	@SuppressWarnings("resource")
	public static void decrypt(InputStream in, OutputStream out)
	{
		init();
		in = new CipherInputStream(in, _decCipher);
		try
		{
			@SuppressWarnings("unused")
			InputStreamReader reader = new InputStreamReader(in, "UTF8");

			int num = 0;
			byte[] buffer = new byte[1024];
			while ((num = in.read(buffer)) >= 0)
			{
				out.write(buffer, 0, num);
			}
			out.flush();
			out.close();
		}
		catch (IOException e)
		{
			_log.error("Cannot decrypt file.", e);
		}
	}

	public static String encrypt(InputStream stream) throws IOException
	{
		init();
		StringBuilder buffer = new StringBuilder();
		int chr;
		while ((chr = stream.read()) >= 0)
		{
			buffer.append(chr);
		}
		return encrypt(buffer.toString());
	}

	public static String decrypt(InputStream stream) throws IOException
	{
		init();
		StringBuilder buffer = new StringBuilder();

		int chr;
		while ((chr = stream.read()) >= 0)
		{
			buffer.append(Character.toChars(chr));
		}
		return decrypt(buffer.toString());
	}

	public static int getKeyHash()
	{
		init();
		return _key.hashCode();
	}
}