package com.miris.ui.comp;

public class CalendarDayEvent {

    private final long timeInMillis;
    private final int color;
    private boolean holiday;

    public CalendarDayEvent(final long timeInMillis, final int color, boolean holiday) {
        this.timeInMillis = timeInMillis;
        this.color = color;
        this.holiday = holiday;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public boolean getholiday() {
        return holiday;
    }

    public int getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarDayEvent event = (CalendarDayEvent) o;

        if (color != event.color) return false;
        if (timeInMillis != event.timeInMillis) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (timeInMillis ^ (timeInMillis >>> 32));
        result = 31 * result + color;
        return result;
    }

    @Override
    public String
    toString() {
        return "CalendarDayEvent{" +
                "timeInMillis=" + timeInMillis +
                ", color=" + color +
                '}';
    }
}
