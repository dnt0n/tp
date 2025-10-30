package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UnarchiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code UnarchiveCommand} object.
 * <p>
 * Supports multiple comma-separated indexes (e.g., {@code "1,2,3"}).
 * Spaces around commas and tokens are allowed; empty tokens are ignored.
 * If no valid indexes remain after parsing, a usage error is raised.
 */
public class UnarchiveCommandParser implements Parser<UnarchiveCommand> {

    @Override
    public UnarchiveCommand parse(String args) throws ParseException {
        try {
            String trimmedArgs = args.trim();
            validateNonEmpty(trimmedArgs);

            String[] tokens = splitByComma(trimmedArgs);
            List<Index> indexes = parseIndexes(tokens);

            ensureNonEmptyIndexes(indexes);

            return new UnarchiveCommand(indexes);
        } catch (ParseException pe) {
            throw usageError(pe);
        }
    }

    // ---------------------------------------------------------------------
    // Helper methods
    // ---------------------------------------------------------------------

    /** Ensures the raw argument string is not empty. */
    private void validateNonEmpty(String trimmedArgs) throws ParseException {
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, UnarchiveCommand.MESSAGE_USAGE));
        }
    }

    private String[] splitByComma(String input) {
        return input.split(",");
    }

    /**
     * Parses each non-blank token into an {@link Index} using {@link ParserUtil#parseIndex(String)}.
     * Blank tokens (e.g., from consecutive commas) are skipped.
     */
    private List<Index> parseIndexes(String[] tokens) throws ParseException {
        List<Index> indexes = new ArrayList<>();
        for (String token : tokens) {
            if (token.isBlank()) {
                // avoid accepting "1,,,,2"
                throw new ParseException(String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT, UnarchiveCommand.MESSAGE_USAGE));
            }
            indexes.add(ParserUtil.parseIndex(token.trim()));
        }
        return indexes;
    }


    /** Ensures at least one valid index was provided */
    private void ensureNonEmptyIndexes(List<Index> indexes) throws ParseException {
        if (indexes.isEmpty()) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, UnarchiveCommand.MESSAGE_USAGE));
        }
    }

    /** Wrapper method for usage errors (to incerase SLAP) */
    private ParseException usageError(ParseException cause) {
        return new ParseException(String.format(
                MESSAGE_INVALID_COMMAND_FORMAT, UnarchiveCommand.MESSAGE_USAGE), cause);
    }
}
