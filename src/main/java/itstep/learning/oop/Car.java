package itstep.learning.oop;

import java.util.Locale;

public class Car extends Vehicle
        implements Trailer {
    private String body;
    public Car( String name, String body ) {
        super( name );
        this.setBody( body );
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String getInfo() {
        return String.format(
                Locale.ROOT,
                "Car '%s', body: %s",
                super.getName(),
                this.getBody()
        );
    }

    @Override
    public String trailerInfo() {
        return "Car trailer";
    }
}