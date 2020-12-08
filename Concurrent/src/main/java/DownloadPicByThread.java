package main.java;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DownloadPicByThread extends Thread{
    private String uri;
    private String fileName;

    public DownloadPicByThread(String uri, String fileName){
        this.uri = uri;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        WebDownload downloader = new WebDownload();
        downloader.download(uri, fileName);
        System.out.println("download file: " + fileName);
    }

    public static void main(String[] args) {
        DownloadPicByThread t1 = new DownloadPicByThread("https://github.com/LianxinGao/Keep_On_Growing/blob/master/images/blob1.jpg?raw=true","./Thread/download/1.jpg");
        DownloadPicByThread t2 = new DownloadPicByThread("https://github.com/LianxinGao/Keep_On_Growing/blob/master/images/blob2.jpg?raw=true","./Thread/download/2.jpg");
        DownloadPicByThread t3 = new DownloadPicByThread("https://github.com/LianxinGao/Keep_On_Growing/blob/master/images/blob3.jpg?raw=true","./Thread/download/3.jpg");
        t1.start();
        t2.start();
        t3.start();
    }
}

class WebDownload{
    public void download(String uri, String fileName){
        try {

            FileUtils.copyURLToFile(new URL(uri), new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}