# TOML Eclipse plugin

Add site in Eclipse: 
https://amadeusitgroup.github.io/eclipse-toml-editor/updates
<br>and install newest version available.


## Eclipse projects
After you get source from repository, just import main project with 3 sub-projects into workspace:
- com.amadeus.eclipse.toml_editor.feature
- com.amadeus.eclipse.toml_editor.plugin
- updates

## Run to test

1. Open plugin.xml (com.amadeus.eclipse.toml_editor.plugin)
2. Navigate to 'Overview' tab
3. Select 'Launch and Eclipse application' in Testing section

## Build Feature

1. Open com.amadeus.eclipse.toml_editor.updates/site.xml
   Either 'Synchronize...' or add feature (if not on the list)
   - Add Feature...
     Select 'com.amadeus.eclipse.toml_editor.feature'
   Then:
   - Build All
 
One can generate plugin and/or feature step-by-step
1. Open com.amadeus.eclipse.taml_editor.plugin/plugin.xml
   - Use Export Wizard
   - Destination: specify directory where to put jar file with plugin
     hint: Browse to select 'com.amadeus.eclipse.toml_editor.updates'
2. Open com.amadeus.eclipse.toml_editor.feature/feature.xml
   - Use Export Wizard
   - Destination: specify directory where to put jar file with plugin
     hint: Browse to select 'updates'
3. Open updates/site.xml
   - Build
   
## Publish

With pages.github we have plugin 'published' automatically when 'updates' project is pushed.
Once other location/webserver is in use:

1. Copy content of project 'updates' to your updates page.

   
## TEST Installation from ZIP - manual process

1. Build All (site.xml - as above)
2. Create ZIP with content of <project>\com.amadeus.eclipse.toml_editor.updates
3. Add new site in Eclipse: point archive (ZIP) instead of http, give name
4. Select new site (by name) to install from
5. Install required feature (watch: newest version of more available)

### Verify:
- Preferences... -> Appearance -> Colors and Fonts -> TOML Editor (colors, fonts)
- Right-click any TOML file, 'Open With' -> Other... -> TOML Editor
