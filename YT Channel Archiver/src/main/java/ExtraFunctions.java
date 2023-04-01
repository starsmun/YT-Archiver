import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExtraFunctions {
    public static String returnMD5(String file1) {
        try {
            String[] cmd = {"python", System.getProperty("user.dir") + "/Scripts/GetAudioHash.py",file1};
            Process process = Runtime.getRuntime().exec(cmd);
            // Read the output of the process

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line, result = "";
            while ((line = reader.readLine()) != null) {
                result = line;
            }
            // Wait for the process to finish
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return result;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}
