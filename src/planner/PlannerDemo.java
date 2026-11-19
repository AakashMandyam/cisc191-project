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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

/** Demonstrate model operations with sample task data */
public class PlannerDemo {
    /** Print actual results for the model, file, sort, filter and summary features */
    public static void main(String[] args) throws IOException {
        PlannerModel model = new PlannerModel();
        StudyTask assignment = new AssignmentTask("Sample lab", "CISC191", LocalDate.of(2026, 11, 11), "Java sources");
        StudyTask exam = new ExamTask("Sample review", "CS130", LocalDate.of(2026, 11, 12), "Sequential logic");
        model.addTask(assignment);
        model.addTask(exam);
        System.out.println("Sample tasks added: " + model.getTasks().size());
        for (StudyTask task : model.getTasks())
            System.out.println(describe(task));
        model.toggleComplete(assignment);
        System.out.println("Assignment completed: " + assignment.isComplete());
        StudyTask replacement = new AssignmentTask("Sample lab revised", "CISC191", LocalDate.of(2026, 11, 13),
                "Revised requirements");
        model.editTask(assignment, replacement);
        System.out.println("Edited title: " + model.getTasks().get(0).getTitle());
        System.out.println("Completion retained after edit: " + replacement.isComplete());
        model.toggleComplete(replacement);
        System.out.println("Assignment reopened: " + !replacement.isComplete());
        System.out.println("Exam removed: " + model.removeTask(exam));
        System.out.println("Remaining tasks: " + model.getTasks().size());
        try {
            replacement.update("Changed", " ", replacement.getDueDate());
        } catch (IllegalArgumentException e) {
            System.out.println("Blank course rejected; title preserved: " + replacement.getTitle());
        }

        // Week 3: sorting, filtering and the seven-day summary
        System.out.println();
        System.out.println("--- Week 3 features ---");
        LocalDate today = LocalDate.of(2026, 11, 17);
        PlannerModel planner = new PlannerModel();
        planner.addTask(new ExamTask("CS130 midterm", "CS130", today.plusDays(10), "Adders, latches"));
        planner.addTask(new AssignmentTask("Week 3 project", "CISC191", today.plusDays(1), "Checkpoint"));
        StudyTask done = new AssignmentTask("Discussion post", "CISC191", today.plusDays(3), "Reply to two peers");
        done.setComplete(true);
        planner.addTask(done);
        planner.addTask(new AssignmentTask("Late reading", "ENGL100", today.minusDays(2), "Chapter 4"));
        planner.addTask(new ExamTask("Quiz review", "CS130", today.plusDays(7), "Chapter 5"));
        planner.sortByDueDate();
        System.out.println("Sorted by due date:");
        for (StudyTask task : planner.getTasks())
            System.out.println("  " + describe(task));
        System.out.println("CISC191 only: " + planner.getTasks("cisc191", false).size());
        System.out.println("Incomplete only: " + planner.getTasks("", true).size());
        System.out.println("CS130 incomplete: " + planner.getTasks("CS130", true).size());
        System.out.println(planner.dueSummary(today));

        // Save and load using a temporary file
        Path file = Files.createTempFile("planner-demo", ".txt");
        try {
            TaskFileStore.save(planner.getTasks(), file);
            PlannerModel reloaded = new PlannerModel();
            reloaded.replaceAll(TaskFileStore.load(file));
            System.out.println("Saved and reloaded tasks: " + reloaded.getTasks().size());
            System.out.println("Same summary after reload: " + reloaded.dueSummary(today).equals(planner.dueSummary(today)));

            // Exception handling: damaged file and missing file
            Files.writeString(file, "# StudyTaskPlanner v1\nEXAM\tTitle\tCourse\tnot-a-date\tfalse\tTopics\n");
            try {
                TaskFileStore.load(file);
            } catch (PlannerException e) {
                System.out.println("Bad file rejected: " + e.getMessage());
            }
            try {
                TaskFileStore.load(file.resolveSibling("does-not-exist.txt"));
            } catch (PlannerException e) {
                System.out.println("Missing file reported: " + e.getMessage().startsWith("Could not read"));
            }
        } catch (PlannerException e) {
            System.out.println("Unexpected file problem: " + e.getMessage());
        } finally {
            Files.deleteIfExists(file);
        }
    }

    /** Format one task as a single line */
    private static String describe(StudyTask task) {
        return task.getClass().getSimpleName() + " | " + task.getTitle() + " | " + task.getCourse() + " | "
                + task.getDueDate() + " | " + task.getDetails() + " | " + (task.isComplete() ? "Complete" : "Open");
    }
}
