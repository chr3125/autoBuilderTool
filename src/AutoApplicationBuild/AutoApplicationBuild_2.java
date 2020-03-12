package AutoApplicationBuild;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.swing.StringUIClientPropertyKey;
import util.unZipUtil;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoApplicationBuild_2 {

    private static final Logger LOG = LoggerFactory.getLogger(AutoApplicationBuild_2.class);

    // APACHE에 들어가는 SERVER NAME ( HOST NAME )
    public final String SERVER_NAME = "localhost";

    // APPLICATION에 해당하는 파일의 경로
    public final String APPLICATION_FILE_PATH = System.getProperty("user.dir")+ File.separatorChar+"application";

    public final String DATASOURCE_FILE_PATH = "WEB-INF" + File.separatorChar + "classes" + File.separatorChar + "smartsuite" ;

    // TOMCAT FILE NAME
    public final String TOMCAT_FILE_NAME_WINDOW = "apache-tomcat-window.zip";
    public final String TOMCAT_FILE_NAME_LINUX = "apache-tomcat-7.0.94.tar.gz";

    // APACHE FILE NAME
    public final String APACHE_FILE_NAME = "";

    // SOLUTION FILE NAME
    public final String SOLUTION_FILE_NAME = "ROOT.war";

    // 젠킨스 FILE NAME
    public final String JENKINS_FILE_NAME = "jenkins.war";

    // DATASOURCE FILE NAME
    public final String DATASOURCE_FILE_NAME = "datasource-context.xml";
    public final String DATASOURCE_TEMP_FILE_NAME = "datasource-context-temp.xml";

    // JAVA INSTALL FILE NAME
    public final String JAVA_FILE_NAME_LINUX = "jdk-7u80-linux-x64.tar.gz";
    public final String JAVA_FILE_NAME_WINDOW = "jdk1.8.0_121.zip";

    // ANT INSTALL FILE NAME
    public final String ANT_FILE_NAME_LINUX = "apache-ant-1.10.5-bin.tar.gz";
    public final String ANT_FILE_NAME_WINDOW = "apache-ant-1.9.14-bin.zip";

    //driver Class Set
    //public final String JDBC_DRIVER_CLASS_NAME_HANADB = "com.sap.db.jdbc.Driver";
    public final String JDBC_DRIVER_CLASS_NAME_POSTGRESQL = "org.postgresql.Driver";
    public final String JDBC_DRIVER_CLASS_NAME_MARIADB = "org.mariadb.jdbc.Driver";
    public final String JDBC_DRIVER_CLASS_NAME_MSSQL = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public final String JDBC_DRIVER_CLASS_NAME_ORACLE = "oracle.jdbc.driver.OracleDriver";


    //public final String JDBC_URL_HANADB = "jdbc:sap://hanadb:39013?databaseName=SYSTEMDB&amp;currentschema=SRM9QA";
    public final String JDBC_URL_POSTGRESQL = "jdbc:postgresql://";
    public final String JDBC_URL_MARIADB = "jdbc:mariadb://";
    public final String JDBC_URL_MSSQL = "jdbc:sqlserver://";
    public final String JDBC_URL_ORACLE = "jdbc:oracle:thin:@";



    //datasource.properties
    public final String DATASOURCE_SQLTYPE = "#sqlType";
    public final String DATASOURCE_DRIVER_CLASSNAME = "#default.datasource.driverclassname";
    public final String DATASOURCE_URL = "#default.datasource.url";
    public final String DATASOURCE_USERNAME = "#default.datasource.username";
    public final String DATASOURCE_PASSWORD = "#default.datasource.password";
    public final String DATASOURCE_DIALECTCLASS = "#dialectClass";
    public final String DATASOURCE_TYPE_HANDLER_JAVATYPE1 = "#javatype1";
    public final String DATASOURCE_TYPE_HANDLER_JAVATYPE2 = "#javatype2";
    public final String DATASOURCE_TYPE_HANDLER_JAVATYPE3 = "#javatype3";

    //dialectClass
    public final String DATASOURCE_DIALECT_CLASS_ORACLE = "smartsuite.mybatis.dialect.OracleDialect";
    public final String DATASOURCE_DIALECT_CLASS_MSSQL = "smartsuite.mybatis.dialect.SQLServer2005Dialect";
    public final String DATASOURCE_DIALECT_CLASS_MARIADB = "smartsuite.mybatis.dialect.MySQLDialect";
    public final String DATASOURCE_DIALECT_CLASS_POSTGRESQL = "smartsuite.mybatis.dialect.PostgreSQLDialect";
    //public final String DATASOURCE_DIALECT_CLASS_HANADB = "smartsuite.mybatis.dialect.SAPHanaDialect";


    //oracle typehandler
    public final String ORACLE_TYPE_HANDLER_JAVATYPE1 = "oracle.sql.TIMESTAMP";
    public final String ORACLE_TYPE_HANDLER_JAVATYPE2 = "oracle.sql.TIMESTAMPLTZ";
    public final String ORACLE_TYPE_HANDLER_JAVATYPE3 = "oracle.sql.TIMESTAMPTZ";

    //mssql typehandler
    public final String MSSQL_TYPE_HANDLER_JAVATYPE1 = "microsoft.sql.DateTimeOffset";
    public final String MSSQL_TYPE_HANDLER_JAVATYPE2 = "microsoft.sql.DateTimeOffset";
    public final String MSSQL_TYPE_HANDLER_JAVATYPE3 = "microsoft.sql.DateTimeOffset";

    //mysql & maria typehandler
    public final String MYSQL_TYPE_HANDLER_JAVATYPE1 = "java.sql.Timestamp";
    public final String MYSQL_TYPE_HANDLER_JAVATYPE2 = "java.sql.Time";
    public final String MYSQL_TYPE_HANDLER_JAVATYPE3 = "java.sql.Date";


    public final String POSTGRESQL_TYPE_HANDLER_JAVATYPE1 = "java.sql.Timestamp";
    public final String POSTGRESQL_TYPE_HANDLER_JAVATYPE2 = "java.sql.Time";
    public final String POSTGRESQL_TYPE_HANDLER_JAVATYPE3 = "java.sql.Date";

    public final String SQLTYPE_ORACLE ="oracle";
    public final String SQLTYPE_MSSQL ="mssql";
    public final String SQLTYPE_MARIADB ="mariadb";
    public final String SQLTYPE_POSTGRESQL ="postgresql";

    //ANT용 PATH REPLACE
    public final String BUILD_ANT_PATH = "#deploy_path";




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

            if(("Y").equals(javaSetupCheckUsrMsg.toUpperCase())){
                System.out.println("설치가 완료된 JAVA_HOME 경로를 입력해주세요.");
                in = new BufferedReader(new InputStreamReader(System.in));
                javaInstallPath = in.readLine();
                this.tomcatSolutionInstall(javaInstallPath);

            }else{
                // 2. 자바를 설치 하지 않았다면, application 내에 있는 zip 파일을 unzip 처리한다.
                File javaFile = new File(javaFilePath);

                boolean javaPass = false;

                if(javaFile.isFile()){
                    javaPass = true;
                }else{
                    // System.out.println("자바 설치 파일이 존재하지 않습니다. 해당 경로에 파일이 존재하는지 확인해주세요. :"+javaFilePath);
                    System.out.println("자바 설치 파일이 존재하지 않습니다. 해당 경로에 파일이 존재하는지 확인해주세요. :"+javaFilePath);
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

                        try{
                            // 사용자의 명령어로 받은 내역이 디렉토리가 존재하는지 확인한다.
                            File fileDir = new File(usrMsg);

                            if(fileDir.isDirectory()){
                                try{
                                    javaInstallPath = usrMsg+File.separatorChar+"java";
                                    System.out.println("자바파일을 압축 해제합니다.");
                                    unZipUtil.unZipFile(javaFilePath,javaInstallPath);
                                    System.out.println("성공!");

                                    System.out.println("JAVA_HOME을 설정합니다.");
                                    if (osName.contains("Windows")) {
                                        shellCommander.execute("set JAVA_HOME ="+usrMsg);
                                        shellCommander.execute("set PATH =%JAVA_HOME%\\bin;%PATH%");
                                    }else{
                                        shellCommander.execute("export JAVA_HOME ="+usrMsg);
                                        shellCommander.execute("export PATH =%JAVA_HOME%\\bin;%PATH%");
                                    }


                                    this.tomcatSolutionInstall(javaInstallPath);
                                }catch (Exception e){
                                    System.out.println("실패!");
                                    LOG.error(e.getMessage());
                                    System.out.println(e.getMessage());
                                }
                            }else{
                                System.out.println("해당 경로가 존재하지 않습니다. OS name ="+osName);

                                if (osName.contains("Windows")) {

                                }else{
                                    fileDir.mkdir();
                                    javaInstallPath = usrMsg+File.separatorChar+"java";
                                    System.out.println("자바파일을 압축 해제합니다.");
                                    unZipUtil.unZipFile(javaFilePath,javaInstallPath);
                                    System.out.println("성공!");

                                    System.out.println("JAVA_HOME을 설정합니다.");

                                    shellCommander.execute("export JAVA_HOME ="+usrMsg);
                                    shellCommander.execute("export PATH =%JAVA_HOME%\\bin;%PATH%");

                                    this.tomcatSolutionInstall(javaInstallPath);
                                }
                            }
                        }catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }



        }catch (Exception e){
            System.out.println(e.getMessage());
            LOG.error("CHECK FILE ALIVE FILE EXCEPTION :"+e.getMessage());
        }
    }

    //ANT INSTALL
    public void antInstall(String webappsPath) throws Exception{

        String antFileName = "";

        //최초에 OS를 판단한다.
        String osName = System.getProperty("os.name");

        // win
        if (osName.contains("Windows")) {
            antFileName = ANT_FILE_NAME_WINDOW;
        }else{
            antFileName = ANT_FILE_NAME_LINUX;
        }


        //파일 경로에 APPLICATION 파일이 있는지 확인한다.
        String antFilePath = APPLICATION_FILE_PATH+ File.separatorChar + antFileName;

        try{

            String antSetupCheckUsrMsg = "";
            String antInstallPath = "";

            ShellCommander shellCommander = new ShellCommander();
            System.out.println("현재 ANT 설치를 진행하셨다면 ('Y') 진행하지 않았다면 ('N')을 입력해주세요.");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            antSetupCheckUsrMsg = in.readLine();

            if(("Y").equals(antSetupCheckUsrMsg)){
                System.out.println("설치가 완료된 ANT_HOME 경로를 입력해주세요.");
                in = new BufferedReader(new InputStreamReader(System.in));
                antInstallPath = in.readLine();
                //this.tomcatInstall(antInstallPath);

            }else{
                File antFile = new File(antFilePath);

                boolean antPass = false;

                if(antFile.isFile()){
                    antPass = true;
                }else{
                     System.out.println("ANT 설치 파일이 존재하지 않습니다. 해당 경로에 파일이 존재하는지 확인해주세요. :"+antFilePath);
                }

                if(antPass){
                    unZipUtil unZipUtil = new unZipUtil();
                    String usrMsg = "";

                    System.out.println("ANT 를 압축 해제할 경로를 지정하세요.");
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
                                antInstallPath = usrMsg+File.separatorChar+"ant";
                                System.out.println("ANT 파일을 압축 해제합니다.");
                                unZipUtil.unZipFile(antFilePath,antInstallPath);
                                System.out.println("성공!");


                               /* int buffer;
                                String repLine = "";
                                String line = "";

                                try{
                                    // read
                                    File file = new File(APPLICATION_FILE_PATH+ File.separatorChar + "ROOT" +File.separatorChar +"build.xml");
                                    BufferedReader fileBufferedReader = new BufferedReader(new FileReader(file));
                                    while ((line = fileBufferedReader.readLine()) != null) {
                                        // 일치하는 패턴에서는 바꿀 문자로 변환
                                        repLine += line.replaceAll(BUILD_ANT_PATH, webappsPath);
                                        repLine += "\r\n";
                                    }
                                    // 새로운 파일에 쓴다.
                                    Writer outFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
                                    outFile.write(repLine);
                                    outFile.close();
                                    fileBufferedReader.close();
                                }catch (Exception e){
                                     System.out.println(e.getMessage());
                                }*/

                            }catch (Exception e){
                                System.out.println("실패!");
                                LOG.error(e.getMessage());
                                 System.out.println(e.getMessage());
                            }
                        }else{
                            System.out.println("해당 경로가 존재하지 않습니다.");

                            if (osName.contains("Windows")) {

                            }else{
                                antInstallPath = usrMsg+File.separatorChar+"ant";
                                System.out.println("ANT 파일을 압축 해제합니다.");
                                unZipUtil.unZipFile(antFilePath,antInstallPath);
                                System.out.println("성공!");
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
             System.out.println(e.getMessage());
            LOG.error("CHECK FILE ALIVE FILE EXCEPTION :"+e.getMessage());
        }
    }



    //tomcat install
    public void tomcatInstall(String javaInstallPath) throws Exception{



        String osName = System.getProperty("os.name");

        //파일 경로에 APPLICATION 파일이 있는지 확인한다.
        String tomcatFilePath = "";

        if (osName.contains("Windows")) {
            tomcatFilePath = APPLICATION_FILE_PATH+ File.separatorChar + TOMCAT_FILE_NAME_WINDOW;
        }else{
            tomcatFilePath = APPLICATION_FILE_PATH+ File.separatorChar + TOMCAT_FILE_NAME_LINUX;
        }


        try{
            File tomcatFile = new File(tomcatFilePath);

            boolean tomcatPass = false;

            if(tomcatFile.isFile()){
                tomcatPass = true;
            }else{
                 System.out.println("TOMCAT 파일이 존재하지 않습니다. 해당 경로에 파일이 존재하는지 확인해주세요. :"+tomcatFilePath);
            }

            if(tomcatPass){
                unZipUtil unZipUtil = new unZipUtil();
                String usrMsg = "";
                String usrMsgApplication = "";

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
                            String updateText = "";

                            if (osName.contains("Windows")) {
                                updateText =  "set \"CATALINA_HOME="+ tomcatIntallPath +"\"\r\n" +
                                              "set \"JAVA_HOME="+javaInstallPath+"\"\r\n" + "setlocal\r\n";
                            }else{
                                updateText =  "export \"CATALINA_HOME="+ tomcatIntallPath +"\"\r\n" +
                                              "export \"JAVA_HOME="+javaInstallPath+"\"\r\n" + "setlocal\r\n";
                            }


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
                                 System.out.println(e.getMessage());
                            }



                            ShellCommander shellCommanderApplication = new ShellCommander();
                            System.out.println("젠킨스를 설치하시려면 Y / 솔루션을 설치하시려면 N을 입력하세요. ( WAR 기준 )");
                            BufferedReader inApplication = new BufferedReader(new InputStreamReader(System.in));
                            usrMsgApplication = inApplication.readLine();

                            if(("Y").equals(usrMsgApplication)){
                                System.out.println("젠킨스 파일을 복사합니다.");
                                // 젠킨스 파일을 복사한다.
                                shellCommander.execute("copy "+APPLICATION_FILE_PATH+File.separatorChar+JENKINS_FILE_NAME+" "+usrMsg+File.separatorChar+"tomcat"+File.separatorChar+"webapps");
                                System.out.println("성공!");
                            }else{
                                System.out.println("솔루션 파일을 복사합니다.");
                                // 젠킨스 파일을 복사한다.
                                shellCommander.execute("copy "+APPLICATION_FILE_PATH+File.separatorChar+SOLUTION_FILE_NAME+" "+usrMsg+File.separatorChar+"tomcat"+File.separatorChar+"webapps");
                                System.out.println("성공!");
                            }



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
                             System.out.println(e.getMessage());
                        }
                    }
                }
            }
        }catch (Exception e){
             System.out.println(e.getMessage());
            LOG.error("CHECK FILE ALIVE FILE EXCEPTION :"+e.getMessage());
        }
    }


    //솔루션 자동 설치용
    public void tomcatSolutionInstall(String javaInstallPath) throws Exception{

        String osName = System.getProperty("os.name");

        //파일 경로에 APPLICATION 파일이 있는지 확인한다.
        String tomcatFilePath = "";

        if (osName.contains("Windows")) {
            tomcatFilePath = APPLICATION_FILE_PATH+ File.separatorChar + TOMCAT_FILE_NAME_WINDOW;
        }else{
            tomcatFilePath = APPLICATION_FILE_PATH+ File.separatorChar + TOMCAT_FILE_NAME_LINUX;
        }

        try{
            File tomcatFile = new File(tomcatFilePath);

            boolean tomcatPass = false;

            if(tomcatFile.isFile()){
                tomcatPass = true;
            }else{
                 System.out.println("TOMCAT 파일이 존재하지 않습니다. 해당 경로에 파일이 존재하는지 확인해주세요. :"+tomcatFilePath);
            }

            if(tomcatPass){
                unZipUtil unZipUtil = new unZipUtil();
                String usrMsg = "";
                String usrMsgApplication = "";

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
                            String updateText = "";

                            if (osName.contains("Windows")) {
                                updateText =  "set \"CATALINA_HOME="+ tomcatIntallPath +"\"\r\n" +
                                        "set \"JAVA_HOME="+javaInstallPath+"\"\r\n" + "setlocal\r\n";
                            }else{
                                updateText =  "export \"CATALINA_HOME="+ tomcatIntallPath +"\"\r\n" +
                                        "export \"JAVA_HOME="+javaInstallPath+"\"\r\n" + "setlocal\r\n";
                            }


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

                                Writer outFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
                                outFile.write(repLine);
                                outFile.close();


                                bufferedReader.close();
                            }catch (Exception e){
                                 System.out.println(e.getMessage());
                            }

                            String webappsRootPath = usrMsg+"\\tomcat\\webapps\\ROOT";

                            //ant install    xcopy C:\project\autobuild\application\deploy_out\*.* C:\test\tomcat\webapps\ROOT /e /h /k
                            //this.antInstall(webappsRootPath);
                            System.out.println("솔루션 파일을 복사합니다.");
                            shellCommander.execute("xcopy "+APPLICATION_FILE_PATH+File.separatorChar+"deploy_out"+File.separatorChar+"*.*"+" "+usrMsg+File.separatorChar+"tomcat"+File.separatorChar+"webapps"+File.separatorChar+"ROOT /e /h /k");
                            System.out.println("성공!");


                            //db connection
                            this.checkDBConnection(webappsRootPath);


                            /*System.out.println("솔루션 파일을 복사합니다.");
                            // 젠킨스 파일을 복사한다.
                            shellCommander.execute("copy "+APPLICATION_FILE_PATH+File.separatorChar+SOLUTION_FILE_NAME+" "+usrMsg+File.separatorChar+"tomcat"+File.separatorChar+"webapps");
                            System.out.println("성공!");*/


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
                             System.out.println(e.getMessage());
                        }
                    }
                }
            }
        }catch (Exception e){
             System.out.println(e.getMessage());
            LOG.error("CHECK FILE ALIVE FILE EXCEPTION :"+e.getMessage());
        }
    }


    // DB 연결 테스트 및 datasource.properties 설정 및 파일 move
    public void checkDBConnection(String webappsPath){


        Connection connection = null;


        String usrMsg = "";
        String usrMsgApplication = "";

        ShellCommander shellCommander = new ShellCommander();


        try{
            System.out.println("DB 타입을 선택하세요. 1: ORACLE  , 2: MSSQL , 3: MARIADB , 4: POSTGRESQL  (숫자입력)");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            usrMsg = in.readLine();

            if(StringUtil.isEmpty(usrMsg)){
                System.out.println("DB타입을 정상적으로 입력되지 않았습니다. 다시 입력해주세요.");
                usrMsg = in.readLine();
            }

            if(StringUtil.isEmpty(usrMsg)){
                System.out.println("두 번 잘못 입력하셨습니다. 프로그램을 종료합니다.");
            }else if(!StringUtil.isEmpty(usrMsg)){

                String driverName = "";
                String url = "";
                String user = "";
                String password ="";
                String ip = "";
                String sqlType = "";
                String dialectClass = "";
                String javatype1 = "";
                String javatype2 = "";
                String javatype3 = "";



                String ipUsrMsg = "";
                String ipREUsrMsg = "";

                String portUsrMsg = "";
                String portREUsrMsg = "";

                String databaseUsrMsg = "";
                String databaseREUsrMsg = "";

                String userIdUsrMsg = "";
                String userIdREUsrMsg = "";

                String passWordUsrMsg = "";
                String passWordREUsrMsg = "";
                /**
                 * public final String JDBC_URL_POSTGRESQL = "jdbc:postgresql://192.168.5.42:5432/srm9op";
                 *     public final String JDBC_URL_MARIADB = "jdbc:mariadb://192.168.5.42:3306/srm9op";
                 *     public final String JDBC_URL_MSSQL = "jdbc:sqlserver://192.168.5.42:14333;databaseName=srm9op";
                 *     public final String JDBC_URL_ORACLE = "jdbc:oracle:thin:@175.124.141.220:1521:emro";
                 */

                System.out.println("DB 연결하고자 하는 IP를 입력해주세요.");
                BufferedReader inIp = new BufferedReader(new InputStreamReader(System.in));
                ipUsrMsg = inIp.readLine(); //ip inline

                System.out.println(ipUsrMsg + "가 맞으면, Y를 입력해주시고, 아니라면 N을 입력해주세요.");
                BufferedReader inIp2 = new BufferedReader(new InputStreamReader(System.in));
                ipREUsrMsg = inIp2.readLine(); //ip2 inline

                if(!ipREUsrMsg.toUpperCase().equals("Y")){
                    System.out.println("DB 연결하고자 하는 IP를 다시 입력해주세요.");
                    inIp = new BufferedReader(new InputStreamReader(System.in));
                    ipUsrMsg = inIp.readLine(); //ip inline
                }

                System.out.println("DB 연결하고자 하는 PORT 를 입력해주세요.");
                BufferedReader inPort = new BufferedReader(new InputStreamReader(System.in));
                portUsrMsg = inPort.readLine(); //port inline

                System.out.println(portUsrMsg + "가 맞으면, Y를 입력해주시고, 아니라면 N을 입력해주세요.");
                BufferedReader inPort2 = new BufferedReader(new InputStreamReader(System.in));
                portREUsrMsg = inPort2.readLine(); //port2 inline

                if(!portREUsrMsg.toUpperCase().equals("Y")){
                    System.out.println("DB 연결하고자 하는 PORT 다시 입력해주세요.");
                    inPort = new BufferedReader(new InputStreamReader(System.in));
                    portUsrMsg = inPort.readLine(); //port inline
                }


                System.out.println("DB 연결하고자 하는 DATABASE NAME 를 입력해주세요.");
                BufferedReader inDataBase = new BufferedReader(new InputStreamReader(System.in));
                databaseUsrMsg = inDataBase.readLine(); //DATABASE inline


                System.out.println(databaseUsrMsg + "가 맞으면, Y를 입력해주시고, 아니라면 N을 입력해주세요.");
                BufferedReader inDataBase2 = new BufferedReader(new InputStreamReader(System.in));
                databaseREUsrMsg = inDataBase2.readLine(); //DATABASE2 inline

                if(!databaseREUsrMsg.toUpperCase().equals("Y")){
                    System.out.println("DB 연결하고자 하는 DATABASE NAME 를 다시 입력해주세요.");
                    inDataBase = new BufferedReader(new InputStreamReader(System.in));
                    databaseUsrMsg = inDataBase.readLine(); //DATABASE inline
                }

                System.out.println("DB 연결하고자 하는 USER ID 를 입력해주세요.");
                BufferedReader inUser = new BufferedReader(new InputStreamReader(System.in));
                userIdUsrMsg = inUser.readLine(); //USER ID inline


                System.out.println(userIdUsrMsg + "가 맞으면, Y를 입력해주시고, 아니라면 N을 입력해주세요.");
                BufferedReader inUser2 = new BufferedReader(new InputStreamReader(System.in));
                userIdREUsrMsg = inUser2.readLine(); //USER ID2 inline

                if(!userIdREUsrMsg.toUpperCase().equals("Y")){
                    System.out.println("DB 연결하고자 하는 USER ID 를 다시 입력해주세요.");
                    inUser = new BufferedReader(new InputStreamReader(System.in));
                    userIdUsrMsg = inUser.readLine(); //USER ID inline
                }


                System.out.println("DB 연결하고자 하는 USER PASSWORD 를 입력해주세요.");
                BufferedReader inPassword = new BufferedReader(new InputStreamReader(System.in));
                passWordUsrMsg = inPassword.readLine(); //USER PASSWORD inline


                System.out.println(passWordUsrMsg + "가 맞으면, Y를 입력해주시고, 아니라면 N을 입력해주세요.");
                BufferedReader inPassword2 = new BufferedReader(new InputStreamReader(System.in));
                passWordREUsrMsg = inPassword2.readLine(); //USER PASSWORD2 inline

                if(!passWordREUsrMsg.toUpperCase().equals("Y")){
                    System.out.println("DB 연결하고자 하는 USER PASSWORD 를 다시 입력해주세요.");
                    inPassword = new BufferedReader(new InputStreamReader(System.in));
                    passWordUsrMsg = inPassword.readLine(); //USER PASSWORD inline
                }


                // jdbc url 만들기
                if(usrMsg.equals("1")){ //oracle
                    driverName = JDBC_DRIVER_CLASS_NAME_ORACLE;
                    url = JDBC_URL_ORACLE+ipUsrMsg+":"+portUsrMsg+":"+databaseUsrMsg;


                    sqlType = SQLTYPE_ORACLE;
                    dialectClass = DATASOURCE_DIALECT_CLASS_ORACLE;
                    javatype1 = ORACLE_TYPE_HANDLER_JAVATYPE1;
                    javatype2 = ORACLE_TYPE_HANDLER_JAVATYPE2;
                    javatype3 = ORACLE_TYPE_HANDLER_JAVATYPE3;

                }else if(usrMsg.equals("2")){ //mssql
                    driverName = JDBC_DRIVER_CLASS_NAME_MSSQL;
                    url = JDBC_URL_MSSQL+ipUsrMsg+":"+portUsrMsg+";databaseName="+databaseUsrMsg;

                    sqlType = SQLTYPE_MSSQL;
                    dialectClass = DATASOURCE_DIALECT_CLASS_MSSQL;
                    javatype1 = MSSQL_TYPE_HANDLER_JAVATYPE1;
                    javatype2 = MSSQL_TYPE_HANDLER_JAVATYPE2;
                    javatype3 = MSSQL_TYPE_HANDLER_JAVATYPE3;


                }else if(usrMsg.equals("3")){ // maria & mssql
                    driverName = JDBC_DRIVER_CLASS_NAME_MARIADB;
                    url = JDBC_URL_MARIADB+ipUsrMsg+":"+portUsrMsg+"/"+databaseUsrMsg;

                    sqlType = SQLTYPE_MARIADB;
                    dialectClass = DATASOURCE_DIALECT_CLASS_MARIADB;
                    javatype1 = MYSQL_TYPE_HANDLER_JAVATYPE1;
                    javatype2 = MYSQL_TYPE_HANDLER_JAVATYPE2;
                    javatype3 = MYSQL_TYPE_HANDLER_JAVATYPE3;

                }else if(usrMsg.equals("4")){ // postgresql
                    driverName = JDBC_DRIVER_CLASS_NAME_POSTGRESQL;
                    url = JDBC_URL_POSTGRESQL+ipUsrMsg+":"+portUsrMsg+"/"+databaseUsrMsg;

                    sqlType = SQLTYPE_POSTGRESQL;
                    dialectClass = DATASOURCE_DIALECT_CLASS_POSTGRESQL;
                    javatype1 = POSTGRESQL_TYPE_HANDLER_JAVATYPE1;
                    javatype2 = POSTGRESQL_TYPE_HANDLER_JAVATYPE2;
                    javatype3 = POSTGRESQL_TYPE_HANDLER_JAVATYPE3;

                }

                user = userIdUsrMsg;
                password = passWordUsrMsg;


                //JDBC 연결
                try{
                    Class.forName(driverName);
                    connection = DriverManager.getConnection(url,user,password);

                    System.out.println("정상적으로 연결되었습니다.");
                    System.out.println(connection);
                }catch (ClassNotFoundException e){
                    System.out.println("[로드 오류]\n" + e.getStackTrace());
                } catch (SQLException e)   {
                    System.out.println("[연결 오류]\n" +  e.getStackTrace());
                }catch (Exception e){
                     System.out.println(e.getMessage());
                }finally {
                    if(connection != null) connection.close();
                }


                //datasource.properties 수정하기
                int buffer;
                String repLine = "";
                String line = "";


                try{
                    // read
                    File file = new File(APPLICATION_FILE_PATH+ File.separatorChar + DATASOURCE_TEMP_FILE_NAME);
                    BufferedReader fileBufferedReader = new BufferedReader(new FileReader(file));
                    while ((line = fileBufferedReader.readLine()) != null) {
                        // 일치하는 패턴에서는 바꿀 문자로 변환

                        line = line.replaceAll(DATASOURCE_SQLTYPE, sqlType);
                        line = line.replaceAll(DATASOURCE_DRIVER_CLASSNAME, driverName);
                        line = line.replaceAll(DATASOURCE_URL, url);
                        line = line.replaceAll(DATASOURCE_USERNAME, userIdUsrMsg);
                        line = line.replaceAll(DATASOURCE_PASSWORD, passWordUsrMsg);
                        line = line.replaceAll(DATASOURCE_DIALECTCLASS, dialectClass);
                        line = line.replaceAll(DATASOURCE_TYPE_HANDLER_JAVATYPE1, javatype1);
                        line = line.replaceAll(DATASOURCE_TYPE_HANDLER_JAVATYPE2, javatype2);
                        line = line.replaceAll(DATASOURCE_TYPE_HANDLER_JAVATYPE3, javatype3);
                        repLine += line;
                        repLine += "\r\n";
                    }
                    // 새로운 파일에 쓴다.
                    File new_file = new File(webappsPath+ File.separatorChar + DATASOURCE_FILE_PATH +  File.separatorChar + DATASOURCE_FILE_NAME);
                    Writer outFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new_file), "utf-8"));
                    outFile.write(repLine);
                    outFile.close();


                    fileBufferedReader.close();
                }catch (Exception e){
                     System.out.println(e.getMessage());
                }
            }
        }catch (Exception e){
            System.out.println("실패!");
            LOG.error(e.getMessage());
             System.out.println(e.getMessage());
        }

    }

    public void antBuild(String webappsPath,String antInstallPath){

        //build 수정하기
        int buffer;
        String repLine = "";
        String line = "";
        ShellCommander shellCommander = new ShellCommander();

        try{
            // read
            File file = new File(APPLICATION_FILE_PATH+ File.separatorChar + "ROOT" +File.separatorChar +"build.xml");
            BufferedReader fileBufferedReader = new BufferedReader(new FileReader(file));
            while ((line = fileBufferedReader.readLine()) != null) {
                // 일치하는 패턴에서는 바꿀 문자로 변환
                repLine += line.replaceAll(BUILD_ANT_PATH, webappsPath);
                repLine += "\r\n";
            }
            // 새로운 파일에 쓴다.
            Writer outFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
            outFile.write(repLine);
            outFile.close();
            fileBufferedReader.close();
        }catch (Exception e){
             System.out.println(e.getMessage());
        }


        //ant build command 처리
        try{
            System.out.println("ANT를 통하여 프로젝트를 Deploy합니다.");
            System.out.println("deploy start ==========================================================" );
            System.out.println(antInstallPath+File.separatorChar +"bin" + File.separatorChar +"ant -f" +  APPLICATION_FILE_PATH+ File.separatorChar + "ROOT deploy");

            //ProcessBuilder processBuilder = new ProcessBuilder();
            //processBuilder.command("cmd.exe", "/c", antInstallPath+File.separatorChar +"bin" + File.separatorChar +"ant -f " +  APPLICATION_FILE_PATH+ File.separatorChar + "ROOT deploy");
            shellCommander.execute("ant -f " +  APPLICATION_FILE_PATH+ File.separatorChar + "ROOT deploy");


            System.out.println("ant deploy end");



        }catch (Exception e){
             System.out.println(e.getMessage());
        }





    }


    public static void main(String[] arg){
        AutoApplicationBuild_2 autoApplicationBuild = new AutoApplicationBuild_2();
        try{
            autoApplicationBuild.javaInstall();
            //autoApplicationBuild.antInstall("C:\\test\\tomcat\\webapps");
        }catch (Exception e){
             System.out.println(e.getMessage());
        }
    }




}



