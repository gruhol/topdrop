package pl.thinkdata.droptop.database.model;

public enum SyncStatus {
    NEW,
    TO_UPDATE,
    STOCK_UPDATE,
    PRICE_UPDATE,
    PRICE_STOCK_UPDATE,
    SYNCED,
    ERROR
}
