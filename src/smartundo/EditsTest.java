package smartundo;

import org.junit.jupiter.api.Assertions;
import smartundo.Edits;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

class EditsTest {

    @org.junit.jupiter.api.Test
    void compareTime() {
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
        }, 2000L);




        Edits edit2 = new Edits(new UndoableEdit() {
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
        }, 1000L);


        //Testing the compareTime function (edit1.getTime = 2000, edit2.getTime = 1000)
        Assertions.assertEquals(1000L, edits1.compareTime(edit2));

    }
}
