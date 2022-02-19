# SYSC 3303 Elevator Project
L4 Group 8

- Jason Gao 101108461
- Shashaank Srivastava 101105658
- Kevin Quach 101115704
- Michael Quach 101179729
- Harjap Gill - 101124926


## Setup
Open Eclipse

File -> Import -> General -> Projects from Folder or Archive -> Select iteration archive -> Select nested folder <em>SYSC3303_ElevatorSystem</em> -> Finish

## How to run (production)
Open a terminal and run 

``java -jar Scheduler.jar``

## How to run (local)
Open the project in eclipse -> Open SchedulerSystem.java -> Run As -> Java Application

## Running JUnit tests
Open the project in eclipse -> right click the test folder -> Run As -> JUnit Test

## Documentation
Documentation is found in doc/

## Iteration 0

##### Time to unload and load the elevator?
We will take the average of the times given my the professor
(8 + 9.9+11.0+7.8) / 4 = 9.175
Therefore, the time for the elevator doors to open, load/unload and close is 9.175 seconds

##### Height of floors
The distance between each floor was found to be 4 metres

##### Speed
We will not include acceleration and assume constant speed to help find a good estimate for max speed of elevator
Average time to travel 7 floors
(17.6 + 19.6 + 19.6 + 22.5) / 4 = 19.825 second
The height of each floor is 4 metres so total distance traveled
 4 metres * 7 floors = 28 metres
We can now calculate top speed
Velocity = distance / time
Velocity = 28 metres / 19.825 seconds
Velocity = 1.412 metres / sec

##### Acceleration 
We could not determine acceleration from the give data so we will use an acceleration of an elevator found online
The value of the acceleration was found to be 1.148 metres / sec2
https://hypertextbook.com/facts/2009/AmosBaptiste.shtml


## Iteration 1 Work Distribution
##### Jason Gao
- Floor System
- Floor message data parse
- Floor message class (deprecated)
- ByteParse (deprecated)

#####  Kevin Quach
- Schduler Datagram Communicator
- Scheduler Subsystem

#####  Michael Quach
- Serializable floor data message
- Scheduler Datagram Communicator
- Scheduler subsystem
- Box Class
- Elevator System

#####  Shashaank Srivastava
- Unit Testing
- UML Diagrams
  
#####  Harjap Gill
- Unit Testing
- UML Diagrams
- Elevator class
- ReadMe

## Iteration 2 Work Distribution
##### Jason Gao
- ByteBuffer
- ByteBufferCommunicator
- Elevator state machine (shared work)
- Pair programming with Harjap Gill

#####  Kevin Quach
- SchedulerSystem
- SchedulerRequestHandler
- Scheduler unit tests
- Scheduler state machine (shared)
- Pair programming with Michael Quach

#####  Michael Quach
- SchedulerSystem
- SchedulerResponseHandler
- SchedulerElevatorData
- FloorResponseHandler
- FloorLightResponseMessage
- Scheduler state machine (shared)
- Pair programming with Kevin Quach for scheduler systems

#####  Shashaank Srivastava
  
#####  Harjap Gill
- Scheduler state machine (shared)
- Elevator state machine (shared work)
- Pair programming with Jason Gao
