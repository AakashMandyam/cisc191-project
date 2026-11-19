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

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Verify the Week 3 features: sorting, filtering, seven-day counts and file handling */
public class PlannerFeaturesTest {
    private final LocalDate today = LocalDate.of(2026, 11, 17);

    private PlannerModel sampleModel() {
        PlannerModel m = new PlannerModel();
        m.addTask(new ExamTask("Midterm", "CS130", today.plusDays(10), "Adders"));
        m.addTask(new AssignmentTask("Project", "CISC191", today.plusDays(1), "Checkpoint"));
        StudyTask done = new AssignmentTask("Post", "CISC191", today.plusDays(3), "Replies");
        done.setComplete(true);
        m.addTask(done);
        m.addTask(new AssignmentTask("Reading", "ENGL100", today.minusDays(2), "Chapter 4"));
        return m;
    }

    @Test
    void sortsByDueDateThenTitle() {
        PlannerModel m = sampleModel();
        m.addTask(new AssignmentTask("alpha", "CS", today.plusDays(1), "x"));
        m.sortByDueDate();
        List<StudyTask> t = m.getTasks();
        assertEquals("Reading", t.get(0).getTitle());
        assertEquals("alpha", t.get(1).getTitle());
        assertEquals("Project", t.get(2).getTitle());
        assertEquals("Midterm", t.get(t.size() - 1).getTitle());
    }

    @Test
    void filtersByCourseAndStatusWithoutChangingList() {
        PlannerModel m = sampleModel();
        assertEquals(2, m.getTasks("cisc191", false).size());
        assertEquals(1, m.getTasks(" CISC191 ", true).size());
        assertEquals(3, m.getTasks("", true).size());
        assertEquals(4, m.getTasks(null, false).size());
        assertTrue(m.getTasks("NOPE", false).isEmpty());
        assertEquals(4, m.getTasks().size());
    }

    @Test
    void countsDueInNextSevenDays() {
        PlannerModel m = sampleModel();
        assertEquals(1, m.countDueWithin(today, 7)); // Project only; Post is complete
        m.addTask(new ExamTask("Edge", "CS", today.plusDays(7), "x"));
        m.addTask(new ExamTask("Too late", "CS", today.plusDays(8), "x"));
        m.addTask(new ExamTask("Today", "CS", today, "x"));
        assertEquals(3, m.countDueWithin(today, 7));
        assertEquals(1, m.countOverdue(today));
        assertEquals("Due in the next 7 days: 3 | Overdue: 1", m.dueSummary(today));
        assertThrows(IllegalArgumentException.class, () -> m.countDueWithin(today, -1));
    }

    @Test
    void replaceAllKeepsOldListWhenInvalid() {
        PlannerModel m = sampleModel();
        StudyTask dup = new ExamTask("Dup", "CS", today, "x");
        assertThrows(IllegalArgumentException.class, () -> m.replaceAll(List.of(dup, dup)));
        assertEquals(4, m.getTasks().size());
        m.replaceAll(List.of(dup));
        assertEquals(1, m.getTasks().size());
    }

    @Test
    void saveThenLoadRoundTrip(@TempDir Path dir) throws Exception {
        PlannerModel m = sampleModel();
        m.addTask(new ExamTask("Tabs\tand\nlines \\ slash", "CS130", today, "Line1\nLine2"));
        Path file = dir.resolve("tasks.txt");
        TaskFileStore.save(m.getTasks(), file);
        List<StudyTask> loaded = TaskFileStore.load(file);
        assertEquals(5, loaded.size());
        for (int i = 0; i < loaded.size(); i++) {
            StudyTask a = m.getTasks().get(i);
            StudyTask b = loaded.get(i);
            assertEquals(a.getClass(), b.getClass());
            assertEquals(a.getTitle(), b.getTitle());
            assertEquals(a.getCourse(), b.getCourse());
            assertEquals(a.getDueDate(), b.getDueDate());
            assertEquals(a.getDetails(), b.getDetails());
            assertEquals(a.isComplete(), b.isComplete());
        }
        assertFalse(Files.exists(dir.resolve("tasks.txt.tmp")));
    }

    @Test
    void loadMissingFileThrowsPlannerException(@TempDir Path dir) {
        assertThrows(PlannerException.class, () -> TaskFileStore.load(dir.resolve("missing.txt")));
    }

    @Test
    void saveToMissingFolderThrowsPlannerException(@TempDir Path dir) {
        Path bad = dir.resolve("no-such-folder").resolve("tasks.txt");
        assertThrows(PlannerException.class, () -> TaskFileStore.save(List.of(), bad));
    }

    @Test
    void loadRejectsDamagedFilesWithLineNumber(@TempDir Path dir) throws IOException {
        String h = "# StudyTaskPlanner v1\n";
        String[] bad = { "not a planner file\n", "", h + "EXAM\tOnly\tthree\n",
                h + "QUIZ\tT\tC\t2026-11-17\tfalse\tD\n", h + "EXAM\tT\tC\t17/11/2026\tfalse\tD\n",
                h + "EXAM\tT\tC\t2026-11-17\tmaybe\tD\n", h + "EXAM\t \tC\t2026-11-17\tfalse\tD\n",
                h + "EXAM\tT\tC\t2026-11-17\tfalse\tbad\\q\n" };
        Path file = dir.resolve("bad.txt");
        for (String text : bad) {
            Files.writeString(file, text);
            assertThrows(PlannerException.class, () -> TaskFileStore.load(file), text);
        }
        Files.writeString(file, h + "EXAM\tOnly\tthree\n");
        PlannerException e = assertThrows(PlannerException.class, () -> TaskFileStore.load(file));
        assertTrue(e.getMessage().startsWith("Line 2"));
    }

    @Test
    void loadSkipsBlankLines(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("ok.txt");
        Files.writeString(file, "# StudyTaskPlanner v1\n\nEXAM\tT\tC\t2026-11-17\ttrue\tD\n\n");
        List<StudyTask> loaded = TaskFileStore.load(file);
        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).isComplete());
    }
}
