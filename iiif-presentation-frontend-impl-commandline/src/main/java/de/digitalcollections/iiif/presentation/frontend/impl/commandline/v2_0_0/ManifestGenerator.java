package de.digitalcollections.iiif.presentation.frontend.impl.commandline.v2_0_0;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.digitalcollections.iiif.presentation.model.api.v2_0_0.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2_0_0.Image;
import de.digitalcollections.iiif.presentation.model.api.v2_0_0.ImageResource;
import de.digitalcollections.iiif.presentation.model.api.v2_0_0.Manifest;
import de.digitalcollections.iiif.presentation.model.api.v2_0_0.PropertyValue;
import de.digitalcollections.iiif.presentation.model.api.v2_0_0.Sequence;
import de.digitalcollections.iiif.presentation.model.api.v2_0_0.Service;
import de.digitalcollections.iiif.presentation.model.impl.jackson.v2_0_0.IiifPresentationApiObjectMapper;
import de.digitalcollections.iiif.presentation.model.impl.v2_0_0.CanvasImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2_0_0.ImageImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2_0_0.ImageResourceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2_0_0.ManifestImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2_0_0.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2_0_0.SequenceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2_0_0.ServiceImpl;

public class ManifestGenerator {

  public static void main(String[] args) throws ParseException, JsonProcessingException, IOException, URISyntaxException {
    Options options = new Options();
    options.addOption("u", true, "Url prefix.");
    options.addOption("o", true, "Output path for JSON file. Prints to stdout by default.");
    options.addOption("x", true, "Filter by extension. Default is jpg.");
    options.addOption("d", true, "Absolute file path to the directory containing the image files.");

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    String urlPrefix = "http://www.yourdomain.com/iiif/presentation/2.0.0/";
    String outputFile = "-";
    String extension = "jpg";

    if (cmd.hasOption("u")) {
      urlPrefix = cmd.getOptionValue("u");
    }
    if (cmd.hasOption("o")) {
      outputFile = cmd.getOptionValue("o");
    }
    if (cmd.hasOption("x")) {
      extension= cmd.getOptionValue("x");
    }
    if (cmd.hasOption("d")) {
      String imageDirectoryPath = cmd.getOptionValue("d");
      Path imageDirectory = Paths.get(imageDirectoryPath);
      List<Path> files = new ArrayList<>();
      try {

        String finalExtension = extension;
        files = Files.walk(imageDirectory)
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName()
                                .toString().endsWith("."+ finalExtension))
                .collect(Collectors.toList());

      } catch (IOException e) {
        e.printStackTrace();
      }
      Collections.sort(files, new Comparator() {
        @Override
        public int compare(Object fileOne, Object fileTwo) {
          String filename1 = ((Path) fileOne).getFileName().toString();
          String filename2 = ((Path) fileTwo).getFileName().toString();

          try {
            // numerical sorting
            Integer number1 = Integer.parseInt(filename1.substring(0, filename1.lastIndexOf(".")));
            Integer number2 = Integer.parseInt(filename2.substring(0, filename2.lastIndexOf(".")));
            return number1.compareTo(number2);
          } catch (NumberFormatException nfe) {
            // alpha-numerical sorting
            return filename1.compareToIgnoreCase(filename2);
          }
        }
      });

      generateManifest(imageDirectory.getFileName().toString(), files, urlPrefix, outputFile);
    } else {
      // automatically generate the help statement
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("ManifestGenerator", options);
    }
  }

  private static void generateManifest(final String imageDirectoryName, final List<Path> files, String urlPrefix, String outputFile)
      throws JsonProcessingException, IOException, URISyntaxException {
    // Start Manifest
    PropertyValue manifestLabel = new PropertyValueSimpleImpl("Manifest for " + imageDirectoryName);
    Manifest manifest = new ManifestImpl(urlPrefix + imageDirectoryName + "/manifest.json", manifestLabel);

    List<Sequence> sequences = new ArrayList<>();
    manifest.setSequences(sequences);

    Sequence seq1 = new SequenceImpl(new PropertyValueSimpleImpl("Current page order"));
    seq1.setId(urlPrefix + imageDirectoryName + "/sequence/normal");
    sequences.add(seq1);

    List<Canvas> canvases = new ArrayList<>();
    seq1.setCanvases(canvases);

    int i = 0;
    for (Path file : files) {
      i = i + 1;
      addPage(urlPrefix, imageDirectoryName, canvases, i, file);
    }

    ManifestGenerator mg = new ManifestGenerator();
    String json = mg.generateJson(manifest);
    if (outputFile.equals("-")) {
      System.out.println(json);
    }else{
      try(  PrintWriter out = new PrintWriter( outputFile )  ){
        out.println( json );
      }
    }
  }

  private static void addPage(String urlPrefix, String imageDirectoryName, List<Canvas> canvases, int pageCounter, Path file)
      throws IOException, URISyntaxException {
    Path fileName = file.getFileName();
    BufferedImage bimg = ImageIO.read(file.toFile());
    int width = bimg.getWidth();
    int height = bimg.getHeight();

    // add a new page
    Canvas canvas1 = new CanvasImpl(urlPrefix + imageDirectoryName + "/canvas/canvas-" + pageCounter, new PropertyValueSimpleImpl("p-" + pageCounter), height, width);
    canvases.add(canvas1);

    List<Image> images = new ArrayList<>();
    canvas1.setImages(images);

    Image image1 = new ImageImpl();
    image1.setOn(canvas1.getId());
    images.add(image1);

    ImageResource imageResource1 = new ImageResourceImpl(urlPrefix + imageDirectoryName + "/" + fileName.
        toString());
    imageResource1.setHeight(height);
    imageResource1.setWidth(width);
    image1.setResource(imageResource1);

    Service service1 = new ServiceImpl(urlPrefix + imageDirectoryName + "/" + fileName.toString() + "?");
    service1.setContext("http://iiif.io/api/image/2/context.json");
    service1.setProfile("http://iiif.io/api/image/2/level1.json");
    imageResource1.setService(service1);
  }

  public ManifestGenerator() {
  }

  public String generateJson(Manifest manifest) throws JsonProcessingException {
    ObjectMapper mapper = new IiifPresentationApiObjectMapper();
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(manifest);
    return json;
  }
}
