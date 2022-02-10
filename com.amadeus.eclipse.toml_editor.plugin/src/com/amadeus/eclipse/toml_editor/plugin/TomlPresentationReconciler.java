package com.amadeus.eclipse.toml_editor.plugin;

import java.util.ArrayList;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

import com.amadeus.eclipse.toml_editor.plugin.rules.KeyValuePairRule;
import com.amadeus.eclipse.toml_editor.plugin.rules.KeywordRule;
import com.amadeus.eclipse.toml_editor.plugin.rules.AnyNumberRule;
import com.amadeus.eclipse.toml_editor.plugin.rules.SectionNameRule;
import com.amadeus.eclipse.toml_editor.plugin.rules.StartEndMultilineRule;
import com.amadeus.eclipse.toml_editor.plugin.rules.StartEndRule;
import com.amadeus.eclipse.toml_editor.plugin.rules.TimestampRule;

public class TomlPresentationReconciler extends PresentationReconciler {
    private RuleBasedScanner scanner;
    private ColorRegistry colorRegistry;
    private FontRegistry fontRegistry;

    public TomlPresentationReconciler() {
        IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
        ITheme currentTheme = themeManager.getCurrentTheme();
        colorRegistry = currentTheme.getColorRegistry();
        fontRegistry = currentTheme.getFontRegistry();

        scanner = new RuleBasedScanner();
        updateRules();

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
        this.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        this.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
    }

    private void updateRules() {
        Color tblColor = colorRegistry.get("com.amadeus.eclipse.toml_editor.plugin.tableName");
        Color keyColor = colorRegistry.get("com.amadeus.eclipse.toml_editor.plugin.keyName");
        Color strColor = colorRegistry.get("com.amadeus.eclipse.toml_editor.plugin.string");
        Color cmtColor = colorRegistry.get("com.amadeus.eclipse.toml_editor.plugin.comment");
        Color numColor = colorRegistry.get("com.amadeus.eclipse.toml_editor.plugin.number");
        Color delColor = colorRegistry.get("com.amadeus.eclipse.toml_editor.plugin.delimeter");
        Color wrdColor = colorRegistry.get("com.amadeus.eclipse.toml_editor.plugin.keyword");
        Color tsmColor = colorRegistry.get("com.amadeus.eclipse.toml_editor.plugin.timestamp");

        Font tableFont = fontRegistry.get("com.amadeus.eclipse.toml_editor.plugin.tableNameFont");
        Font keyFont = fontRegistry.get("com.amadeus.eclipse.toml_editor.plugin.keyNameFont");
        Font commentFont = fontRegistry.get("com.amadeus.eclipse.toml_editor.plugin.commentFont");

        ArrayList<IRule> rules_list = new ArrayList<>();
        //cmtRule.setColumnConstraint(0);

        rules_list.add(new SectionNameRule(new Token(new TextAttribute(tblColor, null, SWT.NORMAL, tableFont))));
        rules_list.add(new EndOfLineRule("#", new Token(new TextAttribute(cmtColor, null, SWT.NORMAL, commentFont))));

        // @formatter:off
        KeyValuePairRule kvp = new KeyValuePairRule(new Token(new TextAttribute(keyColor, null, SWT.NORMAL, keyFont)), 
                                                    new Token(new TextAttribute(delColor)));
        rules_list.add(kvp);
        rules_list.add(new KeywordRule(new Token(new TextAttribute(wrdColor))));
        rules_list.add(new TimestampRule(new Token(new TextAttribute(tsmColor))));
        rules_list.add(new AnyNumberRule(new Token(new TextAttribute(numColor))));
        // @formatter:on


        rules_list.add(new StartEndMultilineRule("\"\"\"", "\"\"\"", new Token(new TextAttribute(strColor)), (char) '\\'));
        rules_list.add(new StartEndMultilineRule("'''", "'''", new Token(new TextAttribute(strColor)), (char) '\\'));
        rules_list.add(new StartEndRule("'", "'", new Token(new TextAttribute(strColor)), (char) '\\'));
        rules_list.add(new StartEndRule("\"", "\"", new Token(new TextAttribute(strColor)), (char) '\\'));

        IRule[] aruls = rules_list.toArray(new IRule[rules_list.size()]);
        scanner.setRules(aruls);
    }

    @Override
    public void install(ITextViewer viewer) {
        super.install(viewer);
        IEclipsePreferences node = InstanceScope.INSTANCE.getNode("org.eclipse.ui.workbench");

        node.addPreferenceChangeListener(event -> {
            updateRules();
            viewer.invalidateTextPresentation();
        });
    }
}