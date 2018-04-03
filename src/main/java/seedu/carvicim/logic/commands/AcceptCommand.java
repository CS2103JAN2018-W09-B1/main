package seedu.carvicim.logic.commands;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.carvicim.commons.core.EventsCenter;
import seedu.carvicim.commons.events.ui.DisplayAllJobsEvent;
import seedu.carvicim.logic.commands.exceptions.CommandException;
import seedu.carvicim.model.job.Job;
import seedu.carvicim.storage.session.ImportSession;
import seedu.carvicim.storage.session.exceptions.DataIndexOutOfBoundsException;
import seedu.carvicim.storage.session.exceptions.InvalidDataException;

//@@author yuhongherald

/**
 * Accepts an unreviewed job entry using job number and adds into servicing manager
 */
public class AcceptCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "accept";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Accepts job entry using job number. "
            + "Example: " + COMMAND_WORD + " JOB_NUMBER";

    public static final String MESSAGE_SUCCESS = "Job #%d accepted!";

    private int jobNumber;

    public AcceptCommand(int jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getMessageSuccess() {
        return String.format(MESSAGE_SUCCESS, jobNumber);
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        ImportSession importSession = ImportSession.getInstance();
        try {
            importSession.getSessionData().reviewJobEntryUsingJobNumber(jobNumber, true, "");

        } catch (DataIndexOutOfBoundsException e) {
            throw new CommandException("Excel file has bad format. Try copying the cell values into a new excel file "
                    + "before trying again");
        } catch (InvalidDataException e) {
            throw new CommandException(e.getMessage());
        }
        ObservableList<Job> jobList = FXCollections.observableList(
                ImportSession.getInstance().getSessionData().getUnreviewedJobEntries());
        if (!model.isViewingImportedJobs()) {
            model.switchJobView();
        }
        model.resetJobView();
        return new CommandResult(getMessageSuccess());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AcceptCommand); // instanceof handles nulls
    }

}
