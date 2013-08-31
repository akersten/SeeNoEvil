/*
 Project: SeeNoEvil
 File: PCAPFile.java (com.alexkersten.SeeNoEvil.parser)
 Author: Alex Kersten
 */
package com.alexkersten.SeeNoEvil.parser;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * A PCAPFile represents a .pcap on disk. We'll constantly poll this object for
 * more "packets" to parse out.
 *
 * @author Alex Kersten
 */
public class PCAPFile {

    //A file that this PCAPFile object refers to. If we get created, this is
    //ensured to be set and readable.
    private final Path fileOnDisk;

    private final FileChannel fc;

    public PCAPFile(Path fileOnDisk) throws IOException {
        this.fileOnDisk = fileOnDisk;
        fc = FileChannel.open(fileOnDisk, StandardOpenOption.READ);


    }

    /**
     * Whether the PCAP file contains another packet in the stream that we've
     * been reading out of it, <b>and</b> if the file itself has that packet
     * available to read.
     *
     * @return Whether another packet is available to be read out of the PCAP
     * file.
     */
    public boolean hasNext() {
        return false;
    }
}
