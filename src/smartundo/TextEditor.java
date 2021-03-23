package smartundo;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TextEditor extends JFrame implements ActionListener {

    private static JTextArea area;
    private static JFrame frame;
    private static JScrollPane scrollPane;
    private static int returnValue = 0;
    UndoManager um;
    edit_function edit = new edit_function(this);
    long group_counter = 1;
    String previousClass = "";


    public TextEditor() { run(); }

    public void run() {



        frame = new JFrame("Text Edit");

        // Set the look-and-feel (LNF) of the application
        // Try to default to whatever the host system prefers
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Build the menu
        JMenuBar menu_main = new JMenuBar();

        JMenu menu_file = new JMenu("File");

        JMenuItem menuitem_new = new JMenuItem("New");
        JMenuItem menuitem_open = new JMenuItem("Open");
        JMenuItem menuitem_save = new JMenuItem("Save");
        JMenuItem menuitem_quit = new JMenuItem("Quit");

        menuitem_new.addActionListener(this);
        menuitem_open.addActionListener(this);
        menuitem_save.addActionListener(this);
        menuitem_quit.addActionListener(this);

        menu_main.add(menu_file);

        menu_file.add(menuitem_new);
        menu_file.add(menuitem_open);
        menu_file.add(menuitem_save);
        menu_file.add(menuitem_quit);

        //Undo and redo menu item
        JMenu undoMenu = new JMenu("Edit");
        JMenuItem undoMenuItem = new JMenuItem("Undo");
        JMenuItem redoMenuItem = new JMenuItem("Redo");
        undoMenuItem.addActionListener(this);
        redoMenuItem.addActionListener(this);
        undoMenuItem.setActionCommand("Undo");
        redoMenuItem.setActionCommand("Redo");
        undoMenu.add(undoMenuItem);
        undoMenu.add(redoMenuItem);
        menu_main.add(undoMenu);

        JMenu menu_groups = new JMenu("Group of Edit...");

        undoMenu.add(menu_groups);

        //For the keyboard shortcut
        KeyStroke keyStrokeToUndo = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK);
        undoMenuItem.setAccelerator(keyStrokeToUndo);

        KeyStroke keyStrokeToRedo = KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK);
        redoMenuItem.setAccelerator(keyStrokeToRedo);

        KeyStroke quit = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK);
        menuitem_quit.setAccelerator(quit);

        //um = new UndoManager();



        // Set attributes of the app window
        area = new JTextArea();
        area.setLineWrap(true);
        scrollPane = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(scrollPane);
        frame.setSize(800, 600);
        frame.setVisible(true);
        area.getDocument().addUndoableEditListener(
                new UndoableEditListener(){
                    public void undoableEditHappened(UndoableEditEvent e){

                        if(!e.getEdit().getPresentationName().equals(previousClass)){
                            previousClass = e.getEdit().getPresentationName();
                            JMenuItem edit = new JMenuItem(e.getEdit().getPresentationName());
                            menu_groups.add(edit);
                            group_counter++;
                        }

                        //um.addEdit(e.getEdit());
                        Edits new_edit = new Edits(e.getEdit(), System.currentTimeMillis(), group_counter);
                        edit.addEdit(new_edit);
                        edit.redoStack.clear();

                    }
                });


        frame.setJMenuBar(menu_main);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String ingest = null;
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose destination.");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        String ae = e.getActionCommand();
        if (ae.equals("Open")) {
            returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File f = new File(jfc.getSelectedFile().getAbsolutePath());

                if(f.getName().contains(".jpg") || f.getName().contains(".png")) {
                    JOptionPane.showMessageDialog(null, "Error, cannot open image");
                }
                else {
                    try {
                        FileReader read = new FileReader(f);
                        Scanner scan = new Scanner(read);
                        while (scan.hasNextLine()) {
                            String line = scan.nextLine() + "\n";
                            ingest = ingest + line;
                        }
                        area.setText(ingest);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            // SAVE
        } else if (ae.equals("Save")) {
            returnValue = jfc.showSaveDialog(null);
            try {
                File f = new File(jfc.getSelectedFile().getAbsolutePath());
                FileWriter out = new FileWriter(f);
                out.write(area.getText());
                out.close();
            } catch (FileNotFoundException ex) {
                Component f = null;
                JOptionPane.showMessageDialog(f,"File not found.");
            } catch (IOException ex) {
                Component f = null;
                JOptionPane.showMessageDialog(f,"Error.");
            }
        } else if (ae.equals("New")) {
            area.setText("");
        } else if (ae.equals("Quit")) { System.exit(0); }

        else if(ae.equals("Undo")){
            edit.undo();
        }
        else if(ae.equals("Redo")){
            edit.redo();
        }
    }
}