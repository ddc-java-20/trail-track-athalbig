package edu.cnm.deepdive.trailtrack.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "track",
    indices = {},
    foreignKeys = {}
)
public class Track {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "track_id")
  private long id;

  @NonNull
  @ColumnInfo(name = "track_name")
  private String trackName = "";

  @NonNull
  @ColumnInfo
  private long userId;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @NonNull
  public String getTrackName() {
    return trackName;
  }

  public void setTrackName(@NonNull String trackName) {
    this.trackName = trackName;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

}
