package edu.cnm.deepdive.trailtrack.viewmodel;


import android.location.Location;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.trailtrack.service.LocationService;
import javax.inject.Inject;

/**
 * A ViewModel class for managing and providing location-related functionality within the application.
 * This class acts as an intermediary between the UI controller and the {@link LocationService},
 * leveraging the lifecycle-aware components to manage location updates and their associated resources.
 *
 * The {@code LocationViewModel} is annotated with {@code @HiltViewModel}, enabling dependency
 * injection of the {@link LocationService}. It also implements {@link DefaultLifecycleObserver} to
 * manage the lifecycle of the location service by stopping it when the associated lifecycle owner
 * is stopped.
 *
 * Responsibilities:
 * - Accessing the current location via {@link LiveData} observed from the {@link LocationService}.
 * - Starting and stopping location updates based on application needs.
 * - Automatically stopping the location updates when the lifecycle owner is stopped.
 *
 * Dependency injection ensures that necessary services are instantiated and managed by the
 * dependency injection framework, promoting decoupling and testability.
 *
 * This class integrates with lifecycle-aware components to ensure proper resource management and
 * adherence to the application's lifecycle.
 */
@HiltViewModel
public class LocationViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final LocationService locationService;

  @Inject
  LocationViewModel(LocationService locationService) {
    this.locationService = locationService;
  }

  /**
   * Retrieves the current location as a live data stream.
   * The returned {@link LiveData} object will be updated whenever the location changes,
   * providing the most recent location information obtained by the application.
   *
   * @return a {@code LiveData<Location>} instance containing the current or most recently
   * available location details.
   */
  public LiveData<Location> getLocation() {
    return locationService.getLocation();
  }

  /**
   * Starts the location service, initiating the process of obtaining and monitoring location updates
   * from the device's location provider. This method delegates the functionality to the
   * {@link LocationService#startService()} method, ensuring that the appropriate setup for location
   * updates is handled according to the application's requirements.
   *
   * This method is typically invoked when location data is required for the application's operations.
   * The lifecycle-aware implementation ensures that the location service integrates properly
   * with the application's lifecycle, allowing it to be started when necessary and stopped when
   * the associated lifecycle is stopped.
   *
   * Note: It is assumed that the necessary location permissions are granted prior to invoking this method.
   */
  public void startService() {
    locationService.startService();
  }

  /**
   * Stops the location service, terminating any ongoing location tracking operations.
   * This method delegates the functionality to the {@link LocationService#stopService()} method,
   * ensuring that active location updates are halted and resources are released. Typically invoked
   * when location data is no longer required or the application is transitioning to a state where
   * location usage is not necessary.
   */
  public void stopService() {
    locationService.stopService();
  }

  @Override
  public void onStop(@NonNull LifecycleOwner owner) {
    stopService();
    DefaultLifecycleObserver.super.onStop(owner);
  }
}