package nutsAndBolts;

import com.google.gson.annotations.Expose;
import controller.Mediator;
import controller.OutputModelData;

import java.util.*;

public class NetworkMessage {
    public enum MsgType {
        Color,
        ColorACK,
        MoveCapturePromote
    }

    @Expose
    private UUID senderUUID;

    @Expose
    private MsgType messageType;

    @Expose
    private List<Integer> params;

    @Expose
    private OutputModelData<Integer> outputModelData;

    private NetworkMessage(MsgType messageType) {
        this.messageType = messageType;
        this.senderUUID = Mediator.clientUUID;
    }

    public NetworkMessage(MsgType messageType, List<Integer> params, OutputModelData<Integer> omd) {
        this(messageType);
        if (params != null && params.size() > 0) {
            this.params = new ArrayList<>(params);
        }
        this.outputModelData = omd;
    }

    public UUID getSenderUUID() {
        return senderUUID;
    }

    public MsgType getMessageType() {
        return messageType;
    }

    public ArrayList<Integer> getParams() {
        return new ArrayList<>(params);
    }

    public OutputModelData<Integer> getOutputModelData() {
        return outputModelData;
    }
}
