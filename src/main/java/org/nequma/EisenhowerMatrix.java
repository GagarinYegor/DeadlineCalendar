package org.nequma;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

class EisenhowerMatrix extends JPanel {
    private List<Task> tasks;

    public EisenhowerMatrix() {
        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.WHITE);
        tasks = new ArrayList<>();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g2d.setColor(Color.BLACK);
        g2d.drawLine(width / 2, 0, width / 2, height);
        g2d.drawLine(0, height / 2, width, height / 2);

        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Важно и срочно", width / 4 - 50, 20);
        g2d.drawString("Важно и не срочно", 3 * width / 4 - 50, 20);
        g2d.drawString("Не важно, срочно", width / 4 - 50, height / 2 + 20);
        g2d.drawString("Не важно и не срочно", 3 * width / 4 - 50, height / 2 + 20);

        List<Task> quadrant1 = new ArrayList<>();
        List<Task> quadrant2 = new ArrayList<>();
        List<Task> quadrant3 = new ArrayList<>();
        List<Task> quadrant4 = new ArrayList<>();

        for (Task task : tasks) {
            if (task.isImportant() && task.isUrgent()) {
                quadrant1.add(task);
            } else if (task.isImportant() && !task.isUrgent()) {
                quadrant2.add(task);
            } else if (!task.isImportant() && task.isUrgent()) {
                quadrant3.add(task);
            } else {
                quadrant4.add(task);
            }
        }

        // Рисуем задачи в квадрантах
        drawTasksInQuadrant(g2d, quadrant1, 0, 0, width / 2, height / 2, Color.RED);
        drawTasksInQuadrant(g2d, quadrant2, width / 2, 0, width / 2, height / 2, Color.GREEN);
        drawTasksInQuadrant(g2d, quadrant3, 0, height / 2, width / 2, height / 2, Color.ORANGE);
        drawTasksInQuadrant(g2d, quadrant4, width / 2, height / 2, width / 2, height / 2, Color.GRAY);
    }

    private void drawTasksInQuadrant(Graphics2D g2d, List<Task> tasks, int x, int y, int width, int height, Color bgColor) {
        g2d.setColor(bgColor);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
        g2d.fillRect(x, y, width, height);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));

        int taskY = y + 40;
        for (Task task : tasks) {
            String taskText = task.getDefinition();
            if (taskText.length() > 20) {
                taskText = taskText.substring(0, 17) + "...";
            }

            // Добавляем индикатор дедлайна
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
            String dateStr = sdf.format(task.getEndDate().getTime());

            g2d.drawString(taskText + " (" + dateStr + ")", x + 10, taskY);
            taskY += 20;

            if (taskY > y + height - 10) break;
        }
    }
}