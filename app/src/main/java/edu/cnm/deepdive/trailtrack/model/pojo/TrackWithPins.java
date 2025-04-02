package edu.cnm.deepdive.trailtrack.model.pojo;

import androidx.room.Relation;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.model.entity.Track;
import java.util.List;

/**
 * Represents a specialized {@code Track} that includes a collection of associated {@code Pin} objects.
 * This class extends the {@link Track} class to encompass additional functionality for managing related pins.
 *
 * Features:
 * - Inherits all properties and methods from the {@link Track} class.
 * - Introduces a one-to-many relationship between a {@code Track} and its {@code Pin} objects.
 *
 * Relationship:
 * - Annotated with {@link Relation} to define a database relationship between the {@code Track}
 *   and {@code Pin} entities. The relation is based on the "track_id" column present in both entities,
 *   wherein each track may have multiple pins associated with it.
 *
 * Methods:
 * - Provides getter and setter methods for accessing and modifying the list of associated {@code Pin} objects.
 */
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
