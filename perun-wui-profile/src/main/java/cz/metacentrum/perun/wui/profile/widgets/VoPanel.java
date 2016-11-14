package cz.metacentrum.perun.wui.profile.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import cz.metacentrum.perun.wui.model.beans.Group;
import cz.metacentrum.perun.wui.model.beans.Member;
import cz.metacentrum.perun.wui.model.beans.Resource;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.html.Div;

/**
 * @author Michal Krajcovic <mkrajcovic@mail.muni.cz>
 */
public class VoPanel extends Composite {

	interface VoPanelUiBinder extends UiBinder<Div, VoPanel> {
	}

	private static VoPanel.VoPanelUiBinder ourUiBinder = GWT.create(VoPanel.VoPanelUiBinder.class);

	private Member member;

	@UiField
	Row firstRow;

	@UiField
	Row secondRow;

	@UiField
	Column statusColumn;

	@UiField
	LoadableDiv groupsDiv;

	@UiField
	LoadableDiv resourcesDiv;

	private Div div;

	public VoPanel(Member member) {
		this();
		this.member = member;
	}

	public VoPanel() {
		div = ourUiBinder.createAndBindUi(this);
		initWidget(div);

		groupsDiv.setMapper(new LoadableDiv.JsonStringMapper() {
			@Override
			public String toString(JavaScriptObject obj) {
				Group g = obj.cast();
				return g.getName() + " - " + g.getDescription();
			}
		});

		resourcesDiv.setMapper(new LoadableDiv.JsonStringMapper() {
			@Override
			public String toString(JavaScriptObject obj) {
				Resource g = obj.cast();
				return g.getDescription() + " (" + g.getName() + ")";
			}
		});
	}

	public Row getFirstRow() {
		return firstRow;
	}

	public Row getSecondRow() {
		return secondRow;
	}

	public Column getStatusColumn() {
		return statusColumn;
	}

	public LoadableDiv getGroupsDiv() {
		return groupsDiv;
	}

	public LoadableDiv getResourcesDiv() {
		return resourcesDiv;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Div getRoot() {
		return div;
	}
}
