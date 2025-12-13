package org.nequma;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

public class DeadlineCalendarApp extends JFrame {
    private TaskManager taskManager;
    private DefaultListModel<String> activeTaskModel;
    private DefaultListModel<String> completedTaskModel;
    private JList<String> activeTaskList;
    private JList<String> completedTaskList;
    private GanttChart ganttChart;
    private EisenhowerMatrix eisenhowerMatrix;

    public DeadlineCalendarApp(){
        taskManager = new TaskManager("data");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Календарь Дедлайнов");
        initComponents();
        loadTask();
        setSize(800, 600);
        Image icon = Toolkit.getDefaultToolkit().getImage("src/main/java/org/nequma/icon.png");
        setPreferredSize(new Dimension(800, 600));
        setIconImage(icon);
        setVisible(true);
    }

    private void initComponents() {
        activeTaskModel = new DefaultListModel<>();
        completedTaskModel = new DefaultListModel<>();

        activeTaskList = new JList<>(activeTaskModel);
        completedTaskList = new JList<>(completedTaskModel);

        activeTaskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        completedTaskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ganttChart = new GanttChart(this.taskManager.getActiveTask());
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

        JPanel tasksPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        tasksPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel activePanel = createTaskListPanel("Активные задачи", activeTaskList,
                this::addTask, this::editTask, null, this::completeTask, null);

        JPanel completedPanel = createTaskListPanel("Завершенные задачи", completedTaskList,
                null, null, this::uncompleteTask, null, this::deleteCompletedTask);

        tasksPanel.add(activePanel);
        tasksPanel.add(completedPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tasksPanel, diagramsSplitPane);
        splitPane.setDividerLocation(310);
        add(splitPane);
    }

    private JPanel createTaskListPanel(String title, JList<String> taskList,
                                       Runnable addAction,
                                       Runnable editAction,
                                       Runnable activateAction,
                                       Runnable deactivateAction,
                                       Runnable deleteAction) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new LineBorder(Color.GRAY, 1));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

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
                else {
                    JOptionPane.showMessageDialog(this,
                            "Выберите задачу для редактирования",
                            "Внимание",
                            JOptionPane.WARNING_MESSAGE);
                }
            });
            buttonPanel.add(editButton);
        }

        if (activateAction != null){
            JButton activateButton = new JButton("Сделать активной");
            activateButton.addActionListener(e -> {
                if (taskList.getSelectedIndex() != -1) {
                    activateAction.run();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Выберите задачу для установки активной",
                            "Внимание",
                            JOptionPane.WARNING_MESSAGE);
                }
            });
            buttonPanel.add(activateButton);
        }

        if(deactivateAction != null){
            JButton deactivateButton = new JButton("Завершить");
            deactivateButton.addActionListener(e -> {
                if (taskList.getSelectedIndex() != -1) {
                    deactivateAction.run();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Выберите задачу для завершения",
                            "Внимание",
                            JOptionPane.WARNING_MESSAGE);
                }
            });
            buttonPanel.add(deactivateButton);
        }

        if (deleteAction != null) {
            JButton deleteButton = new JButton("Удалить");
            deleteButton.addActionListener(e -> {
                if (taskList.getSelectedIndex() != -1) {
                    deleteAction.run();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Выберите задачу для удаления",
                            "Внимание",
                            JOptionPane.WARNING_MESSAGE);
                }
            });
            buttonPanel.add(deleteButton);
        }

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadTask() {
        activeTaskModel.clear();
        completedTaskModel.clear();

        for (Task task : taskManager.getActiveTask()) {
            activeTaskModel.addElement(formatTaskToString(task));
        }

        for (Task task : taskManager.getCompletedTask()) {
            completedTaskModel.addElement(formatTaskToString(task));
        }

        activeTaskList.setModel(activeTaskModel);
        completedTaskList.setModel(completedTaskModel);

        updateDiagrams();
    }

    private String formatTaskToString(Task task) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String status = task.getImportant() ? "❗ " : "";
        String taskName = task.getName() != null ? task.getName() : "Без названия";
        return status + taskName + " (до " + sdf.format(task.getEndDate().getTime()) + ")";
    }

    private void addTask() {
        TaskDialog dialog = new TaskDialog(this, null);
        dialog.setVisible(true);

        if (dialog.getConfirmed()) {
            Task task = dialog.getTask();
            taskManager.addActiveTask(task);
            activeTaskModel.addElement(formatTaskToString(task));
            updateDiagrams();
            activeTaskList.clearSelection();
        }
    }

    private void editTask() {
        int index = activeTaskList.getSelectedIndex();
        if (index >= 0 && index < taskManager.getActiveTask().size()) {
            Task task = taskManager.getActiveTask().get(index);
            TaskDialog dialog = new TaskDialog(this, task);
            dialog.setVisible(true);

            if (dialog.getConfirmed()) {
                activeTaskModel.set(index, formatTaskToString(task));
                taskManager.saveTask(task);
                updateDiagrams();
                activeTaskList.clearSelection();
            }
        }
    }

    private void completeTask() {
        int index = activeTaskList.getSelectedIndex();
        if (index >= 0 && index < taskManager.getActiveTask().size()) {
            Task task = taskManager.getActiveTask().get(index);
            taskManager.completeTask(task);
            activeTaskModel.remove(index);
            completedTaskModel.addElement(formatTaskToString(task));
            updateDiagrams();
            activeTaskList.clearSelection();
        }
    }

    private void uncompleteTask() {
        int index = completedTaskList.getSelectedIndex();
        if (index >= 0 && index < taskManager.getCompletedTask().size()) {
            Task task = taskManager.getCompletedTask().get(index);
            taskManager.unCompleteTask(task);
            completedTaskModel.remove(index);
            activeTaskModel.addElement(formatTaskToString(task));
            updateDiagrams();
            completedTaskList.clearSelection();
        }
    }

    private void deleteCompletedTask() {
        int index = completedTaskList.getSelectedIndex();
        if (index >= 0 && index < taskManager.getCompletedTask().size()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Вы уверены, что хотите удалить задачу?",
                    "Подтверждение удаления",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Task task = taskManager.getCompletedTask().get(index);
                taskManager.deleteTask(task);
                completedTaskModel.remove(index);
                completedTaskList.clearSelection();
            }
        }
    }

    private void updateDiagrams() {
        ganttChart.setTaskList(taskManager.getActiveTask());
        eisenhowerMatrix.setTaskList(taskManager.getActiveTask());
        ganttChart.repaint();
        eisenhowerMatrix.repaint();
    }

    public static void main() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            DeadlineCalendarApp app = new DeadlineCalendarApp();
            app.setVisible(true);
        });
    }
}