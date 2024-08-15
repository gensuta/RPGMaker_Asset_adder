import javax.swing.*;

public class ResultsPopUp extends JFrame{
    private JTextArea textArea1;
    private JPanel MainPanel;

    public ResultsPopUp(String resultsLog)
    {
        setTitle("RPGMaker Generator Mover Results");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(MainPanel);
        textArea1.setText(resultsLog);

        setVisible(true);
    }
}
