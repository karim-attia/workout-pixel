package com.example.workoutpixel.Main;

import android.content.Context;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutpixel.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class InstructionsRecyclerViewAdapter extends RecyclerView.Adapter<InstructionsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "WORKOUT PIXEL InstructionsRecyclerViewAdapter";
    private final List<Instruction> instructions;

    // data is passed into the constructor
    InstructionsRecyclerViewAdapter(Context context, List<Instruction> instructions) {
        this.instructions = instructions;
    }

    // inflates the row layout from xml when needed
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_instruction, viewGroup, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Log.d(TAG, "onBindViewHolder");
        Log.d(TAG, "onBindViewHolder " + "Step " + position);
        Log.d(TAG, "onBindViewHolder " + instructions.get(position).getText());
        Log.d(TAG, "onBindViewHolder " + instructions.get(position).getGif());

        int positionPlusOne = position+1;
        viewHolder.title.setText("Step " + positionPlusOne);
        viewHolder.text.setText(instructions.get(position).getText());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            try {
                Drawable drawable = ImageDecoder.decodeDrawable(ImageDecoder.createSource(viewHolder.itemView.getContext().getResources(), instructions.get(position).getGif()));

                if (drawable instanceof AnimatedImageDrawable) {
                    ((AnimatedImageDrawable) drawable).start();
                }
                viewHolder.image.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
                viewHolder.image.setBackgroundResource(instructions.get(position).getBackupImage());
            }
        }
        else {
            viewHolder.image.setBackgroundResource(instructions.get(position).getBackupImage());
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        Log.d(TAG, "instructions.size " + instructions.size());
        return instructions.size();
    }


    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView text;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.instructions_step_title);
            text = itemView.findViewById(R.id.instructions_step_text);
            image  = itemView.findViewById(R.id.instructions_step_image);
        }
    }

    public static class Instruction {
        int text;
        int gif;
        int backupImage;

        Instruction(int text, int gif, int backupImage) {
            this.text = text;
            this.gif = gif;
            this.backupImage  = backupImage;
        }

        public int getText() {
            return text;
        }

        public int getGif() {
            return gif;
        }

        public int getBackupImage() {
            return backupImage;
        }
    }
}
