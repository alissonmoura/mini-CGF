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

    private ObjectInstanceHandle aircraftObjectInstanceHandle;

    private HlaDestination destination = new HlaDestination(RandomCoordinate.getX(), RandomCoordinate.getY());
    private AircraftCallback aircraftCallback;


    public AircraftFederate(AircraftCallback aircraftCallback) {
        formModuleName = "HLA-course.xml";
        federationExecutionName = "aircraft-destination";
        federationType = "Aircraft";
        this.aircraftCallback = aircraftCallback;
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

        destinationObjectClassHandle = rtIambassador.getObjectClassHandle("HLAobjectRoot.Destination");
        aircraftObjectClassHandle = rtIambassador.getObjectClassHandle("HLAobjectRoot.Aircraft");
    }

    public void register(HlaAircraft aircraft) {
        // TODO Auto-generated method stub

        try {
            AttributeHandleSetFactory attributeHandleSetFactory = rtIambassador.getAttributeHandleSetFactory();
            AttributeHandleSet attributeHandleSet = attributeHandleSetFactory.create();

            destinationAttributeX = rtIambassador.getAttributeHandle(destinationObjectClassHandle, "x");
            destinationAttributeY = rtIambassador.getAttributeHandle(destinationObjectClassHandle, "y");

            attributeHandleSet.add(destinationAttributeX);
            attributeHandleSet.add(destinationAttributeY);

            rtIambassador.subscribeObjectClassAttributes(destinationObjectClassHandle, attributeHandleSet);

            attributeHandleSet.clear();

            aircraftAttributeX = rtIambassador.getAttributeHandle(aircraftObjectClassHandle, "x");
            aircraftAttributeY = rtIambassador.getAttributeHandle(aircraftObjectClassHandle, "y");
            aircraftOrientation = rtIambassador.getAttributeHandle(aircraftObjectClassHandle, "orientation");

            attributeHandleSet.add(aircraftAttributeX);
            attributeHandleSet.add(aircraftAttributeY);
            attributeHandleSet.add(aircraftOrientation);

            rtIambassador.publishObjectClassAttributes(aircraftObjectClassHandle, attributeHandleSet);

            aircraftObjectInstanceHandle = rtIambassador.registerObjectInstance(aircraftObjectClassHandle);
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

    public void update(HlaAircraft aircraft) throws RTIexception {

        this.aircraftCallback.reflect(destination);

        AttributeHandleValueMap attributeValues = rtIambassador.getAttributeHandleValueMapFactory().create(1024);
        HLAfloat64LE x = encoderFactory.createHLAfloat64LE(aircraft.getX());
        HLAfloat64LE y = encoderFactory.createHLAfloat64LE(aircraft.getY());
        HLAfloat64LE orientation = encoderFactory.createHLAfloat64LE(aircraft.getOrientation());
        attributeValues.put(aircraftAttributeX, x.toByteArray());
        attributeValues.put(aircraftAttributeY, y.toByteArray());
        attributeValues.put(aircraftOrientation, orientation.toByteArray());
        rtIambassador.updateAttributeValues(aircraftObjectInstanceHandle, attributeValues, new byte[0]);
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
            final HLAinteger32LE xDecoder = encoderFactory.createHLAinteger32LE();
            final HLAinteger32LE yDecoder = encoderFactory.createHLAinteger32LE();

            if (theAttributes.containsKey(destinationAttributeX)) {
                xDecoder.decode(theAttributes.get(destinationAttributeX));
                int x = xDecoder.getValue();
                //System.out.println("X -> " + x);
                destination.setX(x);
            }
            if (theAttributes.containsKey(destinationAttributeY)) {
                yDecoder.decode(theAttributes.get(destinationAttributeY));
                int y = yDecoder.getValue();
                //System.out.println("Y -> " + y);
                destination.setY(y);
            }
        } catch (DecoderException e) {
            System.out.println("Failed to decode incoming attribute");
        }
    }
}