package de.digitalcollections.iiif.presentation.model.impl.v2_0_0;

import de.digitalcollections.iiif.presentation.model.api.v2_0_0.Tile;

public class TileImpl implements Tile {

  private int width;
  private int[] scaleFactors;

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public void setWidth(int width) {
    this.width = width;
  }

  @Override
  public int[] getScaleFactors() {
    return scaleFactors;
  }

  @Override
  public void setScaleFactors(int[] scaleFactors) {
    this.scaleFactors = scaleFactors;
  }
}
