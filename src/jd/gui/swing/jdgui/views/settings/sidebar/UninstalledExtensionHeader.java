package jd.gui.swing.jdgui.views.settings.sidebar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jdownloader.gui.IconKey;
import org.jdownloader.gui.translate._GUI;
import org.jdownloader.images.AbstractIcon;

public class UninstalledExtensionHeader extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public UninstalledExtensionHeader() {
        super(new MigLayout("ins 0,wrap 2", "[][grow,fill]", "[][]"));
        add(Box.createGlue(), "spanx,height 15!");
        add(new JLabel(new AbstractIcon(IconKey.ICON_EXTENSIONMANAGER, 32)));
        add(new JLabel(_GUI.T.uninstalledextensionManager_title()));
        setOpaque(false);

        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0), BorderFactory.createMatteBorder(1, 0, 2, 0, getBackground().darker())));
        setBackground(null);
    }

    // @Override
    // public Dimension getPreferredSize() {
    // return new Dimension(100, 45);
    // }

    @Override
    public boolean isOpaque() {
        return true;
    }
}
