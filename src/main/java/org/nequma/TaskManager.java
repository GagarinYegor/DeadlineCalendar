package org.nequma;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class TaskManager {
    private File activeFolder;
    private File completedFolder;
    private Gson gson;
    private List <Task> activeTasks;
    private List <Task> completedTasks;

    private List<Task> fillTaskList(File folder) {
        List<Task> tasksInFolder = new ArrayList<Task>();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        for (File currentFile: files){
            try (FileReader reader = new FileReader(currentFile)){
                Task currentTask = gson.fromJson(reader, Task.class);
                tasksInFolder.add(currentTask);
            }catch (JsonSyntaxException e){
                System.out.println("File with name \"" + currentFile.getName() + "\" is not correct json object.");
            } catch (IOException e) {
                System.out.println("File with name \"" + currentFile.getName() + "\" was not found.");
                System.out.println(e.getMessage());
            }
        }
        return tasksInFolder;
    }

    public File getActiveFolder() {return this.activeFolder;}
    public File getCompletedFolder() {return this.completedFolder;}

    public void saveTask(Task task, File folder){
        File file = new File(folder, task.getID() + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(task, writer);
        } catch (IOException e) {
            System.err.println("Error saving task: " + e.getMessage());
        }
    }

    public void addTask(Task task) {
        this.activeTasks.add(task);
        saveTask(task, activeFolder);
    }

    public void moveTask(Task task, File fromFolder, File toFolder) {
        saveTask(task, toFolder);
        new File(fromFolder, task.getID() + ".json").delete();
    }

    public void completeTask(Task task){
        task.setCompleted(true);
        moveTask(task, activeFolder, completedFolder);
        completedTasks.add(task);
        activeTasks.remove(task);
    }

    public void deleteTask(Task task){
        new File(completedFolder, task.getID() + ".json").delete();
        this.completedTasks.remove(task);
    }

    public void saveAll(){
        for (Task task: this.activeTasks){
            saveTask(task, this.activeFolder);
        }
        for (Task task: this.completedTasks){
            saveTask(task, this.completedFolder);
        }
    }

    public List<Task> getActiveTasks(){
        return this.activeTasks;
    }
    public List<Task> getCompletedTasks(){
        return this.completedTasks;
    }

    public TaskManager(String dataPath){
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Color.class, new ColorAdapter())
                .setPrettyPrinting()
                .create();

        this.activeFolder = new File(dataPath + "/active_tasks");
        this.completedFolder = new File(dataPath + "/completed_tasks");

        if (!activeFolder.exists()) activeFolder.mkdirs();
        if (!completedFolder.exists()) completedFolder.mkdirs();

        activeTasks = fillTaskList(activeFolder);
        completedTasks = fillTaskList(completedFolder);
    }
}