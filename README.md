# 3Dprint-Overhang-gcode-editor
This is just a proof of concept program that will print 90 degree overhangs without support. It expands the previous layer to form a base for the overhang.

I made after being inspired by CNC Kitchen's video on printing 90 degree overhangs without supports. In that video, successive arcs with increasing radii made the base of the overhang. In this program, I use the previous layer's outer wall as the starting point of the overhang. Essentially, the nozzle expands the outer layer until it reaches the correct geometry for the layer with overhang. 

This was designed for CURA 5.0 and has its quirks. 
In order to use this you need to:
1. Slice the model you want to print in CURA
2. Export the gcode to your computer. 
3. Run the program
4. Input your printer's nozzle diameter
5. Type M for manual (I didn't make the automatic detection yet)
7. Go into CURA and use the preview function to see which layer of the model has an overhang
8. Input that layer number into the program.
9. Select the gcode file that you exported.
10. Choose where to save your new gcode file.

NOTE: This has its problems. It works best when the part has a single outer wall per layer. Not multiple disconnected outer walls. When the program functions correctly, it makes some amazing overhangs. No support at all!
