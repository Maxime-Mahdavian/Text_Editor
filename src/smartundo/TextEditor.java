package smartundo;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Front-End text editor component
 *
 * Handles displaying the windows and updating them, as well as passing edits to SmartUndoManager
 */
public final class TextEditor extends JFrame implements ActionListener {

    // changed from private static to protected for the test case
    protected JTextArea area;
    protected JFrame frame;
    private static JScrollPane scrollPane;
    private static int returnValue = 0;
    protected JMenu menu_groups;
    protected SmartUndoManager smartUndoManager = new SmartUndoManager(this);
    protected LinkedList<JMenuItem> undoMenuItems;
    protected JFrame undoGroupWindow;
    protected JPanel group_name_panel;
    protected JPanel group_delete_panel;
    protected JPanel group_undo_panel;
    protected JPanel group_content_panel;

    protected JFrame undoEditWindow;
    protected JPanel undoEditWindowPanel;
    protected JScrollPane undoEditWindowScrollPane;

    //List of non supported file extensions that will raise an error if encoutered
    protected final ArrayList<String> non_supported_file_extensions = new ArrayList<>(
            Arrays.asList(".jpg", ".jpeg", ".jpe", ".jif", ".jfif", ".jfi", ".png", ".gif", ".webp",
                    ".tiff", ".tif", ".psd", ".raw", ".arw", ".cr2", ".nrw", "k25", ".bmp", ".dib", ".heif",
                    ".heic", ".ind", ".indd", ".indt", ".jp2", ".j2k", ".jpf", ".jpx", ".jpm", ".mj2", ".svg",
                    ".svgz", ".ai", ".eps", ".pdf"));


    /**
     * Constructor
     *
     * Sets up basic configuration for windows, both the main, the undoGroup window and the undoEdit Window.
     */
    public TextEditor() {
        undoMenuItems = new LinkedList<>();
        undoGroupWindow = new JFrame("Undo Menu");
        undoGroupWindow.setLocationRelativeTo(frame);
        undoGroupWindow.setVisible(false);
        group_name_panel = new JPanel();
        group_name_panel.setVisible(false);
        group_delete_panel = new JPanel();
        group_delete_panel.setVisible(false);
        group_undo_panel = new JPanel();
        group_undo_panel.setVisible(false);
        group_content_panel = new JPanel();
        group_content_panel.setVisible(false);
        undoGroupWindow.setResizable(false);

        //To keep everything organized, the undoWindow is divided into three columns, one for each element
        //in the window (label, undo, delete). The three components are then added to the main panel.
        group_content_panel.setLayout(new GridLayout(0,3,5,5));
        group_content_panel.add(group_name_panel);
        group_content_panel.add(group_undo_panel);
        group_content_panel.add(group_delete_panel);
        undoGroupWindow.add(group_content_panel);
        undoGroupWindow.setSize(350, 500);


        //the edit window needs to scroll, so the config is a little different, but essentially we just need to add
        //a scroll pane
        undoEditWindow = new JFrame("Undo Edit");
        undoEditWindow.setLocationRelativeTo(frame);
        undoEditWindow.setVisible(false);

        undoEditWindowPanel = new JPanel();
        undoEditWindowPanel.setVisible(false);
        undoEditWindowPanel.setLayout(new GridLayout(0,3,5,5));
        undoEditWindowScrollPane = new JScrollPane(undoEditWindowPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        undoEditWindowScrollPane.setBorder(BorderFactory.createEmptyBorder());
        undoEditWindowScrollPane.setVisible(false);
        undoEditWindow.setSize(400, 500);
        undoEditWindow.add(undoEditWindowScrollPane);
        //undoEditWindow.add(undoEditWindowPanel);
        undoEditWindow.setResizable(false);

        run();
    }

    /**
     * run(): main function for the text editor, this is what is continually running while the application is executing
     */
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

        //menu_groups = new JMenu("Group of Edit...");

        //undoMenu.add(menu_groups);

        //For the keyboard shortcuts
        KeyStroke keyStrokeToUndo = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK);
        undoMenuItem.setAccelerator(keyStrokeToUndo);

        KeyStroke keyStrokeToRedo = KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK);
        redoMenuItem.setAccelerator(keyStrokeToRedo);

        KeyStroke quit = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK);
        menuitem_quit.setAccelerator(quit);

        KeyStroke keyStrokeToOpen = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
        menuitem_open.setAccelerator(keyStrokeToOpen);

        KeyStroke keyStrokeToNew = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);
        menuitem_new.setAccelerator(keyStrokeToNew);

        KeyStroke keyStrokeToSave = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
        menuitem_save.setAccelerator(keyStrokeToSave);

        //Font size configuration
        JMenu font_size = new JMenu("Font Size");
        JMenuItem font8 = new JMenuItem("8");
        JMenuItem font10 = new JMenuItem("10");
        JMenuItem font12 = new JMenuItem("12");
        JMenuItem font14 = new JMenuItem("14");
        JMenuItem font16 = new JMenuItem("16");
        JMenuItem font20 = new JMenuItem("20");

        font8.addActionListener(this);
        font10.addActionListener(this);
        font12.addActionListener(this);
        font14.addActionListener(this);
        font16.addActionListener(this);
        font20.addActionListener(this);
        font8.setActionCommand("Font8");
        font10.setActionCommand("Font10");
        font12.setActionCommand("Font12");
        font14.setActionCommand("Font14");
        font16.setActionCommand("Font16");
        font20.setActionCommand("Font20");


        font_size.add(font8);
        font_size.add(font10);
        font_size.add(font12);
        font_size.add(font14);
        font_size.add(font16);
        font_size.add(font20);

        menu_main.add(font_size);

        //Group management configuration
        JMenuItem undoGroupMenu = new JMenuItem("Undo Group");
        undoGroupMenu.addActionListener(this);
        undoGroupMenu.setActionCommand("Undo Group");
        undoMenu.add(undoGroupMenu);

        JMenuItem undoEditMenu = new JMenuItem("Undo Edit");
        undoEditMenu.addActionListener(this);
        undoEditMenu.setActionCommand("Edit Window");
        undoMenu.add(undoEditMenu);


        // Set attributes of the app window
        area = new JTextArea();
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        scrollPane = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(scrollPane);
        frame.setSize(800, 600);
        frame.setVisible(true);

        //Add an ActionListener that listens to any changes in the JTextArea, or the document itself
        area.getDocument().addUndoableEditListener(
                new UndoableEditListener(){
                    public void undoableEditHappened(UndoableEditEvent e){
                        //um.addEdit(e.getEdit());
                        Edits new_edit = new Edits(e.getEdit(), System.currentTimeMillis());
                        smartUndoManager.addEdit(new_edit);
                        //We clear the redo stack after every edit, since we don't want to redo over existing text
                        smartUndoManager.redoStack.clear();
                    }
                });

        frame.setJMenuBar(menu_main);
    }

    /**
     * @param e Most recent action performed by the user
     *
     *  This determines what happens when the user clicks on any buttons
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String ingest = null;
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose destination.");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        String ae = e.getActionCommand();
        //OPEN or CTRL-O
        if (ae.equals("Open")) {
            returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File f = new File(jfc.getSelectedFile().getAbsolutePath());

                String filename = f.getName().substring(f.getName().indexOf('.'));
                if(non_supported_file_extensions.contains(filename)) {
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
                    smartUndoManager.reset();
                }
            }
            // SAVE or Ctrl-S
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
            catch(NullPointerException ex){
                Component f = null;
            }
            //NEW button or Ctrl-N
        } else if (ae.equals("New")) {
            area.setText("");
            smartUndoManager.reset();

            //Quit button or CTRL-Q
        } else if (ae.equals("Quit")) { System.exit(0); }

        //Undo button or CTRL-Z
        else if(ae.equals("Undo")){
            smartUndoManager.undo();
        }

        //Redo button or CTRL-Y
        else if(ae.equals("Redo")){
            smartUndoManager.redo();
        }

        //The following options are to set font sizes
        else if(ae.equals("Font8")){
            setFont(8);
        }
        else if(ae.equals("Font10")){
            setFont(10);
        }
        else if(ae.equals("Font12")){
            setFont(12);
        }
        else if(ae.equals("Font14")){
            setFont(14);
        }
        else if(ae.equals("Font16")){
            setFont(16);
        }
        else if(ae.equals("Font20")){
            setFont(20);
        }

        //If the action has a numeric name, then it is the number of the group the user wishes to undo
        //So we call the undoGroup Function from SmartUndoManager
        else if(isNumeric(ae)){
            //System.out.println(edit.undoStack.toString());
            smartUndoManager.undoGroup(ae);
        }
        //This option means that the user wants to delete the undo group.
        //We take the last part of the string that contains the group number
        else if(ae.startsWith("Delete")){
            String group = ae.substring(6);
            smartUndoManager.deleteUndoGroup(group);

        }
        //Since the window elements are already created, then we can only set them to be visible
        else if(ae.equals("Undo Group")){
            undoGroupWindow.setVisible(true);
            group_content_panel.setVisible(true);
            group_name_panel.setVisible(true);
            group_undo_panel.setVisible(true);
            group_delete_panel.setVisible(true);
        }
        //Once again, the window elements are already created so we set them to be visible
        else if(ae.equals("Edit Window")){
            undoEditWindow.setVisible(true);
            undoEditWindowPanel.setVisible(true);
            undoEditWindowScrollPane.setVisible(true);
        }
        //The format of this command is EditDelete<Time> so we pull the time from the string and
        //send it to smartUndoManager
        else if(ae.startsWith("EditDelete")){
            String time_of_edit = ae.substring(10);
            smartUndoManager.deleteEdit(time_of_edit);
        }
        //The format here is Edit<Time>, so it extracts the time and sends it to smartUndoManager.
        else if(ae.startsWith("Edit")){
            String time_of_edit = ae.substring(4);
            smartUndoManager.undoEdit(time_of_edit);
        }
    }

    /**
     * @return Text Editor ActionListener
     *
     * Used by functions following this one to create buttons using the same ActionListener as everything else
     * in the text editor.
     */
    public ActionListener getActionListener(){
        return this;
    }

    /**
     * @param str A string that might be a number
     * @return True if str is a long, false otherwise.
     *
     * Used to detect if the ActionListener name is a number, which means the user wants to undo a group of edits.
     */
    public boolean isNumeric(String str){

        try{
            Long.parseLong(str);
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * @param size Font size
     *
     *  Sets the font size to size
     */
    public void setFont(int size){
        area.setFont(new Font("Arial", Font.PLAIN, size));
    }


    /**
     * @param edit Edit to be added to undoWindow
     * @param group Group number of the edit
     *
     *  Add a row to UndoWindow containing an edit
     */
    public void addGroupElements(Edits edit, long group){

        JButton label = new JButton(edit.getEdit().getPresentationName());
        JButton undo_button = new JButton("Undo Group");
        JButton delete_button = new JButton("Delete Group");
        label.setSize(100,20);
        label.setEnabled(false);
        undo_button.setSize(100,20);
        delete_button.setSize(100, 20);
        group_name_panel.add(label);
        group_undo_panel.add(undo_button);
        group_delete_panel.add(delete_button);
        undo_button.addActionListener(getActionListener());
        delete_button.addActionListener(getActionListener());
        undo_button.setActionCommand(Long.toString(group));
        delete_button.setActionCommand("Delete" + Long.toString(group));
    }


    /**
     * Remove every row from the undoWindow, essentially clearing all contents
     * For the panels containing these elements, we first need to set them to invisible,
     * then remove elements then invalidate and revalidate the panel to make it interactable again.
     * Finally, we set the panel to be visible once again
     */
    public void removeAllGroupElements(){
        group_name_panel.setVisible(false);
        group_name_panel.removeAll();
        group_name_panel.invalidate();
        group_name_panel.revalidate();
        group_name_panel.setVisible(true);
        group_undo_panel.setVisible(false);
        group_undo_panel.removeAll();
        group_undo_panel.invalidate();
        group_undo_panel.revalidate();
        group_undo_panel.setVisible(true);
        group_delete_panel.setVisible(false);
        group_delete_panel.removeAll();
        group_delete_panel.invalidate();
        group_delete_panel.revalidate();
        group_delete_panel.setVisible(true);
    }


    /**
     * Remove the first element in the undoWindow, clearing the oldest edit in this window.
     */
    public void removeFirstGroupElement(){
        group_name_panel.remove(0);
        group_name_panel.invalidate();
        group_name_panel.revalidate();
        group_undo_panel.remove(0);
        group_undo_panel.invalidate();
        group_undo_panel.revalidate();
        group_delete_panel.remove(0);
        group_delete_panel.invalidate();
        group_delete_panel.revalidate();
    }

    /**
     * Adds a row to the edit window comprising of a new edit.
     *
     * @param edit Edit to be added to the window
     * @param time Time of the edit, used to tell them apart
     */
    public void addEditElements(Edits edit, long time){
        JButton label = new JButton(edit.getEdit().getPresentationName());
        JButton undo_button = new JButton("Undo Edit");
        JButton delete_button = new JButton("Delete Edit");
        label.setSize(100,20);
        label.setEnabled(false);
        undo_button.setSize(100,20);
        delete_button.setSize(100, 20);
        undoEditWindowPanel.add(label);
        undoEditWindowPanel.add(undo_button);
        undoEditWindowPanel.add(delete_button);
        undo_button.addActionListener(getActionListener());
        delete_button.addActionListener(getActionListener());
        undo_button.setActionCommand("Edit" + Long.toString(time));
        delete_button.setActionCommand("EditDelete" + Long.toString(time));
    }

    /**
     * Clears the edit window.s
     */
    public void removeAllEditElements(){
        undoEditWindowPanel.setVisible(false);
        undoEditWindowPanel.removeAll();
        undoEditWindowPanel.invalidate();
        undoEditWindowPanel.revalidate();
        undoEditWindowPanel.setVisible(true);
    }
}