package cz.metacentrum.perun.wui.profile.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Composite;
import cz.metacentrum.perun.wui.profile.client.resources.PerunProfileTranslation;
import org.gwtbootstrap3.client.shared.event.HiddenEvent;
import org.gwtbootstrap3.client.shared.event.HiddenHandler;
import org.gwtbootstrap3.client.shared.event.ShownEvent;
import org.gwtbootstrap3.client.shared.event.ShownHandler;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.html.Div;

import java.util.List;

/**
 * @author Michal Krajcovic <mkrajcovic@mail.muni.cz>
 */
public class LoadableDiv extends Composite {

	private final Button button;

	private final Div div;

	private final ListGroup list = new ListGroup();

	private final PanelCollapse panelCollapse = new PanelCollapse();

	private String text;

	private JsonStringMapper mapper;

	private static PerunProfileTranslation translation = GWT.create(PerunProfileTranslation.class);

	public LoadableDiv(String text, JsonStringMapper mapper, ClickHandler clickHandler) {
		this(text, mapper);
		this.mapper = mapper;
		button.addClickHandler(clickHandler);
	}

	public LoadableDiv(String text, JsonStringMapper mapper) {
		this(text);
		this.mapper = mapper;
	}

	@UiConstructor
	public LoadableDiv(String text) {
		this();
		this.text = text;
		button.setText(translation.load(text) + " ");
	}

	public LoadableDiv() {
		button = new Button();
		div = new Div();
		initWidget(div);
		button.getElement().getStyle().setMarginBottom(6, Style.Unit.PX);
		div.add(button);
		div.add(panelCollapse);
		panelCollapse.add(list);
		panelCollapse.setIn(false);
		list.getElement().getStyle().setMarginBottom(0, Style.Unit.PX);
	}

	public void setData(List<? extends JavaScriptObject> data) {
		if (data.size() > 0) {
			list.clear();

			for (int i = 0; i < data.size(); i++) {
				JavaScriptObject obj = data.get(i);
				ListGroupItem item = new ListGroupItem();
				item.setText(mapper.toString(obj));
				list.add(item);
			}

			list.getElement().getStyle().setMarginBottom(20, Style.Unit.PX);

			collapseShow(panelCollapse.getElement());

			button.clear();
			button.setEnabled(true);
			button.setText(translation.hide(text));
			button.setDataToggle(Toggle.COLLAPSE);
			button.setDataTargetWidget(panelCollapse);

			panelCollapse.addHiddenHandler(new HiddenHandler() {
				@Override
				public void onHidden(HiddenEvent event) {
					button.setText(translation.show(text));
				}
			});

			panelCollapse.addShownHandler(new ShownHandler() {
				@Override
				public void onShown(ShownEvent event) {
					button.setText(translation.hide(text));
				}
			});
		} else {
			button.clear();
			button.setText(translation.zeroFound(text));
		}
	}

	public final native void collapseShow(Element e) /*-{
        $wnd.jQuery(e).collapse("show");
    }-*/;

	public interface JsonStringMapper {
		String toString(JavaScriptObject obj);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public JsonStringMapper getMapper() {
		return mapper;
	}

	public void setMapper(JsonStringMapper mapper) {
		this.mapper = mapper;
	}

	public Button getButton() {
		return button;
	}

	public Div getDiv() {
		return div;
	}
}
