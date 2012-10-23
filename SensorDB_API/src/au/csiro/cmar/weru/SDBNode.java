package au.csiro.cmar.weru;

import au.csiro.cmar.weru.SDBSession;
import au.csiro.cmar.weru.SDBStream;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SDBNode {
	// Node Identifier
	String name;
	String _id;
	String eid;
	String uid;
	double lat;
	double lon;
	double alt;
	String description;
	String picture;
	String website;
	long created_at;
	long updated_at;
	String metadata;

	List<SDBStream> streams = new ArrayList<SDBStream>();
	
	public SDBNode(JSONObject json) {
		_id = json.get("_id").toString();
		name = json.get("name").toString();
		eid = json.get("eid").toString();
		uid = json.get("uid").toString();
		if (!json.get("lat").toString().equals("")) 
			lat = Double.parseDouble(json.get("lat").toString());
		if (!json.get("lon").toString().equals("")) 
			lon = Double.parseDouble(json.get("lon").toString());
		if (!json.get("alt").toString().equals("")) 
			alt = Double.parseDouble(json.get("alt").toString());
		description = json.get("description").toString();
		picture = json.get("picture").toString();
		website = json.get("website").toString();
		created_at = Long.parseLong(json.get("created_at").toString());
		updated_at = Long.parseLong(json.get("updated_at").toString());
		//metadata = json.get("metadata").toString();
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean createStream(SDBSession session, String name, String nid, String mid, String description, String website, String picture) throws IOException, ParseException {
		
		for (int i = 0; i<streams.size(); i++) {
			if (streams.get(i).name.equals(name))
				return false;
		}
		
		URL url = new URL(session.host + "/streams");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Cookie", session.cookie);
		
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("nid", nid);
		obj.put("mid", mid);

		if (description != null)
			obj.put("description", description);
		
		if (website != null)
			obj.put("website", website);

		if (picture != null)
			obj.put("picture", picture);
		
		
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
			
			SDBStream newStream = new SDBStream(jsonObject);
			streams.add(newStream);
			
			return true;
		}
	}
	
	
	public boolean deleteStream(SDBSession session, String name) throws IOException, ParseException {

		String sid = null;
		int index = -1;
		
		for (int i = 0; i<streams.size(); i++) {
			if (streams.get(i).name.equals(name)) {
				sid = streams.get(i)._id;
				index = i;
			}
			
		}
		
		if (sid != null) {
			
			URL url = new URL(session.host + "/streams?sid=" + sid);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Cookie", session.cookie);

			if (conn.getResponseCode() == 200) {
				streams.remove(index);
				return true;
			} else {
				System.out.println("DELETE method failed: " + conn.getResponseCode()
						+ "\t" + conn.getResponseMessage());
				return false;
			}
			
		}
		
		else
			return false;
		
	}
}