package edu.cnm.deepdive.trailTrack.service;

import androidx.core.content.FileProvider;
import edu.cnm.deepdive.trailTrack.R;

public class ImageFileProvider extends FileProvider {

  public ImageFileProvider() {
    super(R.xml.provider_paths); // Initialize with our provider-path mapping
  }

}
