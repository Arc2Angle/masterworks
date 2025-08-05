package com.masterworks.masterworks.data.stat;

public interface StatCarrier {
    /**
     * @param stat The stat to check.
     * @return true if the stat is present, false otherwise.
     */
    boolean hasStat(Stat stat);

    /**
     * @param stat The stat to retrieve.
     * @return The value of the stat.
     * @throws Stat.IrrelevantException if the stat is not relevant to this carrier.
     */
    double getStat(Stat stat);
}
