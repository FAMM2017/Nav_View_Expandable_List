package com.example.fmonasterios.nav_view_expandable_list.General;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by fmonasterios on 5/31/2017.
 */

public class BitmapResized {

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
    {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // return the new Bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }
}
