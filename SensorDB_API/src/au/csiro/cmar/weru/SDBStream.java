package au.csiro.cmar.weru;

import au.csiro.cmar.weru.SDBSession;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import au.com.bytecode.opencsv.CSVReader;

public class SDBStream {
	// Stream Identifier
	String name;
	String _id;
	String nid;
	String uid;
	String mid;
	String description;
	String picture;
	String website;
	String token;
	String metadata;
	long created_at;
	long updated_at;

	public SDBStream(JSONObject json) {
		name = json.get("name").toString();
		_id = json.get("_id").toString();
		nid = json.get("nid").toString();
		uid = json.get("uid").toString();
		mid = json.get("mid").toString();
		description = json.get("description").toString();
		picture = json.get("picture").toString();
		website = json.get("website").toString();
		token = json.get("token").toString();
		// metadata = json.get("metadata").toString();
		created_at = Long.parseLong(json.get("created_at").toString());
		updated_at = Long.parseLong(json.get("updated_at").toString());
	}

	@SuppressWarnings("unchecked")
	public boolean postData(SDBSession session, String filename, int column)
			throws IOException, ParseException, java.text.ParseException {

		CSVReader reader = new CSVReader(new FileReader(filename));
		List<String[]> dataList = reader.readAll();
		// Remove headers
		dataList.remove(0);

		int i = 0;

		while ((i * 60) < dataList.size()) {

			
			URL url = new URL(session.host + "/data");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Cookie", session.cookie);
			
			JSONObject data = new JSONObject();
			Date dataDate;
			for (int j = 0; j < 5000 && ((i * 60) < dataList.size()); j++) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat dateFormatT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				dataDate = dateFormat.parse(dataList.get(i * 60)[0]);
				data.put(dateFormatT.format(dataDate), dataList.get(i * 60)[column]);
				System.out.println(dateFormatT.format(dataDate));
				i++;
			}
			
			JSONObject object = new JSONObject();
			object.put(token, data);
		
			conn.setRequestProperty("Content-Length",
					Integer.toString(object.toString().length()));
			conn.getOutputStream().write(object.toString().getBytes());
			conn.getOutputStream().flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

				System.out.println("POST method failed: "
						+ conn.getResponseCode() + "\t"
						+ conn.getResponseMessage());
				return false;

			} else
				conn.disconnect();
		}

		return true;
	}

	// Start date may be specified. If it is not the earliest date is used.
	// End date may be specified. If it is not the latest date is used.
	// Optionally the aggregation level may be specified as one of the
	// following:
	// raw, 1-minute, 5-minute, 15-minute, 1-hour, 3-hour, 6-hour, 1-day,
	// 1-month, 1-year
	@SuppressWarnings("unchecked")
	public boolean getData(SDBSession session, Calendar start_date,
			Calendar end_date, String agregation_level) throws IOException,
			ParseException {
		
		URL url = new URL(session.host + "/data_download");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Cookie", session.cookie);
		
		JSONObject obj = new JSONObject();

		if (agregation_level != null)
			obj.put("level", agregation_level);
		else
			obj.put("level", "raw");
		
		if (start_date != null)
			obj.put("sd", start_date.get(Calendar.YEAR) + "-" + formatValue(start_date.get(Calendar.MONTH)+1,2) + "-" + formatValue(start_date.get(Calendar.DATE),2));

		if (end_date != null)
			obj.put("ed", end_date.get(Calendar.YEAR) + "-" + formatValue(end_date.get(Calendar.MONTH)+1,2) + "-" + formatValue(end_date.get(Calendar.DATE),2));
		
		obj.put("sid", this._id);
		
		System.out.println(obj.toJSONString());
		
		
		conn.setRequestProperty("Content-Length",
				Integer.toString(obj.toString().length()));
		conn.getOutputStream().write(obj.toString().getBytes());
		conn.getOutputStream().flush();

		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

			System.out.println("POST method failed: " + conn.getResponseCode()
					+ "\t" + conn.getResponseMessage());
			return false;

		} else {

			JSONParser parser = new JSONParser();
			BufferedReader bin = new BufferedReader(new InputStreamReader(
					new DataInputStream(conn.getInputStream())));
			JSONObject jsonObject = (JSONObject) parser.parse(bin);
			bin.close();
			
			System.out.println(jsonObject.toJSONString());
			
			return true;
		}
	}
	
	// Returns a string containing value with a fixed number of characters
	// determined by positions
	// Fills with "0" on the left side of the value
	public static String formatValue(int value, int positions) {
		StringBuilder sbValue = new StringBuilder(Integer.toString(value));

		while (sbValue.length() < positions) {
			sbValue.insert(0, "0");
		}

		return sbValue.toString();
	}

}
