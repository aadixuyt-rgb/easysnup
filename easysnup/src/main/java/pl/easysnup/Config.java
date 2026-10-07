package pl.easysnup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Config {
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Gdy true, data nie pokazuje sie na kostiumie. */
    public static boolean forever = false;
    public static String expiry = LocalDateTime.now().plusDays(30).withNano(0).toString();
    /** Odcisk przedmiotu przebranego w kostium (wersja swap). */
    public static String fingerprint = "";

    private static final class Data {
        boolean forever;
        String expiry;
        String fingerprint;
    }

    private Config() {}

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("easysnup.json");
    }

    public static void load() {
        try {
            Path p = path();
            if (Files.exists(p)) {
                Data d = GSON.fromJson(Files.readString(p), Data.class);
                if (d != null) {
                    forever = d.forever;
                    if (d.expiry != null) expiry = d.expiry;
                    if (d.fingerprint != null) fingerprint = d.fingerprint;
                }
            }
        } catch (Exception ignored) {
        }
        Disguise.refreshFingerprint();
    }

    public static void save() {
        try {
            Data d = new Data();
            d.forever = forever;
            d.expiry = expiry;
            d.fingerprint = fingerprint;
            Files.writeString(path(), GSON.toJson(d));
        } catch (IOException ignored) {
        }
    }

    public static LocalDateTime expiryDate() {
        try {
            return LocalDateTime.parse(expiry);
        } catch (Exception e) {
            return LocalDateTime.now().plusDays(30).withNano(0);
        }
    }

    public static String expiryText() {
        return DATE.format(expiryDate());
    }

    public static void setExpiry(LocalDateTime t) {
        expiry = t.withNano(0).toString();
        save();
    }
}
