package com.julendieguez.quenecesito;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.julendieguez.quenecesito.database.DatabaseFunctions;
import com.julendieguez.quenecesito.database.Group;
import com.julendieguez.quenecesito.database.User;
import com.julendieguez.quenecesito.fragments.CreateNewGroupFragment;
import com.julendieguez.quenecesito.fragments.CreateNewItemFragment;
import com.julendieguez.quenecesito.fragments.EditAccountFragment;
import com.julendieguez.quenecesito.fragments.GroupListFragment;
import com.julendieguez.quenecesito.fragments.ItemListFragment;
import com.julendieguez.quenecesito.fragments.UserContactsFragment;

public class MainMenuActivity extends AppCompatActivity {
    private static FirebaseDatabase mDatabase;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private TextView userName, userNumber;
    private User userData;
    private Group groupData;
    private boolean tbNavigationListenerIsRegistered = false;
    private boolean itemFragmentIsShown = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setUpFireBase();
        setReferences();
        setDefaultFragment();
    }

    private void setUpFireBase(){
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userDataReference = mDatabase.getReference("users/"+DatabaseFunctions.getUserUID(FirebaseAuth.getInstance()));
        userDataReference.keepSynced(true);
        userDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userData = DatabaseFunctions.fillUserWithDataSnapshot(dataSnapshot);
                userName.setText(userData.getName());
                userNumber.setText(userData.getTelephone());
                Fragment f = getSupportFragmentManager().findFragmentByTag("groupListFragment");
                if(f instanceof GroupListFragment){
                    ((GroupListFragment) f).setUserData(userData);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setReferences(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = setDrawerToggle();
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);
        View hamburguerView = navigationView.getHeaderView(0);
        userName = (TextView)hamburguerView.findViewById(R.id.hamburgerUserName);
        userNumber = (TextView)hamburguerView.findViewById(R.id.hamburgerNumber);
        setDrawerContent();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_MENU:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onKeyDown(keycode, e);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_addUser){
            changeToContactsFragment();
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(itemFragmentIsShown)
            getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(itemFragmentIsShown) {
            menu.add(R.id.groupItems, R.id.action_addUser, 0, getString(R.string.menuAddUser));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void setDrawerContent(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item){
                if(item.getItemId() == R.id.action_signOut){
                    FirebaseAuth.getInstance().signOut();
                    changeActivityToLogIn();
                }else{
                    selectDrawerItem(item);
                }
                return true;
            }
        });
    }
    private ActionBarDrawerToggle setDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    public void changeMenuType(boolean hamburger){
        if(!hamburger){
            drawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if(!tbNavigationListenerIsRegistered){
                drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
                tbNavigationListenerIsRegistered = true;
            }
        }else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            drawerToggle.setDrawerIndicatorEnabled(true);
            drawerToggle.setToolbarNavigationClickListener(null);
            tbNavigationListenerIsRegistered = false;
            setTitle(getString(R.string.app_name));
        }
    }

    public void changeMenuValues(boolean b){
        itemFragmentIsShown = b;
        invalidateOptionsMenu();
    }



    private void changeActivityToLogIn(){
        Intent logInActivity = new Intent(this, MainActivity.class);
        TaskStackBuilder.create(getApplicationContext()).addNextIntentWithParentStack(logInActivity).startActivities();
    }


    //GETTERS
    public User getUserData(){
        return userData;
    }
    public Group getGroupData(){
        return groupData;
    }
    public FirebaseDatabase getmDatabase(){
        return mDatabase;
    }

    //FRAGMENT CHANGE FUNCTIONS
    public void changeToItemFragment(final Group g){
        groupData = g;
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.pop_exit).replace(R.id.flContent, new ItemListFragment()).addToBackStack(null).commit();
        setTitle(g.getName());
    }
    public void changeToCreateItemFragment(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.pop_exit).replace(R.id.flContent, new CreateNewItemFragment()).addToBackStack(null).commit();
        setTitle(getString(R.string.createItem));
    }

    public void setDefaultFragment(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.pop_exit).replace(R.id.flContent, new GroupListFragment(),"groupListFragment").commit();
        setTitle(getString(R.string.app_name));
    }

    private void changeToContactsFragment(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.pop_exit).replace(R.id.flContent, new UserContactsFragment()).addToBackStack(null).commit();
        setTitle(getString(R.string.menuAddUser));
    }
    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.action_settings:
                fragment = new EditAccountFragment();
                break;
            case R.id.action_createNew:
                fragment = new CreateNewGroupFragment();
                break;
            default:
                break;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.pop_enter,R.anim.pop_exit);
        fragmentTransaction.replace(R.id.flContent, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();
        changeMenuType(false);
    }

    //HIDE KEYBOARD ON TOUCH
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
