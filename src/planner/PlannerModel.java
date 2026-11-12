package planner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/** Own tasks independently of the future Swing view */
public final class PlannerModel {
    private final ArrayList<StudyTask> tasks = new ArrayList<>();
    /** Add one task reference */
    public void addTask(StudyTask task) {
        Objects.requireNonNull(task, "A task is required");
        if (tasks.contains(task)) throw new IllegalArgumentException("Task already added");
        tasks.add(task);
    }
    /** Return a structural snapshot of the list */
    public List<StudyTask> getTasks() { return List.copyOf(tasks); }
    /** Replace an existing task and retain its completion state */
    public void editTask(StudyTask original, StudyTask replacement) {
        int index = tasks.indexOf(original);
        if (index < 0) throw new IllegalArgumentException("Task is not in this planner");
        Objects.requireNonNull(replacement, "A replacement is required");
        if (replacement != original && tasks.contains(replacement))
            throw new IllegalArgumentException("Replacement already added");
        replacement.setComplete(original.isComplete());
        tasks.set(index, replacement);
    }
    /** Toggle a stored task between complete and incomplete */
    public void toggleComplete(StudyTask task) {
        if (!tasks.contains(task)) throw new IllegalArgumentException("Task is not in this planner");
        task.setComplete(!task.isComplete());
    }
    /** Remove an existing task */
    public boolean removeTask(StudyTask task) { return tasks.remove(task); }
}
