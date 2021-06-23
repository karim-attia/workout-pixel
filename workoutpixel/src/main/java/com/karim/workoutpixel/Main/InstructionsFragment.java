package com.karim.workoutpixel.Main;

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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.karim.workoutpixel.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.synnapps.carouselview.ViewListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.karim.workoutpixel.Main.InstructionsRecyclerViewAdapter.Instruction;

public class InstructionsFragment extends Fragment {
    private static final String TAG = "WORKOUT_PIXEL InstructionsFragment";
    private Context context;
    InstructionsRecyclerViewAdapter instructionsRecyclerViewAdapter;
    List<Instruction> instructions = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.carousel_instructions, container, false);

        requireActivity().setTitle("Workout Pixel");
        try {
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        } catch (Exception e) {
            Log.w(TAG, "requireActivity " + e);
        }

        instructions.add(new Instruction("Reach your goals", R.string.instructions_pitch, R.drawable.instructions_pitch, R.drawable.instructions_pitch));
        instructions.add(new Instruction(getString(R.string.step) + " 1", R.string.instructions_step1, R.drawable.step1, R.drawable.instructions_long_click));
        instructions.add(new Instruction(getString(R.string.step) + " 2", R.string.instructions_step2, R.drawable.step2, R.drawable.instructions_widget_selection));
        instructions.add(new Instruction(getString(R.string.step) + " 3", R.string.instructions_step3, R.drawable.step3, R.drawable.instructions_configure_widget));
        instructions.add(new Instruction(getString(R.string.step) + " 4", R.string.instructions_step4, R.drawable.step4, R.drawable.instructions_widget_created));
        instructions.add(new Instruction(getString(R.string.step) + " 5", R.string.instructions_step5, R.drawable.step5, R.drawable.instructions_main_app));

        // CarouselView carouselView = view.findViewById(R.id.carouselView);
        // carouselView.setPageCount(instructions.size());
        // carouselView.setImageListener(imageListener);

        CarouselView customCarouselView = view.findViewById(R.id.carouselView);
        customCarouselView.setPageCount(instructions.size());
        // set ViewListener for custom view
        customCarouselView.setViewListener(viewListener);

/*
        RecyclerView recyclerView = view.findViewById(R.id.instructions_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        instructionsRecyclerViewAdapter = new InstructionsRecyclerViewAdapter(context, instructions);
        recyclerView.setAdapter(instructionsRecyclerViewAdapter);
*/

        return view;
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(instructions.get(position).getBackupImage());
        }
    };

    ViewListener viewListener = position -> {
        View view = getLayoutInflater().inflate(R.layout.carousel_instruction_step, null);
        //set view attributes here
        TextView title;
        TextView text;
        ImageView image;

        title = view.findViewById(R.id.instructions_step_title);
        text = view.findViewById(R.id.instructions_step_text);
        image  = view.findViewById(R.id.instructions_step_image);

        title.setText(instructions.get(position).getTitle());
        text.setText(instructions.get(position).getText());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            try {
                Drawable drawable = ImageDecoder.decodeDrawable(ImageDecoder.createSource(view.getContext().getResources(), instructions.get(position).getGif()));

                if (drawable instanceof AnimatedImageDrawable) {
                    ((AnimatedImageDrawable) drawable).start();
                }
                image.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
                image.setBackgroundResource(instructions.get(position).getBackupImage());
            }
        }
        else {
            image.setBackgroundResource(instructions.get(position).getBackupImage());
        }

        // TODO: If position = X add goal, then show instruction to add a widget for this goal.

        return view;
    };
}