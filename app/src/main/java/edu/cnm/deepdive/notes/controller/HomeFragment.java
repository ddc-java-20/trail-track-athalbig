package edu.cnm.deepdive.notes.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.notes.R;
import edu.cnm.deepdive.notes.adapter.NotesAdapter;
import edu.cnm.deepdive.notes.databinding.FragmentHomeBinding;
import edu.cnm.deepdive.notes.model.entity.Note;
import edu.cnm.deepdive.notes.viewmodel.NoteViewModel;
import java.util.List;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

  private static final String TAG = HomeFragment.class.getSimpleName();
  private FragmentHomeBinding binding;
  private NoteViewModel viewModel;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentHomeBinding.inflate(inflater, container, false);
binding.newNote.setOnClickListener((v) -> Navigation.findNavController(binding.getRoot())
    .navigate(HomeFragmentDirections.openEditFragment()));
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
    viewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
    viewModel
        .getNotes()
        .observe(lifecycleOwner, this::handleNotes);
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void handleNotes(List<Note> notes) {
    NotesAdapter adapter;
    adapter = new NotesAdapter(requireContext(), notes, (v, note, position) -> {

      PopupMenu popup = new PopupMenu(requireContext(), v);
      Menu menu = popup.getMenu();
      popup.getMenuInflater().inflate(R.menu.note_options, menu);
      menu.findItem(R.id.edit_note).setOnMenuItemClickListener(item -> {
        Log.d(TAG, String.format("onMenuItemClick: item=%s", item));
        Navigation.findNavController(binding.getRoot())
            .navigate(HomeFragmentDirections.openEditFragment().setNoteId(note.getId()));
        return true;
      });
      menu
          .findItem(R.id.delete_note)
          .setOnMenuItemClickListener((item) -> {
            Log.d(TAG, String.format("onMenuItemClick: item=%s", item));
            viewModel.delete(note);
//            adapter.notifyItemRemoved(position);
            return true;
          });
      popup.show();
      return true;
    });
    binding.notes.setAdapter(adapter);
  }
}