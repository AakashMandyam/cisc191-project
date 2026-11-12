package planner;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Display and edit the current planner model */
public final class PlannerWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private final PlannerModel model;
    private final JComboBox<String> type = new JComboBox<>(new String[]{"Assignment", "Exam"});
    private final JTextField title = new JTextField();
    private final JTextField course = new JTextField();
    private final JTextField date = new JTextField(LocalDate.now().toString());
    private final JTextField details = new JTextField();
    private final DefaultTableModel rows = new DefaultTableModel(
            new String[]{"Type", "Title", "Course", "Due date", "Status"}, 0) {
        private static final long serialVersionUID = 1L;
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(rows);
    private final JLabel status = new JLabel("Week 2 progress — tasks remain in memory only");

    /** Connect a Swing view to an existing model */
    public PlannerWindow(PlannerModel model) {
        super("Study Task Planner — Week 2");
        this.model = model;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 520);
        setLocationByPlatform(true);
        JPanel form = new JPanel(new GridLayout(2, 5, 8, 5));
        for (String label : new String[]{"Task type", "Title", "Course", "Due date (YYYY-MM-DD)", "Requirements / topics"})
            form.add(new JLabel(label));
        for (JComponent field : new JComponent[]{type, title, course, date, details}) form.add(field);
        JPanel buttons = new JPanel();
        addButton(buttons, "Add", this::addTask);
        addButton(buttons, "Save edit", this::editTask);
        addButton(buttons, "Complete / reopen", this::toggleTask);
        addButton(buttons, "Remove", this::removeTask);
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        content.add(top, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(status, BorderLayout.SOUTH);
        setContentPane(content);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) fillSelection();
        });
        refresh(-1);
    }

    private void addButton(JPanel panel, String label, Runnable action) {
        JButton button = new JButton(label);
        button.addActionListener(event -> {
            try { action.run(); }
            catch (IllegalArgumentException | IllegalStateException error) {
                JOptionPane.showMessageDialog(this, error.getMessage(), "Check task", JOptionPane.WARNING_MESSAGE);
            }
        });
        panel.add(button);
    }

    private StudyTask inputTask() {
        LocalDate due;
        try { due = LocalDate.parse(date.getText().strip()); }
        catch (java.time.format.DateTimeParseException error) {
            throw new IllegalArgumentException("Enter a valid due date in YYYY-MM-DD format");
        }
        return type.getSelectedIndex() == 0
                ? new AssignmentTask(title.getText(), course.getText(), due, details.getText())
                : new ExamTask(title.getText(), course.getText(), due, details.getText());
    }

    private StudyTask selectedTask() {
        int index = table.getSelectedRow();
        if (index < 0) throw new IllegalStateException("Select a task first");
        return model.getTasks().get(index);
    }

    private void addTask() {
        model.addTask(inputTask());
        refresh(model.getTasks().size() - 1);
        status.setText("Task added — not saved to a file");
    }

    private void editTask() {
        int index = table.getSelectedRow();
        model.editTask(selectedTask(), inputTask());
        refresh(index);
        status.setText("Task edited; completion state retained");
    }

    private void toggleTask() {
        int index = table.getSelectedRow();
        model.toggleComplete(selectedTask());
        refresh(index);
        status.setText("Completion state updated");
    }

    private void removeTask() {
        model.removeTask(selectedTask());
        refresh(-1);
        status.setText("Selected task removed");
    }

    private void fillSelection() {
        if (table.getSelectedRow() < 0) return;
        StudyTask task = selectedTask();
        type.setSelectedIndex(task instanceof AssignmentTask ? 0 : 1);
        title.setText(task.getTitle());
        course.setText(task.getCourse());
        date.setText(task.getDueDate().toString());
        details.setText(task.getDetails());
    }

    private void refresh(int selected) {
        rows.setRowCount(0);
        for (StudyTask task : model.getTasks())
            rows.addRow(new Object[]{task instanceof AssignmentTask ? "Assignment" : "Exam", task.getTitle(),
                    task.getCourse(), task.getDueDate(), task.isComplete() ? "Complete" : "Open"});
        if (selected >= 0 && selected < rows.getRowCount()) table.setRowSelectionInterval(selected, selected);
    }
}
