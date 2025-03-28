package edu.cnm.deepdive.trailtrack.controller;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.trailtrack.R;
import edu.cnm.deepdive.trailtrack.databinding.ActivityMainBinding;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {


  private ActivityMainBinding binding;
  private NavController navController;
  private AppBarConfiguration appBarConfig;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    //Adding our own toolbar
    setupNavigation();
  }

  @Override
  public boolean onSupportNavigateUp() {
    return NavigationUI.navigateUp(navController, appBarConfig);
  }

  private void setupNavigation() {
    setSupportActionBar(binding.toolbar);
    appBarConfig = new AppBarConfiguration.Builder(R.id.home_fragment, R.id.pre_login_fragment, R.id.login_fragment)
        .build();
    navController =((NavHostFragment) binding.navHostContainer.getFragment()).getNavController();
    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
  }



  // TODO: 3/27/25 Figure out how to add Location permission

//  private void getLocationPermission() {
//    /*
//     * Request location permission, so that we can get the location of the
//     * device. The result of the permission request is handled by a callback,
//     * onRequestPermissionsResult.
//     */
//    if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//        android.Manifest.permission.ACCESS_FINE_LOCATION)
//        == PackageManager.PERMISSION_GRANTED) {
//      locationPermissionGranted = true;
//    } else {
//      ActivityCompat.requestPermissions(this,
//          new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//          PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//    }
//  }


}