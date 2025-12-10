package org.nequma;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class TaskDialog extends JDialog {
    private Task task;
    private boolean confirmed = false;
    private JTextField nameField;
    private JTextArea definitionField;
    private JCheckBox importantCheckBox;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JColorChooser colorChooser;

    public TaskDialog(Frame owner, Task task) {
        super(owner, task == null ? "Добавить задачу" : "Редактировать задачу", true);
        this.task = task;

        initComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.add(new JLabel("Название:"));
        nameField = new JTextField();
        if (task != null) {nameField.setText(task.getName());};
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Описание:"));
        definitionField = new JTextArea();
        if (task != null) {definitionField.setText(task.getDefinition());}
        inputPanel.add(new JScrollPane(definitionField));

        inputPanel.add(new JLabel("Начало:"));
        if(task != null){
            startDateSpinner = new JSpinner(new SpinnerDateModel(task.getStartDate().getTime(), null, null, Calendar.DAY_OF_MONTH));
        }
        else {
            Calendar currentTime = new GregorianCalendar();
            startDateSpinner = new JSpinner(new SpinnerDateModel(currentTime.getTime(), null, null, Calendar.DAY_OF_MONTH));
        }
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "dd.MM.yyyy"));
        inputPanel.add(startDateSpinner);

        inputPanel.add(new JLabel("Окончание:"));
        if(task != null) {
            endDateSpinner = new JSpinner(new SpinnerDateModel(
                    task.getEndDate().getTime(), null, null, Calendar.DAY_OF_MONTH));
        }
        else {
            Calendar currentTime = new GregorianCalendar();
            endDateSpinner = new JSpinner(new SpinnerDateModel(currentTime.getTime(), null, null, Calendar.DAY_OF_MONTH));
        }
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "dd.MM.yyyy"));
        inputPanel.add(endDateSpinner);

        importantCheckBox = new JCheckBox("Важная задача");
        if(task != null){
            importantCheckBox.setSelected(task.isImportant());
        }
        else {
            importantCheckBox.setSelected(false);
        }
        inputPanel.add(importantCheckBox);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        if(task != null){
            colorChooser = new JColorChooser(task.getColor());
        }
        else {
            colorChooser = new JColorChooser();
        }
        mainPanel.add(colorChooser, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Отмена");

        okButton.addActionListener(e -> {
            saveTask();
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void saveTask() {
        if (this.task == null){
            Calendar startCal = Calendar.getInstance();
            startCal.setTime((Date) startDateSpinner.getValue());
            Calendar endCal = Calendar.getInstance();
            endCal.setTime((Date) endDateSpinner.getValue());
            this.task = new Task(nameField.getText(),
                    startCal,
                    endCal,
                    importantCheckBox.isSelected(),
                    definitionField.getText(),
                    colorChooser.getColor());
        }
        else {
            task.setName(nameField.getText());
            task.setDefinition(definitionField.getText());
            task.setImportant(importantCheckBox.isSelected());

            Calendar startCal = Calendar.getInstance();
            startCal.setTime((Date) startDateSpinner.getValue());
            task.setStartDate(startCal);

            Calendar endCal = Calendar.getInstance();
            endCal.setTime((Date) endDateSpinner.getValue());
            task.setEndDate(endCal);
            task.setColor(colorChooser.getColor());
        }
    }

    public Task getTask() {
        return task;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}