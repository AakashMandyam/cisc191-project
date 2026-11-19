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

import javax.swing.SwingUtilities;

/** Launch the Week 3 Swing planner application */
public final class PlannerApp {
    private PlannerApp() { }

    /** Start an empty planner on the Swing event thread */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PlannerWindow(new PlannerModel()).setVisible(true));
    }
}
