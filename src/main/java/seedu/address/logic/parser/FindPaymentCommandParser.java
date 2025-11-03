package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PAYMENT_AMOUNT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PAYMENT_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PAYMENT_REMARKS;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.FindPaymentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.payment.Amount;

/**
 * Parses input arguments and creates a new {@link FindPaymentCommand} object.
 *
 * <p>Expected format:
 * <pre>
 *     findpayment INDEX [a/AMOUNT | d/DATE | r/REMARK]
 * </pre>
 *
 * <p>Examples:
 * <ul>
 *     <li>{@code findpayment 1 a/23.50}</li>
 *     <li>{@code findpayment 3 d/2023-12-30}</li>
 *     <li>{@code findpayment 4 r/cca shirt}</li>
 * </ul>
 */
public class FindPaymentCommandParser implements Parser<FindPaymentCommand> {

    private static final String MESSAGE_MISSING_FILTER =
            "Please provide one filter: a/AMOUNT, d/DATE or r/REMARK";
    private static final String MESSAGE_TOO_MANY_FILTERS =
            "Please specify only one filter at a time.";
    private static final String MESSAGE_INVALID_AMOUNT =
            "Invalid amount: must be positive and ≤ 2 decimal places.";
    private static final String MESSAGE_INVALID_DATE =
            "Invalid date. Please use the strict format YYYY-MM-DD and ensure it is not in the future.";
    private static final String MESSAGE_EMPTY_REMARK = "Remark cannot be empty.";
    private static final String MESSAGE_EMPTY_AMOUNT = "Amount cannot be empty.";
    private static final String MESSAGE_EMPTY_DATE = "Date cannot be empty.";
    private static final String MESSAGE_UNKNOWN_PREFIX =
            "Unknown filter: %s (valid filters are a/AMOUNT, d/DATE and r/REMARK)";

    private static final String[] VALID_PREFIXES = { "a/", "r/", "d/" };

    /**
     * Parses the given {@code String} of arguments and returns a {@link FindPaymentCommand} object.
     *
     * @param args full user input string.
     * @return a {@link FindPaymentCommand} representing the parsed filter and member index.
     * @throws ParseException if user input does not conform to the expected format or contains invalid data.
     */
    @Override
    public FindPaymentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMap = ArgumentTokenizer.tokenize(
                args, PREFIX_PAYMENT_AMOUNT, PREFIX_PAYMENT_REMARKS, PREFIX_PAYMENT_DATE);

        Index targetIndex = parseIndex(argMap);
        validatePrefixUsage(argMap);

        return buildCommand(argMap, targetIndex);
    }

    // ----------------------------------------------------
    // Helpers for parsing and validation
    // ----------------------------------------------------

    /**
     * Parses and validates the member index from the argument preamble.
     *
     * @param map the {@link ArgumentMultimap} containing user arguments.
     * @return the parsed {@link Index} of the member.
     * @throws ParseException if the index is missing, not numeric, or invalid.
     */
    private Index parseIndex(ArgumentMultimap map) throws ParseException {
        String preamble = map.getPreamble().trim();
        if (preamble.isBlank()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindPaymentCommand.MESSAGE_USAGE));
        }

        String[] tokens = preamble.split("\\s+");
        if (tokens.length != 1) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindPaymentCommand.MESSAGE_USAGE));
        }

        try {
            return ParserUtil.parseIndex(tokens[0]);
        } catch (Exception e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindPaymentCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Ensures that exactly one filter prefix (amount, date, or remark) is used,
     * and that no duplicates exist.
     *
     * @param map the {@link ArgumentMultimap} containing parsed prefixes.
     * @throws ParseException if no filter or multiple filters are provided.
     */
    private void validatePrefixUsage(ArgumentMultimap map) throws ParseException {
        map.verifyNoDuplicatePrefixesFor(PREFIX_PAYMENT_AMOUNT, PREFIX_PAYMENT_REMARKS, PREFIX_PAYMENT_DATE);

        int filtersUsed = countFilters(map);
        if (filtersUsed == 0) {
            throw new ParseException(MESSAGE_MISSING_FILTER);
        }
        if (filtersUsed > 1) {
            throw new ParseException(MESSAGE_TOO_MANY_FILTERS);
        }
    }

    /**
     * Builds a {@link FindPaymentCommand} using the appropriate filter type.
     *
     * @param map the parsed argument mappings.
     * @param index the target member index.
     * @return a fully constructed {@link FindPaymentCommand}.
     * @throws ParseException if filter data is invalid.
     */
    private FindPaymentCommand buildCommand(ArgumentMultimap map, Index index) throws ParseException {
        Optional<String> amountVal = map.getValue(PREFIX_PAYMENT_AMOUNT);
        Optional<String> remarkVal = map.getValue(PREFIX_PAYMENT_REMARKS);
        Optional<String> dateVal = map.getValue(PREFIX_PAYMENT_DATE);

        if (amountVal.isPresent()) {
            Amount amount = parseAmount(amountVal.get());
            return new FindPaymentCommand(index, amount, null, null);
        }

        if (remarkVal.isPresent()) {
            String remark = parseRemark(remarkVal.get());
            return new FindPaymentCommand(index, null, remark, null);
        }

        LocalDate date = parseDate(dateVal.get());
        return new FindPaymentCommand(index, null, null, date);
    }

    /**
     * Counts the number of filters provided by the user.
     *
     * @param map the {@link ArgumentMultimap} containing all parsed arguments.
     * @return the number of filters (0–3).
     */
    private int countFilters(ArgumentMultimap map) {
        return (map.getValue(PREFIX_PAYMENT_AMOUNT).isPresent() ? 1 : 0)
                + (map.getValue(PREFIX_PAYMENT_REMARKS).isPresent() ? 1 : 0)
                + (map.getValue(PREFIX_PAYMENT_DATE).isPresent() ? 1 : 0);
    }

    // ----------------------------------------------------
    // Filter parsers for amount, remark, and date
    // ----------------------------------------------------

    private Amount parseAmount(String amountStr) throws ParseException {
        if (amountStr.trim().isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_AMOUNT);
        }
        try {
            return Amount.parse(amountStr);
        } catch (IllegalArgumentException ex) {
            throw new ParseException(MESSAGE_INVALID_AMOUNT, ex);
        }
    }

    private String parseRemark(String value) throws ParseException {
        String remark = value.trim();
        if (remark.trim().isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_REMARK);
        }
        return remark;
    }

    private LocalDate parseDate(String dateStr) throws ParseException {
        if (dateStr.trim().isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_DATE);
        }
        try {
            return seedu.address.model.payment.Payment.parseStrictDate(dateStr);
        } catch (DateTimeParseException | IllegalArgumentException ex) {
            throw new ParseException(MESSAGE_INVALID_DATE, ex);
        }
    }
}
