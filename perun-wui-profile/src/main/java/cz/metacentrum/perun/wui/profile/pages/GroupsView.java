package cz.metacentrum.perun.wui.profile.pages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import cz.metacentrum.perun.wui.client.utils.JsUtils;
import cz.metacentrum.perun.wui.json.JsonEvents;
import cz.metacentrum.perun.wui.json.managers.GroupsManager;
import cz.metacentrum.perun.wui.json.managers.MembersManager;
import cz.metacentrum.perun.wui.json.managers.ResourcesManager;
import cz.metacentrum.perun.wui.json.managers.UsersManager;
import cz.metacentrum.perun.wui.model.PerunException;
import cz.metacentrum.perun.wui.model.beans.*;
import cz.metacentrum.perun.wui.model.resources.PerunComparator;
import cz.metacentrum.perun.wui.profile.client.resources.PerunProfileTranslation;
import cz.metacentrum.perun.wui.widgets.PerunLoader;
import cz.metacentrum.perun.wui.widgets.resources.PerunColumnType;
import org.gwtbootstrap3.client.shared.event.HiddenEvent;
import org.gwtbootstrap3.client.shared.event.HiddenHandler;
import org.gwtbootstrap3.client.shared.event.ShownEvent;
import org.gwtbootstrap3.client.shared.event.ShownHandler;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.client.ui.html.Small;
import org.gwtbootstrap3.client.ui.html.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * View for displaying VO membership details
 *
 * @author Pavel Zlámal <zlamal@cesnet.cz>
 */
public class GroupsView extends ViewWithUiHandlers<GroupUiHandlers> implements GroupsPresenter.MyView {

	private RichUser user;

	private Vo vo;

	interface GroupsViewUiBinder extends UiBinder<Widget, GroupsView> {
	}

	private PerunProfileTranslation translation = GWT.create(PerunProfileTranslation.class);

	@UiField
	PerunLoader loader;

	@UiField
	Text text;

	@UiField
	Small small;

	@UiField
	Alert paragraph;

	@UiField
	Div page;

	//@UiField(provided = true)
	//PerunDataGrid<Vo> grid = new PerunDataGrid<Vo>(false, new VoColumnProvider());

	@Inject
	public GroupsView(GroupsViewUiBinder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	public void draw() {

		text.setText(translation.menuGroups());

//		UsersManager.getVosWhereUserIsMember(user.getId(), new JsonEvents() {
//
//			final JsonEvents events = this;
//
//			@Override
//			public void onFinished(JavaScriptObject result) {
//				//grid.setList(JsUtils.<Vo>jsoAsList(result));
//
//				ArrayList<Vo> vos = JsUtils.<Vo>jsoAsList(result);
//				Collections.sort(vos, new PerunComparator<Vo>(PerunColumnType.NAME));
//				boolean in = false;
//				for (final Vo vo : vos) {
//
//					final Panel p = new Panel();
//					final PanelCollapse pc = new PanelCollapse();
//					if (!in) {
//						pc.setIn(true);
//						in = true;
//					}
//					final PanelHeader ph = new PanelHeader();
//					final PanelBody body = new PanelBody();
//					Heading head = new Heading(HeadingSize.H4, vo.getName());
//					ph.add(head);
//					ph.setDataToggle(Toggle.COLLAPSE);
//					ph.setDataTargetWidget(pc);
//
//					p.add(ph);
//					p.add(pc);
//
//					pc.add(body);
//					body.add(new Paragraph("There will be content"));
//					page.add(p);
//
//					MembersManager.getMemberByUser(user.getId(), vo.getId(), new JsonEvents() {
//						@Override
//						public void onFinished(JavaScriptObject result) {
//							final Member member = result.cast();
//
//							Paragraph par = new Paragraph("Membership status: " + member.getMembershipStatus());
//							body.clear();
//							body.add(par);
//
//							if (member.getMembershipStatus().equals("VALID")) p.setType(PanelType.DEFAULT);
//							if (member.getMembershipStatus().equals("EXPIRED")) p.setType(PanelType.WARNING);
//							if (member.getMembershipStatus().equals("DISABLED")) p.setType(PanelType.DANGER);
//							if (member.getMembershipStatus().equals("INVALID")) p.setType(PanelType.DANGER);
//							if (member.getMembershipStatus().equals("SUSPENDED")) p.setType(PanelType.DANGER);
//
//
//							Row row = new Row();
//							body.add(row);
//							Column colLeft = loadColumn("Groups", new JsonStringMapper() {
//								@Override
//								public String toString(JavaScriptObject obj) {
//									Group g = obj.cast();
//									return g.getShortName();
//								}
//							}, new CallFront() {
//								@Override
//								public void call(JsonEvents events) {
//									GroupsManager.getMemberGroups(member.getId(), events);
//								}
//							});
//							row.add(colLeft);
//							Column colRight = loadColumn("Resources", new JsonStringMapper() {
//								@Override
//								public String toString(JavaScriptObject obj) {
//									Resource g = obj.cast();
//									return g.getName();
//								}
//							}, new CallFront() {
//								@Override
//								public void call(JsonEvents events) {
//									ResourcesManager.getAllowedResources(member.getId(), events);
//								}
//							});
//							row.add(colRight);
//						}
//
//						@Override
//						public void onError(PerunException error) {
//
//						}
//
//						@Override
//						public void onLoadingStart() {
//
//						}
//					});
//
//
//				}
//
//			}
//
//			@Override
//			public void onError(PerunException error) {
//				/*grid.getLoaderWidget().onError(error, new ClickHandler() {
//					@Override
//					public void onClick(ClickEvent event) {
//						VosManager.getVos(false, events);
//					}
//				});*/
//			}
//
//			@Override
//			public void onLoadingStart() {
//				//grid.getLoaderWidget().onLoading();
//				page.clear();
//			}
//		});

	}

	private Column loadColumn(final String text, final JsonStringMapper mapper, final CallFront front) {
		final Column column = new Column(ColumnSize.XS_12);
		final Button button = new Button("Load " + text);
		button.getElement().getStyle().setMarginBottom(6, Style.Unit.PX);

		button.addClickHandler(new ClickHandler() {
			private boolean loaded = false;
			@Override
			public void onClick(ClickEvent event) {
				if (!loaded) {
					front.call(new JsonEvents() {
						@Override
						public void onFinished(JavaScriptObject result) {
							JsArray result1 = (JsArray) result;
							final ListGroup list = new ListGroup();
							final PanelCollapse pc = new PanelCollapse();
							for (int i = 0; i < result1.length(); i++) {
								JavaScriptObject obj = result1.get(i);
								ListGroupItem item = new ListGroupItem();
								item.setText(mapper.toString(obj));
								list.add(item);
							}
							pc.add(list);
							pc.setIn(true);
							column.add(pc);
							loaded = true;
							button.clear();
							button.setEnabled(true);
							button.setText("Hide " + text);
							button.setDataToggle(Toggle.COLLAPSE);
							button.setDataTargetWidget(pc);
							button.addClickHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									if (pc.isIn()) {
										button.setText("Show " + text);
									} else {
										button.setText("Hide " + text);
									}
								}
							});
						}

						@Override
						public void onError(PerunException error) {

						}

						@Override
						public void onLoadingStart() {
							button.setEnabled(false);
							Icon child = new Icon(IconType.SPINNER);
							child.setPulse(true);
							button.add(child);
						}
					});
				}
			}
		});
		column.add(button);
		return column;
	}

	private interface JsonStringMapper {
		String toString(JavaScriptObject obj);
	}

	private interface CallFront {
		void call(JsonEvents events);
	}

	@Override
	public void setUser(User user) {
		loader.onFinished();
		loader.setVisible(false);
		if (this.user == null || this.user.getId() != user.getId()) {
			this.user = user.cast();
			draw();
		}
	}

	@Override
	public void setVos(List<Vo> vos, int userId) {
		page.clear();
		loader.onFinished();
		loader.setVisible(false);
		boolean in = false;
		for (final Vo vo : vos) {

			final Panel p = new Panel();
			final PanelCollapse pc = new PanelCollapse();
			if (!in) {
				pc.setIn(true);
				in = true;
			}
			final PanelHeader ph = new PanelHeader();
			final PanelBody body = new PanelBody();
			Heading head = new Heading(HeadingSize.H4, vo.getName());
			ph.add(head);
			ph.setDataToggle(Toggle.COLLAPSE);
			ph.setDataTargetWidget(pc);

			p.add(ph);
			p.add(pc);

			pc.add(body);
			body.add(new Paragraph("There will be content"));
			page.add(p);

			MembersManager.getMemberByUser(userId, vo.getId(), new JsonEvents() {
				@Override
				public void onFinished(JavaScriptObject result) {
					final Member member = result.cast();

					Paragraph par = new Paragraph("Membership status: " + member.getMembershipStatus());
					body.clear();
					body.add(par);

					if (member.getMembershipStatus().equals("VALID")) p.setType(PanelType.DEFAULT);
					if (member.getMembershipStatus().equals("EXPIRED")) p.setType(PanelType.WARNING);
					if (member.getMembershipStatus().equals("DISABLED")) p.setType(PanelType.DANGER);
					if (member.getMembershipStatus().equals("INVALID")) p.setType(PanelType.DANGER);
					if (member.getMembershipStatus().equals("SUSPENDED")) p.setType(PanelType.DANGER);


					Row row = new Row();
					body.add(row);
					Column colLeft = loadColumn("Groups", new JsonStringMapper() {
						@Override
						public String toString(JavaScriptObject obj) {
							Group g = obj.cast();
							return g.getName() + " - " + g.getDescription();
						}
					}, new CallFront() {
						@Override
						public void call(JsonEvents events) {
							GroupsManager.getMemberGroups(member.getId(), events);
						}
					});
					row.add(colLeft);
					Column colRight = loadColumn("Resources", new JsonStringMapper() {
						@Override
						public String toString(JavaScriptObject obj) {
							Resource g = obj.cast();
							return g.getDescription() + " (" + g.getName() + ")";
						}
					}, new CallFront() {
						@Override
						public void call(JsonEvents events) {
							ResourcesManager.getAllowedResources(member.getId(), events);
						}
					});
					row.add(colRight);
				}

				@Override
				public void onError(PerunException error) {

				}

				@Override
				public void onLoadingStart() {

				}
			});


		}
	}

	@Override
	public void onLoadingStart() {
		loader.setVisible(true);
		loader.onLoading();
	}

	@Override
	public void onError(PerunException ex, final JsonEvents retry) {
		loader.onError(ex, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				UsersManager.getRichUserWithAttributes(user.getId(), retry);
			}
		});
	}

}
