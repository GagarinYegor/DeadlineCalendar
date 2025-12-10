package org.nequma;
import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class GanttChart extends JPanel implements AdjustmentListener{
    private List<Task> tasks;
    private JScrollBar horizontalScroller;
    private JScrollBar verticalScroller;
    private static final int DAYS_TO_SHOW = 30;
    private static final int DAY_WIDTH = 40;
    private static final int TASK_HEIGHT = 30;

    public GanttChart(List<Task> tasks) {
        this.tasks = tasks;
        horizontalScroller = new JScrollBar(JScrollBar.HORIZONTAL, 0, DAY_WIDTH, 0, DAYS_TO_SHOW*DAY_WIDTH);
        verticalScroller = new JScrollBar(JScrollBar.VERTICAL, 0, TASK_HEIGHT, 0, tasks.size()*TASK_HEIGHT+TASK_HEIGHT);
        horizontalScroller.addAdjustmentListener(this);
        verticalScroller.addAdjustmentListener(this);
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        repaint();
    }

    public JScrollBar getHorizontalScroller() {
        return this.horizontalScroller;
    }

    public JScrollBar getVerticalScroller() {
        return this.verticalScroller;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        repaint();
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        setBackground(Color.white);

        if(tasks.isEmpty()){return;}

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int startDay = horizontalScroller.getValue() / 10;
        int startTask = verticalScroller.getValue() / 10;
        int visibleDays = DAYS_TO_SHOW;

        drawGrid(g2d, startDay, startTask, visibleDays);
        drawTasks(g2d, startDay, startTask);
    }

    private void drawGrid(Graphics2D g2d, int startDay, int startTask, int visibleDays) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, startDay);

        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawLine(0, TASK_HEIGHT, getWidth(), TASK_HEIGHT);

        for (int i = 0; i <= visibleDays; i++) {
            int x = i * DAY_WIDTH;
            g2d.drawLine(x, 0, x, getHeight());

            String dateStr = sdf.format(cal.getTime());
            g2d.drawString(dateStr, x + 5, 15);

            Calendar today = Calendar.getInstance();
            if (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                g2d.setColor(Color.RED);
                g2d.fillRect(x, 0, DAY_WIDTH, 20);
                g2d.setColor(Color.LIGHT_GRAY);
            }

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        for (int i = 0; i <= tasks.size(); i++) {
            int y = i * TASK_HEIGHT + TASK_HEIGHT;
            g2d.drawLine(0, y, visibleDays * DAY_WIDTH, y);
        }
    }

    private void drawTasks(Graphics2D g2d, int startDay, int startTask) {
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, startDay);

        int y = TASK_HEIGHT - TASK_HEIGHT*startTask;
        for (Task task : tasks) {
            int startX = getDayOffset(task.getStartDate(), startDay);
            int endX = getDayOffset(task.getEndDate(), startDay);
            int taskWidth = Math.max((endX - startX + 1) * DAY_WIDTH, 5);

            g2d.setColor(task.getColor());
            g2d.fillRect(startX * DAY_WIDTH, y, taskWidth, TASK_HEIGHT);
            g2d.setColor(Color.BLACK);
            g2d.drawString(task.getName(), startX * DAY_WIDTH, y + TASK_HEIGHT/2);

            y += TASK_HEIGHT;
        }
    }

    private int getDayOffset(Calendar date, int startDay) {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_MONTH, startDay);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        long diff = date.getTimeInMillis() - today.getTimeInMillis();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }
}