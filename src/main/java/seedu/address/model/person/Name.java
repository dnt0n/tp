package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}.
 *
 * <p>
 * This class enforces a realistic and inclusive naming convention:
 * <ul>
 *   <li>Names must contain only alphabetic characters, spaces, hyphens (-), apostrophes ('), and periods (.)</li>
 *   <li>Names must start with a letter (no leading whitespace or symbols)</li>
 *   <li>Names cannot contain digits or other special symbols (e.g., @, #, $, %, etc.)</li>
 *   <li>Names must not be blank</li>
 *   <li>Supports Unicode letters (e.g., accented or non-Latin alphabets)</li>
 * </ul>
 *
 * Examples of valid names:
 * <pre>
 *   - "Alex Yeoh"
 *   - "Jean-Luc Picard"
 *   - "O’Connor"
 *   - "J. K. Rowling"
 *   - "李小龙"
 * </pre>
 *
 * Examples of invalid names:
 * <pre>
 *   - "123John"        (contains digits)
 *   - "@lex"           (contains invalid character)
 *   - " John"          (starts with space)
 *   - ""               (blank)
 * </pre>
 */
public class Name {

    /**
     * Error message shown when a given name does not meet the required format.
     */
    public static final String MESSAGE_CONSTRAINTS =
        "Names should only contain alphabetic characters, spaces, hyphens (-), apostrophes ('), "
            + "and periods (.), should not be blank and should be at most 70 characters long.";

    /*
     * The name must:
     * 1. Start with an alphabetic character (Unicode supported).
     * 2. Contain only letters, spaces, hyphens, apostrophes, or periods.
     *
     * The pattern "[\\p{L}][\\p{L} .'-]*" ensures:
     * - \\p{L} : any Unicode letter (A–Z, a–z, or letters from other languages)
     * - [\\p{L} .'-]* : subsequent characters can be letters, spaces, '.', '\'', or '-'
     */
    public static final String VALIDATION_REGEX = "[\\p{L}][\\p{L} .'-]*";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     * @throws NullPointerException if {@code name} is null.
     * @throws IllegalArgumentException if {@code name} does not satisfy {@link #isValidName(String)}.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name according to {@link #VALIDATION_REGEX}.
     */
    public static boolean isValidName(String test) {
        return test.length() <= 70 && test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name)) {
            return false;
        }

        Name otherName = (Name) other;
        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
