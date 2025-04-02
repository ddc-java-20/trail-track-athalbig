package edu.cnm.deepdive.trailtrack.model.pojo;

import androidx.room.Relation;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.model.entity.Track;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import java.util.List;

/**
 * The {@code UserWithContent} class is an extension of the {@code User} class
 * that incorporates additional related content, such as {@code Pin} and
 * {@code TrackWithPins} entities associated with a user.
 *
 * It includes relationships to other entities:
 * - {@code pins}: Represents a list of {@code Pin} objects associated with the user,
 *   linked by the "user_id" field.
 * - {@code tracks}: Represents a list of {@code TrackWithPins} objects associated
 *   with the user, also linked by the "user_id" field.
 *
 * This class is typically used for scenarios where additional content or relationships
 * related to the user need to be retrieved or processed.
 */
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
