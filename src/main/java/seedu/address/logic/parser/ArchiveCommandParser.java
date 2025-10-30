package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code ArchiveCommand} object.
 * <p>
 * Expected input format (arguments only, command word handled upstream):
 * <pre>
 *   "1"                // single index
 *   "1,2,5"            // multiple comma-separated indexes
 *   " 1 ,  2 , 3  "    // spaces around commas and indexes are allowed
 * </pre>
 * usage error is raised. Each non-blank token is parsed via {@link ParserUtil#parseIndex(String)}.
 */
public class ArchiveCommandParser implements Parser<ArchiveCommand> {

    @Override
    public ArchiveCommand parse(String args) throws ParseException {
        try {
            String trimmedArgs = args.trim();
            validateNonEmpty(trimmedArgs);

            String[] tokens = splitByComma(trimmedArgs);
            List<Index> indexes = parseIndexes(tokens);

            ensureNonEmptyIndexes(indexes);

            return new ArchiveCommand(indexes);
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
                    MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE));
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
                        MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE));
            }
            indexes.add(ParserUtil.parseIndex(token.trim()));
        }
        return indexes;
    }

    /** Ensures at least one valid index was provided. */
    private void ensureNonEmptyIndexes(List<Index> indexes) throws ParseException {
        if (indexes.isEmpty()) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE));
        }
    }

    /** wrapper method for usage error */
    private ParseException usageError(ParseException cause) {
        return new ParseException(String.format(
                MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE), cause);
    }
}
