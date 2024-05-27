package me.keegan.utils;

public interface protocolUtil {
    void addPacketListener();

    static void registerPackets(protocolUtil protocolUtil) {
        protocolUtil.addPacketListener();
    }
}
