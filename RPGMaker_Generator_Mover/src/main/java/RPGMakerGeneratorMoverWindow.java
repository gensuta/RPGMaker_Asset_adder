import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

//This is the main window that opens up for the tool
//I (Geneva/gensuta) am using a community version of IntelliJ + Swing UI Designer to actually design the window

public class RPGMakerGeneratorMoverWindow extends JFrame {
    //Swing objects added from the UI Designer
    private JButton preferencesButton;
    private JButton aboutButton;
    private JButton helpButton;
    private JPanel MainPanel;
    private JPanel filePanel;
    private JTextArea files_added_text_area;
    private JButton moveGenerateAssetsButton;
    private JTextField generator_path_field;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JPanel draggedPanel;
    private JCheckBox replaceCheckBox;
    private JButton clearFilesFoldersButton;
    private JPanel bodyTypePanel;

    //body type buttons
    private ButtonGroup bodyTypeButtonGroup = new ButtonGroup();
    private JRadioButton allRadioButton;
    private JRadioButton maleRadioButton;
    private JRadioButton femaleRadioButton;
    private JRadioButton kidRadioButton;

    // files that were dragged into the yellow box
    private final ArrayList<File> filesToBeMoved = new ArrayList<>();

    //string builder containing success/error messages for the results screen
    private final StringBuilder resultsLog = new StringBuilder();

    SaveDataHandler saveDataHandler = new SaveDataHandler();

    public RPGMakerGeneratorMoverWindow() {

        //normal frame initialization stuff
        setTitle("RPGMaker Generator Mover");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(MainPanel);
        setVisible(true);

        // adding body types to bodyTypeButton Group
        bodyTypeButtonGroup.add(allRadioButton);
        bodyTypeButtonGroup.add(maleRadioButton);
        bodyTypeButtonGroup.add(femaleRadioButton);
        bodyTypeButtonGroup.add(kidRadioButton);


        // file drag and drop handling from Robert Harder, rharder@usa.net
        new FileDrop(filePanel, files -> {
            for (File file : files) {
                try {
                    files_added_text_area.append(file.getCanonicalPath() + "\n");
                    filesToBeMoved.add(file);
                }   // end try
                catch (java.io.IOException ignored) {
                }
            }   // end for: through each dropped file
        }); // end FileDrop.Listener


        //Button listeners

        //Help button directs user to GitHub page
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create("https://github.com/gensuta/RPGMaker_Generator_Parts_Mover"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        //Attempting to move files/folders
        moveGenerateAssetsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(generator_path_field.getText().isEmpty())
                {
                    JOptionPane.showMessageDialog(new JFrame(), "Please enter a path to the Generator folder first!", "Dialog", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(filesToBeMoved.isEmpty())
                {
                    JOptionPane.showMessageDialog(new JFrame(), "Please drag in files/folders to be moved!", "Dialog", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                MoveFiles();
                SaveGeneratorPath();
            }
        });
        //clearing files/folders to be moved
        clearFilesFoldersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClearFilesAndFolders();
            }
        });

        //Check if we have a generator path saved
        generator_path_field.setText(saveDataHandler.LoadDataFromTxt());
    }

    // Called to move all the files/folders we have stored
    public void MoveFiles() {
        for (File file : filesToBeMoved) {

            resultsLog.append("=====BEGINNING TO MOVE").append(file).append("======").append(System.lineSeparator());

            if (file.isDirectory()) // dragged file is a folder
            {
                MoveFolder(file);

            } else // dragged file is indeed a file
            {
                MoveFile(file);
            }
        }

        //Creating a results pop up, so we can see the success/error messages
        ResultsPopUp resultsPopUp = new ResultsPopUp(resultsLog.toString());
        ClearFilesAndFolders(); // clearing what we just moved out of the list
    }


    // handling going through a folder for files and subdirectories
    private void MoveFolder(File folder) {
        System.out.println("OPENING FOLDER: " + folder.getName());
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                MoveFile(file);
            } else {
                MoveFolder(file);
            }
        }
    }

    //called for every file we move
    private void MoveFile(File file) {
        var fileName = file.getName();
        System.out.println("Current file is " + fileName); // ensuring we know what file we're on

        // Edge Cases
        if(fileName.contains(".zip") || fileName.contains((".rar")))
        {
            resultsLog.append("X Skipping file ").append(fileName).append(". Please decompress the zip/rar file so that it's a folder.").append(System.lineSeparator());
            return;
        }

        if(!fileName.contains(".png") && !fileName.contains((".jpg"))) // We will not move files that aren't images which is fine!
        {
            resultsLog.append("X Skipping file ").append(fileName).append(". This file is not an image.").append(System.lineSeparator());
            return;
        }

        //Grabbing the number of the asset, so we can increase it if there's another file with the same number
        var splitSting = !fileName.contains("P") ? fileName.split("p") : fileName.split("P");

        if(fileName.contains("P")) // we're still moving the file, but it needs to be renamed
        {
            //TODO: Rename the file myself so it's not on the person moving the file... (sorry)
            resultsLog.append("! WARNING: Current file ").append(fileName).append(" has an uppercase P. Please locate the file in the Generator folder and make it lowercase.").append(System.lineSeparator());
        }

        if(splitSting[1].equals("ng")) // the only p in the file is for png
        {
            resultsLog.append("X Skipping file ").append(fileName).append(" since it's missing a 'p' before it's number").append(System.lineSeparator());
            return;
        }

        //checking if the string has an underscore or if it ends at the .png/.jpg. This is because some files have _c after the number
        var numString = (splitSting[1].contains("_")) ? splitSting[1].substring(0, splitSting[1].indexOf("_"))
                : splitSting[1].substring(0, splitSting[1].indexOf("."));

        var fileNum = 0;

        try {
            fileNum = Integer.parseInt(numString);
        }
        catch (NumberFormatException n)
        {
            //we couldn't get that dang number! It's probably not formatted properly.
            resultsLog.append("X Skipping file ").append(fileName).append(" since it's file name is not formatted properly.").append(System.lineSeparator());
            return;
        }

        var targetPath = generator_path_field.getText();

        var body_type = getBodyType(file);


        // Get folder paths
        String faceFolder = targetPath + "\\Face" + "\\" + body_type;
        String svFolder = targetPath + "\\SV" + "\\" + body_type;
        String tvFolder = targetPath + "\\TV" + "\\" + body_type;
        String tvdFolder = targetPath + "\\TVD" + "\\" + body_type;
        String variationFolder = targetPath + "\\Variation" + "\\" + body_type;

        //figuring out which folder we need to put this in
        String chosenFolder;
        if(fileName.contains("FG_"))
        {
            chosenFolder = faceFolder;
        }
        else if(fileName.contains("SV_"))
        {
            chosenFolder = svFolder;
        }
        else if(fileName.contains("TV_"))
        {
            chosenFolder = tvFolder;
        }
        else if(fileName.contains("TVD_"))
        {
            chosenFolder = tvdFolder;
        }
        else if(fileName.contains("icon_"))
        {
            chosenFolder = variationFolder;
        }
        else{
            resultsLog.append("X Skipping file ").append(fileName).append(" since it's file name is not formatted properly.").append(System.lineSeparator());
            return;
        }

        // actually moving the file
        Path path = Paths.get(chosenFolder);
        try {

            if(replaceCheckBox.isSelected()) // replacing instead of renaming if it's checked off
            {
                Files.copy(Paths.get(file.toURI()), path.resolve(file.getName()),StandardCopyOption.REPLACE_EXISTING);
            }
            else {
                Files.copy(Paths.get(file.toURI()), path.resolve(file.getName()));
            }

            resultsLog.append("✓ Successfully moved file: ").append(fileName).append(System.lineSeparator());

        }
        catch (Exception e) {
            if (e instanceof NoSuchFileException) {
                // add to final message as an error ( the target path doesn't exist)
                resultsLog.append("X Couldn't move file. The target path doesn't exist or is incorrect for ").append(targetPath).append(System.lineSeparator());
            }
            else if (e instanceof FileAlreadyExistsException) {
                //handling renaming the file :D
                var newName = getNewFileName(fileName,fileNum,chosenFolder);
                try
                {
                    Files.copy(Paths.get(file.toURI()), path.resolve(newName));
                    resultsLog.append("✓ Successfully moved file, but changed it's name from ").append(fileName).append(" to ").append(newName).append(System.lineSeparator());
                }
                catch (Exception c)
                {
                    c.printStackTrace();

                }
            }
            else {
                //Error I haven't encountered yet, so it'll just be in the results log to be reported to me
                resultsLog.append("X Couldn't move file. Error for the file ").append(fileName).append(" will be below. Please send the error to @gensuta");
                resultsLog.append(e.getMessage()).append(System.lineSeparator());
                e.printStackTrace();
            }

        }
    }

    private String getBodyType(File file) {
        if (femaleRadioButton.isSelected())
        {
            return"Female";
        }
        else if(maleRadioButton.isSelected())  {
            return "Male";
        }
        else if (kidRadioButton.isSelected())
        {
            return "Kid";
        }
        else // all is selected and we're separating based on path
        {
            //we have to look at the path and check which path this file is a part of!
            if (file.getPath().contains("Female")) {
                return "Female";
            }
            else if (file.getPath().contains("Kid")) {
                return "Kid";
            }
            else {
                return "Male"; // defaulting to plopping things here
            }
        }

    }

    // Making sure our new file name is a higher number than any files that currently exists
    private String getNewFileName(String fileName, int fileNum, String targetPath)
    {
        System.out.println(fileNum);
        var newName = fileName;
        Path path = Path.of(targetPath +"\\"+ newName);
        System.out.println(path);

        while (Files.exists(path))
        {
                System.out.println(newName);

                newName = newName.replace(Integer.toString(fileNum), Integer.toString(fileNum+1));
                path = Path.of(targetPath +"\\" +newName);
                fileNum+=1; // increasing the number until the file path doesn't exist
        }
        return newName;
    }

    //clearing out the list of files we need to move
    private void ClearFilesAndFolders()
    {
        files_added_text_area.setText("");
        filesToBeMoved.clear();
    }

    //saving our generator path to a txt file to be loaded upon launching the tool :)
    private void SaveGeneratorPath()
    {
        saveDataHandler.SaveDataToTxt(generator_path_field.getText());
    }

}
