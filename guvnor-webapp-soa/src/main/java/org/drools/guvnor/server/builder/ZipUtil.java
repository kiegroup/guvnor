package org.drools.guvnor.server.builder;

import org.drools.repository.AssetItem;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * This Class Allows us to Zip a set of Assets, in fact we generate an extra asset
 * whith the "zip" format, so we can add it to the package.
 */
public class ZipUtil {

    private final List<AssetItem> assets;

    public ZipUtil(List<AssetItem> assets) {
        this.assets = assets;
    }

    public InputStream zipAssets() {

        AssetItem assetItem = null;
        BufferedInputStream bis = null;
        Iterator<AssetItem> it = assets.iterator();
        byte[] data = new byte[1000];
        int count = 0;

        try {

            if (assets.size() > 1) {
                ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
                ZipOutputStream zipOutputStream = new ZipOutputStream(outputBytes);

                while (it.hasNext()) {

                    assetItem = it.next();
                    bis = new BufferedInputStream(assetItem.getBinaryContentAttachment());
                    
                    if("jar".equals(assetItem.getFormat())) {
                        ZipInputStream zin = new ZipInputStream(bis);
                        ZipEntry ze = null;
                        while ((ze = zin.getNextEntry()) != null) {
                            zipOutputStream.putNextEntry(new ZipEntry(ze.getName()));

                            for (int c = zin.read(); c != -1; c = zin.read()) {
                                zipOutputStream.write(c);
                            }
                            zin.closeEntry();
                            zipOutputStream.flush();
                        }                            
                        bis.close();

                    } else {
                        String fileName = null;
                        String binaryContentAttachmentFileName = assetItem
                                .getBinaryContentAttachmentFileName();
                        // Note the file extension name may not be same as asset
                        // format name in some cases.
                        if (binaryContentAttachmentFileName != null
                                && !"".equals(binaryContentAttachmentFileName)) {
                            fileName = binaryContentAttachmentFileName;
                        } else {
                            fileName = assetItem.getName() + "."
                                    + assetItem.getFormat();
                        }

                        zipOutputStream.putNextEntry(new ZipEntry(fileName));

                        while ((count = bis.read(data, 0, 1000)) != -1) {
                            zipOutputStream.write(data, 0, count);
                        }

                        zipOutputStream.flush();
                        bis.close();
                    }
                }
                zipOutputStream.close();

                return new ByteArrayInputStream(outputBytes.toByteArray());
            } else {

                return it.next().getBinaryContentAttachment();
            }
        } catch (Exception ex) {

        }

        return null;

    }

}
