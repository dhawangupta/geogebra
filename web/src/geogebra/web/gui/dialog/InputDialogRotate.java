package geogebra.web.gui.dialog;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public abstract class InputDialogRotate extends AngleInputDialog implements KeyUpHandler {


	GeoPolygon[] polys;
	GeoPointND[] points;
	GeoElement[] selGeos;
	
	protected EuclidianController ec;

	private Kernel kernel;
	protected static String defaultRotateAngle = "45\u00b0"; // 45 degrees

	public InputDialogRotate(AppW app, String title,
			InputHandler handler, GeoPolygon[] polys, 
			GeoElement[] selGeos, Kernel kernel, EuclidianController ec) {
		super(app, app.getPlain("Angle"), title, defaultRotateAngle, false,
				handler, false);

		this.polys = polys;
		this.selGeos = selGeos;
		this.kernel = kernel;
		
		this.ec = ec;

		this.inputPanel.getTextComponent().getTextField().getValueBox().addKeyUpHandler(this);
	}

	/**
	 * Handles button clicks for dialog.
	 */	
	@Override
    public void onClick(ClickEvent e) {
		actionPerformed(e);
	}
	
	@Override
	protected void actionPerformed(DomEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || sourceShouldHandleOK(source)) {
				//FIXME setVisibleForTools(!processInput());
				if (!processInput()) {
					//wrappedPopup.show();
					inputPanel.getTextComponent().hideTablePopup();
				} else {
					wrappedPopup.hide();
					inputPanel.getTextComponent().hideTablePopup();
					app.getActiveEuclidianView().requestFocusInWindow();
				}
			//} else if (source == btApply) {
			//	processInput();
			} else if (source == btCancel) {
				//FIXME setVisibleForTools(false);
				wrappedPopup.hide();
				inputPanel.getTextComponent().hideTablePopup();
				app.getActiveEuclidianView().requestFocusInWindow();
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			//FIXME setVisibleForTools(false);
			wrappedPopup.hide();
			inputPanel.getTextComponent().hideTablePopup();
			app.getActiveEuclidianView().requestFocusInWindow();
		}
	}

	protected abstract boolean processInput();

/*
	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!isModal()) {
			app.setCurrentSelectionListener(null);
		}
		app.getGuiManager().setCurrentTextfield(this, true);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}
*/
	/*
	 * auto-insert degree symbol when appropriate
	 */
	public void onKeyUp(KeyUpEvent e) {

		// return unless digit typed (instead of !Character.isDigit)
		if (e.getNativeKeyCode() < 48 ||
			(e.getNativeKeyCode() >  57 && e.getNativeKeyCode() < 96) ||
			e.getNativeKeyCode() > 105)
			return;

		AutoCompleteTextFieldW tc = inputPanel.getTextComponent();
		String text = tc.getText();

		// if text already contains degree symbol or variable
		for (int i = 0; i < text.length(); i++) {
			if (!StringUtil.isDigit(text.charAt(i)))
				return;
		}

		int caretPos = tc.getCaretPosition();

		tc.setText(tc.getText() + Unicode.degree);

		tc.setCaretPosition(caretPos);
	}
}
