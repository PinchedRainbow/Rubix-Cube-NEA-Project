package com.jawaadianinc.rubixcubesolver;

import static com.jawaadianinc.rubixcubesolver.MainActivity.COLORS_INPUTTED_BACK;
import static com.jawaadianinc.rubixcubesolver.MainActivity.COLORS_INPUTTED_DOWN;
import static com.jawaadianinc.rubixcubesolver.MainActivity.COLORS_INPUTTED_FRONT;
import static com.jawaadianinc.rubixcubesolver.MainActivity.COLORS_INPUTTED_LEFT;
import static com.jawaadianinc.rubixcubesolver.MainActivity.COLORS_INPUTTED_RIGHT;
import static com.jawaadianinc.rubixcubesolver.MainActivity.COLORS_INPUTTED_UP;
import static com.jawaadianinc.rubixcubesolver.MainActivity.INITIAL_INPUT_TYPE;
import static com.jawaadianinc.rubixcubesolver.MainActivity.MANUAL_COLOR_INPUT;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class TextSolutionFragment extends Fragment implements View.OnClickListener,
        com.jawaadianinc.rubixcubesolver.EditScrambleDialog.EditScrambleDialogListener, SeekBar.OnSeekBarChangeListener {

    private final char[][][] allColorsInputted = new char[6][3][3];
    private View rootView;
    private CubeView cubeView;
    private String inputType;

    public TextSolutionFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_solution, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cubeView = rootView.findViewById(R.id.cube_view);
        rootView.findViewById(R.id.rewind).setOnClickListener(this);
        rootView.findViewById(R.id.skip_forward).setOnClickListener(this);
        SeekBar seekBar = rootView.findViewById(R.id.speed_adjuster);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(14);

        Bundle args = getArguments();
        inputType = (args != null) ? args.getString(INITIAL_INPUT_TYPE) : " ";
        if (inputType != null && inputType.equals(MANUAL_COLOR_INPUT)) {
            try {
                cubeView.resetScrambleByColorInputs(allColorsInputted);

                TextView scrambleView = rootView.findViewById(R.id.scramble_view);
                scrambleView.setText("");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Please make valid inputs", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (inputType.equals(MANUAL_COLOR_INPUT)) {
            ((Activity) requireContext()).getMenuInflater().inflate(R.menu.menu_color_solution, menu);
        } else {
            ((Activity) requireContext()).getMenuInflater().inflate(R.menu.menu_text_solution, menu);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void updateMoves(String movesToPerform, String movesPerformed, String phase) {
        TextView toPerformView = rootView.findViewById(R.id.moves_to_perform);
        toPerformView.setText(movesToPerform);
        TextView performedView = rootView.findViewById(R.id.moves_performed);
        performedView.setText(movesPerformed);
        TextView phaseView = rootView.findViewById(R.id.phase_view);
        phaseView.setText(phase);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skip_forward:
                cubeView.skipToPhase(cubeView.getPhase() + 1);
                break;
            case R.id.rewind:
                cubeView.skipToPhase(cubeView.getPhase() - 1);
                break;
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String scramble) {
        TextView scrambleView = rootView.findViewById(R.id.scramble_view);
        scrambleView.setText(scramble);
        cubeView.resetScramble(scramble);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (cubeView.getAnimationStopped()) {
            cubeView.setSpeedMultiplier(1 + progress);
        } else {
            cubeView.stopAnimation();
            cubeView.startAnimation(1 + progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_random:
                TextView scrambleView = rootView.findViewById(R.id.scramble_view);
                scrambleView.setText(cubeView.randScramble());
                break;
            case R.id.action_reset:
                cubeView.resetCurrentScramble();
                break;
            case R.id.action_edit_scramble:
                if (inputType.equals(MANUAL_COLOR_INPUT)) {
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Bundle args = new Bundle();
                    args.putString(com.jawaadianinc.rubixcubesolver.EditScrambleDialog.SCRAMBLE_TAG,
                            ((TextView) rootView.findViewById(R.id.scramble_view)).getText().toString());
                    com.jawaadianinc.rubixcubesolver.EditScrambleDialog dialog = new com.jawaadianinc.rubixcubesolver.EditScrambleDialog();
                    dialog.setArguments(args);
                    dialog.show(((AppCompatActivity) requireContext())
                            .getSupportFragmentManager(), "edit scramble");
                    break;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if (args != null) {
            String initInputType = args.getString(INITIAL_INPUT_TYPE);
            if (initInputType != null && initInputType.equals(MANUAL_COLOR_INPUT)) {
                allColorsInputted[0] = unpackArrays(args.getCharArray(COLORS_INPUTTED_LEFT));
                allColorsInputted[1] = unpackArrays(args.getCharArray(COLORS_INPUTTED_UP));
                allColorsInputted[2] = unpackArrays(args.getCharArray(COLORS_INPUTTED_FRONT));
                allColorsInputted[3] = unpackArrays(args.getCharArray(COLORS_INPUTTED_BACK));
                allColorsInputted[4] = unpackArrays(args.getCharArray(COLORS_INPUTTED_RIGHT));
                allColorsInputted[5] = unpackArrays(args.getCharArray(COLORS_INPUTTED_DOWN));
            }
        }
    }

    private char[][] unpackArrays(char[] colorsArray) {
        char[][] unpackedArray = new char[3][3];
        for (int i = 0; i < colorsArray.length; i++) {
            unpackedArray[i / 3][i % 3] = colorsArray[i];
        }
        return unpackedArray;
    }
}
