package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.html5.main.AppW;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.properties.PropertiesViewW;

import com.google.gwt.user.client.ui.Widget;

public class PropertiesDockPanelW extends DockPanelW {

	private PropertiesViewW view;
	private boolean auxWasVisible;
	private boolean wasAVShowing;
	private boolean isVisible = false;

	/**
	 * @param app
	 */
	public PropertiesDockPanelW(AppW app) {
		super(
			App.VIEW_PROPERTIES, 	// view id
			"Properties", 					// view title phrase 
			null,	// toolbar string
			true,					// style bar?
			7,						// menu order
			'E' // ctrl-shift-E
		);
		
		this.app = app;
		this.setOpenInFrame(true);
		super.setDialog(true);
		this.setShowStyleBar(true);
	}

	private void getPropertiesView() {
		view = (PropertiesViewW) app.getGuiManager().getPropertiesView();
	}

	@Override
	protected Widget loadComponent() {
		getPropertiesView();

//		if (isOpenInFrame())
//			view.windowPanel();
//		else
//			view.unwindowPanel();
		return view.getWrappedPanel();
	}

//	@Override
//	protected Widget loadStyleBar() {
//		getPropertiesView();
//		return ((PropertiesStyleBarW) view.getStyleBar()).getWrappedPanel();
//	}
//	
	@Override
    public void showView(boolean b) {
		// TODO Auto-generated method stub
	    
    }

	@Override
	public void onResize() {

		// this hack may be temporary
		if (view != null) {
			view.onResize();
			view.repaintView();
			
		}
	}
	
	@Override
	public boolean isStyleBarEmpty(){
		return true;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible == this.isVisible) {
			return;
		}
		this.isVisible = visible;
		if (visible) {
			wasAVShowing = app.getGuiManager().hasAlgebraViewShowing();
			auxWasVisible = app.getSettings().getAlgebra()
				        .getShowAuxiliaryObjects();
			if (!wasAVShowing) {
				app.getGuiManager().setShowView(true, App.VIEW_ALGEBRA);
				app.updateViewSizes();
			}
			app.setShowAuxiliaryObjects(true);

		} else {
			if (!auxWasVisible) {
				app.setShowAuxiliaryObjects(false);
			}
			if (!wasAVShowing) {
				app.getGuiManager().setShowView(false, App.VIEW_ALGEBRA);
				app.updateViewSizes();
			}
		}


	}

}
