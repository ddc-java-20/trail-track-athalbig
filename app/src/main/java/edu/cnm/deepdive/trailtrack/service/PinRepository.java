package edu.cnm.deepdive.trailtrack.service;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.trailtrack.model.dao.PinDao;
import edu.cnm.deepdive.trailtrack.model.dao.TrackDao;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.model.entity.Track;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository class to manage data operations related to Pins and Tracks.
 * Provides methods to interact with the persistence layer through DAOs and
 * integrates with the Google Sign-In service for user-specific operations.
 * Utilizes dependency injection for instantiation and RxJava for handling asynchronous operations.
 */
@Singleton
public class PinRepository {

  private final PinDao pinDao;
  private final TrackDao trackDao;
  private final Scheduler scheduler;
  private final GoogleSignInService googleSignInService;

  /**
   * Constructs a new instance of the PinRepository class. This repository encapsulates
   * data operations related to Pins and Tracks, bridging the persistence layer via DAOs
   * and integrating with user authentication through the Google Sign-In service.
   * The class leverages dependency injection for its dependencies.
   *
   * @param pinDao               Data access object for performing operations on Pin entities.
   * @param trackDao             Data access object for performing operations on Track entities.
   * @param googleSignInService  Service for handling Google Sign-In operations and user-specific tasks.
   */
  @Inject
  PinRepository(PinDao pinDao, TrackDao trackDao, GoogleSignInService googleSignInService) {
    this.pinDao = pinDao;
    this.trackDao = trackDao;
    this.googleSignInService = googleSignInService;
    scheduler = Schedulers.io();
  }

  /**
   * Saves the specified {@code Pin} object to the database. If the pin already exists
   * (determined by a nonzero ID), it updates the existing record and sets the modification timestamp
   * to the current time. Otherwise, it inserts the new {@code Pin} into the database.
   *
   * @param pin The {@code Pin} object to be saved or updated. Must not be null.
   * @return A {@code Single<Pin>} representing the saved or updated {@code Pin} object.
   */
  public Single<Pin> save(Pin pin) {
    return (pin.getId() != 0)
      ? Completable.fromAction(() -> pin.setModifiedOn(Instant.now()))
        .andThen(pinDao.update(pin))
        .toSingle(() -> pin)
        .subscribeOn(scheduler)
      : pinDao
        .insertAndReturn(pin)
        .subscribeOn(scheduler);
  }

  /**
   * Retrieves a {@code Pin} object identified by the specified unique ID from the database.
   *
   * @param id The unique identifier of the {@code Pin} to fetch. Must be a positive number.
   * @return A {@code LiveData<Pin>} instance representing the {@code Pin} entity with the given ID.
   */
  public LiveData<Pin> get(long id) {
    return pinDao.selectById(id);
  }

  /**
   * Deletes the specified {@code Pin} object from the database.
   * This operation is asynchronous and will execute on the specified scheduler.
   *
   * @param pin The {@code Pin} object to be removed from the database. Must not be null.
   * @return A {@code Completable} representing the result of the delete operation. It
   *         completes successfully when the {@code Pin} is deleted or emits an error if the operation fails.
   */
  public Completable remove(Pin pin) {
    return pinDao
        .delete(pin)
        .subscribeOn(scheduler);
  }

  /**
   * Retrieves a list of {@code Pin} entities associated with the specified {@code User},
   * ordered by their creation timestamp in ascending order.
   *
   * @param user The {@code User} whose pins are to be fetched. Must not be null.
   * @return A {@code LiveData<List<Pin>>} containing the list of pins for the given user.
   */
  public LiveData<List<Pin>> getAll(User user) {
    return pinDao.selectByCreatedOnAsc(user.getId());
  }

  /**
   * Retrieves a list of {@code Pin} objects associated with the specified {@code User}.
   *
   * @param user The {@code User} whose associated pins are to be retrieved. Must not be null.
   * @return A {@code LiveData<List<Pin>>} containing the list of pins linked to the specified user.
   */
  public LiveData<List<Pin>> getAllForUser(User user) {
    return pinDao.selectByUserId(user.getId());
  }

  /**
   * Retrieves a list of {@code Pin} entities associated with the specified {@code Track}.
   *
   * @param track The {@code Track} whose associated pins are to be fetched. Must not be null.
   * @return A {@code LiveData<List<Pin>>} containing the list of pins linked to the specified track.
   */
  public LiveData<List<Pin>> getAllForTrack(Track track) {
    return pinDao.selectByTrackId(track.getId());
  }

  /**
   * Retrieves a list of all {@code Track} entities stored in the database.
   * The tracks are ordered alphabetically by their name.
   *
   * @return A {@code LiveData<List<Track>>} containing all tracks in the database.
   */
  public LiveData<List<Track>> getAllTracks(){
    return trackDao.selectAll();
  }

}
