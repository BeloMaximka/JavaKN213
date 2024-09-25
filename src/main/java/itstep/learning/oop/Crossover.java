package itstep.learning.oop;
import itstep.learning.oop.annotations.Product;
import itstep.learning.oop.annotations.Required;

import java.util.Locale;
@Product
public class Crossover extends Vehicle
        implements Trailer {
    @Required
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