package dk.aau.student.b211.sleepattention;

import java.util.Date;

/**
 * @author Group B211, Aalborg University
 */
public class Attention {

    public final static String TABLE_NAME = "AttentionTable";
    public final static String KEY_ID = "_id";
    public final static String KEY_TIME = "time";
    public final static String KEY_DATE = "date";
    public final static String KEY_SCORE = "score";

    private int id;
    private double time;
    private Date date;
    private int score;

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Attention(int id, double time, Date date, int score) {
        setID(id);
        setTime(time);
        setDate(date);
        setScore(score);
    }
}
