package dk.aau.student.b211.sleepattention;

import java.util.Date;

/**
 * @author Group B211, Aalborg University
 */
public class Sleep {

    /**
     *
     * @return
     */
    public double getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     */
    public void setDuration(double duration) {
        this.duration = duration;
    }

    /**
     *
     * @return
     */
    public Date getDate() {
        return date;
    }

    /**
     *
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     *
     * @return
     */
    public int getQuality() {
        return quality;
    }

    /**
     * Set the quality of the sleep.
     * @param quality quality as an int - max 10.
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public static final String TABLE_NAME = "SleepTable";
    public static final String KEY_ID = "_id";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_DATE = "date";
    public static final String KEY_QUALITY = "quality";

    private int id;
    private double duration;
    private Date date;
    private int quality;

    /**
     * Constructor for Sleep class.
     *
     * @param duration duration of the sleep
     * @param date date of the sleep log
     * @param quality quality of the sleep
     */
    public Sleep(int sleepID, double duration, Date date, int quality) {
        setID(sleepID);
        setDuration(duration);
        setDate(date);
        setQuality(quality);
    }


}