package edu.cnm.deepdive.trailtrack.model.entity;

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
  // TODO: 3/17/25 Add fields for attributes


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  // TODO: 3/17/25 Add getters an setters for any remaining fields.
}
