package io.citizenjournalist.as.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TranslationSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("input_text", table, columnPrefix + "_input_text"));
        columns.add(Column.aliased("language", table, columnPrefix + "_language"));
        columns.add(Column.aliased("persist", table, columnPrefix + "_persist"));
        columns.add(Column.aliased("detected_language", table, columnPrefix + "_detected_language"));
        columns.add(Column.aliased("output_text", table, columnPrefix + "_output_text"));

        return columns;
    }
}
