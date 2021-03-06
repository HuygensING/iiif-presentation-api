package de.digitalcollections.iiif.presentation.frontend.impl.client.rest;

import de.digitalcollections.iiif.presentation.model.api.v2_0_0.Manifest;
import feign.Param;
import feign.RequestLine;
import org.springframework.stereotype.Repository;

@Repository
public interface IIIFRepository {

  @RequestLine("GET /{id}/manifest")
  Manifest manifest(@Param("id") String id);

}
