package planner;
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
