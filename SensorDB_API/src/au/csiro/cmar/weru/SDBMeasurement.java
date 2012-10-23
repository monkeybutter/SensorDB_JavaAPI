package au.csiro.cmar.weru;

import org.json.simple.JSONObject;

public class SDBMeasurement {
	public String _id;
	public String name;
	public String website;
	public String description;
	public long created;
	public long updated;
	
	public SDBMeasurement(JSONObject JSONmeasure) {
		_id = JSONmeasure.get("_id").toString();
		name = JSONmeasure.get("name").toString();
		website = JSONmeasure.get("website").toString();
		description = JSONmeasure.get("description").toString();
		created = Double.valueOf(JSONmeasure.get("created_at").toString()).longValue();
		updated = Double.valueOf(JSONmeasure.get("updated_at").toString()).longValue();
	}
	
}
