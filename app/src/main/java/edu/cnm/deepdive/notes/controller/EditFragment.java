package edu.cnm.deepdive.notes.controller;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.notes.R;
import edu.cnm.deepdive.notes.databinding.FragmentEditBinding;
import edu.cnm.deepdive.notes.model.entity.Note;
import edu.cnm.deepdive.notes.viewmodel.NoteViewModel;

@AndroidEntryPoint
public class EditFragment extends BottomSheetDialogFragment {

  private static final String TAG = EditFragment.class.getSimpleName();

  private FragmentEditBinding binding;
  private NoteViewModel viewModel;
  private long noteId;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // TODO: 2/18/25 Read any input arguments.
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
    // TODO: 2/18/25 Attach listeners to UI widgets.
    return binding.getRoot();

  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // Connect to viewmodel(s) and observe LiveData.
    viewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
    if (noteId != 0) {
      viewModel.fetch(noteId);
      viewModel
          .getNote()
          .observe(getViewLifecycleOwner(), this::handleNote);
    } else {
      // TODO: 2/18/25 Configure UI for a new note vs. editing an existing note.
      binding.image.setVisibility(View.GONE);
    }
  }

  @Override
  public void onDestroyView() {
    binding = null; // Set binding reference(s) to null.
    super.onDestroyView();
  }

  @ColorInt
  private int getThemeColor(int colorAttr) {
    TypedValue typedValue = new TypedValue();
    requireContext().getTheme().resolveAttribute(colorAttr, typedValue, true);
    return typedValue.data;
  }

  private void handleNote(Note note) {
    binding.title.setText(note.getTitle());
    binding.content.setText(note.getContent());
    Uri imageURI = note.getImage();
    if (imageURI != null) {
      binding.image.setImageURI(imageURI);
      binding.image.setVisibility(View.VISIBLE);
    } else {
      binding.image.setVisibility(View.GONE);
    }
  }

}
