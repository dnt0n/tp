package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.payment.Amount;
import seedu.address.model.payment.Payment;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Unit tests for DeletePaymentCommand.
 */
public class DeletePaymentCommandTest {

    @Test
    public void execute_validIndex_success() throws Exception {
        Person alice = new PersonBuilder().withName("Alice").build();
        Payment payment = new Payment(new Amount(new BigDecimal("50.00")), LocalDate.of(2025, 1, 1));
        alice = alice.withAddedPayment(payment);

        Model model = new ModelStubWithPerson(alice);
        DeletePaymentCommand command = new DeletePaymentCommand(
                Index.fromOneBased(1), List.of(Index.fromOneBased(1)));

        CommandResult result = command.execute(model);

        assertEquals(String.format(DeletePaymentCommand.MESSAGE_SUCCESS, "1", "Alice"),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_invalidPaymentIndex_throwsCommandException() {
        Person bob = new PersonBuilder().withName("Bob").build(); // no payments
        Model model = new ModelStubWithPerson(bob);

        DeletePaymentCommand command = new DeletePaymentCommand(
                Index.fromOneBased(1), List.of(Index.fromOneBased(1)));

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_emptyFilteredList_throwsCommandException() {
        Model model = new ModelStub() {
            @Override public ObservableList<Person> getFilteredPersonList() {
                return FXCollections.observableArrayList(); // empty
            }
        };

        DeletePaymentCommand cmd = new DeletePaymentCommand(Index.fromOneBased(1),
                List.of(Index.fromOneBased(1)));

        assertThrows(CommandException.class, () -> cmd.execute(model));
    }

    @Test
    public void execute_personIndexOutOfBounds_throwsCommandException() {
        Person p = new PersonBuilder().withName("X").build();
        Model model = new ModelStub() {
            @Override public ObservableList<Person> getFilteredPersonList() {
                return FXCollections.observableArrayList(p); // size=1
            }
        };

        DeletePaymentCommand cmd = new DeletePaymentCommand(Index.fromOneBased(2),
                List.of(Index.fromOneBased(1)));

        assertThrows(CommandException.class, () -> cmd.execute(model));
    }

    @Test
    public void isMutating_returnsTrue() {
        DeletePaymentCommand cmd = new DeletePaymentCommand(Index.fromOneBased(1),
                List.of(Index.fromOneBased(1)));
        assertTrue(cmd.isMutating());
    }

    @Test
    public void equals() {
        DeletePaymentCommand c1 = new DeletePaymentCommand(
                Index.fromOneBased(1), List.of(Index.fromOneBased(1)));
        DeletePaymentCommand c2 = new DeletePaymentCommand(
                Index.fromOneBased(1), List.of(Index.fromOneBased(1)));
        DeletePaymentCommand c3 = new DeletePaymentCommand(
                Index.fromOneBased(2), List.of(Index.fromOneBased(1)));

        assertEquals(c1, c2);
        assertEquals(false, c1.equals(c3));
    }

    // ===== Model stubs =====

    private static class ModelStub implements Model {
        @Override public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError();
        }

        @Override public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError();
        }

        @Override public GuiSettings getGuiSettings() {
            throw new AssertionError();
        }

        @Override public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError();
        }

        @Override public java.nio.file.Path getAddressBookFilePath() {
            throw new AssertionError();
        }

        @Override public void setAddressBookFilePath(java.nio.file.Path path) {
            throw new AssertionError();
        }

        @Override public void setAddressBook(ReadOnlyAddressBook ab) {
            throw new AssertionError();
        }

        @Override public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError();
        }

        @Override public boolean hasPerson(Person person) {
            throw new AssertionError();
        }

        @Override public void addPerson(Person person) {
            throw new AssertionError();
        }

        @Override public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError();
        }

        @Override public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError();
        }

        @Override public void updateFilteredPersonList(java.util.function.Predicate<Person> predicate) {
            throw new AssertionError();
        }

        @Override public boolean canUndo() {
            throw new AssertionError();
        }

        @Override public void saveSnapshot() {
            throw new AssertionError();
        }

        @Override public void undo() {
            throw new AssertionError();
        }

        @Override public void clearRedo() {
            throw new AssertionError();
        }

        @Override public boolean canRedo() {
            throw new AssertionError();
        }

        @Override public void redo() {
            throw new AssertionError();
        }

        @Override
        public void pushUndoSnapshot(ReadOnlyAddressBook snapshot) {
            throw new AssertionError();
        }

    }

    private static class ModelStubWithPerson extends ModelStub {
        private Person person;

        ModelStubWithPerson(Person person) {
            this.person = person;
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return FXCollections.observableArrayList(person);
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            this.person = editedPerson;
        }
    }
}
