package seedu.address.model.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable value object representing a monetary amount.
 * Rules:
 * - strictly positive
 * - at most 2 decimal places
 * - stored at fixed scale of 2 without rounding
 */
public final class Amount implements Comparable<Amount> {
    public static final String MESSAGE_CONSTRAINTS =
        "Invalid amount (must be positive, up to 2 decimal places, and at most 1 million).";
    public static final int SCALE = 2;

    private final BigDecimal value;

    public Amount(BigDecimal value) {
        this.value = normalize(value);
    }

    /**
     * Parse from a string such as "12.34".
     */
    public static Amount parse(String raw) {
        if (raw == null) {
            throw new NullPointerException("raw");
        }
        String s = raw.trim();
        try {
            if ((new BigDecimal(s)).compareTo(new BigDecimal("1000000.01")) > 0) {
                throw new NumberFormatException();
            }
            return new Amount(new BigDecimal(s));
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS, nfe);
        }
    }

    /**
     * Internal BigDecimal at scale 2.
     */
    public BigDecimal asBigDecimal() {
        return value;
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }

    @Override
    public int compareTo(Amount other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Amount)) {
            return false;
        }
        Amount other = (Amount) o;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    // ---------- helpers ----------

    private static BigDecimal normalize(BigDecimal input) {
        Objects.requireNonNull(input, "value");
        if (input.signum() <= 0) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        if (input.scale() > SCALE) {
            // do not round silently
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        return input.setScale(SCALE, RoundingMode.UNNECESSARY);
    }
}
