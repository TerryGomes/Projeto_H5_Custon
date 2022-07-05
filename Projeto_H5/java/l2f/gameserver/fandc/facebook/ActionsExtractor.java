package l2f.gameserver.fandc.facebook;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public interface ActionsExtractor
{
	public static final SimpleDateFormat FACEBOOK_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	void extractData(String p0) throws IOException;

	default JSONObject call(URL apiCallURL) throws IOException
	{
		HttpURLConnection httpConn;
		InputStream inputStream;
		String result;
		httpConn = (HttpURLConnection) apiCallURL.openConnection();
		inputStream = httpConn.getInputStream();
		result = IOUtils.toString(inputStream, "UTF-8");
		inputStream.close();
		httpConn.disconnect();
		return new JSONObject(result);
	}

	default long parseFacebookDate(String date) throws ParseException
	{
		return ActionsExtractor.FACEBOOK_DATE_FORMAT.parse(date).getTime();
	}
}
