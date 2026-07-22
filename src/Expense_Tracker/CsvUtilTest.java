package Expense_Tracker;

import java.util.List;

/**
 * Minimal self-check that CSV fields round-trip safely, including descriptions
 * with commas and quotes (issue #1). No test framework needed:
 *
 * <pre>
 *   javac Expense_Tracker/*.java
 *   java Expense_Tracker.CsvUtilTest
 * </pre>
 */
public class CsvUtilTest {
    private static int failures = 0;

    public static void main(String[] args) {
        roundTrip("2026-07-21", "lunch, coffee", "12.5", "Food");
        roundTrip("2026-07-21", "say \"hi\"", "3.0", "Misc");
        roundTrip("2026-07-21", "comma, and \"quote\"", "9.99", "Food, drink");
        roundTrip("2026-07-21", "plain", "1.0", "Cat");

        if (failures == 0) {
            System.out.println("All CSV round-trip checks passed.");
        } else {
            System.out.println(failures + " check(s) FAILED.");
            System.exit(1);
        }
    }

    private static void roundTrip(String... fields) {
        String line = CsvUtil.toLine(fields);
        List<String> parsed = CsvUtil.parseLine(line);
        if (parsed.size() != fields.length) {
            fail("field count for [" + line + "]", String.valueOf(fields.length),
                    String.valueOf(parsed.size()));
            return;
        }
        for (int i = 0; i < fields.length; i++) {
            if (!fields[i].equals(parsed.get(i))) {
                fail("field " + i, fields[i], parsed.get(i));
            }
        }
    }

    private static void fail(String what, String expected, String actual) {
        failures++;
        System.out.println("FAIL " + what + ": expected [" + expected + "] got [" + actual + "]");
    }
}
