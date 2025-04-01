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

public class Preloader extends RoomDatabase.Callback {


  private final Context context;
  private final Provider<PinDao> pinDaoProvider;
  private final Provider<TrackDao> trackDaoProvider;
  private final Provider<UserDao> userDaoProvider;
  private final Gson gson;

  @Inject
  Preloader(@ApplicationContext Context context, Provider<PinDao> pinDaoProvider,
      Provider<TrackDao> trackDaoProvider, Provider<UserDao> userDaoProvider, Gson gson) {
    this.context = context;
    this.pinDaoProvider = pinDaoProvider;
    this.trackDaoProvider = trackDaoProvider;
    this.userDaoProvider = userDaoProvider;
    this.gson = gson;
  }

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
