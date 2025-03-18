package edu.cnm.deepdive.trailtrack.service;

import android.net.Uri;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import edu.cnm.deepdive.trailtrack.model.dao.PinDao;
import edu.cnm.deepdive.trailtrack.model.dao.UserDao;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.model.entity.Track;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import edu.cnm.deepdive.trailtrack.service.PinDatabase.Converters;
import java.time.Instant;

@Database(entities = {Pin.class, Track.class, User.class}, version = PinDatabase.VERSION)
@TypeConverters(Converters.class)
public abstract class PinDatabase extends RoomDatabase {

  static final int VERSION = 1;
  private static final String DATABASE_NAME = "pins";

  public static String getDatabaseName() {
    return DATABASE_NAME;
  }

  public abstract PinDao getPinDao();

  public abstract UserDao getUserDao();

  // TODO: 3/17/25 Add method for getting TrackDao.

  public static class Converters {

    @TypeConverter
    public static Long fromInstant(Instant value) {
      return (value != null) ? value.toEpochMilli() : null;
    }

    @TypeConverter
    public static Instant fromLong(Long value) {
      return (value != null) ? Instant.ofEpochMilli(value) : null;
    }

    @TypeConverter
    public static String fromUri(Uri value) {
      return (value != null) ? value.toString() : null;
    }

    @TypeConverter
    public static Uri fromString(String value) {
      return (value != null) ? Uri.parse(value) : null;
    }
  }


}
