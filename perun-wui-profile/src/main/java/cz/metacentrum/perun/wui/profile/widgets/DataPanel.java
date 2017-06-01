package cz.metacentrum.perun.wui.profile.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.constants.Toggle;

/**
 * @author Michal Krajcovic <mkrajcovic@mail.muni.cz>
 */
public class DataPanel<T, F extends Widget> extends Composite {

	public interface DataPanelContentProvider<T, F> {
		PanelHeader getHeader(T t);

		F getBody(T t);
	}

	private Panel root = new Panel();

	private PanelCollapse panelCollapse;

	private PanelHeader panelHeader;

	private PanelBody panelBody;

	private F content;

	public DataPanel(T model, DataPanel.DataPanelContentProvider<T, F> contentProvider) {
		initWidget(root);
		panelCollapse = new PanelCollapse();

		panelHeader = contentProvider.getHeader(model);
		panelHeader.setDataToggle(Toggle.COLLAPSE);
		panelHeader.setDataTargetWidget(panelCollapse);
		panelHeader.getElement().getStyle().setCursor(Style.Cursor.POINTER);

		panelBody = new PanelBody();
		content = contentProvider.getBody(model);
		panelBody.add(content);

		panelCollapse.add(panelBody);
		root.add(panelHeader);
		root.add(panelCollapse);
	}

	public F getContent() {
		return content;
	}

	public Panel getRoot() {
		return root;
	}

	public PanelCollapse getPanelCollapse() {
		return panelCollapse;
	}

	public PanelHeader getPanelHeader() {
		return panelHeader;
	}

	public PanelBody getPanelBody() {
		return panelBody;
	}
}
