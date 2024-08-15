import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;

public class ToolWindow {

    JFrame frame = new JFrame("RPGMaker Asset Adder");

    JLabel jLabel = new JLabel("Drag the files/folders you want to add to RPGMaker.", SwingConstants.CENTER);

    JPanel upperPanel = new JPanel();

    JPanel filePanel = new JPanel();


    JLabel jLabel2 = new JLabel("Drag the folder you want your files to go into.", SwingConstants.CENTER);

    JLabel targetLabel = new JLabel("The RPGMaker folder can be found by right-clicking on your engine in Steam and clicking 'Browse local files'", SwingConstants.CENTER);


    JPanel targetPanel = new JPanel();

    JButton moveButton = new JButton("Move Assets");

    ArrayList<File> filesToBeMoved = new ArrayList<File>();

    String targetPath = "";

    JToolBar toolBar = new JToolBar();
    JButton btn = new JButton("Preferences");
    JButton btn2 = new JButton("About");
    JButton btn3 = new JButton("Help");

    public ToolWindow() {


        // TOOL BAR
        toolBar.add(btn);
        toolBar.add(btn2);
        toolBar.add(btn3);

        toolBar.setFloatable( false);
        frame.add(toolBar);

        toolBar.setLayout(new FlowLayout());



        jLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        jLabel2.setFont(new Font("Arial", Font.PLAIN, 18));
        targetLabel.setFont(new Font("Arial", Font.ITALIC, 18));


        //files to be moved
        frame.add(jLabel);

        frame.add(filePanel);

        filePanel.setPreferredSize(new Dimension(800, 50));

        //javax.swing.border.TitledBorder dragBorder = new javax.swing.border.TitledBorder( "Drop 'em" );
        final JTextArea text = new JTextArea();
        frame.getContentPane().add(
                new javax.swing.JScrollPane(text),
                java.awt.BorderLayout.CENTER);

        filePanel.setBorder(BorderFactory.createLineBorder(Color.black));

        new FileDrop(filePanel, new FileDrop.Listener() {
            public void filesDropped(File[] files) {
                for (int i = 0; i < files.length; i++) {
                    try {
                        text.append(files[i].getCanonicalPath() + "\n");
                        filesToBeMoved.add(files[i]);
                    }   // end try
                    catch (java.io.IOException e) {
                    }
                }   // end for: through each dropped file
            }   // end filesDropped
        }); // end FileDrop.Listener

        //target folder
        frame.add(jLabel2);
        frame.add(targetLabel);


        JTextArea textAreaPath = new JTextArea(1, 20);

        frame.add(textAreaPath);
        frame.add(moveButton);

        moveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                targetPath = textAreaPath.getText();
                MoveFiles();
            }
        });



        frame.pack();
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);



        frame.setVisible(true);

    }

    public void MoveFiles() {
        for (int i = 0; i < filesToBeMoved.size(); i++) {
            try {

                // SHOULD BE BASED ON SPECIFIC FOLDERS! We will start with the generator first

                if(targetPath.toLowerCase().contains("generator"))
                {
                    //we place things based on the folder. Maybe we have this window as a general window
                    //and have a second tab just for generator moving? or a separate button. idk we need
                    //to know if it's fem/masc/kid is what I mean

                    String face = targetPath + "\\Face" + "\\Female";
                    String svPath = targetPath + "\\SV" + "\\Female";
                    String tvPath = targetPath + "\\TV" + "\\Female"; // that third folder depends but we need that
                    String tvdPath = targetPath + "\\TVD" + "\\Female";
                    String variationPath = targetPath + "\\Variation" + "\\Female";

                }


                File sourceFile = filesToBeMoved.get(i);
                Files.copy(Paths.get(sourceFile.toURI()), Paths.get(targetPath).resolve(sourceFile.getName()));

                //add to final success message listing everything that's been moved
                System.out.println("File copied successfully using traditional I/O.");
            }
            catch (Exception e) {
                if( e instanceof NoSuchFileException)
                {
                    // add to final message as an error ( the target path doesn't exist)
                }
                else if ( e instanceof FileAlreadyExistsException)
                {
                    // rename asset so that it increases the number by one
                    //add to final message saying it was copied but renamed to ___
                }
                else {
                    e.printStackTrace();
                }

            }
        }

        // scrollable pop up with all the messages
    }
}

