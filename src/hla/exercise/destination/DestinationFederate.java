package hla.exercise.destination;

import hla.destination.DestinationCallback;
import hla.destination.HlaDestination;
import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
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


    public DestinationFederate(DestinationCallback destinationCallback) {
        formModuleName = "HLA-course.xml";
        federationExecutionName = "aircraft-destination";
        federationType = "AircraftDestination";
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
        objectClassHandle = rtIambassador.getObjectClassHandle("HLAobjectRoot.Destination");


    }

    public void register(HlaDestination destination) {
        // TODO Auto-generated method stub

    }

    public void update(HlaDestination destination) throws RTIexception {


    }
}