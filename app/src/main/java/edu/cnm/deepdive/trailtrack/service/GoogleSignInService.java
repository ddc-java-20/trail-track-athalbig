package edu.cnm.deepdive.trailtrack.service;

import android.content.Context;
import android.content.Intent;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javax.inject.Inject;
import javax.inject.Singleton;



/**
 * Provides authentication and session management for Google Sign-In functionality within the app.
 * This service offers methods for silent sign-in, explicit sign-in initiation and completion, and
 * signing out of Google accounts, leveraging the Google Sign-In API and RxJava for asynchronous
 * handling of operations.
 *
 * This class relies on dependency injection to initialize a {@link GoogleSignInClient} instance
 * using the application context.
 */
@Singleton
public class GoogleSignInService {

  private final GoogleSignInClient client;

  /**
   * Initializes an instance of {@code GoogleSignInService} with a configured {@link GoogleSignInClient}.
   * The configuration specifies that email, profile, and ID information should be requested from
   * the user's Google account upon sign-in.
   *
   * @param context The application context used to build the {@link GoogleSignInClient} instance.
   */
  @Inject
  GoogleSignInService(@ApplicationContext Context context) {
    GoogleSignInOptions options = new GoogleSignInOptions.Builder()
        .requestEmail()
        .requestProfile()
        .requestId()
        .build();
    client = GoogleSignIn.getClient(context, options);
  }

  /**
   * Performs a silent sign-in using the {@link GoogleSignInClient} to attempt retrieval of the
   * last signed-in Google account asynchronously. If successful, the sign-in account is returned.
   * If the operation fails, an error notification is sent to the subscriber.
   *
   * @return A {@link Single} emitting a {@link GoogleSignInAccount} object if the silent sign-in
   * succeeds, or an error if the operation fails.
   */
  public Single<GoogleSignInAccount> refresh() {
    return Single.create((SingleEmitter<GoogleSignInAccount> emitter) ->
            client.silentSignIn()
                .addOnSuccessListener(emitter::onSuccess)
                .addOnFailureListener(emitter::onError)
        )
        .observeOn(Schedulers.io());
  }

  /**
   * Initiates the Google Sign-In process by launching the Google Sign-In Intent
   * provided by the {@link GoogleSignInClient}.
   *
   * @param launcher An {@link ActivityResultLauncher} that is used to start an activity
   *                 to handle the Google Sign-In process and receive the result.
   */
  public void startSignIn(ActivityResultLauncher<Intent> launcher) {
    launcher.launch(client.getSignInIntent());
  }

  /**
   * Completes the Google Sign-In process by processing the result of a previously initiated
   * sign-in activity. If the sign-in is successful, the account information is emitted. In case
   * of an error, the error is propagated to the subscriber.
   *
   * @param result The {@link ActivityResult} containing the result data of the sign-in activity.
   *               This data includes information necessary for obtaining the {@link GoogleSignInAccount}.
   * @return A {@link Single} emitting a {@link GoogleSignInAccount} object if the sign-in
   *         is successful, or an error if the process fails.
   */
  public Single<GoogleSignInAccount> completeSignIn(ActivityResult result) {
    return Single.create((SingleEmitter<GoogleSignInAccount> emitter) -> {
          try {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                .getResult(ApiException.class);
            emitter.onSuccess(account);
          } catch (ApiException e) {
            emitter.onError(e);
          }
        })
        .observeOn(Schedulers.io());
  }

  /**
   * Signs out the currently authenticated user by invoking the sign-out functionality
   * of the associated {@link GoogleSignInClient}. The operation is performed asynchronously,
   * emitting either a completion signal or an error notification to the subscriber.
   *
   * @return A {@link Completable} that completes if the sign-out operation succeeds, or
   * emits an error if the operation fails.
   */
  public Completable signOut() {
    return Completable.create(emitter ->
            client
                .signOut()
                .addOnSuccessListener((ignored) -> emitter.onComplete())
                .addOnFailureListener(emitter::onError)
        )
        .observeOn(Schedulers.io());
  }
}
