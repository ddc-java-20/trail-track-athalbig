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

@Singleton
public class LocationService {

  private final Context context;
  private final MutableLiveData<Location> location;
  private final LocationCallback locationCallback;
  private final FusedLocationProviderClient locationClient;
  private final ExecutorService executorService;

  @Inject
  public LocationService(@ApplicationContext Context context) {
    this.context = context;
    location = new MutableLiveData<>();
    locationCallback = new CurrentLocationCallback(location);
    locationClient = LocationServices.getFusedLocationProviderClient(context);
    executorService = Executors.newSingleThreadExecutor();
  }

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

  public void stopService() {
    locationClient.removeLocationUpdates(locationCallback);
  }

  public LiveData<Location> getLocation() {
    return location;
  }

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

    public LiveData<Location> getLocation() {
      return location;
    }
  }
}
