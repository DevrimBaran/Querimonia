package de.fraunhofer.iao.querimonia.complaints;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ID;
    private String text;
    private String preview;
    private String sentiment;
    private LocalDate receiveDate;

    @JsonCreator
    public Complaint(@JsonProperty String text, @JsonProperty String preview, @JsonProperty String sentiment,
                     @JsonProperty LocalDate receiveDate) {
        this.text = text;
        this.preview = preview;
        this.sentiment = sentiment;
        this.receiveDate = receiveDate;
    }

    public int getID() {
        return ID;
    }

    public String getText() {
        return text;
    }

    public String getPreview() {
        return preview;
    }

    public String getSentiment() {
        return sentiment;
    }

    public LocalDate getReceiveDate() {
        return receiveDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Complaint complaint = (Complaint) o;
        return ID == complaint.ID &&
                Objects.equals(text, complaint.text) &&
                Objects.equals(preview, complaint.preview) &&
                Objects.equals(sentiment, complaint.sentiment) &&
                Objects.equals(receiveDate, complaint.receiveDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, text, preview, sentiment, receiveDate);
    }
}
