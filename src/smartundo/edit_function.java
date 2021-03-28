package smartundo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.Stack;

//TODO: put a limit on the undoStack

public class edit_function{

    TextEditor textEditor;
    Stack<Edits> undoStack;
    Stack<Edits> redoStack;


    public edit_function(TextEditor editor){

        this.textEditor = editor;
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        undoStack.setSize(100);
        redoStack.setSize(100);
    }

    public void undo(){

        try {
            //textEditor.um.undo();

            /*String undo_class = textEditor.um.getUndoPresentationName();
            textEditor.um.undo();

            for(int i = 1; i < 5; i++){
                if(!textEditor.um.getUndoPresentationName().equals(undo_class))
                    break;
                else
                    textEditor.um.undo();*/
            if(undoStack.size() == 0)
                return;

            Edits startEdit = undoStack.peek();
            String undo_class = undoStack.peek().getEdit().getUndoPresentationName();

            while(undoStack.size() > 0 && startEdit.compareTime(undoStack.peek()) <= 2000 &&
                    undoStack.peek().getEdit().getUndoPresentationName().equals(undo_class)){
                Edits edit = undoStack.pop();
                redoStack.push(edit);

                edit.getEdit().undo();
                //System.out.println(editsStack.peek().getTime() - startTime);
            }
        } catch(CannotUndoException | NullPointerException e){

        }
    }

    public void redo(){
        try {
            //textEditor.um.redo();

            while(redoStack.size() > 0 ){
                redoStack.peek().getEdit().redo();
                Edits edit = redoStack.pop();
                undoStack.push(edit);
            }
        } catch (CannotRedoException e){

        }
    }

    public String showEdit(){
        return textEditor.um.toString();
    }

    public void addEdit(Edits edit){
        undoStack.push(edit);
    }

    public void emptyStacks(){
        undoStack.clear();
        redoStack.clear();
    }


}
