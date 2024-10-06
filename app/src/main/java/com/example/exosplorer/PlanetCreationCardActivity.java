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
import java.util.ArrayList;
import java.util.List;

public class PlanetCreationCardActivity extends AppCompatActivity {

    private String selected_star;
    private List<Exoplanet> exoplanets;
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

    private void readCSVFile() {
        exoplanets = new ArrayList<>();

        // Get the input stream for the CSV file located in res/raw
        InputStream inputStream = getResources().openRawResource(R.raw.exoplanets); // Make sure to use the correct resource identifier

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            // Skip the header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                // Parse the values and create an Exoplanet object
                String plName = values[0];
                double plOrbsMax = Double.parseDouble(values[1]);
                double plRade = Double.parseDouble(values[2]);
                double plBmasse = Double.parseDouble(values[3]);
                double stRad = Double.parseDouble(values[4]);
                double stMass = Double.parseDouble(values[5]);
                double syDist = Double.parseDouble(values[6]);

                // Create a new Exoplanet object and add it to the list
                Exoplanet exoplanet = new Exoplanet(plName, plOrbsMax, plRade, plBmasse, stRad, stMass, syDist);
                exoplanets.add(exoplanet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace(); // Handle potential parsing errors
        }
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

    private String isHabitable(double stMass, double planetRadius, double plMass) {
        double maxStRadius = Math.sqrt(Math.pow(stMass, 3) / 0.53);
        double minStRadius = Math.sqrt(Math.pow(stMass, 3) / 1.1);

        if (orbitRadius < minStRadius) {
            return "Planet's orbit is too small to be habitable.";
        }
        else if (orbitRadius > maxStRadius) {
            return "Planet's orbit is too large to be habitable.";
        }

        double earthRadius = 6371000;
        double relativeRadius = planetRadius / earthRadius;
        // plMass is the mass of the planet in Earth masses
        // stMass is the mass of the star in solar masses
        // relativeRadius is the radius of the planet relative to the Earth's radius
        double planet_density = plMass*5.51 / Math.pow(relativeRadius, 3);

        if (planet_density < 2){
            return "Planet is too dense to be habitable.";
        }

        // Calculate gravity
        double earthMass = 5.972 * Math.pow(10, 24); // Mass of the Earth in kg
        double G = 6.67430 * Math.pow(10, -11); // Gravitational constant in m^3 kg^-1 s^-2
        double planetMass = plMass * earthMass; // Planet mass in kg
        double planetGravity = G * planetMass / Math.pow(planetRadius, 2); // Gravity in m/s^2

        // Check if gravity is in the range [3, 20]
        if (planetGravity < 3) {
            return "Planet's gravity is too low to be habitable.";
        }
        else if (planetGravity > 20) {
            return "Planet's gravity is too high to be habitable.";
        }

        return "The planet is habitable.";
        // Later we will make it so if the planet is habitable
        // It will have a positive impact on the gameplay
        // And if it is not, it will have a negative impact
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

    public class Exoplanet {
        private String plName;     // Planet Name
        private double plOrbsMax;  // Orbital Semi-Major Axis (in AU)
        private double plRade;     // Planet Radius (in Earth radii)
        private double plBmasse;   // Planet Mass (in Earth masses)
        private double stRad;      // Star Radius (in Solar radii)
        private double stMass;     // Star Mass (in Solar masses)
        private double syDist;     // System Distance (in parsecs)

        // Constructor
        public Exoplanet(String plName, double plOrbsMax, double plRade, double plBmasse,
                         double stRad, double stMass, double syDist) {
            this.plName = plName;
            this.plOrbsMax = plOrbsMax;
            this.plRade = plRade;
            this.plBmasse = plBmasse;
            this.stRad = stRad;
            this.stMass = stMass;
            this.syDist = syDist;
        }

        // Getters and Setters
        public String getPlName() {
            return plName;
        }

        public void setPlName(String plName) {
            this.plName = plName;
        }

        public double getPlOrbsMax() {
            return plOrbsMax;
        }

        public void setPlOrbsMax(double plOrbsMax) {
            this.plOrbsMax = plOrbsMax;
        }

        public double getPlRade() {
            return plRade;
        }

        public void setPlRade(double plRade) {
            this.plRade = plRade;
        }

        public double getPlBmasse() {
            return plBmasse;
        }

        public void setPlBmasse(double plBmasse) {
            this.plBmasse = plBmasse;
        }

        public double getStRad() {
            return stRad;
        }

        public void setStRad(double stRad) {
            this.stRad = stRad;
        }

        public double getStMass() {
            return stMass;
        }

        public void setStMass(double stMass) {
            this.stMass = stMass;
        }

        public double getSyDist() {
            return syDist;
        }

        public void setSyDist(double syDist) {
            this.syDist = syDist;
        }

        public double diff(Exoplanet other) {
            // Definir los pesos para cada variable
            double[] weights = {15, 35, 35, 5, 10};

            // Calcular las diferencias ponderadas
            double diffPlOrbsMax = weights[0] * Math.pow(this.plOrbsMax - other.plOrbsMax, 2);
            double diffPlRade = weights[1] * Math.pow(this.plRade - other.plRade, 2);
            double diffPlBmasse = weights[2] * Math.pow(this.plBmasse - other.plBmasse, 2);
            double diffStRad = weights[3] * Math.pow(this.stRad - other.stRad, 2);
            double diffStMass = weights[4] * Math.pow(this.stMass - other.stMass, 2);

            // Sumar las diferencias ponderadas
            return diffPlOrbsMax + diffPlRade + diffPlBmasse + diffStRad + diffStMass;
        }

        @Override
        public String toString() {
            return "Exoplanet{" +
                    "plName='" + plName + '\'' +
                    ", plOrbsMax=" + plOrbsMax +
                    ", plRade=" + plRade +
                    ", plBmasse=" + plBmasse +
                    ", stRad=" + stRad +
                    ", stMass=" + stMass +
                    ", syDist=" + syDist +
                    '}';
        }
    }
}
