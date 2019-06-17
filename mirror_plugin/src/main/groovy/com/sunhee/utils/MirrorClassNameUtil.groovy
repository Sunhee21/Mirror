package com.sunhee.utils


public class MirrorClassNameUtil {


    static boolean isEmpty(String text) {
        return text == null || text.trim().length() < 1
    }

    static String path2ClassName(String pathName) {
        String finalClassName = pathName.replace(File.separator, ".")
        if (finalClassName.endsWith(".class")){
            finalClassName =  finalClassName.substring(0,finalClassName.length()-6)
        }
        return finalClassName
    }

    /**
     * 全类名转换下符号，例如"xxx.xxx.xxx" -> "xxx/xxx/xxx"
     */
    static String changeClassNameSeparator(String classname) {
        return classname.replace('.', '/')
    }

}