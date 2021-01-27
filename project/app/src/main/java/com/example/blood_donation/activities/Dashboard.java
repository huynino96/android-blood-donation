package com.example.blood_donation.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.blood_donation.R;
import com.example.blood_donation.TimerService;
import com.example.blood_donation.broadcast.MyApplication;
import com.example.blood_donation.fragments.AboutUs;
import com.example.blood_donation.fragments.AchievementView;
import com.example.blood_donation.fragments.BloodInfo;
import com.example.blood_donation.fragments.HomeView;
import com.example.blood_donation.fragments.NearByHospitalActivity;
import com.example.blood_donation.fragments.SearchDonor;
import com.example.blood_donation.model.Donor;
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

// Main screen for regular users
// Same as main screen for admins
public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private TextView getUserName;
    private TextView getUserEmail;
    private FirebaseUser cur_user;
    private FloatingActionButton fab;
    private ProgressDialog pd;
    private String[] bloodGroup, divisionList;
    private int userBloodGroup;
    private int userDivision;
    private String lastDonate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Check internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            changeTextStatus(true);
        } else {
            changeTextStatus(false);
        }

        // Display load animation and greet current user
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase user_db = FirebaseDatabase.getInstance();
        cur_user = mAuth.getCurrentUser();
        bloodGroup = getResources().getStringArray(R.array.Blood_Group);
        divisionList = getResources().getStringArray(R.array.division_list);
        DatabaseReference userdb_ref = user_db.getReference("users");
        DatabaseReference donordb_ref = user_db.getReference("donors");

        getUserEmail = findViewById(R.id.UserEmailView);
        getUserName = findViewById(R.id.UserNameView);

        // Take user to posting activity (create new blood post)
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, PostActivity.class));
            }
        });

        // Update and display donation info
        Query singleUser = userdb_ref.child(cur_user.getUid());
        singleUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    getUserName.setText(user.getName());
                    userBloodGroup = user.getBloodGroup();
                    userDivision = user.getDivision();
                    getUserEmail.setText(cur_user.getEmail());

                    Query userLastDonate = donordb_ref
                            .child(divisionList[userDivision])
                            .child(bloodGroup[userBloodGroup])
                            .child(cur_user.getUid());

                    userLastDonate.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                Donor donor = dataSnapshot.getValue(Donor.class);
//                            Log.d(getApplicationContext() + "", "Last donate: " + donor.getLastDonate() + "");
//                            Log.d(getApplicationContext() + "", "UID: " + donor.getUID() + "");
                                assert donor != null;
                                lastDonate = donor.getLastDonate();
                                Intent intent = new Intent(getApplicationContext(), TimerService.class);
                                serviceRunner(intent);
                                pd.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(getApplicationContext() + "", "This user has no donation info.");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

        // Display regular user drawer
        setUpDrawer(savedInstanceState);
    }

    private void setUpDrawer(Bundle savedInstanceState){
        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new HomeView()).commit();
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

    // Close layout if back button is pressed
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

    private void serviceRunner(Intent intent){
        stopService(intent);
        intent.putExtra("lastDonate", lastDonate);
        startService(intent);
    }
}

