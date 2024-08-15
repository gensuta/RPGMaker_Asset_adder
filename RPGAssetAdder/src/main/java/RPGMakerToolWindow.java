import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Objects;

public class RPGMakerToolWindow extends JFrame {
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

    private final ArrayList<File> filesToBeMoved = new ArrayList<>();
    private final StringBuilder resultsLog = new StringBuilder();

    public RPGMakerToolWindow() {
        setTitle("RPGMaker Generator Asset Adder");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(MainPanel);


        setVisible(true);


        // file drag and drop handling!
        // end filesDropped
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


        //Button listeners below!
        preferencesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        moveGenerateAssetsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MoveFiles();
            }
        });
        clearFilesFoldersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClearFilesAndFolders();
            }
        });
    }

    // We're specifically making this for moving generator parts for now!
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
            System.out.println("File(s) copied successfully using traditional I/O.");


        }

        ResultsPopUp resultsPopUp = new ResultsPopUp(resultsLog.toString());
        ClearFilesAndFolders();
    }


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

    private void MoveFile(File file) {
        var fileName = file.getName();
        System.out.println("Current file is " + fileName);

        // Edge Cases
        if(fileName.contains(".zip") || fileName.contains((".rar")))
        {
            resultsLog.append("X Skipping file ").append(fileName).append(". Please decompress the zip/rar file so that it's a folder.").append(System.lineSeparator());
            return;
        }

        if(!fileName.contains(".png") && !fileName.contains((".jpg")))
        {
            resultsLog.append("X Skipping file ").append(fileName).append(". This file is not an image.").append(System.lineSeparator());
            return;
        }

        //Grabbing the number of the asset
        var splitSting = !fileName.contains("P") ? fileName.split("p") : fileName.split("P");

        if(fileName.contains("P")) {
            resultsLog.append("! WARNING: Current file ").append(fileName).append(" has an uppercase P. Please locate the file in the Generator folder and make it lowercase.").append(System.lineSeparator());
        }

        if(splitSting[1].equals("ng"))
        {
            resultsLog.append("X Skipping file ").append(fileName).append(" since it's missing a 'p' before it's number").append(System.lineSeparator());
            return;
        }

        var numString = (splitSting[1].contains("_")) ? splitSting[1].substring(0, splitSting[1].indexOf("_"))
                : splitSting[1].substring(0, splitSting[1].indexOf("."));

        var fileNum = 0;

        try {
            fileNum = Integer.parseInt(numString);
        }
        catch (NumberFormatException n)
        {
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

        //figure out which folder we need to put this in
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

        // actually moving stuff
        Path path = Paths.get(chosenFolder);
        try {

            if(replaceCheckBox.isSelected())
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
                resultsLog.append("X Couldn't move file. Error for the file ").append(fileName).append(" will be below");
                resultsLog.append(e.getMessage()).append(System.lineSeparator());
                e.printStackTrace();
            }

        }
    }

    private String getBodyType(File file) {

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
                fileNum+=1;
        }
        return newName;
    }

    private void ClearFilesAndFolders()
    {
        files_added_text_area.setText("");
        filesToBeMoved.clear();
    }

}
