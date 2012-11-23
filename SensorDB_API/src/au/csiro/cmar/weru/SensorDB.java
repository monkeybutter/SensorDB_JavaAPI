package au.csiro.cmar.weru;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SensorDB {

	public SDBSession session = new SDBSession();
	public List<SDBMeasurement> measurements = new ArrayList<SDBMeasurement>();
	public List<SDBUser> users = new ArrayList<SDBUser>();

	public List<SDBExperiment> experiments = new ArrayList<SDBExperiment>();

	// Minimal constructor for SensorDB
	// Just can access public services as getMeasurements() and getUsers()
	public SensorDB(String host) throws IOException, ParseException {
		this.session.host = host;
		users = getUsers();
		measurements = getMeasurements();
	}

	public SensorDB(String host, String user, String password)
			throws IOException, ParseException {
		this.session.host = host;
		users = getUsers();
		measurements = getMeasurements();
		JSONObject data = login(user, password);
		populateSensorDB(data);

	}

	@SuppressWarnings("unchecked")
	private JSONObject login(String user, String password) throws IOException,
			ParseException {

		URL url = new URL(session.host + "/login");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");

		JSONObject obj = new JSONObject();
		obj.put("name", user);
		obj.put("password", password);

		conn.setRequestProperty("Content-Length",
				Integer.toString(obj.toString().length()));
		conn.getOutputStream().write(obj.toString().getBytes());
		conn.getOutputStream().flush();
		session.cookie = conn.getHeaderField("Set-Cookie");

		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

			System.out.println("POST method failed: " + conn.getResponseCode()
					+ "\t" + conn.getResponseMessage());
			return null;

		} else {

			JSONParser parser = new JSONParser();
			BufferedReader bin = new BufferedReader(new InputStreamReader(
					new DataInputStream(conn.getInputStream())));
			JSONObject jsonObject = (JSONObject) parser.parse(bin);
			bin.close();
			return jsonObject;
		}

	}

	public void logout() throws IOException {

		URL url = new URL(session.host + "/logout");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Cookie", session.cookie);
		session.cookie = null;
		session.user = null;

	}

	@SuppressWarnings("unchecked")
	public JSONObject createUser(String username, String password, String email,
			String description, String website, String picture)
			throws IOException, ParseException {

		URL url = new URL(session.host + "/register");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Cookie", session.cookie);

		JSONObject obj = new JSONObject();
		obj.put("name", username);
		obj.put("password", password);
		obj.put("email", email);
		if (description != null)
			obj.put("description", description);

		if (website != null)
			obj.put("website", website);

		if (picture != null)
			obj.put("picture", picture);
		// System.out.println(obj.toJSONString());
		System.out.println(obj.toJSONString());

		conn.setRequestProperty("Content-Length",
				Integer.toString(obj.toString().length()));
		conn.getOutputStream().write(obj.toString().getBytes());
		conn.getOutputStream().flush();

		System.out.println(conn.getResponseCode());
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

			System.out.println("POST method failed: " + conn.getResponseCode()
					+ "\t" + conn.getResponseMessage());
			return null;

		} else {

			JSONParser parser = new JSONParser();
			BufferedReader bin = new BufferedReader(new InputStreamReader(
					new DataInputStream(conn.getInputStream())));
			JSONObject jsonObject = (JSONObject) parser.parse(bin);
			bin.close();
			users = getUsers();
			return jsonObject;
		}
	}

	@SuppressWarnings("unchecked")
	public JSONObject deleteUser(String username, String password)
			throws IOException, ParseException {

		URL url = new URL(session.host + "/remove");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Cookie", session.cookie);

		JSONObject obj = new JSONObject();
		obj.put("name", username);
		obj.put("password", password);

		conn.setRequestProperty("Content-Length",
				Integer.toString(obj.toString().length()));
		conn.getOutputStream().write(obj.toString().getBytes());
		conn.getOutputStream().flush();

		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

			System.out.println("POST method failed: " + conn.getResponseCode()
					+ "\t" + conn.getResponseMessage());
			return null;

		} else {

			JSONParser parser = new JSONParser();
			BufferedReader bin = new BufferedReader(new InputStreamReader(
					new DataInputStream(conn.getInputStream())));
			JSONObject jsonObject = (JSONObject) parser.parse(bin);
			bin.close();
			users = getUsers();
			return jsonObject;
		}
	}
	
	public String getUserId(String name) throws IOException, ParseException {

		String uid = null;

		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).name.equals(name)) {
				uid = users.get(i)._id;
			}
		}

		return uid;
	}
	
	// TO BE IMPLEMENTED IN SERVER
	public boolean createMeasurement(String name, String description, String website) {

			return false;
	}

	// TO BE IMPLEMENTED IN SERVER
	public boolean deleteMeasurement(String name) {

			return false;

	}

	public String getMeasurementId(String name) throws IOException,
			ParseException {

		String mid = null;

		for (int i = 0; i < measurements.size(); i++) {
			if (measurements.get(i).name.equals(name)) {
				mid = measurements.get(i)._id;
			}
		}

		return mid;
	}

	@SuppressWarnings("unchecked")
	public SDBExperiment createExperiment(String name, String timezone,
			String description, String website, String picture,
			int public_access) throws IOException, ParseException {

		for (int i = 0; i < experiments.size(); i++) {
			if (experiments.get(i).name.equals(name))
				return null;
		}

		URL url = new URL(session.host + "/experiments");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Cookie", session.cookie);

		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("timezone", timezone);

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
			return null;

		} else {

			JSONParser parser = new JSONParser();
			BufferedReader bin = new BufferedReader(new InputStreamReader(
					new DataInputStream(conn.getInputStream())));
			JSONObject jsonObject = (JSONObject) parser.parse(bin);
			bin.close();

			SDBExperiment experiment = new SDBExperiment(jsonObject);
			experiments.add(experiment);

			return experiment;
		}
	}

	public boolean deleteExperiment(String name) throws IOException,
			ParseException {

		String eid = null;
		int index = -1;

		for (int i = 0; i < experiments.size(); i++) {
			if (experiments.get(i).name.equals(name)) {
				eid = experiments.get(i)._id;
				index = i;
			}
		}

		if (eid != null) {

			URL url = new URL(session.host + "/experiments?eid=" + eid);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Cookie", session.cookie);

			if (conn.getResponseCode() == 200) {
				experiments.remove(index);
				return true;
			} else {
				System.out.println("DELETE method failed: "
						+ conn.getResponseCode() + "\t"
						+ conn.getResponseMessage());
				return false;
			}
		}

		else
			return false;

	}

	public String getExperimentId(String name) throws IOException, ParseException {

		String eid = null;

		for (int i = 0; i < experiments.size(); i++) {
			if (experiments.get(i).name.equals(name)) {
				eid = experiments.get(i)._id;
			}
		}

		return eid;
	}

	private void populateSensorDB(JSONObject data) {

		JSONObject userdata = (JSONObject) data.get("user");
		this.session.user = new SDBUser(userdata);

		JSONArray experArray = (JSONArray) data.get("experiments");
		JSONArray nodesArray = (JSONArray) data.get("nodes");
		JSONArray streamsArray = (JSONArray) data.get("streams");

		if (experArray.size() > 0) {
			JSONObject experJSON;
			SDBExperiment experiment;

			for (int i = 0; i < experArray.size(); i++) {
				experJSON = (JSONObject) experArray.get(i);
				// System.out.println("Experiments:");
				// System.out.println(experJSON.toJSONString());
				experiment = new SDBExperiment(experJSON);
				experiments.add(experiment);
			}

		}

		if (nodesArray.size() > 0) {
			JSONObject nodesJSON;
			SDBNode node;

			for (int i = 0; i < nodesArray.size(); i++) {
				nodesJSON = (JSONObject) nodesArray.get(i);
				// System.out.println("Nodes:");
				// System.out.println(nodesJSON.toJSONString());
				node = new SDBNode(nodesJSON);

				for (int j = 0; j < experiments.size(); j++) {
					if (experiments.get(j)._id.equals(node.eid))
						experiments.get(j).nodes.add(node);
				}
			}
		}

		if (streamsArray.size() > 0) {
			JSONObject streamsJSON;
			SDBStream stream;

			for (int i = 0; i < streamsArray.size(); i++) {
				streamsJSON = (JSONObject) streamsArray.get(i);
				// System.out.println("Streams:");
				// System.out.println(streamsJSON.toJSONString());
				stream = new SDBStream(streamsJSON);

				for (int j = 0; j < experiments.size(); j++) {
					for (int k = 0; k < experiments.get(j).nodes.size(); k++) {
						if (experiments.get(j).nodes.get(k)._id
								.equals(stream.nid))
							experiments.get(j).nodes.get(k).streams.add(stream);
					}
				}
			}
		}

	}

	public List<SDBUser> getUsers() throws IOException, ParseException {

		List<SDBUser> users = new ArrayList<SDBUser>();

		URL url = new URL(session.host + "/users");
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
				JSONObject userJSON;

				for (int i = 0; i < jsonArray.size(); i++) {
					userJSON = (JSONObject) jsonArray.get(i);
					SDBUser user = new SDBUser(userJSON);
					users.add(user);
				}
			}
		}

		return users;
	}

	public List<SDBMeasurement> getMeasurements() throws IOException,
			ParseException {

		List<SDBMeasurement> measurements = new ArrayList<SDBMeasurement>();

		URL url = new URL(session.host + "/measurements");
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
					SDBMeasurement meas = new SDBMeasurement(measJSON);
					measurements.add(meas);
				}
			}
		}

		return measurements;
	}

}
