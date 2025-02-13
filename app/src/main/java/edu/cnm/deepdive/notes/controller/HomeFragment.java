package edu.cnm.deepdive.notes.controller;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.notes.NotesAdapter;
import edu.cnm.deepdive.notes.R;
import edu.cnm.deepdive.notes.databinding.FragmentHomeBinding;
import edu.cnm.deepdive.notes.viewmodel.NoteViewModel;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

  private FragmentHomeBinding binding;
  private NoteViewModel viewModel;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentHomeBinding.inflate(inflater, container, false);

    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
    viewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
    viewModel
        .getNotes()
        .observe(lifecycleOwner, (notes) -> {
          NotesAdapter adapter = new NotesAdapter(requireContext(), notes);
          binding.notes.setAdapter(adapter);
          // TODO: 2025-02-13 If creating a new adapter each time the data changes, create one now;
          //  otherwise, we need to create one earlier, and it will exist by this time.
          // TODO: 2025-02-13 Pass notes to adapter.
          // TODO: 2025-02-13 Notify adapter of change.
        });
  }
}