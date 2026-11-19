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
import java.util.Objects;
/** Shared state and validation for a study task */
public abstract class StudyTask {
    private String title;
    private String course;
    private LocalDate dueDate;
    private boolean complete;
    /** Validate and store the shared fields */
    protected StudyTask(String title, String course, LocalDate dueDate) {
        update(title, course, dueDate);
    }
    /** Reject blank values before changing task state */
    static String required(String text) {
        if (text == null || text.isBlank()) throw new IllegalArgumentException("A value is required");
        return text.strip();
    }
    /** Update shared values together after validation */
    public final void update(String title, String course, LocalDate dueDate) {
        String checkedTitle = required(title);
        String checkedCourse = required(course);
        LocalDate checkedDate = Objects.requireNonNull(dueDate, "A due date is required");
        this.title = checkedTitle;
        this.course = checkedCourse;
        this.dueDate = checkedDate;
    }
    /** Return the title */
    public String getTitle() { return title; }
    /** Return the course */
    public String getCourse() { return course; }
    /** Return the due date */
    public LocalDate getDueDate() { return dueDate; }
    /** Return the completion state */
    public boolean isComplete() { return complete; }
    /** Mark complete or reopen */
    public void setComplete(boolean complete) { this.complete = complete; }
    /** Return subtype-specific details */
    public abstract String getDetails();
}
