package itstep.learning.oop;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import itstep.learning.oop.annotations.Product;
import itstep.learning.oop.annotations.Required;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class VehicleFactory {
    private Map<Class<?>, List<String>> productClasses = null;

    public List<Vehicle> loadFromJson( String resourceName ) throws Exception {
        try( InputStream stream = Objects.requireNonNull(
                this.getClass().getClassLoader().getResourceAsStream( resourceName )
        ) ) {
            String json = readAsString( stream );
            Gson gson = new Gson();
            JsonArray arr = gson.fromJson( json, JsonArray.class ) ;
            List<Vehicle> vehicles = new ArrayList<>();
            for( int i = 0; i < arr.size(); i++ ) {
                Vehicle vehicle = fromJsonObject(arr.get(i).getAsJsonObject());
                if (vehicle != null) {
                    vehicles.add(vehicle);
                }

            }
            return vehicles;
        }
        catch( Exception ex ) {
            throw new Exception( ex.getMessage() );
        }
    }

    private Vehicle fromJsonObject( JsonObject obj ) {
        // Перебираємо всі класи-продукти, в них перебираємо всі поля
        // шукаємо повний збіг полів JSON та полів класу
        Class<?> vehicleClass = null;
        for( Map.Entry<Class<?>, List<String>> entry :
                getProductClasses().entrySet() ) {
            // чи збігаються поля JSON-об'єкту (obj) та Required набір у класу (entry.getValue())
            boolean isMatch = true;
            for( String fieldName : entry.getValue() ) {
                if( ! obj.has( fieldName ) ) {
                    isMatch = false;
                }
            }
            // якщо збіг є, то entry.getKey() - це клас, що підходить для цього JSON
            if( isMatch ) {
                if(vehicleClass != null) {
                  throw new JsonParseException("Entry is ambiguous: " + obj.toString());
                }
                vehicleClass = entry.getKey();
            }
        }
        // якщо жодного збігу не знайдено, повертаємо null
        if( vehicleClass == null ) {
            return null;
        }
        // а якщо знайдено, то робимо об'єкт та заповнюємо його поля
        try {
            Vehicle vehicle = (Vehicle) vehicleClass.getConstructor().newInstance();

            Field field = vehicleClass.getSuperclass().getDeclaredField( "name" );
            field.setAccessible( true );
            field.set( vehicle, obj.get("name").getAsString() );

            return vehicle;
        }
        catch( Exception ignored ) { return null; }
    }

    /**
     * Збирає всі файли-класи, що є у даному пакеті
     * getProductClasses( "itstep.learning.oop" )
     * [ Bike -> ["type"] ]
     * [ Bus -> ["seats"] ]
     */
    private Map<Class<?>, List<String>> getProductClasses() {
        if( productClasses != null ) {
            return productClasses;
        }
        URL classLocation = this.getClass().getClassLoader().getResource(".");
        if( classLocation == null ) {
            throw new RuntimeException( "Error resource locating" );
        }
        File classRoot = null;
        try {
            classRoot = new File(
                    URLDecoder.decode( classLocation.getPath(), "UTF-8" )
            );
        }
        catch( Exception ignored ) { }
        if( classRoot == null ) {
            throw new RuntimeException( "Error resource traversing" );
        }
        List<String> classNames = new ArrayList<>();
        scanClassesInDirectory(classRoot, "", classNames);
        // ---------- Знаходимо лише ті класи, які є @Product -----------
        Map<Class<?>, List<String>> classes = new HashMap<>();
        for( String className : classNames ) {
            Class<?> cls;
            try { cls = Class.forName( className ) ; }
            catch( ClassNotFoundException ignored ) { continue; }
            if( cls.isAnnotationPresent( Product.class ) ) {
                classes.put( cls, getRequired(cls) ) ;
            }
        }
        productClasses = classes;
        return classes;
    }

    private void scanClassesInDirectory(File directory, String packageName, List<String> classNames) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanClassesInDirectory(file, packageName.isEmpty() ? file.getName() : packageName + "." + file.getName(), classNames);
            } else if (file.isFile() && file.getName().endsWith(".class") && file.canRead()) {
                String fileName = file.getName();
                classNames.add(packageName + "." + fileName.substring(0, fileName.length() - 6));
            }
        }
    }

    /**
     * Знайти всі поля класу, помічені анотацією @Required
     */
    private List<String> getRequired( Class<?> cls ) {
        List<String> res = new ArrayList<>();
        for( Field field : cls.getDeclaredFields() ) {
            if( field.isAnnotationPresent( Required.class ) ) {
                Required annotation = field.getAnnotation( Required.class );
                String requiredName = annotation.value();
                // boolean isAlter = annotation.isAlternate();
                res.add(
                    "".equals( requiredName )
                        ? field.getName()
                        : requiredName ) ;
            }
        }
        return res;
    }

    private String readAsString( InputStream stream ) throws IOException {
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream byteBuilder = new ByteArrayOutputStream();
        int length;
        while( ( length = stream.read( buffer ) ) != -1 ) {
            byteBuilder.write( buffer, 0, length );
        }
        return byteBuilder.toString();
    }

}
/*
{ "name": "Kawasaki Ninja",   --> Bike
  "type": "Sport" },

{ "name": "Renault C-Truck",  --> Truck
  "cargo": 7.5 },

[Bike<type>, Truck<cargo>, Bus<seats>] - який з цих класів підходить для JSON?

Д.З. Включити до файлу json всі типи Vehicle, перевірити успішність їх
відновлення (зчитування)
** Реалізувати у циклі, що знаходить відповідність класів та JSON-об'єктів
   можливість множинного збігу (декілька класів можуть обробити цей JSON)
   У такому разі кидати виняток.

 */