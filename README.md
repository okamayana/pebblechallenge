Pebble Android Challenge
===

Attached is a simple program "server.py" that, when started, listens at port 1234 for incoming socket connection.
After a client connects, the server will spit out a stream of messages.

Protocol
---

Each message is a "color command", which the client should use to change the color of the interface in real-time.
The color of the interface is represented by 3x 8-bit values, R, G and B.
The connecting client is supposed to keep state of the current color. Upon connect, the color should be reset to (127, 127, 127).
There are 2 command types, relative and absolute.

## Relative

The relative command, changes the components with a relative offset.
The message has this structure:

* 8-bit constant 	                  -- 0x01 ("relative" command)
* 16-bit signed int, network endian -- R offset
* 16-bit signed int, network endian -- G offset
* 16-bit signed int, network endian -- B offset

The offset fields are 16-bit for future purposes.
Upon receiving a "relative" command, the client is supposed to offset the current components with the given offsets.


## Absolute

The absolute command, changes the components to the specified absolute values.
The message has this structure:

* 8-bit constant 	   -- 0x02 ("absolute" command)
* 8-bit unsigned int -- absolute R value
* 8-bit unsigned int -- absolute G value
* 8-bit unsigned int -- absolute B value

Upon receiving a "absolute" command, the client is supposed to set the current components to the given absolute values.


Challenge
---

Write an Android application that can connect to the "server.py" program, and interpret the incoming messages.
Make a view controller that shows a log of the received commands.
Use ListView for the log, where each command is displayed in a TextView.

When a new command is received, the cell that represents the command would be selected by default.
If the received command is "absolute", all previously selected cells will be deselected.
The cells of the table view should also allow to be manually selected / deselected when tapping on them.
The current RGB values should be calculated from the selected commands.

Also create a TextView that displays the current RGB value (by "summing up" the selected commands) at all times.

Design goals in order of importance:
* Clean, well-structured code. Good separation of concerns.
* Low memory footprint.
* Speed.

You are only allowed to use the standard Android frameworks.

As a reference: The "server.py" program prints the expected current RGB values to stdout, just before sending out the next command.
