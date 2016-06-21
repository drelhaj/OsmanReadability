# Osman Readability Metric

<h3>About</h3>
Open Source tool for Arabic text readability

The tool is a Java open source to calculate readability for Arabic text with and without diacritics (Tashkeel). 
The tool works better with diacritics added in (we provide a method to allow you add diacritics to plain Arabic text).

The tool was published as a full paper at LREC 2016 conference in Slovenia.
[El-Haj, M., and Rayson. "OSMAN - A Novel Arabic Readability Metric". 10th edition of the Language Resources and Evaluation Conference (LREC'16). May 2016. Portoroz, Slovenia.]
http://www.lancaster.ac.uk/staff/elhaj/docs/elhajlrec2016Arabic.pdf


<h3>How to run</h3>
You can download the latest release "runnable.zip" and unzip the file contents into a directory then double click Osman.jar to run the system, which will open a in a Graphical User Interface (GUI) window, so no programming needed. 

Otherwise, Class TestOSMAN shows how to measure OSMAN readability for text with and without diacritics.
Method calculateOsman(String text) can be called using an instance from the class <b>OsmanReadability</b>.
users can also add and remove diacritics using <b>addTashkeel(String text)</b> and <b>removeTashkeel(String text)</b>.

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
