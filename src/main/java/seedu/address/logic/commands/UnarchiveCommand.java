package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PERSONS;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Unarchives one or more persons identified by their indexes in the displayed list.
 *
 * <p>Command format:
 * <pre>
 *   unarchive INDEX[,INDEX]...
 * </pre>
 *
 * <p>Indexes refer to the currently displayed person list.
 * Duplicate indexes are permitted in input but will be de-duplicated internally
 * while preserving the first-seen order.
 */
public class UnarchiveCommand extends Command {

    public static final String COMMAND_WORD = "unarchive";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unarchives one or more persons identified by their indexes in the displayed person list.\n"
            + "Parameters: INDEX[,INDEX]... (each must be a positive integer)\n"
            + "Example (single): " + COMMAND_WORD + " 1\n"
            + "Example (multiple): " + COMMAND_WORD + " 1,2,5";

    public static final String MESSAGE_NOT_ARCHIVED = "One or more selected persons are not archived: %s";

    public static final String MESSAGE_SUCCESS = "Unarchived: %s\nShowing active list.";

    private final List<Index> targetIndexes;

    /**
     * Constructs an {@code UnarchiveCommand} to unarchive one or more persons in the displayed list.
     * Duplicate indexes are removed while preserving the original order.
     *
     * @param targetIndexes indexes (1-based in UI) of persons to unarchive; must not be {@code null}.
     */
    public UnarchiveCommand(List<Index> targetIndexes) {
        requireNonNull(targetIndexes);
        this.targetIndexes = removeDuplicates(targetIndexes);
    }

    /**
     * Unarchives the selected persons and returns a {@link CommandResult} with a summary.
     *
     * <p>Behavior:
     * <ul>
     *   <li>Validates indexes against the currently displayed person list.</li>
     *   <li>Fails if any targeted person is not archived.</li>
     *   <li>Unarchives all valid targets atomically (same failure path for any invalid state).</li>
     *   <li>Refreshes the filtered list to show active persons after completion.</li>
     * </ul>
     *
     * @param model model providing access to persons and mutation APIs; must not be {@code null}.
     * @return result containing the names of unarchived persons.
     * @throws CommandException if an index is out of bounds or a person is not archived.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayedPersons = model.getFilteredPersonList();

        List<Person> personsToUnarchive = validateAndCollect(displayedPersons);
        List<String> unarchivedNames = applyUnarchive(model, personsToUnarchive);

        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, String.join(", ", unarchivedNames)));
    }

    // ----------------------------------------------------
    // Helper methods
    // ----------------------------------------------------

    /**
     * Removes duplicate indexes while preserving their original order.
     *
     * @param indexes list that may contain duplicates.
     * @return unmodifiable list with duplicates removed.
     */
    private List<Index> removeDuplicates(List<Index> indexes) {
        Set<Integer> seen = new LinkedHashSet<>();
        return indexes.stream()
                .filter(i -> seen.add(i.getZeroBased()))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Validates all target indexes and collects the corresponding persons to unarchive.
     *
     * <p>Validation steps:
     * <ul>
     *   <li>Ensure each index is within bounds of the displayed person list.</li>
     *   <li>Detect if any targeted person is not archived; if found, fail the command.</li>
     * </ul>
     *
     * @param displayedPersons current list of persons shown to the user.
     * @return list of persons to unarchive.
     * @throws CommandException if any index is out of bounds or any person is not archived.
     */
    private List<Person> validateAndCollect(List<Person> displayedPersons) throws CommandException {
        List<Person> personsToUnarchive = new ArrayList<>(targetIndexes.size());
        List<String> notArchivedNames = new ArrayList<>();

        for (Index targetIndex : targetIndexes) {
            Person person = getValidPerson(displayedPersons, targetIndex);
            if (!person.isArchived()) {
                notArchivedNames.add(person.getName().toString());
            }
            personsToUnarchive.add(person);
        }

        if (!notArchivedNames.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NOT_ARCHIVED,
                    String.join(", ", notArchivedNames)));
        }

        return personsToUnarchive;
    }

    /**
     * Resolves an index to a valid {@link Person} from the displayed list.
     *
     * @param displayedPersons list being referenced by the user's indexes.
     * @param targetIndex index to resolve.
     * @return the referenced person.
     * @throws CommandException if the index is out of bounds.
     */
    private Person getValidPerson(List<Person> displayedPersons, Index targetIndex) throws CommandException {
        int zeroBasedIndex = targetIndex.getZeroBased();
        if (zeroBasedIndex >= displayedPersons.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return displayedPersons.get(zeroBasedIndex);
    }

    /**
     * Applies the unarchive flag to each person and persists updates through the model.
     *
     * @param model model used to mutate the person entries.
     * @param personsToUnarchive persons to unarchive.
     * @return ordered list of unarchived person names, for display.
     */
    private List<String> applyUnarchive(Model model, List<Person> personsToUnarchive) {
        List<String> unarchivedNames = new ArrayList<>(personsToUnarchive.size());
        for (Person originalPerson : personsToUnarchive) {
            Person unarchivedPerson = originalPerson.withArchived(false);
            model.setPerson(originalPerson, unarchivedPerson);
            unarchivedNames.add(unarchivedPerson.getName().toString());
        }
        return unarchivedNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof UnarchiveCommand
                && targetIndexes.equals(((UnarchiveCommand) other).targetIndexes));
    }

    /**
     * Indicates that this command mutates the model.
     *
     * @return always {@code true}.
     */
    @Override
    public boolean isMutating() {
        return true;
    }
}
