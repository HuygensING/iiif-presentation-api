package de.digitalcollections.iiif.presentation.model.impl.jackson.deserializer.v2_0_0;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import de.digitalcollections.iiif.presentation.model.api.v2_0_0.SeeAlso;
import de.digitalcollections.iiif.presentation.model.impl.v2_0_0.SeeAlsoImpl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class SeeAlsoDeserializer extends JsonDeserializer<SeeAlso> {
  private URI uriFromString(String strUri) {
    try {
      return new URI(strUri);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(String.format("'%s' ist not a valid URI!", strUri));
    }
  }

  @Override
  public SeeAlso deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    ObjectMapper mapper = (ObjectMapper) jp.getCodec();
    TreeNode node = mapper.readTree(jp);
    SeeAlso seeAlso = new SeeAlsoImpl();
    if (ObjectNode.class.isAssignableFrom(node.getClass())) {
      String id = ((TextNode) node.get("@id")).textValue();
      TextNode formatNode = ((TextNode) node.get("format"));
      TextNode profileNode = ((TextNode) node.get("profile"));
      seeAlso.setId(uriFromString(id));
      if (formatNode != null) {
        seeAlso.setFormat(formatNode.textValue());
      }
      if (profileNode != null) {
        seeAlso.setProfile(uriFromString(profileNode.textValue()));
      }
    } else if (TextNode.class.isAssignableFrom(node.getClass())) {
      seeAlso.setId(uriFromString(((TextNode) node).textValue()));
    } else {
      throw new IllegalArgumentException("SeeAlso must be a string or object!");
    }
    return seeAlso;
  }
}
