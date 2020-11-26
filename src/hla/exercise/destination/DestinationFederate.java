package hla.exercise.destination;

import hla.destination.DestinationCallback;
import hla.destination.HlaDestination;
import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger32LE;
import hla.rti1516e.encoding.HLAunicodeString;
import hla.rti1516e.exceptions.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class DestinationFederate extends NullFederateAmbassador {
    private RtiFactory rtiFactory;
    private RTIambassador rtIambassador;
    private URL formModule;
    private String formModuleName;
    private ObjectClassHandle objectClassHandle;
    private String federationType;
    private String federationExecutionName;
    private EncoderFactory encoderFactory;
    private AttributeHandle attributeX;
    private AttributeHandle attributeY;
    private ObjectInstanceHandle objectInstanceHandle;


    public DestinationFederate(DestinationCallback destinationCallback) {
        formModuleName = "HLA-course.xml";
        federationExecutionName = "aircraft-destination";
        federationType = "AircraftDestination";
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
            federationExecutionAlreadyExists.printStackTrace();
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
        objectClassHandle = rtIambassador.getObjectClassHandle("HLAobjectRoot.Destination");
    }

    public void register(HlaDestination destination) {
        try {
            attributeX = rtIambassador.getAttributeHandle(objectClassHandle, "x");
            attributeY = rtIambassador.getAttributeHandle(objectClassHandle, "y");
            AttributeHandleSet attributeSet = rtIambassador.getAttributeHandleSetFactory().create();
            attributeSet.add(attributeX);
            attributeSet.add(attributeY);
            rtIambassador.publishObjectClassAttributes(objectClassHandle, attributeSet);
            objectInstanceHandle = rtIambassador.registerObjectInstance(objectClassHandle);
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

    public void update(HlaDestination destination) throws RTIexception {
        AttributeHandleValueMap attributeValues = rtIambassador.getAttributeHandleValueMapFactory().create(1);
        HLAinteger32LE x = encoderFactory.createHLAinteger32LE(destination.getX());
        HLAinteger32LE y = encoderFactory.createHLAinteger32LE(destination.getY());
        attributeValues.put(attributeX, x.toByteArray());
        attributeValues.put(attributeY, y.toByteArray());
        rtIambassador.updateAttributeValues(objectInstanceHandle, attributeValues, null);
        System.out.println("PRINT");
    }
}