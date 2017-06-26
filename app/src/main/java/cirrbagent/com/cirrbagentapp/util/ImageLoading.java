package cirrbagent.com.cirrbagentapp.util;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by admin on 30/09/16.
 */

public class ImageLoading {

    public static DisplayImageOptions getDisplayImageOption(int cornerRedius, int defaultImage) {
        DisplayImageOptions options = new DisplayImageOptions.Builder().considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true)
                .cacheOnDisc(true)
                .resetViewBeforeLoading(true)
                .displayer(new RoundedBitmapDisplayer(cornerRedius))
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();

        return options;
    }

    public static ImageLoader initImageLoader(Context context, ImageLoader imageLoader) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app\

        imageLoader.getInstance().init(config.build());
        return imageLoader;
    }
}
