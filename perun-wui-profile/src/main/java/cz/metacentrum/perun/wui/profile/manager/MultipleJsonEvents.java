package cz.metacentrum.perun.wui.profile.manager;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import cz.metacentrum.perun.wui.json.JsonEvents;
import cz.metacentrum.perun.wui.model.PerunException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michal Krajcovic <mkrajcovic@mail.muni.cz>
 */
public abstract class MultipleJsonEvents {

	private List<AddMultipleJsonEvent> multiples = new ArrayList<>();

	private Map<String, JavaScriptObject> result = new HashMap<>();

	public MultipleJsonEvents(AddMultipleJsonEvent... adds) {
		Collections.addAll(multiples, adds);
	}

	public abstract void onFinished(Map<String, JavaScriptObject> result);

	public abstract void onError(PerunException error);

	public abstract void onLoadingStart();

	public void addRequest(AddMultipleJsonEvent add) {
		multiples.add(add);
	}

	public void start() {
		for (AddMultipleJsonEvent multiple : multiples) {
			multiple.start(partialJsonEvents(multiple.paramName()));
		}
	}

	private boolean isReady() {
		return result.size() == multiples.size();
	}

	private JsonEvents partialJsonEvents(final String param) {
		return new JsonEvents() {
			@Override
			public void onFinished(JavaScriptObject result) {
				MultipleJsonEvents.this.result.put(param, result);
				if (isReady()) {
					MultipleJsonEvents.this.onFinished(MultipleJsonEvents.this.result);
				}
			}

			@Override
			public void onError(PerunException error) {
				MultipleJsonEvents.this.onError(error);
			}

			@Override
			public void onLoadingStart() {
				MultipleJsonEvents.this.onLoadingStart();
			}
		};
	}

	public interface AddMultipleJsonEvent {
		Request start(JsonEvents events);
		String paramName();
	}
}
