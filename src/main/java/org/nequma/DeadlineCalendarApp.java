package org.nequma;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

public class DeadlineCalendarApp extends JFrame {
    private TaskManager taskManager;
    private DefaultListModel<String> activeTasksModel;
    private DefaultListModel<String> completedTasksModel;
    private JList<String> activeTasksList;
    private JList<String> completedTasksList;
    private GanttChart ganttChart;
    private EisenhowerMatrix eisenhowerMatrix;

    public DeadlineCalendarApp(){
        taskManager = new TaskManager("data");
        ganttChart = new GanttChart(taskManager.getActiveTasks());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Календарь Дедлайнов");
        initComponents();
        loadTasks();
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        activeTasksModel = new DefaultListModel<>();
        completedTasksModel = new DefaultListModel<>();

        ganttChart = new GanttChart(this.taskManager.getActiveTasks());
        eisenhowerMatrix = new EisenhowerMatrix();

        JSplitPane diagramsSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        JPanel ganttContainer = new JPanel(new BorderLayout());
        ganttContainer.setBorder(BorderFactory.createTitledBorder("Диаграмма Гантта"));
        ganttContainer.add(ganttChart, BorderLayout.CENTER);
        ganttContainer.add(ganttChart.getHorizontalScroller(), BorderLayout.SOUTH);
        ganttContainer.add(ganttChart.getVerticalScroller(), BorderLayout.EAST);

        JPanel eisenhowerContainer = new JPanel(new BorderLayout());
        eisenhowerContainer.setBorder(BorderFactory.createTitledBorder("Матрица Эйзенхауэра"));
        eisenhowerContainer.add(eisenhowerMatrix, BorderLayout.CENTER);

        diagramsSplitPane.setTopComponent(ganttContainer);
        diagramsSplitPane.setBottomComponent(eisenhowerContainer);
        diagramsSplitPane.setDividerLocation(300);

        JPanel tasksPanel = createTasksPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tasksPanel, diagramsSplitPane);
        splitPane.setDividerLocation(353);
        add(splitPane);
    }

    private JPanel createTasksPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel activePanel = createTaskListPanel("Активные задачи", activeTasksModel,
                this::addTask, this::editActiveTask, this::completeTask, false);
        activeTasksList = (JList<String>) ((JScrollPane) activePanel.getComponent(1)).getViewport().getView();

        JPanel completedPanel = createTaskListPanel("Завершенные задачи", completedTasksModel,
                null, null, this::deleteCompletedTask, true);
        completedTasksList = (JList<String>) ((JScrollPane) completedPanel.getComponent(1)).getViewport().getView();

        panel.add(activePanel);
        panel.add(completedPanel);

        return panel;
    }

    private JPanel createTaskListPanel(String title, DefaultListModel<String> model,
                                       Runnable addAction, Runnable editAction, Runnable deleteAction,
                                       boolean isCompleted) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new LineBorder(Color.GRAY, 1));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        JList<String> taskList = new JList<>(model);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(taskList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        if (addAction != null) {
            JButton addButton = new JButton("Добавить");
            addButton.addActionListener(e -> addAction.run());
            buttonPanel.add(addButton);
        }

        if (editAction != null) {
            JButton editButton = new JButton("Редактировать");
            editButton.addActionListener(e -> {
                if (taskList.getSelectedIndex() != -1) {
                    editAction.run();
                }
            });
            buttonPanel.add(editButton);
        }

        if (deleteAction != null) {
            JButton deleteButton = new JButton(isCompleted ? "Удалить" : "Завершить");
            deleteButton.addActionListener(e -> {
                if (taskList.getSelectedIndex() != -1) {
                    deleteAction.run();
                }
            });
            buttonPanel.add(deleteButton);
        }

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadTasks() {
        activeTasksModel.clear();
        completedTasksModel.clear();

        for (Task task : taskManager.getActiveTasks()) {
            activeTasksModel.addElement(formatTaskString(task));
        }

        for (Task task : taskManager.getCompletedTasks()) {
            completedTasksModel.addElement(formatTaskString(task));
        }
        updateDiagrams();
    }

    private String formatTaskString(Task task) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String status = task.isImportant() ? "* " : "";
        return status + task.getName() + " (до " + sdf.format(task.getEndDate().getTime()) + ")";
    }

    private void addTask() {
        TaskDialog dialog = new TaskDialog(this, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Task task = dialog.getTask();
            taskManager.addTask(task);
            activeTasksModel.addElement(formatTaskString(task));
            updateDiagrams();
        }
    }

    private void editActiveTask() {
        int index = activeTasksList.getSelectedIndex();
        if (index != -1) {
            Task task = taskManager.getActiveTasks().get(index);
            TaskDialog dialog = new TaskDialog(this, task);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                activeTasksModel.set(index, formatTaskString(task));
                taskManager.saveTask(task, taskManager.getActiveFolder());
                updateDiagrams();
            }
        }
    }

    private void completeTask() {
        int index = activeTasksList.getSelectedIndex();
        if (index != -1) {
            Task task = taskManager.getActiveTasks().get(index);
            taskManager.completeTask(task);
            activeTasksModel.remove(index);
            completedTasksModel.addElement(formatTaskString(task));
            updateDiagrams();
        }
    }

    private void deleteCompletedTask() {
        int index = completedTasksList.getSelectedIndex();
        if (index != -1) {
            Task task = taskManager.getCompletedTasks().get(index);
            taskManager.deleteTask(task);
            completedTasksModel.remove(index);
        }
    }

    private void updateDiagrams() {
        ganttChart.setTasks(taskManager.getActiveTasks());
        eisenhowerMatrix.setTasks(taskManager.getActiveTasks());
    }

    public static void main(String[] args) {
        DeadlineCalendarApp app = new DeadlineCalendarApp();
        app.setVisible(true);
    }
}