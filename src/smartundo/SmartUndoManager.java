package smartundo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.Stack;


/**
 * SmartUndoManager component, it keeps tracks of objects Edits and it can undo them, redo them, as well
 * as undoing group of edits.
 */
public class SmartUndoManager {

    protected TextEditor textEditor;
    protected Stack<Edits> undoStack;
    protected Stack<Edits> redoStack;
    protected int active_group_count;
    protected long group_counter = 1;
    protected String previousClass = "";
    protected long time_of_previous_group;
    private boolean firstEdit;
    private final int MAX_ACTIVE_GROUPS = 15;


    /**
     * @param editor Front-End text editor component
     */
    public SmartUndoManager(TextEditor editor){

        this.textEditor = editor;
        undoStack = new SizedStack<>(200);
        redoStack = new SizedStack<>(200);
        active_group_count = 0;
        time_of_previous_group = 0;
        firstEdit = true;

    }

    /**
     * Undo function, this is what happens when you hit ctrl-z
     * It takes every edit in the undo stack within 2 seconds of the first edit and of the same type
     * and undoes them while also pushing them in the redo stack in case the user wants to redo them
     */
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

            //After undoing them, we want to check if any group was not removed
            updateGroupWindow();
            updateEditWindow();

        } catch(CannotUndoException | NullPointerException e){
            //Silently ignore exceptions, since it is pretty clear when there is nothing to be undone
        }
    }

    /**
     * Redo every edit in the redo stack.
     */
    public void redo(){
        try {
            while(redoStack.size() > 0 ){
                redoStack.peek().getEdit().redo();
                Edits edit = redoStack.pop();
                undoStack.push(edit);
            }
        } catch (CannotRedoException e){
            //Silently ignore exceptions, since it is pretty clear when there is nothing to be undone
        }

        //After redoing, we need to check if any groups was not added back
        updateGroupWindow();
        updateEditWindow();
    }

    /**
     * @param group Number of the group to undo
     * Undo every edit from a group.
     * It undoes every edit between the earliest one to the last edit in the group number
     */
    public void undoGroup(String group){


        long group_number = Long.parseLong(group);
        long previous_group_number = undoStack.peek().getGroup();
        boolean hasFoundGroup = false;

        try{
            while(undoStack.size() > 0){
                Edits edit = undoStack.peek();

                //We have found the group, so we can start undoing it
                if(edit.getGroup() == group_number){
                    hasFoundGroup = true;
                    edit.getEdit().undo();
                    undoStack.pop();
                    redoStack.push(edit);
                }
                //If the group number is not the same as the one we need to undo and we have not found the group yet
                //then this group is earlier than group, so we need to undo it
                else if(edit.getGroup() != group_number && !hasFoundGroup){
                    edit.getEdit().undo();
                    undoStack.pop();
                    redoStack.push(edit);
                }
                //If the group number of the current edit is not the one from the desired group but we have
                //already found the group to undo, then we have undone every edit within the desired group
                //So we update the undoWindow and exit the loop.
                else if(edit.getGroup() != group_number && hasFoundGroup){
                    //textEditor.removeLastGroupMenuItem();
                    updateGroupWindow();
                    updateEditWindow();
                    break;
                }
                //If we have found a new, previously unseen group, then we need to update the previous group number
                //and update the undoWindow. This allows the user to dynamically see the window changes as the undo stack changes
                if(edit.getGroup() != previous_group_number){
                    //textEditor.removeLastGroupMenuItem();
                    updateGroupWindow();
                    updateEditWindow();
                    previous_group_number = edit.getGroup();
                }
            }

            //This removes one-off errors encountered when undoing every group
            if(undoStack.size() == 0){
                //textEditor.removeLastGroupMenuItem();
                textEditor.removeAllGroupElements();
            }


        } catch(CannotUndoException | NullPointerException e){
           //Silently ignore undo mistakes
        }
    }

    /**
     * @param group Group number to delete
     *
     * Delete every edit from a group from the stack, essentially making it not undoable
     */
    public void deleteUndoGroup(String group){

        long group_number = Long.parseLong(group);

        //Uses java stream feature to remove the edits. Much more compact and easier to read
        undoStack.removeIf(edit -> edit.getGroup() == group_number);
        //updateGroupList();
        updateGroupWindow();
        updateEditWindow();
    }

    /**
     * @param time Time of the edit, used to identify the edit to undo
     *
     *  Undo an edit from the window. Its behaviour is identical of undoGroup
     */
    public void undoEdit(String time){

        long time_of_edit = Long.parseLong(time);

        try{
            while(undoStack.size() > 0) {
                Edits edit = undoStack.peek();

                //If we have found the right time, then we have found the right edit to edit
                if (edit.getTime() == time_of_edit) {
                    edit.getEdit().undo();
                    undoStack.pop();
                    redoStack.push(edit);
                    break;

                //If the time is not the same, then it is not the edit we are looking for
                } else if (edit.getTime() != time_of_edit) {
                    edit.getEdit().undo();
                    undoStack.pop();
                    redoStack.push(edit);
                }

                //This removes one-off errors encountered when undoing every edit
                if (undoStack.size() == 0) {
                    textEditor.removeAllGroupElements();
                    textEditor.removeAllEditElements();
                }
            }
        } catch(CannotUndoException | NullPointerException e){
            //Silently ignore undo mistakes
        }
        updateGroupWindow();
        updateEditWindow();

    }

    /**
     * @param time The time of the desired edit, used to identify it
     *
     *  It deletes the edit from the undo stack, its behaviour is identical to its group counterpart.
     */
    public void deleteEdit(String time){

        long time_of_edit = Long.parseLong(time);

        undoStack.removeIf(edit -> edit.getTime() == time_of_edit);
        updateGroupWindow();
        updateEditWindow();
    }

    /**
     * @param edit Newest edit to add to the stack
     *
     *  Called by the front end component every time a new edit is created. It takes care of giving a group
     *  number to the edit, depending on the time the edit arrives to and the type of edit. After this,
     *  it adds the edit to the undo stack
     */
    public void addEdit(Edits edit) {

        //We need to set up the time of the previous group for the first time.
        if(time_of_previous_group == 0){
            time_of_previous_group = edit.getTime();
        }

        //To keep the UndoWindow manageable, we only allow a certain number of groups in the window
        if(active_group_count == MAX_ACTIVE_GROUPS-1){
            active_group_count--;
            //textEditor.removeFirstGroupMenuItem();
            textEditor.removeFirstGroupElement();
        }


        //If edit is of a different type than the last one, or that it is more than two seconds later than the first edit
        //of the last group, then we need to increment the group counter.
        if (!edit.getEdit().getPresentationName().equals(previousClass) || edit.getTime() - time_of_previous_group >= 2000) {
            time_of_previous_group = edit.getTime();
            previousClass = edit.getEdit().getPresentationName();

            //Important, since we don't want to change the group number when it's the first edit done in the document
            if (!firstEdit){
                group_counter++;
                active_group_count++;
            }

            //We need to add the new complete group to the undoWindow
            textEditor.addGroupElements(edit, group_counter);
            firstEdit = false;
        }

        //Set the group number of that edit, then push is to the undo stack
        edit.setGroup(group_counter);
        undoStack.push(edit);
        updateEditWindow();
    }

    /**
     * Used when opening a new file, to clear any information from the previous file
     */
    public void reset(){
        undoStack.clear();
        redoStack.clear();
        updateGroupWindow();
        updateEditWindow();
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
            if (edit.getGroup() != previous_group && active_group_count <= MAX_ACTIVE_GROUPS) {
                previous_group = edit.getGroup();
                active_group_count++;
                textEditor.addGroupMenu(edit, edit.getGroup());
            }
        }
    }

    /**
     * Updates the UndoWindow. It goes through the undo stack and checks for groups, then adds them to the window
     * up to the MAX_ACTIVE_GROUPS
     */
    public void updateGroupWindow(){

        textEditor.removeAllGroupElements();
        active_group_count = 0;
        long previous_group = 0;


        for(Edits edit: undoStack){
            if(edit.getGroup() != previous_group && active_group_count <= MAX_ACTIVE_GROUPS){
                previous_group = edit.getGroup();
                active_group_count++;
                textEditor.addGroupElements(edit, edit.getGroup());
            }
        }
    }

    public void updateEditWindow(){
        textEditor.removeAllEditElements();
        //active_group_count = 0;
        // long previous_group = 0;


        for(Edits edit: undoStack){
            textEditor.addEditElements(edit, edit.getTime());
        }
    }
}
