package com.path.mypath.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.path.mypath.R;

public class ImageLoaderProvider {

    private static ImageLoaderProvider instance = null;

    private DisplayImageOptions options;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private ImageLoaderProvider(Context context){
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.user_not_press)
                .showImageOnFail(R.drawable.user_not_press)
                .showImageOnLoading(R.drawable.user_not_press)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(options).build();
        imageLoader.init(config);
    }

    public static ImageLoaderProvider getInstance(Context context){
        if (instance == null){
            instance = new ImageLoaderProvider(context);
        }
        return instance;
    }

    public void setImage(String photoUrl, RoundedImageView imageView){
        imageLoader.displayImage(photoUrl,imageView);
    }
    public void setImage(String photoUrl, ImageView imageView){
        imageLoader.displayImage(photoUrl,imageView);
    }
}
