package smartundo;

import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;

public class SmartUndoEdit extends CompoundEdit {

	boolean isUnDone = false;
	
	public int getLength(){
		return SmartUndoManager.groups.size();
	}

	public void undo() throws CannotUndoException {
		super.undo();
		isUnDone = true;
	}

	public void redo() throws CannotUndoException {
		super.redo();
		isUnDone = false;
	}

	public boolean canUndo() {
		return SmartUndoManager.groups.size() > 0 && !isUnDone;
	}

	public boolean canRedo() {
		return SmartUndoManager.groups.size() > 0 && isUnDone;
	}
}
