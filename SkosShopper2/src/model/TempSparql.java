package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TempSparql {
	
    private final SimpleStringProperty predicate;
    private final SimpleStringProperty object;
	
    public TempSparql(String predicate, String object) {
        this.predicate = new SimpleStringProperty(predicate);
        this.object = new SimpleStringProperty(object);
    }
    
    public String getPredicate() {
        return predicate.get();
    }
    public void setPredicate(String predicate) {
    	this.predicate.set(predicate);
    }
    
    public StringProperty predicateProperty() {
    	return predicate;
    }
        
    public String getObject() {
        return object.get();
    }
    public void setLastName(String object) {
    	this.object.set(object);
    }
    
    public StringProperty objectProperty() {
    	return object;
    }
}
