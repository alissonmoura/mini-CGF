package hla.exercise.aircraft;

import hla.aircraft.AircraftCallback;
import hla.aircraft.HlaAircraft;
import hla.destination.HlaDestination;
import hla.rti1516e.*;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAfloat64LE;
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
    private AttributeHandle attributeXDestination;
    private AttributeHandle attributeXAircraft;
    private AttributeHandle attributeYAircraft;
    private AttributeHandle attributeYDestination;
    private AttributeHandle attributeOrientationAircraft;
    private HlaDestination destination = new HlaDestination(RandomCoordinate.getX(), RandomCoordinate.getY());
    private AircraftCallback aircraftCallback;
    private ObjectInstanceHandle objectInstanceAircraftHandle;


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
        registerDestination();
        registerAircraft();
    }

    private void registerDestination() throws NameNotFound, InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress {
        attributeXDestination = rtIambassador.getAttributeHandle(objectClassDestinationHandle, "x");
        attributeYDestination = rtIambassador.getAttributeHandle(objectClassDestinationHandle, "y");


        AttributeHandleSet attributeSet = rtIambassador.getAttributeHandleSetFactory().create();


        attributeSet.add(attributeXDestination);
        attributeSet.add(attributeYDestination);


        rtIambassador.subscribeObjectClassAttributes(objectClassDestinationHandle,attributeSet);
    }

    private void registerAircraft() {
        try {
            attributeXAircraft = rtIambassador.getAttributeHandle(objectClassAircraftHandle, "x");
            attributeYAircraft = rtIambassador.getAttributeHandle(objectClassAircraftHandle, "y");
            attributeOrientationAircraft = rtIambassador.getAttributeHandle(objectClassAircraftHandle, "orientation");
            AttributeHandleSet attributeSet = rtIambassador.getAttributeHandleSetFactory().create();
            attributeSet.add(attributeXAircraft);
            attributeSet.add(attributeYAircraft);
            attributeSet.add(attributeOrientationAircraft);
            rtIambassador.publishObjectClassAttributes(objectClassAircraftHandle, attributeSet);
            objectInstanceAircraftHandle = rtIambassador.registerObjectInstance(objectClassAircraftHandle);
        } catch (NotConnected notConnected) {
            notConnected.printStackTrace();
        } catch (FederateNotExecutionMember federateNotExecutionMember) {
            federateNotExecutionMember.printStackTrace();
        } catch (RTIinternalError rtIinternalError) {
            rtIinternalError.printStackTrace();
        } catch (InvalidObjectClassHandle invalidObjectClassHandle) {
            invalidObjectClassHandle.printStackTrace();
        } catch (NameNotFound nameNotFound) {
            nameNotFound.printStackTrace();
        } catch (SaveInProgress saveInProgress) {
            saveInProgress.printStackTrace();
        } catch (AttributeNotDefined attributeNotDefined) {
            attributeNotDefined.printStackTrace();
        } catch (RestoreInProgress restoreInProgress) {
            restoreInProgress.printStackTrace();
        } catch (ObjectClassNotDefined objectClassNotDefined) {
            objectClassNotDefined.printStackTrace();
        } catch (ObjectClassNotPublished objectClassNotPublished) {
            objectClassNotPublished.printStackTrace();
        }
    }

    @Override
    public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass, String objectName) throws FederateInternalError {
    }

    public void update(HlaAircraft aircraft) throws RTIexception {
        updateAircraftDestination();
        updateAircraftsAttributes(aircraft);
    }

    private void updateAircraftsAttributes(HlaAircraft aircraft) throws FederateNotExecutionMember, NotConnected, NameNotFound, InvalidObjectClassHandle, RTIinternalError, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress {
        AttributeHandleValueMap attributeValues = rtIambassador.getAttributeHandleValueMapFactory().create(1024);
        HLAfloat64LE x = encoderFactory.createHLAfloat64LE(aircraft.getX());
        HLAfloat64LE y = encoderFactory.createHLAfloat64LE(aircraft.getY());

        HLAfloat64LE orientation = encoderFactory.createHLAfloat64LE(aircraft.getOrientation());


        attributeXAircraft = rtIambassador.getAttributeHandle(objectClassAircraftHandle, "x");
        attributeYAircraft = rtIambassador.getAttributeHandle(objectClassAircraftHandle, "y");
        attributeOrientationAircraft = rtIambassador.getAttributeHandle(objectClassAircraftHandle, "orientation");


        attributeValues.put(attributeXAircraft, x.toByteArray());
        attributeValues.put(attributeYAircraft, y.toByteArray());
        attributeValues.put(attributeOrientationAircraft, orientation.toByteArray());
        rtIambassador.updateAttributeValues(objectInstanceAircraftHandle, attributeValues, new byte[0]);
    }

    private void updateAircraftDestination() {
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

            if (theAttributes.containsKey(attributeXDestination)) {
                xDecoder.decode(theAttributes.get(attributeXDestination));
                int x = xDecoder.getValue();
                destination.setX(x);
            }
            if (theAttributes.containsKey(attributeYDestination)) {
                yDecoder.decode(theAttributes.get(attributeYDestination));
                int y = yDecoder.getValue();
                destination.setY(y);
            }



        } catch (DecoderException e) {
            System.out.println("Failed to decode incoming attribute");
        }


    }
}