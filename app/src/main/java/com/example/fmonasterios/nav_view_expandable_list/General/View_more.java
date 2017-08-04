package com.example.fmonasterios.nav_view_expandable_list.General;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.fmonasterios.nav_view_expandable_list.R;

/**
 * Created by fmonasterios on 5/22/2017.
 */

public class View_more {

    private TextView title_popup,date_popup,description_popup;
    private String dedImage;
    private ImageView imagenvermas;
    private Context context;


    public View_more(TextView title_popup, TextView date_popup, TextView description_popup, ImageView imagenvermas, Context context) {
        this.title_popup=title_popup;
        this.date_popup=date_popup;
        this.description_popup=description_popup;
        this.imagenvermas=imagenvermas;
        this.context=context;

    }

    public void llenar(Cursor c){

        title_popup.setText(c.getString(1));
        date_popup.setText(c.getString(0));
        description_popup.setText(c.getString(2));
        dedImage=c.getString(3);

        if (dedImage.equals("")){
            Glide.with(context).load(R.drawable.camera).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).override(350, 350)
                    .into(new BitmapImageViewTarget(imagenvermas) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            //Circular img
                            RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            //circularBitmapDrawable.setCornerRadius(Math.max(resource.getWidth(), resource.getHeight()) / 2.0f);
                            imagenvermas.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }else{
            final byte[] decodedString = Base64.decode(dedImage, Base64.DEFAULT);

            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imagenvermas.setImageBitmap(decodedByte);
        }


    }


}
