package com.path.mypath.photo_activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;

import java.util.ArrayList;
import java.util.Locale;

public class ViewPagerAdapter extends PagerAdapter {

    private ArrayList<byte[]> dataArray;

    private Context context;

    public ViewPagerAdapter(ArrayList<byte[]> dataArray, Context context) {
        this.dataArray = dataArray;
        this.context = context;
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
        RoundedImageView ivPhoto = view.findViewById(R.id.share_select_photo);
        byte[] bytes = dataArray.get(position);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        ivPhoto.setImageBitmap(bitmap);
        tvCount.setText(String.format(Locale.getDefault(),"%d/%d",position+1,dataArray.size()));

        container.addView(view);

        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
