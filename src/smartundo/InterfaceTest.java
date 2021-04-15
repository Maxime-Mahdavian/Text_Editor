package smartundo;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import org.junit.jupiter.api.Test;
import smartundo.TextEditor;

class InterfaceTest {
	
	private TextEditor instance;

	@Test
	void actionPerformedNew() {
		System.out.println("TEST --> Perform New Action");
		instance = new TextEditor();
		ActionEvent e = new ActionEvent(instance, ActionEvent.ACTION_PERFORMED, "New");
		// A time frame of 5 seconds to enter some text to check whether the 
		// function works
		try {
			Thread.sleep(5000);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		System.out.println("   > Creating a new file...");
		instance.actionPerformed(e);
		boolean newFileWasCreated = instance.area.getText().length() == 0;
		assertEquals(newFileWasCreated, true);
		if(newFileWasCreated) {
			System.out.println("   > Test terminated successfully");
		} else {
			System.out.println("   > Test terminated with an error");
		}
		try {
			Thread.sleep(1500);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		instance.frame.dispose();
	}
	
// Quit Executes System.exit(0), therefore the section is commented
//	@Test
//	void actionPerformedQuit() {
//		System.out.println("TEST --> Perform Quit Action");
//		instance = new TextEditor();
//		ActionEvent e = new ActionEvent(instance, ActionEvent.ACTION_PERFORMED, "Quit");
//		try {
//			Thread.sleep(5000);
//		} catch(InterruptedException error) {
//			error.printStackTrace();
//		}
//		instance.actionPerformed(e);
//		assertEquals(instance.frame.isShowing(), false);
//		instance.frame.dispose();
//	}
	
	@Test
	void actionPerformedFont() {
		System.out.println("TEST --> Perform Font Action");
		instance = new TextEditor();
		ActionEvent e = new ActionEvent(instance, ActionEvent.ACTION_PERFORMED, "Font20");
		try {
			Thread.sleep(5000);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		System.out.println("   > Changing the font to 20...");
		instance.actionPerformed(e);
		boolean correctFont20 = instance.area.getFont().getSize() == 20;
		e = new ActionEvent(instance, ActionEvent.ACTION_PERFORMED, "Font8");
		try {
			Thread.sleep(3000);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		System.out.println("   > Changing the font to 8...");
		instance.actionPerformed(e);
		boolean correctFont8 = instance.area.getFont().getSize() == 8;
		assertEquals(correctFont20, correctFont8);
		if(correctFont20 && correctFont8) {
			System.out.println("   > Test terminated successfully");
		} else {
			System.out.println("   > Test terminated with an error");
		}
		try {
			Thread.sleep(1500);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		instance.frame.dispose();
	}
	
	@Test
	void actionPerformedUndoLast() {
		System.out.println("TEST --> Perform Undo Last Action");
		instance = new TextEditor();
		ActionEvent e = new ActionEvent(instance, ActionEvent.ACTION_PERFORMED, "Undo");
		try {
			Thread.sleep(5000);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		System.out.println("   > Performing Undo operation...");
		instance.actionPerformed(e);
		boolean redoStackNotZero = instance.smartUndoManager.redoStack.size() != 0;
		assertEquals(redoStackNotZero, true);
		if(redoStackNotZero){
			System.out.println("   > Test terminated successfully");
		} else {
			System.out.println("   > Test terminated with an error");
		}
		try {
			Thread.sleep(1500);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		instance.frame.dispose();
	}
	
	@Test
	void actionPerformedRedoLast() {
		System.out.println("TEST --> Perform Redo Last Action");
		instance = new TextEditor();
		ActionEvent e = new ActionEvent(instance, ActionEvent.ACTION_PERFORMED, "Undo");
		try {
			Thread.sleep(8000);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		System.out.println("   > Performing Undo operation...");
		instance.actionPerformed(e);
		try {
			Thread.sleep(1500);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		e = new ActionEvent(instance, ActionEvent.ACTION_PERFORMED, "Redo");
		System.out.println("   > Performing Redo operation...");
		instance.actionPerformed(e);
		boolean redoStackIsZero = instance.smartUndoManager.redoStack.size() == 0;
		assertEquals(redoStackIsZero, true);
		if(redoStackIsZero){
			System.out.println("   > Test terminated successfully");
		} else {
			System.out.println("   > Test terminated with an error");
		}
		try {
			Thread.sleep(1500);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		instance.frame.dispose();
	}
	
	@Test
	void actionPerformedEditList() {
		System.out.println("TEST --> Perform Edit List Action");
		instance = new TextEditor();
		ActionEvent e = new ActionEvent(instance, ActionEvent.ACTION_PERFORMED, "Edit Window");
		try {
			Thread.sleep(7000);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		System.out.println("   > Opening the Edit Window...");
		instance.actionPerformed(e);
		boolean editWindowIsVisible = instance.undoEditWindow.isVisible();
		assertEquals(editWindowIsVisible, true);
		if(editWindowIsVisible){
			System.out.println("   > Test terminated successfully");
		} else {
			System.out.println("   > Test terminated with an error");
		}
		try {
			Thread.sleep(4000);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		instance.frame.dispose();
		instance.undoEditWindow.dispose();
	}
	
	@Test
	void actionPerformedUndoGroup() {
		System.out.println("TEST --> Perform Undo Group Action");
		instance = new TextEditor();
		ActionEvent e = new ActionEvent(instance, ActionEvent.ACTION_PERFORMED, "Undo Group");
		try {
			Thread.sleep(7000);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		System.out.println("   > Opening the Undo Group Window...");
		instance.actionPerformed(e);
		boolean undoGroupWindowIsVisible = instance.undoGroupWindow.isVisible();
		System.out.println("   > Performing Undo Group 0...");
		int size = instance.smartUndoManager.undoStack.size();
		try {
			Thread.sleep(1500);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		e = new ActionEvent(instance, ActionEvent.ACTION_PERFORMED, "0");
		instance.actionPerformed(e);
		boolean groupSizeWasDecremented = instance.smartUndoManager.undoStack.size() != size;
		assertEquals(undoGroupWindowIsVisible, groupSizeWasDecremented);
		if(undoGroupWindowIsVisible && groupSizeWasDecremented){
			System.out.println("   > Test terminated successfully");
		} else {
			System.out.println("   > Test terminated with an error");
		}
		try {
			Thread.sleep(4000);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		instance.frame.dispose();
		instance.undoEditWindow.dispose();
	}
	
	@Test
	void actionPerformedDeleteGroup() {
		System.out.println("TEST --> Perform Delete Group Action");
		instance = new TextEditor();
		ActionEvent e = new ActionEvent(instance, ActionEvent.ACTION_PERFORMED, "Undo Group");
		try {
			Thread.sleep(7000);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		System.out.println("   > Opening the Delete Group Window...");
		instance.actionPerformed(e);
		boolean undoGroupWindowIsVisible = instance.undoGroupWindow.isVisible();
		System.out.println("   > Performing Delete Group 0...");
		int size = instance.smartUndoManager.undoStack.size();
		try {
			Thread.sleep(1500);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		e = new ActionEvent(instance, ActionEvent.ACTION_PERFORMED, "Delete1");
		instance.actionPerformed(e);
		boolean groupSizeWasDecremented = instance.smartUndoManager.undoStack.size() != size;
		assertEquals(undoGroupWindowIsVisible, groupSizeWasDecremented);
		if(undoGroupWindowIsVisible && groupSizeWasDecremented){
			System.out.println("   > Test terminated successfully");
		} else {
			System.out.println("   > Test terminated with an error");
		}
		try {
			Thread.sleep(4000);
		} catch(InterruptedException error) {
			error.printStackTrace();
		}
		instance.frame.dispose();
		instance.undoGroupWindow.dispose();
	}

}
