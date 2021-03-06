package de.digitalcollections.iiif.presentation.frontend.impl.springmvc.controller.v2_0_0;

import de.digitalcollections.iiif.presentation.business.api.v2_0_0.PresentationService;
import de.digitalcollections.iiif.presentation.frontend.impl.springmvc.exception.NotFoundException;
import de.digitalcollections.iiif.presentation.model.api.v2_0_0.Manifest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * IIIF Presentation API implementation. Supported URLs (examples):
 * <ul>
 * <li>http://localhost:9898/iiif/presentation/2.0.0/1234/manifest</li>
 * </ul>
 */
@Controller(value = "IIIFPresentationApiController-v2.0.0")
@RequestMapping("/iiif/presentation/2.0.0")
public class IIIFPresentationApiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(IIIFPresentationApiController.class);
  public static final String VERSION = "2.0.0";

  @Autowired
  private PresentationService presentationService;

  /**
   * The manifest response contains sufficient information for the client to initialize itself and begin to display
   * something quickly to the user. The manifest resource represents a single object and any intellectual work or works
   * embodied within that object. In particular it includes the descriptive, rights and linking information for the
   * object. It then embeds the sequence(s) of canvases that should be rendered to the user.
   *
   * @param identifier unique id of object to be shown
   * @return the JSON-Manifest
   * @throws NotFoundException if manifest can not be delivered
   * @see <a href="http://iiif.io/api/presentation/2.0/#manifest">IIIF 2.0</a>
   */
  @CrossOrigin(allowedHeaders = {"*"}, origins = {"*"})
  @RequestMapping(value = "{identifier}/manifest", method = RequestMethod.GET,
          produces = "application/json")
  @ResponseBody
  public Manifest getManifest(@PathVariable String identifier) throws NotFoundException {
    LOGGER.info("Manifest version '{}' for identifier '{}' requested.", VERSION, identifier);
    Manifest manifest;
    try {
      manifest = presentationService.getManifest(identifier);
    } catch (de.digitalcollections.iiif.presentation.business.api.exceptions.NotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
    return manifest;
  }
}
