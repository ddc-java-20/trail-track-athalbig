package edu.cnm.deepdive.trailtrack.service;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import edu.cnm.deepdive.trailtrack.model.dao.UserDao;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @noinspection deprecation
 */

/**
 * A repository class responsible for handling operations related to user accounts and user data.
 * This class acts as the main access point for managing users, providing seamless integration
 * between Google Sign-In authentication and local database operations.
 *
 * This class is managed as a singleton and is constructed using dependency injection.
 */
@Singleton
public class UserRepository {

  private final GoogleSignInService signInService;
  private final UserDao userDao;
  private final Scheduler scheduler;

  /**
   * Constructs a UserRepository instance with dependencies for managing Google Sign-In services
   * and user data persistence operations.
   *
   * @param signInService An instance of {@link GoogleSignInService} used to handle authentication
   *                      and session management for Google Sign-In.
   * @param userDao       An instance of {@link UserDao} used to perform persistent data operations
   *                      related to user accounts, such as querying and updating user records.
   */
  @Inject
  UserRepository(GoogleSignInService signInService, UserDao userDao) {
    this.signInService = signInService;
    this.userDao = userDao;
    scheduler = Schedulers.io();
  }

  // TODO: 2/26/25 Add operations (methods) for reading and writing users from/to a database.

  /**
   * Retrieves the current Google Sign-In account information asynchronously. This method
   * performs a silent sign-in operation using the Google Sign-In service and observes the
   * result on the configured scheduler.
   *
   * @return A {@link Single} emitting the currently signed-in {@link GoogleSignInAccount}, or
   * an error if the sign-in attempt fails.
   */
  public Single<GoogleSignInAccount> getCurrentAccount() {
    return signInService
        .refresh()
        .observeOn(scheduler);
  }

  /**
   * Retrieves the currently authenticated user from the database or creates a new user
   * if the user does not exist in the database. This method first attempts to fetch the
   * signed-in account using the Google Sign-In service. If the user's associated record
   * is not found in the database, it creates a new {@code User} instance, initializes its
   * properties (OAuth key and display name), stores it in the database, and returns it.
   *
   * @return A {@link Single} emitting the {@link User} associated with the currently signed-in
   * account. If no record is found in the database, a new {@link User} is created and returned.
   * An error is emitted if the retrieval or creation process fails.
   */
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
