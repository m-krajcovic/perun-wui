package cz.metacentrum.perun.wui.profile.pages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import cz.metacentrum.perun.wui.json.JsonEvents;
import cz.metacentrum.perun.wui.model.PerunException;
import cz.metacentrum.perun.wui.model.beans.Attribute;
import cz.metacentrum.perun.wui.model.beans.Group;
import cz.metacentrum.perun.wui.model.beans.Member;
import cz.metacentrum.perun.wui.model.beans.Resource;
import cz.metacentrum.perun.wui.model.beans.Vo;
import cz.metacentrum.perun.wui.profile.client.resources.PerunProfileTranslation;
import cz.metacentrum.perun.wui.profile.widgets.DataPanel;
import cz.metacentrum.perun.wui.profile.widgets.DataPanels;
import cz.metacentrum.perun.wui.profile.widgets.VoPanel;
import cz.metacentrum.perun.wui.widgets.PerunLoader;
import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.PanelType;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.client.ui.html.Small;
import org.gwtbootstrap3.client.ui.html.Text;

import java.util.List;

/**
 * View for displaying VO membership details
 *
 * @author Pavel Zlámal <zlamal@cesnet.cz>
 */
public class GroupsView extends ViewWithUiHandlers<GroupUiHandlers> implements GroupsPresenter.MyView {

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

	private DataPanels<Vo, VoPanel> panels;

	@Inject
	public GroupsView(GroupsViewUiBinder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	@Override
	public void setGroups(Vo vo, List<Group> groups) {
		VoPanel p = panels.getBody(vo);
		p.getGroupsDiv().setData(groups);
	}

	@Override
	public void setResources(Vo vo, List<Resource> resources) {
		VoPanel p = panels.getBody(vo);
		p.getResourcesDiv().setData(resources);
	}

	@Override
	public void setMember(Vo vo, Member member) {
		VoPanel voPanel = panels.getBody(vo);
		Panel panel = panels.getPanel(vo);
		Paragraph par = new Paragraph("Membership status: " + member.getMembershipStatus());
		voPanel.getStatusColumn().add(par);
		voPanel.setMember(member);
		switch (voPanel.getMember().getMembershipStatus()) {
			case "VALID":
				panel.setType(PanelType.DEFAULT);
				break;
			case "EXPIRED":
				panel.setType(PanelType.WARNING);
				break;
			default:
				panel.setType(PanelType.DANGER);
		}
	}

	@Override
	public void setMember(Vo vo, Member member, Attribute attribute, boolean extend) {
		VoPanel voPanel = panels.getBody(vo);
		Panel panel = panels.getPanel(vo);
		Paragraph par = new Paragraph("Membership status: " + member.getMembershipStatus());
		voPanel.getStatusColumn().add(par);
		voPanel.getStatusColumn().add(new Paragraph(attribute.getFriendlyName() + ": " + attribute.getValue()));
		if (extend) {
			voPanel.getStatusColumn().add(new Paragraph("EXTEND AVAILABLE"));
		}
		voPanel.setMember(member);
		switch (voPanel.getMember().getMembershipStatus()) {
			case "VALID":
				panel.setType(PanelType.DEFAULT);
				break;
			case "EXPIRED":
				panel.setType(PanelType.WARNING);
				break;
			default:
				panel.setType(PanelType.DANGER);
		}


	}

	@Override
	public void setVos(List<Vo> vos) {
		loader.onFinished();
		loader.setVisible(false);
		paragraph.setVisible(true);

		panels = new DataPanels<>(vos, new DataPanel.DataPanelContentProvider<Vo, VoPanel>() {
			@Override
			public PanelHeader getHeader(Vo vo) {
				PanelHeader ph = new PanelHeader();
				Heading head = new Heading(HeadingSize.H4, vo.getName());
				ph.add(head);
				return ph;
			}

			@Override
			public VoPanel getBody(final Vo vo) {
				final VoPanel voPanel = new VoPanel();

				ClickHandler clickHandler = new ClickHandler() {
					private boolean loaded = false;

					@Override
					public void onClick(ClickEvent event) {
						if (!loaded) {
							getUiHandlers().loadGroups(vo, panels.getBody(vo).getMember());
							voPanel.getGroupsDiv().getButton().setEnabled(false);
							Icon child = new Icon(IconType.SPINNER);
							child.setPulse(true);
							voPanel.getGroupsDiv().getButton().add(child);
							loaded = true;
						}
					}
				};
				voPanel.getGroupsDiv().getButton().addClickHandler(clickHandler);

				ClickHandler clickHandler1 = new ClickHandler() {
					private boolean loaded = false;

					@Override
					public void onClick(ClickEvent event) {
						if (!loaded) {
							getUiHandlers().loadResources(vo, panels.getBody(vo).getMember());
							voPanel.getResourcesDiv().getButton().setEnabled(false);
							Icon child = new Icon(IconType.SPINNER);
							child.setPulse(true);
							voPanel.getResourcesDiv().getButton().add(child);
							loaded = true;
						}
					}
				};
				voPanel.getResourcesDiv().getButton().addClickHandler(clickHandler1);

				return voPanel;
			}
		});
		for (Vo vo : vos) {
			getUiHandlers().loadMember(vo);
		}
		page.add(panels);
	}

	@Override
	public void onLoadingStart() {
		loader.setVisible(true);
		loader.onLoading(translation.loadingVos());
		page.clear();
	}

	@Override
	public void onError(PerunException ex, final JsonEvents retry) {
		loader.onError(ex, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
			}
		});
	}

}
