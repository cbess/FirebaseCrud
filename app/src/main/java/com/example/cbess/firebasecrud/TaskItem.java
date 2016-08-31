package com.example.cbess.firebasecrud;

import com.google.firebase.database.IgnoreExtraProperties;

// or @ThrowOnExtraProperties
@IgnoreExtraProperties
public class TaskItem {
    private String name = "";
    private String uid;

    public TaskItem() {
        // empty for Firebase
    }

    public TaskItem(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }

    // implement for easy comparison
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof TaskItem) {
            TaskItem item = ((TaskItem)obj);
            if (item.getUid() == null) {
                return false;
            }
            return item.getUid().contentEquals(getUid());
        }

        return super.equals(obj);
    }
}
