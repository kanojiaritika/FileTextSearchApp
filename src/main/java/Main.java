import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static org.apache.pdfbox.Loader.loadPDF;

public class Main {

    public static void main(String[] args) {
        final JFrame frame = new JFrame("File Text Search App"); // Window title
        final JTextField textField = new JTextField(); // Input field
        final JTextArea textLine = new JTextArea(); // Result display
        JButton folderButton = new JButton("SEARCH ...");
        final JFileChooser chooseFolder = new JFileChooser();
        final String[] userText = new String[1];

        // UI layout
        textField.setBounds(150, 150, 200, 30);
        folderButton.setBounds(150, 190, 120, 40);
        textLine.setBounds(150, 240, 300, 200);
        textLine.setLineWrap(true);
        textLine.setWrapStyleWord(true);

        // Search Button Function
        folderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userText[0] = textField.getText().trim(); // User input text (trimmed of spaces)
                chooseFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Only folder selection allowed

                int check = chooseFolder.showOpenDialog(frame); // Opens folder chooser dialog and stores user response

                // If user clicks "Open" (and does not cancel the dialog)
                if (check == JFileChooser.APPROVE_OPTION) {
                    File selectedFolder = chooseFolder.getSelectedFile(); // Folder selected by the user
                    File[] files = selectedFolder.listFiles(); // Get all files from the selected folder
                    boolean anyMatch = false;

                    textLine.setText(""); // Clear previous search results

                    // Iterate through all files in the folder
                    for (File file : files) {
                        boolean foundInThisFile = false;

                        // For .txt files
                        if (file.isFile() && file.getName().endsWith(".txt")) {
                            try {
                                Scanner myReader = new Scanner(file); // Create a scanner to read the file

                                while (myReader.hasNextLine()) { // Loop through each line
                                    String line = myReader.nextLine();
                                    if (line.contains(userText[0])) { // Check if line contains user input
                                        if (!foundInThisFile) {
                                            textLine.append("Found in: " + file.getName() + "\n");
                                            textLine.append("File path: " + file.getAbsolutePath() + " \n");
                                            foundInThisFile = true;
                                            anyMatch = true;
                                        }
                                        textLine.append("Complete Line: " + line + "\n");
                                    }
                                }

                                myReader.close(); // Close scanner
                            } catch (FileNotFoundException ex) {
                                ex.printStackTrace();
                            }
                        }

                        // For .pdf files (use external library PDFBox)
                        else if (file.isFile() && file.getName().endsWith(".pdf")) {
                            try {
                                PDDocument pdfFile = loadPDF(file); // Load the PDF file
                                PDFTextStripper stripper = new PDFTextStripper(); // Create stripper to extract text
                                String pdfContent = stripper.getText(pdfFile); // Extract content
                                pdfFile.close(); // Close the document

                                String[] lines = pdfContent.split("\\r?\\n"); // Split content into lines
                                for (String line : lines) {
                                    if (line.contains(userText[0])) {
                                        if (!foundInThisFile) {
                                            textLine.append("Found in: " + file.getName() + "\n");
                                            textLine.append("File path: " + file.getAbsolutePath() + " \n");
                                            foundInThisFile = true;
                                            anyMatch = true;
                                        }
                                        textLine.append("Complete Line: " + line + "\n");
                                    }
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    // If no file matched the user input
                    if (!anyMatch) {
                        textLine.append("No file contains the string: " + userText[0]);
                    }
                }
            }
        });

        // Add components to the frame
        frame.add(textField);
        frame.add(folderButton);
        frame.add(textLine);

        frame.setSize(600, 500); // Set window size
        frame.setLayout(null);   // Absolute positioning
        frame.setVisible(true);  // Make window visible
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit app on close
    }
}
