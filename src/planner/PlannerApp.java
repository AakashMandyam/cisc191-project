package planner;

import javax.swing.SwingUtilities;

/** Launch the Week 2 Swing progress application */
public final class PlannerApp {
    private PlannerApp() { }

    /** Start an empty planner on the Swing event thread */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PlannerWindow(new PlannerModel()).setVisible(true));
    }
}
