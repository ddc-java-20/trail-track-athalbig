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
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.trailtrack.MapsPinsNavGraphDirections;
import edu.cnm.deepdive.trailtrack.R;
import edu.cnm.deepdive.trailtrack.controller.ExplanationFragment.OnDismissListener;
import edu.cnm.deepdive.trailtrack.databinding.FragmentHomeBinding;
import edu.cnm.deepdive.trailtrack.viewmodel.LoginViewModel;
import edu.cnm.deepdive.trailtrack.viewmodel.PinViewModel;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements MenuProvider, OnDismissListener {

  private static final int PERMISSIONS_REQUEST_CODE = 674;
  private static final String TAG = HomeFragment.class.getSimpleName();
  private static final String[] permissionsNeeded = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION,
      CAMERA};

  private FragmentHomeBinding binding;
  private LoginViewModel loginViewModel;
  private PinViewModel pinViewModel;
  private String[] permissionsToRequest;
  private Map<String, Boolean> permissionsStatus;
  /** @noinspection FieldCanBeLocal*/
  private ActivityResultLauncher<String[]> requestPermissionsLauncher;
  private NavController navController;

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
    loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    loginViewModel
        .getAccount()
        .observe(lifecycleOwner, (account) -> {
          if (account == null) {
            navController.navigate(HomeFragmentDirections.navigateToPreLoginFragment());
          }
        });
    requireActivity().addMenuProvider(this, getViewLifecycleOwner(), State.RESUMED);
    setupPermissions();
  }

  @Override
  public void onResume() {
    super.onResume();
    // TOD 3/28/25 Setup navigation connection. Connect the nav controller to the appbar and use
    //  the nav UI class to use bottom button with NavController
    navController =
        ((NavHostFragment) binding.mapsPinsFragmentContainer.getFragment()).getNavController();
    NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
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

  private void setupPermissions() {
    permissionsToRequest = Arrays.stream(permissionsNeeded)
        .filter(this::shouldRequestPermission)
        .toArray(String[]::new);
    permissionsStatus = Arrays.stream(permissionsNeeded)
        .filter(Predicate.not(this::shouldRequestPermission))
        .collect(Collectors.toMap(Function.identity(), (permission) -> true));
    String[] permissionsToExplain = Arrays.stream(permissionsToRequest)
        .filter(this::shouldExplainPermission)
        .toArray(String[]::new);
    if (permissionsToExplain.length > 0) {
      navController.navigate(
          MapsPinsNavGraphDirections.openExplanationFragment(permissionsToExplain));
    } else {
      onDismiss();
    }
  }

  public void handleGrantResults(@NonNull Map<String, Boolean> grantResults) {
    Log.d(TAG, grantResults.toString());
    permissionsStatus.putAll(grantResults);

  }

  @Override
  public void onDismiss() {
    requestPermissionsLauncher = registerForActivityResult(
        new ActivityResultContracts.RequestMultiplePermissions(), this::handleGrantResults);
    requestPermissionsLauncher.launch(permissionsToRequest);
  }

  private boolean shouldRequestPermission(String permission) {
    return ContextCompat.checkSelfPermission(requireContext(), permission)
        != PackageManager.PERMISSION_GRANTED;
  }

  private boolean shouldExplainPermission(String permission) {
    return ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission);
  }

}