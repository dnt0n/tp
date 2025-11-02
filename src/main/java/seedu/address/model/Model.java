package seedu.address.model;

import java.nio.file.Path;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /**
     * {@code Predicate} that always evaluate to true
     */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;
    Predicate<Person> PREDICATE_SHOW_ACTIVE_PERSONS = person -> !person.isArchived();
    Predicate<Person> PREDICATE_SHOW_ARCHIVED_PERSONS = Person::isArchived;


    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setAddressBookFilePath(Path addressBookFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /**
     * Returns the AddressBook
     */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the
     * address book.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Returns an unmodifiable view of the filtered person list
     */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Returns true if there is at least one previous state available
     * in the undo history.
     *
     * @return true if undo can be performed, false otherwise.
     */
    boolean canUndo();

    /**
     * Saves the current state of the AddressBook into the undo history.
     * This should be called before executing a mutating command so that
     * the previous state can be restored if needed.
     */
    void saveSnapshot();

    /**
     * Stores the given address book state in the undo history.
     * Called before a mutating command is executed, so that the user can undo it later.
     */
    void pushUndoSnapshot(ReadOnlyAddressBook snapshot);

    /**
     * Restores the AddressBook to its previous state.
     *
     * @throws IllegalStateException if there are no states available to undo.
     */
    void undo();

    /**
     * Clears the redo history.
     * This should be called whenever a new command that changes the state
     * is executed, to maintain consistency of the undo/redo stacks.
     */
    void clearRedo();

    /**
     * Returns true if there is at least one future state available
     * in the redo history.
     *
     * @return true if redo can be performed, false otherwise.
     */
    boolean canRedo();

    /**
     * Reapplies the most recently undone change to restore the AddressBook
     * to its state before the last undo operation.
     *
     * @throws IllegalStateException if there are no states available to redo.
     */
    void redo();

}
