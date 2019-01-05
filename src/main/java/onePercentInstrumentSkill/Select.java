package onePercentInstrumentSkill;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.*;
import javax.swing.filechooser.*;

public class Select extends JPanel implements ActionListener {
    JButton fileButton; // button for select file
    JButton folderButton; // button for select folder
    JButton run; // button for run
    JTextArea fileText; // show file path
    JTextArea folderText; // show folder path
    JFileChooser file; // choose file
    JFileChooser folder; // choose folder
    private int fontSize = 18; // GUI font size
    private static String filePath, folderPath; // path for file and folder
    private final PrintWriter exPrinter;
    // ctor
    public Select() throws FileNotFoundException {
        super(new BorderLayout());
        exPrinter = new PrintWriter(new File("./tmp/SelectException.txt"));
        // create chooser
        file = new JFileChooser();
        folder = new JFileChooser();
        folder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // that it can choose folder
        // file text area
        fileText = new JTextArea(2, 30);
        fileText.setMargin(new Insets(5, 5, 5, 5));
        fileText.setEditable(false);
        fileText.setLineWrap(true);
        fileText.setFont(new Font("Microsoft JhengHei", Font.PLAIN, fontSize));
        JScrollPane fileTextPane = new JScrollPane(fileText);
        // folder text area
        folderText = new JTextArea(2, 30);
        folderText.setMargin(new Insets(5, 5, 5, 5));
        folderText.setEditable(false);
        folderText.setLineWrap(true);
        folderText.setFont(new Font("Microsoft JhengHei", Font.PLAIN, fontSize));
        JScrollPane folderTextPane = new JScrollPane(folderText);
        // new button for select file
        fileButton = new JButton("Select midi file");
        fileButton.setFont(new Font("Microsoft JhengHei", Font.PLAIN, fontSize));
        fileButton.addActionListener(this);
        JPanel filePanel = new JPanel();
        filePanel.add(fileButton);
        filePanel.add(fileTextPane);
        // new button for select folder
        folderButton = new JButton("  Select folder  ");
        folderButton.setFont(new Font("Microsoft JhengHei", Font.PLAIN, fontSize));
        folderButton.addActionListener(this);
        JPanel folderPanel = new JPanel();
        folderPanel.add(folderButton);
        folderPanel.add(folderTextPane);
        // new button for run
        run = new JButton("Run");
        run.setFont(new Font("Microsoft JhengHei", Font.PLAIN, fontSize));
        run.addActionListener(this);
        JPanel runButtonPanel = new JPanel();
        runButtonPanel.add(run);
        // add panel
        JPanel showPanel = new JPanel();
        showPanel.add(filePanel);
        showPanel.add(folderPanel);
        showPanel.add(runButtonPanel);
        add(showPanel, BorderLayout.CENTER);
    }
    // wait event and do something
    public void actionPerformed(ActionEvent e) {
        // when click select file button
        if (e.getSource() == fileButton) {
            int returnValue = file.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                filePath = file.getSelectedFile().toString(); // assign file path
                fileText.setText(filePath);
            }
            fileText.setCaretPosition(fileText.getDocument().getLength());
        }
        // when click select folder button
        if (e.getSource() == folderButton) {
            int returnValue = folder.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                folderPath = folder.getSelectedFile().toString(); // assign folder path
                folderText.setText(folderPath);
            }
            folderText.setCaretPosition(folderText.getDocument().getLength());
        }
        // when click run button
        if (e.getSource() == run) {
            JLabel label = new JLabel("You should have 5G FREE MEMORY AT LEAST!");
            label.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 30));
            JOptionPane.showMessageDialog(null, label, "ALERT", JOptionPane.WARNING_MESSAGE);
            // TODO
            if(filePath != null && folderPath != null) {
                System.out.println(filePath); // filePath
                System.out.println(folderPath); // folderPath
                
                Thread myThread = new MainThread();
                myThread.start();
            }
        }
    }
    // run GUI
    public void GUI() {
        JFrame frame = new JFrame("OnePercentInstrumentSkill"); // GUI title
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setSize(new Dimension(750, 270)); // GUI size
        frame.setVisible(true); // keep show
    }
    
	
	private static class MainThread extends Thread{
		public MainThread(){}
		
		@Override
		public void run() {
			try {
				OnePercentInstrumentSkill.start();
			} catch (InvalidMidiDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}