package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.Complaint;
import de.fraunhofer.iao.querimonia.db.ComplaintFactory;
import de.fraunhofer.iao.querimonia.db.ResponseSuggestion;
import de.fraunhofer.iao.querimonia.db.ResponseTemplate;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repositories.ResponseRepository;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This controller manages complaint view, import and export.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController()
public class ComplaintController {

    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    ResponseRepository responseRepository;
    @Autowired
    TemplateRepository templateRepository;
    @Autowired
    private ComplaintRepository complaintRepository;

    @PostMapping("/api/import/file")
    public void uploadComplaint(@RequestParam("file") MultipartFile file) {
        Logger logger = LoggerFactory.getLogger(getClass());

        String fileName = fileStorageService.storeFile(file);
        String fullFilePath = "src/main/resources/uploads/" + fileName;

        Complaint complaint;

        try (FileReader reader = new FileReader(fullFilePath)) {
            FileInputStream fileInputStream = new FileInputStream(fullFilePath);

            complaint = getComplaintFromData(reader, fullFilePath, fileInputStream);
            makeResponse(complaint);

            fileInputStream.close();
        } catch (IOException e) {
            logger.error("Fehler beim Datei-Upload");
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Fehler beim Lesen der Datei.");
        }

        complaintRepository.save(complaint);
    }

    /**
     * Extracts db out of a file.
     *
     * @param reader          the fileReader that opened the file
     * @param fullFilePath    the file path
     * @param fileInputStream a input stream of that file
     * @return a List of all Complaint-Objects
     * @throws IOException if it is not a supported file format
     */
    private Complaint getComplaintFromData(FileReader reader, String fullFilePath, InputStream fileInputStream)
            throws IOException {
        Logger logger = LoggerFactory.getLogger(getClass());

        String text = null;
        switch (fullFilePath.substring(fullFilePath.lastIndexOf("."))) {
            case ".txt":
                BufferedReader bufferedReader = new BufferedReader(reader);
                text = bufferedReader.lines().collect(Collectors.joining("\n"));
                //read a pdf file
            case ".pdf":
                PDDocument document = PDDocument.load(new File(fullFilePath));
                if (!document.isEncrypted()) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    text = stripper.getText(document);
                }
                document.close();
                break;
            //read a word file (docx)
            case ".docx":
                XWPFDocument docxDocument = new XWPFDocument(fileInputStream);
                XWPFWordExtractor extractor = new XWPFWordExtractor(docxDocument);
                text = extractor.getText();
                extractor.close();
                break;
            //read word file (doc)
            case ".doc":
                HWPFDocument docDocument = new HWPFDocument(fileInputStream);
                WordExtractor docExtractor = new WordExtractor(docDocument);
                text = docExtractor.getText();
                docExtractor.close();
                break;
            default:
                logger.error("Not a supported file format");
                throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Not a supported file format");
        }

        return ComplaintFactory.createComplaint(text);
    }

    private void makeResponse(Complaint complaint) {

        // save response TODO: create proper responses
        ResponseTemplate sampleTemplate = new ResponseTemplate("Danke für ihre Nachricht",
                complaint.getSubject(),
                "COMPLETE_TEXT");

        ResponseSuggestion suggestion = new ResponseSuggestion(complaint, sampleTemplate,
                sampleTemplate.getTemplateText());

        templateRepository.save(sampleTemplate);
        responseRepository.save(suggestion);
    }

    @PostMapping("/api/import/text")
    public Complaint uploadText(@RequestBody TextInput input) {
        Complaint complaint = ComplaintFactory.createComplaint(input.getText());
        complaintRepository.save(complaint);
        return complaint;
    }

    @GetMapping("/api/complaints")
    public List<Complaint> getTexts() {
        ArrayList<Complaint> result = new ArrayList<>();
        complaintRepository.findAll().forEach(result::add);

        return result;
    }

    @GetMapping("/api/complaints/{ID}")
    public Complaint getComplaint(@PathVariable int ID) {
        return complaintRepository.findById(ID).orElse(null);
    }

    @DeleteMapping("/api/complaints/{ID}")
    public void deleteComplaint(@PathVariable int ID) {
        complaintRepository.deleteById(ID);
    }
}
