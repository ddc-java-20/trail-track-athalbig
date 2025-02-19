package edu.cnm.deepdive.notes.controller;

import static android.Manifest.permission.CAMERA;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.notes.R;
import edu.cnm.deepdive.notes.controller.ExplanationFragment.OnDismissListener;
import edu.cnm.deepdive.notes.databinding.ActivityMainBinding;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements OnDismissListener {

  private static final int PERMISSIONS_REQUEST_CODE = 674;
  
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
    setupPermissions();
  }

  @Override
  public boolean onSupportNavigateUp() {
    return NavigationUI.navigateUp(navController, appBarConfig);
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == PERMISSIONS_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // TODO: 2/19/25 Consider saving this information.
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  @Override
  public void onDismiss() {
    requestPermissions(new String[]{CAMERA}, PERMISSIONS_REQUEST_CODE);
  }

  private void setupNavigation() {
    setSupportActionBar(binding.toolbar);
    appBarConfig = new AppBarConfiguration.Builder(R.id.home_fragment).build();
    navController =((NavHostFragment) binding.navHostContainer.getFragment()).getNavController();
    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
  }

  private void setupPermissions() {
    if (shouldRequestCameraPermission()){
      if (shouldExplainCameraPermission()){
        navController.navigate(HomeFragmentDirections.openExplanationFragment());
      } else {
          onDismiss();
      }
    } else {
      // TODO: 2/19/25 Store result if appropriate.
    }
  }

  private boolean shouldRequestCameraPermission() {
    return ContextCompat.checkSelfPermission(this, CAMERA)
        != PackageManager.PERMISSION_GRANTED;
  }

  private boolean shouldExplainCameraPermission() {
    return ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA);
  }
}