Some ideas of improvements
--------------------------

1. Hierarchical layout in Outline View
   Tables (sections) can contain hierarchical structure: [name1.name2.name3]
   We should be able to display them as tree
   
2. TOML validator 
   Validation with errors highligting
   
3. Data types highlighting
   Currently only strings and numbers are coloured. To be syntax-highlighted:
   - timestamp (ISO)
   - boolean (true/false)
   
4. Action: serialize to JSON
   One should be able to easilty serialize TOML file to JSON format.
   It could be either to flat file or into memory, or into new editor window
   
5. Configurable comment character: by default '#' only
   One could want also ';' as a comment, so then INI files (Windows) are also displayed properly
   
Issues
------

1. Disable XML validator to avoid 'Content is not allowed in prolog'
