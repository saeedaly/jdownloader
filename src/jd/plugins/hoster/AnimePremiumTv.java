//jDownloader - Downloadmanager
//Copyright (C) 2013  JD-Team support@jdownloader.org
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.
package jd.plugins.hoster;

import java.io.IOException;

import org.jdownloader.controlling.filter.CompiledFiletypeFilter;

import jd.PluginWrapper;
import jd.http.Browser;
import jd.http.URLConnectionAdapter;
import jd.nutils.encoding.Encoding;
import jd.parser.Regex;
import jd.plugins.DownloadLink;
import jd.plugins.DownloadLink.AvailableStatus;
import jd.plugins.HostPlugin;
import jd.plugins.LinkStatus;
import jd.plugins.PluginException;
import jd.plugins.PluginForHost;

@HostPlugin(revision = "$Revision$", interfaceVersion = 2, names = { "animepremium.tv" }, urls = { "http://(s\\d000\\.animepremium\\.tv/download/\\d+|embeds\\.animepremium\\.tv/share\\.php\\?id=\\d+|download\\.animepremium\\.tv/video/\\d+)" })
public class AnimePremiumTv extends PluginForHost {
    public AnimePremiumTv(PluginWrapper wrapper) {
        super(wrapper);
    }

    private String DLLINK = null;

    @Override
    public String getAGBLink() {
        return "http://s1000.animepremium.tv/";
    }

    @Override
    public void correctDownloadLink(final DownloadLink downloadLink) {
        String fuid = new Regex(downloadLink.getDownloadURL(), "/share\\.php\\?id=(\\d+)").getMatch(0);
        if (fuid != null) {
            downloadLink.setUrlDownload("http://s1000.animepremium.tv/download/" + fuid);
        }
    }

    @Override
    public AvailableStatus requestFileInformation(DownloadLink link) throws IOException, PluginException {
        link.setMimeHint(CompiledFiletypeFilter.VideoExtensions.MP4);
        this.setBrowserExclusive();
        br.setFollowRedirects(true);
        br.getPage(link.getPluginPatternMatcher());
        if (br.getHttpConnection().getResponseCode() == 404 || br.containsHTML("<title>404 Not Found</title>")) {
            throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        }
        String filename = br.getRegex("<title>Download (.*?)  \\|").getMatch(0);
        if (filename == null) {
            filename = br.getRegex("<h2>Download File: (.*?)</h2>").getMatch(0);
        }
        if (filename == null) {
            filename = br.getRegex("<title>Download ([^<>\"]*?)</title>").getMatch(0);
        }
        // final String download = br.getRegex("(https?://[^<>\"\\']+/download2\\.php?[^<>\"\\']+)").getMatch(0);
        // br.getPage(download);
        DLLINK = br.getRegex("\\'file\\'([\t\n\r ]+)?:([\t\n\r ]+)?\\'([^<>\"]*?)\\'").getMatch(2);
        if (DLLINK == null) {
            DLLINK = br.getRegex("(https?://\\w+\\.tinyvid\\.net[^<>\"]*?\\.mp4)").getMatch(0);
        }
        if (filename == null && DLLINK == null) {
            throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        } else if (filename == null || filename.equals("")) {
            throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        }
        filename = Encoding.htmlDecode(filename.trim());
        link.setName(filename);
        DLLINK = Encoding.htmlDecode(DLLINK);
        if (!DLLINK.startsWith("http://")) {
            DLLINK = "http://s1000.animepremium.tv/download/" + DLLINK;
        }
        Browser br2 = br.cloneBrowser();
        // In case the link redirects to the finallink
        br2.setFollowRedirects(true);
        URLConnectionAdapter con = null;
        try {
            con = br2.openGetConnection(DLLINK);
            if (!con.getContentType().contains("html")) {
                String ext = br2.getURL().substring(br2.getURL().lastIndexOf("."));
                if (ext == null || ext.length() > 5) {
                    ext = ".mp4";
                }
                link.setFinalFileName(filename + ext);
                link.setDownloadSize(con.getLongContentLength());
            } else {
                throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
            }
            return AvailableStatus.TRUE;
        } finally {
            try {
                con.disconnect();
            } catch (Throwable e) {
            }
        }
    }

    @Override
    public void handleFree(DownloadLink downloadLink) throws Exception {
        requestFileInformation(downloadLink);
        dl = jd.plugins.BrowserAdapter.openDownload(br, downloadLink, DLLINK, true, 0);
        if (dl.getConnection().getContentType().contains("html")) {
            br.followConnection();
            throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        }
        dl.startDownload();
    }

    @Override
    public int getMaxSimultanFreeDownloadNum() {
        return -1;
    }

    @Override
    public void reset() {
    }

    @Override
    public void resetPluginGlobals() {
    }

    @Override
    public void resetDownloadlink(DownloadLink link) {
    }
}
