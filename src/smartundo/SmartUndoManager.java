package smartundo;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.LinkedList;
import java.util.Stack;


public class SmartUndoManager {

    TextEditor textEditor;
    Stack<Edits> undoStack;
    Stack<Edits> redoStack;
    LinkedList<Long> group_list;
    long group_counter = 1;
    String previousClass = "";


    public SmartUndoManager(TextEditor editor){

        this.textEditor = editor;
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        undoStack.setSize(100);
        redoStack.setSize(100);
        group_list = new LinkedList<>();

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

    public void undoGroup(String group){

        long group_number = Long.parseLong(group);
        boolean hasFoundGroup = false;

        System.out.println(undoStack.toString());
        /*try{
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
            }

        } catch(CannotUndoException e){
            System.out.println("Error in group undo.");
        }*/
    }

    public String showEdit(){
        return textEditor.um.toString();
    }

    public void addEdit(Edits edit) {

        edit.setGroup(group_counter);
        undoStack.push(edit);

        if (!edit.getEdit().getPresentationName().equals(previousClass)) {
            String temp = previousClass;
            previousClass = edit.getEdit().getPresentationName();
            textEditor.addGroupMenu(edit, group_counter);
            //Important, since we don't want to change the group number when it's the first edit done in the document
            if (!temp.equals(""))
                group_counter++;

            System.out.println(this.group_counter);
        }
    }

    public void emptyStacks(){
        undoStack.clear();
        redoStack.clear();
    }


}
