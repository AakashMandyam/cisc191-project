package planner;
import java.time.LocalDate;
/** A study task with requirements */
public final class AssignmentTask extends StudyTask {
    private final String requirements;
    /** Validate all fields and store the details */
    public AssignmentTask(String title, String course, LocalDate date, String details) {
        super(title, course, date);
        requirements = required(details);
    }
    /** Return the requirements */
    @Override public String getDetails() { return requirements; }
}
