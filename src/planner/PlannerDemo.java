package planner;
import java.time.LocalDate;
/** Demonstrate model operations with sample task data */
public class PlannerDemo {
    /** Print actual results for the model milestone */
    public static void main(String[] args) {
        PlannerModel model = new PlannerModel();
        StudyTask assignment = new AssignmentTask("Sample lab", "CISC191", LocalDate.of(2026, 11, 11), "Java sources");
        StudyTask exam = new ExamTask("Sample review", "CS130", LocalDate.of(2026, 11, 12), "Sequential logic");
        model.addTask(assignment);
        model.addTask(exam);
        System.out.println("Sample tasks added: " + model.getTasks().size());
        for (StudyTask task : model.getTasks())
            System.out.println(task.getClass().getSimpleName() + " | " + task.getTitle() + " | " + task.getCourse() + " | " + task.getDueDate() + " | " + task.getDetails());
        model.toggleComplete(assignment);
        System.out.println("Assignment completed: " + assignment.isComplete());
        StudyTask replacement = new AssignmentTask("Sample lab revised", "CISC191", LocalDate.of(2026, 11, 13), "Revised requirements");
        model.editTask(assignment, replacement);
        System.out.println("Edited title: " + model.getTasks().get(0).getTitle());
        System.out.println("Completion retained after edit: " + replacement.isComplete());
        model.toggleComplete(replacement);
        System.out.println("Assignment reopened: " + !replacement.isComplete());
        System.out.println("Exam removed: " + model.removeTask(exam));
        System.out.println("Remaining tasks: " + model.getTasks().size());
        try { replacement.update("Changed", " ", replacement.getDueDate()); }
        catch (IllegalArgumentException e) { System.out.println("Blank course rejected; title preserved: " + replacement.getTitle()); }
    }
}
