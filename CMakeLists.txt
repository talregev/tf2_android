cmake_minimum_required(VERSION 2.8.3)
project(tf2_android)

find_package(catkin REQUIRED rosjava_build_tools)

catkin_android_setup(assembleRelease uploadArchives)

catkin_package()

install(DIRECTORY ${CATKIN_DEVEL_PREFIX}/${CATKIN_GLOBAL_MAVEN_DESTINATION}/org/ros/tf2/
        DESTINATION ${CATKIN_GLOBAL_MAVEN_DESTINATION}/org/ros/tf2/)
