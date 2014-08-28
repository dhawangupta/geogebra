package geogebra.tablet;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.touch.FileManager;
import geogebra.web.gui.GuiManagerW;

public class TabletFileManager extends FileManager {
	
	public TabletFileManager() {
		super();
	}
	
	@Override()
	public void addFile(final Material mat) {
		((GuiManagerW) Tablet.appFrame.app.getGuiManager()).getBrowseGUI().addMaterial(mat);
	}
	
	@Override()
	public void removeFile(final Material mat) {
		((GuiManagerW) Tablet.appFrame.app.getGuiManager()).getBrowseGUI().removeFromLocalList(mat);
	}
}