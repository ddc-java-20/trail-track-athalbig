package edu.cnm.deepdive.trailtrack.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "track_id")
  private long id;

  @NonNull
  private String name = "";

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

}
