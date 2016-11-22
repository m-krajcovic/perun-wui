package cz.metacentrum.perun.wui.profile.pages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import cz.metacentrum.perun.wui.client.PerunPresenter;
import cz.metacentrum.perun.wui.client.resources.PerunConfiguration;
import cz.metacentrum.perun.wui.client.resources.PerunSession;
import cz.metacentrum.perun.wui.client.utils.JsUtils;
import cz.metacentrum.perun.wui.json.JsonEvents;
import cz.metacentrum.perun.wui.json.managers.GroupsManager;
import cz.metacentrum.perun.wui.json.managers.MembersManager;
import cz.metacentrum.perun.wui.json.managers.ResourcesManager;
import cz.metacentrum.perun.wui.json.managers.UsersManager;
import cz.metacentrum.perun.wui.model.PerunException;
import cz.metacentrum.perun.wui.model.beans.Attribute;
import cz.metacentrum.perun.wui.model.beans.Group;
import cz.metacentrum.perun.wui.model.beans.Member;
import cz.metacentrum.perun.wui.model.beans.Resource;
import cz.metacentrum.perun.wui.model.beans.Vo;
import cz.metacentrum.perun.wui.model.resources.PerunComparator;
import cz.metacentrum.perun.wui.profile.client.resources.PerunProfilePlaceTokens;
import cz.metacentrum.perun.wui.profile.manager.AttributesManager;
import cz.metacentrum.perun.wui.profile.manager.MultipleJsonEvents;
import cz.metacentrum.perun.wui.widgets.resources.PerunColumnType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Presenter for displaying registration detail.
 *
 * @author Pavel Zlámal <zlamal@cesnet.cz>
 */
public class GroupsPresenter extends Presenter<GroupsPresenter.MyView, GroupsPresenter.MyProxy> implements GroupUiHandlers {

	private PlaceManager placeManager = PerunSession.getPlaceManager();
	private GroupsPresenter presenter = this;

	public interface MyView extends View, HasUiHandlers<GroupUiHandlers> {
		//		void setUser(User user);
		void setVos(List<Vo> vos);

		void setGroups(Vo vo, List<Group> groups);

		void setResources(Vo vo, List<Resource> resources);

		void onLoadingStart();

		void onError(PerunException ex, JsonEvents retry);

		void setMember(Vo vo, Member member, Attribute attribute, boolean extend);

		void startLoadingMember(Vo vo);
	}

	//	private UsersManager usersManager = GWT.create(UsersManager.class);
	private AttributesManager attributesManager = GWT.create(AttributesManager.class);
	private cz.metacentrum.perun.wui.profile.manager.MembersManager membersManager = GWT.create(cz.metacentrum.perun.wui.profile.manager.MembersManager.class);

	private int userId;

	@NameToken(PerunProfilePlaceTokens.GROUPS)
	@ProxyStandard
	public interface MyProxy extends ProxyPlace<GroupsPresenter> {
	}

	@Inject
	public GroupsPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy) {
		super(eventBus, view, proxy, PerunPresenter.SLOT_MAIN_CONTENT);
		getView().setUiHandlers(this);
	}

	@Override
	public void prepareFromRequest(final PlaceRequest request) {
		super.prepareFromRequest(request);
	}

	@Override
	protected void onReveal() {
		if (PerunConfiguration.areOrganizationsDisabled()) {
			placeManager.revealUnauthorizedPlace(PerunProfilePlaceTokens.UNAUTHORIZED);
		} else {
			userId = getUserId();
			loadVos();
		}
	}

	@Override
	public void loadVos() {
		UsersManager.getVosWhereUserIsMember(userId, new JsonEvents() {
			@Override
			public void onFinished(JavaScriptObject result) {
				List<Vo> vos = JsUtils.jsoAsList(result);
				Collections.sort(vos, new PerunComparator<Vo>(PerunColumnType.NAME));
				getView().setVos(vos);
			}

			@Override
			public void onError(PerunException error) {

			}

			@Override
			public void onLoadingStart() {
				getView().onLoadingStart();
			}
		});
	}

	@Override
	public void loadMember(final Vo vo) {
		MembersManager.getMemberByUser(userId, vo.getId(), new JsonEvents() {
			@Override
			public void onFinished(JavaScriptObject result) {
				final Member member = result.cast();

				MultipleJsonEvents multipleJsonEvents = new MultipleJsonEvents(new MultipleJsonEvents.AddMultipleJsonEvent() {
					@Override
					public Request start(JsonEvents events) {
						return membersManager.canExtendMembership(member.getId(), events);
					}

					@Override
					public String paramName() {
						return "extend";
					}
				}, new MultipleJsonEvents.AddMultipleJsonEvent() {
					@Override
					public Request start(JsonEvents events) {
						return attributesManager.getAttribute(member.getId(), "urn:perun:member:attribute-def:def:membershipExpiration", events);
					}

					@Override
					public String paramName() {
						return "attribute";
					}
				}) {
					@Override
					public void onFinished(Map<String, JavaScriptObject> result) {
						int extend = JsUtils.getNativePropertyInt(result.get("extend"), "value");
						getView().setMember(vo, member, (Attribute) result.get("attribute").cast(), extend == 1);
					}

					@Override
					public void onError(PerunException error) {

					}

					@Override
					public void onLoadingStart() {

					}
				};

				multipleJsonEvents.start();
			}

			@Override
			public void onError(PerunException error) {

			}

			@Override
			public void onLoadingStart() {
				getView().startLoadingMember(vo);
			}
		});
	}

	@Override
	public void loadGroups(final Vo vo, Member member) {
		GroupsManager.getMemberGroups(member.getId(), new JsonEvents() {
			@Override
			public void onFinished(JavaScriptObject result) {
				List<Group> groups = JsUtils.listFromJsArray((JsArray) result.cast());
				getView().setGroups(vo, groups);
			}

			@Override
			public void onError(PerunException error) {

			}

			@Override
			public void onLoadingStart() {

			}
		});
	}

	@Override
	public void loadResources(final Vo vo, Member member) {
		ResourcesManager.getAllowedResources(member.getId(), new JsonEvents() {
			@Override
			public void onFinished(JavaScriptObject result) {
				List<Resource> resources = JsUtils.listFromJsArray((JsArray) result.cast());
//				model.setResources(resources);
				getView().setResources(vo, resources);
			}

			@Override
			public void onError(PerunException error) {

			}

			@Override
			public void onLoadingStart() {

			}
		});
	}

	private Integer getUserId() {

		try {

			String userId = placeManager.getCurrentPlaceRequest().getParameter("id", null);
			if (userId == null) {
				userId = String.valueOf(PerunSession.getInstance().getUserId());
			}

			final int id = Integer.valueOf(userId);

			if (id < 1) {
				placeManager.revealErrorPlace(placeManager.getCurrentPlaceRequest().getNameToken());
			}

			return id;
		} catch (NumberFormatException e) {
			placeManager.revealErrorPlace(placeManager.getCurrentPlaceRequest().getNameToken());
		}
		return null;
	}
}
