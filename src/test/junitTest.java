
import org.junit.Test;

import java.io.*;

public class junitTest {


    @Test
    public void fileReadAndUpdateTest(){

        String line = "";
        String replaceText ="setlocal";
        String updateText = "set \"CATALINA_HOME=E:/\\project_test/\\tomcat\"\r\n" +
                "set \"JAVA_HOME=E:/\\project_test/\\java\"\r\n" + "setlocal\r\n";

        String repLine = "";
        int buffer;
        try{

            // read
            File file = new File("E:\\project_test\\application\\startup.bat");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while ((line = bufferedReader.readLine()) != null) {
                // 일치하는 패턴에서는 바꿀 문자로 변환
                repLine += line.replaceAll(replaceText, updateText);
                repLine += "\r\n";
                System.out.println(repLine);

            }
            // 새로운 파일에 쓴다.
            FileWriter fw = new FileWriter(file);
            fw.write(repLine);
            fw.close();
            bufferedReader.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }





}
