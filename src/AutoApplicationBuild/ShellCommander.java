package AutoApplicationBuild;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


// 인터넷 발췌
public class ShellCommander {

    public String execute(String command) {

        StringBuffer output = new StringBuffer();

        Process process = null;

        BufferedReader br = null;

        Runtime runtime = Runtime.getRuntime();

        String osName = System.getProperty("os.name");

        // win
        if (osName.contains("Windows")) {
            command = "cmd /c " + command;
        }

        try {
            process = runtime.exec(command);

            // write process input
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String msg = null;
            while ((msg=br.readLine()) != null) {
                output.append(msg + System.getProperty("line.separator"));

            }

            br.close();



            // write error input

            br = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            while((msg=br.readLine())!=null) {

                output.append(msg + System.getProperty("line.separator"));

            }

//			br.close();

        } catch (IOException e) {

            output.append("IOException : " + e.getMessage());

            e.printStackTrace();

        } finally {

            process.destroy();

            try {

                if(br!=null) br.close();

            } catch (IOException e) {

                e.printStackTrace();

            }

        }



        return output.toString();

    }



}
