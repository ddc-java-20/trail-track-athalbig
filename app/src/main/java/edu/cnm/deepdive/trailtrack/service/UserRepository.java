package edu.cnm.deepdive.trailtrack.service;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import edu.cnm.deepdive.trailtrack.model.dao.UserDao;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javax.inject.Inject;
import javax.inject.Singleton;

/** @noinspection deprecation*/
@Singleton
public class UserRepository {

  private final GoogleSignInService signInService;
  private final UserDao userDao;
  private final Scheduler scheduler;

  @Inject
  UserRepository(GoogleSignInService signInService, UserDao userDao) {
    this.signInService = signInService;
    this.userDao = userDao;
    scheduler = Schedulers.io();
  }

  // TODO: 2/26/25 Add operations (methods) for reading and writing users from/to a database.

  public Single<GoogleSignInAccount> getCurrentAccount() {
    return signInService
        .refresh()
        .observeOn(scheduler);
  }
/** @noinspection DataFlowIssue*/
public Single<User> getCurrentUser() {
    return getCurrentAccount()
        .flatMap((account) -> {
          String oauthkey = account.getId();
          return userDao
              .select(oauthkey)
              .switchIfEmpty(
                  Single.fromSupplier(User::new)
                      .doOnSuccess((user) -> {
                        user.setOauthKey(oauthkey);
                        user.setDisplayName(account.getDisplayName());
                      })
                      .flatMap(userDao::insertAndReturn)
              );
        });
}

}
