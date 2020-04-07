package com.danieljames.people.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Filter {

    public int title, tempSelectedCount = 0;
    public ArrayList<String> options = new ArrayList<>();
    public ArrayList<Boolean> selected = new ArrayList<>(), tempSelected = new ArrayList<>();
    HashMap<Integer, String> selectionMap = new HashMap<>();

    public int getIndex(String value) {
        int id;
        if (options.contains(value)) {
            id = options.indexOf(value);
        } else {
            id = options.size();
            options.add(value);
            selected.add(false);
            tempSelected.add(false);
            selectionMap.put(id, value);
        }
        return id;
    }

    public Integer[] getActiveFilters() {
        ArrayList<Integer> activeFilters = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i)) {
                activeFilters.add(i);
            }
        }
        return activeFilters.toArray(new Integer[activeFilters.size()]);
    }

    public void resetTempSelected() {
        for (int i = 0; i < selected.size(); i++) {
            tempSelected.set(i, selected.get(i));
        }
        recalculateCount();
    }

    public void checked(int which, boolean val) {
        if (tempSelected.get(which) != val) {
            tempSelected.set(which, val);
            if (val) {
                tempSelectedCount += 1;
            } else {
                tempSelectedCount -= 1;
            }
        }
    }

    public void recalculateCount() {
        this.tempSelectedCount = 0;
        for (int i = 0; i < this.tempSelected.size(); i++) {
            if (this.tempSelected.get(i)) {
                this.tempSelectedCount += 1;
            }
        }
    }

    public void applyFilter() {
        for (int i = 0; i < this.tempSelected.size(); i++) {
            this.selected.set(i, this.tempSelected.get(i));
        }
    }

    public void resetFilter() {
        for (int i = 0; i < this.tempSelected.size(); i++) {
            this.selected.set(i, false);
            this.tempSelected.set(i, false);
        }
        this.tempSelectedCount = 0;
    }
}
