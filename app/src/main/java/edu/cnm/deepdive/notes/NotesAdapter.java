package edu.cnm.deepdive.notes;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.notes.NotesAdapter.Holder;

public class NotesAdapter extends RecyclerView.Adapter<Holder> {

  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return null; // TODO: 2/13/25 Create and return an instance of Holder.
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, int position) {
// TODO: 2/13/25 Invoke holder.bind with the object in 'position'.
  }

  @Override
  public int getItemCount() {
    return 0; // TODO: 2/13/25 Return the number of Note instances that can be displayed in this view.
  }

  static class Holder extends RecyclerView.ViewHolder {

    public Holder(@NonNull View itemView) {
      super(itemView);
      // TODO: 2/13/25 Initialize any fields.
    }

    public void bind(Object item){
      // TODO: 2/13/25 Use data from item to populate widgets in itemView.
    }
  }

}
