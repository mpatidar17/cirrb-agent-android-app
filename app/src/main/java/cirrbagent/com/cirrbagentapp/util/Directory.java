package cirrbagent.com.cirrbagentapp.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by yuva on 22/6/17.
 */

public interface Directory {

    class StoragePaths {
        public static final String EXTERNAL_ROOT_PATH = Environment.getExternalStorageDirectory().toString() + File.separator + "Cirrb";
        public static final String INTERNAL_ROOT_PATH = Environment.getDataDirectory().toString() + File.separator + "Cirrb";
        public static final String ALL_SPORTS_PATH = "All Sports";
        public static final String IMAGES_PATH = "Images";


        public static class PrefStrings {
            public static final String CREATED_DIR_PATH = "createdDirPath";
        }
    }
}
