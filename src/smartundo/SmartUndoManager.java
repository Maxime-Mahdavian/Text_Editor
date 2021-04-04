package smartundo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.Stack;


public class SmartUndoManager {

    TextEditor textEditor;
    Stack<Edits> undoStack;
    Stack<Edits> redoStack;
    int active_group_count;
    long group_counter = 1;
    String previousClass = "";
    long time_of_previous_group;
    boolean firstEdit = true;
    final int MAX_ACTIVE_GROUPS = 15;


    public SmartUndoManager(TextEditor editor){

        this.textEditor = editor;
        undoStack = new SizedStack<>(200);
        redoStack = new SizedStack<>(200);
        active_group_count = 0;
        time_of_previous_group = 0;

    }
    //Undo function, this is what happens when you hit ctrl-z
    public void undo(){

        try {
            if(undoStack.size() == 0)
                return;

            Edits startEdit = undoStack.peek();
            String undo_class = undoStack.peek().getEdit().getUndoPresentationName();

            //We want to undo every edit within 2 seconds of the last one, or if a new edit type shows up
            while(undoStack.size() > 0 && startEdit.compareTime(undoStack.peek()) <= 2000 &&
                    undoStack.peek().getEdit().getUndoPresentationName().equals(undo_class)){
                Edits edit = undoStack.pop();
                redoStack.push(edit);

                edit.getEdit().undo();

            }

            updateGroupList();

        } catch(CannotUndoException | NullPointerException e){

        }
    }

    //Redo everything in the stack
    public void redo(){
        try {
            while(redoStack.size() > 0 ){
                redoStack.peek().getEdit().redo();
                Edits edit = redoStack.pop();
                undoStack.push(edit);
            }
        } catch (CannotRedoException e){

        }

        updateGroupList();
    }

    //undo every edit from a group
    public void undoGroup(String group){

        //System.out.println(undoStack.toString());
        long group_number = Long.parseLong(group);
        long previous_group_number = undoStack.peek().getGroup();
        boolean hasFoundGroup = false;

        try{
            while(undoStack.size() > 0){
                Edits edit = undoStack.peek();

                if(edit.getGroup() == group_number){
                    hasFoundGroup = true;
                    edit.getEdit().undo();
                    undoStack.pop();
                    redoStack.push(edit);
                }
                else if(edit.getGroup() != group_number && !hasFoundGroup){
                    edit.getEdit().undo();
                    undoStack.pop();
                    redoStack.push(edit);
                }
                else if(edit.getGroup() != group_number && hasFoundGroup){
                    textEditor.removeLastGroupMenuItem();
                    break;
                }

                if(edit.getGroup() != previous_group_number){
                    textEditor.removeLastGroupMenuItem();
                    previous_group_number = edit.getGroup();
                }
            }

            if(undoStack.size() == 0)
                textEditor.removeLastGroupMenuItem();

        } catch(CannotUndoException | NullPointerException e){
            System.out.println("Error in group undo.");
        }
    }

    //Delete every edit from a group from the stack, essentially making it undoable
    public void deleteUndoGroup(String group){

        long group_number = Long.parseLong(group);

        undoStack.removeIf(edit -> edit.getGroup() == group_number);
        updateGroupList();
        System.out.println(undoStack.toString());
    }

    //Add the edit to the undostack
    public void addEdit(Edits edit) {

        if(time_of_previous_group == 0){
            time_of_previous_group = edit.getTime();
        }

        //5 is a temp value for testing.
        if(active_group_count > MAX_ACTIVE_GROUPS){
            active_group_count--;
            textEditor.removeFirstGroupMenuItem();
        }

        //This is where the group stuff happens, it seems to be working now

        if (!edit.getEdit().getPresentationName().equals(previousClass) || edit.getTime() - time_of_previous_group >= 2000) {
            time_of_previous_group = edit.getTime();
            previousClass = edit.getEdit().getPresentationName();
            //Important, since we don't want to change the group number when it's the first edit done in the document
            if (!firstEdit){
                group_counter++;
                active_group_count++;
            }
            textEditor.addGroupMenu(edit, group_counter);
            firstEdit = false;
        }

        edit.setGroup(group_counter);
        undoStack.push(edit);
    }

    //We need to reset everything, not only the two stacks. Happens when opening a new file or a new file
    public void reset(){
        undoStack.clear();
        redoStack.clear();
        textEditor.removeAllGroupMenuItem();
        group_counter = 1;
        active_group_count = 0;
        previousClass = "";
        time_of_previous_group = 0;
        firstEdit = true;
    }

    //Update the group list, easier than making updating it while we're undoing
    public void updateGroupList(){

        textEditor.removeAllGroupMenuItem();
        active_group_count = 0;
        long previous_group = 0;

        for (Edits edit : undoStack) {
            if (edit.getGroup() != previous_group) {
                previous_group = edit.getGroup();
                active_group_count++;
                textEditor.addGroupMenu(edit, edit.getGroup());
            }
        }
    }
}
