package com.tct.note.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by xlli on 6/25/15.
 */
public class FileUtils {
    private static FileUtils instance = new FileUtils();
    private int bgd_id;

    public static FileUtils getInstance() {
        return instance;
    }

    /**
     * delete file
     *
     * @param f file
     */
    public void del(File f) throws IOException {
        if (f.exists() && f.isDirectory()) {// determent there are whether file or path or not
            if (f.listFiles().length == 0) {// directly to delete,if the path hasnot contains files
                f.delete();
            } else {// if the path contains files,determent the path has child path.
                File delFile[] = f.listFiles();
                int i = f.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        del(delFile[j].getAbsolutePath());// recursive invoke del function to delete child path file
                    }
                    delFile[j].delete();// delete file
                }
                f.delete();
            }
        }
    }

    /**
     * delete file
     * <p/>
     * param: filepath
     */
    public void del(String filepath) throws IOException {
        del(new File(filepath));
    }

    public long fileSize(String filePath) {
        File f = new File(filePath);
        return fileSize(f);
    }

    public long fileSize(File f) {
        return f.length();
    }

    public long getFileSizes(File f) throws Exception {
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available();
        }
        return s;
    }

    public long getDirectorySize(File f) throws Exception {

        long size = 0;
        if (f.exists()) {
            File flist[] = f.listFiles();
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getDirectorySize(flist[i]);
                } else {
                    size = size + flist[i].length();
                }
            }
        }
        return size;
    }

    public long getDirectorySize(String filepath) throws Exception {
        return getDirectorySize(new File(filepath));
    }
}
