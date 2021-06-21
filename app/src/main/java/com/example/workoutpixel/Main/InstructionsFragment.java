package com.example.workoutpixel.Main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutpixel.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.workoutpixel.Main.InstructionsRecyclerViewAdapter.Instruction;

public class InstructionsFragment extends Fragment {
    private static final String TAG = "WORKOUT_PIXEL InstructionsFragment";
    private Context context;
    InstructionsRecyclerViewAdapter instructionsRecyclerViewAdapter;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("WrongThread")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.instructions, container, false);

        requireActivity().setTitle("Workout Pixel");
        try {
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        } catch (Exception e) {
            Log.w(TAG, "requireActivity " + e);
        }

        List<Instruction> instructions = new ArrayList<>();
        instructions.add(new Instruction(R.string.instructions_step1, R.drawable.step1, R.drawable.instructions_long_click));
        instructions.add(new Instruction(R.string.instructions_step2, R.drawable.step2, R.drawable.instructions_widget_selection));
        instructions.add(new Instruction(R.string.instructions_step3, R.drawable.step3, R.drawable.instructions_configure_widget));
        instructions.add(new Instruction(R.string.instructions_step4, R.drawable.step4, R.drawable.instructions_widget_created));
        instructions.add(new Instruction(R.string.instructions_step5, R.drawable.step5, R.drawable.instructions_main_app));

        RecyclerView recyclerView = view.findViewById(R.id.instructions_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        instructionsRecyclerViewAdapter = new InstructionsRecyclerViewAdapter(context, instructions);
        recyclerView.setAdapter(instructionsRecyclerViewAdapter);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            Drawable drawable;
            try {
                drawable = ImageDecoder.decodeDrawable(ImageDecoder.createSource(getResources(), R.drawable.step1));

                if (drawable instanceof AnimatedImageDrawable) {
                    ((AnimatedImageDrawable) drawable).start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            // instructions_long_click.setImageDrawable(drawable);
        }

        return view;
    }
}