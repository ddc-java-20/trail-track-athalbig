package edu.cnm.deepdive.trailtrack.controller;

import android.Manifest.permission;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.trailtrack.R;
import edu.cnm.deepdive.trailtrack.databinding.FragmentEditBinding;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.service.ImageFileProvider;
import edu.cnm.deepdive.trailtrack.viewmodel.PinViewModel;
import java.io.File;
import java.util.UUID;

@AndroidEntryPoint
public class EditFragment extends BottomSheetDialogFragment {

  private static final String TAG = EditFragment.class.getSimpleName();
  private static final String AUTHORITY = ImageFileProvider.class.getName().toLowerCase();

  private FragmentEditBinding binding;
  private PinViewModel viewModel;
  private long pinId;
  private Pin pin;
  private ActivityResultLauncher<Uri> captureLauncher;
  private Uri uri;

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
    setCaptureVisibility();
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // Connect to viewmodel(s) and observe LiveData.
    viewModel = new ViewModelProvider(requireActivity()).get(PinViewModel.class);
    LifecycleOwner owner = getViewLifecycleOwner();
    if (pinId != 0) {
      viewModel.fetch(pinId);
      viewModel
          .getPin()
          .observe(owner, this::handlePin);
    } else {
      // TODO: 2/18/25 Configure UI for a new pin vs. editing an existing pin.
      binding.image.setVisibility(View.GONE);
      pin = new Pin();
      uri = null;
      viewModel.clearCaptureUri();
    }
    viewModel
        .getCaptureUri()
        .observe(owner, this::handleCaptureUri);

    captureLauncher = registerForActivityResult(
        new ActivityResultContracts.TakePicture(), viewModel::confirmCapture);
  }

  @Override
  public void onDestroyView() {
    binding = null; // Set binding reference(s) to null.
    super.onDestroyView();
  }

  /**
   * @noinspection DataFlowIssue
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
    // TODO: 2/18/25 Set/modify the createdOn/modifiedOn.
    viewModel.savePin(pin);
    dismiss();
  }

  private void handleCaptureUri(Uri uri) {
    if (uri != null) {
      this.uri = uri;
      pin.setImage(uri);
      binding.image.setImageURI(uri);
      binding.image.setVisibility(View.VISIBLE);
    }
  }

  private void setCaptureVisibility() {
    if (ContextCompat.checkSelfPermission(requireContext(), permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED) {
      binding.capture.setVisibility(View.VISIBLE);
    } else {
      binding.capture.setVisibility(View.GONE);
    }
  }

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
    viewModel.setPendingCaptureUri(uri); // Store the URI in the viewmodel.
    captureLauncher.launch(uri); // Launch the capture launcher.
  }

}
