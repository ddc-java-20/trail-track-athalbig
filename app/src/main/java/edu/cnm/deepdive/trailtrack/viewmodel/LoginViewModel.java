package edu.cnm.deepdive.trailtrack.viewmodel;

import android.content.Intent;
import android.util.Log;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.trailtrack.service.GoogleSignInService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javax.inject.Inject;

/**
 * ViewModel for managing user authentication through Google Sign-In.
 * Responsible for coordinating between the UI and the {@link GoogleSignInService},
 * handling sign-in, sign-out, and account refresh operations. This class also
 * manages the lifecycle of ongoing operations and provides LiveData objects
 * for observing authentication-related events.
 *
 * This ViewModel integrates with the Android lifecycle, clearing ongoing operations
 * automatically on lifecycle changes. It uses RxJava for asynchronous operations and
 * provides error handling through LiveData objects to propagate exceptions to the UI layer.
 *
 * Features:
 * - Supports silent sign-in to retrieve the last authenticated Google account.
 * - Initiates and completes Google Sign-In flows.
 * - Handles sign-out functionality.
 * - Exposes LiveData for observing the authenticated account and any errors encountered during operations.
 * - Ensures proper cleanup of disposables upon lifecycle transitions.
 *
 * Constructor:
 * The ViewModel is instantiated via dependency injection using Hilt, injecting
 * an instance of the {@link GoogleSignInService} for managing Google Sign-In flows.
 */
@HiltViewModel
public class LoginViewModel extends ViewModel implements DefaultLifecycleObserver {

  private static final String TAG = LoginViewModel.class.getSimpleName();

  private final GoogleSignInService signInService;
  private final MutableLiveData<GoogleSignInAccount> account;
  private final MutableLiveData<Throwable> refreshThrowable;
  private final MutableLiveData<Throwable> signInThrowable;
  private final CompositeDisposable pending;

  @Inject
  LoginViewModel(GoogleSignInService signInService) {
    this.signInService = signInService;
    account = new MutableLiveData<>();
    refreshThrowable = new MutableLiveData<>();
    signInThrowable = new MutableLiveData<>();
    pending = new CompositeDisposable();
  }

  /**
   * Retrieves the currently signed-in Google account as observable {@link LiveData}.
   * This method provides access to the user's account information, allowing the UI
   * and other observers to react to changes in the user's sign-in status.
   *
   * @return a {@code LiveData<GoogleSignInAccount>} instance representing the signed-in
   * Google account, or {@code null} if no account is currently signed in.
   */
  public LiveData<GoogleSignInAccount> getAccount() {
    return account;
  }

  /**
   * Retrieves the {@link LiveData} instance containing any {@link Throwable} encountered during the
   * refresh operation. Observers can subscribe to this {@link LiveData} to receive updates whenever
   * a refresh-related error occurs.
   *
   * @return a {@code LiveData<Throwable>} instance representing the throwable encountered during
   * the refresh operation, or {@code null} if no throwable is currently reported.
   */
  public LiveData<Throwable> getRefreshThrowable() {
    return refreshThrowable;
  }

  /**
   * Retrieves the {@link LiveData} instance containing any {@link Throwable} encountered during
   * the sign-in operation. Observers can subscribe to this {@link LiveData} to receive updates
   * whenever a sign-in-related error occurs.
   *
   * @return a {@code LiveData<Throwable>} instance representing the throwable encountered during
   * the sign-in operation, or {@code null} if no throwable is currently reported.
   */
  public LiveData<Throwable> getSignInThrowable() {
    return signInThrowable;
  }

  /**
   * Attempts to refresh the currently signed-in Google account information by initiating a silent
   * sign-in process through the {@code GoogleSignInService}. If successful, the updated account
   * information is posted to the associated {@code LiveData}. If an error occurs during the refresh
   * operation, the corresponding throwable is posted to the {@code refreshThrowable} {@code LiveData}.
   * Other observers may monitor these {@code LiveData} instances to track success or failures.
   *
   * Additionally, this method resets any previously reported throwable values for both the
   * refresh and sign-in operations, ensuring a clean state before initiating the new refresh request.
   */
  public void refresh() {
    refreshThrowable.setValue(null);
    signInThrowable.setValue(null);
    signInService
        .refresh()
        .subscribe(
            account::postValue,
            (throwable) -> postThrowable(throwable, refreshThrowable),
            pending
        );
  }

  /**
   * Initiates the sign-in process for the user. This method resets any previously reported
   * throwable values for both the refresh and sign-in operations, ensuring a clean state before
   * starting the sign-in workflow. The actual sign-in process is delegated to the
   * {@code GoogleSignInService} using the provided {@link ActivityResultLauncher}.
   *
   * @param launcher An {@link ActivityResultLauncher} used to manage the result of the Google
   *                 Sign-In activity, handling the user's interaction and receiving their
   *                 authentication information.
   */
  public void startSignIn(ActivityResultLauncher<Intent> launcher) {
    refreshThrowable.setValue(null);
    signInThrowable.setValue(null);
    signInService.startSignIn(launcher);
  }

  /**
   * Completes the sign-in process using the result of a previously initiated sign-in activity.
   * This method clears any previously reported throwable values related to refresh and sign-in
   * operations, ensuring a clean state before updating the active Google account or posting
   * errors encountered during the sign-in process.
   *
   * @param result The {@link ActivityResult} containing the result data of a sign-in activity.
   *               This result is processed to extract and update the signed-in account
   *               information or to handle any errors encountered during the process.
   */
  public void completeSignIn(ActivityResult result) {
    refreshThrowable.setValue(null);
    signInThrowable.setValue(null);
    signInService
        .completeSignIn(result)
        .subscribe(
            account::postValue,
            (throwable) -> postThrowable(throwable, signInThrowable),
            pending
        );
  }

  /**
   * Signs out the currently authenticated user and clears relevant state in the {@code LoginViewModel}.
   *
   * This method performs the following actions:
   * - Resets any previously reported errors for both refresh and sign-in operations by setting
   *   {@code refreshThrowable} and {@code signInThrowable} to {@code null}.
   * - Initiates the sign-out operation via the {@code signInService}.
   * - Updates the user account information to {@code null} asynchronously when the sign-out
   *   operation is completed.
   */
  public void signOut() {
    refreshThrowable.setValue(null);
    signInThrowable.setValue(null);
    signInService
        .signOut()
        .doFinally(() -> account.postValue(null))
        .subscribe();
  }

  @Override
  public void onStop(@NonNull LifecycleOwner owner) {
    pending.clear();
    DefaultLifecycleObserver.super.onStop(owner);
  }

  /**
   * Posts the specified {@link Throwable} to the provided {@link MutableLiveData}. Logs the throwable
   * details for debugging purposes before posting.
   *
   * @param throwable The {@link Throwable} to be logged and posted to the given {@link MutableLiveData}.
   * @param throwableLiveData The {@link MutableLiveData} instance to which the throwable is posted.
   */
  private void postThrowable(Throwable throwable, MutableLiveData<Throwable> throwableLiveData) {
    Log.e(TAG, throwable.getMessage(), throwable);
    throwableLiveData.postValue(throwable);
  }

}
