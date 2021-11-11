package com.jawaadianinc.rubixcubesolver;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements EditScrambleDialog.EditScrambleDialogListener {

    public static final String INITIAL_INPUT_TYPE = "initial input type";
    public static final String MANUAL_COLOR_INPUT = "manual color input";
    public static final String CAMERA_INPUT = "camera input";
    public static final String ALL_COLORS_INPUTTED = "all colors inputted";
    public static final String COLORS_INPUTTED_LEFT = "colors inputted left";
    public static final String COLORS_INPUTTED_UP = "colors inputted up";
    public static final String COLORS_INPUTTED_FRONT = "colors inputted front";
    public static final String COLORS_INPUTTED_BACK = "colors inputted back";
    public static final String COLORS_INPUTTED_RIGHT = "colors inputted right";
    public static final String COLORS_INPUTTED_DOWN = "colors inputted down";

    private final String TEXT_SCRAMBLE = "text scramble";
    private final String COLOR_INPUT = "color input";
    private String currentMode = TEXT_SCRAMBLE;
    private TextSolutionFragment solutionFragment;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView drawerList;
    private String[] navDrawerTitles;

//    @Override
//    public void onBackPressed() {
//        if (getFragmentManager().getBackStackEntryCount() > 0) {
//            getFragmentManager().popBackStack();
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navDrawerTitles = getResources().getStringArray(R.array.nav_drawer_items);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.left_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.open_drawer, R.string.close_drawer);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerList.setNavigationItemSelectedListener(new DrawerClickListener());


        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            Bundle args = (extras != null) ? extras.getBundle(ALL_COLORS_INPUTTED) : null;
            String inputType = (args != null) ? args.getString(INITIAL_INPUT_TYPE) : " ";

            if (inputType != null && inputType.equals(CAMERA_INPUT)) {
                Log.d("Starting Color Input", "true");
                com.jawaadianinc.rubixcubesolver.ColorInputFragment colorInputFragment = new com.jawaadianinc.rubixcubesolver.ColorInputFragment();

                colorInputFragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, colorInputFragment, "Color Input Fragment")
                        .addToBackStack(null)
                        .commit();
                drawerList.setCheckedItem(R.id.color_input);
            } else {
                solutionFragment = new TextSolutionFragment();

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, solutionFragment, "Text Solution Fragment")
                        .addToBackStack(null)
                        .commit();
                drawerList.setCheckedItem(R.id.text_scramble);
            }
        }

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String scramble) {
        solutionFragment.onDialogPositiveClick(dialog, scramble);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private class DrawerClickListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.text_scramble:
                    if (!currentMode.equals(TEXT_SCRAMBLE)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new TextSolutionFragment(), "Text Solution Fragment")
                                .addToBackStack(null)
                                .commit();
                        currentMode = TEXT_SCRAMBLE;
                        drawerLayout.closeDrawers();
                    }
                    break;
                case R.id.color_input:
                    if (!currentMode.equals(COLOR_INPUT)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new com.jawaadianinc.rubixcubesolver.ColorInputFragment(), "Color Input Fragment")
                                .addToBackStack(null)
                                .commit();
                        currentMode = COLOR_INPUT;
                        drawerLayout.closeDrawers();
                    }
                    break;
                case R.id.camera_input:
                    if (!currentMode.equals("Camera Input")) {
                        Intent intent = new Intent(getApplicationContext(), CaptureCubeActivity.class);
                        startActivity(intent);
                        currentMode = "Camera Input";
                        drawerList.setCheckedItem(R.id.color_input);
                        drawerLayout.closeDrawers();
                    }
                    break;
            }
            return true;
        }
    }

}
