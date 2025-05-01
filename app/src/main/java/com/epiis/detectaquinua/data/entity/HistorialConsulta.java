package com.epiis.detectaquinua.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "historial")
public class HistorialConsulta {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String fecha;
    public String rutaImagen;
    public String prediccion;
    public float precision;
}