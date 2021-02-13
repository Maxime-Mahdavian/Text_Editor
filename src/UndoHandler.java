//java undo and redo action classes

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;

class UndoHandler implements UndoableEditListener{


    UndoManager undoManager = new UndoManager();
    private RedoAction redoAction = null;
    private UndoAction undoAction = null;

    public void undoableEditHappened(UndoableEditEvent e){

        undoManager.addEdit(e.getEdit());
        undoAction.update();
        redoAction.update();
    }
}

class UndoAction extends AbstractAction{

    private UndoManager undoManager = new UndoManager();
    private RedoAction redoAction = null;

    public UndoAction(){
        super("Undo");
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e){

        try{
            undoManager.undo();
        }
        catch(CannotUndoException ex){
            //todo deal with that
        }
        update();
        redoAction.update();
    }

    protected void update(){

        if(undoManager.canUndo()){
            setEnabled(true);
            putValue(Action.NAME, undoManager.getUndoPresentationName());
        }
        else{
            setEnabled(false);
            putValue(Action.NAME, "Undo");
        }
    }
}

class RedoAction extends AbstractAction{

    private UndoManager undoManager = new UndoManager();
    private UndoAction undoAction = null;

    public RedoAction(){
        super("Redo");
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e){
        try{
            undoManager.redo();
        }
        catch(CannotRedoException ex){
            //todo do something with that
        }
        update();
        undoAction.update();
    }

    protected void update(){
        if(undoManager.canRedo()){
            setEnabled(true);
            putValue(Action.NAME, undoManager.getRedoPresentationName());
        }
        else{
            setEnabled(false);
            putValue(Action.NAME, "Redo");
        }
    }
}