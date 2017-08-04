package com.example.fmonasterios.nav_view_expandable_list.Memories_List;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.fmonasterios.nav_view_expandable_list.General.Database;
import com.example.fmonasterios.nav_view_expandable_list.General.BitmapResized;
import com.example.fmonasterios.nav_view_expandable_list.MainActivity;
import com.example.fmonasterios.nav_view_expandable_list.R;
import com.example.fmonasterios.nav_view_expandable_list.General.Variables;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Add_Update_Memories extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ImageView img;
    Database database;
    SQLiteDatabase db;
    int mYear,mMonth,mDay,MY_PERMISSIONS_REQUEST_CAMERA=1;
    String encodedImage="";
    boolean permission_camera=false,memorie_duplicate=false;
    TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_memories);

        //initialize database
        database =new Database(getApplicationContext());
        db = database.getReadableDatabase();


        final EditText et_title = (EditText) findViewById(R.id.title);
        final EditText et_description = (EditText) findViewById(R.id.description);
        date = (TextView) findViewById(R.id.date);
        final Button btn_add=(Button)findViewById(R.id.add);
        final Button btn_selectdate=(Button)findViewById(R.id.selectdate);
        img = (ImageView)findViewById(R.id.imageView1);


        if (Variables.editar==0){
            //Add option
            btn_add.setText(getString(R.string.title_btn_view_add));
        }else{
            //update option
            btn_add.setText(getString(R.string.title_btn_view_update));
            db = database.getReadableDatabase();

            final String[] argsel = {Variables.seleccionado};
            final String seleccion = "title" + "=?";

            String[] projection = {"date","title","description","image"};
            Cursor c = db.query
                    ("memories", projection,
                            seleccion, argsel, null, null, null);


            if (c.moveToFirst()) {
                // Load the memory in the view for update
                do {

                    et_title.setText(c.getString(1));
                    et_description.setText(c.getString(2));
                    date.setText(c.getString(0));
                    encodedImage=c.getString(3);

                    if (encodedImage.equals("")){
                        Glide.with(this).load(R.drawable.camera).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).override(200, 200)
                                .into(new BitmapImageViewTarget(img) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        //Circular img
                                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                        img.setImageDrawable(circularBitmapDrawable);
                                    }
                                });
                    }else{
                        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), decodedByte);
                        circularBitmapDrawable.setCornerRadius(decodedByte.getWidth());
                        circularBitmapDrawable.setCircular(true);
                        img.setImageDrawable(circularBitmapDrawable);
                    }


                } while (c.moveToNext());
            }
            c.close();
            db.close();



        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            //The application does not have permission to use the camera

            // Should we show an explanation for use camera?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                // if you want you can show an expanation to the user here
            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
        else{
            //The application have permission to use the camera
            permission_camera=true;
        }

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Button to take a photo pressed
                if (permission_camera){

                    //If we have permission to use the camera we proceed to create the intent to take the photo
                    Intent cameraIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);

                    if (getResources().getConfiguration().orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                        //If the orientation of the phone is in portrait mode, we set the camera in this way
                        cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                    //call the camera with return (forResult)
                    startActivityForResult(cameraIntent, 1);
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.should_permise_camera),Toast.LENGTH_LONG).show();
                }



            }});

        btn_selectdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //button for select date
                Date date = new Date();
                String dia = new SimpleDateFormat("dd",Locale.getDefault()).format(date);
                String mes = new SimpleDateFormat("MM",Locale.getDefault()).format(date);
                String año = new SimpleDateFormat("yyyy",Locale.getDefault()).format(date);
                mYear     = Integer.parseInt(año);
                mMonth    = (Integer.parseInt(mes)-1);
                mDay      = Integer.parseInt(dia);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Add_Update_Memories.this, Add_Update_Memories.this, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memorie_duplicate = false;
                
                //VALIDATION
                if (et_title.length() == 0 || et_description.length() == 0 || date.length() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_fill_all_data), Toast.LENGTH_LONG).show();
                } else {

                    //We set the title, with the first letter in uppercase
                    String title_initial_uppercase = et_title.getText().toString();
                    title_initial_uppercase = title_initial_uppercase.toLowerCase();
                    title_initial_uppercase = Character.toUpperCase(title_initial_uppercase.charAt(0)) + title_initial_uppercase.substring(1, title_initial_uppercase.length());

                    String duplicate_title = "";
                    if (Variables.editar == 0) {
                        //If we are adding a memory check if it already exists in the database

                        db = database.getReadableDatabase();
                        if (et_title.length() > 0) {

                            String[] argsel = {title_initial_uppercase};
                            String seleccion = "title" + "=?";
                            String[] projection = {"title"};

                            Cursor c = db.query("memories", projection, seleccion, argsel, null, null, null);

                            if (c.moveToFirst()) {
                                    duplicate_title = (c.getString(0));
                            }
                            c.close();
                            db.close();


                            if (duplicate_title.length() > 0 & duplicate_title.equalsIgnoreCase(et_title.getText().toString())) {
                                Toast.makeText(getApplicationContext(), getString(R.string.duplicate_memories) + et_title.getText().toString(), Toast.LENGTH_LONG).show();
                                memorie_duplicate = true;
                            }
                        }
                    }

                if (!memorie_duplicate){

                    //The title entered does not exist in the database
                    
                    db = database.getWritableDatabase();
                    ContentValues valores = new ContentValues();

                    valores.put("date", date.getText().toString());
                    valores.put("title", title_initial_uppercase);
                    valores.put("description", et_description.getText().toString());
                    valores.put("image", encodedImage);

                    if (Variables.editar == 0) {
                        //add memories
                        db.insert("memories", null, valores);
                        db.close();
                        Toast.makeText(getApplicationContext(), getString(R.string.memorie_added), Toast.LENGTH_LONG).show();
                        et_title.setText("");
                    } else {
                        //update memories
                        String fran = Variables.seleccionado;
                        final String[] argsel = {fran};
                        final String seleccion = "title" + "=?";

                        db.update("memories", valores, seleccion, argsel);
                        db.close();
                        Toast.makeText(getApplicationContext(), getString(R.string.memorie_update), Toast.LENGTH_LONG).show();
                    }

                    if (MainActivity.active) {
                        //If the main activity is open we restart it to update the data
                        try {
                            WeakReference<MainActivity> close = MainActivity.getInstance();
                            close.get().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Intent intento = new Intent(Add_Update_Memories.this, MainActivity.class);
                    finish();
                    startActivity(intento);

                }
                    
                }
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, :D!
                    permission_camera=true;
                } else {
                    permission_camera=false;
                    // permission denied, :(!
                }
                return;
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //We verified that the photo was taken
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bMap;
            if (data.hasExtra("data")){
                bMap =data.getParcelableExtra("data");
                BitmapResized bitmapresized=new BitmapResized();
                //We reduce the image and show it in the imageview
                bMap=bitmapresized.getResizedBitmap(bMap,500,350);
                img.setImageBitmap(bMap);

                //we convert the image to a string to save it to the database
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bMap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] photo = baos.toByteArray();
                encodedImage = Base64.encodeToString(photo, Base64.DEFAULT);
            }else{
                Toast.makeText(getApplicationContext(),getString(R.string.photo_not_captured),Toast.LENGTH_SHORT).show();
            }



        }
    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        //Shows date selected in textview date
        date.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(dayOfMonth).append("/0")
                        .append(month + 1).append("/")
                        .append(year).append(" "));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

}
