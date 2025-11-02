package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

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

/**
 * Unit tests for {@link FindPaymentCommand}.
 */
public class FindPaymentCommandTest {

    // 1. Find by amount (success)
    @Test
    public void execute_findByAmount_success() throws Exception {
        Payment p1 = new Payment(new Amount(new BigDecimal("10.00")),
                LocalDate.of(2025, 10, 10), "food");
        Payment p2 = new Payment(new Amount(new BigDecimal("20.00")),
                LocalDate.of(2025, 10, 11), "bus");
        Person bob = new PersonBuilder().withName("Bob").withPayments(p1, p2).build();

        Model model = new ModelStubWithPerson(bob);

        Amount searchAmount = new Amount(new BigDecimal("10.00"));
        FindPaymentCommand cmd = new FindPaymentCommand(Index.fromOneBased(1),
                searchAmount, null, null);

        CommandResult result = cmd.execute(model);

        String expectedList = "- " + p1;
        String expectedMsg = String.format(FindPaymentCommand.MESSAGE_SUCCESS, 1, bob.getName(), expectedList);

        assertEquals(expectedMsg, result.getFeedbackToUser());
    }

    // 2. Find by remark (case-insensitive success)
    @Test
    public void execute_findByRemark_success() throws Exception {
        Payment p1 = new Payment(new Amount(new BigDecimal("10.00")),
                LocalDate.of(2025, 10, 10), "CCA fee");
        Payment p2 = new Payment(new Amount(new BigDecimal("5.00")),
                LocalDate.of(2025, 10, 11), "donation");
        Person danton = new PersonBuilder().withName("Danton").withPayments(p1, p2).build();

        Model model = new ModelStubWithPerson(danton);

        FindPaymentCommand cmd = new FindPaymentCommand(Index.fromOneBased(1),
                null, "cca", null);

        CommandResult result = cmd.execute(model);

        String expectedList = "- " + p1;
        String expectedMsg = String.format(FindPaymentCommand.MESSAGE_SUCCESS, 1, danton.getName(), expectedList);

        assertEquals(expectedMsg, result.getFeedbackToUser());
    }

    // 3. Find by date (success)
    @Test
    public void execute_findByDate_success() throws Exception {
        LocalDate d1 = LocalDate.of(2025, 10, 9);
        Payment p1 = new Payment(new Amount(new BigDecimal("12.00")), d1, "lunch");
        Payment p2 = new Payment(new Amount(new BigDecimal("9.00")),
                LocalDate.of(2025, 10, 10), "coffee");
        Person charlie = new PersonBuilder().withName("Charlie").withPayments(p1, p2).build();

        Model model = new ModelStubWithPerson(charlie);

        FindPaymentCommand cmd = new FindPaymentCommand(Index.fromOneBased(1),
                null, null, d1);

        CommandResult result = cmd.execute(model);

        String expectedList = "- " + p1;
        String expectedMsg = String.format(FindPaymentCommand.MESSAGE_SUCCESS, 1, charlie.getName(), expectedList);

        assertEquals(expectedMsg, result.getFeedbackToUser());
    }

    // 4. No matching payments
    @Test
    public void execute_noMatch_returnsNotFoundMessage() throws Exception {
        Payment p = new Payment(new Amount(new BigDecimal("10.00")),
                LocalDate.of(2025, 10, 10), "bus");
        Person dana = new PersonBuilder().withName("Dana").withPayments(p).build();
        Model model = new ModelStubWithPerson(dana);

        FindPaymentCommand cmd = new FindPaymentCommand(Index.fromOneBased(1),
                null, "taxi", null);

        CommandResult result = cmd.execute(model);

        String expectedMsg = String.format(FindPaymentCommand.MESSAGE_NOT_FOUND, dana.getName(), "remark \"taxi\"");
        assertEquals(expectedMsg, result.getFeedbackToUser());
    }

    // 5. Invalid index
    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Person person = new PersonBuilder().withName("Foo").build();
        Model model = new ModelStubWithPerson(person);

        FindPaymentCommand cmd = new FindPaymentCommand(Index.fromOneBased(2),
                new Amount(new BigDecimal("5.00")), null, null);

        assertThrows(CommandException.class, () -> cmd.execute(model));
    }

    // positive equality test:
    @Test
    public void equals_sameValues_returnsTrue() {
        Index idx = Index.fromOneBased(1);
        Amount amt = new Amount(new BigDecimal("5.00"));
        LocalDate date = LocalDate.of(2025, 10, 10);
        String remark = "bus";

        FindPaymentCommand c1 = new FindPaymentCommand(idx, amt, remark, date);
        FindPaymentCommand c2 = new FindPaymentCommand(idx, amt, remark, date);

        assertEquals(c1, c2);
    }

    // negative equality tests:
    @Test
    public void equals_differentAmount_returnsFalse() {
        Index idx = Index.fromOneBased(1);
        FindPaymentCommand c1 = new FindPaymentCommand(idx,
                new Amount(new BigDecimal("5.00")), null, null);
        FindPaymentCommand c2 = new FindPaymentCommand(idx,
                new Amount(new BigDecimal("10.00")), null, null);
        assertEquals(false, c1.equals(c2));
    }

    @Test
    public void equals_differentRemark_returnsFalse() {
        Index idx = Index.fromOneBased(1);
        FindPaymentCommand c1 = new FindPaymentCommand(idx, null, "a", null);
        FindPaymentCommand c2 = new FindPaymentCommand(idx, null, "b", null);
        assertEquals(false, c1.equals(c2));
    }

    @Test
    public void equals_differentDate_returnsFalse() {
        Index idx = Index.fromOneBased(1);
        FindPaymentCommand c1 = new FindPaymentCommand(idx, null, null,
                LocalDate.of(2025, 10, 10));
        FindPaymentCommand c2 = new FindPaymentCommand(idx, null, null,
                LocalDate.of(2025, 10, 11));
        assertEquals(false, c1.equals(c2));
    }

    @Test
    public void equals_differentIndex_returnsFalse() {
        FindPaymentCommand c1 = new FindPaymentCommand(Index.fromOneBased(1),
                null, "a", null);
        FindPaymentCommand c2 = new FindPaymentCommand(Index.fromOneBased(2),
                null, "a", null);
        assertEquals(false, c1.equals(c2));
    }

    // ==== Model Stubs ====

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
        private final Person person;
        ModelStubWithPerson(Person person) {
            this.person = person;
        }
        @Override public ObservableList<Person> getFilteredPersonList() {
            return FXCollections.observableArrayList(person);
        }
    }
}
