/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.
//
This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.web.gui.view.algebra;

import geogebra.common.awt.GFont;
import geogebra.common.gui.view.algebra.AlgebraController;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.settings.SettingListener;
import geogebra.html5.main.AppW;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.layout.panels.AlgebraStyleBarW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * AlgebraView with tree for free and dependent objects.
 * 
 */

public class AlgebraViewW extends AlgebraViewWeb implements SettingListener {
	/**
	 */
	//public static final int MODE_VIEW = 2;

	//private MyRenderer renderer;
	//private MyDefaultTreeCellEditor editor;
	//private MathTextField editTF;

	// store all pairs of GeoElement -> node in the Tree

	/**
	 * The tree model.
	 */
	//protected DefaultTreeModel model;

	/**
	 * Root node for tree mode MODE_DEPENDENCY.
	 */
	//protected TreeItem rootDependency;
	
	private AlgebraStyleBarW styleBar;
	private GeoElement selectedGeoElement;
	private TreeItem selectedNode;

	//private AlgebraHelperBar helperBar;

	private AlgebraController algebraController;

	public AlgebraController getAlgebraController() {
		return algebraController;
	}

	/**
	 * Flag for LaTeX rendering
	 */
	final private static boolean renderLaTeX = true;

	/** Creates new AlgebraView */
	public AlgebraViewW(AlgebraController algCtrl) {

		super((AppW)algCtrl.getApplication());

		App.debug("creating Algebra View");		
		algCtrl.setView(this);
		this.algebraController = algCtrl;

		// cell renderer (tooltips) and editor
		//ToolTipManager.sharedInstance().registerComponent(this);

		// EDITOR
		//setEditable(true);
		//initTreeCellRendererEditor();

		// add listener
		this.addDomHandler((MouseDownHandler)algCtrl, MouseDownEvent.getType());
		this.addDomHandler((TouchStartHandler) algCtrl, TouchStartEvent.getType());
		this.addDomHandler((TouchEndHandler) algCtrl, TouchEndEvent.getType());
		this.addDomHandler((TouchMoveHandler) algCtrl, TouchMoveEvent.getType());

		// add small border
		//setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));

		// initializes the tree model, important to set tree mode first to avoid inf. loop #3651
		this.treeMode = intToMode(app.getSettings().getAlgebra().getTreeMode());
		initModel();

		setLabels();
		
		getElement().setId("View_"+App.VIEW_ALGEBRA);

		this.addKeyDownHandler(this.app.getGlobalKeyDispatcher());
		this.addKeyUpHandler(this.app.getGlobalKeyDispatcher());
		this.addKeyPressHandler(this.app.getGlobalKeyDispatcher());

		this.setFocus(true);

		// tree's options
		//setRootVisible(false);
		// show lines from parent to children
		//putClientProperty("JTree.lineStyle", "Angled");
		//setInvokesStopCellEditing(true);
		//setScrollsOnExpand(true);
		//setRowHeight(-1); // to enable flexible height of cells

		//setToggleClickCount(1);

		// enable drag n drop
		//algCtrl.enableDnD();

		// Initialize settings and register listener
		app.getSettings().getAlgebra().addListener(this);

		
		settingsChanged(app.getSettings().getAlgebra());
	}

	/**
	 * @return {@link AlgebraStyleBarW}
	 */
	@Override
	public AlgebraStyleBarW getStyleBar() {
		if (this.styleBar == null) {
			this.styleBar = new AlgebraStyleBarW(app);
		}
		return this.styleBar;
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		super.updateVisualStyle(geo);
		if (styleBar != null) {
			styleBar.update(geo);
		}
	}

	public void updateFonts() {
		GFont font = app.getPlainFontCommon();
		getStyleElement().getStyle().setFontStyle(Style.FontStyle.valueOf(font.isItalic()?"ITALIC":"NORMAL"));
		getStyleElement().getStyle().setFontSize(font.getSize(), Style.Unit.PX);
		getStyleElement().getStyle().setFontWeight(Style.FontWeight.valueOf(font.isBold()?"BOLD":"NORMAL"));
		//setFont(font);
		//editor.setFont(font);
		//renderer.setFont(font);
		//editTF.setFont(font);
	}

	//@Override
	public void clearSelection() {

		// deselecting this causes a bug; it maybe fixed
		// by changing the repaintView method too,
		// adding setSelectedItem( some TreeItem ),
		// but which TreeItem should be that if more are selected?
		// that's why Arpad choosed to comment this out instead
		//super.setSelectedItem(null);

		for (int i = 0; i < getItemCount(); i++) {
			if (!(getItem(i).getUserObject() instanceof GeoElement))
				for (int j = 0; j < getItem(i).getChildCount(); j++) {
					getItem(i).getChild(j).setSelected(false);
				}
		}
		selectedGeoElement = null;
	}

	public GeoElement getSelectedGeoElement() {
		return selectedGeoElement;
	}

	

	/**
	 * @return The helper bar for this view.
	 */
	/*public AlgebraHelperBar getHelperBar() {
		if (helperBar == null) {
			helperBar = newAlgebraHelperBar();
		}

		return helperBar;
	}*/

	/**
	 * 
	 * @return new algebra helper bar
	 */
	/*protected AlgebraHelperBar newAlgebraHelperBar() {
		return new AlgebraHelperBar(this, app);
	}*/

	public Object getPathForLocation(int x, int y) {
		// TODO: auto-generated method stub
		return null;
	}

	/*@Override
	public void setToolTipText(String text) {
		renderer.setToolTipText(text);
	}*/

	public boolean editing = false;
	private GeoElement draggedGeo;

	/**
	 * Open Editor textfield for geo.
	 */
	public void startEditing(GeoElement geo, boolean shiftDown) {
		if (geo == null)
			return;

		// open Object Properties for eg GeoImages
		if (!geo.isAlgebraViewEditable()) {
			//FIXMEWEB ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
			//FIXMEWEB geos.add(geo);
			//FIXMEWEB app.getDialogManager().showPropertiesDialog(geos);
			return;
		}

		if (!shiftDown || !geo.isPointOnPath() && !geo.isPointInRegion()) {
			if (!geo.isIndependent() || !attached) // needed for F2 when Algebra
				// View closed
			{
				if (geo.isRedefineable()) {
					app.getDialogManager().showRedefineDialog(geo, true);
				}
				return;
			}

			if (!geo.isChangeable()) {
				if (geo.isFixed()) {
					app.showMessage(loc.getError("AssignmentToFixed"));
				} else if (geo.isRedefineable()) {
					app.getDialogManager().showRedefineDialog(geo, true);
				}
				return;
			}
		}

		TreeItem node = nodeTable.get(geo);

		if (node != null) {
			cancelEditing();
			//FIXMEWEB select and show node
			Widget wi = node.getWidget();
			editing = true;
			setAnimationEnabled(false);
			if (wi instanceof RadioButtonTreeItem)
				((RadioButtonTreeItem)wi).startEditing();
		}
	}

	/**
	 * Returns true if rendering is done with LaTeX
	 * 
	 * @return
	 */
	public boolean isRenderLaTeX() {
		return renderLaTeX;
	}

	public int[] getGridColwidths() {
		return new int[] { getElement().getOffsetWidth() };
	}

	public int[] getGridRowHeights() {
		// Object root=model.getRoot();
		// ArrayList<Integer> heights=new ArrayList<Integer>();
		// for (int i=0;i<model.getChildCount(root);i++){
		// Object folder=model.getChild(root, i);
		// if (model.)
		// }
		// // m.getChildCount(root);
		//
		// return new int[]{getHeight()};
		int[] heights = new int[getItemCount()];
		for (int i = 0; i < heights.length; i++) {
			heights[i] = getItem(i).getElement().getOffsetHeight();
		}
		heights[0] += 2;
		return heights;
	}

	/*public Component[][] getPrintComponents() {
		return null;
		//return new Component[][] { { this } };
	}*/

	

	
	
	


	// temporary proxies for the temporary implementation of AlgebraController in common
	public GeoElement getGeoElementForPath(Object tp) {
		//return getGeoElementForPath((TreePath)tp);
		return null;
	}

	public GeoElement getGeoElementForLocation(Object tree, int x, int y) {
		//return getGeoElementForLocation((JTree)tree, x, y);
		return null;
	}

	public Object getPathBounds(Object tp) {
		//return getPathBounds((TreePath)tp);
		return null;
	}
	// temporary proxies end

	public void cancelEditing() {
		editing = false;
		setAnimationEnabled(true);
	}

	public boolean isEditing() {
		return editing;
	}

	@Override
    protected boolean isKeyboardNavigationEnabled(TreeItem ti) {
		//keys should move the geos in the EV
		//if (isEditing())
			return false;
		//return super.isKeyboardNavigationEnabled(ti);
	}
	

	@Override
	public void onBrowserEvent(Event event) {
		if (event.getTypeInt() == Event.ONBLUR) {
			setActiveTreeItem(null);
		}

		// as arrow keys are prevented in super.onBrowserEvent,
		// we need to handle arrow key events before that
		switch (DOM.eventGetType(event)) {
			case Event.ONKEYUP:
				switch (DOM.eventGetKeyCode(event)) {
					case KeyCodes.KEY_UP:
					case KeyCodes.KEY_DOWN:
					case KeyCodes.KEY_LEFT:
					case KeyCodes.KEY_RIGHT:
					// this may be enough for Safari too, because it is not onkeypress
						if (!editing) {
							app.getGlobalKeyDispatcher().handleSelectedGeosKeysNative(event);
							event.stopPropagation();
							event.preventDefault();
							return;
						}
				}
		case Event.ONMOUSEDOWN:
		case Event.ONTOUCHSTART:
			app.closePopups();
			app.focusGained(AlgebraViewW.this);
		}
		if (!editing && (this.inputPanel == null || !this.inputPanel.hasFocus())){
			super.onBrowserEvent(event);
		}
	}

	public boolean hasFocus() {
	    App.debug("unimplemented");
	    return false;
    }

	@Override
    protected void onLoad() {
		// this may be important if the view is added/removed from the DOM
		super.onLoad();
		repaintView();
	}

	@Override
    public final void setUserObject(TreeItem ti, final Object ob) {
		ti.setUserObject(ob);
		if (ob instanceof GeoElement) {
			ti.setWidget(new RadioButtonTreeItem((GeoElement) ob,
			        AppResources.INSTANCE.shown().getSafeUri(),
			        AppResources.INSTANCE.hidden().getSafeUri()));
			ti.addStyleName("avItem");
			//ti.getElement().getStyle().setPadding(0, Unit.PX);

			// Workaround to make treeitem visual selection available
			DOM.setStyleAttribute((com.google.gwt.user.client.Element) ti
			        .getElement().getFirstChildElement(), "display",
			        "-moz-inline-box");
			DOM.setStyleAttribute((com.google.gwt.user.client.Element) ti
			        .getElement().getFirstChildElement(), "display",
			        "inline-block");
		} else {
			ti.setWidget(new GroupHeader(
					this.app.getSelectionManager(), ti, ob.toString(),GuiResources.INSTANCE.algebra_tree_open().getSafeUri(),
					GuiResources.INSTANCE.algebra_tree_closed().getSafeUri()));
		}
	}	

	public boolean isShowing() {
		return isVisible() && isAttached();
    }

	@Override
	public void setLabels() {
		super.setLabels();
		if (this.styleBar != null) {
			this.styleBar.setLabels();
		}
	}

	public void dragStart(DragStartEvent event, GeoElement geo) {
		setDraggedGeo(geo);
	}

	public GeoElement getDraggedGeo() {
		return draggedGeo;
	}

	public void setDraggedGeo(GeoElement draggedGeo) {
		this.draggedGeo = draggedGeo;
	}

} // AlgebraView
