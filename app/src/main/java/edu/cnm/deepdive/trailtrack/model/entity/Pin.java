package edu.cnm.deepdive.trailtrack.model.entity;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import edu.cnm.deepdive.trailtrack.model.pojo.Location;
import java.time.Instant;
import java.util.UUID;

@Entity(
    tableName = "pin",
    indices = {
        @Index(value = {"title"}, unique = true)
    }
)
public class Pin {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "pin_id")
  private long id;

  @NonNull
  @ColumnInfo(collate = ColumnInfo.NOCASE)
  @Expose
  private String title = "";

  // TODO: 3/18/25 add annotations?
  private UUID trackId;

  // TODO: 3/18/25 add annotations?
  private UUID userId;


  @NonNull
  @Expose
  private String content = "";

  private Uri image;

  @NonNull
  @ColumnInfo(name = "created_on", index = true)
  private Instant createdOn = Instant.now();

  @NonNull
  @ColumnInfo(name = "modified_on", index = true)
  private Instant modifiedOn = Instant.now();

  @Embedded
  @NonNull
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


  @NonNull
  public Location getLocation() {
    return location;
  }

  public void setLocation(@NonNull Location location) {
    this.location = location;
  }
}
