package edu.cnm.deepdive.trailtrack.model.pojo;

import androidx.room.Relation;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.model.entity.Track;
import java.util.List;

public class TrackWithPins extends Track {

  @Relation(parentColumn = "track_id", entityColumn = "track_id")
  private List<Pin> pins;

  public List<Pin> getPins() {
    return pins;
  }

  public void setPins(List<Pin> pins) {
    this.pins = pins;
  }

}
