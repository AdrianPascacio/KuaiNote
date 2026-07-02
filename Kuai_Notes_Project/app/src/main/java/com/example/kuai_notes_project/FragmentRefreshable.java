package com.example.kuai_notes_project;

public interface FragmentRefreshable {
    void onFragmentSelected();
    /// Modification in element → 0 Modification, 1 New Element, 2 Element Deleted
    void onFragmentNewElement(int modification_in_element, long element_id);
    void onFragmentElementModification(int modification_in_element, long element_id);
    void onFragmentElementElimination(int modification_in_element, long element_id);
}
