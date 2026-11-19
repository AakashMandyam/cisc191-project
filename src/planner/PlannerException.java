package planner;

/**
 * Lead Author(s):
 * @author Aakash Mandyam
 *
 * Other contributors:
 * none
 *
 * References:
 * Morelli, R., & Walde, R. (2016). Java, Java, Java: Object-Oriented Problem Solving.
 * https://open.umn.edu/opentextbooks/textbooks/java-java-java-object-oriented-problem-solving
 *
 * Version/date: November 18, 2026
 *
 * Responsibilities of class: see the class description below
 */

/**
 * Checked exception for problems saving or loading planner files, such as a
 * missing file, an unreadable file, or a line that is not a valid task.
 */
public class PlannerException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Create an exception with a message that can be shown to the user.
     *
     * @param message what went wrong
     */
    public PlannerException(String message) {
        super(message);
    }

    /**
     * Create an exception that keeps the original cause.
     *
     * @param message what went wrong
     * @param cause   the lower-level exception, such as an IOException
     */
    public PlannerException(String message, Throwable cause) {
        super(message, cause);
    }
}
