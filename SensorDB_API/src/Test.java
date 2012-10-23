
import au.csiro.cmar.weru.*;

public class Test {
	


	/**
	 * @param args
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {	
			
		//SensorDB sensor = new SensorDB("http://phenonet.com:9001", "CMAR", "bernido");
		SensorDB sensor = new SensorDB("http://phenonet.com:9001", "CMAR", "CMARCMAR");
		
		
		
		
		//System.out.println(sensor.experiments.size());
		//System.out.println(sensor.experiments.get(0).nodes.size());
		System.out.println(sensor.experiments.get(0).nodes.get(0)._id);
		
		//sensor.createExperiment("CMAR", "Australia/Canberra", "Testing Java API to upload CMAR Solar Stations Data", "", "", 0);
		//sensor.experiments.get(0).createNode("http://phenonet.com:9001", sensor.session.cookie, "Black Mountain", sensor.experiments.get(0)._id, "Solar Station on Black Mountain", "", "", 0);
		sensor.experiments.get(0).nodes.get(0).deleteStream(sensor.session, "Temperature1");
		sensor.experiments.get(0).nodes.get(0).createStream(sensor.session, "Temperature1", sensor.experiments.get(0).nodes.get(0)._id, "5084e43cca0df8c312193408", "Something", "", "");
		
		System.out.println("First");
		for (int i=1; i<=30; i++) {
			StringBuilder file = new StringBuilder("/home/roz016/workspace/SolarProject/CSV/BM06-01.csv");
			file.replace(45, 47, formatValue(i,2));
			System.out.println(file.toString());
			//System.out.println(file.toString());
			sensor.experiments.get(0).nodes.get(0).streams.get(0).postData(sensor.session, file.toString(), 5);
		}
		/*
		System.out.println("Second");
		for (int i=1; i<=31; i++) {
			StringBuilder file = new StringBuilder("/home/roz016/workspace/SolarProject/CSV/BM07-01.csv");
			file.replace(45, 47, formatValue(i,2));
			System.out.println(file.toString());
			//System.out.println(file.toString());
			sensor.experiments.get(0).nodes.get(0).streams.get(0).postData(sensor.session, file.toString());
		}
		
		System.out.println("Third");
		for (int i=1; i<=31; i++) {
			StringBuilder file = new StringBuilder("/home/roz016/workspace/SolarProject/CSV/BM08-01.csv");
			file.replace(45, 47, formatValue(i,2));
			System.out.println(file.toString());
			//System.out.println(file.toString());
			sensor.experiments.get(0).nodes.get(0).streams.get(0).postData(sensor.session, file.toString());
		}
		
		/*
		
		List<SDBMeasurement> meas = sensor.getMeasurements();
		
		for (int i = 0; i < meas.size(); i ++) {
			System.out.println(meas.get(i).name + " " + meas.get(i)._id);
		}
		
		List<SDBMeasurement> array = sensor.getMeasurements();
		
		System.out.println(array.get(0).description);
		System.out.println(array.get(1).description);
		System.out.println(array.get(2).description);
		System.out.println(array.get(3).description);
		System.out.println(array.get(4).description);
		System.out.println(array.get(5).description);
		System.out.println(array.get(6).description);
		
		//sensor.deleteExperiment("CMAR");
		//sensor.createExperiment("CMAR", "Australia/Canberra", "Testing Java API to upload CMAR Solar Stations Data", "", "", 0);
		//sensor.experiments.get(0).createNode("http://phenonet.com:9001", sensor.cookie, "Black Mountain", sensor.experiments.get(0)._id, "Solar Station on Black Mountain", "", "", 0);
		//sensor.experiments.get(0).nodes.get(0).createStream("http://phenonet.com:9001", sensor.cookie, "Temperature1", sensor.experiments.get(0).nodes.get(0)._id, "502f9f0a515a21b8bdc81d27", "Something", "", "");
		//sensor.experiments.get(0).nodes.get(0).streams.get(0).postData("http://phenonet.com:9001", sensor.cookie);
		//sensor.experiments.get(0).nodes.get(0).deleteStream("http://phenonet.com:9001", sensor.cookie, "Temperature");
		
		
		//System.out.println(sensor.getMeasurements().toJSONString());
		
		for (int i = 0; i < sensor.experiments.size(); i++) {
			System.out.println("Experiment " + (i+1) + ": " + sensor.experiments.get(i).name);
			for (int j = 0; j < sensor.experiments.get(i).nodes.size(); j++) {
				System.out.println("   Node " + (j + 1) + ": " + sensor.experiments.get(i).nodes.get(j).name);
				for (int k = 0; k < sensor.experiments.get(i).nodes.get(j).streams.size(); k++) {
					System.out.println("      Stream " + (k + 1) + ": " + sensor.experiments.get(i).nodes.get(j).streams.get(k).name);
				}
			}
		}
		/*
		sensor.deleteExperiment("TestExperiment");
		
		for (int i = 0; i < sensor.experiments.size(); i++) {
			System.out.println("Experiment " + (i+1) + ": " + sensor.experiments.get(i).name);
			for (int j = 0; j < sensor.experiments.get(i).nodes.size(); j++) {
				System.out.println("   Node " + (j + 1) + ": " + sensor.experiments.get(i).nodes.get(j).name);
			}
		}
		
		// Test for login, creating and deleting an user
		
		SensorDB sensor = new SensorDB("http://phenonet.com:9001", "MonkeyButter", "Aemet2005");
		List<SDBUser> array = sensor.getUsers();
		System.out.println("There are " + array.size() + " registered users in the server");
		
		JSONObject output = sensor.register("testdummy", "testdummy", "test@dum.my", null, null, null);
		
		array = sensor.getUsers();
		System.out.println("There are " + array.size() + " registered users in the server");
		
		output = sensor.remove("testdummy", "testdummy");
		array = sensor.getUsers();
		System.out.println("There are " + array.size() + " registered users in the server");
		 */		
		
	}
	
	private static String formatValue(int value, int positions) {
		StringBuilder sbValue = new StringBuilder(Integer.toString(value));

		while (sbValue.length() < positions) {
			sbValue.insert(0, "0");
		}

		return sbValue.toString();
	}


}
