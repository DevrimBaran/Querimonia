package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaints.Complaint;
import static de.fraunhofer.iao.querimonia.complaints.ComplaintFactory.createComplaint;

import de.fraunhofer.iao.querimonia.complaints.ComplaintFactory;
import de.fraunhofer.iao.querimonia.complaints.ComplaintRepository;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import de.fraunhofer.iao.querimonia.service.FileStorageService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;

/**
 * This controller manages complaint view, import and export.
 */
@RestController("/api")
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;
    @Autowired
    FileStorageService fileStorageService;

    @PostMapping("/import/file")
    public void uploadComplaint(@RequestParam("file") MultipartFile file) {
        Logger logger = LoggerFactory.getLogger(getClass());

        String fileName = fileStorageService.storeFile(file);
        String fullFilePath = "src/main/resources/uploads/" + fileName;

        ArrayList<Complaint> results = new ArrayList<>();


        try (FileReader reader = new FileReader(fullFilePath)) {
            FileInputStream fileInputStream = new FileInputStream(fullFilePath);

            results.addAll(getComplaintsFromData(reader,fullFilePath,fileInputStream));

            fileInputStream.close();
        } catch (IOException e) {
            logger.error("Fehler beim Datei-Upload");
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Fehler beim Lesen der Datei.");
        }

        results.forEach(complaintRepository::save);
    }

    /**
     * Extracts complaints out of a file.
     *
     * @param reader the fileReader that opened the file
     * @param fullFilePath the file path
     * @param fileInputStream a input stream of that file
     * @return a List of all Complaint-Objects
     * @throws IOException if it is not a supported file format
     */
    private ArrayList<Complaint> getComplaintsFromData(FileReader reader, String fullFilePath, InputStream fileInputStream) throws IOException {
        Logger logger = LoggerFactory.getLogger(getClass());

        ArrayList<Complaint> results = new ArrayList<>();
        switch (fullFilePath.substring(fullFilePath.lastIndexOf("."))) {
            case ".txt":
                BufferedReader bufferedReader = new BufferedReader(reader);
                bufferedReader.lines()
                        .map(ComplaintFactory::createComplaint)
                        .forEach(results::add);
                //read a pdf file
                break;
            case ".pdf":
                PDDocument document = PDDocument.load(new File(fullFilePath));
                if (!document.isEncrypted()) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(document);
                    results.add(createComplaint(text));
                }
                document.close();
                break;
            //read a word file (docx)
            case ".docx":
                XWPFDocument docxDocument = new XWPFDocument(fileInputStream);
                XWPFWordExtractor extractor = new XWPFWordExtractor(docxDocument);
                results.add(createComplaint(extractor.getText()));
                extractor.close();
                break;
            //read word file (doc)
            case ".doc":
                HWPFDocument docDocument = new HWPFDocument(fileInputStream);
                WordExtractor docExtractor = new WordExtractor(docDocument);
                results.add(createComplaint(docExtractor.getText()));
                docExtractor.close();
                break;
            default:
                logger.error("Not a supported file format");
                throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Not a supported file format");
        }

        return results;
    }

    @PostMapping("/import/text")
    public void uploadText(@RequestBody TextInput input) {
        complaintRepository.save(createComplaint(input.getText()));
    }
}
