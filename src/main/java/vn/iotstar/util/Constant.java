
package vn.iotstar.util;

import java.io.File;

public class Constant {

    /**
     * Thu muc luu anh upload.
     * Mac dinh: home cua user/upload/category
     * Windows: C:/Users/ten/upload/category
     * Co the doi thanh E:/upload neu muon.
     */
    public static final String DIR =
            System.getProperty("user.home")
                    + File.separator + "upload"
                    + File.separator + "category";

    /** Thu muc luu poster va file video do nguoi dung upload: home/upload/video */
    public static final String VIDEO_DIR =
            System.getProperty("user.home")
                    + File.separator + "upload"
                    + File.separator + "video";

    private Constant() {
    }
}
