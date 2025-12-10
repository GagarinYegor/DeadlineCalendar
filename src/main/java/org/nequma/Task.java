package org.nequma;
import java.awt.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Task {
    private String id;
    private String name;
    private Calendar startDate;
    private Calendar endDate;
    private String definition;
    private boolean isImportant;
    private boolean isCompleted;
    private Color color;

    private String createID(){
        String id = "";
        Calendar today = new GregorianCalendar();
        id += today.get(Calendar.YEAR);
        id += today.get(Calendar.DAY_OF_YEAR);
        id += today.get(Calendar.HOUR);
        id += today.get(Calendar.MINUTE);
        id += today.get(Calendar.SECOND);
        id += today.get(Calendar.MILLISECOND);
        return id;
    }

    /*public Task() {
        this.id = createID();
        this.name = "";
        this.startDate = new GregorianCalendar();
        this.endDate = new GregorianCalendar();
        this.definition = "";
        this.isImportant = false;
        this.isCompleted = false;
        this.color = Color.blue;
    }*/

    public String getID() {return this.id;}

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    public Calendar getStartDate() {return this.startDate;}
    public void setStartDate(Calendar startDate) {this.endDate = startDate;}

    public Calendar getEndDate() {return this.endDate;}
    public void setEndDate(Calendar endDate) {this.endDate = endDate;}

    public String getDefinition() {return this.definition;}
    public void setDefinition(String definition) {this.name = this.definition;}

    public boolean isImportant() {return this.isImportant;}
    public void setImportant(boolean isImportant) {this.isImportant = isImportant;}

    public boolean isCompleted() {return this.isCompleted;}
    public void setCompleted(boolean isCompleted) {this.isCompleted = isCompleted;}

    public Color getColor() {return this.color;}
    public void setColor(Color color) {this.color = color;}

    public boolean isUrgent() {
        Calendar weekLater = Calendar.getInstance();
        weekLater.add(Calendar.DATE, 7);
        return !this.endDate.after(weekLater);
    }

    public Task(String name, Calendar startDate, Calendar endDate, String definition) {
        this.id = createID();
        this.name = name;
        this.endDate = endDate;
        this.definition = definition;
        this.startDate = startDate;
        this.isImportant = false;
        this.color = Color.blue;
        this.isCompleted = false;
    }
    public Task(String name,
                Calendar startDate,
                Calendar endDate,
                boolean isImportant,
                String definition,
                Color color) {
        this.id = createID();
        this.name = name;
        this.endDate = endDate;
        this.definition = definition;
        this.startDate = startDate;
        this.isImportant = isImportant;
        this.color = color;
        this.isCompleted = false;
    }
}