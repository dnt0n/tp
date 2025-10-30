package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PERSONS;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Archives one or more persons identified by their indexes in the displayed list.
 *
 * <p>Command format:
 * <pre>
 *   archive INDEX[,INDEX]...
 * </pre>
 *
 * <p>Indexes refer to the currently displayed person list.
 * Duplicate indexes are permitted in input but will be de-duplicated internally
 * while preserving the first-seen order.
 */
public class ArchiveCommand extends Command {

    public static final String COMMAND_WORD = "archive";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Archives one or more persons identified by their indexes in the displayed person list.\n"
            + "Parameters: INDEX[,INDEX]... (each must be a positive integer)\n"
            + "Example (single): " + COMMAND_WORD + " 2\n"
            + "Example (multiple): " + COMMAND_WORD + " 1,3,5";

    public static final String MESSAGE_ALREADY_ARCHIVED = "One or more selected persons are already archived: %s";

    public static final String MESSAGE_SUCCESS = "Archived: %s";

    private static final Logger logger = LogsCenter.getLogger(ArchiveCommand.class);

    private final List<Index> targetIndexes;

    /**
     * Constructs an {@code ArchiveCommand} to archive one or more persons in the displayed list.
     * Duplicate indexes are removed while preserving the original order.
     *
     * @param targetIndexes indexes (1-based in UI) of persons to archive; must not be {@code null}.
     */
    public ArchiveCommand(List<Index> targetIndexes) {
        requireNonNull(targetIndexes);
        this.targetIndexes = removeDuplicates(targetIndexes);
    }

    /**
     * Archives the selected persons and returns a {@link CommandResult} with a summary.
     *
     * <p>Behavior:
     * <ul>
     *   <li>Validates indexes against the currently displayed person list.</li>
     *   <li>Fails if any targeted person is already archived.</li>
     *   <li>Archives all valid targets atomically (same failure path for any invalid state).</li>
     *   <li>Refreshes the filtered list to show active persons after completion.</li>
     * </ul>
     *
     * @param model model providing access to persons and mutation APIs; must not be {@code null}.
     * @return result containing the names of archived persons.
     * @throws CommandException if an index is out of bounds or a person is already archived.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayedPersons = model.getFilteredPersonList();

        logger.info(String.format("[ArchiveCommand] Executing with indexes: %s",
                formatIndexes(targetIndexes)));

        List<Person> personsToArchive = validateAndCollect(displayedPersons);
        List<String> archivedNames = applyArchive(model, personsToArchive);

        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
        String result = String.format(MESSAGE_SUCCESS, String.join(", ", archivedNames));
        logger.info("[ArchiveCommand] Success: " + result);
        return new CommandResult(result);
    }

    // ----------------------------------------------------
    // Helper methods
    // ----------------------------------------------------

    /**
     * Removes duplicate indexes while preserving their original order.
     *
     * @param indexes list that may contain duplicates.
     * @return new list with duplicates removed.
     */
    private List<Index> removeDuplicates(List<Index> indexes) {
        Set<Integer> seen = new LinkedHashSet<>();
        return indexes.stream()
                .filter(i -> seen.add(i.getZeroBased()))
                .toList();
    }

    /**
     * Formats indexes as a comma-separated string of their one-based values.
     *
     * @param indexes indexes to format.
     * @return formatted string, e.g., {@code "1, 3, 5"}.
     */
    private String formatIndexes(List<Index> indexes) {
        return indexes.stream()
                .map(Index::getOneBased)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    /**
     * Validates all target indexes and collects the corresponding persons to archive.
     *
     * <p>Validation steps:
     * <ul>
     *   <li>Ensure each index is within bounds of the displayed person list.</li>
     *   <li>Detect if any targeted person is already archived; if found, fail the command.</li>
     * </ul>
     *
     * @param displayedPersons current list of persons shown to the user.
     * @return list of persons to archive.
     * @throws CommandException if any index is out of bounds or any person is already archived.
     */
    private List<Person> validateAndCollect(List<Person> displayedPersons) throws CommandException {
        List<Person> personsToArchive = new ArrayList<>(targetIndexes.size());
        List<String> alreadyArchivedNames = new ArrayList<>();

        for (Index targetIndex : targetIndexes) {
            Person person = getValidPerson(displayedPersons, targetIndex);

            if (person.isArchived()) {
                alreadyArchivedNames.add(person.getName().toString());
            }
            personsToArchive.add(person);
        }

        if (!alreadyArchivedNames.isEmpty()) {
            String names = String.join(", ", alreadyArchivedNames);
            logger.warning("[ArchiveCommand] Already archived: " + names);
            throw new CommandException(String.format(MESSAGE_ALREADY_ARCHIVED, names));
        }

        return personsToArchive;
    }

    /**
     * Resolves an index to a valid {@link Person} from the displayed list.
     *
     * @param displayedPersons list being referenced by the user's indexes.
     * @param index index to resolve.
     * @return the referenced person.
     * @throws CommandException if the index is out of bounds.
     */
    private Person getValidPerson(List<Person> displayedPersons, Index index) throws CommandException {
        int zeroBasedIndex = index.getZeroBased();
        if (zeroBasedIndex >= displayedPersons.size()) {
            logger.warning(String.format("[ArchiveCommand] Invalid index: %d (size: %d)",
                    zeroBasedIndex, displayedPersons.size()));
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return displayedPersons.get(zeroBasedIndex);
    }

    /**
     * Applies the archive flag to each person and persists updates through the model.
     *
     * @param model model used to mutate the person entries.
     * @param personsToArchive persons to archive.
     * @return ordered list of archived person names, for display.
     */
    private List<String> applyArchive(Model model, List<Person> personsToArchive) {
        List<String> archivedNames = new ArrayList<>(personsToArchive.size());
        for (Person originalPerson : personsToArchive) {
            Person archivedPerson = originalPerson.withArchived(true);
            model.setPerson(originalPerson, archivedPerson);
            archivedNames.add(archivedPerson.getName().toString());
            logger.fine("[ArchiveCommand] Archived: " + archivedPerson.getName());
        }
        return archivedNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof ArchiveCommand
                && targetIndexes.equals(((ArchiveCommand) other).targetIndexes));
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
