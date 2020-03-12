package AutoApplicationBuild;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.unZipUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoApplicationBuildToTomcatAndSolution {

    private static final Logger LOG = LoggerFactory.getLogger(AutoApplicationBuildToTomcatAndSolution.class);

    // APACHE에 들어가는 SERVER NAME ( HOST NAME )
    public final String SERVER_NAME = "localhost";

    // APPLICATION에 해당하는 파일의 경로
    public final String APPLICATION_FILE_PATH = System.getProperty("user.dir")+ File.separatorChar+"application";

    // TOMCAT FILE NAME
    public final String TOMCAT_FILE_NAME = "apache-tomcat-window.zip";

    // APACHE FILE NAME
    public final String APACHE_FILE_NAME = "";

    // SOLUTION FILE NAME
    public final String SOLUTION_FILE_NAME = "standard-deploy-mssql_Web.war";

    // 젠킨스 FILE NAME
    public final String JENKINS_FILE_NAME = "jenkins.war";

    // JAVA INSTALL FILE NAME
    public final String JAVA_FILE_NAME_LINUX = "jdk-7u80-linux-x64.tar";
    public final String JAVA_FILE_NAME_WINDOW = "jdk1.8.0_121.zip";


    //java install
    public void javaInstall() throws Exception{

        String javaFileName = "";

        //최초에 OS를 판단한다.
        String osName = System.getProperty("os.name");

        // win
        if (osName.contains("Windows")) {
            javaFileName = JAVA_FILE_NAME_WINDOW;
        }else{
            javaFileName = JAVA_FILE_NAME_LINUX;
        }


        //파일 경로에 APPLICATION 파일이 있는지 확인한다.
        String javaFilePath = APPLICATION_FILE_PATH+ File.separatorChar + javaFileName;

        try{

            // 1. 현재 자바를 설치했는지에 대한 여부를 확인한다.
            String javaSetupCheckUsrMsg = "";
            String javaInstallPath = "";

            ShellCommander shellCommander = new ShellCommander();
            System.out.println("현재 자바 설치를 진행하셨다면 ('Y') 진행하지 않았다면 ('N')을 입력해주세요.");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            javaSetupCheckUsrMsg = in.readLine();

            if(("Y").equals(javaSetupCheckUsrMsg)){
                System.out.println("설치가 완료된 JAVA_HOME 경로를 입력해주세요.");
                in = new BufferedReader(new InputStreamReader(System.in));
                javaInstallPath = in.readLine();
                this.tomcatInstall(javaInstallPath);

            }else{
                // 2. 자바를 설치 하지 않았다면, application 내에 있는 zip 파일을 unzip 처리한다.
                File javaFile = new File(javaFilePath);

                boolean javaPass = false;

                if(javaFile.isFile()){
                    javaPass = true;
                }else{
                    LOG.info("자바 설치 파일이 존재하지 않습니다. 해당 경로에 파일이 존재하는지 확인해주세요. :"+javaFilePath);
                }

                if(javaPass){
                    unZipUtil unZipUtil = new unZipUtil();
                    String usrMsg = "";

                    System.out.println("자바를 압축 해제할 경로를 지정하세요.");
                    in = new BufferedReader(new InputStreamReader(System.in));
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
                                javaInstallPath = usrMsg+File.separatorChar+"java";
                                System.out.println("자바파일을 압축 해제합니다.");
                                unZipUtil.unZipFile(javaFilePath,javaInstallPath);
                                System.out.println("성공!");

                                this.tomcatInstall(javaInstallPath);
                            }catch (Exception e){
                                System.out.println("실패!");
                                LOG.error(e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }



        }catch (Exception e){
            e.printStackTrace();
            LOG.error("CHECK FILE ALIVE FILE EXCEPTION :"+e.getMessage());
        }
    }


    //tomcat install
    public void tomcatInstall(String javaInstallPath) throws Exception{


        //파일 경로에 APPLICATION 파일이 있는지 확인한다.
        String tomcatFilePath = APPLICATION_FILE_PATH+ File.separatorChar + TOMCAT_FILE_NAME;

        try{
            File tomcatFile = new File(tomcatFilePath);

            boolean tomcatPass = false;

            if(tomcatFile.isFile()){
                tomcatPass = true;
            }else{
                LOG.info("TOMCAT 파일이 존재하지 않습니다. 해당 경로에 파일이 존재하는지 확인해주세요. :"+tomcatFilePath);
            }

            if(tomcatPass){
                unZipUtil unZipUtil = new unZipUtil();
                String usrMsg = "";

                ShellCommander shellCommander = new ShellCommander();
                System.out.println("TOMCAT 를 압축 해제할 경로를 지정하세요.");
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
                            String tomcatIntallPath = usrMsg+File.separatorChar+"tomcat";
                            System.out.println("TOMCAT 파일을 압축 해제합니다.");
                            unZipUtil.unZipFile(tomcatFilePath,tomcatIntallPath);
                            System.out.println("성공!");

                            System.out.println("JAVA_HOME / CATALINA_HOME을 설정합니다.");
                            //javaInstallPath  ( JAVA_HOME )
                            //tomcatIntallPath ( CATALINA_HOME )

                            String line = "";
                            String replaceText ="setlocal";

                            tomcatIntallPath = tomcatIntallPath.replace("\\","/");
                            javaInstallPath = javaInstallPath.replace("\\","/");
                            String updateText = "set \"CATALINA_HOME="+ tomcatIntallPath +"\"\r\n" +
                                    "set \"JAVA_HOME="+javaInstallPath+"\"\r\n" + "setlocal\r\n";

                            String repLine = "";
                            int buffer;

                            try{

                                // read
                                File file = new File(tomcatIntallPath +File.separatorChar+ "bin"+File.separatorChar+"startup.bat");
                                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                                while ((line = bufferedReader.readLine()) != null) {
                                    // 일치하는 패턴에서는 바꿀 문자로 변환
                                    repLine += line.replaceAll(replaceText, updateText);
                                    repLine += "\r\n";
                                }
                                // 새로운 파일에 쓴다.
                                FileWriter fw = new FileWriter(file);
                                fw.write(repLine);
                                fw.close();
                                bufferedReader.close();
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                            System.out.println("솔루션 파일을 복사합니다.");
                            // 젠킨스 파일을 복사한다.
                            shellCommander.execute("copy "+APPLICATION_FILE_PATH+File.separatorChar+SOLUTION_FILE_NAME+" "+usrMsg+File.separatorChar+"tomcat"+File.separatorChar+"webapps");
                            System.out.println("성공!");

                            // 정상적으로 해제가 되었으면, 해당 경로의 톰캣을 실행시킨다.
                            System.out.println("톰캣을 실행시킵니다.");
                            System.out.println("COMMEND SHELL :"+usrMsg+File.separatorChar+"tomcat"+File.separatorChar+"bin"+File.separatorChar+"startup.bat");

                            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
                            shellCommander.execute(usrMsg+File.separatorChar+"tomcat"+File.separatorChar+"bin"+File.separatorChar+"startup.bat");
                            String now = fm.format(new Date());
                            shellCommander.execute("Get-Content "+usrMsg+File.separatorChar+"tomcat"+File.separatorChar+"logs"+File.separatorChar+"catalina."+now+" -Wait -Tail 100");


                            System.out.println("성공!");

                        }catch (Exception e){
                            System.out.println("실패!");
                            LOG.error(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            LOG.error("CHECK FILE ALIVE FILE EXCEPTION :"+e.getMessage());
        }
    }



    public static void main(String[] arg){
        AutoApplicationBuildToTomcatAndSolution autoApplicationBuild = new AutoApplicationBuildToTomcatAndSolution();
        try{
            autoApplicationBuild.javaInstall();
        }catch (Exception e){
            e.printStackTrace();
        }
    }




}



