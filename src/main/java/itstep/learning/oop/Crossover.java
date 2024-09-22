package itstep.learning.oop;

import java.util.Locale;

public class Crossover extends Vehicle
        implements Trailer {
    private double clearance;
    public Crossover( String name, double clearance ) {
        super( name );
        this.setClearance( clearance );
    }

    public double getClearance() {
        return clearance;
    }

    public void setClearance( double clearance ) {
        this.clearance = clearance;
    }

    @Override
    public String getInfo() {
        return String.format(
                Locale.ROOT,
                "Crossover '%s', clearance: %s",
                super.getName(),
                this.getClearance()
        );
    }

    @Override
    public String trailerInfo() {
        return "Car trailer";
    }
}
