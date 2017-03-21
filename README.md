# javaGui
GUI for interfacing with the fridgeController

### 8 - Enter adding mode
Sent to the listener from the android app to automatically enter adding mode

	8[0][Optional timeout][0][padding to 100 bytes]

### 9 - Dump Expires Before
Sends a stream of 7 packets containing FoodItems that expire before 'Day' to whoever sent the 9 packet, terminated by a blank 7 packet (none will result in a single, blank 7 packet). 
If 'Day' is zero, it will be set to MAX_INT and used as a check, this will dump all items in the fridge, except possibly for universes that aren't dying soon (>5.8 million years)

	9[0][Day][0]
