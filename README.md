
# RPGMaker Generator Parts Mover

[![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/gensuta/RPGMaker_Generator_Parts_Mover?include_prereleases)](https://img.shields.io/github/v/release/gensuta/RPGMaker_Generator_Parts_Mover?include_prereleases)
[![GitHub last commit](https://img.shields.io/github/last-commit/gensuta/RPGMaker_Generator_Parts_Mover)](https://img.shields.io/github/last-commit/gensuta/RPGMaker_Generator_Parts_Mover)
[![GitHub issues](https://img.shields.io/github/issues-raw/gensuta/RPGMaker_Generator_Parts_Mover)](https://img.shields.io/github/issues-raw/gensuta/RPGMaker_Generator_Parts_Mover)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/gensuta/RPGMaker_Generator_Parts_Mover)](https://img.shields.io/github/issues-pr/gensuta/RPGMaker_Generator_Parts_Mover)
[![GitHub Repo Liscence](https://img.shields.io/github/license/gensuta/RPGMaker_Generator_Parts_Mover)](https://img.shields.io/github/license/gensuta/RPGMaker_Generator_Parts_Mover)

RPGMaker Generator Assets Mover is a tool that makes it easier to move character generator parts into your version of RPGMaker.
Whether it's one image or a whole folder, this tool will move the assets as long as they're properly named (ie. face generator parts start with "FG_", SV generator parts start with "SV_" etc.)


# Usage
You need to install [JRE](https://www.java.com/en/download/windows_manual.jsp) to run this tool ( the jar file)

[![Video tutorial](https://img.youtube.com/vi/BU3K0ccPeP8/hqdefault.jpg)](https://www.youtube.com/embed/BU3K0ccPeP8)

The above video has a tutorial on how to use the tool, but if you want instructions without a video, follow along below!



1. To start, download the newest release, unzip it, and open the jar file. Navigate to your RPGMaker Generator folder. If you right-click on your version of RPGMaker in Steam and click 'Browse local files', you'll be able to find the folder. You'll need to get the path of the generator folder and paste it under where it says "Please enter the path of the RPGMaker Generator folder"
(The path will be saved the next time you open the tool, but if you need to change the path afterward that's still doable.)

2. Next, you will drag in the files and/or folders you want to move into the yellow rectangle. You'll know it's in the list of parts to be moved when you see the folder/file name in the box below the yellow rectangle. Again, make sure your files are FORMATTED or they won't work/be moved. Assets should be named like so: `[Part]_[Sub-part]_p[number]` which could be "FG_RearHair1_p41" or "TVD_Clothing_p_60". The only generator part that won't entirely follow this are the parts going into the Variation folder. That file should be named `icon_[sub-part_p[number}`. The transparent file with only yellow is to help with changing colors and should have a _c at the end but match that format from before (for ex: "FG_RearHair1_p41_c").

3. Before we can move the files/folders we want, we have to select the body type these generator parts are for. If there are parts for multiple body types AND they're separated into the appropriate folders titled "Male", "Female", and "Kid", then you should select "All". If you have generator parts for one body type, select the appropriate body type.

4. Finally, you can hit "Move Generator Assets". It will take a second if there are a lot of files to move, but there will be a results log showing which files were successfully and unsuccessfully moved.

That's it! You should be able to see these assets if you open up RPGMaker (if it's open, you may have to close and reopen it).

I'm still fairly new to using Java, but wanted to create something that could be useful to gamedev folks!
If there are any issues, please create an issue and I'll do my best to fix it.


# Contribute
Pull requests are welcome! 


# License
[MIT license](./LICENSE)

This readme was created using [this readme](https://makeread.me/generator/pottekkat-awesome-readme) template.
The badges used here were generated with [shields.io](https://shields.io/).

