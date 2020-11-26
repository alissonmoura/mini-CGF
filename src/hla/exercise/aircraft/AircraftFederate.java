package hla.exercise.aircraft;

import hla.aircraft.AircraftCallback;
import hla.aircraft.HlaAircraft;
import hla.destination.HlaDestination;
import hla.rti1516e.*;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger32LE;
import hla.rti1516e.exceptions.*;
import util.RandomCoordinate;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class AircraftFederate extends NullFederateAmbassador {
    private RtiFactory rtiFactory;
    private RTIambassador rtIambassador;
    private URL formModule;
    private String formModuleName;
    private ObjectClassHandle objectClassDestinationHandle;
    private ObjectClassHandle objectClassAircraftHandle;
    private String federationType;
    private String federationExecutionName;
    private EncoderFactory encoderFactory;
    private AttributeHandle attributeX;
    private AttributeHandle attributeY;
    private AttributeHandle attributeOrientation;
    private HlaDestination destination = new HlaDestination(RandomCoordinate.getX(), RandomCoordinate.getY());
    private AircraftCallback aircraftCallback;


    public AircraftFederate(AircraftCallback aircraftCallback) {
        formModuleName = "HLA-course.xml";
        federationExecutionName = "aircraft-destination";
        federationType = "Aircraft";
        this.aircraftCallback =  aircraftCallback;
    }

    public AircraftFederate() {


    }

    public void init() throws RTIinternalError, AlreadyConnected, CallNotAllowedFromWithinCallback, UnsupportedCallbackModel, ConnectionFailed, InvalidLocalSettingsDesignator, MalformedURLException, SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember, FederationExecutionDoesNotExist, CouldNotCreateLogicalTimeFactory, NotConnected, NameNotFound, FederateNotExecutionMember {
        rtiFactory = RtiFactoryFactory.getRtiFactory();
        encoderFactory  = rtiFactory.getEncoderFactory();
        rtIambassador = rtiFactory.getRtiAmbassador();
        rtIambassador.connect(this, CallbackModel.HLA_IMMEDIATE);
        formModule = Paths.get(formModuleName).toUri().toURL();
        try {
            rtIambassador.createFederationExecution(federationExecutionName,formModule);
        } catch (FederationExecutionAlreadyExists e) {
            System.out.println("Failed");
        } catch (NotConnected notConnected) {
            notConnected.printStackTrace();
        } catch (CouldNotOpenFDD couldNotOpenFDD) {
            couldNotOpenFDD.printStackTrace();
        } catch (ErrorReadingFDD errorReadingFDD) {
            errorReadingFDD.printStackTrace();
        } catch (InconsistentFDD inconsistentFDD) {
            inconsistentFDD.printStackTrace();
        }

        rtIambassador.joinFederationExecution(federationType,federationExecutionName);
        objectClassDestinationHandle = rtIambassador.getObjectClassHandle("HLAobjectRoot.Destination");
        objectClassAircraftHandle = rtIambassador.getObjectClassHandle("HLAobjectRoot.Aircraft");
    }

    public void register(HlaAircraft aircraft) throws NotConnected, FederateNotExecutionMember, NameNotFound, RTIinternalError, InvalidObjectClassHandle, AttributeNotDefined, ObjectClassNotDefined, RestoreInProgress, SaveInProgress {
        attributeX = rtIambassador.getAttributeHandle(objectClassDestinationHandle, "x");
        attributeY = rtIambassador.getAttributeHandle(objectClassDestinationHandle, "y");
        //attributeOrientation = rtIambassador.getAttributeHandle(objectClassHandle, "orientation");


        AttributeHandleSet attributeSet = rtIambassador.getAttributeHandleSetFactory().create();


        attributeSet.add(attributeX);
        attributeSet.add(attributeY);
        //attributeSet.add(attributeOrientation);


        rtIambassador.subscribeObjectClassAttributes(objectClassDestinationHandle,attributeSet);


    }

    @Override
    public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass, String objectName) throws FederateInternalError {
        System.out.println("discoverObjectInstance");
    }

    public void update(HlaAircraft aircraft) throws RTIexception {
       this.aircraftCallback.reflect(destination);
    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject,
                                       AttributeHandleValueMap theAttributes,
                                       byte[] userSuppliedTag,
                                       OrderType sentOrdering,
                                       TransportationTypeHandle theTransport,
                                       SupplementalReflectInfo reflectInfo)
    {

        try {
            final HLAinteger32LE xDecoder = encoderFactory.createHLAinteger32LE();
            final HLAinteger32LE yDecoder = encoderFactory.createHLAinteger32LE();

            if (theAttributes.containsKey(attributeX)) {
                xDecoder.decode(theAttributes.get(attributeX));
                int x = xDecoder.getValue();
                System.out.println("X -> " + x);
                destination.setX(x);
            }
            if (theAttributes.containsKey(attributeY)) {
                yDecoder.decode(theAttributes.get(attributeY));
                int y = yDecoder.getValue();
                System.out.println("Y -> " + y);
                destination.setY(y);
            }



        } catch (DecoderException e) {
            System.out.println("Failed to decode incoming attribute");
        }


    }
}