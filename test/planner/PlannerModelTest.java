package planner;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
/** Verify the Week 2 model milestone */
public class PlannerModelTest {
    private final LocalDate date = LocalDate.of(2026, 11, 11);
    private StudyTask task() { return new AssignmentTask("Lab", "CISC191", date, "Java sources"); }
    @Test void subtypeDetails() {
        StudyTask a = task();
        StudyTask e = new ExamTask("Exam", "CS130", date, "Binary arithmetic");
        assertEquals("Java sources", a.getDetails());
        assertEquals("Binary arithmetic", e.getDetails());
    }
    @Test void validatesFields() {
        assertThrows(IllegalArgumentException.class, () -> new AssignmentTask(" ", "CISC", date, "x"));
        assertThrows(IllegalArgumentException.class, () -> new ExamTask("Exam", " ", date, "x"));
        assertThrows(NullPointerException.class, () -> new ExamTask("Exam", "CS", null, "x"));
        assertThrows(IllegalArgumentException.class, () -> new ExamTask("Exam", "CS", date, " "));
    }
    @Test void failedUpdatePreservesState() {
        StudyTask a = task();
        assertThrows(IllegalArgumentException.class, () -> a.update("Changed", " ", date));
        assertEquals("Lab", a.getTitle());
        assertEquals("CISC191", a.getCourse());
    }
    @Test void addsMixedTasksAndProtectsList() {
        PlannerModel m = new PlannerModel();
        StudyTask a = task();
        m.addTask(a);
        m.addTask(new ExamTask("Exam", "CS", date, "Topics"));
        assertEquals(2, m.getTasks().size());
        assertThrows(UnsupportedOperationException.class, () -> m.getTasks().clear());
        assertThrows(IllegalArgumentException.class, () -> m.addTask(a));
        assertThrows(NullPointerException.class, () -> m.addTask(null));
    }
    @Test void completesAndReopens() {
        PlannerModel m = new PlannerModel(); StudyTask a = task(); m.addTask(a);
        m.toggleComplete(a); assertTrue(a.isComplete());
        m.toggleComplete(a); assertFalse(a.isComplete());
        assertThrows(IllegalArgumentException.class, () -> m.toggleComplete(task()));
    }
    @Test void editsAndRetainsCompletion() {
        PlannerModel m = new PlannerModel(); StudyTask a = task(); m.addTask(a); m.toggleComplete(a);
        StudyTask replacement = new ExamTask("Review", "CS130", date.plusDays(1), "Adders");
        m.editTask(a, replacement);
        assertSame(replacement, m.getTasks().get(0)); assertTrue(replacement.isComplete());
        assertThrows(IllegalArgumentException.class, () -> m.editTask(a, task()));
    }
    @Test void removesOnlySelectedTask() {
        PlannerModel m = new PlannerModel(); StudyTask a = task(); m.addTask(a);
        assertFalse(m.removeTask(task())); assertTrue(m.removeTask(a)); assertTrue(m.getTasks().isEmpty());
    }
}
