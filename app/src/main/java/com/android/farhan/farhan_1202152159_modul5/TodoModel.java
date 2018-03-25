package com.android.farhan.farhan_1202152159_modul5;

/**
 */

public class TodoModel {
    private int id;
    private String Name;
    private String Description;
    private int Priority;

    public TodoModel(String name, String description, int priority) {
        Name = name;
        Description = description;
        Priority = priority;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPriority() {
        return Priority;
    }

    public void setPriority(int priority) {
        Priority = priority;
    }
}
