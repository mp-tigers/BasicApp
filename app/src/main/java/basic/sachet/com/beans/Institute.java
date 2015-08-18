package basic.sachet.com.beans;

/**
 * Created by reliance on 15/08/2015.
 */
public class Institute {
    String name;
    String city;

    public Institute(String name, String city) {
        this.name = name;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String toString(){

        return "Institute Name:" + getName() + " Institute city:" + getCity();
    }
}
