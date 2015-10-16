# OsmanReadability

<h3>About</h3>
Open Source tool for Arabic text readability

The tool is a Java open source to calculate readability for Arabic text with and without diacritics (Tashkeel). 
The tool works better with diacritics added in (we provide a method to allow you add diacritics to plain Arabic text).

<h3>How to run</h3>
Class TestOSMAN shows how to measure OSMAN readability for text with and without diacritics.
Method calculateOsman(String text) can be called using an instance from the class<b>OsmanReadability</b>.
users can also add and remove diacritics using <b>addTashkeel(String text)</b> and <b>removeTashkeel(String text)</b>.

The tool allows you to calculate other readability metrics such as ARI and LIX.
When using Eclipse (or other editors) make sure you set the encoding to UTF-8 for the console output (Run configuration -> Common Tab --> Other Encoding)

<h3>Import into Eclipse (step by step for begginers)</h3>
<ul>
<li>Install EGit: To install Egit and the Github Mylyn Connector from within Eclipse, navigate to the Help menu inside of Eclipse and select Install New Software. Enter the Juno update site url and search 'git' in the filter box. Once you've selected the EGit, JGit, and Mylyn GitHub items hit Next to finish the installation.</li>
<li>In Eclipse go to File --> Import --> and select Git (Import Git repositories from GitHub) from the Select import source window.</li>
<li>In the next window type "OsmanReadability" in the box and hit Search</li>
<li>The results box should show drelhaj/OsmanReadability</li>
<li>Select repository and click Finish</li>
</ul>
