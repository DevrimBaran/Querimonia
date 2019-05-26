package de.fraunhofer.iao.querimonia.db;

import java.time.LocalDate;

/**
 * A factory class for creating response suggestions from templates and data.
 * Currently only supports dynamic dates.
 *
 * @author Simon Weiler
 */
public class ResponseSuggestionFactory {

    /**
     * Creates a new response suggestion with a custom date
     * @param complaint the complaint to respond to
     * @param template the template for the response
     * @return the filled out response suggestion
     */
    public static ResponseSuggestion createResponseSuggestionWithDate(Complaint complaint, ResponseTemplate template) {
        String dateString = extractDateAsString(complaint);
        String templateText = template.getTemplateText();
        String responseText = String.format(templateText, dateString);
        return new ResponseSuggestion(complaint, template, responseText);
    }

    /**
     * Extracts the receive date from a complaint and returns it as a readable string
     * @param complaint the complaint to extract from
     * @return the date as a readable string
     */
    private static String extractDateAsString(Complaint complaint) {
        LocalDate complaintDate = complaint.getReceiveDate();

        int complaintDay = complaintDate.getDayOfMonth();
        String complaintMonth = "";
        int complaintYear = complaintDate.getYear();

        switch (complaintDate.getMonthValue()){
            case 1:
                complaintMonth = "Januar";
                break;
            case 2:
                complaintMonth = "Februar";
                break;
            case 3:
                complaintMonth = "März";
                break;
            case 4:
                complaintMonth = "April";
                break;
            case 5:
                complaintMonth = "Mai";
                break;
            case 6:
                complaintMonth = "Juni";
                break;
            case 7:
                complaintMonth = "Juli";
                break;
            case 8:
                complaintMonth = "August";
                break;
            case 9:
                complaintMonth = "September";
                break;
            case 10:
                complaintMonth = "Oktober";
                break;
            case 11:
                complaintMonth = "November";
                break;
            case 12:
                complaintMonth = "Dezember";
                break;
        }

        return String.format("%d. %s %d", complaintDay, complaintMonth, complaintYear);
    }
}
