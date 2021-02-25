public class edit_function {

    TextEditor textEditor;

    public edit_function(TextEditor editor){

        this.textEditor = editor;
    }

    public void undo(){

        textEditor.um.undo();
    }

    public void redo(){

        textEditor.um.redo();
    }
}
