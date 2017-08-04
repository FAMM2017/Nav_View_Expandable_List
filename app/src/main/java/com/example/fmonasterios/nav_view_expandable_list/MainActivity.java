package com.example.fmonasterios.nav_view_expandable_list;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fmonasterios.nav_view_expandable_list.General.Database;
import com.example.fmonasterios.nav_view_expandable_list.General.SimpleDividerItemDecoration;
import com.example.fmonasterios.nav_view_expandable_list.General.Variables;
import com.example.fmonasterios.nav_view_expandable_list.Memories_List.Adapter_Memories;
import com.example.fmonasterios.nav_view_expandable_list.Memories_List.Add_Update_Memories;
import com.example.fmonasterios.nav_view_expandable_list.Memories_List.Items_Memories;
import com.example.fmonasterios.nav_view_expandable_list.Side_Menu.Navigationview_click;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    int textlength,mYear, mMonth,mDay;
    static final int DATE_DIALOG_ID = 0;
    Database database;
    SQLiteDatabase db;
    Cursor c;
    MenuItem edit,delete,search;
    SearchView searchView;
    Toolbar toolbar;
    ArrayList<Items_Memories> items,items2;
    Adapter_Memories adapter_memories, adapter_memories_search;
    RecyclerView rv_list_memories;
    TextView tx_empty_list;
    boolean search_by_date=false;

    public static boolean active = false;
    static WeakReference<MainActivity> reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize toolbar and navigation view
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ExpandableListView expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final Navigationview_click Nav_View=new Navigationview_click(this,this);
        expandableList.setGroupIndicator(null);
        expandableList.setAdapter(Nav_View.adapterdelnavigation(navigationView));

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Nav_View.clicksubmenu(i,i1);
                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Nav_View.clickmenu(i);
                return false;
            }
        });

        reference =new WeakReference<MainActivity>(this);
        active = true;

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Variables.editar=0;
                Intent intento= new Intent(MainActivity.this,Add_Update_Memories.class);
                startActivity(intento);
            }
        });

        tx_empty_list =(TextView)findViewById(R.id.tx_empy_list);
        rv_list_memories = (RecyclerView)findViewById(R.id.rv_memories);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv_list_memories.setLayoutManager(llm);
        rv_list_memories.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));

        items = new ArrayList<Items_Memories>();
        items2 = new ArrayList<Items_Memories>();

        adapter_memories = new Adapter_Memories(MainActivity.this,items);
        adapter_memories_search = new Adapter_Memories(MainActivity.this,items2);

        database =new Database(getApplicationContext());
        db = database.getReadableDatabase();


        String[] projection={"date","title","description","image"};
        String sortOrder
                = "date" + " ASC";
        c= db.query("memories",projection,
                        null,null,null,null,sortOrder);

        if (c.moveToFirst()) {
            //we fill the adapter with all memories in database
            do {
                items.add(new Items_Memories(c.getString(1),c.getString(3),c.getString(0),c.getString(2)));

            } while(c.moveToNext());
        }
        c.close();
        rv_list_memories.setAdapter(adapter_memories);

        if (items.isEmpty()) {
            rv_list_memories.setVisibility(View.GONE);
            tx_empty_list.setVisibility(View.VISIBLE);
        }

        db.close();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        edit =menu.findItem(R.id.edit);
        delete =menu.findItem(R.id.delete);

        search = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        final SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                edit.setVisible(false);
                delete.setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                edit.setVisible(true);
                delete.setVisible(true);
                return true;
            }
        });

        //add the button calendar in searchview
        Toolbar.LayoutParams navButtonsParams = new Toolbar.LayoutParams(toolbar.getHeight() * 2 / 3, toolbar.getHeight() * 2 / 3);
        Button btn_calendar = new Button(this);
        btn_calendar.setBackground(getResources().getDrawable(R.drawable.calendar));
        ((LinearLayout) searchView.getChildAt(0)).addView(btn_calendar, navButtonsParams);
        ((LinearLayout) searchView.getChildAt(0)).setGravity(Gravity.CENTER);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // when the user change the value of newtext (Text to look for) we enter this cycle
                textlength = newText.length();
                items2.clear();
                Variables.seleccionado=null;
                search_by_date=false;

                if (newText.contains("/")){
                    //search mode
                    search_by_date=true;
                }

                for (int i = 0; i < adapter_memories.getItemCount(); i++)
                {

                    if (!search_by_date){
                        if (textlength <= adapter_memories.getname(i).length() )
                        {

                            if(adapter_memories.getname(i).toLowerCase().contains(
                                    newText.toLowerCase().trim()))
                            {
                                items2.add(new Items_Memories(adapter_memories.getname(i), adapter_memories.getimage(i), adapter_memories.getdate(i), adapter_memories.getdescription(i)));
                            }
                        }

                    }else{
                        if (adapter_memories.getdate(i).toLowerCase().trim().equals(
                                newText.toLowerCase().trim())) {
                            items2.add(new Items_Memories(adapter_memories.getname(i), adapter_memories.getimage(i), adapter_memories.getdate(i), adapter_memories.getdescription(i)));

                        }
                    }
                }

                if (newText.length()>0){
                    rv_list_memories.setAdapter(adapter_memories_search);
                }
                else{
                    rv_list_memories.setAdapter(adapter_memories);
                }


                rv_list_memories.setVisibility(View.VISIBLE);
                tx_empty_list.setVisibility(View.GONE);

                if (items2.isEmpty()) {
                    rv_list_memories.setVisibility(View.GONE);
                    tx_empty_list.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });

        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date = new Date();
                String dia = new SimpleDateFormat("dd", Locale.getDefault()).format(date);
                String mes = new SimpleDateFormat("MM",Locale.getDefault()).format(date);
                String año = new SimpleDateFormat("yyyy",Locale.getDefault()).format(date);

                mYear     = Integer.parseInt(año);
                mMonth    = (Integer.parseInt(mes)-1);
                mDay      = Integer.parseInt(dia);
                showDialog(DATE_DIALOG_ID);
            }
        });

        return true;
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    // display the current date
                    if(monthOfYear<9){
                        searchView.setQuery(new StringBuilder()
                                // Month is 0 based so add 1
                                .append(mDay).append("/0")
                                .append(mMonth + 1).append("/")
                                .append(mYear).append(""), true);

                    }
                    else{
                        searchView.setQuery(new StringBuilder()
                                // Month is 0 based so add 1
                                .append(mDay).append("/")
                                .append(mMonth + 1).append("/")
                                .append(mYear).append(""), true);
                    }

                    View view2 = MainActivity.this.getCurrentFocus();
                    if (view2 != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                        searchView.clearFocus();
                    }
                }
            };
    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case DATE_DIALOG_ID:

                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent intento;
        switch (id){

            case R.id.edit:
                if (Variables.seleccionado == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_select_memories_toupdate), Toast.LENGTH_LONG).show();
                } else {

                    Variables.editar=1;
                    intento= new Intent(MainActivity.this,Add_Update_Memories.class);
                    startActivity(intento);
                }
                break;

            case R.id.delete:

                if (Variables.seleccionado == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_select_memories_todelete), Toast.LENGTH_LONG).show();
                } else {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);


                    alert.setMessage(getString(R.string.warning_delete_memories) + Variables.seleccionado + " ?");


                    alert.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            db = database.getWritableDatabase();
                            String[] argsel = {Variables.seleccionado};
                            String seleccion = "title" + "=?";
                            db.delete("memories", seleccion, argsel);
                            db.close();
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), getString(R.string.deleted_memories) + Variables.seleccionado, Toast.LENGTH_SHORT).show();
                        }
                    });
                    alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });


                    alert.show();
                }

                break;



        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    public static WeakReference<MainActivity> getInstance(){
        return reference;
    }//Weak main activity reference

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //Button pressed back, we closed the application
            if(Build.VERSION.SDK_INT >= 21)
                finishAndRemoveTask ();
            else
                finishAffinity();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
