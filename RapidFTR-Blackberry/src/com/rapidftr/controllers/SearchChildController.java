package com.rapidftr.controllers;

import com.rapidftr.controllers.internal.Controller;
import com.rapidftr.controllers.internal.Dispatcher;
import com.rapidftr.datastore.Children;
import com.rapidftr.datastore.ChildrenRecordStore;
import com.rapidftr.screens.SearchChildScreen;
import com.rapidftr.screens.internal.CustomScreen;
import com.rapidftr.screens.internal.UiStack;

public class SearchChildController extends Controller {

    private final ChildrenRecordStore store;

    public SearchChildController(CustomScreen screen, UiStack uiStack, ChildrenRecordStore store, Dispatcher dispatcher) {
        super(screen, uiStack, dispatcher);
        this.store = store;
    }

    public void searchAndDisplayChildren(String searchQuery) {
        Children children = store.search(searchQuery);
        if (children.count() != 0) {
            dispatcher.viewChildren(children.sortByName());
        } else {
            getSearchChildScreen().showNoSearchResultsAlert();
        }
    }

    private SearchChildScreen getSearchChildScreen() {
        return (SearchChildScreen) currentScreen;
    }
}
