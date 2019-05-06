package za.co.tcg.touchtutorlauncher.utility;

import java.io.File;

public class StringUtils {

    public static String getFontSafeString(String s){
        return s.replaceAll("[^a-zA-Z0-9\\s]", "");
    }

    public static String getFileName(File file) {
        String fileName = file.getName();
        if (fileName.indexOf(".") > 0)
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        return getFontSafeString(fileName);
    }
}
