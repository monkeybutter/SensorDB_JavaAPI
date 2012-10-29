package au.csiro.cmar.weru;

import au.csiro.cmar.weru.SDBNode;

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

public class SDBExperiment {
	// Experiment Identifier
	public String _id;
	// Experiment Name
	public String name;
	// Experiment Description
	public String description;
	// Experiment User Owner Identifier 
	public String uid;
	// Associated Picture URL
	public String picture;
	// Associated Picture
	public String website;
	// Timezone of Experiment
	public String timezone;
	// Created at
	public long created_at;
	// Updated at
	public long updated_at;
	// Access Restriction [0,1]
	public int access_restriction;
	
	public List<SDBNode> nodes = new ArrayList<SDBNode>();
	
	
	public SDBExperiment(JSONObject json) {
		_id = json.get("_id").toString();
		name = json.get("name").toString();
		description = json.get("description").toString();
		uid = json.get("uid").toString();
		picture = json.get("picture").toString();
		website = json.get("website").toString();
		timezone = json.get("timezone").toString();
		created_at = Long.parseLong(json.get("created_at").toString());
		updated_at = Long.parseLong(json.get("updated_at").toString());
		access_restriction = Integer.parseInt(json.get("access_restriction").toString());
	}
	
	

	@SuppressWarnings("unchecked")
	public boolean createNode(String host, String cookie, String name, String eid, String description, String website, String picture, int public_access) throws IOException, ParseException {
		
		for (int i = 0; i<nodes.size(); i++) {
			if (nodes.get(i).name.equals(name))
				return false;
		}
		
		URL url = new URL(host + "/nodes");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Cookie", cookie);
		
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("eid", eid);

		if (description != null)
			obj.put("description", description);
		
		if (website != null)
			obj.put("website", website);

		if (picture != null)
			obj.put("picture", picture);
		
		if (public_access != 0)
			obj.put("public_access", Integer.toString(public_access));
		
		
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
			
			SDBNode newNode = new SDBNode(jsonObject);
			nodes.add(newNode);
			
			return true;
		}
	}
	
	public boolean deleteNode(String host, String cookie, String name) throws IOException, ParseException {

		String nid = null;
		int index = -1;
		
		for (int i = 0; i<nodes.size(); i++) {
			if (nodes.get(i).name.equals(name)) {
				nid = nodes.get(i)._id;
				index = i;
			}
			
		}
		
		if (nid != null) {
			
			URL url = new URL(host + "/nodes?nid=" + nid);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Cookie", cookie);

			if (conn.getResponseCode() == 200) {
				nodes.remove(index);
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
	
	
	public SDBNode getNodebyName(String name) throws IOException, ParseException {

		SDBNode node = null;

		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).name.equals(name)) {
				node = nodes.get(i);
			}
		}

		return node;
	}	
	
}