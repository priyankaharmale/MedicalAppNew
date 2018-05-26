 package com.edwardvanraak.medicalapp.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.medicalapp.R;

 public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView tvUser, tvUserContact;
    NavigationView navigationView;
    boolean doubleBackToExitPressedOnce = false;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);


        setPermission();


    }



    @Override
    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
//Toast.makeText(this,"some ",Toast.LENGTH_SHORT).show();

        int id = item.getItemId();
        Intent intent = null;

        switch (id) {
            case R.id.nav_current_location:
                Toast.makeText(this,"Current Location", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                Toast.makeText(this,"Current Location", Toast.LENGTH_SHORT).show();

                  /*  intent = new Intent(getBaseContext(), CustomerProfileActivity.class);*/
              /*  SharedPreferences settings = getSharedPreferences(CONSTANTS.BICSPREFERENCES.toString(), Context.MODE_PRIVATE);
                settings.edit().clear().commit();
                intent = new Intent(getBaseContext(), HomeActivity.class);
                startActivity(intent);
                finish();*/
                break;

            case R.id.nav_rate_us:
                Toast.makeText(this,"Current Location", Toast.LENGTH_SHORT).show();

                //Toast.makeText(this,"Need app url ",Toast.LENGTH_SHORT).show();
                break;



            default:
                break;
        }
        if (intent != null) {

            startActivity(intent);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }


    protected void setPermission() {
            // navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);

            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
           /* tvUser.setText("Guest User");
            tvUserContact.setText("Welcome to Autobuddy");
*/


    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment_profile is destroyed
        super.onStop();
    }






    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                    Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access location data.", Toast.LENGTH_LONG).show();

                }
                break;

        }
    }


}
