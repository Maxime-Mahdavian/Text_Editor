package smartundo;

import javax.swing.undo.UndoableEdit;

public class Edits {

    private final UndoableEdit edit;
    private final long time;
    private long group;

    public Edits(UndoableEdit edit, long time, long group){
        this.edit = edit;
        this.time = time;
        this.group = group;
    }


    public UndoableEdit getEdit(){
        return edit;
    }

    public long getTime(){
        return time;
    }

    public void setGroup(long group){
        this.group = group;
    }

    public long getGroup(){
        return group;
    }

    public long compareTime(Edits other){
        return this.time - other.time;
    }

    public String toString(){
        return "Edit: " + this.edit.getPresentationName() + "\nTime: " + this.time + "\nGroup: " + this.group;
    }



}
