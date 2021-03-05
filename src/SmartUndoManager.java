package smartundo;

import java.util.LinkedList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author artemcernigel
 */
public class SmartUndoManager extends AbstractUndoableEdit implements UndoableEditListener {

	public static LinkedList<SmartUndoEdit> groups;
	public static LinkedList<String> editStrings;
	private SmartUndoEdit current;
	int index = -1;
	String lastEditName = null;

	public SmartUndoManager() {
		super();
		groups = new LinkedList<>();
		editStrings = new LinkedList<>();
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		UndoableEdit edit = e.getEdit();
		try {
			AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) edit;
			int offset = event.getOffset();
			int length = event.getLength();
			boolean newGroup = false;
			String text = event.getDocument().getText(offset, length);
			if (current == null) {
				newGroup = true;
			} else if (text.contains("\n")) {
				newGroup = true;
			} else if (lastEditName == null || !lastEditName.equals(edit.getPresentationName())) {
				newGroup = true;
			}
			if (newGroup) {
				createEdit();
			}
			if (!newGroup) {
				editStrings.set(index, editStrings.get(index) + text);
			} else {
				editStrings.add(text);
			}
			current.addEdit(edit);
			lastEditName = edit.getPresentationName();
		} catch (BadLocationException exc) {
			exc.printStackTrace();

		}
	}

	public void createEdit() {
		current = new SmartUndoEdit();
		groups.add(current);
		index++;
	}

	@Override
	public void undo() throws CannotUndoException {
		if (!this.canUndo()) {
			throw new CannotUndoException();
		}
		groups.get(index).undo();
		index--;
	}

	public void undo(int i) throws CannotUndoException {
		if (!this.canUndo()) {
			throw new CannotUndoException();
		}
		groups.get(i).undo();
		for(; i < groups.size() - 1; i++){
			groups.set(i, groups.get(i + 1));
		}
		groups.removeLast();
		index--;
	}

	@Override
	public void redo() throws CannotUndoException {
		if (!this.canRedo()) {
			throw new CannotUndoException();
		}
		index++;
		groups.get(index).redo();
		index--;
	}

	@Override
	public boolean canUndo() {
		return index > -1;
	}

	@Override
	public boolean canRedo() {
		return groups.size() > 0 && index < groups.size() - 1;
	}

}
