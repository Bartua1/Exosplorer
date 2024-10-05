package com.example.exosplorer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PlanetCreationCardActivity extends AppCompatActivity {

    private String selected_star;
    private Integer orbitRadius;
    private SeekBar massSeekBar;
    private SeekBar radiusSeekBar;
    private TextView massTextView;
    private TextView radiusTextView;
    private TextView similarExoplanetTextView;
    private TextView similarExoplanetDetailsView;
    private Button startAdventureButton;

    private float mass = 1f;  // Default mass value (1 Earth mass)
    private float radius = 1000f;  // Default radius value (1000 km)
    private boolean isCardVisible = false;
    private TextView textIntro;
    private View cardView;
    private boolean changeIntroduction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planet_creation_card);

        // Retrieve the orbit radius from the intent
        Intent intent = getIntent();
        orbitRadius = intent.getIntExtra("orbitRadius", 0);
        selected_star = intent.getStringExtra("selected_star");

        // Initialize the UI components
        textIntro = findViewById(R.id.text_intro);
        cardView = findViewById(R.id.card_view);

        // Initialize the UI components
        massTextView = findViewById(R.id.text_mass_value);
        radiusTextView = findViewById(R.id.text_radius_value);
        similarExoplanetTextView = findViewById(R.id.text_similar_exoplanet);
        similarExoplanetDetailsView = findViewById(R.id.text_exoplanet_details);
        massSeekBar = findViewById(R.id.mass_seekbar);
        radiusSeekBar = findViewById(R.id.radius_seekbar);
        startAdventureButton = findViewById(R.id.start_adventure_button);

        changeIntroduction = true;

        // Set up the SeekBar for mass
        massSeekBar.setMax(1000); // Max value for mass (0 to 100 Earth masses)
        massSeekBar.setProgress(1); // Start at 1 Earth mass
        massSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mass = progress / 10f; // Convert progress to float (0.0 to 10.0)
                massTextView.setText(String.format("Mass: %.2f Earth Masses", mass)); // Update TextView
                if (changeIntroduction) {
                    changeIntroduction = false;
                    hideCardView();
                    changeCardText(getString(R.string.introduction_card2));
                    showCardView();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Set up the SeekBar for radius
        radiusSeekBar.setMax(500); // Max value for radius (0 to 500, for example, representing 0 to 50000 km)
        radiusSeekBar.setProgress(100); // Start at 1000 km (represented as 100)
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = progress * 100f; // Convert progress to float (0.0 to 50000.0)
                radiusTextView.setText(String.format("Radius: %.2f km", radius)); // Update TextView
                if (changeIntroduction) {
                    changeIntroduction = false;
                    hideCardView();
                    changeCardText(getString(R.string.introduction_card2));
                    showCardView();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Set up the similar exoplanet text view (random exoplanet)
        similarExoplanetTextView.setText(findClosestExoplanet(selected_star, orbitRadius, mass, radius));

        // Set up the Start Adventure button
        startAdventureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanetCreationCardActivity.this, PlanetCreationActivity.class);
                intent.putExtra("selected_star", selected_star);
                intent.putExtra("orbitRadius", orbitRadius);
                intent.putExtra("mass", mass);
                intent.putExtra("radius", radius);
                startActivity(intent);
                finish();
            }
        });

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showCardView();
            }
        }, 500);
    }

    private String findClosestExoplanet(String selected_star, int orbitRadius, float mass, float radius) {
        readCSVFile();
        // The csv has the following columns:
        // loc_rowid,kepid,kepoi_name,kepler_name,koi_disposition,koi_pdisposition,koi_score,koi_fpflag_nt,koi_fpflag_ss,koi_fpflag_co,koi_fpflag_ec,koi_period,koi_period_err1,koi_period_err2,koi_time0bk,koi_time0bk_err1,koi_time0bk_err2,koi_impact,koi_impact_err1,koi_impact_err2,koi_duration,koi_duration_err1,koi_duration_err2,koi_depth,koi_depth_err1,koi_depth_err2,koi_prad,koi_prad_err1,koi_prad_err2,koi_teq,koi_teq_err1,koi_teq_err2,koi_insol,koi_insol_err1,koi_insol_err2,koi_model_snr,koi_tce_plnt_num,koi_tce_delivname,koi_steff,koi_steff_err1,koi_steff_err2,koi_slogg,koi_slogg_err1,koi_slogg_err2,koi_srad,koi_srad_err1,koi_srad_err2,ra,dec,koi_kepmag
        // Create a function that finds the most similar exoplanet to the selected star and returns it
        String closestExoplanet = "";

        // The planet radius is koi_time0bk_err2 and it is given in earth radii
        // That means that we need to convert it to km, also its a number that has the
        // following format 1.950e+01, so we need to convert it to a float

        float radiiDiff = Float.MAX_VALUE;
        float earthRadius = 6371f;

        // Find the close

        return closestExoplanet;
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

    private void readCSVFile() {
        InputStream inputStream = getResources().openRawResource(R.raw.exoplanets);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                // Split the line by commas (you can adjust if your CSV uses a different delimiter)
                String[] fields = line.split(",");
                // Process fields as needed
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
