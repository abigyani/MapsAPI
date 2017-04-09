package lab.abhishek.sriniwebassignment;

/**
 * Created by Abhishek on 09-Apr-17.
 */

public class People {

    private String _id, fname, lname, email,category, description, price, availability, location, lat, lon, image_url;

    public People(){

    }


    public People(String _id, String fname, String lname, String email, String category, String description, String price, String availability, String image_url, String location, String lat, String lon) {
        this._id = _id;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.category = category;
        this.description = description;
        this.price = price;
        this.availability = availability;
        this.image_url = image_url;
        this.location = location;
        this.lat = lat;
        this.lon = lon;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
