package geogebra.web.gui.util;

import geogebra.html5.awt.GDimensionW;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Slider widget that allows Double values
 * 
 * parts of the code copied from {@link geogebra.html5.gui.util.Slider} (which
 * can only be used for Integers)
 * 
 */
public class SliderW extends FocusWidget implements HasChangeHandlers,
		HasValue<Double>, MouseDownHandler, MouseUpHandler, MouseMoveHandler {

	private Element range;
	private boolean valueChangeHandlerInitialized;
	protected Double valueOnDragStart;

	public SliderW(double min, double max) {
		range = Document.get().createElement("input");
		range.setAttribute("type", "range");
		range.setAttribute("min", String.valueOf(min));
		range.setAttribute("max", String.valueOf(max));
		setRangeValue(range, String.valueOf(min));
		setElement(range);
		addMouseDownHandler(this);
		addMouseMoveHandler(this);
		addMouseUpHandler(this);
	}

	private native void setRangeValue(Element range, String value) /*-{
		range.value = value;
	}-*/;

	public Double getValue() {
		return Double.valueOf(getRangeValue(range));
	}

	private native String getRangeValue(Element range1) /*-{
		return range1.value;
	}-*/;

	public void setMinimum(double min) {
		range.setAttribute("min", String.valueOf(min));
	}

	public void setMaximum(double max) {
		range.setAttribute("max", String.valueOf(max));
	}

	public void setMajorTickSpacing(double step) {
		range.setAttribute("step", String.valueOf(step));
	}

	public void setMinorTickSpacing(double step) {
		range.setAttribute("step", String.valueOf(step));
	}

	public GDimensionW getPreferredSize() {
		return new GDimensionW(100, 10);
	}

	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Double> handler) {
		if (!valueChangeHandlerInitialized) {
			valueChangeHandlerInitialized = true;
			addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					ValueChangeEvent.fire(SliderW.this, getValue());
				}
			});
		}
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public void setValue(Double value) {
		setValue(value, false);
	}

	public void setValue(Double value, boolean fireEvents) {
		setSliderValue(String.valueOf(value));
	}

	private void setSliderValue(String value) {
		setRangeValue(range, value);
	}

	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}

	public void onMouseUp(MouseUpEvent event) {
		ValueChangeEvent.fireIfNotEqual(this, valueOnDragStart, getValue());
		valueOnDragStart = null;
	}

	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();
		valueOnDragStart = getValue();
	}

	public void onMouseMove(MouseMoveEvent event) {
		// the event is only fired when the slider is dragged to a new value
		if (valueOnDragStart != null) {
			ValueChangeEvent.fireIfNotEqual(this, valueOnDragStart, getValue());
		}
		event.stopPropagation();
	}

}
