package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.main.MyError;

/*
 * Pyramid[ <GeoPoint3D>, <GeoPoint3D>, <GeoPoint3D>, ... ] 
 */
public class CmdPyramid extends CommandProcessor {

	public CmdPyramid(Kernel kernel) {
		super(kernel);

	}

	public GeoElement[] process(Command c) throws MyError {

		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		if (n == 2) {
			if ((ok[0] = (arg[0].isGeoPolygon()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = kernelA.getManager3D()
						.Pyramid(c.getLabels(), (GeoPolygon) arg[0],
								(GeoPointND) arg[1]);
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPolygon()))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))) {
				GeoElement[] ret = kernelA.getManager3D().Pyramid(
						c.getLabels(), (GeoPolygon) arg[0],
						(GeoNumberValue) arg[1]);
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}
		} else if (n > 2) {
			// polygon for given points
			GeoPointND[] points = new GeoPointND[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint())) {
					throw argErr(app, c.getName(), arg[i]);
				}
				points[i] = (GeoPointND) arg[i];
			}
			// everything ok
			GeoElement[] ret = kernelA.getManager3D().Pyramid(c.getLabels(),
					points);
			return ret;
		} else {
			throw argNumErr(app, c.getName(), n);
		}

	}

}
