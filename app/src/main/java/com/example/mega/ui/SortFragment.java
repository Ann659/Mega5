package com.example.mega.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mega.R;

public class SortFragment extends Fragment {
    private String currentSort = "Популярное";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sort, container, false);

        if (getArguments() != null) {
            currentSort = getArguments().getString("currentSort", "Популярное");
        }

        view.findViewById(R.id.resetButton).setOnClickListener(v -> {
            currentSort = "Популярное";
            updateSortSelection(view);
        });

        setupSortOptions(view);

        view.findViewById(R.id.applyButton).setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString("sortType", currentSort);
            getParentFragmentManager().setFragmentResult("sortRequest", result);

            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });

        return view;
    }

    private void setupSortOptions(View view) {
        String[] options = {"Популярное", "Сначала дешевые",
                "Сначала дорогие", "Высокий рейтинг"};

        for (int i = 0; i < options.length; i++) {
            int optionId = getResources().getIdentifier(
                    "option" + (i + 1), "id", requireContext().getPackageName());

            TextView optionView = view.findViewById(optionId);
            optionView.setText(options[i]);

            if (options[i].equals(currentSort)) {
                view.findViewById(getResources().getIdentifier(
                                "check" + (i + 1), "id", requireContext().getPackageName()))
                        .setVisibility(View.VISIBLE);
            }

            int finalI = i;
            optionView.setOnClickListener(v -> {
                currentSort = options[finalI];
                updateSortSelection(view);
            });
        }
    }

    private void updateSortSelection(View view) {
        for (int i = 1; i <= 4; i++) {
            view.findViewById(getResources().getIdentifier(
                            "check" + i, "id", requireContext().getPackageName()))
                    .setVisibility(View.GONE);
        }

        switch (currentSort) {
            case "Популярное":
                view.findViewById(R.id.check1).setVisibility(View.VISIBLE);
                break;
            case "Сначала дешевые":
                view.findViewById(R.id.check2).setVisibility(View.VISIBLE);
                break;
            case "Сначала дорогие":
                view.findViewById(R.id.check3).setVisibility(View.VISIBLE);
                break;
            case "Высокий рейтинг":
                view.findViewById(R.id.check4).setVisibility(View.VISIBLE);
                break;
        }
    }
}