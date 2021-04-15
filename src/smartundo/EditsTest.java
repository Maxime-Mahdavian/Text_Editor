package smartundo;

import org.junit.jupiter.api.Test;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import static org.junit.jupiter.api.Assertions.*;


class EditsTest {

Edits edits1 = new Edits(new UndoableEdit() {
    @Override
    public void undo() throws CannotUndoException {

    }

    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public void redo() throws CannotRedoException {

    }

    @Override
    public boolean canRedo() {
        return false;
    }

    @Override
    public void die() {

    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean isSignificant() {
        return false;
    }

    @Override
    public String getPresentationName() {
        return null;
    }

    @Override
    public String getUndoPresentationName() {
        return null;
    }

    @Override
    public String getRedoPresentationName() {
        return null;
    }
}, 5L);

Edits edits2 = new Edits(new UndoableEdit() {
    @Override
    public void undo() throws CannotUndoException {

    }

    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public void redo() throws CannotRedoException {

    }

    @Override
    public boolean canRedo() {
        return false;
    }

    @Override
    public void die() {

    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean isSignificant() {
        return false;
    }

    @Override
    public String getPresentationName() {
        return null;
    }

    @Override
    public String getUndoPresentationName() {
        return null;
    }

    @Override
    public String getRedoPresentationName() {
        return null;
    }
}, 10L);


    @Test
    void constructorTest() {
        assertEquals(5L, edits1.getTime());
        assertEquals(0, edits1.getGroup());
        assertEquals(null, edits1.getEdit().getPresentationName());

    }

    @Test
    void getEdit() {
        assertEquals(null, edits1.getEdit().getPresentationName());
    }

    @Test
    void getTime() {
        assertEquals(5L, edits1.getTime());
    }

    @Test
    void setGroup() {
        edits1.setGroup(1);
        assertEquals(1, edits1.getGroup());
    }

    @Test
    void getGroup() {
        assertEquals(0, edits1.getGroup());
    }

    @Test
    void compareTime() {
        assertEquals(5L, edits2.compareTime(edits1));

    }
    
}
