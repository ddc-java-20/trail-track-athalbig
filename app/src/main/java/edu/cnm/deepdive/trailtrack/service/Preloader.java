package edu.cnm.deepdive.trailtrack.service;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.google.gson.Gson;
import dagger.hilt.android.qualifiers.ApplicationContext;
import edu.cnm.deepdive.trailtrack.R;
import edu.cnm.deepdive.trailtrack.model.dao.PinDao;
import edu.cnm.deepdive.trailtrack.model.dao.TrackDao;
import edu.cnm.deepdive.trailtrack.model.dao.UserDao;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import edu.cnm.deepdive.trailtrack.model.pojo.TrackWithPins;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * The Preloader class is responsible for preloading and populating a database with
 * predefined data, such as users, tracks, and pins, during the creation of the database.
 * It extends RoomDatabase.Callback to hook into the database creation lifecycle.
 *
 * This class handles the structured insertion of data:
 * - Reads predefined JSON data (from resources) for users, tracks, and pins.
 * - Inserts the data into the database using corresponding DAOs: PinDao, TrackDao, and UserDao.
 * - Maintains proper relationships between the entities (e.g., associating pins with tracks and users).
 *
 * Preloading data is executed asynchronously and off the main thread using RxJava.
 * Errors during the preloading process result in a runtime exception.
 */
public class Preloader extends RoomDatabase.Callback {


  private final Context context;
  private final Provider<PinDao> pinDaoProvider;
  private final Provider<TrackDao> trackDaoProvider;
  private final Provider<UserDao> userDaoProvider;
  private final Gson gson;

  /**
   * Constructs an instance of the {@code Preloader} class for initializing data access and
   * serialization dependencies required for the application's operation.
   *
   * @param context the application context, used for access to shared application-level resources
   *                and services.
   * @param pinDaoProvider the provider for {@link PinDao}, enabling data access operations
   *                       for {@code Pin} entities.
   * @param trackDaoProvider the provider for {@link TrackDao}, enabling data access operations
   *                         for {@code Track} entities.
   * @param userDaoProvider the provider for {@link UserDao}, enabling data access operations
   *                        for {@code User} entities.
   * @param gson the Gson instance used for JSON serialization and deserialization.
   */
  @Inject
  Preloader(@ApplicationContext Context context, Provider<PinDao> pinDaoProvider,
      Provider<TrackDao> trackDaoProvider, Provider<UserDao> userDaoProvider, Gson gson) {
    this.context = context;
    this.pinDaoProvider = pinDaoProvider;
    this.trackDaoProvider = trackDaoProvider;
    this.userDaoProvider = userDaoProvider;
    this.gson = gson;
  }

  /**
   * Handles the creation and initial population of the database when it is first created.
   * This method preloads pins and tracks data into the database using resources defined
   * in the application, associates them with a user, and establishes the necessary relationships
   * between the data entities.
   *
   * @param db the {@link SupportSQLiteDatabase} instance representing the database being created.
   */
  @Override
  public void onCreate(@NonNull SupportSQLiteDatabase db) {
    super.onCreate(db);

    PinDao pinDao = pinDaoProvider.get();
    TrackDao trackDao = trackDaoProvider.get();
    UserDao userDao = userDaoProvider.get();

    //Need an input stream
    try (
        Reader tracksReader = new InputStreamReader(
            context.getResources().openRawResource(R.raw.preload_pins_tracks));
        Reader userReader = new InputStreamReader(
            context.getResources().openRawResource(R.raw.preload_user))
    ) {
      User user = gson.fromJson(userReader, User.class);
      TrackWithPins[] tracksWithPins = gson.fromJson(tracksReader, TrackWithPins[].class);
      userDao
          .insert(user)
          .flatMap((userId) -> {
            for (TrackWithPins trackWithPins : tracksWithPins) {
              trackWithPins.setUserId(userId);
              for (Pin pin : trackWithPins.getPins()) {
                pin.setUserId(userId);
              }
            }
            return trackDao.insert(tracksWithPins);
          })
          .flatMap((trackIds) -> {
            List<Pin> pins = new LinkedList<>();
            Iterator<Long> idIterator = trackIds.iterator();
            for (TrackWithPins trackWithPins : tracksWithPins) {
              long trackId = idIterator.next();
              for (Pin pin : trackWithPins.getPins()) {
                pin.setTrackId(trackId);
                pins.add(pin);
              }
            }
            return pinDao.insert(pins);
          })
          .subscribeOn(Schedulers.io())
          .subscribe();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
