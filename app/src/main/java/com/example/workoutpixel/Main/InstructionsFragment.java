package com.example.workoutpixel.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.workoutpixel.R;

public class InstructionsFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.instructions, container, false);
        ImageView instructions_long_click = view.findViewById(R.id.instructions_long_click);
        ImageView instructions_widget_selection = view.findViewById(R.id.instructions_widget_selection);
        ImageView instructions_place_widget = view.findViewById(R.id.instructions_place_widget);
        ImageView instructions_configure_widget = view.findViewById(R.id.instructions_configure_widget);
        ImageView instructions_widget_created = view.findViewById(R.id.instructions_widget_created);
        ImageView instructions_widget_clicked = view.findViewById(R.id.instructions_widget_clicked);
        ImageView instructions_main_app = view.findViewById(R.id.instructions_main_app);

        instructions_long_click.setImageResource(R.drawable.instructions_long_click);
        instructions_widget_selection.setImageResource(R.drawable.instructions_widget_selection);
        instructions_place_widget.setImageResource(R.drawable.instructions_place_widget);
        instructions_configure_widget.setImageResource(R.drawable.instructions_configure_widget);
        instructions_widget_created.setImageResource(R.drawable.instructions_widget_created);
        instructions_widget_clicked.setImageResource(R.drawable.instructions_widget_clicked);
        instructions_main_app.setImageResource(R.drawable.instructions_main_app);

        return view;
    }
}