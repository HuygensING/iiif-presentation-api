package de.digitalcollections.iiif.presentation.model.api.v2_0_0;

import java.net.URI;

public interface SeeAlso {
  URI getId();

  void setId(URI uri);

  String getFormat();

  void setFormat(String fmt);

  URI getProfile();

  void setProfile(URI uri);
}
