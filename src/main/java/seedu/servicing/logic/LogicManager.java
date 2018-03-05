package seedu.servicing.logic;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.servicing.commons.core.ComponentManager;
import seedu.servicing.commons.core.LogsCenter;
import seedu.servicing.logic.commands.Command;
import seedu.servicing.logic.commands.CommandResult;
import seedu.servicing.logic.commands.exceptions.CommandException;
import seedu.servicing.logic.parser.AddressBookParser;
import seedu.servicing.logic.parser.exceptions.ParseException;
import seedu.servicing.model.Model;
import seedu.servicing.model.person.Person;

/**
 * The main LogicManager of the app.
 */
public class LogicManager extends ComponentManager implements Logic {
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final CommandHistory history;
    private final AddressBookParser addressBookParser;
    private final UndoRedoStack undoRedoStack;

    public LogicManager(Model model) {
        this.model = model;
        history = new CommandHistory();
        addressBookParser = new AddressBookParser();
        undoRedoStack = new UndoRedoStack();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        try {
            Command command = addressBookParser.parseCommand(commandText);
            command.setData(model, history, undoRedoStack);
            CommandResult result = command.execute();
            undoRedoStack.push(command);
            return result;
        } finally {
            history.add(commandText);
        }
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public ListElementPointer getHistorySnapshot() {
        return new ListElementPointer(history.getHistory());
    }
}
