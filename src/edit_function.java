import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class edit_function {

    TextEditor textEditor;

    public edit_function(TextEditor editor){

        this.textEditor = editor;
    }

    public void undo(){

        try {
            textEditor.um.undo();
        } catch(CannotUndoException e){

        }
    }

    public void redo(){
        try {
            textEditor.um.redo();
        } catch (CannotRedoException e){

        }
    }

    public String showEdit(){
        return textEditor.um.toString();
    }


}
