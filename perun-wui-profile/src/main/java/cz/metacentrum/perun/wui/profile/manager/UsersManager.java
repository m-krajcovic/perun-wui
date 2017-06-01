package cz.metacentrum.perun.wui.profile.manager;

import com.google.gwt.http.client.Request;
import cz.metacentrum.perun.wui.json.JsonEvents;

/**
 * @author Michal Krajcovic <mkrajcovic@mail.muni.cz>
 */
public interface UsersManager extends PerunJsonClientManager {
	Request getVosWhereUserIsMember(int user, JsonEvents events);
	Request isLoginAvailable(String loginNamespace, String login, JsonEvents events);
}
