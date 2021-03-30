package smartundo;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TextEditor extends JFrame implements ActionListener {

    private static JTextArea area;
    private static JFrame frame;
    private static JScrollPane scrollPane;
    private static int returnValue = 0;
    JMenu menu_groups;
    UndoManager um;
    SmartUndoManager edit = new SmartUndoManager(this);
    LinkedList<JMenuItem> undoMenuItems;
    private final static ArrayList<String> non_supported_file_extensions = new ArrayList<>(
            Arrays.asList(".jpg", ".jpeg", ".jpe", ".jif", ".jfif", ".jfi", ".png", ".gif", ".webp",
                    ".tiff", ".tif", ".psd", ".raw", ".arw", ".cr2", ".nrw", "k25", ".bmp", ".dib", ".heif",
                    ".heic", ".ind", ".indd", ".indt", ".jp2", ".j2k", ".jpf", ".jpx", ".jpm", ".mj2", ".svg",
                    ".svgz", ".ai", ".eps", ".pdf"));
    //feel free to add whatever you want, no clue how far you want to go with this


    public TextEditor() { undoMenuItems = new LinkedList<>(); run(); }

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

        menu_groups = new JMenu("Group of Edit...");

        undoMenu.add(menu_groups);

        //For the keyboard shortcut
        KeyStroke keyStrokeToUndo = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK);
        undoMenuItem.setAccelerator(keyStrokeToUndo);

        KeyStroke keyStrokeToRedo = KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK);
        redoMenuItem.setAccelerator(keyStrokeToRedo);

        KeyStroke quit = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK);
        menuitem_quit.setAccelerator(quit);

        //um = new UndoManager();

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

        undoMenu.add(font_size);


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
        area.getDocument().addUndoableEditListener(
                new UndoableEditListener(){
                    public void undoableEditHappened(UndoableEditEvent e){
                        //um.addEdit(e.getEdit());
                        Edits new_edit = new Edits(e.getEdit(), System.currentTimeMillis());
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
                    edit.emptyStacks();
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
        else if(isNumeric(ae)){
            //System.out.println(edit.undoStack.toString());
            edit.undoGroup(ae);
        }
    }

    public ActionListener getActionListener(){
        return this;
    }

    public boolean isNumeric(String str){

        try{
            Long.parseLong(str);
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }

    public void addGroupMenu(Edits edit, long group_counter){
        JMenuItem newedit = new JMenuItem(edit.getEdit().getPresentationName());
        menu_groups.add(newedit);
        newedit.addActionListener(getActionListener());
        newedit.setActionCommand(Long.toString(group_counter));
        undoMenuItems.add(newedit);
    }


    public void setFont(int size){
        area.setFont(new Font("Arial", Font.PLAIN, size));
    }


}