package edu.cnm.deepdive.trailtrack.model.pojo;

import androidx.room.Relation;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import java.util.List;

public class UserWithPins extends User {

  @Relation(parentColumn = "user_id", entityColumn = "user_id")
  private List<Pin> pins;

  public List<Pin> getPins() {
    return pins;
  }

  public void setPins(List<Pin> pins) {
    this.pins = pins;
  }
}
