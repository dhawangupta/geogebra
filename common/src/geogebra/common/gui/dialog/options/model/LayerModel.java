package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.Localization;
import geogebra.common.plugin.EuclidianStyleConstants;

import java.util.ArrayList;
import java.util.List;

public class LayerModel extends MultipleOptionsModel {

	public LayerModel(IComboListener listener) {
		super(listener);
	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index).isDrawable();
	}

	@Override
	public List<String> getChoiches(Localization loc) {
		List<String> choices = new ArrayList<String>();
		for (int layer = 0; layer <= EuclidianStyleConstants.MAX_LAYERS; ++layer) {
			choices.add(" " + layer);
		};
		return choices;
	}

	@Override
	protected void apply(int index, int value) {
		GeoElement geo = getGeoAt(index); 
		geo.setLayer(value);
		geo.updateRepaint();
	
	}

	@Override
	public int getValueAt(int index) {
		return getGeoAt(index).getLayer();
	}

}
