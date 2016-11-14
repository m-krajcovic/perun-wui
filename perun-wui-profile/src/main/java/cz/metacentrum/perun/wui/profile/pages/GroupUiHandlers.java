package cz.metacentrum.perun.wui.profile.pages;

import com.gwtplatform.mvp.client.UiHandlers;
import cz.metacentrum.perun.wui.model.beans.Member;
import cz.metacentrum.perun.wui.model.beans.Vo;

/**
 * @author Michal Krajčovič <mkrajcovic@mail.muni.cz>
 */
public interface GroupUiHandlers extends UiHandlers {
	void loadVos();

	void loadMember(Vo vo);

	void loadGroups(Vo vo, Member member);

	void loadResources(Vo vo, Member member);
}
