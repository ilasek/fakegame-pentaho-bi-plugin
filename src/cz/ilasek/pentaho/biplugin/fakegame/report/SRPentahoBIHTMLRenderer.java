package cz.ilasek.pentaho.biplugin.fakegame.report;

import game.report.ISubreport;
import game.report.SRHTMLRenderer;
import game.report.srobjects.ISRObjectRenderer;
import game.report.srobjects.SRImage;

import java.util.Collection;

public class SRPentahoBIHTMLRenderer extends SRHTMLRenderer {
    protected final String reportImagesDir;
    
    public SRPentahoBIHTMLRenderer(StringBuffer buf, ISubreport subreport, Collection<ISubreport> allSubreports, String reportImagesDir) {
        super(buf, subreport, allSubreports);
        this.reportImagesDir = reportImagesDir;
    }
    
    @Override
    public ISRObjectRenderer createImageRenderer(SRImage srImage) {
        return new SRPentahoBIImageRendererHTML(srImage, this, reportImagesDir);
    }    
}
