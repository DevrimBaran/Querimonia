package de.fraunhofer.iao.querimonia.db;

import de.fraunhofer.iao.querimonia.db.repositories.ResponseRepository;
import de.fraunhofer.iao.querimonia.ner.KIKuKoClassifier;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.TempPipeline;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.Typ;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ComplaintFactory {

    public static Complaint createComplaint(String complaintText) {
        String preview = makePreview(complaintText);
        String subject = getSubject(complaintText);

        return new Complaint(complaintText, preview, "NORMAL", subject, LocalDate.now());
    }

    private static String makePreview(String text) {
        return Arrays.stream(text.split("\n"))
                .filter(line -> !line.trim().isEmpty())
                .limit(2)
                .collect(Collectors.joining("\n"));
    }

    private static String getSubject(String text) {
        HashMap<String, Double> typeMap = new HashMap<>();
        Typ typ = new KIKuKoClassifier().executeKikukoRequest(text)
                .getPipelines()
                .getTempPipeline()
                .get(0)
                .getTyp();

        typeMap.put("Fahrt nicht erfolgt", typ.getFahrtNichtErfolgt());
        typeMap.put("Fahrer unfreundlich", typ.getFahrerUnfreundlich());
        typeMap.put("Sonstiges", (double) typ.getSonstiges());

        return typeMap.entrySet()
                .stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("UNKNOWN");
    }
}
