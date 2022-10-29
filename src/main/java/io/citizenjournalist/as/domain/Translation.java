package io.citizenjournalist.as.domain;

import io.citizenjournalist.as.domain.enumeration.Language;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Translation.
 */
@Table("translation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Translation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("input_text")
    private String inputText;

    @NotNull(message = "must not be null")
    @Column("language")
    private Language language;

    @NotNull(message = "must not be null")
    @Column("persist")
    private Boolean persist;

    @Column("detected_language")
    private String detectedLanguage;

    @Column("output_text")
    private String outputText;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Translation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInputText() {
        return this.inputText;
    }

    public Translation inputText(String inputText) {
        this.setInputText(inputText);
        return this;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public Language getLanguage() {
        return this.language;
    }

    public Translation language(Language language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Boolean getPersist() {
        return this.persist;
    }

    public Translation persist(Boolean persist) {
        this.setPersist(persist);
        return this;
    }

    public void setPersist(Boolean persist) {
        this.persist = persist;
    }

    public String getDetectedLanguage() {
        return this.detectedLanguage;
    }

    public Translation detectedLanguage(String detectedLanguage) {
        this.setDetectedLanguage(detectedLanguage);
        return this;
    }

    public void setDetectedLanguage(String detectedLanguage) {
        this.detectedLanguage = detectedLanguage;
    }

    public String getOutputText() {
        return this.outputText;
    }

    public Translation outputText(String outputText) {
        this.setOutputText(outputText);
        return this;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Translation)) {
            return false;
        }
        return id != null && id.equals(((Translation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Translation{" +
            "id=" + getId() +
            ", inputText='" + getInputText() + "'" +
            ", language='" + getLanguage() + "'" +
            ", persist='" + getPersist() + "'" +
            ", detectedLanguage='" + getDetectedLanguage() + "'" +
            ", outputText='" + getOutputText() + "'" +
            "}";
    }
}
