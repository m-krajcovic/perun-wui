package cz.metacentrum.perun.wui.profile.manager;

import com.google.gwt.http.client.Request;
import cz.metacentrum.perun.wui.json.JsonEvents;

/**
 * @author Michal Krajcovic <mkrajcovic@mail.muni.cz>
 */
public interface MembersManager extends PerunJsonClientManager {
	Request canExtendMembership(int member, JsonEvents events);
}
