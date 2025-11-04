package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.payment.Payment;
import seedu.address.model.person.Person;

/**
 * Deletes one or more payments (by their display indices) from a single person.
 */
public class DeletePaymentCommand extends Command {

    public static final String COMMAND_WORD = "deletepayment";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes one or more payments from the person identified by the displayed index.\n"
            + "Parameters: PERSON_INDEX p/PAYMENT_INDEX[,PAYMENT_INDEX]...\n"
            + "Notes: PAYMENT_INDEX refers to the display order shown by 'viewpayment'. "
            + "Duplicates are not allowed. Indexes must be a positive integer.\n"
            + "Example: " + COMMAND_WORD + " 2 p/1,2,3";

    public static final String MESSAGE_SUCCESS = "Deleted payment(s) #%s from %s";
    public static final String MESSAGE_INVALID_PAYMENT_INDEX = "Invalid payment index #%d for person: %s";
    public static final String MESSAGE_NO_PAYMENTS = "This person has no payments to delete.";

    private static final Logger logger = LogsCenter.getLogger(DeletePaymentCommand.class);

    private final Index personIndex; // one person (1-based input)
    private final List<Index> paymentIndexes; // one or more payment indices (1-based input)

    /**
     * Creates a DeletePaymentCommand to delete the specified payment(s).
     */
    public DeletePaymentCommand(Index personIndex, List<Index> paymentIndexes) {
        requireNonNull(personIndex);
        requireNonNull(paymentIndexes);
        this.personIndex = personIndex;
        this.paymentIndexes = List.copyOf(paymentIndexes);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        logger.info(() -> String.format("Executing DeletePaymentCommand for person #%d with payment indexes %s",
                personIndex.getOneBased(),
                paymentIndexes.stream().map(Index::getOneBased).toList()));

        final Person target = resolveTargetPerson(model);
        logger.fine(() -> String.format("Resolved target person: %s (%s)",
                target.getName(), target.getMatriculationNumber()));

        final List<Payment> displayList = Payment.inDisplayOrder(target.getPayments());
        logger.fine(() -> String.format("Person has %d payment(s) before deletion", displayList.size()));

        if (displayList.isEmpty()) {
            logger.warning(() -> String.format("DeletePayment failed: %s has no payments.", target.getName()));
            throw new CommandException(MESSAGE_NO_PAYMENTS);
        }

        final List<Integer> zeroBased = toZeroBased(paymentIndexes);

        validatePaymentIndices(zeroBased, displayList.size(), target);

        // Resolve payments to delete
        final List<Payment> toDelete = zeroBased.stream()
                .map(displayList::get)
                .toList();

        logger.fine(() -> String.format("Deleting %d payment(s): %s",
                toDelete.size(),
                toDelete.stream().map(Payment::toString).toList()));

        // Apply deletion (by identity, order doesn't matter)
        Person updated = removePayments(target, toDelete);
        model.setPerson(target, updated);

        // Build success message
        final String joined = paymentIndexes.stream()
                .map(i -> Integer.toString(i.getOneBased()))
                .collect(java.util.stream.Collectors.joining(","));

        logger.info(() -> String.format("Deleted payment(s) #%s from %s successfully", joined, updated.getName()));

        return new CommandResult(String.format(MESSAGE_SUCCESS, joined, updated.getName()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof DeletePaymentCommand)
                && personIndex.equals(((DeletePaymentCommand) other).personIndex)
                && paymentIndexes.equals(((DeletePaymentCommand) other).paymentIndexes);
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    // ========
    // Helpers
    // ========

    /**
     * Resolves the target {@link Person} from the model's filtered list using {@code personIndex}.
     *
     * @throws CommandException if the person index is out of bounds.
     */
    private Person resolveTargetPerson(Model model) throws CommandException {
        final List<Person> shown = model.getFilteredPersonList();
        final int pZero = personIndex.getZeroBased();
        if (pZero < 0 || pZero >= shown.size()) {
            logger.warning(() -> String.format(
                    "Invalid person index %d (list size: %d)", personIndex.getOneBased(), shown.size()));
            throw new CommandException(MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return shown.get(pZero);
    }

    /**
     * Converts a list of 1-based {@link Index} to a list of zero-based ints,
     * preserving the original input order.
     */
    private static List<Integer> toZeroBased(List<Index> indices) {
        return indices.stream()
                .map(Index::getZeroBased)
                .toList();
    }

    /**
     * Validates that each zero-based payment index is within {@code [0, size)}.
     *
     * @throws CommandException if any index is invalid.
     */
    private static void validatePaymentIndices(List<Integer> zeroBased, int size, Person target)
            throws CommandException {
        for (int z : zeroBased) {
            if (z < 0 || z >= size) {
                Logger.getLogger(DeletePaymentCommand.class.getName())
                        .warning(() -> String.format("Invalid payment index %d for %s (has %d payments)",
                                z + 1, target.getName(), size));
                throw new CommandException(String.format(MESSAGE_INVALID_PAYMENT_INDEX, z + 1, target.getName()));
            }
        }
    }

    /**
     * Produces a new {@link Person} with the given payments removed (by identity).
     * Deletion order is irrelevant; duplicates are already removed upstream.
     */
    private static Person removePayments(Person original, List<Payment> toDelete) {
        Person updated = original;
        for (Payment pay : toDelete) {
            updated = updated.withRemovedPayment(pay);
        }
        return updated;
    }
}
