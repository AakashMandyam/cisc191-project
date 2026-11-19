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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads tasks as a simple tab-separated text file.
 *
 * <p>
 * The first line is a header. Each later line holds one task in this order:
 * type, title, course, due date, complete, details. Tabs, new lines and
 * backslashes inside a field are escaped so every task stays on one line.
 */
public final class TaskFileStore {
    private static final String HEADER = "# StudyTaskPlanner v1";
    private static final String ASSIGNMENT = "ASSIGNMENT";
    private static final String EXAM = "EXAM";

    private TaskFileStore() {
    }

    /**
     * Write the tasks to a file. The data goes to a temporary file first, so a
     * failed save does not damage an existing file.
     *
     * @param tasks the tasks to write
     * @param file  the destination file
     * @throws PlannerException if the file cannot be written
     */
    public static void save(List<StudyTask> tasks, Path file) throws PlannerException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (StudyTask task : tasks) {
            String type = task instanceof AssignmentTask ? ASSIGNMENT : EXAM;
            lines.add(String.join("\t", type, escape(task.getTitle()), escape(task.getCourse()),
                    task.getDueDate().toString(), String.valueOf(task.isComplete()), escape(task.getDetails())));
        }
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Files.write(temp, lines, StandardCharsets.UTF_8);
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException error) {
            throw new PlannerException("Could not save to " + file + ": " + error.getMessage(), error);
        }
    }

    /**
     * Read tasks from a file. Nothing is returned unless every line is valid.
     *
     * @param file the file to read
     * @return the tasks in file order
     * @throws PlannerException if the file cannot be read or a line is invalid
     */
    public static List<StudyTask> load(Path file) throws PlannerException {
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException error) {
            throw new PlannerException("Could not read " + file + ": " + error.getMessage(), error);
        }
        if (lines.isEmpty() || !lines.get(0).equals(HEADER)) {
            throw new PlannerException("This is not a Study Task Planner file (missing header)");
        }
        List<StudyTask> tasks = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                continue; // ignore empty lines
            }
            tasks.add(parseLine(lines.get(i), i + 1));
        }
        return tasks;
    }

    /** Turn one line into a task, reporting the line number on any problem. */
    private static StudyTask parseLine(String line, int lineNumber) throws PlannerException {
        String[] parts = line.split("\t", -1);
        if (parts.length != 6) {
            throw new PlannerException("Line " + lineNumber + ": expected 6 fields but found " + parts.length);
        }
        try {
            String title = unescape(parts[1]);
            String course = unescape(parts[2]);
            LocalDate due = LocalDate.parse(parts[3]);
            String details = unescape(parts[5]);
            StudyTask task;
            if (parts[0].equals(ASSIGNMENT)) {
                task = new AssignmentTask(title, course, due, details);
            } else if (parts[0].equals(EXAM)) {
                task = new ExamTask(title, course, due, details);
            } else {
                throw new PlannerException("Line " + lineNumber + ": unknown task type '" + parts[0] + "'");
            }
            if (!parts[4].equals("true") && !parts[4].equals("false")) {
                throw new PlannerException("Line " + lineNumber + ": complete must be true or false");
            }
            task.setComplete(Boolean.parseBoolean(parts[4]));
            return task;
        } catch (DateTimeParseException error) {
            throw new PlannerException("Line " + lineNumber + ": invalid date '" + parts[3] + "'", error);
        } catch (IllegalArgumentException | NullPointerException error) {
            // The task constructors reject blank values
            throw new PlannerException("Line " + lineNumber + ": " + error.getMessage(), error);
        }
    }

    /** Replace special characters with escape sequences. */
    static String escape(String text) {
        return text.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r");
    }

    /** Reverse {@link #escape(String)}; rejects unknown escape sequences. */
    static String unescape(String text) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c != '\\') {
                out.append(c);
                continue;
            }
            i++;
            if (i >= text.length()) {
                throw new IllegalArgumentException("Text ends with a lone backslash");
            }
            switch (text.charAt(i)) {
            case '\\' -> out.append('\\');
            case 't' -> out.append('\t');
            case 'n' -> out.append('\n');
            case 'r' -> out.append('\r');
            default -> throw new IllegalArgumentException("Unknown escape \\" + text.charAt(i));
            }
        }
        return out.toString();
    }
}
