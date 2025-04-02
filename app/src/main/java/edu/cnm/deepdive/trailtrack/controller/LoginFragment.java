package edu.cnm.deepdive.trailtrack.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import edu.cnm.deepdive.trailtrack.viewmodel.LoginViewModel;
import edu.cnm.deepdive.trailtrack.R;
import edu.cnm.deepdive.trailtrack.databinding.FragmentLoginBinding;

/**
 * A fragment that handles the login functionality in the application using Google Sign-In.
 *
 *
 * This fragment integrates with a LoginViewModel to manage the sign-in state and handle related
 * operations, such as initiating a sign-in flow, processing results, and observing changes to the user's sign-in status or errors.
 *
 * Key behaviors of this fragment include:
 * - Setting up and binding to a view using data binding.
 * - Observing LiveData from the LoginViewModel to handle account state and error handling.
 * - Registering and managing an ActivityResultLauncher for sign-in result handling.
 * - Navigating to the home fragment upon successful sign-in.
 * - Displaying error messages through a Snackbar when sign-in fails.
 *
 * Methods:
 * - onCreateView: Inflates the layout and sets up a click listener for the sign-in button to initiate the sign-in process.
 * - onViewCreated: Initializes the LoginViewModel, observes relevant LiveData for account and error state changes, and registers the ActivityResultLauncher.
 * - onDestroyView: Cleans up resources when the view is destroyed.
 * - handleAccount: Handles the successful sign-in by navigating to the home fragment.
 * - handleThrowable: Handles sign-in errors by showing a Snackbar with an appropriate message.
 */
public class LoginFragment extends Fragment {

  private FragmentLoginBinding binding;
  private LoginViewModel viewModel;
  private ActivityResultLauncher<Intent> launcher;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentLoginBinding.inflate(inflater, container, false);
    binding.signIn.setOnClickListener((v) -> viewModel.startSignIn(launcher));
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(requireActivity())
        .get(LoginViewModel.class);
    LifecycleOwner owner = getViewLifecycleOwner();
    viewModel
        .getAccount()
        .observe(owner, this::handleAccount);
    viewModel
        .getSignInThrowable()
        .observe(owner, this::handleThrowable);
    launcher = registerForActivityResult(new StartActivityForResult(), viewModel::completeSignIn);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  /**
   * Handles the successful sign-in by navigating to the home fragment.
   *
   * @param account the GoogleSignInAccount object obtained after a successful sign-in.
   *                If null, no action is performed.
   */
  private void handleAccount(GoogleSignInAccount account) {
    if (account != null) {
      Navigation.findNavController(binding.getRoot())
          .navigate(LoginFragmentDirections.navigateToHomeFragment());
    }
  }

  /**
   * Handles throwable errors by displaying a user-facing message.
   *
   * @param throwable the throwable object representing the error. If null, no action is performed.
   */
  private void handleThrowable(Throwable throwable) {
    if (throwable != null) {
      Snackbar.make(binding.getRoot(), R.string.sign_in_failure_message, Snackbar.LENGTH_LONG)
          .show();
    }
  }
}
