package edu.cnm.deepdive.trailtrack.service;

import android.net.Uri;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import edu.cnm.deepdive.trailtrack.model.dao.PinDao;
import edu.cnm.deepdive.trailtrack.model.dao.TrackDao;
import edu.cnm.deepdive.trailtrack.model.dao.UserDao;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.model.entity.Track;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import edu.cnm.deepdive.trailtrack.service.PinDatabase.Converters;
import java.time.Instant;

/**
 * PinDatabase is an abstract representation of the Room database implementation for the application's
 * underlying storage. It provides access to Data Access Objects (DAOs) for database operations on
 * entities such as {@link Pin}, {@link Track}, and {@link User}.
 *
 * Features:
 * - Manages database creation and version management using Room.
 * - Uses entity classes to structure and map the database schema.
 * - Incorporates type converters for type transformations between database values and application values.
 *
 * The database is configured with the following properties:
 * - The database schema includes {@link Pin}, {@link Track}, and {@link User} entities.
 * - The version is managed by the {@code VERSION} constant.
 * - Type converters are provided via the {@link Converters} class to handle custom data types such as
 *   {@link Instant} and {@link Uri}.
 */
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

  public abstract TrackDao getTrackDao();

  /**
   * Provides type conversion methods for Room database to handle custom data types.
   *
   * This utility class is referenced via the {@code @TypeConverters(Converters.class)} annotation in
   * classes such as {@code PinDatabase}. It is responsible for converting between custom types and
   * primitive types that the database can store, ensuring seamless integration between the database
   * schema and the application's object model.
   *
   * Methods in this class include:
   * - Conversion of {@link Instant} objects to {@link Long} values, representing milliseconds
   *   since the Unix epoch, and vice versa.
   * - Conversion of {@link Uri} objects to their {@link String} representations usable by the database,
   *   and vice versa.
   */
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
