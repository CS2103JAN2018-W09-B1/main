package seedu.address.model.job;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//@@author whenzei
/**
 * Represent the date of job creation in the servicing manager
 */
public class Date {

    public static final String MESSAGE_DATE_CONSTRAINTS = "Date should be of the format MMM D YYYY";
    public static final String DATE_VALIDATION_REGEX = "\\w\\w\\w\\s(0[1-9]|[12][0-9]|3[01])\\s(19|20)\\d\\d";

    private static final String DATE_FORMATTER_PATTERN = "MMM d yyy";

    public final String value;

    public Date() {
        value = generateDate();
    }

    public Date(String date) {
        value = date;
    }

    public Date(String date) {
        requireNonNull(date);
        checkArgument(isValidDate(date), MESSAGE_DATE_CONSTRAINTS);
        this.date = date;
    }

    /**
     * Generates the string representation of the current date on the system
     */
    private String generateDate() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER_PATTERN);
        return localDate.format(formatter);
    }

    /**
     * Returns true if a given string is a valid date
     */
    public static boolean isValidDate(String test) {
        return test.matches(DATE_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Date // instanceof handles nulls
                && this.value.equals(((Date) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
