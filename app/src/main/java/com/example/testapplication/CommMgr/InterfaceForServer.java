package com.example.testapplication.CommMgr;

public class InterfaceForServer {
    public InterfaceForServer() {
    }

    /**
     * @param srcID    : client ID
     * @param dstID    : server ID
     * @param dataType : data type of server protocol
     * @param payload  : payload to send message to server
     */
    public static void toSendMessageWithSocket(SocketMgr sock, String srcID, String dstID, int dataType, String payload) {
        /* todo. handler an exception for no server */
        String jsonForPositionInfo;
        PacketMgr pkt = new PacketMgr();
        jsonForPositionInfo = pkt.makeInputToJsonStr(srcID, dstID, dataType, payload);
        sock.send(jsonForPositionInfo);
    }
}
