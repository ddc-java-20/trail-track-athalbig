package edu.cnm.deepdive.notes.controller;

import android.Manifest.permission;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultCallback;
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
import edu.cnm.deepdive.notes.R;
import edu.cnm.deepdive.notes.databinding.FragmentEditBinding;
import edu.cnm.deepdive.notes.model.entity.Note;
import edu.cnm.deepdive.notes.service.ImageFileProvider;
import edu.cnm.deepdive.notes.viewmodel.NoteViewModel;
import java.io.File;
import java.util.UUID;

@AndroidEntryPoint
public class EditFragment extends BottomSheetDialogFragment {

  private static final String TAG = EditFragment.class.getSimpleName();
  private static final String AUTHORITY = ImageFileProvider.class.getName().toLowerCase();

  private FragmentEditBinding binding;
  private NoteViewModel viewModel;
  private long noteId;
  private Note note;
  private ActivityResultLauncher<Uri> captureLauncher;
  private Uri uri;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    noteId = EditFragmentArgs.fromBundle(getArguments()).getNoteId();
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
    viewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
    LifecycleOwner owner = getViewLifecycleOwner();
    if (noteId != 0) {
      viewModel.fetch(noteId);
      viewModel
          .getNote()
          .observe(owner, this::handleNote);
    } else {
      // TODO: 2/18/25 Configure UI for a new note vs. editing an existing note.
      binding.image.setVisibility(View.GONE);
      note = new Note();
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
    note.setTitle(binding.title
        .getText()
        .toString()
        .strip());
    note.setContent(binding.content
        .getText()
        .toString()
        .strip());
    note.setImage(uri);
    // TODO: 2/18/25 Set/modify the createdOn/modifiedOn.
    viewModel.saveNote(note);
    dismiss();
  }

  private void handleCaptureUri(Uri uri) {
    if (uri != null) {
      this.uri = uri;
      note.setImage(uri);
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

  private void handleNote(Note note) {
    this.note = note;
    binding.title.setText(note.getTitle());
    binding.content.setText(note.getContent());
    Uri imageURI = note.getImage();
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
    File captureDir = new File(requireContext().getFilesDir(), getString(R.string.capture_directory)); // Using the context, get a reference to the directory where we store captured images.
    //noinspection ResultOfMethodCallIgnored
    captureDir.mkdir(); // Ensure that the directory exists.
    // TODO: 2/24/25 Genetrate a random filename for captured image.
    File captureFile;
    do {
      captureFile = new File(captureDir, UUID.randomUUID().toString());
    } while (captureFile.exists());
    Uri uri = FileProvider.getUriForFile(requireContext(), AUTHORITY, captureFile); // Get a URI for the random file, using the provider infrastructure.
    viewModel.setPendingCaptureUri(uri); // Store the URI in the viewmodel.
    captureLauncher.launch(uri); // Launch the capture launcher.
  }

}
