package edu.cnm.deepdive.trailtrack.model.entity;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import edu.cnm.deepdive.trailtrack.model.pojo.Location;
import java.time.Instant;
import java.util.UUID;

/**
 * The Pin class represents a pinned item within the application's database,
 * mapped to the "pin" table in SQLite using Room. Each pin is uniquely identified
 * by an auto-generated ID and is associated with a user and optionally with a track.
 * Pins offer functionality for storing textual content, media, and geographical locations.
 *
 * Features include:
 * - Unique combination of a track ID and title.
 * - Associations to {@link User} and {@link Track}, ensuring relational integrity.
 * - Support for additional data like title, textual content, image URI, creation and modification timestamps, and location details.
 * - Automatic cascading deletion of a pin when the associated user is deleted.
 * - Nullification of the track ID when the associated track is deleted.
 *
 * This class includes the following fields:
 * - A unique identifier (ID).
 * - A title for the pin.
 * - The ID of the associated track, if any.
 * - The ID of the associated user.
 * - The textual content associated with the pin.
 * - An optional URI pointing to a related image.
 * - Timestamps for when the pin was created and last modified.
 * - An embedded location containing latitude and longitude coordinates.
 */
@Entity(
    tableName = "pin",
    indices = {
        @Index(value = {"track_id","title"}, unique = true)
    },
    foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "user_id", childColumns = "user_id", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Track.class, parentColumns = "track_id", childColumns = "track_id", onDelete = ForeignKey.SET_NULL)
    }
)


public class Pin {

  /**
   * Unique identifier for a {@code Pin} entity. This identifier is auto-generated
   * when a new record is inserted into the corresponding database table.
   */
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "pin_id")
  private long id;

  /**
   * The title of the pin. This field is case-insensitive and must never be null.
   * It is exposed for serialization and persistence, and is initialized to an empty string by default.
   */
  @NonNull
  @ColumnInfo(collate = ColumnInfo.NOCASE)
  @Expose
  private String title = "";

  /**
   * Reference to the unique identifier of the related track entity. This field is indexed
   * to optimize queries involving track associations.
   */
  @ColumnInfo(name = "track_id", index = true)
  private Long trackId;

  /**
   * Represents the unique identifier of the user associated with this entity. This identifier
   * corresponds to the `user_id` primary key in the `user` table and serves as a foreign key
   * reference in related entities. It is indexed to optimize query performance.
   */
  @ColumnInfo(name = "user_id", index = true)
  private long userId;

  /**
   * Stores the content associated with a pin. This field is non-null and is serialized during
   * JSON operations. Content is intended to store descriptive or relevant textual information
   * about a pin.
   */
  @NonNull
  @Expose
  private String content = "";

  /**
   * Holds the URI referencing the image associated with a specific pin. This variable
   * represents a link to a local or remote resource that can be used to display or process
   * the image.
   */
  private Uri image;

  /**
   * Stores the timestamp representing the creation date and time of the entity.
   * This value is automatically initialized to the current system time when the instance is created.
   * It is immutable and ensures non-null values are always assigned.
   * The column is indexed in the underlying database for optimized query performance based on creation time.
   */
  @NonNull
  @ColumnInfo(name = "created_on", index = true)
  private Instant createdOn = Instant.now();

  /**
   * Timestamp indicating the date and time the entity was last modified.
   * This field is set to the current instant by default and is marked as
   * non-null. It is used for tracking updates to the entity's state.
   * Persisted in the database using the column name "modified_on" with an index
   * for optimizing queries filtered or sorted by this value.
   */
  @NonNull
  @ColumnInfo(name = "modified_on", index = true)
  private Instant modifiedOn = Instant.now();

  /**
   * Geographic location associated with the {@link Pin}. This includes the latitude and longitude
   * coordinates encapsulated in the {@link Location} record. The {@code location} is stored as an
   * embedded object in the database entity and can be null, indicating no specific location is set
   * for the {@link Pin}.
   */
  @Embedded
  private Location location;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @NonNull
  public String getTitle() {
    return title;
  }

  public void setTitle(@NonNull String title) {
    this.title = title;
  }

  @NonNull
  public String getContent() {
    return content;
  }

  public void setContent(@NonNull String content) {
    this.content = content;
  }

  public Uri getImage() {
    return image;
  }

  public void setImage(Uri image) {
    this.image = image;
  }

  @NonNull
  public Instant getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(@NonNull Instant createdOn) {
    this.createdOn = createdOn;
  }

  @NonNull
  public Instant getModifiedOn() {
    return modifiedOn;
  }

  public void setModifiedOn(@NonNull Instant modifiedOn) {
    this.modifiedOn = modifiedOn;
  }


   public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Long getTrackId() {
    return trackId;
  }

  public void setTrackId(Long trackId) {
    this.trackId = trackId;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

}
