package org.drools.guvnor.server.files;

import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This Class Allows us to Zip a set of Assets, in fact we generate an extra asset
 * whith the "zip" format, so we can add it to the package.
 */
public class AssetZipper {

    private final List<AssetItem> assets;

    public AssetZipper(List<AssetItem> assets) {
        this.assets = assets;
    }
    
    public AssetZipper(List<AssetItem> assets,
                       ModuleItem pkg) {
        this.assets = assets;
        //add pkg when it's really needed - zenix
    }

    public InputStream zipAssets() {

        AssetItem zipFileElement = null;
        BufferedInputStream inputZip = null;
        Iterator<AssetItem> it = assets.iterator();
        byte[] data = new byte[1000];
        int count = 0;

        try {

            if (assets.size() > 1) {
                ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
                ZipOutputStream outputZip = new ZipOutputStream(outputBytes);

                while (it.hasNext()) {

                    zipFileElement = it.next();
                    inputZip = new BufferedInputStream(zipFileElement.getBinaryContentAttachment());
                    outputZip.putNextEntry(new ZipEntry(zipFileElement.getName() + "." + zipFileElement.getFormat()));

                    while ((count = inputZip.read(data,
                            0,
                            1000)) != -1) {
                        outputZip.write(data,
                                0,
                                count);
                    }

                    outputZip.flush();
                    inputZip.close();
                }
                outputZip.close();

                return new ByteArrayInputStream(outputBytes.toByteArray());
            } else {

                return it.next().getBinaryContentAttachment();
            }
        } catch (Exception ex) {

        }

        return null;

    }

}
