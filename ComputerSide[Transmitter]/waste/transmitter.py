# -*- coding: utf-8 -*-
"""
Created on Sat Aug 29 20:12:07 2020

@author: Pavan Kalyan Khasha
"""

from PyQt5.QtWidgets import QMainWindow, QApplication, QPushButton, QTextEdit
from PyQt5 import uic
import sys
 
 
class UI(QMainWindow):
    def __init__(self):
        super(UI, self).__init__()
        uic.loadUi("mfile.ui", self)
 
        # find the widgets in the xml file
 
 
        self.show()
 
        #write functions
 
   
 
#Executing 
app = QApplication(sys.argv)
window = UI()
app.exec_()