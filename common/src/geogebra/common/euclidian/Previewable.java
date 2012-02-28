package geogebra.common.euclidian;

/**
 * @author Markus Hohenwarter
 */
public interface Previewable {
	/** updates preview*/
	public void updatePreview();
	/**
	 * Updates preview for new mouse coords
	 * @param x mouse x
	 * @param y mouse y
	 */
	public void updateMousePos(double x, double y);
	/**
	 * Draws preview on given graphics
	 * @param g2 graphics
	 */
	public void drawPreview(geogebra.common.awt.Graphics2D g2);
	/**
	 * Called when preview is not needed anymore
	 */
	public void disposePreview();
	
}
