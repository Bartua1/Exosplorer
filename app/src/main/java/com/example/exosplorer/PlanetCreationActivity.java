package com.example.exosplorer;

import com.bumptech.glide.Glide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class PlanetCreationActivity extends AppCompatActivity {

    private String selected_star = "";
    private FrameLayout planetContainer;
    private ImageView orbitPath;
    private ImageView earthImageView;
    private boolean isDraggingPlanet = false;
    private boolean isOrbitCreated = false;
    private float centerX, centerY;

    private View cardView;
    private TextView textIntro;
    private boolean change_introduction1;
    private boolean isCardVisible = false; // Track if the card is visible
    private FrameLayout starContainer;
    private Float mass;
    private Float radius;
    private boolean viewCard;
    private Float orbit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planet_creation);

        change_introduction1 = true;
        planetContainer = findViewById(R.id.planet_container);
        orbitPath = findViewById(R.id.orbit_path);
        earthImageView = findViewById(R.id.earth_image_view);
        cardView = findViewById(R.id.card_view);
        textIntro = findViewById(R.id.text_intro);

        Intent intent2 = getIntent();
        if (intent2 != null && intent2.hasExtra("selected_star")) {
            selected_star = intent2.getStringExtra("selected_star");
            mass = intent2.getFloatExtra("mass", 0);
            radius = intent2.getFloatExtra("radius", 0);
            orbit = intent2.getFloatExtra("orbit", 0);
            if (mass != 0 && radius != 0) {
                viewCard = false;
                isOrbitCreated = true;
                // Show and start orbit creation
                orbitPath.setVisibility(View.VISIBLE);
                isDraggingPlanet = true;
                setOrbitPathSize(orbit);
                setPlanetPosition(orbit);
                earthImageView.setVisibility(View.VISIBLE);
                startOrbitAnimation(earthImageView);
            }
        }

        starContainer = findViewById(R.id.star_container);

        // Use Glide to load and play the Sun GIF animation
        ImageView sunImageView = findViewById(R.id.sun_image_view);
        Glide.with(this)
                .asGif()
                .load(R.drawable.sun_rotating)  // Load the Sun GIF
                .into(sunImageView);             // Set the GIF to the ImageView

        // Calculate center of the screen for the orbit
        planetContainer.post(() -> {
            centerX = planetContainer.getWidth() / 2f;
            centerY = planetContainer.getHeight() / 2f;

            // Show the CardView after 0.5 seconds
            if (viewCard) {
                new Handler().postDelayed(this::showCardView, 500);
            }
        });

        planetContainer.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!isOrbitCreated) {
                        // Show and start orbit creation
                        orbitPath.setVisibility(View.VISIBLE);
                        isDraggingPlanet = true;
                        updateOrbitPath(event.getX(), event.getY());
                        updatePlanetPosition(event.getX(), event.getY());
                        earthImageView.setVisibility(View.VISIBLE);  // Show the planet

                        if (change_introduction1 && viewCard) {
                            changeCardText(getString(R.string.introduction2)); // Change text without hiding
                            showCardView();
                            change_introduction1 = false;
                        }
                    }
                    return true;


                case MotionEvent.ACTION_MOVE:
                    if (isDraggingPlanet) {
                        updateOrbitPath(event.getX(), event.getY());
                        updatePlanetPosition(event.getX(), event.getY());
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    if (isDraggingPlanet) {
                        isDraggingPlanet = false;
                        isOrbitCreated = true;  // Orbit is now fixed
                        startOrbitAnimation(earthImageView);  // Start Earth orbiting
                        // Start the PlanetCreationCardActivity and send the radius of the orbit
                        Intent intent = new Intent(PlanetCreationActivity.this, PlanetCreationCardActivity.class);
                        intent.putExtra("orbitRadius", orbitPath.getWidth() / 2);
                        intent.putExtra("selected_star", selected_star);
                        startActivity(intent);
                        finish();
                    }
                    return true;
            }
            return false;
        });

        // Use Glide to load and play the Earth GIF animation
        Glide.with(this)
                .asGif()
                .load(R.drawable.exoplanet_gif)  // Load the Earth GIF
                .into(earthImageView);           // Set the GIF to the ImageView

        // Initially hide the CardView
        generateStars(100);
        cardView.setVisibility(View.INVISIBLE);
    }

    private void showCardView() {
        // Show the CardView before animating
        cardView.setVisibility(View.VISIBLE);

        // Animate the CardView from bottom to its position
        ObjectAnimator animator = ObjectAnimator.ofFloat(cardView, "translationY", cardView.getHeight(), 0f);
        animator.setDuration(500); // Duration of 0.5 seconds for the animation
        animator.start();

        isCardVisible = true; // Update visibility state
    }

    private void changeCardText(String newText) {
        textIntro.setText(newText);
    }

    private void hideCardView() {
        // Change the text inside the CardView
        textIntro.setText(R.string.introduction2);

        // Animate the CardView down to hide it
        ObjectAnimator animator = ObjectAnimator.ofFloat(cardView, "translationY", 0f, cardView.getHeight());
        animator.setDuration(500); // Duration of 0.5 seconds for the animation
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cardView.setVisibility(View.INVISIBLE); // Hide after the animation ends
                isCardVisible = false; // Update visibility state
            }
        });
        animator.start();
    }

    private void generateStars(int numberOfStars) {
        Random random = new Random();
        for (int i = 0; i < numberOfStars; i++) {
            ImageView star = new ImageView(this);
            star.setLayoutParams(new FrameLayout.LayoutParams(10, 10)); // Small star size
            star.setBackgroundColor(Color.WHITE); // Star color
            star.setAlpha(0.8f); // Slight transparency

            // Set random position for the star
            int x = random.nextInt(getResources().getDisplayMetrics().widthPixels);
            int y = random.nextInt(getResources().getDisplayMetrics().heightPixels);
            star.setX(x);
            star.setY(y);

            // Add star to the star container
            starContainer.addView(star);
        }
    }

    private void updateOrbitPath(float touchX, float touchY) {
        // Calculate radius from center to touch point
        float dx = touchX - centerX;
        float dy = touchY - centerY;
        float radius = (float) Math.sqrt(dx * dx + dy * dy);

        // Update orbit path size dynamically
        ViewGroup.LayoutParams params = orbitPath.getLayoutParams();
        params.width = (int) (2 * radius);
        params.height = (int) (2 * radius);
        orbitPath.setLayoutParams(params);

        // Center the orbit path
        orbitPath.setX(centerX - radius);
        orbitPath.setY(centerY - radius);
    }

    private void updatePlanetPosition(float touchX, float touchY) {
        // Move the Earth image to follow the touch position
        earthImageView.setX(touchX - earthImageView.getWidth() / 2);
        earthImageView.setY(touchY - earthImageView.getHeight() / 2);
    }

    private void startOrbitAnimation(View planet) {
        // Calculate distance from the center to the planet
        float dx = planet.getX() + planet.getWidth() / 2 - centerX;
        float dy = planet.getY() + planet.getHeight() / 2 - centerY;
        float radius = (float) Math.sqrt(dx * dx + dy * dy);

        // Set up orbit animation for the Earth (1/6 of the original speed)
        ObjectAnimator orbitAnimator = ObjectAnimator.ofFloat(planet, "rotation", 0f, 360f);
        orbitAnimator.setDuration(30000);  // Slowed down to 30 seconds for 1 full orbit (1/6 speed)
        orbitAnimator.setRepeatCount(ValueAnimator.INFINITE);
        orbitAnimator.setInterpolator(new LinearInterpolator());

        // Rotate around the center of the screen (pivotX and pivotY)
        planet.setPivotX(centerX - planet.getX());
        planet.setPivotY(centerY - planet.getY());

        orbitAnimator.start();
    }

    private void setPlanetPosition(float orbit) {
        earthImageView.setX(centerX);
        earthImageView.setY(centerY - orbit);
    }

    private void setOrbitPathSize(float orbit) {
        ViewGroup.LayoutParams params = orbitPath.getLayoutParams();
        params.width = (int) (2 * orbit);
        params.height = (int) (2 * orbit);
    }
}
