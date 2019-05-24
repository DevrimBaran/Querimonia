package de.fraunhofer.iao.querimonia.db;

import de.fraunhofer.iao.querimonia.db.repositories.ResponseRepository;

import java.time.LocalDate;
import java.util.Arrays;
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
        return "UNKNOWN";
    }
}
