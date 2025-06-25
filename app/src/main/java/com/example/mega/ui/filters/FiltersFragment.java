package com.example.mega.ui.filters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mega.R;

public class FiltersFragment extends Fragment {
    private String currentSort = "Популярное";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filters, container, false);

        ((TextView)view.findViewById(R.id.sortValue)).setText(currentSort);

        view.findViewById(R.id.sortButton).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("currentSort", currentSort);

            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_to_sort, args);
        });

        view.findViewById(R.id.applyButton).setOnClickListener(v -> {
            String priceFrom = ((EditText)view.findViewById(R.id.priceFrom)).getText().toString();
            String priceTo = ((EditText)view.findViewById(R.id.priceTo)).getText().toString();

            Bundle result = new Bundle();
            result.putString("priceFrom", priceFrom);
            result.putString("priceTo", priceTo);
            result.putString("sortType", currentSort);
            getParentFragmentManager().setFragmentResult("filters", result);

            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("sortRequest", this, (requestKey, result) -> {
            currentSort = result.getString("sortType", "Популярное");
            if (getView() != null) {
                ((TextView)getView().findViewById(R.id.sortValue)).setText(currentSort);
            }
        });
    }
}