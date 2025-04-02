package edu.cnm.deepdive.trailtrack.controller;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.trailtrack.R;
import edu.cnm.deepdive.trailtrack.controller.ExplanationFragment.OnDismissListener;
import edu.cnm.deepdive.trailtrack.databinding.FragmentHomeBinding;
import edu.cnm.deepdive.trailtrack.viewmodel.LoginViewModel;
import edu.cnm.deepdive.trailtrack.viewmodel.PermissionsViewModel;
import edu.cnm.deepdive.trailtrack.viewmodel.PinViewModel;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The HomeFragment class represents the main user interface component of the application.
 * It is responsible for handling the navigation, permissions, and menu interactions within the app.
 * This fragment is integrated with Android Hilt for dependency injection and utilizes multiple
 * ViewModels for state management and logic handling.
 */
@AndroidEntryPoint
public class HomeFragment extends Fragment implements MenuProvider, OnDismissListener {

  private static final int PERMISSIONS_REQUEST_CODE = 674;
  private static final String TAG = HomeFragment.class.getSimpleName();
  private static final String[] permissionsNeeded = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION,
      CAMERA};

  private FragmentHomeBinding binding;
  private LoginViewModel loginViewModel;
  private PinViewModel pinViewModel;
  private PermissionsViewModel permissionsViewModel;
  private String[] permissionsToRequest;
  /**
   * @noinspection FieldCanBeLocal
   */
  private ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(
      new ActivityResultContracts.RequestMultiplePermissions(), this::handleGrantResults);
  private NavController childNavController;
  private NavController parentNavController;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentHomeBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }


  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
    FragmentActivity activity = requireActivity();
    ViewModelProvider provider = new ViewModelProvider(activity);
    loginViewModel = provider.get(LoginViewModel.class);
    loginViewModel
        .getAccount()
        .observe(lifecycleOwner, (account) -> {
          if (account == null) {
            parentNavController.navigate(HomeFragmentDirections.navigateToPreLoginFragment());
          } else {
            Log.d(TAG,"displayname = " + account.getDisplayName());
            Log.d(TAG,"OAuth key = " + account.getId());
          }
        });
    permissionsViewModel = provider.get(PermissionsViewModel.class);
    activity.addMenuProvider(this, getViewLifecycleOwner(), State.RESUMED);
  }

  @Override
  public void onResume() {
    super.onResume();
    // TOD 3/28/25 Setup navigation connection. Connect the nav controller to the appbar and use
    //  the nav UI class to use bottom button with NavController
    childNavController =
        ((NavHostFragment) binding.mapsPinsFragmentContainer.getFragment()).getNavController();
    parentNavController = Navigation.findNavController(binding.getRoot());
    NavigationUI.setupWithNavController(binding.bottomNavigation, childNavController);
    setupPermissions();
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  @Override
  public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
    menuInflater.inflate(R.menu.main_actions, menu);
  }

  @Override
  public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
    boolean handled = true;
    if (menuItem.getItemId() == R.id.sign_out) {
      loginViewModel.signOut();
    } else {
      handled = false;
    }
    return handled;
  }

  /**
   * Sets up the app permissions workflow by determining which permissions need to be requested,
   * collecting the permissions that are already granted, and handling the explanatory steps for
   * permissions if necessary.
   *
   * The method performs the following operations:
   * - Identifies permissions that still need to be requested based on their current status.
   * - Updates the view model with the status of permissions that are already granted.
   * - Checks if any of the permissions to be requested require an explanation to the user.
   * - If explanations are required, navigates to an explanation fragment to inform the user about
   *   the permissions. Otherwise, directly triggers the permission request process.
   *
   * This method interacts with the view model to maintain the state of permissions and uses
   * navigation to guide the user through the permission flow.
   */
  private void setupPermissions() {
    permissionsToRequest = Arrays.stream(permissionsNeeded)
        .filter(this::shouldRequestPermission)
        .toArray(String[]::new);
    Map<String, Boolean> permissionsStatus = Arrays.stream(permissionsNeeded)
        .filter(Predicate.not(this::shouldRequestPermission))
        .collect(Collectors.toMap(Function.identity(), (permission) -> true));
    permissionsViewModel.updatePermissionsStatus(permissionsStatus);
    String[] permissionsToExplain = Arrays.stream(permissionsToRequest)
        .filter(this::shouldExplainPermission)
        .toArray(String[]::new);
    if (permissionsToExplain.length > 0) {
      parentNavController.navigate(
          LoginFragmentDirections.openExplanationFragment(permissionsToExplain));
    } else {
      onDismiss();
    }
  }

  /**
   * Handles the results of permission requests and updates the permissions status in the view model.
   *
   * @param grantResults A map containing the permissions requested and their corresponding
   *                     grant results, where the key is the permission name, and the value is
   *                     a boolean indicating whether the permission was granted (true) or denied (false).
   */
  public void handleGrantResults(@NonNull Map<String, Boolean> grantResults) {
    Log.d(TAG, grantResults.toString());
    permissionsViewModel.updatePermissionsStatus(grantResults);
  }

  @Override
  public void onDismiss() {
    requestPermissionsLauncher.launch(permissionsToRequest);
  }

  /**
   * Determines whether the specified permission should be requested by checking if it is already granted.
   *
   * @param permission The name of the permission to check (e.g., android.permission.CAMERA).
   * @return {@code true} if the specified permission is not granted and should be requested, {@code false} otherwise.
   */
  private boolean shouldRequestPermission(String permission) {
    return ContextCompat.checkSelfPermission(requireContext(), permission)
        != PackageManager.PERMISSION_GRANTED;
  }

  /**
   * Checks whether an explanation should be shown to the user for the specified permission.
   * This is typically used to determine if rationale should be provided before requesting a permission.
   *
   * @param permission The name of the permission to check (e.g., android.permission.CAMERA).
   * @return {@code true} if an explanation should be shown to the user for the specified permission,
   *         {@code false} otherwise.
   */
  private boolean shouldExplainPermission(String permission) {
    return ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission);
  }

}