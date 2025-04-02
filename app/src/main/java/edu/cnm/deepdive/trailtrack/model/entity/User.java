package edu.cnm.deepdive.trailtrack.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.time.Instant;

/**
 * The {@code User} class represents a user entity in the application's database.
 * It is annotated with {@code @Entity} to map this class to the "user" table
 * in the SQLite database. The table includes an index on the "oauth_key" column,
 * ensuring unique values for that key.
 *
 * The class defines the following main fields:
 * - {@code id}: A unique identifier for each user, auto-generated when a user record is created.
 * - {@code displayName}: The display name of the user, stored as a case-insensitive value.
 * - {@code oauthKey}: A unique identifier for OAuth authentication, stored as a case-insensitive value.
 * - {@code created}: A timestamp indicating when the user entity was created.
 *
 * Getters and setters are provided for accessing and modifying each of these fields.
 *
 * This class is designed to work seamlessly with Room for database management.
 */
@Entity(
    tableName = "user",
    indices = {
        @Index(value = "oauth_key", unique = true)
    }
)
public class User {

  /**
   * Unique identifier for a {@code User} entity. This identifier serves as the primary key
   * for the "user" table in the application's database. The value is auto-generated when
   * a new user record is created, ensuring uniqueness for each user.
   */
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "user_id")
  private long id;

  /**
   * Stores the display name of a {@code User}. This field is case-insensitive and is annotated
   * for storage in the database with the column name "display_name". It is guaranteed to be
   * non-null, and its default value is an empty string. This field is used to represent how
   * a {@code User} is publicly displayed or referenced.
   */
  @ColumnInfo(name = "display_name", collate = ColumnInfo.NOCASE)
  @NonNull
  private String displayName = "";

  /**
   * Represents the OAuth authentication key associated with a {@code User}.
   * This field is stored as a case-insensitive string in the database under
   * the column name "oauth_key". It is a required, non-null value and has
   * a default of an empty string.
   *
   * The "oauth_key" column is indexed to ensure unique values, which helps
   * identify users during authentication processes.
   */
  @ColumnInfo(name = "oauth_key", collate = ColumnInfo.NOCASE)
  @NonNull
  private String oauthKey = "";

  /**
   * Stores the timestamp representing the creation date and time of the {@code User} entity.
   * This field is initialized to the current system time when a {@code User} instance is created.
   * The value is immutable and non-null to ensure that a creation time is always assigned.
   */
  @NonNull
  private Instant created = Instant.now();

  public long getId() {
    return id;
  }


  public void setId(long id) {
    this.id = id;
  }

  @NonNull
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(@NonNull String displayName) {
    this.displayName = displayName;
  }

  @NonNull
  public String getOauthKey() {
    return oauthKey;
  }

  public void setOauthKey(@NonNull String oauthKey) {
    this.oauthKey = oauthKey;
  }

  @NonNull
  public Instant getCreated() {
    return created;
  }

  public void setCreated(@NonNull Instant created) {
    this.created = created;
  }
}
