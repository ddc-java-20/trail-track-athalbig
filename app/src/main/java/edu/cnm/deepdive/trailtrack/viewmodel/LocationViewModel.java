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

@HiltViewModel
public class LocationViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final LocationService locationService;

  @Inject
  LocationViewModel(LocationService locationService) {
    this.locationService = locationService;
  }

  public LiveData<Location> getLocation() {
    return locationService.getLocation();
  }

  public void startService() {
    locationService.startService();
  }

  public void stopService() {
    locationService.stopService();
  }

  @Override
  public void onStop(@NonNull LifecycleOwner owner) {
    stopService();
    DefaultLifecycleObserver.super.onStop(owner);
  }
}