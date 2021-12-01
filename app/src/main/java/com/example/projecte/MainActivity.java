package com.example.projecte;

import androidx.annotation.CallSuper;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends DBActivity {
    protected EditText editA, editB, editC;
    protected Button btnCalculate, btnClear;
    protected ListView simpleList;
    protected void FillListView() throws Exception{
        final ArrayList<String> listResults=
                new ArrayList<>();
        SelectSQL(
                "SELECT * FROM CALDATABASE",
                null,
                (ID, A, B, C, Result)->{
                    listResults.add(ID+"\t"+A+"\t"+B+"\t"+C+ "\t" + Result + "\n");
                }
        );
        simpleList.clearChoices();
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(
                getApplicationContext(),
                R.layout.activity_listview,
                R.id.textView4,
                listResults

        );
        simpleList.setAdapter(arrayAdapter);
    }


    @Override
    @CallSuper
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try {
            FillListView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editA = findViewById(R.id.editA);
        editB = findViewById(R.id.editB);
        editC = findViewById(R.id.editC);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnClear = findViewById(R.id.btnClear);
        simpleList=findViewById(R.id.simpleList);

        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView clickedText = view.findViewById((R.id.textView4));
                String selected = clickedText.getText().toString();
                String[] elements = selected.split("\t");
                String ID = elements[0];
                String A = elements[1];
                String B = elements[2];
                String C = elements[3];
                String Result = elements[4];
                Intent intent = new Intent(MainActivity.this , UpdateDelete.class);
                Bundle b = new Bundle();
                b.putString("ID", ID);
                b.putString("A", A);
                b.putString("B", B);
                b.putString("C", C);
                b.putString("Result", Result);
                intent.putExtras(b);
                startActivityForResult(intent, 200, b);

            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        try {
            initDB();
            FillListView();
        } catch (Exception e) {
            e.printStackTrace();
        }


        btnCalculate.setOnClickListener(this::onClick);
        btnClear.setOnClickListener(this::onClickClear);
    }

    private void onClickClear(View view) {
        try{
            editA.setText("");
            editB.setText("");
            editC.setText("");

        }catch(Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Insert Failed: " + e.getLocalizedMessage()
                    , Toast.LENGTH_SHORT).show();
        }

    }


    private void openPopUpWindow() {
        Intent popUpWindow = new Intent(MainActivity.this, PopUpWindow.class);
        startActivity(popUpWindow);
    }


    private void onClick(View view) {
        try {
            double a = Double.parseDouble(String.valueOf(editA.getText()));
            double b =Double.parseDouble(String.valueOf(editB.getText()));
            double c = Double.parseDouble(String.valueOf(editC.getText()));

            double determinant = b * b - 4 * a * c;

            double root1 = (-b + Math.sqrt(determinant)) / (2 * a);
            double root2 = (-b - Math.sqrt(determinant)) / (2 * a);

            DecimalFormat df = new DecimalFormat("##.#");

            String result =  df.format(root1) + ", " + df.format(root2);

            if ( a == 0 || determinant < 0 ) {
                Toast.makeText(getApplicationContext(), "Invalid equation", Toast.LENGTH_LONG).show();
                return;
            }
            ExecSQL(
                    "INSERT INTO CALDATABASE (A, B, C, Result) " +
                            "VALUES(?, ?, ?, ?) ",
                    new Object[]{a, b, c, result},
                    () -> Toast.makeText(getApplicationContext(),
                            "Record Inserted", Toast.LENGTH_LONG).show()

            );


            Thread t = new Thread(new Runnable() {


                @Override
                public void run() {
                    final StringBuilder jsonObject = new StringBuilder();
                    double a = Double.parseDouble(String.valueOf(editA.getText()));
                    double b =Double.parseDouble(String.valueOf(editB.getText()));
                    double c = Double.parseDouble(String.valueOf(editC.getText()));

                    double determinant = b * b - 4 * a * c;

                    double root1 = (-b + Math.sqrt(determinant)) / (2 * a);
                    double root2 = (-b - Math.sqrt(determinant)) / (2 * a);

                    DecimalFormat df = new DecimalFormat("##.#");

                    String res =  df.format(root1) + ", " + df.format(root2);

                    jsonObject.append("{");
                    jsonObject.append("'a': '" + a + "', ");
                    jsonObject.append("'b': '" + b + "', ");
                    jsonObject.append("'c': '" + c + "', ");
                    jsonObject.append("'result': '" + res + "' ");
                    jsonObject.append("}");
                    final StringBuilder result = new StringBuilder();
                    try {
                        result.append(postData("SaveToFile",
                                editA.getText().toString(),
                                jsonObject.toString()
                        ));
                        JSONObject jo = (JSONObject) new JSONTokener(result.toString())
                                .nextValue();
                        final String message = jo.getString("message");
                        if (message == null) {
                            throw new Exception("SERVER FAULT: " + result.toString());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), message,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (final Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Exception: " + e.getLocalizedMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });

            Intent thirdscreen = new Intent(MainActivity.this, PopUpWindow.class);

            //Sending data to another Activity
            thirdscreen.putExtra("result", result);
            startActivity(thirdscreen);
            //openPopUpWindow();
            t.start();
            FillListView();


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Insert Failed: " + e.getLocalizedMessage()
                    , Toast.LENGTH_SHORT).show();
        }


    }
}