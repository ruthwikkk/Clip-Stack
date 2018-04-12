package com.ruthwikwarrier.cbmanager.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.ruthwikwarrier.cbmanager.R;
import com.ruthwikwarrier.cbmanager.errorhandle.ExceptionHandler;
import com.ruthwikwarrier.cbmanager.utils.AppUtils;
import com.ruthwikwarrier.cbmanager.adapters.CBListAdapter;
import com.ruthwikwarrier.cbmanager.data.SharedPrefNames;
import com.ruthwikwarrier.cbmanager.database.DBHelper;
import com.ruthwikwarrier.cbmanager.model.ClipObject;
import com.ruthwikwarrier.cbmanager.services.CBWatchService;
import com.ruthwikwarrier.cbmanager.services.ClipActionBridge;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends /*BaseActivity*/ AppCompatActivity{

    public final static String EXTRA_IS_FROM_NOTIFICATION = "EXTRA.isFromNotification";
    public final static String EXTRA_IS_FROM_ADD = "EXTRA.isFromAdd";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rv_main_cblist) RecyclerView cbRecyclerView;
    @BindView(R.id.image_main_empty) ImageView imgEmptyList;
    @BindView(R.id.fab_main_addclip) FloatingActionButton btnAddClip;

    CBListAdapter adapter;

    ArrayList<ClipObject> clipList;
    DBHelper dbHelper;

    private SharedPreferences preference;
    boolean allowService = true;

    MenuItem itemSearch, itemStar;
    boolean isStarred = false;
    boolean isFromNotification;

    Context context;
    private Paint paint = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        context = this.getBaseContext();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Integer.parseInt("idid");

        preference = PreferenceManager.getDefaultSharedPreferences(this);
        readPreferences();

        Intent intent = getIntent();
        isFromNotification = intent.getBooleanExtra(EXTRA_IS_FROM_NOTIFICATION,false);
        Log.e("MainActivity","isFromNotification: "+isFromNotification);

        initViews(); //initialise views
        initToolbar(); //initialise toolbar
        setAdapter(); //setting data to recycler view, also used to refresh clip list


        //Start clip board watch service (When the app is installed and opened for first time)
        //Also checks in the preferences
        if(allowService){
            if( !AppUtils.isMyServiceRunning(CBWatchService.class, this))
                CBWatchService.startCBService(this);
            else
                Log.e("MainActivity","CBWatch Service already running.");
        }else
            Log.e("MainActivity","CBWatch Service permission false.");

    }

    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(AppUtils.dip2px(context, 4));
        } else {
            View mToolbarShadow = findViewById(R.id.my_toolbar_shadow);
            if (mToolbarShadow != null) {
                mToolbarShadow.setVisibility(View.VISIBLE);
            }
        }

        setAdapter();

        super.onResume();
    }

    private void initViews(){

        dbHelper =  new DBHelper(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        cbRecyclerView.setLayoutManager(mLayoutManager);
        cbRecyclerView.setItemAnimator(new DefaultItemAnimator());
        cbRecyclerView.setDrawingCacheEnabled(true);
        cbRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        initSwipeToDelete();

        btnAddClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent openIntent = new Intent(context, ClipActionBridge.class)
                        .putExtra(Intent.EXTRA_TEXT, EXTRA_IS_FROM_ADD)
                        .putExtra(ClipActionBridge.ACTION_CODE, ClipActionBridge.ACTION_ADD);
                context.startService(openIntent);

            }
        });


    }

    private void initToolbar(){

       // toolbar.setNavigationIcon(R.mipmap.ic_launcher_round);
       // toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    protected void setAdapter() {


        if(isStarred){ //Shows only favorite clips
            clipList = dbHelper.readFavClipHistory();
            if(clipList.size()<1){
                setViewVisibility(true);
            }else{
                adapter = new CBListAdapter(this, clipList, dbHelper, isFromNotification);
                cbRecyclerView.setAdapter(adapter);
            }

        }else{
            clipList = dbHelper.readAllClipHistory();
            if(clipList.size()<1){
                setViewVisibility(true);
            }else{
                adapter = new CBListAdapter(this, clipList, dbHelper, isFromNotification);
                cbRecyclerView.setAdapter(adapter);
                setViewVisibility(false);
            }
        }

    }

    private void initSwipeToDelete(){

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){
                    adapter.removeItem(position);
                } else {
                    adapter.removeItem(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        paint.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest, paint);
                    } else {
                        paint.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest, paint);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(cbRecyclerView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        itemSearch = menu.findItem(R.id.action_search);
        itemStar = menu.findItem(R.id.action_star);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) itemSearch.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        search(searchView);

        setStarredIcon();

        return super.onCreateOptionsMenu(menu);
    }

    protected void setStarredIcon() {

        //Function to set starred icon
        if (itemStar == null) return;
        if (isStarred) {
            itemStar.setIcon(R.drawable.ic_action_star_white);
        } else {
            itemStar.setIcon(R.drawable.ic_action_star_outline_white);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id){
            case R.id.action_search:
                return super.onOptionsItemSelected(item);
            case R.id.action_star:
                onStarredMenuClicked();
                break;
            case R.id.action_export:

                break;
            case R.id.action_delete_all:
                clearAllClips();
                setAdapter();//refreshing list
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
               break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void onStarredMenuClicked() {
        isStarred = !isStarred;
        setStarredIcon();
        setAdapter();//refreshing list
    }

    private void clearAllClips(){

        new AlertDialog.Builder(this)
                .setTitle(R.string.action_delete_all)
                .setMessage(getString(R.string.dialog_delete_all))
                .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.deleteAllClips();
                                setAdapter();
                            }
                        }
                )
                .setNegativeButton(getString(R.string.dialog_cancel), null)
                .create()
                .show();
    }

    private void readPreferences(){

        allowService = preference.getBoolean(SharedPrefNames.PREF_START_SERVICE, true);
    }

    private void setViewVisibility(boolean isListEmpty){

        if(isListEmpty){
            imgEmptyList.setVisibility(View.VISIBLE);
            cbRecyclerView.setVisibility(View.INVISIBLE);
        }else{
            imgEmptyList.setVisibility(View.GONE);
            cbRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}
