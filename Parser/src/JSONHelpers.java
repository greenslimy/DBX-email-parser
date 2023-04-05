import com.google.gson.Gson;

import java.io.*;
import java.nio.Buffer;

public class JSONHelpers {

    public void separateEmailHeader(String path, String output) throws FileNotFoundException {
        Gson gson = new Gson();

        try {
            File metadata = new File(output);
            BufferedReader reader = new BufferedReader(new FileReader(path));
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));
            //The metadata of the email header seems to span from line 1 to 24; simple for loop?
            for(int i = 0; i <= 23; i++) {
                //We use 23 because java has the reader start at index 0. As if the file would be an array. (april fools: IT IS)
                String line = reader.readLine();
                writer.write(line);
            }
            reader.close();
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}
