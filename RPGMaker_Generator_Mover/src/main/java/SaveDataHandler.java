import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SaveDataHandler {

    // Whatever string is sent here will be saved to a txt file
    public void SaveDataToTxt(String saveDataString) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("save.txt"));
            writer.write(saveDataString);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Grabbing the string saved to the txt file
    public String LoadDataFromTxt()
    {
        String data = "";
        try {
            Path path = Paths.get("save.txt");
            if(Files.exists(path)) // if file doesn't exist, we'll return an empty string
            {
                Stream<String> lines = Files.lines(path);
                String stringFromTxt = lines.collect(Collectors.joining("\n"));
                lines.close();

                data = stringFromTxt;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

}
