package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.payment.Amount;
import seedu.address.model.payment.Payment;
import seedu.address.model.person.Person;

/**
 * Finds and displays payments of a specific person identified by index,
 * filtered by one of: amount, remark, or date.
 *
 * <p>Command format:
 * <pre>
 *     findpayment INDEX a/AMOUNT
 *     findpayment INDEX r/REMARK
 *     findpayment INDEX d/DATE
 * </pre>
 *
 * <p>Exactly one filter must be provided.
 */
public class FindPaymentCommand extends Command {

    public static final String COMMAND_WORD = "findpayment";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds payments of the person identified by the displayed index, "
            + "filtered by amount, remark, or date.\n"
            + "Parameters: INDEX a/AMOUNT | r/REMARK | d/DATE (Exactly one filter should be provided!)\n"
            + "Index should be a positive integer. Amount should be a positive number with at most 2 decimal places. "
            + "Date should be in YYYY-MM-DD format.\n"
            + "Example:\n"
            + COMMAND_WORD + " 1 r/CCA\n"
            + COMMAND_WORD + " 2 d/2023-12-30";

    public static final String MESSAGE_SUCCESS =
            "Found %d payment(s) for %s:\n%s\n\n"
                    + "Note: Payments shown here are not indexed. "
                    + "Do not use these results for 'editpayment' or 'deletepayment'.\n"
                    + "Use the 'viewpayment' command to obtain the correct payment index.";

    public static final String MESSAGE_NOT_FOUND =
            "No payments found for %s matching %s.";

    private static final Logger logger = LogsCenter.getLogger(FindPaymentCommand.class);

    private final Index targetIndex;
    private final Amount amount;
    private final String remark;
    private final LocalDate date;

    /**
     * Creates a {@code FindPaymentCommand} to search for payments of a person.
     *
     * @param targetIndex index of the person in the displayed person list.
     * @param amount      amount filter (nullable).
     * @param remark      remark filter (nullable).
     * @param date        date filter (nullable).
     */
    public FindPaymentCommand(Index targetIndex, Amount amount, String remark, LocalDate date) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
        this.amount = amount;
        this.remark = remark;
        this.date = date;
    }

    /**
     * Executes the findpayment command and returns the result message.
     *
     * @param model {@code Model} which contains the list of persons and their payments.
     * @return {@code CommandResult} containing the formatted search result.
     * @throws CommandException if the index is invalid or any internal validation fails.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person target = getTargetPerson(model);
        List<Payment> matchedPayments = findMatchingPayments(target.getPayments());

        if (matchedPayments.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_NOT_FOUND, target.getName(), describeFilter()));
        }

        logger.info(String.format("Found %d payments for %s", matchedPayments.size(), target.getName()));
        String formatted = formatPayments(matchedPayments);

        return new CommandResult(String.format(
                MESSAGE_SUCCESS, matchedPayments.size(), target.getName(), formatted));
    }

    // ----------------------------------------------------
    // Helper Methods
    // ----------------------------------------------------

    /**
     * Retrieves the {@code Person} at the given index in the filtered person list.
     *
     * @param model active data model containing the person list.
     * @return the {@code Person} corresponding to {@code targetIndex}.
     * @throws CommandException if {@code targetIndex} is out of bounds.
     */
    private Person getTargetPerson(Model model) throws CommandException {
        List<Person> persons = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= persons.size()) {
            throw new CommandException(MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return persons.get(targetIndex.getZeroBased());
    }

    /**
     * Returns a list of payments that match the selected filter (amount, remark, or date).
     *
     * @param payments list of all payments belonging to a person.
     * @return filtered list of payments.
     */
    private List<Payment> findMatchingPayments(List<Payment> payments) {
        if (amount != null) {
            return filterByAmount(payments);
        }
        if (remark != null) {
            return filterByRemark(payments);
        }
        if (date != null) {
            return filterByDate(payments);
        }
        // this should never happen as parser enforces exactly one filter
        assert false : "Parser should ensure exactly one non-null filter.";
        return List.of();
    }

    /**
     * Filters the given list of payments by amount.
     *
     * @param payments list of all payments.
     * @return payments matching the specified amount.
     */
    private List<Payment> filterByAmount(List<Payment> payments) {
        return payments.stream()
                .filter(p -> p.getAmount().equals(amount))
                .sorted(getPaymentComparator())
                .collect(Collectors.toList());
    }

    /**
     * Filters the given list of payments by remark (case-insensitive substring match).
     *
     * @param payments list of all payments.
     * @return payments whose remarks contain the given keyword.
     */
    private List<Payment> filterByRemark(List<Payment> payments) {
        String keyword = remark.toLowerCase();
        return payments.stream()
                .filter(p -> p.getRemarks() != null && p.getRemarks().toLowerCase().contains(keyword))
                .sorted(getPaymentComparator())
                .collect(Collectors.toList());
    }

    /**
     * Filters the given list of payments by exact date.
     *
     * @param payments list of all payments.
     * @return payments matching the specified date.
     */
    private List<Payment> filterByDate(List<Payment> payments) {
        return payments.stream()
                .filter(p -> p.getDate().equals(date))
                .sorted(getPaymentComparator())
                .collect(Collectors.toList());
    }

    /**
     * Returns a comparator that sorts payments by:
     * <ol>
     *     <li>Date (descending)</li>
     *     <li>Amount (descending)</li>
     *     <li>Remark (lexicographically, ascending; nulls first)</li>
     * </ol>
     *
     * @return comparator used for consistent ordering of payment results.
     */
    private Comparator<Payment> getPaymentComparator() {
        return Comparator
                .comparing(Payment::getDate, Comparator.reverseOrder())
                .thenComparing(Payment::getAmount, Comparator.reverseOrder())
                .thenComparing(p -> p.getRemarks() == null ? "" : p.getRemarks().toLowerCase());
    }

    /**
     * Formats a list of payments into a multi-line display string.
     *
     * @param payments list of payments to display.
     * @return formatted string for user output.
     */
    private String formatPayments(List<Payment> payments) {
        return payments.stream()
                .map(p -> "- " + p)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Returns a human-readable description of the active filter.
     *
     * @return description string for use in result messages.
     */
    private String describeFilter() {
        if (amount != null) {
            return "amount " + amount;
        }
        if (date != null) {
            return "date " + date;
        }
        return "remark \"" + remark + "\"";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof FindPaymentCommand)) {
            return false;
        }
        FindPaymentCommand o = (FindPaymentCommand) other;
        return targetIndex.equals(o.targetIndex)
                && Objects.equals(amount, o.amount)
                && Objects.equals(remark, o.remark)
                && Objects.equals(date, o.date);
    }

    /**
     * {@inheritDoc}
     * <p>This command is non-mutating as it only performs a read/filter operation.</p>
     */
    @Override
    public boolean isMutating() {
        return false;
    }
}
