package edu.cnm.deepdive.notes.controller;

import android.app.Dialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.notes.R;

@AndroidEntryPoint
public class EditFragment extends BottomSheetDialogFragment {

  private static final String TAG = EditFragment.class.getSimpleName();

  @ColorInt
  private int cancelColor;
  @ColorInt
  private int saveColor;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // TODO: 2/18/25 Read any input arguments.
    cancelColor = getThemeColor(R.attr.colorCancel);
    saveColor = getThemeColor((R.attr.colorSave))
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
    // TODO: 2/18/25 Return root element of layout.
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // TODO: 2/18/25 Connect to viewmodel(s) and observe LiveData.
  }

  @Override
  public void onDestroyView() {
    // TODO: 2/18/25 Set binding reference(s) to null.
    super.onDestroyView();
  }

  @ColorInt
  private int getThemeColor(int colorAttr) {
    TypedValue typedValue = new TypedValue();
    requireContext().getTheme().resolveAttribute(colorAttr, typedValue, true);
    return typedValue.data;
  }

}
