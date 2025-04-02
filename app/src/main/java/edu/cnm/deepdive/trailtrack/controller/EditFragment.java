package edu.cnm.deepdive.trailtrack.controller;

import android.Manifest.permission;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.trailtrack.R;
import edu.cnm.deepdive.trailtrack.databinding.FragmentEditBinding;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.service.ImageFileProvider;
import edu.cnm.deepdive.trailtrack.viewmodel.LocationViewModel;
import edu.cnm.deepdive.trailtrack.viewmodel.PermissionsViewModel;
import edu.cnm.deepdive.trailtrack.viewmodel.PinViewModel;
import java.io.File;
import java.util.UUID;

/**
 * EditFragment is a BottomSheetDialogFragment that provides editing functionality for a
 * Pin object. This class manages the interaction between the UI, ViewModels, and system
 * resources to allow users to edit and save pin attributes.
 *
 * It includes the following responsibilities:
 * - Displaying the details of a Pin object, such as title, content, and image.
 * - Allowing the user to update the Pin's title, content, image, and location.
 * - Handling camera permissions and initiating image capture.
 * - Observing and interacting with LiveData from various ViewModels.
 *
 * This fragment utilizes data binding to bind UI elements to corresponding data processes
 * and ViewModels for core business logic.
 */
@AndroidEntryPoint
public class EditFragment extends BottomSheetDialogFragment {

  private static final String TAG = EditFragment.class.getSimpleName();
  private static final String AUTHORITY = ImageFileProvider.class.getName().toLowerCase();

  private FragmentEditBinding binding;
  private PinViewModel pinViewModel;
  private PermissionsViewModel permissionsViewModel;
  private LocationViewModel locationViewModel;
  private long pinId;
  private Pin pin;
  private ActivityResultLauncher<Uri> captureLauncher;
  private Uri uri;
  private Location location;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    pinId = EditFragmentArgs.fromBundle(getArguments()).getPinId();
    // Read any input arguments.
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    // TODO: 2/18/25 Inflate layout and construct & return dialog containing layout.
    return super.onCreateDialog(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    // Return root element of layout.
    binding = FragmentEditBinding.inflate(inflater, container, false);
    // Attach listeners to UI widgets.
    binding.cancel.setOnClickListener((v) -> dismiss());
    binding.save.setOnClickListener((v) -> save());
    binding.capture.setOnClickListener((v) -> capture());
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // Connect to viewmodel(s) and observe LiveData.
    FragmentActivity activity = requireActivity();
    ViewModelProvider provider = new ViewModelProvider(activity);
    pinViewModel = provider.get(PinViewModel.class);
    LifecycleOwner owner = getViewLifecycleOwner();
    if (pinId != 0) {
      pinViewModel.fetch(pinId);
      pinViewModel
          .getPin()
          .observe(owner, this::handlePin);
    } else {
      binding.image.setVisibility(View.GONE);
      pin = new Pin();
      uri = null;
      pinViewModel.clearCaptureUri();
    }
    pinViewModel
        .getCaptureUri()
        .observe(owner, this::handleCaptureUri);
    locationViewModel = provider.get(LocationViewModel.class);
    getLifecycle().addObserver(locationViewModel);
    locationViewModel.getLocation()
        .observe(owner, location -> {
          this.location = location;
          Log.d(TAG, "Location: " + location);
          // TODO: 3/31/25 Do something with the location. Probably store it in the location embedded field.

        });
    permissionsViewModel = provider.get(PermissionsViewModel.class);
    permissionsViewModel
        .getPermissionsStatus()
        .observe(owner, (permissions) -> {
          //noinspection DataFlowIssue
          binding.capture.setVisibility(permissions.getOrDefault(permission.CAMERA, false) ? View.VISIBLE : View.GONE);
          if (Boolean.TRUE.equals(permissions.getOrDefault(permission.ACCESS_FINE_LOCATION, false))) {
            locationViewModel.startService();
          }
        });
    captureLauncher = registerForActivityResult(
        new ActivityResultContracts.TakePicture(), pinViewModel::confirmCapture);
  }

  @Override
  public void onDestroyView() {
    binding = null; // Set binding reference(s) to null.
    super.onDestroyView();
  }

  /**
   * @noinspection DataFlowIssue
   */
  /**
   * Saves the current state of the pin object by updating its title, content, image, and optionally
   * its location, and passing the updated object to the appropriate ViewModel for persistence.
   * The method performs the following steps:
   * 1. Extracts and trims user-provided title and content from the UI and updates the pin object.
   * 2. Sets the associated image for the pin.
   * 3. If a location is provided, it creates a {@code Location} object with latitude and longitude values
   *    and assigns it to the pin.
   * 4. Invokes the ViewModel's save method to persist the changes.
   * 5. Closes the current dialog or view after saving.
   *
   * Note: The handling of created and modified timestamps is pending implementation.
   */
  private void save() {
    pin.setTitle(binding.title
        .getText()
        .toString()
        .strip());
    pin.setContent(binding.content
        .getText()
        .toString()
        .strip());
    pin.setImage(uri);
    if (location != null) {
      pin.setLocation(new edu.cnm.deepdive.trailtrack.model.pojo.Location(
          location.getLatitude(), location.getLongitude()));
    }
    // TODO: 2/18/25 Set/modify the createdOn/modifiedOn.
    pinViewModel.savePin(pin);
    dismiss();
  }

  /**
   * Handles the provided URI by updating the associated image and displaying it in the UI.
   *
   * @param uri the URI of the image to be handled. If the URI is null, the method does nothing.
   */
  private void handleCaptureUri(Uri uri) {
    if (uri != null) {
      this.uri = uri;
      pin.setImage(uri);
      binding.image.setImageURI(uri);
      binding.image.setVisibility(View.VISIBLE);
    }
  }

  /**
   * Updates the UI elements with the details of the provided pin object.
   *
   * The method performs the following:
   * - Assigns the input pin to the class-level pin variable.
   * - Sets the title and content in the UI using the title and content values of the pin.
   * - Updates the image view based on the image URI:
   *   - If the image URI is not null, the image is displayed and made visible.
   *   - If the image URI is null, the image view is hidden.
   *
   * @param pin the pin object whose details are to be displayed in the UI.
   */
  private void handlePin(Pin pin) {
    this.pin = pin;
    binding.title.setText(pin.getTitle());
    binding.content.setText(pin.getContent());
    Uri imageURI = pin.getImage();
    if (imageURI != null) {
      binding.image.setImageURI(imageURI);
      binding.image.setVisibility(View.VISIBLE);
    } else {
      binding.image.setVisibility(View.GONE);
    }
    uri = imageURI;
  }

  @ColorInt
  private int getThemeColor(int colorAttr) {
    TypedValue typedValue = new TypedValue();
    requireContext().getTheme().resolveAttribute(colorAttr, typedValue, true);
    return typedValue.data;
  }

  /**
   * Initiates the process of capturing an image and storing its reference for later use.
   *
   * The method performs the following steps:
   * 1. Retrieves the application context to access the file system.
   * 2. Ensures the existence of the directory designed to store captured images.
   * 3. Generates a unique filename for the captured image to avoid conflicts.
   * 4. Obtains a URI for the created file using the application's FileProvider infrastructure.
   * 5. Sets the generated URI as a pending capture URI in the ViewModel.
   * 6. Launches the capture process using the capture launcher.
   *
   * The generated URI is used to save the captured image, and the ViewModel stores it for further handling.
   */
  private void capture() {
    Context context = requireContext();
    File captureDir = new File(context.getFilesDir(), getString(R.string.capture_directory)); // Using the context, get a reference to the directory where we store captured images.
    //noinspection ResultOfMethodCallIgnored
    captureDir.mkdir(); // Ensure that the directory exists.
    // TODO: 2/24/25 Genetrate a random filename for captured image.
    File captureFile;
    do {
      captureFile = new File(captureDir, UUID.randomUUID().toString());
    } while (captureFile.exists());
    Uri uri = FileProvider.getUriForFile(context, AUTHORITY, captureFile); // Get a URI for the random file, using the provider infrastructure.
    pinViewModel.setPendingCaptureUri(uri); // Store the URI in the viewmodel.
    captureLauncher.launch(uri); // Launch the capture launcher.
  }

}
