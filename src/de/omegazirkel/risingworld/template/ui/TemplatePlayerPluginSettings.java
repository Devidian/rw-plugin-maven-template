package de.omegazirkel.risingworld.template.ui;

import de.omegazirkel.risingworld.MavenTemplate;
import de.omegazirkel.risingworld.tools.ui.BasePlayerPluginSettingsPanel;
import de.omegazirkel.risingworld.tools.ui.PlayerPluginSettings;
import net.risingworld.api.objects.Player;
import net.risingworld.api.ui.UILabel;
import net.risingworld.api.ui.UIScrollView;
import net.risingworld.api.ui.UIScrollView.ScrollViewMode;

public class TemplatePlayerPluginSettings extends PlayerPluginSettings {

    public TemplatePlayerPluginSettings() {
        this.pluginLabel = MavenTemplate.name;
    }

    @Override
    public BasePlayerPluginSettingsPanel createPlayerPluginSettingsUIElement(Player uiPlayer) {
        return new BasePlayerPluginSettingsPanel(uiPlayer, pluginLabel) {
            @Override
            protected UIScrollView createSettingsContent() {
                UIScrollView content = new UIScrollView(ScrollViewMode.Vertical);
                // TODO: implement actual settings content for MavenTemplate plugin
                UILabel placeholderLabel = new UILabel("MavenTemplate plugin settings will be here.");
                content.addChild(placeholderLabel);
                return content;
            }
        };
    }

}
