package cz.metacentrum.perun.wui.profile.pages;

import com.google.gwt.core.client.JavaScriptObject;
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
import cz.metacentrum.perun.wui.client.resources.PerunSession;
import cz.metacentrum.perun.wui.client.utils.JsUtils;
import cz.metacentrum.perun.wui.json.JsonEvents;
import cz.metacentrum.perun.wui.json.managers.UsersManager;
import cz.metacentrum.perun.wui.model.PerunException;
import cz.metacentrum.perun.wui.model.beans.*;
import cz.metacentrum.perun.wui.model.resources.PerunComparator;
import cz.metacentrum.perun.wui.profile.client.resources.PerunProfilePlaceTokens;
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
		void setUser(User user);
		void setVos(List<Vo> vos, int userId);
		void onLoadingStart();
		void onError(PerunException ex, JsonEvents retry);
	}

	@NameToken(PerunProfilePlaceTokens.GROUPS)
	@ProxyStandard
	public interface MyProxy extends ProxyPlace<GroupsPresenter> {
	}

	@Inject
	public GroupsPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy) {
		super(eventBus, view, proxy, PerunPresenter.SLOT_MAIN_CONTENT);
	}

	@Override
	public void prepareFromRequest(final PlaceRequest request) {

		super.prepareFromRequest(request);

//
//		try {
//
//			String userId = request.getParameter("id", null);
//			if (userId == null) {
//				userId = String.valueOf(PerunSession.getInstance().getUserId());
//			}
//
//			final int id = Integer.valueOf(userId);
//
//			if (id < 1) {
//				placeManager.revealErrorPlace(request.getNameToken());
//			}
//
//			if (PerunSession.getInstance().isSelf(id)) {
//
//				UsersManager.getRichUserWithAttributes(id, new JsonEvents() {
//
//					final JsonEvents retry = this;
//
//					@Override
//					public void onFinished(JavaScriptObject jso) {
//						RichUser user = jso.cast();
//						getView().setUser(user);
//						getProxy().manualReveal(presenter);
//					}
//
//					@Override
//					public void onError(PerunException error) {
//						if (error.getName().equals("PrivilegeException")) {
//							getProxy().manualRevealFailed();
//							placeManager.revealUnauthorizedPlace(request.getNameToken());
//						} else {
//							getProxy().manualRevealFailed();
//							placeManager.revealErrorPlace(request.getNameToken());
//							getView().onError(error, retry);
//						}
//					}
//
//					@Override
//					public void onLoadingStart() {
//						getView().onLoadingStart();
//					}
//				});
//
//			} else {
//				placeManager.revealUnauthorizedPlace(request.getNameToken());
//			}
//
//		} catch( NumberFormatException e ) {
//			getProxy().manualRevealFailed();
//			placeManager.revealErrorPlace(request.getNameToken());
//		}

	}

	@Override
	protected void onReveal() {
		getVos();
	}

	@Override
	public void getVos() {
		final Integer userId = getUserId();
		UsersManager.getVosWhereUserIsMember(userId, new JsonEvents() {
			@Override
			public void onFinished(JavaScriptObject result) {
				List<Vo> vos = JsUtils.<Vo>jsoAsList(result);
				Collections.sort(vos, new PerunComparator<Vo>(PerunColumnType.NAME));
				getView().setVos(vos, userId);
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
