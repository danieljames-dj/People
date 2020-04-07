package com.danieljames.people.model;

import java.util.HashMap;

public interface ContactInterface {
    Filter[] getFilters();
    boolean hasActiveFilters();
    void resetFilter();
}