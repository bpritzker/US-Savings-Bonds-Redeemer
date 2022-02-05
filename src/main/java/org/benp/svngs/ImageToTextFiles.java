package org.benp.svngs;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import org.apache.commons.io.FilenameUtils;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * This will convert the images to text files.
 *
 */
public class ImageToTextFiles {


    private static final String imageDirectory = "C:\\Ben\\Documents\\Roger\\US-Savingbonds\\SingleFileScans";



    static Logger logger = Logger.getLogger(ImageToTextFiles.class.getName());


    public static void main(String[] args) {
        try {
            ImageToTextFiles detectText = new ImageToTextFiles();
            detectText.runConvertImageFiles(imageDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void runConvertImageFiles(String imageDownloadDir) throws Exception {

        List<Path> allFiles = getAllFiles(imageDownloadDir);

        logger.info("Total File found: " + allFiles.size());


        int counter = 0;
        for (Path currPath : allFiles) {
            counter++;
            System.out.println("Processing " + counter + " of " + allFiles.size());
            String subDir = FilenameUtils.getName(currPath.getParent().toString());
//            System.out.println(subDir);
            String fileName = currPath.getFileName().toString();
//            System.out.println(fileName);

            String textFileName = currPath.getParent().getParent().getParent() + "/SingleFileText/" + subDir + "--" + FilenameUtils.getBaseName(fileName) + ".txt";


            new File(textFileName).getParentFile().mkdir();


            System.out.println(textFileName);

            imageToText(currPath, textFileName);


//            System.out.println("" + currPath.getParent());


        }

    }

    private void imageToText(Path currPath, String textFileName) throws Exception {

        if (new File(textFileName).exists()) {
            logger.info("Already translated: " + textFileName);
            return;
        }


        String imageTextData = detectText(currPath.toString());
        Thread.sleep(1000);

        new File(textFileName).getParentFile().mkdir();

        FileWriter writer = new FileWriter(textFileName);
        writer.write(imageTextData + System.lineSeparator());
        writer.close();


    }

    private List<Path> getAllFiles(String imageDownloadDir) throws IOException {

//        List<String> result = new ArrayList<>();

//        Stream<Path> stream = Files.walk(Paths.get(imageDownloadDir))
//                .filter(Files::isRegularFile);

        List<Path> resultPaths = Files.walk(Paths.get(imageDownloadDir))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

        return resultPaths;

    }

    public void detectText() throws IOException, InterruptedException {

        Set<String> allImageFiles = listAllImageFiles();

        int counter = 0;
        for (String currImageFile : allImageFiles) {
            counter++;

            String textFileName = "C:\\Ben\\Documents\\Roger\\US-Savingbonds\\SingleFileText\\" + FilenameUtils.getBaseName(currImageFile) + ".txt";

            if ( ! new File(textFileName).exists()) {
                new File(currImageFile).getParentFile().mkdir();
                String imageTextData = detectText(currImageFile);


                FileWriter writer = new FileWriter(textFileName);
//            for(String str: imageTextData) {
                writer.write(imageTextData + System.lineSeparator());
//            }
                System.out.println("File Created: " + FilenameUtils.getName(textFileName) + "   " + counter + " of " + allImageFiles.size());
                writer.close();

            } else {
                System.out.println("Skipping.... already exists");
            }


            Thread.sleep(1000);



        }



    }

    private Set<String>  listAllImageFiles() throws IOException {
        String dir = "C:\\Documents\\US-Savingbonds\\SingleFileScans\\Batch-1";
        Set<String> fileList = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileList.add(path.toString());
                }
            }
        }
        return fileList;
    }

    // Detects text in the specified image.
    public String detectText(String filePath) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return null;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {

//                    System.out.format("Text: %s%n", annotation.getDescription());
//                    System.out.format("Position : %s%n", annotation.getBoundingPoly());
                    return annotation.getDescription();
                }
            }
        }
        return null;
    }
}