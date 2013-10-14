tf2_android
===========

Android NDK Wrapper of tf2

How to use tf2_android:

Install ROSJava using these instructions:
http://wiki.ros.org/android/Tutorials/hydro/Installation%20-%20Ros%20Development%20Environment

Now chain the tf2 workspace to the android workspace:
mkdir -p ~/tf2a/src
cd ~/tf2a/src
git clone [this_repo]
cd ..
source ~/android/devel/setup.bash
catkin_make

Now make your own workspace:
mkdir -p ~/my_app_wksp/src
cd ~/my_app_wksp/src

(Run catkin_create_android_project and catkin_create_android_pkg):
http://wiki.ros.org/rosjava_build_tools

cd ..
catkin_make

Now open in your editor of choice

To use tf2_ros as a dependency, edit the build.gradle of your android_pkg and update
depdencies to be the following:

dependencies {
		compile 'org.ros.tf2:tf2_ros:0.0.0-SNAPSHOT'
}

Now you should be able to use the tf2 rosjava classes in your app.