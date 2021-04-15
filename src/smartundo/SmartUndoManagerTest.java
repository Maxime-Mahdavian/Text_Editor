package smartundo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import smartundo.Edits;
import smartundo.SmartUndoManager;
import smartundo.TextEditor;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class SmartUndoManagerTest {

    SmartUndoManager smartUndoManager = new SmartUndoManager(new TextEditor());

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
                return "Undo Stack includes Edit1 ";
            }

            @Override
            public String getUndoPresentationName() {
                return "undoActionName1";
            }

            @Override
            public String getRedoPresentationName() {
                return "redoActionName1";
            }
        }, 1L);

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
                return "Undo Stack includes Edit2";
            }

            @Override
            public String getUndoPresentationName() {
                return "undoActionName2";
            }

            @Override
            public String getRedoPresentationName() {
                return "redoActionName2";
            }
        }, 2L);

        Edits edits3 = new Edits(new UndoableEdit() {
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
                return "Undo Stack includes Edit3";
            }

            @Override
            public String getUndoPresentationName() {
                return "undoActionName3";
            }

            @Override
            public String getRedoPresentationName() {
                return "redoActionName3";
            }
        }, 3L);


    @Test
    void undo() {


        smartUndoManager.undoStack.push(edits1);
        smartUndoManager.undoStack.push(edits2);
        smartUndoManager.undoStack.push(edits3);
        smartUndoManager.undo();

        Assertions.assertAll(
                "header",
                ()->assertEquals(2, smartUndoManager.undoStack.size()),
                ()->assertEquals(1, smartUndoManager.redoStack.size())
        );
    }

    @Test
    void redo() {
        smartUndoManager.undoStack.push(edits1);
        smartUndoManager.undoStack.push(edits2);
        smartUndoManager.undoStack.push(edits3);
        smartUndoManager.undo();
        smartUndoManager.redo();

        Assertions.assertAll(
                "header",
                ()->assertEquals(3, smartUndoManager.undoStack.size()),
                ()->assertEquals(0, smartUndoManager.redoStack.size())
        );
    }


    @Test
    void undoGroup() {

        smartUndoManager.addEdit(edits1);
        smartUndoManager.addEdit(edits2);
        smartUndoManager.addEdit(edits3);

        smartUndoManager.undoGroup("3");

        Assertions.assertAll(
                "heading",
                ()->assertEquals(2, smartUndoManager.undoStack.size()),
                ()->assertEquals(1, smartUndoManager.redoStack.size())

        );
    }


    @Test
    void deleteUndoGroup() {

        smartUndoManager.addEdit(edits1);
        smartUndoManager.addEdit(edits2);
        smartUndoManager.addEdit(edits3);

        smartUndoManager.deleteUndoGroup("1");

        Assertions.assertAll(
                "heading",
                ()->assertEquals(2, smartUndoManager.undoStack.size()),
                ()->assertEquals(0, smartUndoManager.redoStack.size())

        );
    }

    @Test
    void undoEdit() {

        smartUndoManager.addEdit(edits1);
        smartUndoManager.addEdit(edits2);
        smartUndoManager.addEdit(edits3);

        smartUndoManager.undoEdit("3");

        Assertions.assertAll(
                "heading",
                ()->assertEquals(2, smartUndoManager.undoStack.size()),
                ()->assertEquals(1, smartUndoManager.redoStack.size())

        );
    }


    @Test
    void deleteEdit() {

        smartUndoManager.addEdit(edits1);
        smartUndoManager.addEdit(edits2);
        smartUndoManager.addEdit(edits3);

        smartUndoManager.deleteEdit("3");

        Assertions.assertAll(
                "heading",
                ()->assertEquals(2, smartUndoManager.undoStack.size()),
                ()->assertEquals(0, smartUndoManager.redoStack.size())

        );
    }

    @Test
    void addEdit() {

        smartUndoManager.addEdit(edits1);
        smartUndoManager.addEdit(edits2);
        smartUndoManager.addEdit(edits3);

        Assertions.assertAll(
                "heading",
                ()->assertEquals(3, smartUndoManager.undoStack.size()),
                ()->assertEquals(0, smartUndoManager.redoStack.size())

        );
    }

    @Test
    void reset() {

        smartUndoManager.addEdit(edits1);
        smartUndoManager.addEdit(edits2);
        smartUndoManager.addEdit(edits3);

        smartUndoManager.reset();

        Assertions.assertAll(
                "heading",
                ()->assertEquals(0, smartUndoManager.undoStack.size()),
                ()->assertEquals(0, smartUndoManager.redoStack.size())

        );
    }
    
    
    @Test
    void updateGroupWindow() {
        smartUndoManager.addEdit(edits1);
        smartUndoManager.addEdit(edits2);
        smartUndoManager.updateGroupWindow();

        Assertions.assertAll(
                "heading",
                ()->assertEquals(1, edits1.getGroup()),
                ()->assertEquals(2, edits2.getGroup())
        );

    }
    
    
    @Test
    void updateEditWindow() {
        smartUndoManager.addEdit(edits1);
        smartUndoManager.addEdit(edits2);
        smartUndoManager.updateGroupWindow();


        Assertions.assertAll(
                "heading",
                ()->assertEquals(2, smartUndoManager.undoStack.size()),
                ()->assertEquals(0, smartUndoManager.redoStack.size())

        );

    }


}
