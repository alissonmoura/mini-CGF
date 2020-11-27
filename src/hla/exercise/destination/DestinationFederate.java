package hla.exercise.destination;

import hla.aircraft.AircraftCallback;
import hla.aircraft.HlaAircraft;
import hla.destination.DestinationCallback;
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

public class DestinationFederate extends NullFederateAmbassador {
    private RtiFactory rtiFactory;
    private RTIambassador rtIambassador;
    private URL formModule;
    private String formModuleName;

    private ObjectClassHandle destinationObjectClassHandle;
    private ObjectClassHandle aircraftObjectClassHandle;

    private String federationType;
    private String federationExecutionName;
    private EncoderFactory encoderFactory;

    private AttributeHandle destinationAttributeX;
    private AttributeHandle destinationAttributeY;

    private AttributeHandle aircraftAttributeX;
    private AttributeHandle aircraftAttributeY;
    private AttributeHandle aircraftOrientation;

    private ObjectInstanceHandle destinationObjectInstanceHandle;

    private HlaAircraft aircraft = new HlaAircraft(RandomCoordinate.getX(), RandomCoordinate.getY());

    private DestinationCallback destinationCallback;

    public DestinationFederate(DestinationCallback destinationCallback) {
        formModuleName = "HLA-course.xml";
        federationExecutionName = "aircraft-destination";
        federationType = "Destination";
        this.destinationCallback = destinationCallback;
    }

    public DestinationFederate() {


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
        destinationObjectClassHandle = rtIambassador.getObjectClassHandle("HLAobjectRoot.Destination");
        aircraftObjectClassHandle = rtIambassador.getObjectClassHandle("HLAobjectRoot.Aircraft");
    }

    public void register(HlaDestination destination) throws NotConnected, FederateNotExecutionMember {
        // TODO Auto-generated method stub


        try {

            AttributeHandleSetFactory attributeHandleSetFactory = rtIambassador.getAttributeHandleSetFactory();
            AttributeHandleSet attributeHandleSet = attributeHandleSetFactory.create();

            destinationAttributeX = rtIambassador.getAttributeHandle(destinationObjectClassHandle, "x");
            destinationAttributeY = rtIambassador.getAttributeHandle(destinationObjectClassHandle, "y");

            attributeHandleSet.add(destinationAttributeX);
            attributeHandleSet.add(destinationAttributeY);

            rtIambassador.publishObjectClassAttributes(destinationObjectClassHandle, attributeHandleSet);

            destinationObjectInstanceHandle = rtIambassador.registerObjectInstance(destinationObjectClassHandle);

            attributeHandleSet.clear();

            aircraftAttributeX = rtIambassador.getAttributeHandle(aircraftObjectClassHandle, "x");
            aircraftAttributeY = rtIambassador.getAttributeHandle(aircraftObjectClassHandle, "y");
            aircraftOrientation = rtIambassador.getAttributeHandle(aircraftObjectClassHandle, "orientation");

            attributeHandleSet.add(aircraftAttributeX);
            attributeHandleSet.add(aircraftAttributeY);
            attributeHandleSet.add(aircraftOrientation);

            rtIambassador.subscribeObjectClassAttributes(aircraftObjectClassHandle, attributeHandleSet);

        } catch (NameNotFound nameNotFound) {
            nameNotFound.printStackTrace();
        } catch (InvalidObjectClassHandle invalidObjectClassHandle) {
            invalidObjectClassHandle.printStackTrace();
        } catch (FederateNotExecutionMember federateNotExecutionMember) {
            federateNotExecutionMember.printStackTrace();
        } catch (NotConnected notConnected) {
            notConnected.printStackTrace();
        } catch (RTIinternalError rtIinternalError) {
            rtIinternalError.printStackTrace();
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

    public void update(HlaDestination destination) throws RTIexception {

        this.destinationCallback.reflect(aircraft);

        AttributeHandleValueMap attributeValues = rtIambassador.getAttributeHandleValueMapFactory().create(1024);
        HLAinteger32LE x = encoderFactory.createHLAinteger32LE(destination.getX());
        HLAinteger32LE y = encoderFactory.createHLAinteger32LE(destination.getY());
        attributeValues.put(destinationAttributeX, x.toByteArray());
        attributeValues.put(destinationAttributeY, y.toByteArray());
        rtIambassador.updateAttributeValues(destinationObjectInstanceHandle, attributeValues, new byte[0]);
    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject,
                                       AttributeHandleValueMap theAttributes,
                                       byte[] userSuppliedTag,
                                       OrderType sentOrdering,
                                       TransportationTypeHandle theTransport,
                                       SupplementalReflectInfo reflectInfo) throws FederateInternalError
    {
        try
        {
            final HLAfloat64LE xDecoder = encoderFactory.createHLAfloat64LE();
            final HLAfloat64LE yDecoder = encoderFactory.createHLAfloat64LE();
            final HLAfloat64LE orientationDecoder = encoderFactory.createHLAfloat64LE();

            if (theAttributes.containsKey(aircraftAttributeX)) {
                xDecoder.decode(theAttributes.get(aircraftAttributeX));
                double x = xDecoder.getValue();
                //System.out.println("X -> " + x);
                aircraft.setX(x);
            }
            if (theAttributes.containsKey(aircraftAttributeY)) {
                yDecoder.decode(theAttributes.get(aircraftAttributeY));
                double y = yDecoder.getValue();
                //System.out.println("Y -> " + y);
                aircraft.setY(y);
            }

            if (theAttributes.containsKey(aircraftOrientation)) {
                orientationDecoder.decode(theAttributes.get(aircraftOrientation));
                double orientation = orientationDecoder.getValue();
                //System.out.println("Orientation -> " + orientation);
                aircraft.setOrientation(orientation);
            }
        } catch (DecoderException e) {
            System.out.println("Failed to decode incoming attribute");
        }
    }
}