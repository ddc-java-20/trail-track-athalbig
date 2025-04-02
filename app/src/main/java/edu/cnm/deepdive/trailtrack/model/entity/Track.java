package edu.cnm.deepdive.trailtrack.model.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Represents a track object that is mapped to a database entity.
 * Tracks are associated with a user and include attributes such as
 * an auto-generated primary key, a name, and a foreign key linking to the user.
 *
 * Annotations are used to define this entity for database purposes:
 * - {@link Entity} specifies the database table name, indices, and foreign key constraints.
 * - {@link PrimaryKey} and {@link ColumnInfo} define properties of table columns.
 * - {@link ForeignKey} establishes a relationship with the {@link User} entity.
 *
 * The `Track` class overrides `toString` for convenient representation,
 * and overrides `equals` and `hashCode` for consistency when comparing and hashing.
 *
 * Features:
 * - Tracks are uniquely identified by their `id`.
 * - A name must be provided and cannot be null.
 * - Tracks are associated with a user through the `userId` foreign key.
 */
@Entity(
    tableName = "track",
    indices = {
        @Index({"user_id", "name"})
    },
    foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "user_id", childColumns = "user_id", onDelete = ForeignKey.CASCADE)
    }
)
public class Track {

  /**
   * Represents the unique identifier for a {@code Track} entity in the database.
   * This field is marked as the primary key and is auto-generated upon insertion
   * of a new {@code Track} record. It is mapped to the "track_id" column
   * in the underlying SQLite table.
   */
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "track_id")
  private long id;

  /**
   * Represents the name of the {@code Track} entity. This field is annotated with {@link NonNull},
   * ensuring that it cannot be null at any time. It is initialized to an empty string by default.
   *
   * The `name` field serves as an identifier for the track entity's logical grouping,
   * distinct from its unique database identifier (`id`). It must be explicitly set when
   * creating or updating a `Track` instance.
   */
  @NonNull
  private String name = "";

  /**
   * Represents a foreign key linking a {@code Track} entity to a {@code User} entity.
   * This field is mapped to the "user_id" column in the database table and is indexed
   * for optimized query performance. The {@code userId} field is used to establish
   * the association between tracks and their corresponding user in the database.
   */
  @ColumnInfo(name = "user_id", index = true)
  private long userId;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @NonNull
  public String getName() {
    return name;
  }

  public void setName(@NonNull String name) {
    this.name = name;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  @NonNull
  @Override
  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(id);
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    boolean result;
    if (this == obj) {
      result = true;
    } else if (obj instanceof Track other) {
      result = id !=0 && id == other.id;
    } else {
      result = false;
    }
    return result;
  }
}
