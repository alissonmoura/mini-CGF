package hla.aircraft;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;

public class HlaAircraft {

    public static String className = "HLAobjectRoot.Aircraft";
    public static ObjectClassHandle classHandle;
    public static AttributeHandle xHandle;
    public static AttributeHandle yHandle;
    public static AttributeHandle orientationHandle;
    private ObjectInstanceHandle instanceHandle;
    private String name;

    private double x;
    private double y;
    private double orientation;

    public HlaAircraft(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass, String objectName) {
        this.setHandle(theObject);
        this.setName(objectName);
    }

    public HlaAircraft(int x, int y) {
        this.x = x;
        this.y = y;
        this.orientation = 0;
    }

    public ObjectInstanceHandle getHandle() {
        return instanceHandle;
    }

    public void setHandle(ObjectInstanceHandle handle) {
        this.instanceHandle = handle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getOrientation() {
        return orientation;
    }

    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }
}