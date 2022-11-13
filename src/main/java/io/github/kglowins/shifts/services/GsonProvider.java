package io.github.kglowins.shifts.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.kglowins.shifts.enums.ShiftWindow;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GsonProvider {

    private GsonProvider() {
    }

    public static Gson provide() {
        return new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
            .registerTypeAdapter(ShiftWindow.class, new ShiftWindowAdapter().nullSafe())
            .create();
    }

    public static final class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
            jsonWriter.value(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        @Override
        public LocalDate read(final JsonReader jsonReader) throws IOException {
            return LocalDate.parse(jsonReader.nextString(), DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    public static final class ShiftWindowAdapter extends TypeAdapter<ShiftWindow> {
        @Override
        public void write(final JsonWriter jsonWriter, final ShiftWindow shiftWindow) throws IOException {
            jsonWriter.value(shiftWindow.displayName());
        }

        @Override
        public ShiftWindow read(final JsonReader jsonReader) throws IOException {
            return ShiftWindow.fromDisplayName(jsonReader.nextString());
        }
    }
}
