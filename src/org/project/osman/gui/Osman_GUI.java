package org.project.osman.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import org.project.osman.process.OsmanReadability;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JLabel;

//Used Action Listener for JMenuItem &amp; JRadioButtonMenuItem
//Used Item Listener for JCheckBoxMenuItem
public class Osman_GUI extends JFrame implements ActionListener, ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextArea jtAreaOutput;
	JScrollPane jspPane;
	JMenuBar mainMenuBar;
	JMenu menu1, menu2, submenu;
	JMenuItem selectFile, clearMenu, exitMenu, saveOutput;
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;
	JLabel readScore = new JLabel();
	JLabel numWords = new JLabel();
	JLabel numSent = new JLabel();
	JLabel numSylbls = new JLabel();
	JLabel numFaseeh = new JLabel();
	JButton calcOsman = new JButton();
	JButton saveBtn = new JButton("Save");
	JButton clear = new JButton();
	JButton exit = new JButton();
	JComboBox<String> metricList;
	// create an instance of the OsmanReadability classs
	static OsmanReadability osman = new OsmanReadability();

	public JMenuBar createJMenuBar() {

		String[] metrics = { "Select Readability", "OSMAN", "Flesch_Ar", "Kincaid_Ar", "Fog_Ar", "ARI_Ar", "LIX_Ar" };

		// Create the combo box, select item at index 4.
		// Indices start at 0, so 4 specifies the pig.
		metricList = new JComboBox<String>(metrics);
		metricList.setSelectedIndex(0);
		metricList.addActionListener(this);

		mainMenuBar = new JMenuBar();
		menu1 = new JMenu("File");
		menu1.setMnemonic(KeyEvent.VK_M);
		mainMenuBar.add(menu1);
		// Creating the MenuItems
		selectFile = new JMenuItem("Select File", KeyEvent.VK_T);
		saveOutput = new JMenuItem("Save Output", KeyEvent.VK_T);
		// can be done either way for assigning shortcuts
		// menuItem.setMnemonic(KeyEvent.VK_T);
		// Accelerators, offer keyboard shortcuts to bypass navigating
		// the menu hierarchy.
		selectFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		selectFile.addActionListener(this);
		menu1.add(selectFile);
		saveOutput.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
		saveOutput.addActionListener(this);
		menu1.add(saveOutput);

		clearMenu = new JMenuItem("Clear");
		clearMenu.setMnemonic(KeyEvent.VK_B);
		clearMenu.addActionListener(this);
		menu1.add(clearMenu);
		this.mainMenuBar.add(this.menu1); // add it to the "File" menu

		exitMenu = new JMenuItem("Exit");
		exitMenu.setMnemonic(KeyEvent.VK_C);
		exitMenu.addActionListener(this);
		menu1.add(exitMenu);
		this.mainMenuBar.add(this.menu1); // add it to the "File" menu

		calcOsman.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String tashkeelText = "";
				try {
					tashkeelText = osman.addTashkeel(new String(jtAreaOutput.getText().getBytes("UTF-8")));

				} catch (InterruptedException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				double score = 0.0;
				// String[] metrics = { "Select Readability", "OSMAN",
				// "Flesch_Ar", "Fog_Ar", "ARI_Ar", "LIX_Ar" };

				String selectedMetric = metricList.getSelectedItem().toString();
				switch (selectedMetric) {
				case "OSMAN":
					score = osman.calculateOsman(tashkeelText);
					break;
				case "Flesch_Ar":
					score = osman.calculateArabicFlesch(tashkeelText);
					break;
				case "Kincaid_Ar":
					score = osman.calculateArabicKincaid(tashkeelText);
					break;
				case "Fog_Ar":
					score = osman.calculateArabicFog(tashkeelText);
					break;
				case "ARI_Ar":
					score = osman.calculateArabicARI(tashkeelText);
					break;
				case "LIX_Ar":
					score = osman.calculateArabicLIX(tashkeelText);
					break;
				default:
					score = osman.calculateOsman(tashkeelText);
				}
				readScore.setText(metricList.getSelectedItem().toString() + " Score = " + score);
				numSent.setText("\t Num of Sentences = " + osman.countSentences(jtAreaOutput.getText()));
				numWords.setText("\t Num of Words = " + osman.countWords(jtAreaOutput.getText()));
				numFaseeh.setText("\t Num of Faseeh = " + osman.countFaseeh(jtAreaOutput.getText()));
				numSylbls.setText("\t Num of Syllabels = " + osman.countSyllables(tashkeelText));
				jtAreaOutput.setText(tashkeelText);

			}
		});

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);

			}
		});
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jtAreaOutput.setText("");
				readScore.setText("");
				numSent.setText("");
				numWords.setText("");
				numSylbls.setText("");
				numFaseeh.setText("");

			}
		});

		saveBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				// File file = new
				// File("output"+System.currentTimeMillis()+".txt");
				String output = "";
				output += jtAreaOutput.getText() + "\t" + "\n" + "\r";
				output += readScore.getText() + "\t" + "\n" + "\r";
				output += numSent.getText() + "\t" + "\n" + "\r";
				output += numWords.getText() + "\t" + "\n" + "\r";
				output += numSylbls.getText() + "\t" + "\n" + "\r";
				output += numFaseeh.getText() + "\t" + "\n" + "\r";

				String file = "output" + System.currentTimeMillis() + ".txt";
				try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
					out.print(output);
					out.flush();
					out.close();

					Desktop.getDesktop().open(new File(file));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});

		// Build second menu in the menu bar.

		return mainMenuBar;
	}

	public Container createContentPane() {
		// Create the content-pane-to-be.
		JPanel jplContentPane = new JPanel(new BorderLayout());
		JPanel buttonsPanel = new JPanel(new FlowLayout());
		JPanel lebelsPanel = new JPanel(new FlowLayout());
		JPanel groupPanel = new JPanel(new BorderLayout());

		jplContentPane.setLayout(new BorderLayout());// Can do it either way
		// to set layout
		jplContentPane.setOpaque(true);
		// Create a scrolled text area.
		calcOsman.setText("Calculate Readabitly");
		saveBtn.setText("Save output");
		clear.setText("Clear");
		exit.setText("Exit");

		jtAreaOutput = new JTextArea(5, 10);
		jtAreaOutput.setLineWrap(true);
		jtAreaOutput.setEditable(true);
		jspPane = new JScrollPane(jtAreaOutput);
		jtAreaOutput.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		jspPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		// Add the text area to the content pane.
		jplContentPane.add(jspPane, BorderLayout.CENTER);
		lebelsPanel.add(readScore);
		lebelsPanel.add(numWords);
		lebelsPanel.add(numSent);
		lebelsPanel.add(numSylbls);
		lebelsPanel.add(numFaseeh);
		buttonsPanel.add(metricList);
		buttonsPanel.add(calcOsman);
		buttonsPanel.add(saveBtn);
		buttonsPanel.add(clear);
		buttonsPanel.add(exit);
		groupPanel.add(lebelsPanel, BorderLayout.NORTH);
		groupPanel.add(buttonsPanel, BorderLayout.SOUTH);
		jplContentPane.add(groupPanel, BorderLayout.SOUTH);

		return jplContentPane;
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = Osman_GUI.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find image file: " + path);
			return null;
		}
	}

	private static void createGUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		// Create and set up the window.
		JFrame frame = new JFrame("OSMAN Arabic Readabililty Metric");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Osman_GUI app = new Osman_GUI();
		frame.setJMenuBar(app.createJMenuBar());
		frame.setContentPane(app.createContentPane());
		frame.setSize(750, 400);
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		jtAreaOutput.setCaretPosition(jtAreaOutput.getDocument().getLength());

		if (e.getSource() == this.selectFile) {
			JFileChooser open = new JFileChooser(); // open up a file chooser (a
													// dialog for the user to
													// browse files to open)
			open.setCurrentDirectory(new File(System.getProperty("user.home")));
			int option = open.showOpenDialog(this); // get the option that the
													// user selected (approve or
													// cancel)
			// NOTE: because we are OPENing a file, we call showOpenDialog~
			// if the user clicked OK, we have "APPROVE_OPTION"
			// so we want to open the file
			if (option == JFileChooser.APPROVE_OPTION) {
				this.jtAreaOutput.setText(""); // clear the TextArea before
												// applying the file contents
				try {
					// create a scanner to read the file
					// (getSelectedFile().getPath() will get the path to the
					// file)
					@SuppressWarnings("resource")
					Scanner scan = new Scanner(new FileReader(open.getSelectedFile().getPath()));
					while (scan.hasNext()) // while there's still something to
											// read
						this.jtAreaOutput.append(scan.nextLine() + "\n"); // append
																			// the
																			// line
																			// to
																			// the
																			// TextArea
				} catch (Exception ex) { // catch any exceptions, and...
					// ...write to the debug console
					System.out.println(ex.getMessage());
				}
			}
		}

		if (e.getSource() == this.clearMenu) {
			jtAreaOutput.setText("");
			readScore.setText("");
			numSent.setText("");
			numWords.setText("");
			numSylbls.setText("");
			numFaseeh.setText("");

		}

		if (e.getSource() == this.exitMenu) {
			System.exit(0);

		}

		if (e.getSource() == this.saveOutput) {

			// File file = new File("output"+System.currentTimeMillis()+".txt");
			String output = "";
			output += jtAreaOutput.getText() + "\t" + "\n" + "\r";
			output += readScore.getText() + "\t" + "\n" + "\r";
			output += numSent.getText() + "\t" + "\n" + "\r";
			output += numWords.getText() + "\t" + "\n" + "\r";
			output += numSylbls.getText() + "\t" + "\n" + "\r";
			output += numFaseeh.getText() + "\t" + "\n" + "\r";

			String file = "output" + System.currentTimeMillis() + ".txt";
			try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
				out.print(output);
				out.flush();
				out.close();

				Desktop.getDesktop().open(new File(file));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}

	// Returns the class name, no package info
	protected String getClassName(Object o) {
		String classString = o.getClass().getName();
		int dotIndex = classString.lastIndexOf(".");
		return classString.substring(dotIndex + 1); // Returns only Class name
	}

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException, InterruptedException {
		osman.loadData();

		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				createGUI();
			}
		});
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub

	}
}