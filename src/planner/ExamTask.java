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
import java.time.LocalDate;
/** A study task with topics */
public final class ExamTask extends StudyTask {
    private final String topics;
    /** Validate all fields and store the details */
    public ExamTask(String title, String course, LocalDate date, String details) {
        super(title, course, date);
        topics = required(details);
    }
    /** Return the topics */
    @Override public String getDetails() { return topics; }
}
