package com.example.projecte;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;

public class UpdateDelete extends DBActivity {
    protected EditText editA, editB, editC;
    protected Button btnDelete, btnUpdate;
    protected  String ID;
    private void BackToMain(){
        finishActivity(200);
        Intent i = new Intent(UpdateDelete.this, MainActivity.class);
        startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete);
        editA = findViewById(R.id.editA);
        editB = findViewById(R.id.editB);
        editC = findViewById(R.id.editC);
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);
        Bundle e = getIntent().getExtras();
        if(e!=null){
            ID = e.getString("ID");
            editA.setText(e.getString("A"));
            editB.setText(e.getString("B"));
            editC.setText(e.getString("C"));
        }
        btnDelete.setOnClickListener(view ->{
            try {
                ExecSQL("DELETE FROM CALDATABASE WHERE " + "ID = ?",
                        new Object[]{ID},()->Toast.makeText(getApplicationContext(),"Delete Succesful", Toast.LENGTH_LONG).show());
            }catch (Exception exception){
                Toast.makeText(getApplicationContext(), "Delete Error: " + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
            finally {
                BackToMain();
            }
        });

        btnUpdate.setOnClickListener(view ->{

            try {

                double a = Double.parseDouble(String.valueOf(editA.getText()));
                double b =Double.parseDouble(String.valueOf(editB.getText()));
                double c = Double.parseDouble(String.valueOf(editC.getText()));

                double determinant = b * b - 4 * a * c;

                double root1 = (-b + Math.sqrt(determinant)) / (2 * a);
                double root2 = (-b - Math.sqrt(determinant)) / (2 * a);

                DecimalFormat df = new DecimalFormat("##.##");
                String result =  df.format(root1) + ", " + df.format(root2);

                if(a == 0 || determinant < 0 ){
                    Toast.makeText(getApplicationContext(), "Invalid equation", Toast.LENGTH_LONG).show();
                    return;
                }
                ExecSQL("UPDATE CALDATABASE SET " +
                                "A = ?, " +
                                "B = ?, " +
                                "C = ?, " +
                                "Result = ? " +
                                "WHERE ID = ?",
                        new Object[]{a, b, c, result},
                        ()->Toast.makeText(getApplicationContext(),"Update Succesful", Toast.LENGTH_LONG).show());
            }catch (Exception exception){
                Toast.makeText(getApplicationContext(), "Update Error: " + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
            finally {
                BackToMain();
            }
        });

    }
}