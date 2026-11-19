# Study Task Planner

CISC 191, Aakash Mandyam, Fall 2026

Week 3 builds on the Week 2 model and window (StudyTask, AssignmentTask, ExamTask, PlannerModel, PlannerWindow, PlannerApp) and adds:

- **Save and load:** `TaskFileStore` writes and reads tasks as a tab-separated text file using java.nio.file. Special characters are escaped so each task stays on one line. A save goes to a temporary file first so a failed save does not damage the old file. A load checks the whole file before the list is replaced.
- **Exception handling:** `PlannerException` is a checked exception for missing, unreadable or damaged files (the message includes the line number). The window shows these in a dialog and leaves the task list unchanged instead of crashing.
- **Sorting:** `PlannerModel.sortByDueDate()` sorts by due date, then title.
- **Filtering:** `PlannerModel.getTasks(course, incompleteOnly)` returns matching tasks without changing the stored list. The window has a course filter box and an "Incomplete only" check box.
- **Seven-day summary:** `countDueWithin`, `countOverdue` and `dueSummary` count incomplete tasks due in the next seven days and overdue tasks; the window shows the line under the table.
- **Tests:** 16 JUnit 5 tests (7 from Week 2 in `PlannerModelTest`, 9 new in `PlannerFeaturesTest`) cover sorting, filtering, the seven-day window edges, round-trip save/load, and damaged or missing files.

## How to run

Import the Eclipse project with Java 21 and JUnit 5. The src folder contains application code; test contains JUnit tests.

- Run `planner.PlannerApp` as a Java Application for the Swing planner. It starts empty. Use Add / Save edit / Complete / Remove for tasks, "Save to file..." and "Load from file..." for persistence, "Sort by due date", and the course filter / "Incomplete only" controls.
- Run `planner.PlannerDemo` as a Java Application to print a demonstration of the model, sorting, filtering, the seven-day summary, and saving/loading with a good and a damaged file.
- Run `planner.PlannerModelTest` and `planner.PlannerFeaturesTest` as JUnit 5 tests.

Later milestones: more input-handling testing and layout refinement.

Week 3 commit: pending, to be pushed by the student.
Repository baseline: https://github.com/AakashMandyam/cisc191-project at 415a0af7ad74684482ef449f8ef9af99920e5869
No open-source license has been selected.

## References

- Oracle Java SE 21 JFrame API: https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/JFrame.html
- Oracle Java SE 21 SwingUtilities API: https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/SwingUtilities.html
- Oracle Java SE 21 java.nio.file.Files API: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Files.html
- Oracle Java SE 21 JFileChooser API: https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/JFileChooser.html
- Oracle Java SE 21 Comparator API: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Comparator.html
- JUnit 5 User Guide (@TempDir): https://junit.org/junit5/docs/current/user-guide/
- Course checkpoint requirements: https://sdccd.instructure.com/courses/2505799/pages/project-checkpoints-read
