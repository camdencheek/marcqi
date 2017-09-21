# MARCQI simulation tool

This tool was created to generate fake patient data with any given causal relationships.

# Organization

`figures` contains the figures for the paper
`generation` contains the code for simulating patients
`plotting` contains the code for generating the figures
`sources` contains pdfs of the sources used to obtain the simulation numbers

# Usage

## Build System

The simulation build system uses gradle. There are two steps to simulation: generation and analysis. Running `gradle generate` in the `generation` directory runs the main method in `Generate.java`, which is for generating patient data. Running `gradle analyze` in the `generation` directory runs the main method in `Analyze.java`, which is for analyzing the generated patient data with TETRAD. The generation/analysis source code is found in `generation/src/main/java/revgen`.

## Data storage

Due to the volume of data generated, it is infeasable to store it in separate files per simulation. Instead, it is configured to store the results in a MySQL database. A database schema that can be used for the current configuration is included in the root of the project, `schema.sql`. That can be used to create a schema in a new MySQL database from the MySQL CLI as follows:

> create database test;
> use test;
> source source.sql

The table schema will need to be edited accordingly if new variables are to be added. 


# Code

All classes are documented internally, but I have also included an outline of each class here for reference.

### Generate.java

`Generate.java` contains the main method that is run to generate patient data. 

### Analyze.java

`Analyze.java` contains the main method that is run to analyze generated patient data with TETRAD.

### Run.java

The `Run` class represents the largest grouping of simulation data. Each Run contains multiple `ParameterSet`s. Every time `Generate.java` is run, it creates a new `Run` in the database. 

### ParameterSet.java

The `ParameterSet` class is a structure that contains parameters that are used to generate patient data.

### Simulation.java

The `Simulation` class represents a single simulated set of patient data. Each simulation uses one `ParameterSet` to generate its patient data. 

### Case.java

The `Case` class represents one patient and its associated variables. Each case belongs to one simulation.   

### CaseGenerator.java

The `CaseGenerator` class returns generated cases using a given parameter set. 






