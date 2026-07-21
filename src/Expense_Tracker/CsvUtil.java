package Expense_Tracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal RFC 4180-style CSV helpers shared by storage and export.
 *
 * A field is quoted only when it contains a comma, a double quote, or a
 * line break; embedded double quotes are escaped by doubling them. This lets
 * descriptions like {@code lunch, coffee} round-trip safely (see issue #1).
 */
public class CsvUtil {

    /** Escapes a single field for safe inclusion in a CSV line. */
    public static String escape(String field) {
        if (field == null) {
            field = "";
        }
        boolean needsQuoting = field.contains(",") || field.contains("\"")
                || field.contains("\n") || field.contains("\r");
        if (!needsQuoting) {
            return field;
        }
        return "\"" + field.replace("\"", "\"\"") + "\"";
    }

    /** Joins fields into one escaped, comma-separated CSV line. */
    public static String toLine(String... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(escape(fields[i]));
        }
        return sb.toString();
    }

    /** Parses one CSV line back into its fields, honouring quoting. */
    public static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++; // skip the escaped quote
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString());
        return fields;
    }
}
