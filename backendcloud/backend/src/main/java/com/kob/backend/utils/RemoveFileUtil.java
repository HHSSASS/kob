package com.kob.backend.utils;

import java.io.File;

public class RemoveFileUtil {
    public static void remove(String path){
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
