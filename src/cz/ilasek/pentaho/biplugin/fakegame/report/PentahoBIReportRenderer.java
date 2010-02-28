package cz.ilasek.pentaho.biplugin.fakegame.report;

import game.report.ISubreport;
import game.report.ReportRenderer;
import game.report.SRRenderer;

import java.util.Collection;

public class PentahoBIReportRenderer extends ReportRenderer {

    protected final String reportImagesDir;
    protected StringBuffer buf = new StringBuffer();
    
    public PentahoBIReportRenderer(String reportImagesDir)
    {
        this.reportImagesDir = reportImagesDir;
    }
    
    @Override
    protected SRRenderer createRenderer(ISubreport subreport,
            Collection<ISubreport> allSubreports) {
        return new SRPentahoBIHTMLRenderer(buf, subreport, allSubreports, reportImagesDir);
    }

    public StringBuffer getReport()
    {
        buildReport();
        
        return buf;
    }
    
    protected void buildReport()
    {
        buf.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n");
        buf.append("<HTML>\n");
        buf.append("<HEAD><TITLE>Report name</TITLE><link rel='stylesheet' media='screen' type='text/css' href='/pentaho/content/fake-game-static/report-html/screen.css' /></HEAD>\n");
        buf.append("<BODY>\n");
        buf.append("<div class='fakereport'>\n");
        renderTopBar();
        
        super.render();
        
        buf.append("</div>\n");
        buf.append("</BODY>\n");
        buf.append("</HTML>\n");        
    }
    
    protected void renderTopBar() {
        buf.append("<div class='bar' id='bar__top'>\n");
        buf.append("<div class='bar-left' id='bar__topleft'>\n");
        buf.append("<a href='http://fakegame.sourceforge.net' class='wikilink2' title='FAKE GAME Project' rel='nofollow'>FAKE GAME Report</a>\n");
        buf.append("</div>\n");
        buf.append("</div>\n");
    }    
}
