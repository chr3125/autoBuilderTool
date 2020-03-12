package util;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class unZipUtil {


    // 압축 해제를 진행
    public void unZipFile(String fileFullPath,String unzipDirPath){
        try {
            ZipFile zipFile = new ZipFile(fileFullPath);
            zipFile.extractAll(unzipDirPath);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
}
