package cz.metacentrum.perun.wui.profile.manager;

import com.google.gwt.http.client.Request;
import cz.metacentrum.perun.wui.json.JsonEvents;
import cz.metacentrum.perun.wui.model.beans.Vo;

/**
 * @author Michal Krajcovic <mkrajcovic@mail.muni.cz>
 */
public interface VosManager extends PerunJsonClientManager {
	Request getVos(JsonEvents events);
	Request getAllVos(JsonEvents events);
	Request getVoById(int id, JsonEvents events);
	Request createVo(Vo vo, JsonEvents events);
	Request updateVo(Vo vo, JsonEvents events);
	Request deleteVo(Vo vo, int force, JsonEvents events);
	Request findCandidates(int vo, String searchString, JsonEvents events);
}
