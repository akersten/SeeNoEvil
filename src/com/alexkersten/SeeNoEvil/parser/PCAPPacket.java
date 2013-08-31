/*
 Project: SeeNoEvil
 File: PCAPPacket.java (com.alexkersten.SeeNoEvil.parser)
 Author: Alex Kersten
 */
package com.alexkersten.SeeNoEvil.parser;

/**
 * A class which is self-aware about the kind of content in a PCAPPacket...
 *
 * @author Alex Kersten
 */
public class PCAPPacket {

    //The length of this packet (bytes)
    private final int payloadSize;

    //When this packet was captured
    private final int time;

    //How many nano/microseconds this packet was captured after 'time'
    private final int utime;

    private final byte[] data;

    public PCAPPacket(int time, int utime, int payloadSize) {
        this.payloadSize = payloadSize;
        data = new byte[payloadSize];
        this.time = time;
        this.utime = utime;
    }

    /**
     * @return the size
     */
    public int getPayloadSize() {
        return payloadSize;
    }

    /**
     * @return the time
     */
    public int getTime() {
        return time;
    }

    /**
     * @return the utime
     */
    public int getUtime() {
        return utime;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }
}
