package smartundo;

import javax.swing.undo.UndoableEdit;

/**
 * Wrapper class for the UndoableEdit interface. It stores that UndoableEdit along with the time of the
 * edit and the group number of that edit.
 */
public class Edits {

    private final UndoableEdit edit;
    private final long time;
    private long group;

    /**
     * @param edit The undoableEdit from the text editor
     * @param time Time of that edit
     */
    public Edits(UndoableEdit edit, long time){
        this.edit = edit;
        this.time = time;
        this.group = 0;
    }


    /**
     * @return UndoableEdit
     */
    public UndoableEdit getEdit(){
        return edit;
    }

    /**
     * @return Time of the edit
     */
    public long getTime(){
        return time;
    }

    /**
     * @param group Group number for that edit
     */
    public void setGroup(long group){
        this.group = group;
    }

    /**
     * @return Group number for that edit
     */
    public long getGroup(){
        return group;
    }

    /**
     * @param other Another edit to compare the time between both
     * @return The time between this time and other.time
     */
    public long compareTime(Edits other){
        return this.time - other.time;
    }

    /**
     * @return String representation of the Edit object
     */
    public String toString(){
        return "Edit: " + this.edit.getPresentationName() + "\nTime: " + this.time + "\nGroup: " + this.group;
    }



}
