# -*- coding: utf-8 -*-
"""
Created on Sat Aug 29 23:23:37 2020

@author: Pavan Kalyan Khasha
"""
import sys
sys.path.append('C:/Users/Pavan Kalyan Khasha/Desktop/Qtdesigner')

import gamepad

from PyQt5.QtWidgets import QMainWindow,QApplication,QPushButton,QRadioButton,QProgressBar,QLabel,QLineEdit
from PyQt5 import uic
import sys
import time
#from firebase import firebase

#Database connection
#firebase = firebase.FirebaseApplication('https://reciever-a3e3d.firebaseio.com/', None)
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

# Fetch the service account key JSON file contents
cred = credentials.Certificate('C:/Users/Pavan Kalyan Khasha/Desktop/Qtdesigner/firebase-adminsdk.json')
# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://reciever-a3e3d.firebaseio.com/'
}) 
ref = db.reference('values')
 
class UI(QMainWindow):
    def __init__(self):
        super(UI, self).__init__()
        uic.loadUi("C:/Users/Pavan Kalyan Khasha/Desktop/Qtdesigner/Transmitter.ui", self)
 
        # find the widgets in the xml file
        self.progressBar=self.findChild(QProgressBar,"progressBar")
        self.progressBar_2=self.findChild(QProgressBar,"progressBar_2")
        self.progressBar_3=self.findChild(QProgressBar,"progressBar_3")
        self.progressBar_4=self.findChild(QProgressBar,"progressBar_4")
        self.pushButton_5=self.findChild(QPushButton,"pushButton_5")
        self.pushButton=self.findChild(QPushButton,"pushButton")
        self.label_10=self.findChild(QLabel,"label_10")
        self.lineEdit=self.findChild(QLineEdit,"lineEdit")
        #self.pushButton_4=self.findChild(QPushButton,"pushButton_4")
        self.radioButton_2=self.findChild(QRadioButton,"radioButton_2")
        #self.pushButton_4.setCheckable(True)
        self.pushButton_5.clicked.connect(self.ClickedBtn)
        self.pushButton.clicked.connect(self.Exit)
        self.show()
  
        #write functions
    def ClickedBtn(self):
        self.label_10.setText(gamepad.getname())
        print(self.radioButton_2.isChecked())
        b=50
        d=50
        while self.radioButton_2.isChecked():
            #Makes Slow motion if >1
            #time.sleep(0.5)
            self.x = gamepad.get()
            print(self.x)
            a=int(((float(self.x[0])*1000)+1000)/20)
            c=int(((float(self.x[2])*1000)+1000)/20)
            if(b<0):
                b=0
            if(b>100):
                b=100
            if(d<0):
                d=0
            if(d>100):
                d=100
            if(self.x[4]==1):
                b+=3
            if(self.x[6]==1):
                b-=3
            if(self.x[5]==1):
                d+=3
            if(self.x[7]==1):
                d-=3
            print("ajefvjsvfjvsj")
            self.progressBar.setValue(a)
            self.progressBar_2.setValue(b)
            self.progressBar_3.setValue(c)
            self.progressBar_4.setValue(d)
            self.lineEdit.setText(str(a)+"    "+str(b)+"    "+str(c)+"    "+str(d))
            print("going update")
            #firebase.put('/values','a',a)
            #firebase.put('/values','b',b)
            #firebase.put('/values','c',c)
            #firebase.put('/values','d',d)
            ref.update({'a': a,'b': b,'c':c,'d':d})
            print("cpmplteupdate")
            
            
    def Exit(self):
        print("exiting")
        self.lineEdit.setText("Thank You......")
        sys.exit()
        
 
#Executing 
app = QApplication(sys.argv)
window = UI()
sys.exit(app.exec_())
