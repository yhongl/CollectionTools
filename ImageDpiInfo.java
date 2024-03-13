import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ImageDpiInfo {

    public static long[] getDpi(File imageFile) throws IOException {

        //默认水平和垂直的分辨率值
        long[] physicalDpi = new long[]{72, 72};
        ImageInputStream stream = ImageIO.createImageInputStream(imageFile);
        try {
            // 获取图片的元数据
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                reader.setInput(stream);
                IIOMetadata metadata = reader.getImageMetadata(0);
                var tree = metadata.getAsTree("javax_imageio_1.0");
                NodeList children = tree.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node node = children.item(i);
                    if ("Dimension".equals(node.getNodeName())) {
                        NodeList dimensionChildren = node.getChildNodes();
                        for (int j = 0; j < dimensionChildren.getLength(); j++) {
                            Node dimensionNode = dimensionChildren.item(j);
                            if ("HorizontalPixelSize".equals(dimensionNode.getNodeName())) {
                                physicalDpi[0] = Math.round(25.4 / Double.parseDouble(dimensionNode.getAttributes().item(0).getNodeValue()));
                            } else if ("VerticalPixelSize".equals(dimensionNode.getNodeName())) {
                                physicalDpi[1] = Math.round(25.4 / Double.parseDouble(dimensionNode.getAttributes().item(0).getNodeValue()));
                            }
                        }
                    }
                }
                // 返回水平分辨率
                return physicalDpi;
            } else {
                throw new IOException("The image reader can not found Dimension info");
            }
        } finally {
            stream.close();
        }
    }
}
