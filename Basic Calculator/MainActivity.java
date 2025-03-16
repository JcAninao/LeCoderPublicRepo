package com.example.basiccalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    Button clear, clearEntry, divide, multiply, subtract, add, nine, eight, seven, six, five, four, three, two, one, zero, decimal, equal;
    TextView displayTxt, tmpAnswer, tmpFormula, debugger;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        clear = (Button) findViewById(R.id.clear);
        clearEntry = (Button) findViewById(R.id.clearEntry);
        divide = (Button) findViewById(R.id.divide);
        multiply = (Button) findViewById(R.id.multiply);
        subtract = (Button) findViewById(R.id.subtract);
        add = (Button) findViewById(R.id.add);
        decimal = (Button) findViewById(R.id.decimal);
        equal = (Button) findViewById(R.id.equal);

        nine = (Button) findViewById(R.id.nine);
        eight = (Button) findViewById(R.id.eight);
        seven = (Button) findViewById(R.id.seven);
        six = (Button) findViewById(R.id.six);
        five = (Button) findViewById(R.id.five);
        four = (Button) findViewById(R.id.four);
        three = (Button) findViewById(R.id.three);
        two = (Button) findViewById(R.id.two);
        one = (Button) findViewById(R.id.one);
        zero = (Button) findViewById(R.id.zero);

        displayTxt = (TextView) findViewById(R.id.displayTxt);
        tmpAnswer = (TextView) findViewById(R.id.tmpAnswer);
        tmpFormula = (TextView) findViewById(R.id.tmpFormula);
        debugger = (TextView) findViewById(R.id.debugger);

        View.OnClickListener numberClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                String currentText = displayTxt.getText().toString();

                if (currentText.equals("0")) {
                    displayTxt.setText(clickedButton.getText().toString());
                    debuggerTxt(clickedButton.getText().toString());
                } else {
                    displayTxt.setText(currentText + clickedButton.getText().toString());
                    debuggerTxt(clickedButton.getText().toString());
                }

            }
        };
        zero.setOnClickListener(numberClickListener);
        one.setOnClickListener(numberClickListener);
        two.setOnClickListener(numberClickListener);
        three.setOnClickListener(numberClickListener);
        four.setOnClickListener(numberClickListener);
        five.setOnClickListener(numberClickListener);
        six.setOnClickListener(numberClickListener);
        seven.setOnClickListener(numberClickListener);
        eight.setOnClickListener(numberClickListener);
        nine.setOnClickListener(numberClickListener);

        View.OnClickListener operationClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = displayTxt.getText().toString();
                if (tmpAnswer.getText().toString().isBlank()) tmpAnswer.setText("0");
                double tmpAnswerValue = Double.parseDouble(tmpAnswer.getText().toString());

                if (v.getId() == R.id.clear) { debuggerTxt("clear");
                    tmpFormula.setText("0");
                    tmpAnswer.setText("0");
                    displayTxt.setText("0");
                    tmpAnswerValue = 0.0;
                    lastOperatorSymbol = " = ";
                    return;
                } else if (v.getId() == R.id.clearEntry) { debuggerTxt("clearEntry");
                    displayTxt.setText("0");
                    return;
                } else if (v.getId() == R.id.decimal) { debuggerTxt("decimal point");
                    if (!currentText.contains(".")) {
                        displayTxt.setText(currentText + ".");
                    }
                } else if (v.getId() == R.id.equal) { debuggerTxt("equals");
                    if (lastOperatorSymbol.equals(" = ") || tmpAnswerValue == 0.0) return;
                    if (tmpAnswerValue == 0.0 && (lastOperatorSymbol.equals(" ÷ ") || lastOperatorSymbol.equals(" × "))) return;
                    finalAnswer(tmpAnswerValue);
                    lastOperatorSymbol = " = ";
                    tmpFormula.setText(tmpFormula.getText().toString() + lastOperatorSymbol);
                } else if (v.getId() == R.id.add) { debuggerTxt("add");
                    if (displayTxt.getText().toString().equals("0")) return;
                    lastOperatorSymbol = " + ";
                    if (tmpFormula.getText().toString().contains("=")){
                        tmpFormula.setText(tmpAnswer.getText().toString());
                        tmpAnswerValue = 0.0;
                    } else {
                        if (tmpFormula.getText().toString().equals("0")) {
                            tmpFormula.setText(displayTxt.getText().toString());
                            tmpAnswer.setText(displayTxt.getText().toString());
                        } else
                            tmpFormula.setText(tmpFormula.getText().toString() + " + " + displayTxt.getText().toString());
                    }
                    tmpAnswerValue += Double.parseDouble(displayTxt.getText().toString());
                    tmpAnswer.setText(String.valueOf(tmpAnswerValue));
                    displayTxt.setText("0");
                    return;
                } else if (v.getId() == R.id.subtract) {
                    lastOperatorSymbol = " - ";
                    if (tmpFormula.getText().toString().contains("=")){
                        tmpFormula.setText(tmpAnswer.getText().toString());
                        tmpAnswerValue = 0.0;
                    } else {
                        if (tmpFormula.getText().toString().equals("0")) {
                            tmpFormula.setText(displayTxt.getText().toString());
                            tmpAnswer.setText(displayTxt.getText().toString());
                        } else
                            tmpFormula.setText(tmpFormula.getText().toString() + " - " + displayTxt.getText().toString());
                    }
                    if (displayTxt.getText().toString().equals("0")) tmpFormula.setText(" 0"); debuggerTxt("subtract");
                    if (tmpAnswerValue == 0.0) tmpAnswerValue += Double.parseDouble(displayTxt.getText().toString()) * 2;
                    tmpAnswerValue -= Double.parseDouble(displayTxt.getText().toString());
                    tmpAnswer.setText(String.valueOf(tmpAnswerValue));
                    displayTxt.setText("0");
                    return;
                }else if (v.getId() == R.id.multiply) { debuggerTxt("multiply");
                    if (displayTxt.getText().toString().equals("0") || (tmpAnswerValue == 0.0 && lastOperatorSymbol.isBlank())) {
                        Toast.makeText(v.getContext(), "0 multiply anything is always 0", Toast.LENGTH_LONG).show();
                        return;
                    }
//                    if (displayTxt.getText().toString().equals("0")) return;
                    lastOperatorSymbol = " × ";
                    if (tmpFormula.getText().toString().contains("=")){
                        tmpFormula.setText(tmpAnswer.getText().toString());
                        tmpAnswerValue = 0.0;
                    } else {
                        if (tmpFormula.getText().toString().equals("0")) {
                            tmpFormula.setText(displayTxt.getText().toString());
                            tmpAnswer.setText(displayTxt.getText().toString());
                        } else
                            tmpFormula.setText(tmpFormula.getText().toString() + " × " + displayTxt.getText().toString());
                    }
                    if (tmpAnswerValue == 0.0) tmpAnswerValue = 1;
                    tmpAnswerValue *= Double.parseDouble(displayTxt.getText().toString());
                    tmpAnswer.setText(String.valueOf(tmpAnswerValue));
                    displayTxt.setText("0");
                    return;
                }else if (v.getId() == R.id.divide) { debuggerTxt("divide");
                    if (displayTxt.getText().toString().equals("0") || (tmpAnswerValue == 0.0 && lastOperatorSymbol.isBlank())) {
                        Toast.makeText(v.getContext(), "0 divided by something is an error\nor\nSomething divided by 0 is Undefined", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!lastOperatorSymbol.equals(" = ") || lastOperatorSymbol.isBlank()) finalAnswer(tmpAnswerValue);
                    lastOperatorSymbol = " ÷ ";
                    if (tmpFormula.getText().toString().contains("=")){
                        tmpFormula.setText(tmpAnswer.getText().toString());
                        tmpAnswerValue = Double.parseDouble(tmpFormula.getText().toString());
                    } else {
                        if (tmpFormula.getText().toString().equals("0")) {
                            tmpFormula.setText(displayTxt.getText().toString());
                            tmpAnswer.setText(displayTxt.getText().toString());
                        } else
                            tmpFormula.setText(tmpFormula.getText().toString() + " ÷ " + displayTxt.getText().toString());
                    }

                    tmpAnswerValue = Double.parseDouble(tmpFormula.getText().toString());
                    debugger.setText(String.valueOf(tmpAnswerValue));
//                    tmpAnswerValue /= Double.parseDouble(displayTxt.getText().toString());
                    tmpAnswer.setText(String.valueOf(tmpAnswerValue));
                    displayTxt.setText("0");
                    return;
                }



            }
        };
        clear.setOnClickListener(operationClickListener);
        clearEntry.setOnClickListener(operationClickListener);
        decimal.setOnClickListener(operationClickListener);
        equal.setOnClickListener(operationClickListener);
        add.setOnClickListener(operationClickListener);
        subtract.setOnClickListener(operationClickListener);
        divide.setOnClickListener(operationClickListener);
        multiply.setOnClickListener(operationClickListener);
    }
    private void finalAnswer(Double tmpAnswerValue){
        if (lastOperatorSymbol.equals(" + ")) {
            if (tmpFormula.getText().toString().equals("0")) {
                tmpFormula.setText(displayTxt.getText().toString());
                tmpAnswer.setText(displayTxt.getText().toString());
            } else
                tmpFormula.setText(tmpFormula.getText().toString() + " + " + displayTxt.getText().toString());
            tmpAnswerValue += Double.parseDouble(displayTxt.getText().toString());
            tmpAnswer.setText(String.valueOf(tmpAnswerValue));
            displayTxt.setText(String.valueOf(tmpAnswerValue));
            tmpAnswerValue = 0.0;
        } else if (lastOperatorSymbol.equals(" - ")) {
            if (tmpFormula.getText().toString().equals("0")) {
                tmpFormula.setText(displayTxt.getText().toString());
                tmpAnswer.setText(displayTxt.getText().toString());
            } else
                tmpFormula.setText(tmpFormula.getText().toString() + " - " + displayTxt.getText().toString());
            tmpAnswerValue -= Double.parseDouble(displayTxt.getText().toString());
            tmpAnswer.setText(String.valueOf(tmpAnswerValue));
            displayTxt.setText(String.valueOf(tmpAnswerValue));
            tmpAnswerValue = 0.0;
        } else if (lastOperatorSymbol.equals(" × ")) {
            if (tmpFormula.getText().toString().equals("0")) {
                tmpFormula.setText(displayTxt.getText().toString());
                tmpAnswer.setText(displayTxt.getText().toString());
            } else
                tmpFormula.setText(tmpFormula.getText().toString() + " × " + displayTxt.getText().toString());
            tmpAnswerValue *= Double.parseDouble(displayTxt.getText().toString());
            tmpAnswer.setText(String.valueOf(tmpAnswerValue));
            displayTxt.setText(String.valueOf(tmpAnswerValue));
            tmpAnswerValue = 0.0;
        } else if (lastOperatorSymbol.equals(" ÷ ")) {
            if (tmpFormula.getText().toString().equals("0")) {
                tmpFormula.setText(displayTxt.getText().toString());
                tmpAnswer.setText(displayTxt.getText().toString());
            } else
                tmpFormula.setText(tmpFormula.getText().toString() + " ÷ " + displayTxt.getText().toString());
            tmpAnswerValue /= Double.parseDouble(displayTxt.getText().toString());
            tmpAnswer.setText(String.valueOf(tmpAnswerValue));
            displayTxt.setText(String.valueOf(tmpAnswerValue));
            tmpAnswerValue = 0.0;
        }
    }
    static String lastOperatorSymbol = " = ";
    void debuggerTxt(String button){
        debugger.setText(button + " was clicked");
    }
}
