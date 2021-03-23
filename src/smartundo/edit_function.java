package smartundo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.Stack;

public class edit_function{

    TextEditor textEditor;
    Stack<Edits> undoStack;
    Stack<Edits> redoStack;


    public edit_function(TextEditor editor){

        this.textEditor = editor;
        undoStack = new Stack<>();
        redoStack = new Stack<>();
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

            long startTime = undoStack.peek().getTime();
            String undo_class = undoStack.peek().getEdit().getUndoPresentationName();

            while(undoStack.size() > 0 && startTime - undoStack.peek().getTime() <= 2000 &&
                    undoStack.peek().getEdit().getUndoPresentationName().equals(undo_class)){
                Edits edit = undoStack.pop();
                redoStack.push(edit);

                edit.getEdit().undo();
                //System.out.println(editsStack.peek().getTime() - startTime);
            }
        } catch(CannotUndoException e){

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


}
