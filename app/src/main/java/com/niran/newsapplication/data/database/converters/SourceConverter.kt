package com.niran.newsapplication.data.database.converters

import androidx.room.TypeConverter
import com.niran.newsapplication.data.models.Source

class SourceConverter {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(sourceName: String): Source {
        return Source(sourceName, sourceName)
    }

}