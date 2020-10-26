# -*- coding: utf-8 -*-
"""
Created on Sat Aug 29 23:21:25 2020

@author: Pavan Kalyan Khasha
"""
import pygame

pygame.init()
j = pygame.joystick.Joystick(0)
j.init()
def getname():
    return ('Initialized Joystick : %s' % j.get_name())
print(getname())
"""
Returns a vector of the following form:
[LThumbstickX, LThumbstickY, Unknown Coupled Axis???, 
RThumbstickX, RThumbstickY, 
Button 1/X, Button 2/A, Button 3/B, Button 4/Y, 
Left Bumper, Right Bumper, Left Trigger, Right Triller,
Select, Start, Left Thumb Press, Right Thumb Press]

Note:
No D-Pad.
Triggers are switches, not variable. 
Your controller may be different
"""

def get():
    out = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
    pygame.event.pump()
    
    #Read input from the two joysticks       
    for i in range(0, j.get_numaxes()):
        out[i] = "{:.3f}".format(j.get_axis(i))
    #Read input from buttons
    for i in range(4, j.get_numbuttons()):
        out[i] = j.get_button(i)
    return out

def test():
    while True:
        print(get())
#test()
#You should be able to get the d-pad values with j.get_hatsnum() and j.get_hat(1). And i is only 0 since there is only one d-pad. (The output is a tuple)
