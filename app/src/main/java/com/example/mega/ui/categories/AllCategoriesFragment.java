package com.example.mega.ui.categories;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mega.R;
import com.example.mega.supabase.CategoriesClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AllCategoriesFragment extends Fragment {
    private NavController navController;
    private CategoriesClient categoriesClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_categories, container, false);
        navController = Navigation.findNavController(view);
        categoriesClient = new CategoriesClient();

        view.findViewById(R.id.backButton).setOnClickListener(v -> navController.popBackStack());

        loadCategories(view);
        return view;
    }

    private void loadCategories(View view) {
        categoriesClient.getAllCategories(new CategoriesClient.CategoriesCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        Map<Integer, View> categoryCards = new HashMap<>();
                        categoryCards.put(1, view.findViewById(R.id.cardFood));
                        categoryCards.put(2, view.findViewById(R.id.cardGift));
                        categoryCards.put(3, view.findViewById(R.id.cardFashion));
                        categoryCards.put(4, view.findViewById(R.id.cardGadget));
                        categoryCards.put(5, view.findViewById(R.id.cardCompute));

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject categoryJson = response.getJSONObject(i);
                            int categoryId = categoryJson.getInt("category_id");
                            String categoryName = categoryJson.getString("name");

                            View card = categoryCards.get(categoryId);
                            if (card != null) {
                                card.setOnClickListener(v -> {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("category_id", categoryId);
                                    bundle.putString("category_name", categoryName);

                                    navController.navigate(R.id.action_allCategoriesFragment_to_food_category, bundle);
                                });

                                RelativeLayout cardLayout = (RelativeLayout) ((CardView) card).getChildAt(0);
                                TextView title = cardLayout.findViewById(R.id.categoryTitle);
                                if (title != null) {
                                    title.setText(categoryName);
                                } else {
                                    for (int j = 0; j < cardLayout.getChildCount(); j++) {
                                        View child = cardLayout.getChildAt(j);
                                        if (child instanceof TextView) {
                                            ((TextView) child).setText(categoryName);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("AllCategories", "Error parsing categories", e);
                        Toast.makeText(getContext(), "Error loading categories", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    Log.e("AllCategories", errorMessage);
                    Toast.makeText(getContext(), "Error loading categories", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
