[![Build Status](https://travis-ci.org/OpenMined/AndroidWorker.svg?branch=master)](https://travis-ci.org/OpenMined/AndroidWorker)

![PySyft for Android](art/pysyft_android.png)

See publication in [PySyft for Android. Extending OpenMined to mobile devices](https://medium.com/@mccorby/pysyft-android-b28da47a767e)

# Android Worker

The Android Worker is an app that connects to a PySyft worker and performs the operations on its behalf. It is part of a setup that uses a socket server, a socket client and the app itself which is another socket client

## Quick start

* Start the socket server
  * There is an example provided in the code of PyGrid: [`socketio_server_demo.py`](https://github.com/OpenMined/PyGrid/blob/dev/examples/android/socketio_server_demo.py)
* Start the Android application
  * If you're running the application on a physical device (instead of the Android emulator):
    * Edit the IP address in [`MainActivity.kt`](https://github.com/OpenMined/AndroidWorker/blob/dev/app/src/main/java/com/mccorby/openmined/worker/ui/MainActivity.kt#L39) to match the IP address of the machine running the socket server
    * Make the same edit to the IP address in [`network_security_config.xml`](https://github.com/OpenMined/AndroidWorker/blob/dev/app/src/main/res/xml/network_security_config.xml#L5)
* Press the "Connect" button in the Android app    
* Start a Jupyter notebook and create a `WebsocketIOClientWorker` object.
  * Note that you need to provide strategies for serialization and compression
  * See the example [`Socket Bob.ipynb`](https://github.com/OpenMined/PyGrid/blob/dev/examples/android/Socket%20Bob.ipynb)
* Connect the socketio client (Bob)
* Execute the operations in the notebook and see how Android handles them!


#### Notes
* The project is still in an early stage and only some PySyft operations are implemented: `send`, `get`, `add`, `delete`, `mul`
* Make sure the socket server, the client in the notebook and the app all point to the same host/port
* To run the setup locally, it is better to use an Android emulator

## PySyft Version
This app has been tested with PySyft 0.2.0a2 (b0b8a135) and PyGrid commit 8f990d97


