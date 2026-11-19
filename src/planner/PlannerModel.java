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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
/** Own tasks independently of the Swing view */
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

    /** Sort the stored list by due date, then by title, keeping equal tasks in order */
    public void sortByDueDate() {
        tasks.sort(Comparator.comparing(StudyTask::getDueDate)
                .thenComparing(StudyTask::getTitle, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Return a snapshot of the tasks that match a filter. The stored list is not
     * changed.
     *
     * @param course         course to match ignoring case, or null/blank for all courses
     * @param incompleteOnly true to leave out completed tasks
     * @return the matching tasks in their current order
     */
    public List<StudyTask> getTasks(String course, boolean incompleteOnly) {
        String wanted = course == null ? "" : course.strip();
        ArrayList<StudyTask> matches = new ArrayList<>();
        for (StudyTask task : tasks) {
            boolean courseOk = wanted.isEmpty() || task.getCourse().equalsIgnoreCase(wanted);
            boolean statusOk = !incompleteOnly || !task.isComplete();
            if (courseOk && statusOk) matches.add(task);
        }
        return List.copyOf(matches);
    }

    /**
     * Count incomplete tasks due from today through the last day of the window.
     *
     * @param today the first day of the window
     * @param days  how many days after today are included (7 means today plus 7 days)
     * @return the number of incomplete tasks in that window
     */
    public int countDueWithin(LocalDate today, int days) {
        if (days < 0) throw new IllegalArgumentException("Days cannot be negative");
        LocalDate last = today.plusDays(days);
        int count = 0;
        for (StudyTask task : tasks) {
            LocalDate due = task.getDueDate();
            if (!task.isComplete() && !due.isBefore(today) && !due.isAfter(last)) count++;
        }
        return count;
    }

    /** Count incomplete tasks whose due date is before today */
    public int countOverdue(LocalDate today) {
        int count = 0;
        for (StudyTask task : tasks)
            if (!task.isComplete() && task.getDueDate().isBefore(today)) count++;
        return count;
    }

    /** Build a one-line summary of what is due in the next seven days and what is overdue */
    public String dueSummary(LocalDate today) {
        return "Due in the next 7 days: " + countDueWithin(today, 7) + " | Overdue: " + countOverdue(today);
    }

    /**
     * Replace the whole list, for example after loading a file. The new tasks are
     * checked first so the old list is kept if they are not valid.
     *
     * @param newTasks the tasks that become the planner contents
     */
    public void replaceAll(List<StudyTask> newTasks) {
        Objects.requireNonNull(newTasks, "A task list is required");
        ArrayList<StudyTask> copy = new ArrayList<>();
        for (StudyTask task : newTasks) {
            Objects.requireNonNull(task, "A task is required");
            if (copy.contains(task)) throw new IllegalArgumentException("Task listed twice");
            copy.add(task);
        }
        tasks.clear();
        tasks.addAll(copy);
    }
}
