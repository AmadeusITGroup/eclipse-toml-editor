
Eclipse projects
----------------
After you get source from repository, just import 3 projects into workspace:
com.amadeus.eclipse.toml_editor.feature
com.amadeus.eclipse.toml_editor.updates
com.amadeus.eclipse.toml_editor.plugin

Build
-----
1. Open com.amadeus.eclipse.taml_editor.plugin/plugin.xml
   - Use Export Wizard
   - Destination: specify directory where to put jar file with plugin
     hint: Browse to select <project>\com.amadeus.eclipse.toml_editor.updates
2. Open com.amadeus.eclipse.toml_editor.feature/feature.xml
   - Use Export Wizard
   - Destination: specify directory where to put jar file with plugin
     hint: Browse to select <project>\com.amadeus.eclipse.toml_editor.updates
3. Open com.amadeus.eclipse.toml_editor.updates/site.xml
   - Build All
   
Publish
-------
1. Copy content of <project>\com.amadeus.eclipse.toml_editor.updates
   to your updates page.
   See also: http://eclipse.kacprzak.org/
   
Manual process
--------------
1. Build All (site.xml - as above)
2. Create ZIP with content of <project>\com.amadeus.eclipse.toml_editor.updates
3. Add new site in Eclipse: point archive (ZIP) inestad of http
4. Select new site to install from
5. Verify:
    Preferences... -> Appearance -> Colors and Fonts -> TOML Editor (colors, fonts)