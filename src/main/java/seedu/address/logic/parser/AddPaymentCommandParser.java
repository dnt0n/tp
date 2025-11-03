package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PAYMENT_AMOUNT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PAYMENT_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PAYMENT_REMARKS;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddPaymentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.payment.Amount;

/**
 * Parses input arguments and creates a new {@code AddPaymentCommand} object.
 * <p>
 * Enforces strict date format (YYYY-MM-DD), ensures no duplicate indexes or prefixes,
 * and rejects invalid or future dates.
 */
public class AddPaymentCommandParser implements Parser<AddPaymentCommand> {

    public static final String MESSAGE_INVALID_AMOUNT =
        "Invalid amount (must be positive, up to 2 decimal places, and at most 1 million).";
    public static final String MESSAGE_INVALID_DATE =
        "Invalid date. Please use the strict format YYYY-MM-DD (e.g., 2025-01-01) and ensure it is not in the future.";
    public static final String MESSAGE_INVALID_REMARKS = "Remarks must be at most 100 characters long";

    @Override
    public AddPaymentCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(
            args, PREFIX_PAYMENT_AMOUNT, PREFIX_PAYMENT_DATE, PREFIX_PAYMENT_REMARKS);

        // Step 1: Validate prefix usage
        validatePrefixes(map);

        // Step 2: Parse indexes from preamble
        List<Index> indexes = parseIndexes(map.getPreamble());

        // Step 3: Parse values (amount, date, remarks)
        Amount amount = parseAmount(map.getValue(PREFIX_PAYMENT_AMOUNT).get());
        LocalDate date = parseDate(map.getValue(PREFIX_PAYMENT_DATE).get());
        String remarks = map.getValue(PREFIX_PAYMENT_REMARKS).orElse(null);
        validateRemarks(remarks);

        // Step 4: Build and return command
        return new AddPaymentCommand(indexes, amount, date, remarks);
    }

    // ----------------------------------------------------
    // Helper methods
    // ----------------------------------------------------

    private void validatePrefixes(ArgumentMultimap map) throws ParseException {
        map.verifyNoDuplicatePrefixesFor(
            PREFIX_PAYMENT_AMOUNT, PREFIX_PAYMENT_DATE, PREFIX_PAYMENT_REMARKS);

        if (map.getPreamble().isBlank()
            || !arePrefixesPresent(map, PREFIX_PAYMENT_AMOUNT, PREFIX_PAYMENT_DATE)) {
            throw new ParseException(String.format(
                MESSAGE_INVALID_COMMAND_FORMAT, AddPaymentCommand.MESSAGE_USAGE));
        }
    }

    private List<Index> parseIndexes(String preamble) throws ParseException {
        String[] indexTokens = preamble.split(",");
        List<Index> indexes = new ArrayList<>();

        for (String token : indexTokens) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty()) {
                indexes.add(ParserUtil.parseIndex(trimmed));
            }
        }

        if (indexes.isEmpty()) {
            throw new ParseException(String.format(
                MESSAGE_INVALID_COMMAND_FORMAT, AddPaymentCommand.MESSAGE_USAGE));
        }

        long uniqueCount = indexes.stream()
            .map(Index::getZeroBased)
            .distinct()
            .count();

        if (indexes.size() != uniqueCount) {
            throw new ParseException("Duplicate indexes detected. Each index must be unique.");
        }

        return indexes;
    }

    private Amount parseAmount(String amountStr) throws ParseException {
        try {
            return Amount.parse(amountStr);
        } catch (IllegalArgumentException ex) {
            throw new ParseException(MESSAGE_INVALID_AMOUNT, ex);
        }
    }

    private void validateRemarks(String remarkStr) throws ParseException {
        if (remarkStr != null && remarkStr.length() >= 100) {
            throw new ParseException(MESSAGE_INVALID_REMARKS);
        }
    }

    /**
     * Parses a valid LocalDate strictly in YYYY-MM-DD format.
     *
     * @throws ParseException if the date format is invalid or in the future.
     */
    private LocalDate parseDate(String dateStr) throws ParseException {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            if (date.isAfter(LocalDate.now())) {
                throw new ParseException(MESSAGE_INVALID_DATE);
            }
            return date;
        } catch (DateTimeParseException ex) {
            throw new ParseException(MESSAGE_INVALID_DATE, ex);
        }
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
