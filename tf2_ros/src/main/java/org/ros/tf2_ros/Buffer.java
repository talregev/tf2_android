package org.ros.tf2_ros;

import android.util.Log;

import geometry_msgs.Quaternion;
import geometry_msgs.Transform;
import geometry_msgs.TransformStamped;
import geometry_msgs.Vector3;
import std_msgs.Header;

import org.ros.message.Duration;
import org.ros.message.MessageFactory;
import org.ros.message.Time;
import org.ros.node.NodeConfiguration;

import java.util.Random;

public class Buffer {
    static {
        System.loadLibrary("tf2_ros");
    }

    NodeConfiguration mNodeConfiguration;
    MessageFactory mMessageFactory;

    public Buffer(){
        this(new Duration(0));
    }

    public Buffer(Duration cache_time){
        mNodeConfiguration = NodeConfiguration.newPrivate();
        mMessageFactory = mNodeConfiguration.getTopicMessageFactory();
        if(cache_time.isPositive()){ // Override default cache_time
            newBufferWithCacheDuration(cache_time.secs, cache_time.nsecs);
        }
        clear();
    }

    private native void newBufferWithCacheDuration(int sec, int nsec);

    public boolean setTransform(final geometry_msgs.TransformStamped transform){
        return setTransform(transform, "unknown", false);
    }

    public boolean setTransform(final geometry_msgs.TransformStamped transform, String authority){
        return setTransform(transform, authority, false);
    }

    public boolean setTransform(final geometry_msgs.TransformStamped transform, String authority, boolean is_static){
        final Header h = transform.getHeader();
        final Time s = h.getStamp();
        final Transform t = transform.getTransform();
        final Vector3 tr = t.getTranslation();
        final Quaternion q = t.getRotation();
        return setTransform(h.getFrameId(), s.secs, s.nsecs, transform.getChildFrameId(),
                tr.getX(), tr.getY(), tr.getZ(),
                q.getW(), q.getX(), q.getY(), q.getZ(), authority, is_static);
    }

    /* Decided to break it out since I read that finding JNI method IDs are expensive, and ROSJava messages are  almost entirely calling getters */
    private native boolean setTransform(final String frame_id, final int sec, final int nsec, final String child_frame_id,
                                        final double tx, final double ty, final double tz,
                                        final double rw, final double rx, final double ry, final double rz, final String authority, final boolean is_static);

    /* Convenience form */
    public geometry_msgs.TransformStamped lookupTransform(String target_frame, String source_frame, Time time){
        TransformStamped tfs = mMessageFactory.newFromType(TransformStamped._TYPE);
        boolean result = lookupTransform(tfs, target_frame, source_frame, time);
        if(result == false){
            return null;
        }
        return tfs;
    }

    /* Performance form */
    public boolean lookupTransform(geometry_msgs.TransformStamped tfs, String target_frame, String source_frame, Time time){
        // Lookup through JNI
        // double[] doubles = tx, ty, tz, rw, rx, ry, rz, sec, nsec
        double[] doubles = lookupTransform(target_frame, source_frame, time.secs, time.nsecs);
        if(doubles.length != 9){
            return false;
        }
        Time actual_time = new Time((int)doubles[7], (int)doubles[8]);

        // Set output transform
        Header h = tfs.getHeader();
        h.setFrameId(source_frame);
        tfs.setChildFrameId(target_frame);
        h.setStamp(actual_time);
        Transform t = tfs.getTransform();
        Vector3 tr = t.getTranslation();
        Quaternion q = t.getRotation();
        tr.setX(doubles[0]);
        tr.setY(doubles[1]);
        tr.setZ(doubles[2]);
        q.setW(doubles[3]);
        q.setX(doubles[4]);
        q.setY(doubles[5]);
        q.setZ(doubles[6]);
        return true;
    }

    /* Simplest JNI interface that passes by references are arrays of primitive types. */
    /* If this is too slow, could try passing by serializing */
    private native double[] lookupTransform(String target_frame, String source_frame, int sec, int nsec);

    /*geometry_msgs::TransformStamped
    lookupTransform(const std::string& target_frame, const ros::Time& target_time,
            const std::string& source_frame, const ros::Time& source_time,
            const std::string& fixed_frame) const;

    bool canTransform(const std::string& target_frame, const std::string& source_frame,
                    const ros::Time& time, std::string* error_msg = NULL) const;

    bool canTransform(const std::string& target_frame, const ros::Time& target_time,
                    const std::string& source_frame, const ros::Time& source_time,
                    const std::string& fixed_frame, std::string* error_msg = NULL) const;*/

    
    public native String allFramesAsYAML();

    public native String allFramesAsString();

    public native void clear();

    public native void loadPR2Tree();

    public native String[] getPR2FrameIds();
}
