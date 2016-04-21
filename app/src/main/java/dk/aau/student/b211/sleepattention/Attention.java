package dk.aau.student.b211.sleepattention;

import java.util.Date;

/**
 * @author Group B211, Aalborg University
 */
class Attention {

    //Constants used for database fields and tables
    public final static String TABLE_NAME = "AttentionTable";
    public final static String KEY_ID = "_id";
    public final static String KEY_DATE = "date";
    public final static String KEY_SCORE = "score";

    private int id;
    private Date date;
    private int score;

    public int getID() {
        return id;
    }

    private void setID(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    private void setScore(int score) {
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    private void setDate(Date date) {
        this.date = date;
    }

    public Attention(int id, Date date, int score) {
        setID(id);
        setDate(date);
        setScore(score);
    }
}
