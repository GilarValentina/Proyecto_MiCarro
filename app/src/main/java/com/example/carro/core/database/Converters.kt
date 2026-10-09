package com.example.carro.core.database

import androidx.room.TypeConverter
import java.time.LocalDate

/** Conversores de tipos para Room. Las fechas se almacenan como día epoch (Long). */
class Converters {

    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? =
        value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun localDateToEpochDay(date: LocalDate?): Long? =
        date?.toEpochDay()
}
