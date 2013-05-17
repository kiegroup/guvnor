package org.kie.guvnor.datamodel.backend.server.testclasses.annotations;

public class SmurfHouse {

    @SmurfFieldDescriptor(gender = "M", description = "Brains")
    private Smurf occupant;

    @SmurfFieldPositionDescriptor(value = 1)
    private Smurf positionedOccupant;

}
