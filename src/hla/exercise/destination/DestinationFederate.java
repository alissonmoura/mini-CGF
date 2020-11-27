package hla.exercise.destination;

import hla.aircraft.HlaAircraft;
import hla.destination.DestinationCallback;
import hla.destination.DestinationChangedListener;
import hla.destination.HlaDestination;
import hla.rti1516e.*;
import hla.rti1516e.encoding.*;
import hla.rti1516e.exceptions.*;
import util.RandomCoordinate;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class DestinationFederate extends NullFederateAmbassador {
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
    private ObjectInstanceHandle objectInstanceHandle;
    private AttributeHandle attributeXAircraft;
    private AttributeHandle attributeYAircraft;
    private AttributeHandle attributeOrientation;
    private HlaAircraft aircraft = new HlaAircraft(0,0);
    private DestinationCallback destinationCallback;



    public DestinationFederate(DestinationCallback destinationCallback) {
        formModuleName = "HLA-course.xml";
        federationExecutionName = "aircraft-destination";
        federationType = "Destination";
        this.destinationCallback = destinationCallback;

    }

    public DestinationFederate() {


    }

    public void init() throws SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember, RTIinternalError, FederationExecutionDoesNotExist, CallNotAllowedFromWithinCallback, CouldNotCreateLogicalTimeFactory, NotConnected, NameNotFound, FederateNotExecutionMember {
        try {
            rtiFactory = RtiFactoryFactory.getRtiFactory();
            encoderFactory = rtiFactory.getEncoderFactory();
            rtIambassador = rtiFactory.getRtiAmbassador();
            rtIambassador.connect(this, CallbackModel.HLA_IMMEDIATE);
            formModule = Paths.get(formModuleName).toUri().toURL();
            rtIambassador.createFederationExecution(federationExecutionName, formModule);
        } catch (RTIinternalError rtIinternalError) {
            rtIinternalError.printStackTrace();
        } catch (UnsupportedCallbackModel unsupportedCallbackModel) {
            unsupportedCallbackModel.printStackTrace();
        } catch (InconsistentFDD inconsistentFDD) {
            inconsistentFDD.printStackTrace();
        } catch (FederationExecutionAlreadyExists federationExecutionAlreadyExists) {
            System.out.println("Failed");
        } catch (CallNotAllowedFromWithinCallback callNotAllowedFromWithinCallback) {
            callNotAllowedFromWithinCallback.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (CouldNotOpenFDD couldNotOpenFDD) {
            couldNotOpenFDD.printStackTrace();
        } catch (InvalidLocalSettingsDesignator invalidLocalSettingsDesignator) {
            invalidLocalSettingsDesignator.printStackTrace();
        } catch (ConnectionFailed connectionFailed) {
            connectionFailed.printStackTrace();
        } catch (NotConnected notConnected) {
            notConnected.printStackTrace();
        } catch (ErrorReadingFDD errorReadingFDD) {
            errorReadingFDD.printStackTrace();
        } catch (AlreadyConnected alreadyConnected) {
            alreadyConnected.printStackTrace();
        }

        rtIambassador.joinFederationExecution(federationType,federationExecutionName);
        objectClassDestinationHandle = rtIambassador.getObjectClassHandle("HLAobjectRoot.Destination");
        objectClassAircraftHandle = rtIambassador.getObjectClassHandle("HLAobjectRoot.Aircraft");
    }

    public void register(HlaDestination destination) throws NotConnected, AttributeNotDefined, NameNotFound, RestoreInProgress, ObjectClassNotDefined, SaveInProgress, FederateNotExecutionMember, RTIinternalError, InvalidObjectClassHandle {
        registerDestination();
        registerAircraft();
    }

    private void registerAircraft() throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError, InvalidObjectClassHandle, AttributeNotDefined, ObjectClassNotDefined, RestoreInProgress, SaveInProgress {
        attributeXAircraft = rtIambassador.getAttributeHandle(objectClassAircraftHandle, "x");
        attributeYAircraft = rtIambassador.getAttributeHandle(objectClassAircraftHandle, "y");
        attributeOrientation = rtIambassador.getAttributeHandle(objectClassAircraftHandle, "orientation");


        AttributeHandleSet attributeSet = rtIambassador.getAttributeHandleSetFactory().create();


        attributeSet.add(attributeXAircraft);
        attributeSet.add(attributeYAircraft);
        attributeSet.add(attributeOrientation);


        rtIambassador.subscribeObjectClassAttributes(objectClassAircraftHandle,attributeSet);
    }

    private void registerDestination() {
        try {
            attributeX = rtIambassador.getAttributeHandle(objectClassDestinationHandle, "x");
            attributeY = rtIambassador.getAttributeHandle(objectClassDestinationHandle, "y");
            AttributeHandleSet attributeSet = rtIambassador.getAttributeHandleSetFactory().create();
            attributeSet.add(attributeX);
            attributeSet.add(attributeY);
            rtIambassador.publishObjectClassAttributes(objectClassDestinationHandle, attributeSet);
            objectInstanceHandle = rtIambassador.registerObjectInstance(objectClassDestinationHandle);
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


    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject,
                                       AttributeHandleValueMap theAttributes,
                                       byte[] userSuppliedTag,
                                       OrderType sentOrdering,
                                       TransportationTypeHandle theTransport,
                                       SupplementalReflectInfo reflectInfo)
    {

        try {
            final HLAfloat64LE xDecoder = encoderFactory.createHLAfloat64LE();
            final HLAfloat64LE yDecoder = encoderFactory.createHLAfloat64LE();
            final HLAfloat64LE orientationDecoder = encoderFactory.createHLAfloat64LE();

            if (theAttributes.containsKey(attributeXAircraft)) {
                xDecoder.decode(theAttributes.get(attributeXAircraft));
                double x = xDecoder.getValue();
                aircraft.setX(x);
            }
            if (theAttributes.containsKey(attributeYAircraft)) {
                yDecoder.decode(theAttributes.get(attributeYAircraft));
                double y = yDecoder.getValue();
                aircraft.setY(y);
            }
            if (theAttributes.containsKey(attributeOrientation)) {
                orientationDecoder.decode(theAttributes.get(attributeOrientation));
                double o = orientationDecoder.getValue();
                aircraft.setOrientation(o);
            }
            destinationCallback.reflect(aircraft);



        } catch (DecoderException e) {
            System.out.println("Failed to decode incoming attribute");
        }


    }



    public void update(HlaDestination destination) throws RTIexception {
        AttributeHandleValueMap attributeValues = rtIambassador.getAttributeHandleValueMapFactory().create(1024);
        HLAinteger32LE x = encoderFactory.createHLAinteger32LE(destination.getX());
        HLAinteger32LE y = encoderFactory.createHLAinteger32LE(destination.getY());
        attributeValues.put(attributeX, x.toByteArray());
        attributeValues.put(attributeY, y.toByteArray());
        rtIambassador.updateAttributeValues(objectInstanceHandle, attributeValues, new byte[0]);
    }
}