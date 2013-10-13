package org.ros.tf2_ros;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import java.util.Arrays;
import java.util.List;

import geometry_msgs.TransformStamped;
import tf2_msgs.TFMessage;

public class TransformBroadcaster extends AbstractNodeMain {
    private Publisher<TFMessage> mPublisher;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("transform_broadcaster"); ///< @TODO Randomize
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        mPublisher = connectedNode.newPublisher("/tf", TFMessage._TYPE);
    }

    //TransformBroadcaster::sendTransform(const geometry_msgs::TransformStamped & msgtf)
    public void sendTransform(final TransformStamped msgtf){
        List<TransformStamped> v1 = Arrays.asList(msgtf);
        sendTransform(v1);
    }

    public void sendTransform(final List<TransformStamped> msgtf){
        TFMessage message = mPublisher.newMessage();
        message.setTransforms(msgtf);
        mPublisher.publish(message);
    }
}
