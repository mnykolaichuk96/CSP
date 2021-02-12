package mnykolaichuk.prz.pracaDyplomowa.model.enums;

public enum EngineType {
    DIESEL("Diesel"),
    LPG("Benzyna+LPG"),
    PETROL("Benzyna"),
    ELECTRIC("Elektryczny"),
    HYBRID("Hybryda");

    private String pl;

    EngineType(String pl) {
        this.pl = pl;
    }

    public String getPl() {
        return pl;
    }
}
