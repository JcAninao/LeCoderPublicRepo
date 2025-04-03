package com.example.gwacalculator;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LinearLayout subjectsLayout;
    private Button addSubjectButton, removeSubjectButton, calculateButton, presetButton;
    private TextView resultTextView, subjectCountTextView;
    private List<View> subjectViews = new ArrayList<>();
    private boolean isPreset2ndSem = true;
    private final double midTermPercentage = 0.4;
    private final double finalTermPercentage = 0.6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subjectsLayout = findViewById(R.id.subjectsLayout);
        addSubjectButton = findViewById(R.id.addSubjectButton);
        removeSubjectButton = findViewById(R.id.removeSubjectButton);
        calculateButton = findViewById(R.id.calculateButton);
        presetButton = findViewById(R.id.presetButton);
        resultTextView = findViewById(R.id.resultTextView);
        subjectCountTextView = findViewById(R.id.subjectCountTextView);

        addSubjectButton.setOnClickListener(v -> addSubjectInput(-1));
        removeSubjectButton.setOnClickListener(v -> removeLastSubject());
        calculateButton.setOnClickListener(v -> calculateGWA());
        presetButton.setOnClickListener(v -> showPresetOptions());

        load2ndSemPreset();
    }

    private void showPresetOptions() {
        new AlertDialog.Builder(this)
                .setTitle("Choose a preset")
                .setItems(new CharSequence[]{" Load 1st Sem", " Load 2nd Sem"}, (dialog, which) -> {
                    if (which == 0) load1stSemPreset();
                    else if (which == 1) load2ndSemPreset();
                })
                .setNegativeButton("Clear Entry", (dialog, which) -> resetInputs())
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void addSubjectInput(int index) {
        View subjectView = getLayoutInflater().inflate(R.layout.subject_input, null);

        EditText subjectCodeInput = subjectView.findViewById(R.id.subjectCodeInput);
        EditText unitsInput = subjectView.findViewById(R.id.unitsInput);
        EditText midTermInput = subjectView.findViewById(R.id.midTermInput);
        EditText finalTermInput = subjectView.findViewById(R.id.finalTermInput);
        EditText finalGradeInput = subjectView.findViewById(R.id.finalGradeInput);

        if (index >= 0) {
            String[][] firstSemSubjects = {
                    {"CCS 116", "5", "1.75"},
                    {"CCS 125", "3", "1.5"},
                    {"CS 110", "3", "1.5"},
                    {"CS 116", "3", "1.5"},
                    {"CSE 101", "3", "1.25"},
                    {"GEC 007", "3", "1.25"},
                    {"GEC 008", "3", "1.25"}
            };

            String[][] secondSemSubjects = {
                    {"CCS 106", "5", ""},
                    {"CCS 126", "3", ""},
                    {"CS 111", "3", ""},
                    {"CSE 102", "3", ""},
                    {"GEC 006", "3", ""},
                    {"GEM 001", "3", ""},
                    {"RES 001", "3", ""}
            };

            String[][] subjects = isPreset2ndSem ? secondSemSubjects : firstSemSubjects;
            subjectCodeInput.setText(subjects[index][0]);
            unitsInput.setText(subjects[index][1]);
            finalGradeInput.setText(subjects[index][2]);
        }

        subjectsLayout.addView(subjectView);
        subjectViews.add(subjectView);
    }

    private void removeLastSubject() {
        if (subjectViews.size() > 2) {
            View lastSubject = subjectViews.remove(subjectViews.size() - 1);
            subjectsLayout.removeView(lastSubject);
        } else {
            Toast.makeText(this, "At least 2 subjects are required", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateGWA() {
        double totalWeightedGrade = 0;
        int totalUnits = 0;
        int countedSubjects = 0;
        int vitalMissingFields = 0, invalidGrades = 0, invalidUnits = 0, minorMissingFields = 0;

        for (View view : subjectViews) {
            EditText unitsInput = view.findViewById(R.id.unitsInput);
            EditText midTermInput = view.findViewById(R.id.midTermInput);
            EditText finalTermInput = view.findViewById(R.id.finalTermInput);
            EditText finalGradeInput = view.findViewById(R.id.finalGradeInput);

            String unitsText = unitsInput.getText().toString().trim();
            String midTermText = midTermInput.getText().toString().trim();
            String finalTermText = finalTermInput.getText().toString().trim();
            String finalGradeText = finalGradeInput.getText().toString().trim();

            if (unitsText.isEmpty()) {
                invalidUnits++;
                continue;
            }
            if (!midTermText.isEmpty() && !finalTermText.isEmpty()){
                finalGradeText = "";
            }

            int units;
            double finalGrade;
            double midTerm = 0;
            double finalTerm = 0;

            try {
                units = Integer.parseInt(unitsText);
                if (!finalGradeText.isEmpty()) {
                    finalGrade = Double.parseDouble(finalGradeText);
                    if (midTermText.isEmpty() || finalTermText.isEmpty()) minorMissingFields++;
                } else {
                    midTerm = Double.parseDouble(midTermText);
                    finalTerm = Double.parseDouble(finalTermText);
                    finalGrade = (midTerm * midTermPercentage) + (finalTerm * finalTermPercentage);
                    finalGradeInput.setText(Double.toString(finalGrade));
                }
            } catch (NumberFormatException e) {
                vitalMissingFields++;
                continue;
            }


            if (units <= 0) {
                invalidUnits++;
                continue;
            }

            if (((finalTerm >= 1 && finalTerm <= 5) || (finalTerm <= 100 && finalTerm >= 60)) ||
                    ((midTerm >= 1 && midTerm <= 5) || (midTerm <= 100 && midTerm >= 60)) ||
                    ((finalGrade >= 1 && finalGrade <= 5) || (finalGrade <= 100 && finalGrade >= 60))) {
                if (finalTerm >= 60) finalGrade = gwaConverter(finalGrade);
                if (midTerm >= 60) finalGrade = gwaConverter(finalGrade);
                if (finalGrade >= 60) finalGrade = gwaConverter(finalGrade);
                totalWeightedGrade += finalGrade * units;
                totalUnits += units;
                countedSubjects++;
            } else {
                invalidGrades++;
            }
        }

        if (vitalMissingFields > 0)
            Toast.makeText(this, vitalMissingFields + " missing vital field(s)", Toast.LENGTH_SHORT).show();
        if (invalidGrades > 0)
            Toast.makeText(this, invalidGrades + " invalid grade(s)", Toast.LENGTH_SHORT).show();
        if (invalidUnits > 0)
            Toast.makeText(this, invalidUnits + " invalid unit(s)", Toast.LENGTH_SHORT).show();
        if (minorMissingFields > 0)
            Toast.makeText(this, minorMissingFields +  " empty field(s) is ignored since\nvital final grade was filled", Toast.LENGTH_SHORT).show();



        resultTextView.setText(totalUnits > 0 ? "GWA: " + String.format("%.3f", totalWeightedGrade / totalUnits) : "GWA: N/A");
        subjectCountTextView.setText("Subjects: " + countedSubjects);
    }

    private double gwaConverter(double grade) {
        if (grade <= 100 && grade >= 97) return 1.0;
        if (grade >= 94) return 1.25;
        if (grade >= 91) return 1.5;
        if (grade >= 88) return 1.75;
        if (grade >= 85) return 2.0;
        if (grade >= 82) return 2.25;
        if (grade >= 79) return 2.5;
        if (grade >= 76) return 2.75;
        if (grade == 75) return 3.0;
        return 5.0;
    }

    private void resetInputs() {
        subjectsLayout.removeAllViews();
        subjectViews.clear();
        resultTextView.setText("GWA: ");
        subjectCountTextView.setText("Subjects: 0");
        addSubjectInput(-1);
        addSubjectInput(-1);
    }

    private void load1stSemPreset() {
        isPreset2ndSem = false;
        subjectsLayout.removeAllViews();
        subjectViews.clear();
        resultTextView.setText("GWA: ");
        subjectCountTextView.setText("Subjects: 0");
        Toast.makeText(this, "1st sem loaded", Toast.LENGTH_LONG).show();
        for (int i = 0; i < 7; i++) addSubjectInput(i);
    }

    private void load2ndSemPreset() {
        isPreset2ndSem = true;
        subjectsLayout.removeAllViews();
        subjectViews.clear();
        resultTextView.setText("GWA: ");
        subjectCountTextView.setText("Subjects: 0");
        Toast.makeText(this, "2nd sem loaded", Toast.LENGTH_LONG).show();
        for (int i = 0; i < 7; i++) addSubjectInput(i);
    }
}
