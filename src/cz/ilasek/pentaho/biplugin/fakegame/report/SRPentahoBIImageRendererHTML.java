package cz.ilasek.pentaho.biplugin.fakegame.report;

import game.report.SRHTMLRenderer;
import game.report.srobjects.ISRObjectRenderer;
import game.report.srobjects.SRImage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SRPentahoBIImageRendererHTML implements ISRObjectRenderer 
{
    protected final String IMAGE_RESOLVER = "/pentaho/getImage?image=";
    protected SRImage image;
    protected SRHTMLRenderer srhtmlRenderer;
    protected final String reportImagesDir;

    public SRPentahoBIImageRendererHTML(SRImage image, SRHTMLRenderer srhtmlRenderer, String reportImagesDir) {
        this.image = image;
        this.srhtmlRenderer = srhtmlRenderer;
        this.reportImagesDir = reportImagesDir;
    }

    public void render() {
        srhtmlRenderer.append("<P><DIV class='image'><IMG src = '");
        srhtmlRenderer.append(getImagePath());
        srhtmlRenderer.append("'/><DIV>" + image.getCaption() + "</DIV></DIV></P>\n");
    }
    
    protected String getImagePath()
    {
        String imagePath;
        try {
            imagePath = IMAGE_RESOLVER + URLEncoder.encode("/" + reportImagesDir + "/" + image.getImageFile(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            imagePath = IMAGE_RESOLVER + "/" + reportImagesDir + "/" + image.getImageFile();
        }        
        
        return imagePath;
    }
}
