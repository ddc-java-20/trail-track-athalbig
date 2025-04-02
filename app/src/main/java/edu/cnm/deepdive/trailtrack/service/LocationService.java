package edu.cnm.deepdive.trailtrack.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import dagger.hilt.android.qualifiers.ApplicationContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Provides location services for obtaining and monitoring location updates. This class interacts
 * with the device's fused location provider to retrieve location data and manages the lifecycle
 * of location updates.
 *
 * The service makes use of the {@link MutableLiveData} to hold and expose the current location,
 * allowing other components to observe changes. It includes functionality to start and stop the
 * location updates as needed, based on the client's requirements.
 *
 * The {@code LocationService} ensures that location updates are retrieved asynchronously and
 * can handle updates at a specified interval and distance. The class also includes an
 * internal callback for processing location updates.
 */
@Singleton
public class LocationService {

  private final Context context;
  private final MutableLiveData<Location> location;
  private final LocationCallback locationCallback;
  private final FusedLocationProviderClient locationClient;
  private final ExecutorService executorService;

  /**
   * Creates an instance of {@code LocationService} with the specified application context.
   * Initializes necessary components for managing and retrieving location updates.
   *
   * @param context application context used to initialize the fused location provider client
   *                and other components.
   */
  @Inject
  public LocationService(@ApplicationContext Context context) {
    this.context = context;
    location = new MutableLiveData<>();
    locationCallback = new CurrentLocationCallback(location);
    locationClient = LocationServices.getFusedLocationProviderClient(context);
    executorService = Executors.newSingleThreadExecutor();
  }

  /**
   * Initiates the process of obtaining and monitoring location updates from the device's
   * fused location provider. This method sets up a location request with a specified interval,
   * maximum number of updates, and minimum distance threshold for updates. It retrieves the
   * current location asynchronously and updates the internal {@code MutableLiveData<Location>}
   * with the latest location data. Additionally, it registers a location callback to listen
   * for ongoing location updates.
   *
   * This method relies on the appropriate permissions being granted to access location services.
   * It is annotated with {@code @SuppressLint("MissingPermission")} to suppress related lint
   * checks, assuming the required permissions are being handled by the caller.
   */
  @SuppressLint("MissingPermission")
  public void startService() {
    LocationRequest request = new LocationRequest.Builder(10_000)
        .setMaxUpdates(1)
        .setMinUpdateDistanceMeters(10)
        .build();
    locationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .addOnSuccessListener(value ->
                this.location.postValue(value));
    locationClient.requestLocationUpdates(request, executorService, locationCallback);
  }

  /**
   * Stops location updates from the device's fused location provider by removing the previously
   * registered location callback. This method is intended to terminate any ongoing location
   * tracking and release resources associated with location updates.
   */
  public void stopService() {
    locationClient.removeLocationUpdates(locationCallback);
  }

  /**
   * Retrieves the current location data encapsulated in a {@code LiveData<Location>} object.
   * This method returns a reactive data holder that gets updated whenever the location service
   * detects changes to the device's location.
   *
   * @return a {@code LiveData<Location>} object containing the current or most recent location
   * of the device if available.
   */
  public LiveData<Location> getLocation() {
    return location;
  }

  /**
   * A callback implementation for handling location updates from the fused location provider.
   * This class extends {@link LocationCallback} and is intended to capture the most recent
   * location and store it in a {@link MutableLiveData} object, enabling reactive updates of
   * the location data.
   */
  private static class CurrentLocationCallback extends LocationCallback {

    private final MutableLiveData<Location> location;

    private CurrentLocationCallback(MutableLiveData<Location> location) {
      this.location = location;
    }

    @Override
    public void onLocationResult(@NonNull LocationResult locationResult) {
      super.onLocationResult(locationResult);
      location.postValue(locationResult.getLastLocation());
    }

    /**
     * Returns a {@link LiveData} object that contains the most recent location data.
     * This method provides reactive updates on location changes, allowing consumers
     * to observe and respond to location updates as they occur.
     *
     * @return A {@link LiveData} instance holding the latest {@link Location} data.
     */
    public LiveData<Location> getLocation() {
      return location;
    }
  }
}
