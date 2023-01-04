package traben.entity_model_features.utils;

import org.spongepowered.asm.mixin.transformer.throwables.ReEntrantTransformerError;

import java.awt.desktop.SystemEventListener;

public class AnimationValues {
       // private Double rx, ry, rz;
      //  private Double tx, ty, tz;

    private static double timeBetweenUpdates = 1000;// 1000 = 1 second

    public boolean needsToUpdate() {
        return lastUpdate + timeBetweenUpdates <= System.currentTimeMillis();
    }
    public static boolean needsToUpdate(long time) {
        return time + timeBetweenUpdates <= System.currentTimeMillis();
    }

    private double calculateInbetweenValue(double next, double last, long time) {
        //first line *should be impossible to be true
        //todo optimize if above comment remains true
        //if(current >= next) return next;

        //if( last == null) return Double.NaN;

        //return last;


        if (next == last) return next;

        double difference = Math.abs(next-last);
        double percentageToNext = ((double) time - lastUpdate) / ((double) timeBetweenUpdates);
        return (last + (difference * percentageToNext));
//
//        double difference = next - last;
//        double percentageToNext = (time - lastUpdate) / timeBetweenUpdates;
//        return last + difference / percentageToNext;
    }

    private boolean canRx() {
        return lastrx != null;
    }

    private boolean canRy() {
        return lastry != null;
    }

    private boolean canRz() {
        return lastrz != null;
    }

    private boolean canTx() {
        return lasttx != null;
    }

    private boolean canTy() {
        return lastty != null;
    }

    private boolean canTz() {
        return lasttz != null;
    }

    private boolean canSx() {
        return lastsx != null;
    }

    private boolean canSy() {
        return lastsy != null;
    }

    private boolean canSz() {
        return lastsz != null;
    }

    public Double getRx(long time) {
        if(canRx()) return calculateInbetweenValue(nextrx, lastrx, time);
        return null;
    }

    public Double getRy(long time) {
        //needsUpdate = false;
        if(canRy()) return calculateInbetweenValue(nextry, lastry, time);

        return null;
    }

    public Double getRz(long time) {
        //needsUpdate = false;
        if(canRz()) return calculateInbetweenValue(nextrz, lastrz, time);
        return null;
    }

    public Double getTx(long time) {
        //needsUpdate = false;
        if(canTx()) return calculateInbetweenValue(nexttx, lasttx, time);
        return null;
    }

    public Double getTy(long time) {
        //needsUpdate = false;
        if(canTy()) return calculateInbetweenValue(nextty, lastty, time);
        return null;
    }

    public Double getTz(long time) {
        // needsUpdate = false;
        if(canTz()) return calculateInbetweenValue(nexttz, lasttz, time);
        return null;
    }

    public Double getSx(long time) {
        //needsUpdate = false;
        if(canSx()) return calculateInbetweenValue(nextsx, lastsx, time);
        return null;
    }

    public Double getSy(long time) {
        //needsUpdate = false;
        if(canSy()) return calculateInbetweenValue(nextsy, lastsy, time);
        return null;
    }

    public Double getSz(long time) {
        //needsUpdate = false;
        if(canSx()) return calculateInbetweenValue(nextsz, lastsz, time);
        return null;
    }


    //private Double sx, sy, sz;
    private Double lastrx, lastry, lastrz;
    private Double lasttx, lastty, lasttz;
    private Double lastsx, lastsy, lastsz;
    private double nextrx = 0d;
    private double nextry = 0d;
    private double nextrz = 0d;
    private double nexttx = 0d;
    private double nextty = 0d;
    private double nexttz = 0d;
    private double nextsx = 0d;
    private double nextsy = 0d;
    private double nextsz = 0d;
    public boolean visible_boxes = true;
    public boolean visible = true;

    private long lastUpdate;

    public AnimationValues(long updateTime) {
        lastUpdate = updateTime;
    }

    private void updateOccurred(){
        lastUpdate = System.currentTimeMillis();
    }

    public void newRX(double d) {
        updateOccurred();
        lastrx = nextrx;
        nextrx = d;
    }

    public void newRY(double d) {
        updateOccurred();
        lastry = nextry;
        nextry = d;
    }

    public void newRZ(double d) {
        updateOccurred();
        lastrz = nextrz;
        nextrz = d;
    }

    public void newTX(double d) {
        updateOccurred();
        lasttx = nexttx;
        nexttx = d;
    }

    public void newTY(double d) {
        updateOccurred();
        lastty = nextty;
        nextty = d;
    }

    public void newTZ(double d) {
        updateOccurred();
        lasttz = nexttz;
        nexttz = d;
    }

    public void newSX(double d) {
        updateOccurred();
        lastsx = nextsx;
        nextsx = d;
    }

    public void newSY(double d) {
        updateOccurred();
        lastsy = nextsy;
        nextsy = d;
    }

    public void newSZ(double d) {
        updateOccurred();
        lastsz = nextsz;
        nextsz = d;
    }


}
