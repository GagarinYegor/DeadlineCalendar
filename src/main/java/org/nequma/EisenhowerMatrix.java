package org.nequma;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

class EisenhowerMatrix extends JPanel {
    private List<Task> taskList;
    private JPanel quadrant1Panel;
    private JPanel quadrant2Panel;
    private JPanel quadrant3Panel;
    private JPanel quadrant4Panel;

    private JLabel quadrant1Label;
    private JLabel quadrant2Label;
    private JLabel quadrant3Label;
    private JLabel quadrant4Label;

    public EisenhowerMatrix() {
        setLayout(new GridLayout(2, 2, 2, 2));
        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.WHITE);
        taskList = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        JPanel q1Container = new JPanel(new BorderLayout(0, 5));
        q1Container.setBorder(new LineBorder(Color.RED, 2));

        quadrant1Label = new JLabel("Важно и срочно", SwingConstants.CENTER);
        quadrant1Label.setFont(new Font("Arial", Font.BOLD, 14));
        quadrant1Label.setForeground(Color.RED);
        quadrant1Label.setOpaque(true);
        quadrant1Label.setBackground(new Color(255, 230, 230));
        quadrant1Label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        q1Container.add(quadrant1Label, BorderLayout.NORTH);

        quadrant1Panel = new JPanel();
        quadrant1Panel.setLayout(new BoxLayout(quadrant1Panel, BoxLayout.Y_AXIS));
        quadrant1Panel.setBackground(new Color(255, 240, 240));

        JScrollPane q1ScrollPane = new JScrollPane(quadrant1Panel);
        q1ScrollPane.setBorder(null);
        q1ScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        q1ScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        q1Container.add(q1ScrollPane, BorderLayout.CENTER);

        JPanel q2Container = new JPanel(new BorderLayout(0, 5));
        q2Container.setBorder(new LineBorder(Color.GREEN, 2));

        quadrant2Label = new JLabel("Важно и не срочно", SwingConstants.CENTER);
        quadrant2Label.setFont(new Font("Arial", Font.BOLD, 14));
        quadrant2Label.setForeground(new Color(0, 150, 0));
        quadrant2Label.setOpaque(true);
        quadrant2Label.setBackground(new Color(230, 255, 230));
        quadrant2Label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        q2Container.add(quadrant2Label, BorderLayout.NORTH);

        quadrant2Panel = new JPanel();
        quadrant2Panel.setLayout(new BoxLayout(quadrant2Panel, BoxLayout.Y_AXIS));
        quadrant2Panel.setBackground(new Color(240, 255, 240));

        JScrollPane q2ScrollPane = new JScrollPane(quadrant2Panel);
        q2ScrollPane.setBorder(null);
        q2ScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        q2ScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        q2Container.add(q2ScrollPane, BorderLayout.CENTER);

        JPanel q3Container = new JPanel(new BorderLayout(0, 5));
        q3Container.setBorder(new LineBorder(Color.ORANGE, 2));

        quadrant3Label = new JLabel("Не важно и срочно", SwingConstants.CENTER);
        quadrant3Label.setFont(new Font("Arial", Font.BOLD, 14));
        quadrant3Label.setForeground(new Color(200, 100, 0));
        quadrant3Label.setOpaque(true);
        quadrant3Label.setBackground(new Color(255, 245, 230));
        quadrant3Label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        q3Container.add(quadrant3Label, BorderLayout.NORTH);

        quadrant3Panel = new JPanel();
        quadrant3Panel.setLayout(new BoxLayout(quadrant3Panel, BoxLayout.Y_AXIS));
        quadrant3Panel.setBackground(new Color(255, 250, 240));

        JScrollPane q3ScrollPane = new JScrollPane(quadrant3Panel);
        q3ScrollPane.setBorder(null);
        q3ScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        q3ScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        q3Container.add(q3ScrollPane, BorderLayout.CENTER);

        JPanel q4Container = new JPanel(new BorderLayout(0, 5));
        q4Container.setBorder(new LineBorder(Color.GRAY, 2));

        quadrant4Label = new JLabel("Не важно и не срочно", SwingConstants.CENTER);
        quadrant4Label.setFont(new Font("Arial", Font.BOLD, 14));
        quadrant4Label.setForeground(Color.DARK_GRAY);
        quadrant4Label.setOpaque(true);
        quadrant4Label.setBackground(new Color(240, 240, 240));
        quadrant4Label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        q4Container.add(quadrant4Label, BorderLayout.NORTH);

        quadrant4Panel = new JPanel();
        quadrant4Panel.setLayout(new BoxLayout(quadrant4Panel, BoxLayout.Y_AXIS));
        quadrant4Panel.setBackground(new Color(245, 245, 245));

        JScrollPane q4ScrollPane = new JScrollPane(quadrant4Panel);
        q4ScrollPane.setBorder(null);
        q4ScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        q4ScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        q4Container.add(q4ScrollPane, BorderLayout.CENTER);

        add(q1Container);
        add(q2Container);
        add(q3Container);
        add(q4Container);
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        quadrant1Panel.removeAll();
        quadrant2Panel.removeAll();
        quadrant3Panel.removeAll();
        quadrant4Panel.removeAll();

        List<Task> quadrant1Tasks = new ArrayList<>();
        List<Task> quadrant2Tasks = new ArrayList<>();
        List<Task> quadrant3Tasks = new ArrayList<>();
        List<Task> quadrant4Tasks = new ArrayList<>();

        for (Task task : taskList) {
            if (task.getImportant() && task.isUrgent()) {
                quadrant1Tasks.add(task);
            } else if (task.getImportant() && !task.isUrgent()) {
                quadrant2Tasks.add(task);
            } else if (!task.getImportant() && task.isUrgent()) {
                quadrant3Tasks.add(task);
            } else {
                quadrant4Tasks.add(task);
            }
        }

        updateQuadrantLabel(quadrant1Label, "Важно и срочно", quadrant1Tasks.size(), Color.RED);
        updateQuadrantLabel(quadrant2Label, "Важно и не срочно", quadrant2Tasks.size(), Color.GREEN);
        updateQuadrantLabel(quadrant3Label, "Не важно и срочно", quadrant3Tasks.size(), Color.ORANGE);
        updateQuadrantLabel(quadrant4Label, "Не важно и не срочно", quadrant4Tasks.size(), Color.GRAY);

        addTasksToQuadrant(quadrant1Panel, quadrant1Tasks, Color.RED);
        addTasksToQuadrant(quadrant2Panel, quadrant2Tasks, Color.GREEN);
        addTasksToQuadrant(quadrant3Panel, quadrant3Tasks, Color.ORANGE);
        addTasksToQuadrant(quadrant4Panel, quadrant4Tasks, Color.GRAY);

        revalidate();
        repaint();
    }

    private void updateQuadrantLabel(JLabel label, String title, int count, Color color) {
        String countText = count > 0 ? " (" + count + ")" : "";
        label.setText(title + countText);
        label.setForeground(color);
    }

    private void addTasksToQuadrant(JPanel panel, List<Task> taskListLocal, Color borderColor) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        for (Task task : taskListLocal) {
            JPanel taskPanel = new JPanel(new BorderLayout(5, 5));
            taskPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            taskPanel.setBackground(Color.WHITE);
            taskPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

            JLabel nameLabel = new JLabel(task.getName());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
            taskPanel.add(nameLabel, BorderLayout.NORTH);

            if (task.getDefinition() != null && !task.getDefinition().isEmpty()) {
                JTextArea descArea = new JTextArea(task.getDefinition());
                descArea.setEditable(false);
                descArea.setLineWrap(true);
                descArea.setWrapStyleWord(true);
                descArea.setBackground(Color.WHITE);
                descArea.setFont(new Font("Arial", Font.PLAIN, 11));
                descArea.setRows(2);

                JScrollPane descScroll = new JScrollPane(descArea);
                descScroll.setBorder(null);
                descScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                taskPanel.add(descScroll, BorderLayout.CENTER);
            }

            JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            datePanel.setBackground(Color.WHITE);

            JLabel startLabel = new JLabel("С: " + sdf.format(task.getStartDate().getTime()));
            startLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            startLabel.setForeground(Color.GRAY);
            datePanel.add(startLabel);

            JLabel endLabel = new JLabel("До: " + sdf.format(task.getEndDate().getTime()));
            endLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            endLabel.setForeground(Color.GRAY);
            datePanel.add(endLabel);

            taskPanel.add(datePanel, BorderLayout.SOUTH);

            panel.add(taskPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        panel.add(Box.createVerticalGlue());
    }
}