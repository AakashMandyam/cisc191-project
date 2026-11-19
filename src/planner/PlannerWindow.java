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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Display and edit the current planner model */
public final class PlannerWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private final transient PlannerModel model;
    /** Tasks currently shown in the table, in row order (the table may be filtered) */
    private transient List<StudyTask> shown = List.of();
    private final JComboBox<String> type = new JComboBox<>(new String[] { "Assignment", "Exam" });
    private final JTextField title = new JTextField();
    private final JTextField course = new JTextField();
    private final JTextField date = new JTextField(LocalDate.now().toString());
    private final JTextField details = new JTextField();
    private final JTextField courseFilter = new JTextField(10);
    private final JCheckBox incompleteOnly = new JCheckBox("Incomplete only");
    private final DefaultTableModel rows = new DefaultTableModel(
            new String[] { "Type", "Title", "Course", "Due date", "Status" }, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(rows);
    private final JLabel summary = new JLabel();
    private final JLabel status = new JLabel("Week 3 - tasks can be saved to and loaded from a file");

    /**
     * Connect a Swing view to an existing model.
     *
     * @param model the planner data this window displays and changes
     */
    public PlannerWindow(PlannerModel model) {
        super("Study Task Planner - Week 3");
        this.model = model;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationByPlatform(true);

        // Input form: labels on the first row, fields on the second
        JPanel form = new JPanel(new GridLayout(2, 5, 8, 5));
        for (String label : new String[] { "Task type", "Title", "Course", "Due date (YYYY-MM-DD)",
                "Requirements / topics" })
            form.add(new JLabel(label));
        for (JComponent field : new JComponent[] { type, title, course, date, details })
            form.add(field);

        // Row of task buttons
        JPanel buttons = new JPanel();
        addButton(buttons, "Add", this::addTask);
        addButton(buttons, "Save edit", this::editTask);
        addButton(buttons, "Complete / reopen", this::toggleTask);
        addButton(buttons, "Remove", this::removeTask);

        // Row for file, sort and filter controls
        JPanel tools = new JPanel();
        addButton(tools, "Save to file...", this::saveFile);
        addButton(tools, "Load from file...", this::loadFile);
        addButton(tools, "Sort by due date", this::sortTasks);
        tools.add(new JLabel("Course filter:"));
        tools.add(courseFilter);
        incompleteOnly.addActionListener(event -> applyFilter());
        tools.add(incompleteOnly);
        addButton(tools, "Apply filter", this::applyFilter);
        addButton(tools, "Clear filter", this::clearFilter);

        JPanel buttonRows = new JPanel(new GridLayout(2, 1));
        buttonRows.add(buttons);
        buttonRows.add(tools);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(form, BorderLayout.CENTER);
        top.add(buttonRows, BorderLayout.SOUTH);

        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.add(summary);
        bottom.add(status);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        content.add(top, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);
        setContentPane(content);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting())
                fillSelection();
        });
        refresh(null);
    }

    /**
     * Create a button whose action reports problems in a dialog instead of
     * letting the exception escape.
     */
    private void addButton(JPanel panel, String label, ThrowingAction action) {
        JButton button = new JButton(label);
        button.addActionListener(event -> {
            try {
                action.run();
            } catch (IllegalArgumentException | IllegalStateException error) {
                JOptionPane.showMessageDialog(this, error.getMessage(), "Check task", JOptionPane.WARNING_MESSAGE);
            } catch (PlannerException error) {
                status.setText("File problem - nothing was changed");
                JOptionPane.showMessageDialog(this, error.getMessage(), "File problem", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(button);
    }

    /** An action that may fail with a file problem */
    @FunctionalInterface
    private interface ThrowingAction {
        void run() throws PlannerException;
    }

    /** Build a task from the form fields, rejecting a bad date */
    private StudyTask inputTask() {
        LocalDate due;
        try {
            due = LocalDate.parse(date.getText().strip());
        } catch (java.time.format.DateTimeParseException error) {
            throw new IllegalArgumentException("Enter a valid due date in YYYY-MM-DD format");
        }
        return type.getSelectedIndex() == 0
                ? new AssignmentTask(title.getText(), course.getText(), due, details.getText())
                : new ExamTask(title.getText(), course.getText(), due, details.getText());
    }

    /** Return the task for the selected row (rows map to the filtered list) */
    private StudyTask selectedTask() {
        int index = table.getSelectedRow();
        if (index < 0)
            throw new IllegalStateException("Select a task first");
        return shown.get(index);
    }

    private void addTask() {
        StudyTask task = inputTask();
        model.addTask(task);
        refresh(task);
        status.setText(shown.contains(task) ? "Task added - not saved to a file yet"
                : "Task added but hidden by the current filter");
    }

    private void editTask() {
        StudyTask replacement = inputTask();
        model.editTask(selectedTask(), replacement);
        refresh(replacement);
        status.setText("Task edited; completion state retained");
    }

    private void toggleTask() {
        StudyTask task = selectedTask();
        model.toggleComplete(task);
        refresh(task);
        status.setText("Completion state updated");
    }

    private void removeTask() {
        model.removeTask(selectedTask());
        refresh(null);
        status.setText("Selected task removed");
    }

    /** Ask for a file and save every task (not just the filtered ones) */
    private void saveFile() throws PlannerException {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("tasks.txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        Path file = chooser.getSelectedFile().toPath();
        TaskFileStore.save(model.getTasks(), file);
        status.setText("Saved " + model.getTasks().size() + " tasks to " + file.getFileName());
    }

    /** Ask for a file and replace the list only if the whole file is valid */
    private void loadFile() throws PlannerException {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        Path file = chooser.getSelectedFile().toPath();
        List<StudyTask> loaded = TaskFileStore.load(file);
        model.replaceAll(loaded);
        refresh(null);
        status.setText("Loaded " + loaded.size() + " tasks from " + file.getFileName());
    }

    private void sortTasks() {
        model.sortByDueDate();
        refresh(null);
        status.setText("Sorted by due date");
    }

    private void applyFilter() {
        refresh(null);
        status.setText("Showing " + shown.size() + " of " + model.getTasks().size() + " tasks");
    }

    private void clearFilter() {
        courseFilter.setText("");
        incompleteOnly.setSelected(false);
        applyFilter();
    }

    /** Copy the selected row into the form fields */
    private void fillSelection() {
        if (table.getSelectedRow() < 0)
            return;
        StudyTask task = selectedTask();
        type.setSelectedIndex(task instanceof AssignmentTask ? 0 : 1);
        title.setText(task.getTitle());
        course.setText(task.getCourse());
        date.setText(task.getDueDate().toString());
        details.setText(task.getDetails());
    }

    /**
     * Rebuild the table from the model using the current filter, update the
     * seven-day summary, and reselect a task if it is still shown.
     */
    private void refresh(StudyTask selected) {
        shown = model.getTasks(courseFilter.getText(), incompleteOnly.isSelected());
        rows.setRowCount(0);
        for (StudyTask task : shown)
            rows.addRow(new Object[] { task instanceof AssignmentTask ? "Assignment" : "Exam", task.getTitle(),
                    task.getCourse(), task.getDueDate(), task.isComplete() ? "Complete" : "Open" });
        summary.setText(model.dueSummary(LocalDate.now()));
        int index = selected == null ? -1 : shown.indexOf(selected); // List.copyOf lists reject null lookups
        if (index >= 0)
            table.setRowSelectionInterval(index, index);
    }
}
