package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.job.DateRange;

//@@author richardson0694
/**
 * Archives job entries within selected date range.
 */
public class ArchiveCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "archive";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Archives job entries within selected date range. "
            + "Parameters: "
            + PREFIX_DATE + "DATE "
            + PREFIX_DATE + "DATE "
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_DATE + "Mar 01 2018 "
            + PREFIX_DATE + "Mar 25 2018 ";

    public static final String MESSAGE_SUCCESS = "Archived successfully";
    public static final String MESSAGE_DUPLICATE_FILE = "This archive already exists in the address book";

    private final DateRange toArchive;

    /**
     * Creates an ArchiveCommand to archive the job entries within the specified {@code DateRange}
     */
    public ArchiveCommand(DateRange dateRange) {
        requireNonNull(dateRange);
        toArchive = dateRange;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        try {
            model.archiveJob(toArchive);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toArchive));
        } catch (DuplicateFileException e) {
            throw new CommandException(MESSAGE_DUPLICATE_FILE);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ArchiveCommand // instanceof handles nulls
                && toArchive.equals(((ArchiveCommand) other).toArchive));
    }
}
