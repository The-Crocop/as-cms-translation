package io.citizenjournalist.as.service.dto;

import io.citizenjournalist.as.domain.enumeration.Language;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.citizenjournalist.as.domain.Translation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TranslationDTO implements Serializable {

    private Long id;

    @Lob
    private String inputText;

    @NotNull(message = "must not be null")
    private Language language;

    @NotNull(message = "must not be null")
    private Boolean persist;

    private String detectedLanguage;

    @Lob
    private String outputText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Boolean getPersist() {
        return persist;
    }

    public void setPersist(Boolean persist) {
        this.persist = persist;
    }

    public String getDetectedLanguage() {
        return detectedLanguage;
    }

    public void setDetectedLanguage(String detectedLanguage) {
        this.detectedLanguage = detectedLanguage;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TranslationDTO)) {
            return false;
        }

        TranslationDTO translationDTO = (TranslationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, translationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TranslationDTO{" +
            "id=" + getId() +
            ", inputText='" + getInputText() + "'" +
            ", language='" + getLanguage() + "'" +
            ", persist='" + getPersist() + "'" +
            ", detectedLanguage='" + getDetectedLanguage() + "'" +
            ", outputText='" + getOutputText() + "'" +
            "}";
    }
}
