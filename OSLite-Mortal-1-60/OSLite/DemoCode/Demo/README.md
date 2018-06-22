## About
### This demo gives a typical example to develop your own robot based on Tuling Development Framework.

## How To
### 1. Necessary configurations have all been set in this demo. So you can check it directly in the mentioned file below.
### 2. Read comment in source code in this demo.

## Configuration
### If you want to do the configuration by your self, check all steps beneath.

- compileSdkVersion 22
- minSdkVersion 19
- targetSdkVersion 22
- new module named as RobotFrame for RobotFrame.aar(Aofei only)
- add following configuration in your app's build.gradle
    - in defaultConfig block  

            ndk {  
                abiFilters 'armeabi'  
            }
        
    - in dependencies block (Aofei available only)
    
            compile project (':RobotFrame')
            compile 'com.android.support:appcompat-v7:22.2.0'
            compile 'com.android.support.constraint:constraint-layout:1.0.1'
            compile 'com.tencent.bugly:crashreport:2.4.0'
            compile 'com.tencent.bugly:nativecrashreport:3.1.2'
            compile 'com.squareup.retrofit2:adapter-rxjava:2.1.0'
            compile 'com.squareup.retrofit2:converter-gson:2.1.0'
            compile 'com.squareup.retrofit2:retrofit:2.1.0'
            compile group: 'com.google.code.gson', name: 'gson', version: '2.8.0'
            compile 'io.reactivex:rxjava:1.2.5'
            compile 'pl.droidsonroids.gif:android-gif-drawable:1.2.4'
        
        
- add permission in manifest

        SYSTEM_ALERT_WINDOW
        READ_EXTERNAL_STORAGE
        WRITE_EXTERNAL_STORAGE
        RECORD_AUDIO
        WRITE_SETTINGS
        READ_PHONE_STATE
        BROADCAST_STICKY
        BLUETOOTH
        INTERNET
        ACCESS_NETWORK_STATE
        ACCESS_WIFI_STATE
        CHANGE_WIFI_STATE
        READ_LOGS
    
- add repositories(Ignore this step for Aofei)
    - in buildscript block  
    
            mavenCentral()
            
    - in allprojects block
    
            maven{ url 'URL_ADDRESS' }
            
## Trouble Shooting
1. If there is error about android:roundIcon, please delete it in AndroidManifest.xml 

        
        
    
    
    
    