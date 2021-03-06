package geogebra.web.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.html5.main.AppW;

public class InputDialogCylinderTwoPointsRadiusW extends InputDialogRadiusW {

	private GeoPointND a, b;

	public InputDialogCylinderTwoPointsRadiusW(AppW app, String title,
            InputHandler handler, GeoPointND a, GeoPointND b, Kernel kernel) {
	    super(app, title, handler, kernel);
	    this.a = a;
	    this.b = b; 
	    
    }

	@Override
    protected GeoElement createOutput(NumberValue num) {
		return kernel.getManager3D().CylinderLimited(null, a, b, num)[0];
    }

}
