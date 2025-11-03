package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.FindPaymentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.payment.Amount;

public class FindPaymentCommandParserTest {

    private final FindPaymentCommandParser parser = new FindPaymentCommandParser();

    @Test
    public void parse_validAmount_returnsCommand() throws Exception {
        FindPaymentCommand command = parser.parse("1 a/50.00");
        FindPaymentCommand expected = new FindPaymentCommand(Index.fromOneBased(1),
                new Amount(new BigDecimal("50.00")), null, null);
        assertEquals(expected, command);
    }

    @Test
    public void parse_validRemark_returnsCommand() throws Exception {
        FindPaymentCommand command = parser.parse("1 r/CCA");
        FindPaymentCommand expected = new FindPaymentCommand(Index.fromOneBased(1), null, "CCA", null);
        assertEquals(expected, command);
    }

    @Test
    public void parse_validDate_returnsCommand() throws Exception {
        LocalDate date = LocalDate.of(2025, 1, 1);
        FindPaymentCommand command = parser.parse("1 d/2025-01-01");
        FindPaymentCommand expected = new FindPaymentCommand(Index.fromOneBased(1), null, null, date);
        assertEquals(expected, command);
    }

    @Test
    public void parse_missingFilter_throwsParseException() {
        ParseException e = assertThrows(ParseException.class, () -> parser.parse("1"));
        assertEquals("Please provide one filter: a/AMOUNT, d/DATE or r/REMARK", e.getMessage());
    }

    @Test
    public void parse_multipleFilters_throwsParseException() {
        ParseException e = assertThrows(ParseException.class, () -> parser.parse("1 a/50.00 r/CCA"));
        assertEquals("Please specify only one filter at a time.", e.getMessage());
    }

    @Test
    public void parse_invalidAmount_throwsParseException() {
        ParseException e = assertThrows(ParseException.class, () -> parser.parse("1 a/abc"));
        assertEquals("Invalid amount: must be positive, ≤ 2 decimal places, and at most 1 million.", e.getMessage());
    }

    @Test
    public void parse_invalidDate_throwsParseException() {
        ParseException e = assertThrows(ParseException.class, () -> parser.parse("1 d/2025-31-12"));
        assertEquals(
            "Invalid date. Please use the strict format YYYY-MM-DD and ensure it is not in the future.",
            e.getMessage()
        );
    }


    @Test
    public void parse_emptyRemark_throwsParseException() {
        ParseException e = assertThrows(ParseException.class, () -> parser.parse("1 r/   "));
        assertEquals("Remark cannot be empty.", e.getMessage());
    }
}
