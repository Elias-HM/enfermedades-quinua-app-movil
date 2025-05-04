package com.epiis.detectaquinua;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class ResultadoActivity extends BaseActivity {

    ImageView imageViewResultado;
    TextView lblPrediccion, lblPrecision, lblInterpretacion;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setActivityLayout(R.layout.activity_resultado);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle(getString(R.string.result_title));

        imageViewResultado = findViewById(R.id.imgResultado);
        lblPrediccion = findViewById(R.id.lblPrediccion);
        lblPrecision = findViewById(R.id.lblPrecision);
        lblInterpretacion = findViewById(R.id.lblInterpretacionPrecision);
        progressBar = findViewById(R.id.progressBarPrecision);

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");
        String prediccion = intent.getStringExtra("prediccion");
        Double precision = Double.parseDouble(intent.getStringExtra("precision"));
        String interpretacion = "";

        if (precision>=90) interpretacion=getString(R.string.result_lbl_interpretacion_0);
        else if (precision>=75) interpretacion=getString(R.string.result_lbl_interpretacion_1);
        else if (precision>=60) interpretacion=getString(R.string.result_lbl_interpretacion_2);
        else  interpretacion=getString(R.string.result_lbl_interpretacion_3);

        if (imagePath != null) {
            imageViewResultado.setImageURI(Uri.fromFile(new File(imagePath)));
        }

        lblPrediccion.setText(getString(R.string.result_lbl_prediccion) + " " + prediccion);
        lblPrecision.setText(getString(R.string.result_lbl_precision) + " " + precision + "%");
        progressBar.setProgress(precision.intValue());
        lblInterpretacion.setText(interpretacion);
    }
}