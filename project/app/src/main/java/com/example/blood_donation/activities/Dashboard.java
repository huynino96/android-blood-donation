package com.example.blood_donation.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.blood_donation.R;
import com.example.blood_donation.broadcast.MyApplication;
import com.example.blood_donation.fragments.AboutUs;
import com.example.blood_donation.fragments.AchievementView;
import com.example.blood_donation.fragments.BloodInfo;
import com.example.blood_donation.fragments.HomeView;
import com.example.blood_donation.fragments.NearByHospitalActivity;
import com.example.blood_donation.fragments.SearchDonor;
import com.example.blood_donation.fragments.UserList;
import com.example.blood_donation.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;



public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private TextView getUserName;
    private TextView getUserEmail;
    private FirebaseUser cur_user;
    private FloatingActionButton fab;
    private ProgressDialog pd;
    private Boolean isAdmin = false;
    private Boolean roleAdmin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            changeTextStatus(true);
        } else {
            changeTextStatus(false);
        }

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase user_db = FirebaseDatabase.getInstance();
        cur_user = mAuth.getCurrentUser();
        DatabaseReference userdb_ref = user_db.getReference("users");

        getUserEmail = findViewById(R.id.UserEmailView);
        getUserName = findViewById(R.id.UserNameView);

        Query singleUser = userdb_ref.child(cur_user.getUid());
        pd.show();

        setUpDrawer(savedInstanceState);

        singleUser.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //pd.show();
                User user =  dataSnapshot.getValue(User.class);
                String name = user.getName();
                getUserName.setText(name);
                getUserEmail.setText(cur_user.getEmail());
                //If admin show the admin site
                if (user.getRole() != null){
                    if (user.getRole().equals("admin")){
                        isAdmin = true;
                        roleAdmin = true;
                        setUpDrawer(savedInstanceState);
                    }
                }
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });



    }

    private void setUpDrawer(Bundle savedInstanceState){

        if(savedInstanceState == null)
        {
            if (isAdmin && roleAdmin){
                setContentView(R.layout.activity_admin);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new UserList()).commit();
            }
            else {
                setContentView(R.layout.activity_dashboard);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new HomeView()).commit();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        getUserEmail = (TextView) header.findViewById(R.id.UserEmailView);
        getUserName = (TextView) header.findViewById(R.id.UserNameView);

        navigationView.getMenu().getItem(0).setChecked(true);

    }

    public void changeTextStatus(boolean isConnected) {

        // Change status according to boolean value
        if (isConnected) {
            Toast.makeText(getApplicationContext(), "Internet is connected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "There is no internet connection", Toast.LENGTH_LONG).show();

        }
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
        getMenuInflater().inflate(R.menu.dashboard, menu);
        if (isAdmin){
            menu.findItem(R.id.switchRole).setVisible(true);
            if (roleAdmin){
                menu.findItem(R.id.switchRole).setTitle("Switch to member");
            }else {
                menu.findItem(R.id.switchRole).setTitle("Switch to admin");

            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.donateinfo) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new BloodInfo()).commit();
        }
        if (id == R.id.devinfo) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new AboutUs()).commit();
        }
        if (id == R.id.switchRole){
            roleAdmin = !roleAdmin;
            setUpDrawer(null);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new HomeView()).commit();
            fab.setVisibility(View.VISIBLE);

        } else if (id == R.id.userprofile) {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

        }
        else if (id == R.id.user_achiev) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new AchievementView()).commit();

        }
        else if (id == R.id.logout) {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        }
        else if (id == R.id.blood_storage){

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new SearchDonor()).commit();

        } else if (id == R.id.nearby_hospital) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new NearByHospitalActivity()).commit();
            fab.setVisibility(View.INVISIBLE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        MyApplication.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();// On Resume notify the Application
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}

