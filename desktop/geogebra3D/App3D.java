/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
package geogebra3D;

import geogebra.CommandLineArguments;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.euclidian3D.Input3D;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import geogebra.common.geogebra3D.main.App3DCompanion;
import geogebra.common.geogebra3D.util.CopyPaste3D;
import geogebra.common.gui.layout.DockManager;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;
import geogebra.common.main.AppCompanion;
import geogebra.common.util.CopyPaste;
import geogebra.euclidian.event.MouseEventD;
import geogebra.euclidianND.EuclidianViewInterfaceDesktop;
import geogebra.gui.GuiManagerD;
import geogebra.gui.app.GeoGebraFrame3D;
import geogebra.gui.layout.DockManagerD;
import geogebra.main.AppD;
import geogebra.main.AppletImplementation;
import geogebra.main.LocalizationD;
import geogebra.util.FrameCollector;
import geogebra3D.euclidian3D.EuclidianController3DD;
import geogebra3D.euclidian3D.EuclidianView3DD;
import geogebra3D.euclidian3D.opengl.GLFactoryD;
import geogebra3D.euclidian3D.opengl.RendererD;
import geogebra3D.euclidianFor3D.EuclidianControllerFor3DD;
import geogebra3D.euclidianFor3D.EuclidianViewFor3DD;
import geogebra3D.euclidianInput3D.EuclidianControllerHand3D;
import geogebra3D.euclidianInput3D.EuclidianControllerInput3D;
import geogebra3D.euclidianInput3D.EuclidianViewInput3D;
import geogebra3D.gui.GuiManager3D;
import geogebra3D.gui.layout.panels.EuclidianDockPanel3DD;
import geogebra3D.input3D.Input3DFactory;
import geogebra3D.util.ImageManager3D;

import java.awt.Component;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class App3D extends AppD {

	private EuclidianView3D euclidianView3D;
	private EuclidianController3D euclidianController3D;

	public App3D(CommandLineArguments args, JFrame frame, boolean undoActive) {
		this(args, frame, null, undoActive);
	}

	public App3D(CommandLineArguments args, AppletImplementation applet,
			boolean undoActive) {
		this(args, null, applet, undoActive);
	}

	private App3D(CommandLineArguments args, JFrame frame,
			AppletImplementation applet, boolean undoActive) {

		super(args, frame, applet, null, undoActive, new LocalizationD(3));

		// euclidianView3D.initAxisAndPlane();

		// TODO remove 3D test : just comment following line
		// new Test3D(kernel3D,euclidianView,euclidianView3D,this);

	}

	public App3D(CommandLineArguments args, Container comp, boolean undoActive) {
		super(args, null, null, comp, undoActive, new LocalizationD(3));
	}

	@Override
	protected void initImageManager(Component component) {
		imageManager = new ImageManager3D(component);
	}

	/**
	 * init the EuclidianView (and EuclidianView3D for 3D)
	 */
	@Override
	public void initEuclidianViews() {

		// init the 2D euclidian view
		super.initEuclidianViews();

		// init the 3D euclidian view (with perhaps a specific 3D input)
		Input3D input3D = Input3DFactory.createInput3D();
		if (input3D != null) {
			switch (input3D.getDeviceType()) {
			case HAND:
				euclidianController3D = new EuclidianControllerHand3D(kernel,
						input3D);
				break;
			case PEN:
			default:
				euclidianController3D = new EuclidianControllerInput3D(kernel,
						input3D);
				break;
			}
		} else {
			euclidianController3D = new EuclidianController3DD(kernel);
		}

	}

	@Override
	public boolean isRightClick(AbstractEvent e) {
		if (e instanceof MouseEventD) {
			return isRightClick(geogebra.euclidian.event.MouseEventD
					.getEvent(e));
		}

		return e.isRightClick();
	}

	@Override
	public EuclidianController newEuclidianController(Kernel kernel) {
		return new EuclidianControllerFor3DD(kernel);
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxes1,
			boolean showGrid1) {
		return new EuclidianViewFor3DD(euclidianController, showAxes1,
				showGrid1, 1, getSettings().getEuclidian(1));
	}

	@Override
	public void setMode(int mode) {
		super.setMode(mode);

		if (isEuclidianView3Dinited()) {
			euclidianView3D.setMode(mode);
		}
	}

	@Override
	public String getCompleteUserInterfaceXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();

		// save super settings
		sb.append(super.getCompleteUserInterfaceXML(asPreference));

		// save euclidianView3D settings
		if (isEuclidianView3Dinited()) {
			euclidianView3D.getXML(sb, asPreference);
		}

		// save euclidian views for plane settings
		((App3DCompanion) companion).addCompleteUserInterfaceXMLForPlane(sb,
				asPreference);

		return sb.toString();
	}

	/**
	 * return the 3D euclidian view
	 * 
	 * @return the 3D euclidian view
	 */
	@Override
	public EuclidianView3D getEuclidianView3D() {
		if (this.euclidianView3D == null) {
			if (euclidianController3D.hasInput3D()) {
				euclidianView3D = new EuclidianViewInput3D(
						euclidianController3D, getSettings().getEuclidian(3));
			} else {
				euclidianView3D = new EuclidianView3DD(euclidianController3D,
						getSettings().getEuclidian(3));
			}
		}
		return euclidianView3D;
	}

	@Override
	public boolean hasEuclidianView3D() {
		return this.euclidianController3D != null;
	}

	@Override
	public boolean isEuclidianView3Dinited() {
		return this.euclidianView3D != null;
	}

	@Override
	public boolean saveGeoGebraFile(File file) {
		// TODO generate it before
		if (euclidianView3D != null) {
			((RendererD) getEuclidianView3D().getRenderer()).needExportImage();
		}

		return super.saveGeoGebraFile(file);
	}

	// ///////////////////////////////
	// GUI
	// ///////////////////////////////

	@Override
	public void refreshViews() {
		if (isEuclidianView3Dinited()) {
			getEuclidianView3D().reset();
			DockManagerD dockManager = (DockManagerD) getGuiManager()
					.getLayout()
					.getDockManager();
			((EuclidianDockPanel3DD) dockManager.getPanel(VIEW_EUCLIDIAN3D))
					.refresh(dockManager);

		}
		super.refreshViews();
	}

	@Override
	public void resume3DRenderer() {
		if (isEuclidianView3Dinited()) {
			DockManager dockManager = (DockManager) getGuiManager().getLayout()
					.getDockManager();
			((EuclidianDockPanel3DD) dockManager.getPanel(VIEW_EUCLIDIAN3D))
					.resumeRenderer();

		}
	}

	public void toggleAxis3D() {
		// toggle axis
		getEuclidianView3D().toggleAxis();
	}

	public void togglePlane() {
		// toggle xOy plane
		getEuclidianView3D().getSettings().togglePlane();
	}

	public void toggleGrid3D() {
		// toggle xOy grid
		getEuclidianView3D().toggleGrid();
	}

	public void setShowAxesSelected3D(JCheckBoxMenuItem cb) {
		cb.setSelected(getEuclidianView3D().axesAreAllVisible());
	}

	/**
	 * set the show plane combo box selected if the plane is visible
	 * 
	 * @param cb
	 */
	public void setShowPlaneSelected(JCheckBoxMenuItem cb) {
		GeoPlane3D p = (GeoPlane3D) getKernel().getXOYPlane();
		cb.setSelected(p.isPlateVisible());
	}

	/**
	 * set the show grid combo box selected if the plane is visible
	 * 
	 * @param cb
	 */
	public void setShowGridSelected3D(JCheckBoxMenuItem cb) {
		GeoPlane3D p = (GeoPlane3D) getKernel().getXOYPlane();
		cb.setSelected(p.isGridVisible());
	}

	@Override
	protected GuiManagerD newGuiManager() {
		return new GuiManager3D(this);
	}

	// /////////////////////////////////////
	// COMMANDS
	// /////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see geogebra.main.Application#getCommandSyntax(java.lang.String) check
	 * if there's a Command.Syntax3D key. If not, return Command.Syntax key
	 */



	@Override
	public void updateStyleBars() {
		super.updateStyleBars();
		if (showView(App.VIEW_EUCLIDIAN3D)) {
			getEuclidianView3D().getStyleBar().updateStyleBar();
		}
	}

	// ///////////////////////////////
	// FOR TESTING : TODO remove all

	@Override
	public boolean is3D() {
		return true;
	}

	private int oldCursorMode;

	@Override
	protected void handleShiftEvent(boolean isShiftDown) {
		if (!this.isEuclidianView3Dinited()) {
			return;
		}
		if (isShiftDown) {
			oldCursorMode = getEuclidianView3D().getCursor();
			getEuclidianView3D().setMoveCursor();
		} else {
			getEuclidianView3D().setCursor(oldCursorMode);

		}
	}

	@Override
	public String getVersionString() {
		return super.getVersionString() + "-3D";
	}

	@Override
	public void exportAnimatedGIF(EuclidianView ev, FrameCollector gifEncoder,
			GeoNumeric num,
			int n, double val, double min, double max, double step) {

		if (!(ev instanceof EuclidianView3D)) {
			// regular 2D export
			super.exportAnimatedGIF(ev, gifEncoder, num, n, val, min, max, step);
			return;
		}

		((RendererD) getEuclidianView3D().getRenderer())
				.startAnimatedGIFExport(gifEncoder, num, n, val, min, max, step);
	}

	@Override
	public void copyGraphicsViewToClipboard() {

		if (!(getActiveEuclidianView() instanceof EuclidianView3D)) {
			// regular 2D export
			super.copyGraphicsViewToClipboard();
			return;
		}

		getEuclidianView3D().getRenderer().exportToClipboard();

	}

	@Override
	public void fileNew() {
		super.fileNew();

		((App3DCompanion) companion).removeAllEuclidianViewForPlane();
	}

	@Override
	public boolean loadFile(File file, boolean isMacroFile) {

		if (!checkFileExistsAndShowFileNotFound(file)) {
			return false;
		}

		((App3DCompanion) companion).removeAllEuclidianViewForPlane();

		return loadExistingFile(file, isMacroFile);
	}

	@Override
	public void createNewWindow() {
		GeoGebraFrame3D.createNewWindow3D(args.getGlobalArguments());
	}

	@Override
	public BufferedImage getExportImage(double maxX, double maxY)
			throws OutOfMemoryError {

		double scale = Math.min(maxX / getEuclidianView1().getSelectedWidth(),
				maxY / getEuclidianView1().getSelectedHeight());

		if (this.euclidianView3D == null) {
			return ((EuclidianViewInterfaceDesktop) getActiveEuclidianView())
					.getExportImage(scale);
		}

		EuclidianView3D ev3D = getEuclidianView3D();

		if (ev3D.isShowing()) {
			return ((RendererD) getEuclidianView3D().getRenderer())
					.getExportImage();
		}

		return ((EuclidianViewInterfaceDesktop) getActiveEuclidianView())
				.getExportImage(scale);
	}

	/**
	 * only for 3D really. Overridden in App3D
	 */
	@Override
	public void uploadToGeoGebraTubeOnCallback() {

		if (!isEuclidianView3Dinited()) {
			uploadToGeoGebraTube();
			return;
		}

		EuclidianView3D ev3D = getEuclidianView3D();

		if (ev3D.isShowing()) {
			ev3D.getRenderer().uploadToGeoGebraTube();
		} else {
			uploadToGeoGebraTube();
		}

	}

	@Override
	protected void initFactories() {
		super.initFactories();

		geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory.prototype = new GLFactoryD();
		geogebra.common.util.CopyPaste.INSTANCE = new CopyPaste3D();

	}

	@Override
	protected AppCompanion newAppCompanion() {
		return new App3DCompanionD(this);
	}

	@Override
	protected void handleOptionArgsEarly(CommandLineArguments args) {
		super.handleOptionArgsEarly(args);

		if (args != null && args.containsArg("testShaders")) {
			useShaders = true;
		} else {
			useShaders = false;
		}

	}

	private boolean useShaders;

	/**
	 * 
	 * @return true if we want to use shaders
	 */
	public boolean useShaders() {
		return useShaders;
	}

	@Override
	public void insertFile(File file) {

		// using code from newWindowAction, combined with
		// Michael's suggestion
		App3D ad = new App3D(new CommandLineArguments(null), new JPanel(), true);// true
																					// as
																					// undo
																					// info
																					// is
																					// necessary
																					// for
																					// copy-paste!

		// now, we have to load the file into AppD
		ad.getGuiManager().loadFile(file, false);

		// now we have to copy the macros from ad to app
		// in order to make some advanced constructions work
		// as it was hard to copy macro classes, let's use
		// strings, but how to load them into the application?
		try {
			getXMLio().processXMLString(ad.getMacroXML(), false, true);

			// alternative solution
			// app.addMacroXML(ad.getKernel().getMacroXML(
			// ad.getKernel().getAllMacros()));
		} catch (Exception ex) {
			App.debug("Could not load any macros at \"Insert File\"");
			ex.printStackTrace();
		}

		// afterwards, the file is loaded into "ad" in theory,
		// so we have to use the CopyPaste class to copy it

		CopyPaste.INSTANCE.copyToXML(ad, new ArrayList<GeoElement>(ad
				.getKernel().getConstruction()
				.getGeoSetWithCasCellsConstructionOrder()), true);

		// and paste
		CopyPaste.INSTANCE.pasteFromXML(this, true);

		// forgotten something important!
		// ad should be closed!
		ad.exit();
		// this is also needed to make it possible
		// to load the same file once again
		ad.getFrame().dispose();

	}

}
