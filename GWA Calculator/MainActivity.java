import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
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
    private Button addSubjectButton, removeSubjectButton, calculateButton, resetButton;
    private TextView resultTextView, subjectCountTextView;
    private List<View> subjectViews = new ArrayList<>();
    private Handler longPressHandler = new Handler();
    private boolean isLongPressTriggered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subjectsLayout = findViewById(R.id.subjectsLayout);
        addSubjectButton = findViewById(R.id.addSubjectButton);
        removeSubjectButton = findViewById(R.id.removeSubjectButton);
        calculateButton = findViewById(R.id.calculateButton);
        resetButton = findViewById(R.id.resetButton);
        resultTextView = findViewById(R.id.resultTextView);
        subjectCountTextView = findViewById(R.id.subjectCountTextView);

        addSubjectButton.setOnClickListener(v -> addSubjectInput(-1));
        removeSubjectButton.setOnClickListener(v -> removeLastSubject());
        calculateButton.setOnClickListener(v -> calculateGWA());
        resetButton.setOnClickListener(v -> showResetOptions());

        load2ndSemPreset(-1);
    }

    private void showResetOptions() {
        new AlertDialog.Builder(this)
                .setTitle("Choose a preset")
                .setItems(new CharSequence[]{" Load 1st Sem", " Load 2nd Sem"}, (dialog, which) -> {
                    if (which == 0) load1stSemPreset(-1);
                    else if (which == 1) load2ndSemPreset(-1);
                })
                .setNegativeButton("Clear Entry", (dialog, which) -> resetInputs())
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void addSubjectInput(int index) {
        View subjectView = getLayoutInflater().inflate(R.layout.subject_input, null);

        EditText subjectCodeInput = subjectView.findViewById(R.id.subjectCodeInput);
        EditText unitsInput = subjectView.findViewById(R.id.unitsInput);
        EditText gradeInput = subjectView.findViewById(R.id.gradeInput);

        if (index >= 0 && isPreset2ndSem == false) {
            String[][] subjects = {
                    {"CCS 116", "5", "1.75"},
                    {"CCS 125", "3", "1.5"},
                    {"CS 110", "3", "1.5"},
                    {"CS 116", "3", "1.5"},
                    {"CSE 101", "3", "1.25"},
                    {"GEC 007", "3", "1.25"},
                    {"GEC 008", "3", "1.25"}
            };
            subjectCodeInput.setText(subjects[index][0]);
            unitsInput.setText(subjects[index][1]);
            gradeInput.setText(subjects[index][2]);
        }else if (index >= 0 && isPreset2ndSem == true) {
            String[][] subjects = {
                    {"CCS 106", "5", ""},
                    {"CCS 126", "3", ""},
                    {"CS 111", "3", ""},
                    {"CSE 102", "3", ""},
                    {"GEC 006", "3", ""},
                    {"GEM 001", "3", ""},
                    {"RES 001", "3", ""}
            };
            subjectCodeInput.setText(subjects[index][0]);
            unitsInput.setText(subjects[index][1]);
            gradeInput.setText(subjects[index][2]);
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
        int missingFieldCounter = 0;
        int invalidGradeCounter = 0;
        int invalidUnitCounter = 0;

        for (int i = 0; i < subjectsLayout.getChildCount(); i++) {
            View view = subjectsLayout.getChildAt(i);
            EditText unitsInput = view.findViewById(R.id.unitsInput);
            EditText gradeInput = view.findViewById(R.id.gradeInput);

            String gradeText = gradeInput.getText().toString().trim();
            String unitsText = unitsInput.getText().toString().trim();

            if (gradeText.isEmpty() || unitsText.isEmpty()) {
                missingFieldCounter++;
                continue;
            }

            double grade;
            int units;

            try {
                grade = Double.parseDouble(gradeText);
                units = Integer.parseInt(unitsText);
            } catch (NumberFormatException e) {
                missingFieldCounter++;
                continue;
            }

            if (units <= 0) {
                invalidUnitCounter++;
                continue;
            }

            if (grade >= 1 && grade <= 5) {
                totalWeightedGrade += grade * units;
                totalUnits += units;
                countedSubjects++;
            } else if (grade >= 60 && grade <= 100) {
                grade = gwaConverter(grade);
                totalWeightedGrade += grade * units;
                totalUnits += units;
                countedSubjects++;
            } else invalidGradeCounter++;
        }

        if (missingFieldCounter != 0) {
            Toast.makeText(this, "(" + missingFieldCounter + ") Missing field(s) detected.\nPlease enter valid numbers", Toast.LENGTH_SHORT).show();
        }
        if (invalidGradeCounter != 0) {
            Toast.makeText(this, "(" + invalidGradeCounter + ") Invalid grade(s) detected.\nPlease enter 1 ~ 5 or 60 ~ 100", Toast.LENGTH_SHORT).show();
        }
        if (invalidUnitCounter != 0) {
            Toast.makeText(this, "(" + invalidUnitCounter + ") Invalid unit(s) detected.\nPlease enter positive unit", Toast.LENGTH_SHORT).show();
        }

        if (totalUnits > 0) {
            double gwa = totalWeightedGrade / totalUnits;
            resultTextView.setText("GWA: " + String.format("%.3f", gwa));
        } else resultTextView.setText("GWA: N/A");

        subjectCountTextView.setText("Subjects: " + countedSubjects);
        Log.d("DEBUG", "total weighted ave: " + totalWeightedGrade +
                "\ntotal units: " + totalUnits +
                "\ncounted subjects: " + countedSubjects +
                "\nrow(s) with missing field(s): " + missingFieldCounter +
                "\ntotal units: " + (totalWeightedGrade / totalUnits));
    }

    private double gwaConverter(double grade) {
        if (grade >= 97) return 1.0;
        else if (grade >= 94) return 1.25;
        else if (grade >= 91) return 1.5;
        else if (grade >= 88) return 1.75;
        else if (grade >= 85) return 2.0;
        else if (grade >= 82) return 2.25;
        else if (grade >= 79) return 2.5;
        else if (grade >= 76) return 2.75;
        else if (grade == 75) return 3.0;
        else return 5.0;
    }

    private void resetInputs() {
        subjectsLayout.removeAllViews();
        subjectViews.clear();
        resultTextView.setText("GWA: ");
        subjectCountTextView.setText("Subjects: ");
        addSubjectInput(-1);
        addSubjectInput(-1);
    }

    private void load1stSemPreset(int index) {
        isPreset2ndSem = false;
        Toast.makeText(this, "1st sem was loaded", Toast.LENGTH_LONG).show();
        subjectsLayout.removeAllViews();
        subjectViews.clear();
        resultTextView.setText("GWA: ");
        subjectCountTextView.setText("Subjects: ");
        if (index == -1) {
            for (int i = 0; i < 7; i++) {
                addSubjectInput(i);
            }
        }
    }
    private void load2ndSemPreset(int index) {
        isPreset2ndSem = true;
        Toast.makeText(this, "2nd sem was loaded", Toast.LENGTH_LONG).show();
        subjectsLayout.removeAllViews();
        subjectViews.clear();
        resultTextView.setText("GWA: ");
        subjectCountTextView.setText("Subjects: ");
        if (index == -1) {
            for (int i = 0; i < 7; i++) {
                addSubjectInput(i);
            }
        }
    }
    static boolean isPreset2ndSem = true;
}

