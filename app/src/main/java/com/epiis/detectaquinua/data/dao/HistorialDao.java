package com.epiis.detectaquinua.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.epiis.detectaquinua.data.entity.HistorialConsulta;

import java.util.List;

@Dao
public interface HistorialDao {
    @Insert
    void insertar(HistorialConsulta consulta);

    @Query("SELECT * FROM historial ORDER BY id DESC")
    List<HistorialConsulta> obtenerHistorial();

    @Delete
    void eliminar(HistorialConsulta consulta);

    @Query("SELECT COUNT(*) FROM historial")
    int contarHistorial();

    @Query("SELECT * FROM historial WHERE id = :id")
    HistorialConsulta obtenerPorId(int id);
}