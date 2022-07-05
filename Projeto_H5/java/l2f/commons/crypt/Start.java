package l2f.commons.crypt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Start
{

	/**
	 * @param args
	 * @throws java.io.IOException
	 */
	public static void main(String[] args) throws IOException
	{
		// String command = "";
		// BufferedReader read = new BufferedReader(new InputStreamReader(System.in));

		System.out.print("Path to file:\n");
		String path = Scanning(System.in).next();

		System.out.print("User name:\n");
		String user = Scanning(System.in).next();

		System.out.print("Key:\n");
		int key = Integer.valueOf(Scanning(System.in).next());

		System.out.print("IP adress:\n");
		String ipadr = Scanning(System.in).next();

		System.out.print("Crypting file...\n");
		String newKey = key + ipadr + user;
		NewCrypt crypt = new NewCrypt(newKey.getBytes());

		File file = new File(path);
		if (!file.exists())
		{
			System.out.print("File not found..\n");
			System.exit(0);
		}

		if (file.length() >= Integer.MAX_VALUE)
		{
			System.out.print("Big file!\n");
			System.exit(0);
		}

		byte[] data = new byte[(int) file.length()];

		data = crypt.crypt(data);

		File file2 = new File("start.bin");
		if (file2.exists())
		{
			file2.createNewFile();
			FileOutputStream out = new FileOutputStream(file2);
			out.write(data);
			out.flush();
			out.close();

		}
	}

	private static Scanner Scanning(InputStream in)
	{
		return new Scanner(in);
	}

}
