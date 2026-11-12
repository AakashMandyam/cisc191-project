# Study Task Planner

CISC 191, Aakash Mandyam, Fall 2026

Week 2 implements StudyTask, AssignmentTask, ExamTask, PlannerModel, PlannerWindow and PlannerApp. Seven JUnit tests cover validation, subtype details, add, edit, completion/reopening, remove, and protecting the list. Import the Eclipse project with Java 21 and JUnit 5. The src folder contains application code; test contains JUnit tests.

Run planner.PlannerApp as a Java Application for the Swing progress view. It starts empty and offers task-type, title, course, due-date and detail fields with Add, Save edit, Complete / reopen and Remove buttons. Tasks remain in memory and are lost when the application closes. The window opens and shows the input fields, buttons and an empty task table.

Run planner.PlannerDemo as a Java Application for the verified model demonstration using sample task data. It exercises adding mixed task types, completion, editing, reopening, removal, and rejected invalid updates.

Persistence, sorting, filtering and seven-day counts remain later milestones.

Repository baseline: https://github.com/AakashMandyam/cisc191-project at 415a0af7ad74684482ef449f8ef9af99920e5869
No open-source license has been selected.

## References

- Oracle Java SE 21 JFrame API: https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/JFrame.html
- Oracle Java SE 21 SwingUtilities API: https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/SwingUtilities.html
- Course checkpoint requirements: https://sdccd.instructure.com/courses/2505799/pages/project-checkpoints-read
