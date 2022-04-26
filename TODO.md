Some ideas of improvements
--------------------------

1. Hierarchical layout in Outline View
   <br>
   Tables (sections) can contain hierarchical structure: [name1.name2.name3].
   <br>We should be able to display them as tree. 
   It should be done with action button in Outline View.
   
2. TOML validator 
   <br>
   Validation with errors highlighting
   
3. Data types highlighting
   <br>
   Currently coloured are:
    - strings
    - numbers
    - timestamp (ISO)
    - boolean (true/false)
   
   <br>To be added:
    - arrays
   
4. Action: serialize to JSON
   <br>
   One should be able to easilty serialize TOML file to JSON format.
   It could be either to flat file or into memory, or into new editor window
   
5. Configurable comment character
   <br>By default: '#' only. 
   One could want also ';' as a comment, so then INI files (Windows) are also displayed properly
   
