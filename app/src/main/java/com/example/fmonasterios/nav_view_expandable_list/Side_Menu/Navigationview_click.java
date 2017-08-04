package com.example.fmonasterios.nav_view_expandable_list.Side_Menu;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.fmonasterios.nav_view_expandable_list.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fmonasterios on 5/5/2017.
 */

public class Navigationview_click{
    //This class handles everything related to the nav view, if any item is pressed, then it is directed to the corresponding activity.
    private Context context;
    private Activity activity;
    private DrawerLayout drawer;
    ExpandableListAdapter mMenuAdapter;
    private ExpandableListView expandableList;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<String>> listDataChild;
    private Intent intent;

    public Navigationview_click(Context context, Activity activity) {
        this.context = context;
        this.activity=activity;

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CALL_PHONE)) {

                // Show an expanation to the user

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CALL_PHONE},
                        1);
            }
        }
    }


    private void setupDrawerContent(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        final ImageView img_user_logo = (ImageView) headerView.findViewById(R.id.imageView);

 Glide.with(context).load(R.drawable.famm).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).override(350, 200)
                .into(new BitmapImageViewTarget(img_user_logo) {
            @Override
            protected void setResource(Bitmap resource) {
                //Circular img
               RoundedBitmapDrawable circularBitmapDrawable =
               RoundedBitmapDrawableFactory.create(context.getResources(), resource);
               circularBitmapDrawable.setCircular(true);
                //circularBitmapDrawable.setCornerRadius(Math.max(resource.getWidth(), resource.getHeight()) / 2.0f);
                img_user_logo.setImageDrawable(circularBitmapDrawable);
            }
        });
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawer.closeDrawers();
                        return true;
                    }
                });

    }

    private HashMap prepareListData() {
        listDataHeader = new ArrayList<ExpandedMenuModel>();
        listDataChild = new HashMap<ExpandedMenuModel, List<String>>();


        ExpandedMenuModel item1 = new ExpandedMenuModel();
        item1.setIconName(context.getString(R.string.personal_date));
        item1.setIconImg(R.drawable.icon_personal_date);
        listDataHeader.add(item1);

        ExpandedMenuModel item2 = new ExpandedMenuModel();
        item2.setIconName(context.getString(R.string.phone));
        item2.setIconImg(R.drawable.icon_phone);
        listDataHeader.add(item2);

        ExpandedMenuModel item3 = new ExpandedMenuModel();
        item3.setIconName(context.getString(R.string.Send_email));
        item3.setIconImg(R.drawable.icon_send_mail);
        listDataHeader.add(item3);

        // Adding child data

        List<String> personal_date = new ArrayList<String>();
        personal_date.add(context.getString(R.string.age));
        personal_date.add(context.getString(R.string.born));
        personal_date.add(context.getString(R.string.location));
        personal_date.add(context.getString(R.string.passport));

        List<String> contact = new ArrayList<String>();
        contact.add(context.getString(R.string.phone1));
        contact.add(context.getString(R.string.phone2));

        List<String> sendemail = new ArrayList<String>();

        listDataChild.put(listDataHeader.get(0), personal_date);// Header, Child data
        listDataChild.put(listDataHeader.get(1), contact);
        listDataChild.put(listDataHeader.get(2), sendemail);

        return listDataChild;
    }

    private List getlistdataheader(){
        return listDataHeader;
    }


    public ExpandableListAdapter adapterdelnavigation(NavigationView navigationView){

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        HashMap<ExpandedMenuModel, List<String>> listDataChild =prepareListData();

        List<ExpandedMenuModel> listDataHeader=getlistdataheader();
        ExpandableListAdapter mMenuAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild, expandableList);
        return mMenuAdapter;
    }


    public void clickmenu(int i){
        drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);

        switch(i){

            case 2:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"fammelectronica@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Asunto");
                intent.putExtra(Intent.EXTRA_TEXT   , "body of email");
                try {
                    activity.startActivity(Intent.createChooser(intent, "Enviar correo a Francisco Monasterios..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, "No se puede enviar el email", Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }

    public void clicksubmenu(int i, int i1){

        drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);

        switch(i){

            case 1://
                Intent intent;
                switch (i1){
                    case 0:
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + context.getString(R.string.phone1)));
                        if (ContextCompat.checkSelfPermission(context,
                                Manifest.permission.CALL_PHONE)
                                == PackageManager.PERMISSION_GRANTED) {
                            activity.startActivity(intent);
                        }else{
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    1);
                        }

                        break;

                    case 1:
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + context.getString(R.string.phone2)));
                        if (ContextCompat.checkSelfPermission(context,
                                Manifest.permission.CALL_PHONE)
                                == PackageManager.PERMISSION_GRANTED) {
                            activity.startActivity(intent);
                        }else{
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    1);
                        }
                        break;


                }
                break;
            case 2:


                break;

            case 3:

                break;
            default:

        }

    }


}
