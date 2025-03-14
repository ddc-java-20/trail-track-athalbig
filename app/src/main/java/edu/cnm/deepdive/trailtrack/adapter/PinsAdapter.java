package edu.cnm.deepdive.trailtrack.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import edu.cnm.deepdive.trailtrack.databinding.ItemPinBinding;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import java.util.List;

public class PinsAdapter extends Adapter<ViewHolder> {

  private final LayoutInflater inflater;
  private final List<Pin> pins;
  private final OnLongClickListener listener;

  public PinsAdapter(@NonNull Context context, @NonNull List<Pin> pins, @NonNull OnLongClickListener listener) {
    this.pins = pins;
    inflater = LayoutInflater.from(context);
    this.listener = listener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    ItemPinBinding binding = ItemPinBinding.inflate(inflater, viewGroup, false);
    return new Holder(binding, listener);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ((Holder) holder).bind(position, pins.get(position));
    // done 2025-02-13 Invoke holder.bind with the object in 'position'.
  }

  @Override
  public int getItemCount() {
    return pins.size();
    // done 2025-02-13 Return the number of Pin instances that can be displayed in this view.
  }

  private static class Holder extends ViewHolder {

    private final ItemPinBinding binding;
    private final OnLongClickListener listener;

    public Holder(@NonNull ItemPinBinding binding, @NonNull OnLongClickListener listener) {
      super(binding.getRoot());
      this.binding = binding;
      // TODO: 2025-02-13 Initialize any fields.
      this.listener = listener;
    }

    public void bind(int position, Pin pin) {
      binding.title.setText(pin.getTitle());
      binding.modifiedOn.setText(pin.getModifiedOn().toString());
      // 2025-02-13 Use data from item to populate widgets in itemView.
      binding.getRoot().setOnLongClickListener(view -> listener.onLongClick(view, pin, position));
    }

  }

  @FunctionalInterface
  public interface OnLongClickListener {
    boolean onLongClick(View view, Pin pin, int position);
  }
}
