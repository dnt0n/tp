package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_PAYMENT_AMOUNT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PAYMENT_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PAYMENT_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PAYMENT_REMARKS;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditPaymentCommand;
import seedu.address.logic.commands.EditPaymentCommand.EditPaymentDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.payment.Amount;

/**
 * Parses input arguments and creates a new {@code EditPaymentCommand} object.
 */
public class EditPaymentCommandParser implements Parser<EditPaymentCommand> {

    public static final String MESSAGE_INVALID_DATE =
        "Invalid date. Please use the strict format YYYY-MM-DD (e.g., 2025-01-01) and ensure it is not in the future.";

    @Override
    public EditPaymentCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(
            args, PREFIX_PAYMENT_INDEX, PREFIX_PAYMENT_AMOUNT, PREFIX_PAYMENT_DATE, PREFIX_PAYMENT_REMARKS);

        // Reject multiple of the same prefix
        map.verifyNoDuplicatePrefixesFor(PREFIX_PAYMENT_INDEX, PREFIX_PAYMENT_AMOUNT,
            PREFIX_PAYMENT_DATE, PREFIX_PAYMENT_REMARKS);

        // Check for unknown prefixes
        checkForUnknownPrefixes(args);

        Index personIndex;
        try {
            personIndex = ParserUtil.parseIndex(map.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(EditPaymentCommand.MESSAGE_USAGE, pe);
        }

        int paymentOneBased = parseRequiredPaymentIndex(map);
        EditPaymentDescriptor d = new EditPaymentDescriptor();

        // Optional fields
        Optional<String> amtOpt = map.getValue(PREFIX_PAYMENT_AMOUNT);
        if (amtOpt.isPresent()) {
            d.setAmount(parseAmount(amtOpt.get()));
        }

        Optional<String> dateOpt = map.getValue(PREFIX_PAYMENT_DATE);
        if (dateOpt.isPresent()) {
            d.setDate(parseDate(dateOpt.get())); // strict date parsing
        }

        map.getValue(PREFIX_PAYMENT_REMARKS).ifPresent(d::setRemarks);

        if (!d.isAnyFieldEdited()) {
            throw new ParseException(EditPaymentCommand.MESSAGE_NO_FIELDS);
        }

        return new EditPaymentCommand(personIndex, paymentOneBased, d);
    }

    private static int parseRequiredPaymentIndex(ArgumentMultimap map) throws ParseException {
        String raw = map.getValue(PREFIX_PAYMENT_INDEX)
            .orElseThrow(() -> new ParseException("Missing required prefix p/ for payment index"));
        try {
            int oneBased = Integer.parseInt(raw.trim());
            if (oneBased <= 0) {
                throw new NumberFormatException();
            }
            return oneBased;
        } catch (NumberFormatException e) {
            throw new ParseException("Payment index after p/ must be a positive integer (e.g. p/1)");
        }
    }

    private static Amount parseAmount(String s) throws ParseException {
        try {
            return Amount.parse(s.trim());
        } catch (IllegalArgumentException ex) {
            throw new ParseException("Invalid amount: must be positive, ≤ 2 decimal places, and at most 1 million.");
        }
    }

    /**
     * ✅ Parses dates strictly in YYYY-MM-DD format and disallows future dates.
     */
    private static LocalDate parseDate(String s) throws ParseException {
        try {
            return seedu.address.model.payment.Payment.parseStrictDate(s.trim());
        } catch (DateTimeParseException | IllegalArgumentException ex) {
            throw new ParseException(MESSAGE_INVALID_DATE);
        }
    }

    /**
     * ✅ Detects any prefixes that are not recognized and throws a ParseException.
     */
    private static void checkForUnknownPrefixes(String args) throws ParseException {
        // All valid prefixes for this command
        String[] validPrefixes = {"p/", "a/", "d/", "r/"};

        // Regex to find all potential prefixes like x/
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\b([a-zA-Z])/").matcher(args);

        while (matcher.find()) {
            String prefix = matcher.group(1) + "/";
            boolean isKnown = java.util.Arrays.stream(validPrefixes).anyMatch(prefix::equals);
            if (!isKnown) {
                throw new ParseException("Unknown parameter: " + prefix);
            }
        }
    }
}
