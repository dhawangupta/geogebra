package geogebra.common.euclidian.draw;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.statistics.AlgoDotPlot;

import java.util.ArrayList;

/**
 * Drawable representation of a point plot
 * 
 */
public class DrawPointPlot extends Drawable {

	/** graph types */
	public enum DrawType {
		/** vertical bars */
		DOT_PLOT,
		/** horizontal bars */
		POINT_PLOT
	}

	private DrawType drawType = DrawType.DOT_PLOT;

	private boolean isVisible, labelVisible;
	private double[] coords = new double[2];

	private AlgoElement algo;
	private ArrayList<DrawPoint> drawPoints;

	private int pointStyle;
	private int pointSize;
	private int oldPointSize = -1;
	private int oldPointStyle = -1;
	private GColor oldColor = null;
	private GColor pointColor;

	/*************************************************
	 * @param view
	 *            view
	 * @param pointList
	 *            list of GeoPoints to plot
	 */
	public DrawPointPlot(EuclidianView view, GeoList pointList,
			DrawType drawType) {
		this.view = view;
		this.drawType = drawType;
		geo = pointList;
		init();
		update();
	}

	private void init() {
		algo = geo.getParentAlgorithm();
		drawPoints = new ArrayList<DrawPoint>();
		updatePointLists();
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public geogebra.common.awt.GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}

		GRectangle rect = drawPoints.get(0).getBounds();
		for (int i = 1; i < drawPoints.size(); i++) {
			rect.add(drawPoints.get(i).getBounds());
		}
		return rect;
	}

	@Override
	public void draw(geogebra.common.awt.GGraphics2D g2) {

		if (isVisible) {

			for (int i = 0; i < drawPoints.size(); i++) {
				((GeoList) geo).get(i).setHighlighted(geo.doHighlighting());
				drawPoints.get(i).draw(g2);
			}

			if (labelVisible) {
				g2.setFont(view.getFontConic());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {

		for (int i = 0; i < drawPoints.size(); i++) {
			if (drawPoints.get(i).hit(x, y, hitThreshold)) {
				setToolTipForPoint(i);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {

		for (int i = 0; i < drawPoints.size(); i++) {
			Drawable d = drawPoints.get(i);
			if (d.intersectsRectangle(rect))
				return true;
		}
		return false;
	}

	@Override
	public boolean isInside(GRectangle rect) {

		int size = drawPoints.size();
		for (int i = 0; i < size; i++) {
			Drawable d = drawPoints.get(i);
			if (!d.isInside(rect))
				return false;
		}
		return size > 0;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	@Override
	public void update() {

		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		if (!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm()))
			init();
		labelVisible = geo.isLabelVisible();

		// add or remove point drawables
		updatePointLists();

		// adjust point coordinates for a density plot
		if (drawType == DrawType.DOT_PLOT
				&& ((AlgoDotPlot) algo).useDensityPlot()) {
			doDotDensity();
		}

		pointStyle = ((GeoList) geo).getPointStyle();
		pointSize = ((GeoList) geo).getPointSize();
		pointColor = geo.getObjectColor();

		boolean doVisualStyleUpdate = (oldPointSize != pointSize)
				|| (oldPointStyle != pointStyle)
				|| !(oldColor.equals(pointColor));

		oldPointSize = pointSize;
		oldPointStyle = pointStyle;
		oldColor = geo.getObjectColor();

		for (int i = 0; i < ((GeoList) geo).size(); i++) {
			GeoPoint pt = (GeoPoint) ((GeoList) geo).get(i);

			if (doVisualStyleUpdate) {
				pt.setObjColor(pointColor);
				pt.setPointSize(pointSize);
				pt.setPointStyle(pointStyle);
			}

			drawPoints.get(i).update();
		}

		GeoPoint pt = (GeoPoint) ((GeoList) geo).get(0);
		coords[0] = pt.getX();
		coords[1] = pt.getY();
		view.toScreenCoords(coords);

		if (labelVisible) {
			xLabel = (int) coords[0];
			yLabel = (int) coords[1] + 2 * view.getFontSize();
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}

	}

	private void updatePointLists() {

		// find the number of points to draw
		int n = ((GeoList) geo).size();
		drawPoints.ensureCapacity(n);

		// adjust the lists
		if (n > drawPoints.size()) {
			// add
			for (int i = drawPoints.size(); i < n; i++) {
				GeoPoint pt = (GeoPoint) ((GeoList) geo).get(i);
				DrawPoint d = new DrawPoint(view, pt);
				d.setGeoElement(pt);
				drawPoints.add(d);
			}
		} else if (n < drawPoints.size()) {
			// remove
			for (int i = n; n < drawPoints.size(); i++) {
				drawPoints.remove(i);
			}
		}
	}

	/**
	 * Adjusts the y-coordinates of all points so that they appear as stacks of
	 * dots with no vertical gaps between them.
	 */
	private void stackDots() {

		pointSize = ((GeoList) geo).getPointSize();

		int xIndex = 0;
		GeoPoint pt = null;
		GeoList list2 = ((AlgoDotPlot) algo).getFrequencyList();

		// iterate through the frequency list
		for (int i = 0; i < list2.size(); i++) {

			// for each frequency adjust the y coords so that dots appear
			// stacked on top of each other
			int dotCountMax = (int) ((GeoNumeric) list2.get(i)).getDouble();
			for (int dotCount = 1; dotCount <= dotCountMax; dotCount++) {
				pt = (GeoPoint) ((GeoList) geo).get(xIndex);
				setDotHeight(pt, dotCount);
				xIndex++;
			}

		}
	}

	/**
	 * Sets the real world height of a point so that it fits in a stack of dots
	 * 
	 * @param pt
	 * @param dotCount
	 *            number of dots the point is stacked above the x-axis
	 */
	private void setDotHeight(GeoPoint pt, int dotCount) {
		double y;
		pointSize = ((GeoList) geo).getPointSize();

		// get y coord for the stacked dot
		y = (view.getYZero() - pointSize); // first dot on axis
		y = y - 2 * (dotCount - 1) * pointSize; // higher dot
		y = view.toRealWorldCoordY(y);

		// set the y coord of the GeoPoint
		pt.setY(y);
		pt.updateCoords();
	}

	/**
	 * Stacks dots when x values are less than a dot diameter away. Follows
	 * Wilkinson's algorithm.
	 */
	private void doDotDensity() {

		pointSize = ((GeoList) geo).getPointSize();
		double h = 2 * pointSize * view.getInvXscale();

		GeoPoint pt = null;
		GeoList xList = ((AlgoDotPlot) algo).getUniqueXList();
		GeoList freqList = ((AlgoDotPlot) algo).getFrequencyList();

		int xIndex = 0;
		int dotCount = 1;
		double stackX = ((GeoNumeric) xList.get(xIndex)).getDouble();

		for (int i = 0; i < xList.size(); i++) {

			double x = ((GeoNumeric) xList.get(i)).getDouble();
			int freq = (int) ((GeoNumeric) freqList.get(i)).getDouble();

			if (x > stackX + h) {
				stackX = x;
				dotCount = 1;
			}

			for (int k = 0; k < freq; k++) {
				pt = (GeoPoint) ((GeoList) geo).get(xIndex);
				pt.setX(stackX);
				pt.updateCoords();
				setDotHeight(pt, dotCount);
				dotCount++;
				xIndex++;
			}

		}

	}

	/**
	 * @param index
	 *            index of point in the algorithm output list
	 */
	public void setToolTipForPoint(int index) {
		double x = getDotPlotX(index);
		String text = view.getKernel()
				.format(x, StringTemplate.defaultTemplate);
		((AlgoDotPlot) geo.getParentAlgorithm()).setToolTipPointText(text);

		// force automatic tool tip update
		view.setToolTipText(" ");
	}

	private double getDotPlotX(int index) {
		double x = 0;
		int xIndex = 0;
		GeoList list1 = ((AlgoDotPlot) algo).getUniqueXList();
		GeoList list2 = ((AlgoDotPlot) algo).getFrequencyList();

		for (int i = 0; i < list1.size(); i++) {

			x = ((GeoNumeric) list1.get(i)).getDouble();
			int freq = (int) ((GeoNumeric) list2.get(i)).getDouble();

			for (int k = 0; k < freq; k++) {
				if (index == xIndex) {
					return x;
				}
				xIndex++;
			}
		}
		return x;
	}

}