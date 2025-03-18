package edu.cnm.deepdive.trailtrack.model.pojo;

import androidx.room.Relation;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.model.entity.Track;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import java.util.List;

public class UserWithContent extends User {

  @Relation(parentColumn = "user_id", entityColumn = "user_id")
  private List<Pin> pins;
  @Relation(entity = Track.class, parentColumn = "user_id", entityColumn = "user_id")
  private List<TrackWithPins> tracks;

  public List<Pin> getPins() {
    return pins;
  }

  public void setPins(List<Pin> pins) {
    this.pins = pins;
  }

  public List<TrackWithPins> getTracks() {
    return tracks;
  }

  public void setTracks(List<TrackWithPins> tracks) {
    this.tracks = tracks;
  }

}
