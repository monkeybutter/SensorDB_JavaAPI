package au.csiro.cmar.weru;

import au.csiro.cmar.weru.SDBSession;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.simple.JSONArray;
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
		
		int i=0;

		while (i < dataList.size()) {

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
		Calendar cal;

			for (int j = 0; j < 5000 && (i < dataList.size()); j++) {
				dataDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(dataList.get(i + 1)[0]);
				cal = Calendar.getInstance();
				cal.setTimeZone(TimeZone.getTimeZone("GMT"));
				cal.setTimeInMillis(dataDate.getTime());
				data.put(cal.getTimeInMillis() / 1000L, dataList.get(i)[column]);
				i++;
			}

			JSONObject object = new JSONObject();
			object.put(token, data);

			conn.setRequestProperty("Content-Length",
					Integer.toString(object.toString().length()));
			conn.getOutputStream().write(object.toString().getBytes());
			conn.getOutputStream().flush();
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

				System.out.println("POST method failed: " + conn.getResponseCode()
						+ "\t" + conn.getResponseMessage());
				return false;

			}
			else 
				conn.disconnect();

		}

			return true;
	}

	// Start date may be specified. If it is not the earliest date is used.
	// End date may be specified. If it is not the latest date is used.
	// Optionally the aggregation level may be specified as one of the
	// following:
	// raw, 1-minute, 5-minute, 15-minute, 1-hour, 3-hour, 6-hour, 1-day,
	// 1-month, 1-year"""
	@SuppressWarnings("unchecked")
	public boolean getData(SDBSession session, Calendar start_date,
			Calendar end_date, int agregation_level) throws IOException,
			ParseException {

		URL url = new URL(session.host + "/data");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-length", "0");
		conn.setUseCaches(false);
		conn.setAllowUserInteraction(false);
		conn.setConnectTimeout(30000);
		conn.setReadTimeout(30000);
		conn.connect();
		int status = conn.getResponseCode();

		if (status == 200) {
			JSONParser parser = new JSONParser();
			BufferedReader bin = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			JSONArray jsonArray = (JSONArray) parser.parse(bin);
			bin.close();

			if (jsonArray.size() > 0) {
				JSONObject measJSON;

				for (int i = 0; i < jsonArray.size(); i++) {
					measJSON = (JSONObject) jsonArray.get(i);
					System.out.println(measJSON.toJSONString());
				}
			}
		}

		return true;
	}

}
