package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.ner.AnnotatedText;
import de.fraunhofer.iao.querimonia.ner.SimpleTestRecognizer;
import de.fraunhofer.iao.querimonia.ner.TextominadoTestRecognizer;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import de.fraunhofer.iao.querimonia.service.FileStorageService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a rest controller for the integration tests. For example requests, see doc/rest-examples.http
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class RecognizerController {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * This method annotates a given text input with "line" and "date" entities. It can be called with a POST-Request.
     *
     * @param input the text which will be annotated.
     * @return the text with the annotated entities.
     */
    @PostMapping(value = "/api/test/recognizer")
    public List<AnnotatedText> annotateText(@RequestBody TextInput input) {
        List<AnnotatedText> results = new ArrayList<>();
        results.add(new SimpleTestRecognizer().annotateText(input.getText()));
        return results;
    }

    /**
     * This methods finds the money value in the given text and creates a simple answer. The extraction is done using
     * textominado.
     *
     * @param input the complaint texts where answers should be generated.
     * @return // TODO documentation for return
     */
    @PostMapping(value = "/api/test/textominado")
    public List<AnnotatedText> getAnswer(@RequestBody TextInput input) {
        List<AnnotatedText> results = new ArrayList<>();
        results.add(new TextominadoTestRecognizer().annotateText(input.getText()));
        return results;
    }

    @PostMapping(value = "/api/test/textominado-batch")
    public List<AnnotatedText> getAnswersWithTextominado(@RequestParam("file") MultipartFile file) {
        Logger logger = LoggerFactory.getLogger(RecognizerController.class);
        String fileName = fileStorageService.storeFile(file);
        String fullFilePath = "src/main/resources/uploads/" + fileName;
        TextominadoTestRecognizer recognizer = new TextominadoTestRecognizer();


        List<AnnotatedText> results = new ArrayList<>();
        // read out the file and recognize text
        try (FileReader reader = new FileReader(fullFilePath)) {
            //read a txt file
            if (fullFilePath.endsWith(".txt")) {
                BufferedReader bufferedReader = new BufferedReader(reader);

                bufferedReader.lines()
                        .map(recognizer::annotateText)
                        .forEach(results::add);
            //read a pdf file
            } else if (fullFilePath.endsWith(".pdf")) {
                PDDocument document = PDDocument.load(new File(fullFilePath));
                if (!document.isEncrypted()) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(document);
                    AnnotatedText annotadedText = recognizer.annotateText(text);
                    results.add(annotadedText);
                }
                document.close();
            //read a word file
            } else if (fullFilePath.endsWith(".doc") ||fullFilePath.endsWith(".docx") || fullFilePath.endsWith(".docm")) {
                WordExtractor extractor;
                try {
                    FileInputStream fileInputStream = new FileInputStream(fullFilePath);
                    HWPFDocument document = new HWPFDocument(fileInputStream);
                    extractor = new WordExtractor(document);
                    String[] fileDates = extractor.getParagraphText();
                    for (String fileData : fileDates) {
                        if (fileData != null) {
                            AnnotatedText annotatedText = recognizer.annotateText(fileData);
                            results.add(annotatedText);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            logger.error("Fehler beim Datei-Upload");
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Fehler beim Lesen der Datei.");
        }

        File uploadFile = new File(fullFilePath);
        if (uploadFile.delete()) {
            logger.debug("Temporary file deleted");
        } else {
            logger.warn("Temporary file " + fileName + " could not be deleted");
        }
        return results;
    }
}
