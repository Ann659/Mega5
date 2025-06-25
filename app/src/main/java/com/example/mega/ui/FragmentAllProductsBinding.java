package com.example.mega.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mega.R;
import com.google.android.material.appbar.MaterialToolbar;

public final class FragmentAllProductsBinding {
    private final RelativeLayout rootView;

    public final MaterialToolbar toolbar;
    public final RelativeLayout searchContainer;
    public final EditText searchInput;
    public final ImageButton searchIcon;
    public final RecyclerView productsRecyclerView;
    public final ProgressBar progressBar;
    public final TextView emptyState;

    private FragmentAllProductsBinding(RelativeLayout rootView, MaterialToolbar toolbar,
                                       RelativeLayout searchContainer, EditText searchInput,
                                       ImageButton searchIcon, RecyclerView productsRecyclerView,
                                       ProgressBar progressBar, TextView emptyState) {
        this.rootView = rootView;
        this.toolbar = toolbar;
        this.searchContainer = searchContainer;
        this.searchInput = searchInput;
        this.searchIcon = searchIcon;
        this.productsRecyclerView = productsRecyclerView;
        this.progressBar = progressBar;
        this.emptyState = emptyState;
    }

    public static FragmentAllProductsBinding inflate(LayoutInflater inflater,
                                                     ViewGroup parent,
                                                     boolean attachToParent) {
        View root = inflater.inflate(R.layout.all_products_fragment, parent, attachToParent);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static FragmentAllProductsBinding bind(View rootView) {
        int toolbarId = R.id.toolbar;
        MaterialToolbar toolbar = rootView.findViewById(toolbarId);
        if (toolbar == null) {
            throw new NullPointerException("Missing required view with ID: " + toolbarId);
        }

        int searchContainerId = R.id.search_container;
        RelativeLayout searchContainer = rootView.findViewById(searchContainerId);
        if (searchContainer == null) {
            throw new NullPointerException("Missing required view with ID: " + searchContainerId);
        }

        int searchInputId = R.id.searchInput;
        EditText searchInput = rootView.findViewById(searchInputId);
        if (searchInput == null) {
            throw new NullPointerException("Missing required view with ID: " + searchInputId);
        }

        int searchIconId = R.id.searchIcon;
        ImageButton searchIcon = rootView.findViewById(searchIconId);
        if (searchIcon == null) {
            throw new NullPointerException("Missing required view with ID: " + searchIconId);
        }

        int productsRecyclerViewId = R.id.products_recycler_view;
        RecyclerView productsRecyclerView = rootView.findViewById(productsRecyclerViewId);
        if (productsRecyclerView == null) {
            throw new NullPointerException("Missing required view with ID: " + productsRecyclerViewId);
        }

        int progressBarId = R.id.progressBar;
        ProgressBar progressBar = rootView.findViewById(progressBarId);
        if (progressBar == null) {
            throw new NullPointerException("Missing required view with ID: " + progressBarId);
        }

        int emptyStateId = R.id.emptyState;
        TextView emptyState = rootView.findViewById(emptyStateId);
        if (emptyState == null) {
            throw new NullPointerException("Missing required view with ID: " + emptyStateId);
        }

        return new FragmentAllProductsBinding(
                (RelativeLayout) rootView,
                toolbar,
                searchContainer,
                searchInput,
                searchIcon,
                productsRecyclerView,
                progressBar,
                emptyState
        );
    }

    public RelativeLayout getRoot() {
        return rootView;
    }
}