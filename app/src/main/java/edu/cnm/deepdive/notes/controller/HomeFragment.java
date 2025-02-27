package edu.cnm.deepdive.notes.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.notes.R;
import edu.cnm.deepdive.notes.adapter.NotesAdapter;
import edu.cnm.deepdive.notes.databinding.FragmentHomeBinding;
import edu.cnm.deepdive.notes.model.entity.Note;
import edu.cnm.deepdive.notes.viewmodel.LoginViewModel;
import edu.cnm.deepdive.notes.viewmodel.NoteViewModel;
import java.util.List;

/**
 * @noinspection deprecation
 */
@AndroidEntryPoint
public class HomeFragment extends Fragment implements MenuProvider {

  private static final String TAG = HomeFragment.class.getSimpleName();

  private FragmentHomeBinding binding;
  private NoteViewModel noteViewModel;
  private LoginViewModel loginViewModel;

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
    ViewModelProvider provider = new ViewModelProvider(requireActivity());
    noteViewModel = provider.get(NoteViewModel.class);
    noteViewModel
        .getNotes()
        .observe(lifecycleOwner, this::handleNotes);
    loginViewModel = provider.get(LoginViewModel.class);
    loginViewModel
        .getAccount()
        .observe(lifecycleOwner, this::handleAccount);
    requireActivity().addMenuProvider(this, lifecycleOwner, State.RESUMED);
  }

  private void handleAccount(GoogleSignInAccount account) {
    if (account == null) { //non-null account could be used to add customization for logged-in user
      Navigation.findNavController(binding.getRoot())
          .navigate(HomeFragmentDirections.navigateToPreLoginFragment());
    }
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
            noteViewModel.delete(note);
//            adapter.notifyItemRemoved(position);
            return true;
          });
      popup.show();
      return true;
    });
    binding.notes.setAdapter(adapter);
  }

  @Override
  public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
    menuInflater.inflate(R.menu.note_actions, menu);
  }

  @Override
  public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
    boolean handled = true;
    if (menuItem.getItemId() == R.id.sign_out) {
      loginViewModel.signOut();
    } else {
      handled = false;
    }
    return handled;
  }
}