package fr.rgary.learningcar.tracks;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Class JsonToTrack.
 */
public class JsonToTrack {

    public static Track buildTrack() {
        ObjectMapper om = new ObjectMapper();

        List<String> file;
        Track objectTrackBorder1 = null;
        try {
            file = Files.readAllLines(Paths.get("src/main/resources/track_1.json"));
            objectTrackBorder1 = om.readValue(Strings.join(file, ' '), Track.class);
            objectTrackBorder1.init();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return objectTrackBorder1;
        }
    }

}
