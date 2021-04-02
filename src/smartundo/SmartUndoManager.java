package smartundo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.LinkedList;
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


    public SmartUndoManager(TextEditor editor){

        this.textEditor = editor;
        undoStack = new SizedStack<>(200);
        redoStack = new SizedStack<>(200);
        active_group_count = 0;

        time_of_previous_group = 0;

    }

    public void undo(){

        try {
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

    public void undoGroup(String group){

        long group_number = Long.parseLong(group);
        long previous_group_number = undoStack.peek().getGroup();
        boolean hasFoundGroup = false;


        //System.out.println(group);
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
                    break;
                }

                if(edit.getGroup() != previous_group_number){
                    textEditor.removeLastGroupMenuItem();
                    previous_group_number = edit.getGroup();
                }
            }

        } catch(CannotUndoException | NullPointerException e){
            System.out.println("Error in group undo.");
        }
    }

    public String showEdit(){
        return textEditor.um.toString();
    }

    public void addEdit(Edits edit) {


        if(time_of_previous_group == 0){
            time_of_previous_group = edit.getTime();
        }

        //5 is a temp value for testing.
        if(active_group_count == 5){
            active_group_count--;
            textEditor.removeFirstGroupMenuItem();
        }

        //This is where the group stuff happens, it seems to be working now
        System.out.println(edit.getEdit().getPresentationName().equals(previousClass));

        if (!edit.getEdit().getPresentationName().equals(previousClass) || edit.getTime() - time_of_previous_group >= 2000) {
            time_of_previous_group = edit.getTime();
            previousClass = edit.getEdit().getPresentationName();
            textEditor.addGroupMenu(edit, group_counter);
            //Important, since we don't want to change the group number when it's the first edit done in the document
            if (!firstEdit){
                ++group_counter;
                active_group_count++;
            }

            firstEdit = false;
            //System.out.println(group_counter);
        }

        edit.setGroup(group_counter);
        undoStack.push(edit);
    }
    //We need to reset everything, not only the two stacks.
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



}
