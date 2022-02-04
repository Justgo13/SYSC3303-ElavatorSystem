<h1> SYSC 3303 Elevator Project <h1>
<h3> L4 Group 8</h3>
<ul>
  <li> Jason Gao </li>
  <li> Kevin Quach 101115704</li>
  <li> Michael Quach 101179729</li>
  <li> Shashaank Srivastava </li>
  <li> Harjap Gill - 101 124 926 </li>
</ul>

<h2> Iteration 0 </h2>

<h4> Time to unload and load the elevator? </h4>
We will take the average of the times given my the professor
(8 + 9.9+11.0+7.8) / 4 = 9.175
Therefore, the time for the elevator doors to open, load/unload and close is 9.175 seconds

<h4> Height of floors </h4>
The distance between each floor was found to be 4 metres

<h4>Speed</h4>
We will not include acceleration and assume constant speed to help find a good estimate for max speed of elevator
Average time to travel 7 floors
(17.6 + 19.6 + 19.6 + 22.5) / 4 = 19.825 second
The height of each floor is 4 metres so total distance traveled
 4 metres * 7 floors = 28 metres
We can now calculate top speed
Velocity = distance / time
Velocity = 28 metres / 19.825 seconds
Velocity = 1.412 metres / sec

<h4> Acceleration </h4>
We could not determine acceleration from the give data so we will use an acceleration of an elevator found online
The value of the acceleration was found to be 1.148 metres / sec2
https://hypertextbook.com/facts/2009/AmosBaptiste.shtml


<h2> Iteration 1 Work Distribution </h2>
<h3> Jason Gao </h3>
  <ul>
    <li>Floor System </li>
    <li> Floor message data parse</li>
    <li> Floor message class (deprecated)</li>
    <li> ByteParse (deprecated)</li>
  </ul>

<h3> Kevin Quach </h3>
  <ul>
    <li> Schduler Datagram Communicator</li>
    <li> Scheduler Subsystem </li>
  </ul>

<h3> Michael Quach </h3>
  <ul>
    <li> Serializable floor data message </li>
    <li> Scheduler Datagram Communicator </li>
    <li> Scheduler subsystem</li>
    <li> Box Class </li>
    <li> Elevator System </li>
  </ul>

<h3> Shashaank Srivastava </h3>
  <ul>
    <li> Unit Testing </li>
    <li> UML Diagrams </li>
  </ul>
  
<h3> Harjap Gill </h3>
  <ul>
    <li> Unit Testing </li>
    <li> UML Diagrams </li>
    <li> Elevator class </li>
    <li> ReadMe </li>
  </ul>



