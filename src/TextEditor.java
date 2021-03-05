package smartundo;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import static smartundo.SmartUndoManager.editStrings;

public final class TextEditor extends JFrame implements ActionListener {

	private static JTextArea area;
	private static JFrame frame;
	private static int returnValue = 0;
	private SmartUndoManager manager = new SmartUndoManager();
	private LinkedList<JMenuItem> edits;
	private JMenu menu_groups;

	public TextEditor() {
		edits = new LinkedList<>();
		run();
	}

	public void run() {
		// Set the look-and-feel (LNF) of the application
		// Try to default to whatever the host system prefers
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
		}

		// Set attributes of the app window
		area = new JTextArea();
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(area);
		frame.setVisible(true);

		// Build the menu
		JMenuBar menu_main = new JMenuBar();

		JMenu menu_file = new JMenu("File");
		JMenu menu_edit = new JMenu("Edit");
		menu_groups = new JMenu("Group of Edit...");

		JMenuItem menuitem_new = new JMenuItem("New");
		JMenuItem menuitem_open = new JMenuItem("Open");
		JMenuItem menuitem_save = new JMenuItem("Save");
		JMenuItem menuitem_quit = new JMenuItem("Quit");
		JMenuItem menuitem_undo = new JMenuItem("Undo");
		JMenuItem menuitem_redo = new JMenuItem("Redo");

		menuitem_undo.setEnabled(manager.canUndo());
		menuitem_redo.setEnabled(manager.canRedo());
		area.getDocument().addUndoableEditListener(manager);
		area.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent arg) {
			}

			public void insertUpdate(DocumentEvent arg) {
				try {
					int offset = arg.getOffset();
					int length = arg.getLength();
					String text = arg.getDocument().getText(offset, length);
					if (text.contains("\n") || edits.size() == 0) {
						text = text.equals("\n") ? "" : text;
						JMenuItem edit = new JMenuItem(text);
						menu_groups.add(edit);
						edits.add(edit);
						updateListeners();
					} else {
						edits.getLast().setText(edits.getLast().getText() + text);
					}
					menuitem_undo.setEnabled(manager.canUndo());
					menuitem_redo.setEnabled(manager.canRedo());
				} catch (BadLocationException e) {
					e.printStackTrace();
				}

			}

			public void removeUpdate(DocumentEvent arg) {
				menuitem_undo.setEnabled(manager.canUndo());
				menuitem_redo.setEnabled(manager.canRedo());
			}
		}
		);

		menuitem_new.addActionListener(this);
		menuitem_open.addActionListener(this);
		menuitem_save.addActionListener(this);
		menuitem_quit.addActionListener(this);
		menuitem_undo.addActionListener(this);
		menuitem_redo.addActionListener(this);

		menu_main.add(menu_file);
		menu_main.add(menu_edit);

		menu_file.add(menuitem_new);
		menu_file.add(menuitem_open);
		menu_file.add(menuitem_save);
		menu_file.add(menuitem_quit);
		menu_edit.add(menuitem_undo);
		menu_edit.add(menuitem_redo);
		menu_edit.add(menu_groups);

		frame.pack();
		frame.setSize(640, 480);

		frame.setJMenuBar(menu_main);

	}

	public void updateListeners() {
		edits.getLast().addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String ingest = null;
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle("Choose destination.");
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		String ae = e.getActionCommand();
		for (int i = 0; i < edits.size(); i++) {
			if (ae.equals(edits.get(i).getText())) {
				manager.undo(i);
				menu_groups.remove(edits.get(i));
				edits.remove(i);
			}
		}
		if (ae.equals("Open")) {
			returnValue = jfc.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File f = new File(jfc.getSelectedFile().getAbsolutePath());
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
				JOptionPane.showMessageDialog(f, "File not found.");
			} catch (IOException ex) {
				Component f = null;
				JOptionPane.showMessageDialog(f, "Error.");
			}
		} else if (ae.equals("New")) {
			area.setText("");
		} else if (ae.equals("Quit")) {
			System.exit(0);
		} else if (ae.equals("Undo")) {
			try {
				manager.undo();
				menu_groups.remove(edits.getLast());
				edits.removeLast();
			} catch (CannotUndoException exception) {
				exception.printStackTrace();
			}
		} else if (ae.equals("Redo")) {
			manager.redo();
		}
	}
}
