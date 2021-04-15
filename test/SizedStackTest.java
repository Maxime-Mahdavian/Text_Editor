import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import smartundo.Edits;
import smartundo.SizedStack;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import static org.junit.jupiter.api.Assertions.*;

class SizedStackTest {
    SizedStack sizedStack = new SizedStack(2);
    
    Edits edit1 = new Edits(new UndoableEdit() {
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
    }, 1L);

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
    }, 2L);

    Edits edit3 = new Edits(new UndoableEdit() {
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
    }, 3L);


    @Test
    void push() {

        //Testing if the SizedStack behaves correctly when edits are pushed beyond stack size limit
        //Pushed all 3 edits on stack
        //Edit1.getTime = 1, Edit2.getTime = 2, Edit3.getTime = 3, SizedStack.size = 2
        Assertions.assertAll(
                "heading",
                ()->assertEquals(edit1, sizedStack.push(edit1)),
                ()->assertEquals(edit2, sizedStack.push((edit2))),
                ()->assertEquals(edit3, sizedStack.push(edit3)),
                ()->assertEquals(edit2,sizedStack.firstElement())
        );
    }
}
