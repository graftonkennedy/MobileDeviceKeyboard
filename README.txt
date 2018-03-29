MobileDeviceKeyboard README

The main class is MobileDeviceKeyboard in MobileDeviceKeyboard.java.
The test class is MobileDeviceKeyboardTest in MobileDeviceKeyboardTest.java.

To install, download and unzip MobileDeviceKeyboard.zip.

MobileDeviceKeyboard expects MobileDeviceKeyboard-input01.txt to be in the
current directory.
In MobileDeviceKeyboard.java you can uncomment the line containing
"MobileDeviceKeyboard-input00.txt" and comment-out the line containing
"MobileDeviceKeyboard-input01.txt" to switch to the original test data from
the problem statement.

To run:
  $ java MobileDeviceKeyboard

To run TestRunner from TestRunner.java:
    ("dojavac" is "dojavac.bat" or "dojavac.sh".  It runs the Java Compiler
    on every .java file in the directory.  TestRunner requires the .class files it generates.
  $ dojavac
  $ java TestRunner

Documentation is available in the HTML files in the html directory or online at:
http://htmlpreview.github.io/?https://github.com/graftonkennedy/MobileDeviceKeyboard/master/html/index.html

Troubleshooting:
The CLASSPATH may not include the current directory.
Try
  $ java -cp . MobileDeviceKeyboard


I'm using Java 8 as indicated here:

$ java -version
openjdk version "1.8.0_161-1-redhat"
OpenJDK Runtime Environment (build 1.8.0_161-1-redhat-b14)
OpenJDK 64-Bit Server VM (build 25.161-b14, mixed mode)

I'm using Visual Studio Code.  Hence the .vscode directory and files.
I've included dojavac.bat and dojavadoc.bat that run the Java compiler and Javadoc similar to the way that the .vscode\tasks.json file defines them.
