package hla.destination;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;

public class HlaDestination {
    public static String className = "HLAobjectRoot.Destination";
    public static ObjectClassHandle classHandle;
    public static AttributeHandle xHandle;
    public static AttributeHandle yHandle;

    private ObjectInstanceHandle instance;
    private ObjectClassHandle objectClass;
    private String name;

    private int x;
    private int y;

    public HlaDestination(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public HlaDestination(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass, String objectName) {
        instance = theObject;
        objectClass = theObjectClass;
        name = objectName;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ObjectInstanceHandle getInstance() {
        return instance;
    }

    public void setInstance(ObjectInstanceHandle instance) {
        this.instance = instance;
    }

    public ObjectClassHandle getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(ObjectClassHandle objectClass) {
        this.objectClass = objectClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}