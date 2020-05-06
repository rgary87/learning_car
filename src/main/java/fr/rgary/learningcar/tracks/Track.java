package fr.rgary.learningcar.tracks;

import fr.rgary.learningcar.base.Constant;
import fr.rgary.learningcar.trigonometry.Line;
import fr.rgary.learningcar.trigonometry.Point;
import fr.rgary.learningcar.trigonometry.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Track.
 */
public class Track {
    public List<Line> borderOne;
    public List<Line> borderTwo;
    public List<Line> fitnessZoneLines;
    public Point startPoint;
    public List<Line> borders;
    public List<Zone> zones;
    public static Track instance;

    private Track() {
    }

    public Track(List<Line> borderOne, List<Line> borderTwo, List<Line> fitnessZoneLines) {
        this.borderOne = borderOne;
        this.borderTwo = borderTwo;
        this.fitnessZoneLines = fitnessZoneLines;
    }

    public void init() {
        Track.instance = this;
        Constant.setTRACK(this);
        this.borders = new ArrayList<>();
        this.borders.addAll(borderOne);
        this.borders.addAll(borderTwo);
        this.zones = new ArrayList<>();
        for (int i = 1; i < fitnessZoneLines.size(); i++) {
            this.zones.add(new Zone(i -1,
                    fitnessZoneLines.get(i - 1).S,
                    fitnessZoneLines.get(i - 1).E,
                    fitnessZoneLines.get(i).S,
                    fitnessZoneLines.get(i).E));
        }
    }

    public int getZoneNumberPerPosition(Point position) {
        for (Zone zone : zones) {
            if (zone.polygon.contains(position.X, position.Y)) {
                return zone.zoneNumber;
            }
        }
        return -1;
//        throw new InternalError("YOU SHOULD NOT COME HERE, WERE ARE YOU ?!");
    }



}
