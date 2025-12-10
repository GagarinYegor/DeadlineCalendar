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
    private List <Task> activeTask;
    private List <Task> completedTask;

    private List<Task> loadTaskList(File folder) {
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

    public void saveTask(Task task){
        File currentFile = new File((task.getCompleted()? completedFolder:activeFolder), task.getID() + ".json");
        try (FileWriter writer = new FileWriter(currentFile)) {
            gson.toJson(task, writer);
        } catch (IOException e) {
            System.out.println("File with name \"" + currentFile.getName() + "\" can not be saved.");
            System.out.println(e.getMessage());
        }
    }

    public void addActiveTask(Task task) {
        this.activeTask.add(task);
        saveTask(task);
    }

    public void completeTask(Task task){
        if(task.getCompleted() == false){
            task.setCompleted(true);
            saveTask(task);
            new File(activeFolder, task.getID() + ".json").delete();
            completedTask.add(task);
            activeTask.remove(task);
        }
    }

    public void unCompleteTask(Task task){
        if(task.getCompleted() == true){
            task.setCompleted(false);
            saveTask(task);
            new File(completedFolder, task.getID() + ".json").delete();
            activeTask.add(task);
            completedTask.remove(task);
        }
    }

    public void deleteTask(Task task){
        if(task.getCompleted() == true){
            new File(completedFolder, task.getID() + ".json").delete();
            this.completedTask.remove(task);
        }
    }

    public List<Task> getActiveTask(){
        return this.activeTask;
    }
    public List<Task> getCompletedTask(){
        return this.completedTask;
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

        activeTask = loadTaskList(activeFolder);
        completedTask = loadTaskList(completedFolder);
    }
}