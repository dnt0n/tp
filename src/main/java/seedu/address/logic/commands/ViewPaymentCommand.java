package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.payment.Payment;
import seedu.address.model.person.Person;

/**
 * Shows payments for one person by index, or for everyone if 'all' is used.
 * <p>
 * Usage:
 * viewpayment INDEX
 * viewpayment all
 */
public class ViewPaymentCommand extends Command {

    public static final String COMMAND_WORD = "viewpayment";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows recorded payments.\n"
            + "Parameters: INDEX (positive integer) OR 'all'\n"
            + "Examples: " + COMMAND_WORD + " 1    |    " + COMMAND_WORD + " all";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final Logger logger = LogsCenter.getLogger(ViewPaymentCommand.class);

    private final Index index; // null => all

    public ViewPaymentCommand(Index index) {
        this.index = index;
    }

    public static ViewPaymentCommand forIndex(Index index) {
        return new ViewPaymentCommand(index);
    }

    public static ViewPaymentCommand forAll() {
        return new ViewPaymentCommand(null);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        logger.fine(() -> "Executing viewpayment" + (index == null ? " all" : " " + index.getOneBased()));

        final List<Person> people = (index == null)
                ? model.getAddressBook().getPersonList() // includes archived too
                : model.getFilteredPersonList();

        // 'all' mode: show per-person totals and a grand total
        if (index == null) {
            String perPerson = people.stream()
                    .map(p -> {
                        BigDecimal total = p.getPayments().stream()
                                .map(pay -> pay.getAmount().asBigDecimal())
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        return String.format("- %s%s: $%s", p.isArchived() ? "[Archived] " : "",
                                p.getName(), total.toPlainString());
                    })
                    .collect(Collectors.joining("\n"));

            BigDecimal grand = people.stream()
                    .flatMap(p -> p.getPayments().stream())
                    .map(pay -> pay.getAmount().asBigDecimal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String header = String.format(
                    "Payments summary for %d people. Grand total: $%s",
                    people.size(), grand.toPlainString()
            );
            return new CommandResult(header + (perPerson.isEmpty() ? "\n(no payments)" : "\n" + perPerson));
        }

        // single person mode
        if (index.getZeroBased() >= people.size()) {
            throw new CommandException(MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person person = people.get(index.getZeroBased());
        List<Payment> sorted = Payment.inDisplayOrder(person.getPayments());

        if (sorted.isEmpty()) {
            return new CommandResult(String.format("%s has no payments recorded.", person.getName()));
        }

        BigDecimal total = sorted.stream()
                .map(p -> p.getAmount().asBigDecimal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StringBuilder body = new StringBuilder();
        for (int i = 0; i < sorted.size(); i++) {
            Payment p = sorted.get(i);
            body.append(i + 1)
                    .append(". ")
                    .append(DATE_FMT.format(p.getDate()))
                    .append(" | ")
                    .append("$").append(p.getAmount().toString());

            if (p.getRemarks() != null && !p.getRemarks().isEmpty()) {
                body.append(" | ").append(p.getRemarks());
            }
            if (i < sorted.size() - 1) {
                body.append("\n");
            }
        }

        String header = String.format(
                "Payments for %s (%d). Total: $%s",
                person.getName(), sorted.size(), total.toPlainString()
        );
        return new CommandResult(header + "\n" + body);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ViewPaymentCommand)) {
            return false;
        }
        ViewPaymentCommand o = (ViewPaymentCommand) other;
        return (this.index == null && o.index == null)
                || (this.index != null && this.index.equals(o.index));
    }

    @Override
    public int hashCode() {
        return index == null ? 0 : index.hashCode();
    }
}
