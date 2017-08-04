package com.example.fmonasterios.nav_view_expandable_list.Memories_List;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.fmonasterios.nav_view_expandable_list.General.Database;
import com.example.fmonasterios.nav_view_expandable_list.General.BitmapResized;
import com.example.fmonasterios.nav_view_expandable_list.R;
import com.example.fmonasterios.nav_view_expandable_list.General.Variables;
import com.example.fmonasterios.nav_view_expandable_list.General.View_more;

import java.util.ArrayList;

/**
 * Created by fmonasterios on 3/28/2017.
 */

public class Adapter_Memories extends RecyclerView.Adapter<Adapter_Memories.MyViewHolder> {

    private ArrayList<Items_Memories> itemsArrayList;
    private Context context;
    private Database BaseDatos;
    private SQLiteDatabase db;
    private Cursor c;
    private int numerodeitem,numeromaximo;


    public Adapter_Memories(Context context, ArrayList<Items_Memories> itemsArrayList) {
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public int getItemCount() {

        return itemsArrayList.size();
    }



    public String getname(int pos) {
        return itemsArrayList.get(pos).getname();
    }
    public String getimage(int pos) {
        return itemsArrayList.get(pos).getimage();
    }
    public String getdate(int pos) {
        return itemsArrayList.get(pos).getdate();
    }
    public String getdescription(int pos) {
        return itemsArrayList.get(pos).getdescription();
    }


    @Override
    public void onBindViewHolder(final MyViewHolder itemsViewHolder, final int i) {


        itemsViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                return true;
                    }
                });

            itemsViewHolder.title.setText(itemsArrayList.get(i).getname());
            itemsViewHolder.date.setText(itemsArrayList.get(i).getdate());
            itemsViewHolder.description.setText(itemsArrayList.get(i).getdescription());


        String dedImage=itemsArrayList.get(i).getimage();
        if (dedImage.equals("")){
            Glide.with(context).load(R.drawable.camera).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).override(150, 150)
                    .into(new BitmapImageViewTarget(itemsViewHolder.imagen) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            //Circular img
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            //circularBitmapDrawable.setCornerRadius(Math.max(resource.getWidth(), resource.getHeight()) / 2.0f);
                            itemsViewHolder.imagen.setImageDrawable(circularBitmapDrawable);
                        }
                    });

        }else {
            byte[] decodedString = Base64.decode(dedImage, Base64.DEFAULT);

            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            BitmapResized bitmapresized = new BitmapResized();
            decodedByte = bitmapresized.getResizedBitmap(decodedByte, 150, 150);
            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), decodedByte);
            circularBitmapDrawable.setCornerRadius(decodedByte.getWidth());
            circularBitmapDrawable.setCircular(true);
            itemsViewHolder.imagen.setImageDrawable(circularBitmapDrawable);
        }

            if(Variables.selectedPos == i){
                itemsViewHolder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.edge_memories_selected));
            }else{
                itemsViewHolder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.edge_memories));
                itemsViewHolder.itemView.setSelected(false);


            }

            itemsViewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if(Variables.selectedPos != i){
                        if (Variables.selectedPos!=-1){
                            notifyItemChanged(Variables.selectedPos);
                        }
                        Variables.selectedPos = i;
                        notifyItemChanged(Variables.selectedPos);
                    }
                    if(i>=0){
                        Variables.seleccionado=itemsArrayList.get(i).getname();

                    }
                    else{
                        Variables.seleccionado=null;
                    }

                    return false;
                }
            });

        itemsViewHolder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Variables.selectedPos != i){
                    if (Variables.selectedPos!=-1){
                        notifyItemChanged(Variables.selectedPos);
                    }
                    Variables.selectedPos = i;
                    notifyItemChanged(Variables.selectedPos);
                }
                Variables.seleccionado=itemsArrayList.get(i).getname();


                    itemsViewHolder.dialog.show();
                    BaseDatos=new Database(context);
                    db = BaseDatos.getReadableDatabase();
                    final String[] argsel = {Variables.seleccionado};
                    final String seleccion = "title" + "=?";

                    final String[] projection = {"date","title","description","image"};

                    c = db.query
                            ("memories", projection,
                                    seleccion, argsel, null, null, null);


                    if (c.moveToFirst()) {
                        do {
                            itemsViewHolder.inicializarvermas.llenar(c);
                        } while (c.moveToNext());
                    }
                    c.close();
                    db.close();

                    itemsViewHolder.hverizq.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            db = BaseDatos.getReadableDatabase();
                            c = db.query
                                    ("memories", projection,
                                            null, null, null, null, null);

                            if (c.moveToFirst()) {
                                do {

                                    if(c.getString(1).equals(itemsViewHolder.title_popup.getText().toString())){

                                        numerodeitem=c.getPosition();
                                        numeromaximo=c.getCount();
                                        numerodeitem=numerodeitem+1;
                                    }

                                } while (c.moveToNext());
                            }

                            if(numerodeitem>=numeromaximo){
                                numerodeitem=0;

                                c.moveToPosition(numerodeitem);
                                itemsViewHolder.inicializarvermas.llenar(c);

                            }else{

                                c.moveToPosition(numerodeitem);
                                itemsViewHolder.inicializarvermas.llenar(c);
                            }
                            c.close();
                            db.close();

                        }
                    });


                    itemsViewHolder.hverder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            db = BaseDatos.getReadableDatabase();
                            c = db.query
                                    ("memories", projection,
                                            null, null, null, null, null);

                            if (c.moveToFirst()) {
                                do {

                                    if(c.getString(1).equals(itemsViewHolder.title_popup.getText().toString())){

                                        numerodeitem=c.getPosition();
                                        numeromaximo=c.getCount();
                                        numerodeitem=numerodeitem-1;

                                    }

                                } while (c.moveToNext());
                            }

                            if(numerodeitem<0){
                                numerodeitem=numeromaximo-1;
                                c.moveToPosition(numerodeitem);
                                itemsViewHolder.inicializarvermas.llenar(c);

                            }else{

                                c.moveToPosition(numerodeitem);
                                itemsViewHolder.inicializarvermas.llenar(c);
                            }
                            c.close();
                            db.close();
                        }
                    });


            }
        });

    }





    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView;

            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.memories_item_view, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title,date,description,title_popup,date_popup,description_popup;
        ImageView imagen,imagenvermas;

        Button hverizq,hverder;
        AlertDialog dialog;
        LayoutInflater inflater;
        AlertDialog.Builder alerto;
        View_more inicializarvermas;


        public MyViewHolder(View rowView) {
            super(rowView);
            Context context = itemView.getContext();

            title = (TextView) rowView.findViewById(R.id.title);
            imagen = (ImageView) rowView.findViewById(R.id.imageView1);
            date = (TextView) rowView.findViewById(R.id.date);
            description = (TextView) rowView.findViewById(R.id.description);
            alerto = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);

            View dialoglayout = inflater.inflate(R.layout.memories_view_more, null);
            title_popup = (TextView) dialoglayout.findViewById(R.id.title);
            date_popup = (TextView) dialoglayout.findViewById(R.id.date);
            description_popup = (TextView) dialoglayout.findViewById(R.id.description);
            hverizq=(Button) dialoglayout.findViewById(R.id.vizq);
            hverder=(Button) dialoglayout.findViewById(R.id.vder);
            imagenvermas = (ImageView) dialoglayout.findViewById(R.id.imageViewvermas);

            alerto.setView(dialoglayout);
            alerto.setCancelable(true);
            dialog = alerto.create();
            inicializarvermas=new View_more(title_popup,date_popup,description_popup,imagenvermas,context);

        }
    }
}