package cz.metacentrum.perun.wui.profile.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.html.Div;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michal Krajcovic <mkrajcovic@mail.muni.cz>
 */
public class DataPanels<T, F extends Widget> extends Composite {

	private Map<T, DataPanel<T, F>> map = new HashMap<>();

	public DataPanels(Collection<T> set, DataPanel.DataPanelContentProvider<T, F> contentProvider) {
		Div root = new Div();
		initWidget(root);

		boolean in = false;
		for (final T model : set) {
			DataPanel<T, F> p = new DataPanel<>(model, contentProvider);
			if (!in) {
				p.getPanelCollapse().setIn(true);
				in = true;
			}
			root.add(p);
			map.put(model, p);
		}
	}

	public DataPanel<T, F> get(T key) {
		return map.get(key);
	}

	public F getBody(T key) {
		return get(key).getContent();
	}

	public Panel getPanel(T key) {
		return get(key).getRoot();
	}
}
