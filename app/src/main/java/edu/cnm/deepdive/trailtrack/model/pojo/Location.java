package edu.cnm.deepdive.trailtrack.model.pojo;

/**
 * Represents a geographical location defined by its latitude and longitude.
 * Instances of this class are immutable and provide values for latitude and longitude
 * in decimal degrees.
 */
public record Location(
    double latitude,
    double longitude
) {

}
