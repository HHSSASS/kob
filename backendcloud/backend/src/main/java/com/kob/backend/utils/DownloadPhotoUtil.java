package com.kob.backend.utils;

import java.io.*;
import java.net.URL;

public class DownloadPhotoUtil {
    public static boolean downloadPhoto(String urlString,String path){
        try {
            URL url = new URL(urlString);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
            return true;
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
