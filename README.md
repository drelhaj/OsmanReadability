# Osman Readability Metric

<h3>About</h3>
Open Source tool for Arabic text readability (updated on 14/August/2020)

The tool is a Java open source to calculate readability for Arabic text with and without diacritics (Tashkeel). 
The tool works better with diacritics added in (we provide a method to calculate readability even with plain Arabic text, OSMAN will automatically estimate syllables and calculate readability even when no tashkeel is added in).

The tool was published as a full paper at LREC 2016 conference in Slovenia.
[El-Haj, M., and Rayson. "OSMAN - A Novel Arabic Readability Metric". 10th edition of the Language Resources and Evaluation Conference (LREC'16). May 2016. Portoroz, Slovenia.]
http://www.lancaster.ac.uk/staff/elhaj/docs/elhajlrec2016Arabic.pdf

<h3>New 2020</h3>
I have updated the code on 14/August/2020 and now it runs much faster and uses less disk space. 
In this version we decided to <b>stop</b> using Farasa or any of its components due to restrictions and copyrights issues and therefore we decided to have our own in-house fully free and open source tool to help advance the research on Arabic NLP and Linguistics, and we are proud of this. 

I've updated the code to use a RegEx word and sentence splitter instead of Stanford as many users complained having problems running Stanford Segmenter (you can edit the code to use your own NLP tools if you like).


<h3>How to run</h3>
To run the Runnable Jar version of the tool download the latest release "Osman2020.jar". To run the jar file on your preferred command line (CMD) application type: "java -jar Osman2020.jar" without the double quotes. This will open in a Graphical User Interface (GUI) window, so no programming needed. If your Windows machine/java is not displaying Arabic, the tool will not run correctly. You can fix that by running the following:
1-	Change the locale to Arabic as in the video https://www.youtube.com/watch?v=XkczYaBlbNY (I know it’s not the best video :)){optional but helps sometimes}.
2-	Run the following command “set JAVA_TOOL_OPTIONS =-Dfile.encoding=UTF-8” in the CMD window.
3-	Then run the tool in UTF8 format: “java -Dfile.encoding=UTF-8 -jar Osman2020.jar”


If you know how to use Java you can otherwise clone the source code. Class TestOSMAN shows how to measure OSMAN readability for text with and without diacritics.
Method calculateOsman(String text) can be called using an instance from the class <b>OsmanReadability</b>.
users can also remove diacritics using <b>removeTashkeel(String text)</b>.

The tool allows you to calculate other readability metrics such as ARI and LIX.
When using Eclipse (or other editors) make sure you set the encoding to UTF-8 for the console output (Run configuration -> Common Tab --> Other Encoding)

<h3>Import into Eclipse (step by step for beginners)</h3>
<ul>
<li>Install EGit: To install Egit and the Github Mylyn Connector from within Eclipse, navigate to the Help menu inside of Eclipse and select Install New Software. Enter the Juno update site url and search 'git' in the filter box. Once you've selected the EGit, JGit, and Mylyn GitHub items hit Next to finish the installation.</li>
<li>In Eclipse go to File --> Import --> and select Git (Import Git repositories from GitHub) from the Select import source window.</li>
<li>In the next window type "OsmanReadability" in the box and hit Search</li>
<li>The results box should show drelhaj/OsmanReadability</li>
<li>Select repository and click Finish</li>
</ul>

<h3>Download OSMAN UN Corpus</h3>
You can click on Download Zip button on the right hand side or download the latest release "runnable.zip"
Or you can navigate to Osman_UN_Corpus navigate to each folder and download the dataset zip files one by one. To download any of the zip files click on the zip file then click on "View Raw".

<h3>Contact</h3>
Have a question? Get in touch with us on: dr.melhaj@gmail.com
