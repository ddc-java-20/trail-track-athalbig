package edu.cnm.deepdive.trailtrack.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.trailtrack.R;
import edu.cnm.deepdive.trailtrack.viewmodel.LoginViewModel;

/**
 * A Fragment that represents the pre-login state for the application.
 * It observes the user's Google Sign-In account status and handles navigation
 * to appropriate fragments based on the account state.
 *
 * This fragment initializes by setting up a ViewModel to monitor the account
 * and throwable states for possible sign-in errors or account changes.
 */
@AndroidEntryPoint
public class PreLoginFragment extends Fragment {

  private View root;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    root = inflater.inflate(R.layout.fragment_pre_login, container, false);
    return root;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    LoginViewModel viewModel = new ViewModelProvider(requireActivity())
        .get(LoginViewModel.class);
    LifecycleOwner owner = getViewLifecycleOwner();
    viewModel
        .getAccount()
        .observe(owner, this::handleAccount);
    viewModel
        .getRefreshThrowable()
        .observe(owner, this::handleThrowable);
    viewModel.refresh();
  }

  /**
   * Handles the user's Google Sign-In account. If the account is not null,
   * it navigates the user to the HomeFragment.
   *
   * @param account the GoogleSignInAccount instance representing the user's
   *                signed-in Google account, or null if no account is signed in
   */
  private void handleAccount(GoogleSignInAccount account) {
    if (account != null) {
      Navigation.findNavController(root)
          .navigate(PreLoginFragmentDirections.navigateToHomeFragment());
    }
  }

  /**
   * Handles throwable errors that may occur during the account refresh process.
   * If a throwable is not null, it navigates the user to the LoginFragment to
   * handle the error scenario.
   *
   * @param throwable the Throwable instance representing the error encountered,
   *                  or null if no error occurred
   */
  private void handleThrowable(Throwable throwable) {
    if (throwable != null) {
      Navigation.findNavController(root)
          .navigate(PreLoginFragmentDirections.navigateToLoginFragment());
    }
  }

}
