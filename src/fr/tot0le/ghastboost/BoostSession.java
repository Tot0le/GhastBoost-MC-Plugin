package fr.tot0le.ghastboost;

public class BoostSession {

    private long endTime;

    private static final long BOOST_DURATION_MILLIS = 20_000L;
    private static final long MAX_BOOST_DURATION_MILLIS = 180_000L;

    public BoostSession(long currentTime) {
        this.endTime = currentTime + BOOST_DURATION_MILLIS;
    }

    public void extend(long currentTime) {
        long targetEndTime = currentTime + BOOST_DURATION_MILLIS;

        if (!isExpired(currentTime)) {
            targetEndTime = this.endTime + BOOST_DURATION_MILLIS;
        }

        this.endTime = targetEndTime;
        return;
    }

    public boolean isAtMaximum(long currentTime) {
        boolean atMaximum = false;

        if (!isExpired(currentTime)) {
            long remainingMillis = this.endTime - currentTime;
            if (remainingMillis >= MAX_BOOST_DURATION_MILLIS) {
                atMaximum = true;
            }
        }

        return atMaximum;
    }

    public boolean isExpired(long currentTime) {
        boolean expired = false;

        if (this.endTime <= currentTime) {
            expired = true;
        }

        return expired;
    }

    public long getRemainingSeconds(long currentTime) {
        long remainingSeconds = 0L;

        if (!isExpired(currentTime)) {
            remainingSeconds = (this.endTime - currentTime) / 1000L;
        }

        return remainingSeconds;
    }

    public long getRemainingMillis(long currentTime) {
        long remainingMillis = 0L;

        if (!isExpired(currentTime)) {
            remainingMillis = this.endTime - currentTime;
        }

        return remainingMillis;
    }
}
