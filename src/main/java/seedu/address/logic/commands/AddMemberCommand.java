package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATIONNUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddMemberCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the active member list. "
        + "Parameters: "
        + PREFIX_NAME + "NAME "
        + PREFIX_PHONE + "PHONE "
        + PREFIX_EMAIL + "EMAIL "
        + PREFIX_MATRICULATIONNUMBER + "MATRICULATION NUMBER "
        + "[" + PREFIX_TAG + "TAG]...\n"
        + "Example: " + COMMAND_WORD + " "
        + PREFIX_NAME + "John Doe "
        + PREFIX_PHONE + "98765432 "
        + PREFIX_EMAIL + "johnd@example.com "
        + PREFIX_MATRICULATIONNUMBER + "A1234567X "
        + PREFIX_TAG + "friends "
        + PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s\nShowing active member list";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in member list. "
            + "A person is uniquely identified by his/her matriculation number.";

    private static final Logger logger = LogsCenter.getLogger(AddMemberCommand.class);

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddMemberCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        // Log user intent
        logger.info(String.format("User triggered 'add' command for person: %s", toAdd.getName()));

        // Check for duplicates
        if (model.hasPerson(toAdd)) {
            logger.warning(String.format(
                "Duplicate detected — person with matriculation number %s already exists.",
                toAdd.getMatriculationNumber()));
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        // Proceed to add
        model.addPerson(toAdd);
        logger.info(String.format(
            "Successfully added new person: Name=%s, MatricNo=%s, Email=%s, Phone=%s, Tags=%s",
            toAdd.getName(),
            toAdd.getMatriculationNumber(),
            toAdd.getEmail(),
            toAdd.getPhone(),
            toAdd.getTags()));

        // Return command result
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddMemberCommand)) {
            return false;
        }

        AddMemberCommand otherAddMemberCommand = (AddMemberCommand) other;
        return toAdd.equals(otherAddMemberCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .add("toAdd", toAdd)
            .toString();
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
