package geogebra.web.gui;

import geogebra.common.GeoGebraConstants;
import geogebra.common.awt.GPoint;
import geogebra.common.cas.view.CASView;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.Layout;
import geogebra.common.gui.layout.DockPanel;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import geogebra.common.gui.view.properties.PropertiesView;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.javax.swing.GTextComponent;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.common.main.Localization;
import geogebra.common.main.MyError;
import geogebra.common.util.AsyncOperation;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.event.PointerEvent;
import geogebra.html5.gui.browser.BrowseGUI;
import geogebra.html5.main.AppWeb;
import geogebra.web.cas.view.CASTableW;
import geogebra.web.cas.view.CASViewW;
import geogebra.web.cas.view.RowHeaderPopupMenuW;
import geogebra.web.cas.view.RowHeaderWidget;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.euclidian.EuclidianViewWInterface;
import geogebra.web.gui.app.GGWMenuBar;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.dialog.ImageFileInputDialog;
import geogebra.web.gui.dialog.InputDialogOpenURL;
import geogebra.web.gui.inputbar.AlgebraInputW;
import geogebra.web.gui.inputbar.InputBarHelpPanelW;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.layout.LayoutW;
import geogebra.web.gui.layout.panels.AlgebraDockPanelW;
import geogebra.web.gui.layout.panels.CASDockPanelW;
import geogebra.web.gui.layout.panels.ConstructionProtocolDockPanelW;
import geogebra.web.gui.layout.panels.Euclidian2DockPanelW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelWAbstract;
import geogebra.web.gui.layout.panels.FunctionInspectorDockPanelW;
import geogebra.web.gui.layout.panels.ProbabilityCalculatorDockPanelW;
import geogebra.web.gui.layout.panels.PropertiesDockPanelW;
import geogebra.web.gui.layout.panels.SpreadsheetDockPanelW;
import geogebra.web.gui.menubar.MainMenu;
import geogebra.web.gui.properties.PropertiesViewW;
import geogebra.web.gui.toolbar.ToolBarW;
import geogebra.web.gui.util.GeoGebraFileChooserW;
import geogebra.web.gui.view.algebra.AlgebraContextMenuW;
import geogebra.web.gui.view.algebra.AlgebraControllerW;
import geogebra.web.gui.view.algebra.AlgebraViewW;
import geogebra.web.gui.view.consprotocol.ConstructionProtocolNavigationW;
import geogebra.web.gui.view.consprotocol.ConstructionProtocolViewW;
import geogebra.web.gui.view.probcalculator.ProbabilityCalculatorViewW;
import geogebra.web.gui.view.spreadsheet.MyTableW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetContextMenuW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;
import geogebra.web.html5.AttachedToDOM;
import geogebra.web.html5.Dom;
import geogebra.web.javax.swing.GOptionPaneW;
import geogebra.web.main.AppW;
import geogebra.web.main.AppWapplet;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class GuiManagerW extends GuiManager implements GuiManagerInterfaceW {

	/**
	 * container for the Popup that only one exist for a given type
	 */
	public AttachedToDOM currentPopup;

	private AlgebraControllerW algebraController;
	private AlgebraViewW algebraView;
	private SpreadsheetViewW spreadsheetView;
	private ArrayList<EuclidianViewW> euclidianView2;
	private BrowseGUI browseGUI;
	protected LayoutW layout;

	private CASViewW casView;

	private Euclidian2DockPanelW euclidianView2DockPanel;

	
	private String strCustomToolbarDefinition;
	private boolean draggingViews;
	
	
	public GuiManagerW(AppW app) {
		this.app = app;
		this.kernel = app.getKernel();
		// AGdialogManagerFactory = new DialogManager.Factory();
	}

	public void redo() {
		app.setWaitCursor();
		kernel.redo();
		updateActions();
		app.resetPen();
		app.setDefaultCursor();
	}

	public void undo() {
		app.setWaitCursor();
		kernel.undo();
		updateActions();
		app.resetPen();
		app.setDefaultCursor();
	}

	public void updateMenubarSelection() {
		if (((AppW) app).getObjectPool().getGgwMenubar() != null) {
			((AppW) app).getObjectPool().getGgwMenubar().getMenubar().updateSelection();
		}

	}

	@Override
	public void updateMenubar() {
		GGWMenuBar ggwMenuBar = ((AppW) app).getObjectPool().getGgwMenubar();
		if (ggwMenuBar != null) {
			MainMenu menuBar = ((AppW) app).getObjectPool().getGgwMenubar().getMenubar();
			if (menuBar != null) {
				menuBar.updateMenubar();
			}
		}
		
	}

	@Override
	public void updateActions() {
		GGWMenuBar ggwMenuBar = ((AppW) app).getObjectPool().getGgwMenubar();
		if (ggwMenuBar != null) {
			ggwMenuBar.getMenubar().updateSelection();
		}
		if(getToolbarPanel() != null){
			getToolbarPanel().updateUndoActions();
		}
	}

	public DialogManager getDialogManager() {
		return app.getDialogManager();
	}

	public void showPopupMenu(ArrayList<GeoElement> selectedGeos,
	        EuclidianViewInterfaceCommon view, GPoint mouseLoc) {
		showPopupMenu(selectedGeos, ((EuclidianViewW) view).g2p.getCanvas(),
		        mouseLoc);

	}

	private void showPopupMenu(ArrayList<GeoElement> geos, Canvas invoker,
	        GPoint p) {
		if (geos == null || !app.letShowPopupMenu())
			return;
		if (app.getKernel().isAxis(geos.get(0))) {
			showDrawingPadPopup(invoker, p);
		} else {
			// clear highlighting and selections in views
			app.getActiveEuclidianView().resetMode();
			getPopupMenu(geos, p).show(invoker, p.x, p.y);
		}
	}
	
	public void showPopupMenu(ArrayList<GeoElement> geos, AlgebraView invoker,
	        GPoint p) {
		// clear highlighting and selections in views
		app.getActiveEuclidianView().resetMode();
		getPopupMenu(geos, p).show(p);
	}

	public SpreadsheetContextMenuW getSpreadsheetContextMenu(MyTableW mt, boolean shift) {
		removePopup();
		currentPopup = new SpreadsheetContextMenuW(mt, shift);
		return (SpreadsheetContextMenuW)currentPopup;
	}

	public AlgebraContextMenuW getAlgebraContextMenu(){
		removePopup();
		currentPopup = new AlgebraContextMenuW((AppW) app);
		return (AlgebraContextMenuW)currentPopup;
	}
	
	public RowHeaderPopupMenuW getCASContextMenu(RowHeaderWidget rowHeader, CASTableW table){
		removePopup();
		currentPopup = new RowHeaderPopupMenuW(rowHeader, table, (AppW) app);
		return (RowHeaderPopupMenuW)currentPopup;
	}
	
	public ContextMenuGeoElementW getPopupMenu(ArrayList<GeoElement> geos,
	        GPoint location) {
		removePopup();
		currentPopup = new ContextMenuGeoElementW((AppW) app, geos, location);
		((ContextMenuGeoElementW)currentPopup).addOtherItems();
		return (ContextMenuGeoElementW) currentPopup;
	}

	public void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
	        ArrayList<GeoElement> geos, EuclidianViewInterfaceCommon view,
	        geogebra.common.awt.GPoint p) {
		showPopupChooseGeo(selectedGeos, geos, (EuclidianView) view, p);
	}

	private void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
	        ArrayList<GeoElement> geos, EuclidianView view, GPoint p) {
		
		if (geos == null || !app.letShowPopupMenu())
			return;

		if (app.getKernel().isAxis(geos.get(0))) {
			showDrawingPadPopup(view, p);
		} else {

			Canvas invoker = ((EuclidianViewWInterface) view).getCanvas();
			// clear highlighting and selections in views
			GPoint screenPos = (invoker == null) ? new GPoint(0, 0)
			        : new GPoint(invoker.getAbsoluteLeft() + p.x,
			                invoker.getAbsoluteTop() + p.y);

			app.getActiveEuclidianView().resetMode();
			getPopupMenu(app, view, selectedGeos, geos, screenPos, p).show(
			        invoker, p.x, p.y);
		}

	}

	private ContextMenuGeoElementW getPopupMenu(App app, EuclidianView view,
	        ArrayList<GeoElement> selectedGeos, ArrayList<GeoElement> geos,
	        GPoint screenPos, GPoint p) {
		currentPopup = new ContextMenuChooseGeoW((AppW) app, view,
		        selectedGeos, geos, screenPos, p);
		return (ContextMenuGeoElementW) currentPopup;
	}

	public void setFocusedPanel(AbstractEvent event,
	        boolean updatePropertiesView) {
		setFocusedPanel( ((PointerEvent)event).getEvID(), updatePropertiesView);
	}

	public void setFocusedPanel(int evID, boolean updatePropertiesView) {

		if (!(((AppW) app).getEuclidianViewpanel() instanceof DockPanel)) {
			App.debug("This part of the code should not have run!");
			return;
		}

		if (evID == 1)
			setFocusedPanel((DockPanel)((AppW) app).getEuclidianViewpanel(), updatePropertiesView);
		else if (evID == 2)
			setFocusedPanel(getEuclidianView2DockPanel(1), updatePropertiesView);
	}

	public void setFocusedPanel(DockPanel panel, boolean updatePropertiesView) {
		if (panel != null) {
			getLayout().getDockManager()
					.setFocusedPanel(panel,updatePropertiesView);

			// notify the properties view
			if  (updatePropertiesView)
				updatePropertiesView();
		}
	}

	public void loadImage(GeoPoint loc, Object object, boolean altDown) {
		// TODO Auto-generated method stub
		//App.debug("unimplemented method");

		app.setWaitCursor();

		ImageFileInputDialog dialog = new ImageFileInputDialog((AppW) app, loc);
		dialog.setVisible(true);
	}

	public void updateFonts() {
		
		((AppW) app).getFrameElement().getStyle().setFontSize(app.getFontSize(), Unit.PX);
		
		// if (((AppW) app).getObjectPool().getGgwMenubar() != null){
		//	GeoGebraMenubarW menubar = ((AppW) app).getObjectPool().getGgwMenubar().getMenubar();
		//	if (menubar != null) menubar.updateFonts();
		// }

		updateFontSizeStyleElement();
		
		if(hasPropertiesView()){
			((PropertiesViewW)getPropertiesView()).updateFonts();
		}
		
		if(hasSpreadsheetView()){
			getSpreadsheetView().updateFonts();
		}
			
	}
	
	
	private void updateFontSizeStyleElement(){
		
		String fontsizeString = app.getGUIFontSize() + "px";
		int imagesize = Math.round(app.getGUIFontSize() * 4 / 3);
		int toolbariconSize = 2 * app.getGUIFontSize();

		// until we have no enough place for the big icons in the toolbar, don't
		// enable to increase too much the size of icons.
		if (toolbariconSize > 45)
			toolbariconSize = 45;

		
		// Build inner text for a style element that handles font size 
		// =============================================================
		String innerText = ".GeoGebraMenuBar, .GeoGebraPopupMenu, .DialogBox, .gwt-PopupPanel, .ToolTip, .gwt-SuggestBoxPopup";
		innerText += "{font-size: " + fontsizeString + " !important}";

		innerText += ".GeoGebraMenuImage{height: " + imagesize + "px; width: "
		        + imagesize + "px;}";

		innerText += ".GeoGebraMenuBar input[type=\"checkbox\"], .GeogebraMenuBar input[type=\"radio\"], "
		        + ".GeoGebraPopupMenu input[type=\"checkbox\"], .GeogebraPopupMenu input[type=\"radio\"] ";
		innerText += "{height: " + fontsizeString + "; width: "
		        + fontsizeString + ";}";

		innerText += ".toolbar_menuitem{font-size: " + fontsizeString + ";}";
		innerText += ".toolbar_menuitem img{width: " + toolbariconSize + "px;}";
		
		// ============================================================

		// Create a new style element for font size changes, and remove the old
		// ones, if they already exist. Then add the new element for all
		// GeoGebraWeb applets or application.

		NodeList<Element> fontsizeElements = Dom
		        .getElementsByClassName("GGWFontsize");
		for (int i = 0; i < fontsizeElements.getLength(); i++) {
			fontsizeElements.getItem(i).removeFromParent();
		}

		Element fontsizeElement = DOM.createElement("style");
		fontsizeElement.addClassName("GGWFontsize");
		fontsizeElement.setInnerText(innerText);

		NodeList<Element> geogebrawebElements = Dom
		        .getElementsByClassName("geogebraweb");
		for (int i = 0; i < geogebrawebElements.getLength(); i++) {
			geogebrawebElements.getItem(i).appendChild(fontsizeElement);
		}
	}

	public boolean isInputFieldSelectionListener() {
		// TODO Auto-generated method stub
		//App.debug("unimplemented method");
		return false;
	}

	public GTextComponent getAlgebraInputTextField() {
		//App.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	public void showDrawingPadPopup(EuclidianViewInterfaceCommon view,
	        GPoint mouseLoc) {
		showDrawingPadPopup(((EuclidianViewW) view).g2p.getCanvas(), mouseLoc);
	}
	
	public void showDrawingPadPopup3D(EuclidianViewInterfaceCommon view,
			geogebra.common.awt.GPoint mouseLoc) {
		// 3D stuff
	}

	private void showDrawingPadPopup(Canvas invoker, GPoint p) {
		// clear highlighting and selections in views
		app.getActiveEuclidianView().resetMode();
		getDrawingPadpopupMenu(p.x, p.y).show(invoker, p.x, p.y);
	}

	private ContextMenuGeoElementW getDrawingPadpopupMenu(int x, int y) {
		currentPopup = new ContextMenuGraphicsWindowW((AppW) app, x, y);
		return (ContextMenuGeoElementW) currentPopup;
	}

	public boolean hasSpreadsheetView() {
		if (spreadsheetView == null)
			return false;
		if (!spreadsheetView.isShowing())
			return false;
		return true;
	}

	public void attachSpreadsheetView() {
		getSpreadsheetView();
		spreadsheetView.attachView();
	}

	public void setShowView(boolean flag, int viewId) {
		setShowView(flag, viewId, true);
	}

	public void setShowView(boolean flag, int viewId, boolean isPermanent) {
		if (flag) {
			if (!showView(viewId))
				layout.getDockManager().show(viewId);

			if (viewId == App.VIEW_SPREADSHEET) {
				getSpreadsheetView().requestFocus();
			}
		} else {
			if (showView(viewId))
				layout.getDockManager().hide(viewId, isPermanent);

			if (viewId == App.VIEW_SPREADSHEET) {
				(app).getActiveEuclidianView().requestFocus();
			}
		}
		((AppW)app).closePopups();
		
		//toolbarPanel.validate();
		//toolbarPanel.updateHelpText();
	}
	
	public boolean showView(int viewId) {
		/*
		Element e = Document.get().getElementById("View_" + viewId);
		if (e != null) {
			return !(e.getStyle().getDisplay().equals("none") || e.getStyle()
			        .getVisibility().equals("hidden"));
		}
		return false;
		*/
		
		try {
			return layout.getDockManager().getPanel(viewId).isVisible();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	public View getConstructionProtocolData() {
		//App.debug("unimplemented method");
		// TODO Auto-generated method stub
		return null;
	}

	public View getCasView() {
		if (casView == null)
			casView = new CASViewW((AppW) app);
		return casView;
	}

	public boolean hasCasView() {
		return casView != null;
	}

	public SpreadsheetViewW getSpreadsheetView() {
		// init spreadsheet view
		if (spreadsheetView == null) {
			spreadsheetView = new SpreadsheetViewW((AppW) app);
		}

		return spreadsheetView;
	}
	
	public View getProbabilityCalculator() {
		if (probCalculator == null) {
			probCalculator = new ProbabilityCalculatorViewW((AppW) app);
		}

		return probCalculator;
	}
	
	/**
	 * @return wheter it has probablity calculator or not
	 */
	public boolean hasProbablitiyCalculator() {
		return probCalculator != null;
	}
	
	public boolean hasPlotPanelEuclidianView() {
		return hasProbablitiyCalculator();
	}

	public void updateSpreadsheetColumnWidths() {
		// TODO Auto-generated method stub
		App.debug("unimplemented");
		// if (spreadsheetView != null) {
		// spreadsheetView.updateColumnWidths();
		// }
	}

	public void resize(int width, int height) {
		
		int widthChanged = 0;
		int heightChanged = 0;
		Element geogebraFrame = ((AppW) app).getFrameElement();
		
		widthChanged = width - geogebraFrame.getOffsetWidth();
		heightChanged = height - geogebraFrame.getOffsetHeight();		

		if (getLayout() != null && getLayout().getRootComponent() != null) {
			Widget root = getLayout().getRootComponent();
			root.setPixelSize(root.getOffsetWidth() + widthChanged, root.getOffsetHeight() + heightChanged);		
		}
		if(this.algebraInput != null){
			this.algebraInput.setWidth((width-100)+"px");
		}
		//EuclidianPanelWAbstract epanel = ((AppW) app).getEuclidianViewpanel();
		//epanel.setPixelSize(epanel.getOffsetWidth() + widthChanged, epanel.getOffsetHeight() + heightChanged );

		// maybe onResize is good here too, but call deferredOnResize for security
		((AppW) app).getEuclidianViewpanel().deferredOnResize();
		((AppW) app).recalculateEnvironments();
	}

	public ToolBarW getGeneralToolbar() {
		return toolbarPanel.getToolBar();
	}

	public String getToolbarDefinition() {
		if (strCustomToolbarDefinition == null && getToolbarPanel() != null)
			return getGeneralToolbar().getDefaultToolbarString();
			//return geogebra.web.gui.toolbar.ToolBarW.getAllTools(app);
		return strCustomToolbarDefinition;
	}

	public String getToolbarDefinition(Integer viewId) {
		if (viewId == App.VIEW_CAS)
			return CASView.TOOLBAR_DEFINITION;
		return getToolbarDefinition();
	}

	public void removeFromToolbarDefinition(int mode) {
		if (strCustomToolbarDefinition != null) {
			// Application.debug("before: " + strCustomToolbarDefinition +
			// ",  delete " + mode);

			strCustomToolbarDefinition = strCustomToolbarDefinition.replaceAll(
					Integer.toString(mode), "");

			if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
				// if a macro mode is removed all higher macros get a new id
				// (i.e. id-1)
				int lastID = kernel.getMacroNumber()
						+ EuclidianConstants.MACRO_MODE_ID_OFFSET - 1;
				for (int id = mode + 1; id <= lastID; id++) {
					strCustomToolbarDefinition = strCustomToolbarDefinition
							.replaceAll(Integer.toString(id),
									Integer.toString(id - 1));
				}
			}

			// Application.debug("after: " + strCustomToolbarDefinition);
		}
	}
	
	public void addToToolbarDefinition(int mode) {
		if (strCustomToolbarDefinition != null) {
			strCustomToolbarDefinition = strCustomToolbarDefinition + " | "
					+ mode;
		}
	}
	
	public final String getCustomToolbarDefinition() {
		return strCustomToolbarDefinition;
	}

	

	/**
	 * Initializes GuiManager for web
	 */
	public void initialize() {
		initAlgebraController(); // ? needed for keyboard input in EuclidianView
								 // in Desktop
		layout.initialize(app);
		initLayoutPanels();		
	}

	/**
	 * Register panels for the layout manager.
	 */
	protected boolean initLayoutPanels() {

		// register euclidian view
		// this is done earlier
		if (((AppW) app).getEuclidianViewpanel() instanceof DockPanelW) {
			layout.registerPanel((DockPanelW)((AppW) app).getEuclidianViewpanel());
		} else {
			App.debug("This part of the code should not have been called!");
			return false;
		}

		// register spreadsheet view
		layout.registerPanel(new SpreadsheetDockPanelW(app));

		// register algebra view
		layout.registerPanel(new AlgebraDockPanelW());

		// register CAS view
		if (GeoGebraConstants.CAS_VIEW_ENABLED)
			layout.registerPanel(new CASDockPanelW(app));

		// register EuclidianView2
		layout.registerPanel(getEuclidianView2DockPanel(1));

		// register ConstructionProtocol view
		layout.registerPanel(new ConstructionProtocolDockPanelW((AppW) app));

		// register ProbabilityCalculator view
		layout.registerPanel(new ProbabilityCalculatorDockPanelW(app));

		// register FunctionInspector view
		layout.registerPanel(new FunctionInspectorDockPanelW(app));

		// register Properties view
		
		layout.registerPanel(new PropertiesDockPanelW((AppW) app));

		// register data analysis view
	//	layout.registerPanel(new DataAnalysisViewDockPanel(app));
		
		if (!app.isApplet()) {
			// register python view
		//	layout.registerPanel(new PythonDockPanel(app));
		}
		
		/*
		if (!app.isWebstart() || app.is3D()) {
			// register Assignment view
			layout.registerPanel(new AssignmentDockPanel(app));
		}*/
		
		return true;

	}

	public void setLayout(Layout layout) {
		this.layout = (LayoutW) layout;
	}

	public LayoutW getLayout() {
		return layout;
	}

	private GGWToolBar toolbarPanel = null;

	private InputBarHelpPanelW inputHelpPanel;

	private AlgebraInputW algebraInput;

	public GGWToolBar getToolbarPanel() {
		if (toolbarPanel == null) {
			toolbarPanel = (GGWToolBar)((AppW) app).getToolbar();
			if (toolbarPanel != null && !toolbarPanel.isInited()) {
				toolbarPanel.init((AppW)app);
			}
		}

		return toolbarPanel;
	}

	public void updateToolbar() {
//		if (toolbarPanel != null) {
//			toolbarPanel.buildGui();
//		}

		if (layout != null) {
			// AG layout.getDockManager().updateToolbars();
			if (getToolbarPanel() != null) {
				getToolbarPanel().updateToolbarPanel();
			}
		}
	}

	public void updateAlgebraInput() {
		App.debug("Implementation needed...");
	}

	public InputBarHelpPanelW getInputHelpPanel() {
		if (inputHelpPanel == null)
			inputHelpPanel = new InputBarHelpPanelW((AppW) app);
		return inputHelpPanel;
	}

	public void setShowAuxiliaryObjects(boolean flag) {
		if (!hasAlgebraViewShowing())
			return;
		getAlgebraView();
		algebraView.setShowAuxiliaryObjects(flag);
		app.getSettings().getAlgebra().setShowAuxiliaryObjects(flag);
	}

	public AlgebraViewW getAlgebraView() {
		if (algebraView == null) {
			initAlgebraController();
			algebraView = newAlgebraView(algebraController);
			// if (!app.isApplet()) {
			// allow drag & drop of files on algebraView
			// algebraView.setDropTarget(new DropTarget(algebraView,
			// new FileDropTargetListener(app)));
			// }
		}

		return algebraView;
	}

	private void initAlgebraController() {
		if (algebraController == null) {
			algebraController = new AlgebraControllerW(app.getKernel());
		}
	}

	/**
	 * 
	 * @param algc
	 * @return new algebra view
	 */
	protected AlgebraViewW newAlgebraView(AlgebraControllerW algc) {
		// if (USE_COMPRESSED_VIEW) {
		// return new CompressedAlgebraView(algc, CV_UPDATES_PER_SECOND);
		// }
		return new AlgebraViewW(algc);
	}

	public void attachAlgebraView() {
		getAlgebraView();
		algebraView.attachView();
	}

	public void detachAlgebraView() {
		if (algebraView != null)
			algebraView.detachView();
	}

	public void applyAlgebraViewSettings() {
		if (algebraView != null)
			algebraView.applySettings();
	}

	private PropertiesView propertiesView;

	public View getPropertiesView() {

		if (propertiesView == null) {
			// initPropertiesDialog();
			propertiesView = newPropertiesViewW((AppW) app);
		}

		return propertiesView;
	}
	
	/**
	 * 
	 * @param app
	 * @return new properties view
	 */
	protected PropertiesViewW newPropertiesViewW(AppW app){
		return new PropertiesViewW(app);
	}

	public void updatePropertiesView() {
		if (propertiesView != null) {
			propertiesView.updatePropertiesView();
		}
	}

	public void mousePressedForPropertiesView() {
		if (propertiesView != null) {
			propertiesView.mousePressedForPropertiesView();
		} else {
			// TODO: @Gabor: do we want this? I don't think we want to
			// initialize the view unnecessarily.
			// ((PropertiesViewW)
			// getPropertiesView()).mousePressedForPropertiesView();
		}
	}

	public void mouseReleasedForPropertiesView(boolean creatorMode) {
		// TODO Auto-generated method stub

	}

	public void addAlgebraInput(AlgebraInputW ai) {
		this.algebraInput = ai;
	}

	public AlgebraInputW getAlgebraInput() {
		return algebraInput;
	}

	public boolean save() {
		app.setWaitCursor();
		// String fileName = Window.prompt("File name", "Bunny");
		// do saving here if getBase64 will be good
		GeoGebraFileChooserW fileChooser = ((DialogManagerW) app
		        .getDialogManager()).getFileChooser();
		if (((AppW) app).getFileName() != null) {
			fileChooser.setFileName(((AppW) app).getFileName());
		} else {
			fileChooser.setFileName("");
		}

		if (((AppW) app).getFileDescription() != null) {
			fileChooser.setDescription(((AppW) app).getFileDescription());
		} else {
			fileChooser.setDescription("");
		}
		fileChooser.center();
		return true;
	}

	public void showPropertiesViewSliderTab() {
		App.debug("unimplemented");
	}

	public void openURL() {
		InputDialogOpenURL id = new InputDialogOpenURL((AppW) app);
		id.setVisible(true);
	}

	@Override
	protected boolean loadURL_GGB(String url) {
		((AppW) app).loadURL_GGB(url);
		return true;
	}

	@Override
	protected boolean loadURL_base64(String url) {
		App.debug("implementation needed");
		return true;
	}

	@Override
	protected boolean loadFromApplet(String url) throws Exception {
		App.debug("implementation needed");
		return false;
	}

	public void updateGUIafterLoadFile(boolean success, boolean isMacroFile) {
		if (success && !isMacroFile
			&& !app.getSettings().getLayout().isIgnoringDocumentLayout()) {

			getLayout().setPerspectives(app.getTmpPerspectives(), null);
			//SwingUtilities.updateComponentTreeUI(getLayout().getRootComponent());

			if (!app.isIniting()) {
				updateFrameSize(); // checks internally if frame is available
				if (app.needsSpreadsheetTableModel())
					(app).getSpreadsheetTableModel(); //ensure create one if not already done
			}
		} else if (isMacroFile && success) {
			//setToolBarDefinition(ToolbarW.getAllTools(app));
			//(app).updateToolBar();
			//(app).updateContentPane();
		}

		// force JavaScript ggbOnInit(); to be called
		if (!app.isApplet())
			app.getScriptManager().ggbOnInit();
	}

	public void startEditing(GeoElement geoElement) {
		App.debug("unimplemented");

	}

	public boolean noMenusOpen() {
		App.debug("unimplemented");
		return true;
	}

	public void openFile() {
		App.debug("unimplemented");
	}

	public void showGraphicExport() {
		App.debug("unimplemented");

	}

	public void showPSTricksExport() {
		App.debug("unimplemented");

	}

	public void showWebpageExport() {
		App.debug("unimplemented");

	}

	public void detachPropertiesView() {
		if (propertiesView != null)
			propertiesView.detachView();
	}

	public boolean hasPropertiesView() {
		return propertiesView != null;
	}

	public void attachPropertiesView() {
		getPropertiesView();
		propertiesView.attachView();
	}

	public void attachCasView() {
		getCasView();
		casView.attachView();
	}

	public void attachConstructionProtocolView() {
		App.debug("unimplemented");
	}

	public void attachProbabilityCalculatorView() {
		getProbabilityCalculator();
		probCalculator.attachView();
	}

	public void attachAssignmentView() {
		App.debug("unimplemented");
	}

	public EuclidianView getActiveEuclidianView() {

		if (layout == null)
			return app.getEuclidianView1();

		EuclidianDockPanelWAbstract focusedEuclidianPanel = layout
				.getDockManager().getFocusedEuclidianPanel();

		if (focusedEuclidianPanel != null) {
			return focusedEuclidianPanel.getEuclidianView();
		}
		return (app).getEuclidianView1();
		//return app.getEuclidianView1();
	}

	public Command getShowAxesAction() {
		return new Command() {

			public void execute() {
				showAxesCmd();
			}
		};
	}

	public Command getShowGridAction() {
		return new Command() {

			public void execute() {
				showGridCmd();
			}
		};
	}

	public View getDataAnalysisView() {
		App.debug("unimplemented");
		return null;
	}

	public void attachDataAnalysisView() {
		App.debug("unimplemented");
	}

	public void detachDataAnalysisView() {
		App.debug("unimplemented");
	}

	public boolean hasDataAnalysisView() {
		App.debug("unimplemented");
		return false;
	}

	public void detachAssignmentView() {
		App.debug("unimplemented");
	}

	public void detachProbabilityCalculatorView() {
		App.debug("unimplemented");
	}

	public void detachCasView() {
		App.debug("unimplemented");
	}

	public void detachConstructionProtocolView() {
		App.debug("unimplemented");
	}

	public void detachSpreadsheetView() {
		if (spreadsheetView != null)
			spreadsheetView.detachView();
	}

	@Override
	protected void openHelp(String page, Help type) {
		try {
			String helpURL = getHelpURL(type, page);
			Window.open(helpURL, "_blank","");
		} catch (MyError e) {
			app.showError(e);
		} catch (Exception e) {
			App.debug("openHelp error: " + e.toString() + " " + e.getMessage()
			        + " " + page + " " + type);
			app.showError(e.getMessage());
			e.printStackTrace();
		}
	}

	private String getHelpURL(Help type, String pageName) {
		// try to get help for given language
		// eg http://www.geogebra.org/help/en_GB/cmd/FitLogistic

		StringBuilder urlSB = new StringBuilder();
		// StringBuilder urlOffline = new StringBuilder();
		//
		// urlOffline.append(AppD.getCodeBaseFolder());
		// urlOffline.append("help/");
		// urlOffline.append(((AppD)app).getLocale().getLanguage()); // eg en
		// urlOffline.append('/');

		urlSB.append(GeoGebraConstants.GEOGEBRA_WEBSITE);
		urlSB.append("help/");
		urlSB.append(app.getLocalization().getLanguage()); // eg en_GB

		switch (type) {
		case COMMAND:
			pageName = ((AppWeb) app).getEnglishCommand(pageName);
			// String pageNameOffline = pageName.replace(":",
			// "%3A").replace(" ",
			// "_");
			urlSB.append("/cmd/");
			urlSB.append(pageName);

			// urlOffline.append(pageNameOffline);
			// urlOffline.append("_Command.html");
			break;
		case TOOL:
			// pageNameOffline = pageName.replace(":", "%3A").replace(" ", "_");
			urlSB.append("/tool/");
			urlSB.append(pageName);

			// urlOffline.append(pageNameOffline);
			// urlOffline.append("_Tool.html");
			break;
		case GENERIC:
			// pageNameOffline = pageName.replace(":", "%3A").replace(" ", "_");
			urlSB.append("/article/");
			urlSB.append(pageName);

			// urlOffline.append(pageNameOffline);
			// urlOffline.append(".html");
			break;
		default:
			App.printStacktrace("Bad getHelpURL call");
		}
		try {
			// Application.debug(urlOffline.toString());
			// Application.debug(urlSB.toString());

			// String offlineStr = urlOffline.toString();

			// File file = new File(AppD.WINDOWS ? offlineStr.replaceAll(
			// "[/\\\\]+", "\\" + "\\") : offlineStr); // replace slashes
			// // with
			// // backslashes
			//
			// if (file.exists())
			// return getEscapedUrl("file:///" + offlineStr);
			// else
			// return getEscapedUrl(urlSB.toString());

			return urlSB.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void resetSpreadsheet() {
		if (spreadsheetView != null)
			spreadsheetView.restart();
	}

	public void setScrollToShow(boolean b) {
		if (spreadsheetView != null)
			spreadsheetView.setScrollToShow(b);
	}

	public void showURLinBrowser(String strURL) {
		App.debug("unimplemented");
	}

	public void updateMenuWindow() {
	}

	public void updateMenuFile() {
	}

	public void clearInputbar() {
		App.debug("unimplemented");
	}

	public Object createFrame() {
		return null;
	}

	public int getInputHelpPanelMinimumWidth() {
		App.debug("unimplemented");
		return 0;
	}

	public void exitAll() {
		App.debug("unimplemented");
		// TODO Auto-generated method stub

	}

	public boolean saveCurrentFile() {
		// TODO Auto-generated method stub
		App.debug("unimplemented");
		return false;
	}

	public boolean hasEuclidianView2(int idx) {
		if (euclidianView2 == null)
			return false;
		if (!euclidianView2.get(idx).isShowing())
			return false;
		return true;
	}

	public void allowGUIToRefresh() {
		App.debug("unimplemented");
		// TODO Auto-generated method stub

	}

	public void updateFrameTitle() {
		// TODO Auto-generated method stub
		App.debug("unimplemented");

	}

	public void setLabels() {
		if (algebraInput != null)
			algebraInput.setLabels();

		if (toolbarPanel != null && toolbarPanel.getToolBar() != null) {
			toolbarPanel.getToolBar().buildGui();
		}
		GGWMenuBar bar = ((AppW) app).getObjectPool().getGgwMenubar();
		if (bar != null && bar.getMenubar() != null) {
			bar.removeMenus();
			bar.init((AppW) app);
		}

		getConstructionProtocolNavigation().setLabels();

		// set the labelling of the panels
		// titles on the top of their style bars
		if (getLayout() != null && getLayout().getDockManager() != null) {
			DockPanelW[] panels = getLayout().getDockManager().getPanels();
			for (int i = 0; i < panels.length; i++)
				panels[i].setLabels();
		}

		((DialogManagerW) app.getDialogManager()).setLabels();
	}

	public void setShowToolBarHelp(boolean showToolBarHelp) {
		App.debug("unimplemented");
		// TODO Auto-generated method stub

	}

	public View getEuclidianView2(int idx) {
		for(int i = euclidianView2.size(); i<=idx; i++){
			euclidianView2.add(null);
		}
		if (euclidianView2.get(idx) == null) {
			boolean[] showAxis = { true, true };
			boolean showGrid = false;
			App.debug("Creating 2nd Euclidian View");
			EuclidianViewW ev =  newEuclidianView(showAxis, showGrid, 2);
			euclidianView2.set(idx, ev);
			// euclidianView2.setEuclidianViewNo(2);
			ev.setAntialiasing(true);
			ev.updateFonts();
		}
		return euclidianView2.get(idx);
	}

	public Euclidian2DockPanelW getEuclidianView2DockPanel(int idx) {
		if (euclidianView2DockPanel == null) {
			euclidianView2DockPanel = new Euclidian2DockPanelW(app.isFullAppGui(), idx);
		}
		return euclidianView2DockPanel;
	}

	protected EuclidianViewW newEuclidianView(boolean[] showAxis,
			boolean showGrid, int id) {
		if (id == 2) {
			return ((AppW) app).newEuclidianView(getEuclidianView2DockPanel(1), app.newEuclidianController(kernel), showAxis,
				showGrid, id, app.getSettings().getEuclidian(id));
		}
		return ((AppW) app).newEuclidianView(((AppW) app).getEuclidianViewpanel(), app.newEuclidianController(kernel), showAxis,
			showGrid, id, app.getSettings().getEuclidian(id));
	}

	public boolean hasEuclidianView2EitherShowingOrNot(int idx) {
		if (euclidianView2.size() <= idx || euclidianView2.get(idx) == null)
			return false;
		return true;
	}

	public void updateFrameSize() {
		// TODO Auto-generated method stub
		App.debug("unimplemented");

	}

	@Override
	public void getSpreadsheetViewXML(StringBuilder sb, boolean asPreference) {
		if (spreadsheetView != null)
			spreadsheetView.getXML(sb, asPreference);
	}

	public boolean hasAlgebraViewShowing() {
		if (algebraView == null)
			return false;
		if (!algebraView.isShowing())
			return false;
		return true;
	}

	@Override
	public boolean hasAlgebraView() {
		if (algebraView == null)
			return false;
		return true;
	}

	public void getAlgebraViewXML(StringBuilder sb, boolean asPreference) {
		if (algebraView != null)
			algebraView.getXML(sb, asPreference);
	}



	private int toolbarID = App.VIEW_EUCLIDIAN;

	private ConstructionProtocolViewW constructionProtocolView;

	private boolean oldDraggingViews;

	private String generalToolbarDefinition;

	public int getActiveToolbarId() {
		return toolbarID;
	}

	public void setActiveToolbarId(int toolbarID) {

		// set the toolbar string directly from the panels
		// after closing some panels, this may need to be done
		// even if the following need not
		// only do this if toolbar string not null, otherwise this may
		String def = layout.getDockManager().getPanel(toolbarID).getToolbarString();
		if(def == null && this.generalToolbarDefinition != null){
			def = this.generalToolbarDefinition;
		}

		setToolBarDefinition(def);
		

		if (this.toolbarID != toolbarID && toolbarPanel != null) {
			toolbarPanel.setActiveToolbar(new Integer(toolbarID));
			updateToolbar();			
		}
		this.toolbarID = toolbarID;
	}

	@Override
    public AppW getApp() {
		return (AppW) app;
	}
	
	public void removePopup() {
		if (currentPopup != null) {
			currentPopup.removeFromDOM();
			currentPopup = null;
		}
	    
    }
	
	public void setGeneralToolBarDefinition(String toolBarDefinition) {
		if(toolBarDefinition == null){
			return;
		}
		generalToolbarDefinition = toolBarDefinition;
		strCustomToolbarDefinition = toolBarDefinition;
	}
	
	public void setToolBarDefinition(String toolBarDefinition) {
		strCustomToolbarDefinition = toolBarDefinition;
	}

    public ConstructionProtocolView getConstructionProtocolView() {
		if (constructionProtocolView == null) {
			constructionProtocolView = new ConstructionProtocolViewW((AppW) app);
		}

		return constructionProtocolView;
    }

    public void clearAbsolutePanels() {
		clearAbsolutePanel(App.VIEW_EUCLIDIAN);
		clearAbsolutePanel(App.VIEW_EUCLIDIAN2);   
    }
	
	private void clearAbsolutePanel(int viewid){
		AbsolutePanel ep;
		if (viewid==App.VIEW_EUCLIDIAN){
			ep = ((EuclidianDockPanelW) getLayout().getDockManager().getPanel(viewid)).getAbsolutePanel();
		} else if (viewid==App.VIEW_EUCLIDIAN2){
			ep = ((Euclidian2DockPanelW) getLayout().getDockManager().getPanel(viewid)).getAbsolutePanel();
		} else return;
		
		if (ep == null) return;
	    Iterator<Widget> it = ep.iterator();
	    while (it.hasNext()) {
	    	Widget nextItem = it.next();
	    	if (!(nextItem instanceof Canvas)) it.remove();
	    }
	}

    public boolean checkAutoCreateSliders(String s, AsyncOperation callback) {       	
       	Localization loc = ((AppW) app).getLocalization();
       	
		String[] options = { loc.getPlain("CreateSliders"),
		        app.getMenu("Cancel") };
		
		Image icon  = new Image(GGWToolBar.getMyIconResourceBundle().mode_slider_32().getSafeUri());
		icon.getElement().getStyle().setProperty("border", "3px solid steelblue");
		
		GOptionPaneW.INSTANCE.showOptionDialog(app, loc.getPlain("CreateSlidersForA", s),
		        loc.getPlain("CreateSliders"), GOptionPane.CUSTOM_OPTION,
		        GOptionPane.INFORMATION_MESSAGE, icon, options, callback);

		return false;
    }
    
    @Override
    public ConstructionProtocolNavigation getConstructionProtocolNavigation() {

		if (constProtocolNavigation == null) {
			constProtocolNavigation = new ConstructionProtocolNavigationW(this.getApp());
		}

		return constProtocolNavigation;
	
    }

	public void logout() {
	    // TODO Auto-generated method stub
	    
    }

	/**
	 * @param show whether to show the menubar or not
	 */
	public void showMenuBar(boolean show) {
	   if (((AppW) app).getObjectPool().getGgwMenubar() != null) {
		   ((AppW) app).getObjectPool().getGgwMenubar().setVisible(show);
	   } else {
		   ((AppWapplet) app).attachMenubar();
	   }
	   ((AppW)app).closePopups();
    }

	public void showToolBar(boolean show) {
	   if (((AppWapplet) app).getToolbar() != null) {
		   ((AppWapplet) app).getToolbar().setVisible(show);
	   } else {
		   ((AppWapplet) app).attachToolbar();
	   }
	   ((AppW)app).closePopups();
    }

	/**
	 * @param show
	 * 
	 * wheter to show algebra input or not
	 */
	public void showAlgebraInput(boolean show) {
	    if (algebraInput != null) {
	    	algebraInput.setVisible(show);
	    } else {
	    	((AppWapplet) app).attachAlgebraInput();
	    }
	    ((AppW)app).closePopups();
    }

	@Override
    protected void setCallerApp() {
	   this.caller_APP = WEB;
    }
    
	@Override
	public int setToolbarMode(int mode) {
		if (toolbarPanel == null) {
			return 0;
		}

		int ret = toolbarPanel.setMode(mode);
//		layout.getDockManager().setToolbarMode(mode);
		return ret;
//		return mode;
	}
	
	private int getAlgebraInputHeight() {
		if (algebraInput != null) {
			return algebraInput.getOffsetHeight();
		}
		return 0;
	}
	
	private int getToolbarHeight() {
		if (toolbarPanel != null) {
			return toolbarPanel.getOffsetHeight();
		}
		return 0;
	}

	@Override
    public void setActiveView(int evID) {
		if (layout == null || layout.getDockManager() == null){
			return;
		}
		layout.getDockManager().setFocusedPanel(evID);
	}

	@Override
    public boolean isDraggingViews() {
	    return draggingViews;
    }
	
	public void setDraggingViews(boolean draggingViews, boolean temporary){
		if(!temporary){
			this.oldDraggingViews = draggingViews;
		}
		this.draggingViews = draggingViews;
		if(layout != null){
			layout.getDockManager().enableDragging(draggingViews);
		}
	}
	
	public void refreshDraggingViews(){
		layout.getDockManager().enableDragging(oldDraggingViews);
	}

	public EuclidianViewWeb getPlotPanelEuclidanView() {
	    return (EuclidianViewWeb) probCalculator.plotPanel;
    }
	
	public boolean isConsProtNavigationPlayButtonVisible() {
		return getConstructionProtocolNavigation().isPlayButtonVisible();		
	}

	public boolean isConsProtNavigationProtButtonVisible() {
		return getConstructionProtocolNavigation().isConsProtButtonVisible();		
	}

	@Override
	public void detachView(int viewId) {
		switch (viewId) {
		case App.VIEW_FUNCTION_INSPECTOR:
			App.debug("Detaching VIEW_FUNCTION_INSPECTOR");
			((DialogManagerW) app.getDialogManager()).getFunctionInspector().setInspectorVisible(false);
			break;
		default:
			super.detachView(viewId);
		}
	}
	
	/**
	 * @return {@link BrowseGUI}
	 */
	public BrowseGUI getBrowseGUI() {
		if (this.browseGUI == null) {
			this.browseGUI = new BrowseGUI((AppWeb)this.app);
		}
		return this.browseGUI;
	}

	@Override
    public void invokeLater(Runnable runnable) {
	    runnable.run();
	    
    }
}
