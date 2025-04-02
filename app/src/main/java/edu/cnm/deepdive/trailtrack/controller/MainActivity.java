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

/**
 * MainActivity serves as the primary entry point of the application and acts as the host for
 * navigation within the app. It is annotated with @AndroidEntryPoint to enable dependency
 * injection with Hilt. This activity is responsible for setting up and managing the application's
 * navigation components, including the toolbar and NavController.
 *
 * The activity uses a custom toolbar for navigation and integrates it with the navigation
 * architecture component using AppBarConfiguration and NavController.
 *
 * Lifecycle methods:
 * - onCreate: Initializes the activity, sets up data binding, and calls the navigation setup
 *   method to configure navigation and toolbar.
 *
 * Core methods:
 * - setupNavigation: Configures the AppBar with the NavController and associates the toolbar
 *   with the navigation graph, enabling proper navigation between screens.
 * - onSupportNavigateUp: Handles navigation when the user taps on the up button, delegating
 *   the action to NavigationUI.
 *
 * Note: The activity includes a placeholder for obtaining location permission but it is
 * currently commented out and marked as a TODO for future implementation.
 */
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

  /**
   * Configures the application's navigation components, integrating the toolbar with the app's
   * navigation graph and setting up proper handling of navigation actions.
   *
   * This method sets the activity's toolbar as the support ActionBar and establishes an
   * AppBarConfiguration that defines the top-level destinations within the app's navigation graph.
   * It retrieves the NavController from the NavHostFragment and binds it to the ActionBar to enable
   * navigation using the NavController.
   *
   * Core responsibilities:
   * - Sets up the custom toolbar as the ActionBar for the activity.
   * - Configures an AppBarConfiguration to manage navigation behaviors for top-level destinations.
   * - Associates the NavController with the ActionBar to synchronize navigation actions and UI behavior.
   */
  private void setupNavigation() {
    setSupportActionBar(binding.toolbar);
    appBarConfig = new AppBarConfiguration.Builder(R.id.home_fragment, R.id.pre_login_fragment, R.id.login_fragment)
        .build();
    navController =((NavHostFragment) binding.navHostContainer.getFragment()).getNavController();
    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
  }
}