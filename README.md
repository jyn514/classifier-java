# classifier-java
A port of [classifier](https://github.com/bhrigu123/classifier) to Java

## Description
Organizes your files by extension. Can be used on arbitrary directories.

### Default directories
+	Audio
+ 	Ringtones
+	Videos
+	Pictures
+	Archives
+ 	Documents
+ 	Webpages
+	Programming files
+ 	Plain text
+ 	Books
+ 	Packages
+	Programs

## Usage
`java -jar /path/to/classifier.jar DIRECTORY` or
`java -jar /path/to/classifier.jar --help`

## Options
    --config, -c
      View the current master config file
    
    --copyright, --license, -C
      Show copyright information

    --edit, -e
      Edit the master config file

    --edit-local, -l
      Edit the local config file

    --help, -h
      Show this help message

    --reset, -R
      Reset the master config file to its default value

    --default, --show-default, -s
      Show the default configuration file

    --verbose, -V
      Increase output

    --version, -v
      Print the version and exit



## Dependencies
+ [JCommander](https://jcommander.org/) as a argument parser

### Future plans
Include a save + undo feature  
Classify by date as well as extension (useful for photos)

Copyright (C) 2017  Joshua Nelson  
Version 0.1
