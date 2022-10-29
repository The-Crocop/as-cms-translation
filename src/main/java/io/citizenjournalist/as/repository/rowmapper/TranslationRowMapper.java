package io.citizenjournalist.as.repository.rowmapper;

import io.citizenjournalist.as.domain.Translation;
import io.citizenjournalist.as.domain.enumeration.Language;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Translation}, with proper type conversions.
 */
@Service
public class TranslationRowMapper implements BiFunction<Row, String, Translation> {

    private final ColumnConverter converter;

    public TranslationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Translation} stored in the database.
     */
    @Override
    public Translation apply(Row row, String prefix) {
        Translation entity = new Translation();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setInputText(converter.fromRow(row, prefix + "_input_text", String.class));
        entity.setLanguage(converter.fromRow(row, prefix + "_language", Language.class));
        entity.setPersist(converter.fromRow(row, prefix + "_persist", Boolean.class));
        entity.setDetectedLanguage(converter.fromRow(row, prefix + "_detected_language", String.class));
        entity.setOutputText(converter.fromRow(row, prefix + "_output_text", String.class));
        return entity;
    }
}
