package pl.thinkdata.droptop.database.model;

public enum ImportTypeEnu {
    PRODUCT("Product"),
    STOCK("Stock");

    private String value;

    ImportTypeEnu(String value) {
        this.value = value;
    }
}
