package seedu.address.model.util;

import seedu.address.model.person.Person;

/**
 * Contains utility method(s) for formatting a person's information for viewing purposes
 */
public class PersonFormatter {
    /**
     * Returns a human-readable formatted profile string for the given person for viewing purposes
     */
    public static String formatProfile(Person person) {
        StringBuilder sb = new StringBuilder();

        sb.append("Name: ").append(person.getName()).append("\n");
        sb.append("Phone: ").append(person.getPhone()).append("\n");
        sb.append("Email: ").append(person.getEmail()).append("\n");
        sb.append("Matriculation No.: ").append(person.getMatriculationNumber()).append("\n");

        if (person.getTags().isEmpty()) {
            sb.append("Tags: (none)\n");
        } else {
            sb.append("Tags: ").append(person.getTags()).append("\n");
        }

        sb.append("Status: ").append(person.isArchived() ? "Archived" : "Active").append("\n");

        sb.append("Payments: ").append(person.getPayments().size()).append(" total").append("\n");

        // include latest payment info if available
        person.getLatestPayment().ifPresent(latest ->
                sb.append(" (Latest on ").append(latest.getDate()).append(")").append("\n")
        );

        sb.append("To view the full list of payments, use the \"viewpayments\" command.");

        return sb.toString();
    }
}
