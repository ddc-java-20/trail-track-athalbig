package edu.cnm.deepdive.trailtrack.controller;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.trailtrack.MapsPinsNavGraphDirections;
import edu.cnm.deepdive.trailtrack.databinding.FragmentMapBinding;
import edu.cnm.deepdive.trailtrack.databinding.FragmentPinsBinding;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.model.entity.Track;
import edu.cnm.deepdive.trailtrack.model.pojo.Location;
import edu.cnm.deepdive.trailtrack.viewmodel.LocationViewModel;
import edu.cnm.deepdive.trailtrack.viewmodel.PermissionsViewModel;
import edu.cnm.deepdive.trailtrack.viewmodel.PinViewModel;
import java.util.List;

/**
 * MapFragment is a Fragment subclass responsible for displaying a Google Map and interacting
 * with map-related elements and user actions. It implements OnMapReadyCallback to manage
 * GoogleMap initialization and OnItemSelectedListener to handle selection changes in the tracks
 * dropdown menu. The fragment integrates multiple ViewModels and observes LiveData to synchronize
 * UI elements with real-time data updates.
 *
 * Responsibilities:
 * - Displays a Google Map using Google Maps SDK.
 * - Manages lifecycle events of the map to ensure proper behavior during Fragment lifecycle changes.
 * - Observes and reacts to changes in permissions, location, and pin data via injected ViewModels.
 * - Displays and updates markers on the map based on observed pin data.
 * - Handles user interactions such as selecting tracks and creating new pins.
 *
 * Integrations:
 * - PermissionsViewModel: Monitors and updates the user's permissions, specifically location-related permissions.
 * - LocationViewModel: Provides live updates of the user's current location and manages location services.
 * - PinViewModel: Stores and updates information about tracks and pins, including handling current selection and displaying pin markers on the map.
 *
 * Key Lifecycle Handling:
 * - Registers observers for LiveData from ViewModels to update UI elements dynamically.
 * - Manages Google Map lifecycle by forwarding lifecycle callbacks to the map object.
 *
 * Implementations:
 * - OnMapReadyCallback: Enables map setup when the map instance is ready.
 * - OnItemSelectedListener: Handles user interactions with the track selection dropdown menu.
 */
@AndroidEntryPoint
public class MapFragment extends Fragment implements OnMapReadyCallback, OnItemSelectedListener {

  private PermissionsViewModel permissionsViewModel;
  private LocationViewModel locationViewModel;
  private PinViewModel pinViewModel;
  private FragmentMapBinding mapBinding;
  private boolean currentLocationEnabled;
  private GoogleMap map;
  private List<Track> tracks;
  private CameraUpdate cameraUpdate;
  private Track track;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mapBinding = FragmentMapBinding.inflate(inflater, container, false);
    mapBinding.mainMap.onCreate(savedInstanceState);
    mapBinding.mainMap.getMapAsync(this);
    mapBinding.tracks.setOnItemSelectedListener(this);
    mapBinding.newPin.setOnClickListener((v) -> Navigation.findNavController(mapBinding.getRoot())
        .navigate(MapsPinsNavGraphDirections.editPin()));
    // TODO: 3/31/25 Attach listeners to UI widgets
    return mapBinding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    FragmentActivity activity = requireActivity();
    ViewModelProvider provider = new ViewModelProvider(activity);
    LifecycleOwner owner = getViewLifecycleOwner();

    locationViewModel = provider.get(LocationViewModel.class);
    locationViewModel.getLocation()
        .observe(owner, location -> {
           cameraUpdate = CameraUpdateFactory.newLatLng(
              new LatLng(location.getLatitude(), location.getLongitude()));
          centerCamera();
        });
    getLifecycle().addObserver(locationViewModel);
    permissionsViewModel = provider.get(PermissionsViewModel.class);
    permissionsViewModel
        .getPermissionsStatus()
        .observe(owner, (permissions) -> {
          if (Boolean.TRUE.equals(
              permissions.getOrDefault(permission.ACCESS_FINE_LOCATION, false))) {
            locationViewModel.startService();
            currentLocationEnabled = true;
          } else {
            currentLocationEnabled = false;
          }
        });
    pinViewModel = new ViewModelProvider(requireActivity()).get(PinViewModel.class);
    pinViewModel
        .getTracks()
        .observe(owner, (tracks) -> {
          this.tracks = tracks;
          ArrayAdapter<Track> adapter =
              new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, tracks);
          mapBinding.tracks.setAdapter(adapter);
          setSelectedTrack();
        });
    pinViewModel.getTrack().observe(owner, (track) -> {
      this.track = track;
      setSelectedTrack();
    });
    pinViewModel
        .getPins()
        .observe(owner, this::handlePins);
  }

  /**
   * Sets the currently selected track in the UI to match the position of the {@code track}
   * field within the {@code tracks} list. If {@code track} or {@code tracks} are {@code null},
   * this method does nothing.
   *
   * The method retrieves the index of the {@code track} within the {@code tracks} list using
   * the {@code indexOf} method. If the {@code track} exists in the list, the selection of
   * the UI component represented by {@code mapBinding.tracks} is updated to the matching
   * position.
   */
  private void setSelectedTrack() {
    if (track != null && tracks != null) {
      int position = tracks.indexOf(track);
      mapBinding.tracks.setSelection(position);
    }
  }

  /**
   * Adjusts the camera of the map to a specified update if both the map and
   * camera update objects are not null.
   *
   * This method ensures the camera's position is updated smoothly by invoking
   * the moveCamera method on the map instance with the specified camera update.
   * If either the map or the camera update is null, the method does nothing.
   */
  private void centerCamera() {
    if (map != null && cameraUpdate != null) {
      map.moveCamera(cameraUpdate);
    }
  }

  /**
   * Processes a list of pins to populate a map with markers based on each pin's location.
   * The map is cleared initially, and for each pin in the list, a marker is created
   * using the title and location from the pin and added to the map.
   *
   * @param pins a list of Pin objects to be processed, each containing a title and location data
   */
  private void handlePins(List<Pin> pins) {
    map.clear();
    pins.forEach((pin) -> {
      Location location = pin.getLocation();
      if (location != null) {
        MarkerOptions options = new MarkerOptions()
            .title(pin.getTitle())
            .position(new LatLng(location.latitude(), location.longitude()));
        map.addMarker(options);
      }
    });
    // DONE 4/1/25 Iterate over pins, create marker for each, and add it to map (googleMap Map field)
  }

  @Override
  public void onStart() {
    super.onStart();
    mapBinding.mainMap.onStart();
  }

  @Override
  public void onResume() {
    super.onResume();
    mapBinding.mainMap.onResume();
  }

  @Override
  public void onPause() {
    mapBinding.mainMap.onPause();
    super.onPause();
  }

  @Override
  public void onStop() {
    mapBinding.mainMap.onStop();
    super.onStop();
  }

  @Override
  public void onDestroyView() {
    mapBinding.mainMap.onDestroy();
    super.onDestroyView();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    mapBinding.mainMap.onSaveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onLowMemory() {
    mapBinding.mainMap.onLowMemory();
    super.onLowMemory();
  }

  /**
   * Manipulates the map once available. This callback is triggered when the map is ready to be
   * used. This is where we can add markers or lines, add listeners or move the camera. In this
   * case, we just add a marker near Sydney, Australia. If Google Play services is not installed on
   * the device, the user will be prompted to install it inside the SupportMapFragment. This method
   * will only be triggered once the user has installed Google Play services and returned to the
   * app.
   */

  @SuppressLint("MissingPermission")
  @Override
  public void onMapReady(GoogleMap googleMap) {
    map = googleMap;
    if (currentLocationEnabled) {
      googleMap.setMyLocationEnabled(true);
      centerCamera();
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> adapterView, View view, int position,
      long generatedId) {
    pinViewModel.setTrack((Track) adapterView.getItemAtPosition(position));
  }

  @Override
  public void onNothingSelected(AdapterView<?> adapterView) {
//No action needed, this doesn't happen
  }
}
