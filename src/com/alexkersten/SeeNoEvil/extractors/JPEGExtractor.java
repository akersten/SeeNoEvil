/*
 Project: SeeNoEvil
 File: JPEGExtractor.java (com.alexkersten.SeeNoEvil.extractors)
 Author: Alex Kersten
 */
package com.alexkersten.SeeNoEvil.extractors;

import com.alexkersten.SeeNoEvil.parser.PCAPPacket;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Alex Kersten
 */
public class JPEGExtractor {

    /**
     * Check the file for the content type JPEG JFIF words - if it's got these,
     * it probably is a JPEG.
     *
     * @param p A PCAPPacket object.
     * @return Whether this packet likely contains a JPEG.
     */
    public static boolean packetHasJFIF(PCAPPacket p) {
        //FIXME: This is bad for obvious reasons and will give some false results
        //but we're testing with hand-picked data here...
        return (new String(p.getData())).contains(" image/jpeg");
    }

    /**
     * *
     * Assuming you've called packetHasJFIF on this and it returned true, this
     * will return the contained JPEG image for you.
     *
     * @param p A PCAPPacket object.
     * @return A JPEG image from a packet, in a BufferedImage.
     */
    public static BufferedImage getJFIFFromPacket(PCAPPacket p) throws IOException {
        String search = new String(p.getData());
        //FIXME: Ignore how bad this next line is - it's just for testing.
        //It finds the JPEG in this packet and then uses ImageIO to read it.
        int where = search.indexOf(new String(
                new byte[]{(byte) 0xff, (byte) 0xd8, (byte) 0xff, (byte) 0xe0,
                           (byte) 0x00, (byte) 0x10, (byte) 0x4a, (byte) 0x46,
                           (byte) 0x49, (byte) 0x46
        }));

        if (where == -1) {
            return null;
        }

        byte[] imgdata = new byte[p.getPayloadSize() - where];

        System.arraycopy(p.getData(), where, imgdata, 0, imgdata.length);
        return ImageIO.read(new ByteArrayInputStream(imgdata));
    }
}
