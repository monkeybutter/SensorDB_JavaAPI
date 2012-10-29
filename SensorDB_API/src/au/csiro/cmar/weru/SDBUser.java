package au.csiro.cmar.weru;

import org.json.simple.JSONObject;

public class SDBUser {

	public String _id;
	public String name;
	public String picture;
	public String website;
	public String description;
	public long created;
	public long updated;
	public boolean active;
	
	public SDBUser(JSONObject JSONuser) {
		_id = JSONuser.get("_id").toString();
		name = JSONuser.get("name").toString();
		picture = JSONuser.get("picture").toString();
		website = JSONuser.get("website").toString();
		description = JSONuser.get("description").toString();
		if (JSONuser.containsKey("created_at"))
			created = Long.parseLong(JSONuser.get("created_at").toString());
		if (JSONuser.containsKey("updated_at"))
			updated = Long.parseLong(JSONuser.get("updated_at").toString());
	}
}
