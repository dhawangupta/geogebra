package geogebra.common.gui.dialog;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class ToolCreationDialogModel {

	private Kernel kernel;
	private App app;

	// Drop Down Lists
	private ToolInputOutputList inputAddList;
	private ToolInputOutputList outputAddList;

	// List Boxes
	private ToolInputOutputList inputList;
	private ToolInputOutputList outputList;

	// Change Listener
	private ToolInputOutputListener listener;

	private Macro newTool;

	public ToolCreationDialogModel(App app, ToolInputOutputListener listener) {
		this.app = app;
		this.kernel = app.getKernel();
		this.listener = listener;
		initLists();
	}

	private void initLists() {
		inputAddList = new ToolInputOutputList() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean remove(Object geo) {
				boolean ret = super.remove(geo);
				outputList.remove(geo);
				return ret;
			}
		};
		outputAddList = new ToolInputOutputList();
		inputList = new ToolInputOutputList() {
			@Override
			public boolean add(GeoElement geo) {
				if (!geo.hasChildren() || contains(geo)) {
					return false;
				}
				// add geo to list
				boolean ret = super.add(geo);
				if (ret) {
					inputAddList.remove(geo);
				}
				return ret;
			}
		};
		outputList = new ToolInputOutputList() {
			@Override
			public boolean add(GeoElement geo) {
				if (geo.isIndependent() || contains(geo)) {
					return false;
				}
				// add geo to list
				boolean ret = super.add(geo);
				if (ret) {
					outputAddList.remove(geo);
				}
				return ret;
			}
		};
	}

	/**
	 * Adds all possible input and output Geos to addLists should be called as
	 * before addSelectedGeosToOutput
	 */
	public void initAddLists() {
		if (inputAddList.size() == 0 || outputAddList.size() == 0) {
			TreeSet<GeoElement> sortedSet = kernel.getConstruction()
					.getGeoSetNameDescriptionOrder();
			Iterator<GeoElement> it = sortedSet.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				if (geo.hasChildren()) {
					inputAddList.add(geo);
				}
				if (!geo.isIndependent()) {
					outputAddList.add(geo);
				}
			}
		}
		listener.updateLists();
	}

	/**
	 * Adds selected Geos to outputList if outputList is empty
	 */
	public void addSelectedGeosToOutput() {
		if (outputList.size() == 0) {
			ArrayList<GeoElement> selGeos = app.getSelectionManager()
					.getSelectedGeos();
			for (int i = 0; i < selGeos.size(); i++) {
				GeoElement geo = selGeos.get(i);
				outputList.add(geo);
			}
		}
		listener.updateLists();
	}

	/**
	 * Updates the list of input objects by using the specified output objects.
	 */
	public void updateInputList() {
		// only change empty input list
		if (inputList.size() > 0)
			return;

		// get output objects
		GeoElement[] output = outputList.toGeoElements();

		// determine all free parents of output
		TreeSet<GeoElement> freeParents = new TreeSet<GeoElement>();
		for (int i = 0; i < output.length; i++) {
			output[i].addPredecessorsToSet(freeParents, true);
		}

		// fill input list with labeled free parents
		Iterator<GeoElement> it = freeParents.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isLabelSet()) {
				inputList.add(geo);
			}
		}
		listener.updateLists();
	}

	/**
	 * Tries to create a new Macro to decide whether finish button should be
	 * shown
	 * 
	 * @return false if creation of Macro fails, else true
	 */
	public boolean createTool() {
		// get input and output objects
		GeoElement[] input = inputList.toGeoElements();
		GeoElement[] output = outputList.toGeoElements();

		// try to create macro
		try {
			newTool = new Macro(kernel, "newTool", input, output);
			return true;
		} catch (Exception e) {
			// show error message
			app.showError(app.getLocalization().getError("Tool.CreationFailed")
					+ "\n" + e.getMessage());
			e.printStackTrace();
			newTool = null;
			return false;
		}
	}

	public void finish(App appToSave, String cmdName, String toolName,
			String toolHelp, boolean showInToolBar, String iconFileName) {

		newTool.setCommandName(cmdName);
		newTool.setToolName(toolName);
		newTool.setToolHelp(toolHelp);
		newTool.setShowInToolBar(showInToolBar);
		newTool.setIconFileName(iconFileName);

		// make sure new macro command gets into dictionary
		appToSave.updateCommandDictionary();
		Kernel kernel = appToSave.getKernel();
		// check if command name is not used already by another macro
		if (kernel.getMacro(cmdName) != null) {
			overwriteMacro(appToSave, kernel.getMacro(cmdName));
			return;
		}

		kernel.addMacro(newTool);
		// make sure new macro command gets into dictionary
		appToSave.updateCommandDictionary();

		// set macro mode
		if (newTool.isShowInToolBar()) {
			int mode = kernel.getMacroID(newTool)
					+ EuclidianConstants.MACRO_MODE_ID_OFFSET;
			appToSave.getGuiManager().addToToolbarDefinition(mode);
			appToSave.getGuiManager().updateToolbar();
			appToSave.setMode(mode);
		}

		// app.showMessage(app.getMenu("Tool.CreationSuccess"));
	}

	public boolean overwriteMacro(App appToSave, Macro macro) {
		boolean compatible = newTool.getNeededTypesString().equals(
				macro.getNeededTypesString());
		for (int i = 0; compatible && i < macro.getMacroOutput().length; i++)
			compatible = compatible
					&& macro.getMacroOutput()[i].getClass().equals(
							newTool.getMacroOutput()[i].getClass());
		if (compatible) {
			StringBuilder sb = new StringBuilder();
			newTool.getXML(sb);
			kernel.removeMacro(app.getMacro());
			if (appToSave.addMacroXML(sb.toString())) {
				// successfully saved, quitting
				appToSave.setXML(appToSave.getXML(), true);
				if (app.getMacro() != null)
					app.setSaved();
				// app.exit(); TODO?
			}
			return true;
		}
		App.debug("not compatible");
		return false;
		// TODO JOptionPane.showMessageDialog(this,
		// app.getLocalization().getError("InvalidInput") + ":\n"
		// + macro.toString());

	}

	public GeoElement[] getInputAddList() {
		return inputAddList.toGeoElements();
	}

	public GeoElement[] getOutputAddList() {
		return outputAddList.toGeoElements();
	}

	public GeoElement[] getInputList() {
		return inputList.toGeoElements();
	}

	public GeoElement[] getOutputList() {
		return outputList.toGeoElements();
	}

	public Macro getNewTool() {
		return newTool;
	}

	public void addToOutput(GeoElement geo) {
		outputList.add(geo);
		listener.updateLists();
	}

	public void addToOutput(int selectedIndex) {
		addToOutput(outputAddList.get(selectedIndex));
	}

	public void addToInput(GeoElement geo) {
		inputList.add(geo);
		listener.updateLists();
	}

	public void addToInput(int selectedIndex) {
		addToInput(inputAddList.get(selectedIndex));
	}

	public void removeFromOutput(ArrayList<Integer> selIndices) {
		for (int i = selIndices.size() - 1; i >= 0; i--) {
			int selectedIndex = selIndices.get(i);
			outputAddList.add(outputList.remove(selectedIndex)); // TODO insert
			// sorted

		}
		listener.updateLists();
	}

	public void removeFromInput(ArrayList<Integer> selIndices) {
		for (int i = selIndices.size() - 1; i >= 0; i--) {
			int selectedIndex = selIndices.get(i);
			inputAddList.add(inputList.remove(selectedIndex)); // TODO insert
																// sorted
		}
		listener.updateLists();
	}

	public void moveOutputUp(ArrayList<Integer> selIndices) {
		for (int i = 0; i < selIndices.size(); i++) {
			int selectedIndex = selIndices.get(i);
			if (selectedIndex > 0) {
				GeoElement geo = outputList.remove(selectedIndex);
				outputList.add(selectedIndex - 1, geo);
				selIndices.set(i, selectedIndex - 1);
			}
		}
		listener.updateLists();
	}

	public void moveOutputDown(ArrayList<Integer> selIndices) {
		for (int i = selIndices.size() - 1; i >= 0; i--) {
			int selectedIndex = selIndices.get(i);
			if (selectedIndex < outputList.size() - 1) {
				GeoElement geo = outputList.remove(selectedIndex);
				outputList.add(selectedIndex + 1, geo);
				selIndices.set(i, selectedIndex + 1);
			}
		}
		listener.updateLists();
	}

	public void moveInputUp(ArrayList<Integer> selIndices) {
		for (int i = 0; i < selIndices.size(); i++) {
			int selectedIndex = selIndices.get(i);
			if (selectedIndex > 0) {
				GeoElement geo = inputList.remove(selectedIndex);
				inputList.add(selectedIndex - 1, geo);
				selIndices.set(i, selectedIndex - 1);
			}
		}
		listener.updateLists();
	}

	public void moveInputDown(ArrayList<Integer> selIndices) {
		for (int i = selIndices.size() - 1; i >= 0; i--) {
			int selectedIndex = selIndices.get(i);
			if (selectedIndex < inputList.size() - 1) {
				GeoElement geo = inputList.remove(selectedIndex);
				inputList.add(selectedIndex + 1, geo);
				selIndices.set(i, selectedIndex + 1);
			}
		}
		listener.updateLists();
	}

}
