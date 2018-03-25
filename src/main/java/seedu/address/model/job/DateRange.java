package seedu.address.model.job;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

//@@author richardson0694
/**
 * Represents a date range in the car servicing manager
 */
public class DateRange {

    private final Date startDate;
    private final Date endDate;

    public DateRange(Date startDate, Date endDate) {
        requireAllNonNull(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() { return startDate; }

    public Date getEndDate() { return endDate; }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DateRange)) {
            return false;
        }

        DateRange otherDateRange = (DateRange) other;
        return otherDateRange.getStartDate().equals(this.getStartDate())
                && otherDateRange.getEndDate().equals(this.getEndDate());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(startDate, endDate);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Starting Date: ")
                .append(getStartDate())
                .append(" Ending Date: ")
                .append(getEndDate());
        return builder.toString();
    }

}
