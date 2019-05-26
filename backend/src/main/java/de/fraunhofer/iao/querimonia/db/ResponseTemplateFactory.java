package de.fraunhofer.iao.querimonia.db;

/**
 * A factory class for creating new complaints from an input string
 * @author Simon Weiler
 */
public class ResponseTemplateFactory {

    /**
     * Create a new template with the given parameters
     * @param templateText The actual text of the template
     * @param subject The subject/category of the template
     * @param responsePart The role/position of this template in a response
     * @return the created template
     */
    public static ResponseTemplate createTemplate(String templateText, String subject, String responsePart) {
        return new ResponseTemplate(templateText, subject, responsePart);
    }
}
