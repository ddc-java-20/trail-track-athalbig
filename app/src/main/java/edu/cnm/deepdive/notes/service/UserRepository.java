package edu.cnm.deepdive.notes.service;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;
import javax.inject.Singleton;

/** @noinspection deprecation*/
@Singleton
public class UserRepository {

  private final GoogleSignInService signInService;

  @Inject
  UserRepository(GoogleSignInService signInService) {
    this.signInService = signInService;
  }
  // TODO: 2/26/25 Add operations (methods) for reading and writing users from/to a database.

  public Single<GoogleSignInAccount> getCurrentAccount() {
    return signInService
        .refresh();
    // TODO: 2/26/25 Add additional steps for database persistence.
  }


}
