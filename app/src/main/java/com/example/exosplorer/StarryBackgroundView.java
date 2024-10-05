package com.example.exosplorer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import java.util.Random;

public class StarryBackgroundView extends View {
    private Paint paint;
    private Random random;
    private int numberOfStars = 100; // Adjust the number of stars
    private int[] starX;
    private int[] starY;
    private int[] starSizes;

    public StarryBackgroundView(Context context) {
        super(context);
        paint = new Paint();
        random = new Random();

        // Initialize arrays to store the stars' positions and sizes
        starX = new int[numberOfStars];
        starY = new int[numberOfStars];
        starSizes = new int[numberOfStars];

        // Generate random positions and sizes for the stars
        for (int i = 0; i < numberOfStars; i++) {
            starX[i] = random.nextInt(getResources().getDisplayMetrics().widthPixels); // Random x position
            starY[i] = random.nextInt(getResources().getDisplayMetrics().heightPixels); // Random y position
            starSizes[i] = random.nextInt(5) + 2; // Random size between 2 and 7 pixels
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Set the background color to #3f428f
        canvas.drawColor(Color.parseColor("#17153B"));

        // Draw the circle in the top-left corner with color #524ea3
        paint.setColor(Color.parseColor("#2E236C"));
        float radius = 1200f; // Adjust the radius as needed
        float centerX = 0; // Center it off-screen to the left
        float centerY = 400; // Center it off-screen to the top
        canvas.drawCircle(centerX, centerY, radius, paint);

        // Draw the stars
        paint.setColor(Color.WHITE);
        for (int i = 0; i < numberOfStars; i++) {
            canvas.drawCircle(starX[i], starY[i], starSizes[i], paint);
        }
    }
}
