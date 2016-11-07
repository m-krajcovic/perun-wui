package cz.metacentrum.perun.wui.json.managers;

import com.google.gwt.http.client.Request;
import cz.metacentrum.perun.wui.json.JsonClient;
import cz.metacentrum.perun.wui.json.JsonEvents;

/**
 * Manager with standard callbacks to Perun's API (ResourcesManager).
 * <p/>
 * Each callback returns unique Request used to make call. Such call can be removed
 * while processing to prevent any further actions based on it's {@link cz.metacentrum.perun.wui.json.JsonEvents JsonEvents}.
 *
 * @author Michal Krajčovič <mkrajcovic@mail.muni.cz>
 */
public class ResourcesManager {

	private static final String RESOURCES_MANAGER = "resourcesManager/";

	/**
	 *
	 * @param memberId
	 * @return
	 */
	public static Request getAllowedResources(int memberId, JsonEvents events) {
		JsonClient client = new JsonClient(true, events);
		client.put("member", memberId);
		return client.call(RESOURCES_MANAGER + "getAllowedResources");
	}
}
