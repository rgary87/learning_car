package fr.rgary.learningcar.display;

public enum DrawLevelEnum {

    NO_DRAW("Nothing", 0),
    TRACK("Track", 1),
    CAR("Track + Car", 2),
    SENSOR("Track + Car + Sensor", 3),
    ZONE("Track + Car + Sensor + Zone", 4),
    OVERLAY("Track + Car + Sensor + Zone + Overlay", 5);

    public final String label;

    public final int level;

    DrawLevelEnum(String label, int level) {
        this.label = label;
        this.level = level;
    }

    public DrawLevelEnum getNext() {
        switch (this) {
            case NO_DRAW:
                return TRACK;
            case TRACK:
                return CAR;
            case CAR:
                return SENSOR;
            case SENSOR:
                return ZONE;
            case ZONE:
                return OVERLAY;
            case OVERLAY:
            default:
                return NO_DRAW;
        }
    }
}
