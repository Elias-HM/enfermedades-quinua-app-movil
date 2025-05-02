package com.epiis.detectaquinua;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class ResultadoActivity extends BaseActivity {

    private ImageView imageViewResultado;
    private TextView txtResultado;

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
        txtResultado = findViewById(R.id.lblResultado);

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");
        String resultado = intent.getStringExtra("resultado");

        if (imagePath != null) {
            imageViewResultado.setImageURI(Uri.fromFile(new File(imagePath)));
        }

        txtResultado.setText(resultado);
    }
}