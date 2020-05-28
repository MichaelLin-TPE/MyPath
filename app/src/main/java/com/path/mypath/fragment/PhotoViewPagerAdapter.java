package com.path.mypath.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.tools.ImageLoaderProvider;

import java.util.ArrayList;
import java.util.Locale;

public class PhotoViewPagerAdapter extends PagerAdapter {

    private Context context;

    private ArrayList<String> dataArray;

    private OnPhotoClickListener listener;

    public void setOnPhotoClickListener(OnPhotoClickListener listener){
        this.listener = listener;
    }

    public PhotoViewPagerAdapter(Context context, ArrayList<String> dataArray) {
        this.context = context;
        this.dataArray = dataArray;
    }

    @Override
    public int getCount() {
        return dataArray == null ? 0 : dataArray.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_pager_item,null);
        TextView tvCount = view.findViewById(R.id.share_text_pic_count);
        ImageView ivPhoto = view.findViewById(R.id.share_select_photo);
        String url = dataArray.get(position);
        ImageLoaderProvider.getInstance(context).setImage(url,ivPhoto);
        tvCount.setText(String.format(Locale.getDefault(),"%d/%d",position+1,dataArray.size()));

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick();
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    public interface OnPhotoClickListener{
        void onClick();
    }
}
