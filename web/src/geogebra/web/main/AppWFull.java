package geogebra.web.main;

import geogebra.common.GeoGebraConstants;
import geogebra.html5.gui.laf.GLookAndFeelI;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import geogebra.html5.gui.util.CancelEventTimer;
import geogebra.html5.main.AppW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.util.keyboard.OnScreenKeyBoard;

import com.google.gwt.user.client.ui.Widget;

public abstract class AppWFull extends AppW {

	protected AppWFull(ArticleElement ae, int dimension, GLookAndFeelI laf) {
		super(ae, dimension, laf);
	}

	@Override
	public void showKeyboard(Widget textField) {
		getAppletFrame().showKeyBoard(true, textField, false);
		if (textField != null) {
			CancelEventTimer.keyboardSetVisible();
		}
	}

	@Override
	public void showKeyboard(Widget textField, boolean forceShow) {
		getAppletFrame().showKeyBoard(true, textField, forceShow);
		if (textField != null) {
			CancelEventTimer.keyboardSetVisible();
		}
	}

	@Override
	public void updateKeyBoardField(Widget field) {
		OnScreenKeyBoard.setInstanceTextField(this, field);
	}

	@Override
	public void hideKeyboard() {
		getAppletFrame().showKeyBoard(false, null, false);
	}

	public void showStartTooltip() {
		if (articleElement.getDataParamShowStartTooltip()) {
			ToolTipManagerW.sharedInstance().setBlockToolTip(false);
			ToolTipManagerW.sharedInstance().showBottomInfoToolTip(
			        "Welcome to GeoGebra<br/>Click ? for tutorials.",
			        GeoGebraConstants.QUICKSTART_URL, ToolTipLinkType.Help,
			        this);
			ToolTipManagerW.sharedInstance().setBlockToolTip(true);
		}
	}
}
