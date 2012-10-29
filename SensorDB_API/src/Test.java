
import java.util.Calendar;

import au.csiro.cmar.weru.*;

public class Test {
	


	/**
	 * @param args
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {	
			
		
		SensorDB sensor = new SensorDB("http://phenonet.com:9001", "CMAR", "CMARCMAR");
				
		//sensor.experiments.get(0).nodes.get(0).deleteStream(sensor.session, "SPTemp1Physical");
		//sensor.experiments.get(0).nodes.get(0).deleteStream(sensor.session, "SPVoltsPhysical");
		//sensor.experiments.get(0).nodes.get(0).deleteStream(sensor.session, "WindSpeedPhysical");
				

		SDBNode blackMountain = sensor.getExperimentbyName("CMAR").getNodebyName("Black Mountain");
				
		SDBStream windSpd = blackMountain.getStreambyName("WindSpeedPhysical");
				
		Calendar start_date = Calendar.getInstance();
		start_date.set(Calendar.YEAR, 2012);
		start_date.set(Calendar.MONTH, 5);
		start_date.set(Calendar.DAY_OF_MONTH, 19);
				
		Calendar end_date = Calendar.getInstance();
				end_date.set(Calendar.YEAR, 2012);
				end_date.set(Calendar.MONTH, 5);
				end_date.set(Calendar.DAY_OF_MONTH, 22);
				
		windSpd.getData(sensor.session, start_date, end_date, "1-hour");
		//sensor.experiments.get(0).nodes.get(0).deleteStream(sensor.session, "SPPower");
		//sensor.experiments.get(0).nodes.get(0).deleteStream(sensor.session, "SPTemp1Physical");
		//sensor.experiments.get(0).nodes.get(0).deleteStream(sensor.session, "SPPowerPhysical");
				
				
		
		blackMountain.createStream(sensor.session, "WindSpeedPhysical", blackMountain._id, sensor.getMeasurementId("Celsius"), "Something", "", "");
		System.out.println("First");
		for (int i=1; i<=1; i++) {
			StringBuilder file = new StringBuilder("/home/roz016/workspace/SolarProject/CSV/BM06-01.csv");
			file.replace(45, 47, formatValue(i,2));
			System.out.println(file.toString());
			//blackMountain.getStreambyName("SPTemp1Physical").postData(sensor.session, file.toString(), 5);
			blackMountain.getStreambyName("WindSpeedPhysical").postData(sensor.session, file.toString(), 16);
		}
		
				
		//blackMountain.createStream(sensor.session, "WindSpeedPhysical", blackMountain._id, sensor.getMeasurementId("Metres per Second (m/s)"), "Something", "", "");
		//blackMountain.createStream(sensor.session, "SPVoltsPhysical", blackMountain._id, sensor.getMeasurementId("Voltage (V)"), "Something", "", "");
				
		//sensor.createExperiment("CMAR", "UTC", "Testing Java API to upload CMAR Solar Stations Data", "", "", 0);
		
	}
	
	private static String formatValue(int value, int positions) {
		StringBuilder sbValue = new StringBuilder(Integer.toString(value));

		while (sbValue.length() < positions) {
			sbValue.insert(0, "0");
		}

		return sbValue.toString();
	}

//
}
