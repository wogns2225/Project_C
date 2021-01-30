package com.example.testapplication.CommMgr;

public class InterfaceForServerAPI {
    public InterfaceForServerAPI() {
    }

    /**
     * @param srcID    : client ID
     * @param dstID    : server ID
     * @param dataType : data type of server protocol
     * @param payload  : payload to send message to server
     */
    public static void toSendMessageWithSocket(SocketMgr sock, String srcID, String dstID, String dataType, String payload) {
        /* todo. handler an exception for no server */
        String jsonForPositionInfo;
        jsonForPositionInfo = PacketAPI.makeInputToJsonStr(srcID, dstID, dataType, payload);
        sock.send(jsonForPositionInfo);
    }
}
