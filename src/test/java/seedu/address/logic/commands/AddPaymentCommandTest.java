package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.payment.Amount;
import seedu.address.model.payment.Payment;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

// Test scheme:
// Assuming the model stub with person is correctly created:
// 1. add payment to single person successfully without remarks
// 2. add payment to single person successfully with remarks
// 3. add payment to multiple persons successfully without remarks
// 4. add payment to multiple persons successfully with remarks
// 5. add payment to multiple persons with invalid index(es)
// 6. add payment to single person with missing date
// 7. add payment to single person with missing amount
// 8. add payment to single person with incorrect date
// 9. add payment to single person with incorrect amount
// 10. add payment when the list is empty

/**
 * Unit tests for AddPaymentCommand.
 */
public class AddPaymentCommandTest {

    // 1. Single person, no remarks
    @Test
    public void execute_singlePersonNoRemarks_success() throws Exception {
        Person bob = new PersonBuilder().withName("Bob").build();
        Model model = new ModelStubWithPerson(bob);

        Amount amount = new Amount(new BigDecimal("66.66"));
        LocalDate date = LocalDate.of(2025, 10, 10);

        AddPaymentCommand command = new AddPaymentCommand(
                List.of(Index.fromOneBased(1)), amount, date, null);

        CommandResult result = command.execute(model);

        Payment expectedPayment = new Payment(amount, date, null);
        String expectedMessage = String.format(AddPaymentCommand.MESSAGE_SUCCESS_TEMPLATE, expectedPayment, "Bob");

        assertEquals(expectedMessage, result.getFeedbackToUser());
    }

    // 2. Single person, with remarks
    @Test
    public void execute_singlePersonWithRemarks_success() throws Exception {
        Person alice = new PersonBuilder().withName("Alice").build();
        Model model = new ModelStubWithPerson(alice);

        Amount amount = new Amount(new BigDecimal("23.50"));
        LocalDate date = LocalDate.of(2025, 10, 9);
        String remarks = "taxi home";

        AddPaymentCommand command = new AddPaymentCommand(
                List.of(Index.fromOneBased(1)), amount, date, remarks);

        CommandResult result = command.execute(model);

        Payment expectedPayment = new Payment(amount, date, remarks);
        String expectedMessage = String.format(AddPaymentCommand.MESSAGE_SUCCESS_TEMPLATE, expectedPayment, "Alice");

        assertEquals(expectedMessage, result.getFeedbackToUser());
    }

    // 3. Multiple persons, no remarks
    @Test
    public void execute_multiplePersonsNoRemarks_success() throws Exception {
        Person charlie = new PersonBuilder().withName("Charlie").build();
        Person dana = new PersonBuilder().withName("Dana").build();
        Model model = new ModelStubWithMultiplePersons(List.of(charlie, dana));

        Amount amount = new Amount(new BigDecimal("10.00"));
        LocalDate date = LocalDate.of(2025, 10, 9);

        AddPaymentCommand command = new AddPaymentCommand(
                List.of(Index.fromOneBased(1),
                        Index.fromOneBased(2)), amount, date, null);

        CommandResult result = command.execute(model);

        Payment expectedPayment = new Payment(amount, date, null);
        String expectedMessage = String.format(AddPaymentCommand.MESSAGE_SUCCESS_TEMPLATE,
                expectedPayment, "Charlie, Dana");

        assertEquals(expectedMessage, result.getFeedbackToUser());
    }

    // 4. Multiple persons, with remarks
    @Test
    public void execute_multiplePersonsWithRemarks_success() throws Exception {
        Person ethan = new PersonBuilder().withName("Ethan").build();
        Person danton = new PersonBuilder().withName("Danton").build();
        Model model = new ModelStubWithMultiplePersons(List.of(ethan, danton));

        Amount amount = new Amount(new BigDecimal("12.75"));
        LocalDate date = LocalDate.of(2025, 10, 9);
        String remarks = "grab to hawker center";

        AddPaymentCommand command = new AddPaymentCommand(
                List.of(Index.fromOneBased(1), Index.fromOneBased(2)), amount, date, remarks);

        CommandResult result = command.execute(model);

        Payment expectedPayment = new Payment(amount, date, remarks);
        String expectedMessage = String.format(AddPaymentCommand.MESSAGE_SUCCESS_TEMPLATE,
                expectedPayment, "Ethan, Danton");

        assertEquals(expectedMessage, result.getFeedbackToUser());
    }

    // 5. Multiple persons, invalid index
    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Person ian = new PersonBuilder().withName("Ian").build();
        Model model = new ModelStubWithPerson(ian);

        Amount amount = new Amount(new BigDecimal("20.00"));
        LocalDate date = LocalDate.of(2025, 10, 9);

        AddPaymentCommand command = new AddPaymentCommand(
                List.of(Index.fromOneBased(2)), amount, date, null);

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    // 6. Missing date (null)
    @Test
    public void constructor_nullDate_throwsNullPointerException() {
        List<Index> indexes = List.of(Index.fromOneBased(1));
        Amount amount = new Amount(new BigDecimal("15.00"));

        assertThrows(NullPointerException.class, () -> new AddPaymentCommand(indexes, amount, null, null));
    }

    // 7. Missing amount (null)
    @Test
    public void constructor_nullAmount_throwsNullPointerException() {
        List<Index> indexes = List.of(Index.fromOneBased(1));

        assertThrows(NullPointerException.class, () -> new AddPaymentCommand(indexes, null,
                        LocalDate.of(2025, 10, 9), null));
    }

    // 8. Incorrect date format
    @Test
    public void execute_invalidDate_throwsCommandException() {
        Person john = new PersonBuilder().withName("John").build();
        Model model = new ModelStubWithPerson(john);

        Amount amount = new Amount(new BigDecimal("10.00"));
        AddPaymentCommand command = new AddPaymentCommand(
                List.of(Index.fromOneBased(2)), amount,
                LocalDate.of(2025, 10, 9), null);

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    // 9. Incorrect amount (negative)
    @Test
    public void constructor_invalidAmount_throwsIllegalArgumentException() {
        List<Index> indexes = List.of(Index.fromOneBased(1));

        assertThrows(IllegalArgumentException.class, () -> new Amount(new BigDecimal("-5.00")));
    }

    // 10. Empty model (no persons)
    @Test
    public void execute_emptyModel_throwsCommandException() {
        Model model = new EmptyModelStub();

        Amount amount = new Amount(new BigDecimal("10.00"));
        LocalDate date = LocalDate.of(2025, 10, 9);

        AddPaymentCommand command = new AddPaymentCommand(
                List.of(Index.fromOneBased(1)), amount, date, null);

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    // 11. equals() method tests
    @Test
    public void equals_sameValues_returnsTrue() {
        List<Index> indexes = List.of(Index.fromOneBased(1));
        Amount amount = new Amount(new BigDecimal("10.00"));
        LocalDate date = LocalDate.of(2025, 10, 10);

        AddPaymentCommand command1 = new AddPaymentCommand(indexes, amount, date, "remark");
        AddPaymentCommand command2 = new AddPaymentCommand(indexes, amount, date, "remark");

        assertEquals(true, command1.equals(command2));
    }

    @Test
    public void equals_differentIndexes_returnsFalse() {
        AddPaymentCommand cmd1 = new AddPaymentCommand(
                List.of(Index.fromOneBased(1)),
                new Amount(new BigDecimal("10.00")),
                LocalDate.of(2025, 10, 10),
                null);
        AddPaymentCommand cmd2 = new AddPaymentCommand(
                List.of(Index.fromOneBased(2)),
                new Amount(new BigDecimal("10.00")),
                LocalDate.of(2025, 10, 10),
                null);
        assertEquals(false, cmd1.equals(cmd2));
    }

    @Test
    public void equals_differentAmount_returnsFalse() {
        AddPaymentCommand cmd1 = new AddPaymentCommand(
                List.of(Index.fromOneBased(1)),
                new Amount(new BigDecimal("10.00")),
                LocalDate.of(2025, 10, 10),
                null);
        AddPaymentCommand cmd2 = new AddPaymentCommand(
                List.of(Index.fromOneBased(1)),
                new Amount(new BigDecimal("20.00")),
                LocalDate.of(2025, 10, 10),
                null);
        assertEquals(false, cmd1.equals(cmd2));
    }

    @Test
    public void equals_differentDate_returnsFalse() {
        AddPaymentCommand cmd1 = new AddPaymentCommand(
                List.of(Index.fromOneBased(1)),
                new Amount(new BigDecimal("10.00")),
                LocalDate.of(2025, 10, 10),
                null);
        AddPaymentCommand cmd2 = new AddPaymentCommand(
                List.of(Index.fromOneBased(1)),
                new Amount(new BigDecimal("10.00")),
                LocalDate.of(2026, 10, 10),
                null);
        assertEquals(false, cmd1.equals(cmd2));
    }

    @Test
    public void equals_differentRemarks_returnsFalse() {
        AddPaymentCommand cmd1 = new AddPaymentCommand(
                List.of(Index.fromOneBased(1)),
                new Amount(new BigDecimal("10.00")),
                LocalDate.of(2025, 10, 10),
                "A");
        AddPaymentCommand cmd2 = new AddPaymentCommand(
                List.of(Index.fromOneBased(1)),
                new Amount(new BigDecimal("10.00")),
                LocalDate.of(2025, 10, 10),
                "B");
        assertEquals(false, cmd1.equals(cmd2));
    }

    // Model Stubs
    private static class ModelStub implements Model {
        @Override public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError();
        }
        @Override public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError();
        }
        @Override public void addPerson(Person person) {
            throw new AssertionError();
        }
        @Override public boolean hasPerson(Person person) {
            throw new AssertionError();
        }
        @Override public void updateFilteredPersonList(java.util.function.Predicate<Person> predicate) {
            throw new AssertionError();
        }
        @Override public void setUserPrefs(seedu.address.model.ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError();
        }
        @Override public seedu.address.model.ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError();
        }
        @Override public void setGuiSettings(seedu.address.commons.core.GuiSettings guiSettings) {
            throw new AssertionError();
        }
        @Override public seedu.address.commons.core.GuiSettings getGuiSettings() {
            throw new AssertionError();
        }
        @Override public void setAddressBookFilePath(java.nio.file.Path addressBookFilePath) {
            throw new AssertionError();
        }
        @Override public java.nio.file.Path getAddressBookFilePath() {
            throw new AssertionError();
        }
        @Override public void setAddressBook(seedu.address.model.ReadOnlyAddressBook newData) {
            throw new AssertionError();
        }
        @Override public seedu.address.model.ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError();
        }
        @Override
        public boolean canUndo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void saveSnapshot() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void undo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void clearRedo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canRedo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void redo() {
            throw new AssertionError("This method should not be called.");
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
        @Override public ObservableList<Person> getFilteredPersonList() {
            return FXCollections.observableArrayList(person);
        }
        @Override public void setPerson(Person target, Person editedPerson) {
            this.person = editedPerson;
        }
    }

    private static class ModelStubWithMultiplePersons extends ModelStub {
        private final List<Person> persons;
        ModelStubWithMultiplePersons(List<Person> persons) {
            this.persons = new ArrayList<>(persons);
        }
        @Override public ObservableList<Person> getFilteredPersonList() {
            return FXCollections.observableArrayList(persons);
        }
        @Override public void setPerson(Person target, Person editedPerson) {
            int index = persons.indexOf(target);
            persons.set(index, editedPerson);
        }
    }

    private static class EmptyModelStub extends ModelStub {
        @Override public ObservableList<Person> getFilteredPersonList() {
            return FXCollections.observableArrayList(); // empty list
        }
    }
}
