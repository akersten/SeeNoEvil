/*
 Project: SeeNoEvil
 File: PCAPFile.java (com.alexkersten.SeeNoEvil.parser)
 Author: Alex Kersten
 */
package com.alexkersten.SeeNoEvil.parser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * A PCAPFile represents a .pcap on disk. We'll constantly poll this object for
 * more "packets" to parse out.
 *
 * When using this class with a file that's being constantly updated, a big
 * assumption is being made: whenever that file gets flushed to by whatever is
 * writing to it, it's writing complete packets - otherwise our parsing will
 * fail, or only be partial. This is unlikely to be the case - it's likely just
 * flushing a buffer out to disk - so for the future we'll fix this by checking
 * the header of packets against the length of the file we can see.
 *
 * @author Alex Kersten
 */
public class PCAPFile {

    public static final int PCAP_MAGIC_NUMBER = 0xa1b2c3d4;

    //A file that this PCAPFile object refers to. If we get created, this is
    //ensured to be set and readable.
    private final Path fileOnDisk;

    private final FileChannel fc;

    public PCAPFile(Path fileOnDisk) throws IOException {
        this.fileOnDisk = fileOnDisk;
        fc = FileChannel.open(fileOnDisk, StandardOpenOption.READ);

        //Check magic number
        ByteBuffer wordBuf = ByteBuffer.allocate(4);
        wordBuf.order(ByteOrder.LITTLE_ENDIAN);

        if (fc.read(wordBuf) != 4) {
            throw new IOException(
                    "Empty file! (Not long enough for magic number.)");
        }

        wordBuf.flip();

        //FIXME: Support for other byte orderings and nanosecond resolution as
        //contained in alternate magic numbers
        if (wordBuf.getInt() != PCAP_MAGIC_NUMBER) {
            throw new IOException("Magic number mismatch - check PCAP file.");
        }

        //Skip all the other header junk
        fc.position(fc.position() + 20);
    }

    /**
     * Whether the PCAP file still has data to be read.
     *
     * @return Whether the FileChannel is at the end of its stream or not. No
     * promises if it's actually a real packet though or junk data, you'll just
     * have to attempt a read.
     */
    public boolean hasNext() throws IOException {
        return fc.position() != fc.size();
    }

    /**
     * Parse out a packet... Maybe some packets are split and need to be
     * reconstructed - do that too before returning here.
     *
     * @return The next packet or reconstructed multi-part packet in the stream.
     */
    public PCAPPacket getNext() throws IOException {
        if (!hasNext()) {
            throw new RuntimeException("getNext called without checking hasNext!");
        }

        //Attempt to parse a packet.

        //Read in the packet header - first the time
        ByteBuffer wordBuf = ByteBuffer.allocate(4);
        wordBuf.order(ByteOrder.LITTLE_ENDIAN);

        if (fc.read(wordBuf) != 4) {
            throw new IOException("Sudden EOF - time");
        }

        wordBuf.flip();
        int thisTime = wordBuf.getInt();
        wordBuf.flip();

        //Now the microtime
        if (fc.read(wordBuf) != 4) {
            throw new IOException("Sudden EOF - microtime");
        }

        wordBuf.flip();
        int thisUTime = wordBuf.getInt();
        wordBuf.flip();

        //Now the packet captured length
        if (fc.read(wordBuf) != 4) {
            throw new IOException("Sudden EOF - incl_len");
        }

        wordBuf.flip();
        int thisInclLen = wordBuf.getInt();
        wordBuf.flip();


        //Finally, the original packet length - we might have to reconstruct.
        //Now the packet captured length
        if (fc.read(wordBuf) != 4) {
            throw new IOException("Sudden EOF - orig_len");
        }

        wordBuf.flip();
        int thisOrigLen = wordBuf.getInt();

        if (thisOrigLen == thisInclLen) {
            //Simple enough - just make a new object with this info and copy
            //the data buffer over
            PCAPPacket p = new PCAPPacket(thisTime, thisUTime, thisOrigLen);
            ByteBuffer dbuf = ByteBuffer.allocate(thisOrigLen);

            if (fc.read(dbuf) != thisOrigLen) {
                throw new IOException("Sudden EOF - short data");
            }

            dbuf.flip();
            System.arraycopy(dbuf.array(), 0, p.getData(), 0, thisOrigLen);

            return p;

        } else {
            //FIXME: Support for data split across multiple packets...
            System.err.println("Big packet, giving up");

            return null;
        }
    }
}
