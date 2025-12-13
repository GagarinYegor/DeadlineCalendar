package org.nequma;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class GanttChart extends JPanel {
    private List<Task> taskList;
    private JScrollBar horizontalScroller;
    private JScrollBar verticalScroller;
    private int daysToShow = 30;
    private int dayWidth = 40;
    private int taskHeight = 30;

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        setBackground(Color.white);

        if(taskList.isEmpty()){return;}

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int startDay = horizontalScroller.getValue() / dayWidth;
        int startTask = verticalScroller.getValue() / taskHeight;
        int visibleDays = daysToShow;

        drawGridAndTasks(g2d, startDay, startTask, visibleDays);
    }

    public GanttChart(List<Task> taskList) {
        this.taskList = taskList;
        this.horizontalScroller = new JScrollBar(JScrollBar.HORIZONTAL, 0, dayWidth, 0, daysToShow * dayWidth);
        this.verticalScroller = new JScrollBar(JScrollBar.VERTICAL, 0, taskHeight, 0, taskList.size()* taskHeight + taskHeight);
        this.horizontalScroller.addAdjustmentListener(e -> repaint());
        this.verticalScroller.addAdjustmentListener(e -> repaint());
    }

    public JScrollBar getHorizontalScroller() {
        return this.horizontalScroller;
    }

    public JScrollBar getVerticalScroller() {
        return this.verticalScroller;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        repaint();
    }

    private void drawGridAndTasks(Graphics2D g2d, int startDay, int startTask, int visibleDays) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, startDay);

        g2d.setColor(new Color(0, 150, 100));
        g2d.fillRect(0, 0, visibleDays * dayWidth, taskHeight);

        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawLine(0, taskHeight, getWidth(), taskHeight);

        for (int i = 0; i <= visibleDays; i++) {
            int x = i * dayWidth;
            g2d.setColor(Color.GRAY);
            g2d.drawLine(x, 0, x, taskHeight);

            g2d.setColor(Color.WHITE);
            String dateStr = sdf.format(cal.getTime());
            g2d.drawString(dateStr, x + 5, 15);

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        for (int i = 0; i <= visibleDays; i++) {
            int x = i * dayWidth;
            g2d.setColor(Color.GRAY);
            g2d.drawLine(x, taskHeight, x, getHeight());
        }

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar chartStartDate = Calendar.getInstance();
        chartStartDate.add(Calendar.DAY_OF_MONTH, startDay);
        chartStartDate.set(Calendar.HOUR_OF_DAY, 0);
        chartStartDate.set(Calendar.MINUTE, 0);
        chartStartDate.set(Calendar.SECOND, 0);
        chartStartDate.set(Calendar.MILLISECOND, 0);
        int y = taskHeight - taskHeight * startTask;

        for (Task task : taskList) {
            g2d.setColor(Color.GRAY);
            g2d.drawLine(0, y, visibleDays * dayWidth, y);

            if (y < taskHeight || y > getHeight()) {
                y += taskHeight;
                continue;
            }
            g2d.setColor(Color.GRAY);
            g2d.drawLine(0, y, visibleDays * dayWidth, y);

            boolean isOverdue = task.getEndDate().before(today);

            long diffStart = task.getStartDate().getTimeInMillis() - chartStartDate.getTimeInMillis();
            long diffEnd = task.getEndDate().getTimeInMillis() - chartStartDate.getTimeInMillis();

            int taskStartDay = (int) (diffStart / (1000 * 60 * 60 * 24));
            int taskEndDay = (int) (diffEnd / (1000 * 60 * 60 * 24));

            int visibleStartX;
            int visibleEndX;
            Color taskColor;

            if (isOverdue) {
                visibleStartX = 0;
                visibleEndX = visibleDays - 1;
                taskColor = new Color(220, 50, 50);
            } else {
                if (taskEndDay < 0 || taskStartDay >= visibleDays) {
                    y += taskHeight;
                    continue;
                }
                visibleStartX = Math.max(taskStartDay, 0);
                visibleEndX = Math.min(taskEndDay, visibleDays - 1);
                taskColor = task.getColor();
            }

            int visibleWidth = Math.max((visibleEndX - visibleStartX + 1) * dayWidth, 5);

            g2d.setColor(taskColor);
            g2d.fillRect(visibleStartX * dayWidth, y, visibleWidth, taskHeight);

            g2d.setColor(Color.BLACK);
            if (visibleWidth > 20) {
                String taskName = task.getName();
                if (isOverdue) {
                    taskName = "Просрочено: " + taskName;
                }
                int textStartX = visibleStartX * dayWidth;
                if (!isOverdue && taskStartDay < 0) {
                    textStartX = 0;
                }

                FontMetrics fm = g2d.getFontMetrics();
                int maxTextWidth = visibleWidth - 10;
                int textWidth = fm.stringWidth(taskName);

                if (textWidth > maxTextWidth) {
                    while (textWidth > maxTextWidth && taskName.length() > 3) {
                        taskName = taskName.substring(0, taskName.length() - 1);
                        textWidth = fm.stringWidth(taskName + "...");
                    }
                    taskName = taskName + "...";
                }

                g2d.drawString(taskName, textStartX + 5, y + taskHeight / 2 + 4);
            }
            y += taskHeight;
        }

        long diffToday = today.getTimeInMillis() - chartStartDate.getTimeInMillis();
        int todayX = (int) (diffToday / (1000 * 60 * 60 * 24));
        if (todayX >= 0 && todayX < visibleDays) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 9));
            g2d.drawString("Сегодня", todayX * dayWidth + 2, taskHeight - 5);
        }
    }
}