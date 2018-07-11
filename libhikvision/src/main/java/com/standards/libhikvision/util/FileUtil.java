package com.standards.libhikvision.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function <描述功能>
 * @date: 2018/6/27 16:50
 */

public class FileUtil {

    public static File getVideoDirPath(String directoryName) {
        File var1 = null;
        try {
            File var0 = Environment.getExternalStorageDirectory();
            var1 = new File(var0.getAbsolutePath() + File.separator + "stdCache" + File.separator + directoryName);
            if (!var1.exists()) {
                var1.mkdirs();
                var1.createNewFile();
            }
        } catch (IOException var3) {
            var3.printStackTrace();
        }
        return var1;
    }

    public static List<File> visitFileInPath(String path, String extension) {
        List<File> files = new ArrayList<>();
        File directory = new File(path);
        if (directory.exists()) {
            files = Arrays.asList(directory.listFiles((dir, name) -> name.contains(extension)));
            Collections.sort(files, (o1, o2) -> o2.getName().compareTo(o1.getName()));
        }
        return files;
    }
}
