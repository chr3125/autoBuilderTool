package AutoApplicationBuild;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.unZipUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoApplicationBuild {

    private static final Logger LOG = LoggerFactory.getLogger(AutoApplicationBuild.class);

    // APACHE에 들어가는 SERVER NAME ( HOST NAME )
    public final String SERVER_NAME = "localhost";

    // APPLICATION에 해당하는 파일의 경로
    public final String APPLICATION_FILE_PATH = System.getProperty("user.dir")+ File.separatorChar+"application";

    // TOMCAT FILE NAME
    public final String TOMCAT_FILE_NAME = "apache-tomcat-window.zip";

    // APACHE FILE NAME
    public final String APACHE_FILE_NAME = "";

    // SOLUTION FILE NAME
    public final String SOLUTION_FILE_NAME = "";

    // 젠킨스 FILE NAME
    public final String JENKINS_FILE_NAME = "jenkins.war";

    // STEP 1 SAMPLING
    public void buildStepOne() throws Exception{

        //파일 경로에 APPLICATION 파일이 있는지 확인한다.

        String tomcatFilePath = APPLICATION_FILE_PATH+ File.separatorChar + TOMCAT_FILE_NAME;
        //String apacheFilePath = APPLICATION_FILE_PATH+ File.pathSeparator + APACHE_FILE_NAME;
        //String solutionFilePath = APPLICATION_FILE_PATH+ File.pathSeparator + SOLUTION_FILE_NAME;

        try{
            File tomcatFile = new File(tomcatFilePath);
            //File apacheFile = new File(apacheFilePath);
            //File solutionFile = new File(solutionFilePath);

            boolean tomcatPass = false;
            boolean apachePass = false;
            boolean solutionPass = false;

            if(tomcatFile.isFile()){
                tomcatPass = true;
            }else{
                LOG.info("TOMCAT FILE CHECK NOT IS PATH :"+tomcatFilePath);
            }
            /*if(apacheFile.isFile()){
                apachePass = true;
            }
            if(solutionFile.isFile()){
                solutionPass = true;
            }*/

            if(!(tomcatPass)){
            //if(!(tomcatPass && apachePass && solutionPass)){
                LOG.info(" FILE IS ALIVE CHECK IS FAIL TOMCAT FILE : "+ tomcatPass);
                //LOG.info(" FILE IS ALIVE CHECK IS FAIL TOMCAT FILE : "+ tomcatPass +" & APACHE FILE :"+ apachePass +" & SOLUTION FILE :"+ solutionPass);
            }else{
                //this.buildStepTwo(tomcatFilePath , apacheFilePath , solutionFilePath);
                this.buildStepTwo(tomcatFilePath);
            }
        }catch (Exception e){
            e.printStackTrace();
            LOG.error("CHECK FILE ALIVE FILE EXCEPTION :"+e.getMessage());
        }


    }

    // 해당 FILE들 압축 해제를 진행한다.
    public void buildStepTwo(String tomcatFile) throws Exception{
    //public void buildStepTwo(String tomcatFile ,String apacheFile ,String solutionFile) throws Exception{

        unZipUtil unZipUtil = new unZipUtil();
        String usrMsg = "";


        // LINUX JAVA HOME을 설정한다.
        ShellCommander shellCommander = new ShellCommander();
        System.out.println("TOMCAT 압축 해제할 경로를 지정하세요.");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        usrMsg = in.readLine();

        if(StringUtil.isEmpty(usrMsg)){
            System.out.println("경로가 정상적으로 입력되지 않았습니다. 다시 입력해주세요.");
            usrMsg = in.readLine();
        }
        if(StringUtil.isEmpty(usrMsg)){
            System.out.println("두 번 잘못 입력하셨습니다. 프로그램을 종료합니다.");
        }else{
            // 사용자의 명령어로 받은 내역이 디렉토리가 존재하는지 확인한다.
            File fileDir = new File(usrMsg);

            if(fileDir.isDirectory()){
                try{
                    System.out.println("톰캣파일을 압축 해제합니다.");
                    unZipUtil.unZipFile(tomcatFile,usrMsg+File.separatorChar+"tomcat");
                    System.out.println("성공!");

                    System.out.println("젠킨스 파일을 복사합니다.");
                    // 젠킨스 파일을 복사한다.
                    shellCommander.execute("copy "+APPLICATION_FILE_PATH+File.separatorChar+JENKINS_FILE_NAME+" "+usrMsg+File.separatorChar+"tomcat"+File.separatorChar+"webapps");
                    System.out.println("성공!");

                    // 정상적으로 해제가 되었으면, 해당 경로의 톰캣을 실행시킨다.
                    System.out.println("톰캣을 실행시킵니다.");
                    System.out.println("COMMEND SHELL :"+usrMsg+File.separatorChar+"tomcat"+File.separatorChar+"bin"+File.separatorChar+"startup.bat");

                    SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
                    shellCommander.execute(usrMsg+File.separatorChar+"tomcat"+File.separatorChar+"bin"+File.separatorChar+"startup.bat");
                    String now = fm.format(new Date());
                    shellCommander.execute("Get-Content "+usrMsg+File.separatorChar+"tomcat"+File.separatorChar+"logs"+File.separatorChar+"catalina."+now+" -Wait -Tail 100");


                    System.out.println("성공!");
                    //this.unZipFile(apacheFile);
                    //this.unZipFile(solutionFile);
                }catch (Exception e){
                    System.out.println("실패!");
                    LOG.error(e.getMessage());
                    e.printStackTrace();
                }
            }

        }
    }





    public static void main(String[] arg){
        AutoApplicationBuild autoApplicationBuild = new AutoApplicationBuild();
        try{
            autoApplicationBuild.buildStepOne();
        }catch (Exception e){
            e.printStackTrace();
        }
    }




}



